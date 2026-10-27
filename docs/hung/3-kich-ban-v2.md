# KỊCH BẢN v2 VÀ BIỂU ĐỒ TUẦN TỰ PHA PHÂN TÍCH — CHƠI ONLINE, XEM LỊCH SỬ ĐẤU, QUẢN LÝ BẠN BÈ

---

## 1. Chức năng "Chơi online"

### 1.1. Đoạn A: Gửi lời thách đấu

1. Người chơi nhấn nút Chơi online trên MainView.
2. MainView gọi OnlineView.
3. OnlineView gọi hàm goOnline() của lớp Player để ghi người chơi vào danh sách online với trạng thái rỗi.
4. OnlineView gọi hàm getOnlinePlayers() của lớp Player.
5. Lớp Player trả về danh sách người chơi online, kèm tổng điểm và trạng thái, cho OnlineView.
6. OnlineView hiển thị tên, tổng điểm của người chơi và danh sách người chơi online.
7. Người chơi bấm vào tên một đối thủ đang rỗi trên OnlineView.
8. OnlineView gọi hàm sendChallenge() của lớp Challenge.
9. Lớp Challenge tạo lời mời đang chờ và trả về cho OnlineView.
10. OnlineView hiển thị thông báo đang chờ đối thủ trả lời.
11. OnlineView gọi hàm watchChallenge() của lớp Challenge để theo dõi lời mời.
12. Khi đối thủ đồng ý, lớp Challenge trả về lời mời đã được đồng ý, kèm trận đấu, cho OnlineView.
13. OnlineView gọi MatchView.

**Ngoại lệ 5a: Không có người chơi nào khác online.**

- 5a.1. Lớp Player trả về danh sách chỉ có người chơi cho OnlineView.
- 5a.2. OnlineView hiển thị thông báo chưa có ai khác online; danh sách tự cập nhật khi có người vào.

**Ngoại lệ 7a: Người chơi bấm vào một đối thủ đang bận.**

- 7a.1. OnlineView hiển thị thông báo đối thủ đang bận, không gọi sendChallenge().

**Ngoại lệ 11a: Người chơi hủy lời mời khi đang chờ.**

- 11a.1. Người chơi nhấn Hủy trên OnlineView.
- 11a.2. OnlineView gọi hàm rejectChallenge() của lớp Challenge.
- 11a.3. Lớp Challenge đánh dấu lời mời đã hủy và trả về cho OnlineView.
- 11a.4. OnlineView đóng thông báo chờ và giữ nguyên giao diện chơi online.

**Ngoại lệ 12a: Đối thủ từ chối lời mời.**

- 12a.1. Lớp Challenge trả về lời mời đã bị từ chối cho OnlineView.
- 12a.2. OnlineView hiển thị thông báo đối thủ đã từ chối và giữ nguyên giao diện chơi online.

![Hình 11: Tuần tự pha phân tích Chơi online – Gửi lời thách đấu](tuan-tu-phan-tich-uc03a.png)

*Hình 11: Biểu đồ tuần tự pha phân tích chức năng Chơi online, đoạn A: Gửi lời thách đấu.*

- Biểu đồ có 13 thông điệp, khớp với 13 bước của kịch bản chuẩn.
- MainView chỉ mở OnlineView (bước 1–2). Người chơi chỉ vào danh sách online khi OnlineView gọi goOnline() (bước 3), nên chỉ người đang ở giao diện chơi online mới được người khác thấy và thách đấu.
- Hàm watchChallenge() ở bước 11 là lời đăng ký theo dõi: kết quả không trả về ngay mà trả về ở bước 12, khi đối thủ trả lời.
- Bước 12 xảy ra khi đối thủ đồng ý ở đoạn B, nên giữa bước 11 và bước 12 có một khoảng chờ.

### 1.2. Đoạn B: Nhận lời thách đấu

Người chơi là người được mời, đang ở OnlineView; OnlineView đã gọi goOnline() như bước 3 của đoạn A.

