# THIẾT KẾ GIAO DIỆN VÀ BIỂU ĐỒ LỚP CHI TIẾT — CHƠI ONLINE, XEM LỊCH SỬ ĐẤU, QUẢN LÝ BẠN BÈ

---

## 0. Ánh xạ từ pha phân tích sang pha thiết kế

### 0.1. Lớp View sang Activity

| Lớp phân tích | Lớp thiết kế | Layout |
|---|---|---|
| MainView | MainActivity + PlayerAdapter | activity_main.xml, item_player.xml |
| MatchView | MatchActivity + QuestionAdapter | activity_match.xml, item_question.xml |
| MatchResultView | Overlay layoutResult nằm trong MatchActivity | activity_match.xml |
| HistoryView | HistoryActivity + PieChartView + MatchHistoryAdapter + RankingAdapter | activity_history.xml, item_match_history.xml, item_ranking.xml |
| MatchDetailView | MatchDetailActivity + ProblemAdapter | activity_match_detail.xml, item_problem.xml |
| FriendView | FriendActivity + FriendRequestAdapter + FriendAdapter | activity_friend.xml, item_friend_request.xml, item_friend.xml |

MatchResultView là overlay chứ không phải Activity riêng:
- Kết quả hiện đè lên màn thi đấu ngay khi trận xong.
- Câu hỏi "Chơi tiếp?" cần tiếp tục theo dõi trận đang mở.

HistoryView là một Activity có 2 thẻ (TabLayout):
- Thẻ Trận của tôi gồm PieChartView và danh sách rvMatches; thẻ Bảng xếp hạng là danh sách rvRanking. Chọn thẻ chỉ ẩn / hiện 2 khối này, không mở Activity mới.
- PieChartView là View tự vẽ biểu đồ tròn bằng Canvas, không dùng thư viện ngoài.

### 0.2. Phương thức của thực thể sang Activity hoặc DAO

| Phương thức pha phân tích | Lớp phân tích | Phương thức pha thiết kế | Lớp thiết kế |
|---|---|---|---|
| goOnline() | Player | goOnline(player : Player) : void | PresenceFirebaseDAO |
| getOnlinePlayers() | Player | watchOnlinePlayers(callback : PlayerListCallback) : void<br>onPlayersChanged(players : List\<Player>) : void | PresenceFirebaseDAO<br>MainActivity |
| sendChallenge() | Challenge | sendChallenge(challenger : Player, opponent : Player) : Challenge | ChallengeFirebaseDAO |
| watchChallenge() | Challenge | watchIncoming(playerId : int, callback : ChallengeCallback) : void<br>watchChallenge(challengeId : String, callback : ChallengeCallback) : void<br>onChallengeChanged(challenge : Challenge) : void | ChallengeFirebaseDAO<br>ChallengeFirebaseDAO<br>MainActivity |
| rejectChallenge() | Challenge | rejectChallenge(challengeId : String) : void | ChallengeFirebaseDAO |
| createMatch() | Match | createMatch(challenge : Challenge) : Match → create(challenge : Challenge) : Match<br>readPresence(playerId : int) : Player, updateStatus(playerId : int, status : String) : void, updateChallenge(challengeId : String, status : String, matchId : int) : void, writeMatch(match : Match) : void<br>insert(match : Match) : boolean | MatchApiDAO → MatchRestController<br>FirebaseAdminDAO<br><br>MatchDAO |
| generateProblem() | MathProblem | generateProblems(count : int) : List\<MathProblem><br>generateProblem() : MathProblem | ProblemDAO (server) |
| watchMatch() | Match | watchMatch(matchId : int, callback : MatchCallback) : void<br>onSuccess(match : Match) : void, onNextMatch(nextMatchId : int) : void, onStopped(playerId : int) : void | MatchFirebaseDAO<br>MatchActivity |
| submitAnswers() | Match | submitAnswers() : void<br>submit(matchId : int, playerId : int, answers : List\<Integer>) : Match → submit(id : int, matchPlayer : MatchPlayer) : Match<br>findById(id : int) : Match, updateSubmission(matchId : int, matchPlayer : MatchPlayer) : boolean, updateEndedAt(matchId : int, endedAt : Date) : boolean<br>writeSubmitted(matchId : int, playerId : int) : void, writeFinished(matchId : int) : void, updatePoints(playerId : int, totalPoints : double) : void | MatchActivity<br>MatchApiDAO → MatchRestController<br>MatchDAO<br><br>FirebaseAdminDAO |
| isCorrect() | MathProblem | countCorrect(answers : List\<Integer>) : int<br>isCorrect(answer : int) : boolean | Match<br>MathProblem |
| isOver(), getResult(), getFinishTime(), getPoints() | Match | Giữ nguyên, thêm tham số playerId : int cho 3 hàm sau | Match |
| forfeit() | Match | leaveMatch() : void<br>forfeit(matchId : int, playerId : int) : Match → forfeit(id : int, playerId : int) : Match<br>updateForfeit(matchId : int, playerId : int) : boolean, writeStopped(matchId : int, playerId : int) : void | MatchActivity<br>MatchApiDAO → MatchRestController<br>MatchDAO, FirebaseAdminDAO |
| continueMatch() | Match | sendChoice(choice : boolean) : void<br>continueMatch(matchId : int, playerId : int, choice : boolean) : Match → continueMatch(id : int, playerId : int, choice : boolean) : Match<br>writeChoice(matchId : int, playerId : int, choice : boolean) : void, writeNextMatch(matchId : int, nextMatchId : int) : void, writeStopped(matchId : int, playerId : int) : void | MatchActivity<br>MatchApiDAO → MatchRestController<br>FirebaseAdminDAO |
| getHistory() | Match | getHistory(playerId : int) : List\<Match> → getHistory(playerId : int) : List\<Match><br>findByPlayer(playerId : int) : List\<Match> | MatchApiDAO → MatchRestController<br>MatchDAO |
| getMatch() | Match | getById(matchId : int) : Match → getById(id : int) : Match<br>findById(id : int) : Match, getTotalPoints(playerId : int) : double | MatchApiDAO → MatchRestController<br>MatchDAO, PlayerDAO |
| getOpponent() | Match | getOpponent(playerId : int) : MatchPlayer | Match |
| getResult(), getPoints(), getFinishTime() | Match | Giữ nguyên như ở Chơi online | Match |
| getRanking() | Player | getRanking() : List\<Player> → getRanking() : List\<Player><br>getRanking() : List\<Player>, rank(players : List\<Player>, matches : List\<Match>) : List\<Player>, findFinished() : List\<Match> | PlayerApiDAO → PlayerRestController<br>PlayerDAO, MatchDAO |
| Đếm thắng / hòa / thua của HistoryView | HistoryView | showSummary(matches : List\<Match>) : void<br>setData(wins : int, draws : int, losses : int) : void | HistoryActivity<br>PieChartView |
| getFriendships() | Friendship | loadFriendships() : void, showFriendships(list : List\<Friendship>) : void<br>getFriendships(playerId : int) : List\<Friendship> → getFriendships(playerId : int) : List\<Friendship><br>findByPlayer(playerId : int) : List\<Friendship>, getTotalPoints(playerId : int) : double | FriendActivity<br>FriendshipApiDAO → FriendshipRestController<br>FriendshipDAO, PlayerDAO |
| getOnlinePlayers() | Player | watchOnlinePlayers(callback : PlayerListCallback) : void<br>onPlayersChanged(players : List\<Player>) : void, setOnlineIds(onlineIds : Set\<Integer>) : void | PresenceFirebaseDAO<br>FriendActivity, FriendAdapter |
| sendRequest() | Friendship | onClick(v : View) : void<br>sendRequest(senderId : int, receiverUsername : String) : Friendship → sendRequest(body : Map\<String, String>) : Friendship<br>findById(id : int) : Player, findByUsername(username : String) : Player<br>existsBetween(playerId1 : int, playerId2 : int) : boolean, insert(friendship : Friendship) : boolean | FriendActivity<br>FriendshipApiDAO → FriendshipRestController<br>PlayerDAO<br>FriendshipDAO |
| accept() | Friendship | onAccept(friendship : Friendship) : void<br>accept(friendshipId : int, playerId : int) : Friendship → accept(id : int, playerId : int) : Friendship<br>findById(id : int) : Friendship, updateStatus(id : int, status : String) : boolean | FriendActivity<br>FriendshipApiDAO → FriendshipRestController<br>FriendshipDAO |
| delete() | Friendship | onReject(friendship : Friendship) : void, onUnfriend(friendship : Friendship) : void<br>delete(friendshipId : int, playerId : int) : void → delete(id : int, playerId : int) : void<br>delete(path : String) : void<br>findById(id : int) : Friendship, delete(id : int) : boolean | FriendActivity<br>FriendshipApiDAO → FriendshipRestController<br>ApiDAO<br>FriendshipDAO |
| canAccept(), getFriend() | Friendship | Giữ nguyên, thêm tham số playerId : int | Friendship |

