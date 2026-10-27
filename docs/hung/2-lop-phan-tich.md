# BIỂU ĐỒ LỚP PHA PHÂN TÍCH — CHƠI ONLINE, XEM LỊCH SỬ ĐẤU, QUẢN LÝ BẠN BÈ

---

## 1. Phân tích tĩnh chức năng "Chơi online"

### Bước 1 + 2: Lớp View và thành phần

| Bước kịch bản v1 | Giao diện | Lớp View | Thành phần |
|---|---|---|---|
| 1–4 | Giao diện chính | MainView | outMyInfo: dòng "HUNG · 12,5 điểm"<br>outsubListPlayer: danh sách người chơi online; mỗi dòng có tên, tổng điểm, trạng thái; bấm vào một dòng để thách đấu |
| 8–12 | Giao diện thi đấu | MatchView | outTimer: ô thời gian đếm ngược<br>outOpponentStatus: dòng "HOANG đang làm bài" / "HOANG đã nộp bài"<br>inoutListQuestion: danh sách 10 câu hỏi, mỗi câu hiện phép tính và 4 phương án để chọn<br>subSubmit: nút Nộp bài<br>subExit: nút Thoát |
| 14–15 | Giao diện kết quả | MatchResultView | outResult: THẮNG! / THUA! / HÒA!<br>outMyResult: số câu đúng, thời gian làm bài của mình<br>outOpponentResult: số câu đúng, thời gian làm bài của đối thủ<br>outPoints: điểm trận được cộng và tổng điểm mới<br>subContinue: nút Chơi tiếp<br>subStop: nút Dừng |

inoutListQuestion mang tiền tố inout vì danh sách vừa hiển thị câu hỏi vừa nhận phương án người chơi chọn.

Không đề xuất lớp View cho:
- Hộp thoại lời mời "HUNG (12,5 điểm) thách đấu bạn" với 2 nút Đồng ý, Từ chối. Đây là hộp thoại xác nhận hiện trên giao diện chính.
- Hộp thoại "Đang chờ HOANG trả lời...", hộp thoại "Thoát sẽ bị xử thua?".
- Các thông báo "MINH đang bận", "HOANG đã từ chối lời mời", "Đối thủ đã thoát, bạn thắng", "Đối thủ đã dừng chơi".

### Bước 3: Phương thức và lớp thực thể được gán

