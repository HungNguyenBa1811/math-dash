# LỚP THỰC THỂ TOÀN HỆ THỐNG — MATH DASH 1v1

> Issue: #23 [M1-02] · Người làm: Hùng · Người duyệt: Hoàng
> Nguồn cho **mục 3 "Thiết kế lớp thực thể"** trong báo cáo của cả 2 người. Mỗi người chỉ lấy các lớp liên quan đến chức năng của mình (bảng ở mục A.8).
>
> **Quy ước đặt tên:**
> - Tên lớp tiếng Anh, viết PascalCase, **trùng tên lớp trong code** (`entity/`).
> - Tên thuộc tính viết camelCase.
> - Pha phân tích **chưa có kiểu dữ liệu, chưa có `id`, chưa cần đủ thuộc tính**. Biểu đồ chỉ có tên lớp, thuộc tính chính và quan hệ.
> - Quan hệ "thông tin của A nằm trong B" vẽ là kết tập `A --<> B`, hình thoi đặt ở phía B.

---

# PHẦN A — PHA PHÂN TÍCH

## A.1. Bước 1: Mô tả hệ thống trong một đoạn văn

Hệ thống là một ứng dụng Android giúp người chơi luyện tính nhẩm. Mỗi người chơi được nhận diện bằng tên nhập ở màn hình chính; trên một máy có thể có nhiều người chơi.

Khi chơi offline, người chơi bắt đầu một ván chơi với 3 mạng. Trong ván chơi, hệ thống lần lượt đưa ra các câu hỏi, mỗi câu hỏi là một phép tính gồm hai toán hạng và một toán tử, kèm 4 phương án trả lời trong đó có một đáp án đúng. Cứ 5 câu hỏi thì mức độ khó tăng một bậc, tối đa 4 bậc. Mỗi câu có 6 giây để trả lời. Trả lời đúng thì người chơi được cộng điểm theo chuỗi đúng liên tiếp; trả lời sai hoặc hết giờ thì mất một mạng và chuỗi đúng trở về 0. Khi hết mạng, ván chơi kết thúc và hệ thống lưu kết quả ván gồm điểm, chuỗi đúng dài nhất, số câu đúng, số câu sai và thời điểm chơi. Kỷ lục của người chơi là điểm cao nhất trong các kết quả ván của người đó. Bảng vàng hiển thị các kết quả ván có điểm cao nhất trên máy.

Khi chơi online, người chơi tìm trận: hệ thống đăng nhập ẩn danh để lấy mã định danh của máy, rồi tìm một phòng đấu đang chờ. Nếu có, người chơi vào phòng đó; nếu không, hệ thống tạo phòng đấu mới cùng mã đồng bộ câu hỏi và chờ đối thủ. Mỗi phòng đấu có trạng thái và tối đa hai người chơi trong phòng; mỗi người chơi trong phòng có mã định danh, tên, điểm, trạng thái trực tuyến và trạng thái đã xong trận. Hai người chơi trong cùng một phòng thi đấu 60 giây trên cùng một dãy câu hỏi được sinh từ mã đồng bộ, trả lời sai không mất mạng. Điểm của mỗi người được cập nhật lên phòng đấu để đối thủ thấy ngay. Hết giờ, người nhiều điểm hơn thắng, bằng điểm thì hòa; người bỏ trận bị xử thua. Kết quả trận gồm tên đối thủ, điểm của mình, điểm của đối thủ và thời điểm đấu được lưu vào lịch sử đấu của người chơi; người chơi có thể xem và xóa các trận trong lịch sử. Người chơi cũng có thể bật hoặc tắt âm thanh và rung.

## A.2. Bước 2: Trích các danh từ xuất hiện trong đoạn văn

- **Các danh từ liên quan đến người:** người chơi, đối thủ, chủ phòng, người chơi trong phòng.
- **Các danh từ liên quan đến vật:** ứng dụng, máy (điện thoại), màn hình chính.
- **Các danh từ liên quan đến thông tin:** tên, mã định danh, ván chơi, mạng, câu hỏi, phép tính, toán hạng, toán tử, phương án trả lời, đáp án đúng, mức độ khó, thời gian (6 giây, 60 giây), điểm, chuỗi đúng, chuỗi đúng dài nhất, số câu đúng, số câu sai, kết quả ván, thời điểm chơi, kỷ lục, bảng vàng, phòng đấu, trạng thái phòng, mã đồng bộ câu hỏi, trạng thái trực tuyến, trạng thái đã xong trận, trận đấu, kết quả trận (thắng / thua / hòa), lịch sử đấu, thời điểm đấu, âm thanh, rung.

