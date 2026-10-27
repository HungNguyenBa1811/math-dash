# KỊCH BẢN v3 VÀ BIỂU ĐỒ TUẦN TỰ CHI TIẾT — CHƠI ONLINE, XEM LỊCH SỬ ĐẤU, QUẢN LÝ BẠN BÈ

---

## 1. Chức năng "Chơi online"

### 1.1. Đoạn A: Gửi lời thách đấu

1. Người chơi đăng nhập thành công, MainActivity được mở.
2. Hàm onCreate() của MainActivity gọi hàm getPlayer() của SessionDAO.
3. Hàm getPlayer() gọi lớp Player để đóng gói người chơi đang đăng nhập.
4. Lớp Player trả đối tượng Player cho hàm getPlayer().
5. Hàm getPlayer() trả đối tượng Player cho hàm onCreate().
6. Hàm onCreate() gọi hàm goOnline() của chính nó.
7. Hàm goOnline() mở một Thread và gọi hàm getById() của PlayerApiDAO để lấy tổng điểm mới nhất.
8. Hàm onCreate() gọi hàm watchOnlinePlayers() của PresenceFirebaseDAO, truyền chính MainActivity làm PlayerListCallback.
9. Hàm onCreate() gọi hàm watchIncoming() của ChallengeFirebaseDAO, truyền chính MainActivity làm ChallengeCallback, để nhận các lời mời gửi tới người chơi.
10. PlayerApiDAO gửi yêu cầu GET /api/players/{id} tới hàm getById() của PlayerRestController.
11. Hàm getById() của PlayerRestController gọi hàm findById() của PlayerDAO.
12. Hàm findById() truy vấn bảng tblPlayer và gọi lớp Player để đóng gói kết quả.
13. Lớp Player trả đối tượng Player cho hàm findById().
14. Hàm findById() trả đối tượng Player cho hàm getById().
15. Hàm getById() gọi hàm getTotalPoints() của PlayerDAO.
16. Hàm getTotalPoints() cộng điểm trận của người chơi qua các trận đã kết thúc và trả về cho hàm getById().
17. Hàm getById() trả người chơi kèm tổng điểm dạng JSON cho PlayerApiDAO.
18. Hàm toPlayer() của PlayerApiDAO gọi lớp Player để đóng gói dữ liệu JSON.
19. Lớp Player trả đối tượng Player cho PlayerApiDAO.
20. PlayerApiDAO trả đối tượng Player cho hàm goOnline().
21. Trong hàm runOnUiThread(), hàm goOnline() của MainActivity gọi hàm goOnline() của PresenceFirebaseDAO để ghi người chơi vào danh sách online với trạng thái rỗi.
22. Khi danh sách online thay đổi, hàm onDataChange() của PresenceFirebaseDAO gọi hàm toPlayer() cho từng người chơi.
23. Hàm toPlayer() gọi lớp Player để đóng gói người chơi kèm tổng điểm và trạng thái.
24. Lớp Player trả đối tượng Player cho hàm toPlayer().
25. PresenceFirebaseDAO gọi hàm onPlayersChanged() của PlayerListCallback đã đăng ký ở bước 8, chính là MainActivity, truyền danh sách người chơi online.
26. Hàm onPlayersChanged() hiển thị tổng điểm của chính người chơi, rồi gọi hàm setPlayers() của PlayerAdapter với danh sách những người còn lại.
27. PlayerAdapter hiển thị danh sách người chơi online gồm tên, tổng điểm, trạng thái.
28. Người chơi bấm vào tên một đối thủ đang rỗi.
29. Hàm onClick() của PlayerAdapter gọi hàm onPlayerClick() của MainActivity.
30. Hàm onPlayerClick() thấy đối thủ đang rỗi và người chơi chưa có lời mời nào đang chờ, gọi hàm sendChallenge() của ChallengeFirebaseDAO.
31. Hàm sendChallenge() gọi lớp Challenge để tạo lời mời đang chờ, rồi ghi lời mời lên Firebase.
32. Lớp Challenge trả đối tượng Challenge cho hàm sendChallenge().
33. Hàm sendChallenge() trả đối tượng Challenge cho hàm onPlayerClick(); MainActivity giữ lời mời này trong sentChallenge.
34. Hàm onPlayerClick() gọi hàm watchChallenge() của ChallengeFirebaseDAO, truyền chính MainActivity làm ChallengeCallback, để theo dõi lời mời vừa gửi.
35. Hàm onPlayerClick() gọi hàm showWaitingDialog(), MainActivity hiển thị hộp thoại đang chờ đối thủ trả lời kèm nút Hủy.
36. Khi đối thủ đồng ý, hàm onDataChange() của ChallengeFirebaseDAO gọi hàm toChallenge().
37. Hàm toChallenge() gọi lớp Challenge để đóng gói lời mời mới nhất.
38. Lớp Challenge trả đối tượng Challenge cho hàm toChallenge().
39. ChallengeFirebaseDAO gọi hàm onChallengeChanged() của ChallengeCallback đã đăng ký ở bước 34, chính là MainActivity.
40. Hàm onChallengeChanged() thấy lời mời đã được đồng ý, gọi hàm closeSentChallenge() của chính nó.
41. Hàm closeSentChallenge() gọi hàm stopWatchingSent() của ChallengeFirebaseDAO để gỡ theo dõi lời mời, rồi đóng hộp thoại chờ.
42. Hàm onChallengeChanged() gọi hàm openMatch() để mở MatchActivity với mã trận trong lời mời.

