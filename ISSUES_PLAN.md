# KẾ HOẠCH ISSUE — MATH DASH 1v1 (BẢN 2: CHIA THEO CHỨC NĂNG + OOAD, CHỜ DUYỆT)

> Nguồn: `PLAN.md`, `CONVENTION.md` (quy ước code), `REPORT_CONVENTION.md` (yêu cầu báo cáo + quy trình OOAD), khung stub trong `app/`.
> Thành viên: **@HungNguyenBa1811** (Hùng – nhóm trưởng) · **@Minahn210** (Hoàng – thành viên 2).
> Sau khi duyệt, mỗi mục `[Mx-yy]` bên dưới sẽ thành 1 GitHub issue (title, label, milestone, assignee, body giữ nguyên).
> Số thứ tự trong mỗi milestone là thứ tự nên làm.

**Thay đổi so với bản 1:**
- Bản 1 chia theo **tầng**: Hùng làm logic + Firebase, Hoàng làm giao diện + SQLite.
- Bản 2 chia theo **chức năng (use case)**: mỗi người làm trọn chức năng của mình, từ giao diện đến DAO, CSDL và thực thể.
  Lý do: đề yêu cầu *"bạn nào demo chức năng nào thì trình bày thiết kế cho chức năng đấy"*, gồm biểu đồ lớp và biểu đồ tuần tự cho cả frontend lẫn backend.
- Milestone đi theo đúng quy trình OOAD: **Phân tích → Thiết kế → Lập trình → Kiểm thử & Báo cáo**.
- Báo cáo là **bài cá nhân**: mỗi người một file, không còn báo cáo chung.

---

## 0. Yêu cầu của đề (bài làm cá nhân)

Mỗi người trình bày đúng các chức năng mình **demo**, theo thứ tự:

1. Mô tả chi tiết các chức năng mình làm bằng ngôn ngữ tự nhiên (specification).
2. Thiết kế CSDL (trình bày 1 lần cho tất cả chức năng của mình).
3. Thiết kế lớp thực thể (trình bày 1 lần cho tất cả chức năng của mình).
4. Lần lượt cho **từng** chức năng (xong chức năng 1 mới sang chức năng 2):
   - Thiết kế giao diện.
   - Biểu đồ lớp chi tiết (frontend + backend, client + server).
   - Biểu đồ tuần tự chi tiết.

Điểm: báo cáo thiết kế 30% + demo 30% điểm tổng kết. Vì vậy **ai demo chức năng nào thì phải tự code và tự vẽ chức năng đó**.

Đề bài nguyên văn, khung báo cáo, cách làm từng bước OOAD và checklist trước khi nộp: xem **`REPORT_CONVENTION.md`**.

---

## 1. Các quyết định cần duyệt

| # | Vấn đề | Đề xuất | Lý do |
|---|---|---|---|
| D1 | Firebase Realtime Database hay Firestore | **Realtime Database** | Có sẵn `onDisconnect()`, listener đơn giản, độ trễ thấp |
| D2 | Luật trận 1v1 | **60 giây/trận**, 2 người làm chung một dãy câu. Đúng: +10 + combo×2. Sai: 0 điểm, reset combo, **không mất mạng**. Hết giờ ai nhiều điểm hơn thắng, bằng điểm thì hòa | Đơn giản, dùng lại được `GameSession` |
| D3 | Đồng bộ câu hỏi | Chủ phòng tạo `seed` (long) ghi lên phòng; 2 máy cùng tạo `new ProblemDAO(new Random(seed))` | Không phải đẩy từng câu lên Firebase |
| D4 | Đăng nhập | Firebase **ẩn danh** + tên nhập ở màn Chính | Không phải làm màn đăng ký |
| D5 | Chia việc | **Theo chức năng** (mục 2.1). Mỗi người làm trọn chức năng của mình | Đúng yêu cầu báo cáo và demo cá nhân |
| D6 | Lõi dùng chung `MathProblem`, `ProblemDAO`, `GameSession` | Hùng viết (tuần 1 Hoàng bận). **Hoàng bắt buộc review PR** vì 3 lớp này nằm trong biểu đồ lớp của "Chơi offline" | Hoàng phải giải thích được khi bị hỏi lúc demo |
| D7 | Lớp DAO | Theo mẫu OOAD: **lớp cha giữ kết nối dùng chung** (`DAO` cho SQLite, `FirebaseDAO` cho Firebase), các DAO con kế thừa | Đúng tài liệu môn học |
| D8 | Tên bảng | Tiền tố `tbl` + tên lớp thực thể: `tblPlayer`, `tblGameRecord`, `tblMatchHistory`. Khóa ngoại `tblPlayerId` | Đúng bước 1 và bước 4 thiết kế CSDL |
| D9 | Thuộc tính dẫn xuất | **Không lưu** cột tính được từ cột khác: bỏ `high_score` (= MAX điểm trong `tblGameRecord`), `result` (suy từ 2 điểm), `% chính xác` | Đúng bước 5 thiết kế CSDL |
| D10 | Màn Cài đặt | 2 công tắc **Âm thanh / Rung**, lưu bằng SharedPreferences. Là chức năng đầu tiên bị cắt nếu trễ | Phạm vi nhỏ |
| D11 | Báo cáo | Mỗi người **một file riêng**. Phần chung (use case tổng quát, lớp thực thể, CSDL) làm chung để 2 bản không lệch nhau, nhưng mỗi người chỉ đưa vào báo cáo phần liên quan đến chức năng của mình | Đề ghi "bài làm cá nhân" |

---

## 2. Hợp đồng chung (chốt ở M1-01 và M2-07, sau đó KHÔNG đổi tùy tiện)

Mục này cố định **tên gọi** để 2 bản báo cáo và code khớp nhau. Thuộc tính và phương thức cụ thể do các issue phân tích/thiết kế quyết định.

### 2.1. Use case và người phụ trách

Tác nhân duy nhất: **Người chơi**. Đối thủ trong trận online cũng là một Người chơi.

| UC | Tên use case | Người làm và demo | Label |
|---|---|---|---|
| UC1 | Tìm trận | Hùng | `uc-tim-tran` |
| UC2 | Thi đấu online (gồm xử lý thoát trận, mất mạng) | Hùng | `uc-thi-dau-online` |
| UC3 | Quản lý lịch sử đấu (xem, xóa) | Hùng | `uc-lich-su` |
| UC4 | Chơi offline | Hoàng | `uc-choi-offline` |
| UC5 | Xem bảng vàng | Hoàng | `uc-bang-vang` |
| UC6 | Cài đặt âm thanh | Hoàng | `uc-cai-dat` |

### 2.2. Tên lớp

| Loại | Lớp | Chủ sở hữu |
|---|---|---|
| Thực thể | `Player`, `GameRecord` | Hoàng |
| Thực thể | `MathProblem`, `GameSession` (lõi dùng chung, D6) | Hùng |
| Thực thể | `Room`, `RoomPlayer`, `MatchHistory` | Hùng |
| DAO SQLite | `DAO` (lớp cha), `DatabaseHelper`, `PlayerDAO`, `GameRecordDAO` | Hoàng |
| DAO SQLite | `MatchHistoryDAO` | Hùng |
| DAO Firebase | `FirebaseDAO` (lớp cha), `AuthDAO`, `RoomDAO` | Hùng |
| Sinh câu hỏi | `ProblemDAO` (không kế thừa `DAO`, không truy cập CSDL) | Hùng |
| Điều khiển | `GameController` (offline) | Hoàng |
| Điều khiển | `MatchController` (online, Singleton) | Hùng |
| Giao diện | `FrmMainActivity`, `FrmGameActivity`, `FrmLeaderboardActivity`, `LeaderboardAdapter`, `FrmSettingsActivity` | Hoàng |
| Giao diện | `FrmLobbyActivity`, `FrmOnlineGameActivity`, `FrmHistoryActivity`, `HistoryAdapter` | Hùng |
| Tiện ích | `SoundManager` (Singleton) | Hoàng |

Lớp giao diện ở bước **phân tích** đặt tên `XxxView`; sang bước **thiết kế** ánh xạ sang Activity thật:

| Phân tích | Thiết kế |
|---|---|
| `MainView` | `FrmMainActivity` |
| `GameView`, `GameOverView` | `FrmGameActivity` (Game Over là overlay trong cùng Activity) |
| `LeaderboardView` | `FrmLeaderboardActivity` |
| `SettingsView` | `FrmSettingsActivity` |
| `LobbyView` | `FrmLobbyActivity` |
| `OnlineGameView`, `MatchResultView` | `FrmOnlineGameActivity` (Kết quả là overlay trong cùng Activity) |
| `HistoryView` | `FrmHistoryActivity` |

### 2.3. Lớp DAO dùng chung kết nối (D7)

```java
// dao/DAO.java — lớp cha của mọi DAO SQLite, chỉ giữ kết nối dùng chung
public abstract class DAO {
    protected static SQLiteDatabase con;

    public DAO(Context context) {
        if (con == null) {
            con = new DatabaseHelper(context.getApplicationContext()).getWritableDatabase();
        }
    }
}
// PlayerDAO, GameRecordDAO, MatchHistoryDAO: "extends DAO", dùng con để truy vấn

// dao/FirebaseDAO.java — lớp cha của mọi DAO Firebase
public abstract class FirebaseDAO {
    protected static DatabaseReference con;
    protected static FirebaseAuth auth;
    // constructor: nếu con == null thì lấy FirebaseDatabase.getInstance().getReference() và FirebaseAuth.getInstance()
}
// AuthDAO, RoomDAO: "extends FirebaseDAO"
```

### 2.4. CSDL

- **SQLite:** 3 bảng `tblPlayer`, `tblGameRecord`, `tblMatchHistory`. Cột cụ thể chốt ở M2-02.
- **Firebase Realtime Database:**

