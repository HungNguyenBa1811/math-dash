# QUY ƯỚC BÁO CÁO THIẾT KẾ — MATH DASH 1v1

> Áp dụng cho: mọi issue label `ooad` (milestone Phân tích, Thiết kế) và 2 issue báo cáo cá nhân (label `docs`).
> Đi kèm: `CONVENTION.md` (quy ước code), `ISSUES_PLAN.md` mục 2 (tên UC, tên lớp, tên bảng dùng chung).

---

## 1. Yêu cầu của đề (nguyên văn)

> Bài làm cá nhân (bạn nào sẽ demo được chức năng nào của hệ thống thì sẽ trình bày phần thiết kế này cho các chức năng đấy):
> - Mô tả chi tiết các chức năng mình làm bằng ngôn ngữ tự nhiên (specification)
> - Thiết kế CSDL, nếu có (trình bày 1 lần chung cho các chức năng của mình làm)
> - Thiết kế lớp thực thể (trình bày 1 lần chung cho các chức năng của mình làm)
>
> Các bước sau trình bày mỗi bước riêng cho từng chức năng, trình bày lần lượt các bước của chức năng thứ nhất xong mới trình bày lần lượt các bước của chức năng thứ hai... cho đến hết các chức năng của mình làm:
> - Thiết kế các giao diện của chức năng
> - Thiết kế biểu đồ lớp chi tiết của chức năng (bao gồm cả phần frontend và backend, bao gồm bên client và bên server nếu có)
> - Thiết kế biểu đồ tuần tự chi tiết của chức năng.
>
> Bài này nộp lấy điểm thi cuối kì của môn học (điểm thi cuối kì chiếm 60% điểm tổng kết môn học, trong đó bài báo cáo thiết kế chiếm 30% - điểm demo chiếm 30%).

**Hệ quả cho nhóm:**
- Ai **demo** chức năng nào thì **tự code và tự vẽ** chức năng đó. Phân công ở `ISSUES_PLAN.md` mục 2.1.
- Mỗi người **một file báo cáo riêng**. Không viết chung, không chép nguyên phần của người kia.
- Phần dùng chung (lớp thực thể, CSDL) làm chung để 2 bản không lệch nhau. Mỗi người chỉ đưa vào báo cáo của mình **phần liên quan đến chức năng của mình**.

---

## 2. Khung báo cáo bắt buộc

Mỗi báo cáo cá nhân có đúng thứ tự sau. Không đảo mục, không gộp các chức năng vào chung một biểu đồ.

```
1. Mô tả chi tiết các chức năng (specification)
2. Thiết kế cơ sở dữ liệu                 ← 1 lần cho mọi chức năng của mình
3. Thiết kế lớp thực thể                  ← 1 lần cho mọi chức năng của mình
4. Thiết kế chi tiết từng chức năng
   4.1. <Chức năng 1>
        4.1.1. Thiết kế giao diện
        4.1.2. Biểu đồ lớp chi tiết       (boundary + control + entity, client + server)
        4.1.3. Biểu đồ tuần tự chi tiết   (kèm kịch bản v3)
   4.2. <Chức năng 2>
        4.2.1 → 4.2.3 như trên
   4.3. <Chức năng 3>
        4.3.1 → 4.3.3 như trên
```

Các bước phân tích (kịch bản v1, v2, biểu đồ lớp phân tích, tuần tự phân tích) **bắt buộc phải làm** vì là đầu vào của thiết kế. Trong báo cáo chỉ đưa vào nếu còn chỗ, đặt ở **Phụ lục**.

---

## 3. Quy trình OOAD của nhóm

Làm đúng thứ tự dưới đây. Mỗi bước dùng kết quả của bước trước, không nhảy cóc.

```
PHÂN TÍCH                                         THIẾT KẾ
A1 Kịch bản v1 ─> A2 Lớp thực thể ─┐              D1 Lớp thực thể thiết kế ─> D2 CSDL
                                   ├─> A3 Tĩnh ─┐                               │
                                   │            ├─> A4 Động ──> D3 Tĩnh (lớp chi tiết) ─> D4 Động (v3)
```

| Bước | Tên | Issue | Mục báo cáo |
|---|---|---|---|
| A1 | Đặc tả + kịch bản v1 | M1-02, M1-03 | 1 |
| A2 | Lớp thực thể phân tích | M1-04 | (Phụ lục) |
| A3 | Thiết kế tĩnh phân tích: View + thực thể | M1-05, M1-06 | (Phụ lục) |
| A4 | Thiết kế động phân tích: kịch bản v2 + tuần tự | M1-07, M1-08 | (Phụ lục) |
| D1 | Thiết kế lớp thực thể | M2-01 | 3 |
| D2 | Thiết kế CSDL | M2-02 | 2 |
| D3 | Thiết kế tĩnh: giao diện + biểu đồ lớp chi tiết | M2-03, M2-04 | 4.x.1, 4.x.2 |
| D4 | Thiết kế động: kịch bản v3 + tuần tự chi tiết | M2-05, M2-06 | 4.x.3 |