| Bước v1 | Xử lý hệ thống | Tên phương thức | Đầu vào | Đầu ra | Gán cho lớp | Lý do |
|---|---|---|---|---|---|---|
| 1 | Ghi người chơi vào danh sách online với trạng thái rỗi | goOnline() | Người chơi (Player) | Không có | Player | Đầu vào chỉ thuộc Player |
| 1, 7 | Lấy danh sách người chơi online và theo dõi thay đổi | getOnlinePlayers() | Không có | Danh sách Player kèm tổng điểm, trạng thái | Player | Đầu ra là danh sách Player |
| 3 | Gửi lời mời tới một người chơi đang rỗi | sendChallenge() | Người mời, người được mời (Player) | Lời mời đang chờ (Challenge) | Challenge | Đầu ra là Challenge |
| 4, 7 | Theo dõi lời mời: người được mời nhận lời mời mới; người mời biết lời mời được đồng ý hay bị từ chối | watchChallenge() | Người chơi, hoặc lời mời | Lời mời mới nhất (Challenge) | Challenge | Đầu ra là Challenge |
| 5 (ngoại lệ) | Từ chối lời mời | rejectChallenge() | Lời mời (Challenge) | Không có | Challenge | Đầu vào chỉ thuộc Challenge |
| 6 | Tạo trận từ lời mời đã đồng ý: sinh đề, chuyển 2 người sang bận | createMatch() | Lời mời (Challenge) | Trận mới (Match) | Match | Đầu ra là Match |
| 6, 17 | Sinh một câu hỏi kèm 4 phương án | generateProblem() | Không có | Câu hỏi (MathProblem) | MathProblem | Đầu ra là MathProblem |
| 8, 11, 13 | Theo dõi trận: nhận đề, biết đối thủ đã nộp bài, biết trận đã xong | watchMatch() | Trận (Match) | Trận mới nhất (Match) | Match | Đầu ra là Match |
| 11 | Nhận bài làm, ghi thời điểm nộp, chấm bài | submitAnswers() | Trận (Match), bài làm của người chơi (MatchPlayer) | Trận sau khi chấm (Match) | Match | Đầu ra là Match |
| 11 | Chấm một câu | isCorrect() | Câu hỏi (MathProblem), phương án đã chọn | Đúng / sai | MathProblem | Đầu ra không phải thực thể; đầu vào chính là MathProblem |
| 13 | Kiểm tra trận đã xong chưa | isOver() | Trận (Match) | Đúng / sai | Match | Đầu vào chỉ thuộc Match |
| 14 | Lấy kết quả thắng / thua / hòa của một người chơi | getResult() | Trận (Match), người chơi | WIN / LOSE / DRAW | Match | Match chứa được cả 2 đầu vào, qua MatchPlayer |
| 14 | Lấy thời gian làm bài của một người chơi | getFinishTime() | Trận (Match), người chơi | Số mili giây | Match | Cần cả thời điểm bắt đầu của Match và thời điểm nộp của MatchPlayer |
| 14 | Lấy điểm trận của một người chơi | getPoints() | Trận (Match), người chơi | 1 / 0,5 / 0 | Match | Như getResult() |
| 9–10 (ngoại lệ) | Người chơi thoát trận và bị xử thua | forfeit() | Trận (Match), người chơi | Trận đã kết thúc (Match) | Match | Đầu ra là Match |
| 15–17 | Ghi nhận lựa chọn chơi tiếp hoặc dừng; cả 2 chơi tiếp thì tạo trận mới | continueMatch() | Trận (Match), người chơi, lựa chọn | Trận mới, hoặc trận cũ đã dừng (Match) | Match | Đầu ra là Match |

### Bước 4: Biểu đồ lớp pha phân tích

![Hình 8: Biểu đồ lớp pha phân tích chức năng Chơi online](lop-phan-tich-uc03.png)

*Hình 8: Biểu đồ lớp pha phân tích chức năng Chơi online.*

- **3 lớp View** nối nhau bằng quan hệ liên kết theo luồng màn hình: MainView – MatchView – MatchResultView. MatchResultView quay về MatchView khi chơi tiếp, về MainView khi dừng.
- Mỗi View nối với lớp thực thể nó gọi phương thức bằng **quan hệ liên kết**:
  - MainView: Player (danh sách online), Challenge (lời mời), Match (tạo trận).
  - MatchView: Match (theo dõi trận, nộp bài, thoát trận).
  - MatchResultView: Match (kết quả, điểm, chơi tiếp).
- **Quan hệ giữa các thực thể** giữ đúng như Hình 2:
  - Challenge chứa thông tin 2 Player (kết tập).
  - Player – Match là n-n, qua lớp liên kết MatchPlayer.
  - MathProblem là thành phần của Match (hợp thành).
- **Match nhận nhiều phương thức nhất**, vì hầu hết xử lý của chức năng trả về một trận đấu. Các luật của trận đều nằm ở thực thể, không nằm ở giao diện.

---

## 2. Phân tích tĩnh chức năng "Xem lịch sử đấu"

### Bước 1 + 2: Lớp View và thành phần

