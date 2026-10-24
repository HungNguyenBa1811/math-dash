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
9. Hàm onClick() của OnlineActivity gọi hàm createMatch() của MatchFirebaseDAO.
10. Hàm createMatch() gọi hàm newMatch() của chính nó. Hàm newMatch() sinh mã phòng 6 chữ số và mã đồng bộ câu hỏi, rồi gọi lớp Match để tạo trận ở trạng thái chờ.
11. Lớp Match trả đối tượng Match cho hàm createMatch().
12. Hàm createMatch() gọi hàm runTransaction() để ghi trận mới lên Firebase.
13. Khi ghi xong, hàm onComplete() của MatchFirebaseDAO gọi hàm onSuccess() của OnlineActivity.
14. Hàm onSuccess() gọi hàm startActivity() mở LobbyActivity.
15. Hàm onCreate() của LobbyActivity gọi hàm watchMatch() của MatchFirebaseDAO.
16. LobbyActivity hiển thị mã phòng và trạng thái chờ đối thủ cho người chơi.
17. Khi có đối thủ vào phòng, hàm onDataChange() của MatchFirebaseDAO gọi hàm toMatch() của chính nó.
18. Hàm toMatch() gọi lớp Match để đóng gói trận.
19. Lớp Match trả đối tượng Match cho hàm toMatch().
20. MatchFirebaseDAO gọi hàm onSuccess() của LobbyActivity.
21. Hàm onSuccess() thấy trận đã bắt đầu nên gọi hàm stopWatching() của MatchFirebaseDAO.
22. LobbyActivity hiển thị thông báo trận đấu bắt đầu và tên đối thủ cho người chơi.
23. LobbyActivity gọi hàm startActivity() mở OnlineGameActivity.

![Hình 16: Tuần tự thiết kế Chơi online – Tạo phòng](tuan-tu-thiet-ke-uc05a.png)

*Hình 16: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn A: Tạo phòng.*

- Biểu đồ có 23 thông điệp, khớp 23 bước ở trên.
- Activity gọi thẳng MatchFirebaseDAO; chỉ MatchFirebaseDAO đọc / ghi Firebase.
- Kết quả bất đồng bộ đi ngược lại qua callback: onComplete() hoặc onDataChange() của MatchFirebaseDAO gọi onSuccess() của Activity, vì Activity cài đặt interface MatchCallback.
- Bước 12 dùng transaction nên hai người không thể tạo trùng một mã phòng.

### 1.2. Đoạn B: Tìm phòng

1. Người chơi nhấn nút Chơi online trên MainActivity.
2. Hàm onClick() của MainActivity gọi hàm startActivity() mở OnlineActivity.
3. Hàm onCreate() của OnlineActivity gọi hàm getPlayer() của SessionDAO.
4. Hàm getPlayer() gọi lớp Player để đóng gói thông tin.
5. Lớp Player trả về đối tượng Player cho hàm getPlayer().
6. Hàm getPlayer() trả đối tượng Player cho hàm onCreate().
7. OnlineActivity hiển thị giao diện chơi online cho người chơi.
8. Người chơi nhập mã phòng và nhấn nút Vào phòng.
9. Hàm onClick() kiểm tra mã phòng rồi gọi hàm joinMatch() của MatchFirebaseDAO.
10. Hàm joinMatch() chạy một transaction trên Firebase. Trong hàm doTransaction(), nó gọi hàm toMatch() của chính nó.
11. Hàm toMatch() gọi lớp Match để đóng gói trận hiện tại.
12. Lớp Match trả đối tượng Match cho hàm toMatch().
13. Hàm doTransaction() gọi hàm canJoin() của Match.
14. Hàm canJoin() trả về đúng: trận đang chờ, mới có một người và người đó không phải người chơi này.
15. Hàm doTransaction() thêm người chơi vào trận và chuyển trận sang trạng thái đang chơi.
16. Khi ghi xong, hàm onComplete() gọi hàm onSuccess() của OnlineActivity.
17. Hàm onSuccess() gọi hàm startActivity() mở LobbyActivity.
18. Hàm onCreate() của LobbyActivity gọi hàm watchMatch() của MatchFirebaseDAO.
19. Hàm onDataChange() của MatchFirebaseDAO gọi hàm toMatch() của chính nó.
20. Hàm toMatch() gọi lớp Match để đóng gói trận.
21. Lớp Match trả đối tượng Match cho hàm toMatch().
22. MatchFirebaseDAO gọi hàm onSuccess() của LobbyActivity.
23. Hàm onSuccess() thấy trận đã bắt đầu nên gọi hàm stopWatching() của MatchFirebaseDAO.
24. LobbyActivity hiển thị thông báo trận đấu bắt đầu và tên đối thủ cho người chơi.
25. LobbyActivity gọi hàm startActivity() mở OnlineGameActivity.