### 0.3. Lớp DAO cha

Mỗi nơi lưu trữ có một lớp DAO cha giữ kết nối dùng chung; các DAO con kế thừa.

| Nơi lưu trữ | DAO cha | Giữ | DAO con dùng ở đây |
|---|---|---|---|
| App → Firebase | FirebaseDAO | con : DatabaseReference | PresenceFirebaseDAO, ChallengeFirebaseDAO, MatchFirebaseDAO |
| App → REST | ApiDAO | BASE_URL; gửi / nhận JSON | PlayerApiDAO, MatchApiDAO, FriendshipApiDAO |
| Server → MySQL | DAO | conn : Connection | PlayerDAO, MatchDAO, FriendshipDAO |

ApiDAO có 3 hàm gửi yêu cầu: get, post và delete; delete dùng để từ chối lời mời và hủy kết bạn.

Các lớp không có lớp cha:
- SessionDAO chỉ đọc SharedPreferences.
- FirebaseAdminDAO gọi REST API của Firebase Realtime Database, không dùng JDBC nên không kế thừa DAO của server.
- ProblemDAO ở server chỉ sinh câu hỏi bằng Random, không kết nối nơi lưu trữ nào.

---

## 1. Chức năng "Chơi online"

### 1.1. Thiết kế giao diện

![Hình 19: Thiết kế giao diện chức năng Chơi online](giao-dien-uc03.png)

*Hình 19: Thiết kế giao diện chức năng Chơi online.*

Chức năng có 2 màn, 1 hộp thoại lời mời và 1 overlay, đi theo thứ tự MainActivity → hộp thoại lời mời trên máy người được mời → MatchActivity → overlay layoutResult.
- **Màn chính** chia 2 phần:
  - Trên cùng là thẻ thông tin của mình: tên, tổng điểm.
  - Bên dưới là danh sách người chơi online. Mỗi dòng có tên, tổng điểm và nhãn trạng thái: Rỗi nền xanh, Bận nền xám. Dòng Bận vẫn bấm được nhưng chỉ hiện thông báo.
- **Màn thi đấu** chia 3 tầng từ trên xuống:
  1. Hàng trên: ô thời gian đếm ngược bên trái, dòng trạng thái đối thủ bên phải.
  2. Danh sách 10 câu cuộn được. Mỗi câu là một thẻ gồm số thứ tự, phép tính chữ lớn, 4 phương án dạng RadioButton xếp thành một hàng ngang.
  3. Hàng dưới: nút Thoát nhỏ bên trái, nút Nộp bài lớn bên phải.
- **Overlay kết quả** che màn thi đấu. Bảng so sánh 2 cột (Bạn, đối thủ) giúp người chơi thấy vì sao thắng: đúng hết hay nhanh hơn.

**Bảng thành phần:**

| Màn | id trong layout | Loại View | Chức năng | Tên ở pha phân tích |
|---|---|---|---|---|
| MainActivity | txtMyName | TextView | Tên hiển thị của mình | outMyInfo |
| | txtMyPoints | TextView | "12,5 điểm" | outMyInfo |
| | rvPlayers | RecyclerView | Danh sách người chơi online, bấm được | outsubListPlayer |
| item_player.xml | txtName | TextView | Tên người chơi | một dòng của outsubListPlayer |
| | txtPoints | TextView | Tổng điểm | một dòng của outsubListPlayer |
| | txtStatus | TextView | Rỗi (xanh) / Bận (xám) | một dòng của outsubListPlayer |
| MatchActivity | txtTimer | TextView | Ô thời gian 01:00 → 00:00; nộp xong thì hiện "Đã nộp bài, đang chờ đối thủ..." | outTimer |
| | txtOpponentStatus | TextView | "HOANG đang làm bài" / "HOANG đã nộp bài" | outOpponentStatus |
| | rvQuestions | RecyclerView | Danh sách 10 câu hỏi | inoutListQuestion |
| | btnSubmit | Button | Nộp bài | subSubmit |
| | btnExit | Button | Thoát trận | subExit |
| item_question.xml | txtExpression | TextView | "1. 12 + 7 = ?" | một câu của inoutListQuestion |
| | rgOptions | RadioGroup | Nhóm 4 phương án, chỉ chọn được 1 | một câu của inoutListQuestion |
| | rbOptions | RadioButton[4] | 4 phương án | một câu của inoutListQuestion |
| Overlay layoutResult | txtResult | TextView | THẮNG! (xanh) / THUA! (đỏ) / HÒA! (vàng) | outResult |
| | txtMyResult | TextView | "Bạn: 10/10 câu đúng · 38,2 giây" | outMyResult |
| | txtOpponentResult | TextView | "HOANG: 10/10 câu đúng · 45,6 giây" | outOpponentResult |
| | txtPoints | TextView | "+1 điểm · Tổng điểm: 13,5" | outPoints |
| | btnContinue | Button | Chơi tiếp | subContinue |
| | btnStop | Button | Dừng; khi có người bỏ trận thì đổi thành Về menu | subStop |

