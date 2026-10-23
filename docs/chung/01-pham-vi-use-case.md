# PHẠM VI HỆ THỐNG VÀ BIỂU ĐỒ USE CASE TỔNG QUÁT — MATH DASH 1v1

---

## 1. Bảng thuật ngữ

| STT | Thuật ngữ | Định nghĩa / Giải thích |
|---|---|---|
| 1 | Người chơi | Người có tài khoản (tên đăng nhập, mật khẩu, tên hiển thị) và đã đăng nhập vào ứng dụng |
| 2 | Câu hỏi | Một phép tính gồm 2 toán hạng và 1 toán tử (+, −, ×), kèm 4 phương án trả lời, trong đó có đúng 1 phương án đúng. Chơi offline thì chọn 1 trong 4 phương án; chơi online thì người chơi tự nhập đáp án bằng bàn phím số |
| 3 | Mức (level) | Mức 1 đến 4, quyết định toán tử và khoảng số của câu hỏi; tăng 1 mức sau mỗi 5 câu, tối đa mức 4 |
| 4 | Độ khó | Dễ, Trung bình hoặc Khó; người chơi chọn trước khi chơi offline. Ván bắt đầu ở mức 1, 2 hoặc 3 tương ứng |
| 5 | Ván chơi | Một lượt chơi offline của một người chơi, kết thúc khi hết mạng |
| 6 | Mạng | Số lần được phép trả lời sai (hoặc hết giờ) trong ván offline; mỗi ván có 3 mạng |
| 7 | Chuỗi đúng (combo) | Số câu trả lời đúng liên tiếp; trả lời sai hoặc hết giờ thì chuỗi về 0 |
| 8 | Điểm | Mỗi câu đúng được 10 + chuỗi đúng × 2 điểm; câu sai được 0 điểm |
| 9 | Kết quả ván | Thông tin được lưu khi ván offline kết thúc: độ khó, điểm, chuỗi đúng dài nhất, số câu đúng, số câu sai, thời điểm chơi |
| 10 | Kỷ lục | Điểm cao nhất của một người chơi ở một độ khó trong chế độ offline. Chỉ hiển thị ở màn chơi, không có màn xem riêng |
| 11 | Cấu hình game | Âm thanh bật / tắt, rung bật / tắt, thời gian trả lời mỗi câu trong chế độ offline |
| 12 | Phòng đấu | Nơi 2 người chơi gặp nhau để thi đấu online, được nhận diện bằng **mã phòng** gồm 6 chữ số |
| 13 | Tạo phòng | Người chơi tạo phòng mới, nhận mã phòng để gửi cho bạn, rồi chờ người thứ hai vào |
| 14 | Tìm phòng | Người chơi nhập mã phòng do bạn gửi để vào phòng đó |
| 15 | Trận đấu | Một lượt thi đấu online 60 giây giữa 2 người chơi trong cùng một phòng; trạng thái waiting (đang chờ), playing (đang đấu), finished (đã xong) |
| 16 | Mã đồng bộ câu hỏi (seed) | Một số nguyên được tạo khi tạo phòng; 2 người chơi dùng cùng mã này nên nhận cùng một dãy câu hỏi |
| 17 | Bỏ trận | Người chơi rời trận khi chưa hết giờ, hoặc bị mất kết nối; người bỏ trận bị xử thua |
| 18 | Kết quả trận | Thắng, thua hoặc hòa. Người bỏ trận thua; nếu không ai bỏ trận thì so điểm khi hết giờ |
| 19 | Lịch sử đấu | Danh sách các trận online đã đấu của một người chơi |

---

## 2. Mô hình nghiệp vụ bằng ngôn ngữ tự nhiên

### 2.1. Mục tiêu và phạm vi hệ thống

Math Dash 1v1 là ứng dụng giúp người chơi luyện tính nhẩm nhanh bằng các câu hỏi cộng, trừ, nhân có độ khó tăng dần. Ứng dụng giải quyết việc luyện tập đơn điệu bằng 2 hình thức có tính thi đua: chơi một mình để phá kỷ lục, và thi đấu trực tiếp với bạn bè.

Phạm vi:
- Người chơi phải có tài khoản và đăng nhập trước khi dùng các chức năng khác.
- **Chơi offline:** người chơi chọn độ khó rồi chơi một mình. Mỗi ván được lưu lại để tính kỷ lục; kỷ lục hiển thị ngay ở màn chơi.
- **Chơi online:** một người tạo phòng và gửi mã phòng cho bạn; người kia nhập mã để vào phòng. Mỗi trận có đúng 2 người.
- Người chơi xem được lịch sử các trận online của mình.
- Ngoài phạm vi: sửa thông tin cá nhân, quản lý người dùng, quản lý lịch sử đấu (vai trò quản trị viên), xem danh sách các ván offline đã chơi, bảng xếp hạng, ghép trận ngẫu nhiên với người lạ, trận nhiều hơn 2 người.

