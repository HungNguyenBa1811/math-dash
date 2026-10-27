# KỊCH BẢN v3 VÀ BIỂU ĐỒ TUẦN TỰ CHI TIẾT — CHƠI ONLINE, XEM LỊCH SỬ ĐẤU, QUẢN LÝ BẠN BÈ

---

## 1. Chức năng "Chơi online"

### 1.1. Đoạn A: Gửi lời thách đấu

1. Người chơi đăng nhập thành công, MainActivity được mở.
2. Hàm onCreate() của MainActivity gọi hàm getPlayer() của SessionDAO.
3. Hàm getPlayer() gọi lớp Player để đóng gói người chơi đang đăng nhập.
4. Lớp Player trả đối tượng Player cho hàm getPlayer().
5. Hàm getPlayer() trả đối tượng Player cho hàm onCreate().
6. Hàm onCreate() mở một Thread và gọi hàm getById() của PlayerApiDAO để lấy tổng điểm mới nhất.
7. PlayerApiDAO gửi yêu cầu GET /api/players/{id} tới hàm getById() của PlayerRestController.
8. Hàm getById() của PlayerRestController gọi hàm findById() của PlayerDAO.
9. Hàm findById() truy vấn bảng tblPlayer và gọi lớp Player để đóng gói kết quả.
10. Lớp Player trả đối tượng Player cho hàm findById().
11. Hàm findById() trả đối tượng Player cho hàm getById().
12. Hàm getById() gọi hàm getTotalPoints() của PlayerDAO.
13. Hàm getTotalPoints() cộng điểm trận của người chơi qua các trận đã kết thúc và trả về cho hàm getById().
14. Hàm getById() trả người chơi kèm tổng điểm dạng JSON cho PlayerApiDAO.
15. Hàm toPlayer() của PlayerApiDAO gọi lớp Player để đóng gói dữ liệu JSON.
16. Lớp Player trả đối tượng Player cho PlayerApiDAO.
17. PlayerApiDAO trả đối tượng Player cho hàm onCreate().
18. Trong hàm runOnUiThread(), hàm onCreate() gọi hàm goOnline() của PresenceFirebaseDAO để ghi người chơi vào danh sách online với trạng thái rỗi.
19. Hàm onCreate() gọi hàm watchOnlinePlayers() của PresenceFirebaseDAO.
20. Hàm onCreate() gọi hàm watchIncoming() của ChallengeFirebaseDAO để nhận các lời mời gửi tới người chơi.
21. Khi danh sách online thay đổi, hàm onDataChange() của PresenceFirebaseDAO gọi hàm toPlayer() cho từng người chơi.
22. Hàm toPlayer() gọi lớp Player để đóng gói người chơi kèm tổng điểm và trạng thái.
23. Lớp Player trả đối tượng Player cho hàm toPlayer().
24. PresenceFirebaseDAO gọi hàm onPlayersChanged() của MainActivity, truyền danh sách người chơi online.
25. Hàm onPlayersChanged() hiển thị tên, tổng điểm của chính người chơi, rồi gọi hàm setPlayers() của PlayerAdapter với danh sách những người còn lại.
26. PlayerAdapter hiển thị danh sách người chơi online gồm tên, tổng điểm, trạng thái.
27. Người chơi bấm vào tên một đối thủ đang rỗi.
28. Hàm onClick() của PlayerAdapter gọi hàm onPlayerClick() của MainActivity.
29. Hàm onPlayerClick() thấy đối thủ đang rỗi, gọi hàm sendChallenge() của ChallengeFirebaseDAO.
30. Hàm sendChallenge() gọi lớp Challenge để tạo lời mời đang chờ, rồi ghi lời mời lên Firebase.
31. Lớp Challenge trả đối tượng Challenge cho hàm sendChallenge().
32. Hàm sendChallenge() trả đối tượng Challenge cho hàm onPlayerClick().
33. Hàm onPlayerClick() gọi hàm watchChallenge() của ChallengeFirebaseDAO để theo dõi lời mời vừa gửi.
34. MainActivity hiển thị hộp thoại đang chờ đối thủ trả lời.
35. Khi đối thủ đồng ý, hàm onDataChange() của ChallengeFirebaseDAO gọi hàm toChallenge().
36. Hàm toChallenge() gọi lớp Challenge để đóng gói lời mời mới nhất.
37. Lớp Challenge trả đối tượng Challenge cho hàm toChallenge().
38. ChallengeFirebaseDAO gọi hàm onChallengeChanged() của MainActivity.
39. Hàm onChallengeChanged() thấy lời mời đã được đồng ý, đóng hộp thoại chờ và gọi hàm openMatch() để mở MatchActivity với mã trận trong lời mời.