1. OnlineView gọi hàm watchChallenge() của lớp Challenge để nhận các lời mời gửi tới người chơi.
2. Khi có lời mời mới, lớp Challenge trả về lời mời cho OnlineView.
3. OnlineView hiển thị hộp thoại lời mời gồm tên và tổng điểm của người mời.
4. Người chơi nhấn Đồng ý.
5. OnlineView gọi hàm createMatch() của lớp Match.
6. Lớp Match gọi hàm generateProblem() của lớp MathProblem để sinh đề.
7. Lớp MathProblem trả về các câu hỏi của đề cho lớp Match.
8. Lớp Match tạo trận với đề vừa sinh, chuyển 2 người chơi sang trạng thái bận và trả về trận cho OnlineView.
9. OnlineView gọi MatchView.

**Ngoại lệ 4a: Người chơi từ chối lời mời.**

- 4a.1. Người chơi nhấn Từ chối.
- 4a.2. OnlineView gọi hàm rejectChallenge() của lớp Challenge.
- 4a.3. Lớp Challenge đánh dấu lời mời bị từ chối và trả về cho OnlineView.
- 4a.4. OnlineView đóng hộp thoại lời mời.

**Ngoại lệ 8a: Không tạo được trận.**

- 8a.1. Lớp Match trả về cho OnlineView: người mời không còn online hoặc đã bận.
- 8a.2. OnlineView hiển thị thông báo không bắt đầu được trận đấu.

![Hình 12: Tuần tự pha phân tích Chơi online – Nhận lời thách đấu](tuan-tu-phan-tich-uc03b.png)

*Hình 12: Biểu đồ tuần tự pha phân tích chức năng Chơi online, đoạn B: Nhận lời thách đấu.*

- Biểu đồ có 9 thông điệp, khớp với 9 bước của kịch bản chuẩn.
- Lời mời chỉ tới được người chơi đang ở OnlineView, vì OnlineView là nơi đăng ký watchChallenge() (bước 1).
- Việc sinh đề nằm ở lớp thực thể (bước 6, 7). Cả 2 người chơi nhận đúng trận do lớp Match trả về, nên cùng một đề.

### 1.3. Đoạn C: Thi đấu

1. MatchView gọi hàm watchMatch() của lớp Match để theo dõi trận.
2. Lớp Match trả về trận đấu kèm đề bài cho MatchView.
3. MatchView hiển thị ô thời gian, trạng thái đối thủ và nút Thoát cho người chơi.
4. MatchView hiển thị số thứ tự, phép tính của câu đang làm và 4 ô phương án A, B, C, D cho người chơi.
5. Người chơi bấm một ô phương án trên MatchView; MatchView ghi phương án đã chọn, thời gian làm câu và chuyển sang câu sau.

   Các bước 4–5 lặp lại cho đến câu thứ 10.
6. MatchView hiển thị thông báo đã làm xong 10 câu và nút Nộp bài cho người chơi.
7. Người chơi nhấn Nộp bài trên MatchView.
8. MatchView gọi hàm submitAnswers() của lớp Match, gửi bài làm gồm phương án đã chọn và thời gian làm từng câu.
9. Lớp Match gọi hàm isCorrect() của lớp Answer để chấm từng câu của bài làm.
10. Lớp Answer gọi hàm isCorrect() của lớp MathProblem để so phương án đã chọn với đáp án đúng.
11. Lớp MathProblem trả kết quả so sánh cho lớp Answer.
12. Lớp Answer trả về câu đúng hay sai cho lớp Match.
13. Lớp Match lưu bài làm từng câu, số câu đúng, thời điểm nộp bài và trả về trận cho MatchView.
14. MatchView hiển thị thông báo đã nộp bài, đang chờ đối thủ.
15. Khi đối thủ cũng nộp bài, lớp Match gọi hàm isOver() của chính nó để kiểm tra trận đã xong.
16. Lớp Match trả về trận đã kết thúc cho MatchView, theo lời đăng ký ở bước 1.
17. MatchView gọi MatchResultView.
18. MatchResultView gọi hàm getResult() của lớp Match.
19. Lớp Match trả về kết quả của người chơi cho MatchResultView.
20. MatchResultView gọi hàm getFinishTime() của lớp Match.
21. Lớp Match trả về thời gian làm bài của 2 người chơi cho MatchResultView.
22. MatchResultView gọi hàm getPoints() của lớp Match.
23. Lớp Match trả về điểm trận của người chơi cho MatchResultView.
24. MatchResultView hiển thị kết quả, số câu đúng, thời gian làm bài, điểm trận, tổng điểm và câu hỏi chơi tiếp cho người chơi.

