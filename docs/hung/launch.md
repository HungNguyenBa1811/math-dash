# BÁO CÁO THIẾT KẾ CÁ NHÂN — MATH DASH 1v1

Sinh viên: Nguyễn Bá Hùng · Lớp: E23CNPM01
Chức năng trình bày: Chơi online, Xem lịch sử đấu, Quản lý bạn bè

Phạm vi hệ thống:
- Math Dash 1v1 là trò chơi giải toán nhanh trên Android: người chơi thách đấu nhau trên cùng một đề 10 câu trắc nghiệm, hoặc chơi với máy.
- Hệ thống gồm một server và nhiều client:
  - App Android (client): Java, mô hình MVC 3 tầng entity – dao – view.
  - Server Spring Boot: REST, MySQL. Lưu tài khoản, trận đấu, đề bài, quan hệ bạn bè; sinh đề, chấm bài và làm trọng tài.
  - Firebase Realtime Database: kênh báo tức thời giữa các máy: ai đang online, lời mời thách đấu, diễn biến trận đang đấu.
- 3 chức năng thuộc 3 module:
  - Chơi online thuộc module Đấu đối kháng.
  - Xem lịch sử đấu thuộc module Xem lịch sử đấu; module này đọc kết quả các trận chơi online và gồm cả bảng xếp hạng.
  - Quản lý bạn bè thuộc module Bạn bè.

---

## 1. Mô tả chi tiết các chức năng

### 1.1. Chức năng "Chơi online"

#### 1.1.1. Biểu đồ use case chi tiết module Đấu đối kháng

@uc-chi-tiet-doi-khang.png

Hình 1: Biểu đồ use case chi tiết module Đấu đối kháng.

Người chơi khởi phát trực tiếp use case "Chơi online" (UC03) của module. UC03 được phân rã như sau:
- include "Xem danh sách người chơi online": danh sách tên, tổng điểm, trạng thái, tự cập nhật.
- include "Vào trận đấu": use case trừu tượng (chữ nghiêng), có 2 use case con kế thừa:
  - "Gửi lời thách đấu": bấm vào tên một người đang rỗi và chờ người đó trả lời.
  - "Nhận lời thách đấu": đồng ý lời mời của người khác.
- "Từ chối lời thách đấu" extend "Nhận lời thách đấu": người được mời có thể từ chối.
- include "Thi đấu": làm đề 10 câu, nộp bài, nhận kết quả và điểm trận.
- "Thoát trận" extend "Thi đấu": người chơi thoát giữa trận và bị xử thua.
- "Chơi tiếp" extend "Thi đấu": sau mỗi trận, cả 2 chọn chơi tiếp thì vào trận mới.

| Use case | Mô tả |
|---|---|
| Chơi online (UC03) | Thách đấu một người chơi online, thi đấu trên cùng đề do server sinh, nhận kết quả do server xác định |
| Xem danh sách người chơi online | Hiển thị các người chơi đang online kèm tổng điểm, trạng thái rỗi / bận; cập nhật ngay khi có thay đổi |
| Vào trận đấu | Use case trừu tượng: đưa 2 người chơi vào cùng một trận |
| Gửi lời thách đấu | Người chơi bấm vào tên một người đang rỗi; hệ thống gửi lời mời và chờ trả lời |
| Nhận lời thách đấu | Người được mời đồng ý; server sinh đề, tạo trận và gửi đề cho cả 2 máy |
| Từ chối lời thách đấu | Người được mời từ chối; người mời nhận thông báo |
| Thi đấu | Hai người làm cùng đề 10 câu trong tối đa 60 giây, nộp bài; server chấm, xác định kết quả, cộng điểm |
| Thoát trận | Người chơi rời trận trước khi trận kết thúc (hoặc mất kết nối) và bị xử thua |
| Chơi tiếp | Cả 2 người chọn chơi tiếp sau trận; server sinh đề mới và tạo trận tiếp theo |

#### 1.1.2. Mô tả chi tiết chức năng "Chơi online"

Người chơi đã đăng nhập → Giao diện chính hiện lên với:
- Dòng thông tin của mình: tên hiển thị, tổng điểm.
- Danh sách người chơi online, xếp theo tổng điểm giảm dần; mỗi dòng có tên, tổng điểm, trạng thái Rỗi (màu xanh) / Bận (màu xám).
- Các nút Chơi với máy, Bảng xếp hạng, Đăng xuất.

→ Danh sách tự cập nhật khi có người vào, người thoát hoặc người đổi trạng thái → Người chơi bấm vào tên một người đang rỗi → Hệ thống gửi lời mời tới máy người đó → Hộp thoại "Đang chờ <tên> trả lời..." hiện lên với nút Hủy.

Trên máy người được mời: hộp thoại "<tên> (<tổng điểm> điểm) thách đấu bạn" hiện lên với 2 nút Đồng ý, Từ chối → Người được mời nhấn Đồng ý → Server kiểm tra cả 2 vẫn online và rỗi → Sinh đề 10 câu, tạo trận, chuyển cả 2 sang Bận, gửi đề cho cả 2 máy → Cả 2 máy chuyển sang giao diện thi đấu.

- Bấm vào người đang bận: "<tên> đang bận", không gửi lời mời.
- Người được mời nhấn Từ chối: máy người mời đóng hộp thoại chờ và báo "<tên> đã từ chối lời mời".
- Người mời nhấn Hủy khi đang chờ: lời mời bị hủy; người được mời có nhấn Đồng ý sau đó thì báo "Không bắt đầu được trận đấu".
- Người mời đã tắt ứng dụng hoặc đã vào trận khác khi người được mời nhấn Đồng ý: "Không bắt đầu được trận đấu".

Thi đấu: Giao diện thi đấu hiện lên với:
- Ô thời gian đếm ngược từ 60 giây.
- Dòng trạng thái đối thủ: "<tên đối thủ> đang làm bài".
- Danh sách 10 câu hỏi cuộn được; mỗi câu có số thứ tự, phép tính và 4 phương án dạng nút chọn.
- Nút Nộp bài, nút Thoát.

→ Người chơi chọn một phương án cho từng câu, đổi lựa chọn được trước khi nộp → Nhấn Nộp bài → Server ghi nhận thời điểm nộp, chấm bài → Danh sách câu bị khóa, nút Nộp bài ẩn, hiện "Đã nộp bài, đang chờ đối thủ..." → Khi đối thủ nộp bài, dòng trạng thái đối thủ trên máy còn lại đổi thành "<tên đối thủ> đã nộp bài" → Khi cả 2 đã nộp, server xác định kết quả, cộng điểm trận, cập nhật tổng điểm, báo cả 2 máy.

Giao diện kết quả hiện đè lên giao diện thi đấu với:
- Dòng kết quả THẮNG! (xanh) / THUA! (đỏ) / HÒA! (vàng).
- Bảng so sánh 2 người: số câu đúng, thời gian làm bài.
- Điểm trận được cộng và tổng điểm mới.
- Câu hỏi "Chơi tiếp?" với 2 nút Chơi tiếp, Dừng.

→ Người chơi nhấn Chơi tiếp → Hiện "Đang chờ đối thủ..." → Khi đối thủ cũng chọn Chơi tiếp: server sinh đề mới, tạo trận mới → Cả 2 máy vào giao diện thi đấu của trận mới.

- Hết 60 giây mà chưa nộp: hệ thống tự nộp, câu chưa chọn tính là sai.
- Nhấn Thoát hoặc Back khi đang thi đấu: hỏi "Thoát sẽ bị xử thua?" với 2 nút Ở lại / Thoát → Thoát: bị xử thua, về giao diện chính.
- Đối thủ thoát hoặc mất kết nối: báo "Đối thủ đã thoát, bạn thắng" → giao diện kết quả THẮNG!, không có nút Chơi tiếp.
- Một trong 2 người nhấn Dừng: cả 2 về giao diện chính, trạng thái trở lại Rỗi; máy người còn lại báo "Đối thủ đã dừng chơi".
- Không kết nối được server khi nộp bài: "Không kết nối được máy chủ, kiểm tra kết nối mạng", nút Nộp bài bấm lại được.

Dữ liệu mẫu:

| Người chơi | Tên đăng nhập | Tên hiển thị | Tổng điểm | Trạng thái | Máy |
|---|---|---|---|---|---|
| Hùng | hung | HUNG | 12,5 | Rỗi | Máy 1, người mời |
| Hoàng | hoang | HOANG | 9 | Rỗi | Máy 2, người được mời |
| Minh | minh | MINH | 15 | Bận | Máy 3, đang đấu với người khác |
| Lan | lan | LAN | 4,5 | Rỗi | Máy 4 |

Trận số 37 bắt đầu lúc 20:15:03,0 ngày 25/10/2026. Đề bài server sinh cho trận 37:

| Câu | Phép tính | 4 phương án | Đáp án đúng |
|---|---|---|---|
| 1 | 12 + 7 | 17, 19, 21, 18 | 19 |
| 2 | 9 × 6 | 48, 54, 56, 63 | 54 |
| 3 | 45 − 18 | 27, 23, 33, 37 | 27 |
| 4 | 8 × 7 | 54, 56, 48, 64 | 56 |
| 5 | 36 + 25 | 51, 61, 59, 71 | 61 |
| 6 | 72 − 39 | 33, 43, 31, 37 | 33 |
| 7 | 7 × 12 | 74, 84, 82, 96 | 84 |
| 8 | 58 + 27 | 75, 85, 83, 95 | 85 |
| 9 | 91 − 46 | 45, 55, 35, 47 | 45 |
| 10 | 6 × 15 | 80, 90, 96, 75 | 90 |

#### 1.1.3. Kịch bản UC03 "Chơi online"

| | |
|---|---|
| Use case | Chơi online (UC03), gồm Xem danh sách người chơi online, Gửi lời thách đấu, Nhận lời thách đấu, Thi đấu, Chơi tiếp |
| Actor | Người chơi: Hùng ở máy 1 là người mời, Hoàng ở máy 2 là người được mời; mỗi người là actor trên máy của mình |
| Tiền điều kiện | Cả 2 người chơi đã đăng nhập, máy có kết nối mạng, đang ở giao diện chính |
| Hậu điều kiện | Trận đấu được lưu trên server cùng đề, số câu đúng và thời gian nộp bài của 2 người; tổng điểm của 2 người được cập nhật; cả 2 thấy cùng một kết quả |

Kịch bản chuẩn chia 4 đoạn, khớp với các use case con:
- A. Gửi lời thách đấu (máy 1).
- B. Nhận lời thách đấu (máy 2).
- C. Thi đấu (máy 1; máy 2 diễn ra tương tự).
- D. Chơi tiếp.

Kịch bản chuẩn

A. Gửi lời thách đấu

1. Hùng đăng nhập trên máy 1. Giao diện chính hiện lên gồm:

   | Thành phần | Giá trị |
   |---|---|
   | Dòng thông tin | HUNG · 12,5 điểm |
   | Danh sách người chơi online | Bảng dưới đây |
   | Nút | Chơi với máy, Bảng xếp hạng, Đăng xuất |

   | Tên | Tổng điểm | Trạng thái |
   |---|---|---|
   | MINH | 15 | Bận |
   | HOANG | 9 | Rỗi |
   | LAN | 4,5 | Rỗi |

2. Hùng bấm vào dòng HOANG.
3. Hệ thống thấy HOANG đang rỗi, gửi lời mời tới máy 2. Máy 1 hiện hộp thoại "Đang chờ HOANG trả lời..." với nút Hủy.

B. Nhận lời thách đấu

4. Trên máy 2, Hoàng đang ở giao diện chính. Hộp thoại "HUNG (12,5 điểm) thách đấu bạn" hiện lên với 2 nút Đồng ý, Từ chối.
5. Hoàng nhấn Đồng ý.
6. Server kiểm tra HUNG và HOANG vẫn online và đang rỗi. Server sinh đề 10 câu như bảng dữ liệu mẫu, tạo trận số 37 bắt đầu lúc 20:15:03,0, chuyển HUNG và HOANG sang Bận, rồi gửi đề cho cả 2 máy.
7. Máy 2 chuyển sang giao diện thi đấu. Cùng lúc, máy 1 đóng hộp thoại chờ và chuyển sang giao diện thi đấu. Trên máy của Lan, HUNG và HOANG chuyển sang Bận.

C. Thi đấu

8. Giao diện thi đấu trên máy 1 gồm:

   | Thành phần | Giá trị |
   |---|---|
   | Ô thời gian | 60 |
   | Trạng thái đối thủ | HOANG đang làm bài |
   | Danh sách câu hỏi | 10 câu như bảng dữ liệu mẫu, mỗi câu 4 nút chọn, chưa câu nào được chọn |
   | Nút | Nộp bài, Thoát |

   Máy 2 hiện cùng đề, dòng trạng thái là "HUNG đang làm bài".
9. Hùng lần lượt chọn 19, 54, 27, 56, 61, 33, 84, 85, 45, 90. Ô thời gian đếm ngược mỗi giây.
10. Khi ô thời gian còn 22, Hùng nhấn Nộp bài.
11. Server nhận bài làm của HUNG lúc 20:15:41,2, tức thời gian làm bài 38,2 giây, chấm đúng 10/10 câu. Máy 1 khóa danh sách câu, ẩn nút Nộp bài, hiện "Đã nộp bài, đang chờ đối thủ...". Trên máy 2, dòng trạng thái đổi thành "HUNG đã nộp bài".
12. Hoàng nộp bài trên máy 2. Server nhận lúc 20:15:48,6, tức 45,6 giây, đúng 10/10 câu.
13. Server xác định kết quả: không ai bỏ trận, cả 2 đúng hết, HUNG nộp nhanh hơn nên HUNG thắng. HUNG được 1 điểm, tổng điểm thành 13,5; HOANG được 0 điểm, tổng điểm giữ 9. Server lưu trận và báo cả 2 máy.
14. Giao diện kết quả hiện lên trên máy 1 gồm:

    | Thành phần | Giá trị |
    |---|---|
    | Dòng kết quả | THẮNG! (màu xanh) |
    | Bạn | 10/10 câu đúng · 38,2 giây |
    | HOANG | 10/10 câu đúng · 45,6 giây |
    | Điểm | +1 điểm · Tổng điểm: 13,5 |
    | Câu hỏi | Chơi tiếp? |
    | Nút | Chơi tiếp, Dừng |

    Máy 2 hiện THUA! (màu đỏ), +0 điểm, tổng điểm 9.

D. Chơi tiếp

15. Hùng nhấn Chơi tiếp. Máy 1 ẩn 2 nút, hiện "Đang chờ đối thủ...".
16. Hoàng nhấn Chơi tiếp trên máy 2.
17. Server sinh đề mới, câu đầu là 23 + 48, tạo trận số 38 và gửi đề cho cả 2 máy.
18. Cả 2 máy hiện giao diện thi đấu của trận 38, ô thời gian 60. Trận diễn ra như các bước 8–14.

Kịch bản ngoại lệ

| Bước | Tình huống ngoại lệ và cách xử lý |
|---|---|
| 2 | Hùng bấm vào dòng MINH. MINH đang Bận nên hệ thống báo "MINH đang bận", không gửi lời mời, giữ nguyên giao diện chính. |
| 3 | Hủy lời mời. Hùng nhấn Hủy trên hộp thoại chờ. Lời mời bị hủy, máy 1 ở lại giao diện chính. Nếu Hoàng nhấn Đồng ý sau đó, server thấy lời mời đã hủy nên không tạo trận; máy 2 báo "Không bắt đầu được trận đấu". |
| 5 | Từ chối. Hoàng nhấn Từ chối. Máy 2 đóng hộp thoại. Máy 1 đóng hộp thoại chờ và báo "HOANG đã từ chối lời mời"; Hùng vẫn ở giao diện chính, có thể mời người khác. |
| 6 | Hùng tắt ứng dụng trước khi Hoàng nhấn Đồng ý. Server thấy HUNG không còn online nên không tạo trận. Máy 2 báo "Không bắt đầu được trận đấu" và ở lại giao diện chính. |
| 9–10 | Hết giờ. Hùng mới chọn 8 câu thì ô thời gian về 0. Hệ thống tự nộp bài; 2 câu chưa chọn tính là sai, HUNG đúng 8/10. |
| 9–10 | Thoát trận. Hùng nhấn Thoát. Hệ thống hỏi "Thoát sẽ bị xử thua?" với 2 nút Ở lại, Thoát. Hùng nhấn Thoát: server xử HUNG thua, HOANG thắng và được 1 điểm (tổng 10), trả cả 2 về Rỗi. Máy 1 về giao diện chính. Máy 2 báo "Đối thủ đã thoát, bạn thắng" và hiện giao diện kết quả THẮNG! chỉ có nút Về menu, không hỏi chơi tiếp. Nhấn Ở lại thì tiếp tục làm bài, ô thời gian vẫn chạy. |
| 9–12 | Đối thủ mất kết nối. Máy 2 mất mạng khi đang làm bài. HOANG biến khỏi danh sách online; hệ thống xử HOANG bỏ trận. Máy 1 báo "Đối thủ đã thoát, bạn thắng" như ngoại lệ trên. |
| 10 | Máy 1 không kết nối được server khi nộp bài. Hệ thống báo "Không kết nối được máy chủ, kiểm tra kết nối mạng", nút Nộp bài vẫn bấm được; ô thời gian vẫn chạy. |
| 13 | Một người đúng hết. Hoàng chọn 74 ở câu 7, đúng 9/10, dù nộp ở giây 30,0. HUNG đúng hết nên HUNG thắng, không xét thời gian. |
| 13 | Hòa. HUNG chọn 23 ở câu 3, HOANG chọn 35 ở câu 9; cả 2 đều có câu sai nên hòa. Mỗi người được 0,5 điểm: HUNG 13, HOANG 9,5. Giao diện kết quả hiện HÒA! (màu vàng). |
| 15–16 | Dừng chơi. Hoàng nhấn Dừng. Server trả HUNG và HOANG về Rỗi. Máy 2 về giao diện chính. Máy 1 báo "Đối thủ đã dừng chơi" và về giao diện chính. |