![Hình 26: Tuần tự thiết kế Chơi online – Gửi lời thách đấu](tuan-tu-thiet-ke-uc03a.png)

*Hình 26: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn A: Gửi lời thách đấu.*

- Biểu đồ có 39 thông điệp.
- **Lời gọi REST** duy nhất là bước 7, để lấy tổng điểm mới nhất từ server. Server đi theo khuôn PlayerRestController → PlayerDAO → Player.
- **Firebase** làm 3 việc:
  - Ghi người chơi vào danh sách online (bước 18).
  - Báo khi danh sách thay đổi (bước 21–24).
  - Chuyển lời mời giữa 2 máy (bước 29–38).
- **Bất đồng bộ:** watchOnlinePlayers(), watchIncoming(), watchChallenge() là lời đăng ký; kết quả đến sau qua onDataChange(). Bước 35 xảy ra khi đối thủ đồng ý ở đoạn B.

### 1.2. Đoạn B: Nhận lời thách đấu

1. Khi có lời mời mới gửi tới người chơi, hàm onDataChange() của ChallengeFirebaseDAO gọi hàm toChallenge().
2. Hàm toChallenge() gọi lớp Challenge để đóng gói lời mời.
3. Lớp Challenge trả đối tượng Challenge cho hàm toChallenge().
4. ChallengeFirebaseDAO gọi hàm onChallengeChanged() của MainActivity.
5. Hàm onChallengeChanged() thấy lời mời đang chờ, gọi hàm showChallengeDialog() của chính nó.
6. MainActivity hiển thị hộp thoại lời mời gồm tên, tổng điểm của người mời và 2 nút Đồng ý, Từ chối.
7. Người chơi nhấn Đồng ý.
8. Hàm onClick() của nút Đồng ý gọi hàm acceptChallenge() của MainActivity.
9. Hàm acceptChallenge() mở một Thread và gọi hàm createMatch() của MatchApiDAO.
10. MatchApiDAO gửi yêu cầu POST /api/matches kèm lời mời tới hàm create() của MatchRestController.
11. Hàm create() gọi hàm readChallengeStatus() của FirebaseAdminDAO; lời mời vẫn đang chờ, người mời chưa hủy.
12. Hàm create() gọi hàm readPresence() của FirebaseAdminDAO cho người mời và người được mời.
13. Hàm readPresence() đọc node presence và gọi lớp Player để đóng gói người chơi kèm trạng thái.
14. Lớp Player trả đối tượng Player cho hàm readPresence().
15. Hàm readPresence() trả đối tượng Player cho hàm create(); cả 2 người đang online và rỗi.
16. Hàm create() gọi hàm startMatch() của chính nó.
17. Hàm startMatch() gọi hàm generateProblems() của ProblemDAO để sinh đề 10 câu.
18. Với mỗi câu, hàm generateProblems() sinh phép tính ngẫu nhiên và gọi lớp MathProblem để đóng gói kèm 4 phương án.
19. Lớp MathProblem trả đối tượng MathProblem cho hàm generateProblems().
20. Hàm generateProblems() trả danh sách 10 câu hỏi cho hàm startMatch().
21. Hàm startMatch() gọi lớp Match để tạo trận với đề vừa sinh, thời điểm bắt đầu và 2 người chơi.
22. Lớp Match trả đối tượng Match cho hàm startMatch().
23. Hàm startMatch() gọi hàm insert() của MatchDAO.
24. Hàm insert() lưu trận, 10 câu hỏi và 2 người chơi trong trận vào CSDL trong một transaction, gán id vừa sinh cho trận.
25. Hàm insert() trả kết quả cho hàm startMatch().
26. Hàm startMatch() gọi hàm writeMatch() của FirebaseAdminDAO để gửi đề, không kèm đáp án, cho cả 2 máy.
27. Hàm startMatch() gọi hàm updateStatus() của FirebaseAdminDAO để chuyển 2 người chơi sang trạng thái bận.
28. Hàm create() gọi hàm updateChallenge() của FirebaseAdminDAO để đánh dấu lời mời đã được đồng ý, kèm mã trận.
29. Hàm create() trả trận dạng JSON cho MatchApiDAO.
30. Hàm toMatch() của MatchApiDAO gọi lớp Match để đóng gói dữ liệu JSON.
31. Lớp Match trả đối tượng Match cho MatchApiDAO.
32. MatchApiDAO trả đối tượng Match cho hàm acceptChallenge().
33. Trong hàm runOnUiThread(), hàm acceptChallenge() gọi hàm openMatch() để mở MatchActivity với mã trận.