## A.3. Bước 3: Đánh giá và lựa chọn các danh từ làm lớp thực thể hoặc thuộc tính

- **Các danh từ trừu tượng hoặc thuộc về giao diện:** hệ thống, ứng dụng, máy, màn hình chính → **loại**. Màn hình là lớp giao diện (boundary), không phải thực thể.
- **Các danh từ liên quan đến người:**
  - Người chơi → lớp `Player`: tên (`username`), thời điểm tạo (`createdAt`).
  - Tên → thuộc tính của `Player`.
  - Đối thủ → là một người chơi trên **máy khác**. Hồ sơ của đối thủ không nằm trong CSDL của máy mình, máy mình chỉ biết tên đối thủ → **không đề xuất lớp**. Trong lịch sử đấu, tên đối thủ là thuộc tính `opponentName` của kết quả trận.
  - Chủ phòng → là người chơi đã tạo phòng, cũng là người tạo mã đồng bộ câu hỏi → **không đề xuất lớp**.
  - Người chơi trong phòng → lớp `RoomPlayer`: mã định danh (`uid`), tên (`name`), điểm (`score`), đang trực tuyến (`online`), đã xong trận (`finished`). Giữ thành lớp riêng vì đây là thông tin của **một người chơi trong một phòng cụ thể**: cùng một người chơi, mỗi phòng có điểm và trạng thái khác nhau. Lớp này là lớp trung gian của quan hệ n-n ở bước 4.
  - Mã định danh → thuộc tính `uid` của `RoomPlayer`, do máy chủ cấp khi đăng nhập ẩn danh.
- **Các danh từ liên quan đến thông tin:**
  - Câu hỏi / phép tính → cùng một khái niệm → lớp `MathProblem`: toán hạng thứ nhất (`operand1`), toán hạng thứ hai (`operand2`), toán tử (`operator`), đáp án đúng (`correctAnswer`), các phương án trả lời (`options`).
  - Toán hạng, toán tử, đáp án đúng, phương án trả lời → thuộc tính của `MathProblem`.
  - Ván chơi → lớp `GameSession`: điểm (`score`), chuỗi đúng (`streak`), chuỗi đúng dài nhất (`maxStreak`), số mạng còn lại (`lives`), số câu đúng (`totalCorrect`), số câu sai (`totalWrong`), đã kết thúc (`isGameOver`). Đây là trạng thái của ván **đang chơi**; ván chơi online dùng chung lớp này nhưng không trừ mạng.
  - Mạng, điểm, chuỗi đúng, chuỗi đúng dài nhất, số câu đúng, số câu sai → thuộc tính của `GameSession`.
  - Mức độ khó → tính được từ số câu đã ra (`min(số câu / 5 + 1, 4)`) → thuộc tính dẫn xuất, **không đề xuất**.
  - Thời gian 6 giây / 60 giây → là bộ đếm giờ của luật chơi, không phải dữ liệu cần lưu → **loại**.
  - Kết quả ván → lớp `GameRecord`: điểm (`score`), chuỗi đúng dài nhất (`maxStreak`), số câu đúng (`totalCorrect`), số câu sai (`totalWrong`), thời điểm chơi (`playedAt`). Đây là bản lưu lại khi ván chơi kết thúc.
  - Kỷ lục → là điểm cao nhất trong các `GameRecord` của một người chơi → thuộc tính dẫn xuất, **không đề xuất**.
  - Bảng vàng → là danh sách các `GameRecord` sắp theo điểm giảm dần, không có thông tin riêng nào ngoài các kết quả ván → **loại**.
  - Phòng đấu → lớp `Room`: trạng thái (`status`: `waiting` / `playing` / `finished`), mã đồng bộ câu hỏi (`seed`), thời điểm tạo (`createdAt`).
  - Trận đấu → mỗi phòng đấu chỉ diễn ra đúng một trận và trận không có thông tin riêng ngoài thông tin của phòng (quan hệ 1-1) → **gộp vào lớp `Room`**, không đề xuất lớp.
  - Trạng thái phòng, mã đồng bộ câu hỏi → thuộc tính của `Room`.
  - Trạng thái trực tuyến, trạng thái đã xong trận → thuộc tính của `RoomPlayer`.
  - Kết quả trận / lịch sử đấu → lịch sử đấu là danh sách các kết quả trận → lớp `MatchHistory` cho **một** kết quả trận: tên đối thủ (`opponentName`), điểm của mình (`myScore`), điểm của đối thủ (`opponentScore`), thời điểm đấu (`playedAt`).
  - Thắng / thua / hòa → suy ra được bằng cách so `myScore` với `opponentScore` → thuộc tính dẫn xuất, **không lưu**.
  - Âm thanh, rung → là cấu hình của thiết bị, không phải đối tượng nghiệp vụ, không có quan hệ với lớp nào → **loại** (lưu bằng SharedPreferences ở pha thiết kế).

