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
  - Câu hỏi / phép tính → cùng một khái niệm → lớp `MathProblem`: toán hạng thứ nhất (`operand1`), toán hạng thứ hai (`operand2`), toán tử (`operator`), đáp án đúng (`correctAnswer`), các phương án trả lời (`answerOptions`).
  - Ván chơi → lớp `GameSession`: độ khó (`difficulty`), điểm (`score`), chuỗi đúng (`currentStreak`), chuỗi đúng dài nhất (`maxStreak`), số mạng còn lại (`livesLeft`), số câu đúng (`correctCount`), số câu sai, tính cả câu hết giờ (`wrongCount`), đã kết thúc (`gameOver`). Đây là trạng thái của ván **đang chơi**; trận online cũng dùng lớp này để chấm điểm cho từng người, nhưng không trừ mạng.
  - Độ khó, mạng, điểm, chuỗi đúng, số câu đúng, số câu sai → thuộc tính của `GameSession`.
  - Mức → tính được từ độ khó và số câu đã ra → thuộc tính dẫn xuất, **không đề xuất**.
  - Kết quả ván → lớp `GameRecord`: độ khó (`difficulty`), điểm (`score`), chuỗi đúng dài nhất (`maxStreak`), số câu đúng (`correctCount`), số câu sai (`wrongCount`), thời điểm chơi (`playedAt`). Đây là bản lưu lại khi ván kết thúc.
  - Kỷ lục → là điểm cao nhất trong các `GameRecord` của một người chơi ở một độ khó → thuộc tính dẫn xuất, **không đề xuất**.
  - Cấu hình game → lớp `GameConfig`: âm thanh bật (`soundEnabled`), rung bật (`vibrationEnabled`), thời gian mỗi câu (`secondsPerQuestion`). Giữ thành lớp vì thời gian mỗi câu là dữ liệu quyết định luật chơi của ván, không chỉ là tùy chọn hiển thị.
  - Âm thanh, rung, thời gian trả lời → thuộc tính của `GameConfig`.
- **Các danh từ liên quan đến thông tin của chế độ online:**
  - Trận đấu → lớp `Match`: mã phòng (`roomCode`), trạng thái (`status`: `waiting` / `playing` / `finished`), mã đồng bộ câu hỏi (`questionSeed`), thời điểm tạo (`createdAt`), thời điểm kết thúc (`endedAt`).
  - Phòng đấu → mỗi phòng chỉ diễn ra đúng một trận và phòng không có thông tin riêng ngoài mã phòng (quan hệ 1-1) → **gộp vào `Match`**, mã phòng là thuộc tính `roomCode`.
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
- Một người chơi tham gia nhiều trận đấu, một trận đấu có đúng 2 người chơi → `Player` – `Match` là **n-n** → đề xuất lớp trung gian `MatchPlayer` (người chơi trong trận) xác định duy nhất một người chơi trong một trận, kèm điểm (`score`) và bỏ trận (`forfeited`) của người đó.
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

---

# PHẦN B — PHA THIẾT KẾ

Đầu vào là biểu đồ lớp thực thể pha phân tích (Hình 2). Ngôn ngữ lập trình là Java, dùng chung cho app Android và backend Spring Boot.

## B.1. Bước 1: Bổ sung thuộc tính khóa

Bổ sung thuộc tính `id : int` cho các lớp **cần lưu trữ lâu dài** và **cần phân biệt từng đối tượng**: `Player`, `GameRecord`, `Match`, `MatchPlayer`. Không có lớp nào kế thừa lớp khác.

Không bổ sung `id` cho:
- `MathProblem`, `GameSession`: chỉ tồn tại trong bộ nhớ khi đang chơi, không được lưu, không lớp nào tham chiếu tới từng đối tượng.
- `GameConfig`: mỗi máy chỉ có đúng một cấu hình, không cần phân biệt.

`Match` có thêm mã phòng `roomCode` để người chơi nhập khi tìm phòng. Mã phòng chỉ duy nhất trong các trận chưa kết thúc (các trận đã kết thúc có thể trùng mã), nên **không dùng làm khóa** mà vẫn cần `id` riêng.

## B.2. Bước 2: Bổ sung kiểu dữ liệu

Quy ước: chuỗi → `String`; số đếm, điểm, số giây → `int`; mã đồng bộ câu hỏi → `long` (khớp kiểu tham số của `java.util.Random`); thời điểm → `Date`; cờ bật / tắt → `boolean`. Độ khó dùng `int`: 1 = Dễ, 2 = Trung bình, 3 = Khó (cũng chính là mức bắt đầu của ván).

Bổ sung 2 thuộc tính phục vụ thiết kế cho `GameSession`, không có ở pha phân tích:
- `onlineMode : boolean`: cùng một lớp chấm điểm cho cả 2 chế độ; `true` là trận online, không trừ mạng.
- `questionsAsked : int`: số câu đã ra trong ván, dùng để tính mức hiện tại.