![Hình 27: Tuần tự thiết kế Chơi online – Nhận lời thách đấu](tuan-tu-thiet-ke-uc03b.png)

*Hình 27: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn B: Nhận lời thách đấu.*

- Biểu đồ có 33 thông điệp.
- **Server sinh đề** (bước 17–20) và lưu đề vào MySQL cùng trận (bước 23–24), vì server cần đáp án để chấm bài sau này.
- **Đề gửi qua Firebase không kèm đáp án** (bước 26): người chơi không đọc được đáp án trước khi nộp bài.
- Bước 28 cập nhật lời mời, đây chính là tín hiệu để máy người mời mở MatchActivity ở bước 35 của đoạn A.
- Hàm create() kiểm tra lời mời chưa bị hủy (bước 11) và cả 2 người còn online, rỗi (bước 12–15), nên không thể có người ở trong 2 trận cùng lúc.

### 1.3. Đoạn C: Thi đấu

1. Hàm onCreate() của MatchActivity gọi hàm getPlayer() của SessionDAO.
2. Hàm getPlayer() đóng gói và trả đối tượng Player cho hàm onCreate().
3. Hàm onCreate() gọi hàm watchMatch() của MatchFirebaseDAO với mã trận.
4. Hàm onDataChange() của MatchFirebaseDAO gọi hàm toMatch() của chính nó.
5. Hàm toMatch() gọi lớp Match để đóng gói trận, kèm 10 câu hỏi và 2 người chơi trong trận.
6. Lớp Match trả đối tượng Match cho hàm toMatch().
7. MatchFirebaseDAO gọi hàm onSuccess() của MatchActivity.
8. Lần đầu nhận trận, hàm onSuccess() gọi hàm startMatch() của chính nó.
9. Hàm startMatch() gọi hàm setQuestions() của QuestionAdapter với 10 câu hỏi của trận.
10. Với mỗi câu, hàm onBindViewHolder() của QuestionAdapter gọi hàm getExpression() của MathProblem.
11. MathProblem trả chuỗi phép tính cho QuestionAdapter.
12. Hàm startMatch() gọi hàm start() của CountDownTimer 60 giây.
13. MatchActivity hiển thị ô thời gian, trạng thái đối thủ, danh sách câu hỏi, nút Nộp bài và nút Thoát cho người chơi.
14. Mỗi giây, CountDownTimer gọi hàm onTick() của MatchActivity để hiển thị thời gian còn lại.
15. Người chơi chọn một phương án cho từng câu hỏi.
16. Hàm onCheckedChanged() của QuestionAdapter lưu phương án đã chọn của câu đó.
17. Người chơi nhấn Nộp bài.
18. Hàm onClick() của nút Nộp bài gọi hàm submitAnswers() của chính nó.
19. Hàm submitAnswers() gọi hàm getAnswers() của QuestionAdapter.
20. QuestionAdapter trả bài làm gồm 10 phương án đã chọn cho hàm submitAnswers().
21. Hàm submitAnswers() mở một Thread và gọi hàm submit() của MatchApiDAO.
22. MatchApiDAO gửi yêu cầu POST /api/matches/{id}/submit kèm bài làm tới hàm submit() của MatchRestController.
23. Hàm submit() ghi lại thời điểm nhận bài làm, rồi gọi hàm findById() của MatchDAO.
24. Hàm findById() truy vấn trận, đề và 2 người chơi, rồi gọi lớp Match để đóng gói.
25. Lớp Match trả đối tượng Match cho hàm findById().
26. Hàm findById() trả đối tượng Match cho hàm submit().
27. Hàm submit() gọi hàm countCorrect() của Match để chấm bài làm.
28. Hàm countCorrect() gọi hàm isCorrect() của MathProblem cho từng câu.
29. MathProblem trả kết quả chấm câu đó cho hàm countCorrect().
30. Hàm countCorrect() trả số câu đúng cho hàm submit().
31. Hàm submit() gọi hàm updateSubmission() của MatchDAO để lưu số câu đúng và thời điểm nộp bài.
32. Hàm updateSubmission() trả kết quả cho hàm submit().
33. Hàm submit() gọi hàm writeSubmitted() của FirebaseAdminDAO để máy đối thủ biết người chơi đã nộp bài.
34. Hàm submit() gọi hàm isOver() của Match.
35. Match trả về sai vì đối thủ chưa nộp bài.
36. Hàm submit() trả trận dạng JSON cho MatchApiDAO.
37. Hàm toMatch() của MatchApiDAO gọi lớp Match để đóng gói dữ liệu JSON.
38. Lớp Match trả đối tượng Match cho MatchApiDAO.
39. MatchApiDAO trả đối tượng Match cho hàm submitAnswers().
40. Trong hàm runOnUiThread(), hàm submitAnswers() gọi hàm cancel() của CountDownTimer.
41. MatchActivity khóa danh sách câu hỏi, ẩn nút Nộp bài và hiển thị thông báo đã nộp bài, đang chờ đối thủ.
42. Khi đối thủ nộp bài, hàm submit() của MatchRestController, lúc này xử lý yêu cầu từ máy đối thủ, chấm bài như trên rồi gọi hàm isOver() của Match.
43. Match trả về đúng vì cả 2 người chơi đã nộp bài.
44. Hàm submit() gọi hàm updateEndedAt() của MatchDAO để ghi thời điểm kết thúc trận.
45. Hàm updateEndedAt() trả kết quả cho hàm submit().
46. Hàm submit() gọi hàm getTotalPoints() của PlayerDAO cho từng người chơi trong trận.
47. Hàm getTotalPoints() trả tổng điểm mới cho hàm submit().
48. Hàm submit() gọi hàm updatePoints() của FirebaseAdminDAO để cập nhật tổng điểm trong danh sách online.
49. Hàm submit() gọi hàm writeFinished() của FirebaseAdminDAO để báo trận đã kết thúc cho cả 2 máy.
50. Hàm onDataChange() của MatchFirebaseDAO gọi hàm toMatch().
51. Hàm toMatch() gọi lớp Match để đóng gói trận mới nhất.
52. Lớp Match trả đối tượng Match cho hàm toMatch().
53. MatchFirebaseDAO gọi hàm onSuccess() của MatchActivity.
54. Hàm onSuccess() thấy trận đã kết thúc, gọi hàm loadResult() của chính nó.
55. Hàm loadResult() mở một Thread và gọi hàm getById() của MatchApiDAO để lấy trận đầy đủ, có đáp án và kết quả chấm.
56. MatchApiDAO gửi yêu cầu GET /api/matches/{id} tới hàm getById() của MatchRestController.
57. Hàm getById() gọi hàm findById() của MatchDAO.
58. Hàm findById() truy vấn trận, đề, 2 người chơi và gọi lớp Match để đóng gói.
59. Lớp Match trả đối tượng Match cho hàm findById().
60. Hàm findById() trả đối tượng Match cho hàm getById().
61. Hàm getById() gọi hàm getTotalPoints() của PlayerDAO cho 2 người chơi và gán vào Player của trận.
62. Hàm getTotalPoints() trả tổng điểm cho hàm getById().
63. Hàm getById() trả trận dạng JSON cho MatchApiDAO.
64. Hàm toMatch() của MatchApiDAO gọi lớp Match để đóng gói dữ liệu JSON.
65. Lớp Match trả đối tượng Match cho MatchApiDAO.
66. MatchApiDAO trả đối tượng Match cho hàm loadResult().
67. Trong hàm runOnUiThread(), hàm loadResult() gọi hàm showResult() của chính nó.
68. Hàm showResult() gọi hàm getResult() của Match.
69. Match trả về kết quả của người chơi.
70. Hàm showResult() gọi hàm getFinishTime() của Match cho người chơi và đối thủ.
71. Match trả về thời gian làm bài.
72. Hàm showResult() gọi hàm getPoints() của Match.
73. Match trả về điểm trận của người chơi.
74. MatchActivity hiển thị lớp kết quả gồm kết quả, số câu đúng, thời gian làm bài, điểm trận, tổng điểm và câu hỏi chơi tiếp cho người chơi.