**Ngoại lệ 4a: Hết thời gian trước khi người chơi nộp bài.**

- 4a.1. MatchView hiển thị thông báo hết giờ.
- 4a.2. MatchView tự gọi hàm submitAnswers() của lớp Match với bài làm hiện có; câu chưa làm ghi là bỏ trống và tính là sai. Kịch bản tiếp tục từ bước 9.

**Ngoại lệ 4b: Người chơi thoát trận.**

- 4b.1. Người chơi nhấn Thoát trên MatchView và xác nhận thoát.
- 4b.2. MatchView gọi hàm forfeit() của lớp Match.
- 4b.3. Lớp Match đánh dấu người chơi bỏ trận, kết thúc trận và trả về cho MatchView.
- 4b.4. MatchView gọi OnlineView.
- 4b.5. OnlineView hiển thị giao diện chơi online cho người chơi.

**Ngoại lệ 13a: Không nộp được bài.**

- 13a.1. Lớp Match trả về lỗi cho MatchView.
- 13a.2. MatchView hiển thị thông báo không kết nối được máy chủ và hiện lại nút Nộp bài. Kịch bản tiếp tục từ bước 7.

**Ngoại lệ 16a: Đối thủ bỏ trận.**

- 16a.1. Lớp Match trả về trận đã kết thúc do đối thủ bỏ trận cho MatchView.
- 16a.2. MatchView hiển thị thông báo đối thủ đã thoát. Kịch bản tiếp tục từ bước 17; MatchResultView không hiển thị lựa chọn chơi tiếp.

![Hình 13: Tuần tự pha phân tích Chơi online – Thi đấu](tuan-tu-phan-tich-uc03c.png)

*Hình 13: Biểu đồ tuần tự pha phân tích chức năng Chơi online, đoạn C: Thi đấu.*

- Biểu đồ có 24 thông điệp, khớp với 24 bước của kịch bản chuẩn. Khung loop 10 câu bọc bước 4–5.
- Trong lúc làm bài, MatchView chỉ ghi phương án đã chọn và thời gian làm từng câu, không gọi thực thể nào và không báo đúng sai. Bài làm 10 câu được gửi một lần khi nộp (bước 8).
- Chấm bài (bước 9–12) và kiểm tra trận đã xong (bước 15) đều do lớp thực thể làm, giao diện không tự chấm. Answer chấm từng câu bằng luật isCorrect() của MathProblem.
- Bước 16 trả về theo lời đăng ký watchMatch() ở bước 1, khi đối thủ nộp bài.
- Kết quả, thời gian, điểm trận đều là thuộc tính dẫn xuất, được tính bằng getResult(), getFinishTime(), getPoints() (bước 18–23).

### 1.4. Đoạn D: Chơi tiếp

1. Người chơi nhấn Chơi tiếp trên MatchResultView.
2. MatchResultView gọi hàm continueMatch() của lớp Match.
3. Lớp Match ghi nhận lựa chọn chơi tiếp và trả về cho MatchResultView.
4. MatchResultView hiển thị thông báo đang chờ đối thủ.
5. MatchResultView gọi hàm watchMatch() của lớp Match để biết lựa chọn của đối thủ.
6. Khi đối thủ cũng chọn chơi tiếp, lớp Match gọi hàm generateProblem() của lớp MathProblem để sinh đề mới.
7. Lớp MathProblem trả về các câu hỏi của đề mới cho lớp Match.
8. Lớp Match tạo trận mới và trả về cho MatchResultView.
9. MatchResultView gọi MatchView.
10. MatchView hiển thị giao diện thi đấu với câu đầu tiên của đề mới cho người chơi; trận tiếp theo diễn ra như đoạn C.