```
rooms/{roomId}
    status    : "waiting" | "playing" | "finished"
    seed      : long
    createdAt : ServerValue.TIMESTAMP
    players/
        {uid} : { name: String, score: int, online: bool, finished: bool }
```

### 2.5. Ba phiên bản kịch bản

| Phiên bản | Issue | Đối tượng xuất hiện | Đi kèm |
|---|---|---|---|
| v1 | M1-02, M1-03 | Người chơi ↔ Hệ thống | — |
| v2 | M1-07, M1-08 | Người chơi, lớp `XxxView`, lớp thực thể | Biểu đồ tuần tự phân tích, **khớp từng bước** |
| v3 | M2-05, M2-06 | Người chơi, `FrmXxxActivity` (boundary), Controller + DAO (control), thực thể (entity) | Biểu đồ tuần tự thiết kế, **khớp từng bước** |

---

## 3. Quy ước chung cho mọi issue

- **Branch:**
  - Tài liệu: `docs/M1-02-kich-ban-v1`.
  - Code: `feat/M3-03-tim-tran`.
  - Tạo nhanh bằng `gh issue develop <số> --checkout`.
- **PR:** mô tả ghi `Closes #<số>`. Người còn lại review rồi mới merge, kể cả PR tài liệu.
- **Thư mục tài liệu:**
  ```
  docs/
    chung/   use case tổng quát, lớp thực thể (phân tích + thiết kế), CSDL
    hung/    kịch bản v1/v2/v3, biểu đồ lớp và tuần tự cho UC1–UC3
    hoang/   kịch bản v1/v2/v3, biểu đồ lớp và tuần tự cho UC4–UC6
    TEST_REPORT.md
  ```
  - Kịch bản viết bằng Markdown để xem được thay đổi trên GitHub.
  - Biểu đồ vẽ bằng Visual Paradigm hoặc draw.io, xuất PNG lưu cạnh file Markdown.
  - Mỗi người giữ file dự án vẽ riêng (`docs/hung/*.vpp`, `docs/hoang/*.vpp`). File nhị phân không merge được, nên không sửa file của người kia.
- **Đưa vào báo cáo:** xong mỗi issue phân tích hoặc thiết kế thì dán ngay hình và chữ vào báo cáo cá nhân, không dồn đến cuối.
- **Definition of Done:**
  - Issue tài liệu (label `ooad`): đạt hết *Requirement* và tick hết checklist OOAD ở cuối issue.
  - Issue code: build pass, chạy được trên máy ảo, đạt hết *Requirement* và tick hết checklist CONVENTION.
- **Checklist CONVENTION** (tự thêm vào cuối issue code):
  - [ ] Không dùng Hilt/Dagger, Room, RxJava/LiveData/ViewModel, ViewBinding/DataBinding
  - [ ] Chỉ dùng `findViewById`, `SQLiteOpenHelper`, `Thread` + `runOnUiThread`
  - [ ] Mỗi hàm < 20 dòng, callback lồng tối đa 2 cấp
  - [ ] Dùng Anonymous Class, KHÔNG dùng lambda
  - [ ] Comment tiếng Việt cho các khối code quan trọng
- **Checklist OOAD** (tự thêm vào cuối issue tài liệu):
  - [ ] Làm đúng các bước của `REPORT_CONVENTION.md` mục 3
  - [ ] Tên UC, lớp, bảng khớp mục 2 của `ISSUES_PLAN.md`
  - [ ] Kịch bản và biểu đồ tuần tự khớp từng bước (cùng số bước, cùng thứ tự)
  - [ ] Mỗi hình có chú thích "Hình x: ..." và 3–5 dòng giải thích
  - [ ] Đã lưu Markdown + PNG vào `docs/` và dán vào báo cáo cá nhân
  - [ ] Người còn lại đã review

---

## 4. MILESTONE 1 — Phân tích · label `ooad`

### Mô tả milestone (dùng làm description cho Milestone `M1 - Phan tich`)

**Người phụ trách:** Hùng làm UC1–UC3, Hoàng làm UC4–UC6. M1-01 và M1-04 làm chung.
**Thời gian:** 21/10 → **28/10/2026**. Hùng làm phần mình trong tuần 21–25/10. Hoàng làm từ 26/10, vì tuần 19–25/10 Hoàng bận.

**Mục tiêu.** Hiểu hệ thống phải làm gì, từ góc nhìn người chơi, trước khi nghĩ đến code:
- Mô tả chức năng bằng ngôn ngữ tự nhiên.
- Kịch bản v1.
- Lớp thực thể.
- Biểu đồ lớp phân tích.
- Kịch bản v2 kèm biểu đồ tuần tự phân tích.

**Trong phạm vi:** 6 use case ở mục 2.1, chỉ dùng 3 loại đối tượng: tác nhân, lớp `XxxView`, lớp thực thể.

**Ngoài phạm vi:**
- DAO, Controller, Activity, kiểu dữ liệu, thuộc tính id → milestone Thiết kế.
- Code. Ngoại lệ duy nhất là M3-01 (dựng Firebase), được làm song song vì là hạ tầng, không phụ thuộc thiết kế.

**Sản phẩm bàn giao:**

| Loại | File |
|---|---|
| Chung | `docs/chung/01-pham-vi-use-case.md` + `uc-tong-quat.png`; `docs/chung/02-lop-thuc-the-phan-tich.md` + `.png` |
| Hùng | `docs/hung/1-kich-ban-v1.md`, `docs/hung/2-lop-phan-tich.md` + 3 PNG, `docs/hung/3-kich-ban-v2.md` + 3 PNG tuần tự |
| Hoàng | `docs/hoang/1-kich-ban-v1.md`, `docs/hoang/2-lop-phan-tich.md` + 3 PNG, `docs/hoang/3-kich-ban-v2.md` + 3 PNG tuần tự |

**Điều kiện hoàn thành milestone:**
1. 8 issue M1-01 → M1-08 đã đóng, mỗi issue có PR đã merge.
2. Mỗi phương thức trên biểu đồ tuần tự v2 đều có trên biểu đồ lớp phân tích tương ứng.
3. Mỗi lớp thực thể trên biểu đồ lớp phân tích đều có trên biểu đồ lớp thực thể chung (M1-04).

**Rủi ro đã biết:** Hoàng chỉ có 3 ngày (26–28/10) cho 4 issue phân tích. Hùng làm xong phần mình trước để Hoàng có mẫu bám theo.

### [M1-01] Phạm vi hệ thống + biểu đồ use case tổng quát · @HungNguyenBa1811
- **Phụ thuộc:** không có. **LÀM ĐẦU TIÊN.**
- **Mô tả:** Bước "Yêu cầu" của OOAD, phần dùng chung cho cả 2 báo cáo. Hùng soạn, Hoàng review, để 2 người có cùng một bức tranh hệ thống và cùng tên use case.
- **Input:** README (luật chơi), `PLAN.md`, mục 1 và 2 của tài liệu này.
- **Output:** `docs/chung/01-pham-vi-use-case.md` + `docs/chung/uc-tong-quat.png`, gồm:
  - **Mục tiêu và phạm vi:** app Android, chơi offline trên 1 máy hoặc đấu 1v1 online qua Firebase.
  - **Người dùng và chức năng:** Người chơi dùng được 6 chức năng ở mục 2.1.
  - **Biểu đồ use case tổng quát:** tác nhân Người chơi, 6 UC, các quan hệ `<<include>>` / `<<extend>>` nếu có (ví dụ Tìm trận include Đăng nhập ẩn danh).
  - **Mô tả mỗi UC 1 dòng**, kèm bảng UC → người làm.
- **Requirement:**
  - Hoàng đã review và approve PR.
  - Từ đây tên 6 UC dùng nguyên văn trong mọi tài liệu và báo cáo.
- **Constraint:** Không đi vào chi tiết từng UC; phần đó làm ở M1-02 và M1-03.

### [M1-02] Đặc tả + kịch bản v1: Tìm trận, Thi đấu online, Quản lý lịch sử đấu · @HungNguyenBa1811
- **Phụ thuộc:** M1-01.
- **Mô tả:** Mô tả chức năng bằng ngôn ngữ tự nhiên và viết kịch bản v1 (chỉ có Người chơi và Hệ thống) cho UC1–UC3. Phần đặc tả chính là mục 1 trong báo cáo của Hùng.
- **Input:** M1-01; luật D2, D3; schema mục 2.4.
- **Output:** `docs/hung/1-kich-ban-v1.md`. Với mỗi UC:
  - **Đặc tả:** viết theo mẫu luồng nghiệp vụ "Người chơi … → Giao diện … xuất hiện với … → Người chơi … → …". Mỗi giao diện ghi rõ gồm những ô nhập, nút, danh sách nào.
  - **Kịch bản chuẩn v1:** các bước đánh số, xen kẽ Người chơi và Hệ thống, dùng dữ liệu cụ thể (tên `HUNG`, đối thủ `HOANG`, tỉ số `120 - 96`…). Nếu có danh sách thì vẽ bảng dữ liệu mẫu.
  - **Kịch bản ngoại lệ**, tối thiểu:
    - Tìm trận: không có mạng; bấm Hủy khi đang chờ; 2 người bấm gần như cùng lúc.
    - Thi đấu online: đối thủ thoát giữa trận; mình mất mạng; hòa điểm.
    - Quản lý lịch sử đấu: chưa có trận nào; bấm xóa rồi chọn Hủy.
- **Requirement:**
  - Không có tên lớp hay tên hàm trong kịch bản v1.
  - Mỗi bước của hệ thống ghi rõ giao diện hiện ra gồm thành phần gì.
- **Constraint:** Dùng đúng tên UC ở M1-01.

