# THIẾT KẾ GIAO DIỆN VÀ BIỂU ĐỒ LỚP CHI TIẾT — CHƠI ONLINE, XEM LỊCH SỬ ĐẤU

---

## 0. Ánh xạ từ pha phân tích sang pha thiết kế

### 0.1. Lớp View sang Activity

| Lớp phân tích | Lớp thiết kế | Layout |
|---|---|---|
| MainView | MainActivity | activity_main.xml |
| OnlineView | OnlineActivity | activity_online.xml |
| LobbyView | LobbyActivity | activity_lobby.xml |
| OnlineGameView | OnlineGameActivity | activity_online_game.xml |
| MatchResultView | Overlay layoutResult nằm trong OnlineGameActivity | activity_online_game.xml |
| HistoryView | HistoryActivity + HistoryAdapter | activity_history.xml, item_history.xml |

MatchResultView là overlay chứ không phải Activity riêng, vì kết quả hiện đè lên màn thi đấu ngay khi trận xong, không cần chuyển dữ liệu qua Intent.

### 0.2. Phương thức của thực thể sang Activity hoặc DAO

| Phương thức pha phân tích | Lớp phân tích | Phương thức pha thiết kế | Lớp thiết kế |
|---|---|---|---|
| createMatch() | Match | createMatch(me : Player, callback : MatchCallback) : void<br>newMatch(me : Player) : Match | MatchFirebaseDAO |
| joinMatch() | Match | joinMatch(roomCode : String, me : Player, callback : MatchCallback) : void | MatchFirebaseDAO |
| canJoin() | Match | canJoin(playerId : int) : boolean | Match |
| watchMatch() | Match | watchMatch(roomCode : String, callback : MatchCallback) : void<br>onSuccess(match : Match) : void | MatchFirebaseDAO<br>LobbyActivity, OnlineGameActivity |
| deleteMatch() | Match | deleteMatch(roomCode : String) : void | MatchFirebaseDAO |
| generateProblem() | MathProblem | nextProblem() : MathProblem<br>generateProblem(level : int) : MathProblem | OnlineGameActivity<br>ProblemDAO |
| submitAnswer() | GameSession | submitAnswer(value : int) : void<br>isCorrect(answer : int) : boolean<br>recordCorrectAnswer() : int, recordWrongAnswer() : void | OnlineGameActivity<br>MathProblem<br>GameSession |
| updateScore() | MatchPlayer | updateScore(roomCode : String, playerId : int, score : int) : void | MatchFirebaseDAO |
| forfeit() | MatchPlayer | leaveMatch() : void<br>forfeit(roomCode : String, playerId : int) : void | OnlineGameActivity<br>MatchFirebaseDAO |
| finishMatch() | Match | setTimeUp(roomCode : String, playerId : int) : void<br>isOver() : boolean<br>finishMatch() : void<br>finishMatch(roomCode : String) : Match → finish(roomCode : String) : Match<br>readMatch(roomCode : String) : Match, updateStatus(roomCode : String, status : String) : void<br>insert(match : Match) : boolean, findByRoomCode(roomCode : String, createdAt : Date) : Match | MatchFirebaseDAO<br>Match<br>OnlineGameActivity<br>MatchApiDAO → MatchRestController<br>FirebaseAdminDAO<br>MatchDAO |
| getResult() | Match | getResult(playerId : int) : String | Match |
| getHistory() | Match | getHistory(playerId : int) : List\<Match> → getByPlayer(playerId : int) : List\<Match><br>findByPlayer(playerId : int) : List\<Match> | MatchApiDAO → MatchRestController<br>MatchDAO |

### 0.3. Lớp DAO cha

Mỗi nơi lưu trữ có một lớp DAO cha giữ kết nối dùng chung; các DAO con kế thừa.

| Nơi lưu trữ | DAO cha | Giữ | DAO con dùng ở đây |
|---|---|---|---|
| App → Firebase | FirebaseDAO | con : DatabaseReference | MatchFirebaseDAO |
| App → REST | ApiDAO | BASE_URL; gửi / nhận JSON | MatchApiDAO |
| Server → MySQL | DAO | conn : Connection | MatchDAO |