| Bước kịch bản v1 | Giao diện | Lớp View | Thành phần |
|---|---|---|---|
| 1 | Giao diện chính | MainView | outMyInfo: dòng "HUNG · 13,5 điểm"<br>subHistory: nút Lịch sử đấu |
| 2, 6–9 | Giao diện lịch sử đấu | HistoryView | outWinRateChart: biểu đồ tròn tỉ lệ thắng / hòa / thua<br>outSummary: số trận thắng, hòa, thua và tổng số trận<br>outsubListMatch: danh sách trận; mỗi dòng có đối thủ, kết quả, điểm trận, số câu đúng, thời điểm kết thúc; bấm vào một dòng để xem chi tiết trận<br>subTabRanking: thẻ Bảng xếp hạng<br>outListRanking: danh sách người chơi đã xếp hạng; dòng của người xem được tô nổi bật |
| 4–5 | Giao diện chi tiết trận | MatchDetailView | outResult: THẮNG! / THUA! / HÒA!<br>outPoints: điểm trận<br>outMyResult: số câu đúng, thời gian làm bài của mình<br>outOpponentResult: tên, số câu đúng, thời gian làm bài của đối thủ<br>outListProblem: 10 câu hỏi kèm đáp án đúng |

Thẻ Trận của tôi và thẻ Bảng xếp hạng là 2 phần của cùng một giao diện, chuyển qua lại bằng thẻ, nên chỉ có một lớp HistoryView. Danh sách xếp hạng và danh sách câu hỏi chỉ để xem, bấm vào không làm gì, nên mang tiền tố out. Danh sách trận mang tiền tố outsub vì bấm vào một dòng thì mở chi tiết trận.

Không đề xuất lớp View cho các thông báo "Bạn chưa đấu trận nào", "Không tải được lịch sử đấu…", "Không tải được chi tiết trận…", "Không tải được bảng xếp hạng…".

### Bước 3: Phương thức và lớp thực thể được gán

| Bước v1 | Xử lý hệ thống | Tên phương thức | Đầu vào | Đầu ra | Gán cho lớp | Lý do |
|---|---|---|---|---|---|---|
| 2 | Lấy các trận đã kết thúc mà người chơi tham gia, mới nhất đứng đầu | getHistory() | Người chơi (Player) | Danh sách Match kèm 2 người chơi | Match | Đầu ra là danh sách Match |
| 2, 4 | Biết người chơi thắng, hòa hay thua một trận | getResult() | Trận (Match), người chơi | WIN / LOSE / DRAW | Match | Như ở Chơi online; dùng chung |
| 2, 4 | Lấy điểm trận của người chơi | getPoints() | Trận (Match), người chơi | 1 / 0,5 / 0 | Match | Như ở Chơi online |
| 2, 4 | Lấy người chơi còn lại trong trận | getOpponent() | Trận (Match), người chơi | Người chơi trong trận của đối thủ (MatchPlayer) | Match | Match chứa cả 2 MatchPlayer |
| 4 | Lấy một trận kèm đề, đáp án đúng và 2 người chơi | getMatch() | Trận (Match) | Trận đầy đủ (Match) | Match | Đầu ra là Match |
| 4 | Lấy thời gian làm bài của mỗi bên | getFinishTime() | Trận (Match), người chơi | Số mili giây | Match | Như ở Chơi online |
| 8 | Lấy mọi người chơi kèm 3 tiêu chí, đã sắp xếp | getRanking() | Không có | Danh sách Player kèm tổng điểm, trung bình điểm đối thủ, trung bình thời gian thắng | Player | Đầu ra là danh sách Player |

Số trận thắng, hòa, thua và tỉ lệ thắng trên biểu đồ tròn không phải phương thức của thực thể: HistoryView đếm các kết quả do getResult() trả về để hiển thị. Đây là thông tin dẫn xuất, không lưu.

### Bước 4: Biểu đồ lớp pha phân tích

![Hình 9: Biểu đồ lớp pha phân tích chức năng Xem lịch sử đấu](lop-phan-tich-uc04.png)

*Hình 9: Biểu đồ lớp pha phân tích chức năng Xem lịch sử đấu.*