![Hình 28: Tuần tự thiết kế Chơi online – Thi đấu](tuan-tu-thiet-ke-uc03c.png)

*Hình 28: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn C: Thi đấu.*

- Biểu đồ có 74 thông điệp.
- Bước 2 lược bỏ thông điệp đóng gói Player bên trong SessionDAO, vì đã vẽ ở đoạn A.
- **Server làm trọng tài**, gồm 3 phần nghiệp vụ là hàm Java thuần của lớp thực thể:
  - **Chấm bài:** hàm countCorrect() của Match gọi hàm isCorrect() của MathProblem (bước 27–30).
  - **Điều kiện kết thúc:** hàm isOver() của Match (bước 34, 42).
  - **Thắng thua:** hàm getResult() và getPoints() của Match. App gọi 2 hàm này trên bản trận server trả về (bước 68–73).
- **Thời gian làm bài** tính theo đồng hồ server: thời điểm server nhận bài làm trừ thời điểm bắt đầu trận. Độ trễ khác nhau giữa 2 máy không làm sai kết quả do đồng hồ máy lệch nhau.
- Bước 42–49 diễn ra khi server xử lý yêu cầu nộp bài từ máy đối thủ. Máy này biết trận đã xong nhờ Firebase (bước 50–53).

### 1.4. Đoạn D: Chơi tiếp