![Hình 26: Tuần tự thiết kế Chơi online – Gửi lời thách đấu](tuan-tu-thiet-ke-uc03a.png)

*Hình 26: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn A: Gửi lời thách đấu.*

- Biểu đồ có 42 thông điệp.
- **Lời gọi REST** duy nhất là bước 10, để lấy tổng điểm mới nhất từ server. Server đi theo khuôn PlayerRestController → PlayerDAO → Player.
- **Firebase** làm 3 việc:
  - Ghi người chơi vào danh sách online (bước 21).
  - Báo khi danh sách thay đổi (bước 22–25).
  - Chuyển lời mời giữa 2 máy (bước 30–39).
- **Bất đồng bộ:** hàm goOnline() mở Thread ở bước 7 rồi trả về ngay, nên onCreate() đăng ký theo dõi ở bước 8–9 trong lúc yêu cầu REST ở bước 10–20 còn đang chạy. watchOnlinePlayers(), watchIncoming(), watchChallenge() là lời đăng ký; kết quả đến sau qua onDataChange(). Bước 36 xảy ra khi đối thủ đồng ý ở đoạn B.
- **Callback:** các FirebaseDAO không biết MainActivity, chỉ giữ interface PlayerListCallback, ChallengeCallback được truyền vào lúc đăng ký (bước 8, 9, 34). Bước 25 và 39 gọi qua interface đó; ở chức năng này đối tượng cài đặt là MainActivity.
- Bước 40–41 gỡ theo dõi lời mời đã gửi trước khi mở trận, để lần ghi sau của server lên lời mời không gọi lại onChallengeChanged().

### 1.2. Đoạn B: Nhận lời thách đấu

1. Khi có lời mời mới gửi tới người chơi, hàm onDataChange() của ChallengeFirebaseDAO gọi hàm toChallenge().
2. Hàm toChallenge() gọi lớp Challenge để đóng gói lời mời.
3. Lớp Challenge trả đối tượng Challenge cho hàm toChallenge().
4. ChallengeFirebaseDAO gọi hàm onChallengeChanged() của ChallengeCallback đã đăng ký bằng watchIncoming(), chính là MainActivity.
5. Hàm onChallengeChanged() thấy lời mời gửi tới người chơi chưa từng hiện, ghi id lời mời vào shownChallenges và gọi hàm showChallengeDialog() của chính nó.
6. MainActivity hiển thị hộp thoại lời mời gồm tên, tổng điểm của người mời và 2 nút Đồng ý, Từ chối.
7. Người chơi nhấn Đồng ý.
8. Hàm onClick() của nút Đồng ý gọi hàm acceptChallenge() của MainActivity.
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

![Hình 27: Tuần tự thiết kế Chơi online – Nhận lời thách đấu](tuan-tu-thiet-ke-uc03b.png)

*Hình 27: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn B: Nhận lời thách đấu.*

- Biểu đồ có 35 thông điệp.
- **Server sinh đề** (bước 19–22) và lưu đề vào MySQL cùng trận (bước 23–25), vì server cần đáp án để chấm bài sau này.
- **Đề không kèm đáp án:** đề gửi qua Firebase không có đáp án (bước 26), và bản trận trả về cho app cũng đã bị hideAnswers() xóa đáp án (bước 30). Người chơi không đọc được đáp án trước khi nộp bài.
- Bước 29 cập nhật lời mời, đây chính là tín hiệu để máy người mời mở MatchActivity ở bước 36 của đoạn A.
- Hàm create() đọc trạng thái online của 2 người (bước 11–14) và trạng thái lời mời (bước 15) trước khi tạo trận, nên không thể có người ở trong 2 trận cùng lúc. Lời mời đã hủy hoặc một người không rỗi thì server ghi lời mời là bị từ chối.

### 1.3. Đoạn C: Thi đấu

