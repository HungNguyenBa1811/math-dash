CHIẾN LƯỢC PHÂN CHIA MODULE

Vì tuần tới thành viên 2 bận, chúng ta sẽ chia thành 2 module độc lập hoàn toàn. Bạn (Nhóm trưởng) sẽ làm toàn bộ Core Logic và Backend (Module 1) từ bây giờ. Tuần sau, thành viên 2 sẽ ghép Giao diện, Local Data và làm Báo cáo (Module 2). Khi cả 2 xong sẽ bước vào Phase tích hợp.


MODULE 1: CORE LOGIC & REALTIME MATCHMAKING (Nhóm trưởng - Có thể làm ngay)
Module này chạy ngầm, bạn có thể test bằng log mà không cần giao diện hoàn chỉnh.

1. Khởi tạo project trên Android Studio và kết nối project với Firebase.
2. Xây dựng module sinh câu hỏi toán học: Tự động tạo phép tính ngẫu nhiên, tăng dần độ khó, đảm bảo kết quả không bị âm (nếu cần).
3. Xây dựng module quản lý trận đấu local: Hệ thống đếm ngược thời gian (Timer), chấm điểm đúng/sai.
4. Xây dựng module xác thực: Đăng nhập Firebase ẩn danh hoặc bằng email.
5. Xây dựng module Matchmaking: Logic tạo phòng mới, tìm phòng đang trống để join, xử lý full phòng.
6. Xây dựng module Realtime Gameplay: Đồng bộ câu hỏi giữa 2 máy, bắn điểm lên Firebase khi user trả lời, lắng nghe điểm của đối thủ để cập nhật.
7. Xây dựng module xử lý sự cố: Xử lý trường hợp người chơi mất mạng, thoát app đột ngột, hoặc kết thúc trận đấu sớm.


MODULE 2: GIAO DIỆN, OFFLINE DATA & TÀI LIỆU (Thành viên 2 - Tuần sau bắt đầu làm)
Module này chủ yếu tạo vỏ bọc, hoàn toàn độc lập, chỉ cần làm giao diện tĩnh và data giả (mock data) trước.

1. Thiết kế giao diện (UI) cho màn hình Chính: Gồm tiêu đề game, các nút chức năng (Chơi ngay, Chơi Offline, Lịch sử, Cài đặt).
2. Thiết kế giao diện màn hình Phòng chờ: Hiển thị UID hoặc tên 2 người chơi, trạng thái sẵn sàng.
3. Thiết kế giao diện màn hình Gameplay: Hiển thị thời gian đếm ngược, phép tính, các nút đáp án, điểm số 2 bên.
4. Thiết kế giao diện màn hình Lịch sử / Bảng xếp hạng.
5. Xây dựng module Database Offline (SQLite): Tạo bảng lưu trữ kết quả các ván đấu (Thời gian, Điểm mình, Điểm đối thủ, Kết quả Thắng/Thua).
6. Xây dựng module truy vấn SQLite: Lấy danh sách trận đấu từ Database đổ lên màn hình Lịch sử.
7. Xây dựng module Media: Thêm các hiệu ứng âm thanh ngắn (trả lời đúng, trả lời sai, tiếng hết giờ) và rung điện thoại.
8. Soạn thảo Báo cáo Word: Chụp ảnh các giao diện đã làm, viết chi tiết mục "Hướng dẫn sử dụng sản phẩm".


MODULE 3: GHÉP NỐI & LUYỆN THI LIVE-CODING (Cả 2 cùng làm)

1. Ghép nối giao diện (Module 2) với logic ẩn (Module 1): Gắn action cho các nút bấm trên UI để gọi vào logic Firebase/Game.
2. Kiểm thử chéo: Cài đặt app lên 2 điện thoại thật hoặc 2 máy ảo để test độ trễ (latency) và các lỗi phát sinh.
3. Nhóm trưởng bổ sung Báo cáo: Vẽ sơ đồ Use case, sơ đồ hoạt động (Activity diagram) cho phần Realtime Firebase vì phần này khó, thành viên 2 không viết được.
4. Luyện thi cho Thành viên 2: Nhóm trưởng giao thành viên 2 tự gõ hoàn chỉnh 2 chức năng: Tạo Adapter để hiển thị lịch sử đấu, và tự tay viết 3 hàm thêm/sửa/xóa trong SQLite. Đây là vùng thầy rất hay bắt xóa đi code lại.