- **3 lớp View** nối nhau bằng quan hệ liên kết theo luồng màn hình: MainView – HistoryView – MatchDetailView.
- Mỗi View nối với lớp thực thể nó gọi phương thức bằng **quan hệ liên kết**:
  - HistoryView: Match (lịch sử, kết quả, điểm, đối thủ), Player (bảng xếp hạng).
  - MatchDetailView: Match (trận đầy đủ, kết quả, điểm, đối thủ, thời gian làm bài).
- **Quan hệ giữa các thực thể** giữ đúng như Hình 2:
  - Player – Match là n-n, qua lớp liên kết MatchPlayer.
  - MathProblem là thành phần của Match (hợp thành); chi tiết trận cần đề để hiện đáp án đúng.
- **Lịch sử đấu và bảng xếp hạng dùng cùng luật thắng thua** với trận đấu: getResult(), getPoints(), getFinishTime() của Match. Không có luật nào được viết lại ở giao diện.

---

## 3. Phân tích tĩnh chức năng "Quản lý bạn bè"

### Bước 1 + 2: Lớp View và thành phần

| Bước kịch bản v1 | Giao diện | Lớp View | Thành phần |
|---|---|---|---|
| 1, 5 | Giao diện chính | MainView | outMyInfo: dòng "HUNG · 13,5 điểm"<br>subFriends: nút Bạn bè |
| 2–8 | Giao diện bạn bè | FriendView | inUsername: ô nhập tên đăng nhập<br>subAddFriend: nút Kết bạn<br>outsubListRequest: danh sách lời mời kết bạn đã nhận; mỗi dòng có tên người gửi, nút Đồng ý, nút Từ chối<br>outsubListFriend: danh sách bạn bè và lời mời đã gửi; mỗi dòng có trạng thái Online / Offline, tên, tổng điểm hoặc "Đang chờ trả lời", nút Hủy kết bạn |

outsubListRequest và outsubListFriend mang tiền tố outsub vì mỗi dòng vừa hiển thị thông tin vừa có nút để bấm.

Không đề xuất lớp View cho:
- Hộp thoại "Hủy kết bạn với TUAN?" với 2 nút Không, Hủy kết bạn. Đây là hộp thoại xác nhận hiện trên giao diện bạn bè.
- Các thông báo "Đã gửi lời mời kết bạn tới HOANG", "Không tìm thấy người chơi", "Không thể kết bạn với chính mình", "Đã có lời mời hoặc đã là bạn bè", "Lời mời không còn".

### Bước 3: Phương thức và lớp thực thể được gán

| Bước v1 | Xử lý hệ thống | Tên phương thức | Đầu vào | Đầu ra | Gán cho lớp | Lý do |
|---|---|---|---|---|---|---|
| 2, 5 | Lấy các lời mời kết bạn và quan hệ bạn bè của người chơi, ở cả 2 vai trò người gửi và người nhận | getFriendships() | Người chơi (Player) | Danh sách Friendship kèm người gửi, người nhận | Friendship | Đầu ra là danh sách Friendship |
| 2, 7 | Lấy danh sách người chơi đang online để biết bạn nào Online | getOnlinePlayers() | Không có | Danh sách Player kèm trạng thái | Player | Như ở Chơi online; dùng chung |
| 2, 7 | Lấy người còn lại trong một quan hệ: là người nhận nếu mình là người gửi, ngược lại là người gửi | getFriend() | Quan hệ (Friendship), người chơi | Người bạn (Player) | Friendship | Friendship chứa được cả 2 đầu vào: người gửi và người nhận |
| 4 | Tìm người nhận theo tên đăng nhập, kiểm tra chưa có quan hệ giữa 2 người, tạo lời mời đang chờ | sendRequest() | Người gửi (Player), tên đăng nhập người nhận | Lời mời đang chờ (Friendship) | Friendship | Đầu ra là Friendship |
| 7 | Chuyển lời mời sang trạng thái đã là bạn bè | accept() | Lời mời (Friendship), người chơi | Quan hệ đã đồng ý (Friendship) | Friendship | Đầu ra là Friendship |
| 7 | Kiểm tra lời mời đang chờ và người chơi là người nhận | canAccept() | Lời mời (Friendship), người chơi | Đúng / sai | Friendship | Đầu ra không phải thực thể; đầu vào chính là Friendship |
| 2, 6 (ngoại lệ) | Xóa lời mời khi từ chối hoặc rút lại; xóa quan hệ khi hủy kết bạn | delete() | Quan hệ (Friendship), người chơi | Không có | Friendship | Đầu vào chính là Friendship |