### 1.2. Chức năng "Xem lịch sử đấu"

#### 1.2.1. Biểu đồ use case chi tiết module Xem lịch sử đấu

@uc-chi-tiet-lich-su-dau.png

Hình 2: Biểu đồ use case chi tiết module Xem lịch sử đấu.

Người chơi khởi phát trực tiếp use case "Xem lịch sử đấu" (UC04). UC04 được phân rã như sau:
- include "Xem trận của tôi": thẻ được mở sẵn, gồm biểu đồ tròn tỉ lệ thắng / hòa / thua và danh sách các trận đã đấu.
- "Xem chi tiết trận" extend "Xem trận của tôi": người chơi bấm vào một trận để xem lại đề, đáp án và kết quả của trận đó.
- "Xem bảng xếp hạng" extend "Xem lịch sử đấu": người chơi chọn thẻ Bảng xếp hạng để xem thứ hạng mọi người chơi.

| Use case | Mô tả |
|---|---|
| Xem lịch sử đấu (UC04) | Xem kết quả các trận chơi online mình đã đấu và thứ hạng của mọi người chơi |
| Xem trận của tôi | Xem tỉ lệ thắng / hòa / thua dạng biểu đồ tròn và danh sách các trận đã kết thúc, trận mới nhất đứng đầu |
| Xem chi tiết trận | Xem đối thủ, kết quả, điểm trận, số câu đúng và thời gian làm bài của 2 bên, đề 10 câu kèm đáp án đúng của một trận |
| Xem bảng xếp hạng | Xem thứ hạng mọi người chơi theo tổng điểm, trung bình điểm đối thủ, trung bình thời gian thắng |

#### 1.2.2. Mô tả chi tiết chức năng "Xem lịch sử đấu"

Người chơi đã đăng nhập, đang ở giao diện chính → Người chơi nhấn Lịch sử đấu → Hệ thống lấy các trận chơi online đã kết thúc mà người chơi tham gia, trận mới nhất đứng đầu → Giao diện lịch sử đấu hiện lên với:
- Thanh tiêu đề "Lịch sử đấu" có nút Back.
- 2 thẻ Trận của tôi, Bảng xếp hạng; thẻ Trận của tôi được mở sẵn.
- Khối tóm tắt: biểu đồ tròn dạng vành khuyên chia 3 phần Thắng (xanh lá), Hòa (vàng), Thua (đỏ), giữa biểu đồ ghi tỉ lệ thắng; bên cạnh ghi số trận thắng, hòa, thua và tổng số trận.
- Danh sách trận cuộn được. Mỗi dòng có dải màu theo kết quả, tên đối thủ, số trận, THẮNG / HÒA / THUA, điểm trận, số câu đúng của mình và của đối thủ, thời điểm kết thúc.

→ Người chơi bấm vào một trận → Hệ thống lấy trận kèm đề và đáp án đúng → Giao diện chi tiết trận hiện lên với:
- Thanh tiêu đề "Trận #<số trận>" kèm thời điểm, có nút Back.
- Dòng kết quả THẮNG! (xanh) / THUA! (đỏ) / HÒA! (vàng) và điểm trận.
- Bảng so sánh 2 người: số câu đúng, thời gian làm bài.
- Danh sách 10 câu hỏi, mỗi câu ghi phép tính và đáp án đúng.

→ Người chơi nhấn Back → Giao diện lịch sử đấu hiện lại ở thẻ Trận của tôi.

→ Người chơi chọn thẻ Bảng xếp hạng → Hệ thống lấy mọi người chơi và các trận đã kết thúc, tính 3 tiêu chí rồi sắp xếp → Thẻ Bảng xếp hạng hiện danh sách mọi người chơi; mỗi dòng gồm hạng, tên, tổng điểm, trung bình điểm đối thủ, trung bình thời gian thắng; dòng của chính người chơi được tô nổi bật → Người chơi nhấn Back → Quay về giao diện chính.

Thứ tự xếp hạng:
1. Tổng điểm, giảm dần.
2. Cùng tổng điểm thì xét trung bình tổng điểm của các đối thủ đã gặp, giảm dần.
3. Cùng cả 2 tiêu chí trên thì xét trung bình thời gian làm bài trong các trận thắng, tăng dần. Người chưa thắng trận nào hiện "—" và đứng sau.

- Người chơi chưa đấu trận nào: biểu đồ để trống, danh sách hiện "Bạn chưa đấu trận nào".
- Trận có người bỏ trận: dòng của trận ghi thêm "Đối thủ bỏ trận" hoặc "Bạn bỏ trận".
- Không kết nối được server khi mở lịch sử đấu: "Không tải được lịch sử đấu, kiểm tra kết nối mạng", danh sách để trống.
- Không kết nối được server khi mở chi tiết trận: "Không tải được chi tiết trận, kiểm tra kết nối mạng", quay lại giao diện lịch sử đấu.
- Không kết nối được server khi chọn thẻ Bảng xếp hạng: "Không tải được bảng xếp hạng, kiểm tra kết nối mạng", danh sách để trống.

#### 1.2.3. Kịch bản UC04 "Xem lịch sử đấu"

| | |
|---|---|
| Use case | Xem lịch sử đấu (UC04), gồm Xem trận của tôi, Xem chi tiết trận, Xem bảng xếp hạng |
| Actor | Người chơi (Hùng) |
| Tiền điều kiện | Hùng đã đăng nhập, máy có kết nối mạng, đang ở giao diện chính; Hùng đã đấu 20 trận online, trận gần nhất là trận số 37 |
| Hậu điều kiện | Hùng thấy tỉ lệ thắng / hòa / thua, các trận đã đấu, chi tiết một trận và thứ hạng của mọi người chơi; dữ liệu không thay đổi |

Kịch bản chuẩn chia 2 đoạn, khớp với các use case con:
- A. Xem trận của tôi và chi tiết trận.
- B. Xem bảng xếp hạng.

Kịch bản chuẩn

A. Xem trận của tôi và chi tiết trận

1. Ở giao diện chính (dòng "HUNG · 13,5 điểm"), Hùng nhấn nút Lịch sử đấu.
2. Hệ thống lấy 20 trận đã kết thúc của HUNG, trận mới nhất đứng đầu. Giao diện lịch sử đấu hiện lên, thẻ Trận của tôi được mở sẵn, gồm:

   | Thành phần | Giá trị |
   |---|---|
   | Thanh tiêu đề | Lịch sử đấu, nút Back |
   | Thẻ | Trận của tôi (đang chọn), Bảng xếp hạng |
   | Biểu đồ tròn | Thắng 60%, Hòa 15%, Thua 25%; giữa biểu đồ ghi "60% thắng" |
   | Chú giải | Thắng 12 · Hòa 3 · Thua 5 · 20 trận |
   | Danh sách trận | Bảng dưới đây, cuộn xuống để xem các trận cũ hơn |

   | Trận | Đối thủ | Kết quả | Điểm | Câu đúng | Kết thúc |
   |---|---|---|---|---|---|
   | #37 | HOANG | THẮNG | +1 | 10 – 10 | 25/10 20:15 |
   | #35 | LAN | HÒA | +0,5 | 9 – 8 | 25/10 19:58 |
   | #33 | MINH | THUA | +0 | 10 – 10 | 25/10 19:40 |
   | #28 | HOANG | THẮNG · Đối thủ bỏ trận | +1 | 6 – 3 | 24/10 20:47 |
   | … | … | … | … | … | … |

   Tổng điểm trận của 20 trận là 12 × 1 + 3 × 0,5 = 13,5, khớp với tổng điểm của HUNG.
3. Hùng bấm vào dòng trận #37.
4. Hệ thống lấy trận 37 kèm đề và đáp án đúng. Giao diện chi tiết trận hiện lên gồm:

   | Thành phần | Giá trị |
   |---|---|
   | Thanh tiêu đề | Trận #37 · 25/10/2026 20:15, nút Back |
   | Dòng kết quả | THẮNG! (màu xanh) · +1 điểm |
   | Bạn · HUNG | 10/10 câu đúng · 38,2 giây |
   | HOANG | 10/10 câu đúng · 45,6 giây |
   | Đề bài và đáp án | 10 câu như bảng dữ liệu mẫu: 12 + 7 = 19, 9 × 6 = 54, …, 6 × 15 = 90 |

5. Hùng xem xong và nhấn Back.
6. Giao diện lịch sử đấu hiện lại ở thẻ Trận của tôi, danh sách giữ nguyên vị trí.

B. Xem bảng xếp hạng

7. Hùng chọn thẻ Bảng xếp hạng.
8. Hệ thống lấy mọi người chơi và các trận đã kết thúc, tính 3 tiêu chí rồi sắp xếp. Thẻ Bảng xếp hạng hiện danh sách:

   | Hạng | Tên | Tổng điểm | TB điểm đối thủ | TB thời gian thắng |
   |---|---|---|---|---|
   | 1 | MINH | 15 | 10,5 | 41,3 giây |
   | 2 | HUNG (dòng nổi bật) | 13,5 | 11 | 39,8 giây |
   | 3 | LAN | 13,5 | 9 | 44,0 giây |
   | 4 | HOANG | 9 | 13,5 | 47,2 giây |
   | 5 | AN | 4,5 | 12 | 49,0 giây |
   | 6 | NAM | 4,5 | 12 | 52,1 giây |
   | 7 | TUAN | 0 | 13,5 | — |

   - HUNG và LAN cùng 13,5 điểm, HUNG đã gặp các đối thủ mạnh hơn (11 so với 9) nên đứng trên.
   - AN và NAM trùng cả 2 tiêu chí đầu, AN thắng nhanh hơn nên đứng trên.
9. Hùng xem danh sách rồi nhấn Back.
10. Giao diện chính hiện lại.

Kịch bản ngoại lệ

| Bước | Tình huống ngoại lệ và cách xử lý |
|---|---|
| 2 | Chưa đấu trận nào. TUAN vừa đăng ký, mở Lịch sử đấu. Biểu đồ tròn để trống, chú giải ghi "Thắng 0 · Hòa 0 · Thua 0 · 0 trận", danh sách hiện "Bạn chưa đấu trận nào". |
| 2 | Máy mất mạng hoặc server không phản hồi. Hệ thống báo "Không tải được lịch sử đấu, kiểm tra kết nối mạng", biểu đồ và danh sách để trống; Hùng nhấn Back để về giao diện chính. |
| 2 | Trận có người bỏ trận. Ở trận #28, HOANG nhấn Thoát giữa trận nên bị xử thua. Dòng của trận ghi "THẮNG · Đối thủ bỏ trận"; trên máy của Hoàng, dòng này ghi "THUA · Bạn bỏ trận". |
| 4 | Máy mất mạng khi Hùng bấm vào trận #37. Hệ thống báo "Không tải được chi tiết trận, kiểm tra kết nối mạng" và quay lại giao diện lịch sử đấu. |
| 8 | Máy mất mạng khi Hùng chọn thẻ Bảng xếp hạng. Hệ thống báo "Không tải được bảng xếp hạng, kiểm tra kết nối mạng", danh sách để trống; Hùng chọn lại thẻ để tải lại. |

### 1.3. Chức năng "Quản lý bạn bè"

Dữ liệu mẫu:

| Người chơi | Tên đăng nhập | Tên hiển thị | Tổng điểm | Đang online | Máy |
|---|---|---|---|---|---|
| Hùng | hung | HUNG | 13,5 | Có | Máy 1, người gửi lời mời |
| Hoàng | hoang | HOANG | 9 | Có | Máy 2, người nhận lời mời |
| Minh | minh | MINH | 15 | Có | Máy 3 |
| Tuấn | tuan | TUAN | 0 | Không | |
| Lan | lan | LAN | 13,5 | Không | |

Quan hệ bạn bè có sẵn trước khi bắt đầu:

| Người gửi | Người nhận | Trạng thái | Thời điểm gửi |
|---|---|---|---|
| MINH | HUNG | Đã là bạn bè | 20/10/2026 19:02 |
| HUNG | TUAN | Đã là bạn bè | 22/10/2026 08:45 |
| LAN | HUNG | Đang chờ trả lời | 26/10/2026 21:30 |

#### 1.3.1. Biểu đồ use case chi tiết module Bạn bè

@uc-chi-tiet-ban-be.png

Hình 3: Biểu đồ use case chi tiết module Bạn bè.

Người chơi khởi phát trực tiếp use case "Quản lý bạn bè" (UC07). UC07 được phân rã như sau:
- include "Xem danh sách bạn bè": mỗi lần mở giao diện bạn bè, hệ thống luôn hiện lời mời đã nhận và danh sách bạn bè kèm trạng thái online.
- "Gửi lời mời kết bạn" extend "Quản lý bạn bè": người chơi nhập tên đăng nhập của một người và gửi lời mời.
- "Đồng ý lời mời kết bạn" extend "Quản lý bạn bè": người nhận đồng ý, hai người thành bạn bè.
- "Từ chối lời mời kết bạn" extend "Quản lý bạn bè": người nhận từ chối, lời mời bị xóa.
- "Hủy kết bạn" extend "Quản lý bạn bè": một trong hai người xóa quan hệ bạn bè; người gửi cũng dùng nó để rút lại lời mời chưa được trả lời.

| Use case | Mô tả |
|---|---|
| Quản lý bạn bè (UC07) | Mở giao diện bạn bè để xem, gửi, trả lời lời mời kết bạn và hủy kết bạn |
| Xem danh sách bạn bè | Hiển thị lời mời đã nhận, các bạn bè kèm tổng điểm và trạng thái Online / Offline, các lời mời mình đã gửi đang chờ trả lời |
| Gửi lời mời kết bạn | Người chơi nhập tên đăng nhập của người khác; hệ thống tạo lời mời đang chờ |
| Đồng ý lời mời kết bạn | Người nhận đồng ý; hai người trở thành bạn bè |
| Từ chối lời mời kết bạn | Người nhận từ chối; lời mời bị xóa |
| Hủy kết bạn | Xóa quan hệ bạn bè, hoặc rút lại lời mời mình đã gửi |

#### 1.3.2. Mô tả chi tiết chức năng "Quản lý bạn bè"

Người chơi đã đăng nhập, đang ở giao diện chính → Người chơi nhấn Bạn bè → Hệ thống lấy các lời mời kết bạn và các quan hệ bạn bè của người chơi, cùng danh sách người chơi đang online → Giao diện bạn bè hiện lên với:
- Hàng trên cùng: ô nhập tên đăng nhập và nút Kết bạn.
- Mục "Lời mời kết bạn (số lời mời)", chỉ hiện khi có lời mời đã nhận: mỗi dòng có tên người gửi, nút Đồng ý, nút Từ chối.
- Mục "Bạn bè (số người)": mỗi dòng có chấm trạng thái xanh (Online) hoặc xám (Offline), tên, tổng điểm, nút Hủy kết bạn dạng biểu tượng ×. Các lời mời mình đã gửi nằm cuối mục, chữ xám "Đang chờ trả lời" thay cho tổng điểm. Chưa có ai thì hiện "Chưa có bạn bè".

→ Trạng thái Online / Offline tự cập nhật khi bạn bè vào hoặc thoát ứng dụng.

Gửi lời mời: Người chơi nhập tên đăng nhập của một người và nhấn Kết bạn → Hệ thống tìm người chơi theo tên đăng nhập, kiểm tra giữa 2 người chưa có lời mời hay quan hệ bạn bè nào → Tạo lời mời đang chờ → Thông báo "Đã gửi lời mời kết bạn tới <tên>" → Ô tên đăng nhập được xóa trống, cuối mục Bạn bè thêm dòng "<tên> · Đang chờ trả lời".

Đồng ý: Trên máy người nhận, người chơi nhấn Đồng ý ở một lời mời → Hệ thống kiểm tra lời mời vẫn đang chờ và người chơi là người nhận → Ghi nhận 2 người là bạn bè → Lời mời biến khỏi mục Lời mời kết bạn; người gửi hiện trong mục Bạn bè với tổng điểm và trạng thái.

