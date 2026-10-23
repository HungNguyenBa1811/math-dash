# KỊCH BẢN v2 VÀ BIỂU ĐỒ TUẦN TỰ PHA PHÂN TÍCH — CHƠI ONLINE, XEM LỊCH SỬ ĐẤU

---

## 1. Chức năng "Chơi online"

### 1.1. Đoạn A: Tạo phòng

1. Người chơi nhấn nút Chơi online trên MainView.
2. MainView gọi OnlineView.
3. OnlineView hiển thị giao diện chơi online cho người chơi.
4. Người chơi nhấn nút Tạo phòng trên OnlineView.
5. OnlineView gọi hàm createMatch() của lớp Match.
6. Lớp Match tạo trận mới ở trạng thái chờ và trả về cho OnlineView.
7. OnlineView gọi LobbyView.
8. LobbyView hiển thị mã phòng và trạng thái chờ đối thủ cho người chơi.
9. LobbyView gọi hàm watchMatch() của lớp Match để theo dõi trận.
10. Khi có đối thủ vào phòng, lớp Match trả về trận đã bắt đầu cho LobbyView.
11. LobbyView hiển thị thông báo trận đấu bắt đầu và tên đối thủ cho người chơi.
12. LobbyView gọi OnlineGameView.

**Ngoại lệ 9a: Người chơi hủy phòng trước khi có đối thủ.**

- 9a.1. Người chơi nhấn nút Hủy trên LobbyView.
- 9a.2. LobbyView gọi hàm deleteMatch() của lớp Match.
- 9a.3. Lớp Match xóa phòng và trả về cho LobbyView.
- 9a.4. LobbyView gọi MainView.
- 9a.5. MainView hiển thị giao diện chính cho người chơi.

![Hình 8: Tuần tự pha phân tích Chơi online – Tạo phòng](tuan-tu-phan-tich-uc05a.png)

*Hình 8: Biểu đồ tuần tự pha phân tích chức năng Chơi online, đoạn A: Tạo phòng.*

- Biểu đồ có 12 thông điệp, khớp với 12 bước của kịch bản chuẩn.
- Hàm watchMatch() ở bước 9 là lời đăng ký theo dõi: kết quả không trả về ngay mà trả về ở bước 10, khi trận thay đổi.
- Bước 10 xảy ra khi đối thủ vào phòng ở đoạn B, nên giữa bước 9 và bước 10 có thể là một khoảng chờ.

### 1.2. Đoạn B: Tìm phòng

1. Người chơi nhấn nút Chơi online trên MainView.
2. MainView gọi OnlineView.
3. OnlineView hiển thị giao diện chơi online cho người chơi.
4. Người chơi nhập mã phòng và nhấn nút Vào phòng trên OnlineView.
5. OnlineView gọi hàm joinMatch() của lớp Match.
6. Lớp Match gọi hàm canJoin() của chính nó để kiểm tra người chơi có vào được phòng không.
7. Lớp Match thêm người chơi vào trận, chuyển trận sang trạng thái đang chơi, rồi trả về cho OnlineView.
8. OnlineView gọi LobbyView.
9. LobbyView hiển thị thông báo trận đấu bắt đầu và tên đối thủ cho người chơi.
10. LobbyView gọi OnlineGameView.

**Ngoại lệ 7a: Người chơi không vào được phòng.**

- 7a.1. Lớp Match trả về cho OnlineView: không tìm thấy phòng, hoặc phòng đã đầy.
- 7a.2. OnlineView hiển thị thông báo không vào được phòng cho người chơi.

![Hình 9: Tuần tự pha phân tích Chơi online – Tìm phòng](tuan-tu-phan-tich-uc05b.png)

*Hình 9: Biểu đồ tuần tự pha phân tích chức năng Chơi online, đoạn B: Tìm phòng.*

- Biểu đồ có 10 thông điệp, khớp với kịch bản chuẩn.
- Luật vào phòng nằm trong lớp thực thể Match (bước 6, thông điệp gọi chính nó), không nằm ở giao diện.

### 1.3. Đoạn C: Thi đấu

1. OnlineGameView gọi hàm watchMatch() của lớp Match để theo dõi điểm đối thủ.
2. OnlineGameView gọi hàm generateProblem() của lớp MathProblem.
3. Lớp MathProblem trả về câu hỏi cho OnlineGameView.
4. OnlineGameView hiển thị điểm, đồng hồ, câu hỏi và bàn phím số cho người chơi.
5. Người chơi nhập đáp án và nhấn OK trên OnlineGameView.
6. OnlineGameView gọi hàm submitAnswer() của lớp GameSession.
7. Lớp GameSession chấm đáp án và trả về điểm mới cho OnlineGameView.
8. OnlineGameView gọi hàm updateScore() của lớp MatchPlayer.
9. Lớp MatchPlayer ghi điểm mới để máy đối thủ thấy, rồi trả về cho OnlineGameView.
10. OnlineGameView gọi hàm generateProblem() của lớp MathProblem.
11. Lớp MathProblem trả về câu hỏi tiếp theo.
12. OnlineGameView hiển thị điểm mới và câu hỏi mới cho người chơi.
13. Khi đối thủ ghi điểm, lớp Match trả về trận mới cho OnlineGameView.
14. OnlineGameView hiển thị điểm đối thủ cho người chơi.