### [M1-03] Đặc tả + kịch bản v1: Chơi offline, Xem bảng vàng, Cài đặt âm thanh · @Minahn210
- **Phụ thuộc:** M1-01.
- **Mô tả:** Như M1-02, cho UC4–UC6. Phần đặc tả chính là mục 1 trong báo cáo của Hoàng.
- **Input:** M1-01; luật offline trong README (6 giây/câu, 3 mạng, +10 + combo×2); `docs/hung/1-kich-ban-v1.md` để tham khảo cách viết.
- **Output:** `docs/hoang/1-kich-ban-v1.md`. Với mỗi UC: đặc tả, kịch bản chuẩn v1, kịch bản ngoại lệ. Ngoại lệ tối thiểu:
  - Chơi offline: bỏ trống tên (dùng `PLAYER_1`); hết giờ 1 câu; hết 3 mạng; đạt kỷ lục mới; bấm Back giữa ván.
  - Xem bảng vàng: chưa có ván nào.
  - Cài đặt âm thanh: tắt âm thanh rồi vào chơi.
- **Requirement:** Giống M1-02.
- **Constraint:** Dùng đúng tên UC ở M1-01.

### [M1-04] Lớp thực thể phân tích (toàn hệ thống) · @HungNguyenBa1811 @Minahn210
- **Phụ thuộc:** M1-02, M1-03.
- **Mô tả:** Trích lớp thực thể từ đặc tả và kịch bản v1, rồi vẽ **một** biểu đồ lớp thực thể chung cho cả hệ thống.
  - Hùng làm trước phần UC1–UC3; Hoàng bổ sung phần UC4–UC6 vào cùng biểu đồ.
  - Mỗi người đưa vào báo cáo phần liên quan đến chức năng của mình.
- **Input:** M1-02, M1-03; danh sách lớp thực thể ở mục 2.2.
- **Output:** `docs/chung/02-lop-thuc-the-phan-tich.md` + `.png`:
  - **Bảng trích danh từ:** mỗi danh từ trong đặc tả được giữ thành lớp, thành thuộc tính, hoặc bị loại, kèm lý do. Ví dụ "điểm", "combo", "mạng" là thuộc tính; "đồng hồ" bị loại vì không phải dữ liệu.
  - **Biểu đồ lớp thực thể:** mỗi lớp ghi tên tiếng Anh theo mục 2.2 kèm tên tiếng Việt, cùng các thuộc tính chính.
  - **Quan hệ và bội số giữa các lớp:**
    - Nếu thông tin của A nằm trong B thì vẽ quan hệ kết tập (aggregation) `A --<> B`, hình thoi ở phía B.
    - Ví dụ: thông tin Người chơi nằm trong Kết quả ván, nên vẽ `Player --<> GameRecord`.
- **Requirement:**
  - Mỗi lớp truy được về ít nhất một bước trong kịch bản v1.
  - Cả 2 người đã đồng ý biểu đồ.
- **Constraint:**
  - **Không cần đủ thuộc tính, không thêm id** (id thêm ở M2-01).
  - Không vẽ DAO, View, Controller.

### [M1-05] Biểu đồ lớp phân tích: Tìm trận, Thi đấu online, Quản lý lịch sử đấu · @HungNguyenBa1811
- **Phụ thuộc:** M1-02, M1-04.
- **Mô tả:** Bước phân tích tĩnh. Mỗi UC một biểu đồ, gồm các lớp View và các lớp thực thể liên quan.
- **Input:** Kịch bản v1 (M1-02); lớp thực thể (M1-04); bảng tên View ở mục 2.2.
- **Output:** `docs/hung/2-lop-phan-tich.md` + 3 PNG. Với mỗi UC:
  - **Lớp View:** mỗi giao diện một lớp, trừ thông báo và hộp thoại xác nhận. Ví dụ `MainView`, `LobbyView`, `OnlineGameView`, `MatchResultView`, `HistoryView`.
  - **Thành phần của View, đặt tiền tố theo loại:**
    - `in`: ô nhập.
    - `out`: thành phần hiển thị.
    - `sub`: nút bấm.
    - Có thể ghép, ví dụ `outsubListMatch`.
    - Ví dụ `LobbyView`: `outMyName`, `outOpponentName`, `outStatus`, `subCancel`.
  - **Phương thức:** mỗi xử lý hệ thống phải làm được trả lời 4 câu hỏi: tên hàm, đầu vào, đầu ra, gán cho lớp nào. Quy tắc gán:
    - Đầu ra là lớp thực thể nào thì gán vào lớp đó.
    - Nếu không, xét đầu vào thuộc lớp thực thể nào.
  - **Biểu đồ lớp:** View, thực thể và quan hệ giữa chúng.
- **Requirement:** Mỗi thành phần giao diện nêu trong kịch bản v1 đều có trong View tương ứng.
- **Constraint:** Chưa có DAO, Controller, Activity. Tên lớp giao diện kết thúc bằng `View`.

### [M1-06] Biểu đồ lớp phân tích: Chơi offline, Xem bảng vàng, Cài đặt âm thanh · @Minahn210
- **Phụ thuộc:** M1-03, M1-04.
- **Mô tả:** Như M1-05, cho UC4–UC6.
- **Input:** Kịch bản v1 (M1-03); lớp thực thể (M1-04); `docs/hung/2-lop-phan-tich.md` để tham khảo cách làm.
- **Output:** `docs/hoang/2-lop-phan-tich.md` + 3 PNG.
  - View dự kiến: `MainView` (`inUsername`, `subPlayOffline`…), `GameView`, `GameOverView`, `LeaderboardView`, `SettingsView`.
  - Phương thức gán vào thực thể theo quy tắc 4 câu hỏi.
- **Requirement:** Giống M1-05.
- **Constraint:** Giống M1-05.

### [M1-07] Kịch bản v2 + biểu đồ tuần tự phân tích: Tìm trận, Thi đấu online, Quản lý lịch sử đấu · @HungNguyenBa1811
- **Phụ thuộc:** M1-05.
- **Mô tả:** Bước phân tích động. Viết lại kịch bản chuẩn thành v2 và vẽ biểu đồ tuần tự khớp từng bước.
- **Input:** Kịch bản v1 (M1-02); biểu đồ lớp phân tích (M1-05).
- **Output:** `docs/hung/3-kich-ban-v2.md` + 3 PNG. Với mỗi UC:
  - **Kịch bản v2:** đánh số, mỗi bước là đúng một thông điệp giữa Người chơi, `XxxView` và lớp thực thể. Ví dụ "5. `LobbyView` gọi phương thức `findWaitingRoom()` của lớp `Room`".
  - **Biểu đồ tuần tự** đánh số giống kịch bản.
  - Ngoại lệ quan trọng nhất của UC vẽ bằng khung `alt`.
- **Requirement:**
  - Số bước trong kịch bản bằng số thông điệp trên biểu đồ, cùng thứ tự.
  - Mọi phương thức trên biểu đồ đã có ở M1-05.
- **Constraint:** Chỉ có 3 loại đối tượng: tác nhân, View, thực thể.

### [M1-08] Kịch bản v2 + biểu đồ tuần tự phân tích: Chơi offline, Xem bảng vàng, Cài đặt âm thanh · @Minahn210
- **Phụ thuộc:** M1-06.
- **Mô tả:** Như M1-07, cho UC4–UC6.
- **Input:** Kịch bản v1 (M1-03); biểu đồ lớp phân tích (M1-06); `docs/hung/3-kich-ban-v2.md` để tham khảo cách làm.
- **Output:** `docs/hoang/3-kich-ban-v2.md` + 3 PNG.
- **Requirement:** Giống M1-07.
- **Constraint:** Giống M1-07.

---

## 5. MILESTONE 2 — Thiết kế · label `ooad`

### Mô tả milestone (dùng làm description cho Milestone `M2 - Thiet ke`)

**Người phụ trách:** Hùng làm UC1–UC3, Hoàng làm UC4–UC6. M2-01, M2-02 làm chung; M2-07 do Hùng làm.
**Thời gian:** 26/10 → **01/11/2026**.

**Mục tiêu.** Biến kết quả phân tích thành bản thiết kế đủ chi tiết để code theo:
- Lớp thực thể thiết kế.
- CSDL.
- Biểu đồ lớp thiết kế theo MVC (boundary – control – entity).
- Kịch bản v3 kèm biểu đồ tuần tự thiết kế.

Đây là **4 phần chính của báo cáo**: CSDL, lớp thực thể, lớp chi tiết, tuần tự chi tiết.

**Trong phạm vi:**
- Thêm id, kiểu dữ liệu, kết tập/hợp thành vào lớp thực thể.
- Bảng `tbl*`, cây Firebase.
- Activity, Adapter, Controller, DAO với chữ ký hàm đầy đủ.
- Phác thảo giao diện từng màn.
- M2-07: sửa khung code cho khớp thiết kế.

**Ngoài phạm vi:** Thân hàm (milestone Lập trình).

**Sản phẩm bàn giao:**

| Loại | File |
|---|---|
| Chung | `docs/chung/03-lop-thuc-the-thiet-ke.md` + `.png`; `docs/chung/04-csdl.md` + `.png` |
| Hùng | `docs/hung/4-lop-thiet-ke.md` + 3 PNG + phác thảo giao diện; `docs/hung/5-kich-ban-v3.md` + 3 PNG tuần tự |
| Hoàng | `docs/hoang/4-lop-thiet-ke.md` + 3 PNG + phác thảo giao diện; `docs/hoang/5-kich-ban-v3.md` + 3 PNG tuần tự |
| Code | Khung stub khớp thiết kế (M2-07) |

**Điều kiện hoàn thành milestone:**
1. 7 issue M2-01 → M2-07 đã đóng.
2. Mỗi View ở bước phân tích ánh xạ sang đúng một Activity hoặc overlay (bảng ở mục 2.2).
3. Mỗi phương thức của thực thể ở bước phân tích đã được chuyển sang DAO hoặc Controller phù hợp.
4. Tên lớp và tên hàm trong code (sau M2-07) giống hệt trên biểu đồ.

