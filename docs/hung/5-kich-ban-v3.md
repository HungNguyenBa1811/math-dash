# KỊCH BẢN v3 VÀ BIỂU ĐỒ TUẦN TỰ CHI TIẾT — CHƠI ONLINE, XEM LỊCH SỬ ĐẤU

---

## 1. Chức năng "Chơi online"

### 1.1. Đoạn A: Gửi lời thách đấu

1. Người chơi nhấn nút Chơi online trên MainActivity.
2. Hàm onClick() của MainActivity gọi hàm startActivity() mở OnlineActivity.
3. Hàm onCreate() của OnlineActivity gọi hàm getPlayer() của SessionDAO.
4. Hàm getPlayer() gọi lớp Player để đóng gói người chơi đang đăng nhập.
5. Lớp Player trả đối tượng Player cho hàm getPlayer().
6. Hàm getPlayer() trả đối tượng Player cho hàm onCreate().
7. Hàm onCreate() gọi hàm watchOnlinePlayers() của PresenceFirebaseDAO, truyền chính OnlineActivity làm PlayerListCallback.
8. Hàm onCreate() gọi hàm watchIncoming() của ChallengeFirebaseDAO, truyền chính OnlineActivity làm ChallengeCallback, để nhận các lời mời gửi tới người chơi.
9. Hàm onCreate() gọi hàm goOnline() của chính nó.
10. Hàm goOnline() mở một Thread và gọi hàm getById() của PlayerApiDAO để lấy tổng điểm mới nhất.
11. PlayerApiDAO gửi yêu cầu GET /api/players/{id} tới hàm getById() của PlayerRestController.
12. Hàm getById() của PlayerRestController gọi hàm findById() của PlayerDAO.
13. Hàm findById() truy vấn bảng tblPlayer và gọi lớp Player để đóng gói kết quả.
14. Lớp Player trả đối tượng Player cho hàm findById().
15. Hàm findById() trả đối tượng Player cho hàm getById().
16. Hàm getById() gọi hàm getTotalPoints() của PlayerDAO.
17. Hàm getTotalPoints() cộng điểm trận của người chơi qua các trận đã kết thúc và trả về cho hàm getById().
18. Hàm getById() trả người chơi kèm tổng điểm dạng JSON cho PlayerApiDAO.
19. Hàm toPlayer() của PlayerApiDAO gọi lớp Player để đóng gói dữ liệu JSON.
20. Lớp Player trả đối tượng Player cho PlayerApiDAO.
21. PlayerApiDAO trả đối tượng Player cho hàm goOnline().
22. Trong hàm runOnUiThread(), hàm goOnline() của OnlineActivity gọi hàm goOnline() của PresenceFirebaseDAO để ghi người chơi vào danh sách online với trạng thái rỗi.
23. Khi danh sách online thay đổi, hàm onDataChange() của PresenceFirebaseDAO gọi hàm toPlayer() cho từng người chơi.
24. Hàm toPlayer() gọi lớp Player để đóng gói người chơi kèm tổng điểm và trạng thái.
25. Lớp Player trả đối tượng Player cho hàm toPlayer().
26. PresenceFirebaseDAO gọi hàm onPlayersChanged() của PlayerListCallback đã đăng ký ở bước 7, chính là OnlineActivity, truyền danh sách người chơi online.
27. Hàm onPlayersChanged() hiển thị tổng điểm của chính người chơi, rồi gọi hàm setPlayers() của PlayerAdapter với danh sách những người còn lại, xếp theo tổng điểm giảm dần.
28. PlayerAdapter hiển thị danh sách người chơi online gồm tên, tổng điểm, trạng thái.
29. Người chơi bấm vào tên một đối thủ đang rỗi.
30. Hàm onClick() của PlayerAdapter gọi hàm onPlayerClick() của OnlineActivity.
31. Hàm onPlayerClick() thấy đối thủ đang rỗi và người chơi chưa có lời mời nào đang chờ, gọi hàm sendChallenge() của ChallengeFirebaseDAO.
32. Hàm sendChallenge() gọi lớp Challenge để tạo lời mời đang chờ, rồi ghi lời mời lên Firebase.
33. Lớp Challenge trả đối tượng Challenge cho hàm sendChallenge().
34. Hàm sendChallenge() trả đối tượng Challenge cho hàm onPlayerClick(); OnlineActivity giữ lời mời này trong sentChallenge.
35. Hàm onPlayerClick() gọi hàm watchChallenge() của ChallengeFirebaseDAO, truyền chính OnlineActivity làm ChallengeCallback, để theo dõi lời mời vừa gửi.
36. Hàm onPlayerClick() gọi hàm showWaitingDialog(), OnlineActivity hiển thị hộp thoại đang chờ đối thủ trả lời kèm nút Hủy.
37. Khi đối thủ đồng ý, hàm onDataChange() của ChallengeFirebaseDAO gọi hàm toChallenge().
38. Hàm toChallenge() gọi lớp Challenge để đóng gói lời mời mới nhất.
39. Lớp Challenge trả đối tượng Challenge cho hàm toChallenge().
40. ChallengeFirebaseDAO gọi hàm onChallengeChanged() của ChallengeCallback đã đăng ký ở bước 35, chính là OnlineActivity.
41. Hàm onChallengeChanged() thấy lời mời đã được đồng ý, gọi hàm closeSentChallenge() của chính nó.
42. Hàm closeSentChallenge() gọi hàm stopWatchingSent() của ChallengeFirebaseDAO để gỡ theo dõi lời mời, rồi đóng hộp thoại chờ.
43. Hàm onChallengeChanged() gọi hàm openMatch() để mở MatchActivity với mã trận trong lời mời.

![Hình 20: Tuần tự thiết kế Chơi online – Gửi lời thách đấu](tuan-tu-thiet-ke-uc03a.png)

*Hình 20: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn A: Gửi lời thách đấu.*