Các thông báo ngắn ("MINH đang bận", "HOANG đã từ chối lời mời", "Đối thủ đã dừng chơi") dùng Toast. Hộp thoại lời mời, hộp thoại chờ trả lời và hộp thoại "Thoát sẽ bị xử thua?" dùng AlertDialog. Cả hai đều không phải thành phần của layout.

### 1.2. Biểu đồ lớp chi tiết

![Hình 20: Biểu đồ lớp chi tiết chức năng Chơi online](lop-thiet-ke-uc03.png)

*Hình 20: Biểu đồ lớp chi tiết chức năng Chơi online.*

**Package "Client (Android)".**

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | MainActivity | Màn chính, cài đặt PlayerListCallback và ChallengeCallback. Giữ người chơi đang đăng nhập (me), lời mời mình đã gửi và đang chờ trả lời (sentChallenge), id các lời mời tới mình đã hiện hộp thoại (shownChallenges), hộp thoại chờ (waitingDialog).<br>- onCreate lấy me bằng SessionDAO.getPlayer, gọi goOnline, rồi watchOnlinePlayers và watchIncoming, truyền chính nó (this) làm callback.<br>- goOnline gọi PlayerApiDAO.getById trong Thread để có tổng điểm mới, xong thì gọi PresenceFirebaseDAO.goOnline.<br>- onPlayersChanged hiện thông tin của mình và đưa những người còn lại vào PlayerAdapter.<br>- onPlayerClick: đối thủ bận thì hiện Toast; đã có sentChallenge thì bỏ qua; ngược lại sendChallenge, watchChallenge rồi showWaitingDialog hiện hộp thoại chờ có nút Hủy.<br>- onChallengeChanged: lời mời tới mình chưa có trong shownChallenges thì showChallengeDialog; lời mình gửi được đồng ý thì closeSentChallenge rồi openMatch(challenge.getMatchId()); bị từ chối thì closeSentChallenge và hiện Toast.<br>- closeSentChallenge gọi ChallengeFirebaseDAO.stopWatchingSent, xóa sentChallenge và đóng hộp thoại chờ.<br>- acceptChallenge gọi MatchApiDAO.createMatch trong Thread, xong thì openMatch. Nút Từ chối gọi ChallengeFirebaseDAO.rejectChallenge.<br>- logout gọi PresenceFirebaseDAO.goOffline, SessionDAO.clear rồi về màn đăng nhập. onDestroy gọi stopWatching của PresenceFirebaseDAO và ChallengeFirebaseDAO |
| Boundary | PlayerAdapter, PlayerViewHolder | PlayerAdapter kế thừa RecyclerView.Adapter, giữ danh sách Player. setPlayers thay danh sách và vẽ lại. onClick của một dòng gọi MainActivity.onPlayerClick. PlayerViewHolder giữ 3 TextView của một dòng |
| Boundary | MatchActivity | Màn thi đấu, cài đặt MatchCallback và PlayerListCallback. Giữ me, mã trận (matchId, nhận qua Intent với khóa EXTRA_MATCH_ID), id đối thủ (opponentId), đồng hồ (timer, MATCH_MILLIS = 60 giây). 6 cờ started, submitted, resultLoading, resultShown, opponentLeft, leaving chặn gọi lặp, vì onSuccess được gọi lại mỗi lần node trận thay đổi.<br>- onCreate gọi MatchFirebaseDAO.watchMatch, truyền chính nó làm MatchCallback.<br>- Lần đầu nhận trận có đề, onSuccess gọi startMatch: lưu opponentId bằng opponentOf, đưa đề vào QuestionAdapter, createTimer rồi start, gọi PresenceFirebaseDAO.watchOnlinePlayers với chính nó làm PlayerListCallback.<br>- onTick vẽ thời gian; onFinish gọi submitAnswers.<br>- submitAnswers khóa danh sách câu bằng QuestionAdapter.setLocked, lấy bài làm bằng getAnswers, gọi MatchApiDAO.submit trong Thread. Thành công thì onSubmitted hủy đồng hồ; lỗi thì onSubmitFailed mở khóa để nộp lại.<br>- Mỗi lần onSuccess gọi showOpponentStatus; trận đã xong thì loadResult gọi MatchApiDAO.getById rồi showResult.<br>- showResult dùng Match.getResult cho showResultTitle; showScores dùng getFinishTime, getPoints trên bản trận server trả về. mineOf, opponentOf tìm MatchPlayer của mình và của đối thủ.<br>- Nút Chơi tiếp / Dừng gọi sendChoice (MatchApiDAO.continueMatch). onNextMatch gọi openNextMatch: stopWatching trận cũ rồi mở trận mới. onStopped hiện Toast và về màn chính.<br>- Nút Thoát và onBackPressed gọi confirmLeave hỏi xác nhận rồi gọi leaveMatch (MatchApiDAO.forfeit).<br>- onPlayersChanged: khi trận đang đấu mà đối thủ biến khỏi danh sách online (mất kết nối), gọi forfeit(opponentId) thay cho đối thủ.<br>- onDestroy hủy đồng hồ, gọi stopWatching của MatchFirebaseDAO và PresenceFirebaseDAO.<br>Activity không tự chấm bài, không tự phân thắng thua |
| Boundary | QuestionAdapter, QuestionViewHolder | QuestionAdapter giữ danh sách MathProblem và mảng phương án đã chọn (answers). onBindViewHolder hiện getExpression và 4 phương án; onCheckedChanged lưu phương án vào answers. getAnswers trả bài làm, câu chưa chọn là −1. setLocked khóa mọi RadioButton sau khi nộp. QuestionViewHolder giữ txtExpression, rgOptions, rbOptions |
| Control | SessionDAO | Giữ prefs : SharedPreferences "session". getPlayer() đọc playerId, username, displayName và đóng gói thành Player. clear() xóa phiên khi đăng xuất |
| Control | FirebaseDAO ← PresenceFirebaseDAO | FirebaseDAO giữ con (gốc Realtime Database). PresenceFirebaseDAO đọc / ghi node presence:<br>- goOnline ghi tên, tổng điểm, trạng thái free và đăng ký onDisconnect().removeValue(). goOffline xóa node của mình.<br>- watchOnlinePlayers / stopWatching thêm / gỡ ValueEventListener, giữ listener đang đăng ký trong watcher để gỡ đúng listener.<br>- toPlayer đóng gói một node thành Player. presenceRef trả node presence |
| Control | FirebaseDAO ← ChallengeFirebaseDAO | Đọc / ghi node challenges:<br>- sendChallenge tạo node bằng push().<br>- watchIncoming lọc lời mời có opponentId là mình và status pending; giữ incomingQuery, incomingWatcher.<br>- watchChallenge theo dõi một lời mời đã gửi; giữ sentRef, sentWatcher.<br>- stopWatchingSent chỉ gỡ theo dõi lời mời đã gửi; stopWatching gỡ cả 2.<br>- rejectChallenge ghi status rejected.<br>- toChallenge đóng gói thành Challenge, dùng toPlayer cho người mời và người được mời. challengesRef trả node challenges |
| Control | FirebaseDAO ← MatchFirebaseDAO | Chỉ đọc node matches/{matchId}; giữ matchRef và watcher để stopWatching gỡ đúng listener. toMatch đóng gói thành Match kèm 10 MathProblem (toProblem, không có đáp án) và 2 MatchPlayer (toMatchPlayer). Khi node có nextMatchId thì gọi onNextMatch; có stoppedBy thì gọi onStopped; còn lại gọi onSuccess. Mất kết nối thì gọi onFail với câu NETWORK_ERROR |
| — | PlayerListCallback, ChallengeCallback, MatchCallback | Interface trả kết quả bất đồng bộ từ các FirebaseDAO về Activity. FirebaseDAO chỉ phụ thuộc vào interface, không biết Activity cụ thể; Activity truyền chính nó (this) khi gọi hàm watch... Nhờ vậy PresenceFirebaseDAO dùng chung cho MainActivity, MatchActivity và FriendActivity |
| Control | ApiDAO ← PlayerApiDAO | getById gửi GET /api/players/{id}; toPlayer đóng gói JSON thành Player. Hàm chạy đồng bộ nên phải gọi trong Thread |
| Control | ApiDAO ← MatchApiDAO | ApiDAO giữ BASE_URL, câu báo lỗi mạng NETWORK_ERROR và các hàm gửi / nhận JSON bằng HttpURLConnection. 3 hàm riêng: open mở kết nối, readGet gửi GET, read đọc phản hồi và ném IOException kèm câu thông báo của server khi mã ≥ 400. MatchApiDAO có 5 hàm, mỗi hàm một API:<br>- createMatch: POST /api/matches.<br>- submit: POST /api/matches/{id}/submit.<br>- forfeit: POST /api/matches/{id}/forfeit.<br>- continueMatch: POST /api/matches/{id}/continue.<br>- getById: GET /api/matches/{id}.<br>toMatch đóng gói JSON trả về thành Match, dùng toProblem cho từng câu hỏi và toMatchPlayer cho từng người chơi trong trận |
| Entity | Player, Challenge, Match, MatchPlayer, MathProblem | Như thiết kế lớp thực thể. Các phương thức nghiệp vụ là hàm Java thuần, có unit test:<br>- MathProblem: isCorrect, getExpression.<br>- Match: countCorrect, isOver, getFinishTime, getResult, getPoints |

