# BÁO CÁO THIẾT KẾ CÁ NHÂN — MATH DASH 1v1

Sinh viên: Nguyễn Bá Hùng · Lớp: E23CNPM01
Chức năng trình bày: Chơi online, Xem bảng xếp hạng, Đăng nhập – đăng ký

Phạm vi hệ thống:
- Math Dash 1v1 là trò chơi giải toán nhanh trên Android: người chơi thách đấu nhau trên cùng một đề 10 câu trắc nghiệm, hoặc chơi với máy.
- Hệ thống gồm một server và nhiều client:
  - App Android (client): Java, mô hình MVC 3 tầng entity – dao – view.
  - Server Spring Boot: REST, MySQL. Lưu tài khoản, trận đấu, đề bài; sinh đề, chấm bài và làm trọng tài.
  - Firebase Realtime Database: kênh báo tức thời giữa các máy: ai đang online, lời mời thách đấu, diễn biến trận đang đấu.
- Chơi online và Xem bảng xếp hạng thuộc module Chơi online; bảng xếp hạng tính từ kết quả các trận chơi online. Đăng nhập, đăng ký thuộc module Tài khoản, dùng chung cho mọi chức năng.

---

## 1. Mô tả chi tiết các chức năng

### 1.1. Biểu đồ use case chi tiết module Chơi online

@uc-chi-tiet-choi-online.png

Hình 1: Biểu đồ use case chi tiết module Chơi online.

Người chơi khởi phát trực tiếp 2 use case của module: "Chơi online" (UC03) và "Xem bảng xếp hạng" (UC04). UC03 được phân rã như sau:
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
| Xem bảng xếp hạng (UC04) | Xem thứ hạng mọi người chơi theo tổng điểm, trung bình điểm đối thủ, trung bình thời gian thắng |

### 1.2. Chức năng "Chơi online"

Luồng nghiệp vụ:

Người chơi đã đăng nhập → Giao diện chính hiện lên với:
- Dòng thông tin của mình: tên hiển thị, tổng điểm.
- Danh sách người chơi online, xếp theo tổng điểm giảm dần; mỗi dòng có tên, tổng điểm, trạng thái Rỗi (màu xanh) / Bận (màu xám).
- Các nút Chơi với máy, Bảng xếp hạng, Đăng xuất.

→ Danh sách tự cập nhật khi có người vào, người thoát hoặc người đổi trạng thái → Người chơi bấm vào tên một người đang rỗi → Hệ thống gửi lời mời tới máy người đó → Hộp thoại "Đang chờ <tên> trả lời..." hiện lên.

Trên máy người được mời: hộp thoại "<tên> (<tổng điểm> điểm) thách đấu bạn" hiện lên với 2 nút Đồng ý, Từ chối → Người được mời nhấn Đồng ý → Server kiểm tra cả 2 vẫn online và rỗi → Sinh đề 10 câu, tạo trận, chuyển cả 2 sang Bận, gửi đề cho cả 2 máy → Cả 2 máy chuyển sang giao diện thi đấu.

- Bấm vào người đang bận: "<tên> đang bận", không gửi lời mời.
- Người được mời nhấn Từ chối: máy người mời đóng hộp thoại chờ và báo "<tên> đã từ chối lời mời".
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
- Không kết nối được server khi nộp bài: "Không nộp được bài, kiểm tra kết nối mạng", nút Nộp bài bấm lại được.

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

#### Kịch bản chuẩn

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
3. Hệ thống thấy HOANG đang rỗi, gửi lời mời tới máy 2. Máy 1 hiện hộp thoại "Đang chờ HOANG trả lời...".

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

#### Kịch bản ngoại lệ

| Bước | Tình huống ngoại lệ và cách xử lý |
|---|---|
| 2 | Hùng bấm vào dòng MINH. MINH đang Bận nên hệ thống báo "MINH đang bận", không gửi lời mời, giữ nguyên giao diện chính. |
| 5 | Từ chối. Hoàng nhấn Từ chối. Máy 2 đóng hộp thoại. Máy 1 đóng hộp thoại chờ và báo "HOANG đã từ chối lời mời"; Hùng vẫn ở giao diện chính, có thể mời người khác. |
| 6 | Hùng tắt ứng dụng trước khi Hoàng nhấn Đồng ý. Server thấy HUNG không còn online nên không tạo trận. Máy 2 báo "Không bắt đầu được trận đấu" và ở lại giao diện chính. |
| 9–10 | Hết giờ. Hùng mới chọn 8 câu thì ô thời gian về 0. Hệ thống tự nộp bài; 2 câu chưa chọn tính là sai, HUNG đúng 8/10. |
| 9–10 | Thoát trận. Hùng nhấn Thoát. Hệ thống hỏi "Thoát sẽ bị xử thua?" với 2 nút Ở lại, Thoát. Hùng nhấn Thoát: server xử HUNG thua, HOANG thắng và được 1 điểm (tổng 10), trả cả 2 về Rỗi. Máy 1 về giao diện chính. Máy 2 báo "Đối thủ đã thoát, bạn thắng" và hiện giao diện kết quả THẮNG! chỉ có nút Dừng. Nhấn Ở lại thì tiếp tục làm bài, ô thời gian vẫn chạy. |
| 9–12 | Đối thủ mất kết nối. Máy 2 mất mạng khi đang làm bài. HOANG biến khỏi danh sách online; hệ thống xử HOANG bỏ trận. Máy 1 báo "Đối thủ đã thoát, bạn thắng" như ngoại lệ trên. |
| 10 | Máy 1 không kết nối được server khi nộp bài. Hệ thống báo "Không nộp được bài, kiểm tra kết nối mạng", nút Nộp bài vẫn bấm được; ô thời gian vẫn chạy. |
| 13 | Một người đúng hết. Hoàng chọn 74 ở câu 7, đúng 9/10, dù nộp ở giây 30,0. HUNG đúng hết nên HUNG thắng, không xét thời gian. |
| 13 | Hòa. HUNG chọn 23 ở câu 3, HOANG chọn 35 ở câu 9; cả 2 đều có câu sai nên hòa. Mỗi người được 0,5 điểm: HUNG 13, HOANG 9,5. Giao diện kết quả hiện HÒA! (màu vàng). |
| 15–16 | Dừng chơi. Hoàng nhấn Dừng. Server trả HUNG và HOANG về Rỗi. Máy 2 về giao diện chính. Máy 1 báo "Đối thủ đã dừng chơi" và về giao diện chính. |

### 1.3. Chức năng "Xem bảng xếp hạng"

Luồng nghiệp vụ:

Người chơi đã đăng nhập, đang ở giao diện chính → Người chơi nhấn Bảng xếp hạng → Hệ thống lấy mọi người chơi và các trận đã kết thúc, tính 3 tiêu chí rồi sắp xếp → Giao diện bảng xếp hạng hiện lên với: tiêu đề "BẢNG XẾP HẠNG", danh sách mọi người chơi; mỗi dòng gồm hạng, tên, tổng điểm, trung bình điểm đối thủ, trung bình thời gian thắng; dòng của chính người chơi được tô nổi bật → Người chơi nhấn Back → Quay về giao diện chính.

Thứ tự xếp hạng:
1. Tổng điểm, giảm dần.
2. Cùng tổng điểm thì xét trung bình tổng điểm của các đối thủ đã gặp, giảm dần.
3. Cùng cả 2 tiêu chí trên thì xét trung bình thời gian làm bài trong các trận thắng, tăng dần. Người chưa thắng trận nào hiện "—" và đứng sau.

- Không kết nối được server: "Không tải được bảng xếp hạng, kiểm tra kết nối mạng", danh sách để trống.

| | |
|---|---|
| Use case | Xem bảng xếp hạng (UC04) |
| Actor | Người chơi (Hùng) |
| Tiền điều kiện | Hùng đã đăng nhập, máy có kết nối mạng, đang ở giao diện chính |
| Hậu điều kiện | Hùng thấy thứ hạng của mọi người chơi; dữ liệu không thay đổi |