**Rủi ro đã biết:**
- Firebase là bất đồng bộ, nên trên biểu đồ tuần tự kết quả trả về qua callback (`onDataChange`, `onComplete`).
- Quy ước nhóm: vẽ lời gọi DAO là một thông điệp, kết quả là một thông điệp trả về từ callback. Cả 2 người vẽ cùng một kiểu.

### [M2-01] Thiết kế lớp thực thể (toàn hệ thống) · @HungNguyenBa1811 @Minahn210
- **Phụ thuộc:** M1-04.
- **Mô tả:** Từ biểu đồ lớp thực thể phân tích, áp dụng 4 bước thiết kế lớp thực thể của OOAD. Mỗi người làm các lớp mình sở hữu (mục 2.2) trên cùng một biểu đồ.
- **Input:** `docs/chung/02-lop-thuc-the-phan-tich.md`.
- **Output:** `docs/chung/03-lop-thuc-the-thiet-ke.md` + `.png`, trình bày theo 4 bước:
  1. **Thêm thuộc tính khóa** `id` cho các lớp không kế thừa lớp khác. Với `RoomPlayer`, khóa là `uid`.
  2. **Thêm kiểu dữ liệu Java** cho mọi thuộc tính.
  3. **Chuyển mọi quan hệ liên kết (association) thành kết tập/hợp thành**, ghi rõ lớp nào là thành phần của lớp nào và bội số.
     - Ví dụ: `RoomPlayer` là thành phần (hợp thành) của `Room`; `Player` là thành phần của `GameRecord`.
  4. **Thêm thuộc tính đối tượng** tương ứng với các quan hệ ở bước 3.
     - `GameRecord` có `Player player`, thay cho `playerId` + `playerName` đang có trong stub.
     - `Room` có `List<RoomPlayer> players`.
- **Requirement:** Không còn đường liên kết trơn nào trên biểu đồ.
- **Constraint:** Thuộc tính dẫn xuất (D9) nếu cần thì là **phương thức** (ví dụ `MatchHistory.getResult()`), không phải thuộc tính lưu trữ.

### [M2-02] Thiết kế CSDL: SQLite + Firebase · @HungNguyenBa1811 @Minahn210
- **Phụ thuộc:** M2-01.
- **Mô tả:** Từ lớp thực thể thiết kế, làm theo 5 bước thiết kế CSDL của OOAD.
  - Hoàng làm `tblPlayer`, `tblGameRecord`.
  - Hùng làm `tblMatchHistory` và cây `rooms` trên Firebase.
- **Input:** `docs/chung/03-lop-thuc-the-thiet-ke.md`; mục 2.4.
- **Output:** `docs/chung/04-csdl.md` + `.png`:
  - **SQLite, trình bày đủ 5 bước:**
    1. Mỗi lớp thực thể cần lưu thành một bảng `tbl` + tên lớp.
    2. Thuộc tính không phải đối tượng thành cột.
    3. Quan hệ số lượng giữa các bảng.
    4. Khóa chính `id`; khóa ngoại `tblPlayerId` trong `tblGameRecord` và `tblMatchHistory`.
    5. Loại thuộc tính dẫn xuất theo D9.
  - **Câu lệnh `CREATE TABLE`** cho cả 3 bảng. M3-07 sẽ dán nguyên các câu này vào `DatabaseHelper`.
  - **Firebase:** ánh xạ `Room` và `RoomPlayer` sang cây `rooms`:
    - Mỗi lớp thành một node; thuộc tính thường thành field.
    - Quan hệ hợp thành thành node con; khóa là push id hoặc `uid`.
  - Lớp `MathProblem` và `GameSession` không lưu, ghi rõ lý do.
- **Requirement:**
  - Mỗi bảng có khóa chính.
  - Khóa ngoại đúng chiều 1-n.
  - Cột khớp với thuộc tính không phải đối tượng của lớp thực thể.
- **Constraint:** Không dùng Room ORM (CONVENTION). Không có cột dẫn xuất.

### [M2-03] Biểu đồ lớp thiết kế + phác thảo giao diện: Tìm trận, Thi đấu online, Quản lý lịch sử đấu · @HungNguyenBa1811
- **Phụ thuộc:** M1-05, M2-01, M2-02.
- **Mô tả:** Bước thiết kế tĩnh, mỗi UC một biểu đồ đủ 3 loại lớp. Đây là 2 mục "Thiết kế giao diện" và "Biểu đồ lớp chi tiết" trong báo cáo của Hùng.
- **Input:** Biểu đồ lớp phân tích (M1-05); thực thể và CSDL (M2-01, M2-02); mục 2.2, 2.3.
- **Output:** `docs/hung/4-lop-thiet-ke.md` + 3 PNG. Với mỗi UC:
  - **Thiết kế giao diện:**
    - Phác thảo từng màn (wireframe hoặc ảnh chụp layout).
    - Bảng thành phần: tên `id` trong layout (`btnCancel`, `tvOpponentName`…), loại View, chức năng.
  - **Lớp giao diện (boundary):** `FrmLobbyActivity`, `FrmOnlineGameActivity`, `FrmHistoryActivity`, `HistoryAdapter`, cùng các interface listener nếu dùng.
  - **Lớp điều khiển (control):**
    - `MatchController`.
    - `FirebaseDAO` ← `AuthDAO`, `RoomDAO`.
    - `DAO` ← `MatchHistoryDAO`.
    - `ProblemDAO`.
    - Mọi phương thức ghi đủ tham số và kiểu trả về.
  - **Lớp thực thể:** `Room`, `RoomPlayer`, `MatchHistory`, `Player`, `MathProblem`, `GameSession`.
  - **Mô tả từng lớp 1–2 dòng**, theo mẫu tài liệu OOAD: "`RoomDAO` có phương thức `findWaitingRoom` để …".
- **Requirement:** Đi được từ mỗi nút trên giao diện, qua Controller/DAO, đến thực thể trên biểu đồ.
- **Constraint:** Tên lớp và hàm phải là tên sẽ dùng trong code; M2-07 sẽ khóa lại.

### [M2-04] Biểu đồ lớp thiết kế + phác thảo giao diện: Chơi offline, Xem bảng vàng, Cài đặt âm thanh · @Minahn210
- **Phụ thuộc:** M1-06, M2-01, M2-02.
- **Mô tả:** Như M2-03, cho UC4–UC6.
- **Input:** Biểu đồ lớp phân tích (M1-06); M2-01, M2-02; mục 2.2, 2.3; `docs/hung/4-lop-thiet-ke.md` để tham khảo cách làm.
- **Output:** `docs/hoang/4-lop-thiet-ke.md` + 3 PNG. Với mỗi UC:
  - **Thiết kế giao diện:** phác thảo và bảng thành phần.
    - Màn chơi offline cần: tên, BEST, điểm, combo `xN`, mạng `♥ ♥ ♥`, thanh thời gian, phép tính, lưới 2x2 nút đáp án, dòng phản hồi, overlay Game Over.
  - **Lớp giao diện (boundary):** `FrmMainActivity`, `FrmGameActivity`, `FrmLeaderboardActivity`, `LeaderboardAdapter`, `FrmSettingsActivity`.
  - **Lớp điều khiển (control):**
    - `GameController`.
    - `DAO` ← `PlayerDAO`, `GameRecordDAO`.
    - `DatabaseHelper`, `ProblemDAO`, `SoundManager`.
  - **Lớp thực thể:** `Player`, `GameRecord`, `MathProblem`, `GameSession`.
  - **Mô tả từng lớp 1–2 dòng.**
- **Requirement:** Giống M2-03.
- **Constraint:** Giống M2-03.

### [M2-05] Kịch bản v3 + biểu đồ tuần tự thiết kế: Tìm trận, Thi đấu online, Quản lý lịch sử đấu · @HungNguyenBa1811
- **Phụ thuộc:** M1-07, M2-03.
- **Mô tả:** Bước thiết kế động. Đây là mục "Biểu đồ tuần tự chi tiết" trong báo cáo của Hùng.
- **Input:** Kịch bản v2 (M1-07); biểu đồ lớp thiết kế (M2-03).
- **Output:** `docs/hung/5-kich-ban-v3.md` + 3 PNG. Với mỗi UC:
  - **Kịch bản v3:** đánh số theo mẫu tài liệu OOAD, mỗi bước một thông điệp. Ví dụ một đoạn:
    - "Người chơi nhấn nút CHƠI NGAY trên `FrmMainActivity`."
    - "Phương thức `onClick` được gọi."
    - "`onClick` gọi `FrmLobbyActivity`."
    - "`findMatch` gọi `findWaitingRoom` của `RoomDAO`."
    - "`RoomDAO` đóng gói kết quả vào đối tượng `Room`."
    - "`Room` trả về cho `findWaitingRoom`."
  - **Biểu đồ tuần tự** đủ boundary, control (Controller + DAO) và entity, đánh số khớp kịch bản.
  - Ngoại lệ chính (đối thủ thoát, mất mạng) vẽ bằng khung `alt`.
- **Requirement:** Số bước bằng số thông điệp, cùng thứ tự. Mọi hàm trên biểu đồ có trong M2-03.
- **Constraint:** Callback Firebase vẽ theo quy ước nêu trong mô tả milestone.

### [M2-06] Kịch bản v3 + biểu đồ tuần tự thiết kế: Chơi offline, Xem bảng vàng, Cài đặt âm thanh · @Minahn210
- **Phụ thuộc:** M1-08, M2-04.
- **Mô tả:** Như M2-05, cho UC4–UC6. Đây là mục "Biểu đồ tuần tự chi tiết" trong báo cáo của Hoàng.
- **Input:** Kịch bản v2 (M1-08); biểu đồ lớp thiết kế (M2-04); `docs/hung/5-kich-ban-v3.md` để tham khảo cách làm.
- **Output:** `docs/hoang/5-kich-ban-v3.md` + 3 PNG.
  - Bộ đếm giờ (`CountDownTimer`) vẽ như một đối tượng điều khiển.
  - Truy vấn SQLite trong `Thread` vẽ như lời gọi từ Activity tới DAO.