- Nhấn Từ chối: hệ thống xóa lời mời; dòng biến khỏi mục Lời mời kết bạn.
- Nhấn × ở một người bạn: hỏi "Hủy kết bạn với <tên>?" với 2 nút Không / Hủy kết bạn → Hủy kết bạn: hệ thống xóa quan hệ, dòng biến khỏi danh sách trên máy mình; máy người kia thấy thay đổi khi mở lại giao diện bạn bè.
- Nhấn × ở một lời mời mình đã gửi: lời mời bị rút lại theo cách như trên.
- Tên đăng nhập không tồn tại: "Không tìm thấy người chơi".
- Nhập tên đăng nhập của chính mình: "Không thể kết bạn với chính mình".
- Hai người đã là bạn bè hoặc đã có lời mời giữa 2 người, theo bất kỳ chiều nào: "Đã có lời mời hoặc đã là bạn bè".
- Ô tên đăng nhập bỏ trống: "Vui lòng nhập tên đăng nhập".
- Lời mời đã bị người gửi rút lại khi người nhận nhấn Đồng ý: "Lời mời không còn"; dòng biến khỏi mục Lời mời kết bạn.
- Không kết nối được server: "Không kết nối được máy chủ, kiểm tra kết nối mạng", danh sách giữ nguyên.

#### 1.3.3. Kịch bản UC07 "Quản lý bạn bè"

| | |
|---|---|
| Use case | Quản lý bạn bè (UC07), gồm Xem danh sách bạn bè, Gửi lời mời kết bạn, Đồng ý lời mời kết bạn |
| Actor | Người chơi: Hùng ở máy 1 là người gửi lời mời, Hoàng ở máy 2 là người nhận; mỗi người là actor trên máy của mình |
| Tiền điều kiện | Cả 2 người chơi đã đăng nhập, máy có kết nối mạng, đang ở giao diện chính; giữa HUNG và HOANG chưa có lời mời hay quan hệ bạn bè nào |
| Hậu điều kiện | Server lưu quan hệ bạn bè giữa HUNG và HOANG; mỗi người thấy người kia trong danh sách bạn bè |

Kịch bản chuẩn chia 2 đoạn, khớp với các use case con:
- A. Gửi lời mời kết bạn (máy 1).
- B. Đồng ý lời mời kết bạn (máy 2).

Kịch bản chuẩn

A. Gửi lời mời kết bạn

1. Ở giao diện chính trên máy 1 (dòng "HUNG · 13,5 điểm"), Hùng nhấn nút Bạn bè.
2. Hệ thống lấy lời mời và quan hệ bạn bè của HUNG, cùng danh sách người chơi đang online. Giao diện bạn bè hiện lên gồm:

   | Thành phần | Giá trị |
   |---|---|
   | Ô tên đăng nhập | Trống |
   | Nút | Kết bạn |
   | Lời mời kết bạn (1) | LAN, nút Đồng ý, nút Từ chối |
   | Bạn bè (2) | Bảng dưới đây |

   | Trạng thái | Tên | Tổng điểm | Nút |
   |---|---|---|---|
   | ● Online (xanh) | MINH | 15 | × |
   | ● Offline (xám) | TUAN | 0 | × |

3. Hùng nhập "hoang" vào ô tên đăng nhập và nhấn Kết bạn.
4. Hệ thống tìm thấy người chơi HOANG, thấy giữa HUNG và HOANG chưa có lời mời hay quan hệ nào, tạo lời mời kết bạn đang chờ lúc 21:40 ngày 27/10/2026. Máy 1 thông báo "Đã gửi lời mời kết bạn tới HOANG", xóa trống ô tên đăng nhập. Mục Bạn bè đổi thành:

   | Trạng thái | Tên | Tổng điểm | Nút |
   |---|---|---|---|
   | ● Online (xanh) | MINH | 15 | × |
   | ● Offline (xám) | TUAN | 0 | × |
   | ● (xám) | HOANG | Đang chờ trả lời | × |

B. Đồng ý lời mời kết bạn

5. Trên máy 2, Hoàng nhấn nút Bạn bè ở giao diện chính. Giao diện bạn bè hiện lên gồm:

   | Thành phần | Giá trị |
   |---|---|
   | Ô tên đăng nhập | Trống |
   | Nút | Kết bạn |
   | Lời mời kết bạn (1) | HUNG, nút Đồng ý, nút Từ chối |
   | Bạn bè (0) | "Chưa có bạn bè" |

6. Hoàng nhấn Đồng ý ở dòng HUNG.
7. Hệ thống thấy lời mời vẫn đang chờ và HOANG là người nhận, ghi nhận HUNG và HOANG là bạn bè. Trên máy 2, mục Lời mời kết bạn ẩn đi; mục Bạn bè (1) có một dòng: ● Online (xanh) · HUNG · 13,5 điểm · ×.
8. Lần sau Hùng mở giao diện bạn bè trên máy 1, dòng HOANG trong mục Bạn bè đổi thành ● Online (xanh) · HOANG · 9 điểm · ×.

Kịch bản ngoại lệ

| Bước | Tình huống ngoại lệ và cách xử lý |
|---|---|
| 2 | Từ chối. Hùng nhấn Từ chối ở dòng LAN. Hệ thống xóa lời mời của LAN; mục Lời mời kết bạn ẩn đi. LAN không nhận thông báo nào. |
| 2 | Hủy kết bạn. Hùng nhấn × ở dòng TUAN. Hệ thống hỏi "Hủy kết bạn với TUAN?" với 2 nút Không, Hủy kết bạn. Hùng nhấn Hủy kết bạn: hệ thống xóa quan hệ, mục Bạn bè còn MINH. Nhấn Không thì giữ nguyên. |
| 2 | Máy 1 không kết nối được server. Hệ thống báo "Không kết nối được máy chủ, kiểm tra kết nối mạng", 2 mục để trống; Hùng nhấn Back để về giao diện chính. |
| 3 | Hùng nhấn Kết bạn khi ô tên đăng nhập trống. Hệ thống báo "Vui lòng nhập tên đăng nhập", không gửi gì. |
| 4 | Hùng nhập "hoangg". Không có người chơi nào có tên đăng nhập này, hệ thống báo "Không tìm thấy người chơi", giữ nguyên nội dung ô để Hùng sửa. |
| 4 | Hùng nhập "hung". Hệ thống báo "Không thể kết bạn với chính mình". |
| 4 | Hùng nhập "minh" (đã là bạn) hoặc "lan" (LAN đã gửi lời mời cho HUNG). Hệ thống báo "Đã có lời mời hoặc đã là bạn bè", không tạo lời mời mới. Muốn kết bạn với LAN thì Hùng nhấn Đồng ý ở mục Lời mời kết bạn. |
| 4–6 | Rút lời mời. Trước khi Hoàng trả lời, Hùng nhấn × ở dòng "HOANG · Đang chờ trả lời" và xác nhận. Hệ thống xóa lời mời. Hoàng nhấn Đồng ý sau đó thì máy 2 báo "Lời mời không còn" và dòng HUNG biến khỏi mục Lời mời kết bạn. |
| 6 | Từ chối. Hoàng nhấn Từ chối ở dòng HUNG. Hệ thống xóa lời mời. Lần sau Hùng mở giao diện bạn bè, dòng "HOANG · Đang chờ trả lời" không còn. |

---

## 2. Thiết kế cơ sở dữ liệu

Ba chức năng dùng 2 nơi lưu trữ:
- MySQL trên server: tài khoản, trận đấu, đề bài, kết quả chấm và quan hệ bạn bè. Đây là nguồn dữ liệu của tổng điểm, lịch sử đấu và bảng xếp hạng.
- Firebase Realtime Database: người chơi đang online, lời mời, trận đang đấu. Dữ liệu này chỉ có ý nghĩa trong lúc chơi.

### 2.1. CSDL MySQL

Bước 1: Mỗi lớp thực thể cần lưu đề xuất một bảng.
- Player → tblPlayer.
- Match → tblMatch.
- MatchPlayer → tblMatchPlayer.
- MathProblem → tblMathProblem: server phải giữ đề và đáp án để chấm bài và để xem lại trong lịch sử đấu.
- Friendship → tblFriendship.
- Challenge không có bảng: lời mời chỉ có ý nghĩa vài giây, sau đó không chức năng nào tra lại.

Bước 2: Thuộc tính kiểu cơ bản thành cột.
- Bỏ thuộc tính kiểu đối tượng: Match.questions, Match.players, MatchPlayer.player, Friendship.sender, Friendship.receiver.
- Kiểu dữ liệu: String → varchar(255); int → integer(10); Date → datetime(3), có mili giây để so thời gian làm bài; boolean → tinyint(1).
- answerOptions luôn có 4 phần tử nên tách thành 4 cột option1 … option4.
- Kết quả:
  - tblPlayer: id, username, passwordHash, displayName, role, createdAt.
  - tblMatch: id, startedAt, endedAt.
  - tblMathProblem: id, operand1, operand2, operator, correctAnswer, option1, option2, option3, option4.
  - tblMatchPlayer: id, correctCount, submittedAt, forfeited.
  - tblFriendship: id, status, createdAt.

Bước 3: Quan hệ giữa các bảng.
- 1 tblMatch – n tblMatchPlayer (đúng 2 dòng mỗi trận).
- 1 tblMatch – n tblMathProblem (đúng 10 dòng mỗi trận).
- 1 tblPlayer – n tblMatchPlayer.
- 1 tblPlayer – n tblFriendship ở vai trò người gửi, và 1 tblPlayer – n tblFriendship ở vai trò người nhận. Quan hệ bạn bè n-n giữa người chơi với người chơi đã được tách bằng tblFriendship.
- Quan hệ n-n tblPlayer – tblMatch đã được tách bằng tblMatchPlayer từ thiết kế lớp thực thể.

Bước 4: Khóa.
- Khóa chính: id (AUTO_INCREMENT) ở mọi bảng.
- Khóa ngoại đặt ở bảng phía n:
  - tblMatchPlayer.tblMatchId → tblMatch.id và tblMathProblem.tblMatchId → tblMatch.id, có ON DELETE CASCADE đúng với quan hệ hợp thành.
  - tblMatchPlayer.tblPlayerId → tblPlayer.id.
  - tblFriendship.tblPlayerSenderId và tblFriendship.tblPlayerReceiverId → tblPlayer.id. Hai khóa cùng trỏ tới tblPlayer nên tên khóa thêm vai trò. Có ON DELETE CASCADE: xóa người chơi thì quan hệ bạn bè của người đó cũng mất.
- Ràng buộc duy nhất: tblPlayer.username; tblMatchPlayer (tblMatchId, tblPlayerId); tblFriendship (tblPlayerSenderId, tblPlayerReceiverId). Chiều ngược lại của quan hệ bạn bè được FriendshipDAO kiểm tra trước khi chèn.
- Thứ tự câu trong đề là thứ tự id tăng dần, vì server chèn 10 câu theo đúng thứ tự trong một transaction.

Bước 5: Loại thuộc tính gây dư thừa.
- Thuộc tính dẫn xuất không có cột:
  - Kết quả và điểm trận: suy ra từ correctCount, submittedAt, forfeited của 2 người và startedAt của trận → Match.getResult(playerId), Match.getPoints(playerId).
  - Tổng điểm, trung bình điểm đối thủ, trung bình thời gian thắng: tính từ các trận đã xong.
  - Tỉ lệ thắng / hòa / thua trong lịch sử đấu: đếm Match.getResult(playerId) trên các trận của người chơi.
  - Trạng thái online của bạn bè: lấy từ Firebase, không lưu cùng quan hệ bạn bè.
  - tblMatch.status: trận đã xong khi và chỉ khi endedAt có giá trị.
- Không cần lưu lâu dài: trạng thái rỗi / bận của người chơi (chỉ ở Firebase); bài làm answers (chấm xong thì chỉ cần correctCount).
- Giữ lại có chủ đích: correctAnswer suy ra được từ phép tính, nhưng là căn cứ chấm và đối chiếu kết quả.
- Chỉ endedAt và submittedAt được rỗng (trận đang đấu, người chưa nộp). Mật khẩu chỉ lưu dạng băm BCrypt.

@csdl-mysql.png

Hình 4: Biểu đồ thiết kế CSDL MySQL cho chức năng Chơi online, Xem lịch sử đấu, Quản lý bạn bè.

- tblMatch là trung tâm: 10 dòng tblMathProblem là đề, 2 dòng tblMatchPlayer nối trận với 2 người chơi.
- Lịch sử đấu và bảng xếp hạng chỉ đọc tblPlayer, tblMatch, tblMatchPlayer; riêng chi tiết trận đọc thêm 10 dòng tblMathProblem.
- tblFriendship nối 2 dòng tblPlayer, một dòng là người gửi, một dòng là người nhận.

### 2.2. Dữ liệu trên Firebase Realtime Database

Firebase là cây JSON, ánh xạ từ lớp thực thể theo cùng tinh thần: mỗi lớp là một node, thuộc tính cơ bản là field, quan hệ hợp thành là node con.

```
presence/{playerId}            displayName, totalPoints, status ("free" | "busy")
challenges/{challengeId}       challengerId, challengerName, challengerPoints,
                               opponentId, opponentName,
                               status ("pending" | "accepted" | "rejected"), createdAt, matchId
matches/{matchId}              status ("playing" | "finished"), startedAt,
    questions/{i}              operand1, operand2, operator, answerOptions
    players/{playerId}         displayName, submitted, forfeited, choice
    nextMatchId, stoppedBy
```

- presence: app ghi khi vào giao diện chính, đăng ký onDisconnect().removeValue() để tự xóa khi mất kết nối. Server đổi status và cập nhật totalPoints.
- challenges: app người mời tạo, app người được mời ghi rejected, server ghi accepted kèm matchId.
- Quản lý bạn bè chỉ đọc presence để biết bạn nào đang online, không thêm node mới.
- matches: chỉ server ghi. Đề trên Firebase không có correctAnswer, để người chơi không đọc được đáp án trước khi nộp bài.

---

## 3. Thiết kế lớp thực thể

Đầu vào là biểu đồ lớp thực thể pha phân tích (Phụ lục A). Các lớp liên quan đến 3 chức năng: Player, Challenge, Match, MatchPlayer, MathProblem, Friendship.

Bước 1: Bổ sung thuộc tính khóa.
- Thêm id : int cho Player, Match, MatchPlayer, MathProblem, Friendship.
- Challenge có id : String là push id do Firebase sinh, vì lời mời chỉ nằm trên Firebase.

Bước 2: Bổ sung kiểu dữ liệu.
- Kiểu Java: chuỗi String; số đếm, toán hạng int; thời gian làm bài long (mili giây); thời điểm Date; cờ boolean; điểm double vì có điểm hòa 0,5.
- Thuộc tính boolean không có tiền tố is (forfeited → getter isForfeited()), để Jackson và Firebase SDK đọc đúng tên trường.
- password đổi thành passwordHash vì chỉ lưu mật khẩu đã băm.

Bước 3: Chuyển quan hệ liên kết thành kết tập / hợp thành.
- Quan hệ n-n Player – Match qua lớp liên kết MatchPlayer chuyển thành:
  - MatchPlayer là thành phần của Match (hợp thành, 1-2): người chơi trong trận không tồn tại nếu không có trận.
  - Player nằm trong MatchPlayer (kết tập, n-1): người chơi tồn tại độc lập với trận.
- Giữ nguyên từ pha phân tích:
  - MathProblem là thành phần của Match (hợp thành, 1-10).
  - Player nằm trong Challenge ở 2 vai trò người mời, người được mời (kết tập).
  - Player nằm trong Friendship ở 2 vai trò người gửi, người nhận (kết tập).

Bước 4: Bổ sung thuộc tính đối tượng.
- Challenge.challenger, Challenge.opponent : Player.
- Match.questions : List\<MathProblem>, Match.players : List\<MatchPlayer>.
- MatchPlayer.player : Player.
- Friendship.sender, Friendship.receiver : Player.

Thuộc tính dẫn xuất được viết thành phương thức nghiệp vụ (hàm Java thuần, có unit test):

| Lớp | Phương thức | Ý nghĩa |
|---|---|---|
| MathProblem | isCorrect(answer : int) : boolean | answer bằng correctAnswer |
| Match | countCorrect(answers : List\<Integer>) : int | Số câu đúng của một bài làm |
| Match | isOver() : boolean | Có người bỏ trận, hoặc cả 2 đã nộp bài |
| Match | getFinishTime(playerId : int) : long | submittedAt − startedAt |
| Match | getResult(playerId : int) : String | WIN / LOSE / DRAW: bỏ trận thì thua; một người đúng hết thì người đó thắng; cả 2 có câu sai thì hòa; cả 2 đúng hết thì ai nhanh hơn thắng |
| Match | getPoints(playerId : int) : double | 1 / 0,5 / 0 |
| Match | getOpponent(playerId : int) : MatchPlayer | Người chơi còn lại trong trận, dùng cho lịch sử đấu |
| Friendship | getFriend(playerId : int) : Player | Người còn lại trong quan hệ bạn bè |
| Friendship | canAccept(playerId : int) : boolean | Lời mời đang chờ và playerId là người nhận |

Player giữ thêm các giá trị dẫn xuất mà giao diện cần hiển thị cùng tên: totalPoints, avgOpponentPoints, avgWinTime do server tính; status lấy từ Firebase. Các giá trị này không có cột trong CSDL.

@lop-thuc-the-hung.png

Hình 5: Biểu đồ lớp thực thể pha thiết kế cho Chơi online, Xem lịch sử đấu, Quản lý bạn bè.

