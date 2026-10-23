# KỊCH BẢN v3 VÀ BIỂU ĐỒ TUẦN TỰ CHI TIẾT — CHƠI ONLINE, XEM LỊCH SỬ ĐẤU

---

## 1. Chức năng "Chơi online"

### 1.1. Đoạn A: Tạo phòng

1. Người chơi nhấn nút Chơi online trên MainActivity.
2. Hàm onClick() của MainActivity gọi hàm startActivity() mở OnlineActivity.
3. Hàm onCreate() của OnlineActivity gọi hàm getPlayer() của SessionDAO.
4. Hàm getPlayer() đọc thông tin phiên đăng nhập và gọi lớp Player để đóng gói.
5. Lớp Player trả về đối tượng Player cho hàm getPlayer().
6. Hàm getPlayer() trả đối tượng Player cho hàm onCreate(); OnlineActivity giữ đối tượng này trong thuộc tính me.
7. OnlineActivity hiển thị giao diện chơi online cho người chơi.
8. Người chơi nhấn nút Tạo phòng.
9. Hàm onClick() của OnlineActivity gọi hàm createRoom() của MatchController.
10. Hàm createRoom() sinh mã phòng và gọi lớp Match để tạo trận ở trạng thái chờ.
11. Lớp Match trả đối tượng Match cho hàm createRoom().
12. Hàm createRoom() gọi hàm createMatch() của MatchFirebaseDAO.
13. Hàm createMatch() gọi hàm runTransaction() để ghi trận mới lên Firebase.
14. Khi ghi xong, hàm onComplete() của MatchFirebaseDAO gọi hàm onSuccess() của MatchController.
15. MatchController gọi hàm onRoomReady() của OnlineActivity.
16. Hàm onRoomReady() gọi hàm startActivity() mở LobbyActivity.
17. Hàm onCreate() của LobbyActivity gọi hàm waitForOpponent() của MatchController.
18. Hàm waitForOpponent() gọi hàm watchMatch() của MatchFirebaseDAO.
19. LobbyActivity hiển thị mã phòng và trạng thái chờ đối thủ cho người chơi.
20. Khi có đối thủ vào phòng, hàm onDataChange() của MatchFirebaseDAO gọi hàm toMatch() của chính nó.
21. Hàm toMatch() gọi lớp Match để đóng gói trận.
22. Lớp Match trả đối tượng Match cho hàm toMatch().
23. MatchFirebaseDAO gọi hàm onSuccess() của MatchController.
24. MatchController thấy trận đã bắt đầu nên gọi hàm onRoomReady() của LobbyActivity.
25. LobbyActivity hiển thị thông báo trận đấu bắt đầu và tên đối thủ cho người chơi.
26. LobbyActivity gọi hàm startActivity() mở OnlineGameActivity.

![Hình 16: Tuần tự thiết kế Chơi online – Tạo phòng](tuan-tu-thiet-ke-uc05a.png)

*Hình 16: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn A: Tạo phòng.*

- Biểu đồ có 26 thông điệp, khớp 26 bước ở trên.
- Activity không gọi Firebase trực tiếp. Mọi thao tác đi qua MatchController rồi tới MatchFirebaseDAO.
- Kết quả bất đồng bộ đi ngược lại qua callback: onComplete() hoặc onDataChange(), rồi onSuccess(), rồi onRoomReady().
- Bước 13 dùng transaction nên hai người không thể tạo trùng một mã phòng.

### 1.2. Đoạn B: Tìm phòng

