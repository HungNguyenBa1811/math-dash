# THIẾT KẾ GIAO DIỆN VÀ BIỂU ĐỒ LỚP CHI TIẾT — CHƠI ONLINE, XEM LỊCH SỬ ĐẤU

---

## 0. Ánh xạ từ pha phân tích sang pha thiết kế

### 0.1. Lớp View sang Activity

| Lớp phân tích | Lớp thiết kế | Layout |
|---|---|---|
| `MainView` | `MainActivity` | `activity_main.xml` |
| `OnlineView` | `OnlineActivity` | `activity_online.xml` |
| `LobbyView` | `LobbyActivity` | `activity_lobby.xml` |
| `OnlineGameView` | `OnlineGameActivity` | `activity_online_game.xml` |
| `MatchResultView` | Overlay `layoutResult` nằm trong `OnlineGameActivity` | `activity_online_game.xml` |
| `HistoryView` | `HistoryActivity` + `HistoryAdapter` | `activity_history.xml`, `item_history.xml` |

`MatchResultView` là overlay chứ không phải Activity riêng, vì kết quả hiện đè lên màn thi đấu ngay khi trận xong, không cần chuyển dữ liệu qua `Intent`.

### 0.2. Phương thức của thực thể sang Controller hoặc DAO

| Phương thức pha phân tích | Lớp phân tích | Phương thức pha thiết kế | Lớp thiết kế |
|---|---|---|---|
| `createMatch()` | `Match` | `createRoom(me : Player, listener : RoomListener) : void`<br>`createMatch(match : Match, callback : MatchCallback) : void` | `MatchController`<br>`MatchFirebaseDAO` |
| `joinMatch()` | `Match` | `findRoom(roomCode : String, me : Player, listener : RoomListener) : void`<br>`joinMatch(roomCode : String, me : Player, callback : MatchCallback) : void` | `MatchController`<br>`MatchFirebaseDAO` |
| `canJoin()` | `Match` | `canJoin(playerId : int) : boolean` | `Match` |
| `watchMatch()` | `Match` | `waitForOpponent(listener : RoomListener) : void`, `startGame(listener : GameListener) : MathProblem`<br>`watchMatch(roomCode : String, callback : MatchCallback) : void` | `MatchController`<br>`MatchFirebaseDAO` |
| `deleteMatch()` | `Match` | `cancelRoom() : void`<br>`deleteMatch(roomCode : String) : void` | `MatchController`<br>`MatchFirebaseDAO` |
| `generateProblem()` | `MathProblem` | `nextProblem() : MathProblem`<br>`generateProblem(level : int) : MathProblem` | `MatchController`<br>`ProblemDAO` |
| `submitAnswer()` | `GameSession` | `submitAnswer(value : int) : int`<br>`isCorrect(answer : int) : boolean`<br>`recordCorrectAnswer() : int`, `recordWrongAnswer() : void` | `MatchController`<br>`MathProblem`<br>`GameSession` |
| `updateScore()` | `MatchPlayer` | `updateScore(roomCode : String, playerId : int, score : int) : void` | `MatchFirebaseDAO` |
| `forfeit()` | `MatchPlayer` | `leaveMatch() : void`<br>`forfeit(roomCode : String, playerId : int) : void` | `MatchController`<br>`MatchFirebaseDAO` |
| `finishMatch()` | `Match` | `setTimeUp(roomCode : String, playerId : int) : void`<br>`isOver() : boolean`<br>`finishMatch(roomCode : String) : Match` → `finish(roomCode : String) : Match`<br>`readMatch(roomCode : String) : Match`, `updateStatus(roomCode : String, status : String) : void`<br>`insert(match : Match) : boolean`, `findByRoomCode(roomCode : String, createdAt : Date) : Match` | `MatchFirebaseDAO`<br>`Match`<br>`MatchApiDAO` → `MatchRestController`<br>`FirebaseAdminDAO`<br>`MatchDAO` |
| `getResult()` | `Match` | `getResult(playerId : int) : String` | `Match` |
| `getHistory()` | `Match` | `getHistory(playerId : int) : List<Match>` → `getByPlayer(playerId : int) : List<Match>`<br>`findByPlayer(playerId : int) : List<Match>` | `MatchApiDAO` → `MatchRestController`<br>`MatchDAO` |

### 0.3. Lớp DAO cha

Mỗi nơi lưu trữ có một lớp DAO cha giữ kết nối dùng chung; các DAO con kế thừa.

