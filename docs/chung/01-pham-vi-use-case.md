# PHẠM VI HỆ THỐNG VÀ BIỂU ĐỒ USE CASE TỔNG QUÁT — MATH DASH 1v1

---

## 1. Bảng thuật ngữ

| STT | Thuật ngữ | Định nghĩa / Giải thích |
|---|---|---|
| 1 | Người chơi | Người có tài khoản gồm tên đăng nhập, mật khẩu, tên hiển thị, và đã đăng nhập vào ứng dụng từ một máy client |
| 2 | Quản trị viên | Người có tài khoản quản trị, dùng để quản lý thông tin các người chơi. Tài khoản quản trị được tạo sẵn trên server, không đăng ký từ ứng dụng. Quản trị viên không thi đấu, không có trong danh sách online và bảng xếp hạng |
| 3 | Server | Máy chủ duy nhất của hệ thống. Server lưu toàn bộ thông tin tài khoản và trận đấu, sinh đề bài và làm trọng tài cho các trận đối kháng |
| 4 | Câu hỏi | Một phép tính gồm 2 toán hạng và 1 toán tử (+, −, ×), kèm 4 phương án trả lời, trong đó có đúng 1 phương án đúng. Người chơi chọn 1 trong 4 phương án |
| 5 | Đề bài | Danh sách N = 10 câu hỏi. Trong một trận đối kháng, server sinh một đề và gửi cùng đề đó cho cả 2 người chơi |
| 6 | Danh sách người chơi online | Danh sách các người chơi đang ở giao diện chơi online. Mỗi dòng có tên, tổng điểm, trạng thái. Người chơi rời giao diện chơi online thì không còn trong danh sách và không nhận được lời thách đấu |
| 7 | Trạng thái | Rỗi: người chơi không chơi với ai. Bận: người chơi đang trong một trận đối kháng. Chỉ thách đấu được người đang rỗi |
| 8 | Lời mời thách đấu | Lời mời người chơi gửi tới một người chơi khác đang rỗi bằng cách bấm vào tên người đó. Người được mời chọn Đồng ý hoặc Từ chối |
| 9 | Trận đấu | Một ván đối kháng giữa 2 người chơi trên cùng một đề bài, thời gian tối đa 60 giây |
| 10 | Bài làm | Phương án người chơi đã chọn cho từng câu trong 10 câu hỏi, kèm thời gian làm câu: khoảng thời gian từ lúc câu hiện ra tới lúc người chơi chọn phương án. Người chơi làm lần lượt từng câu, chọn xong một câu thì sang câu sau; làm xong 10 câu thì nộp bài bằng nút Nộp bài. Hết 60 giây mà chưa nộp thì hệ thống tự nộp, câu chưa chọn là câu bỏ trống và tính là sai |
| 11 | Thời gian làm bài | Khoảng thời gian từ lúc trận bắt đầu đến lúc server nhận được bài làm của người chơi |
| 12 | Kết quả trận | Thắng, thua hoặc hòa, do server xác định:<br>- Người bỏ trận thua.<br>- Người đúng hết 10 câu thắng người có ít nhất 1 câu sai.<br>- Cả 2 đúng hết thì ai có thời gian làm bài ngắn hơn thắng; bằng nhau thì hòa.<br>- Cả 2 đều có ít nhất 1 câu sai thì hòa |
| 13 | Điểm trận | Thắng được 1 điểm, hòa 0,5 điểm, thua 0 điểm |
| 14 | Tổng điểm | Tổng điểm trận của một người chơi qua mọi trận đối kháng đã kết thúc |
| 15 | Bỏ trận | Người chơi nhấn nút Thoát khi trận chưa kết thúc, hoặc máy bị mất kết nối. Người bỏ trận bị xử thua |
| 16 | Chơi tiếp | Sau mỗi trận, hệ thống hỏi từng người có muốn chơi tiếp không. Cả 2 đồng ý thì server sinh đề mới cho trận tiếp theo; một người dừng thì cả 2 thoát ra và server báo cho người còn lại |
| 17 | Chơi với máy | Người chơi thi đấu với một đối thủ do ứng dụng giả lập, theo cùng luật của trận đối kháng. Chơi được khi không có mạng; kết quả lưu trên máy, không tính vào tổng điểm |
| 18 | Độ khó của máy | Dễ, Trung bình hoặc Khó. Độ khó quyết định khả năng trả lời đúng mỗi câu và thời gian làm bài của máy |
| 19 | Bảng xếp hạng | Danh sách mọi người chơi trong hệ thống, sắp lần lượt theo 3 tiêu chí:<br>1. Tổng điểm, giảm dần.<br>2. Trung bình tổng điểm của các đối thủ đã gặp, giảm dần.<br>3. Trung bình thời gian làm bài trong các trận thắng, tăng dần |
| 20 | Lịch sử đấu | Danh sách các trận đối kháng đã kết thúc mà người chơi đã tham gia, trận mới nhất đứng đầu, kèm tỉ lệ thắng / hòa / thua của người chơi qua các trận đó. Ván đấu với máy không nằm trong lịch sử đấu |