1. Hàm onCreate() của MatchActivity gọi hàm getPlayer() của SessionDAO.
2. Hàm getPlayer() đóng gói và trả đối tượng Player cho hàm onCreate().
3. Hàm onCreate() gọi hàm watchMatch() của MatchFirebaseDAO với mã trận, truyền chính MatchActivity làm MatchCallback.
4. Hàm onDataChange() của MatchFirebaseDAO gọi hàm toMatch() của chính nó.
5. Hàm toMatch() gọi lớp Match để đóng gói trận, kèm 10 câu hỏi và 2 người chơi trong trận.
6. Lớp Match trả đối tượng Match cho hàm toMatch().
7. MatchFirebaseDAO gọi hàm onSuccess() của MatchCallback đã đăng ký ở bước 3, chính là MatchActivity.
8. Lần đầu nhận trận có đề, hàm onSuccess() gọi hàm startMatch() của chính nó; hàm startMatch() lưu id đối thủ vào opponentId.
9. Hàm startMatch() gọi hàm setQuestions() của QuestionAdapter với 10 câu hỏi của trận.
10. Với mỗi câu, hàm onBindViewHolder() của QuestionAdapter gọi hàm getExpression() của MathProblem.
11. MathProblem trả chuỗi phép tính cho QuestionAdapter.
12. Hàm startMatch() gọi hàm createTimer() của chính nó để tạo CountDownTimer 60 giây.
13. Hàm startMatch() gọi hàm start() của CountDownTimer.
14. Hàm startMatch() gọi hàm watchOnlinePlayers() của PresenceFirebaseDAO, truyền chính MatchActivity làm PlayerListCallback, để biết đối thủ còn online hay không.
15. MatchActivity hiển thị ô thời gian, trạng thái đối thủ, danh sách câu hỏi, nút Nộp bài và nút Thoát cho người chơi.
16. Hàm onDataChange() của PresenceFirebaseDAO đóng gói danh sách người chơi online và gọi hàm onPlayersChanged() của PlayerListCallback, chính là MatchActivity; đối thủ vẫn có trong danh sách nên trận tiếp tục.
17. Mỗi giây, CountDownTimer gọi hàm onTick() của MatchActivity để hiển thị thời gian còn lại.
18. Người chơi chọn một phương án cho từng câu hỏi.
19. Hàm onCheckedChanged() của QuestionAdapter lưu phương án đã chọn của câu đó.
20. Người chơi nhấn Nộp bài.
21. Hàm onClick() của nút Nộp bài gọi hàm submitAnswers() của chính nó.
22. Hàm submitAnswers() gọi hàm setLocked() của QuestionAdapter để khóa danh sách câu hỏi.
23. Hàm submitAnswers() gọi hàm getAnswers() của QuestionAdapter.
24. QuestionAdapter trả bài làm gồm 10 phương án đã chọn cho hàm submitAnswers().
25. Hàm submitAnswers() mở một Thread và gọi hàm submit() của MatchApiDAO.
26. MatchApiDAO gửi yêu cầu POST /api/matches/{id}/submit kèm bài làm tới hàm submit() của MatchRestController.
27. Hàm submit() ghi lại thời điểm nhận bài làm, rồi gọi hàm findMatch() của chính nó.
28. Hàm findMatch() gọi hàm findById() của MatchDAO.
29. Hàm findById() truy vấn trận, đề và 2 người chơi, rồi gọi lớp Match để đóng gói.
30. Lớp Match trả đối tượng Match cho hàm findById().
31. Hàm findById() trả đối tượng Match cho hàm findMatch(), hàm findMatch() trả về cho hàm submit().
32. Hàm submit() gọi hàm countCorrect() của Match để chấm bài làm.
33. Hàm countCorrect() gọi hàm isCorrect() của MathProblem cho từng câu.
34. MathProblem trả kết quả chấm câu đó cho hàm countCorrect().
35. Hàm countCorrect() trả số câu đúng cho hàm submit().
36. Hàm submit() gọi hàm updateSubmission() của MatchDAO để lưu số câu đúng và thời điểm nộp bài.
37. Hàm updateSubmission() trả kết quả cho hàm submit().
38. Hàm submit() gọi hàm writeSubmitted() của FirebaseAdminDAO để máy đối thủ biết người chơi đã nộp bài.
39. Hàm submit() gọi hàm isOver() của Match.
40. Match trả về sai vì đối thủ chưa nộp bài.
41. Hàm submit() gọi hàm hideAnswers() của chính nó để xóa đáp án vì trận chưa kết thúc.
42. Hàm submit() trả trận dạng JSON cho MatchApiDAO.
43. Hàm toMatch() của MatchApiDAO gọi lớp Match để đóng gói dữ liệu JSON.
44. Lớp Match trả đối tượng Match cho MatchApiDAO.
45. MatchApiDAO trả đối tượng Match cho hàm submitAnswers().
46. Trong hàm runOnUiThread(), hàm submitAnswers() gọi hàm onSubmitted() của chính nó.
47. Hàm onSubmitted() gọi hàm cancel() của CountDownTimer.
48. MatchActivity ẩn nút Nộp bài và hiển thị thông báo đã nộp bài, đang chờ đối thủ.
49. Khi đối thủ nộp bài, hàm submit() của MatchRestController, lúc này xử lý yêu cầu từ máy đối thủ, chấm bài như trên rồi gọi hàm isOver() của Match.
50. Match trả về đúng vì cả 2 người chơi đã nộp bài.
51. Hàm submit() gọi hàm endMatch() của chính nó.
52. Hàm endMatch() gọi hàm updateEndedAt() của MatchDAO để ghi thời điểm kết thúc trận.
53. Hàm updateEndedAt() trả kết quả cho hàm endMatch().
54. Hàm endMatch() gọi hàm getTotalPoints() của PlayerDAO cho từng người chơi trong trận.
55. Hàm getTotalPoints() trả tổng điểm mới cho hàm endMatch().
56. Hàm endMatch() gọi hàm updatePoints() của FirebaseAdminDAO để cập nhật tổng điểm trong danh sách online.
57. Hàm submit() gọi hàm writeFinished() của FirebaseAdminDAO để báo trận đã kết thúc cho cả 2 máy.
58. Hàm onDataChange() của MatchFirebaseDAO gọi hàm toMatch().
59. Hàm toMatch() gọi lớp Match để đóng gói trận mới nhất.
60. Lớp Match trả đối tượng Match cho hàm toMatch().
61. MatchFirebaseDAO gọi hàm onSuccess() của MatchCallback, chính là MatchActivity.
62. Hàm onSuccess() thấy trận đã kết thúc, gọi hàm loadResult() của chính nó.
63. Hàm loadResult() mở một Thread và gọi hàm getById() của MatchApiDAO để lấy trận đầy đủ, có đáp án và kết quả chấm.
64. MatchApiDAO gửi yêu cầu GET /api/matches/{id} tới hàm getById() của MatchRestController.
65. Hàm getById() gọi hàm findMatch() của chính nó.
66. Hàm findMatch() gọi hàm findById() của MatchDAO.
67. Hàm findById() truy vấn trận, đề, 2 người chơi và gọi lớp Match để đóng gói.
68. Lớp Match trả đối tượng Match cho hàm findById().
69. Hàm findById() trả đối tượng Match cho hàm findMatch(), hàm findMatch() trả về cho hàm getById().
70. Hàm getById() gọi hàm getTotalPoints() của PlayerDAO cho 2 người chơi và gán vào Player của trận.
71. Hàm getTotalPoints() trả tổng điểm cho hàm getById().
72. Hàm getById() trả trận dạng JSON, kèm đáp án vì trận đã kết thúc, cho MatchApiDAO.
73. Hàm toMatch() của MatchApiDAO gọi lớp Match để đóng gói dữ liệu JSON.
74. Lớp Match trả đối tượng Match cho MatchApiDAO.
75. MatchApiDAO trả đối tượng Match cho hàm loadResult().
76. Trong hàm runOnUiThread(), hàm loadResult() gọi hàm showResult() của chính nó.
77. Hàm showResult() gọi hàm getResult() của Match.
78. Match trả về kết quả của người chơi.
79. Hàm showResult() gọi hàm showScores() của chính nó.
80. Hàm showScores() gọi hàm getFinishTime() của Match cho người chơi và đối thủ.
81. Match trả về thời gian làm bài.
82. Hàm showScores() gọi hàm getPoints() của Match.
83. Match trả về điểm trận của người chơi.
84. MatchActivity hiển thị lớp kết quả gồm kết quả, số câu đúng, thời gian làm bài, điểm trận, tổng điểm và câu hỏi chơi tiếp cho người chơi.

