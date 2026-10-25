# QUY ƯỚC BÁO CÁO THIẾT KẾ — MATH DASH 1v1

> Áp dụng cho: mọi issue label `ooad` (milestone Phân tích, Thiết kế) và 2 issue báo cáo cá nhân (label `docs`).
> Đi kèm: `CONVENTION.md` (quy ước code), `ISSUES_PLAN.md` mục 2 (tên UC, tên lớp, tên bảng dùng chung).

---

## 1. Yêu cầu của đề (nguyên văn)

### 1.1. Đề bài số 9: Game giải toán nhanh

> **Mô tả:**
> - Hệ thống có một server và nhiều client. Server lưu toàn bộ thông tin và dữ liệu.
> - Để chơi, người chơi phải login vào tài khoản của mình từ một máy client. Sau khi login thành công, giao diện hiện lên một danh sách các người chơi đang online, mỗi người chơi có các thông tin: tên, tổng số điểm hiện có của người chơi, trạng thái (hoặc đang bận nếu đang chơi với người khác, hoặc đang rỗi nếu không chơi với ai).
> - Muốn mời (thách đấu) ai thì người chơi click vào tên của đối thủ đó trong danh sách online.
> - Khi bị thách đấu, người chơi có thể chấp nhận (OK), hoặc từ chối (Reject).
> - Khi chấp nhận, 2 người chơi sẽ vào chơi với nhau, và server sẽ làm trọng tài. Giao diện chơi gồm một danh sách N câu hỏi toán nhanh theo dạng trắc nghiệm, ô thời gian và nút thoát.
> - Server sẽ sinh tự động cùng một đề bài và gửi về cho cả hai đối thủ chơi. Mỗi người chơi phải click chọn các đáp án của các câu hỏi. Khi hoàn thành thì click nút submit bài làm. Ai đúng hết và nhanh hơn thì giành chiến thắng. Nếu cả hai đều có ít nhất 1 câu sai thì hòa.
> - Sau mỗi trận, server sẽ kiểm tra xem ai thắng và gửi kết quả về cho cả 2 đối thủ: thắng 1 điểm, hòa 0.5 điểm, thua 0 điểm.
> - Sau mỗi ván, đều có dialog hỏi mỗi người chơi có muốn tiếp tục không. Nếu cả hai tiếp tục thì chơi tiếp, nếu một trong hai đối thủ dừng chơi thì thoát ra và server báo cho người chơi còn lại.
> - Kết quả các trận đấu được lưu vào server. Mỗi người chơi đều có thể vào xem bảng xếp hạng các người chơi trong toàn bộ hệ thống, theo lần lượt các tiêu chí: tổng số điểm (giảm dần), trung bình điểm của các đối thủ đã gặp (giảm dần), trung bình thời gian kết thúc trong các trận thắng (tăng dần).
>
> **Các chức năng thực hiện:**
> - (Khó) Người chơi thách đấu và đấu đối kháng
> - (Trung bình) Người chơi thi đấu với máy
> - (Dễ) Quản lí thông tin người chơi, xem BXH

Ánh xạ sang use case của nhóm: "thách đấu và đấu đối kháng" là UC03 Chơi online; "xem BXH" là UC04 Xem bảng xếp hạng; "thi đấu với máy" là UC05 Chơi với máy; "quản lí thông tin người chơi" là UC06, do quản trị viên thực hiện.

### 1.2. Yêu cầu báo cáo

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

**Lưu ý thêm của thầy:** demo riêng từng người, **tối đa 3 chức năng** mỗi người.

**Mốc thời gian:**

| Hạng mục | Ngày |
|---|---|
| Demo cá nhân (module của mình) | **28/10/2026** |
| Nộp báo cáo thiết kế cá nhân | **15/11/2026** |

**Hệ quả cho nhóm:**
- Ai **demo** chức năng nào thì **tự code và tự vẽ** chức năng đó. Mỗi người tối đa 3 chức năng, phân công ở `ISSUES_PLAN.md` mục 2.1 (Hùng: Chơi online, Xem bảng xếp hạng, Đăng nhập / Đăng ký; Hoàng: Chơi với máy, Quản lý thông tin người chơi, Đăng nhập / Đăng ký).
- Mỗi người **một file báo cáo riêng**. Không viết chung, không chép nguyên phần của người kia.
- **Phần chung** gồm use case tổng quát, lớp thực thể, CSDL:
  - Nhóm trưởng làm trước khi code; thành viên còn lại duyệt.
  - Mỗi người chỉ đưa vào báo cáo của mình **phần liên quan đến chức năng của mình**.

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