1. Người chơi nhấn nút Chơi online trên MainActivity.
2. Hàm onClick() của MainActivity gọi hàm startActivity() mở OnlineActivity.
3. Hàm onCreate() của OnlineActivity gọi hàm getPlayer() của SessionDAO.
4. Hàm getPlayer() gọi lớp Player để đóng gói thông tin.
5. Lớp Player trả về đối tượng Player cho hàm getPlayer().
6. Hàm getPlayer() trả đối tượng Player cho hàm onCreate().
7. OnlineActivity hiển thị giao diện chơi online cho người chơi.
8. Người chơi nhập mã phòng và nhấn nút Vào phòng.
9. Hàm onClick() kiểm tra mã phòng rồi gọi hàm findRoom() của MatchController.
10. Hàm findRoom() gọi hàm joinMatch() của MatchFirebaseDAO.
11. Hàm joinMatch() chạy một transaction trên Firebase. Trong hàm doTransaction(), nó gọi hàm toMatch() của chính nó.
12. Hàm toMatch() gọi lớp Match để đóng gói trận hiện tại.
13. Lớp Match trả đối tượng Match cho hàm toMatch().
14. Hàm doTransaction() gọi hàm canJoin() của Match.
15. Hàm canJoin() trả về đúng: trận đang chờ, mới có một người và người đó không phải người chơi này.
16. Hàm doTransaction() thêm người chơi vào trận và chuyển trận sang trạng thái đang chơi.
17. Khi ghi xong, hàm onComplete() gọi hàm onSuccess() của MatchController.
18. MatchController gọi hàm onRoomReady() của OnlineActivity.
19. Hàm onRoomReady() gọi hàm startActivity() mở LobbyActivity.
20. Hàm onCreate() của LobbyActivity gọi hàm waitForOpponent() của MatchController.
21. Hàm waitForOpponent() thấy trận đã bắt đầu nên gọi ngay hàm onRoomReady() của LobbyActivity.
22. LobbyActivity hiển thị thông báo trận đấu bắt đầu và tên đối thủ cho người chơi.
23. LobbyActivity gọi hàm startActivity() mở OnlineGameActivity.

![Hình 17: Tuần tự thiết kế Chơi online – Tìm phòng](tuan-tu-thiet-ke-uc05b.png)

*Hình 17: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn B: Tìm phòng.*

- Biểu đồ có 23 thông điệp.
- **Bước 14–16** là trọng tâm:
  - Luật vào phòng canJoin() của Match chạy **bên trong transaction** của Firebase.
  - Nếu hai người cùng nhập mã một lúc, Firebase chạy lại transaction cho người đến sau, nên phòng không bao giờ có ba người.
- Hàm canJoin() là hàm Java thuần nên kiểm thử được mà không cần Firebase.

### 1.3. Đoạn C: Thi đấu và kết thúc trận

1. Hàm onCreate() của OnlineGameActivity gọi hàm startGame() của MatchController.
2. Hàm startGame() gọi hàm registerDisconnect() của MatchFirebaseDAO, để khi máy mất kết nối thì người chơi bị tính bỏ trận.
3. Hàm startGame() tạo ProblemDAO theo mã đồng bộ câu hỏi của trận, nên hai máy có cùng dãy câu hỏi.
4. Hàm startGame() gọi lớp GameSession để tạo ván chấm điểm ở chế độ online.
5. Hàm startGame() gọi hàm watchMatch() của MatchFirebaseDAO để theo dõi điểm đối thủ.
6. Hàm startGame() gọi hàm start() của CountDownTimer.
7. Hàm startGame() gọi hàm getCurrentLevel() của GameSession.
8. GameSession trả về mức hiện tại.
9. Hàm startGame() gọi hàm generateProblem() của ProblemDAO.
10. Hàm generateProblem() gọi lớp MathProblem để đóng gói câu hỏi.
11. Lớp MathProblem trả đối tượng MathProblem cho hàm generateProblem().
12. Hàm generateProblem() trả đối tượng MathProblem cho hàm startGame(); hàm startGame() lưu nó làm câu hiện tại.
13. Hàm startGame() trả đối tượng MathProblem cho hàm onCreate().
14. OnlineGameActivity hiển thị điểm, đồng hồ, câu hỏi và bàn phím số cho người chơi.
15. Mỗi giây, CountDownTimer gọi hàm onTick() của MatchController.
16. MatchController gọi hàm onTick() của OnlineGameActivity.
17. OnlineGameActivity hiển thị thời gian còn lại.
18. Người chơi nhập đáp án bằng bàn phím số và nhấn OK.
19. Hàm onClick() của nút OK đọc đáp án và gọi hàm submitAnswer() của MatchController.
20. Hàm submitAnswer() gọi hàm isCorrect() của MathProblem.
21. MathProblem trả về đúng.
22. Hàm submitAnswer() gọi hàm recordCorrectAnswer() của GameSession.
23. GameSession cộng điểm và trả về số điểm được cộng cho hàm submitAnswer().
24. Hàm submitAnswer() gọi hàm updateScore() của MatchFirebaseDAO để máy đối thủ thấy điểm mới.
25. Hàm submitAnswer() trả số điểm được cộng cho hàm onClick().
26. Hàm onClick() gọi hàm nextProblem() của MatchController.
27. Hàm nextProblem() gọi hàm getCurrentLevel() của GameSession.
28. GameSession trả về mức hiện tại.
29. Hàm nextProblem() gọi hàm generateProblem() của ProblemDAO.
30. Hàm generateProblem() gọi lớp MathProblem để đóng gói câu hỏi tiếp theo.
31. Lớp MathProblem trả đối tượng MathProblem cho hàm generateProblem().
32. Hàm generateProblem() trả đối tượng MathProblem cho hàm nextProblem().
33. Hàm nextProblem() trả đối tượng MathProblem cho hàm onClick().
34. OnlineGameActivity gọi hàm showScorePopup() để hiển thị điểm được cộng, điểm mới và câu hỏi mới.
35. Khi đối thủ ghi điểm, hàm onDataChange() của MatchFirebaseDAO gọi hàm toMatch().
36. Hàm toMatch() gọi lớp Match để đóng gói trận mới nhất.
37. Lớp Match trả đối tượng Match cho hàm toMatch().
38. MatchFirebaseDAO gọi hàm onSuccess() của MatchController.
39. MatchController gọi hàm isOver() của Match.
40. Match trả về sai vì trận chưa kết thúc.
41. MatchController gọi hàm onMatchChanged() của OnlineGameActivity.
42. OnlineGameActivity hiển thị điểm đối thủ cho người chơi.