![Hình 28: Tuần tự thiết kế Chơi online – Thi đấu](tuan-tu-thiet-ke-uc03c.png)

*Hình 28: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn C: Thi đấu.*

- Biểu đồ có 84 thông điệp.
- Bước 2 lược bỏ thông điệp đóng gói Player bên trong SessionDAO, vì đã vẽ ở đoạn A.
- **Server làm trọng tài**, gồm 3 phần nghiệp vụ là hàm Java thuần của lớp thực thể:
  - **Chấm bài:** hàm countCorrect() của Match gọi hàm isCorrect() của MathProblem (bước 32–35).
  - **Điều kiện kết thúc:** hàm isOver() của Match (bước 39, 49).
  - **Thắng thua:** hàm getResult() và getPoints() của Match. App gọi 2 hàm này trên bản trận server trả về (bước 77–83).
- **Thời gian làm bài** tính theo đồng hồ server: thời điểm server nhận bài làm trừ thời điểm bắt đầu trận. Độ trễ khác nhau giữa 2 máy không làm sai kết quả do đồng hồ máy lệch nhau.
- Bước 49–57 diễn ra khi server xử lý yêu cầu nộp bài từ máy đối thủ. Máy này biết trận đã xong nhờ Firebase (bước 58–61).
- **MatchActivity cài đặt 2 callback:** MatchCallback nhận diễn biến trận (bước 3, 7, 61), PlayerListCallback nhận danh sách người chơi online (bước 14, 16). Ở kịch bản chuẩn đối thủ vẫn online nên bước 16 không làm gì thêm; khi đối thủ biến khỏi danh sách giữa trận, hàm onPlayersChanged() gọi forfeit() thay cho đối thủ.
- Các cờ started, submitted, resultLoading của MatchActivity chặn gọi lặp: onSuccess() được gọi lại mỗi lần node trận thay đổi, nhưng startMatch() chỉ chạy ở lần đầu (bước 8), loadResult() chỉ chạy một lần (bước 62).

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

![Hình 29: Tuần tự thiết kế Chơi online – Chơi tiếp](tuan-tu-thiet-ke-uc03d.png)

*Hình 29: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn D: Chơi tiếp.*

- Biểu đồ có 30 thông điệp, vẽ cho người chọn chơi tiếp trước.
- Hàm continueMatch() chạy tuần tự (synchronized). Nếu 2 yêu cầu chơi tiếp đến cùng lúc, chỉ yêu cầu đến sau tạo trận mới, nên không sinh 2 trận.
- Trận mới dùng lại hàm startMatch() của đoạn B (bước 14–25), không viết lại logic sinh đề.
- Máy này nhận trận mới qua MatchCallback (bước 27), gỡ theo dõi trận cũ (bước 29), mở MatchActivity mới và đi lại đoạn C.