- Không còn đường liên kết trơn nào trên biểu đồ.
- Player, Challenge, Match, MatchPlayer, MathProblem, Friendship có ở cả app và server với cùng tên và cùng thuộc tính, để JSON đi qua REST ánh xạ thẳng vào đối tượng.
- passwordHash chỉ có giá trị ở server; correctAnswer chỉ được gửi về app khi trận đã xong.

---

## 4. Thiết kế chi tiết từng chức năng

Ánh xạ từ pha phân tích sang pha thiết kế:

Lớp View sang Activity

| Lớp phân tích | Lớp thiết kế | Layout |
|---|---|---|
| MainView | MainActivity + PlayerAdapter | activity_main.xml, item_player.xml |
| MatchView | MatchActivity + QuestionAdapter | activity_match.xml, item_question.xml |
| MatchResultView | Overlay layoutResult nằm trong MatchActivity | activity_match.xml |
| HistoryView | HistoryActivity + PieChartView + MatchHistoryAdapter + RankingAdapter | activity_history.xml, item_match_history.xml, item_ranking.xml |
| MatchDetailView | MatchDetailActivity + ProblemAdapter | activity_match_detail.xml, item_problem.xml |
| FriendView | FriendActivity + FriendRequestAdapter + FriendAdapter | activity_friend.xml, item_friend_request.xml, item_friend.xml |

MatchResultView là overlay chứ không phải Activity riêng:
- Kết quả hiện đè lên màn thi đấu ngay khi trận xong.
- Câu hỏi "Chơi tiếp?" cần tiếp tục theo dõi trận đang mở.

HistoryView là một Activity có 2 thẻ (TabLayout):
- Thẻ Trận của tôi gồm PieChartView và danh sách rvMatches; thẻ Bảng xếp hạng là danh sách rvRanking. Chọn thẻ chỉ ẩn / hiện 2 khối này, không mở Activity mới.
- PieChartView là View tự vẽ biểu đồ tròn bằng Canvas, không dùng thư viện ngoài.

Phương thức của thực thể sang Activity hoặc DAO

| Phương thức pha phân tích | Lớp phân tích | Phương thức pha thiết kế | Lớp thiết kế |
|---|---|---|---|
| goOnline() | Player | goOnline(player : Player) : void | PresenceFirebaseDAO |
| getOnlinePlayers() | Player | watchOnlinePlayers(callback : PlayerListCallback) : void<br>onPlayersChanged(players : List\<Player>) : void | PresenceFirebaseDAO<br>MainActivity |
| sendChallenge() | Challenge | sendChallenge(challenger : Player, opponent : Player) : Challenge | ChallengeFirebaseDAO |
| watchChallenge() | Challenge | watchIncoming(playerId : int, callback : ChallengeCallback) : void<br>watchChallenge(challengeId : String, callback : ChallengeCallback) : void<br>onChallengeChanged(challenge : Challenge) : void | ChallengeFirebaseDAO<br>ChallengeFirebaseDAO<br>MainActivity |
| rejectChallenge() | Challenge | rejectChallenge(challengeId : String) : void | ChallengeFirebaseDAO |
| createMatch() | Match | createMatch(challenge : Challenge) : Match → create(challenge : Challenge) : Match<br>readPresence(playerId : int) : Player, updateStatus(playerId : int, status : String) : void, updateChallenge(challengeId : String, status : String, matchId : int) : void, writeMatch(match : Match) : void<br>insert(match : Match) : boolean | MatchApiDAO → MatchRestController<br>FirebaseAdminDAO<br><br>MatchDAO |
| generateProblem() | MathProblem | generateProblems(count : int) : List\<MathProblem><br>generateProblem() : MathProblem | ProblemDAO (server) |
| watchMatch() | Match | watchMatch(matchId : int, callback : MatchCallback) : void<br>onSuccess(match : Match) : void, onNextMatch(nextMatchId : int) : void, onStopped(playerId : int) : void | MatchFirebaseDAO<br>MatchActivity |
| submitAnswers() | Match | submitAnswers() : void<br>submit(matchId : int, playerId : int, answers : List\<Integer>) : Match → submit(id : int, matchPlayer : MatchPlayer) : Match<br>findById(id : int) : Match, updateSubmission(matchId : int, matchPlayer : MatchPlayer) : boolean, updateEndedAt(matchId : int, endedAt : Date) : boolean<br>writeSubmitted(matchId : int, playerId : int) : void, writeFinished(matchId : int) : void, updatePoints(playerId : int, totalPoints : double) : void | MatchActivity<br>MatchApiDAO → MatchRestController<br>MatchDAO<br><br>FirebaseAdminDAO |
| isCorrect() | MathProblem | countCorrect(answers : List\<Integer>) : int<br>isCorrect(answer : int) : boolean | Match<br>MathProblem |
| isOver(), getResult(), getFinishTime(), getPoints() | Match | Giữ nguyên, thêm tham số playerId : int cho 3 hàm sau | Match |
| forfeit() | Match | leaveMatch() : void<br>forfeit(matchId : int, playerId : int) : Match → forfeit(id : int, playerId : int) : Match<br>updateForfeit(matchId : int, playerId : int) : boolean, writeStopped(matchId : int, playerId : int) : void | MatchActivity<br>MatchApiDAO → MatchRestController<br>MatchDAO, FirebaseAdminDAO |
| continueMatch() | Match | sendChoice(choice : boolean) : void<br>continueMatch(matchId : int, playerId : int, choice : boolean) : Match → continueMatch(id : int, playerId : int, choice : boolean) : Match<br>writeChoice(matchId : int, playerId : int, choice : boolean) : void, writeNextMatch(matchId : int, nextMatchId : int) : void, writeStopped(matchId : int, playerId : int) : void | MatchActivity<br>MatchApiDAO → MatchRestController<br>FirebaseAdminDAO |
| getHistory() | Match | getHistory(playerId : int) : List\<Match> → getHistory(playerId : int) : List\<Match><br>findByPlayer(playerId : int) : List\<Match> | MatchApiDAO → MatchRestController<br>MatchDAO |
| getMatch() | Match | getById(matchId : int) : Match → getById(id : int) : Match<br>findById(id : int) : Match, getTotalPoints(playerId : int) : double | MatchApiDAO → MatchRestController<br>MatchDAO, PlayerDAO |
| getOpponent() | Match | getOpponent(playerId : int) : MatchPlayer | Match |
| getResult(), getPoints(), getFinishTime() | Match | Giữ nguyên như ở Chơi online | Match |
| getRanking() | Player | getRanking() : List\<Player> → getRanking() : List\<Player><br>getRanking() : List\<Player>, rank(players : List\<Player>, matches : List\<Match>) : List\<Player>, findFinished() : List\<Match> | PlayerApiDAO → PlayerRestController<br>PlayerDAO, MatchDAO |
| Đếm thắng / hòa / thua của HistoryView | HistoryView | showSummary(matches : List\<Match>) : void<br>setData(wins : int, draws : int, losses : int) : void | HistoryActivity<br>PieChartView |
| getFriendships() | Friendship | loadFriendships() : void, showFriendships(list : List\<Friendship>) : void<br>getFriendships(playerId : int) : List\<Friendship> → getFriendships(playerId : int) : List\<Friendship><br>findByPlayer(playerId : int) : List\<Friendship>, getTotalPoints(playerId : int) : double | FriendActivity<br>FriendshipApiDAO → FriendshipRestController<br>FriendshipDAO, PlayerDAO |
| getOnlinePlayers() | Player | watchOnlinePlayers(callback : PlayerListCallback) : void<br>onPlayersChanged(players : List\<Player>) : void, setOnlineIds(onlineIds : Set\<Integer>) : void | PresenceFirebaseDAO<br>FriendActivity, FriendAdapter |
| sendRequest() | Friendship | onClick(v : View) : void<br>sendRequest(senderId : int, receiverUsername : String) : Friendship → sendRequest(body : Map\<String, String>) : Friendship<br>findById(id : int) : Player, findByUsername(username : String) : Player<br>existsBetween(playerId1 : int, playerId2 : int) : boolean, insert(friendship : Friendship) : boolean | FriendActivity<br>FriendshipApiDAO → FriendshipRestController<br>PlayerDAO<br>FriendshipDAO |
| accept() | Friendship | onAccept(friendship : Friendship) : void<br>accept(friendshipId : int, playerId : int) : Friendship → accept(id : int, playerId : int) : Friendship<br>findById(id : int) : Friendship, updateStatus(id : int, status : String) : boolean | FriendActivity<br>FriendshipApiDAO → FriendshipRestController<br>FriendshipDAO |
| delete() | Friendship | onReject(friendship : Friendship) : void, onUnfriend(friendship : Friendship) : void<br>delete(friendshipId : int, playerId : int) : void → delete(id : int, playerId : int) : void<br>delete(path : String) : void<br>findById(id : int) : Friendship, delete(id : int) : boolean | FriendActivity<br>FriendshipApiDAO → FriendshipRestController<br>ApiDAO<br>FriendshipDAO |
| canAccept(), getFriend() | Friendship | Giữ nguyên, thêm tham số playerId : int | Friendship |

Lớp DAO cha

Mỗi nơi lưu trữ có một lớp DAO cha giữ kết nối dùng chung; các DAO con kế thừa.

| Nơi lưu trữ | DAO cha | Giữ | DAO con dùng ở đây |
|---|---|---|---|
| App → Firebase | FirebaseDAO | con : DatabaseReference | PresenceFirebaseDAO, ChallengeFirebaseDAO, MatchFirebaseDAO |
| App → REST | ApiDAO | BASE_URL; gửi / nhận JSON | PlayerApiDAO, MatchApiDAO, FriendshipApiDAO |
| Server → MySQL | DAO | conn : Connection | PlayerDAO, MatchDAO, FriendshipDAO |

ApiDAO có 3 hàm gửi yêu cầu: get, post và delete; delete dùng để từ chối lời mời và hủy kết bạn.

Các lớp không có lớp cha:
- SessionDAO chỉ đọc SharedPreferences.
- FirebaseAdminDAO gọi REST API của Firebase Realtime Database, không dùng JDBC nên không kế thừa DAO của server.
- ProblemDAO ở server chỉ sinh câu hỏi bằng Random, không kết nối nơi lưu trữ nào.

### 4.1. Chức năng "Chơi online"

#### 4.1.1. Thiết kế giao diện

@giao-dien-uc03.png

Hình 6: Thiết kế giao diện chức năng Chơi online.

Chức năng có 2 màn, 1 hộp thoại lời mời và 1 overlay, đi theo thứ tự MainActivity → hộp thoại lời mời trên máy người được mời → MatchActivity → overlay layoutResult.
- Màn chính chia 2 phần:
  - Trên cùng là thẻ thông tin của mình: tên, tổng điểm.
  - Bên dưới là danh sách người chơi online. Mỗi dòng có tên, tổng điểm và nhãn trạng thái: Rỗi nền xanh, Bận nền xám. Dòng Bận vẫn bấm được nhưng chỉ hiện thông báo.
- Màn thi đấu chia 3 tầng từ trên xuống:
  1. Hàng trên: ô thời gian đếm ngược bên trái, dòng trạng thái đối thủ bên phải.
  2. Danh sách 10 câu cuộn được. Mỗi câu là một thẻ gồm số thứ tự, phép tính chữ lớn, 4 phương án dạng RadioButton xếp thành một hàng ngang.
  3. Hàng dưới: nút Thoát nhỏ bên trái, nút Nộp bài lớn bên phải.
- Overlay kết quả che màn thi đấu. Bảng so sánh 2 cột (Bạn, đối thủ) giúp người chơi thấy vì sao thắng: đúng hết hay nhanh hơn.

Bảng thành phần:

| Màn | id trong layout | Loại View | Chức năng | Tên ở pha phân tích |
|---|---|---|---|---|
| MainActivity | txtMyName | TextView | Tên hiển thị của mình | outMyInfo |
| | txtMyPoints | TextView | "12,5 điểm" | outMyInfo |
| | rvPlayers | RecyclerView | Danh sách người chơi online, bấm được | outsubListPlayer |
| item_player.xml | txtName | TextView | Tên người chơi | một dòng của outsubListPlayer |
| | txtPoints | TextView | Tổng điểm | một dòng của outsubListPlayer |
| | txtStatus | TextView | Rỗi (xanh) / Bận (xám) | một dòng của outsubListPlayer |
| MatchActivity | txtTimer | TextView | Ô thời gian 01:00 → 00:00; nộp xong thì hiện "Đã nộp bài, đang chờ đối thủ..." | outTimer |
| | txtOpponentStatus | TextView | "HOANG đang làm bài" / "HOANG đã nộp bài" | outOpponentStatus |
| | rvQuestions | RecyclerView | Danh sách 10 câu hỏi | inoutListQuestion |
| | btnSubmit | Button | Nộp bài | subSubmit |
| | btnExit | Button | Thoát trận | subExit |
| item_question.xml | txtExpression | TextView | "1. 12 + 7 = ?" | một câu của inoutListQuestion |
| | rgOptions | RadioGroup | Nhóm 4 phương án, chỉ chọn được 1 | một câu của inoutListQuestion |
| | rbOptions | RadioButton[4] | 4 phương án | một câu của inoutListQuestion |
| Overlay layoutResult | txtResult | TextView | THẮNG! (xanh) / THUA! (đỏ) / HÒA! (vàng) | outResult |
| | txtMyResult | TextView | "Bạn: 10/10 câu đúng · 38,2 giây" | outMyResult |
| | txtOpponentResult | TextView | "HOANG: 10/10 câu đúng · 45,6 giây" | outOpponentResult |
| | txtPoints | TextView | "+1 điểm · Tổng điểm: 13,5" | outPoints |
| | btnContinue | Button | Chơi tiếp | subContinue |
| | btnStop | Button | Dừng; khi có người bỏ trận thì đổi thành Về menu | subStop |

Các thông báo ngắn ("MINH đang bận", "HOANG đã từ chối lời mời", "Đối thủ đã dừng chơi") dùng Toast. Hộp thoại lời mời, hộp thoại chờ trả lời và hộp thoại "Thoát sẽ bị xử thua?" dùng AlertDialog. Cả hai đều không phải thành phần của layout.

#### 4.1.2. Biểu đồ lớp chi tiết

@lop-thiet-ke-uc03.png

Hình 7: Biểu đồ lớp chi tiết chức năng Chơi online.