Vì demo (28/10) đến trước hạn báo cáo (15/11), nhóm làm theo 2 đợt:
- **Trước demo:** chỉ làm phần chung, vì code cần dùng. Gồm use case tổng quát, A2 lớp thực thể, D1 thiết kế lớp thực thể, D2 CSDL.
- **Sau demo:** mỗi người làm A1 → A3 → A4 → D3 → D4 cho các chức năng của mình, **đối chiếu với code thật**.
  - Nếu kịch bản phát hiện thiếu lớp thực thể thì bổ sung vào tài liệu chung (qua PR, người kia duyệt).

```
PHÂN TÍCH                                         THIẾT KẾ
A1 Kịch bản v1 ─> A2 Lớp thực thể ─┐              D1 Lớp thực thể thiết kế ─> D2 CSDL
                                   ├─> A3 Tĩnh ─┐                               │
                                   │            ├─> A4 Động ──> D3 Tĩnh (lớp chi tiết) ─> D4 Động (v3)
```

| Bước | Tên | Issue | Mục báo cáo |
|---|---|---|---|
| — | Phạm vi + use case tổng quát (chung) | M1-01 | (Mở đầu) |
| A1 | Đặc tả + kịch bản v1 | M3-01, M3-02 | 1 |
| A2 | Lớp thực thể phân tích (chung) | M1-02 | (Phụ lục) |
| A3 | Thiết kế tĩnh phân tích: View + thực thể | M3-03, M3-04 | (Phụ lục) |
| A4 | Thiết kế động phân tích: kịch bản v2 + tuần tự | M3-05, M3-06 | (Phụ lục) |
| D1 | Thiết kế lớp thực thể (chung) | M1-02 | 3 |
| D2 | Thiết kế CSDL (chung) | M1-03 | 2 |
| D3 | Thiết kế tĩnh: giao diện + biểu đồ lớp chi tiết | M4-01, M4-02 | 4.x.1, 4.x.2 |
| D4 | Thiết kế động: kịch bản v3 + tuần tự chi tiết | M4-03, M4-04 | 4.x.3 |

### A1. Đặc tả + kịch bản v1 (Người chơi ↔ Hệ thống)

**Đặc tả** (mục 1 của báo cáo):
- Viết theo dạng luồng nghiệp vụ, nối các bước bằng `→`.
- Mỗi lần có giao diện hiện ra thì liệt kê đủ thành phần của giao diện đó.

> *Chơi với máy:* Người chơi nhấn Chơi với máy ở giao diện chính → Giao diện chọn độ khó xuất hiện với 3 nút: Dễ, Trung bình, Khó → Người chơi chọn Trung bình → Giao diện thi đấu xuất hiện với: ô thời gian, dòng trạng thái của máy, danh sách 10 câu hỏi mỗi câu 4 phương án, nút Nộp bài, nút Thoát → …

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
   - Ví dụ: thông tin Khách hàng nằm trong Hóa đơn → `KhachHang --<> HoaDon`. Trong Math Dash: `Player --<> BotGame`.
   - Ghi bội số ở 2 đầu (1, n).

**Không cần** đủ thuộc tính. **Không thêm** `id`. **Không vẽ** View, DAO.

**Đặt tên:** tên lớp tiếng Anh, PascalCase, trùng tên lớp trong code; tên thuộc tính camelCase.

### A3. Thiết kế tĩnh phân tích (mỗi chức năng một biểu đồ)

Gồm **lớp View + lớp thực thể liên quan**. Làm theo 4 bước:

1. **Mỗi giao diện một lớp `XxxView`.** Thông báo và hộp thoại xác nhận không tính là giao diện.
2. **Đặt tên thành phần theo loại:**
   - `in`: ô nhập (`inUsername`).
   - `out`: thành phần hiển thị (`outScore`, `outListRecord`).
   - `sub`: nút bấm (`subPlayBot`).
   - Có thể ghép: `outsubListMatch` (danh sách bấm được), `inoutName`.
3. **Mỗi xử lý hệ thống phải làm, trả lời 4 câu hỏi:**
   1. Tên phương thức là gì?
   2. Đầu vào là gì?
   3. Đầu ra là gì?
   4. Gán cho lớp nào?
      - Đầu ra là lớp thực thể nào thì gán vào lớp đó.
      - Nếu không, xét đầu vào: chỉ thuộc một lớp thực thể thì gán vào lớp đó; thuộc nhiều lớp thì gán vào lớp chứa được tất cả đầu vào.