- Biểu đồ có 43 thông điệp.
- MainActivity chỉ là điểm vào: nút Chơi online mở OnlineActivity (bước 1–2). Mọi việc của chức năng này, từ danh sách online tới lời mời, nằm ở OnlineActivity.
- **Lời gọi REST** duy nhất là bước 11, để lấy tổng điểm mới nhất từ server. Server đi theo khuôn PlayerRestController → PlayerDAO → Player.
- **Firebase** làm 3 việc:
  - Ghi người chơi vào danh sách online (bước 22).
  - Báo khi danh sách thay đổi (bước 23–26).
  - Chuyển lời mời giữa 2 máy (bước 31–40).
- **Chỉ online khi ở màn Chơi online:** người chơi được ghi vào danh sách online khi mở OnlineActivity (bước 22). Khi rời màn này, hàm onDestroy() của OnlineActivity gỡ các theo dõi rồi gọi hàm goOffline() của PresenceFirebaseDAO. Lời mời thách đấu cũng chỉ nhận được trong lúc đó, vì watchIncoming() đăng ký ở bước 8.
- **Bất đồng bộ:** onCreate() đăng ký theo dõi ở bước 7–8 trước, rồi hàm goOnline() mở Thread ở bước 10 và trả về ngay; yêu cầu REST ở bước 11–21 chạy trên Thread đó. watchOnlinePlayers(), watchIncoming(), watchChallenge() là lời đăng ký; kết quả đến sau qua onDataChange(). Bước 37 xảy ra khi đối thủ đồng ý ở đoạn B.
- **Callback:** các FirebaseDAO không biết OnlineActivity, chỉ giữ interface PlayerListCallback, ChallengeCallback được truyền vào lúc đăng ký (bước 7, 8, 35). Bước 26 và 40 gọi qua interface đó; ở chức năng này đối tượng cài đặt là OnlineActivity.
- Bước 41–42 gỡ theo dõi lời mời đã gửi trước khi mở trận, để lần ghi sau của server lên lời mời không gọi lại onChallengeChanged().

### 1.2. Đoạn B: Nhận lời thách đấu

1. Khi có lời mời mới gửi tới người chơi, hàm onDataChange() của ChallengeFirebaseDAO gọi hàm toChallenge().
2. Hàm toChallenge() gọi lớp Challenge để đóng gói lời mời.
3. Lớp Challenge trả đối tượng Challenge cho hàm toChallenge().
4. ChallengeFirebaseDAO gọi hàm onChallengeChanged() của ChallengeCallback đã đăng ký bằng watchIncoming(), chính là OnlineActivity.
5. Hàm onChallengeChanged() thấy lời mời gửi tới người chơi chưa từng hiện, ghi id lời mời vào shownChallenges và gọi hàm showChallengeDialog() của chính nó.
6. OnlineActivity hiển thị hộp thoại lời mời gồm tên, tổng điểm của người mời và 2 nút Đồng ý, Từ chối.
7. Người chơi nhấn Đồng ý.
8. Hàm onClick() của nút Đồng ý gọi hàm acceptChallenge() của OnlineActivity.
9. Hàm acceptChallenge() mở một Thread và gọi hàm createMatch() của MatchApiDAO.
10. MatchApiDAO gửi yêu cầu POST /api/matches kèm lời mời tới hàm create() của MatchRestController.
11. Hàm create() gọi hàm readPresence() của FirebaseAdminDAO cho người mời và người được mời.
12. Hàm readPresence() đọc node presence và gọi lớp Player để đóng gói người chơi kèm trạng thái.
13. Lớp Player trả đối tượng Player cho hàm readPresence().
14. Hàm readPresence() trả đối tượng Player cho hàm create().
15. Hàm create() gọi hàm readChallengeStatus() của FirebaseAdminDAO; lời mời vẫn đang chờ, người mời chưa hủy, và cả 2 người đang online, rỗi.
16. Hàm create() gọi hàm startMatch() của chính nó.
17. Hàm startMatch() gọi lớp Match để tạo trận mới ở trạng thái đang đấu, kèm thời điểm bắt đầu.
18. Lớp Match trả đối tượng Match cho hàm startMatch().
19. Hàm startMatch() gọi hàm generateProblems() của ProblemDAO để sinh đề 10 câu.
20. Với mỗi câu, hàm generateProblems() sinh phép tính ngẫu nhiên và gọi lớp MathProblem để đóng gói kèm 4 phương án.
21. Lớp MathProblem trả đối tượng MathProblem cho hàm generateProblems().
22. Hàm generateProblems() trả danh sách 10 câu hỏi cho hàm startMatch(); hàm startMatch() gán đề và 2 người chơi vào trận.
23. Hàm startMatch() gọi hàm insert() của MatchDAO.
24. Hàm insert() lưu trận, 10 câu hỏi và 2 người chơi trong trận vào CSDL trong một transaction, gán id vừa sinh cho trận.
25. Hàm insert() trả kết quả cho hàm startMatch().
26. Hàm startMatch() gọi hàm writeMatch() của FirebaseAdminDAO để gửi đề, không kèm đáp án, cho cả 2 máy.
27. Hàm startMatch() gọi hàm setStatus() của chính nó với trạng thái bận.
28. Hàm setStatus() gọi hàm updateStatus() của FirebaseAdminDAO cho từng người chơi trong trận.
29. Hàm create() gọi hàm updateChallenge() của FirebaseAdminDAO để đánh dấu lời mời đã được đồng ý, kèm mã trận.
30. Hàm create() gọi hàm hideAnswers() của chính nó để xóa đáp án khỏi các câu hỏi trước khi trả về.
31. Hàm create() trả trận dạng JSON cho MatchApiDAO.
32. Hàm toMatch() của MatchApiDAO gọi lớp Match để đóng gói dữ liệu JSON.
33. Lớp Match trả đối tượng Match cho MatchApiDAO.
34. MatchApiDAO trả đối tượng Match cho hàm acceptChallenge().
35. Trong hàm runOnUiThread(), hàm acceptChallenge() gọi hàm openMatch() để mở MatchActivity với mã trận.

