# BIỂU ĐỒ LỚP PHA PHÂN TÍCH — CHƠI ONLINE, XEM LỊCH SỬ ĐẤU

---

## 1. Phân tích tĩnh chức năng "Chơi online"

### Bước 1 + 2: Lớp View và thành phần

| Bước kịch bản v1 | Giao diện | Lớp View | Thành phần |
|---|---|---|---|
| 1 | Giao diện chính | MainView | outDisplayName: dòng "Xin chào, HUNG"<br>subPlayOnline: nút Chơi online |
| 2, 7 | Giao diện chơi online | OnlineView | subCreateRoom: nút Tạo phòng<br>inRoomCode: ô nhập mã phòng<br>subJoinRoom: nút Vào phòng |
| 4, 9, 10 | Giao diện phòng chờ | LobbyView | outRoomCode: mã phòng chữ lớn<br>outStatus: dòng trạng thái ("Đang chờ đối thủ...", "Trận đấu bắt đầu!")<br>outOpponentName: tên đối thủ<br>subCancel: nút Hủy |
| 11–15 | Giao diện thi đấu | OnlineGameView | outMyScore: điểm của mình (bên trái, nổi bật)<br>outOpponentScore: điểm đối thủ (bên phải). Màn này không hiện tên; tên đối thủ đã hiện ở phòng chờ<br>outScorePopup: chữ "+12" / "MISS" bay lên từ điểm của mình rồi biến mất<br>outTimer: đồng hồ đếm ngược, hết giờ thì hiện "Hết giờ! Đang chờ kết quả..."<br>outProblem: câu hỏi<br>inAnswer: số đang nhập, hiện sau dấu =<br>subDigits: 10 nút số 0–9<br>subBackspace: nút xóa số cuối<br>subSubmit: nút OK |
| 17 | Giao diện kết quả | MatchResultView | outResult: THẮNG! / THUA! / HÒA!<br>outScoreLine: tỉ số "120 - 96" (mình - đối thủ)<br>subBackToMenu: nút Về menu |

Không đề xuất lớp View cho:
- Hộp thoại "Thoát sẽ bị xử thua?".
- Các thông báo "Không tìm thấy phòng", "Phòng đã đầy", "Đối thủ đã thoát".

### Bước 3: Phương thức và lớp thực thể được gán

| Bước v1 | Xử lý hệ thống | Tên phương thức | Đầu vào | Đầu ra | Gán cho lớp | Lý do |
|---|---|---|---|---|---|---|
| 4 | Tạo phòng mới có mã phòng, mã đồng bộ câu hỏi, trạng thái đang chờ, người tạo là người chơi thứ nhất | createMatch() | Người chơi đang đăng nhập (Player) | Trận mới (Match) | Match | Đầu ra là Match |
| 9 | Kiểm tra người chơi có được vào trận không | canJoin() | Trận (Match), người chơi | Đúng / sai | Match | Đầu ra không phải thực thể; đầu vào chính là Match |
| 9 | Đưa người chơi vào trận bằng mã phòng, chuyển trận sang đang đấu | joinMatch() | Mã phòng, người chơi (Player) | Trận đã có 2 người (Match) | Match | Đầu ra là Match |
| 10, 14 | Theo dõi trận: biết khi có đối thủ vào, khi đối thủ ghi điểm, khi đối thủ bỏ trận | watchMatch() | Mã phòng | Trận mới nhất (Match, gồm 2 MatchPlayer) | Match | Đầu ra là Match |
| 5 (ngoại lệ) | Hủy phòng đang chờ | deleteMatch() | Trận (Match) | Không có | Match | Đầu vào chỉ thuộc Match |
| 11, 13 | Sinh câu hỏi theo mã đồng bộ và mức hiện tại | generateProblem() | Mã đồng bộ câu hỏi, mức | Câu hỏi (MathProblem) | MathProblem | Đầu ra là MathProblem |
| 13 | Chấm câu trả lời, cộng điểm, cập nhật chuỗi đúng | submitAnswer() | Ván đang chơi (GameSession, giữ câu hiện tại), đáp án chọn | Ván sau khi chấm (GameSession): điểm vừa cộng, điểm mới | GameSession | Đầu ra là GameSession |
| 13 | Gửi điểm mới của mình để máy đối thủ thấy | updateScore() | Người chơi trong trận (MatchPlayer), điểm mới | Không có | MatchPlayer | Đầu vào chỉ thuộc MatchPlayer |
| 15, 16 | Báo hết giờ; khi trận đã xong thì xác định kết quả và lưu trận vào lịch sử đấu | finishMatch() | Trận (Match) | Trận đã lưu, có thời điểm kết thúc (Match) | Match | Đầu ra là Match |
| 17 | Lấy kết quả thắng / thua / hòa của một người chơi | getResult() | Trận (Match), người chơi | WIN / LOSE / DRAW | Match | Đầu ra không phải thực thể; Match chứa được cả 2 đầu vào (qua MatchPlayer) |
| 12–14 (ngoại lệ) | Người chơi thoát trận và bị xử thua | forfeit() | Người chơi trong trận (MatchPlayer) | Không có | MatchPlayer | Đầu vào chỉ thuộc MatchPlayer |