Package "Client (Android)".

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | MainActivity | Màn chính, cài đặt PlayerListCallback và ChallengeCallback. Giữ người chơi đang đăng nhập (me), lời mời mình đã gửi và đang chờ trả lời (sentChallenge), id các lời mời tới mình đã hiện hộp thoại (shownChallenges), hộp thoại chờ (waitingDialog).<br>- onCreate lấy me bằng SessionDAO.getPlayer, gọi goOnline, rồi watchOnlinePlayers và watchIncoming, truyền chính nó (this) làm callback.<br>- goOnline gọi PlayerApiDAO.getById trong Thread để có tổng điểm mới, xong thì gọi PresenceFirebaseDAO.goOnline.<br>- onPlayersChanged hiện thông tin của mình và đưa những người còn lại vào PlayerAdapter.<br>- onPlayerClick: đối thủ bận thì hiện Toast; đã có sentChallenge thì bỏ qua; ngược lại sendChallenge, watchChallenge rồi showWaitingDialog hiện hộp thoại chờ có nút Hủy.<br>- onChallengeChanged: lời mời tới mình chưa có trong shownChallenges thì showChallengeDialog; lời mình gửi được đồng ý thì closeSentChallenge rồi openMatch(challenge.getMatchId()); bị từ chối thì closeSentChallenge và hiện Toast.<br>- closeSentChallenge gọi ChallengeFirebaseDAO.stopWatchingSent, xóa sentChallenge và đóng hộp thoại chờ.<br>- acceptChallenge gọi MatchApiDAO.createMatch trong Thread, xong thì openMatch. Nút Từ chối gọi ChallengeFirebaseDAO.rejectChallenge.<br>- logout gọi PresenceFirebaseDAO.goOffline, SessionDAO.clear rồi về màn đăng nhập. onDestroy gọi stopWatching của PresenceFirebaseDAO và ChallengeFirebaseDAO |
| Boundary | PlayerAdapter, PlayerViewHolder | PlayerAdapter kế thừa RecyclerView.Adapter, giữ danh sách Player. setPlayers thay danh sách và vẽ lại. onClick của một dòng gọi MainActivity.onPlayerClick. PlayerViewHolder giữ 3 TextView của một dòng |
| Boundary | MatchActivity | Màn thi đấu, cài đặt MatchCallback và PlayerListCallback. Giữ me, mã trận (matchId, nhận qua Intent với khóa EXTRA_MATCH_ID), id đối thủ (opponentId), đồng hồ (timer, MATCH_MILLIS = 60 giây). 6 cờ started, submitted, resultLoading, resultShown, opponentLeft, leaving chặn gọi lặp, vì onSuccess được gọi lại mỗi lần node trận thay đổi.<br>- onCreate gọi MatchFirebaseDAO.watchMatch, truyền chính nó làm MatchCallback.<br>- Lần đầu nhận trận có đề, onSuccess gọi startMatch: lưu opponentId bằng opponentOf, đưa đề vào QuestionAdapter, createTimer rồi start, gọi PresenceFirebaseDAO.watchOnlinePlayers với chính nó làm PlayerListCallback.<br>- onTick vẽ thời gian; onFinish gọi submitAnswers.<br>- submitAnswers khóa danh sách câu bằng QuestionAdapter.setLocked, lấy bài làm bằng getAnswers, gọi MatchApiDAO.submit trong Thread. Thành công thì onSubmitted hủy đồng hồ; lỗi thì onSubmitFailed mở khóa để nộp lại.<br>- Mỗi lần onSuccess gọi showOpponentStatus; trận đã xong thì loadResult gọi MatchApiDAO.getById rồi showResult.<br>- showResult dùng Match.getResult cho showResultTitle; showScores dùng getFinishTime, getPoints trên bản trận server trả về. mineOf, opponentOf tìm MatchPlayer của mình và của đối thủ.<br>- Nút Chơi tiếp / Dừng gọi sendChoice (MatchApiDAO.continueMatch). onNextMatch gọi openNextMatch: stopWatching trận cũ rồi mở trận mới. onStopped hiện Toast và về màn chính.<br>- Nút Thoát và onBackPressed gọi confirmLeave hỏi xác nhận rồi gọi leaveMatch (MatchApiDAO.forfeit).<br>- onPlayersChanged: khi trận đang đấu mà đối thủ biến khỏi danh sách online (mất kết nối), gọi forfeit(opponentId) thay cho đối thủ.<br>- onDestroy hủy đồng hồ, gọi stopWatching của MatchFirebaseDAO và PresenceFirebaseDAO.<br>Activity không tự chấm bài, không tự phân thắng thua |
| Boundary | QuestionAdapter, QuestionViewHolder | QuestionAdapter giữ danh sách MathProblem và mảng phương án đã chọn (answers). onBindViewHolder hiện getExpression và 4 phương án; onCheckedChanged lưu phương án vào answers. getAnswers trả bài làm, câu chưa chọn là −1. setLocked khóa mọi RadioButton sau khi nộp. QuestionViewHolder giữ txtExpression, rgOptions, rbOptions |
| Control | SessionDAO | Giữ prefs : SharedPreferences "session". getPlayer() đọc playerId, username, displayName và đóng gói thành Player. clear() xóa phiên khi đăng xuất |
| Control | FirebaseDAO ← PresenceFirebaseDAO | FirebaseDAO giữ con (gốc Realtime Database). PresenceFirebaseDAO đọc / ghi node presence:<br>- goOnline ghi tên, tổng điểm, trạng thái free và đăng ký onDisconnect().removeValue(). goOffline xóa node của mình.<br>- watchOnlinePlayers / stopWatching thêm / gỡ ValueEventListener, giữ listener đang đăng ký trong watcher để gỡ đúng listener.<br>- toPlayer đóng gói một node thành Player. presenceRef trả node presence |
| Control | FirebaseDAO ← ChallengeFirebaseDAO | Đọc / ghi node challenges:<br>- sendChallenge tạo node bằng push().<br>- watchIncoming lọc lời mời có opponentId là mình và status pending; giữ incomingQuery, incomingWatcher.<br>- watchChallenge theo dõi một lời mời đã gửi; giữ sentRef, sentWatcher.<br>- stopWatchingSent chỉ gỡ theo dõi lời mời đã gửi; stopWatching gỡ cả 2.<br>- rejectChallenge ghi status rejected.<br>- toChallenge đóng gói thành Challenge, dùng toPlayer cho người mời và người được mời. challengesRef trả node challenges |
| Control | FirebaseDAO ← MatchFirebaseDAO | Chỉ đọc node matches/{matchId}; giữ matchRef và watcher để stopWatching gỡ đúng listener. toMatch đóng gói thành Match kèm 10 MathProblem (toProblem, không có đáp án) và 2 MatchPlayer (toMatchPlayer). Khi node có nextMatchId thì gọi onNextMatch; có stoppedBy thì gọi onStopped; còn lại gọi onSuccess. Mất kết nối thì gọi onFail với câu NETWORK_ERROR |
| — | PlayerListCallback, ChallengeCallback, MatchCallback | Interface trả kết quả bất đồng bộ từ các FirebaseDAO về Activity. FirebaseDAO chỉ phụ thuộc vào interface, không biết Activity cụ thể; Activity truyền chính nó (this) khi gọi hàm watch... Nhờ vậy PresenceFirebaseDAO dùng chung cho MainActivity, MatchActivity và FriendActivity |
| Control | ApiDAO ← PlayerApiDAO | getById gửi GET /api/players/{id}; toPlayer đóng gói JSON thành Player. Hàm chạy đồng bộ nên phải gọi trong Thread |
| Control | ApiDAO ← MatchApiDAO | ApiDAO giữ BASE_URL, câu báo lỗi mạng NETWORK_ERROR và các hàm gửi / nhận JSON bằng HttpURLConnection. 3 hàm riêng: open mở kết nối, readGet gửi GET, read đọc phản hồi và ném IOException kèm câu thông báo của server khi mã ≥ 400. MatchApiDAO có 5 hàm, mỗi hàm một API:<br>- createMatch: POST /api/matches.<br>- submit: POST /api/matches/{id}/submit.<br>- forfeit: POST /api/matches/{id}/forfeit.<br>- continueMatch: POST /api/matches/{id}/continue.<br>- getById: GET /api/matches/{id}.<br>toMatch đóng gói JSON trả về thành Match, dùng toProblem cho từng câu hỏi và toMatchPlayer cho từng người chơi trong trận |
| Entity | Player, Challenge, Match, MatchPlayer, MathProblem | Như thiết kế lớp thực thể. Các phương thức nghiệp vụ là hàm Java thuần, có unit test:<br>- MathProblem: isCorrect, getExpression.<br>- Match: countCorrect, isOver, getFinishTime, getResult, getPoints |

Package "Server (Spring Boot)".

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | PlayerRestController | getById(id) nhận GET /api/players/{id}: PlayerDAO.findById rồi gán tổng điểm bằng PlayerDAO.getTotalPoints |
| Boundary | MatchRestController | Trọng tài của mọi trận. Giữ waitingChoices: các trận đang có một người chọn chơi tiếp.<br>- create(challenge) nhận POST /api/matches:<br>  1. readChallengeStatus: lời mời đã bị hủy thì trả 409. readPresence của 2 người; một người không online hoặc đang bận thì trả 409 "Không bắt đầu được trận đấu".<br>  2. startMatch: ProblemDAO.generateProblems(10), tạo Match, MatchDAO.insert, FirebaseAdminDAO.writeMatch, updateStatus busy cho 2 người.<br>  3. updateChallenge accepted kèm id trận, rồi hideAnswers xóa đáp án trước khi trả về.<br>- submit(id, matchPlayer):<br>  1. Ghi thời điểm nhận, findById, chấm bằng Match.countCorrect, updateSubmission, writeSubmitted.<br>  2. Nếu Match.isOver: endMatch (updateEndedAt, tính lại tổng điểm 2 người, updatePoints), rồi writeFinished.<br>- forfeit(id, playerId): updateForfeit, endMatch, rồi mới writeStopped, writeFinished để máy đối thủ đọc được kết quả đã lưu; trả 2 người về free.<br>- continueMatch(id, playerId, choice), synchronized:<br>  - Dừng: writeStopped, 2 người về free.<br>  - Chơi tiếp mà đối thủ chưa chọn: lưu vào waitingChoices, writeChoice.<br>  - Đối thủ đã chọn chơi tiếp: startMatch rồi writeNextMatch.<br>- getById(id): findById, gán tổng điểm cho 2 người chơi.<br>Các hàm riêng: findMatch gọi MatchDAO.findById, không có thì trả 404; findPlayer tìm MatchPlayer theo id, không có thì trả 403; setStatus gọi updateStatus cho 2 người; isFree kiểm tra người chơi online và rỗi; hideAnswers đặt correctAnswer = −1 khi trận chưa kết thúc |
| Control | DAO ← PlayerDAO | DAO giữ conn : Connection (JDBC), mở một lần bằng cấu hình loadConfig.<br>- findById đọc tblPlayer qua findOne; toPlayer đóng gói một dòng, COLUMNS là danh sách cột dùng chung.<br>- getTotalPoints lấy các trận đã xong bằng MatchDAO.findFinished, lọc trận có người chơi bằng hasPlayer rồi cộng Match.getPoints |
| Control | DAO ← MatchDAO | - insert ghi 1 dòng tblMatch, 10 dòng tblMathProblem, 2 dòng tblMatchPlayer trong một transaction bằng insertMatch, insertProblem, insertMatchPlayer, gán id vừa sinh.<br>- findById, findFinished dùng chung câu SELECT_PLAYERS qua query; toMatch, toMatchPlayer đóng gói dòng kết quả, findProblems và toProblem đọc đề.<br>- findById đọc trận kèm đề và 2 người chơi.<br>- updateSubmission ghi correctCount, submittedAt.<br>- updateForfeit ghi forfeited.<br>- updateEndedAt ghi endedAt |
| Control | ProblemDAO | Giữ random : Random và 3 hằng OPERATORS, OPTION_COUNT = 4, MAX_OFFSET = 10. generateProblems gọi generateProblem 10 lần. Mỗi câu chọn toán tử +, −, ×, sinh 2 toán hạng bằng nextInt, tính correctAnswer; createOptions sinh 3 phương án nhiễu lệch không quá MAX_OFFSET rồi xáo trộn |
| Control | FirebaseAdminDAO | Giữ dbUrl, khóa truy cập secret, client : HttpClient và mapper : ObjectMapper; gọi REST API của Firebase:<br>- readPresence, readChallengeStatus gửi GET.<br>- Các hàm write..., update... gửi PUT hoặc PATCH tới đúng node trong tài liệu CSDL mục 2. updateStatus, updatePoints đi qua updatePresence.<br>- toQuestions, toPlayers chuyển đề (bỏ đáp án) và 2 người chơi thành dữ liệu node.<br>- send gửi yêu cầu và trả nội dung phản hồi |
| Entity | Player, Challenge, Match, MatchPlayer, MathProblem | Cùng tên, cùng thuộc tính với phía app, để Jackson chuyển JSON thẳng vào đối tượng. Player ở server có thêm passwordHash, không bao giờ trả ra JSON. correctAnswer chỉ trả ra JSON khi trận đã xong |

Quan hệ giữa các lớp giao diện:
- MainActivity – MatchActivity nối nhau bằng quan hệ liên kết. MainActivity – PlayerAdapter – PlayerViewHolder và MatchActivity – QuestionAdapter – QuestionViewHolder cũng vậy.
- Mỗi lớp nối với layout của nó: activity_main.xml, item_player.xml, activity_match.xml, item_question.xml. Overlay kết quả nằm trong activity_match.xml.
- Kết tập:
  - MainActivity và MatchActivity chứa thông tin người chơi trong thuộc tính me, nên Player kết tập vào 2 Activity này.
  - PlayerAdapter chứa danh sách Player.
  - QuestionAdapter chứa danh sách MathProblem.

Quan hệ liên kết HTTP/JSON giữa 2 package có 2 cặp:
- MatchApiDAO – MatchRestController: trận đấu.
- PlayerApiDAO – PlayerRestController: tổng điểm.

Firebase không phải một lớp trên biểu đồ: các FirebaseDAO ở app và FirebaseAdminDAO ở server cùng đọc / ghi một cây dữ liệu, như mô tả trong tài liệu CSDL mục 2.

#### 4.1.3. Biểu đồ tuần tự chi tiết

Đoạn A: Gửi lời thách đấu

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

@tuan-tu-thiet-ke-uc03a.png

Hình 8: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn A: Gửi lời thách đấu.

- Biểu đồ có 42 thông điệp.
- Lời gọi REST duy nhất là bước 10, để lấy tổng điểm mới nhất từ server. Server đi theo khuôn PlayerRestController → PlayerDAO → Player.
- Firebase làm 3 việc:
  - Ghi người chơi vào danh sách online (bước 21).
  - Báo khi danh sách thay đổi (bước 22–25).
  - Chuyển lời mời giữa 2 máy (bước 30–39).
- Bất đồng bộ: hàm goOnline() mở Thread ở bước 7 rồi trả về ngay, nên onCreate() đăng ký theo dõi ở bước 8–9 trong lúc yêu cầu REST ở bước 10–20 còn đang chạy. watchOnlinePlayers(), watchIncoming(), watchChallenge() là lời đăng ký; kết quả đến sau qua onDataChange(). Bước 36 xảy ra khi đối thủ đồng ý ở đoạn B.
- Callback: các FirebaseDAO không biết MainActivity, chỉ giữ interface PlayerListCallback, ChallengeCallback được truyền vào lúc đăng ký (bước 8, 9, 34). Bước 25 và 39 gọi qua interface đó; ở chức năng này đối tượng cài đặt là MainActivity.
- Bước 40–41 gỡ theo dõi lời mời đã gửi trước khi mở trận, để lần ghi sau của server lên lời mời không gọi lại onChallengeChanged().

Đoạn B: Nhận lời thách đấu

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

@tuan-tu-thiet-ke-uc03b.png

Hình 9: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn B: Nhận lời thách đấu.

- Biểu đồ có 35 thông điệp.
- Server sinh đề (bước 19–22) và lưu đề vào MySQL cùng trận (bước 23–25), vì server cần đáp án để chấm bài sau này.
- Đề không kèm đáp án: đề gửi qua Firebase không có đáp án (bước 26), và bản trận trả về cho app cũng đã bị hideAnswers() xóa đáp án (bước 30). Người chơi không đọc được đáp án trước khi nộp bài.
- Bước 29 cập nhật lời mời, đây chính là tín hiệu để máy người mời mở MatchActivity ở bước 36 của đoạn A.
- Hàm create() đọc trạng thái online của 2 người (bước 11–14) và trạng thái lời mời (bước 15) trước khi tạo trận, nên không thể có người ở trong 2 trận cùng lúc. Lời mời đã hủy hoặc một người không rỗi thì server ghi lời mời là bị từ chối.

Đoạn C: Thi đấu

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

@tuan-tu-thiet-ke-uc03c.png

Hình 10: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn C: Thi đấu.

- Biểu đồ có 84 thông điệp.
- Bước 2 lược bỏ thông điệp đóng gói Player bên trong SessionDAO, vì đã vẽ ở đoạn A.
- Server làm trọng tài, gồm 3 phần nghiệp vụ là hàm Java thuần của lớp thực thể:
  - Chấm bài: hàm countCorrect() của Match gọi hàm isCorrect() của MathProblem (bước 32–35).
  - Điều kiện kết thúc: hàm isOver() của Match (bước 39, 49).
  - Thắng thua: hàm getResult() và getPoints() của Match. App gọi 2 hàm này trên bản trận server trả về (bước 77–83).
- Thời gian làm bài tính theo đồng hồ server: thời điểm server nhận bài làm trừ thời điểm bắt đầu trận. Độ trễ khác nhau giữa 2 máy không làm sai kết quả do đồng hồ máy lệch nhau.
- Bước 49–57 diễn ra khi server xử lý yêu cầu nộp bài từ máy đối thủ. Máy này biết trận đã xong nhờ Firebase (bước 58–61).
- MatchActivity cài đặt 2 callback: MatchCallback nhận diễn biến trận (bước 3, 7, 61), PlayerListCallback nhận danh sách người chơi online (bước 14, 16). Ở kịch bản chuẩn đối thủ vẫn online nên bước 16 không làm gì thêm; khi đối thủ biến khỏi danh sách giữa trận, hàm onPlayersChanged() gọi forfeit() thay cho đối thủ.
- Các cờ started, submitted, resultLoading của MatchActivity chặn gọi lặp: onSuccess() được gọi lại mỗi lần node trận thay đổi, nhưng startMatch() chỉ chạy ở lần đầu (bước 8), loadResult() chỉ chạy một lần (bước 62).

Đoạn D: Chơi tiếp

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

@tuan-tu-thiet-ke-uc03d.png

Hình 11: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn D: Chơi tiếp.

- Biểu đồ có 30 thông điệp, vẽ cho người chọn chơi tiếp trước.
- Hàm continueMatch() chạy tuần tự (synchronized). Nếu 2 yêu cầu chơi tiếp đến cùng lúc, chỉ yêu cầu đến sau tạo trận mới, nên không sinh 2 trận.
- Trận mới dùng lại hàm startMatch() của đoạn B (bước 14–25), không viết lại logic sinh đề.
- Máy này nhận trận mới qua MatchCallback (bước 27), gỡ theo dõi trận cũ (bước 29), mở MatchActivity mới và đi lại đoạn C.

### 4.2. Chức năng "Xem lịch sử đấu"

#### 4.2.1. Thiết kế giao diện

@giao-dien-uc04.png

Hình 12: Thiết kế giao diện chức năng Xem lịch sử đấu.

