# PHẠM VI HỆ THỐNG VÀ BIỂU ĐỒ USE CASE TỔNG QUÁT — MATH DASH 1v1

> Issue: #22 [M1-01] · Người làm: Hùng · Người duyệt: Hoàng
> Tài liệu dùng chung cho cả 2 báo cáo cá nhân. Tên use case (UC) và mã UC ở đây được dùng **nguyên văn** trong code, tài liệu phân tích – thiết kế và báo cáo.

---

## 1. Bảng thuật ngữ

| STT | Thuật ngữ | Định nghĩa / Giải thích |
|---|---|---|
| 1 | Người chơi | Người dùng ứng dụng trên một điện thoại Android, được nhận diện bằng tên nhập ở màn hình chính |
| 2 | Câu hỏi | Một phép tính gồm 2 toán hạng và 1 toán tử (`+`, `−`, `×`), kèm 4 phương án trả lời, trong đó có đúng 1 phương án đúng |
| 3 | Mức độ khó (level) | Mức 1 đến 4, quyết định toán tử và khoảng số của câu hỏi; tăng 1 mức sau mỗi 5 câu |
| 4 | Ván chơi | Một lượt chơi offline của một người chơi, kết thúc khi hết mạng |
| 5 | Mạng | Số lần được phép trả lời sai (hoặc hết giờ) trong ván offline; mỗi ván có 3 mạng |
| 6 | Chuỗi đúng (combo) | Số câu trả lời đúng liên tiếp; trả lời sai hoặc hết giờ thì chuỗi về 0 |
| 7 | Điểm | Mỗi câu đúng được `10 + chuỗi đúng × 2` điểm; câu sai được 0 điểm |
| 8 | Kết quả ván | Thông tin được lưu lại khi ván offline kết thúc: điểm, chuỗi đúng dài nhất, số câu đúng, số câu sai, thời điểm chơi |
| 9 | Kỷ lục | Điểm cao nhất trong các kết quả ván của một người chơi |
| 10 | Bảng vàng | Danh sách các kết quả ván có điểm cao nhất, xếp giảm dần theo điểm |
| 11 | Phòng đấu | Nơi 2 người chơi trên 2 máy gặp nhau để thi đấu online; có trạng thái `waiting` (đang chờ), `playing` (đang đấu), `finished` (đã xong) |
| 12 | Mã đồng bộ câu hỏi (seed) | Một số nguyên do chủ phòng tạo ra; 2 máy dùng cùng mã này để sinh ra cùng một dãy câu hỏi |
| 13 | Trận đấu | Một lượt thi đấu online 60 giây giữa 2 người chơi trong cùng một phòng |
| 14 | Kết quả trận | Thắng, thua hoặc hòa, so theo điểm của 2 người khi hết giờ; người bỏ trận bị xử thua |
| 15 | Cách kết thúc trận | Trận kết thúc bình thường (hết 60 giây), do đối thủ bỏ trận, hoặc do mình bỏ trận |
| 16 | Lịch sử đấu | Danh sách các trận online đã đấu, lưu trên máy của người chơi |
| 17 | Đăng nhập ẩn danh | Máy chủ cấp cho mỗi máy một mã định danh cố định mà không cần tài khoản hay mật khẩu |

---

## 2. Mô hình nghiệp vụ bằng ngôn ngữ tự nhiên

### 2.1. Mục tiêu và phạm vi hệ thống

Math Dash 1v1 là ứng dụng Android giúp người chơi luyện tính nhẩm nhanh bằng các câu hỏi cộng, trừ, nhân có độ khó tăng dần. Ứng dụng giải quyết việc luyện tập đơn điệu bằng 2 hình thức có tính thi đua: chơi một mình để phá kỷ lục, và thi đấu trực tiếp với một người chơi khác qua mạng.

