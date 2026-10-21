"""
Tạo label, milestone và issue trên GitHub từ ISSUES_PLAN.md.

Chạy thử (không tạo gì, chỉ in ra và ghi body vào scripts/preview/):
    python scripts/create_issues.py --dry-run
Chạy thật:
    python scripts/create_issues.py

Chạy lại nhiều lần được: issue trùng tiêu đề sẽ bị bỏ qua, milestone đã có thì chỉ cập nhật.
Thêm --relink để ghi đè body mọi issue theo ISSUES_PLAN.md (sẽ mất các ô checklist đã tick).
"""
import json
import re
import subprocess
import sys
import tempfile
from pathlib import Path

REPO = "HungNguyenBa1811/math-dash"
ROOT = Path(__file__).resolve().parent.parent
PLAN = ROOT / "ISSUES_PLAN.md"
PREVIEW = ROOT / "scripts" / "preview"

PEOPLE = {
    "Hùng": ["HungNguyenBa1811"],
    "Hoàng": ["Minahn210"],
    "Cả 2": ["HungNguyenBa1811", "Minahn210"],
}

LABELS = {
    "ooad": ("5319E7", "Phân tích / thiết kế OOAD (tài liệu, biểu đồ)"),
    "chung": ("BFD4F2", "Việc chung của cả hệ thống"),
    "uc-tim-tran": ("1D76DB", "UC1 Tìm trận (Hùng)"),
    "uc-thi-dau-online": ("0052CC", "UC2 Thi đấu online (Hùng)"),
    "uc-lich-su": ("006B75", "UC3 Quản lý lịch sử đấu (Hùng)"),
    "uc-choi-offline": ("0E8A16", "UC4 Chơi offline (Hoàng)"),
    "uc-bang-vang": ("FBCA04", "UC5 Xem bảng vàng (Hoàng)"),
    "uc-cai-dat": ("C2E0C6", "UC6 Cài đặt âm thanh (Hoàng)"),
    "docs": ("C5DEF5", "Báo cáo cá nhân"),
    "bug": ("D73A4A", "Lỗi phát hiện khi kiểm thử"),
    "enhancement": ("A2EEEF", "Ý tưởng tính năng mới, không gắn milestone"),
}

# Mã milestone -> (tiêu đề milestone, hạn chót)
MILESTONES = {
    "M1": ("M1 - Phan tich", "2026-10-28"),
    "M2": ("M2 - Thiet ke", "2026-11-01"),
    "M3": ("M3 - Lap trinh", "2026-11-10"),
    "M4": ("M4 - Kiem thu & Bao cao", "2026-11-15"),
}

HEADER = ("> Tên gọi chung (UC, lớp, bảng) và quy ước: xem [`ISSUES_PLAN.md`](https://github.com/{0}/blob/main/ISSUES_PLAN.md) mục 2 và 3. "
          "Yêu cầu báo cáo và quy trình OOAD: xem [`REPORT_CONVENTION.md`](https://github.com/{0}/blob/main/REPORT_CONVENTION.md).\n\n".format(REPO))

CHECKLIST = """
---
### Checklist CONVENTION.md
- [ ] Không dùng Hilt/Dagger, Room, RxJava/LiveData/ViewModel, ViewBinding/DataBinding
- [ ] Chỉ dùng `findViewById`, `SQLiteOpenHelper`, `Thread` + `runOnUiThread`
- [ ] Mỗi hàm < 20 dòng, callback lồng tối đa 2 cấp
- [ ] Dùng Anonymous Class, KHÔNG dùng lambda
- [ ] Comment tiếng Việt cho các khối code quan trọng
"""

OOAD_CHECKLIST = """
---
### Checklist OOAD
- [ ] Làm đúng các bước của `REPORT_CONVENTION.md` mục 3
- [ ] Tên UC, lớp, bảng khớp mục 2 của `ISSUES_PLAN.md`
- [ ] Kịch bản và biểu đồ tuần tự khớp từng bước (cùng số bước, cùng thứ tự)
- [ ] Mỗi hình có chú thích "Hình x: ..." và 3–5 dòng giải thích
- [ ] Đã lưu Markdown + PNG vào `docs/` và dán vào báo cáo cá nhân
- [ ] Người còn lại đã review
"""

DRY_RUN = "--dry-run" in sys.argv