---

## 2. Mô hình nghiệp vụ bằng ngôn ngữ tự nhiên

### 2.1. Mục tiêu và phạm vi hệ thống

Math Dash 1v1 là trò chơi giải toán nhanh trên điện thoại Android. Người chơi thi xem ai giải đúng và nhanh hơn một đề gồm 10 câu cộng, trừ, nhân trắc nghiệm, với người chơi khác hoặc với máy.

Hệ thống gồm một server và nhiều client. Server lưu toàn bộ thông tin và dữ liệu, sinh đề bài và làm trọng tài cho mọi trận đối kháng. Mỗi điện thoại cài ứng dụng là một client.

Phạm vi:
- Người chơi phải có tài khoản và đăng nhập trước khi dùng các chức năng khác.
- **Chơi online:** thách đấu và đấu đối kháng.
  - Người chơi vào giao diện chơi online, thấy danh sách người chơi đang online và thách đấu một người đang rỗi. Người chơi chỉ online khi đang ở giao diện này.
  - Hai người làm cùng một đề do server sinh ra, lần lượt từng câu; server chấm bài, lưu bài làm từng câu và cộng điểm.
  - Sau mỗi trận, hai người chọn chơi tiếp hoặc dừng.
- **Chơi với máy:** người chơi chọn độ khó của máy rồi thi đấu theo cùng luật. Chức năng này chạy được khi không có mạng.
- **Xem lịch sử đấu:** người chơi xem tỉ lệ thắng / hòa / thua và danh sách các trận chơi online mình đã đấu, xem lại đề, kết quả và bài làm từng câu của 2 người trong từng trận, và xem bảng xếp hạng của mọi người chơi trong hệ thống.
- **Quản lý thông tin người chơi:** quản trị viên xem danh sách người chơi, sửa tên hiển thị, đặt lại mật khẩu, xóa tài khoản người chơi.
- Ngoài phạm vi:
  - Người chơi tự sửa thông tin cá nhân.
  - Đăng nhập không cần tài khoản.
  - Lịch sử các ván đấu với máy.
  - Trận nhiều hơn 2 người.
  - Ghép trận ngẫu nhiên.
  - Trò chuyện giữa các người chơi.

### 2.2. Ai có thể sử dụng phần mềm?

Có 2 nhóm người dùng:
- **Người chơi:** ai muốn chơi thì tự đăng ký tài khoản. Trong trận đối kháng, đối thủ cũng là một người chơi đang dùng ứng dụng trên một máy client khác.
- **Quản trị viên:** quản lý thông tin các người chơi. Tài khoản quản trị được tạo sẵn trên server.

### 2.3. Người dùng có những chức năng gì?