Đổi tên 1 thuộc tính so với pha phân tích: `password` của `Player` thành **`passwordHash`**, vì hệ thống chỉ lưu mật khẩu đã băm (BCrypt), không lưu mật khẩu gốc.

Thuộc tính kiểu `boolean` đặt tên theo trạng thái, không có tiền tố `is` (`gameOver`, `forfeited`, `soundEnabled`…); getter tương ứng là `isGameOver()`, `isForfeited()`, `isSoundEnabled()` theo quy ước JavaBean. Nếu đặt tên trường là `isForfeited` thì getter thành `isIsForfeited()`, và thư viện chuyển JSON (Jackson ở server, Firebase SDK ở app) sẽ đọc sai tên trường.

| Lớp | Thuộc tính |
|---|---|
| `Player` | id: int, username: String, passwordHash: String, displayName: String, createdAt: Date |
| `MathProblem` | operand1: int, operand2: int, operator: String, correctAnswer: int, answerOptions: List&lt;Integer&gt; |
| `GameSession` | difficulty: int, onlineMode: boolean, questionsAsked: int, score: int, currentStreak: int, maxStreak: int, livesLeft: int, correctCount: int, wrongCount: int, gameOver: boolean |
| `GameRecord` | id: int, difficulty: int, score: int, maxStreak: int, correctCount: int, wrongCount: int, playedAt: Date |
| `GameConfig` | soundEnabled: boolean, vibrationEnabled: boolean, secondsPerQuestion: int |
| `Match` | id: int, roomCode: String, status: String, questionSeed: long, createdAt: Date, endedAt: Date |
| `MatchPlayer` | id: int, score: int, forfeited: boolean |

## B.3. Bước 3: Chuyển quan hệ liên kết thành kết tập / hợp thành

- Quan hệ n-n `Player` – `Match` qua lớp liên kết `MatchPlayer` chuyển thành:
  - **`Match` chứa `MatchPlayer`** (hợp thành, 1 – 2): người chơi trong trận chỉ có nghĩa khi thuộc về một trận; xóa trận thì 2 dòng người chơi trong trận cũng mất.
  - **`MatchPlayer` chứa `Player`** (kết tập, n – 1): người chơi tồn tại độc lập với trận, mỗi người chơi trong trận chỉ tham chiếu đúng một người chơi.

  Lí do chọn hướng chứa này: khi xử lí (kết thúc trận, xem lịch sử đấu), hệ thống luôn lấy trận kèm cả 2 người chơi trong trận để so điểm và biết ai bỏ trận.
- Các quan hệ đã là kết tập / hợp thành từ pha phân tích giữ nguyên:
  - `GameSession` chứa `MathProblem` (hợp thành).
  - `GameSession` chứa `Player`, `GameConfig` (kết tập).
  - `GameRecord` chứa `Player` (kết tập).

Sau bước này không còn đường liên kết trơn nào trên biểu đồ.

## B.4. Bước 4: Bổ sung thuộc tính kiểu đối tượng

Quan hệ 1-n thì lớp chứa khai báo danh sách; quan hệ n-1 hoặc 1-1 thì khai báo một đối tượng:

- `GameSession` chứa 1 `Player`, 1 `GameConfig` → `player : Player`, `config : GameConfig`.
- `GameSession` chứa n `MathProblem` nhưng chỉ giữ câu đang hiển thị → `currentProblem : MathProblem`. Các câu trước đó không cần giữ lại.
- `GameRecord` chứa 1 `Player` → `player : Player`.
- `Match` chứa 2 `MatchPlayer` → `players : List<MatchPlayer>`.
- `MatchPlayer` chứa 1 `Player` → `player : Player`.

Các thuộc tính dẫn xuất ở pha phân tích không thành thuộc tính lưu trữ mà thành **phương thức nghiệp vụ** của lớp thực thể. Đây là các hàm Java thuần, có unit test:

| Lớp | Phương thức | Ý nghĩa |
|---|---|---|
| `GameSession` | `getCurrentLevel() : int` | `min(difficulty + questionsAsked / 5, 4)` |
| `GameRecord` | `getAccuracy() : double` | `correctCount / (correctCount + wrongCount)` |
| `Match` | `canJoin(playerId : int) : boolean` | Trận đang `waiting`, chưa đủ 2 người, người này chưa có trong trận |
| `Match` | `getResult(playerId : int) : String` | `WIN` / `LOSE` / `DRAW`: ai bỏ trận thì thua; không ai bỏ thì so điểm |

Kỷ lục không là phương thức của lớp nào vì phải truy vấn nhiều `GameRecord`; nó được tính trong DAO (`SELECT MAX(score)`, xem tài liệu CSDL).

## B.5. Biểu đồ lớp thực thể pha thiết kế

![Hình 3: Biểu đồ lớp thực thể pha thiết kế](lop-thuc-the-thiet-ke.png)

*Hình 3: Biểu đồ lớp thực thể pha thiết kế toàn hệ thống Math Dash 1v1.*