Màn Lịch sử đấu có 2 thẻ ở dưới thanh tiêu đề.
- Thẻ Trận của tôi mở sẵn. Trên cùng là thẻ tóm tắt: biểu đồ tròn dạng vành khuyên bên trái, giữa ghi tỉ lệ thắng to; chú giải 3 màu và tổng số trận bên phải. Người chơi thấy ngay phong độ của mình trước khi đọc từng trận.
- Danh sách trận nằm dưới thẻ tóm tắt, trận mới nhất ở trên. Mỗi dòng là một item_match_history.xml có dải màu bên trái theo kết quả: xanh lá thắng, vàng hòa, đỏ thua. Dòng trên ghi đối thủ, số trận, kết quả, điểm trận; dòng dưới ghi số câu đúng 2 bên và thời điểm kết thúc. Trận có người bỏ trận ghi thêm "Đối thủ bỏ trận" hoặc "Bạn bỏ trận".
- Thẻ Bảng xếp hạng giữ nguyên bố cục bảng xếp hạng: hàng tiêu đề cột cố định, mỗi dòng là một item_ranking.xml, dòng của người xem có nền màu nhấn.

@giao-dien-uc04b.png

Hình 13: Thiết kế giao diện chi tiết trận.

Màn Chi tiết trận đi từ tổng quát tới chi tiết: kết quả và điểm trận to ở trên, bảng so sánh 2 bên ở giữa, đề bài kèm đáp án đúng ở dưới. Cột của người xem in đậm.

Bảng thành phần:

| Màn | id trong layout | Loại View | Chức năng | Tên ở pha phân tích |
|---|---|---|---|---|
| MainActivity | txtMyName, txtMyPoints | TextView | "HUNG · 13,5 điểm" | outMyInfo |
| | btnHistory | Button | Mở màn Lịch sử đấu | subHistory |
| HistoryActivity | tabLayout | TabLayout | 2 thẻ Trận của tôi, Bảng xếp hạng | subTabRanking |
| | layoutMyMatches | LinearLayout | Khối thẻ Trận của tôi, ẩn khi chọn thẻ Bảng xếp hạng | |
| | pieChart | PieChartView | Biểu đồ tròn tỉ lệ thắng, hòa, thua | outWinRateChart |
| | txtWins, txtDraws, txtLosses | TextView | Chú giải số trận thắng, hòa, thua | outSummary |
| | txtTotal | TextView | Tổng số trận | outSummary |
| | rvMatches | RecyclerView | Danh sách trận | outsubListMatch |
| | txtEmpty | TextView | "Bạn chưa đấu trận nào", ẩn khi có trận | outsubListMatch |
| | rvRanking | RecyclerView | Danh sách đã xếp hạng | outListRanking |
| item_match_history.xml | viewResultBar | View | Dải màu theo kết quả | một dòng của outsubListMatch |
| | txtOpponent | TextView | "vs HOANG · #37" | một dòng của outsubListMatch |
| | txtResult | TextView | THẮNG / HÒA / THUA, kèm "Đối thủ bỏ trận" nếu có | một dòng của outsubListMatch |
| | txtPoints | TextView | "+1" | một dòng của outsubListMatch |
| | txtScore | TextView | "10 – 10 câu" | một dòng của outsubListMatch |
| | txtEndedAt | TextView | "25/10 20:15" | một dòng của outsubListMatch |
| item_ranking.xml | txtRank, txtName, txtPoints, txtAvgOpponent, txtAvgWinTime | TextView | Hạng, tên, tổng điểm, trung bình điểm đối thủ, trung bình thời gian thắng ("—" nếu chưa thắng) | một dòng của outListRanking |
| MatchDetailActivity | txtTitle | TextView | "Trận #37 · 25/10/2026 20:15" | |
| | txtResult | TextView | THẮNG! / THUA! / HÒA! | outResult |
| | txtPoints | TextView | "+1 điểm" | outPoints |
| | txtMyName, txtMyStats | TextView | Tên, "10/10 · 38,2 giây" của mình | outMyResult |
| | txtOpponentName, txtOpponentStats | TextView | Tên, "10/10 · 45,6 giây" hoặc "Bỏ trận" của đối thủ | outOpponentResult |
| | rvProblems | RecyclerView | 10 câu hỏi kèm đáp án đúng | outListProblem |
| item_problem.xml | txtIndex | TextView | Số thứ tự câu | một dòng của outListProblem |
| | txtExpression | TextView | "12 + 7 = 19" | một dòng của outListProblem |

#### 4.2.2. Biểu đồ lớp chi tiết

@lop-thiet-ke-uc04.png

Hình 14: Biểu đồ lớp chi tiết chức năng Xem lịch sử đấu.

Package "Client (Android)".

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | MainActivity | onClick của nút Lịch sử đấu mở HistoryActivity |
| Boundary | HistoryActivity | onCreate lấy người chơi bằng SessionDAO.getPlayer rồi gọi loadHistory. loadHistory gọi MatchApiDAO.getHistory trong Thread, rồi trong runOnUiThread gọi showSummary và gắn MatchHistoryAdapter. showSummary đếm Match.getResult theo id người chơi thành số trận thắng, hòa, thua, gọi PieChartView.setData và ghi chú giải. onTabSelected ẩn / hiện 2 khối layoutMyMatches, layoutRanking; chưa tải bảng xếp hạng (rankingLoaded sai) thì gọi loadRanking. loadRanking đặt rankingLoaded, lỗi thì đặt lại để lần chọn sau tải lại. onMatchClick mở MatchDetailActivity kèm matchId với khóa EXTRA_MATCH_ID. Lỗi mạng thì showError hiện Toast |
| Boundary | PieChartView | View tự vẽ, kế thừa View của Android. Giữ 3 số wins, draws, losses, 3 Paint (arcPaint, percentPaint, labelPaint) và khung oval tạo sẵn để onDraw không cấp phát lại. setData lưu 3 số rồi gọi invalidate; onDraw gọi drawPart vẽ từng cung tròn bằng Canvas.drawArc theo tỉ lệ, khoét giữa thành vành khuyên và ghi tỉ lệ thắng. Cả 3 số bằng 0 thì vẽ vòng xám |
| Boundary | MatchHistoryAdapter, MatchHistoryViewHolder | MatchHistoryAdapter giữ danh sách Match, id người xem, HistoryActivity chứa nó và timeFormat định dạng thời điểm kết thúc. onBindViewHolder gọi Match.getOpponent, getResult, getPoints để điền một dòng và tô màu dải kết quả; bấm vào dòng thì gọi onMatchClick của HistoryActivity, giống cách PlayerAdapter gọi về MainActivity. MatchHistoryViewHolder giữ các View của một dòng |
| Boundary | RankingAdapter, RankingViewHolder | RankingAdapter hiện hạng bằng vị trí + 1, 3 tiêu chí của Player, tô nền dòng của người xem |
| Boundary | MatchDetailActivity | onCreate lấy người chơi bằng SessionDAO.getPlayer, đọc matchId từ Intent rồi gọi loadMatch. loadMatch gọi MatchApiDAO.getById trong Thread, rồi trong runOnUiThread gọi showMatch. showMatch dùng Match.getOpponent, getResult, getPoints để điền kết quả; stats dùng getFinishTime ghi số câu đúng và thời gian của từng bên; rồi gắn ProblemAdapter. matchId giữ mã trận đọc từ Intent |
| Boundary | ProblemAdapter, ProblemViewHolder | ProblemAdapter giữ 10 MathProblem; onBindViewHolder ghi số thứ tự và MathProblem.getExpression thay dấu "?" bằng correctAnswer |
| Control | SessionDAO | Như ở Chơi online |
| Control | ApiDAO ← MatchApiDAO | getHistory gửi GET /api/matches?playerId={id}; getById gửi GET /api/matches/{id}. Cả 2 đóng gói JSON thành Match bằng toMatch |
| Control | ApiDAO ← PlayerApiDAO | getRanking gửi GET /api/players/ranking, đóng gói từng phần tử thành Player bằng toPlayer |
| Entity | Match, MatchPlayer, MathProblem, Player | Match có thêm getOpponent trả MatchPlayer còn lại; các luật getResult, getPoints, getFinishTime dùng chung với Chơi online |

Package "Server (Spring Boot)".

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | MatchRestController | getHistory(playerId) nhận GET /api/matches?playerId={id}, gọi MatchDAO.findByPlayer và trả danh sách Match dạng JSON. getById(id) nhận GET /api/matches/{id}, gọi MatchDAO.findById, gán tổng điểm 2 người bằng PlayerDAO.getTotalPoints; trận đã xong nên trả kèm đáp án đúng |
| Boundary | PlayerRestController | getRanking() nhận GET /api/players/ranking, gọi PlayerDAO.getRanking |
| Control | DAO ← MatchDAO | findByPlayer truy vấn tblMatch có endedAt khác rỗng và có dòng tblMatchPlayer của người chơi, JOIN tblMatchPlayer, tblPlayer, sắp theo endedAt giảm dần; đóng gói mỗi trận thành 1 Match + 2 MatchPlayer + 2 Player, không đọc đề. findById đọc thêm 10 dòng tblMathProblem. findFinished dùng cho bảng xếp hạng |
| Control | DAO ← PlayerDAO | getRanking đọc mọi người chơi bằng findAllPlayers, lấy các trận đã xong bằng MatchDAO.findFinished rồi gọi rank. PlayerDAO tạo MatchDAO ngay trong hàm, không giữ làm thuộc tính. rank là hàm static không truy cập CSDL: addMatch cộng điểm, ghi đối thủ và thời gian thắng của từng trận; averagePoints, averageSeconds tính 2 tiêu chí trung bình; cuối cùng sắp xếp 3 tiêu chí. getTotalPoints cộng Match.getPoints qua các trận đã xong |
| Entity | Player, Match, MatchPlayer, MathProblem | Như ở Chơi online |

Quan hệ giữa các lớp giao diện:
- MainActivity – HistoryActivity – MatchDetailActivity nối nhau bằng quan hệ liên kết theo luồng màn hình.
- HistoryActivity nối liên kết với PieChartView, MatchHistoryAdapter, RankingAdapter; mỗi Adapter nối với ViewHolder của nó. MatchDetailActivity – ProblemAdapter – ProblemViewHolder cũng vậy.
- MatchHistoryAdapter giữ HistoryActivity để gọi lại onMatchClick khi một dòng được bấm.
- Layout: MainActivity – activity_main.xml, HistoryActivity – activity_history.xml, MatchHistoryViewHolder – item_match_history.xml, RankingViewHolder – item_ranking.xml, MatchDetailActivity – activity_match_detail.xml, ProblemViewHolder – item_problem.xml.
- Kết tập: HistoryActivity giữ me và danh sách trận; MatchHistoryAdapter giữ danh sách Match; RankingAdapter giữ danh sách Player; MatchDetailActivity giữ me và một Match; ProblemAdapter giữ 10 MathProblem. Hình thoi ở phía Activity hoặc Adapter.

Chức năng dùng lại MatchApiDAO, PlayerApiDAO, MatchRestController, PlayerRestController, MatchDAO, PlayerDAO của Chơi online; biểu đồ chỉ vẽ các phương thức chức năng này dùng. Phần mới gồm MatchApiDAO.getHistory, MatchRestController.getHistory, MatchDAO.findByPlayer và Match.getOpponent.

#### 4.2.3. Biểu đồ tuần tự chi tiết

Đoạn A: Xem trận của tôi và chi tiết trận

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

@tuan-tu-thiet-ke-uc04a.png

Hình 15: Biểu đồ tuần tự chi tiết chức năng Xem lịch sử đấu, đoạn A: Xem trận của tôi và chi tiết trận.

- Biểu đồ có 64 thông điệp.
- Bước 3–4 và 32–33 lược bỏ thông điệp đóng gói Player bên trong SessionDAO, vì đã vẽ ở chức năng Chơi online.
- Có 2 lời gọi REST: bước 7 lấy danh sách trận, bước 36 lấy một trận đầy đủ. Server đi theo khuôn MatchRestController → MatchDAO → Match.
- Danh sách trận không kèm đề (bước 9) để mỗi lần mở lịch sử chỉ đọc 2 bảng tblMatch, tblMatchPlayer. Đề và đáp án chỉ được đọc khi người chơi mở một trận (bước 39).
- Biểu đồ tròn và từng dòng trận đều dựa trên getResult(), getPoints(), getOpponent() của Match (bước 17, 22–27). Đây là cùng luật dùng khi kết thúc trận, nên lịch sử luôn khớp với kết quả đã báo cho 2 người.
- Lời gọi mạng chạy trong Thread (bước 6, 35). Phần vẽ giao diện chạy trong hàm runOnUiThread() (bước 16, 48).

Đoạn B: Xem bảng xếp hạng

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

@tuan-tu-thiet-ke-uc04b.png

Hình 16: Biểu đồ tuần tự chi tiết chức năng Xem lịch sử đấu, đoạn B: Xem bảng xếp hạng.

- Biểu đồ có 31 thông điệp.
- Lời gọi REST duy nhất là bước 4. Server đi theo khuôn PlayerRestController → PlayerDAO → MatchDAO → Match, Player.
- Các tiêu chí không lưu trong CSDL mà được tính lại ở bước 13–21 bằng getPoints(), getResult(), getFinishTime() của Match. Bảng xếp hạng vì vậy luôn khớp với kết quả các trận.
- rank() là hàm static, không truy cập CSDL: nhận danh sách người chơi và danh sách trận đã đọc ở bước 6–12 rồi chỉ tính toán, nên kiểm thử được bằng unit test.
- Bảng xếp hạng là một thẻ của HistoryActivity nên không mở Activity mới; chỉ lần chọn thẻ đầu tiên mới gọi server, các lần sau dùng lại danh sách đã tải.

### 4.3. Chức năng "Quản lý bạn bè"

#### 4.3.1. Thiết kế giao diện

@giao-dien-uc07.png

Hình 17: Thiết kế giao diện chức năng Quản lý bạn bè.

Chức năng có 1 màn và 1 hộp thoại xác nhận, đi theo thứ tự MainActivity → FriendActivity → hộp thoại "Hủy kết bạn với <tên>?" khi nhấn ×.
- Màn chính có thêm nút Bạn bè nằm giữa nút Lịch sử đấu và nút Đăng xuất.
- Màn Bạn bè chia 3 phần từ trên xuống:
  1. Hàng nhập: ô tên đăng nhập chiếm phần lớn chiều ngang, nút Kết bạn bên phải. Gửi xong thì ô được xóa trống để gửi tiếp.
  2. Mục "Lời mời kết bạn (n)": chỉ hiện khi có lời mời đã nhận, đặt trên cùng để người chơi thấy ngay. Mỗi dòng có tên người gửi, nút Đồng ý nổi bật, nút Từ chối viền mảnh để tránh bấm nhầm.
  3. Mục "Bạn bè (n)": danh sách cuộn. Mỗi dòng có chấm xanh (Online) hoặc xám (Offline) bên trái, tên chữ đậm, dòng dưới là tổng điểm và chữ Online / Offline, nút × bên phải. Lời mời mình đã gửi nằm cuối, chữ nghiêng màu xám "Đang chờ trả lời". Chưa có ai thì hiện "Chưa có bạn bè".
- Nút × là nút biểu tượng nên có mô tả "Hủy kết bạn" cho trình đọc màn hình. Hủy kết bạn luôn hỏi xác nhận vì không hoàn tác được.

Bảng thành phần:

| Màn | id trong layout | Loại View | Chức năng | Tên ở pha phân tích |
|---|---|---|---|---|
| MainActivity | btnFriends | Button | Mở màn Bạn bè | subFriends |
| FriendActivity | edtUsername | EditText | Ô nhập tên đăng nhập người muốn kết bạn | inUsername |
| | btnAddFriend | Button | Kết bạn | subAddFriend |
| | txtRequestsTitle | TextView | "Lời mời kết bạn (1)"; ẩn khi không có lời mời | outsubListRequest |
| | rvRequests | RecyclerView | Danh sách lời mời đã nhận | outsubListRequest |
| | txtFriendsTitle | TextView | "Bạn bè (3)" | outsubListFriend |
| | txtEmpty | TextView | "Chưa có bạn bè"; chỉ hiện khi danh sách trống | outsubListFriend |
| | rvFriends | RecyclerView | Danh sách bạn bè và lời mời đã gửi | outsubListFriend |
| item_friend_request.xml | txtName | TextView | Tên người gửi | một dòng của outsubListRequest |
| | btnAccept | Button | Đồng ý | một dòng của outsubListRequest |
| | btnReject | Button | Từ chối | một dòng của outsubListRequest |
| item_friend.xml | viewOnlineDot | View | Chấm tròn xanh / xám | một dòng của outsubListFriend |
| | txtName | TextView | Tên người bạn | một dòng của outsubListFriend |
| | txtPoints | TextView | "15 điểm"; lời mời đã gửi thì để trống | một dòng của outsubListFriend |
| | txtStatus | TextView | Online / Offline / Đang chờ trả lời | một dòng của outsubListFriend |
| | btnUnfriend | ImageButton | Hủy kết bạn hoặc rút lời mời, contentDescription "Hủy kết bạn" | một dòng của outsubListFriend |

Các thông báo ngắn ("Đã gửi lời mời kết bạn tới HOANG", "Không tìm thấy người chơi", "Lời mời không còn"…) dùng Toast. Hộp thoại "Hủy kết bạn với TUAN?" dùng AlertDialog. Cả hai đều không phải thành phần của layout.

#### 4.3.2. Biểu đồ lớp chi tiết

@lop-thiet-ke-uc07.png

Hình 18: Biểu đồ lớp chi tiết chức năng Quản lý bạn bè.