**Package "Server (Spring Boot)".**

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | PlayerRestController | getById(id) nhận GET /api/players/{id}: PlayerDAO.findById rồi gán tổng điểm bằng PlayerDAO.getTotalPoints |
| Boundary | MatchRestController | Trọng tài của mọi trận. Giữ waitingChoices: các trận đang có một người chọn chơi tiếp.<br>- create(challenge) nhận POST /api/matches:<br>  1. readChallengeStatus: lời mời đã bị hủy thì trả 409. readPresence của 2 người; một người không online hoặc đang bận thì trả 409 "Không bắt đầu được trận đấu".<br>  2. startMatch: ProblemDAO.generateProblems(10), tạo Match, MatchDAO.insert, FirebaseAdminDAO.writeMatch, updateStatus busy cho 2 người.<br>  3. updateChallenge accepted kèm id trận, rồi hideAnswers xóa đáp án trước khi trả về.<br>- submit(id, matchPlayer):<br>  1. Ghi thời điểm nhận, findById, chấm bằng Match.countCorrect, updateSubmission, writeSubmitted.<br>  2. Nếu Match.isOver: endMatch (updateEndedAt, tính lại tổng điểm 2 người, updatePoints), rồi writeFinished.<br>- forfeit(id, playerId): updateForfeit, endMatch, rồi mới writeStopped, writeFinished để máy đối thủ đọc được kết quả đã lưu; trả 2 người về free.<br>- continueMatch(id, playerId, choice), synchronized:<br>  - Dừng: writeStopped, 2 người về free.<br>  - Chơi tiếp mà đối thủ chưa chọn: lưu vào waitingChoices, writeChoice.<br>  - Đối thủ đã chọn chơi tiếp: startMatch rồi writeNextMatch.<br>- getById(id): findById, gán tổng điểm cho 2 người chơi.<br>Các hàm riêng: findMatch gọi MatchDAO.findById, không có thì trả 404; findPlayer tìm MatchPlayer theo id, không có thì trả 403; setStatus gọi updateStatus cho 2 người; isFree kiểm tra người chơi online và rỗi; hideAnswers đặt correctAnswer = −1 khi trận chưa kết thúc |
| Control | DAO ← PlayerDAO | DAO giữ conn : Connection (JDBC), mở một lần bằng cấu hình loadConfig.<br>- findById đọc tblPlayer qua findOne; toPlayer đóng gói một dòng, COLUMNS là danh sách cột dùng chung.<br>- getTotalPoints lấy các trận đã xong bằng MatchDAO.findFinished, lọc trận có người chơi bằng hasPlayer rồi cộng Match.getPoints |
| Control | DAO ← MatchDAO | - insert ghi 1 dòng tblMatch, 10 dòng tblMathProblem, 2 dòng tblMatchPlayer trong một transaction bằng insertMatch, insertProblem, insertMatchPlayer, gán id vừa sinh.<br>- findById, findFinished dùng chung câu SELECT_PLAYERS qua query; toMatch, toMatchPlayer đóng gói dòng kết quả, findProblems và toProblem đọc đề.<br>- findById đọc trận kèm đề và 2 người chơi.<br>- updateSubmission ghi correctCount, submittedAt.<br>- updateForfeit ghi forfeited.<br>- updateEndedAt ghi endedAt |
| Control | ProblemDAO | Giữ random : Random và 3 hằng OPERATORS, OPTION_COUNT = 4, MAX_OFFSET = 10. generateProblems gọi generateProblem 10 lần. Mỗi câu chọn toán tử +, −, ×, sinh 2 toán hạng bằng nextInt, tính correctAnswer; createOptions sinh 3 phương án nhiễu lệch không quá MAX_OFFSET rồi xáo trộn |
| Control | FirebaseAdminDAO | Giữ dbUrl, khóa truy cập secret, client : HttpClient và mapper : ObjectMapper; gọi REST API của Firebase:<br>- readPresence, readChallengeStatus gửi GET.<br>- Các hàm write..., update... gửi PUT hoặc PATCH tới đúng node trong tài liệu CSDL mục 2. updateStatus, updatePoints đi qua updatePresence.<br>- toQuestions, toPlayers chuyển đề (bỏ đáp án) và 2 người chơi thành dữ liệu node.<br>- send gửi yêu cầu và trả nội dung phản hồi |
| Entity | Player, Challenge, Match, MatchPlayer, MathProblem | Cùng tên, cùng thuộc tính với phía app, để Jackson chuyển JSON thẳng vào đối tượng. Player ở server có thêm passwordHash, không bao giờ trả ra JSON. correctAnswer chỉ trả ra JSON khi trận đã xong |