4. **Vẽ biểu đồ lớp** cho chức năng. View – View và View – thực thể đều nối bằng **liên kết (association)**, không vẽ mũi tên.

Pha phân tích chưa có kiểu dữ liệu, chưa có DAO, Activity, REST.

### A4. Thiết kế động phân tích: kịch bản v2 + tuần tự

- **Kịch bản v2:** viết lại kịch bản chuẩn v1, mỗi bước là **đúng một thông điệp**. Ví dụ:
  > 7. GameView gọi hàm generateProblem() của lớp MathProblem.
  > 8. Lớp MathProblem trả về câu hỏi cho GameView.
- **Biểu đồ tuần tự:**
  - Chỉ có 3 loại đối tượng: **tác nhân – View – thực thể**.
  - Số thông điệp và thứ tự **giống hệt** kịch bản chuẩn của v2.
- Kịch bản v2 **vẫn viết ngoại lệ** (đánh số theo bước rẽ nhánh, ví dụ `9a.1`). Ngoại lệ mô tả ở mức chung ("hiển thị thông báo không tìm thấy phòng"), không ghi chi tiết kỹ thuật như `Toast`, `AlertDialog`. Biểu đồ tuần tự **chỉ vẽ kịch bản chuẩn**, không vẽ khung `alt`.
- Mọi phương thức trên biểu đồ tuần tự phải có ở biểu đồ lớp phân tích.
- Chức năng dài (Chơi online) chia đoạn như kịch bản v1, mỗi đoạn một biểu đồ, đánh số lại từ 1.

### D1. Thiết kế lớp thực thể (mục 3 của báo cáo)

Đầu vào là biểu đồ A2. Trình bày đủ 4 bước:

1. **Thêm thuộc tính khóa `id`** cho các lớp không kế thừa lớp khác. Với `Challenge`, khóa là push id kiểu `String` của Firebase.
2. **Thêm kiểu dữ liệu** Java cho mọi thuộc tính.
3. **Bỏ hết quan hệ liên kết (association)**, chuyển thành **kết tập (aggregation)** hoặc **hợp thành (composition)**. Ghi rõ "X là thành phần của Y, kiểu 1-n".
   - Composition: thành phần không tồn tại nếu thiếu lớp chứa, ví dụ `MatchPlayer` trong `Match`.
   - Aggregation: thành phần tồn tại độc lập, ví dụ `Player` trong `MatchPlayer`.
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
   - Khóa ngoại đặt ở bảng phía n, tên **`tbl<Bảng phía 1>Id`**, ví dụ `tblMatchPlayer.tblMatchId`.
5. Loại thuộc tính gây dư thừa: thuộc tính trùng lặp và **thuộc tính dẫn xuất** (`result`, `totalPoints`, điểm trận).

Kết quả: biểu đồ CSDL (bảng, kiểu cột, PK, FK, bội số) và câu lệnh `CREATE TABLE`.

**Firebase** (phần của Hùng): áp dụng cùng tinh thần:
- Mỗi lớp là một node.
- Thuộc tính thường là field.
- Quan hệ hợp thành là node con, khóa là push id hoặc `uid`.

Vẽ thành cây như `ISSUES_PLAN.md` mục 2.4.

### D3. Thiết kế tĩnh: giao diện + biểu đồ lớp chi tiết (mục 4.x.1, 4.x.2)

**4.x.1 Thiết kế giao diện:**
- Ảnh từng màn của chức năng: lúc thiết kế dùng phác thảo, khi nộp thay bằng ảnh chụp app thật.
- Bảng thành phần gồm `id` trong layout (`btnPlayBot`), loại View (`Button`), chức năng, và tên tương ứng ở bước phân tích (`subPlayBot`).
- Thuộc tính của View chuyển sang thành phần Android:
  - Bỏ tiền tố `in` / `out` / `sub`, thay bằng tiền tố theo loại View: `btn` (`Button`), `edt` (`EditText`), `txt` (`TextView`), `rv` (`RecyclerView`), `layout` (`LinearLayout`).
  - Nhóm nút cùng loại gom thành mảng, ví dụ `subDigits` → `btnDigits : Button[10]`.
  - Nút chỉ có biểu tượng dùng `ImageButton`, đặt `contentDescription`.
  - Thuộc tính ẩn kiểu thực thể giữ dữ liệu dùng giữa các bước, ví dụ `me : Player`.