---

## 2. Chức năng "Xem lịch sử đấu"

### 2.1. Đoạn A: Xem trận của tôi và chi tiết trận

1. Người chơi nhấn nút Lịch sử đấu trên MainActivity.
2. Hàm onClick() của MainActivity gọi hàm startActivity() mở HistoryActivity.
3. Hàm onCreate() của HistoryActivity gọi hàm getPlayer() của SessionDAO.
4. Hàm getPlayer() trả đối tượng Player đang đăng nhập cho hàm onCreate().
5. Hàm onCreate() gọi hàm loadHistory() của chính nó.
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
41. Hàm findById() trả đối tượng Match cho hàm findMatch(), hàm findMatch() trả về cho hàm getById().
42. Hàm getById() gọi hàm getTotalPoints() của PlayerDAO cho 2 người chơi và gán vào Player của trận.
43. Hàm getTotalPoints() trả tổng điểm cho hàm getById().
44. Hàm getById() trả trận dạng JSON, kèm đáp án đúng vì trận đã kết thúc, cho MatchApiDAO.
45. Hàm toMatch() của MatchApiDAO gọi lớp Match để đóng gói dữ liệu JSON.
46. Lớp Match trả đối tượng Match cho MatchApiDAO.
47. MatchApiDAO trả đối tượng Match cho hàm loadMatch().
48. Trong hàm runOnUiThread(), hàm loadMatch() gọi hàm showMatch() của chính nó.
49. Hàm showMatch() gọi hàm getOpponent() của Match.
50. Match trả về đối thủ trong trận.
51. Hàm showMatch() gọi hàm getResult() của Match.
52. Match trả về kết quả trận của người chơi.
53. Hàm showMatch() gọi hàm getPoints() của Match.
54. Match trả về điểm trận.
55. Với người chơi và đối thủ, hàm showMatch() gọi hàm stats() của chính nó.
56. Hàm stats() gọi hàm getFinishTime() của Match.
57. Match trả về thời gian làm bài.
58. Hàm showMatch() tạo ProblemAdapter với 10 câu hỏi của trận, rồi gắn vào danh sách.
59. Hàm onBindViewHolder() của ProblemAdapter gọi hàm getExpression() của MathProblem.
60. MathProblem trả về chuỗi phép tính.
61. MatchDetailActivity hiển thị kết quả, điểm trận, số câu đúng và thời gian làm bài của 2 bên, 10 câu hỏi kèm đáp án đúng cho người chơi.
62. Người chơi nhấn Back.
63. MatchDetailActivity gọi hàm finish(), quay về HistoryActivity.
64. HistoryActivity hiển thị lại danh sách trận cho người chơi.

![Hình 30: Tuần tự thiết kế Xem lịch sử đấu – Xem trận của tôi](tuan-tu-thiet-ke-uc04a.png)

*Hình 30: Biểu đồ tuần tự chi tiết chức năng Xem lịch sử đấu, đoạn A: Xem trận của tôi và chi tiết trận.*

- Biểu đồ có 64 thông điệp.
- Bước 3–4 và 32–33 lược bỏ thông điệp đóng gói Player bên trong SessionDAO, vì đã vẽ ở chức năng Chơi online.
- **Có 2 lời gọi REST:** bước 7 lấy danh sách trận, bước 36 lấy một trận đầy đủ. Server đi theo khuôn MatchRestController → MatchDAO → Match.
- **Danh sách trận không kèm đề** (bước 9) để mỗi lần mở lịch sử chỉ đọc 2 bảng tblMatch, tblMatchPlayer. Đề và đáp án chỉ được đọc khi người chơi mở một trận (bước 39).
- **Biểu đồ tròn và từng dòng trận** đều dựa trên getResult(), getPoints(), getOpponent() của Match (bước 17, 22–27). Đây là cùng luật dùng khi kết thúc trận, nên lịch sử luôn khớp với kết quả đã báo cho 2 người.
- Lời gọi mạng chạy trong Thread (bước 6, 35). Phần vẽ giao diện chạy trong hàm runOnUiThread() (bước 16, 48).

### 2.2. Đoạn B: Xem bảng xếp hạng