#### Kịch bản chuẩn

1. Ở giao diện chính (dòng "HUNG · 13,5 điểm"), Hùng nhấn nút Bảng xếp hạng.
2. Hệ thống lấy mọi người chơi và các trận đã kết thúc, tính 3 tiêu chí rồi sắp xếp. Giao diện bảng xếp hạng hiện lên gồm tiêu đề "BẢNG XẾP HẠNG" và danh sách:

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
3. Hùng xem danh sách rồi nhấn Back.
4. Giao diện chính hiện lại.

#### Kịch bản ngoại lệ

| Bước | Tình huống ngoại lệ và cách xử lý |
|---|---|
| 2 | Máy mất mạng hoặc server không phản hồi. Hệ thống báo "Không tải được bảng xếp hạng, kiểm tra kết nối mạng", danh sách để trống; Hùng nhấn Back để về giao diện chính. |

### 1.4. Chức năng "Đăng nhập, đăng ký"

@uc-chi-tiet-tai-khoan.png

Hình 2: Biểu đồ use case chi tiết module Tài khoản.

- Người chơi khởi phát "Đăng ký" (UC01) và "Đăng nhập" (UC02); Quản trị viên chỉ khởi phát "Đăng nhập".
- "Đăng nhập" include "Kiểm tra tài khoản" và "Ghi nhớ phiên đăng nhập": lần sau mở ứng dụng không phải đăng nhập lại.
- Hai use case được trình bày chung thành chức năng "Đăng nhập, đăng ký", vì người dùng đi từ giao diện đăng nhập sang đăng ký rồi quay lại.

| Use case | Mô tả |
|---|---|
| Đăng ký (UC01) | Tạo tài khoản người chơi mới bằng tên đăng nhập, mật khẩu, tên hiển thị |
| Đăng nhập (UC02) | Đăng nhập bằng tên đăng nhập và mật khẩu; vào giao diện chính, hoặc giao diện quản lý người chơi nếu là quản trị viên |
| Kiểm tra tài khoản | Hệ thống so tên đăng nhập và mật khẩu với tài khoản đã lưu |
| Ghi nhớ phiên đăng nhập | Hệ thống nhớ người đã đăng nhập trên máy cho tới khi đăng xuất |

Luồng nghiệp vụ:

Người dùng mở ứng dụng → Hệ thống kiểm tra phiên đăng nhập đã ghi nhớ → Chưa có phiên: giao diện đăng nhập hiện lên với ô tên đăng nhập, ô mật khẩu, nút Đăng nhập, nút Đăng ký.

- Đăng ký: Người dùng nhấn Đăng ký → Giao diện đăng ký hiện lên với ô tên đăng nhập, ô mật khẩu, ô nhập lại mật khẩu, ô tên hiển thị, nút Đăng ký → Người dùng nhập thông tin và nhấn Đăng ký → Hệ thống kiểm tra thông tin, tạo tài khoản người chơi → Thông báo "Đăng ký thành công" → Quay về giao diện đăng nhập.
  - Có ô bỏ trống: "Vui lòng nhập đủ thông tin".
  - Mật khẩu nhập lại không khớp: "Mật khẩu nhập lại không khớp".
  - Tên đăng nhập đã có: "Tên đăng nhập đã được dùng".
- Đăng nhập: Người dùng nhập tên đăng nhập, mật khẩu và nhấn Đăng nhập → Hệ thống kiểm tra tài khoản → Ghi nhớ phiên đăng nhập → Mở giao diện chính (người chơi) hoặc giao diện quản lý người chơi (quản trị viên).
  - Sai tên đăng nhập hoặc mật khẩu: "Sai tên đăng nhập hoặc mật khẩu".
  - Đã có phiên từ lần trước: vào thẳng giao diện chính, không hiện giao diện đăng nhập.
  - Không kết nối được server: thông báo lỗi kết nối, nút Đăng nhập bấm lại được.

| | |
|---|---|
| Use case | Đăng ký (UC01), Đăng nhập (UC02) |
| Actor | Người chơi (Hùng, máy 1) |
| Tiền điều kiện | Máy có kết nối mạng; trên máy chưa có phiên đăng nhập; tên đăng nhập hung chưa có trong hệ thống |
| Hậu điều kiện | Hệ thống có tài khoản hung; máy 1 ghi nhớ phiên của HUNG và đang ở giao diện chính |

Kịch bản chuẩn chia 2 đoạn: A. Đăng ký, B. Đăng nhập.

#### Kịch bản chuẩn

A. Đăng ký

1. Hùng mở ứng dụng trên máy 1. Giao diện đăng nhập hiện lên gồm: tiêu đề MATH DASH 1v1, ô tên đăng nhập, ô mật khẩu, nút Đăng nhập, nút Đăng ký.
2. Hùng nhấn Đăng ký.
3. Giao diện đăng ký hiện lên gồm: ô tên đăng nhập, ô mật khẩu, ô nhập lại mật khẩu, ô tên hiển thị, nút Đăng ký.
4. Hùng nhập:

   | Ô | Giá trị |
   |---|---|
   | Tên đăng nhập | hung |
   | Mật khẩu | hung123 |
   | Nhập lại mật khẩu | hung123 |
   | Tên hiển thị | HUNG |

   rồi nhấn Đăng ký.
5. Hệ thống thấy các ô đủ, mật khẩu nhập lại khớp, tên đăng nhập hung chưa có. Hệ thống tạo tài khoản người chơi hung, tên hiển thị HUNG, tổng điểm 0, và thông báo "Đăng ký thành công".
6. Giao diện đăng nhập hiện lại.

B. Đăng nhập

7. Hùng nhập tên đăng nhập hung, mật khẩu hung123 và nhấn Đăng nhập.
8. Hệ thống kiểm tra đúng tài khoản, vai trò là người chơi. Hệ thống ghi nhớ phiên đăng nhập của HUNG trên máy 1.
9. Giao diện chính hiện lên với dòng "HUNG · 0 điểm", danh sách người chơi online, các nút Chơi với máy, Bảng xếp hạng, Đăng xuất.

#### Kịch bản ngoại lệ

| Bước | Tình huống ngoại lệ và cách xử lý |
|---|---|
| 1 | Máy 1 đã ghi nhớ phiên của HUNG từ lần trước. Hệ thống vào thẳng giao diện chính như bước 9. |
| 4 | Hùng để trống ô tên hiển thị. Hệ thống báo "Vui lòng nhập đủ thông tin", không tạo tài khoản. |
| 4 | Hùng nhập lại mật khẩu là hung124. Hệ thống báo "Mật khẩu nhập lại không khớp", không tạo tài khoản. |
| 5 | Tên đăng nhập hung đã có người dùng. Hệ thống báo "Tên đăng nhập đã được dùng", giữ nguyên giao diện đăng ký để nhập tên khác. |
| 8 | Hùng nhập mật khẩu hung000. Hệ thống báo "Sai tên đăng nhập hoặc mật khẩu", giữ nguyên giao diện đăng nhập. |
| 8 | Quản trị viên đăng nhập bằng tài khoản admin. Hệ thống ghi nhớ phiên và mở giao diện quản lý người chơi thay cho giao diện chính. |

---

## 2. Thiết kế cơ sở dữ liệu

Hai chức năng dùng 2 nơi lưu trữ:
- MySQL trên server: tài khoản, trận đấu, đề bài và kết quả chấm. Đây là nguồn dữ liệu của tổng điểm và bảng xếp hạng.
- Firebase Realtime Database: người chơi đang online, lời mời, trận đang đấu. Dữ liệu này chỉ có ý nghĩa trong lúc chơi.

### 2.1. CSDL MySQL