![Hình 21: Tuần tự thiết kế Chơi online – Nhận lời thách đấu](tuan-tu-thiet-ke-uc03b.png)

*Hình 21: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn B: Nhận lời thách đấu.*

- Biểu đồ có 35 thông điệp.
- **Server sinh đề** (bước 19–22) và lưu đề vào MySQL cùng trận (bước 23–25), vì server cần đáp án để chấm bài sau này.
- **Đề không kèm đáp án:** đề gửi qua Firebase không có đáp án (bước 26), và bản trận trả về cho app cũng đã bị hideAnswers() xóa đáp án (bước 30). Người chơi không đọc được đáp án trước khi nộp bài.
- Bước 29 cập nhật lời mời, đây chính là tín hiệu để máy người mời mở MatchActivity ở bước 37 của đoạn A.
- Hàm create() đọc trạng thái online của 2 người (bước 11–14) và trạng thái lời mời (bước 15) trước khi tạo trận, nên không thể có người ở trong 2 trận cùng lúc. Lời mời đã hủy hoặc một người không rỗi thì server ghi lời mời là bị từ chối.

### 1.3. Đoạn C: Thi đấu

1. Hàm onCreate() của MatchActivity gọi hàm getPlayer() của SessionDAO.
2. Hàm getPlayer() đóng gói và trả đối tượng Player cho hàm onCreate().
3. Hàm onCreate() gọi hàm watchMatch() của MatchFirebaseDAO với mã trận, truyền chính MatchActivity làm MatchCallback.
4. Hàm onDataChange() của MatchFirebaseDAO gọi hàm toMatch() của chính nó.
5. Hàm toMatch() gọi lớp Match để đóng gói trận, kèm 10 câu hỏi và 2 người chơi trong trận.
6. Lớp Match trả đối tượng Match cho hàm toMatch().
7. MatchFirebaseDAO gọi hàm onSuccess() của MatchCallback đã đăng ký ở bước 3, chính là MatchActivity.
8. Lần đầu nhận trận có đề, hàm onSuccess() gọi hàm startMatch() của chính nó; hàm startMatch() lưu id đối thủ vào opponentId và lưu 10 câu hỏi của trận vào questions.
9. Hàm startMatch() gọi lớp Answer để tạo bài làm rỗng, chưa chọn phương án, cho từng câu.
10. Lớp Answer trả đối tượng Answer cho hàm startMatch(); hàm startMatch() giữ 10 bài làm trong answers.
11. Hàm startMatch() gọi hàm showQuestion() của chính nó để hiện câu đầu tiên.
12. Hàm showQuestion() gọi hàm getExpression() của MathProblem của câu đang làm.
13. MathProblem trả chuỗi phép tính cho hàm showQuestion().
14. Hàm showQuestion() gọi hàm getAnswerOptions() của MathProblem.
15. MathProblem trả 4 phương án cho hàm showQuestion().
16. Với mỗi ô, hàm showQuestion() gọi hàm optionText() của chính nó để ghép chữ cái A, B, C, D với giá trị phương án; sau đó hàm showQuestion() bật 4 ô và ghi thời điểm câu hiện ra vào shownAt.
17. Hàm startMatch() gọi hàm createTimer() của chính nó để tạo CountDownTimer 60 giây.
18. Hàm startMatch() gọi hàm start() của CountDownTimer.
19. Hàm startMatch() gọi hàm watchOnlinePlayers() của PresenceFirebaseDAO, truyền chính MatchActivity làm PlayerListCallback, để biết đối thủ còn online hay không.
20. MatchActivity hiển thị ô thời gian, trạng thái đối thủ và nút Thoát cho người chơi.
21. Hàm onDataChange() của PresenceFirebaseDAO đóng gói danh sách người chơi online và gọi hàm onPlayersChanged() của PlayerListCallback, chính là MatchActivity; đối thủ vẫn có trong danh sách nên trận tiếp tục.
22. Mỗi giây, CountDownTimer gọi hàm onTick() của MatchActivity để hiển thị thời gian còn lại.
23. MatchActivity hiển thị số thứ tự câu đang làm, phép tính và 4 ô phương án A, B, C, D cho người chơi.
24. Người chơi bấm một ô phương án.
25. Hàm onClick() của MatchActivity gọi hàm chooseOption() của chính nó.
26. Hàm chooseOption() gọi hàm setSelectedOption() của Answer ứng với câu đang làm để ghi phương án đã chọn.
27. Hàm chooseOption() gọi hàm setAnswerTime() của Answer để ghi thời gian làm câu, tính từ shownAt tới lúc bấm.
28. Hàm chooseOption() khóa 4 ô, làm mờ 3 ô không chọn, tăng current, rồi sau NEXT_DELAY gọi hàm showQuestion() của chính nó; khi còn câu, hàm showQuestion() hiện câu tiếp theo như bước 12–16.

Các bước 23–28 lặp lại cho đến câu thứ 10.

29. Sau câu thứ 10, hàm showQuestion() thấy đã làm xong 10 câu, gọi hàm showMessage() của chính nó.
30. MatchActivity thay câu hỏi bằng thông báo đã làm xong 10 câu và hiển thị nút Nộp bài cho người chơi.
31. Người chơi nhấn Nộp bài.
32. Hàm onClick() của MatchActivity gọi hàm submitAnswers() của chính nó.
33. Hàm submitAnswers() khóa nút Nộp bài và gọi hàm showMessage() của chính nó để hiển thị thông báo đang nộp bài.
34. Hàm submitAnswers() mở một Thread và gọi hàm submit() của MatchApiDAO với 10 bài làm.
35. MatchApiDAO gửi yêu cầu POST /api/matches/{id}/submit kèm phương án đã chọn và thời gian làm từng câu tới hàm submit() của MatchRestController.
36. Hàm submit() ghi lại thời điểm nhận bài làm, rồi gọi hàm findMatch() của chính nó.
37. Hàm findMatch() gọi hàm findById() của MatchDAO.
38. Hàm findById() truy vấn trận, đề và 2 người chơi, rồi gọi lớp Match để đóng gói.
39. Lớp Match trả đối tượng Match cho hàm findById().
40. Hàm findById() trả đối tượng Match cho hàm findMatch(), hàm findMatch() trả về cho hàm submit().
41. Hàm submit() gán bài làm nhận được cho người chơi trong trận, rồi gọi hàm countCorrect() của Match để chấm.
42. Hàm countCorrect() gọi hàm setProblem() của Answer để gắn câu thứ i của đề vào bài làm thứ i.
43. Hàm countCorrect() gọi hàm isCorrect() của Answer.
44. Hàm isCorrect() của Answer gọi hàm isCorrect() của MathProblem để so phương án đã chọn với đáp án đúng.
45. MathProblem trả kết quả so sánh cho hàm isCorrect() của Answer.
46. Answer trả đúng / sai cho hàm countCorrect().