### A1. Đặc tả + kịch bản v1 (Người chơi ↔ Hệ thống)

**Đặc tả** (mục 1 của báo cáo):
- Viết theo dạng luồng nghiệp vụ, nối các bước bằng `→`.
- Mỗi lần có giao diện hiện ra thì liệt kê đủ thành phần của giao diện đó.

> *Chơi offline:* Người chơi mở ứng dụng → Giao diện chính xuất hiện với một ô nhập tên và 5 nút: Chơi ngay, Chơi offline, Lịch sử, Bảng vàng, Cài đặt → Người chơi nhập tên và nhấn Chơi offline → Giao diện chơi xuất hiện với: tên, kỷ lục, điểm, combo, số mạng, thanh thời gian, phép tính và 4 nút đáp án → …

**Kịch bản chuẩn v1:**
- Các bước đánh số, xen kẽ hành động của Người chơi và phản hồi của Hệ thống.
- Dùng **dữ liệu cụ thể** (tên `HUNG`, phép tính `12 + 7`, điểm `120`). Có danh sách thì vẽ bảng dữ liệu mẫu.

**Kịch bản ngoại lệ:**
- Ghi rõ rẽ nhánh từ bước số mấy của kịch bản chuẩn.
- Ví dụ: "5a. Người chơi để trống tên → hệ thống dùng tên `PLAYER_1`".

**Cấm:** tên lớp, tên hàm, thuật ngữ kỹ thuật (Activity, SQLite, Firebase) trong kịch bản v1.

### A2. Lớp thực thể phân tích

1. Trích mọi danh từ trong đặc tả và kịch bản v1.
2. Lập bảng: mỗi danh từ được **giữ thành lớp**, **là thuộc tính**, hay **bị loại**, kèm lý do.
3. Vẽ biểu đồ lớp thực thể. Mỗi lớp ghi tên tiếng Anh theo `ISSUES_PLAN.md` mục 2.2, kèm tên tiếng Việt.
4. Quan hệ:
   - Nếu **thông tin của A nằm trong B** thì vẽ kết tập (aggregation) `A --<> B`, hình thoi đặt ở phía B.
   - Ví dụ: thông tin Khách hàng nằm trong Hóa đơn → `KhachHang --<> HoaDon`. Trong Math Dash: `Player --<> GameRecord`.
   - Ghi bội số ở 2 đầu (1, n).

**Không cần** đủ thuộc tính. **Không thêm** `id`. **Không vẽ** View, DAO, Controller.

### A3. Thiết kế tĩnh phân tích (mỗi chức năng một biểu đồ)

Gồm **lớp View + lớp thực thể liên quan**. Làm theo 4 bước:

1. **Mỗi giao diện một lớp `XxxView`.** Thông báo và hộp thoại xác nhận không tính là giao diện.
2. **Đặt tên thành phần theo loại:**
   - `in`: ô nhập (`inUsername`).
   - `out`: thành phần hiển thị (`outScore`, `outListRecord`).
   - `sub`: nút bấm (`subPlayOffline`).
   - Có thể ghép: `outsubListMatch` (danh sách bấm được), `inoutName`.
3. **Mỗi xử lý hệ thống phải làm, trả lời 4 câu hỏi:**
   1. Tên phương thức là gì?
   2. Đầu vào là gì?
   3. Đầu ra là gì?
   4. Gán cho lớp nào?
      - Đầu ra là lớp thực thể nào thì gán vào lớp đó.
      - Nếu không, xét đầu vào: chỉ thuộc một lớp thực thể thì gán vào lớp đó; thuộc nhiều lớp thì gán vào lớp chứa được tất cả đầu vào.
4. **Vẽ biểu đồ lớp** cho chức năng.

### A4. Thiết kế động phân tích: kịch bản v2 + tuần tự

- **Kịch bản v2:** viết lại kịch bản chuẩn v1, mỗi bước là **đúng một thông điệp**. Ví dụ:
  > 7. `GameView` gọi phương thức `generateProblem()` của lớp `MathProblem`.
  > 8. `MathProblem` trả về câu hỏi cho `GameView`.
- **Biểu đồ tuần tự:**
  - Chỉ có 3 loại đối tượng: **tác nhân – View – thực thể**.
  - Số thông điệp và thứ tự **giống hệt** kịch bản v2.