**Quan hệ giữa các lớp giao diện:**
- MainActivity – MatchActivity nối nhau bằng quan hệ liên kết. MainActivity – PlayerAdapter – PlayerViewHolder và MatchActivity – QuestionAdapter – QuestionViewHolder cũng vậy.
- Mỗi lớp nối với layout của nó: activity_main.xml, item_player.xml, activity_match.xml, item_question.xml. Overlay kết quả nằm trong activity_match.xml.
- Kết tập:
  - MainActivity và MatchActivity chứa thông tin người chơi trong thuộc tính me, nên Player kết tập vào 2 Activity này.
  - PlayerAdapter chứa danh sách Player.
  - QuestionAdapter chứa danh sách MathProblem.

**Quan hệ liên kết HTTP/JSON** giữa 2 package có 2 cặp:
- MatchApiDAO – MatchRestController: trận đấu.
- PlayerApiDAO – PlayerRestController: tổng điểm.

Firebase không phải một lớp trên biểu đồ: các FirebaseDAO ở app và FirebaseAdminDAO ở server cùng đọc / ghi một cây dữ liệu, như mô tả trong tài liệu CSDL mục 2.

---

## 2. Chức năng "Xem lịch sử đấu"

### 2.1. Thiết kế giao diện

![Hình 21: Thiết kế giao diện chức năng Xem lịch sử đấu](giao-dien-uc04.png)

*Hình 21: Thiết kế giao diện chức năng Xem lịch sử đấu.*

Màn Lịch sử đấu có 2 thẻ ở dưới thanh tiêu đề.
- **Thẻ Trận của tôi** mở sẵn. Trên cùng là thẻ tóm tắt: biểu đồ tròn dạng vành khuyên bên trái, giữa ghi tỉ lệ thắng to; chú giải 3 màu và tổng số trận bên phải. Người chơi thấy ngay phong độ của mình trước khi đọc từng trận.
- **Danh sách trận** nằm dưới thẻ tóm tắt, trận mới nhất ở trên. Mỗi dòng là một item_match_history.xml có dải màu bên trái theo kết quả: xanh lá thắng, vàng hòa, đỏ thua. Dòng trên ghi đối thủ, số trận, kết quả, điểm trận; dòng dưới ghi số câu đúng 2 bên và thời điểm kết thúc. Trận có người bỏ trận ghi thêm "Đối thủ bỏ trận" hoặc "Bạn bỏ trận".
- **Thẻ Bảng xếp hạng** giữ nguyên bố cục bảng xếp hạng: hàng tiêu đề cột cố định, mỗi dòng là một item_ranking.xml, dòng của người xem có nền màu nhấn.

![Hình 22: Thiết kế giao diện chi tiết trận](giao-dien-uc04b.png)

*Hình 22: Thiết kế giao diện chi tiết trận.*

Màn Chi tiết trận đi từ tổng quát tới chi tiết: kết quả và điểm trận to ở trên, bảng so sánh 2 bên ở giữa, đề bài kèm đáp án đúng ở dưới. Cột của người xem in đậm.

**Bảng thành phần:**

| Màn | id trong layout | Loại View | Chức năng | Tên ở pha phân tích |
|---|---|---|---|---|
| MainActivity | txtMyName, txtMyPoints | TextView | "HUNG · 13,5 điểm" | outMyInfo |
| | btnHistory | Button | Mở màn Lịch sử đấu | subHistory |
| HistoryActivity | tabLayout | TabLayout | 2 thẻ Trận của tôi, Bảng xếp hạng | subTabRanking |
| | layoutMyMatches | LinearLayout | Khối thẻ Trận của tôi, ẩn khi chọn thẻ Bảng xếp hạng | |
| | pieChart | PieChartView | Biểu đồ tròn tỉ lệ thắng, hòa, thua | outWinRateChart |
| | txtWins, txtDraws, txtLosses | TextView | Chú giải số trận thắng, hòa, thua | outSummary |
| | txtTotal | TextView | Tổng số trận | outSummary |
| | rvMatches | RecyclerView | Danh sách trận | outsubListMatch |
| | txtEmpty | TextView | "Bạn chưa đấu trận nào", ẩn khi có trận | outsubListMatch |
| | rvRanking | RecyclerView | Danh sách đã xếp hạng | outListRanking |
| item_match_history.xml | viewResultBar | View | Dải màu theo kết quả | một dòng của outsubListMatch |
| | txtOpponent | TextView | "vs HOANG · #37" | một dòng của outsubListMatch |
| | txtResult | TextView | THẮNG / HÒA / THUA, kèm "Đối thủ bỏ trận" nếu có | một dòng của outsubListMatch |
| | txtPoints | TextView | "+1" | một dòng của outsubListMatch |
| | txtScore | TextView | "10 – 10 câu" | một dòng của outsubListMatch |
| | txtEndedAt | TextView | "25/10 20:15" | một dòng của outsubListMatch |
| item_ranking.xml | txtRank, txtName, txtPoints, txtAvgOpponent, txtAvgWinTime | TextView | Hạng, tên, tổng điểm, trung bình điểm đối thủ, trung bình thời gian thắng ("—" nếu chưa thắng) | một dòng của outListRanking |
| MatchDetailActivity | txtTitle | TextView | "Trận #37 · 25/10/2026 20:15" | |
| | txtResult | TextView | THẮNG! / THUA! / HÒA! | outResult |
| | txtPoints | TextView | "+1 điểm" | outPoints |
| | txtMyName, txtMyStats | TextView | Tên, "10/10 · 38,2 giây" của mình | outMyResult |
| | txtOpponentName, txtOpponentStats | TextView | Tên, "10/10 · 45,6 giây" hoặc "Bỏ trận" của đối thủ | outOpponentResult |
| | rvProblems | RecyclerView | 10 câu hỏi kèm đáp án đúng | outListProblem |
| item_problem.xml | txtIndex | TextView | Số thứ tự câu | một dòng của outListProblem |
| | txtExpression | TextView | "12 + 7 = 19" | một dòng của outListProblem |