![Hình 17: Tuần tự thiết kế Chơi online – Tìm phòng](tuan-tu-thiet-ke-uc05b.png)

*Hình 17: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn B: Tìm phòng.*

- Biểu đồ có 25 thông điệp.
- **Bước 13–15** là trọng tâm:
  - Luật vào phòng canJoin() của Match chạy **bên trong transaction** của Firebase.
  - Nếu hai người cùng nhập mã một lúc, Firebase chạy lại transaction cho người đến sau, nên phòng không bao giờ có ba người.
- Hàm canJoin() là hàm Java thuần nên kiểm thử được mà không cần Firebase.
- Bước 19: trận đã ở trạng thái đang chơi nên lần đọc đầu tiên đã báo ngay, người tìm phòng không phải chờ.

### 1.3. Đoạn C: Thi đấu và kết thúc trận

1. Hàm onCreate() của OnlineGameActivity gọi hàm watchMatch() của MatchFirebaseDAO để theo dõi trận.
2. Hàm onDataChange() của MatchFirebaseDAO gọi hàm toMatch() của chính nó.
3. Hàm toMatch() gọi lớp Match để đóng gói trận.
4. Lớp Match trả đối tượng Match cho hàm toMatch().
5. MatchFirebaseDAO gọi hàm onSuccess() của OnlineGameActivity.
6. Lần đầu nhận trận, hàm onSuccess() gọi hàm startGame() của chính nó.
7. Hàm startGame() gọi hàm registerDisconnect() của MatchFirebaseDAO, để khi máy mất kết nối thì người chơi bị tính bỏ trận.
8. Hàm startGame() tạo ProblemDAO theo mã đồng bộ câu hỏi của trận, nên hai máy có cùng dãy câu hỏi.
9. Hàm startGame() gọi lớp GameSession để tạo ván chấm điểm ở chế độ online.
10. Hàm startGame() gọi hàm start() của CountDownTimer.
11. Hàm startGame() gọi hàm nextProblem() của chính nó.
12. Hàm nextProblem() gọi hàm getCurrentLevel() của GameSession.
13. GameSession trả về mức hiện tại.
14. Hàm nextProblem() gọi hàm generateProblem() của ProblemDAO.
15. Hàm generateProblem() gọi lớp MathProblem để đóng gói câu hỏi.
16. Lớp MathProblem trả đối tượng MathProblem cho hàm generateProblem().
17. Hàm generateProblem() trả đối tượng MathProblem cho hàm nextProblem(); hàm nextProblem() lưu nó làm câu hiện tại.
18. Hàm onSuccess() gọi hàm isOver() của Match.
19. Match trả về sai vì trận mới bắt đầu.
20. OnlineGameActivity hiển thị điểm, đồng hồ, câu hỏi và bàn phím số cho người chơi.
21. Mỗi giây, CountDownTimer gọi hàm onTick() của OnlineGameActivity.
22. OnlineGameActivity hiển thị thời gian còn lại.
23. Người chơi nhập đáp án bằng bàn phím số và nhấn OK.
24. Hàm onClick() của nút OK đọc đáp án và gọi hàm submitAnswer() của chính nó.
25. Hàm submitAnswer() gọi hàm isCorrect() của MathProblem.
26. MathProblem trả về đúng.
27. Hàm submitAnswer() gọi hàm recordCorrectAnswer() của GameSession.
28. GameSession cộng điểm và trả về số điểm được cộng cho hàm submitAnswer().
29. Hàm submitAnswer() gọi hàm updateScore() của MatchFirebaseDAO để máy đối thủ thấy điểm mới.
30. Hàm submitAnswer() gọi hàm nextProblem() của chính nó.
31. Hàm nextProblem() gọi hàm getCurrentLevel() của GameSession.
32. GameSession trả về mức hiện tại.
33. Hàm nextProblem() gọi hàm generateProblem() của ProblemDAO.
34. Hàm generateProblem() gọi lớp MathProblem để đóng gói câu hỏi tiếp theo.
35. Lớp MathProblem trả đối tượng MathProblem cho hàm generateProblem().
36. Hàm generateProblem() trả đối tượng MathProblem cho hàm nextProblem().
37. OnlineGameActivity gọi hàm showScorePopup() để hiển thị điểm được cộng, điểm mới và câu hỏi mới.
38. Khi đối thủ ghi điểm, hàm onDataChange() của MatchFirebaseDAO gọi hàm toMatch().
39. Hàm toMatch() gọi lớp Match để đóng gói trận mới nhất.
40. Lớp Match trả đối tượng Match cho hàm toMatch().
41. MatchFirebaseDAO gọi hàm onSuccess() của OnlineGameActivity.
42. Hàm onSuccess() gọi hàm isOver() của Match.
43. Match trả về sai vì trận chưa kết thúc.
44. OnlineGameActivity gọi hàm showOpponentScore() để hiển thị điểm đối thủ cho người chơi.