1. Người chơi nhấn Chơi tiếp trên lớp kết quả của MatchActivity.
2. Hàm onClick() của nút Chơi tiếp gọi hàm sendChoice() của chính nó.
3. Hàm sendChoice() mở một Thread và gọi hàm continueMatch() của MatchApiDAO.
4. MatchApiDAO gửi yêu cầu POST /api/matches/{id}/continue kèm lựa chọn tới hàm continueMatch() của MatchRestController.
5. Hàm continueMatch() thấy đối thủ chưa chọn, ghi nhớ lựa chọn rồi gọi hàm writeChoice() của FirebaseAdminDAO.
6. Hàm continueMatch() trả trận hiện tại dạng JSON cho MatchApiDAO.
7. Hàm toMatch() của MatchApiDAO gọi lớp Match để đóng gói dữ liệu JSON.
8. Lớp Match trả đối tượng Match cho MatchApiDAO.
9. MatchApiDAO trả đối tượng Match cho hàm sendChoice().
10. Trong hàm runOnUiThread(), MatchActivity ẩn 2 nút và hiển thị thông báo đang chờ đối thủ.
11. Khi đối thủ cũng chọn chơi tiếp, hàm continueMatch(), lúc này xử lý yêu cầu từ máy đối thủ, gọi hàm startMatch() của chính nó.
12. Hàm startMatch() gọi hàm generateProblems() của ProblemDAO để sinh đề mới.
13. Hàm generateProblems() gọi lớp MathProblem để đóng gói từng câu hỏi.
14. Lớp MathProblem trả đối tượng MathProblem cho hàm generateProblems().
15. Hàm generateProblems() trả danh sách 10 câu hỏi cho hàm startMatch().
16. Hàm startMatch() gọi lớp Match để tạo trận mới với đề mới và 2 người chơi cũ.
17. Lớp Match trả đối tượng Match cho hàm startMatch().
18. Hàm startMatch() gọi hàm insert() của MatchDAO để lưu trận mới.
19. Hàm insert() trả kết quả cho hàm startMatch().
20. Hàm startMatch() gọi hàm writeMatch() của FirebaseAdminDAO để gửi đề mới cho cả 2 máy.
21. Hàm continueMatch() gọi hàm writeNextMatch() của FirebaseAdminDAO để ghi mã trận mới vào trận cũ.
22. Hàm onDataChange() của MatchFirebaseDAO thấy trận cũ đã có mã trận tiếp theo, gọi hàm onNextMatch() của MatchActivity.
23. Hàm onNextMatch() gọi hàm stopWatching() của MatchFirebaseDAO để ngừng theo dõi trận cũ.
24. Hàm onNextMatch() gọi hàm openNextMatch() mở MatchActivity mới với mã trận mới và đóng màn hiện tại; trận mới diễn ra như đoạn C.

![Hình 29: Tuần tự thiết kế Chơi online – Chơi tiếp](tuan-tu-thiet-ke-uc03d.png)

*Hình 29: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn D: Chơi tiếp.*