- **Requirement:** Giống M2-05.
- **Constraint:** Mọi hàm trên biểu đồ có trong M2-04.

### [M2-07] Chốt khung code theo thiết kế · @HungNguyenBa1811
- **Phụ thuộc:** M2-01, M2-02, M2-03, M2-04.
- **Mô tả:** Sửa khung stub hiện có cho khớp bản thiết kế, để lúc lập trình 2 người không phải đổi tên lớp hay chữ ký hàm nữa. Hoàng review.
- **Input:** Biểu đồ lớp thiết kế (M2-03, M2-04); thực thể (M2-01); khung stub hiện tại.
- **Output:**
  - **Entity:** thuộc tính, kiểu, constructor, getter/setter đúng như M2-01. Cụ thể:
    - Sửa `GameRecord` dùng `Player player`.
    - Sửa `Player` bỏ `highScore`.
    - Thêm `MatchHistory`, `Room`, `RoomPlayer`.
  - **DAO:**
    - `dao/DAO.java` theo mục 2.3.
    - `PlayerDAO`, `GameRecordDAO`, `MatchHistoryDAO` `extends DAO`.
    - `FirebaseDAO`, `AuthDAO`, `RoomDAO` theo mục 2.3; nếu M3-01 chưa merge thì để TODO.
    - Mọi hàm có chữ ký đúng như biểu đồ, thân để `// TODO`.
  - **Controller và Frm:** đủ lớp và chữ ký theo biểu đồ.
    - Activity mới chỉ có `setContentView` và được khai báo trong Manifest.
    - Layout tạm chỉ hiện tên màn.
  - **Tài liệu:** `PLAN.md` và README dùng tên bảng `tbl*`.
- **Requirement:**
  - Build pass, `gradlew test` pass.
  - Hoàng đối chiếu: tên lớp và tên hàm trong code trùng 100% với biểu đồ của mình.
- **Constraint:** Chỉ có chữ ký và `// TODO`, không viết logic.

---

## 6. MILESTONE 3 — Lập trình · label theo UC

### Mô tả milestone (dùng làm description cho Milestone `M3 - Lap trinh`)

**Người phụ trách:** Hùng làm M3-01 → M3-06 (hạ tầng Firebase, lõi câu hỏi, UC1–UC3). Hoàng làm M3-07 → M3-10 (CSDL SQLite, màn Chính, UC4–UC6).
**Thời gian:** 31/10 → **10/11/2026**. Riêng M3-01 làm từ tuần 1, song song với phân tích.

**Mục tiêu.** Code đúng như bản thiết kế. Mỗi người code trọn chức năng mình sẽ demo: layout, Activity, Controller, DAO, câu SQL / truy vấn Firebase.

**Trong phạm vi:** Mọi lớp ở mục 2.2; âm thanh và rung; unit test cho lõi.

**Ngoài phạm vi** (KHÔNG làm):
- Đăng nhập email, chơi từ 3 người trở lên, chống gian lận.
- Đổi tên lớp hoặc chữ ký hàm so với thiết kế. Nếu bắt buộc phải đổi thì sửa luôn biểu đồ trong cùng PR.

**Sản phẩm bàn giao:** Code của 6 UC chạy được.
- Hùng: `FrmLobbyActivity`, `FrmOnlineGameActivity`, `FrmHistoryActivity`, `HistoryAdapter`, `MatchController`, `FirebaseDAO`, `AuthDAO`, `RoomDAO`, `MatchHistoryDAO`, `MathProblem`, `ProblemDAO`, `GameSession`, `Room`, `RoomPlayer`, `MatchHistory`, 2 unit test.
- Hoàng: `FrmMainActivity`, `FrmGameActivity`, `FrmLeaderboardActivity`, `LeaderboardAdapter`, `FrmSettingsActivity`, `GameController`, `DAO`, `DatabaseHelper`, `PlayerDAO`, `GameRecordDAO`, `Player`, `GameRecord`, `SoundManager`, `res/raw/*.mp3`.

**Quy tắc sửa file (tránh conflict):**
- Mỗi người chỉ sửa file mình sở hữu ở mục 2.2.
- File dùng chung: `AndroidManifest.xml`, `build.gradle.kts`, `libs.versions.toml`, `DatabaseHelper` (chỉ Hoàng sửa).
  - Pull `main` trước khi sửa.
  - Chỉ thêm đúng dòng mình cần.
  - Merge sớm, không giữ nhánh lâu.

**Điều kiện hoàn thành milestone:**
1. 10 issue M3-01 → M3-10 đã đóng.
2. `gradlew test` pass.
3. Từ màn Chính mở được mọi chức năng; bấm Back ở mọi màn không crash; xoay màn hình không crash.
4. Trên 2 máy ảo đấu được 1 trận trọn vẹn, và trận đó hiện trong Lịch sử của cả 2 máy.
5. Tìm `"-> "` trong `app/src/main/java` không ra kết quả nào (không dùng lambda).

**Rủi ro đã biết:**
- Khi mất mạng đột ngột, Firebase có thể mất vài chục giây mới báo `onDisconnect`. Chấp nhận điều này và ghi vào báo cáo.
- `VibrationEffect` chỉ có từ API 26, trong khi minSdk là 24. Phải rẽ nhánh theo `Build.VERSION.SDK_INT`.
- Hoàng cần M3-02 (lõi câu hỏi) trước khi làm M3-08. Hùng phải merge M3-02 **trước 03/11**.

### [M3-01] Hạ tầng Firebase: FirebaseDAO + đăng nhập ẩn danh · @HungNguyenBa1811
- **Phụ thuộc:** không có. Là hạ tầng nên làm song song với phân tích ở tuần 1.
- **Mô tả:** Tạo project Firebase, nối với app, viết lớp cha `FirebaseDAO` và `AuthDAO`. Phục vụ UC1 Tìm trận (include đăng nhập ẩn danh).
- **Input:** applicationId `ptit.e23cnpm01.nguyenbahung.math_dash`; mục 2.3.
- **Output:**
  - **Cấu hình:**
    - File `app/google-services.json`.
    - Plugin `com.google.gms.google-services`.
    - Firebase BoM + `firebase-auth` + `firebase-database` trong `libs.versions.toml`.
    - Quyền `INTERNET` và `ACCESS_NETWORK_STATE` trong Manifest.
  - **Database Rules:** chỉ user đã đăng nhập (`auth != null`) mới được đọc/ghi; có `.indexOn: "status"` cho `rooms`.
  - **Code:**
    - `dao/FirebaseDAO.java` theo mục 2.3.
    - `dao/AuthDAO.java extends FirebaseDAO` có `signIn(AuthCallback)` và `getUid()`.
    - Interface `AuthCallback { onSuccess(String uid); onError(String message); }`.
  - **Thêm @Minahn210 vào Firebase project.**
- **Requirement:**
  - Logcat in `Firebase OK`.
  - Lần đầu mở app tạo UID mới; tắt app mở lại vẫn giữ UID cũ.
  - Mất mạng thì gọi `onError` với thông báo tiếng Việt, app không crash.
- **Constraint:** Không để rules public (`.read/.write: true`). Không dùng Firestore.

### [M3-02] Lõi câu hỏi và luật điểm: MathProblem, ProblemDAO, GameSession · @HungNguyenBa1811
- **Phụ thuộc:** M2-07.
- **Mô tả:** Lõi dùng chung cho UC2 (online) và UC4 (offline), theo D6. **Hoàng bắt buộc review PR** để giải thích được khi demo "Chơi offline".
- **Input:** Stub sau M2-07; luật trong README và D2; biểu đồ lớp M2-03, M2-04.
- **Output:**
  - **`MathProblem`:** `getExpression()` trả chuỗi kiểu `"12 + 7"`; `isCorrect(int)`.
  - **`GameSession`:**
    - `reset()`.
    - `recordCorrectAnswer()`: cộng `10 + streak * 2`, trả về số điểm vừa cộng.
    - `recordWrongAnswer()`: reset combo, trừ 1 mạng; hết mạng thì game over.
    - Chế độ online (D2): trả lời sai không trừ mạng.
  - **`ProblemDAO.generateProblem(int level)`**, với `level = min(questionIndex / 5 + 1, 4)`:

    | Level | Phép tính | Khoảng số |
    |---|---|---|
    | 1 | `+ −` | 1–10 |
    | 2 | `+ −` | 1–30 |
    | 3 | `+ − ×` | `+ −`: 1–50, `×`: 2–9 |
    | 4 | `+ − ×` | `+ −`: 1–100, `×`: 2–12 |

    4 đáp án khác nhau, không âm, có đáp án đúng, xáo trộn bằng `Collections.shuffle(list, random)`.
  - **Unit test `GameSessionTest`:** trạng thái ban đầu; combo +10 rồi +12; sai thì reset combo và trừ mạng; sai 3 lần thì game over; online sai không trừ mạng.
  - **Unit test `ProblemDAOTest`:**
    - Sinh 1000 câu mỗi level: không âm, 4 đáp án khác nhau, có đáp án đúng.
    - **2 `ProblemDAO` cùng seed sinh ra 30 câu giống hệt nhau.**
- **Requirement:** `gradlew test` pass. Hoàng đã approve PR.
- **Constraint:**
  - Entity không import `android.*`.
  - Mọi chỗ random đi qua `this.random`, không dùng `Math.random()` hay `new Random()` bên trong hàm. Nếu vi phạm, 2 máy cùng seed sẽ ra câu khác nhau.

