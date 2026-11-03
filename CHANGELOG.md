# Changelog

## Bản 15 (03/11/2026)

### Tài liệu
- 6 biểu đồ tuần tự thiết kế trong VP vẽ người chơi bằng actor hình người thay cho lifeline hình chữ nhật (đoạn Thi đấu có cả người chơi A và B); xuất lại ảnh.

## Bản 14 (03/11/2026)

### Tài liệu
- Dựng lại 6 biểu đồ tuần tự thiết kế (VP và puml) và kịch bản v3 theo cách vẽ gọn:
  - View chỉ gọi Activity, DAO; không gọi hàm nghiệp vụ của entity. DAO trả đối tượng về, Activity xử lý trong mã nguồn.
  - DAO gọi entity bằng `call`, entity tự gọi hàm khởi tạo (`Player()`, `Match()`...) hoặc setter khi đóng gói yêu cầu, rồi `return`.
  - Mọi mũi tên trả về giữa các lớp ghi `return`; Activity trả giao diện cho người chơi ghi `display`, và có bước display trước mỗi thao tác của người chơi và ở cuối biểu đồ.
  - Nhãn Activity bỏ chi tiết phía sau (`onClick()`), bỏ tên biến giao diện; TabLayout gộp vào HistoryActivity.
  - Lời đăng ký theo dõi Firebase (`watch...`) vẽ bất đồng bộ; v3 giải thích vì sao không có thông điệp trả về và cách xử lý nhiều người thao tác cùng lúc.
- Đoạn C (Thi đấu) vẽ 2 người chơi: A bên trái, B bên phải, các lớp ghi (A), (B); A nộp trước, B nộp sau, server kết thúc trận và cả 2 máy nhận kết quả.
- Số thông điệp: Chơi online A 49, B 39, C 102, D 35; Xem lịch sử đấu A 49, B 32.

## Bản 13 (03/11/2026)

### Code
- Adapter và ViewHolder của danh sách thành lớp nội bộ trong Activity dùng chúng: PlayerAdapter trong OnlineActivity, MatchHistoryAdapter và RankingAdapter trong HistoryActivity, ProblemAdapter trong MatchDetailActivity. Bỏ BaseAdapter.
- PieChartView thành lớp nội bộ của HistoryActivity; activity_history.xml tham chiếu `HistoryActivity$PieChartView`.
- Bảng xếp hạng chỉ hiện hạng, tên, tổng điểm; bỏ 2 cột trung bình điểm đối thủ và trung bình thời gian thắng. Server vẫn sắp xếp theo đủ 3 tiêu chí.
- Comment trong common dịch sang tiếng Anh, mọi comment 1 dòng; bỏ import thừa.

### Tài liệu
- Biểu đồ lớp chi tiết (VP và puml) không còn Adapter, ViewHolder, item_*.xml, PieChartView; Activity kết tập trực tiếp danh sách Player, Match.
- Thêm các hàm nghiệp vụ còn thiếu: bỏ trận (MatchActivity.leaveMatch, forfeit; MatchRestController.forfeit; MatchDAO.updateForfeit; FirebaseAdminDAO.writeForfeited, writeStopped), hết giờ (onFinish), lời mời (showChallengeDialog), rời màn online (onDestroy), ProblemDAO.generateProblem, Match.judge.
- Dựng lại 3 biểu đồ tuần tự thiết kế (Chơi online đoạn A, Xem lịch sử đấu đoạn A, B) không có lifeline Adapter, PieChartView; số thông điệp giữ nguyên. Lời đăng ký theo dõi Firebase (watch...) vẽ bằng thông điệp bất đồng bộ.
- Kịch bản v3 giải thích vì sao bước đăng ký theo dõi không có thông điệp trả về và vì sao nhiều người thao tác cùng lúc không làm sai trạng thái.
- Mô tả lớp, kịch bản v1, đặc tả chung cập nhật theo bảng xếp hạng 3 cột; sửa các chỗ lệch code (số cờ của MatchActivity, BaseAdapter).

## Bản 12 (02/11/2026)
- DAO của app viết ít hàm nhất; ApiDAO còn một hàm request.
- Biểu đồ lớp chi tiết chỉ còn lớp và hàm thuộc logic chức năng.