### 2.2. Ai có thể sử dụng phần mềm?

Chỉ có một nhóm người dùng là **Người chơi**. Ai muốn dùng ứng dụng thì tự đăng ký tài khoản. Trong trận online, đối thủ cũng là một người chơi đang dùng ứng dụng trên một máy khác.

### 2.3. Người dùng có những chức năng gì?

Người chơi có thể:
- **Đăng ký:** tạo tài khoản mới bằng tên đăng nhập, mật khẩu và tên hiển thị.
- **Đăng nhập:** đăng nhập bằng tên đăng nhập và mật khẩu; lần sau mở ứng dụng không phải đăng nhập lại.
- **Chơi offline:** chọn độ khó, chơi một mình với 3 mạng, xem kỷ lục và biết khi nào phá kỷ lục.
- **Cài đặt cấu hình game:** bật / tắt âm thanh, bật / tắt rung, chọn thời gian trả lời mỗi câu.
- **Chơi online:** tạo phòng hoặc tìm phòng bằng mã, thi đấu 60 giây với bạn trên cùng một dãy câu hỏi, thấy điểm của bạn mỗi khi bạn ghi điểm, nhận kết quả thắng / thua / hòa. Có thể thoát trận giữa chừng và bị xử thua.
- **Xem lịch sử đấu:** xem danh sách các trận online đã đấu.

### 2.4. Mỗi chức năng hoạt động như thế nào?

Chức năng "Đăng ký":
Người chơi nhấn "Đăng ký" ở màn hình đăng nhập → Hệ thống hiển thị giao diện đăng ký: ô tên đăng nhập, ô mật khẩu, ô nhập lại mật khẩu, ô tên hiển thị, nút Đăng ký → Người chơi nhập thông tin và nhấn Đăng ký → Hệ thống kiểm tra thông tin → Hệ thống tạo tài khoản → Thông báo "Đăng ký thành công" → Quay về màn hình đăng nhập

- Nếu tên đăng nhập đã tồn tại: Hệ thống thông báo "Tên đăng nhập đã được dùng" → Người chơi nhập tên khác
- Nếu mật khẩu nhập lại không khớp hoặc có ô bỏ trống: Hệ thống thông báo lỗi tương ứng


Chức năng "Đăng nhập":
Người chơi mở ứng dụng → Hệ thống hiển thị giao diện đăng nhập: ô tên đăng nhập, ô mật khẩu, nút Đăng nhập, nút Đăng ký → Người chơi nhập tên đăng nhập, mật khẩu và nhấn Đăng nhập → Hệ thống kiểm tra tài khoản → Hệ thống ghi nhớ phiên đăng nhập → Hiển thị màn hình chính: tên hiển thị, các nút Chơi offline, Chơi online, Lịch sử đấu, Cài đặt, Đăng xuất

- Nếu sai tên đăng nhập hoặc mật khẩu: Hệ thống thông báo "Sai tên đăng nhập hoặc mật khẩu"
- Nếu đã đăng nhập từ lần trước: Hệ thống vào thẳng màn hình chính


Chức năng "Chơi offline":
Người chơi nhấn "Chơi offline" ở màn hình chính → Hệ thống hiển thị giao diện chọn độ khó: Dễ, Trung bình, Khó, mỗi nút kèm kỷ lục hiện tại ở độ khó đó → Người chơi chọn một độ khó → Hệ thống hiển thị màn hình chơi: tên, kỷ lục, điểm, chuỗi đúng, 3 mạng, thanh thời gian, câu hỏi, 4 nút đáp án → Người chơi chọn một đáp án

- Nếu đúng: Hệ thống cộng điểm, tăng chuỗi đúng, hiển thị "+12 PTS!" → Hiển thị câu hỏi tiếp theo
- Nếu sai hoặc hết thời gian của câu: Hệ thống trừ 1 mạng, đưa chuỗi đúng về 0, hiển thị "MISS!" → Hiển thị câu hỏi tiếp theo

→ (lặp lại, cứ 5 câu thì tăng 1 mức) → Hết 3 mạng: Hệ thống lưu kết quả ván → Hiển thị màn hình Game Over: điểm cuối, kỷ lục, "KỶ LỤC MỚI" nếu vượt kỷ lục → Người chơi chọn "Chơi lại" hoặc "Về menu"