## A.4. Bước 4: Xác định quan hệ số lượng giữa các thực thể

- Một người chơi chơi nhiều ván chơi, một ván chơi thuộc về một người chơi → `Player` – `GameSession` là **1-n**.
- Một ván chơi lần lượt có nhiều câu hỏi, mỗi câu hỏi được sinh ra cho đúng một ván chơi → `GameSession` – `MathProblem` là **1-n**. Ván chơi chỉ giữ câu hỏi đang hiển thị.
- Một ván chơi kết thúc sinh ra đúng một kết quả ván, một kết quả ván ứng với một ván chơi → `GameSession` – `GameRecord` là **1-1**. **Giữ 2 lớp riêng** vì vòng đời khác nhau: `GameSession` chỉ tồn tại trong bộ nhớ khi đang chơi, còn `GameRecord` là bản được lưu lâu dài. `GameRecord` cũng không giữ những thông tin chỉ có ý nghĩa khi đang chơi (chuỗi đúng hiện tại, số mạng, câu hỏi hiện tại).
- Một người chơi có nhiều kết quả ván, một kết quả ván thuộc về một người chơi → `Player` – `GameRecord` là **1-n**.
- Một người chơi có nhiều kết quả trận, một kết quả trận thuộc về một người chơi → `Player` – `MatchHistory` là **1-n**.
- Một người chơi tham gia nhiều phòng đấu theo thời gian, một phòng đấu có đúng 2 người chơi → `Player` – `Room` là **n-n** → đề xuất lớp trung gian `RoomPlayer` xác định duy nhất một người chơi trong một phòng, kèm điểm và trạng thái của người đó trong phòng.
- Một phòng đấu kết thúc sinh ra một kết quả trận **trên mỗi máy** (theo góc nhìn của người chơi trên máy đó). Phòng đấu không được giữ lại sau khi trận kết thúc và kết quả trận chỉ cần tên đối thủ cùng 2 điểm số → `Room` – `MatchHistory` **không nối trực tiếp**. Các thông tin cần thiết được sao chép sang `MatchHistory` khi trận kết thúc.
- Một trận đấu dùng câu hỏi sinh từ mã đồng bộ của phòng; câu hỏi không được lưu trên phòng (cả 2 máy tự sinh ra cùng một dãy) → `Room` – `MathProblem` **không nối trực tiếp**.

## A.5. Bước 5: Xác định quan hệ đối tượng giữa các thực thể

- **Quan hệ kế thừa (generalization):** không có. Các lớp không chia sẻ thuộc tính chung theo kiểu cha – con. Ván offline và ván online khác nhau duy nhất ở luật trừ mạng nên dùng chung lớp `GameSession`.
- **Quan hệ gắn chặt (composition)** — thành phần không thể tồn tại nếu không có tổng thể:
  - Câu hỏi là thành phần của ván chơi: câu hỏi chỉ được sinh ra và có ý nghĩa trong một ván chơi.