Hai lớp không có lớp cha:
- SessionDAO chỉ đọc SharedPreferences.
- FirebaseAdminDAO gọi REST API của Firebase Realtime Database, không dùng JDBC nên không kế thừa DAO của server.

---

## 1. Chức năng "Chơi online"

### 1.1. Thiết kế giao diện

![Hình 12: Thiết kế giao diện chức năng Chơi online](giao-dien-uc05.png)

*Hình 12: Thiết kế giao diện chức năng Chơi online.*

Chức năng có 3 màn và 1 overlay, đi theo thứ tự OnlineActivity → LobbyActivity → OnlineGameActivity → overlay layoutResult.
- **Người tạo phòng** đi đủ 4 bước.
- **Người tìm phòng** nhập mã ở màn đầu tiên. Màn phòng chờ hiện ngay "Trận đấu bắt đầu!" rồi tự sang màn thi đấu sau 2 giây.
- **Màn thi đấu chia 4 tầng** từ trên xuống:
  1. Hàng điểm: **36** VS 24, không hiện tên. Điểm của mình luôn ở bên trái, to hơn và có màu nhấn; tên đối thủ đã hiện ở phòng chờ. Khi trả lời, chữ +12 (xanh) hoặc MISS (đỏ) hiện ngay trên điểm của mình, bay lên khoảng 40dp và mờ dần trong 0,6 giây.
  2. Đồng hồ ở giữa. Hết giờ thì đổi thành "Hết giờ! Đang chờ kết quả...".
  3. Câu hỏi, chữ monospace lớn. Số đang nhập hiện ngay sau dấu = thay cho ?.
  4. Bàn phím số 3x4: 1 2 3 / 4 5 6 / 7 8 9 / ⌫ 0 OK. Nút cao tối thiểu 64dp. Nút xóa chỉ có biểu tượng ⌫. Số nhập tối đa 3 chữ số (đáp án lớn nhất là 200). Nút OK chỉ bấm được khi đã nhập ít nhất 1 số.
- **Vì sao online dùng bàn phím số thay cho 4 phương án:** trận online là thi đấu tốc độ, nên tự nhập số buộc người chơi phải tính thật, không đoán được trong 4 phương án. Chơi offline (Hoàng) vẫn giữ 4 phương án.

**Bảng thành phần:**

| Màn | id trong layout | Loại View | Chức năng | Tên ở pha phân tích |
|---|---|---|---|---|
| MainActivity | txtDisplayName | TextView | "Xin chào, HUNG" | outDisplayName |
| | btnPlayOnline | Button | Mở màn Chơi online | subPlayOnline |
| OnlineActivity | btnCreateRoom | Button | Tạo phòng mới | subCreateRoom |
| | edtRoomCode | EditText (inputType=number, maxLength=6) | Nhập mã phòng | inRoomCode |
| | btnJoinRoom | Button | Vào phòng theo mã | subJoinRoom |
| LobbyActivity | txtRoomCode | TextView (36sp, đậm) | Mã phòng | outRoomCode |
| | txtStatus | TextView | "Đang chờ đối thủ..." / "Trận đấu bắt đầu!" | outStatus |
| | txtOpponentName | TextView | "Đối thủ: HOANG" | outOpponentName |
| | btnCancel | Button | Hủy phòng, ẩn khi đã có đối thủ | subCancel |
| OnlineGameActivity | txtMyScore | TextView | Điểm của mình, bên trái (cỡ chữ lớn hơn, màu nhấn) | outMyScore |
| | txtTimer | TextView | Đồng hồ đếm ngược 00:42; hết giờ thì hiện "Hết giờ! Đang chờ kết quả..." | outTimer |
| | txtOpponentScore | TextView | Điểm đối thủ, bên phải | outOpponentScore |
| | txtProblem | TextView | Câu hỏi 12 + 7 = ? | outProblem |
| | txtScorePopup | TextView (ẩn, chạy hiệu ứng bay lên + mờ dần) | "+12" xanh / "MISS" đỏ trên điểm của mình | outScorePopup |
| | txtAnswer | TextView | Số đang nhập, hiện sau dấu = | inAnswer |
| | btnDigit0 … btnDigit9 (mảng btnDigits) | Button | Thêm 1 chữ số vào cuối số đang nhập | subDigits |
| | btnBackspace | ImageButton (icon ⌫, contentDescription="Xóa") | Xóa chữ số cuối | subBackspace |
| | btnSubmit | Button | OK: nộp đáp án | subSubmit |
| Overlay layoutResult | txtResult | TextView | THẮNG! (xanh) / THUA! (đỏ) / HÒA! (vàng) | outResult |
| | txtFinalScore | TextView | 120 - 96 (mình - đối thủ) | outScoreLine |
| | btnBackToMenu | Button | Về màn chính | subBackToMenu |