Chức năng "Cài đặt cấu hình game":
Người chơi nhấn "Cài đặt" ở màn hình chính → Hệ thống hiển thị cấu hình đang dùng: công tắc Âm thanh, công tắc Rung, lựa chọn thời gian mỗi câu (4, 6, 8 hoặc 10 giây) → Người chơi thay đổi cấu hình → Nhấn Lưu → Hệ thống lưu cấu hình → Thông báo "Đã lưu" → Từ ván offline tiếp theo, hệ thống dùng cấu hình mới


Chức năng "Chơi online":
Người chơi nhấn "Chơi online" ở màn hình chính → Hệ thống hiển thị 2 lựa chọn: Tạo phòng, Tìm phòng

- Tạo phòng: Người chơi nhấn "Tạo phòng" → Hệ thống tạo phòng với mã phòng mới → Hiển thị mã phòng và "Đang chờ đối thủ..." → Người chơi gửi mã cho bạn → Khi bạn vào phòng: Hệ thống hiển thị tên đối thủ → Chuyển sang màn hình thi đấu
  - Nếu người chơi nhấn "Hủy" khi đang chờ: Hệ thống xóa phòng → Quay về màn hình chính
- Tìm phòng: Người chơi nhấn "Tìm phòng" → Hệ thống hiển thị ô nhập mã phòng và nút Vào phòng → Người chơi nhập mã và nhấn Vào phòng → Hệ thống kiểm tra phòng → Đưa người chơi vào phòng → Hiển thị tên chủ phòng → Chuyển sang màn hình thi đấu
  - Nếu mã phòng không tồn tại: Hệ thống thông báo "Không tìm thấy phòng"
  - Nếu phòng đã đủ 2 người hoặc đã bắt đầu: Hệ thống thông báo "Phòng đã đầy"

Thi đấu: Hệ thống hiển thị điểm của mình (nổi bật) VS điểm của đối thủ, đồng hồ 60 giây, câu hỏi, bàn phím số 0–9 kèm nút xóa và nút OK → Người chơi nhập đáp án và nhấn OK → Hệ thống chấm điểm, ghi nhận điểm mới để đối thủ thấy → Hiển thị câu hỏi tiếp theo → (lặp lại đến khi hết giờ) → Mỗi khi đối thủ ghi điểm, hệ thống cập nhật điểm đối thủ trên màn hình → Hết 60 giây: Hệ thống xác định kết quả → Lưu trận vào lịch sử đấu → Hiển thị THẮNG / THUA / HÒA kèm tỉ số → Người chơi nhấn "Về menu"

- Nếu người chơi nhấn Back: Hệ thống hỏi "Thoát sẽ bị xử thua?" → Đồng ý: người chơi bị xử thua, quay về màn hình chính
- Nếu đối thủ thoát hoặc mất kết nối: Hệ thống thông báo "Đối thủ đã thoát" → Người chơi được tính thắng


Chức năng "Xem lịch sử đấu":
Người chơi nhấn "Lịch sử đấu" ở màn hình chính → Hệ thống hiển thị danh sách các trận của người chơi, mới nhất ở trên: thời gian, tên đối thủ, tỉ số, kết quả → Người chơi nhấn Back để về màn hình chính

- Nếu chưa có trận nào: Hệ thống hiển thị "Chưa có trận nào"


### 2.5. Những thông tin / đối tượng mà hệ thống cần xử lý

- **Người chơi:** tên đăng nhập, mật khẩu, tên hiển thị, thời điểm tạo tài khoản.
- **Câu hỏi:** toán hạng thứ nhất, toán hạng thứ hai, toán tử, đáp án đúng, 4 phương án trả lời.
- **Ván chơi:** người chơi, độ khó, điểm, chuỗi đúng hiện tại, chuỗi đúng dài nhất, số mạng còn lại, số câu đúng, số câu sai, câu hỏi hiện tại.
- **Kết quả ván:** người chơi, độ khó, điểm, chuỗi đúng dài nhất, số câu đúng, số câu sai, thời điểm chơi.
- **Cấu hình game:** âm thanh bật / tắt, rung bật / tắt, thời gian mỗi câu.
- **Trận đấu:** mã phòng, trạng thái, mã đồng bộ câu hỏi, thời điểm tạo, thời điểm kết thúc, 2 người chơi tham gia.
- **Người chơi trong trận:** người chơi, điểm, đã bỏ trận hay chưa.

### 2.6. Quan hệ giữa các đối tượng