| Nơi lưu trữ | DAO cha | Giữ | DAO con dùng ở đây |
|---|---|---|---|
| App → Firebase | `FirebaseDAO` | `con : DatabaseReference`, `auth : FirebaseAuth` | `MatchFirebaseDAO` |
| App → REST | `ApiDAO` | `BASE_URL`, `token`; gửi / nhận JSON | `MatchApiDAO` |
| Server → MySQL | `DAO` | `conn : Connection` | `MatchDAO` |

Hai lớp không có lớp cha:
- `SessionDAO` chỉ đọc SharedPreferences.
- `FirebaseAdminDAO` dùng Firebase Admin SDK, không dùng JDBC nên không kế thừa `DAO` của server.

---

## 1. Chức năng "Chơi online"

### 1.1. Thiết kế giao diện

![Hình 12: Thiết kế giao diện chức năng Chơi online](giao-dien-uc05.png)

*Hình 12: Thiết kế giao diện chức năng Chơi online.*

Chức năng có 3 màn và 1 overlay, đi theo thứ tự `OnlineActivity` → `LobbyActivity` → `OnlineGameActivity` → overlay `layoutResult`.
- **Người tạo phòng** đi đủ 4 bước.
- **Người tìm phòng** nhập mã ở màn đầu tiên. Màn phòng chờ hiện ngay "Trận đấu bắt đầu!" rồi tự sang màn thi đấu sau 2 giây.
- **Màn thi đấu chia 4 tầng** từ trên xuống:
  1. Hàng điểm: **36** `VS` 24, không hiện tên. Điểm của mình luôn ở bên trái, to hơn và có màu nhấn; tên đối thủ đã hiện ở phòng chờ. Khi trả lời, chữ `+12` (xanh) hoặc `MISS` (đỏ) hiện ngay trên điểm của mình, bay lên khoảng 40dp và mờ dần trong 0,6 giây.
  2. Đồng hồ ở giữa. Hết giờ thì đổi thành "Hết giờ! Đang chờ kết quả...".
  3. Câu hỏi, chữ monospace lớn. Số đang nhập hiện ngay sau dấu `=` thay cho `?`.
  4. Bàn phím số 3x4: `1 2 3 / 4 5 6 / 7 8 9 / ⌫ 0 OK`. Nút cao tối thiểu 64dp. Nút xóa chỉ có biểu tượng ⌫. Số nhập tối đa 3 chữ số (đáp án lớn nhất là 200). Nút OK chỉ bấm được khi đã nhập ít nhất 1 số.
- **Vì sao online dùng bàn phím số thay cho 4 phương án:** trận online là thi đấu tốc độ, nên tự nhập số buộc người chơi phải tính thật, không đoán được trong 4 phương án. Chơi offline (Hoàng) vẫn giữ 4 phương án.

**Bảng thành phần:**

| Màn | `id` trong layout | Loại View | Chức năng | Tên ở pha phân tích |
|---|---|---|---|---|
| `MainActivity` | `txtDisplayName` | `TextView` | "Xin chào, HUNG" | `outDisplayName` |
| | `btnPlayOnline` | `Button` | Mở màn Chơi online | `subPlayOnline` |
| `OnlineActivity` | `btnCreateRoom` | `Button` | Tạo phòng mới | `subCreateRoom` |
| | `edtRoomCode` | `EditText` (`inputType=number`, `maxLength=6`) | Nhập mã phòng | `inRoomCode` |
| | `btnJoinRoom` | `Button` | Vào phòng theo mã | `subJoinRoom` |
| `LobbyActivity` | `txtRoomCode` | `TextView` (36sp, đậm) | Mã phòng | `outRoomCode` |
| | `txtStatus` | `TextView` | "Đang chờ đối thủ..." / "Trận đấu bắt đầu!" | `outStatus` |
| | `txtOpponentName` | `TextView` | "Đối thủ: HOANG" | `outOpponentName` |
| | `btnCancel` | `Button` | Hủy phòng, ẩn khi đã có đối thủ | `subCancel` |
| `OnlineGameActivity` | `txtMyScore` | `TextView` | Điểm của mình, bên trái (cỡ chữ lớn hơn, màu nhấn) | `outMyScore` |
| | `txtTimer` | `TextView` | Đồng hồ đếm ngược `00:42`; hết giờ thì hiện "Hết giờ! Đang chờ kết quả..." | `outTimer` |
| | `txtOpponentScore` | `TextView` | Điểm đối thủ, bên phải | `outOpponentScore` |
| | `txtProblem` | `TextView` | Câu hỏi `12 + 7 = ?` | `outProblem` |
| | `txtScorePopup` | `TextView` (ẩn, chạy hiệu ứng bay lên + mờ dần) | "+12" xanh / "MISS" đỏ trên điểm của mình | `outScorePopup` |
| | `txtAnswer` | `TextView` | Số đang nhập, hiện sau dấu `=` | `inAnswer` |
| | `btnDigit0` … `btnDigit9` (mảng `btnDigits`) | `Button` | Thêm 1 chữ số vào cuối số đang nhập | `subDigits` |
| | `btnBackspace` | `ImageButton` (icon ⌫, `contentDescription="Xóa"`) | Xóa chữ số cuối | `subBackspace` |
| | `btnSubmit` | `Button` | OK: nộp đáp án | `subSubmit` |
| Overlay `layoutResult` | `txtResult` | `TextView` | THẮNG! (xanh) / THUA! (đỏ) / HÒA! (vàng) | `outResult` |
| | `txtFinalScore` | `TextView` | `120 - 96` (mình - đối thủ) | `outScoreLine` |
| | `btnBackToMenu` | `Button` | Về màn chính | `subBackToMenu` |