Các bước 42–46 lặp lại cho đến câu thứ 10.

47. Hàm countCorrect() trả số câu đúng cho hàm submit().
48. Hàm submit() ghi số câu đúng và thời điểm nộp bài vào người chơi trong trận, rồi gọi hàm updateSubmission() của MatchDAO.
49. Hàm updateSubmission() mở một transaction và gọi hàm updateMatchPlayer() của chính nó để lưu số câu đúng và thời điểm nộp bài vào bảng tblMatchPlayer.
50. Với mỗi câu, hàm updateSubmission() gọi hàm insertAnswer() của chính nó để lưu phương án đã chọn và thời gian làm câu vào bảng tblAnswer.
51. Hàm updateSubmission() xác nhận transaction và trả kết quả cho hàm submit().
52. Hàm submit() gọi hàm writeSubmitted() của FirebaseAdminDAO để máy đối thủ biết người chơi đã nộp bài.
53. Hàm submit() gọi hàm isOver() của Match.
54. Match trả về sai vì đối thủ chưa nộp bài.
55. Hàm submit() gọi hàm hideAnswers() của chính nó để xóa đáp án và bài làm vì trận chưa kết thúc.
56. Hàm submit() trả trận dạng JSON cho MatchApiDAO.
57. Hàm toMatch() của MatchApiDAO gọi lớp Match để đóng gói dữ liệu JSON.
58. Lớp Match trả đối tượng Match cho MatchApiDAO.
59. MatchApiDAO trả đối tượng Match cho hàm submitAnswers().
60. Trong hàm runOnUiThread(), hàm submitAnswers() gọi hàm onSubmitted() của chính nó.
61. Hàm onSubmitted() gọi hàm cancel() của CountDownTimer.
62. Hàm onSubmitted() ẩn nút Nộp bài và gọi hàm showMessage() của chính nó.
63. MatchActivity hiển thị thông báo đã nộp bài, đang chờ đối thủ cho người chơi.
64. Khi đối thủ nộp bài, hàm submit() của MatchRestController, lúc này xử lý yêu cầu từ máy đối thủ, chấm và lưu bài làm như trên rồi gọi hàm isOver() của Match.
65. Match trả về đúng vì cả 2 người chơi đã nộp bài.
66. Hàm submit() gọi hàm endMatch() của chính nó.
67. Hàm endMatch() gọi hàm updateEndedAt() của MatchDAO để ghi thời điểm kết thúc trận.
68. Hàm updateEndedAt() trả kết quả cho hàm endMatch().
69. Hàm endMatch() gọi hàm getTotalPoints() của PlayerDAO cho từng người chơi trong trận.
70. Hàm getTotalPoints() trả tổng điểm mới cho hàm endMatch().
71. Hàm endMatch() gọi hàm updatePoints() của FirebaseAdminDAO để cập nhật tổng điểm trong danh sách online.
72. Hàm submit() gọi hàm writeFinished() của FirebaseAdminDAO để báo trận đã kết thúc cho cả 2 máy.
73. Hàm onDataChange() của MatchFirebaseDAO gọi hàm toMatch().
74. Hàm toMatch() gọi lớp Match để đóng gói trận mới nhất.
75. Lớp Match trả đối tượng Match cho hàm toMatch().
76. MatchFirebaseDAO gọi hàm onSuccess() của MatchCallback, chính là MatchActivity.
77. Hàm onSuccess() thấy trận đã kết thúc, gọi hàm loadResult() của chính nó.
78. Hàm loadResult() mở một Thread và gọi hàm getById() của MatchApiDAO để lấy trận đầy đủ, có đáp án và kết quả chấm.
79. MatchApiDAO gửi yêu cầu GET /api/matches/{id} tới hàm getById() của MatchRestController.
80. Hàm getById() gọi hàm findMatch() của chính nó.
81. Hàm findMatch() gọi hàm findById() của MatchDAO.
82. Hàm findById() truy vấn trận, đề, 2 người chơi và bài làm từng câu, rồi gọi lớp Match để đóng gói.
83. Lớp Match trả đối tượng Match cho hàm findById().
84. Hàm findById() trả đối tượng Match cho hàm findMatch(), hàm findMatch() trả về cho hàm getById().
85. Hàm getById() gọi hàm getTotalPoints() của PlayerDAO cho 2 người chơi và gán vào Player của trận.
86. Hàm getTotalPoints() trả tổng điểm cho hàm getById().
87. Hàm getById() trả trận dạng JSON, kèm đáp án và bài làm vì trận đã kết thúc, cho MatchApiDAO.
88. Hàm toMatch() của MatchApiDAO gọi lớp Match để đóng gói dữ liệu JSON.
89. Lớp Match trả đối tượng Match cho MatchApiDAO.
90. MatchApiDAO trả đối tượng Match cho hàm loadResult().
91. Trong hàm runOnUiThread(), hàm loadResult() gọi hàm showResult() của chính nó.
92. Hàm showResult() gọi hàm getResult() của Match.
93. Match trả về kết quả của người chơi.
94. Hàm showResult() gọi hàm showScores() của chính nó.
95. Hàm showScores() gọi hàm getFinishTime() của Match cho người chơi và đối thủ.
96. Match trả về thời gian làm bài.
97. Hàm showScores() gọi hàm getPoints() của Match.
98. Match trả về điểm trận của người chơi.
99. MatchActivity hiển thị lớp kết quả gồm kết quả, số câu đúng, thời gian làm bài, điểm trận, tổng điểm và câu hỏi chơi tiếp cho người chơi.