Bước 1: Mỗi lớp thực thể cần lưu đề xuất một bảng.
- Player → tblPlayer.
- Match → tblMatch.
- MatchPlayer → tblMatchPlayer.
- MathProblem → tblMathProblem: server phải giữ đề và đáp án để chấm bài.
- Challenge không có bảng: lời mời chỉ có ý nghĩa vài giây, sau đó không chức năng nào tra lại.

Bước 2: Thuộc tính kiểu cơ bản thành cột.
- Bỏ thuộc tính kiểu đối tượng: Match.questions, Match.players, MatchPlayer.player.
- Kiểu dữ liệu: String → varchar(255); int → integer(10); Date → datetime(3), có mili giây để so thời gian làm bài; boolean → tinyint(1).
- answerOptions luôn có 4 phần tử nên tách thành 4 cột option1 … option4.
- Kết quả:
  - tblPlayer: id, username, passwordHash, displayName, role, createdAt.
  - tblMatch: id, startedAt, endedAt.
  - tblMathProblem: id, operand1, operand2, operator, correctAnswer, option1, option2, option3, option4.
  - tblMatchPlayer: id, correctCount, submittedAt, forfeited.

Bước 3: Quan hệ giữa các bảng.
- 1 tblMatch – n tblMatchPlayer (đúng 2 dòng mỗi trận).
- 1 tblMatch – n tblMathProblem (đúng 10 dòng mỗi trận).
- 1 tblPlayer – n tblMatchPlayer.
- Quan hệ n-n tblPlayer – tblMatch đã được tách bằng tblMatchPlayer từ thiết kế lớp thực thể.

Bước 4: Khóa.
- Khóa chính: id (AUTO_INCREMENT) ở mọi bảng.
- Khóa ngoại đặt ở bảng phía n:
  - tblMatchPlayer.tblMatchId → tblMatch.id và tblMathProblem.tblMatchId → tblMatch.id, có ON DELETE CASCADE đúng với quan hệ hợp thành.
  - tblMatchPlayer.tblPlayerId → tblPlayer.id.
- Ràng buộc duy nhất: tblPlayer.username; tblMatchPlayer (tblMatchId, tblPlayerId).
- Thứ tự câu trong đề là thứ tự id tăng dần, vì server chèn 10 câu theo đúng thứ tự trong một transaction.

Bước 5: Loại thuộc tính gây dư thừa.
- Thuộc tính dẫn xuất không có cột:
  - Kết quả và điểm trận: suy ra từ correctCount, submittedAt, forfeited của 2 người và startedAt của trận → Match.getResult(playerId), Match.getPoints(playerId).
  - Tổng điểm, trung bình điểm đối thủ, trung bình thời gian thắng: tính từ các trận đã xong.
  - tblMatch.status: trận đã xong khi và chỉ khi endedAt có giá trị.
- Không cần lưu lâu dài: trạng thái rỗi / bận của người chơi (chỉ ở Firebase); bài làm answers (chấm xong thì chỉ cần correctCount).
- Giữ lại có chủ đích: correctAnswer suy ra được từ phép tính, nhưng là căn cứ chấm và đối chiếu kết quả.
- Chỉ endedAt và submittedAt được rỗng (trận đang đấu, người chưa nộp). Mật khẩu chỉ lưu dạng băm BCrypt.

@csdl-mysql.png

Hình 3: Biểu đồ thiết kế CSDL MySQL cho chức năng Chơi online và Xem bảng xếp hạng.

- tblMatch là trung tâm: 10 dòng tblMathProblem là đề, 2 dòng tblMatchPlayer nối trận với 2 người chơi.
- Bảng xếp hạng chỉ đọc tblPlayer, tblMatch, tblMatchPlayer; không cần đọc đề.

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
- matches: chỉ server ghi. Đề trên Firebase không có correctAnswer, để người chơi không đọc được đáp án trước khi nộp bài.

---

## 3. Thiết kế lớp thực thể

Đầu vào là biểu đồ lớp thực thể pha phân tích (Phụ lục A). Các lớp liên quan đến 2 chức năng: Player, Challenge, Match, MatchPlayer, MathProblem.

Bước 1: Bổ sung thuộc tính khóa.
- Thêm id : int cho Player, Match, MatchPlayer, MathProblem.
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

Bước 4: Bổ sung thuộc tính đối tượng.
- Challenge.challenger, Challenge.opponent : Player.
- Match.questions : List\<MathProblem>, Match.players : List\<MatchPlayer>.
- MatchPlayer.player : Player.

Thuộc tính dẫn xuất được viết thành phương thức nghiệp vụ (hàm Java thuần, có unit test):

| Lớp | Phương thức | Ý nghĩa |
|---|---|---|
| MathProblem | isCorrect(answer : int) : boolean | answer bằng correctAnswer |
| Match | countCorrect(answers : List\<Integer>) : int | Số câu đúng của một bài làm |
| Match | isOver() : boolean | Có người bỏ trận, hoặc cả 2 đã nộp bài |
| Match | getFinishTime(playerId : int) : long | submittedAt − startedAt |
| Match | getResult(playerId : int) : String | WIN / LOSE / DRAW: bỏ trận thì thua; một người đúng hết thì người đó thắng; cả 2 có câu sai thì hòa; cả 2 đúng hết thì ai nhanh hơn thắng |
| Match | getPoints(playerId : int) : double | 1 / 0,5 / 0 |

Player giữ thêm các giá trị dẫn xuất mà giao diện cần hiển thị cùng tên: totalPoints, avgOpponentPoints, avgWinTime do server tính; status lấy từ Firebase. Các giá trị này không có cột trong CSDL.

@lop-thuc-the-hung.png

Hình 4: Biểu đồ lớp thực thể pha thiết kế cho Chơi online và Xem bảng xếp hạng.

- Không còn đường liên kết trơn nào trên biểu đồ.
- Player, Challenge, Match, MatchPlayer, MathProblem có ở cả app và server với cùng tên và cùng thuộc tính, để JSON đi qua REST ánh xạ thẳng vào đối tượng.
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
| RankingView | RankingActivity + RankingAdapter | activity_ranking.xml, item_ranking.xml |

MatchResultView là overlay chứ không phải Activity riêng:
- Kết quả hiện đè lên màn thi đấu ngay khi trận xong.
- Câu hỏi "Chơi tiếp?" cần tiếp tục theo dõi trận đang mở.

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
| register(), checkLogin() | Player | register(username : String, password : String, displayName : String) : void → register(body : Map\<String, String>) : void<br>login(username : String, password : String) : Player → login(body : Map\<String, String>) : Player<br>findByUsername(username : String) : Player, insert(player : Player) : boolean | PlayerApiDAO → AuthRestController<br><br>PlayerDAO (server) |
| saveSession(), getSession() | Player | save(player : Player) : void, getPlayer() : Player<br>saveOrUpdate(player : Player) : void | SessionDAO<br>PlayerDAO (app) |
| getRanking() | Player | getRanking() : List\<Player> → getRanking() : List\<Player><br>getRanking() : List\<Player>, findFinished() : List\<Match> | PlayerApiDAO → PlayerRestController<br>PlayerDAO, MatchDAO |

Lớp DAO cha

Mỗi nơi lưu trữ có một lớp DAO cha giữ kết nối dùng chung; các DAO con kế thừa.

| Nơi lưu trữ | DAO cha | Giữ | DAO con dùng ở đây |
|---|---|---|---|
| App → Firebase | FirebaseDAO | con : DatabaseReference | PresenceFirebaseDAO, ChallengeFirebaseDAO, MatchFirebaseDAO |
| App → REST | ApiDAO | BASE_URL; gửi / nhận JSON | PlayerApiDAO, MatchApiDAO |
| Server → MySQL | DAO | conn : Connection | PlayerDAO, MatchDAO |