- Biểu đồ có 24 thông điệp, vẽ cho người chọn chơi tiếp trước.
- Hàm continueMatch() chạy tuần tự (synchronized). Nếu 2 yêu cầu chơi tiếp đến cùng lúc, chỉ yêu cầu đến sau tạo trận mới, nên không sinh 2 trận.
- Trận mới dùng lại hàm startMatch() của đoạn B (bước 11–20), không viết lại logic sinh đề.
- Máy này nhận trận mới qua Firebase (bước 22), mở MatchActivity mới và đi lại đoạn C.

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
37. Hàm getById() gọi hàm findById() của MatchDAO.
38. Hàm findById() truy vấn trận, đề, 2 người chơi và gọi lớp Match để đóng gói.
39. Lớp Match trả đối tượng Match cho hàm findById().
40. Hàm findById() trả đối tượng Match cho hàm getById().
41. Hàm getById() gọi hàm getTotalPoints() của PlayerDAO cho 2 người chơi và gán vào Player của trận.
42. Hàm getTotalPoints() trả tổng điểm cho hàm getById().
43. Hàm getById() trả trận dạng JSON, kèm đáp án đúng vì trận đã kết thúc, cho MatchApiDAO.
44. Hàm toMatch() của MatchApiDAO gọi lớp Match để đóng gói dữ liệu JSON.
45. Lớp Match trả đối tượng Match cho MatchApiDAO.
46. MatchApiDAO trả đối tượng Match cho hàm loadMatch().
47. Trong hàm runOnUiThread(), hàm loadMatch() gọi hàm showMatch() của chính nó.
48. Hàm showMatch() gọi hàm getResult() của Match.
49. Match trả về kết quả trận của người chơi.
50. Hàm showMatch() gọi hàm getPoints() của Match.
51. Match trả về điểm trận.
52. Hàm showMatch() gọi hàm getOpponent() của Match.
53. Match trả về đối thủ trong trận.
54. Hàm showMatch() gọi hàm getFinishTime() của Match cho người chơi và đối thủ.
55. Match trả về thời gian làm bài.
56. Hàm showMatch() tạo ProblemAdapter với 10 câu hỏi của trận, rồi gắn vào danh sách.
57. Hàm onBindViewHolder() của ProblemAdapter gọi hàm getExpression() của MathProblem.
58. MathProblem trả về chuỗi phép tính.
59. MatchDetailActivity hiển thị kết quả, điểm trận, số câu đúng và thời gian làm bài của 2 bên, 10 câu hỏi kèm đáp án đúng cho người chơi.
60. Người chơi nhấn Back.
61. MatchDetailActivity gọi hàm finish(), quay về HistoryActivity.
62. HistoryActivity hiển thị lại danh sách trận cho người chơi.

![Hình 30: Tuần tự thiết kế Xem lịch sử đấu – Xem trận của tôi](tuan-tu-thiet-ke-uc04a.png)

*Hình 30: Biểu đồ tuần tự chi tiết chức năng Xem lịch sử đấu, đoạn A: Xem trận của tôi và chi tiết trận.*

- Biểu đồ có 62 thông điệp.
- Bước 3–4 và 32–33 lược bỏ thông điệp đóng gói Player bên trong SessionDAO, vì đã vẽ ở chức năng Chơi online.
- **Có 2 lời gọi REST:** bước 7 lấy danh sách trận, bước 36 lấy một trận đầy đủ. Server đi theo khuôn MatchRestController → MatchDAO → Match.
- **Danh sách trận không kèm đề** (bước 9) để mỗi lần mở lịch sử chỉ đọc 2 bảng tblMatch, tblMatchPlayer. Đề và đáp án chỉ được đọc khi người chơi mở một trận (bước 38).
- **Biểu đồ tròn và từng dòng trận** đều dựa trên getResult(), getPoints(), getOpponent() của Match (bước 17, 22–27). Đây là cùng luật dùng khi kết thúc trận, nên lịch sử luôn khớp với kết quả đã báo cho 2 người.
- Lời gọi mạng chạy trong Thread (bước 6, 35). Phần vẽ giao diện chạy trong hàm runOnUiThread() (bước 16, 47).

### 2.2. Đoạn B: Xem bảng xếp hạng