Các thông báo ("Không tìm thấy phòng", "Phòng đã đầy", "Đối thủ đã thoát") dùng Toast. Hộp thoại "Thoát sẽ bị xử thua?" dùng AlertDialog. Cả hai đều không phải thành phần của layout.

### 1.2. Biểu đồ lớp chi tiết

![Hình 13: Biểu đồ lớp chi tiết chức năng Chơi online](lop-thiet-ke-uc05.png)

*Hình 13: Biểu đồ lớp chi tiết chức năng Chơi online.*

**Package "Client (Android)".**

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | MainActivity | Màn chính (lớp chung); onClick của nút Chơi online mở OnlineActivity |
| Boundary | OnlineActivity | Có onClick cho 2 nút:<br>- Tạo phòng gọi MatchFirebaseDAO.createMatch.<br>- Vào phòng kiểm tra mã đủ 6 chữ số rồi gọi MatchFirebaseDAO.joinMatch.<br>Cài đặt MatchCallback: vào được phòng thì onSuccess mở LobbyActivity (kèm roomCode, playerId); không vào được thì onFail hiện Toast |
| Boundary | LobbyActivity | Cài đặt MatchCallback. onCreate hiện mã phòng và gọi MatchFirebaseDAO.watchMatch. onSuccess khi trận đã sang đang chơi thì gọi stopWatching, showOpponent hiện tên đối thủ, sau 2 giây mở OnlineGameActivity. onClick của nút Hủy (và onBackPressed khi đang chờ) gọi stopWatching rồi deleteMatch |
| Boundary | OnlineGameActivity | Màn thi đấu, cài đặt MatchCallback; giữ ván chấm điểm (session), người chơi (me), mã phòng và đồng hồ 60 giây (timer).<br>- onCreate gọi MatchFirebaseDAO.watchMatch. Lần đầu nhận trận, onSuccess gọi startGame: registerDisconnect, tạo ProblemDAO(new Random(questionSeed)) và GameSession chế độ online, bật đồng hồ, nextProblem.<br>- onClick của nút số gọi appendDigit, nút xóa gọi deleteDigit (chỉ đổi txtAnswer). Nút OK gọi submitAnswer: chấm bằng MathProblem.isCorrect + GameSession, đúng thì MatchFirebaseDAO.updateScore; rồi nextProblem; showScorePopup chạy hiệu ứng "+12" / "MISS".<br>- Đồng hồ: onTick vẽ thời gian; onFinish gọi setTimeUp rồi lockKeypad.<br>- onSuccess: Match.isOver() đúng thì finishMatch gọi MatchApiDAO.finishMatch trong Thread (cờ finishing đảm bảo chỉ gọi một lần), rồi showResult trên bản trận server trả về; chưa xong thì showOpponentScore.<br>- onBackPressed hỏi xác nhận rồi gọi leaveMatch (MatchFirebaseDAO.forfeit).<br>Activity không tự phân thắng thua: kết quả là Match.getResult trên bản trận server trả về |
| Control | SessionDAO | getPlayer() đọc playerId, displayName từ SharedPreferences "session" và đóng gói thành Player (lớp chung) |
| Control | FirebaseDAO ← MatchFirebaseDAO | FirebaseDAO giữ con (gốc Realtime Database). MatchFirebaseDAO đọc / ghi node matches/{roomCode}:<br>- createMatch tạo trận mới bằng newMatch (mã phòng 6 chữ số, questionSeed ngẫu nhiên, trạng thái chờ) rồi ghi bằng transaction; mã đã có trận chưa finished thì báo trùng.<br>- joinMatch dùng transaction và gọi Match.canJoin bên trong.<br>- watchMatch / stopWatching thêm / gỡ ValueEventListener.<br>- updateScore, setTimeUp, forfeit ghi node của chính mình.<br>- registerDisconnect đặt onDisconnect ghi connected = false, forfeited = true.<br>- toMatch đóng gói dữ liệu node thành Match + 2 MatchPlayer |
| — | MatchCallback | Interface trả kết quả bất đồng bộ của MatchFirebaseDAO (onSuccess(Match), onFail(String)); OnlineActivity, LobbyActivity, OnlineGameActivity cài đặt |
| Control | ApiDAO ← MatchApiDAO | ApiDAO giữ BASE_URL và 2 hàm gửi / nhận JSON bằng HttpURLConnection. MatchApiDAO.finishMatch gửi POST /api/matches/{roomCode}/finish, đóng gói JSON trả về thành Match bằng toMatch. Lần ghi hết giờ của máy này có thể chưa tới Firebase khi server đọc, nên lỗi thì gửi lại, tối đa FINISH_ATTEMPTS = 3 lần. Hàm chạy đồng bộ nên phải gọi trong Thread |
| Control | ProblemDAO | Sinh câu hỏi theo mức (bảng ở M1-07). Mọi số ngẫu nhiên đi qua random, nên 2 máy cùng questionSeed sinh ra cùng dãy câu |
| Entity | Match, MatchPlayer, Player | Như thiết kế lớp thực thể. 3 phương thức nghiệp vụ của Match là hàm Java thuần, có unit test:<br>- canJoin: luật vào phòng.<br>- isOver: trận đã xong chưa.<br>- getResult: thắng / thua / hòa |
| Entity | GameSession, MathProblem | Lõi chấm điểm dùng chung với Chơi offline. onlineMode = true nên trả lời sai không trừ mạng |