![Hình 22: Tuần tự thiết kế Chơi online – Thi đấu](tuan-tu-thiet-ke-uc03c.png)

*Hình 22: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn C: Thi đấu.*

- Biểu đồ có 99 thông điệp.
- Bước 2 lược bỏ thông điệp đóng gói Player bên trong SessionDAO, vì đã vẽ ở đoạn A.
- **Làm bài từng câu:** MatchActivity hiện mỗi lần một câu với 4 ô A, B, C, D (bước 11–16, 23). Người chơi bấm một ô thì hàm chooseOption() ghi phương án đã chọn và thời gian làm câu vào Answer của câu đó (bước 26–27) rồi chuyển sang câu sau; vòng này lặp cho 10 câu (bước 23–28). App không có đáp án nên không báo đúng / sai ngay. NEXT_DELAY khóa 4 ô một lúc sau khi chọn, để bấm đúp không trả lời nhầm câu sau.
- **Server làm trọng tài**, gồm 3 phần nghiệp vụ là hàm Java thuần của lớp thực thể:
  - **Chấm bài:** hàm countCorrect() của Match gắn từng câu của đề vào bài làm tương ứng rồi gọi hàm isCorrect() của Answer; Answer dùng hàm isCorrect() của MathProblem để so phương án đã chọn với đáp án (bước 41–47).
  - **Điều kiện kết thúc:** hàm isOver() của Match (bước 53, 64).
  - **Thắng thua:** hàm getResult() và getPoints() của Match. App gọi 2 hàm này trên bản trận server trả về (bước 92–98).
- **Lưu bài làm từng câu:** hàm updateSubmission() ghi số câu đúng, thời điểm nộp vào tblMatchPlayer và 10 dòng bài làm vào tblAnswer trong cùng một transaction (bước 48–51). Bài làm được lưu để chi tiết trận ở chức năng Xem lịch sử đấu hiện được đúng / sai và thời gian từng câu của mỗi người.
- **Không lộ bài làm giữa trận:** khi trận chưa kết thúc, hàm hideAnswers() xóa đáp án và bài làm của cả 2 người khỏi bản trận trả về (bước 55).
- **Thời gian làm bài** tính theo đồng hồ server: thời điểm server nhận bài làm trừ thời điểm bắt đầu trận. Độ trễ khác nhau giữa 2 máy không làm sai kết quả do đồng hồ máy lệch nhau. Thời gian làm từng câu đo ở app, từ lúc câu hiện ra tới lúc bấm, và chỉ dùng để hiển thị trong chi tiết trận.
- Bước 64–72 diễn ra khi server xử lý yêu cầu nộp bài từ máy đối thủ. Máy này biết trận đã xong nhờ Firebase (bước 73–76).
- **MatchActivity cài đặt 2 callback:** MatchCallback nhận diễn biến trận (bước 3, 7, 76), PlayerListCallback nhận danh sách người chơi online (bước 19, 21). Ở kịch bản chuẩn đối thủ vẫn online nên bước 21 không làm gì thêm; khi đối thủ biến khỏi danh sách giữa trận, hàm onPlayersChanged() gọi forfeit() thay cho đối thủ.
- Các cờ started, submitted, resultLoading của MatchActivity chặn gọi lặp: onSuccess() được gọi lại mỗi lần node trận thay đổi, nhưng startMatch() chỉ chạy ở lần đầu (bước 8), loadResult() chỉ chạy một lần (bước 77).
- Hết 60 giây mà người chơi chưa nộp thì CountDownTimer gọi hàm onFinish() của MatchActivity; hàm onFinish() gọi showMessage() rồi submitAnswers() thay cho bước 31–32, các câu chưa làm được gửi đi là bỏ trống.

### 1.4. Đoạn D: Chơi tiếp