Các bước 21–44 lặp lại đến khi hết giờ. Khi trả lời sai, bước 27 gọi hàm recordWrongAnswer(), không có bước 29, và hàm showScorePopup() hiển thị thông báo trả lời sai.

45. Hết giờ, CountDownTimer gọi hàm onFinish() của OnlineGameActivity.
46. Hàm onFinish() gọi hàm setTimeUp() của MatchFirebaseDAO.
47. OnlineGameActivity gọi hàm lockKeypad() để khóa bàn phím số và hiển thị thông báo chờ kết quả.
48. Khi máy đối thủ cũng hết giờ, hàm onDataChange() gọi hàm toMatch().
49. Hàm toMatch() gọi lớp Match để đóng gói trận.
50. Lớp Match trả đối tượng Match cho hàm toMatch().
51. MatchFirebaseDAO gọi hàm onSuccess() của OnlineGameActivity.
52. Hàm onSuccess() gọi hàm isOver() của Match.
53. Match trả về đúng vì cả hai người chơi đã hết giờ.
54. Hàm onSuccess() gọi hàm finishMatch() của chính nó.
55. Hàm finishMatch() gọi hàm stopGame(): dừng đồng hồ và gọi hàm stopWatching() của MatchFirebaseDAO để ngừng theo dõi trận.
56. Hàm finishMatch() mở một Thread và gọi hàm finishMatch() của MatchApiDAO.
57. MatchApiDAO gửi yêu cầu kết thúc trận tới hàm finish() của MatchRestController.
58. Hàm finish() gọi hàm readMatch() của FirebaseAdminDAO.
59. Hàm readMatch() đọc trận từ Firebase và gọi lớp Match để đóng gói.
60. Lớp Match trả đối tượng Match cho hàm readMatch().
61. Hàm readMatch() trả đối tượng Match cho hàm finish().
62. Hàm finish() gọi hàm isOver() của Match để kiểm tra lại ở phía server.
63. Match trả về đúng.
64. Hàm finish() ghi thời điểm kết thúc rồi gọi hàm insert() của MatchDAO.
65. Hàm insert() lưu trận và hai người chơi của trận vào CSDL trong một transaction.
66. Hàm insert() trả kết quả cho hàm finish().
67. Hàm finish() gọi hàm updateStatus() của FirebaseAdminDAO để chuyển trận sang trạng thái đã kết thúc.
68. Hàm finish() trả trận đã lưu dạng JSON cho MatchApiDAO.
69. MatchApiDAO gọi hàm toMatch() để tạo đối tượng Match.
70. Lớp Match trả đối tượng Match cho MatchApiDAO.
71. MatchApiDAO trả đối tượng Match cho hàm finishMatch() của OnlineGameActivity.
72. Trong hàm runOnUiThread(), hàm finishMatch() gọi hàm showResult() của chính nó.
73. Hàm showResult() gọi hàm getResult() của Match.
74. Match trả về kết quả trận.
75. OnlineGameActivity hiển thị kết quả và tỉ số cho người chơi.
76. Người chơi nhấn nút Về menu.
77. Hàm onClick() gọi hàm finish(), quay về MainActivity.
78. MainActivity hiển thị giao diện chính cho người chơi.

![Hình 18: Tuần tự thiết kế Chơi online – Thi đấu và kết thúc trận](tuan-tu-thiet-ke-uc05c.png)

*Hình 18: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn C: Thi đấu và kết thúc trận.*

- Biểu đồ có 78 thông điệp.
- Ba phần nghiệp vụ là hàm Java thuần của lớp thực thể, nằm trong code của nhóm:
  - **Chấm điểm**: hàm isCorrect() của MathProblem và hàm recordCorrectAnswer() của GameSession, chạy trên app.
  - **Điều kiện kết thúc**: hàm isOver() của Match, chạy ở cả app (để biết lúc gọi server) và server (để từ chối yêu cầu sớm).
  - **Thắng thua**: hàm getResult() của Match, tính trên bản trận server đã lưu.
- **Firebase** chỉ chuyển điểm giữa hai máy (bước 29, 38) và báo trạng thái trận.
- **Lời gọi REST** duy nhất là bước 57. Nếu lần ghi hết giờ của máy này chưa tới Firebase khi server đọc, server trả 409 và MatchApiDAO gửi lại, tối đa 3 lần. Phía server đi đúng khuôn RestController → DAO → Entity, không có tầng Service.

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