Người chơi có thể:
- **Đăng ký:** tạo tài khoản mới bằng tên đăng nhập, mật khẩu và tên hiển thị.
- **Đăng nhập:** đăng nhập bằng tên đăng nhập và mật khẩu; lần sau mở ứng dụng không phải đăng nhập lại.
- **Chơi online:**
  - Vào giao diện chơi online, xem danh sách người chơi online.
  - Thách đấu một người đang rỗi, hoặc đồng ý / từ chối lời thách đấu của người khác.
  - Làm lần lượt 10 câu, mỗi câu chọn 1 trong 4 phương án, trong tối đa 60 giây rồi nộp bài; được thoát giữa chừng nhưng bị xử thua.
  - Nhận kết quả và điểm trận, rồi chọn chơi tiếp hoặc dừng.
- **Chơi với máy:** chọn độ khó của máy, làm đề 10 câu và nhận kết quả thắng / thua / hòa với máy.
- **Xem lịch sử đấu:**
  - Xem tỉ lệ thắng / hòa / thua và danh sách các trận chơi online đã đấu.
  - Xem chi tiết một trận: đối thủ, kết quả, số câu đúng và thời gian của 2 bên, đề 10 câu kèm đáp án đúng, và ở từng câu, mỗi người đúng hay sai, đã chọn phương án nào, làm câu đó hết bao lâu.
  - Xem bảng xếp hạng: thứ hạng, tên, tổng điểm, trung bình điểm đối thủ, trung bình thời gian thắng của mọi người chơi.

Quản trị viên có thể:
- **Đăng nhập:** dùng chung giao diện đăng nhập với người chơi; hệ thống nhận ra tài khoản quản trị và mở giao diện quản lý người chơi.
- **Quản lý thông tin người chơi:** xem danh sách người chơi, tìm theo tên, sửa tên hiển thị, đặt lại mật khẩu, xóa tài khoản người chơi.

### 2.4. Mỗi chức năng hoạt động như thế nào?

**Chức năng "Đăng ký":**

Người chơi nhấn "Đăng ký" ở giao diện đăng nhập → Hệ thống hiển thị giao diện đăng ký gồm: ô tên đăng nhập, ô mật khẩu, ô nhập lại mật khẩu, ô tên hiển thị, nút Đăng ký → Người chơi nhập thông tin và nhấn Đăng ký → Hệ thống kiểm tra thông tin → Hệ thống tạo tài khoản → Thông báo "Đăng ký thành công" → Quay về giao diện đăng nhập.

- Tên đăng nhập đã tồn tại: Hệ thống thông báo "Tên đăng nhập đã được dùng" → Người chơi nhập tên khác.
- Mật khẩu nhập lại không khớp hoặc có ô bỏ trống: Hệ thống thông báo lỗi tương ứng.

**Chức năng "Đăng nhập":**

Người chơi mở ứng dụng → Hệ thống hiển thị giao diện đăng nhập gồm: ô tên đăng nhập, ô mật khẩu, nút Đăng nhập, nút Đăng ký → Người chơi nhập tên đăng nhập, mật khẩu và nhấn Đăng nhập → Hệ thống kiểm tra tài khoản → Hệ thống ghi nhớ phiên đăng nhập → Hiển thị giao diện chính.

Giao diện chính gồm:
- Lời chào kèm tên hiển thị của mình.
- Hai nút chọn chế độ chơi: Chơi với máy, Chơi online.
- Nút Đăng xuất.

Ngoại lệ:
- Sai tên đăng nhập hoặc mật khẩu: Hệ thống thông báo "Sai tên đăng nhập hoặc mật khẩu".
- Đã đăng nhập từ lần trước: Hệ thống vào thẳng giao diện chính.
- Tài khoản là tài khoản quản trị: Hệ thống mở giao diện quản lý người chơi thay cho giao diện chính.

**Chức năng "Chơi online":**