**Package "Server (Spring Boot)".**

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | MatchRestController | finish(roomCode) nhận POST /api/matches/{roomCode}/finish:<br>1. Đọc trận từ Firebase bằng FirebaseAdminDAO.readMatch.<br>2. Nếu trận đã finished thì trả lại bản đã lưu (MatchDAO.findByRoomCode).<br>3. Nếu Match.isOver() sai thì trả 409 "Trận chưa kết thúc".<br>4. Ngược lại: ghi endedAt, MatchDAO.insert, FirebaseAdminDAO.updateStatus(..., "finished"), trả Match |
| Control | DAO ← MatchDAO | DAO giữ conn : Connection (JDBC).<br>- insert ghi 1 dòng tblMatch + 2 dòng tblMatchPlayer trong một transaction, gán id vừa sinh vào match.<br>- findByRoomCode tìm theo khóa duy nhất (roomCode, createdAt) |
| Control | FirebaseAdminDAO | Giữ dbUrl (địa chỉ Realtime Database), gọi REST API của Firebase.<br>- readMatch gửi GET matches/{roomCode}.json và đóng gói thành Match.<br>- updateStatus gửi PUT matches/{roomCode}/status.json. |
| Entity | Match, MatchPlayer, Player | Cùng tên, cùng thuộc tính với phía app, để Jackson chuyển JSON thẳng vào đối tượng. Player ở server có thêm passwordHash, không bao giờ trả ra JSON |

**Quan hệ giữa các lớp giao diện:**
- 4 Activity nối nhau bằng quan hệ liên kết. Mỗi Activity nối với layout của nó: activity_main.xml, activity_online.xml, activity_lobby.xml, activity_online_game.xml. Overlay kết quả nằm trong activity_online_game.xml.
- OnlineActivity và OnlineGameActivity chứa thông tin người chơi trong thuộc tính me, nên Player kết tập vào 2 Activity này.

**Quan hệ liên kết HTTP/JSON** giữa MatchApiDAO và MatchRestController là đường nối duy nhất giữa 2 package. Mọi lời gọi giữa app và server đều đi qua cặp lớp này.

---

## 2. Chức năng "Xem lịch sử đấu"

### 2.1. Thiết kế giao diện

![Hình 14: Thiết kế giao diện chức năng Xem lịch sử đấu](giao-dien-uc06.png)

*Hình 14: Thiết kế giao diện chức năng Xem lịch sử đấu.*