Các lớp không có lớp cha:
- SessionDAO chỉ đọc SharedPreferences.
- FirebaseAdminDAO gọi REST API của Firebase Realtime Database, không dùng JDBC nên không kế thừa DAO của server.
- ProblemDAO ở server chỉ sinh câu hỏi bằng Random, không kết nối nơi lưu trữ nào.

### 4.1. Chức năng "Chơi online"

#### 4.1.1. Thiết kế giao diện

@giao-dien-uc03.png

Hình 5: Thiết kế giao diện chức năng Chơi online.

Chức năng có 2 màn, 1 hộp thoại lời mời và 1 overlay, đi theo thứ tự MainActivity → hộp thoại lời mời trên máy người được mời → MatchActivity → overlay layoutResult.
- Màn chính chia 2 phần:
  - Trên cùng là thẻ thông tin của mình: tên, tổng điểm.
  - Bên dưới là danh sách người chơi online. Mỗi dòng có tên, tổng điểm và nhãn trạng thái: Rỗi nền xanh, Bận nền xám. Dòng Bận vẫn bấm được nhưng chỉ hiện thông báo.
- Màn thi đấu chia 3 tầng từ trên xuống:
  1. Hàng trên: ô thời gian đếm ngược bên trái, dòng trạng thái đối thủ bên phải.
  2. Danh sách 10 câu cuộn được. Mỗi câu là một thẻ gồm số thứ tự, phép tính chữ lớn, 4 phương án dạng RadioButton xếp 2 x 2.
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
| MatchActivity | txtTimer | TextView | Ô thời gian 00:60 → 00:00 | outTimer |
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
| | btnStop | Button | Dừng | subStop |

Các thông báo ngắn ("MINH đang bận", "HOANG đã từ chối lời mời", "Đối thủ đã dừng chơi") dùng Toast. Hộp thoại lời mời, hộp thoại chờ trả lời và hộp thoại "Thoát sẽ bị xử thua?" dùng AlertDialog. Cả hai đều không phải thành phần của layout.

#### 4.1.2. Biểu đồ lớp chi tiết

@lop-thiet-ke-uc03.png

Hình 6: Biểu đồ lớp chi tiết chức năng Chơi online.

Package "Client (Android)".

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | MainActivity | Màn chính, cài đặt PlayerListCallback và ChallengeCallback; giữ người chơi đang đăng nhập (me).<br>- onCreate lấy me bằng SessionDAO.getPlayer, gọi PlayerApiDAO.getById trong Thread để có tổng điểm mới, rồi goOnline, watchOnlinePlayers, watchIncoming.<br>- onPlayersChanged hiện thông tin của mình và đưa những người còn lại vào PlayerAdapter.<br>- onPlayerClick: đối thủ bận thì hiện Toast; rỗi thì sendChallenge, watchChallenge và hiện hộp thoại chờ.<br>- onChallengeChanged: lời mời tới mình đang chờ thì showChallengeDialog; lời mình gửi được đồng ý thì openMatch; bị từ chối thì hiện Toast.<br>- acceptChallenge gọi MatchApiDAO.createMatch trong Thread, xong thì openMatch. Nút Từ chối gọi ChallengeFirebaseDAO.rejectChallenge |
| Boundary | PlayerAdapter, PlayerViewHolder | PlayerAdapter kế thừa RecyclerView.Adapter, giữ danh sách Player. setPlayers thay danh sách và vẽ lại. onClick của một dòng gọi MainActivity.onPlayerClick. PlayerViewHolder giữ 3 TextView của một dòng |
| Boundary | MatchActivity | Màn thi đấu, cài đặt MatchCallback; giữ me, mã trận (matchId), đồng hồ 60 giây (timer).<br>- onCreate gọi MatchFirebaseDAO.watchMatch. Lần đầu nhận trận, onSuccess gọi startMatch: đưa đề vào QuestionAdapter, bật đồng hồ.<br>- onTick vẽ thời gian; onFinish gọi submitAnswers.<br>- submitAnswers lấy bài làm bằng QuestionAdapter.getAnswers, gọi MatchApiDAO.submit trong Thread, rồi khóa danh sách câu.<br>- onSuccess các lần sau: đối thủ đã nộp thì showOpponentStatus; trận đã xong thì loadResult gọi MatchApiDAO.getById rồi showResult.<br>- showResult dùng Match.getResult, getFinishTime, getPoints trên bản trận server trả về.<br>- Nút Chơi tiếp / Dừng gọi sendChoice (MatchApiDAO.continueMatch). onNextMatch mở trận mới bằng openNextMatch; onStopped hiện Toast và về màn chính.<br>- Nút Thoát và onBackPressed hỏi xác nhận rồi gọi leaveMatch (MatchApiDAO.forfeit).<br>Activity không tự chấm bài, không tự phân thắng thua |
| Boundary | QuestionAdapter, QuestionViewHolder | QuestionAdapter giữ danh sách MathProblem và mảng phương án đã chọn (answers). onBindViewHolder hiện getExpression và 4 phương án; onCheckedChanged lưu phương án vào answers. getAnswers trả bài làm, câu chưa chọn là −1. setLocked khóa mọi RadioButton sau khi nộp. QuestionViewHolder giữ txtExpression, rgOptions, rbOptions |
| Control | SessionDAO | getPlayer() đọc playerId, username, displayName từ SharedPreferences "session" và đóng gói thành Player |
| Control | FirebaseDAO ← PresenceFirebaseDAO | FirebaseDAO giữ con (gốc Realtime Database). PresenceFirebaseDAO đọc / ghi node presence:<br>- goOnline ghi tên, tổng điểm, trạng thái free và đăng ký onDisconnect().removeValue().<br>- watchOnlinePlayers / stopWatching thêm / gỡ ValueEventListener.<br>- toPlayer đóng gói một node thành Player |
| Control | FirebaseDAO ← ChallengeFirebaseDAO | Đọc / ghi node challenges:<br>- sendChallenge tạo node bằng push().<br>- watchIncoming lọc lời mời có opponentId là mình và status pending.<br>- watchChallenge theo dõi một lời mời đã gửi.<br>- rejectChallenge ghi status rejected.<br>- toChallenge đóng gói thành Challenge |
| Control | FirebaseDAO ← MatchFirebaseDAO | Chỉ đọc node matches/{matchId}. toMatch đóng gói thành Match kèm 10 MathProblem (không có đáp án) và 2 MatchPlayer. Khi node có nextMatchId thì gọi onNextMatch; có stoppedBy thì gọi onStopped |
| — | PlayerListCallback, ChallengeCallback, MatchCallback | Interface trả kết quả bất đồng bộ từ các FirebaseDAO về Activity |
| Control | ApiDAO ← PlayerApiDAO | getById gửi GET /api/players/{id}; toPlayer đóng gói JSON thành Player. Hàm chạy đồng bộ nên phải gọi trong Thread |
| Control | ApiDAO ← MatchApiDAO | ApiDAO giữ BASE_URL và các hàm gửi / nhận JSON bằng HttpURLConnection. MatchApiDAO có 5 hàm, mỗi hàm một API:<br>- createMatch: POST /api/matches.<br>- submit: POST /api/matches/{id}/submit.<br>- forfeit: POST /api/matches/{id}/forfeit.<br>- continueMatch: POST /api/matches/{id}/continue.<br>- getById: GET /api/matches/{id}.<br>toMatch đóng gói JSON trả về thành Match |
| Entity | Player, Challenge, Match, MatchPlayer, MathProblem | Như thiết kế lớp thực thể. Các phương thức nghiệp vụ là hàm Java thuần, có unit test:<br>- MathProblem: isCorrect, getExpression.<br>- Match: countCorrect, isOver, getFinishTime, getResult, getPoints |