Người chơi đăng nhập vào hệ thống → Giao diện chính xuất hiện với: chữ MATH DASH, lời chào "Xin chào, <tên>", 2 nút Chơi với máy, Chơi online, nút biểu tượng Đăng xuất ở góc phải trên → Người chơi nhấn Chơi online → Hệ thống ghi người chơi vào danh sách online với trạng thái Rỗi → Giao diện chơi online xuất hiện với: tiêu đề Chơi online, 2 nút biểu tượng ở góc phải trên là Lịch sử đấu, Bảng xếp hạng, thẻ tên và tổng điểm của mình, danh sách những người khác đang ở giao diện chơi online xếp theo tổng điểm giảm dần, mỗi dòng gồm: tên, tổng điểm, trạng thái Rỗi màu xanh hoặc Bận màu xám, danh sách tự cập nhật khi có người vào, người rời hoặc đổi trạng thái → Người chơi bấm vào tên một người đang rỗi → Hệ thống gửi lời mời tới máy người đó và hiện hộp thoại "Đang chờ <tên> trả lời..." với nút Hủy → Máy người được mời hiện hộp thoại "<tên> (<tổng điểm> điểm) thách đấu bạn" với 2 nút Đồng ý, Từ chối → Người được mời nhấn Đồng ý → Server kiểm tra cả 2 vẫn online và rỗi, sinh đề 10 câu, tạo trận, chuyển cả 2 sang Bận và gửi đề cho cả 2 máy → Giao diện thi đấu xuất hiện trên cả 2 máy, mỗi lần một câu, với: ô thời gian đếm ngược từ 01:00, dòng trạng thái "<tên đối thủ> đang làm bài", dòng "Câu 1/10", phép tính chữ lớn, 4 ô phương án xếp 2 × 2 là A màu đỏ, B màu xanh dương, C màu vàng, D màu xanh lá, nút Thoát → Người chơi bấm một ô → Hệ thống ghi phương án đã chọn và thời gian làm câu tính từ lúc câu hiện ra, chưa báo đúng hay sai, rồi hiện câu tiếp theo → Người chơi làm lần lượt tới câu thứ 10 → Giao diện ẩn 4 ô, hiện dòng "Đã làm xong 10 câu" và nút Nộp bài → Người chơi nhấn Nộp bài → Server ghi thời điểm nộp, lưu bài làm từng câu và chấm bài → Giao diện hiện "Đã nộp bài, đang chờ đối thủ...", máy đối thủ đổi dòng trạng thái thành "<tên> đã nộp bài" → Khi cả 2 đã nộp, server xác định kết quả, cộng điểm trận, cập nhật tổng điểm và báo cả 2 máy → Giao diện kết quả hiện đè lên giao diện thi đấu với: dòng THẮNG! màu xanh, THUA! màu đỏ hoặc HÒA! màu vàng, số câu đúng và thời gian làm bài của 2 người, điểm trận được cộng và tổng điểm mới, câu hỏi "Chơi tiếp?" với 2 nút Chơi tiếp, Dừng → Người chơi nhấn Chơi tiếp → Giao diện hiện "Đang chờ đối thủ..." → Đối thủ cũng nhấn Chơi tiếp → Server sinh đề mới, tạo trận mới → Cả 2 máy vào giao diện thi đấu của trận mới.

**Chức năng "Xem lịch sử đấu":**