Phạm vi:
- Ứng dụng chạy trên điện thoại Android (từ Android 7.0). Mỗi máy có thể có nhiều người chơi, phân biệt bằng tên.
- **Chơi offline** hoạt động hoàn toàn trên máy, kết quả lưu trong CSDL SQLite của máy.
- **Chơi online** cần kết nối Internet. Việc ghép trận và đồng bộ điểm giữa 2 máy đi qua Firebase Realtime Database. Mỗi trận chỉ có đúng 2 người.
- Lịch sử đấu và bảng vàng chỉ lưu trên từng máy, không đồng bộ giữa các máy.
- Ngoài phạm vi: đăng ký tài khoản bằng email, trận nhiều hơn 2 người, chống gian lận, bảng xếp hạng toàn cầu.

### 2.2. Ai có thể sử dụng phần mềm?

Chỉ có một nhóm người dùng là **Người chơi**. Bất kỳ ai cài ứng dụng đều là người chơi, không cần đăng ký. Người chơi tự đặt tên cho mình ở màn hình chính. Trong trận online, đối thủ cũng là một người chơi đang dùng ứng dụng trên một máy khác.

Firebase là hạ tầng lưu trữ và truyền dữ liệu, không phải người dùng nên không được xét là tác nhân.

### 2.3. Người dùng có những chức năng gì?

Người chơi có thể:
- **Tìm trận:** tìm một đối thủ đang chờ, hoặc tạo phòng mới để chờ đối thủ. Có thể hủy khi đang chờ.
- **Thi đấu online:** trả lời cùng một dãy câu hỏi với đối thủ trong 60 giây, thấy điểm của đối thủ thay đổi theo thời gian thực, nhận kết quả thắng / thua / hòa. Có thể thoát trận giữa chừng và bị xử thua.
- **Quản lý lịch sử đấu:** xem danh sách các trận online đã đấu, xóa một trận khỏi danh sách.
- **Chơi offline:** chơi một mình với 3 mạng, mỗi câu có 6 giây, điểm cao được lưu lại.
- **Xem bảng vàng:** xem các ván offline điểm cao nhất trên máy.
- **Cài đặt âm thanh:** bật / tắt âm thanh và rung khi trả lời.

### 2.4. Mỗi chức năng hoạt động như thế nào?

Chức năng "Tìm trận":
```
Người chơi nhập tên ở màn hình chính → Người chơi nhấn "Chơi ngay" → Hệ thống đăng nhập ẩn danh (lần đầu thì máy chủ cấp mã định danh mới, các lần sau dùng lại mã cũ) → Hệ thống tìm một phòng đang chờ
  → Nếu có phòng đang chờ: Hệ thống đưa người chơi vào phòng → Phòng chuyển sang trạng thái đang đấu → Hệ thống hiển thị tên đối thủ → Sau 1,5 giây chuyển sang màn hình thi đấu
  → Nếu không có: Hệ thống tạo phòng mới kèm mã đồng bộ câu hỏi → Hiển thị "Đang tìm đối thủ..." → Khi có người vào phòng: hiển thị tên đối thủ → chuyển sang màn hình thi đấu
  → Nếu người chơi nhấn "Hủy" khi đang chờ: Hệ thống xóa phòng → Quay về màn hình chính
  → Nếu không có mạng: Hệ thống thông báo "Không có kết nối mạng" → Quay về màn hình chính
```