Các thông báo ("Không tìm thấy phòng", "Phòng đã đầy", "Đối thủ đã thoát") dùng `Toast`. Hộp thoại "Thoát sẽ bị xử thua?" dùng `AlertDialog`. Cả hai đều không phải thành phần của layout.

### 1.2. Biểu đồ lớp chi tiết

![Hình 13: Biểu đồ lớp chi tiết chức năng Chơi online](lop-thiet-ke-uc05.png)

*Hình 13: Biểu đồ lớp chi tiết chức năng Chơi online.*

**Package "Client (Android)".**

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | `MainActivity` | Màn chính (lớp chung); `onClick` của nút Chơi online mở `OnlineActivity` |
| Boundary | `OnlineActivity` | Có `onClick` cho 2 nút:<br>- Tạo phòng gọi `MatchController.createRoom`.<br>- Vào phòng kiểm tra mã đủ 6 chữ số rồi gọi `MatchController.findRoom`.<br>Cài đặt `RoomListener`: vào được phòng thì `onRoomReady` mở `LobbyActivity`; không vào được thì `onError` hiện `Toast` |
| Boundary | `LobbyActivity` | `onCreate` hiện mã phòng và gọi `MatchController.waitForOpponent`. `onRoomReady` hiện tên đối thủ, sau 2 giây mở `OnlineGameActivity`. `onClick` của nút Hủy gọi `cancelRoom` |
| Boundary | `OnlineGameActivity` | Màn thi đấu, cài đặt `GameListener`.<br>- `onCreate` gọi `startGame`.<br>- `onClick` của nút số gọi `appendDigit`, nút xóa gọi `deleteDigit` (chỉ đổi `txtAnswer`, không gọi Controller). Nút OK gọi `submitAnswer` rồi `nextProblem`; `showScorePopup` chạy hiệu ứng "+12" / "MISS".<br>- `onTick` vẽ đồng hồ; `onMatchChanged` vẽ điểm đối thủ; `onMatchEnd` gọi `showResult` để hiện overlay.<br>- `onBackPressed` hỏi xác nhận rồi gọi `leaveMatch`.<br>Activity không tự chấm điểm, không tự phân thắng thua |
| — | `RoomListener`, `GameListener` | 2 interface để `MatchController` báo kết quả bất đồng bộ về Activity |
| Control | `MatchController` | Singleton, điều phối cả trận online, giữ trận (`match`), ván chấm điểm (`session`), người chơi (`me`) và đồng hồ 60 giây (`timer`).<br>- `createRoom` sinh mã phòng bằng `generateRoomCode` và `questionSeed` ngẫu nhiên rồi nhờ `MatchFirebaseDAO` tạo trận.<br>- `startGame` tạo `ProblemDAO(new Random(questionSeed))` và `GameSession` chế độ online, đăng ký `onDisconnect`, bật đồng hồ.<br>- `submitAnswer` chấm bằng `MathProblem.isCorrect` + `GameSession`, đúng thì ghi điểm lên Firebase.<br>- Hết giờ thì ghi `timeUp`. Khi `Match.isOver()` đúng thì `finishMatch` gọi backend trong `Thread` (cờ `finishing` đảm bảo chỉ gọi một lần) |
| Control | `SessionDAO` | `getPlayer()` đọc `playerId`, `displayName` từ SharedPreferences `"session"` và đóng gói thành `Player` (lớp chung) |
| Control | `FirebaseDAO` ← `MatchFirebaseDAO` | `FirebaseDAO` giữ `con` (gốc Realtime Database) và `auth`. `MatchFirebaseDAO` đọc / ghi node `matches/{roomCode}`:<br>- `createMatch` dùng transaction; mã đã có trận chưa `finished` thì báo trùng.<br>- `joinMatch` dùng transaction và gọi `Match.canJoin` bên trong.<br>- `watchMatch` / `stopWatching` thêm / gỡ `ValueEventListener`.<br>- `updateScore`, `setTimeUp`, `forfeit` ghi node của chính mình.<br>- `registerDisconnect` đặt `onDisconnect` ghi `connected = false`, `forfeited = true`.<br>- `toMatch` đóng gói dữ liệu node thành `Match` + 2 `MatchPlayer` |
| — | `MatchCallback` | Interface trả kết quả bất đồng bộ của `MatchFirebaseDAO` (`onSuccess(Match)`, `onFail(String)`) |
| Control | `ApiDAO` ← `MatchApiDAO` | `ApiDAO` giữ `BASE_URL`, `token` và 2 hàm gửi / nhận JSON bằng `HttpURLConnection`. `MatchApiDAO.finishMatch` gửi `POST /api/matches/{roomCode}/finish`, đóng gói JSON trả về thành `Match` bằng `toMatch`. Hàm chạy đồng bộ nên phải gọi trong `Thread` |
| Control | `ProblemDAO` | Sinh câu hỏi theo mức (bảng ở M1-07). Mọi số ngẫu nhiên đi qua `random`, nên 2 máy cùng `questionSeed` sinh ra cùng dãy câu |
| Entity | `Match`, `MatchPlayer`, `Player` | Như thiết kế lớp thực thể. 3 phương thức nghiệp vụ của `Match` là hàm Java thuần, có unit test:<br>- `canJoin`: luật vào phòng.<br>- `isOver`: trận đã xong chưa.<br>- `getResult`: thắng / thua / hòa |
| Entity | `GameSession`, `MathProblem` | Lõi chấm điểm dùng chung với Chơi offline. `onlineMode = true` nên trả lời sai không trừ mạng |