# ---------- Đọc ISSUES_PLAN.md ----------

def read_sections(text):
    """Tách các khối '### ...' cho tới heading kế tiếp hoặc đường kẻ '---'."""
    sections = []
    current = None
    in_code = False
    for line in text.splitlines():
        if line.strip().startswith("```"):
            in_code = not in_code
        is_break = not in_code and (line.startswith("## ") or line.startswith("### ") or line.strip() == "---")
        if is_break:
            if current:
                sections.append(current)
            current = {"heading": line, "lines": []} if line.startswith("### ") else None
            continue
        if current is not None:
            current["lines"].append(line)
    if current:
        sections.append(current)
    return sections


def read_summary_table(text):
    """Đọc bảng 'Tổng hợp nhanh' để lấy assignee và label của từng issue."""
    table = {}
    part = re.split(r"^## \d+\. Tổng hợp nhanh", text, maxsplit=1, flags=re.M)[1]
    for row in re.findall(r"^\| (M\d-\d\d) \|(.+)$", part, re.M):
        cells = [c.strip() for c in row[1].strip(" |").split("|")]
        table[row[0]] = {"assignees": PEOPLE[cells[1]], "labels": [l.strip() for l in cells[2].split(",")]}
    return table


def build_issues(text):
    summary = read_summary_table(text)
    issues = []
    for sec in read_sections(text):
        m = re.match(r"### \[(M\d-\d\d)\] (.+)", sec["heading"])
        if not m:
            continue
        code = m.group(1)
        title = "[%s] %s" % (code, m.group(2).split(" · ")[0].strip())
        body = HEADER + "\n".join(sec["lines"]).strip() + "\n"
        info = summary[code]
        if "docs" in info["labels"]:
            body += report_checklist(body)
        elif "ooad" in info["labels"]:
            body += OOAD_CHECKLIST
        else:
            body += CHECKLIST
        issues.append({"code": code, "title": title, "body": body,
                       "labels": info["labels"], "assignees": info["assignees"],
                       "milestone": MILESTONES[code[:2]][0]})
    return issues


def report_checklist(body):
    """Biến bảng phân công mục báo cáo thành task list để tick trên GitHub."""
    rows = re.findall(r"^\s*\| ([\d.]+) \| (.+?) \| (.+?) \|\s*$", body, re.M)
    items = ["- [ ] Mục %s — %s (%s)" % (num, re.sub(r"\*\*", "", content), who) for num, content, who in rows]
    return "\n---\n### Checklist mục báo cáo\n" + "\n".join(items) + "\n"


def build_milestone_descriptions(text):
    descs = {}
    for sec in read_sections(text):
        m = re.search(r"Milestone `(M\d) - ", sec["heading"])
        if m:
            descs[m.group(1)] = "\n".join(sec["lines"]).strip()
    return descs


# ---------- Gọi gh ----------

def gh(args, body=None):
    if body is not None:
        with tempfile.NamedTemporaryFile("w", encoding="utf-8", suffix=".md", delete=False) as f:
            f.write(body)
        args = args + ["--body-file", f.name]
    result = subprocess.run(["gh"] + args, capture_output=True, text=True, encoding="utf-8")
    if result.returncode != 0:
        raise RuntimeError(result.stderr.strip())
    return result.stdout.strip()


def gh_api(method, path, fields):
    args = ["api", "-X", method, path]
    for key, value in fields.items():
        args += ["-f", "%s=%s" % (key, value)]
    return json.loads(gh(args))


def ensure_labels():
    for name, (color, desc) in LABELS.items():
        gh(["label", "create", name, "-R", REPO, "-c", color, "-d", desc, "--force"])
        print("  label  ", name)


def ensure_milestones(descs):
    existing = {m["title"]: m["number"] for m in json.loads(gh(["api", "repos/%s/milestones?state=all" % REPO]))}
    for code, (title, due) in MILESTONES.items():
        fields = {"title": title, "description": descs[code], "due_on": due + "T12:00:00Z"}
        if title in existing:
            gh_api("PATCH", "repos/%s/milestones/%d" % (REPO, existing[title]), fields)
            print("  milestone (cập nhật)", title)
        else:
            gh_api("POST", "repos/%s/milestones" % REPO, fields)
            print("  milestone (mới)    ", title)