### Bước 4: Biểu đồ lớp pha phân tích

![Hình 10: Biểu đồ lớp pha phân tích chức năng Quản lý bạn bè](lop-phan-tich-uc07.png)

*Hình 10: Biểu đồ lớp pha phân tích chức năng Quản lý bạn bè.*

- Chức năng có 2 lớp View nối nhau bằng quan hệ liên kết: MainView mở FriendView.
- FriendView nối với 2 lớp thực thể nó gọi phương thức:
  - Friendship: lấy, gửi, đồng ý, xóa lời mời và quan hệ bạn bè.
  - Player: lấy danh sách người chơi online để hiện trạng thái của bạn bè.
- **Quan hệ giữa các thực thể** giữ đúng như Hình 2: Friendship chứa thông tin 2 Player ở 2 vai trò người gửi và người nhận (kết tập).
- **Friendship nhận mọi phương thức** của chức năng, kể cả luật "chỉ người nhận mới đồng ý được" (canAccept()). Trạng thái online của bạn bè không lưu trong Friendship mà lấy từ danh sách online.

Đối chiếu với kịch bản v1:

| Thành phần trong kịch bản v1 | Thuộc tính |
|---|---|
| Nút Bạn bè | MainView.subFriends |
| Ô tên đăng nhập, nút Kết bạn | FriendView.inUsername, subAddFriend |
| Mục Lời mời kết bạn, nút Đồng ý, nút Từ chối | FriendView.outsubListRequest |
| Mục Bạn bè, trạng thái, tổng điểm, dòng "Đang chờ trả lời", nút × | FriendView.outsubListFriend |

---

## 4. Đối chiếu với kịch bản v1

Mọi thành phần giao diện nêu trong kịch bản v1 đều có ở lớp View tương ứng:

| Thành phần trong kịch bản v1 | Thuộc tính |
|---|---|
| Dòng thông tin của mình, danh sách người chơi online | MainView.outMyInfo, outsubListPlayer |
| Nút Lịch sử đấu, nút Bạn bè | MainView.subHistory, subFriends |
| Ô thời gian, trạng thái đối thủ, danh sách câu hỏi, nút Nộp bài, nút Thoát | MatchView.outTimer, outOpponentStatus, inoutListQuestion, subSubmit, subExit |
| Dòng kết quả, kết quả của mình, của đối thủ, điểm, nút Chơi tiếp, nút Dừng | MatchResultView.outResult, outMyResult, outOpponentResult, outPoints, subContinue, subStop |
| Biểu đồ tròn, số trận thắng / hòa / thua, danh sách trận, thẻ Bảng xếp hạng, danh sách xếp hạng | HistoryView.outWinRateChart, outSummary, outsubListMatch, subTabRanking, outListRanking |
| Kết quả, điểm trận, kết quả của mình, của đối thủ, danh sách 10 câu kèm đáp án | MatchDetailView.outResult, outPoints, outMyResult, outOpponentResult, outListProblem |
| Ô tên đăng nhập, nút Kết bạn, danh sách lời mời, danh sách bạn bè | FriendView.inUsername, subAddFriend, outsubListRequest, outsubListFriend |