### Bước 4: Biểu đồ lớp pha phân tích

![Hình 6: Biểu đồ lớp pha phân tích chức năng Chơi online](lop-phan-tich-uc05.png)

*Hình 6: Biểu đồ lớp pha phân tích chức năng Chơi online.*

- **5 lớp View** nối nhau bằng quan hệ liên kết theo luồng màn hình: MainView – OnlineView – LobbyView – OnlineGameView – MatchResultView – quay về MainView.
- Mỗi View nối với lớp thực thể nó gọi phương thức bằng **quan hệ liên kết**. OnlineGameView dùng nhiều thực thể nhất:
  - MathProblem: sinh câu hỏi.
  - GameSession: chấm điểm.
  - MatchPlayer: gửi điểm, thoát trận.
  - Match: theo dõi trận, kết thúc trận.
- **Quan hệ giữa các thực thể** giữ đúng như Hình 2:
  - Player – Match là n-n, qua lớp liên kết MatchPlayer.
  - GameSession chứa thông tin Player (kết tập).
  - MathProblem là thành phần của GameSession (hợp thành).
- **Match nhận nhiều phương thức nhất**, vì hầu hết xử lý của chức năng trả về một trận đấu.

---

## 2. Phân tích tĩnh chức năng "Xem lịch sử đấu"

### Bước 1 + 2: Lớp View và thành phần

| Bước kịch bản v1 | Giao diện | Lớp View | Thành phần |
|---|---|---|---|
| 1 | Giao diện chính | MainView | outDisplayName: dòng "Xin chào, HUNG"<br>subHistory: nút Lịch sử đấu |
| 2 | Giao diện lịch sử đấu | HistoryView | outListMatch: danh sách trận; mỗi dòng có thời gian, tên đối thủ, tỉ số, kết quả. Khi danh sách rỗng, thành phần này hiện "Chưa có trận nào" |

Danh sách chỉ để xem, bấm vào một dòng không làm gì. Vì vậy thành phần mang tiền tố out, không phải outsub.

### Bước 3: Phương thức và lớp thực thể được gán

| Bước v1 | Xử lý hệ thống | Tên phương thức | Đầu vào | Đầu ra | Gán cho lớp | Lý do |
|---|---|---|---|---|---|---|
| 2 | Lấy các trận đã kết thúc mà người chơi tham gia, mới nhất trước | getHistory() | Người chơi (Player) | Danh sách Match, mỗi trận có 2 MatchPlayer | Match | Đầu ra là danh sách Match |
| 2 | Lấy kết quả của người chơi trong từng trận để hiện chữ và màu | getResult() | Trận (Match), người chơi | WIN / LOSE / DRAW | Match | Như ở UC05; dùng chung một phương thức |

### Bước 4: Biểu đồ lớp pha phân tích

![Hình 7: Biểu đồ lớp pha phân tích chức năng Xem lịch sử đấu](lop-phan-tich-uc06.png)

*Hình 7: Biểu đồ lớp pha phân tích chức năng Xem lịch sử đấu.*

- Chức năng chỉ có 2 lớp View, nối nhau bằng quan hệ liên kết:
  - MainView giữ người chơi đang đăng nhập.
  - HistoryView gọi getHistory() và getResult() của Match.
- Tên đối thủ và tỉ số có sẵn trong 2 MatchPlayer của mỗi trận (lớp liên kết giữa Player và Match), nên không cần thêm phương thức nào khác.
- Kết quả thắng / thua / hòa là thuộc tính dẫn xuất, được tính bằng getResult() chứ không lưu.

---

## 3. Đối chiếu với kịch bản v1

Mọi thành phần giao diện nêu trong kịch bản v1 đều có ở lớp View tương ứng:

| Thành phần trong kịch bản v1 | Thuộc tính |
|---|---|
| Nút Chơi online, nút Lịch sử đấu | MainView.subPlayOnline, MainView.subHistory |
| Nút Tạo phòng, ô mã phòng, nút Vào phòng | OnlineView.subCreateRoom, inRoomCode, subJoinRoom |
| Mã phòng, trạng thái, đối thủ, nút Hủy | LobbyView.outRoomCode, outStatus, outOpponentName, subCancel |
| Hàng điểm, chữ bay "+12" / "MISS", đồng hồ, câu hỏi, số đang nhập, bàn phím số, nút xóa, nút OK | OnlineGameView.outMyScore, outOpponentScore, outScorePopup, outTimer, outProblem, inAnswer, subDigits, subBackspace, subSubmit |
| Dòng kết quả, tỉ số, nút Về menu | MatchResultView.outResult, outScoreLine, subBackToMenu |
| Danh sách trận (hoặc "Chưa có trận nào") | HistoryView.outListMatch |