def existing_issue_numbers():
    data = json.loads(gh(["issue", "list", "-R", REPO, "--state", "all", "--limit", "500", "--json", "number,title"]))
    return {i["title"]: i["number"] for i in data}


def create_issue(issue):
    base = ["issue", "create", "-R", REPO, "-t", issue["title"], "-m", issue["milestone"]]
    for label in issue["labels"]:
        base += ["-l", label]
    url = gh(base + ["-a", ",".join(issue["assignees"])], body=issue["body"])
    return int(url.rstrip("/").split("/")[-1])


def parse_dependencies(dep_line, numbers):
    """Đọc dòng 'Phụ thuộc': bỏ qua 'không có', mở rộng 'Mx-aa → Mx-bb' và 'Toàn bộ Mx'."""
    text = dep_line.replace("- **Phụ thuộc:**", "").strip()
    if text.startswith("không có"):
        return []
    codes = set(re.findall(r"M\d-\d\d", text))
    for start, end in re.findall(r"(M\d-\d\d) → (M\d-\d\d)", text):
        prefix, first, last = start[:3], int(start[3:]), int(end[3:])
        codes.update("%s%02d" % (prefix, n) for n in range(first, last + 1))
    for module in re.findall(r"Toàn bộ (M\d)\b", text):
        codes.update(c for c in numbers if c.startswith(module + "-"))
    return sorted(c for c in codes if c in numbers)


def link_dependencies(issues, numbers):
    """Thêm mục 'Liên kết' trỏ tới số issue thật của các task phụ thuộc."""
    for issue in issues:
        dep_line = next((l for l in issue["body"].splitlines() if l.startswith("- **Phụ thuộc:**")), "")
        codes = parse_dependencies(dep_line, numbers)
        links = ["- %s → #%d" % (c, numbers[c]) for c in codes]
        body = issue["body"]
        if links:
            body += "\n---\n### Liên kết phụ thuộc\n" + "\n".join(links) + "\n"
        gh(["issue", "edit", str(numbers[issue["code"]]), "-R", REPO], body=body)
        print("  liên kết #%d %s" % (numbers[issue["code"]], ", ".join(codes)))


# ---------- Chạy ----------

def main():
    sys.stdout.reconfigure(encoding="utf-8")
    text = PLAN.read_text(encoding="utf-8")
    issues = build_issues(text)
    descs = build_milestone_descriptions(text)
    assert len(descs) == len(MILESTONES), "Thiếu mô tả milestone: %s" % list(descs)

    if DRY_RUN:
        PREVIEW.mkdir(parents=True, exist_ok=True)
        for code, desc in descs.items():
            (PREVIEW / ("milestone-%s.md" % code)).write_text(desc, encoding="utf-8")
        for issue in issues:
            (PREVIEW / (issue["code"] + ".md")).write_text(issue["body"], encoding="utf-8")
            print("%-60s %-28s %-32s %s" % (issue["title"][:60], ",".join(issue["labels"]),
                                           ",".join(issue["assignees"]), issue["milestone"]))
        print("\n%d issue, %d milestone. Body đã ghi vào %s" % (len(issues), len(descs), PREVIEW))
        return

    print("Labels:")
    ensure_labels()
    print("Milestones:")
    ensure_milestones(descs)

    print("Issues:")
    numbers = existing_issue_numbers()
    numbers = {i["code"]: numbers[i["title"]] for i in issues if i["title"] in numbers}
    created = set()
    for issue in issues:
        if issue["code"] in numbers:
            print("  bỏ qua (đã có) #%d %s" % (numbers[issue["code"]], issue["title"]))
            continue
        numbers[issue["code"]] = create_issue(issue)
        created.add(issue["code"])
        print("  tạo #%d %s" % (numbers[issue["code"]], issue["title"]))

    # Mặc định chỉ ghi body cho issue vừa tạo, để không xóa mất checklist đã tick.
    # Muốn ghi đè lại body của mọi issue theo ISSUES_PLAN.md thì chạy kèm --relink.
    targets = issues if "--relink" in sys.argv else [i for i in issues if i["code"] in created]
    print("Liên kết phụ thuộc:")
    link_dependencies(targets, numbers)
    print("\nXong: https://github.com/%s/issues" % REPO)


if __name__ == "__main__":
    main()