**Ngoại lệ 1a: Người chơi dừng chơi.**

- 1a.1. Người chơi nhấn Dừng trên MatchResultView.
- 1a.2. MatchResultView gọi hàm continueMatch() của lớp Match với lựa chọn dừng.
- 1a.3. Lớp Match ghi nhận, trả 2 người chơi về trạng thái rỗi và trả về cho MatchResultView.
- 1a.4. MatchResultView gọi OnlineView.
- 1a.5. OnlineView hiển thị giao diện chơi online cho người chơi.

**Ngoại lệ 3a: Đối thủ đã chọn chơi tiếp trước.**

- 3a.1. Lớp Match gọi hàm generateProblem() của lớp MathProblem để sinh đề mới, tạo trận mới và trả về ngay cho MatchResultView. Kịch bản tiếp tục từ bước 9.

**Ngoại lệ 8a: Đối thủ dừng chơi.**

- 8a.1. Lớp Match trả về trận đã dừng cho MatchResultView.
- 8a.2. MatchResultView hiển thị thông báo đối thủ đã dừng chơi.
- 8a.3. MatchResultView gọi OnlineView.
- 8a.4. OnlineView hiển thị giao diện chơi online cho người chơi.

![Hình 14: Tuần tự pha phân tích Chơi online – Chơi tiếp](tuan-tu-phan-tich-uc03d.png)

*Hình 14: Biểu đồ tuần tự pha phân tích chức năng Chơi online, đoạn D: Chơi tiếp.*

- Biểu đồ có 10 thông điệp, khớp với 10 bước của kịch bản chuẩn, vẽ cho người chọn chơi tiếp trước.
- Trận mới chỉ được tạo khi cả 2 người đã chọn chơi tiếp (bước 6–8). Người chọn sau đi theo ngoại lệ 3a.

---

## 2. Chức năng "Xem lịch sử đấu"

### 2.1. Đoạn A: Xem trận của tôi và chi tiết trận

1. Người chơi nhấn nút Lịch sử đấu trên OnlineView.
2. OnlineView gọi HistoryView.
3. HistoryView gọi hàm getHistory() của lớp Match.
4. Lớp Match trả về danh sách các trận đã kết thúc của người chơi, mới nhất đứng đầu, cho HistoryView.
5. HistoryView gọi hàm getResult() của lớp Match cho từng trận.
6. Lớp Match trả về kết quả trận của người chơi cho HistoryView.
7. HistoryView gọi hàm getPoints() của lớp Match.
8. Lớp Match trả về điểm trận cho HistoryView.
9. HistoryView gọi hàm getOpponent() của lớp Match.
10. Lớp Match trả về đối thủ trong trận cho HistoryView.
11. HistoryView đếm số trận thắng, hòa, thua, hiển thị biểu đồ tròn tỉ lệ thắng, hòa, thua và danh sách trận cho người chơi.
12. Người chơi bấm vào một trận trên HistoryView.
13. HistoryView gọi MatchDetailView.
14. MatchDetailView gọi hàm getMatch() của lớp Match.
15. Lớp Match trả về trận kèm đề, đáp án đúng, 2 người chơi và bài làm từng câu của mỗi người cho MatchDetailView.
16. MatchDetailView gọi hàm getResult() của lớp Match.
17. Lớp Match trả về kết quả trận cho MatchDetailView.
18. MatchDetailView gọi hàm getPoints() của lớp Match.
19. Lớp Match trả về điểm trận cho MatchDetailView.
20. MatchDetailView gọi hàm getOpponent() của lớp Match.
21. Lớp Match trả về đối thủ trong trận cho MatchDetailView.
22. MatchDetailView gọi hàm getFinishTime() của lớp Match cho người chơi và đối thủ.
23. Lớp Match trả về thời gian làm bài cho MatchDetailView.
24. MatchDetailView gọi hàm isCorrect() của lớp Answer cho từng câu trong bài làm của 2 người chơi.
25. Lớp Answer gọi hàm isCorrect() của lớp MathProblem để so phương án đã chọn với đáp án đúng.
26. Lớp MathProblem trả kết quả so sánh cho lớp Answer.
27. Lớp Answer trả về câu đúng hay sai cho MatchDetailView.
28. MatchDetailView hiển thị kết quả, điểm trận, số câu đúng và thời gian làm bài của 2 bên, và 10 câu, mỗi câu có phép tính kèm đáp án đúng và, với mỗi người chơi, đúng / sai, phương án đã chọn, thời gian làm câu cho người chơi.
29. Người chơi nhấn Back trên MatchDetailView.
30. MatchDetailView gọi HistoryView.
31. HistoryView hiển thị lại danh sách trận cho người chơi.