1. Người chơi nhấn Chơi tiếp trên lớp kết quả của MatchActivity.
2. Hàm onClick() của nút Chơi tiếp gọi hàm sendChoice() của chính nó.
3. Hàm sendChoice() khóa 2 nút Chơi tiếp, Dừng; MatchActivity hiển thị thông báo đang chờ đối thủ.
4. Hàm sendChoice() mở một Thread và gọi hàm continueMatch() của MatchApiDAO.
5. MatchApiDAO gửi yêu cầu POST /api/matches/{id}/continue kèm lựa chọn tới hàm continueMatch() của MatchRestController.
6. Hàm continueMatch() gọi hàm findMatch() của chính nó.
7. Hàm findMatch() gọi hàm findById() của MatchDAO.
8. Hàm findById() đóng gói trận và trả đối tượng Match cho hàm findMatch(), hàm findMatch() trả về cho hàm continueMatch().
9. Hàm continueMatch() gọi hàm writeChoice() của FirebaseAdminDAO để ghi lựa chọn của người chơi.
10. Hàm continueMatch() thấy đối thủ chưa chọn, ghi người chơi vào waitingChoices rồi trả trận hiện tại dạng JSON cho MatchApiDAO.
11. Hàm toMatch() của MatchApiDAO gọi lớp Match để đóng gói dữ liệu JSON.
12. Lớp Match trả đối tượng Match cho MatchApiDAO.
13. MatchApiDAO trả đối tượng Match cho hàm sendChoice().
14. Khi đối thủ cũng chọn chơi tiếp, hàm continueMatch(), lúc này xử lý yêu cầu từ máy đối thủ, thấy người chơi đã có trong waitingChoices nên gọi hàm startMatch() của chính nó.
15. Hàm startMatch() gọi lớp Match để tạo trận mới với 2 người chơi cũ.
16. Lớp Match trả đối tượng Match cho hàm startMatch().
17. Hàm startMatch() gọi hàm generateProblems() của ProblemDAO để sinh đề mới.
18. Hàm generateProblems() gọi lớp MathProblem để đóng gói từng câu hỏi.
19. Lớp MathProblem trả đối tượng MathProblem cho hàm generateProblems().
20. Hàm generateProblems() trả danh sách 10 câu hỏi cho hàm startMatch().
21. Hàm startMatch() gọi hàm insert() của MatchDAO để lưu trận mới.
22. Hàm insert() trả kết quả cho hàm startMatch().
23. Hàm startMatch() gọi hàm writeMatch() của FirebaseAdminDAO để gửi đề mới cho cả 2 máy.
24. Hàm startMatch() gọi hàm setStatus() của chính nó với trạng thái bận.
25. Hàm setStatus() gọi hàm updateStatus() của FirebaseAdminDAO cho từng người chơi.
26. Hàm continueMatch() gọi hàm writeNextMatch() của FirebaseAdminDAO để ghi mã trận mới vào trận cũ.
27. Hàm onDataChange() của MatchFirebaseDAO thấy trận cũ đã có mã trận tiếp theo, gọi hàm onNextMatch() của MatchCallback, chính là MatchActivity.
28. Hàm onNextMatch() gọi hàm openNextMatch() của chính nó.
29. Hàm openNextMatch() gọi hàm stopWatching() của MatchFirebaseDAO để ngừng theo dõi trận cũ.
30. Hàm openNextMatch() mở MatchActivity mới với mã trận mới và đóng màn hiện tại; trận mới diễn ra như đoạn C.

![Hình 23: Tuần tự thiết kế Chơi online – Chơi tiếp](tuan-tu-thiet-ke-uc03d.png)

*Hình 23: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn D: Chơi tiếp.*

- Biểu đồ có 30 thông điệp, vẽ cho người chọn chơi tiếp trước.
- Hàm continueMatch() chạy tuần tự (synchronized). Nếu 2 yêu cầu chơi tiếp đến cùng lúc, chỉ yêu cầu đến sau tạo trận mới, nên không sinh 2 trận.
- Trận mới dùng lại hàm startMatch() của đoạn B (bước 14–25), không viết lại logic sinh đề.
- Máy này nhận trận mới qua MatchCallback (bước 27), gỡ theo dõi trận cũ (bước 29), mở MatchActivity mới và đi lại đoạn C.

---

## 2. Chức năng "Xem lịch sử đấu"

### 2.1. Đoạn A: Xem trận của tôi và chi tiết trận