Package "Server (Spring Boot)".

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | PlayerRestController | getById(id) nhận GET /api/players/{id}: PlayerDAO.findById rồi gán tổng điểm bằng PlayerDAO.getTotalPoints |
| Boundary | MatchRestController | Trọng tài của mọi trận. Giữ waitingChoices: các trận đang có một người chọn chơi tiếp.<br>- create(challenge) nhận POST /api/matches:<br>  1. readPresence của 2 người; một người không online hoặc đang bận thì trả 409 "Không bắt đầu được trận đấu".<br>  2. startMatch: ProblemDAO.generateProblems(10), tạo Match, MatchDAO.insert, FirebaseAdminDAO.writeMatch, updateStatus busy cho 2 người.<br>  3. updateChallenge accepted kèm id trận.<br>- submit(id, matchPlayer):<br>  1. Ghi thời điểm nhận, findById, chấm bằng Match.countCorrect, updateSubmission, writeSubmitted.<br>  2. Nếu Match.isOver: updateEndedAt, tính lại tổng điểm 2 người, updatePoints, writeFinished.<br>- forfeit(id, playerId): updateForfeit, kết thúc trận như trên, writeStopped, trả 2 người về free.<br>- continueMatch(id, playerId, choice), synchronized:<br>  - Dừng: writeStopped, 2 người về free.<br>  - Chơi tiếp mà đối thủ chưa chọn: lưu vào waitingChoices, writeChoice.<br>  - Đối thủ đã chọn chơi tiếp: startMatch rồi writeNextMatch.<br>- getById(id): findById, gán tổng điểm cho 2 người chơi |
| Control | DAO ← PlayerDAO | DAO giữ conn : Connection (JDBC).<br>- findById đọc tblPlayer.<br>- getTotalPoints lấy các trận đã xong của người chơi rồi cộng Match.getPoints |
| Control | DAO ← MatchDAO | - insert ghi 1 dòng tblMatch, 10 dòng tblMathProblem, 2 dòng tblMatchPlayer trong một transaction, gán id vừa sinh.<br>- findById đọc trận kèm đề và 2 người chơi.<br>- updateSubmission ghi correctCount, submittedAt.<br>- updateForfeit ghi forfeited.<br>- updateEndedAt ghi endedAt |
| Control | ProblemDAO | Giữ random : Random. generateProblems gọi generateProblem 10 lần. Mỗi câu chọn toán tử +, −, ×, sinh 2 toán hạng, tính correctAnswer, sinh 3 phương án nhiễu gần đáp án rồi xáo trộn |
| Control | FirebaseAdminDAO | Giữ dbUrl, gọi REST API của Firebase:<br>- readPresence gửi GET.<br>- Các hàm write..., update... gửi PUT hoặc PATCH tới đúng node trong mục 2.2 |
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

Firebase không phải một lớp trên biểu đồ: các FirebaseDAO ở app và FirebaseAdminDAO ở server cùng đọc / ghi một cây dữ liệu, như mô tả trong mục 2.2.

#### 4.1.3. Biểu đồ tuần tự chi tiết

Kịch bản v3 chia 4 đoạn như kịch bản v1.

Đoạn A: Gửi lời thách đấu

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

@tuan-tu-thiet-ke-uc03a.png

Hình 7: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn A: Gửi lời thách đấu.

- Biểu đồ có 39 thông điệp.
- Lời gọi REST duy nhất là bước 7, để lấy tổng điểm mới nhất từ server. Server đi theo khuôn PlayerRestController → PlayerDAO → Player.
- Firebase làm 3 việc:
  - Ghi người chơi vào danh sách online (bước 18).
  - Báo khi danh sách thay đổi (bước 21–24).
  - Chuyển lời mời giữa 2 máy (bước 29–38).
- Bất đồng bộ: watchOnlinePlayers(), watchIncoming(), watchChallenge() là lời đăng ký; kết quả đến sau qua onDataChange(). Bước 35 xảy ra khi đối thủ đồng ý ở đoạn B.

Đoạn B: Nhận lời thách đấu

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
11. Hàm create() gọi hàm readPresence() của FirebaseAdminDAO cho người mời và người được mời.
12. Hàm readPresence() đọc node presence và gọi lớp Player để đóng gói người chơi kèm trạng thái.
13. Lớp Player trả đối tượng Player cho hàm readPresence().
14. Hàm readPresence() trả đối tượng Player cho hàm create(); cả 2 người đang online và rỗi.
15. Hàm create() gọi hàm startMatch() của chính nó.
16. Hàm startMatch() gọi hàm generateProblems() của ProblemDAO để sinh đề 10 câu.
17. Với mỗi câu, hàm generateProblems() sinh phép tính ngẫu nhiên và gọi lớp MathProblem để đóng gói kèm 4 phương án.
18. Lớp MathProblem trả đối tượng MathProblem cho hàm generateProblems().
19. Hàm generateProblems() trả danh sách 10 câu hỏi cho hàm startMatch().
20. Hàm startMatch() gọi lớp Match để tạo trận với đề vừa sinh, thời điểm bắt đầu và 2 người chơi.
21. Lớp Match trả đối tượng Match cho hàm startMatch().
22. Hàm startMatch() gọi hàm insert() của MatchDAO.
23. Hàm insert() lưu trận, 10 câu hỏi và 2 người chơi trong trận vào CSDL trong một transaction, gán id vừa sinh cho trận.
24. Hàm insert() trả kết quả cho hàm startMatch().
25. Hàm startMatch() gọi hàm writeMatch() của FirebaseAdminDAO để gửi đề, không kèm đáp án, cho cả 2 máy.
26. Hàm startMatch() gọi hàm updateStatus() của FirebaseAdminDAO để chuyển 2 người chơi sang trạng thái bận.
27. Hàm create() gọi hàm updateChallenge() của FirebaseAdminDAO để đánh dấu lời mời đã được đồng ý, kèm mã trận.
28. Hàm create() trả trận dạng JSON cho MatchApiDAO.
29. Hàm toMatch() của MatchApiDAO gọi lớp Match để đóng gói dữ liệu JSON.
30. Lớp Match trả đối tượng Match cho MatchApiDAO.
31. MatchApiDAO trả đối tượng Match cho hàm acceptChallenge().
32. Trong hàm runOnUiThread(), hàm acceptChallenge() gọi hàm openMatch() để mở MatchActivity với mã trận.

@tuan-tu-thiet-ke-uc03b.png

Hình 8: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn B: Nhận lời thách đấu.

- Biểu đồ có 32 thông điệp.
- Server sinh đề (bước 16–19) và lưu đề vào MySQL cùng trận (bước 22–23), vì server cần đáp án để chấm bài sau này.
- Đề gửi qua Firebase không kèm đáp án (bước 25): người chơi không đọc được đáp án trước khi nộp bài.
- Bước 27 cập nhật lời mời, đây chính là tín hiệu để máy người mời mở MatchActivity ở bước 35 của đoạn A.
- Hàm create() kiểm tra lại cả 2 người còn online và rỗi (bước 11–14), nên không thể có người ở trong 2 trận cùng lúc.

Đoạn C: Thi đấu

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

@tuan-tu-thiet-ke-uc03c.png

Hình 9: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn C: Thi đấu.

- Biểu đồ có 74 thông điệp.
- Bước 2 lược bỏ thông điệp đóng gói Player bên trong SessionDAO, vì đã vẽ ở đoạn A.
- Server làm trọng tài, gồm 3 phần nghiệp vụ là hàm Java thuần của lớp thực thể:
  - Chấm bài: hàm countCorrect() của Match gọi hàm isCorrect() của MathProblem (bước 27–30).
  - Điều kiện kết thúc: hàm isOver() của Match (bước 34, 42).
  - Thắng thua: hàm getResult() và getPoints() của Match. App gọi 2 hàm này trên bản trận server trả về (bước 68–73).