**Ngoại lệ 4a: Người chơi chưa đấu trận nào.**

- 4a.1. Lớp Match trả về danh sách rỗng cho HistoryView.
- 4a.2. HistoryView hiển thị biểu đồ trống và thông báo người chơi chưa đấu trận nào.

**Ngoại lệ 4b: Không lấy được lịch sử đấu.**

- 4b.1. Lớp Match trả về lỗi cho HistoryView.
- 4b.2. HistoryView hiển thị thông báo không tải được lịch sử đấu, biểu đồ và danh sách để trống.

**Ngoại lệ 15a: Không lấy được chi tiết trận.**

- 15a.1. Lớp Match trả về lỗi cho MatchDetailView.
- 15a.2. MatchDetailView hiển thị thông báo không tải được chi tiết trận, các mục kết quả và từng câu để trống.

![Hình 15: Tuần tự pha phân tích Xem lịch sử đấu – Xem trận của tôi](tuan-tu-phan-tich-uc04a.png)

*Hình 15: Biểu đồ tuần tự pha phân tích chức năng Xem lịch sử đấu, đoạn A: Xem trận của tôi và chi tiết trận.*

- Biểu đồ có 31 thông điệp, khớp với 31 bước của kịch bản chuẩn.
- Kết quả, điểm và đối thủ của từng dòng trong danh sách đều lấy từ Match (bước 5–10). Biểu đồ tròn là phép đếm trên các kết quả này, không lưu ở đâu.
- Chi tiết trận cần đề, đáp án đúng và bài làm từng câu nên gọi getMatch() để lấy trận đầy đủ (bước 14), thay vì dùng lại trận trong danh sách vốn không kèm đề và bài làm.
- Đúng / sai của từng ô bài làm do Answer tính bằng isCorrect() (bước 24–27), cùng luật chấm với lúc nộp bài; phương án đã chọn và thời gian làm câu là thuộc tính của Answer.

### 2.2. Đoạn B: Xem bảng xếp hạng

Người chơi đang ở OnlineView.

1. Người chơi nhấn nút Bảng xếp hạng trên OnlineView.
2. OnlineView gọi HistoryView, mở sẵn thẻ Bảng xếp hạng.
3. HistoryView gọi hàm getRanking() của lớp Player.
4. Lớp Player gọi hàm getPoints() của lớp Match cho từng trận đã kết thúc.
5. Lớp Match trả về điểm trận của người chơi cho lớp Player.
6. Lớp Player gọi hàm getResult() của lớp Match để biết các trận thắng.
7. Lớp Match trả về kết quả trận cho lớp Player.
8. Lớp Player gọi hàm getFinishTime() của lớp Match cho các trận thắng.
9. Lớp Match trả về thời gian làm bài cho lớp Player.
10. Lớp Player tính 3 tiêu chí, sắp xếp và trả danh sách người chơi cho HistoryView.
11. HistoryView hiển thị bảng xếp hạng ở thẻ Bảng xếp hạng cho người chơi.
12. Người chơi nhấn Back trên HistoryView.
13. HistoryView gọi OnlineView.
14. OnlineView hiển thị giao diện chơi online cho người chơi.

**Ngoại lệ 10a: Không lấy được bảng xếp hạng.**

- 10a.1. Lớp Player trả về lỗi cho HistoryView.
- 10a.2. HistoryView hiển thị thông báo không tải được bảng xếp hạng, danh sách để trống.

