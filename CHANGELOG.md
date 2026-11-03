# Changelog

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