- Thời gian làm bài tính theo đồng hồ server: thời điểm server nhận bài làm trừ thời điểm bắt đầu trận. Độ trễ khác nhau giữa 2 máy không làm sai kết quả do đồng hồ máy lệch nhau.
- Bước 42–49 diễn ra khi server xử lý yêu cầu nộp bài từ máy đối thủ. Máy này biết trận đã xong nhờ Firebase (bước 50–53).

Đoạn D: Chơi tiếp

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

@tuan-tu-thiet-ke-uc03d.png

Hình 10: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn D: Chơi tiếp.

- Biểu đồ có 24 thông điệp, vẽ cho người chọn chơi tiếp trước.
- Hàm continueMatch() chạy tuần tự (synchronized). Nếu 2 yêu cầu chơi tiếp đến cùng lúc, chỉ yêu cầu đến sau tạo trận mới, nên không sinh 2 trận.
- Trận mới dùng lại hàm startMatch() của đoạn B (bước 11–20), không viết lại logic sinh đề.
- Máy này nhận trận mới qua Firebase (bước 22), mở MatchActivity mới và đi lại đoạn C.

### 4.2. Chức năng "Xem bảng xếp hạng"

#### 4.2.1. Thiết kế giao diện

@giao-dien-uc04.png

Hình 11: Thiết kế giao diện chức năng Xem bảng xếp hạng.

Màn Bảng xếp hạng là một danh sách cuộn, mỗi dòng là một item_ranking.xml.
- Hàng tiêu đề cột cố định ở trên.
- Dòng của người xem có nền màu nhấn, để thấy ngay hạng của mình.
- 3 hạng đầu có số hạng màu vàng, bạc, đồng.

Bảng thành phần:

| Màn | id trong layout | Loại View | Chức năng | Tên ở pha phân tích |
|---|---|---|---|---|
| MainActivity | txtMyName, txtMyPoints | TextView | "HUNG · 13,5 điểm" | outMyInfo |
| | btnRanking | Button | Mở màn Bảng xếp hạng | subRanking |
| RankingActivity | rvRanking | RecyclerView | Danh sách đã xếp hạng | outListRanking |
| item_ranking.xml | txtRank | TextView | Hạng | một dòng của outListRanking |
| | txtName | TextView | Tên | một dòng của outListRanking |
| | txtPoints | TextView | Tổng điểm | một dòng của outListRanking |
| | txtAvgOpponent | TextView | Trung bình điểm đối thủ | một dòng của outListRanking |
| | txtAvgWinTime | TextView | Trung bình thời gian thắng, "—" nếu chưa thắng | một dòng của outListRanking |

#### 4.2.2. Biểu đồ lớp chi tiết

@lop-thiet-ke-uc04.png

Hình 12: Biểu đồ lớp chi tiết chức năng Xem bảng xếp hạng.

Package "Client (Android)".

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | MainActivity | onClick của nút Bảng xếp hạng mở RankingActivity |
| Boundary | RankingActivity | onCreate lấy người chơi bằng SessionDAO.getPlayer rồi gọi loadRanking. loadRanking gọi PlayerApiDAO.getRanking trong Thread, rồi trong runOnUiThread gắn RankingAdapter. Lỗi mạng thì hiện Toast |
| Boundary | RankingAdapter, RankingViewHolder | RankingAdapter giữ danh sách Player và id của người xem. onBindViewHolder hiện hạng bằng vị trí + 1, 3 tiêu chí của Player, tô nền dòng có id trùng người xem. RankingViewHolder giữ 5 TextView của một dòng |
| Control | SessionDAO | Như ở Chơi online |
| Control | ApiDAO ← PlayerApiDAO | getRanking gửi GET /api/players/ranking, đóng gói từng phần tử JSON thành Player bằng toPlayer |
| Entity | Player | Mang 3 tiêu chí totalPoints, avgOpponentPoints, avgWinTime do server tính |

Package "Server (Spring Boot)".

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | PlayerRestController | getRanking() nhận GET /api/players/ranking, gọi PlayerDAO.getRanking và trả danh sách Player dạng JSON |
| Control | DAO ← PlayerDAO | getRanking:<br>1. Đọc mọi tblPlayer có role = player.<br>2. Lấy các trận đã xong bằng MatchDAO.findFinished.<br>3. Cộng Match.getPoints vào tổng điểm từng người; ghi lại các đối thủ đã gặp; với trận Match.getResult là WIN thì cộng Match.getFinishTime.<br>4. Tính 2 giá trị trung bình rồi sắp xếp theo 3 tiêu chí |
| Control | DAO ← MatchDAO | findFinished truy vấn tblMatch có endedAt khác rỗng, JOIN tblMatchPlayer, tblPlayer, đóng gói mỗi trận thành 1 Match + 2 MatchPlayer + 2 Player. Không cần đề nên không đọc tblMathProblem |
| Entity | Player, Match, MatchPlayer | Như ở Chơi online |

Quan hệ giữa các lớp giao diện:
- MainActivity – RankingActivity – RankingAdapter – RankingViewHolder nối nhau bằng quan hệ liên kết.
- Layout: MainActivity – activity_main.xml, RankingActivity – activity_ranking.xml, RankingViewHolder – item_ranking.xml.
- RankingAdapter chứa danh sách người chơi trong thuộc tính listPlayer, nên Player kết tập vào RankingAdapter, bội số n ở phía Player.

Chức năng dùng lại các lớp PlayerApiDAO, PlayerRestController, PlayerDAO, MatchDAO với Chơi online. Biểu đồ chỉ vẽ các phương thức chức năng này dùng.

#### 4.2.3. Biểu đồ tuần tự chi tiết

1. Người chơi nhấn nút Bảng xếp hạng trên MainActivity.
2. Hàm onClick() của MainActivity gọi hàm startActivity() mở RankingActivity.
3. Hàm onCreate() của RankingActivity gọi hàm getPlayer() của SessionDAO.
4. Hàm getPlayer() gọi lớp Player để đóng gói người chơi đang đăng nhập.
5. Lớp Player trả đối tượng Player cho hàm getPlayer().
6. Hàm getPlayer() trả đối tượng Player cho hàm onCreate().
7. Hàm onCreate() gọi hàm loadRanking() của chính nó.
8. Hàm loadRanking() mở một Thread và gọi hàm getRanking() của PlayerApiDAO.
9. PlayerApiDAO gửi yêu cầu GET /api/players/ranking tới hàm getRanking() của PlayerRestController.
10. Hàm getRanking() của PlayerRestController gọi hàm getRanking() của PlayerDAO.
11. Hàm getRanking() của PlayerDAO gọi hàm findFinished() của MatchDAO.
12. Hàm findFinished() truy vấn các trận đã kết thúc kèm 2 người chơi, và gọi lớp Match để đóng gói từng trận.
13. Lớp Match trả đối tượng Match cho hàm findFinished().
14. Hàm findFinished() trả danh sách trận cho hàm getRanking().
15. Hàm getRanking() truy vấn các tài khoản có vai trò người chơi và gọi lớp Player để đóng gói từng người.
16. Lớp Player trả đối tượng Player cho hàm getRanking().
17. Với mỗi trận, hàm getRanking() gọi hàm getPoints() của Match để cộng điểm trận vào tổng điểm của 2 người.
18. Match trả về điểm trận.
19. Hàm getRanking() gọi hàm getResult() của Match để biết người thắng trận.
20. Match trả về kết quả trận.
21. Với trận thắng, hàm getRanking() gọi hàm getFinishTime() của Match.
22. Match trả về thời gian làm bài của người thắng.
23. Hàm getRanking() tính trung bình điểm đối thủ, trung bình thời gian thắng của từng người, rồi sắp xếp theo 3 tiêu chí.
24. Hàm getRanking() của PlayerDAO trả danh sách người chơi đã xếp hạng cho PlayerRestController.
25. Hàm getRanking() của PlayerRestController trả danh sách dạng JSON cho PlayerApiDAO.
26. Với mỗi phần tử JSON, hàm toPlayer() của PlayerApiDAO gọi lớp Player để đóng gói.
27. Lớp Player trả đối tượng Player cho PlayerApiDAO.
28. PlayerApiDAO trả danh sách người chơi cho hàm loadRanking().
29. Trong hàm runOnUiThread(), hàm loadRanking() tạo RankingAdapter với danh sách và id của người xem, rồi gắn vào danh sách.
30. RankingAdapter hiển thị bảng xếp hạng, dòng của người xem được tô nổi bật.
31. Người chơi nhấn Back.
32. RankingActivity gọi hàm finish(), quay về MainActivity.
33. MainActivity hiển thị giao diện chính cho người chơi.