Người chơi đăng nhập vào hệ thống → Giao diện chính xuất hiện với 2 nút Chơi với máy, Chơi online → Người chơi nhấn Chơi online → Giao diện chơi online xuất hiện với: 2 nút biểu tượng Lịch sử đấu, Bảng xếp hạng ở góc phải trên, thẻ tên và tổng điểm của mình, danh sách người chơi online → Người chơi nhấn nút Lịch sử đấu → Hệ thống lấy các trận chơi online đã kết thúc mà người chơi tham gia, trận mới nhất đứng đầu → Giao diện lịch sử đấu xuất hiện với: tiêu đề Lịch sử đấu, 2 thẻ Trận của tôi và Bảng xếp hạng, thẻ Trận của tôi mở sẵn gồm biểu đồ tròn chia 3 phần Thắng màu xanh lá, Hòa màu vàng, Thua màu đỏ, giữa biểu đồ ghi tỉ lệ thắng, bên cạnh ghi số trận thắng, hòa, thua và tổng số trận, dưới là danh sách trận, mỗi dòng gồm: dải màu theo kết quả, tên đối thủ, số trận, THẮNG / HÒA / THUA, điểm trận, số câu đúng của mình và của đối thủ, thời điểm kết thúc → Người chơi bấm vào một trận → Hệ thống lấy trận kèm đề, đáp án đúng và bài làm từng câu của 2 người → Giao diện chi tiết trận xuất hiện với: tiêu đề Chi tiết trận, dòng "Trận #<số trận> · <ngày giờ kết thúc>", dòng kết quả THẮNG! / THUA! / HÒA! và điểm trận, khung so sánh tên, số câu đúng, thời gian làm bài của mình và của đối thủ, mục Từng câu gồm hàng tiêu đề cột #, Câu, tên mình, tên đối thủ và 10 dòng, mỗi dòng gồm: số thứ tự, phép tính kèm đáp án đúng, với mỗi người chơi một ô ghi ✓ màu xanh hoặc ✗ màu đỏ kèm phương án đã chọn và thời gian làm câu, câu bỏ trống ghi "Bỏ trống" → Người chơi nhấn Back → Giao diện lịch sử đấu hiện lại → Người chơi chọn thẻ Bảng xếp hạng, hoặc từ giao diện chơi online nhấn nút Bảng xếp hạng để mở thẳng thẻ này → Hệ thống lấy mọi người chơi và các trận đã kết thúc, xếp hạng theo tổng điểm giảm dần, cùng tổng điểm thì theo trung bình tổng điểm các đối thủ đã gặp giảm dần, cùng cả 2 thì theo trung bình thời gian làm bài trong các trận thắng tăng dần, người chưa thắng trận nào ghi "—" và đứng sau → Thẻ Bảng xếp hạng hiện danh sách mọi người chơi, mỗi dòng gồm: hạng, tên, tổng điểm, trung bình điểm đối thủ, trung bình thời gian thắng, dòng của chính người chơi được tô nổi bật → Người chơi nhấn Back → Hệ thống quay về giao diện chơi online.

**Chức năng "Chơi với máy":**

Người chơi nhấn "Chơi với máy" ở giao diện chính → Hệ thống hiển thị giao diện chọn độ khó gồm 3 nút Dễ, Trung bình, Khó → Người chơi chọn một độ khó → Hệ thống sinh đề 10 câu → Hiển thị giao diện thi đấu giống trận đối kháng, đối thủ là Máy: ô thời gian, dòng trạng thái của máy, mỗi lần một câu với 4 ô phương án A, B, C, D, nút Thoát → Người chơi làm lần lượt 10 câu, mỗi câu bấm một ô là sang câu sau → Làm xong 10 câu, người chơi nhấn Nộp bài → Hệ thống chấm bài của người chơi, giả lập bài làm của máy theo độ khó → Xác định kết quả theo cùng luật → Lưu kết quả ván trên máy → Hiển thị giao diện kết quả gồm THẮNG / THUA / HÒA, số câu đúng và thời gian của mình và của máy, 2 nút Chơi lại, Về menu.

- Hết 60 giây mà chưa nộp bài: Hệ thống tự nộp bài.
- Máy có thể nộp bài trước người chơi: dòng trạng thái đối thủ đổi thành "Máy đã nộp bài".

**Chức năng "Quản lý thông tin người chơi":**

Quản trị viên đăng nhập → Hệ thống hiển thị giao diện quản lý người chơi gồm: ô tìm theo tên, danh sách người chơi (tên đăng nhập, tên hiển thị, tổng điểm, ngày tạo tài khoản), nút Đăng xuất → Quản trị viên bấm vào một người chơi → Hệ thống hiển thị giao diện thông tin người chơi gồm: tên đăng nhập, ô tên hiển thị, tổng điểm, số trận thắng / hòa / thua, các nút Lưu, Đặt lại mật khẩu, Xóa tài khoản → Quản trị viên sửa tên hiển thị và nhấn Lưu → Hệ thống cập nhật thông tin → Thông báo "Đã lưu" → Quay về danh sách người chơi.