**Ngoại lệ 12a: Người chơi chọn thẻ Trận của tôi.**

- 12a.1. Người chơi chọn thẻ Trận của tôi trên HistoryView.
- 12a.2. HistoryView hiển thị biểu đồ tỉ lệ thắng, hòa, thua và danh sách trận như bước 3–11 của đoạn A. Người chơi chọn lại thẻ Bảng xếp hạng thì HistoryView hiển thị lại bảng xếp hạng.

![Hình 16: Tuần tự pha phân tích Xem lịch sử đấu – Xem bảng xếp hạng](tuan-tu-phan-tich-uc04b.png)

*Hình 16: Biểu đồ tuần tự pha phân tích chức năng Xem lịch sử đấu, đoạn B: Xem bảng xếp hạng.*

- Biểu đồ có 14 thông điệp, khớp với 14 bước của kịch bản chuẩn.
- Bảng xếp hạng mở thẳng từ nút Bảng xếp hạng của OnlineView (bước 1–2), không phải qua thẻ Trận của tôi; 2 thẻ vẫn thuộc cùng một HistoryView.
- Lớp Player tính các tiêu chí dựa trên 3 phương thức của lớp Match (bước 4–9). Bảng xếp hạng vì vậy dùng đúng luật thắng thua của trận đấu.

---

## 3. Chức năng "Quản lý bạn bè"

### 3.1. Đoạn A: Gửi lời mời kết bạn

1. Người chơi nhấn nút Bạn bè trên OnlineView.
2. OnlineView gọi FriendView.
3. FriendView gọi hàm getFriendships() của lớp Friendship.
4. Lớp Friendship trả về các lời mời kết bạn và quan hệ bạn bè của người chơi cho FriendView.
5. FriendView gọi hàm getOnlinePlayers() của lớp Player.
6. Lớp Player trả về danh sách người chơi online cho FriendView.
7. FriendView gọi hàm getFriend() của lớp Friendship để lấy người còn lại trong từng quan hệ.
8. Lớp Friendship trả về người bạn cho FriendView.
9. FriendView hiển thị các lời mời đã nhận và danh sách bạn bè kèm tổng điểm, trạng thái online cho người chơi.
10. Người chơi nhập tên đăng nhập của một người và nhấn Kết bạn trên FriendView.
11. FriendView gọi hàm sendRequest() của lớp Friendship.
12. Lớp Friendship tìm người nhận theo tên đăng nhập, tạo lời mời đang chờ và trả về cho FriendView.
13. FriendView hiển thị thông báo đã gửi lời mời và thêm dòng đang chờ trả lời vào cuối danh sách bạn bè.

**Ngoại lệ 4a: Không lấy được danh sách bạn bè.**

- 4a.1. Lớp Friendship trả về lỗi cho FriendView.
- 4a.2. FriendView hiển thị thông báo không kết nối được máy chủ, 2 danh sách để trống.

**Ngoại lệ 9a: Người chơi từ chối một lời mời đã nhận.**

- 9a.1. Người chơi nhấn Từ chối ở một lời mời trên FriendView.
- 9a.2. FriendView gọi hàm delete() của lớp Friendship.
- 9a.3. Lớp Friendship xóa lời mời và trả về cho FriendView.
- 9a.4. FriendView bỏ lời mời khỏi danh sách lời mời đã nhận.

**Ngoại lệ 9b: Người chơi hủy kết bạn hoặc rút lại lời mời đã gửi.**

- 9b.1. Người chơi nhấn nút Hủy kết bạn ở một dòng của danh sách bạn bè trên FriendView.
- 9b.2. FriendView hỏi người chơi có chắc muốn hủy kết bạn không.
- 9b.3. Người chơi đồng ý.
- 9b.4. FriendView gọi hàm delete() của lớp Friendship.
- 9b.5. Lớp Friendship xóa quan hệ và trả về cho FriendView.
- 9b.6. FriendView bỏ dòng đó khỏi danh sách bạn bè.

**Ngoại lệ 10a: Người chơi để trống ô tên đăng nhập.**