Chức năng "Thi đấu online":
```
Hệ thống hiển thị màn hình thi đấu: đồng hồ 60 giây, câu hỏi, 4 nút đáp án, điểm của mình và điểm của đối thủ → Người chơi chọn một đáp án → Hệ thống chấm điểm, cập nhật điểm của mình lên máy chủ → Hệ thống hiển thị câu hỏi tiếp theo → (lặp lại đến khi hết giờ) → Mỗi khi đối thủ có điểm mới, hệ thống cập nhật điểm đối thủ trên màn hình → Hết 60 giây: Hệ thống so điểm 2 người → Hiển thị kết quả THẮNG / THUA / HÒA kèm tỉ số → Hệ thống lưu trận vào lịch sử đấu → Người chơi nhấn "Về menu"
  → Nếu người chơi nhấn Back: Hệ thống hỏi "Thoát sẽ bị xử thua?" → Đồng ý: người chơi bị xử thua, quay về màn hình chính
  → Nếu đối thủ thoát hoặc mất kết nối: Hệ thống thông báo "Đối thủ đã thoát" → Người chơi được tính thắng
  → Nếu máy của người chơi mất mạng: Hệ thống thông báo "Mất kết nối" → Kết thúc trận
```

Chức năng "Quản lý lịch sử đấu":
```
Người chơi nhấn "Lịch sử" ở màn hình chính → Hệ thống hiển thị danh sách các trận của người chơi, mới nhất ở trên: thời gian, tên đối thủ, tỉ số, kết quả → Người chơi nhấn giữ một trận → Hệ thống hỏi "Xóa trận này?" → Người chơi chọn Đồng ý → Hệ thống xóa trận và cập nhật danh sách
  → Nếu chưa có trận nào: Hệ thống hiển thị "Chưa có trận nào"
  → Nếu người chơi chọn Hủy: Hệ thống giữ nguyên danh sách
```

Chức năng "Chơi offline":
```
Người chơi nhập tên ở màn hình chính → Người chơi nhấn "Chơi offline" → Hệ thống tìm người chơi theo tên (chưa có thì tạo mới; tên trống thì dùng PLAYER_1) → Hệ thống hiển thị màn hình chơi: tên, kỷ lục, điểm, chuỗi đúng, 3 mạng, thanh thời gian 6 giây, câu hỏi, 4 nút đáp án → Người chơi chọn một đáp án
  → Nếu đúng: Hệ thống cộng điểm, tăng chuỗi đúng, hiển thị "+12 PTS!" → Hiển thị câu hỏi tiếp theo
  → Nếu sai hoặc hết 6 giây: Hệ thống trừ 1 mạng, đưa chuỗi đúng về 0, hiển thị "MISS!" → Hiển thị câu hỏi tiếp theo
→ (lặp lại, cứ 5 câu thì tăng mức độ khó) → Hết 3 mạng: Hệ thống lưu kết quả ván → Hiển thị màn hình Game Over: điểm cuối, kỷ lục, "KỶ LỤC MỚI" nếu vượt kỷ lục → Người chơi chọn "Chơi lại" hoặc "Về menu"
```

Chức năng "Xem bảng vàng":
```
Người chơi nhấn "Bảng vàng" ở màn hình chính → Hệ thống lấy 20 kết quả ván có điểm cao nhất → Hiển thị danh sách: hạng, tên người chơi, điểm, chuỗi đúng dài nhất, tỉ lệ trả lời đúng → Người chơi nhấn Back để về màn hình chính
  → Nếu chưa có ván nào: Hệ thống hiển thị "Chưa có kỷ lục"
```

Chức năng "Cài đặt âm thanh":
```
Người chơi nhấn "Cài đặt" ở màn hình chính → Hệ thống hiển thị 2 công tắc Âm thanh và Rung ở trạng thái đã lưu → Người chơi bật / tắt một công tắc → Hệ thống lưu lựa chọn ngay → Từ lần trả lời tiếp theo, hệ thống phát (hoặc không phát) âm thanh và rung theo lựa chọn
```

### 2.5. Những thông tin / đối tượng mà hệ thống cần xử lý