**4.x.2 Biểu đồ lớp chi tiết** theo MVC, đủ 3 loại lớp:

| Loại | Lớp trong Math Dash |
|---|---|
| Boundary | App: `XxxActivity`, Adapter. Server: `XxxRestController` |
| Control | App: DAO (`XxxDAO`, `XxxApiDAO`, `XxxFirebaseDAO`, `ProblemDAO`, `SessionDAO`). Server: DAO |
| Entity | Lớp ở D1 (cùng tên ở app và server) |

App **không có lớp Controller riêng**: Activity xử lý sự kiện, điều phối và gọi thẳng DAO (`ISSUES_PLAN.md` D16).

**Tên lớp giao diện:** theo chuẩn Android, tên kết thúc bằng `Activity` (`MainActivity`, `MatchActivity`), layout tương ứng `activity_main.xml`, `activity_match.xml`; đặt trong package `view`. **Không dùng tiền tố `Frm`**: đó là quy ước của Java Swing (`JFrame`), Android không có form. Hậu tố `Activity` đã đủ cho biết lớp là boundary.

**Quan hệ của lớp giao diện (phần app):**
- Boundary nối boundary bằng **liên kết (association)**: Activity – Activity, Activity – Adapter, Adapter – ViewHolder.
- Boundary – control (Activity – DAO), control – control và control – entity cũng nối bằng **liên kết**, không vẽ mũi tên phụ thuộc. Chỉ giữ ký hiệu riêng cho kế thừa, thực thi interface, kết tập và hợp thành.
- **Mỗi Activity nối với một lớp layout** đặt tên đúng tên file, ví dụ `activity_main.xml`. Lớp này để **rỗng**.
- Activity **giữ thực thể làm thuộc tính** (ví dụ `me : Player`) thì nối bằng **kết tập (aggregation)**, hình thoi ở phía Activity: `Player --<> MatchActivity`. Cách đọc giống `KhachHang --<> HoaDon`: hóa đơn chứa thông tin khách hàng nên hình thoi ở phía hóa đơn.

**Quy tắc DAO:** mỗi nơi lưu trữ có một lớp cha giữ kết nối dùng chung, các DAO con kế thừa.
- App, SQLite: `DAO` (giữ `SQLiteDatabase con`) ← `PlayerDAO`, `BotGameDAO`.
- App, REST: `ApiDAO` (giữ địa chỉ server) ← `PlayerApiDAO`, `MatchApiDAO`.
- App, Firebase: `FirebaseDAO` (giữ `DatabaseReference con`) ← `PresenceFirebaseDAO`, `ChallengeFirebaseDAO`, `MatchFirebaseDAO`.
- Server, MySQL: `DAO` (giữ `Connection conn`) ← `PlayerDAO`, `MatchDAO`.

**Chức năng có gọi server (REST)** vẽ thành **2 package** trên cùng một biểu đồ:
- **"Client (Android)"**: boundary (Activity) – control (`XxxApiDAO`, các DAO khác) – entity.
- **"Server (Spring Boot)"**: `XxxRestController` – `XxxDAO extends DAO` – entity. Không có tầng Service.
- Hai package nối nhau bằng **đúng một đường liên kết** giữa `XxxApiDAO` và `XxxRestController`, ghi `HTTP/JSON`.
- Không vẽ lớp của framework (Spring, Firebase SDK, `HttpURLConnection`); chỉ ghi chúng làm kiểu dữ liệu của thuộc tính.

Chức năng chỉ chạy trên app (Chơi với máy) vẽ một package như bài mẫu.

**Yêu cầu khác:**
- Mỗi phương thức ghi đủ tham số và kiểu trả về.
- Dưới biểu đồ mô tả mỗi lớp 1–2 dòng, theo mẫu: "`RoomDAO` có phương thức `findWaitingRoom` để tìm phòng đang chờ…".

**Ánh xạ từ phân tích sang thiết kế:**
- Mỗi `XxxView` thành một Activity hoặc overlay (bảng ở `ISSUES_PLAN.md` mục 2.2).
- Mỗi phương thức của thực thể ở A3 chuyển sang DAO (truy cập dữ liệu) hoặc Activity (điều phối) tương ứng; luật nghiệp vụ giữ ở thực thể.