1. Người chơi nhấn biểu tượng Lịch sử đấu trên OnlineActivity.
2. Hàm onClick() của OnlineActivity gọi hàm startActivity() mở HistoryActivity.
3. Hàm onCreate() của HistoryActivity gọi hàm getPlayer() của SessionDAO.
4. Hàm getPlayer() trả đối tượng Player đang đăng nhập cho hàm onCreate().
5. Hàm onCreate() gọi hàm loadHistory() của chính nó; Intent không kèm EXTRA_TAB nên màn mở ở thẻ Trận của tôi.
6. Hàm loadHistory() mở một Thread và gọi hàm getHistory() của MatchApiDAO.
7. MatchApiDAO gửi yêu cầu GET /api/matches?playerId={id} tới hàm getHistory() của MatchRestController.
8. Hàm getHistory() của MatchRestController gọi hàm findByPlayer() của MatchDAO.
9. Hàm findByPlayer() truy vấn các trận đã kết thúc mà người chơi tham gia, kèm 2 người chơi, mới nhất đứng đầu, và gọi lớp Match để đóng gói từng trận.
10. Lớp Match trả đối tượng Match cho hàm findByPlayer().
11. Hàm findByPlayer() trả danh sách trận cho hàm getHistory().
12. Hàm getHistory() của MatchRestController trả danh sách trận dạng JSON cho MatchApiDAO.
13. Với mỗi phần tử JSON, hàm toMatch() của MatchApiDAO gọi lớp Match để đóng gói.
14. Lớp Match trả đối tượng Match cho MatchApiDAO.
15. MatchApiDAO trả danh sách trận cho hàm loadHistory().
16. Trong hàm runOnUiThread(), hàm loadHistory() gọi hàm showSummary() của chính nó.
17. Hàm showSummary() gọi hàm getResult() của Match cho từng trận để đếm số trận thắng, hòa, thua.
18. Match trả về kết quả trận của người chơi.
19. Hàm showSummary() gọi hàm setData() của PieChartView với số trận thắng, hòa, thua.
20. Hàm onDraw() của PieChartView vẽ biểu đồ tròn tỉ lệ thắng, hòa, thua cho người chơi.
21. Hàm loadHistory() tạo MatchHistoryAdapter với danh sách trận và id của người chơi, rồi gắn vào danh sách.
22. Hàm onBindViewHolder() của MatchHistoryAdapter gọi hàm getOpponent() của Match.
23. Match trả về đối thủ trong trận.
24. Hàm onBindViewHolder() gọi hàm getResult() của Match.
25. Match trả về kết quả trận.
26. Hàm onBindViewHolder() gọi hàm getPoints() của Match.
27. Match trả về điểm trận.
28. MatchHistoryAdapter hiển thị danh sách trận cho người chơi.
29. Người chơi bấm vào một trận trong danh sách.
30. MatchHistoryAdapter gọi hàm onMatchClick() của HistoryActivity.
31. Hàm onMatchClick() gọi hàm startActivity() mở MatchDetailActivity, kèm mã trận.
32. Hàm onCreate() của MatchDetailActivity gọi hàm getPlayer() của SessionDAO.
33. Hàm getPlayer() trả đối tượng Player cho hàm onCreate().
34. Hàm onCreate() gọi hàm loadMatch() của chính nó.
35. Hàm loadMatch() mở một Thread và gọi hàm getById() của MatchApiDAO.
36. MatchApiDAO gửi yêu cầu GET /api/matches/{id} tới hàm getById() của MatchRestController.
37. Hàm getById() gọi hàm findMatch() của chính nó.
38. Hàm findMatch() gọi hàm findById() của MatchDAO.
39. Hàm findById() truy vấn trận, đề, 2 người chơi và gọi lớp Match để đóng gói.
40. Lớp Match trả đối tượng Match cho hàm findById().
41. Với mỗi người chơi trong trận, hàm findById() gọi hàm findAnswers() của chính nó để đọc bài làm từng câu ở bảng tblAnswer theo thứ tự câu và gắn câu hỏi tương ứng vào từng bài làm.
42. Hàm findById() trả đối tượng Match cho hàm findMatch(), hàm findMatch() trả về cho hàm getById().
43. Hàm getById() gọi hàm getTotalPoints() của PlayerDAO cho 2 người chơi và gán vào Player của trận.
44. Hàm getTotalPoints() trả tổng điểm cho hàm getById().
45. Hàm getById() trả trận dạng JSON, kèm đáp án đúng và bài làm từng câu của 2 người vì trận đã kết thúc, cho MatchApiDAO.
46. Hàm toMatch() của MatchApiDAO gọi lớp Match để đóng gói dữ liệu JSON, kèm bài làm từng câu của 2 người chơi.
47. Lớp Match trả đối tượng Match cho MatchApiDAO.
48. MatchApiDAO trả đối tượng Match cho hàm loadMatch().
49. Trong hàm runOnUiThread(), hàm loadMatch() gọi hàm showMatch() của chính nó.
50. Hàm showMatch() gọi hàm getOpponent() của Match.
51. Match trả về đối thủ trong trận.
52. Hàm showMatch() gọi hàm getResult() của Match.
53. Match trả về kết quả trận của người chơi.
54. Hàm showMatch() gọi hàm showResult() của chính nó để hiển thị kết quả với màu tương ứng.
55. Hàm showMatch() gọi hàm getPoints() của Match.
56. Match trả về điểm trận.
57. Với người chơi và đối thủ, hàm showMatch() gọi hàm stats() của chính nó.
58. Hàm stats() gọi hàm getFinishTime() của Match.
59. Match trả về thời gian làm bài.
60. Hàm showMatch() tạo ProblemAdapter với 10 câu hỏi của trận và bài làm từng câu của người chơi, của đối thủ, rồi gắn vào danh sách.
61. Hàm onBindViewHolder() của ProblemAdapter gọi hàm getExpression() của MathProblem của câu đó.
62. MathProblem trả về chuỗi phép tính; hàm onBindViewHolder() ghép đáp án đúng vào phép tính.
63. Hàm onBindViewHolder() gọi hàm answerAt() của chính nó để lấy bài làm câu đó của người chơi và của đối thủ.
64. Với từng bài làm, hàm onBindViewHolder() gọi hàm showAnswer() của chính nó.
65. Hàm showAnswer() gọi hàm isAnswered() của Answer.
66. Answer trả về câu đó đã được làm.
67. Hàm showAnswer() gọi hàm isCorrect() của Answer.
68. Hàm isCorrect() của Answer gọi hàm isCorrect() của MathProblem để so phương án đã chọn với đáp án đúng.
69. MathProblem trả kết quả so sánh cho hàm isCorrect() của Answer.
70. Answer trả đúng / sai cho hàm showAnswer().

Các bước 61–70 lặp lại cho đến câu thứ 10.

71. MatchDetailActivity hiển thị cho người chơi kết quả, điểm trận, số câu đúng và thời gian làm bài của 2 bên, và 10 câu, mỗi câu gồm phép tính kèm đáp án đúng, với mỗi người là đúng / sai, phương án đã chọn và thời gian làm câu.
72. Người chơi nhấn Back.
73. MatchDetailActivity gọi hàm finish(), quay về HistoryActivity.
74. HistoryActivity hiển thị lại danh sách trận cho người chơi.

![Hình 24: Tuần tự thiết kế Xem lịch sử đấu – Xem trận của tôi](tuan-tu-thiet-ke-uc04a.png)

*Hình 24: Biểu đồ tuần tự chi tiết chức năng Xem lịch sử đấu, đoạn A: Xem trận của tôi và chi tiết trận.*

- Biểu đồ có 74 thông điệp.
- Bước 3–4 và 32–33 lược bỏ thông điệp đóng gói Player bên trong SessionDAO, vì đã vẽ ở chức năng Chơi online.
- **Có 2 lời gọi REST:** bước 7 lấy danh sách trận, bước 36 lấy một trận đầy đủ. Server đi theo khuôn MatchRestController → MatchDAO → Match.
- **Danh sách trận không kèm đề** (bước 9) để mỗi lần mở lịch sử chỉ đọc 2 bảng tblMatch, tblMatchPlayer. Đề, đáp án và bài làm từng câu chỉ được đọc khi người chơi mở một trận (bước 39–41).
- **Biểu đồ tròn và từng dòng trận** đều dựa trên getResult(), getPoints(), getOpponent() của Match (bước 17, 22–27). Đây là cùng luật dùng khi kết thúc trận, nên lịch sử luôn khớp với kết quả đã báo cho 2 người.
- **Từng câu của chi tiết trận** (bước 61–70): ProblemAdapter lấy bài làm câu đó của mỗi người bằng answerAt(), rồi showAnswer() hỏi Answer: isAnswered() để biết câu bỏ trống, isCorrect() để tô đúng / sai. Đúng / sai không lưu trong CSDL mà tính lại từ phương án đã chọn và đáp án đúng, bằng cùng hàm isCorrect() của MathProblem mà server dùng khi chấm. Người bỏ trận trước khi nộp không có bài làm, answerAt() không trả bài làm nào và ô đó hiện dấu gạch ngang.
- Lời gọi mạng chạy trong Thread (bước 6, 35). Phần vẽ giao diện chạy trong hàm runOnUiThread() (bước 16, 49).