- **Người chơi:** tên, thời điểm tạo.
- **Câu hỏi:** toán hạng thứ nhất, toán hạng thứ hai, toán tử, đáp án đúng, 4 phương án trả lời.
- **Ván chơi:** người chơi, điểm, chuỗi đúng hiện tại, chuỗi đúng dài nhất, số mạng còn lại, số câu đúng, số câu sai, câu hỏi hiện tại.
- **Kết quả ván:** người chơi, điểm, chuỗi đúng dài nhất, số câu đúng, số câu sai, thời điểm chơi.
- **Phòng đấu:** trạng thái, mã đồng bộ câu hỏi, thời điểm tạo, danh sách người chơi trong phòng.
- **Người chơi trong phòng:** mã định danh ẩn danh, tên, điểm, đang trực tuyến hay không, đã xong trận hay chưa.
- **Kết quả trận (lịch sử đấu):** người chơi, tên đối thủ, điểm của mình, điểm của đối thủ, cách kết thúc trận, thời điểm đấu.

### 2.6. Quan hệ giữa các đối tượng

- Một người chơi chơi nhiều ván chơi; một ván chơi thuộc về một người chơi (1-n).
- Một ván chơi lần lượt có nhiều câu hỏi; mỗi câu hỏi được sinh ra cho một ván chơi (1-n). Ván chơi chỉ giữ câu hỏi hiện tại.
- Một ván chơi khi kết thúc sinh ra đúng một kết quả ván; một kết quả ván ứng với một ván chơi (1-1).
- Một người chơi có nhiều kết quả ván; một kết quả ván thuộc về một người chơi (1-n).
- Kỷ lục của người chơi là điểm cao nhất trong các kết quả ván của người chơi đó (quan hệ dẫn xuất, không lưu riêng).
- Bảng vàng gồm nhiều kết quả ván của nhiều người chơi khác nhau trên cùng một máy (quan hệ dẫn xuất).
- Một phòng đấu có tối đa 2 người chơi trong phòng; một người chơi trong phòng chỉ thuộc về một phòng đấu (1-n, n ≤ 2).
- Một người chơi có thể tham gia nhiều phòng đấu theo thời gian, một phòng đấu có 2 người chơi (n-n), thể hiện qua đối tượng trung gian "người chơi trong phòng".
- Chủ phòng là người chơi tạo phòng; mã đồng bộ câu hỏi của phòng do chủ phòng tạo.
- Một trận đấu sinh ra câu hỏi từ mã đồng bộ của phòng: hai người chơi trong cùng một phòng nhận cùng một dãy câu hỏi.
- Một phòng đấu khi kết thúc sinh ra một kết quả trận trên **mỗi** máy (2 máy, mỗi máy một bản ghi theo góc nhìn của người chơi đó).
- Một người chơi có nhiều kết quả trận; một kết quả trận thuộc về một người chơi (1-n).
- Kết quả thắng / thua / hòa của một trận được suy ra từ điểm của mình, điểm của đối thủ và cách kết thúc trận (quan hệ dẫn xuất, không lưu riêng). Riêng cách kết thúc trận phải lưu, vì đối thủ bỏ trận khi đang dẫn điểm thì mình vẫn thắng.
- Ràng buộc: mỗi phòng đấu chỉ nhận người khi đang ở trạng thái `waiting` và chưa đủ 2 người; một người chơi không tự vào phòng do chính mình tạo.

---

## 3. Mô hình nghiệp vụ bằng UML

### 3.1. Danh sách Actor

| STT | Tên Actor | Mô tả |
|---|---|---|
| 1 | Người chơi | Người dùng ứng dụng trên một máy Android. Khởi phát mọi chức năng; trong trận online, đối thủ cũng là một Người chơi trên máy khác |

### 3.2. Các Use Case cho từng Actor