### D4. Thiết kế động: kịch bản v3 + tuần tự chi tiết (mục 4.x.3)

**Kịch bản v3:** đánh số, mỗi bước một thông điệp, dùng **tên lớp và tên hàm thật trong code**. Mẫu:

> 1. Người chơi nhấn nút Bảng xếp hạng trên MainActivity.
> 2. Hàm onClick() của MainActivity gọi hàm startActivity() mở RankingActivity.
> 3. Hàm onCreate() của RankingActivity gọi hàm getPlayer() của SessionDAO.
> 4. Hàm getPlayer() gọi lớp Player để đóng gói người chơi đang đăng nhập.
> 5. Lớp Player trả đối tượng Player cho hàm getPlayer().
> 6. Hàm getPlayer() trả đối tượng Player cho hàm onCreate().
> 7. Hàm onCreate() gọi hàm loadRanking() của chính nó.
> 8. …

**Biểu đồ tuần tự chi tiết:**
- Đủ boundary – control – entity.
- Đánh số **khớp 1-1** với kịch bản v3.
- Kịch bản v3 và biểu đồ **chỉ gồm kịch bản chuẩn**, không có ngoại lệ.
- Đóng gói thực thể con được lược bỏ: khi đóng gói `Match` thì 2 `MatchPlayer`, 2 `Player` bên trong không vẽ thành thông điệp riêng.

**Quy ước riêng:**
- **REST:** mỗi lời gọi API là **một thông điệp** từ `XxxApiDAO` tới `XxxRestController`, ghi phương thức và đường dẫn (ví dụ `POST /api/matches/{id}/submit`). Kết quả trả về là một thông điệp ngược lại, ghi kiểu dữ liệu (ví dụ `Match`). Phía server tiếp tục như bài mẫu: Controller gọi DAO → DAO đóng gói Entity → trả về.
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
  - UC, lớp, bảng, trạng thái (`free` / `busy`, `pending` / `accepted` / `rejected`, `playing` / `finished`) phải trùng với `ISSUES_PLAN.md` mục 2 và với code.
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
- [ ] Biểu đồ lớp chi tiết có đủ boundary, control (DAO), entity. DAO con kế thừa lớp DAO cha.
- [ ] Kịch bản v3 và biểu đồ tuần tự khớp từng bước.
- [ ] Mọi tên lớp, hàm, bảng trong báo cáo đều có trong code ở nhánh `main`.
- [ ] Mỗi hình có "Hình x" và lời giải thích.
- [ ] Người còn lại đã đọc lại và góp ý.

---

## 6. Quy ước viết tài liệu trong `docs/`

- **Tài liệu chỉ chứa nội dung báo cáo.** Không ghi quy ước, luật, trạng thái, số issue, người làm / người duyệt vào tiêu đề hay nội dung tài liệu. Những thứ đó ghi ở file này (mục 6, 7).
- **Tiêu đề và chú thích hình không có ghi chú trong ngoặc.** Viết `## 1. Chức năng "Chơi online"`, không viết `## 1. Chức năng "Chơi online" (UC03)`; viết `*Hình 13: Biểu đồ lớp chi tiết chức năng Chơi online.*`, không thêm `(client + server)` hay `(phác thảo…)`. Tiêu đề trong ảnh PlantUML cũng vậy.
- **Ngoại lệ trong kịch bản v2** viết ở mức chung: "hiển thị thông báo không vào được phòng", không ghi `Toast`, `AlertDialog`.
- **Dữ liệu mẫu** chỉ dùng ở kịch bản v1 (ví dụ của Hùng: HUNG máy 1 mời, HOANG máy 2 được mời, MINH đang bận; trận số 37; câu đầu `12 + 7`; HUNG 10/10 câu, 38,2 giây; HOANG 10/10 câu, 45,6 giây).
- **Kịch bản v2, v3 viết ở mức chung:**
  - Tác nhân gọi là "người chơi" (hoặc "người dùng"), bên kia là "đối thủ". Không ghi tên người, không ghi "hiển thị cho Hùng".
  - Không nêu dữ liệu cụ thể: mã phòng, câu hỏi, điểm của ai bao nhiêu, kết quả WIN/LOSE. Viết "hiển thị điểm", "trả về kết quả trận".
  - Hàm ghi đầy đủ dạng "hàm onFinish()", không ghi tham số truyền vào.
  - Biểu đồ tuần tự đi kèm cũng vậy: tác nhân "Người chơi", thông điệp ghi hàm không tham số, không có dữ liệu cụ thể.