Các bước 5–14 lặp lại cho đến khi hết giờ.

15. Hết giờ, OnlineGameView khóa bàn phím số và hiển thị thông báo chờ kết quả.
16. OnlineGameView gọi hàm finishMatch() của lớp Match.
17. Khi cả hai người chơi đã hết giờ, lớp Match lưu trận vào lịch sử đấu và trả về trận đã kết thúc cho OnlineGameView.
18. OnlineGameView gọi hàm getResult() của lớp Match.
19. Lớp Match trả về kết quả trận.
20. OnlineGameView gọi MatchResultView.
21. MatchResultView hiển thị kết quả và tỉ số cho người chơi.
22. Người chơi nhấn nút Về menu trên MatchResultView.
23. MatchResultView gọi MainView.
24. MainView hiển thị giao diện chính cho người chơi.

**Ngoại lệ 5a: Người chơi thoát trận.**

- 5a.1. Người chơi nhấn quay lại. OnlineGameView hỏi người chơi có chắc muốn thoát không, vì thoát sẽ bị xử thua; người chơi chọn Thoát.
- 5a.2. OnlineGameView gọi hàm forfeit() của lớp MatchPlayer.
- 5a.3. Lớp MatchPlayer ghi người chơi đã bỏ trận, rồi trả về cho OnlineGameView.
- 5a.4. OnlineGameView gọi hàm finishMatch() của lớp Match.
- 5a.5. Lớp Match lưu trận và trả về cho OnlineGameView.
- 5a.6. OnlineGameView gọi MainView.
- 5a.7. MainView hiển thị giao diện chính cho người chơi.

**Ngoại lệ 13a: Đối thủ bỏ trận.**

- 13a.1. Lớp Match trả về cho OnlineGameView trận mới, trong đó đối thủ đã bỏ trận.
- 13a.2. OnlineGameView hiển thị thông báo đối thủ đã thoát, rồi đi tiếp từ bước 16. Ở bước 19, người chơi được tính thắng.

![Hình 10: Tuần tự pha phân tích Chơi online – Thi đấu](tuan-tu-phan-tich-uc05c.png)

*Hình 10: Biểu đồ tuần tự pha phân tích chức năng Chơi online, đoạn C: Thi đấu.*

- Biểu đồ có 24 thông điệp, khớp với kịch bản chuẩn.
- Khung loop bao các bước 5–14, lặp đến khi hết giờ.
- Chấm điểm thuộc GameSession (bước 6–7). Thắng thua thuộc Match (bước 18–19). Giao diện chỉ hiển thị.
- Hai máy chạy giống nhau; biểu đồ vẽ từ phía một máy.

---

## 2. Chức năng "Xem lịch sử đấu"

1. Người chơi nhấn nút Lịch sử đấu trên MainView.
2. MainView gọi HistoryView.
3. HistoryView gọi hàm getHistory() của lớp Match.
4. Lớp Match trả về cho HistoryView danh sách các trận đã kết thúc của người chơi, trận mới nhất xếp trước.
5. Với mỗi trận, HistoryView gọi hàm getResult() của lớp Match.
6. Lớp Match trả về kết quả của trận.
7. HistoryView hiển thị danh sách trận đấu cho người chơi.
8. Người chơi nhấn nút quay lại trên HistoryView.
9. HistoryView gọi MainView.
10. MainView hiển thị giao diện chính cho người chơi.

**Ngoại lệ 4a: Người chơi chưa đấu trận nào.**

- 4a.1. Lớp Match trả về danh sách rỗng cho HistoryView.
- 4a.2. HistoryView hiển thị thông báo chưa có trận nào. Kịch bản đi tiếp từ bước 8.

![Hình 11: Tuần tự pha phân tích Xem lịch sử đấu](tuan-tu-phan-tich-uc06.png)

*Hình 11: Biểu đồ tuần tự pha phân tích chức năng Xem lịch sử đấu.*

- Biểu đồ có 10 thông điệp, khớp với kịch bản chuẩn.
- Bước 5–6 nằm trong khung loop, vì mỗi dòng của danh sách cần một kết quả.
- Kết quả không được lưu sẵn mà tính lại bằng hàm getResult().