**Package "Server (Spring Boot)".**

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | `MatchRestController` | `finish(roomCode)` nhận `POST /api/matches/{roomCode}/finish`:<br>1. Đọc trận từ Firebase bằng `FirebaseAdminDAO.readMatch`.<br>2. Nếu trận đã `finished` thì trả lại bản đã lưu (`MatchDAO.findByRoomCode`).<br>3. Nếu `Match.isOver()` sai thì trả 409 "Trận chưa kết thúc".<br>4. Ngược lại: ghi `endedAt`, `MatchDAO.insert`, `FirebaseAdminDAO.updateStatus(..., "finished")`, trả `Match` |
| Control | `DAO` ← `MatchDAO` | `DAO` giữ `conn : Connection` (JDBC).<br>- `insert` ghi 1 dòng `tblMatch` + 2 dòng `tblMatchPlayer` trong một transaction, gán `id` vừa sinh vào `match`.<br>- `findByRoomCode` tìm theo khóa duy nhất `(roomCode, createdAt)` |
| Control | `FirebaseAdminDAO` | Dùng Firebase Admin SDK (quyền server, không bị Security Rules chặn).<br>- `readMatch` đọc node `matches/{roomCode}` và đóng gói thành `Match`.<br>- `updateStatus` ghi trạng thái. |
| Entity | `Match`, `MatchPlayer`, `Player` | Cùng tên, cùng thuộc tính với phía app, để Jackson chuyển JSON thẳng vào đối tượng. `Player` ở server có thêm `passwordHash`, không bao giờ trả ra JSON |

**Quan hệ giữa các lớp giao diện:**
- 4 Activity nối nhau bằng quan hệ liên kết. Mỗi Activity nối với layout của nó: `activity_main.xml`, `activity_online.xml`, `activity_lobby.xml`, `activity_online_game.xml`. Overlay kết quả nằm trong `activity_online_game.xml`.
- `OnlineActivity` và `OnlineGameActivity` chứa thông tin người chơi trong thuộc tính `me`, nên `Player` kết tập vào 2 Activity này.

**Mũi tên `HTTP/JSON`** từ `MatchApiDAO` tới `MatchRestController` là đường nối duy nhất giữa 2 package. Mọi lời gọi giữa app và server đều đi qua cặp lớp này.

---

## 2. Chức năng "Xem lịch sử đấu"

### 2.1. Thiết kế giao diện

![Hình 14: Thiết kế giao diện chức năng Xem lịch sử đấu](giao-dien-uc06.png)

*Hình 14: Thiết kế giao diện chức năng Xem lịch sử đấu.*

Màn Lịch sử đấu là một danh sách cuộn, mỗi dòng là một `item_history.xml`. Khi danh sách rỗng, `RecyclerView` ẩn đi và `txtEmpty` hiện "Chưa có trận nào".

