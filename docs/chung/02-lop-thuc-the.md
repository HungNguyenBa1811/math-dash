# LỚP THỰC THỂ TOÀN HỆ THỐNG — MATH DASH 1v1

> Issue: #23 [M1-02] · Người làm: Hùng · Người duyệt: Hoàng
> Nguồn cho **mục 3 "Thiết kế lớp thực thể"** trong báo cáo của cả 2 người. Mỗi người chỉ lấy các lớp liên quan đến chức năng của mình (bảng ở mục A.8).
>
> **Quy ước đặt tên:**
> - Tên lớp tiếng Anh, viết PascalCase, **trùng tên lớp trong code**.
> - Tên thuộc tính viết camelCase.
> - Pha phân tích **chưa có kiểu dữ liệu, chưa có `id`, chưa cần đủ thuộc tính**. Biểu đồ chỉ có tên lớp, thuộc tính chính và quan hệ.
> - Quan hệ "thông tin của A nằm trong B" vẽ là kết tập `A --<> B`, hình thoi đặt ở phía B.

---

# PHẦN A — PHA PHÂN TÍCH

## A.1. Bước 1: Mô tả hệ thống trong một đoạn văn

Hệ thống là một ứng dụng giúp người chơi luyện tính nhẩm. Mỗi người chơi có một tài khoản gồm tên đăng nhập, mật khẩu và tên hiển thị; người chơi đăng ký tài khoản rồi đăng nhập để dùng ứng dụng.

Khi chơi offline, người chơi chọn một độ khó (Dễ, Trung bình hoặc Khó) rồi bắt đầu một ván chơi với 3 mạng. Trong ván chơi, hệ thống lần lượt đưa ra các câu hỏi; mỗi câu hỏi là một phép tính gồm hai toán hạng và một toán tử, kèm 4 phương án trả lời trong đó có một đáp án đúng. Độ khó quyết định mức bắt đầu của ván; cứ 5 câu hỏi thì mức tăng một bậc. Mỗi câu có một khoảng thời gian trả lời lấy từ cấu hình game. Trả lời đúng thì người chơi được cộng điểm theo chuỗi đúng liên tiếp; trả lời sai hoặc hết giờ thì mất một mạng và chuỗi đúng trở về 0. Khi hết mạng, hệ thống lưu kết quả ván gồm độ khó, điểm, chuỗi đúng dài nhất, số câu đúng, số câu sai và thời điểm chơi. Kỷ lục của người chơi ở một độ khó là điểm cao nhất trong các kết quả ván của người đó ở độ khó đó, và được hiển thị ngay ở màn chơi. Cấu hình game gồm âm thanh bật hay tắt, rung bật hay tắt, và thời gian trả lời mỗi câu.

Khi chơi online, người chơi tạo một trận đấu: hệ thống sinh mã phòng và mã đồng bộ câu hỏi, rồi chờ đối thủ. Người chơi kia nhập mã phòng để vào trận. Mỗi trận đấu có trạng thái, thời điểm tạo, thời điểm kết thúc và đúng hai người chơi; với mỗi người, trận đấu ghi nhận điểm và việc người đó có bỏ trận hay không. Hai người thi đấu 60 giây trên cùng một dãy câu hỏi sinh từ mã đồng bộ, trả lời sai không mất mạng; điểm của mỗi người được cập nhật để đối thủ thấy ngay. Hết giờ, người nhiều điểm hơn thắng, bằng điểm thì hòa; người bỏ trận bị xử thua. Người chơi có thể xem lịch sử đấu gồm các trận online đã đấu.

## A.2. Bước 2: Trích các danh từ xuất hiện trong đoạn văn

- **Các danh từ liên quan đến người:** người chơi, đối thủ, người chơi kia.
- **Các danh từ liên quan đến vật:** ứng dụng, màn chơi.
- **Các danh từ liên quan đến thông tin:** tài khoản, tên đăng nhập, mật khẩu, tên hiển thị, độ khó, ván chơi, mạng, câu hỏi, phép tính, toán hạng, toán tử, phương án trả lời, đáp án đúng, mức, thời gian trả lời, điểm, chuỗi đúng, chuỗi đúng dài nhất, số câu đúng, số câu sai, kết quả ván, thời điểm chơi, kỷ lục, cấu hình game, âm thanh, rung, trận đấu, mã phòng, mã đồng bộ câu hỏi, trạng thái, thời điểm tạo, thời điểm kết thúc, bỏ trận, kết quả trận (thắng / thua / hòa), lịch sử đấu.

## A.3. Bước 3: Đánh giá và lựa chọn các danh từ làm lớp thực thể hoặc thuộc tính