### [M3-03] UC Tìm trận: FrmLobbyActivity + RoomDAO + MatchController · @HungNguyenBa1811
- **Phụ thuộc:** M3-01, M2-07.
- **Mô tả:** Code UC1 đúng theo M2-03 và M2-05. Người chơi bấm CHƠI NGAY thì app tìm phòng `waiting`: có thì vào, không có thì tạo phòng mới rồi chờ.
- **Input:** Biểu đồ lớp và tuần tự UC1; schema mục 2.4; tên người chơi từ Intent.
- **Output:**
  - **`RoomDAO extends FirebaseDAO`:**
    - `findWaitingRoom()`: query `orderByChild("status").equalTo("waiting").limitToFirst(1)`, kết quả đóng gói vào `Room`.
    - `createRoom(seed)`.
    - `joinRoom(roomId)`: dùng `runTransaction`.
  - **`MatchController`:**
    - `findMatch(name, listener)`.
    - `cancelFind()`: nếu mình là chủ phòng thì xóa phòng.
  - **Xử lý phòng chờ:**
    - Khi tạo phòng: `onDisconnect().removeValue()`.
    - Khi có người vào: `onDisconnect().cancel()`.
  - **`FrmLobbyActivity`** + layout: tên mình; tên đối thủ hoặc "Đang tìm đối thủ..." kèm ProgressBar; dòng trạng thái; nút HỦY. Có đối thủ thì sau 1.5s tự chuyển sang `FrmOnlineGameActivity`.
- **Requirement:**
  - 2 máy bấm gần như cùng lúc thì vào chung 1 phòng.
  - Máy thứ 3 phải tạo phòng mới.
  - Đủ 2 người thì phòng chuyển sang `playing`.
  - Bấm HỦY hoặc kill app khi đang chờ thì phòng biến mất trên Console.
- **Constraint:**
  - Join **bắt buộc** qua transaction, kiểm tra `status == "waiting"` và số người < 2 *bên trong* transaction.
  - Activity không gọi Firebase trực tiếp, chỉ gọi `MatchController`.
  - Nếu M3-07 chưa xong, test bằng cách mở thẳng `FrmLobbyActivity` với tên mặc định.

### [M3-04] UC Thi đấu online: FrmOnlineGameActivity + đồng bộ realtime · @HungNguyenBa1811
- **Phụ thuộc:** M3-02, M3-03.
- **Mô tả:** Code UC2 (luồng chính) đúng theo M2-03 và M2-05. 2 máy chơi cùng một dãy câu theo seed, mỗi máy ghi điểm của mình và lắng nghe điểm đối thủ.
- **Input:** Biểu đồ UC2; phòng đang `playing`; `seed`; uid đối thủ.
- **Output:**
  - **`MatchController`:**
    - `startGame(listener)`: dựng `ProblemDAO(new Random(seed))`; `CountDownTimer` 60s tick mỗi 1s.
    - `submitAnswer(value)`: chấm bằng `GameSession`, rồi `setValue` vào `players/{myUid}/score`.
    - `addValueEventListener` trên `players/{opponentUid}/score`.
    - Hết giờ: set `finished = true`. Cả 2 cùng `finished` (hoặc quá 3s sau khi hết giờ) thì gọi `onMatchEnd(MatchHistory)`.
  - **`FrmOnlineGameActivity`** + layout: đồng hồ 60s, phép tính, lưới 2x2 nút đáp án, điểm **MÌNH vs ĐỐI THỦ**, overlay THẮNG / THUA / HÒA có tỉ số và nút "Về menu".
- **Requirement:**
  - 2 máy thấy cùng một dãy câu.
  - Điểm đối thủ cập nhật dưới 1s khi mạng bình thường.
  - Kết quả 2 máy khớp nhau (A `WIN` thì B `LOSE`, hoặc cả 2 `DRAW`).
- **Constraint:**
  - Chỉ ghi vào node của chính mình.
  - `removeEventListener` và hủy timer khi kết thúc.
  - Không tính điểm trong Activity.

### [M3-05] UC Thi đấu online: xử lý thoát trận và mất mạng · @HungNguyenBa1811
- **Phụ thuộc:** M3-04.
- **Mô tả:** Code các kịch bản ngoại lệ của UC2 (khung `alt` trong M2-05), để không bao giờ để trận bị treo.
- **Input:** Kịch bản ngoại lệ UC2 (M1-02); biểu đồ tuần tự M2-05.
- **Output:**
  - Khi vào trận: `onDisconnect().setValue(false)` cho `players/{uid}/online`.
  - Đối thủ `online == false` thì gọi `onOpponentLeft()` và tính mình thắng.
  - `leaveMatch()` (nút Back + `AlertDialog` "Thoát sẽ bị xử thua?"): set `online = false`, mình bị xử thua.
  - Lắng nghe `.info/connected`; mất kết nối thì gọi `onConnectionLost()`.
- **Requirement:** Pass cả 4 test case:
  1. Máy A đang chờ thì kill app → phòng biến mất.
  2. Đang chơi, B bấm Back → A thắng ngay.
  3. Đang chơi, B tắt Wi-Fi → A nhận "Đối thủ đã thoát". Firebase có thể mất vài chục giây mới phát hiện, điều này chấp nhận được.
  4. A mất mạng → A thấy thông báo, app không crash.
- **Constraint:** Không còn phòng `waiting` nào tồn tại khi không có người chờ.

### [M3-06] UC Quản lý lịch sử đấu: MatchHistoryDAO + FrmHistoryActivity · @HungNguyenBa1811
- **Phụ thuộc:** M3-04, M3-07.
- **Mô tả:** Code UC3 đúng theo M2-03 và M2-05: lưu kết quả khi trận kết thúc, xem danh sách, xóa một trận.
- **Input:** Bảng `tblMatchHistory` (M2-02, đã được tạo trong `DatabaseHelper` ở M3-07); `DAO` và `PlayerDAO` (M3-07).
- **Output:**
  - **`MatchHistoryDAO extends DAO`:**
    - `insert(MatchHistory)`.
    - `getAll(Player)`: mới nhất xếp trước.
    - `delete(int id)`.
  - **Lưu kết quả:** khi `onMatchEnd`, `FrmOnlineGameActivity` gọi `insert` trong `Thread`.
  - **`FrmHistoryActivity`** + `HistoryAdapter` (Pattern 1) + `item_history.xml`:
    - Mỗi dòng gồm thời gian, đối thủ, tỉ số `X - Y`, kết quả: WIN màu `arcade_green`, LOSE màu `arcade_red`, DRAW màu vàng.
    - Kết quả tính bằng `getResult()`, không lưu vào CSDL.
  - **Xóa:** nhấn giữ 1 dòng thì hiện `AlertDialog` "Xóa trận này?"; đồng ý thì `delete` + `notifyItemRemoved`.
  - Thêm `androidx.recyclerview` vào `libs.versions.toml` nếu M3-09 chưa thêm.
- **Requirement:**
  - Đấu xong 1 trận thì Lịch sử có thêm dòng mới.
  - Chưa có trận thì hiện "Chưa có trận nào".
  - Xoay màn hình không crash.
- **Constraint:**
  - SQLite chạy trong `Thread` + `runOnUiThread` (Pattern 3).
  - `delete` dùng placeholder `"id = ?"`, không nối chuỗi.
  - Không dùng DiffUtil, ListAdapter.

### [M3-07] CSDL SQLite + DAO cha + PlayerDAO + màn hình Chính · @Minahn210
- **Phụ thuộc:** M2-07.
- **Mô tả:** Nền SQLite cho cả nhóm và màn mở đầu của UC4 (nhập tên). Hùng cần issue này cho M3-06, nên **merge trước 04/11**.
- **Input:** Câu `CREATE TABLE` ở M2-02; mục 2.3; biểu đồ lớp M2-04.
- **Output:**
  - **`DatabaseHelper.onCreate`:** tạo đủ 3 bảng `tblPlayer`, `tblGameRecord`, `tblMatchHistory`, dán nguyên câu lệnh từ M2-02. `onUpgrade`: DROP rồi tạo lại.
  - **`dao/DAO.java`** đúng mục 2.3.
  - **`PlayerDAO extends DAO`:** `findOrCreateByUsername(String)`: trim tên, viết hoa; tên rỗng thì dùng `PLAYER_1`.
  - **`FrmMainActivity`** + layout:
    - Tiêu đề game, ô nhập `etUsername`.
    - 5 nút, đều truyền tên qua Intent:
      - `btnPlayOnline` "CHƠI NGAY" → `FrmLobbyActivity`.
      - `btnPlayOffline` "CHƠI OFFLINE" → `FrmGameActivity`.
      - `btnHistory` "LỊCH SỬ" → `FrmHistoryActivity`.
      - `btnLeaderboard` "BẢNG VÀNG" → `FrmLeaderboardActivity`.
      - `btnSettings` "CÀI ĐẶT" → `FrmSettingsActivity`.
- **Requirement:**
  - Log: gọi `findOrCreateByUsername("hung")` 2 lần thì trả về cùng id.
  - Tên `O'NEIL` không làm lỗi SQL.
  - Mỗi nút mở đúng màn.
- **Constraint:**
  - Dùng `ContentValues` và placeholder `?`, không nối chuỗi.
  - Truy vấn trong `Thread`.
  - `findViewById` và `new View.OnClickListener()`, không dùng lambda.