**Bảng thành phần:**

| Màn | `id` trong layout | Loại View | Chức năng | Tên ở pha phân tích |
|---|---|---|---|---|
| `MainActivity` | `txtDisplayName` | `TextView` | "Xin chào, HUNG" | `outDisplayName` |
| | `btnHistory` | `Button` | Mở màn Lịch sử đấu | `subHistory` |
| `HistoryActivity` | `rvHistory` | `RecyclerView` | Danh sách trận, mới nhất trước | `outListMatch` |
| | `txtEmpty` | `TextView` | "Chưa có trận nào" khi danh sách rỗng | `outListMatch` (trạng thái rỗng) |
| `item_history.xml` | `txtPlayedAt` | `TextView` | Thời gian kết thúc `23/10/2026 20:16` | một dòng của `outListMatch` |
| | `txtOpponent` | `TextView` | `vs HOANG` | một dòng của `outListMatch` |
| | `txtScore` | `TextView` | Tỉ số `120 - 96` (mình - đối thủ) | một dòng của `outListMatch` |
| | `txtResult` | `TextView` | THẮNG (`arcade_green`) / THUA (`arcade_red`) / HÒA (vàng) | một dòng của `outListMatch` |

### 2.2. Biểu đồ lớp chi tiết

![Hình 15: Biểu đồ lớp chi tiết chức năng Xem lịch sử đấu](lop-thiet-ke-uc06.png)

*Hình 15: Biểu đồ lớp chi tiết chức năng Xem lịch sử đấu.*

**Package "Client (Android)".**

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | `MainActivity` | `onClick` của nút Lịch sử đấu mở `HistoryActivity` |
| Boundary | `HistoryActivity` | `onCreate` lấy người chơi bằng `SessionDAO.getPlayer` rồi gọi `loadHistory(playerId)`. `loadHistory` gọi `MatchApiDAO.getHistory` trong `Thread`, rồi trong `runOnUiThread` gắn `HistoryAdapter`. Danh sách rỗng thì hiện `txtEmpty`; lỗi mạng thì hiện `Toast` |
| Boundary | `HistoryAdapter`, `HistoryViewHolder` | `HistoryAdapter` kế thừa `RecyclerView.Adapter`, giữ danh sách `Match` và `playerId` của người xem. `onBindViewHolder` tìm `MatchPlayer` của mình và của đối thủ trong `match.getPlayers()`, rồi gọi `match.getResult(playerId)` để chọn chữ và màu. `HistoryViewHolder` giữ 4 `TextView` của một dòng |
| Control | `SessionDAO` | Như ở UC05 |
| Control | `ApiDAO` ← `MatchApiDAO` | `getHistory(playerId)` gửi `GET /api/matches?playerId={id}` qua `ApiDAO.getArray`, đóng gói từng phần tử JSON thành `Match` bằng `toMatch` |
| Entity | `Match`, `MatchPlayer`, `Player` | `getResult` là phương thức duy nhất được gọi; dữ liệu đến từ MySQL nên kết quả trùng với lúc trận kết thúc |

**Package "Server (Spring Boot)".**

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | `MatchRestController` | `getByPlayer(playerId)` nhận `GET /api/matches?playerId={id}`, gọi `MatchDAO.findByPlayer` và trả danh sách `Match` dạng JSON |
| Control | `DAO` ← `MatchDAO` | `findByPlayer` chạy truy vấn `tblMatch` JOIN 2 lần `tblMatchPlayer` (mình và đối thủ) JOIN `tblPlayer` (tên đối thủ), sắp `endedAt DESC`, rồi đóng gói mỗi dòng thành 1 `Match` + 2 `MatchPlayer` + 2 `Player` |
| Entity | `Match`, `MatchPlayer`, `Player` | Như ở UC05 |

**Quan hệ giữa các lớp giao diện:**
- `MainActivity` – `HistoryActivity` – `HistoryAdapter` – `HistoryViewHolder` nối nhau bằng quan hệ liên kết.
- Layout: `MainActivity` – `activity_main.xml`, `HistoryActivity` – `activity_history.xml`, `HistoryViewHolder` – `item_history.xml`.
- `HistoryAdapter` chứa danh sách trận trong thuộc tính `listMatch`, nên `Match` kết tập vào `HistoryAdapter`, bội số n ở phía `Match`.

UC06 dùng lại cùng các lớp `MatchApiDAO`, `MatchRestController`, `MatchDAO` với UC05. Biểu đồ chỉ vẽ các phương thức chức năng này dùng.