### 2.2. Biểu đồ lớp chi tiết

![Hình 23: Biểu đồ lớp chi tiết chức năng Xem lịch sử đấu](lop-thiet-ke-uc04.png)

*Hình 23: Biểu đồ lớp chi tiết chức năng Xem lịch sử đấu.*

**Package "Client (Android)".**

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | MainActivity | onClick của nút Lịch sử đấu mở HistoryActivity |
| Boundary | HistoryActivity | onCreate lấy người chơi bằng SessionDAO.getPlayer rồi gọi loadHistory. loadHistory gọi MatchApiDAO.getHistory trong Thread, rồi trong runOnUiThread gọi showSummary và gắn MatchHistoryAdapter. showSummary đếm Match.getResult theo id người chơi thành số trận thắng, hòa, thua, gọi PieChartView.setData và ghi chú giải. onTabSelected ẩn / hiện 2 khối layoutMyMatches, layoutRanking; chưa tải bảng xếp hạng (rankingLoaded sai) thì gọi loadRanking. loadRanking đặt rankingLoaded, lỗi thì đặt lại để lần chọn sau tải lại. onMatchClick mở MatchDetailActivity kèm matchId với khóa EXTRA_MATCH_ID. Lỗi mạng thì showError hiện Toast |
| Boundary | PieChartView | View tự vẽ, kế thừa View của Android. Giữ 3 số wins, draws, losses, 3 Paint (arcPaint, percentPaint, labelPaint) và khung oval tạo sẵn để onDraw không cấp phát lại. setData lưu 3 số rồi gọi invalidate; onDraw gọi drawPart vẽ từng cung tròn bằng Canvas.drawArc theo tỉ lệ, khoét giữa thành vành khuyên và ghi tỉ lệ thắng. Cả 3 số bằng 0 thì vẽ vòng xám |
| Boundary | MatchHistoryAdapter, MatchHistoryViewHolder | MatchHistoryAdapter giữ danh sách Match, id người xem, HistoryActivity chứa nó và timeFormat định dạng thời điểm kết thúc. onBindViewHolder gọi Match.getOpponent, getResult, getPoints để điền một dòng và tô màu dải kết quả; bấm vào dòng thì gọi onMatchClick của HistoryActivity, giống cách PlayerAdapter gọi về MainActivity. MatchHistoryViewHolder giữ các View của một dòng |
| Boundary | RankingAdapter, RankingViewHolder | RankingAdapter hiện hạng bằng vị trí + 1, 3 tiêu chí của Player, tô nền dòng của người xem |
| Boundary | MatchDetailActivity | onCreate lấy người chơi bằng SessionDAO.getPlayer, đọc matchId từ Intent rồi gọi loadMatch. loadMatch gọi MatchApiDAO.getById trong Thread, rồi trong runOnUiThread gọi showMatch. showMatch dùng Match.getOpponent, getResult, getPoints để điền kết quả; stats dùng getFinishTime ghi số câu đúng và thời gian của từng bên; rồi gắn ProblemAdapter. matchId giữ mã trận đọc từ Intent |
| Boundary | ProblemAdapter, ProblemViewHolder | ProblemAdapter giữ 10 MathProblem; onBindViewHolder ghi số thứ tự và MathProblem.getExpression thay dấu "?" bằng correctAnswer |
| Control | SessionDAO | Như ở Chơi online |
| Control | ApiDAO ← MatchApiDAO | getHistory gửi GET /api/matches?playerId={id}; getById gửi GET /api/matches/{id}. Cả 2 đóng gói JSON thành Match bằng toMatch |
| Control | ApiDAO ← PlayerApiDAO | getRanking gửi GET /api/players/ranking, đóng gói từng phần tử thành Player bằng toPlayer |
| Entity | Match, MatchPlayer, MathProblem, Player | Match có thêm getOpponent trả MatchPlayer còn lại; các luật getResult, getPoints, getFinishTime dùng chung với Chơi online |

**Package "Server (Spring Boot)".**

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | MatchRestController | getHistory(playerId) nhận GET /api/matches?playerId={id}, gọi MatchDAO.findByPlayer và trả danh sách Match dạng JSON. getById(id) nhận GET /api/matches/{id}, gọi MatchDAO.findById, gán tổng điểm 2 người bằng PlayerDAO.getTotalPoints; trận đã xong nên trả kèm đáp án đúng |
| Boundary | PlayerRestController | getRanking() nhận GET /api/players/ranking, gọi PlayerDAO.getRanking |
| Control | DAO ← MatchDAO | findByPlayer truy vấn tblMatch có endedAt khác rỗng và có dòng tblMatchPlayer của người chơi, JOIN tblMatchPlayer, tblPlayer, sắp theo endedAt giảm dần; đóng gói mỗi trận thành 1 Match + 2 MatchPlayer + 2 Player, không đọc đề. findById đọc thêm 10 dòng tblMathProblem. findFinished dùng cho bảng xếp hạng |
| Control | DAO ← PlayerDAO | getRanking đọc mọi người chơi bằng findAllPlayers, lấy các trận đã xong bằng MatchDAO.findFinished rồi gọi rank. PlayerDAO tạo MatchDAO ngay trong hàm, không giữ làm thuộc tính. rank là hàm static không truy cập CSDL: addMatch cộng điểm, ghi đối thủ và thời gian thắng của từng trận; averagePoints, averageSeconds tính 2 tiêu chí trung bình; cuối cùng sắp xếp 3 tiêu chí. getTotalPoints cộng Match.getPoints qua các trận đã xong |
| Entity | Player, Match, MatchPlayer, MathProblem | Như ở Chơi online |