### [M3-08] UC Chơi offline: GameController + FrmGameActivity + lưu kết quả · @Minahn210
- **Phụ thuộc:** M3-02, M3-07.
- **Mô tả:** Code UC4 đúng theo M2-04 và M2-06. Đây là chức năng chính Hoàng demo.
- **Input:** Biểu đồ UC4; lõi `GameSession` và `ProblemDAO` (M3-02); `PlayerDAO` (M3-07).
- **Output:**
  - **`GameController`:**
    - Constructor nhận `(GameSession, ProblemDAO, int bestScore, GameControllerListener)`.
    - `startGame()`.
    - `CountDownTimer` 6s mỗi câu; hết giờ tính như trả lời sai.
    - `submitAnswer()`.
    - Level tăng theo số câu đã ra.
    - `onPause`/`onResume`/`onDestroy`: dừng hoặc chạy lại timer.
    - Hết mạng thì gọi `onGameOver(session, score > bestScore)`.
  - **`GameRecordDAO extends DAO`:**
    - `insertRecord(GameRecord)`.
    - `getBestScore(Player)`: `SELECT MAX(score)`, thay cho cột `high_score` đã bỏ.
  - **`FrmGameActivity`** + layout theo phác thảo M2-04:
    - Lưới 2x2 nút đáp án, cao ≥ 72dp, chữ monospace 26sp.
    - Overlay Game Over: điểm cuối, kỷ lục, nút CHƠI LẠI / VỀ MENU.
    - Khi `onGameOver`: lưu `GameRecord` trong `Thread`.
- **Requirement:**
  - Hết giờ thì mất mạng; hết 3 mạng thì hiện Game Over.
  - Ván vừa chơi được lưu vào `tblGameRecord`.
  - Điểm vượt kỷ lục thì hiện "KỶ LỤC MỚI".
- **Constraint:**
  - Không tính điểm trong Activity (logic nằm ở `GameController` / `GameSession`).
  - Hủy timer trong `onDestroy()`.

### [M3-09] UC Xem bảng vàng: FrmLeaderboardActivity + LeaderboardAdapter · @Minahn210
- **Phụ thuộc:** M3-08.
- **Mô tả:** Code UC5 đúng theo M2-04 và M2-06. Đây là phần thầy hay yêu cầu xóa đi và code lại ngay tại chỗ (Adapter + SQLite).
- **Input:** Biểu đồ UC5; `tblGameRecord`, `tblPlayer`.
- **Output:**
  - **`GameRecordDAO.getTopRecords(int limit)`:**
    - JOIN `tblGameRecord` với `tblPlayer`, sắp `score DESC`.
    - Mỗi dòng đóng gói vào `GameRecord` có `Player` bên trong.
  - Thêm `androidx.recyclerview` vào `libs.versions.toml` và `app/build.gradle.kts`.
  - **`FrmLeaderboardActivity`** + `LeaderboardAdapter` (Pattern 1) + `item_leaderboard.xml`:
    - Hiện top 20.
    - Mỗi dòng gồm hạng, tên, điểm, combo cao nhất, % chính xác. % chính xác tính khi hiển thị, không lưu.
- **Requirement:**
  - Chơi 3 ván thì Bảng vàng xếp đúng thứ tự.
  - Chưa có dữ liệu thì hiện "Chưa có kỷ lục".
- **Constraint:**
  - Truy vấn trong `Thread` (Pattern 3).
  - Không dùng DiffUtil, ListAdapter, ViewBinding.

### [M3-10] UC Cài đặt âm thanh: SoundManager + FrmSettingsActivity · @Minahn210
- **Phụ thuộc:** M3-08.
- **Mô tả:** Code UC6 đúng theo M2-04 và M2-06: phản hồi âm thanh và rung khi trả lời, người chơi tắt/bật được. Đây là chức năng **bị cắt đầu tiên** nếu trễ (mục 8).
- **Input:** Biểu đồ UC6; Pattern 4 (Singleton).
- **Output:**
  - `res/raw/correct.mp3`, `wrong.mp3`, `timeout.mp3`: lấy từ nguồn miễn phí, ghi lại nguồn để đưa vào báo cáo.
  - **`util/SoundManager.java`** dạng Singleton:
    - `playSound(context, resId)` và `vibrate(context, ms)`.
    - Kiểm tra cờ trong SharedPreferences `"settings"` trước khi phát âm hoặc rung.
  - Quyền `VIBRATE` trong Manifest.
  - **`FrmSettingsActivity`** + layout: 2 Switch Âm thanh / Rung.
  - Gắn vào `FrmGameActivity` (khi trả lời và khi hết giờ). Màn online do Hùng gắn ở M4-01.
- **Requirement:** Bật/tắt có hiệu lực ngay và vẫn giữ sau khi tắt app mở lại.
- **Constraint:**
  - `release()` MediaPlayer cũ trước khi tạo cái mới.
  - Kiểm tra `Build.VERSION.SDK_INT` trước khi dùng `VibrationEffect`.
  - Mỗi file âm thanh < 100KB.

---

## 7. MILESTONE 4 — Kiểm thử & Báo cáo

### Mô tả milestone (dùng làm description cho Milestone `M4 - Kiem thu & Bao cao`)

**Người phụ trách:** M4-01 làm chung. M4-02 là của Hùng, M4-03 là của Hoàng; mỗi người tự viết báo cáo của mình.
**Thời gian:** 10/11 → **15/11/2026**. Hai issue báo cáo mở ngay từ đầu dự án, và mỗi issue phân tích/thiết kế xong là dán luôn vào báo cáo.

**Mục tiêu.** Chứng minh app chạy đúng trên 2 máy và nộp 2 bài báo cáo cá nhân đúng khung đề (mục 0).

**Trong phạm vi:**
- Gắn âm thanh vào màn online.
- Kiểm thử 2 máy và đo độ trễ.
- Chụp ảnh giao diện từ app thật.
- Hoàn thiện 2 báo cáo.

**Ngoài phạm vi:**
- **Không thêm tính năng mới.** Lỗi phát hiện được thì mở issue mới, label `bug`, gắn vào milestone này.
- Ý tưởng mới thì mở issue label `enhancement`, không gắn milestone.

**Sản phẩm bàn giao:**
- `docs/TEST_REPORT.md`.
- APK debug.
- `docs/BaoCao_Hung.docx` và `docs/BaoCao_Hoang.docx`.

**Điều kiện hoàn thành milestone:**
1. M4-01 → M4-03 và mọi issue `bug` trong milestone đều đã đóng.
2. Chạy được trên 2 máy:
   - Luồng online: Chính → Tìm trận → Thi đấu → Kết quả → Lịch sử có dòng mới.
   - Luồng offline: Chính → Chơi offline → Game Over → Bảng vàng có dòng mới.
3. Mỗi báo cáo có đủ các mục theo thứ tự đề yêu cầu. Mỗi hình và mỗi tên lớp đều khớp với code.

### [M4-01] Ghép nối, kiểm thử chéo trên 2 máy + đo độ trễ · @HungNguyenBa1811 @Minahn210
- **Phụ thuộc:** Toàn bộ M3.
- **Mô tả:** Chạy toàn bộ app trên 2 máy ảo (hoặc 2 điện thoại), ghi kết quả kiểm thử.
- **Input:** Kịch bản chuẩn và ngoại lệ của 6 UC (M1-02, M1-03); test case của M3-03, M3-05.
- **Output:**
  - Hùng gắn `SoundManager` vào `FrmOnlineGameActivity`.
  - **`docs/TEST_REPORT.md`:**
    - Ghi môi trường test: máy ảo hay máy thật, phiên bản Android, loại mạng.
    - Bảng test case: Bước / Kỳ vọng / Thực tế / Pass-Fail.
    - Mỗi UC tối thiểu kịch bản chuẩn + các ngoại lệ đã viết.
  - **Độ trễ:**
    - Log `System.currentTimeMillis()` lúc máy A gửi điểm và lúc máy B nhận được.
    - 2 máy ảo trên cùng 1 PC dùng chung đồng hồ nên trừ trực tiếp được.
    - Đo 10 lần, ghi trung bình.
  - Mỗi bug phát hiện được thành một issue mới, có label `bug`.
  - Chụp ảnh từng màn của app thật cho 2 báo cáo.
- **Requirement:** Pass toàn bộ test case, hoặc các case Fail đều đã có issue `bug` đã đóng.
- **Constraint:** Không thêm tính năng mới trong issue này.

### [M4-02] Báo cáo cá nhân – Hùng (Tìm trận, Thi đấu online, Quản lý lịch sử đấu) · @HungNguyenBa1811
- **Phụ thuộc:** M1-02, M2-01, M2-02, M2-03, M2-05, M4-01.
- **Mô tả:** Báo cáo thiết kế cá nhân của Hùng, đúng khung đề ở mục 0. Mở issue từ đầu; mỗi mục dán vào ngay khi issue nguồn được đóng.
- **Input:** `REPORT_CONVENTION.md` (khung + checklist trước khi nộp); tài liệu trong `docs/chung/` và `docs/hung/`; ảnh app thật từ M4-01.
- **Output:**
  - Một file báo cáo online của riêng Hùng; dán link vào issue này.
  - Bản cuối xuất ra `docs/BaoCao_Hung.docx`.
  - Thứ tự mục bắt buộc:

    | Mục | Nội dung | Lấy từ |
    |---|---|---|
    | 1 | Mô tả chi tiết 3 chức năng bằng ngôn ngữ tự nhiên | M1-02 |
    | 2 | Thiết kế CSDL: `tblMatchHistory` (kèm `tblPlayer` được tham chiếu) và cây `rooms` trên Firebase | M2-02 |
    | 3 | Thiết kế lớp thực thể: `Room`, `RoomPlayer`, `MatchHistory`, `Player`, `MathProblem`, `GameSession` | M2-01 |
    | 4.1.1 | Tìm trận: thiết kế giao diện | M2-03 + ảnh app thật |
    | 4.1.2 | Tìm trận: biểu đồ lớp chi tiết | M2-03 |
    | 4.1.3 | Tìm trận: kịch bản v3 + biểu đồ tuần tự chi tiết | M2-05 |
    | 4.2.1 | Thi đấu online: thiết kế giao diện | M2-03 + ảnh app thật |
    | 4.2.2 | Thi đấu online: biểu đồ lớp chi tiết | M2-03 |
    | 4.2.3 | Thi đấu online: kịch bản v3 + biểu đồ tuần tự chi tiết (cả ngoại lệ thoát trận, mất mạng) | M2-05 |
    | 4.3.1 | Quản lý lịch sử đấu: thiết kế giao diện | M2-03 + ảnh app thật |
    | 4.3.2 | Quản lý lịch sử đấu: biểu đồ lớp chi tiết | M2-03 |
    | 4.3.3 | Quản lý lịch sử đấu: kịch bản v3 + biểu đồ tuần tự chi tiết | M2-05 |