- **Các danh từ trừu tượng hoặc thuộc về giao diện:** hệ thống, ứng dụng, màn chơi → **loại**. Màn chơi là lớp giao diện (boundary), không phải thực thể.
- **Các danh từ liên quan đến người:**
  - Người chơi → lớp `Player`: tên đăng nhập (`username`), mật khẩu (`password`), tên hiển thị (`displayName`), thời điểm tạo (`createdAt`).
  - Tài khoản → là thông tin đăng nhập của người chơi, quan hệ 1-1 → **gộp vào `Player`**.
  - Tên đăng nhập, mật khẩu, tên hiển thị → thuộc tính của `Player`.
  - Đối thủ, người chơi kia → cũng là một `Player` (có tài khoản trong hệ thống), đóng vai trò người chơi thứ hai của trận → **không đề xuất lớp**.
- **Các danh từ liên quan đến thông tin của chế độ offline:**
  - Câu hỏi / phép tính → cùng một khái niệm → lớp `MathProblem`: toán hạng thứ nhất (`operand1`), toán hạng thứ hai (`operand2`), toán tử (`operator`), đáp án đúng (`correctAnswer`), các phương án trả lời (`options`).
  - Ván chơi → lớp `GameSession`: độ khó (`difficulty`), điểm (`score`), chuỗi đúng (`streak`), chuỗi đúng dài nhất (`maxStreak`), số mạng còn lại (`lives`), số câu đúng (`totalCorrect`), số câu sai (`totalWrong`), đã kết thúc (`isGameOver`). Đây là trạng thái của ván **đang chơi**; trận online cũng dùng lớp này để chấm điểm cho từng người, nhưng không trừ mạng.
  - Độ khó, mạng, điểm, chuỗi đúng, số câu đúng, số câu sai → thuộc tính của `GameSession`.
  - Mức → tính được từ độ khó và số câu đã ra → thuộc tính dẫn xuất, **không đề xuất**.
  - Kết quả ván → lớp `GameRecord`: độ khó (`difficulty`), điểm (`score`), chuỗi đúng dài nhất (`maxStreak`), số câu đúng (`totalCorrect`), số câu sai (`totalWrong`), thời điểm chơi (`playedAt`). Đây là bản lưu lại khi ván kết thúc.
  - Kỷ lục → là điểm cao nhất trong các `GameRecord` của một người chơi ở một độ khó → thuộc tính dẫn xuất, **không đề xuất**.
  - Cấu hình game → lớp `GameConfig`: âm thanh bật (`soundOn`), rung bật (`vibrationOn`), thời gian mỗi câu (`secondsPerQuestion`). Giữ thành lớp vì thời gian mỗi câu là dữ liệu quyết định luật chơi của ván, không chỉ là tùy chọn hiển thị.
  - Âm thanh, rung, thời gian trả lời → thuộc tính của `GameConfig`.
- **Các danh từ liên quan đến thông tin của chế độ online:**
  - Trận đấu → lớp `Match`: mã phòng (`code`), trạng thái (`status`: `waiting` / `playing` / `finished`), mã đồng bộ câu hỏi (`seed`), thời điểm tạo (`createdAt`), thời điểm kết thúc (`endedAt`).
  - Phòng đấu → mỗi phòng chỉ diễn ra đúng một trận và phòng không có thông tin riêng ngoài mã phòng (quan hệ 1-1) → **gộp vào `Match`**, mã phòng là thuộc tính `code`.
  - Mã phòng, mã đồng bộ câu hỏi, trạng thái, thời điểm tạo, thời điểm kết thúc → thuộc tính của `Match`.
  - Điểm của từng người trong trận, bỏ trận → là thông tin của **một người chơi trong một trận cụ thể**, không thuộc riêng `Player` hay `Match` → quyết định ở bước 4.
  - Kết quả trận (thắng / thua / hòa) → suy ra được từ việc bỏ trận và điểm của 2 người → thuộc tính dẫn xuất, **không lưu**.
  - Lịch sử đấu → là danh sách các `Match` đã kết thúc mà người chơi tham gia, không có thông tin riêng → **loại**.

## A.4. Bước 4: Xác định quan hệ số lượng giữa các thực thể

- Một người chơi chơi nhiều ván chơi, một ván chơi thuộc về một người chơi → `Player` – `GameSession` là **1-n**.
- Một ván chơi lần lượt có nhiều câu hỏi, mỗi câu hỏi được sinh ra cho đúng một ván chơi → `GameSession` – `MathProblem` là **1-n**. Ván chơi chỉ giữ câu hỏi đang hiển thị.
- Một ván chơi kết thúc sinh ra đúng một kết quả ván, một kết quả ván ứng với một ván chơi → `GameSession` – `GameRecord` là **1-1**. **Giữ 2 lớp riêng** vì vòng đời khác nhau: `GameSession` chỉ tồn tại khi đang chơi, còn `GameRecord` là bản lưu lâu dài. `GameRecord` cũng không giữ những thông tin chỉ có ý nghĩa khi đang chơi (chuỗi đúng hiện tại, số mạng, câu hỏi hiện tại).
- Một người chơi có nhiều kết quả ván, một kết quả ván thuộc về một người chơi → `Player` – `GameRecord` là **1-n**.
- Một cấu hình game được dùng cho nhiều ván chơi, mỗi ván chơi dùng một cấu hình → `GameConfig` – `GameSession` là **1-n**.
- Một người chơi tham gia nhiều trận đấu, một trận đấu có đúng 2 người chơi → `Player` – `Match` là **n-n** → đề xuất lớp trung gian `MatchPlayer` (người chơi trong trận) xác định duy nhất một người chơi trong một trận, kèm điểm (`score`) và bỏ trận (`left`) của người đó.
- Một trận đấu dùng các câu hỏi sinh từ mã đồng bộ; câu hỏi không được lưu cùng trận (cả 2 máy tự sinh ra cùng một dãy) → `Match` – `MathProblem` **không nối trực tiếp**.