1. Người chơi chọn thẻ Bảng xếp hạng trên HistoryActivity.
2. Hàm onTabSelected() của HistoryActivity ẩn khối Trận của tôi, hiện danh sách xếp hạng và, vì chưa tải lần nào, gọi hàm loadRanking() của chính nó.
3. Hàm loadRanking() ghi rankingLoaded, mở một Thread và gọi hàm getRanking() của PlayerApiDAO.
4. PlayerApiDAO gửi yêu cầu GET /api/players/ranking tới hàm getRanking() của PlayerRestController.
5. Hàm getRanking() của PlayerRestController gọi hàm getRanking() của PlayerDAO.
6. Hàm getRanking() của PlayerDAO gọi hàm findAllPlayers() của chính nó.
7. Hàm findAllPlayers() truy vấn các tài khoản có vai trò người chơi và gọi lớp Player để đóng gói từng người.
8. Lớp Player trả đối tượng Player cho hàm findAllPlayers().
9. Hàm getRanking() gọi hàm findFinished() của MatchDAO.
10. Hàm findFinished() truy vấn các trận đã kết thúc kèm 2 người chơi, và gọi lớp Match để đóng gói từng trận.
11. Lớp Match trả đối tượng Match cho hàm findFinished().
12. Hàm findFinished() trả danh sách trận cho hàm getRanking().
13. Hàm getRanking() gọi hàm rank() của chính nó với danh sách người chơi và danh sách trận.
14. Với mỗi trận, hàm rank() gọi hàm addMatch() của chính nó.
15. Hàm addMatch() gọi hàm getPoints() của Match để cộng điểm trận vào tổng điểm của 2 người.
16. Match trả về điểm trận.
17. Hàm addMatch() gọi hàm getFinishTime() của Match.
18. Match trả về thời gian làm bài.
19. Hàm addMatch() gọi hàm getResult() của Match để biết người thắng; trận thắng thì ghi thời gian làm bài vào danh sách thời gian thắng.
20. Match trả về kết quả trận.
21. Hàm rank() tính trung bình điểm đối thủ bằng hàm averagePoints(), trung bình thời gian thắng bằng hàm averageSeconds(), rồi sắp xếp theo 3 tiêu chí.
22. Hàm getRanking() của PlayerDAO trả danh sách người chơi đã xếp hạng cho PlayerRestController.
23. Hàm getRanking() của PlayerRestController trả danh sách dạng JSON cho PlayerApiDAO.
24. Với mỗi phần tử JSON, hàm toPlayer() của PlayerApiDAO gọi lớp Player để đóng gói.
25. Lớp Player trả đối tượng Player cho PlayerApiDAO.
26. PlayerApiDAO trả danh sách người chơi cho hàm loadRanking().
27. Trong hàm runOnUiThread(), hàm loadRanking() tạo RankingAdapter với danh sách và id của người xem, rồi gắn vào danh sách xếp hạng.
28. RankingAdapter hiển thị bảng xếp hạng, dòng của người xem được tô nổi bật.
29. Người chơi nhấn Back.
30. HistoryActivity gọi hàm finish(), quay về MainActivity.
31. MainActivity hiển thị giao diện chính cho người chơi.

![Hình 31: Tuần tự thiết kế Xem lịch sử đấu – Xem bảng xếp hạng](tuan-tu-thiet-ke-uc04b.png)

*Hình 31: Biểu đồ tuần tự chi tiết chức năng Xem lịch sử đấu, đoạn B: Xem bảng xếp hạng.*

- Biểu đồ có 31 thông điệp.
- **Lời gọi REST** duy nhất là bước 4. Server đi theo khuôn PlayerRestController → PlayerDAO → MatchDAO → Match, Player.
- **Các tiêu chí không lưu trong CSDL** mà được tính lại ở bước 13–21 bằng getPoints(), getResult(), getFinishTime() của Match. Bảng xếp hạng vì vậy luôn khớp với kết quả các trận.
- rank() là hàm static, không truy cập CSDL: nhận danh sách người chơi và danh sách trận đã đọc ở bước 6–12 rồi chỉ tính toán, nên kiểm thử được bằng unit test.
- Bảng xếp hạng là một thẻ của HistoryActivity nên không mở Activity mới; chỉ lần chọn thẻ đầu tiên mới gọi server, các lần sau dùng lại danh sách đã tải.

---

## 3. Chức năng "Quản lý bạn bè"

### 3.1. Đoạn A: Gửi lời mời kết bạn