@tuan-tu-thiet-ke-uc04.png

Hình 13: Biểu đồ tuần tự chi tiết chức năng Xem bảng xếp hạng.

- Biểu đồ có 33 thông điệp.
- Lời gọi REST duy nhất là bước 9. Server đi theo khuôn PlayerRestController → PlayerDAO → MatchDAO → Match, Player.
- Các tiêu chí không lưu trong CSDL mà được tính lại ở bước 17–23 bằng getPoints(), getResult(), getFinishTime() của Match. Đây là cùng luật dùng khi kết thúc trận, nên bảng xếp hạng luôn khớp với kết quả các trận.
- Lời gọi mạng chạy trong Thread (bước 8). Phần vẽ giao diện chạy trong hàm runOnUiThread() (bước 29).

### 4.3. Chức năng "Đăng nhập, đăng ký"

#### 4.3.1. Thiết kế giao diện

@giao-dien-tai-khoan.png

Hình 14: Thiết kế giao diện chức năng Đăng nhập, đăng ký.

Chức năng có 2 màn: LoginActivity là màn mở đầu của ứng dụng; RegisterActivity mở từ nút Đăng ký và tự đóng khi đăng ký thành công. Ô mật khẩu dùng inputType textPassword.

Bảng thành phần:

| Màn | id trong layout | Loại View | Chức năng | Tên ở pha phân tích |
|---|---|---|---|---|
| LoginActivity | edtUsername | EditText | Tên đăng nhập | inUsername |
| | edtPassword | EditText (textPassword) | Mật khẩu | inPassword |
| | btnLogin | Button | Đăng nhập | subLogin |
| | btnRegister | Button | Mở màn Đăng ký | subRegister |
| RegisterActivity | edtUsername | EditText | Tên đăng nhập | inUsername |
| | edtPassword | EditText (textPassword) | Mật khẩu | inPassword |
| | edtConfirmPassword | EditText (textPassword) | Nhập lại mật khẩu | inConfirmPassword |
| | edtDisplayName | EditText | Tên hiển thị | inDisplayName |
| | btnRegister | Button | Đăng ký | subRegister |

#### 4.3.2. Biểu đồ lớp chi tiết

@lop-thiet-ke-tai-khoan.png

Hình 15: Biểu đồ lớp chi tiết chức năng Đăng nhập, đăng ký.

Package "Client (Android)".

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | LoginActivity | onCreate: SessionDAO.getPlayer khác rỗng thì gọi openHome ngay. onClick: nút Đăng ký mở RegisterActivity; nút Đăng nhập kiểm tra 2 ô rồi gọi login. login gọi PlayerApiDAO.login trong Thread, lưu phiên bằng SessionDAO.save, lưu bản sao bằng PlayerDAO.saveOrUpdate, rồi openHome. openHome mở AdminActivity nếu role là admin, ngược lại mở MainActivity |
| Boundary | RegisterActivity | onClick kiểm tra ô trống và mật khẩu nhập lại ngay ở app, đúng thì gọi register. register gọi PlayerApiDAO.register trong Thread; showResult hiện thông báo, thành công thì finish |
| Control | SessionDAO | Đọc / ghi / xóa SharedPreferences "session" |
| Control | DAO ← PlayerDAO | PlayerDAO phía app ghi bản sao người chơi vào tblPlayer của SQLite, để Chơi với máy chạy được khi mất mạng |
| Control | ApiDAO ← PlayerApiDAO | register gửi POST /api/auth/register; login gửi POST /api/auth/login và đóng gói JSON thành Player bằng toPlayer. Lỗi 401, 409 chuyển thành thông báo tiếng Việt |
| Entity | Player | id, username, displayName, role. App không bao giờ giữ mật khẩu |

Package "Server (Spring Boot)".

| Loại | Lớp | Mô tả |
|---|---|---|
| Boundary | AuthRestController | register băm mật khẩu bằng BCryptPasswordEncoder rồi gọi PlayerDAO.insert; trùng tên đăng nhập thì trả 409. login gọi PlayerDAO.findByUsername và so mật khẩu bằng BCrypt; sai thì trả 401 |
| Control | DAO ← PlayerDAO | findByUsername đọc tblPlayer theo username; insert thêm dòng mới với role = player, trả false nếu trùng khóa duy nhất |
| Entity | Player | Có passwordHash, không trả ra JSON |

Quan hệ giữa các lớp giao diện: LoginActivity – RegisterActivity, LoginActivity – MainActivity nối bằng liên kết; mỗi Activity nối với layout của nó. Đường HTTP/JSON duy nhất là PlayerApiDAO – AuthRestController.

#### 4.3.3. Biểu đồ tuần tự chi tiết

Đoạn A: Đăng ký

1. Người chơi nhấn nút Đăng ký trên LoginActivity.
2. Hàm onClick() của LoginActivity gọi hàm startActivity() mở RegisterActivity.
3. Hàm onCreate() của RegisterActivity hiển thị giao diện đăng ký cho người chơi.
4. Người chơi nhập thông tin và nhấn Đăng ký.
5. Hàm onClick() kiểm tra các ô không bỏ trống và mật khẩu nhập lại khớp, rồi gọi hàm register() của chính nó.
6. Hàm register() mở một Thread và gọi hàm register() của PlayerApiDAO.
7. PlayerApiDAO gửi yêu cầu POST /api/auth/register tới hàm register() của AuthRestController.
8. Hàm register() gọi lớp Player để tạo người chơi mới, mật khẩu được băm bằng BCrypt.
9. Lớp Player trả đối tượng Player cho hàm register().
10. Hàm register() gọi hàm insert() của PlayerDAO.
11. Hàm insert() thêm một dòng vào bảng tblPlayer với vai trò người chơi.
12. Hàm insert() trả kết quả cho hàm register().
13. Hàm register() trả mã 201 cho PlayerApiDAO.
14. PlayerApiDAO trả về cho hàm register() của RegisterActivity.
15. Trong hàm runOnUiThread(), hàm register() gọi hàm showResult() của chính nó.
16. RegisterActivity hiển thị thông báo đăng ký thành công cho người chơi.
17. Hàm showResult() gọi hàm finish(), quay về LoginActivity.
18. LoginActivity hiển thị giao diện đăng nhập cho người chơi.

@tuan-tu-thiet-ke-tai-khoan-a.png

Hình 16: Biểu đồ tuần tự chi tiết chức năng Đăng nhập, đăng ký, đoạn A: Đăng ký.

- Biểu đồ có 18 thông điệp.
- Lời gọi REST duy nhất là bước 7. Server đi theo khuôn AuthRestController → PlayerDAO → Player.
- Mật khẩu được băm ở server (bước 8) trước khi lưu; app chỉ gửi mật khẩu qua HTTPS và không lưu lại.
- Kiểm tra ô trống và mật khẩu nhập lại chạy ở app (bước 5), nên lỗi nhập liệu không tốn một lần gọi server.

Đoạn B: Đăng nhập