Các bước 15–42 lặp lại đến khi hết giờ. Khi trả lời sai, bước 22 gọi hàm recordWrongAnswer(), không có bước 24, và hàm showScorePopup() hiển thị thông báo trả lời sai.

43. Hết giờ, CountDownTimer gọi hàm onFinish() của MatchController.
44. Hàm onFinish() gọi hàm setTimeUp() của MatchFirebaseDAO.
45. Hàm onFinish() gọi hàm onTimeUp() của OnlineGameActivity.
46. OnlineGameActivity khóa bàn phím số và hiển thị thông báo chờ kết quả.
47. Khi máy đối thủ cũng hết giờ, hàm onDataChange() gọi hàm toMatch().
48. Hàm toMatch() gọi lớp Match để đóng gói trận.
49. Lớp Match trả đối tượng Match cho hàm toMatch().
50. MatchFirebaseDAO gọi hàm onSuccess() của MatchController.
51. MatchController gọi hàm isOver() của Match.
52. Match trả về đúng vì cả hai người chơi đã hết giờ.
53. MatchController gọi hàm finishMatch() của chính nó trong một Thread.
54. Hàm finishMatch() gọi hàm finishMatch() của MatchApiDAO.
55. MatchApiDAO gửi yêu cầu kết thúc trận tới hàm finish() của MatchRestController.
56. Hàm finish() gọi hàm readMatch() của FirebaseAdminDAO.
57. Hàm readMatch() đọc trận từ Firebase và gọi lớp Match để đóng gói.
58. Lớp Match trả đối tượng Match cho hàm readMatch().
59. Hàm readMatch() trả đối tượng Match cho hàm finish().
60. Hàm finish() gọi hàm isOver() của Match để kiểm tra lại ở phía server.
61. Match trả về đúng.
62. Hàm finish() ghi thời điểm kết thúc rồi gọi hàm insert() của MatchDAO.
63. Hàm insert() lưu trận và hai người chơi của trận vào CSDL trong một transaction.
64. Hàm insert() trả kết quả cho hàm finish().
65. Hàm finish() gọi hàm updateStatus() của FirebaseAdminDAO để chuyển trận sang trạng thái đã kết thúc.
66. Hàm finish() trả trận đã lưu dạng JSON cho MatchApiDAO.
67. MatchApiDAO gọi hàm toMatch() để tạo đối tượng Match.
68. Lớp Match trả đối tượng Match cho MatchApiDAO.
69. MatchApiDAO trả đối tượng Match cho hàm finishMatch() của MatchController.
70. Hàm finishMatch() gọi hàm stopWatching() của MatchFirebaseDAO để ngừng theo dõi trận.
71. Hàm finishMatch() gọi hàm onMatchEnd() của OnlineGameActivity trong hàm runOnUiThread().
72. Hàm onMatchEnd() gọi hàm showResult(), hàm showResult() gọi hàm getResult() của Match.
73. Match trả về kết quả trận.
74. OnlineGameActivity hiển thị kết quả và tỉ số cho người chơi.
75. Người chơi nhấn nút Về menu.
76. Hàm onClick() gọi hàm finish(), quay về MainActivity.
77. MainActivity hiển thị giao diện chính cho người chơi.