- **Quan hệ hợp thành (aggregation)** — dùng cho các quan hệ 1-n, hình thoi đặt ở lớp chứa thông tin của lớp kia:
  - Thông tin người chơi nằm trong thông tin ván chơi.
  - Thông tin người chơi nằm trong thông tin kết quả ván.
  - Thông tin người chơi nằm trong thông tin kết quả trận.
- **Quan hệ liên kết (association)** — dùng cho quan hệ n-n, lớp trung gian được vẽ thành lớp liên kết gắn vào đường association:
  - Người chơi và Phòng đấu liên kết tạo ra Người chơi trong phòng duy nhất.

Kết quả thu được biểu đồ lớp thực thể pha phân tích toàn hệ thống như mục A.7.

## A.6. Danh sách lớp thực thể

| TT | Lớp | Nguồn gốc | Ghi chú |
|---|---|---|---|
| 1 | `Player` | Người chơi | Hồ sơ người chơi trên máy |
| 2 | `MathProblem` | Câu hỏi / phép tính | Thành phần của `GameSession`, không lưu |
| 3 | `GameSession` | Ván chơi | Trạng thái ván đang chơi, dùng cho cả offline và online, không lưu |
| 4 | `GameRecord` | Kết quả ván | Bản lưu khi ván offline kết thúc; kỷ lục và bảng vàng suy ra từ lớp này |
| 5 | `Room` | Phòng đấu (gộp trận đấu) | Lưu trên máy chủ |
| 6 | `RoomPlayer` | Người chơi trong phòng | Lớp liên kết của quan hệ n-n `Player` – `Room`, lưu trên máy chủ |
| 7 | `MatchHistory` | Kết quả trận / lịch sử đấu | Bản lưu trên máy khi trận online kết thúc; thắng / thua / hòa suy ra từ 2 điểm |

## A.7. Biểu đồ lớp thực thể pha phân tích

![Hình 2: Biểu đồ lớp thực thể pha phân tích](lop-thuc-the-phan-tich.png)

*Hình 2: Biểu đồ lớp thực thể pha phân tích toàn hệ thống Math Dash 1v1.*

Biểu đồ có 7 lớp thực thể.
- `Player` là trung tâm: thông tin người chơi nằm trong ván chơi, kết quả ván và kết quả trận, nên cả 3 lớp này có hình thoi rỗng (kết tập) ở phía mình.
- `MathProblem` là thành phần gắn chặt của `GameSession` (hình thoi đặc), vì câu hỏi chỉ tồn tại trong một ván.
- Quan hệ n-n giữa `Player` và `Room` được tách bằng lớp liên kết `RoomPlayer`, nơi giữ điểm và trạng thái của từng người trong phòng.
- Các thuộc tính dẫn xuất (kỷ lục, mức độ khó, thắng / thua / hòa) không xuất hiện trên biểu đồ.

Mã nguồn PlantUML: [`lop-thuc-the-phan-tich.puml`](lop-thuc-the-phan-tich.puml).

## A.8. Lớp thực thể dùng cho chức năng nào

Mỗi người lấy đúng các lớp có đánh dấu ở cột chức năng của mình vào mục 3 báo cáo cá nhân.

| Lớp | UC01 Tìm trận | UC02 Thi đấu online | UC03 Quản lý lịch sử đấu | UC04 Chơi offline | UC05 Xem bảng vàng | UC06 Cài đặt âm thanh |
|---|---|---|---|---|---|---|
| `Player` | ✓ | ✓ | ✓ | ✓ | ✓ | |
| `MathProblem` | | ✓ | | ✓ | | |
| `GameSession` | | ✓ | | ✓ | | |
| `GameRecord` | | | | ✓ | ✓ | |
| `Room` | ✓ | ✓ | | | | |
| `RoomPlayer` | ✓ | ✓ | | | | |
| `MatchHistory` | | ✓ | ✓ | | | |

- **Báo cáo của Hùng** (UC01–UC03): `Player`, `MathProblem`, `GameSession`, `Room`, `RoomPlayer`, `MatchHistory`.
- **Báo cáo của Hoàng** (UC04–UC06): `Player`, `MathProblem`, `GameSession`, `GameRecord`. UC06 không dùng lớp thực thể nào; cấu hình âm thanh lưu bằng SharedPreferences.