| Mã UC | Actor | Use case | Loại | Người code, demo, vẽ |
|---|---|---|---|---|
| UC01 | Người chơi | Tìm trận | Chính | Hùng |
| UC02 | Người chơi | Thi đấu online | Chính | Hùng |
| UC03 | Người chơi | Quản lý lịch sử đấu | Chính | Hùng |
| UC04 | Người chơi | Chơi offline | Chính | Hoàng |
| UC05 | Người chơi | Xem bảng vàng | Chính | Hoàng |
| UC06 | Người chơi | Cài đặt âm thanh | Chính | Hoàng |
| UC07 | — | Nhập tên người chơi | Include của UC01, UC04 | Chung |
| UC08 | — | Đăng nhập ẩn danh | Include của UC01 | Hùng |
| UC09 | — | Hủy tìm trận | Extend của UC01 | Hùng |
| UC10 | — | Trả lời câu hỏi | Include của UC02, UC04 | Chung |
| UC11 | — | Thoát trận | Extend của UC02 | Hùng |
| UC12 | — | Xem lịch sử đấu | Include của UC03 | Hùng |
| UC13 | — | Xóa trận đấu | Extend của UC03 | Hùng |

Mô tả các use case:
1. "Tìm trận" (UC01): cho phép người chơi tìm một đối thủ đang chờ hoặc tạo phòng mới để chờ đối thủ.
2. "Thi đấu online" (UC02): cho phép 2 người chơi trong cùng một phòng thi đấu 60 giây trên cùng một dãy câu hỏi và nhận kết quả thắng / thua / hòa.
3. "Quản lý lịch sử đấu" (UC03): cho phép người chơi xem và xóa các trận online đã đấu.
4. "Chơi offline" (UC04): cho phép người chơi chơi một mình với 3 mạng, mỗi câu 6 giây, và lưu kết quả ván.
5. "Xem bảng vàng" (UC05): cho phép người chơi xem các kết quả ván offline có điểm cao nhất trên máy.
6. "Cài đặt âm thanh" (UC06): cho phép người chơi bật / tắt âm thanh và rung.
7. "Nhập tên người chơi" (UC07): cho phép UC01 và UC04 xác định người chơi theo tên; tên chưa có thì tạo mới.
8. "Đăng nhập ẩn danh" (UC08): cho phép UC01 lấy mã định danh của máy trên máy chủ mà không cần tài khoản.
9. "Hủy tìm trận" (UC09): cho phép người chơi dừng việc chờ đối thủ trong UC01; phòng đã tạo bị xóa.
10. "Trả lời câu hỏi" (UC10): cho phép UC02 và UC04 nhận đáp án người chơi chọn, chấm đúng / sai và tính điểm.
11. "Thoát trận" (UC11): cho phép người chơi rời trận giữa chừng trong UC02 và bị xử thua.
12. "Xem lịch sử đấu" (UC12): cho phép UC03 hiển thị danh sách các trận đã đấu.
13. "Xóa trận đấu" (UC13): cho phép người chơi xóa một trận khỏi lịch sử trong UC03.

### 3.3. Biểu đồ use case tổng quát

![Hình 1: Biểu đồ use case tổng quát](uc-tong-quat.png)

*Hình 1: Biểu đồ use case tổng quát của Math Dash 1v1.*

Biểu đồ có một tác nhân là Người chơi, khởi phát trực tiếp 6 use case chính (UC01–UC06). Các use case được chia thành 3 nhóm theo người phụ trách: nhóm chơi online (Hùng), nhóm chơi offline (Hoàng) và nhóm dùng chung.
- "Tìm trận" và "Chơi offline" đều bắt buộc gồm bước "Nhập tên người chơi". "Tìm trận" còn bắt buộc gồm "Đăng nhập ẩn danh".
- "Thi đấu online" và "Chơi offline" cùng dùng "Trả lời câu hỏi", vì cách chấm điểm của 2 chế độ giống nhau, chỉ khác ở chỗ chế độ online không trừ mạng.
- "Hủy tìm trận", "Thoát trận" và "Xóa trận đấu" là các bước chỉ xảy ra khi người chơi chủ động chọn, nên là quan hệ `<<extend>>`.

Mã nguồn PlantUML: [`uc-tong-quat.puml`](uc-tong-quat.puml).