- Nhấn Đặt lại mật khẩu: Hệ thống hiển thị ô mật khẩu mới → Quản trị viên nhập và xác nhận → Hệ thống cập nhật mật khẩu.
- Nhấn Xóa tài khoản: Hệ thống hỏi xác nhận → Đồng ý: Hệ thống xóa tài khoản.
- Người chơi đã tham gia trận đối kháng: Hệ thống không xóa, để giữ kết quả của các đối thủ, và thông báo "Không xóa được người chơi đã có trận đấu".
- Tên hiển thị bỏ trống: Hệ thống thông báo lỗi.

### 2.5. Những thông tin / đối tượng mà hệ thống cần xử lý

- **Người chơi:** tên đăng nhập, mật khẩu, tên hiển thị, thời điểm tạo tài khoản, vai trò (người chơi hoặc quản trị viên), tổng điểm, trạng thái rỗi / bận khi đang online.
- **Câu hỏi:** toán hạng thứ nhất, toán hạng thứ hai, toán tử, đáp án đúng, 4 phương án trả lời.
- **Lời mời thách đấu:** người mời, người được mời, trạng thái (đang chờ, đã đồng ý, đã từ chối), thời điểm gửi.
- **Trận đấu:** đề bài gồm 10 câu hỏi, thời điểm bắt đầu, thời điểm kết thúc, trạng thái (đang đấu, đã xong), 2 người chơi tham gia.
- **Người chơi trong trận:** người chơi, bài làm, số câu đúng, thời điểm nộp bài, đã bỏ trận hay chưa.
- **Bài làm một câu:** câu hỏi, phương án đã chọn, thời gian làm câu.
- **Ván đấu với máy:** người chơi, độ khó của máy, đề bài, số câu đúng và thời gian làm bài của người chơi, số câu đúng và thời gian làm bài của máy, thời điểm chơi.

### 2.6. Quan hệ giữa các đối tượng

- Một người chơi gửi nhiều lời mời và nhận nhiều lời mời. Mỗi lời mời có đúng một người mời và một người được mời (1-n ở mỗi vai trò).
- Một lời mời được đồng ý thì sinh ra trận đầu tiên giữa 2 người; các trận chơi tiếp sau đó không cần lời mời mới.
- Một người chơi tham gia nhiều trận đấu; một trận đấu có đúng 2 người chơi (n-n). Quan hệ này được thể hiện qua đối tượng trung gian "người chơi trong trận", nơi giữ bài làm, số câu đúng, thời điểm nộp bài và trạng thái bỏ trận của từng người.
- Một trận đấu có đúng 10 câu hỏi; mỗi câu hỏi thuộc về đúng một trận (1-n). Hai người trong trận làm chung đề này.
- Mỗi người chơi trong trận làm 10 câu của đề, mỗi câu hỏi được 2 người trong trận cùng làm (n-n). Quan hệ này được thể hiện qua đối tượng "bài làm một câu", giữ phương án đã chọn và thời gian làm câu của một người cho một câu.
- Một người chơi chơi nhiều ván với máy; mỗi ván thuộc về một người chơi (1-n). Mỗi ván có 10 câu hỏi do ứng dụng sinh ra (1-n).
- Các thông tin dẫn xuất, không lưu riêng:
  - Đúng hay sai của một câu trong bài làm: so phương án đã chọn với đáp án đúng của câu đó.
  - Kết quả trận và điểm trận của một người chơi: suy ra từ số câu đúng, thời gian làm bài và trạng thái bỏ trận của 2 người.
  - Tổng điểm: tổng điểm trận của người đó qua các trận đã kết thúc.
  - Các tiêu chí của bảng xếp hạng: tính từ các trận đã kết thúc.
  - Tỉ lệ thắng / hòa / thua trong lịch sử đấu: đếm từ kết quả các trận đã kết thúc của người chơi.
  - Kết quả ván đấu với máy: suy ra từ số câu đúng và thời gian của người và của máy.