**Quan hệ giữa các lớp giao diện:**
- MainActivity – HistoryActivity – MatchDetailActivity nối nhau bằng quan hệ liên kết theo luồng màn hình.
- HistoryActivity nối liên kết với PieChartView, MatchHistoryAdapter, RankingAdapter; mỗi Adapter nối với ViewHolder của nó. MatchDetailActivity – ProblemAdapter – ProblemViewHolder cũng vậy.
- MatchHistoryAdapter giữ HistoryActivity để gọi lại onMatchClick khi một dòng được bấm.
- Layout: MainActivity – activity_main.xml, HistoryActivity – activity_history.xml, MatchHistoryViewHolder – item_match_history.xml, RankingViewHolder – item_ranking.xml, MatchDetailActivity – activity_match_detail.xml, ProblemViewHolder – item_problem.xml.
- **Kết tập:** HistoryActivity giữ me và danh sách trận; MatchHistoryAdapter giữ danh sách Match; RankingAdapter giữ danh sách Player; MatchDetailActivity giữ me và một Match; ProblemAdapter giữ 10 MathProblem. Hình thoi ở phía Activity hoặc Adapter.

Chức năng dùng lại MatchApiDAO, PlayerApiDAO, MatchRestController, PlayerRestController, MatchDAO, PlayerDAO của Chơi online; biểu đồ chỉ vẽ các phương thức chức năng này dùng. Phần mới gồm MatchApiDAO.getHistory, MatchRestController.getHistory, MatchDAO.findByPlayer và Match.getOpponent.

---

## 3. Chức năng "Quản lý bạn bè"

### 3.1. Thiết kế giao diện

![Hình 24: Thiết kế giao diện chức năng Quản lý bạn bè](giao-dien-uc07.png)

*Hình 24: Thiết kế giao diện chức năng Quản lý bạn bè.*

Chức năng có 1 màn và 1 hộp thoại xác nhận, đi theo thứ tự MainActivity → FriendActivity → hộp thoại "Hủy kết bạn với <tên>?" khi nhấn ×.
- **Màn chính** có thêm nút Bạn bè nằm giữa nút Lịch sử đấu và nút Đăng xuất.
- **Màn Bạn bè** chia 3 phần từ trên xuống:
  1. Hàng nhập: ô tên đăng nhập chiếm phần lớn chiều ngang, nút Kết bạn bên phải. Gửi xong thì ô được xóa trống để gửi tiếp.
  2. Mục "Lời mời kết bạn (n)": chỉ hiện khi có lời mời đã nhận, đặt trên cùng để người chơi thấy ngay. Mỗi dòng có tên người gửi, nút Đồng ý nổi bật, nút Từ chối viền mảnh để tránh bấm nhầm.
  3. Mục "Bạn bè (n)": danh sách cuộn. Mỗi dòng có chấm xanh (Online) hoặc xám (Offline) bên trái, tên chữ đậm, dòng dưới là tổng điểm và chữ Online / Offline, nút × bên phải. Lời mời mình đã gửi nằm cuối, chữ nghiêng màu xám "Đang chờ trả lời". Chưa có ai thì hiện "Chưa có bạn bè".
- Nút × là nút biểu tượng nên có mô tả "Hủy kết bạn" cho trình đọc màn hình. Hủy kết bạn luôn hỏi xác nhận vì không hoàn tác được.

**Bảng thành phần:**

| Màn | id trong layout | Loại View | Chức năng | Tên ở pha phân tích |
|---|---|---|---|---|
| MainActivity | btnFriends | Button | Mở màn Bạn bè | subFriends |
| FriendActivity | edtUsername | EditText | Ô nhập tên đăng nhập người muốn kết bạn | inUsername |
| | btnAddFriend | Button | Kết bạn | subAddFriend |
| | txtRequestsTitle | TextView | "Lời mời kết bạn (1)"; ẩn khi không có lời mời | outsubListRequest |
| | rvRequests | RecyclerView | Danh sách lời mời đã nhận | outsubListRequest |
| | txtFriendsTitle | TextView | "Bạn bè (3)" | outsubListFriend |
| | txtEmpty | TextView | "Chưa có bạn bè"; chỉ hiện khi danh sách trống | outsubListFriend |
| | rvFriends | RecyclerView | Danh sách bạn bè và lời mời đã gửi | outsubListFriend |
| item_friend_request.xml | txtName | TextView | Tên người gửi | một dòng của outsubListRequest |
| | btnAccept | Button | Đồng ý | một dòng của outsubListRequest |
| | btnReject | Button | Từ chối | một dòng của outsubListRequest |
| item_friend.xml | viewOnlineDot | View | Chấm tròn xanh / xám | một dòng của outsubListFriend |
| | txtName | TextView | Tên người bạn | một dòng của outsubListFriend |
| | txtPoints | TextView | "15 điểm"; lời mời đã gửi thì để trống | một dòng của outsubListFriend |
| | txtStatus | TextView | Online / Offline / Đang chờ trả lời | một dòng của outsubListFriend |
| | btnUnfriend | ImageButton | Hủy kết bạn hoặc rút lời mời, contentDescription "Hủy kết bạn" | một dòng của outsubListFriend |

Các thông báo ngắn ("Đã gửi lời mời kết bạn tới HOANG", "Không tìm thấy người chơi", "Lời mời không còn"…) dùng Toast. Hộp thoại "Hủy kết bạn với TUAN?" dùng AlertDialog. Cả hai đều không phải thành phần của layout.

### 3.2. Biểu đồ lớp chi tiết

![Hình 25: Biểu đồ lớp chi tiết chức năng Quản lý bạn bè](lop-thiet-ke-uc07.png)

*Hình 25: Biểu đồ lớp chi tiết chức năng Quản lý bạn bè.*