- **Ngoại lệ quan trọng nhất** vẽ bằng khung `alt`.

### D1. Thiết kế lớp thực thể (mục 3 của báo cáo)

Đầu vào là biểu đồ A2. Trình bày đủ 4 bước:

1. **Thêm thuộc tính khóa `id`** cho các lớp không kế thừa lớp khác. Với `RoomPlayer`, khóa là `uid`.
2. **Thêm kiểu dữ liệu** Java cho mọi thuộc tính.
3. **Bỏ hết quan hệ liên kết (association)**, chuyển thành **kết tập (aggregation)** hoặc **hợp thành (composition)**. Ghi rõ "X là thành phần của Y, kiểu 1-n".
   - Composition: thành phần không tồn tại nếu thiếu lớp chứa, ví dụ `RoomPlayer` trong `Room`.
   - Aggregation: thành phần tồn tại độc lập, ví dụ `Player` trong `GameRecord`.
4. **Thêm thuộc tính đối tượng** tương ứng:
   - "Y có một X" → `X x`.
   - "Y có danh sách X" → `List<X> listX`.

Thuộc tính tính được từ thuộc tính khác (D9 trong `ISSUES_PLAN.md`) **không** là thuộc tính lưu trữ. Nếu cần thì viết thành phương thức, ví dụ `getResult()`, `getAccuracy()`.

### D2. Thiết kế CSDL (mục 2 của báo cáo)

**SQLite**, trình bày đủ 5 bước:

1. Mỗi lớp thực thể cần lưu thành một bảng có tiền tố **`tbl`**: `Player` → `tblPlayer`.
2. Thuộc tính **không phải đối tượng** thành cột. Thuộc tính đối tượng (`Player player`, `List<...>`) bị loại.
3. Chuyển quan hệ giữa các lớp thành quan hệ giữa các bảng:
   - 1-1: gộp bảng.
   - 1-n: giữ riêng.
   - n-n: tách bằng bảng trung gian.
4. Khóa:
   - Khóa chính `id`.
   - Khóa ngoại đặt ở bảng phía n, tên **`tbl<Bảng phía 1>Id`**, ví dụ `tblGameRecord.tblPlayerId`.
5. Loại thuộc tính gây dư thừa: thuộc tính trùng lặp và **thuộc tính dẫn xuất** (`high_score`, `result`, `% chính xác`).

Kết quả: biểu đồ CSDL (bảng, kiểu cột, PK, FK, bội số) và câu lệnh `CREATE TABLE`.

**Firebase** (phần của Hùng): áp dụng cùng tinh thần:
- Mỗi lớp là một node.
- Thuộc tính thường là field.
- Quan hệ hợp thành là node con, khóa là push id hoặc `uid`.

Vẽ thành cây như `ISSUES_PLAN.md` mục 2.4.

### D3. Thiết kế tĩnh: giao diện + biểu đồ lớp chi tiết (mục 4.x.1, 4.x.2)

**4.x.1 Thiết kế giao diện:**
- Ảnh từng màn của chức năng: lúc thiết kế dùng phác thảo, khi nộp thay bằng ảnh chụp app thật.
- Bảng thành phần gồm `id` trong layout (`btnPlayOffline`), loại View (`Button`), chức năng, và tên tương ứng ở bước phân tích (`subPlayOffline`).

**4.x.2 Biểu đồ lớp chi tiết** theo MVC, đủ 3 loại lớp:

| Loại | Lớp trong Math Dash |
|---|---|
| Boundary (frontend) | `FrmXxxActivity`, Adapter, interface listener |
| Control (backend) | Controller (`GameController`, `MatchController`), DAO |
| Entity | Lớp ở D1 |

**Quy tắc DAO:** lớp cha giữ kết nối dùng chung (`con`), các DAO con kế thừa.
- `DAO` (giữ `SQLiteDatabase con`) ← `PlayerDAO`, `GameRecordDAO`, `MatchHistoryDAO`.
- `FirebaseDAO` (giữ `DatabaseReference con`) ← `AuthDAO`, `RoomDAO`.

**Yêu cầu khác:**
- Mỗi phương thức ghi đủ tham số và kiểu trả về.
- Chức năng có phần server (Firebase) thì vẽ cả phía server trên cùng biểu đồ.
- Dưới biểu đồ mô tả mỗi lớp 1–2 dòng, theo mẫu: "`RoomDAO` có phương thức `findWaitingRoom` để tìm phòng đang chờ…".