Màn Lịch sử đấu là một danh sách cuộn, mỗi dòng là một item_history.xml. Khi danh sách rỗng, RecyclerView ẩn đi và txtEmpty hiện "Chưa có trận nào".

**Bảng thành phần:**

| Màn | id trong layout | Loại View | Chức năng | Tên ở pha phân tích |
|---|---|---|---|---|
| MainActivity | txtDisplayName | TextView | "Xin chào, HUNG" | outDisplayName |
| | btnHistory | Button | Mở màn Lịch sử đấu | subHistory |
| HistoryActivity | rvHistory | RecyclerView | Danh sách trận, mới nhất trước | outListMatch |
| | txtEmpty | TextView | "Chưa có trận nào" khi danh sách rỗng | outListMatch (trạng thái rỗng) |
| item_history.xml | txtPlayedAt | TextView | Thời gian kết thúc 23/10/2026 20:16 | một dòng của outListMatch |
| | txtOpponent | TextView | vs HOANG | một dòng của outListMatch |
| | txtScore | TextView | Tỉ số 120 - 96 (mình - đối thủ) | một dòng của outListMatch |
| | txtResult | TextView | THẮNG (arcade_green) / THUA (arcade_red) / HÒA (vàng) | một dòng của outListMatch |

### 2.2. Biểu đồ lớp chi tiết

![Hình 15: Biểu đồ lớp chi tiết chức năng Xem lịch sử đấu](lop-thiet-ke-uc06.png)

*Hình 15: Biểu đồ lớp chi tiết chức năng Xem lịch sử đấu.*

**Package "Client (Android)".**

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | MainActivity | onClick của nút Lịch sử đấu mở HistoryActivity |
| Boundary | HistoryActivity | onCreate lấy người chơi bằng SessionDAO.getPlayer rồi gọi loadHistory(playerId). loadHistory gọi MatchApiDAO.getHistory trong Thread, rồi trong runOnUiThread gắn HistoryAdapter. Danh sách rỗng thì hiện txtEmpty; lỗi mạng thì hiện Toast |
| Boundary | HistoryAdapter, HistoryViewHolder | HistoryAdapter kế thừa RecyclerView.Adapter, giữ danh sách Match và playerId của người xem. onBindViewHolder tìm MatchPlayer của mình và của đối thủ trong match.getPlayers(), rồi gọi match.getResult(playerId) để chọn chữ và màu. HistoryViewHolder giữ 4 TextView của một dòng |
| Control | SessionDAO | Như ở UC05 |
| Control | ApiDAO ← MatchApiDAO | getHistory(playerId) gửi GET /api/matches?playerId={id} qua ApiDAO.getArray, đóng gói từng phần tử JSON thành Match bằng toMatch |
| Entity | Match, MatchPlayer, Player | getResult là phương thức duy nhất được gọi; dữ liệu đến từ MySQL nên kết quả trùng với lúc trận kết thúc |

**Package "Server (Spring Boot)".**

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | MatchRestController | getByPlayer(playerId) nhận GET /api/matches?playerId={id}, gọi MatchDAO.findByPlayer và trả danh sách Match dạng JSON |
| Control | DAO ← MatchDAO | findByPlayer chạy truy vấn tblMatch JOIN 2 lần tblMatchPlayer (mình và đối thủ) JOIN tblPlayer (tên đối thủ), sắp endedAt DESC, rồi đóng gói mỗi dòng thành 1 Match + 2 MatchPlayer + 2 Player |
| Entity | Match, MatchPlayer, Player | Như ở UC05 |

**Quan hệ giữa các lớp giao diện:**
- MainActivity – HistoryActivity – HistoryAdapter – HistoryViewHolder nối nhau bằng quan hệ liên kết.
- Layout: MainActivity – activity_main.xml, HistoryActivity – activity_history.xml, HistoryViewHolder – item_history.xml.
- HistoryAdapter chứa danh sách trận trong thuộc tính listMatch, nên Match kết tập vào HistoryAdapter, bội số n ở phía Match.

UC06 dùng lại cùng các lớp MatchApiDAO, MatchRestController, MatchDAO với UC05. Biểu đồ chỉ vẽ các phương thức chức năng này dùng.