1. Người chơi chọn thẻ Bảng xếp hạng trên HistoryActivity.
2. Hàm onTabSelected() của HistoryActivity ẩn khối Trận của tôi, hiện danh sách xếp hạng và gọi hàm loadRanking() của chính nó.
3. Hàm loadRanking() mở một Thread và gọi hàm getRanking() của PlayerApiDAO.
4. PlayerApiDAO gửi yêu cầu GET /api/players/ranking tới hàm getRanking() của PlayerRestController.
5. Hàm getRanking() của PlayerRestController gọi hàm getRanking() của PlayerDAO.
6. Hàm getRanking() của PlayerDAO gọi hàm findFinished() của MatchDAO.
7. Hàm findFinished() truy vấn các trận đã kết thúc kèm 2 người chơi, và gọi lớp Match để đóng gói từng trận.
8. Lớp Match trả đối tượng Match cho hàm findFinished().
9. Hàm findFinished() trả danh sách trận cho hàm getRanking().
10. Hàm getRanking() truy vấn các tài khoản có vai trò người chơi và gọi lớp Player để đóng gói từng người.
11. Lớp Player trả đối tượng Player cho hàm getRanking().
12. Với mỗi trận, hàm getRanking() gọi hàm getPoints() của Match để cộng điểm trận vào tổng điểm của 2 người.
13. Match trả về điểm trận.
14. Hàm getRanking() gọi hàm getResult() của Match để biết người thắng trận.
15. Match trả về kết quả trận.
16. Với trận thắng, hàm getRanking() gọi hàm getFinishTime() của Match.
17. Match trả về thời gian làm bài của người thắng.
18. Hàm rank() của PlayerDAO tính trung bình điểm đối thủ, trung bình thời gian thắng của từng người, rồi sắp xếp theo 3 tiêu chí.
19. Hàm getRanking() của PlayerDAO trả danh sách người chơi đã xếp hạng cho PlayerRestController.
20. Hàm getRanking() của PlayerRestController trả danh sách dạng JSON cho PlayerApiDAO.
21. Với mỗi phần tử JSON, hàm toPlayer() của PlayerApiDAO gọi lớp Player để đóng gói.
22. Lớp Player trả đối tượng Player cho PlayerApiDAO.
23. PlayerApiDAO trả danh sách người chơi cho hàm loadRanking().
24. Trong hàm runOnUiThread(), hàm loadRanking() tạo RankingAdapter với danh sách và id của người xem, rồi gắn vào danh sách xếp hạng.
25. RankingAdapter hiển thị bảng xếp hạng, dòng của người xem được tô nổi bật.
26. Người chơi nhấn Back.
27. HistoryActivity gọi hàm finish(), quay về MainActivity.
28. MainActivity hiển thị giao diện chính cho người chơi.

![Hình 31: Tuần tự thiết kế Xem lịch sử đấu – Xem bảng xếp hạng](tuan-tu-thiet-ke-uc04b.png)

*Hình 31: Biểu đồ tuần tự chi tiết chức năng Xem lịch sử đấu, đoạn B: Xem bảng xếp hạng.*

- Biểu đồ có 28 thông điệp.
- **Lời gọi REST** duy nhất là bước 4. Server đi theo khuôn PlayerRestController → PlayerDAO → MatchDAO → Match, Player.
- **Các tiêu chí không lưu trong CSDL** mà được tính lại ở bước 12–18 bằng getPoints(), getResult(), getFinishTime() của Match. Bảng xếp hạng vì vậy luôn khớp với kết quả các trận.
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
7. Hàm onCreate() gọi hàm watchOnlinePlayers() của PresenceFirebaseDAO để theo dõi danh sách người chơi online.
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
33. PresenceFirebaseDAO gọi hàm onPlayersChanged() của FriendActivity, truyền danh sách người chơi online.
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
53. Hàm sendRequest() trả lời mời dạng JSON cho FriendshipApiDAO.
54. Hàm toFriendship() của FriendshipApiDAO gọi lớp Friendship để đóng gói dữ liệu JSON.
55. Lớp Friendship trả đối tượng Friendship cho FriendshipApiDAO.
56. FriendshipApiDAO trả đối tượng Friendship cho hàm onClick().
57. Trong hàm runOnUiThread(), hàm onClick() thêm lời mời vào cuối danh sách bạn bè và gọi hàm setFriendships() của FriendAdapter.
58. FriendAdapter hiển thị dòng người nhận với trạng thái đang chờ trả lời; FriendActivity hiển thị thông báo đã gửi lời mời.

![Hình 32: Tuần tự thiết kế Quản lý bạn bè – Gửi lời mời kết bạn](tuan-tu-thiet-ke-uc07a.png)

*Hình 32: Biểu đồ tuần tự chi tiết chức năng Quản lý bạn bè, đoạn A: Gửi lời mời kết bạn.*