**Ánh xạ từ phân tích sang thiết kế:**
- Mỗi `XxxView` thành một Activity hoặc overlay (bảng ở `ISSUES_PLAN.md` mục 2.2).
- Mỗi phương thức của thực thể ở A3 chuyển sang DAO hoặc Controller tương ứng.

### D4. Thiết kế động: kịch bản v3 + tuần tự chi tiết (mục 4.x.3)

**Kịch bản v3:** đánh số, mỗi bước một thông điệp, dùng **tên lớp và tên hàm thật trong code**. Mẫu:

> 1. Người chơi nhập tên `HUNG` và nhấn nút Chơi offline trên `FrmMainActivity`.
> 2. Phương thức `onClick` của `FrmMainActivity` được gọi.
> 3. `onClick` gọi `FrmGameActivity`.
> 4. Phương thức `onCreate` của `FrmGameActivity` được gọi.
> 5. `onCreate` gọi phương thức `findOrCreateByUsername` của `PlayerDAO`.
> 6. `findOrCreateByUsername` truy vấn bảng `tblPlayer`.
> 7. `findOrCreateByUsername` gọi lớp `Player` để đóng gói kết quả.
> 8. Lớp `Player` trả về đối tượng `Player` cho `findOrCreateByUsername`.
> 9. `findOrCreateByUsername` trả về cho `onCreate`.
> 10. …

**Biểu đồ tuần tự chi tiết:**
- Đủ boundary – control – entity.
- Đánh số **khớp 1-1** với kịch bản v3.
- Ngoại lệ chính vẽ bằng khung `alt`.

**Quy ước riêng:**
- **Firebase (bất đồng bộ):** lời gọi DAO là một thông điệp. Kết quả là thông điệp trả về từ callback (`onDataChange`, `onComplete`), ghi tên callback trên mũi tên.
- **`Thread` + `runOnUiThread`:** vẽ lời gọi DAO từ Activity như bình thường. Ghi chú `{Thread}` cạnh thông điệp.
- **`CountDownTimer`:** vẽ như một đối tượng điều khiển, có thông điệp `onTick` / `onFinish`.

---

## 4. Quy ước hình vẽ và trình bày

- **Công cụ:** Visual Paradigm hoặc draw.io, ký hiệu UML chuẩn.
  - Kết tập: hình thoi rỗng. Hợp thành: hình thoi đặc. Kế thừa: tam giác rỗng.
- **Lưu trữ:**
  - Kịch bản viết bằng Markdown trong `docs/`.
  - Hình xuất PNG đặt cạnh file Markdown.
  - Mỗi người giữ file dự án vẽ riêng (`docs/hung/*.vpp`, `docs/hoang/*.vpp`), không sửa file của người kia.
- **Mỗi hình:**
  - Có chú thích **"Hình x: …"**, đánh số liên tục trong báo cáo.
  - Có 3–5 dòng giải thích ngay dưới hình.
- **Tên gọi:**
  - UC, lớp, bảng, trạng thái phòng (`waiting` / `playing` / `finished`) phải trùng với `ISSUES_PLAN.md` mục 2 và với code.
  - Báo cáo nhắc tới một tên mà code không có là lỗi.
- **Ngôn ngữ:** tiếng Việt. Tên lớp, hàm, bảng giữ nguyên tiếng Anh như trong code.
- **Bản cuối:** viết trên file online của riêng mình, xuất 1 lần ra `docs/BaoCao_Hung.docx` / `docs/BaoCao_Hoang.docx`. Không sửa `.docx` trong repo.

---

## 5. Checklist trước khi nộp

- [ ] Đúng khung mục 2. Hết các bước của chức năng 1 mới sang chức năng 2.
- [ ] Mục 1 mô tả đủ mọi chức năng mình demo, có kịch bản ngoại lệ.
- [ ] Mục 2 có đủ 5 bước. Tên bảng có tiền tố `tbl`, đủ PK và FK, không còn cột dẫn xuất.
- [ ] Mục 3 có đủ 4 bước. Không còn quan hệ liên kết trơn, mọi lớp có khóa.
- [ ] Mỗi chức năng có đủ 3 phần: giao diện (ảnh app thật), lớp chi tiết, tuần tự chi tiết.
- [ ] Biểu đồ lớp chi tiết có đủ boundary, control (Controller + DAO), entity. DAO con kế thừa lớp DAO cha.
- [ ] Kịch bản v3 và biểu đồ tuần tự khớp từng bước.
- [ ] Mọi tên lớp, hàm, bảng trong báo cáo đều có trong code ở nhánh `main`.
- [ ] Mỗi hình có "Hình x" và lời giải thích.
- [ ] Người còn lại đã đọc lại và góp ý.