- Ràng buộc:
  - Chỉ thách đấu được người đang online và đang rỗi; không tự thách đấu chính mình.
  - Mỗi người chỉ ở trong tối đa một trận chưa kết thúc.
  - Mỗi người chỉ nộp bài một lần trong một trận.

---

## 3. Mô hình nghiệp vụ bằng UML

### 3.1. Danh sách Actor

| STT | Tên Actor | Mô tả |
|---|---|---|
| 1 | Người chơi | Người dùng ứng dụng để chơi. Trong trận đối kháng, người được mời và đối thủ cũng là một Người chơi trên máy client khác |
| 2 | Quản trị viên | Người quản lý thông tin các người chơi |

### 3.2. Các Use Case cho từng Actor

| Mã UC | Actor | Use case | Module |
|---|---|---|---|
| UC01 | Người chơi | Đăng ký | Tài khoản |
| UC02 | Người chơi, Quản trị viên | Đăng nhập | Tài khoản |
| UC03 | Người chơi | Chơi online | Đấu đối kháng |
| UC04 | Người chơi | Xem lịch sử đấu | Xem lịch sử đấu |
| UC05 | Người chơi | Chơi với máy | Chơi với máy |
| UC06 | Quản trị viên | Quản lý thông tin người chơi | Quản trị |

Mô tả các use case:
1. "Đăng ký" (UC01): cho phép người chơi tạo tài khoản mới.
2. "Đăng nhập" (UC02): cho phép người chơi đăng nhập để dùng các chức năng khác; ứng dụng ghi nhớ phiên đăng nhập.
3. "Chơi online" (UC03): cho phép người chơi vào giao diện chơi online để xem danh sách người chơi online, thách đấu hoặc nhận lời thách đấu, làm cùng một đề với đối thủ, nhận kết quả do server xác định và chọn chơi tiếp.
4. "Xem lịch sử đấu" (UC04): cho phép người chơi xem tỉ lệ thắng / hòa / thua, danh sách và chi tiết các trận chơi online mình đã đấu kèm bài làm từng câu của 2 người, và xem bảng xếp hạng mọi người chơi theo 3 tiêu chí.
5. "Chơi với máy" (UC05): cho phép người chơi chọn độ khó của máy và thi đấu với máy theo cùng luật, kể cả khi không có mạng.
6. "Quản lý thông tin người chơi" (UC06): cho phép quản trị viên xem danh sách người chơi, sửa tên hiển thị, đặt lại mật khẩu và xóa tài khoản người chơi.

Các bước con của từng UC được phân rã ở **biểu đồ use case chi tiết của từng module** trong tài liệu phân tích cá nhân, không đưa vào biểu đồ tổng quát. Ví dụ, UC03 có Xem danh sách người chơi online, Gửi lời thách đấu, Nhận lời thách đấu, Thi đấu, Thoát trận, Chơi tiếp; UC04 có Xem trận của tôi, Xem chi tiết trận, Xem bảng xếp hạng.

### 3.3. Biểu đồ use case tổng quát

![Hình 1: Biểu đồ use case tổng quát](uc-tong-quat.png)

*Hình 1: Biểu đồ use case tổng quát của Math Dash 1v1.*

Biểu đồ có 2 tác nhân:
- Người chơi khởi phát 5 use case: từ UC01 đến UC05.
- Quản trị viên khởi phát 2 use case: UC02 và UC06.

Các use case được nhóm theo 5 module:
- **Tài khoản** (UC01, UC02): mọi chức năng khác cần đã đăng nhập.
- **Đấu đối kháng** (UC03): cần kết nối tới server. Server sinh đề, làm trọng tài và lưu kết quả trận.
- **Xem lịch sử đấu** (UC04): chỉ đọc kết quả các trận đối kháng đã lưu trên server; bảng xếp hạng cũng tính từ các trận này.
- **Chơi với máy** (UC05): chạy hoàn toàn trên ứng dụng, chơi được khi không có mạng.
- **Quản trị** (UC06): quản trị viên đọc và sửa dữ liệu người chơi lưu trên server.

Mã nguồn PlantUML: [uc-tong-quat.puml](uc-tong-quat.puml).