- Một người chơi chơi nhiều ván chơi; một ván chơi thuộc về một người chơi (1-n).
- Một ván chơi lần lượt có nhiều câu hỏi; mỗi câu hỏi được sinh ra cho một ván chơi (1-n). Ván chơi chỉ giữ câu hỏi hiện tại.
- Một ván chơi khi kết thúc sinh ra đúng một kết quả ván; một kết quả ván ứng với một ván chơi (1-1).
- Một người chơi có nhiều kết quả ván; một kết quả ván thuộc về một người chơi (1-n).
- Kỷ lục của người chơi ở một độ khó là điểm cao nhất trong các kết quả ván của người đó ở độ khó đó (quan hệ dẫn xuất, không lưu riêng).
- Cấu hình game áp dụng cho các ván chơi offline trên máy: thời gian mỗi câu quyết định đồng hồ của mỗi câu hỏi trong ván.
- Một người chơi tham gia nhiều trận đấu; một trận đấu có đúng 2 người chơi (n-n), thể hiện qua đối tượng trung gian "người chơi trong trận" giữ điểm và trạng thái bỏ trận của từng người.
- Người tạo phòng là người chơi thứ nhất của trận; mã phòng và mã đồng bộ câu hỏi được tạo cùng lúc với trận.
- Hai người chơi trong cùng một trận nhận cùng một dãy câu hỏi, sinh ra từ mã đồng bộ của trận.
- Kết quả thắng / thua / hòa của một người chơi trong trận được suy ra từ điểm của 2 người và trạng thái bỏ trận (quan hệ dẫn xuất, không lưu riêng).
- Lịch sử đấu của một người chơi là các trận đã kết thúc mà người đó tham gia (quan hệ dẫn xuất).
- Ràng buộc: chỉ vào được trận đang ở trạng thái waiting và chưa đủ 2 người; người chơi không tự vào trận do chính mình tạo; mã phòng là duy nhất trong các trận chưa kết thúc.

---

## 3. Mô hình nghiệp vụ bằng UML

### 3.1. Danh sách Actor

| STT | Tên Actor | Mô tả |
|---|---|---|
| 1 | Người chơi | Người dùng ứng dụng, khởi phát mọi chức năng. Trong trận online, đối thủ cũng là một Người chơi trên máy khác |

### 3.2. Các Use Case cho từng Actor

| Mã UC | Actor | Use case | Module | Người code, demo, vẽ |
|---|---|---|---|---|
| UC01 | Người chơi | Đăng ký | Tài khoản | Chung (Hùng làm, Hoàng duyệt) |
| UC02 | Người chơi | Đăng nhập | Tài khoản | Chung (Hùng làm, Hoàng duyệt) |
| UC03 | Người chơi | Chơi offline | Chơi offline | Hoàng |
| UC04 | Người chơi | Cài đặt cấu hình game | Chơi offline | Hoàng |
| UC05 | Người chơi | Chơi online | Chơi online | Hùng |
| UC06 | Người chơi | Xem lịch sử đấu | Chơi online | Hùng |

Mô tả các use case:
1. "Đăng ký" (UC01): cho phép người chơi tạo tài khoản mới.
2. "Đăng nhập" (UC02): cho phép người chơi đăng nhập để dùng các chức năng khác; ứng dụng ghi nhớ phiên đăng nhập.
3. "Chơi offline" (UC03): cho phép người chơi chọn độ khó, chơi một mình với 3 mạng và phá kỷ lục ở độ khó đó.
4. "Cài đặt cấu hình game" (UC04): cho phép người chơi bật / tắt âm thanh, rung và chọn thời gian trả lời mỗi câu.
5. "Chơi online" (UC05): cho phép người chơi tạo phòng hoặc tìm phòng bằng mã, thi đấu 60 giây với một người chơi khác và nhận kết quả.
6. "Xem lịch sử đấu" (UC06): cho phép người chơi xem các trận online đã đấu.

Các bước con của từng UC (ví dụ Tạo phòng, Tìm phòng, Thi đấu, Thoát trận của UC05; Chọn độ khó của UC03) được phân rã ở **biểu đồ use case chi tiết của từng module** trong tài liệu phân tích cá nhân, không đưa vào biểu đồ tổng quát.

### 3.3. Biểu đồ use case tổng quát

![Hình 1: Biểu đồ use case tổng quát](uc-tong-quat.png)

*Hình 1: Biểu đồ use case tổng quát của Math Dash 1v1.*

Biểu đồ có một tác nhân là Người chơi, khởi phát trực tiếp 6 use case. Các use case được nhóm theo 3 module:
- **Tài khoản** (UC01, UC02): phần chung, mọi chức năng khác cần người chơi đã đăng nhập.
- **Chơi offline** (UC03, UC04): Hoàng phụ trách; chạy được khi không có mạng.
- **Chơi online** (UC05, UC06): Hùng phụ trách.

Mã nguồn PlantUML: [uc-tong-quat.puml](uc-tong-quat.puml).