![Hình 18: Tuần tự thiết kế Chơi online – Thi đấu và kết thúc trận](tuan-tu-thiet-ke-uc05c.png)

*Hình 18: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn C: Thi đấu và kết thúc trận.*

- Biểu đồ có 77 thông điệp.
- Ba phần nghiệp vụ là hàm Java thuần, nằm trong code của nhóm:
  - **Chấm điểm**: hàm isCorrect() của MathProblem và hàm recordCorrectAnswer() của GameSession, chạy trên app.
  - **Điều kiện kết thúc**: hàm isOver() của Match, chạy ở cả app (để biết lúc gọi server) và server (để từ chối yêu cầu sớm).
  - **Thắng thua**: hàm getResult() của Match, tính trên bản trận server đã lưu.
- **Firebase** chỉ chuyển điểm giữa hai máy (bước 24, 35) và báo trạng thái trận.
- **Lời gọi REST** duy nhất là bước 55; phía server đi đúng khuôn Controller → DAO → Entity, không có tầng Service.

---

## 2. Chức năng "Xem lịch sử đấu"

1. Người chơi nhấn nút Lịch sử đấu trên MainActivity.
2. Hàm onClick() của MainActivity gọi hàm startActivity() mở HistoryActivity.
3. Hàm onCreate() của HistoryActivity gọi hàm getPlayer() của SessionDAO.
4. Hàm getPlayer() gọi lớp Player để đóng gói thông tin.
5. Lớp Player trả về đối tượng Player cho hàm getPlayer().
6. Hàm getPlayer() trả đối tượng Player cho hàm onCreate().
7. Hàm onCreate() gọi hàm loadHistory() của chính nó.
8. Hàm loadHistory() mở một Thread và gọi hàm getHistory() của MatchApiDAO.
9. MatchApiDAO gửi yêu cầu lấy lịch sử đấu tới hàm getByPlayer() của MatchRestController.
10. Hàm getByPlayer() gọi hàm findByPlayer() của MatchDAO.
11. Hàm findByPlayer() truy vấn các trận đã kết thúc của người chơi, trận mới nhất xếp trước.
12. Với mỗi dòng kết quả, hàm findByPlayer() gọi lớp Match để đóng gói một trận.
13. Lớp Match trả đối tượng Match cho hàm findByPlayer().
14. Hàm findByPlayer() trả danh sách trận cho hàm getByPlayer().
15. Hàm getByPlayer() trả danh sách trận dạng JSON cho MatchApiDAO.
16. Với mỗi phần tử JSON, hàm getHistory() gọi hàm toMatch() để tạo đối tượng Match.
17. Lớp Match trả đối tượng Match cho hàm getHistory().
18. Hàm getHistory() trả danh sách trận cho hàm loadHistory().
19. Trong hàm runOnUiThread(), hàm loadHistory() tạo HistoryAdapter và gắn vào danh sách.
20. Với mỗi dòng, hàm onBindViewHolder() của HistoryAdapter gọi hàm getResult() của Match.
21. Match trả về kết quả của trận.
22. HistoryActivity hiển thị danh sách trận đấu cho người chơi.
23. Người chơi nhấn nút quay lại.
24. HistoryActivity gọi hàm finish(), quay về MainActivity.
25. MainActivity hiển thị giao diện chính cho người chơi.

![Hình 19: Tuần tự thiết kế Xem lịch sử đấu](tuan-tu-thiet-ke-uc06.png)

*Hình 19: Biểu đồ tuần tự chi tiết chức năng Xem lịch sử đấu.*

- Biểu đồ có 25 thông điệp.
- **Lời gọi REST** duy nhất là bước 9. Server đi theo khuôn MatchRestController → MatchDAO → Match.
- **Lời gọi mạng** chạy trong Thread (bước 8). Phần vẽ giao diện chạy trong hàm runOnUiThread() (bước 19).
- **Kết quả không lưu trong CSDL** mà tính lại ở bước 20–21 bằng hàm getResult() của Match. Dữ liệu lấy từ MySQL nên kết quả trùng với lúc trận kết thúc.