- Biểu đồ có 58 thông điệp.
- **Lời gọi REST** có 2 lần: bước 10 lấy lời mời và bạn bè, bước 38 gửi lời mời. Server đi theo khuôn FriendshipRestController → FriendshipDAO, PlayerDAO → Friendship, Player.
- **Firebase** chỉ dùng để biết bạn nào đang online (bước 7, 30–34). Bước 30 xảy ra mỗi khi có người vào hoặc thoát ứng dụng, nên trạng thái Online / Offline tự cập nhật mà không phải gọi server.
- **Luật nghiệp vụ** nằm ở thực thể Friendship: canAccept() tách lời mời đã nhận (bước 22), getFriend() lấy người còn lại trong quan hệ (bước 27). Kiểm tra trùng quan hệ theo cả 2 chiều nằm ở existsBetween() của FriendshipDAO (bước 47).
- Lời gọi mạng chạy trong Thread (bước 9, 37). Phần vẽ giao diện chạy trong hàm runOnUiThread() (bước 21, 57).

### 3.2. Đoạn B: Đồng ý lời mời kết bạn

Người chơi là người nhận; FriendActivity đã hiển thị các lời mời đã nhận như bước 1–35 của đoạn A.

1. Người chơi nhấn nút Đồng ý ở một lời mời trong danh sách lời mời đã nhận.
2. Hàm onClick() của FriendRequestAdapter gọi hàm onAccept() của FriendActivity.
3. Hàm onAccept() mở một Thread và gọi hàm accept() của FriendshipApiDAO.
4. FriendshipApiDAO gửi yêu cầu POST /api/friendships/{id}/accept?playerId={id} tới hàm accept() của FriendshipRestController.
5. Hàm accept() gọi hàm findById() của FriendshipDAO.
6. Hàm findById() truy vấn bảng tblFriendship nối với tblPlayer, và gọi lớp Friendship để đóng gói kết quả.
7. Lớp Friendship trả đối tượng Friendship cho hàm findById().
8. Hàm findById() trả đối tượng Friendship cho hàm accept().
9. Hàm accept() gọi hàm canAccept() của Friendship để kiểm tra lời mời còn đang chờ và người chơi là người nhận.
10. Friendship trả về hợp lệ cho hàm accept().
11. Hàm accept() gọi hàm updateStatus() của FriendshipDAO để chuyển lời mời sang trạng thái accepted.
12. Hàm updateStatus() cập nhật bảng tblFriendship và trả về thành công.
13. Hàm accept() gọi hàm getTotalPoints() của PlayerDAO cho người gửi lời mời.
14. Hàm getTotalPoints() trả tổng điểm cho hàm accept().
15. Hàm accept() trả quan hệ đã đồng ý dạng JSON cho FriendshipApiDAO.
16. Hàm toFriendship() của FriendshipApiDAO gọi lớp Friendship để đóng gói dữ liệu JSON.
17. Lớp Friendship trả đối tượng Friendship cho FriendshipApiDAO.
18. FriendshipApiDAO trả đối tượng Friendship cho hàm onAccept().
19. Trong hàm runOnUiThread(), hàm onAccept() bỏ lời mời khỏi danh sách lời mời đã nhận và gọi hàm setFriendships() của FriendRequestAdapter.
20. Hàm onAccept() thêm quan hệ vào danh sách bạn bè và gọi hàm setFriendships() của FriendAdapter.
21. Hàm onBindViewHolder() của FriendAdapter gọi hàm getFriend() của Friendship.
22. Friendship trả đối tượng Player của người gửi cho FriendAdapter.
23. FriendAdapter hiển thị người gửi trong danh sách bạn bè kèm tổng điểm và trạng thái online.

![Hình 33: Tuần tự thiết kế Quản lý bạn bè – Đồng ý lời mời kết bạn](tuan-tu-thiet-ke-uc07b.png)

*Hình 33: Biểu đồ tuần tự chi tiết chức năng Quản lý bạn bè, đoạn B: Đồng ý lời mời kết bạn.*

- Biểu đồ có 23 thông điệp.
- **Lời gọi REST** duy nhất là bước 4. Server không tin vào máy gửi yêu cầu: nó đọc lại lời mời từ CSDL (bước 5–8) rồi mới kiểm tra bằng canAccept() (bước 9).
- updateStatus() chỉ cập nhật dòng đang pending, nên 2 lần nhấn Đồng ý liên tiếp không làm hỏng dữ liệu.
- Trạng thái Online / Offline của người bạn mới lấy từ onlineIds mà FriendActivity đã có từ đoạn A, không cần gọi Firebase lại.