1. Người chơi nhấn nút Bạn bè trên MainActivity.
2. Hàm onClick() của MainActivity gọi hàm startActivity() mở FriendActivity.
3. Hàm onCreate() của FriendActivity gọi hàm getPlayer() của SessionDAO.
4. Hàm getPlayer() gọi lớp Player để đóng gói người chơi đang đăng nhập.
5. Lớp Player trả đối tượng Player cho hàm getPlayer().
6. Hàm getPlayer() trả đối tượng Player cho hàm onCreate().
7. Hàm onCreate() gọi hàm watchOnlinePlayers() của PresenceFirebaseDAO, truyền chính FriendActivity làm PlayerListCallback, để theo dõi danh sách người chơi online.
8. Hàm onCreate() gọi hàm loadFriendships() của chính nó.
9. Hàm loadFriendships() mở một Thread và gọi hàm getFriendships() của FriendshipApiDAO.
10. FriendshipApiDAO gửi yêu cầu GET /api/friendships?playerId={id} tới hàm getFriendships() của FriendshipRestController.
11. Hàm getFriendships() của FriendshipRestController gọi hàm findByPlayer() của FriendshipDAO.
12. Hàm findByPlayer() truy vấn bảng tblFriendship nối với tblPlayer, và gọi lớp Friendship để đóng gói từng quan hệ kèm người gửi, người nhận.
13. Lớp Friendship trả đối tượng Friendship cho hàm findByPlayer().
14. Hàm findByPlayer() trả danh sách Friendship cho hàm getFriendships().
15. Với mỗi người gửi và người nhận, hàm getFriendships() gọi hàm getTotalPoints() của PlayerDAO.
16. Hàm getTotalPoints() trả tổng điểm cho hàm getFriendships().
17. Hàm getFriendships() trả danh sách quan hệ dạng JSON cho FriendshipApiDAO.
18. Với mỗi phần tử JSON, hàm toFriendship() của FriendshipApiDAO gọi lớp Friendship để đóng gói.
19. Lớp Friendship trả đối tượng Friendship cho FriendshipApiDAO.
20. FriendshipApiDAO trả danh sách Friendship cho hàm loadFriendships().
21. Trong hàm runOnUiThread(), hàm loadFriendships() gọi hàm showFriendships() của chính nó.
22. Với mỗi quan hệ, hàm showFriendships() gọi hàm canAccept() của Friendship để biết đó có phải lời mời người chơi cần trả lời không.
23. Friendship trả kết quả kiểm tra cho hàm showFriendships().
24. Hàm showFriendships() gọi hàm setFriendships() của FriendRequestAdapter với các lời mời đã nhận.
25. FriendRequestAdapter hiển thị danh sách lời mời đã nhận cho người chơi.
26. Hàm showFriendships() gọi hàm setFriendships() của FriendAdapter với các bạn bè và lời mời đã gửi.
27. Với mỗi quan hệ, hàm onBindViewHolder() của FriendAdapter gọi hàm getFriend() của Friendship.
28. Friendship trả đối tượng Player của người bạn cho FriendAdapter.
29. FriendAdapter hiển thị danh sách bạn bè gồm tên, tổng điểm hoặc dòng đang chờ trả lời.
30. Khi danh sách online thay đổi, hàm onDataChange() của PresenceFirebaseDAO gọi hàm toPlayer() cho từng người chơi.
31. Hàm toPlayer() gọi lớp Player để đóng gói người chơi kèm trạng thái.
32. Lớp Player trả đối tượng Player cho hàm toPlayer().
33. PresenceFirebaseDAO gọi hàm onPlayersChanged() của PlayerListCallback đã đăng ký ở bước 7, chính là FriendActivity, truyền danh sách người chơi online.
34. Hàm onPlayersChanged() lấy id những người đang online và gọi hàm setOnlineIds() của FriendAdapter.
35. FriendAdapter hiển thị trạng thái Online / Offline của từng người bạn.
36. Người chơi nhập tên đăng nhập của một người và nhấn nút Kết bạn.
37. Hàm onClick() của FriendActivity mở một Thread và gọi hàm sendRequest() của FriendshipApiDAO.
38. FriendshipApiDAO gửi yêu cầu POST /api/friendships tới hàm sendRequest() của FriendshipRestController.
39. Hàm sendRequest() gọi hàm findById() của PlayerDAO để lấy người gửi.
40. Hàm findById() truy vấn bảng tblPlayer và gọi lớp Player để đóng gói kết quả.
41. Lớp Player trả đối tượng Player cho hàm findById().
42. Hàm findById() trả đối tượng Player cho hàm sendRequest().
43. Hàm sendRequest() gọi hàm findByUsername() của PlayerDAO để tìm người nhận theo tên đăng nhập.
44. Hàm findByUsername() truy vấn bảng tblPlayer và gọi lớp Player để đóng gói kết quả.
45. Lớp Player trả đối tượng Player cho hàm findByUsername().
46. Hàm findByUsername() trả đối tượng Player cho hàm sendRequest().
47. Hàm sendRequest() gọi hàm existsBetween() của FriendshipDAO để kiểm tra giữa 2 người đã có quan hệ chưa.
48. Hàm existsBetween() trả về chưa có quan hệ cho hàm sendRequest().
49. Hàm sendRequest() gọi lớp Friendship để tạo lời mời đang chờ với người gửi, người nhận.
50. Lớp Friendship trả đối tượng Friendship cho hàm sendRequest().
51. Hàm sendRequest() gọi hàm insert() của FriendshipDAO.
52. Hàm insert() ghi 1 dòng vào bảng tblFriendship, gán id vừa sinh và trả về thành công.
53. Hàm sendRequest() gọi hàm getTotalPoints() của PlayerDAO cho người nhận.
54. Hàm getTotalPoints() trả tổng điểm cho hàm sendRequest().
55. Hàm sendRequest() trả lời mời dạng JSON cho FriendshipApiDAO.
56. Hàm toFriendship() của FriendshipApiDAO gọi lớp Friendship để đóng gói dữ liệu JSON.
57. Lớp Friendship trả đối tượng Friendship cho FriendshipApiDAO.
58. FriendshipApiDAO trả đối tượng Friendship cho hàm onClick().
59. Trong hàm runOnUiThread(), hàm onClick() thêm lời mời vào cuối danh sách bạn bè và gọi hàm setFriendships() của FriendAdapter.
60. FriendAdapter hiển thị dòng người nhận với trạng thái đang chờ trả lời; FriendActivity hiển thị thông báo đã gửi lời mời.