- 10a.1. FriendView hiển thị thông báo yêu cầu nhập tên đăng nhập, không gọi sendRequest().

**Ngoại lệ 12a: Không có người chơi nào có tên đăng nhập đã nhập.**

- 12a.1. Lớp Friendship trả về lỗi không tìm thấy người chơi cho FriendView.
- 12a.2. FriendView hiển thị thông báo không tìm thấy người chơi, giữ nguyên nội dung ô tên đăng nhập.

**Ngoại lệ 12b: Tên đăng nhập là của chính người chơi.**

- 12b.1. Lớp Friendship trả về lỗi không thể kết bạn với chính mình cho FriendView.
- 12b.2. FriendView hiển thị thông báo không thể kết bạn với chính mình.

**Ngoại lệ 12c: Hai người đã là bạn bè hoặc đã có lời mời giữa 2 người.**

- 12c.1. Lớp Friendship trả về lỗi đã có quan hệ cho FriendView.
- 12c.2. FriendView hiển thị thông báo đã có lời mời hoặc đã là bạn bè, không thêm dòng mới.

![Hình 17: Tuần tự pha phân tích Quản lý bạn bè – Gửi lời mời kết bạn](tuan-tu-phan-tich-uc07a.png)

*Hình 17: Biểu đồ tuần tự pha phân tích chức năng Quản lý bạn bè, đoạn A: Gửi lời mời kết bạn.*

- Biểu đồ có 13 thông điệp, khớp với 13 bước của kịch bản chuẩn.
- Trạng thái online của bạn bè không nằm trong Friendship: FriendView lấy danh sách online từ Player (bước 5–6) rồi so với người bạn trong từng quan hệ (bước 7–8).
- Việc tìm người nhận theo tên đăng nhập và kiểm tra trùng quan hệ nằm trong sendRequest() của Friendship (bước 12), không nằm ở giao diện.

### 3.2. Đoạn B: Đồng ý lời mời kết bạn

Người chơi là người nhận, đang ở FriendView; các lời mời đã nhận được hiển thị như bước 1–9 của đoạn A.

1. Người chơi nhấn Đồng ý ở một lời mời trên FriendView.
2. FriendView gọi hàm accept() của lớp Friendship.
3. Lớp Friendship gọi hàm canAccept() của chính nó để kiểm tra lời mời còn đang chờ và người chơi là người nhận.
4. Lớp Friendship chuyển lời mời sang trạng thái đã là bạn bè và trả về cho FriendView.
5. FriendView gọi hàm getFriend() của lớp Friendship.
6. Lớp Friendship trả về người gửi lời mời cho FriendView.
7. FriendView bỏ lời mời khỏi danh sách lời mời đã nhận và hiển thị người gửi trong danh sách bạn bè cho người chơi.

**Ngoại lệ 1a: Người chơi từ chối lời mời.**

- 1a.1. Người chơi nhấn Từ chối ở lời mời trên FriendView.
- 1a.2. FriendView gọi hàm delete() của lớp Friendship.
- 1a.3. Lớp Friendship xóa lời mời và trả về cho FriendView.
- 1a.4. FriendView bỏ lời mời khỏi danh sách lời mời đã nhận.

**Ngoại lệ 3a: Lời mời không còn, vì người gửi đã rút lại.**

- 3a.1. Lớp Friendship trả về lỗi lời mời không còn cho FriendView.
- 3a.2. FriendView hiển thị thông báo lời mời không còn và bỏ lời mời khỏi danh sách lời mời đã nhận.

![Hình 18: Tuần tự pha phân tích Quản lý bạn bè – Đồng ý lời mời kết bạn](tuan-tu-phan-tich-uc07b.png)

*Hình 18: Biểu đồ tuần tự pha phân tích chức năng Quản lý bạn bè, đoạn B: Đồng ý lời mời kết bạn.*

- Biểu đồ có 7 thông điệp, khớp với 7 bước của kịch bản chuẩn.
- Luật "chỉ người nhận mới đồng ý được lời mời đang chờ" là phương thức canAccept() của Friendship (bước 3), nên không máy nào đồng ý thay cho người khác.