### 2.2. Đoạn B: Xem bảng xếp hạng

1. Người chơi nhấn biểu tượng Bảng xếp hạng trên OnlineActivity.
2. Hàm onClick() của OnlineActivity gọi hàm startActivity() mở HistoryActivity, kèm EXTRA_TAB bằng TAB_RANKING.
3. Hàm onCreate() của HistoryActivity gọi hàm getPlayer() của SessionDAO.
4. Hàm getPlayer() trả đối tượng Player đang đăng nhập cho hàm onCreate().
5. Hàm onCreate() gọi hàm loadHistory() của chính nó để tải sẵn thẻ Trận của tôi như bước 6–28 của đoạn A.
6. Hàm onCreate() thấy Intent có EXTRA_TAB bằng TAB_RANKING, gọi hàm selectTab() của TabLayout để chọn thẻ Bảng xếp hạng.
7. TabLayout gọi hàm onTabSelected() của HistoryActivity.
8. Hàm onTabSelected() ẩn khối Trận của tôi, hiện danh sách xếp hạng và, vì chưa tải lần nào, gọi hàm loadRanking() của chính nó.
9. Hàm loadRanking() ghi rankingLoaded, mở một Thread và gọi hàm getRanking() của PlayerApiDAO.
10. PlayerApiDAO gửi yêu cầu GET /api/players/ranking tới hàm getRanking() của PlayerRestController.
11. Hàm getRanking() của PlayerRestController gọi hàm getRanking() của PlayerDAO.
12. Hàm getRanking() của PlayerDAO gọi hàm findAllPlayers() của chính nó.
13. Hàm findAllPlayers() truy vấn các tài khoản có vai trò người chơi và gọi lớp Player để đóng gói từng người.
14. Lớp Player trả đối tượng Player cho hàm findAllPlayers().
15. Hàm getRanking() gọi hàm findFinished() của MatchDAO.
16. Hàm findFinished() truy vấn các trận đã kết thúc kèm 2 người chơi, và gọi lớp Match để đóng gói từng trận.
17. Lớp Match trả đối tượng Match cho hàm findFinished().
18. Hàm findFinished() trả danh sách trận cho hàm getRanking().
19. Hàm getRanking() gọi hàm rank() của chính nó với danh sách người chơi và danh sách trận.
20. Với mỗi trận, hàm rank() gọi hàm addMatch() của chính nó.
21. Hàm addMatch() gọi hàm getPoints() của Match để cộng điểm trận vào tổng điểm của 2 người.
22. Match trả về điểm trận.
23. Hàm addMatch() gọi hàm getFinishTime() của Match.
24. Match trả về thời gian làm bài.
25. Hàm addMatch() gọi hàm getResult() của Match để biết người thắng; trận thắng thì ghi thời gian làm bài vào danh sách thời gian thắng.
26. Match trả về kết quả trận.
27. Hàm rank() tính trung bình điểm đối thủ bằng hàm averagePoints(), trung bình thời gian thắng bằng hàm averageSeconds(), rồi sắp xếp theo 3 tiêu chí.
28. Hàm getRanking() của PlayerDAO trả danh sách người chơi đã xếp hạng cho PlayerRestController.
29. Hàm getRanking() của PlayerRestController trả danh sách dạng JSON cho PlayerApiDAO.
30. Với mỗi phần tử JSON, hàm toPlayer() của PlayerApiDAO gọi lớp Player để đóng gói.
31. Lớp Player trả đối tượng Player cho PlayerApiDAO.
32. PlayerApiDAO trả danh sách người chơi cho hàm loadRanking().
33. Trong hàm runOnUiThread(), hàm loadRanking() tạo RankingAdapter với danh sách và id của người xem, rồi gắn vào danh sách xếp hạng.
34. RankingAdapter hiển thị bảng xếp hạng, dòng của người xem được tô nổi bật.
35. Người chơi nhấn Back.
36. HistoryActivity gọi hàm finish(), quay về OnlineActivity.
37. OnlineActivity hiển thị lại màn Chơi online cho người chơi.

![Hình 25: Tuần tự thiết kế Xem lịch sử đấu – Xem bảng xếp hạng](tuan-tu-thiet-ke-uc04b.png)

*Hình 25: Biểu đồ tuần tự chi tiết chức năng Xem lịch sử đấu, đoạn B: Xem bảng xếp hạng.*

- Biểu đồ có 37 thông điệp.
- Bước 4 lược bỏ thông điệp đóng gói Player bên trong SessionDAO, vì đã vẽ ở chức năng Chơi online.
- **Lời gọi REST** của bảng xếp hạng là bước 10. Server đi theo khuôn PlayerRestController → PlayerDAO → MatchDAO → Match, Player. Danh sách trận cho thẻ Trận của tôi vẫn được tải ở nền bằng loadHistory() (bước 5) như đoạn A.
- **Mở thẳng thẻ Bảng xếp hạng:** biểu tượng Bảng xếp hạng gửi EXTRA_TAB trong Intent (bước 2), HistoryActivity chọn sẵn thẻ bằng selectTab() và TabLayout gọi lại onTabSelected() (bước 6–8), giống như khi người chơi tự chọn thẻ.
- **Các tiêu chí không lưu trong CSDL** mà được tính lại ở bước 19–27 bằng getPoints(), getResult(), getFinishTime() của Match. Bảng xếp hạng vì vậy luôn khớp với kết quả các trận.
- rank() là hàm static, không truy cập CSDL: nhận danh sách người chơi và danh sách trận đã đọc ở bước 12–18 rồi chỉ tính toán, nên kiểm thử được bằng unit test.
- Bảng xếp hạng là một thẻ của HistoryActivity nên không cần Activity riêng; chỉ lần chọn thẻ đầu tiên mới gọi server, các lần sau dùng lại danh sách đã tải.