Package "Client (Android)".

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | MainActivity | onClick của nút Bạn bè mở FriendActivity |
| Boundary | FriendActivity | Màn Bạn bè, cài đặt View.OnClickListener và PlayerListCallback; giữ người chơi đang đăng nhập (me), các lời mời đã nhận (requests), bạn bè và lời mời đã gửi (friends), id những người đang online (onlineIds).<br>- onCreate lấy me bằng SessionDAO.getPlayer, gọi PresenceFirebaseDAO.watchOnlinePlayers rồi loadFriendships.<br>- loadFriendships gọi FriendshipApiDAO.getFriendships trong Thread, rồi trong runOnUiThread gọi showFriendships.<br>- showFriendships dùng Friendship.canAccept để tách lời mời mình cần trả lời vào requests, phần còn lại vào friends, rồi đưa vào 2 Adapter.<br>- onPlayersChanged lấy id những người đang online vào onlineIds và gọi FriendAdapter.setOnlineIds.<br>- onClick của nút Kết bạn kiểm tra ô không trống, gọi FriendshipApiDAO.sendRequest trong Thread, thêm lời mời vào cuối friends và hiện Toast.<br>- onAccept gọi FriendshipApiDAO.accept, chuyển quan hệ từ requests sang friends. onReject và onUnfriend (sau khi hỏi xác nhận) cùng gọi deleteFriendship, hàm này gọi FriendshipApiDAO.delete rồi bỏ quan hệ khỏi 2 danh sách.<br>- refreshTitles cập nhật số lời mời, số bạn và dòng "Chưa có bạn bè"; enableAddButton bật lại nút Kết bạn khi gửi lỗi.<br>- onDestroy gọi PresenceFirebaseDAO.stopWatching.<br>Lỗi server (không tìm thấy, đã có quan hệ, lời mời không còn) hiện bằng Toast với câu thông báo server trả về |
| Boundary | FriendRequestAdapter, FriendRequestViewHolder | FriendRequestAdapter kế thừa RecyclerView.Adapter, giữ danh sách Friendship đã nhận. setFriendships thay danh sách và vẽ lại. onBindViewHolder hiện tên người gửi (sender); nút Đồng ý gọi FriendActivity.onAccept, nút Từ chối gọi FriendActivity.onReject. FriendRequestViewHolder giữ txtName, btnAccept, btnReject |
| Boundary | FriendAdapter, FriendViewHolder | FriendAdapter giữ danh sách Friendship, id của người xem (playerId) và onlineIds. onBindViewHolder lấy người bạn bằng Friendship.getFriend(playerId):<br>- Quan hệ accepted: hiện tổng điểm, chấm xanh và Online nếu id người bạn có trong onlineIds, ngược lại chấm xám và Offline.<br>- Lời mời pending mình đã gửi: hiện "Đang chờ trả lời".<br>Nút × gọi FriendActivity.onUnfriend. setOnlineIds chỉ vẽ lại trạng thái, không gọi server. FriendViewHolder giữ viewOnlineDot, txtName, txtPoints, txtStatus, btnUnfriend |
| — | PlayerListCallback | Interface trả danh sách người chơi online từ PresenceFirebaseDAO về FriendActivity; dùng chung với Chơi online |
| Control | SessionDAO | Như ở Chơi online |
| Control | FirebaseDAO ← PresenceFirebaseDAO | Như ở Chơi online. Chức năng này chỉ đọc node presence qua watchOnlinePlayers, stopWatching; không ghi gì lên Firebase |
| Control | ApiDAO ← FriendshipApiDAO | ApiDAO có thêm delete(path) gửi yêu cầu DELETE. FriendshipApiDAO có 4 hàm, mỗi hàm một API:<br>- getFriendships: GET /api/friendships?playerId={id}.<br>- sendRequest: POST /api/friendships, thân gồm senderId và receiverUsername.<br>- accept: POST /api/friendships/{id}/accept?playerId={id}.<br>- delete: DELETE /api/friendships/{id}?playerId={id}.<br>toFriendship đóng gói JSON thành Friendship, dùng toPlayer cho người gửi và người nhận |
| Entity | Friendship, Player | Như thiết kế lớp thực thể. Friendship có 2 phương thức nghiệp vụ là hàm Java thuần:<br>- getFriend(playerId): người còn lại trong quan hệ.<br>- canAccept(playerId): lời mời đang chờ và playerId là người nhận |

Package "Server (Spring Boot)".

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | FriendshipRestController | - getFriendships(playerId) nhận GET /api/friendships: FriendshipDAO.findByPlayer, rồi gán tổng điểm cho người gửi và người nhận bằng PlayerDAO.getTotalPoints.<br>- sendRequest(body) nhận POST /api/friendships:<br>  1. PlayerDAO.findById lấy người gửi; PlayerDAO.findByUsername tìm người nhận, không có thì trả 404 "Không tìm thấy người chơi".<br>  2. Người nhận là người gửi thì trả 400 "Không thể kết bạn với chính mình".<br>  3. FriendshipDAO.existsBetween đúng thì trả 409 "Đã có lời mời hoặc đã là bạn bè".<br>  4. Tạo Friendship pending, FriendshipDAO.insert, trả 201 kèm lời mời.<br>- accept(id, playerId): FriendshipDAO.findById, không có thì trả 404 "Lời mời không còn"; Friendship.canAccept sai thì trả 403; đúng thì updateStatus accepted, gán tổng điểm và trả quan hệ.<br>- delete(id, playerId): FriendshipDAO.findById; playerId không phải người gửi hay người nhận thì trả 403; ngược lại FriendshipDAO.delete, trả 204.<br>Các hàm riêng: findFriendship gọi FriendshipDAO.findById, không có thì trả 404 "Lời mời không còn"; parseId đọc senderId, sai định dạng thì trả 400 |
| Control | DAO ← FriendshipDAO | DAO giữ conn : Connection (JDBC). FriendshipDAO giữ câu SELECT dùng chung cho findByPlayer, findById.<br>- findByPlayer truy vấn tblFriendship có tblPlayerSenderId hoặc tblPlayerReceiverId bằng playerId, JOIN tblPlayer 2 lần để có người gửi và người nhận.<br>- findById đọc một quan hệ theo id, cùng cách JOIN.<br>- existsBetween kiểm tra có dòng nào giữa 2 người theo cả 2 chiều.<br>- insert ghi 1 dòng tblFriendship, gán id vừa sinh.<br>- updateStatus ghi status, chỉ khi dòng đang pending.<br>- delete xóa 1 dòng theo id.<br>- toFriendship đóng gói một dòng kết quả thành Friendship kèm 2 Player |
| Control | DAO ← PlayerDAO | findById, findByUsername đọc tblPlayer; getTotalPoints như ở Chơi online. Cả 3 hàm thuộc phần chung, chức năng này chỉ gọi lại |
| Entity | Friendship, Player | Cùng tên, cùng thuộc tính với phía app, để Jackson chuyển JSON thẳng vào đối tượng. passwordHash của Player không bao giờ trả ra JSON |

Quan hệ giữa các lớp giao diện:
- MainActivity – FriendActivity, FriendActivity – FriendRequestAdapter – FriendRequestViewHolder, FriendActivity – FriendAdapter – FriendViewHolder nối nhau bằng quan hệ liên kết.
- Layout: MainActivity – activity_main.xml, FriendActivity – activity_friend.xml, FriendRequestViewHolder – item_friend_request.xml, FriendViewHolder – item_friend.xml.
- FriendActivity thực thi interface PlayerListCallback.
- Kết tập:
  - FriendActivity chứa người chơi trong thuộc tính me, nên Player kết tập vào FriendActivity.
  - FriendActivity, FriendRequestAdapter, FriendAdapter chứa danh sách Friendship, bội số n ở phía Friendship.
  - Friendship chứa 2 Player ở vai trò sender và receiver, ở cả 2 package.

Quan hệ liên kết HTTP/JSON giữa 2 package có 1 cặp: FriendshipApiDAO – FriendshipRestController.

Trạng thái online của bạn bè đi qua Firebase, không đi qua server: PresenceFirebaseDAO đọc cùng node presence mà Chơi online đã ghi.

#### 4.3.3. Biểu đồ tuần tự chi tiết

Đoạn A: Gửi lời mời kết bạn

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

@tuan-tu-thiet-ke-uc07a.png

Hình 19: Biểu đồ tuần tự chi tiết chức năng Quản lý bạn bè, đoạn A: Gửi lời mời kết bạn.

- Biểu đồ có 60 thông điệp.
- Lời gọi REST có 2 lần: bước 10 lấy lời mời và bạn bè, bước 38 gửi lời mời. Server đi theo khuôn FriendshipRestController → FriendshipDAO, PlayerDAO → Friendship, Player.
- Firebase chỉ dùng để biết bạn nào đang online (bước 7, 30–34). Bước 30 xảy ra mỗi khi có người vào hoặc thoát ứng dụng, nên trạng thái Online / Offline tự cập nhật mà không phải gọi server. PresenceFirebaseDAO gọi về qua interface PlayerListCallback (bước 33), dùng chung với MainActivity, MatchActivity của Chơi online.
- Luật nghiệp vụ nằm ở thực thể Friendship: canAccept() tách lời mời đã nhận (bước 22), getFriend() lấy người còn lại trong quan hệ (bước 27). Kiểm tra trùng quan hệ theo cả 2 chiều nằm ở existsBetween() của FriendshipDAO (bước 47).
- Lời gọi mạng chạy trong Thread (bước 9, 37). Phần vẽ giao diện chạy trong hàm runOnUiThread() (bước 21, 59).

Đoạn B: Đồng ý lời mời kết bạn

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

@tuan-tu-thiet-ke-uc07b.png

Hình 20: Biểu đồ tuần tự chi tiết chức năng Quản lý bạn bè, đoạn B: Đồng ý lời mời kết bạn.

- Biểu đồ có 24 thông điệp.
- Lời gọi REST duy nhất là bước 4. Server không tin vào máy gửi yêu cầu: nó đọc lại lời mời từ CSDL qua findFriendship() (bước 5–9) rồi mới kiểm tra bằng canAccept() (bước 10).
- updateStatus() chỉ cập nhật dòng đang pending, nên 2 lần nhấn Đồng ý liên tiếp không làm hỏng dữ liệu.
- Trạng thái Online / Offline của người bạn mới lấy từ onlineIds mà FriendActivity đã có từ đoạn A, không cần gọi Firebase lại.

---

## Phụ lục: các bước phân tích

### A. Lớp thực thể pha phân tích

@../chung/lop-thuc-the-phan-tich.png

Hình 21: Biểu đồ lớp thực thể pha phân tích toàn hệ thống.

### B. Biểu đồ lớp pha phân tích

@lop-phan-tich-uc03.png

Hình 22: Biểu đồ lớp pha phân tích chức năng Chơi online.

- 3 lớp View nối nhau bằng quan hệ liên kết theo luồng màn hình: MainView – MatchView – MatchResultView. MatchResultView quay về MatchView khi chơi tiếp, về MainView khi dừng.
- Mỗi View nối với lớp thực thể nó gọi phương thức bằng quan hệ liên kết:
  - MainView: Player (danh sách online), Challenge (lời mời), Match (tạo trận).
  - MatchView: Match (theo dõi trận, nộp bài, thoát trận).
  - MatchResultView: Match (kết quả, điểm, chơi tiếp).
- Quan hệ giữa các thực thể giữ đúng như biểu đồ lớp thực thể pha phân tích ở Phụ lục A:
  - Challenge chứa thông tin 2 Player (kết tập).
  - Player – Match là n-n, qua lớp liên kết MatchPlayer.
  - MathProblem là thành phần của Match (hợp thành).
- Match nhận nhiều phương thức nhất, vì hầu hết xử lý của chức năng trả về một trận đấu. Các luật của trận đều nằm ở thực thể, không nằm ở giao diện.

@lop-phan-tich-uc04.png

Hình 23: Biểu đồ lớp pha phân tích chức năng Xem lịch sử đấu.

- 3 lớp View nối nhau bằng quan hệ liên kết theo luồng màn hình: MainView – HistoryView – MatchDetailView.
- Mỗi View nối với lớp thực thể nó gọi phương thức bằng quan hệ liên kết:
  - HistoryView: Match (lịch sử, kết quả, điểm, đối thủ), Player (bảng xếp hạng).
  - MatchDetailView: Match (trận đầy đủ, kết quả, điểm, đối thủ, thời gian làm bài).
- Quan hệ giữa các thực thể giữ đúng như biểu đồ lớp thực thể pha phân tích ở Phụ lục A:
  - Player – Match là n-n, qua lớp liên kết MatchPlayer.
  - MathProblem là thành phần của Match (hợp thành); chi tiết trận cần đề để hiện đáp án đúng.
- Lịch sử đấu và bảng xếp hạng dùng cùng luật thắng thua với trận đấu: getResult(), getPoints(), getFinishTime() của Match. Không có luật nào được viết lại ở giao diện.

@lop-phan-tich-uc07.png

Hình 24: Biểu đồ lớp pha phân tích chức năng Quản lý bạn bè.

- Chức năng có 2 lớp View nối nhau bằng quan hệ liên kết: MainView mở FriendView.
- FriendView nối với 2 lớp thực thể nó gọi phương thức:
  - Friendship: lấy, gửi, đồng ý, xóa lời mời và quan hệ bạn bè.
  - Player: lấy danh sách người chơi online để hiện trạng thái của bạn bè.
- Quan hệ giữa các thực thể giữ đúng như biểu đồ lớp thực thể pha phân tích ở Phụ lục A: Friendship chứa thông tin 2 Player ở 2 vai trò người gửi và người nhận (kết tập).
- Friendship nhận mọi phương thức của chức năng, kể cả luật "chỉ người nhận mới đồng ý được" (canAccept()). Trạng thái online của bạn bè không lưu trong Friendship mà lấy từ danh sách online.

Đối chiếu với kịch bản v1:

| Thành phần trong kịch bản v1 | Thuộc tính |
|---|---|
| Nút Bạn bè | MainView.subFriends |
| Ô tên đăng nhập, nút Kết bạn | FriendView.inUsername, subAddFriend |
| Mục Lời mời kết bạn, nút Đồng ý, nút Từ chối | FriendView.outsubListRequest |
| Mục Bạn bè, trạng thái, tổng điểm, dòng "Đang chờ trả lời", nút × | FriendView.outsubListFriend |

### C. Kịch bản v2 và biểu đồ tuần tự pha phân tích

Chơi online, đoạn A: Gửi lời thách đấu

1. MainView gọi hàm goOnline() của lớp Player để ghi người chơi vào danh sách online với trạng thái rỗi.
2. MainView gọi hàm getOnlinePlayers() của lớp Player.
3. Lớp Player trả về danh sách người chơi online, kèm tổng điểm và trạng thái, cho MainView.
4. MainView hiển thị tên, tổng điểm của người chơi và danh sách người chơi online.
5. Người chơi bấm vào tên một đối thủ đang rỗi trên MainView.
6. MainView gọi hàm sendChallenge() của lớp Challenge.
7. Lớp Challenge tạo lời mời đang chờ và trả về cho MainView.
8. MainView hiển thị thông báo đang chờ đối thủ trả lời.
9. MainView gọi hàm watchChallenge() của lớp Challenge để theo dõi lời mời.
10. Khi đối thủ đồng ý, lớp Challenge trả về lời mời đã được đồng ý, kèm trận đấu, cho MainView.
11. MainView gọi MatchView.

Ngoại lệ 5a: Người chơi bấm vào một đối thủ đang bận.

- 5a.1. MainView hiển thị thông báo đối thủ đang bận, không gọi sendChallenge().

Ngoại lệ 9a: Người chơi hủy lời mời khi đang chờ.

- 9a.1. Người chơi nhấn Hủy trên MainView.
- 9a.2. MainView gọi hàm rejectChallenge() của lớp Challenge.
- 9a.3. Lớp Challenge đánh dấu lời mời đã hủy và trả về cho MainView.
- 9a.4. MainView đóng thông báo chờ và giữ nguyên giao diện chính.

Ngoại lệ 10a: Đối thủ từ chối lời mời.

- 10a.1. Lớp Challenge trả về lời mời đã bị từ chối cho MainView.
- 10a.2. MainView hiển thị thông báo đối thủ đã từ chối và giữ nguyên giao diện chính.

@tuan-tu-phan-tich-uc03a.png

Hình 25: Biểu đồ tuần tự pha phân tích chức năng Chơi online, đoạn A: Gửi lời thách đấu.

- Biểu đồ có 11 thông điệp, khớp với 11 bước của kịch bản chuẩn.
- Hàm watchChallenge() ở bước 9 là lời đăng ký theo dõi: kết quả không trả về ngay mà trả về ở bước 10, khi đối thủ trả lời.
- Bước 10 xảy ra khi đối thủ đồng ý ở đoạn B, nên giữa bước 9 và bước 10 có một khoảng chờ.

Chơi online, đoạn B: Nhận lời thách đấu