So với pha phân tích:
- Các lớp cần lưu có thêm `id`; mọi thuộc tính có kiểu Java.
- Lớp liên kết `MatchPlayer` trở thành thành phần (hình thoi đặc) của `Match` và chứa `Player` (hình thoi rỗng).
- Mỗi quan hệ chứa được thể hiện bằng một thuộc tính đối tượng ở lớp chứa: `GameSession.player`, `GameSession.config`, `GameSession.currentProblem`, `GameRecord.player`, `Match.players`, `MatchPlayer.player`.

Các lớp `Player`, `Match`, `MatchPlayer` có mặt ở **cả app và backend** với cùng tên và cùng thuộc tính, để dữ liệu JSON đi qua REST ánh xạ thẳng vào đối tượng. Riêng `passwordHash` chỉ có giá trị ở backend; app không bao giờ giữ mật khẩu.

Mã nguồn PlantUML: [`lop-thuc-the-thiet-ke.puml`](lop-thuc-the-thiet-ke.puml).

## B.6. Ý nghĩa các thuộc tính

| Lớp | Thuộc tính | Ý nghĩa | Giá trị |
|---|---|---|---|
| `Player` | `username` | Tên đăng nhập, dùng khi đăng nhập | Duy nhất trong hệ thống |
| `Player` | `passwordHash` | Mật khẩu đã băm | Chuỗi BCrypt, không bao giờ là mật khẩu gốc |
| `Player` | `displayName` | Tên hiển thị cho đối thủ và ở màn Chính | |
| `Player` | `createdAt` | Thời điểm đăng ký tài khoản | |
| `MathProblem` | `operand1`, `operand2` | Toán hạng bên trái, bên phải của phép tính | Số nguyên ≥ 1 |
| `MathProblem` | `operator` | Toán tử | `"+"`, `"-"`, `"×"` |
| `MathProblem` | `correctAnswer` | Kết quả đúng của phép tính | ≥ 0 |
| `MathProblem` | `answerOptions` | 4 phương án hiển thị trên 4 nút, đã xáo trộn, có chứa `correctAnswer` | 4 số khác nhau, ≥ 0 |
| `GameSession` | `difficulty` | Độ khó người chơi chọn, cũng là mức bắt đầu | 1 = Dễ, 2 = Trung bình, 3 = Khó; trận online luôn là 1 |
| `GameSession` | `onlineMode` | Ván này là trận online hay không | `true`: không trừ mạng |
| `GameSession` | `questionsAsked` | Số câu đã ra trong ván | ≥ 0 |
| `GameSession` | `score` | Tổng điểm hiện tại | ≥ 0 |
| `GameSession` | `currentStreak` | Số câu đúng liên tiếp tính đến hiện tại | Về 0 khi sai hoặc hết giờ |
| `GameSession`, `GameRecord` | `maxStreak` | Chuỗi đúng liên tiếp dài nhất trong ván | ≥ 0 |
| `GameSession` | `livesLeft` | Số mạng còn lại | 3 → 0 (chế độ offline) |
| `GameSession`, `GameRecord` | `correctCount` | Số câu trả lời đúng trong ván | ≥ 0 |
| `GameSession`, `GameRecord` | `wrongCount` | Số câu trả lời sai trong ván, **tính cả câu hết giờ** | ≥ 0 |
| `GameSession` | `gameOver` | Ván đã kết thúc hay chưa | `true` khi hết mạng (offline) hoặc hết 60 giây (online) |
| `GameRecord` | `difficulty` | Độ khó của ván đã chơi | 1, 2, 3 |
| `GameRecord` | `score` | Điểm cuối cùng của ván | ≥ 0 |
| `GameRecord` | `playedAt` | Thời điểm ván kết thúc | |
| `GameConfig` | `soundEnabled` | Có phát âm thanh khi trả lời và khi hết giờ không | Mặc định `true` |
| `GameConfig` | `vibrationEnabled` | Có rung khi trả lời sai và khi hết giờ không | Mặc định `true` |
| `GameConfig` | `secondsPerQuestion` | Thời gian trả lời mỗi câu ở chế độ offline | 4, 6, 8, 10 giây; mặc định 6 |
| `Match` | `roomCode` | Mã phòng người tạo gửi cho bạn để vào trận | 6 chữ số, duy nhất trong các trận chưa kết thúc |
| `Match` | `status` | Trạng thái trận | `waiting` (chờ người thứ hai), `playing` (đang đấu), `finished` (đã xong) |
| `Match` | `questionSeed` | Hạt giống sinh dãy câu hỏi; 2 máy dùng cùng giá trị nên có cùng dãy câu | Số nguyên `long` ngẫu nhiên |
| `Match` | `createdAt` | Thời điểm tạo phòng | |
| `Match` | `endedAt` | Thời điểm trận kết thúc | Chỉ có khi `status = finished` |
| `MatchPlayer` | `score` | Điểm của người chơi này trong trận | ≥ 0 |
| `MatchPlayer` | `forfeited` | Người chơi này đã bỏ trận (bấm thoát hoặc mất kết nối) và bị xử thua | Mặc định `false` |