- **Requirement:**
  - Mọi mục trong checklist đã tick.
  - Mỗi hình có chú thích "Hình x: ..." và 3–5 dòng giải thích.
  - Mỗi tên lớp, tên hàm, tên bảng trong báo cáo đều có trong code.
- **Constraint:**
  - Bài cá nhân: tự viết, tự vẽ. Phần chung (lớp thực thể, CSDL) chỉ lấy phần liên quan chức năng của mình.
  - Ảnh giao diện chụp từ app thật, không dùng ảnh phác thảo.

### [M4-03] Báo cáo cá nhân – Hoàng (Chơi offline, Xem bảng vàng, Cài đặt âm thanh) · @Minahn210
- **Phụ thuộc:** M1-03, M2-01, M2-02, M2-04, M2-06, M4-01.
- **Mô tả:** Báo cáo thiết kế cá nhân của Hoàng, đúng khung đề ở mục 0. Mở issue từ đầu; mỗi mục dán vào ngay khi issue nguồn được đóng.
- **Input:** `REPORT_CONVENTION.md` (khung + checklist trước khi nộp); tài liệu trong `docs/chung/` và `docs/hoang/`; ảnh app thật từ M4-01.
- **Output:**
  - Một file báo cáo online của riêng Hoàng; dán link vào issue này.
  - Bản cuối xuất ra `docs/BaoCao_Hoang.docx`.
  - Thứ tự mục bắt buộc:

    | Mục | Nội dung | Lấy từ |
    |---|---|---|
    | 1 | Mô tả chi tiết 3 chức năng bằng ngôn ngữ tự nhiên | M1-03 |
    | 2 | Thiết kế CSDL: `tblPlayer`, `tblGameRecord` | M2-02 |
    | 3 | Thiết kế lớp thực thể: `Player`, `GameRecord`, `MathProblem`, `GameSession` | M2-01 |
    | 4.1.1 | Chơi offline: thiết kế giao diện | M2-04 + ảnh app thật |
    | 4.1.2 | Chơi offline: biểu đồ lớp chi tiết | M2-04 |
    | 4.1.3 | Chơi offline: kịch bản v3 + biểu đồ tuần tự chi tiết | M2-06 |
    | 4.2.1 | Xem bảng vàng: thiết kế giao diện | M2-04 + ảnh app thật |
    | 4.2.2 | Xem bảng vàng: biểu đồ lớp chi tiết | M2-04 |
    | 4.2.3 | Xem bảng vàng: kịch bản v3 + biểu đồ tuần tự chi tiết | M2-06 |
    | 4.3.1 | Cài đặt âm thanh: thiết kế giao diện | M2-04 + ảnh app thật |
    | 4.3.2 | Cài đặt âm thanh: biểu đồ lớp chi tiết | M2-04 |
    | 4.3.3 | Cài đặt âm thanh: kịch bản v3 + biểu đồ tuần tự chi tiết | M2-06 |

- **Requirement:** Giống M4-02.
- **Constraint:** Giống M4-02. Nếu UC6 bị cắt (mục 8), bỏ các mục 4.3.x.

---

## 8. Thứ tự, lịch & cắt phạm vi

```
Hùng :  M1-01 ─> M1-02 ─> M1-05 ─> M1-07 ──┐                M3-01 (song song tuần 1)
                     └─> M1-04 <─ M1-03     │
Hoàng:  (25/10 review M1-01) M1-03 ─> M1-06 ─> M1-08
Chung:  M1-04 ─> M2-01 ─> M2-02 ─┬─> M2-03 (Hùng) ─> M2-05 ──┐
                                 └─> M2-04 (Hoàng) ─> M2-06 ─┤
                                     M2-03 + M2-04 ─> M2-07 ─┴─> Lập trình
Hùng :  M2-07 ─> M3-02 ─┐
        M3-01 ─> M3-03 ─┴─> M3-04 ─> M3-05 ─> M3-06 (cần M3-07) ─┐
Hoàng:  M2-07 ─> M3-07 ─> M3-08 (cần M3-02) ─> M3-09 ─> M3-10 ───┴─> M4-01 ─> M4-02, M4-03
```

| Tuần | Hùng | Hoàng |
|---|---|---|
| **1** · 21/10 – 25/10 | M1-01 (22/10), M1-02 (23/10), M1-05 (24/10), M1-07 (25/10); song song M3-01 | Bận. Tối 25/10: review M1-01, đọc M1-02 làm mẫu |
| **2** · 26/10 – 01/11 | M1-04 phần UC1–3 (26/10), M2-01 + M2-02 phần mình (27/10), M2-03 (28/10), M2-05 (29–30/10), M2-07 (31/10), M3-02 (31/10–02/11) | M1-03 (26/10), M1-04 phần UC4–6 + M1-06 (27/10), M1-08 (28/10), M2-01 + M2-02 phần mình (29/10), M2-04 (30/10), M2-06 (31/10–01/11), review M2-07 |
| **3** · 02/11 – 08/11 | M3-03 (02–04/11), M3-04 (04–07/11), M3-05 (07–08/11) | Review M3-02, M3-07 (02–03/11), M3-08 (03–06/11), M3-09 (06–08/11) |
| **4** · 09/11 – 15/11 | M3-06 (09–10/11) → M4-01 (10–12/11) → M4-02 (13–15/11) | M3-10 (09–10/11) → M4-01 (10–12/11) → M4-03 (13–15/11) |

**Mốc kiểm tra:** Tối Chủ nhật hằng tuần (25/10, 01/11, 08/11), cả 2 xem board GitHub. Issue nào trễ hơn lịch trên 2 ngày thì quyết định ngay: dồn việc hoặc cắt bớt phạm vi.

**Nếu phải cắt phạm vi để kịp, bỏ theo thứ tự:**
1. UC6 Cài đặt (M3-10, kèm mục 4.3 trong báo cáo của Hoàng). Âm thanh luôn bật nếu đã kịp làm `SoundManager`.
2. Level 4 của câu hỏi.
3. Chức năng xóa trong UC3 (giữ chức năng xem).

Không cắt M3-05 (xử lý sự cố) và M4-01 (kiểm thử): đây là 2 phần thầy dễ hỏi nhất.

---

## 9. Tổng hợp nhanh

| Mã | Tiêu đề | Assignee | Label |
|---|---|---|---|
| M1-01 | Phạm vi + use case tổng quát | Hùng | ooad, chung |
| M1-02 | Đặc tả + kịch bản v1 UC1–3 | Hùng | ooad, uc-tim-tran, uc-thi-dau-online, uc-lich-su |
| M1-03 | Đặc tả + kịch bản v1 UC4–6 | Hoàng | ooad, uc-choi-offline, uc-bang-vang, uc-cai-dat |
| M1-04 | Lớp thực thể phân tích | Cả 2 | ooad, chung |
| M1-05 | Lớp phân tích UC1–3 | Hùng | ooad, uc-tim-tran, uc-thi-dau-online, uc-lich-su |
| M1-06 | Lớp phân tích UC4–6 | Hoàng | ooad, uc-choi-offline, uc-bang-vang, uc-cai-dat |
| M1-07 | Kịch bản v2 + tuần tự phân tích UC1–3 | Hùng | ooad, uc-tim-tran, uc-thi-dau-online, uc-lich-su |
| M1-08 | Kịch bản v2 + tuần tự phân tích UC4–6 | Hoàng | ooad, uc-choi-offline, uc-bang-vang, uc-cai-dat |
| M2-01 | Thiết kế lớp thực thể | Cả 2 | ooad, chung |
| M2-02 | Thiết kế CSDL | Cả 2 | ooad, chung |
| M2-03 | Lớp thiết kế + giao diện UC1–3 | Hùng | ooad, uc-tim-tran, uc-thi-dau-online, uc-lich-su |
| M2-04 | Lớp thiết kế + giao diện UC4–6 | Hoàng | ooad, uc-choi-offline, uc-bang-vang, uc-cai-dat |
| M2-05 | Kịch bản v3 + tuần tự thiết kế UC1–3 | Hùng | ooad, uc-tim-tran, uc-thi-dau-online, uc-lich-su |
| M2-06 | Kịch bản v3 + tuần tự thiết kế UC4–6 | Hoàng | ooad, uc-choi-offline, uc-bang-vang, uc-cai-dat |
| M2-07 | Chốt khung code theo thiết kế | Hùng | chung |
| M3-01 | Hạ tầng Firebase + đăng nhập ẩn danh | Hùng | uc-tim-tran |
| M3-02 | Lõi câu hỏi và luật điểm | Hùng | chung |
| M3-03 | UC Tìm trận | Hùng | uc-tim-tran |
| M3-04 | UC Thi đấu online | Hùng | uc-thi-dau-online |
| M3-05 | UC Thi đấu online: sự cố | Hùng | uc-thi-dau-online |
| M3-06 | UC Quản lý lịch sử đấu | Hùng | uc-lich-su |
| M3-07 | CSDL SQLite + màn Chính | Hoàng | chung, uc-choi-offline |
| M3-08 | UC Chơi offline | Hoàng | uc-choi-offline |
| M3-09 | UC Xem bảng vàng | Hoàng | uc-bang-vang |
| M3-10 | UC Cài đặt âm thanh | Hoàng | uc-cai-dat |
| M4-01 | Kiểm thử chéo + độ trễ | Cả 2 | chung |
| M4-02 | Báo cáo cá nhân Hùng | Hùng | docs |
| M4-03 | Báo cáo cá nhân Hoàng | Hoàng | docs |