1. MainView gọi hàm watchChallenge() của lớp Challenge để nhận các lời mời gửi tới người chơi.
2. Khi có lời mời mới, lớp Challenge trả về lời mời cho MainView.
3. MainView hiển thị hộp thoại lời mời gồm tên và tổng điểm của người mời.
4. Người chơi nhấn Đồng ý.
5. MainView gọi hàm createMatch() của lớp Match.
6. Lớp Match gọi hàm generateProblem() của lớp MathProblem để sinh đề.
7. Lớp MathProblem trả về các câu hỏi của đề cho lớp Match.
8. Lớp Match tạo trận với đề vừa sinh, chuyển 2 người chơi sang trạng thái bận và trả về trận cho MainView.
9. MainView gọi MatchView.

Ngoại lệ 4a: Người chơi từ chối lời mời.

- 4a.1. Người chơi nhấn Từ chối.
- 4a.2. MainView gọi hàm rejectChallenge() của lớp Challenge.
- 4a.3. Lớp Challenge đánh dấu lời mời bị từ chối và trả về cho MainView.
- 4a.4. MainView đóng hộp thoại lời mời.

Ngoại lệ 8a: Không tạo được trận.

- 8a.1. Lớp Match trả về cho MainView: người mời không còn online hoặc đã bận.
- 8a.2. MainView hiển thị thông báo không bắt đầu được trận đấu.

@tuan-tu-phan-tich-uc03b.png

Hình 26: Biểu đồ tuần tự pha phân tích chức năng Chơi online, đoạn B: Nhận lời thách đấu.

- Biểu đồ có 9 thông điệp, khớp với kịch bản chuẩn.
- Việc sinh đề nằm ở lớp thực thể (bước 6, 7). Cả 2 người chơi nhận đúng trận do lớp Match trả về, nên cùng một đề.

Chơi online, đoạn C: Thi đấu

1. MatchView gọi hàm watchMatch() của lớp Match để theo dõi trận.
2. Lớp Match trả về trận đấu kèm đề bài cho MatchView.
3. MatchView hiển thị ô thời gian, trạng thái đối thủ, danh sách câu hỏi, nút Nộp bài và nút Thoát cho người chơi.
4. Người chơi chọn đáp án cho các câu hỏi và nhấn Nộp bài trên MatchView.
5. MatchView gọi hàm submitAnswers() của lớp Match.
6. Lớp Match gọi hàm isCorrect() của lớp MathProblem để chấm từng câu.
7. Lớp MathProblem trả kết quả chấm cho lớp Match.
8. Lớp Match lưu số câu đúng, thời điểm nộp bài và trả về trận cho MatchView.
9. MatchView hiển thị thông báo đã nộp bài, đang chờ đối thủ.
10. Khi đối thủ cũng nộp bài, lớp Match gọi hàm isOver() của chính nó để kiểm tra trận đã xong.
11. Lớp Match trả về trận đã kết thúc cho MatchView, theo lời đăng ký ở bước 1.
12. MatchView gọi MatchResultView.
13. MatchResultView gọi hàm getResult() của lớp Match.
14. Lớp Match trả về kết quả của người chơi cho MatchResultView.
15. MatchResultView gọi hàm getFinishTime() của lớp Match.
16. Lớp Match trả về thời gian làm bài của 2 người chơi cho MatchResultView.
17. MatchResultView gọi hàm getPoints() của lớp Match.
18. Lớp Match trả về điểm trận của người chơi cho MatchResultView.
19. MatchResultView hiển thị kết quả, số câu đúng, thời gian làm bài, điểm trận, tổng điểm và câu hỏi chơi tiếp cho người chơi.

Ngoại lệ 4a: Hết thời gian mà người chơi chưa nộp bài.

- 4a.1. MatchView tự gọi hàm submitAnswers() của lớp Match với các câu đã chọn; câu chưa chọn tính là sai. Kịch bản tiếp tục từ bước 6.

Ngoại lệ 4b: Người chơi thoát trận.

- 4b.1. Người chơi nhấn Thoát trên MatchView và xác nhận thoát.
- 4b.2. MatchView gọi hàm forfeit() của lớp Match.
- 4b.3. Lớp Match đánh dấu người chơi bỏ trận, kết thúc trận và trả về cho MatchView.
- 4b.4. MatchView gọi MainView.
- 4b.5. MainView hiển thị giao diện chính cho người chơi.

Ngoại lệ 11a: Đối thủ bỏ trận.

- 11a.1. Lớp Match trả về trận đã kết thúc do đối thủ bỏ trận cho MatchView.
- 11a.2. MatchView hiển thị thông báo đối thủ đã thoát. Kịch bản tiếp tục từ bước 12; MatchResultView không hiển thị lựa chọn chơi tiếp.

@tuan-tu-phan-tich-uc03c.png

Hình 27: Biểu đồ tuần tự pha phân tích chức năng Chơi online, đoạn C: Thi đấu.

- Biểu đồ có 19 thông điệp.
- Chấm bài (bước 6, 7) và kiểm tra trận đã xong (bước 10) đều do lớp thực thể làm, giao diện không tự chấm.
- Bước 11 trả về theo lời đăng ký watchMatch() ở bước 1, khi đối thủ nộp bài.
- Kết quả, thời gian, điểm trận đều là thuộc tính dẫn xuất, được tính bằng getResult(), getFinishTime(), getPoints() (bước 13–18).

Chơi online, đoạn D: Chơi tiếp

1. Người chơi nhấn Chơi tiếp trên MatchResultView.
2. MatchResultView gọi hàm continueMatch() của lớp Match.
3. Lớp Match ghi nhận lựa chọn chơi tiếp và trả về cho MatchResultView.
4. MatchResultView hiển thị thông báo đang chờ đối thủ.
5. MatchResultView gọi hàm watchMatch() của lớp Match để biết lựa chọn của đối thủ.
6. Khi đối thủ cũng chọn chơi tiếp, lớp Match gọi hàm generateProblem() của lớp MathProblem để sinh đề mới.
7. Lớp MathProblem trả về các câu hỏi của đề mới cho lớp Match.
8. Lớp Match tạo trận mới và trả về cho MatchResultView.
9. MatchResultView gọi MatchView.
10. MatchView hiển thị giao diện thi đấu với đề mới cho người chơi; trận tiếp theo diễn ra như đoạn C.

Ngoại lệ 1a: Người chơi dừng chơi.

- 1a.1. Người chơi nhấn Dừng trên MatchResultView.
- 1a.2. MatchResultView gọi hàm continueMatch() của lớp Match với lựa chọn dừng.
- 1a.3. Lớp Match ghi nhận, trả 2 người chơi về trạng thái rỗi và trả về cho MatchResultView.
- 1a.4. MatchResultView gọi MainView.
- 1a.5. MainView hiển thị giao diện chính cho người chơi.

Ngoại lệ 3a: Đối thủ đã chọn chơi tiếp trước.

- 3a.1. Lớp Match gọi hàm generateProblem() của lớp MathProblem để sinh đề mới, tạo trận mới và trả về ngay cho MatchResultView. Kịch bản tiếp tục từ bước 9.

Ngoại lệ 8a: Đối thủ dừng chơi.

- 8a.1. Lớp Match trả về trận đã dừng cho MatchResultView.
- 8a.2. MatchResultView hiển thị thông báo đối thủ đã dừng chơi.
- 8a.3. MatchResultView gọi MainView.
- 8a.4. MainView hiển thị giao diện chính cho người chơi.

@tuan-tu-phan-tich-uc03d.png

Hình 28: Biểu đồ tuần tự pha phân tích chức năng Chơi online, đoạn D: Chơi tiếp.

- Biểu đồ có 10 thông điệp, vẽ cho người chọn chơi tiếp trước.
- Trận mới chỉ được tạo khi cả 2 người đã chọn chơi tiếp (bước 6–8). Người chọn sau đi theo ngoại lệ 3a.

Xem lịch sử đấu, đoạn A: Xem trận của tôi và chi tiết trận

1. Người chơi nhấn nút Lịch sử đấu trên MainView.
2. MainView gọi HistoryView.
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
15. Lớp Match trả về trận kèm đề, đáp án đúng và 2 người chơi cho MatchDetailView.
16. MatchDetailView gọi hàm getResult() của lớp Match.
17. Lớp Match trả về kết quả trận cho MatchDetailView.
18. MatchDetailView gọi hàm getPoints() của lớp Match.
19. Lớp Match trả về điểm trận cho MatchDetailView.
20. MatchDetailView gọi hàm getOpponent() của lớp Match.
21. Lớp Match trả về đối thủ trong trận cho MatchDetailView.
22. MatchDetailView gọi hàm getFinishTime() của lớp Match cho người chơi và đối thủ.
23. Lớp Match trả về thời gian làm bài cho MatchDetailView.
24. MatchDetailView hiển thị kết quả, điểm trận, số câu đúng và thời gian làm bài của 2 bên, 10 câu hỏi kèm đáp án đúng cho người chơi.
25. Người chơi nhấn Back trên MatchDetailView.
26. MatchDetailView gọi HistoryView.
27. HistoryView hiển thị lại danh sách trận cho người chơi.

Ngoại lệ 4a: Người chơi chưa đấu trận nào.

- 4a.1. Lớp Match trả về danh sách rỗng cho HistoryView.
- 4a.2. HistoryView hiển thị biểu đồ trống và thông báo người chơi chưa đấu trận nào.

Ngoại lệ 4b: Không lấy được lịch sử đấu.

- 4b.1. Lớp Match trả về lỗi cho HistoryView.
- 4b.2. HistoryView hiển thị thông báo không tải được lịch sử đấu, biểu đồ và danh sách để trống.

Ngoại lệ 15a: Không lấy được chi tiết trận.

- 15a.1. Lớp Match trả về lỗi cho MatchDetailView.
- 15a.2. MatchDetailView hiển thị thông báo không tải được chi tiết trận và gọi HistoryView.

@tuan-tu-phan-tich-uc04a.png

Hình 29: Biểu đồ tuần tự pha phân tích chức năng Xem lịch sử đấu, đoạn A: Xem trận của tôi và chi tiết trận.

- Biểu đồ có 27 thông điệp, khớp với 27 bước của kịch bản chuẩn.
- Kết quả, điểm và đối thủ của từng dòng trong danh sách đều lấy từ Match (bước 5–10). Biểu đồ tròn là phép đếm trên các kết quả này, không lưu ở đâu.
- Chi tiết trận cần đề và đáp án đúng nên gọi getMatch() để lấy trận đầy đủ (bước 14), thay vì dùng lại trận trong danh sách vốn không kèm đề.

Xem lịch sử đấu, đoạn B: Xem bảng xếp hạng

1. Người chơi chọn thẻ Bảng xếp hạng trên HistoryView.
2. HistoryView gọi hàm getRanking() của lớp Player.
3. Lớp Player gọi hàm getPoints() của lớp Match cho từng trận đã kết thúc.
4. Lớp Match trả về điểm trận của người chơi cho lớp Player.
5. Lớp Player gọi hàm getResult() của lớp Match để biết các trận thắng.
6. Lớp Match trả về kết quả trận cho lớp Player.
7. Lớp Player gọi hàm getFinishTime() của lớp Match cho các trận thắng.
8. Lớp Match trả về thời gian làm bài cho lớp Player.
9. Lớp Player tính 3 tiêu chí, sắp xếp và trả danh sách người chơi cho HistoryView.
10. HistoryView hiển thị bảng xếp hạng cho người chơi.
11. Người chơi nhấn Back trên HistoryView.
12. HistoryView gọi MainView.
13. MainView hiển thị giao diện chính cho người chơi.

Ngoại lệ 9a: Không lấy được bảng xếp hạng.

- 9a.1. Lớp Player trả về lỗi cho HistoryView.
- 9a.2. HistoryView hiển thị thông báo không tải được bảng xếp hạng, danh sách để trống.

@tuan-tu-phan-tich-uc04b.png

Hình 30: Biểu đồ tuần tự pha phân tích chức năng Xem lịch sử đấu, đoạn B: Xem bảng xếp hạng.

- Biểu đồ có 13 thông điệp, khớp với 13 bước của kịch bản chuẩn.
- Lớp Player tính các tiêu chí dựa trên 3 phương thức của lớp Match (bước 3–8). Bảng xếp hạng vì vậy dùng đúng luật thắng thua của trận đấu.

Quản lý bạn bè, đoạn A: Gửi lời mời kết bạn

1. Người chơi nhấn nút Bạn bè trên MainView.
2. MainView gọi FriendView.
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

Ngoại lệ 4a: Không lấy được danh sách bạn bè.

- 4a.1. Lớp Friendship trả về lỗi cho FriendView.
- 4a.2. FriendView hiển thị thông báo không kết nối được máy chủ, 2 danh sách để trống.

Ngoại lệ 9a: Người chơi từ chối một lời mời đã nhận.

- 9a.1. Người chơi nhấn Từ chối ở một lời mời trên FriendView.
- 9a.2. FriendView gọi hàm delete() của lớp Friendship.
- 9a.3. Lớp Friendship xóa lời mời và trả về cho FriendView.
- 9a.4. FriendView bỏ lời mời khỏi danh sách lời mời đã nhận.

Ngoại lệ 9b: Người chơi hủy kết bạn hoặc rút lại lời mời đã gửi.

- 9b.1. Người chơi nhấn nút Hủy kết bạn ở một dòng của danh sách bạn bè trên FriendView.
- 9b.2. FriendView hỏi người chơi có chắc muốn hủy kết bạn không.
- 9b.3. Người chơi đồng ý.
- 9b.4. FriendView gọi hàm delete() của lớp Friendship.
- 9b.5. Lớp Friendship xóa quan hệ và trả về cho FriendView.
- 9b.6. FriendView bỏ dòng đó khỏi danh sách bạn bè.

Ngoại lệ 10a: Người chơi để trống ô tên đăng nhập.

- 10a.1. FriendView hiển thị thông báo yêu cầu nhập tên đăng nhập, không gọi sendRequest().

Ngoại lệ 12a: Không có người chơi nào có tên đăng nhập đã nhập.

- 12a.1. Lớp Friendship trả về lỗi không tìm thấy người chơi cho FriendView.
- 12a.2. FriendView hiển thị thông báo không tìm thấy người chơi, giữ nguyên nội dung ô tên đăng nhập.

Ngoại lệ 12b: Tên đăng nhập là của chính người chơi.

- 12b.1. Lớp Friendship trả về lỗi không thể kết bạn với chính mình cho FriendView.
- 12b.2. FriendView hiển thị thông báo không thể kết bạn với chính mình.

Ngoại lệ 12c: Hai người đã là bạn bè hoặc đã có lời mời giữa 2 người.

- 12c.1. Lớp Friendship trả về lỗi đã có quan hệ cho FriendView.
- 12c.2. FriendView hiển thị thông báo đã có lời mời hoặc đã là bạn bè, không thêm dòng mới.

@tuan-tu-phan-tich-uc07a.png

Hình 31: Biểu đồ tuần tự pha phân tích chức năng Quản lý bạn bè, đoạn A: Gửi lời mời kết bạn.

- Biểu đồ có 13 thông điệp, khớp với 13 bước của kịch bản chuẩn.
- Trạng thái online của bạn bè không nằm trong Friendship: FriendView lấy danh sách online từ Player (bước 5–6) rồi so với người bạn trong từng quan hệ (bước 7–8).
- Việc tìm người nhận theo tên đăng nhập và kiểm tra trùng quan hệ nằm trong sendRequest() của Friendship (bước 12), không nằm ở giao diện.

Quản lý bạn bè, đoạn B: Đồng ý lời mời kết bạn

Người chơi là người nhận, đang ở FriendView; các lời mời đã nhận được hiển thị như bước 1–9 của đoạn A.

1. Người chơi nhấn Đồng ý ở một lời mời trên FriendView.
2. FriendView gọi hàm accept() của lớp Friendship.
3. Lớp Friendship gọi hàm canAccept() của chính nó để kiểm tra lời mời còn đang chờ và người chơi là người nhận.
4. Lớp Friendship chuyển lời mời sang trạng thái đã là bạn bè và trả về cho FriendView.
5. FriendView gọi hàm getFriend() của lớp Friendship.
6. Lớp Friendship trả về người gửi lời mời cho FriendView.
7. FriendView bỏ lời mời khỏi danh sách lời mời đã nhận và hiển thị người gửi trong danh sách bạn bè cho người chơi.

Ngoại lệ 1a: Người chơi từ chối lời mời.

- 1a.1. Người chơi nhấn Từ chối ở lời mời trên FriendView.
- 1a.2. FriendView gọi hàm delete() của lớp Friendship.
- 1a.3. Lớp Friendship xóa lời mời và trả về cho FriendView.
- 1a.4. FriendView bỏ lời mời khỏi danh sách lời mời đã nhận.

Ngoại lệ 3a: Lời mời không còn, vì người gửi đã rút lại.

- 3a.1. Lớp Friendship trả về lỗi lời mời không còn cho FriendView.
- 3a.2. FriendView hiển thị thông báo lời mời không còn và bỏ lời mời khỏi danh sách lời mời đã nhận.

@tuan-tu-phan-tich-uc07b.png

Hình 32: Biểu đồ tuần tự pha phân tích chức năng Quản lý bạn bè, đoạn B: Đồng ý lời mời kết bạn.

- Biểu đồ có 7 thông điệp, khớp với 7 bước của kịch bản chuẩn.
- Luật "chỉ người nhận mới đồng ý được lời mời đang chờ" là phương thức canAccept() của Friendship (bước 3), nên không máy nào đồng ý thay cho người khác.