![Hình 32: Tuần tự thiết kế Quản lý bạn bè – Gửi lời mời kết bạn](tuan-tu-thiet-ke-uc07a.png)

*Hình 32: Biểu đồ tuần tự chi tiết chức năng Quản lý bạn bè, đoạn A: Gửi lời mời kết bạn.*

- Biểu đồ có 60 thông điệp.
- **Lời gọi REST** có 2 lần: bước 10 lấy lời mời và bạn bè, bước 38 gửi lời mời. Server đi theo khuôn FriendshipRestController → FriendshipDAO, PlayerDAO → Friendship, Player.
- **Firebase** chỉ dùng để biết bạn nào đang online (bước 7, 30–34). Bước 30 xảy ra mỗi khi có người vào hoặc thoát ứng dụng, nên trạng thái Online / Offline tự cập nhật mà không phải gọi server. PresenceFirebaseDAO gọi về qua interface PlayerListCallback (bước 33), dùng chung với MainActivity, MatchActivity của Chơi online.
- **Luật nghiệp vụ** nằm ở thực thể Friendship: canAccept() tách lời mời đã nhận (bước 22), getFriend() lấy người còn lại trong quan hệ (bước 27). Kiểm tra trùng quan hệ theo cả 2 chiều nằm ở existsBetween() của FriendshipDAO (bước 47).
- Lời gọi mạng chạy trong Thread (bước 9, 37). Phần vẽ giao diện chạy trong hàm runOnUiThread() (bước 21, 59).

### 3.2. Đoạn B: Đồng ý lời mời kết bạn

Người chơi là người nhận; FriendActivity đã hiển thị các lời mời đã nhận như bước 1–35 của đoạn A.

1. Người chơi nhấn nút Đồng ý ở một lời mời trong danh sách lời mời đã nhận.
2. Hàm onClick() của FriendRequestAdapter gọi hàm onAccept() của FriendActivity.
3. Hàm onAccept() mở một Thread và gọi hàm accept() của FriendshipApiDAO.
4. FriendshipApiDAO gửi yêu cầu POST /api/friendships/{id}/accept?playerId={id} tới hàm accept() của FriendshipRestController.
5. Hàm accept() gọi hàm findFriendship() của chính nó.
6. Hàm findFriendship() gọi hàm findById() của FriendshipDAO.
7. Hàm findById() truy vấn bảng tblFriendship nối với tblPlayer, và gọi lớp Friendship để đóng gói kết quả.
8. Lớp Friendship trả đối tượng Friendship cho hàm findById().
9. Hàm findById() trả đối tượng Friendship cho hàm findFriendship(), hàm findFriendship() trả về cho hàm accept().
10. Hàm accept() gọi hàm canAccept() của Friendship để kiểm tra lời mời còn đang chờ và người chơi là người nhận.
11. Friendship trả về hợp lệ cho hàm accept().
12. Hàm accept() gọi hàm updateStatus() của FriendshipDAO để chuyển lời mời sang trạng thái accepted.
13. Hàm updateStatus() cập nhật bảng tblFriendship và trả về thành công.
14. Hàm accept() gọi hàm getTotalPoints() của PlayerDAO cho người gửi lời mời.
15. Hàm getTotalPoints() trả tổng điểm cho hàm accept().
16. Hàm accept() trả quan hệ đã đồng ý dạng JSON cho FriendshipApiDAO.
17. Hàm toFriendship() của FriendshipApiDAO gọi lớp Friendship để đóng gói dữ liệu JSON.
18. Lớp Friendship trả đối tượng Friendship cho FriendshipApiDAO.
19. FriendshipApiDAO trả đối tượng Friendship cho hàm onAccept().
20. Trong hàm runOnUiThread(), hàm onAccept() bỏ lời mời khỏi danh sách lời mời đã nhận và gọi hàm setFriendships() của FriendRequestAdapter.
21. Hàm onAccept() thêm quan hệ vào danh sách bạn bè và gọi hàm setFriendships() của FriendAdapter.
22. Hàm onBindViewHolder() của FriendAdapter gọi hàm getFriend() của Friendship.
23. Friendship trả đối tượng Player của người gửi cho FriendAdapter.
24. FriendAdapter hiển thị người gửi trong danh sách bạn bè kèm tổng điểm và trạng thái online.

![Hình 33: Tuần tự thiết kế Quản lý bạn bè – Đồng ý lời mời kết bạn](tuan-tu-thiet-ke-uc07b.png)

*Hình 33: Biểu đồ tuần tự chi tiết chức năng Quản lý bạn bè, đoạn B: Đồng ý lời mời kết bạn.*

- Biểu đồ có 24 thông điệp.
- **Lời gọi REST** duy nhất là bước 4. Server không tin vào máy gửi yêu cầu: nó đọc lại lời mời từ CSDL qua findFriendship() (bước 5–9) rồi mới kiểm tra bằng canAccept() (bước 10).
- updateStatus() chỉ cập nhật dòng đang pending, nên 2 lần nhấn Đồng ý liên tiếp không làm hỏng dữ liệu.
- Trạng thái Online / Offline của người bạn mới lấy từ onlineIds mà FriendActivity đã có từ đoạn A, không cần gọi Firebase lại.