**Package "Client (Android)".**

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | MainActivity | onClick của nút Bạn bè mở FriendActivity |
| Boundary | FriendActivity | Màn Bạn bè, cài đặt View.OnClickListener và PlayerListCallback; giữ người chơi đang đăng nhập (me), các lời mời đã nhận (requests), bạn bè và lời mời đã gửi (friends), id những người đang online (onlineIds).<br>- onCreate lấy me bằng SessionDAO.getPlayer, gọi PresenceFirebaseDAO.watchOnlinePlayers rồi loadFriendships.<br>- loadFriendships gọi FriendshipApiDAO.getFriendships trong Thread, rồi trong runOnUiThread gọi showFriendships.<br>- showFriendships dùng Friendship.canAccept để tách lời mời mình cần trả lời vào requests, phần còn lại vào friends, rồi đưa vào 2 Adapter.<br>- onPlayersChanged lấy id những người đang online vào onlineIds và gọi FriendAdapter.setOnlineIds.<br>- onClick của nút Kết bạn kiểm tra ô không trống, gọi FriendshipApiDAO.sendRequest trong Thread, thêm lời mời vào cuối friends và hiện Toast.<br>- onAccept gọi FriendshipApiDAO.accept, chuyển quan hệ từ requests sang friends. onReject và onUnfriend (sau khi hỏi xác nhận) cùng gọi deleteFriendship, hàm này gọi FriendshipApiDAO.delete rồi bỏ quan hệ khỏi 2 danh sách.<br>- refreshTitles cập nhật số lời mời, số bạn và dòng "Chưa có bạn bè"; enableAddButton bật lại nút Kết bạn khi gửi lỗi.<br>- onDestroy gọi PresenceFirebaseDAO.stopWatching.<br>Lỗi server (không tìm thấy, đã có quan hệ, lời mời không còn) hiện bằng Toast với câu thông báo server trả về |
| Boundary | FriendRequestAdapter, FriendRequestViewHolder | FriendRequestAdapter kế thừa RecyclerView.Adapter, giữ danh sách Friendship đã nhận. setFriendships thay danh sách và vẽ lại. onBindViewHolder hiện tên người gửi (sender); nút Đồng ý gọi FriendActivity.onAccept, nút Từ chối gọi FriendActivity.onReject. FriendRequestViewHolder giữ txtName, btnAccept, btnReject |
| Boundary | FriendAdapter, FriendViewHolder | FriendAdapter giữ danh sách Friendship, id của người xem (playerId) và onlineIds. onBindViewHolder lấy người bạn bằng Friendship.getFriend(playerId):<br>- Quan hệ accepted: hiện tổng điểm, chấm xanh và Online nếu id người bạn có trong onlineIds, ngược lại chấm xám và Offline.<br>- Lời mời pending mình đã gửi: hiện "Đang chờ trả lời".<br>Nút × gọi FriendActivity.onUnfriend. setOnlineIds chỉ vẽ lại trạng thái, không gọi server. FriendViewHolder giữ viewOnlineDot, txtName, txtPoints, txtStatus, btnUnfriend |
| — | PlayerListCallback | Interface trả danh sách người chơi online từ PresenceFirebaseDAO về FriendActivity; dùng chung với Chơi online |
| Control | SessionDAO | Như ở Chơi online |
| Control | FirebaseDAO ← PresenceFirebaseDAO | Như ở Chơi online. Chức năng này chỉ đọc node presence qua watchOnlinePlayers, stopWatching; không ghi gì lên Firebase |
| Control | ApiDAO ← FriendshipApiDAO | ApiDAO có thêm delete(path) gửi yêu cầu DELETE. FriendshipApiDAO có 4 hàm, mỗi hàm một API:<br>- getFriendships: GET /api/friendships?playerId={id}.<br>- sendRequest: POST /api/friendships, thân gồm senderId và receiverUsername.<br>- accept: POST /api/friendships/{id}/accept?playerId={id}.<br>- delete: DELETE /api/friendships/{id}?playerId={id}.<br>toFriendship đóng gói JSON thành Friendship, dùng toPlayer cho người gửi và người nhận |
| Entity | Friendship, Player | Như thiết kế lớp thực thể. Friendship có 2 phương thức nghiệp vụ là hàm Java thuần:<br>- getFriend(playerId): người còn lại trong quan hệ.<br>- canAccept(playerId): lời mời đang chờ và playerId là người nhận |

**Package "Server (Spring Boot)".**

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | FriendshipRestController | - getFriendships(playerId) nhận GET /api/friendships: FriendshipDAO.findByPlayer, rồi gán tổng điểm cho người gửi và người nhận bằng PlayerDAO.getTotalPoints.<br>- sendRequest(body) nhận POST /api/friendships:<br>  1. PlayerDAO.findById lấy người gửi; PlayerDAO.findByUsername tìm người nhận, không có thì trả 404 "Không tìm thấy người chơi".<br>  2. Người nhận là người gửi thì trả 400 "Không thể kết bạn với chính mình".<br>  3. FriendshipDAO.existsBetween đúng thì trả 409 "Đã có lời mời hoặc đã là bạn bè".<br>  4. Tạo Friendship pending, FriendshipDAO.insert, trả 201 kèm lời mời.<br>- accept(id, playerId): FriendshipDAO.findById, không có thì trả 404 "Lời mời không còn"; Friendship.canAccept sai thì trả 403; đúng thì updateStatus accepted, gán tổng điểm và trả quan hệ.<br>- delete(id, playerId): FriendshipDAO.findById; playerId không phải người gửi hay người nhận thì trả 403; ngược lại FriendshipDAO.delete, trả 204.<br>Các hàm riêng: findFriendship gọi FriendshipDAO.findById, không có thì trả 404 "Lời mời không còn"; parseId đọc senderId, sai định dạng thì trả 400 |
| Control | DAO ← FriendshipDAO | DAO giữ conn : Connection (JDBC). FriendshipDAO giữ câu SELECT dùng chung cho findByPlayer, findById.<br>- findByPlayer truy vấn tblFriendship có tblPlayerSenderId hoặc tblPlayerReceiverId bằng playerId, JOIN tblPlayer 2 lần để có người gửi và người nhận.<br>- findById đọc một quan hệ theo id, cùng cách JOIN.<br>- existsBetween kiểm tra có dòng nào giữa 2 người theo cả 2 chiều.<br>- insert ghi 1 dòng tblFriendship, gán id vừa sinh.<br>- updateStatus ghi status, chỉ khi dòng đang pending.<br>- delete xóa 1 dòng theo id.<br>- toFriendship đóng gói một dòng kết quả thành Friendship kèm 2 Player |
| Control | DAO ← PlayerDAO | findById, findByUsername đọc tblPlayer; getTotalPoints như ở Chơi online. Cả 3 hàm thuộc phần chung, chức năng này chỉ gọi lại |
| Entity | Friendship, Player | Cùng tên, cùng thuộc tính với phía app, để Jackson chuyển JSON thẳng vào đối tượng. passwordHash của Player không bao giờ trả ra JSON |

**Quan hệ giữa các lớp giao diện:**
- MainActivity – FriendActivity, FriendActivity – FriendRequestAdapter – FriendRequestViewHolder, FriendActivity – FriendAdapter – FriendViewHolder nối nhau bằng quan hệ liên kết.
- Layout: MainActivity – activity_main.xml, FriendActivity – activity_friend.xml, FriendRequestViewHolder – item_friend_request.xml, FriendViewHolder – item_friend.xml.
- FriendActivity thực thi interface PlayerListCallback.
- Kết tập:
  - FriendActivity chứa người chơi trong thuộc tính me, nên Player kết tập vào FriendActivity.
  - FriendActivity, FriendRequestAdapter, FriendAdapter chứa danh sách Friendship, bội số n ở phía Friendship.
  - Friendship chứa 2 Player ở vai trò sender và receiver, ở cả 2 package.

**Quan hệ liên kết HTTP/JSON** giữa 2 package có 1 cặp: FriendshipApiDAO – FriendshipRestController.

Trạng thái online của bạn bè đi qua Firebase, không đi qua server: PresenceFirebaseDAO đọc cùng node presence mà Chơi online đã ghi.