1. Người chơi mở ứng dụng, LoginActivity được mở.
2. Hàm onCreate() của LoginActivity gọi hàm getPlayer() của SessionDAO.
3. Hàm getPlayer() trả về rỗng cho hàm onCreate() vì chưa có phiên đăng nhập.
4. LoginActivity hiển thị giao diện đăng nhập cho người chơi.
5. Người chơi nhập tên đăng nhập, mật khẩu và nhấn Đăng nhập.
6. Hàm onClick() kiểm tra 2 ô không bỏ trống, rồi gọi hàm login() của chính nó.
7. Hàm login() mở một Thread và gọi hàm login() của PlayerApiDAO.
8. PlayerApiDAO gửi yêu cầu POST /api/auth/login tới hàm login() của AuthRestController.
9. Hàm login() gọi hàm findByUsername() của PlayerDAO.
10. Hàm findByUsername() truy vấn bảng tblPlayer và gọi lớp Player để đóng gói kết quả.
11. Lớp Player trả đối tượng Player cho hàm findByUsername().
12. Hàm findByUsername() trả đối tượng Player cho hàm login().
13. Hàm login() so mật khẩu nhận được với passwordHash bằng BCrypt.
14. Hàm login() trả người chơi, không kèm passwordHash, dạng JSON cho PlayerApiDAO.
15. Hàm toPlayer() của PlayerApiDAO gọi lớp Player để đóng gói dữ liệu JSON.
16. Lớp Player trả đối tượng Player cho PlayerApiDAO.
17. PlayerApiDAO trả đối tượng Player cho hàm login() của LoginActivity.
18. Hàm login() gọi hàm save() của SessionDAO để ghi nhớ phiên đăng nhập.
19. Hàm login() gọi hàm saveOrUpdate() của PlayerDAO phía app để lưu bản sao người chơi vào SQLite.
20. Trong hàm runOnUiThread(), hàm login() gọi hàm openHome() của chính nó.
21. Hàm openHome() thấy vai trò là người chơi, gọi hàm startActivity() mở MainActivity và đóng LoginActivity.
22. MainActivity hiển thị giao diện chính cho người chơi.

@tuan-tu-thiet-ke-tai-khoan-b.png

Hình 17: Biểu đồ tuần tự chi tiết chức năng Đăng nhập, đăng ký, đoạn B: Đăng nhập.

- Biểu đồ có 22 thông điệp.
- So mật khẩu bằng BCrypt ở server (bước 13); server không bao giờ trả passwordHash về app.
- Phiên được ghi bằng SessionDAO (bước 18); bản sao người chơi được ghi vào SQLite bằng PlayerDAO phía app (bước 19), để Chơi với máy chạy được khi mất mạng.
- Hàm openHome() (bước 20, 21) rẽ nhánh theo vai trò: người chơi vào MainActivity, quản trị viên vào AdminActivity.

---

## Phụ lục: các bước phân tích

### A. Lớp thực thể pha phân tích

@../chung/lop-thuc-the-phan-tich.png

Hình 18: Biểu đồ lớp thực thể pha phân tích toàn hệ thống.

### B. Biểu đồ lớp pha phân tích

@lop-phan-tich-uc03.png

Hình 19: Biểu đồ lớp pha phân tích chức năng Chơi online.

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

Hình 20: Biểu đồ lớp pha phân tích chức năng Xem bảng xếp hạng.

- Chức năng có 2 lớp View nối nhau bằng quan hệ liên kết: MainView mở RankingView; RankingView gọi getRanking() của Player.
- Player dùng 3 phương thức của Match để tính các tiêu chí. Bảng xếp hạng vì vậy dùng cùng luật thắng thua với trận đấu, không viết lại luật ở nơi khác.
- Các tiêu chí xếp hạng là thuộc tính dẫn xuất của Player, được tính khi xem chứ không lưu.

@lop-phan-tich-tai-khoan.png

Hình 21: Biểu đồ lớp pha phân tích chức năng Đăng nhập, đăng ký.

- 3 lớp View nối nhau bằng quan hệ liên kết: LoginView mở RegisterView và MainView.
- Cả 4 phương thức đều gán cho Player vì đầu ra hoặc đầu vào của chúng chỉ là người chơi.
- Vai trò (role) của Player quyết định giao diện mở ra sau khi đăng nhập.

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

@tuan-tu-phan-tich-uc03a.png

Hình 22: Biểu đồ tuần tự pha phân tích Chơi online, đoạn A.

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

@tuan-tu-phan-tich-uc03b.png

Hình 23: Biểu đồ tuần tự pha phân tích Chơi online, đoạn B.

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

@tuan-tu-phan-tich-uc03c.png

Hình 24: Biểu đồ tuần tự pha phân tích Chơi online, đoạn C.

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

@tuan-tu-phan-tich-uc03d.png

Hình 25: Biểu đồ tuần tự pha phân tích Chơi online, đoạn D.

Xem bảng xếp hạng

1. Người chơi nhấn nút Bảng xếp hạng trên MainView.
2. MainView gọi RankingView.
3. RankingView gọi hàm getRanking() của lớp Player.
4. Lớp Player gọi hàm getPoints() của lớp Match cho từng trận đã kết thúc.
5. Lớp Match trả về điểm trận của người chơi cho lớp Player.
6. Lớp Player gọi hàm getResult() của lớp Match để biết các trận thắng.
7. Lớp Match trả về kết quả trận cho lớp Player.
8. Lớp Player gọi hàm getFinishTime() của lớp Match cho các trận thắng.
9. Lớp Match trả về thời gian làm bài cho lớp Player.
10. Lớp Player tính 3 tiêu chí, sắp xếp và trả danh sách người chơi cho RankingView.
11. RankingView hiển thị bảng xếp hạng cho người chơi.
12. Người chơi nhấn Back trên RankingView.
13. RankingView gọi MainView.
14. MainView hiển thị giao diện chính cho người chơi.

@tuan-tu-phan-tich-uc04.png

Hình 26: Biểu đồ tuần tự pha phân tích Xem bảng xếp hạng.

Đăng nhập, đăng ký, đoạn A: Đăng ký

1. Người chơi nhấn nút Đăng ký trên LoginView.
2. LoginView gọi RegisterView.
3. RegisterView hiển thị giao diện đăng ký cho người chơi.
4. Người chơi nhập tên đăng nhập, mật khẩu, mật khẩu nhập lại, tên hiển thị và nhấn Đăng ký trên RegisterView.
5. RegisterView gọi hàm register() của lớp Player.
6. Lớp Player tạo tài khoản mới và trả về cho RegisterView.
7. RegisterView hiển thị thông báo đăng ký thành công cho người chơi.
8. RegisterView gọi LoginView.
9. LoginView hiển thị giao diện đăng nhập cho người chơi.

@tuan-tu-phan-tich-tai-khoan-a.png

Hình 27: Biểu đồ tuần tự pha phân tích Đăng nhập, đăng ký, đoạn A.

Đăng nhập, đăng ký, đoạn B: Đăng nhập

1. Khi người chơi mở ứng dụng, LoginView gọi hàm getSession() của lớp Player để kiểm tra phiên đăng nhập đã ghi nhớ.
2. Lớp Player trả về cho LoginView: chưa có phiên đăng nhập.
3. LoginView hiển thị giao diện đăng nhập cho người chơi.
4. Người chơi nhập tên đăng nhập, mật khẩu và nhấn Đăng nhập trên LoginView.
5. LoginView gọi hàm checkLogin() của lớp Player.
6. Lớp Player kiểm tra tài khoản và trả về người chơi cho LoginView.
7. LoginView gọi hàm saveSession() của lớp Player để ghi nhớ phiên đăng nhập.
8. Lớp Player ghi nhớ phiên và trả về cho LoginView.
9. LoginView gọi MainView.
10. MainView hiển thị giao diện chính cho người chơi.

@tuan-tu-phan-tich-tai-khoan-b.png

Hình 28: Biểu đồ tuần tự pha phân tích Đăng nhập, đăng ký, đoạn B.