## A.5. Bước 5: Xác định quan hệ đối tượng giữa các thực thể

- **Quan hệ kế thừa (generalization):** không có. Ván offline và trận online khác nhau duy nhất ở luật trừ mạng nên dùng chung lớp `GameSession` để chấm điểm.
- **Quan hệ gắn chặt (composition)** — thành phần không thể tồn tại nếu không có tổng thể:
  - Câu hỏi là thành phần của ván chơi: câu hỏi chỉ được sinh ra và có ý nghĩa trong một ván chơi.
- **Quan hệ hợp thành (aggregation)** — dùng cho các quan hệ 1-n, hình thoi đặt ở lớp chứa thông tin của lớp kia:
  - Thông tin người chơi nằm trong thông tin ván chơi.
  - Thông tin người chơi nằm trong thông tin kết quả ván.
  - Thông tin cấu hình game nằm trong thông tin ván chơi.
- **Quan hệ liên kết (association)** — dùng cho quan hệ n-n, lớp trung gian được vẽ thành lớp liên kết gắn vào đường association:
  - Người chơi và Trận đấu liên kết tạo ra Người chơi trong trận duy nhất.

Kết quả thu được biểu đồ lớp thực thể pha phân tích toàn hệ thống như mục A.7.

## A.6. Danh sách lớp thực thể

| TT | Lớp | Nguồn gốc | Ghi chú |
|---|---|---|---|
| 1 | `Player` | Người chơi (gộp tài khoản) | |
| 2 | `MathProblem` | Câu hỏi / phép tính | Thành phần của `GameSession`, không lưu |
| 3 | `GameSession` | Ván chơi | Trạng thái ván đang chơi, dùng cho cả offline và online, không lưu |
| 4 | `GameRecord` | Kết quả ván | Bản lưu khi ván offline kết thúc; kỷ lục suy ra từ lớp này |
| 5 | `GameConfig` | Cấu hình game | |
| 6 | `Match` | Trận đấu (gộp phòng đấu) | Lịch sử đấu và kết quả trận suy ra từ lớp này |
| 7 | `MatchPlayer` | Người chơi trong trận | Lớp liên kết của quan hệ n-n `Player` – `Match` |

## A.7. Biểu đồ lớp thực thể pha phân tích

![Hình 2: Biểu đồ lớp thực thể pha phân tích](lop-thuc-the-phan-tich.png)

*Hình 2: Biểu đồ lớp thực thể pha phân tích toàn hệ thống Math Dash 1v1.*

Biểu đồ có 7 lớp thực thể.
- **Phía offline:** `GameSession` là trung tâm. Thông tin người chơi và cấu hình game nằm trong ván chơi (hình thoi rỗng ở phía `GameSession`). Câu hỏi là thành phần gắn chặt của ván (hình thoi đặc). Khi ván kết thúc, kết quả được lưu thành `GameRecord`, cũng chứa thông tin người chơi.
- **Phía online:** quan hệ n-n giữa `Player` và `Match` được tách bằng lớp liên kết `MatchPlayer`, nơi giữ điểm và trạng thái bỏ trận của từng người.
- **Các thuộc tính dẫn xuất** không có trên biểu đồ: kỷ lục, mức, kết quả trận, lịch sử đấu.

Mã nguồn PlantUML: [`lop-thuc-the-phan-tich.puml`](lop-thuc-the-phan-tich.puml).

## A.8. Lớp thực thể dùng cho chức năng nào

| Lớp | UC01 Đăng ký | UC02 Đăng nhập | UC03 Chơi offline | UC04 Cài đặt cấu hình game | UC05 Chơi online | UC06 Xem lịch sử đấu |
|---|---|---|---|---|---|---|
| `Player` | ✓ | ✓ | ✓ | | ✓ | ✓ |
| `MathProblem` | | | ✓ | | ✓ | |
| `GameSession` | | | ✓ | | ✓ | |
| `GameRecord` | | | ✓ | | | |
| `GameConfig` | | | ✓ | ✓ | | |
| `Match` | | | | | ✓ | ✓ |
| `MatchPlayer` | | | | | ✓ | ✓ |

- **Báo cáo của Hùng** (UC05, UC06; thêm UC01, UC02 nếu trình bày): `Player`, `MathProblem`, `GameSession`, `Match`, `MatchPlayer`.
- **Báo cáo của Hoàng** (UC03, UC04): `Player`, `MathProblem`, `GameSession`, `GameRecord`, `GameConfig`.