- **Không dùng định dạng backtick** ở mọi tài liệu trong docs/: tên lớp, hàm, bảng, id viết chữ thường. Đặc tả dạng luồng viết thành đoạn văn và danh sách lồng nhau, không đặt trong khối code. Chỉ giữ khối code cho câu lệnh SQL và cây dữ liệu Firebase. Ký tự < và * trong tên kiểu (List\<Match>) phải thoát bằng dấu \.
- **Kịch bản v1** chỉ có Người chơi và Hệ thống, không có tên lớp, tên hàm, thuật ngữ kỹ thuật.

---

## 7. Bảng tài liệu và việc còn lại

| Tài liệu | Bước | Vị trí trong báo cáo | Issue | Người làm / duyệt | Đầu vào |
|---|---|---|---|---|---|
| `docs/chung/01-pham-vi-use-case.md` | Phạm vi, UC tổng quát | Mở đầu, cả 2 báo cáo | #22 M1-01 | Hùng / Hoàng | — |
| `docs/chung/02-lop-thuc-the.md` | A2, D1 | Mục 3, cả 2 báo cáo | #23 M1-02 | Hùng / Hoàng | 01 |
| `docs/chung/03-csdl.md` | D2 | Mục 2: Hoàng phần SQLite + `tblPlayer` của MySQL, Hùng phần MySQL + Firebase | #24 M1-03 | Hùng / Hoàng | 02 phần B |
| `docs/hung/1-kich-ban-v1.md` | A1 | Mục 1 | #37 M3-01 | Hùng / Hoàng | 01 |
| `docs/hung/2-lop-phan-tich.md` | A3 | Phụ lục | #39 M3-03 | Hùng / Hoàng | v1, 02 phần A |
| `docs/hung/3-kich-ban-v2.md` | A4 | Phụ lục | #41 M3-05 | Hùng / Hoàng | v1, 2-lop-phan-tich |
| `docs/hung/4-lop-thiet-ke.md` | D3 | Mục 4.x.1, 4.x.2 | #43 M4-01 | Hùng / Hoàng | 2-lop-phan-tich, 02, 03 |
| `docs/hung/5-kich-ban-v3.md` | D4 | Mục 4.x.3 | #45 M4-03 | Hùng / Hoàng | v2, 4-lop-thiet-ke |
| `docs/hung/BaoCao_Hung.md` | Bản nháp báo cáo | Toàn bộ | #47 M4-05 | Hùng / Hoàng | Các file trên |
| `docs/hung/launch.md` | Bản báo cáo để dán vào file online (ảnh dạng @file.png, không in đậm) | Toàn bộ | #47 M4-05 | Hùng / Hoàng | Sinh từ BaoCao_Hung.md |

Lớp thực thể theo báo cáo: Hùng (UC03, UC04, UC01, UC02) dùng `Player`, `MathProblem`, `Challenge`, `Match`, `MatchPlayer`; Hoàng (UC05, UC06, UC01, UC02) dùng `Player`, `MathProblem`, `BotGame`, `Match`, `MatchPlayer`.

Phạm vi ở mức MVP: chỉ giữ các chức năng cần để demo ngày 28/10/2026.

**Trạng thái tài liệu của Hùng:** viết trước khi code, thiết kế dẫn code. Tên lớp, hàm trong `4-lop-thiet-ke.md` là chữ ký code M2-02 → M2-05 phải theo; code buộc phải khác thì sửa tài liệu theo code trước khi nộp.

**Bản 5 (26/10/2026):** toàn bộ tài liệu chung và tài liệu UC03, UC04, Đăng nhập – đăng ký của Hùng viết lại theo đề 9. Code trên `main` vẫn là bản 4 (phòng mã 6 số, lịch sử đấu), phải làm lại theo M1-04 → M2-05.

**Việc còn lại trước khi nộp (Hùng):**
1. Thay hình phác thảo giao diện bằng ảnh chụp app thật (M2-08).
2. Đối chiếu tên lớp, hàm, API trong tài liệu với code trên `main`.
3. Hoàng đọc lại.
4. Báo cáo cuối viết trên file online riêng, xuất một lần ra `docs/BaoCao_Hung.docx`. Hình trong báo cáo đánh số lại từ 1, khác số hình trong `docs/`.
