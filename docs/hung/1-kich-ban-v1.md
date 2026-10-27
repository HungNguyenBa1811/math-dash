# USE CASE CHI TIẾT VÀ KỊCH BẢN v1 — CHƠI ONLINE, XEM LỊCH SỬ ĐẤU, QUẢN LÝ BẠN BÈ

**Dữ liệu mẫu:**

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

---

## 1. Chức năng "Chơi online"

### 1.1. Biểu đồ use case chi tiết module Đấu đối kháng

![Hình 5: Biểu đồ use case chi tiết module Đấu đối kháng](uc-chi-tiet-doi-khang.png)

*Hình 5: Biểu đồ use case chi tiết module Đấu đối kháng.*

Người chơi khởi phát trực tiếp use case "Chơi online" (UC03) của module. UC03 được phân rã như sau:
- **include "Xem danh sách người chơi online"**: danh sách tên, tổng điểm, trạng thái, tự cập nhật.
- **include "Vào trận đấu"**: use case trừu tượng (chữ nghiêng), có 2 use case con kế thừa:
  - "Gửi lời thách đấu": bấm vào tên một người đang rỗi và chờ người đó trả lời.
  - "Nhận lời thách đấu": đồng ý lời mời của người khác.
- **"Từ chối lời thách đấu" extend "Nhận lời thách đấu"**: người được mời có thể từ chối.
- **include "Thi đấu"**: làm đề 10 câu, nộp bài, nhận kết quả và điểm trận.
- **"Thoát trận" extend "Thi đấu"**: người chơi thoát giữa trận và bị xử thua.
- **"Chơi tiếp" extend "Thi đấu"**: sau mỗi trận, cả 2 chọn chơi tiếp thì vào trận mới.

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

### 1.2. Mô tả chi tiết chức năng "Chơi online"

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

### 1.3. Kịch bản UC03 "Chơi online"

| | |
|---|---|
| **Use case** | Chơi online (UC03), gồm Xem danh sách người chơi online, Gửi lời thách đấu, Nhận lời thách đấu, Thi đấu, Chơi tiếp |
| **Actor** | Người chơi: Hùng ở máy 1 là người mời, Hoàng ở máy 2 là người được mời; mỗi người là actor trên máy của mình |
| **Tiền điều kiện** | Cả 2 người chơi đã đăng nhập, máy có kết nối mạng, đang ở giao diện chính |
| **Hậu điều kiện** | Trận đấu được lưu trên server cùng đề, số câu đúng và thời gian nộp bài của 2 người; tổng điểm của 2 người được cập nhật; cả 2 thấy cùng một kết quả |

Kịch bản chuẩn chia 4 đoạn, khớp với các use case con:
- A. Gửi lời thách đấu (máy 1).
- B. Nhận lời thách đấu (máy 2).
- C. Thi đấu (máy 1; máy 2 diễn ra tương tự).
- D. Chơi tiếp.

#### Kịch bản chuẩn

**A. Gửi lời thách đấu**

1. Hùng đăng nhập trên máy 1. Giao diện chính hiện lên gồm:

   | Thành phần | Giá trị |
   |---|---|
   | Dòng thông tin | HUNG · 12,5 điểm |
   | Danh sách người chơi online | Bảng dưới đây |
   | Nút | **Chơi với máy**, **Bảng xếp hạng**, **Đăng xuất** |

   | Tên | Tổng điểm | Trạng thái |
   |---|---|---|
   | MINH | 15 | Bận |
   | HOANG | 9 | Rỗi |
   | LAN | 4,5 | Rỗi |

2. Hùng bấm vào dòng HOANG.
3. Hệ thống thấy HOANG đang rỗi, gửi lời mời tới máy 2. Máy 1 hiện hộp thoại "Đang chờ HOANG trả lời..." với nút **Hủy**.

**B. Nhận lời thách đấu**

4. Trên máy 2, Hoàng đang ở giao diện chính. Hộp thoại "HUNG (12,5 điểm) thách đấu bạn" hiện lên với 2 nút **Đồng ý**, **Từ chối**.
5. Hoàng nhấn **Đồng ý**.
6. Server kiểm tra HUNG và HOANG vẫn online và đang rỗi. Server sinh đề 10 câu như bảng dữ liệu mẫu, tạo trận số 37 bắt đầu lúc 20:15:03,0, chuyển HUNG và HOANG sang Bận, rồi gửi đề cho cả 2 máy.
7. Máy 2 chuyển sang giao diện thi đấu. Cùng lúc, máy 1 đóng hộp thoại chờ và chuyển sang giao diện thi đấu. Trên máy của Lan, HUNG và HOANG chuyển sang Bận.

**C. Thi đấu**

8. Giao diện thi đấu trên máy 1 gồm:

   | Thành phần | Giá trị |
   |---|---|
   | Ô thời gian | 60 |
   | Trạng thái đối thủ | HOANG đang làm bài |
   | Danh sách câu hỏi | 10 câu như bảng dữ liệu mẫu, mỗi câu 4 nút chọn, chưa câu nào được chọn |
   | Nút | **Nộp bài**, **Thoát** |

   Máy 2 hiện cùng đề, dòng trạng thái là "HUNG đang làm bài".
9. Hùng lần lượt chọn 19, 54, 27, 56, 61, 33, 84, 85, 45, 90. Ô thời gian đếm ngược mỗi giây.
10. Khi ô thời gian còn 22, Hùng nhấn **Nộp bài**.
11. Server nhận bài làm của HUNG lúc 20:15:41,2, tức thời gian làm bài 38,2 giây, chấm đúng 10/10 câu. Máy 1 khóa danh sách câu, ẩn nút Nộp bài, hiện "Đã nộp bài, đang chờ đối thủ...". Trên máy 2, dòng trạng thái đổi thành "HUNG đã nộp bài".
12. Hoàng nộp bài trên máy 2. Server nhận lúc 20:15:48,6, tức 45,6 giây, đúng 10/10 câu.
13. Server xác định kết quả: không ai bỏ trận, cả 2 đúng hết, HUNG nộp nhanh hơn nên HUNG thắng. HUNG được 1 điểm, tổng điểm thành 13,5; HOANG được 0 điểm, tổng điểm giữ 9. Server lưu trận và báo cả 2 máy.
14. Giao diện kết quả hiện lên trên máy 1 gồm:

    | Thành phần | Giá trị |
    |---|---|
    | Dòng kết quả | **THẮNG!** (màu xanh) |
    | Bạn | 10/10 câu đúng · 38,2 giây |
    | HOANG | 10/10 câu đúng · 45,6 giây |
    | Điểm | +1 điểm · Tổng điểm: 13,5 |
    | Câu hỏi | Chơi tiếp? |
    | Nút | **Chơi tiếp**, **Dừng** |

    Máy 2 hiện **THUA!** (màu đỏ), +0 điểm, tổng điểm 9.

**D. Chơi tiếp**

15. Hùng nhấn **Chơi tiếp**. Máy 1 ẩn 2 nút, hiện "Đang chờ đối thủ...".
16. Hoàng nhấn **Chơi tiếp** trên máy 2.
17. Server sinh đề mới, câu đầu là 23 + 48, tạo trận số 38 và gửi đề cho cả 2 máy.
18. Cả 2 máy hiện giao diện thi đấu của trận 38, ô thời gian 60. Trận diễn ra như các bước 8–14.

#### Kịch bản ngoại lệ

| Bước | Tình huống ngoại lệ và cách xử lý |
|---|---|
| 2 | Hùng bấm vào dòng MINH. MINH đang Bận nên hệ thống báo "MINH đang bận", không gửi lời mời, giữ nguyên giao diện chính. |
| 3 | **Hủy lời mời.** Hùng nhấn **Hủy** trên hộp thoại chờ. Lời mời bị hủy, máy 1 ở lại giao diện chính. Nếu Hoàng nhấn Đồng ý sau đó, server thấy lời mời đã hủy nên không tạo trận; máy 2 báo "Không bắt đầu được trận đấu". |
| 5 | **Từ chối.** Hoàng nhấn **Từ chối**. Máy 2 đóng hộp thoại. Máy 1 đóng hộp thoại chờ và báo "HOANG đã từ chối lời mời"; Hùng vẫn ở giao diện chính, có thể mời người khác. |
| 6 | Hùng tắt ứng dụng trước khi Hoàng nhấn Đồng ý. Server thấy HUNG không còn online nên không tạo trận. Máy 2 báo "Không bắt đầu được trận đấu" và ở lại giao diện chính. |
| 9–10 | **Hết giờ.** Hùng mới chọn 8 câu thì ô thời gian về 0. Hệ thống tự nộp bài; 2 câu chưa chọn tính là sai, HUNG đúng 8/10. |
| 9–10 | **Thoát trận.** Hùng nhấn **Thoát**. Hệ thống hỏi "Thoát sẽ bị xử thua?" với 2 nút **Ở lại**, **Thoát**. Hùng nhấn **Thoát**: server xử HUNG thua, HOANG thắng và được 1 điểm (tổng 10), trả cả 2 về Rỗi. Máy 1 về giao diện chính. Máy 2 báo "Đối thủ đã thoát, bạn thắng" và hiện giao diện kết quả THẮNG! chỉ có nút **Về menu**, không hỏi chơi tiếp. Nhấn **Ở lại** thì tiếp tục làm bài, ô thời gian vẫn chạy. |
| 9–12 | **Đối thủ mất kết nối.** Máy 2 mất mạng khi đang làm bài. HOANG biến khỏi danh sách online; hệ thống xử HOANG bỏ trận. Máy 1 báo "Đối thủ đã thoát, bạn thắng" như ngoại lệ trên. |
| 10 | Máy 1 không kết nối được server khi nộp bài. Hệ thống báo "Không kết nối được máy chủ, kiểm tra kết nối mạng", nút Nộp bài vẫn bấm được; ô thời gian vẫn chạy. |
| 13 | **Một người đúng hết.** Hoàng chọn 74 ở câu 7, đúng 9/10, dù nộp ở giây 30,0. HUNG đúng hết nên HUNG thắng, không xét thời gian. |
| 13 | **Hòa.** HUNG chọn 23 ở câu 3, HOANG chọn 35 ở câu 9; cả 2 đều có câu sai nên hòa. Mỗi người được 0,5 điểm: HUNG 13, HOANG 9,5. Giao diện kết quả hiện **HÒA!** (màu vàng). |
| 15–16 | **Dừng chơi.** Hoàng nhấn **Dừng**. Server trả HUNG và HOANG về Rỗi. Máy 2 về giao diện chính. Máy 1 báo "Đối thủ đã dừng chơi" và về giao diện chính. |

---

## 2. Chức năng "Xem lịch sử đấu"

### 2.1. Biểu đồ use case chi tiết module Xem lịch sử đấu

![Hình 6: Biểu đồ use case chi tiết module Xem lịch sử đấu](uc-chi-tiet-lich-su-dau.png)

*Hình 6: Biểu đồ use case chi tiết module Xem lịch sử đấu.*

Người chơi khởi phát trực tiếp use case "Xem lịch sử đấu" (UC04). UC04 được phân rã như sau:
- **include "Xem trận của tôi"**: thẻ được mở sẵn, gồm biểu đồ tròn tỉ lệ thắng / hòa / thua và danh sách các trận đã đấu.
- **"Xem chi tiết trận" extend "Xem trận của tôi"**: người chơi bấm vào một trận để xem lại đề, đáp án và kết quả của trận đó.
- **"Xem bảng xếp hạng" extend "Xem lịch sử đấu"**: người chơi chọn thẻ Bảng xếp hạng để xem thứ hạng mọi người chơi.

| Use case | Mô tả |
|---|---|
| Xem lịch sử đấu (UC04) | Xem kết quả các trận chơi online mình đã đấu và thứ hạng của mọi người chơi |
| Xem trận của tôi | Xem tỉ lệ thắng / hòa / thua dạng biểu đồ tròn và danh sách các trận đã kết thúc, trận mới nhất đứng đầu |
| Xem chi tiết trận | Xem đối thủ, kết quả, điểm trận, số câu đúng và thời gian làm bài của 2 bên, đề 10 câu kèm đáp án đúng của một trận |
| Xem bảng xếp hạng | Xem thứ hạng mọi người chơi theo tổng điểm, trung bình điểm đối thủ, trung bình thời gian thắng |


### 2.2. Mô tả chi tiết chức năng "Xem lịch sử đấu"

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


### 2.3. Kịch bản UC04 "Xem lịch sử đấu"

| | |
|---|---|
| **Use case** | Xem lịch sử đấu (UC04), gồm Xem trận của tôi, Xem chi tiết trận, Xem bảng xếp hạng |
| **Actor** | Người chơi (Hùng) |
| **Tiền điều kiện** | Hùng đã đăng nhập, máy có kết nối mạng, đang ở giao diện chính; Hùng đã đấu 20 trận online, trận gần nhất là trận số 37 |
| **Hậu điều kiện** | Hùng thấy tỉ lệ thắng / hòa / thua, các trận đã đấu, chi tiết một trận và thứ hạng của mọi người chơi; dữ liệu không thay đổi |

Kịch bản chuẩn chia 2 đoạn, khớp với các use case con:
- A. Xem trận của tôi và chi tiết trận.
- B. Xem bảng xếp hạng.

#### Kịch bản chuẩn

**A. Xem trận của tôi và chi tiết trận**

1. Ở giao diện chính (dòng "HUNG · 13,5 điểm"), Hùng nhấn nút **Lịch sử đấu**.
2. Hệ thống lấy 20 trận đã kết thúc của HUNG, trận mới nhất đứng đầu. Giao diện lịch sử đấu hiện lên, thẻ **Trận của tôi** được mở sẵn, gồm:

   | Thành phần | Giá trị |
   |---|---|
   | Thanh tiêu đề | Lịch sử đấu, nút Back |
   | Thẻ | **Trận của tôi** (đang chọn), Bảng xếp hạng |
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
   | Dòng kết quả | **THẮNG!** (màu xanh) · +1 điểm |
   | Bạn · HUNG | 10/10 câu đúng · 38,2 giây |
   | HOANG | 10/10 câu đúng · 45,6 giây |
   | Đề bài và đáp án | 10 câu như bảng dữ liệu mẫu: 12 + 7 = 19, 9 × 6 = 54, …, 6 × 15 = 90 |

5. Hùng xem xong và nhấn Back.
6. Giao diện lịch sử đấu hiện lại ở thẻ Trận của tôi, danh sách giữ nguyên vị trí.

**B. Xem bảng xếp hạng**

7. Hùng chọn thẻ **Bảng xếp hạng**.
8. Hệ thống lấy mọi người chơi và các trận đã kết thúc, tính 3 tiêu chí rồi sắp xếp. Thẻ Bảng xếp hạng hiện danh sách:

   | Hạng | Tên | Tổng điểm | TB điểm đối thủ | TB thời gian thắng |
   |---|---|---|---|---|
   | 1 | MINH | 15 | 10,5 | 41,3 giây |
   | 2 | **HUNG** (dòng nổi bật) | 13,5 | 11 | 39,8 giây |
   | 3 | LAN | 13,5 | 9 | 44,0 giây |
   | 4 | HOANG | 9 | 13,5 | 47,2 giây |
   | 5 | AN | 4,5 | 12 | 49,0 giây |
   | 6 | NAM | 4,5 | 12 | 52,1 giây |
   | 7 | TUAN | 0 | 13,5 | — |

   - HUNG và LAN cùng 13,5 điểm, HUNG đã gặp các đối thủ mạnh hơn (11 so với 9) nên đứng trên.
   - AN và NAM trùng cả 2 tiêu chí đầu, AN thắng nhanh hơn nên đứng trên.
9. Hùng xem danh sách rồi nhấn Back.
10. Giao diện chính hiện lại.

#### Kịch bản ngoại lệ

| Bước | Tình huống ngoại lệ và cách xử lý |
|---|---|
| 2 | **Chưa đấu trận nào.** TUAN vừa đăng ký, mở Lịch sử đấu. Biểu đồ tròn để trống, chú giải ghi "Thắng 0 · Hòa 0 · Thua 0 · 0 trận", danh sách hiện "Bạn chưa đấu trận nào". |
| 2 | Máy mất mạng hoặc server không phản hồi. Hệ thống báo "Không tải được lịch sử đấu, kiểm tra kết nối mạng", biểu đồ và danh sách để trống; Hùng nhấn Back để về giao diện chính. |
| 2 | **Trận có người bỏ trận.** Ở trận #28, HOANG nhấn Thoát giữa trận nên bị xử thua. Dòng của trận ghi "THẮNG · Đối thủ bỏ trận"; trên máy của Hoàng, dòng này ghi "THUA · Bạn bỏ trận". |
| 4 | Máy mất mạng khi Hùng bấm vào trận #37. Hệ thống báo "Không tải được chi tiết trận, kiểm tra kết nối mạng" và quay lại giao diện lịch sử đấu. |
| 8 | Máy mất mạng khi Hùng chọn thẻ Bảng xếp hạng. Hệ thống báo "Không tải được bảng xếp hạng, kiểm tra kết nối mạng", danh sách để trống; Hùng chọn lại thẻ để tải lại. |

---

## 3. Chức năng "Quản lý bạn bè"

**Dữ liệu mẫu:**

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

### 3.1. Biểu đồ use case chi tiết module Bạn bè

![Hình 7: Biểu đồ use case chi tiết module Bạn bè](uc-chi-tiet-ban-be.png)

*Hình 7: Biểu đồ use case chi tiết module Bạn bè.*

Người chơi khởi phát trực tiếp use case "Quản lý bạn bè" (UC07). UC07 được phân rã như sau:
- **include "Xem danh sách bạn bè"**: mỗi lần mở giao diện bạn bè, hệ thống luôn hiện lời mời đã nhận và danh sách bạn bè kèm trạng thái online.
- **"Gửi lời mời kết bạn" extend "Quản lý bạn bè"**: người chơi nhập tên đăng nhập của một người và gửi lời mời.
- **"Đồng ý lời mời kết bạn" extend "Quản lý bạn bè"**: người nhận đồng ý, hai người thành bạn bè.
- **"Từ chối lời mời kết bạn" extend "Quản lý bạn bè"**: người nhận từ chối, lời mời bị xóa.
- **"Hủy kết bạn" extend "Quản lý bạn bè"**: một trong hai người xóa quan hệ bạn bè; người gửi cũng dùng nó để rút lại lời mời chưa được trả lời.

| Use case | Mô tả |
|---|---|
| Quản lý bạn bè (UC07) | Mở giao diện bạn bè để xem, gửi, trả lời lời mời kết bạn và hủy kết bạn |
| Xem danh sách bạn bè | Hiển thị lời mời đã nhận, các bạn bè kèm tổng điểm và trạng thái Online / Offline, các lời mời mình đã gửi đang chờ trả lời |
| Gửi lời mời kết bạn | Người chơi nhập tên đăng nhập của người khác; hệ thống tạo lời mời đang chờ |
| Đồng ý lời mời kết bạn | Người nhận đồng ý; hai người trở thành bạn bè |
| Từ chối lời mời kết bạn | Người nhận từ chối; lời mời bị xóa |
| Hủy kết bạn | Xóa quan hệ bạn bè, hoặc rút lại lời mời mình đã gửi |

### 3.2. Mô tả chi tiết chức năng "Quản lý bạn bè"

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

### 3.3. Kịch bản UC07 "Quản lý bạn bè"

| | |
|---|---|
| **Use case** | Quản lý bạn bè (UC07), gồm Xem danh sách bạn bè, Gửi lời mời kết bạn, Đồng ý lời mời kết bạn |
| **Actor** | Người chơi: Hùng ở máy 1 là người gửi lời mời, Hoàng ở máy 2 là người nhận; mỗi người là actor trên máy của mình |
| **Tiền điều kiện** | Cả 2 người chơi đã đăng nhập, máy có kết nối mạng, đang ở giao diện chính; giữa HUNG và HOANG chưa có lời mời hay quan hệ bạn bè nào |
| **Hậu điều kiện** | Server lưu quan hệ bạn bè giữa HUNG và HOANG; mỗi người thấy người kia trong danh sách bạn bè |

Kịch bản chuẩn chia 2 đoạn, khớp với các use case con:
- A. Gửi lời mời kết bạn (máy 1).
- B. Đồng ý lời mời kết bạn (máy 2).

#### Kịch bản chuẩn

**A. Gửi lời mời kết bạn**

1. Ở giao diện chính trên máy 1 (dòng "HUNG · 13,5 điểm"), Hùng nhấn nút **Bạn bè**.
2. Hệ thống lấy lời mời và quan hệ bạn bè của HUNG, cùng danh sách người chơi đang online. Giao diện bạn bè hiện lên gồm:

   | Thành phần | Giá trị |
   |---|---|
   | Ô tên đăng nhập | Trống |
   | Nút | **Kết bạn** |
   | Lời mời kết bạn (1) | LAN, nút **Đồng ý**, nút **Từ chối** |
   | Bạn bè (2) | Bảng dưới đây |

   | Trạng thái | Tên | Tổng điểm | Nút |
   |---|---|---|---|
   | ● Online (xanh) | MINH | 15 | × |
   | ● Offline (xám) | TUAN | 0 | × |

3. Hùng nhập "hoang" vào ô tên đăng nhập và nhấn **Kết bạn**.
4. Hệ thống tìm thấy người chơi HOANG, thấy giữa HUNG và HOANG chưa có lời mời hay quan hệ nào, tạo lời mời kết bạn đang chờ lúc 21:40 ngày 27/10/2026. Máy 1 thông báo "Đã gửi lời mời kết bạn tới HOANG", xóa trống ô tên đăng nhập. Mục Bạn bè đổi thành:

   | Trạng thái | Tên | Tổng điểm | Nút |
   |---|---|---|---|
   | ● Online (xanh) | MINH | 15 | × |
   | ● Offline (xám) | TUAN | 0 | × |
   | ● (xám) | HOANG | Đang chờ trả lời | × |

**B. Đồng ý lời mời kết bạn**

5. Trên máy 2, Hoàng nhấn nút **Bạn bè** ở giao diện chính. Giao diện bạn bè hiện lên gồm:

   | Thành phần | Giá trị |
   |---|---|
   | Ô tên đăng nhập | Trống |
   | Nút | **Kết bạn** |
   | Lời mời kết bạn (1) | HUNG, nút **Đồng ý**, nút **Từ chối** |
   | Bạn bè (0) | "Chưa có bạn bè" |

6. Hoàng nhấn **Đồng ý** ở dòng HUNG.
7. Hệ thống thấy lời mời vẫn đang chờ và HOANG là người nhận, ghi nhận HUNG và HOANG là bạn bè. Trên máy 2, mục Lời mời kết bạn ẩn đi; mục Bạn bè (1) có một dòng: ● Online (xanh) · HUNG · 13,5 điểm · ×.
8. Lần sau Hùng mở giao diện bạn bè trên máy 1, dòng HOANG trong mục Bạn bè đổi thành ● Online (xanh) · HOANG · 9 điểm · ×.

#### Kịch bản ngoại lệ

| Bước | Tình huống ngoại lệ và cách xử lý |
|---|---|
| 2 | **Từ chối.** Hùng nhấn **Từ chối** ở dòng LAN. Hệ thống xóa lời mời của LAN; mục Lời mời kết bạn ẩn đi. LAN không nhận thông báo nào. |
| 2 | **Hủy kết bạn.** Hùng nhấn × ở dòng TUAN. Hệ thống hỏi "Hủy kết bạn với TUAN?" với 2 nút **Không**, **Hủy kết bạn**. Hùng nhấn **Hủy kết bạn**: hệ thống xóa quan hệ, mục Bạn bè còn MINH. Nhấn **Không** thì giữ nguyên. |
| 2 | Máy 1 không kết nối được server. Hệ thống báo "Không kết nối được máy chủ, kiểm tra kết nối mạng", 2 mục để trống; Hùng nhấn Back để về giao diện chính. |
| 3 | Hùng nhấn **Kết bạn** khi ô tên đăng nhập trống. Hệ thống báo "Vui lòng nhập tên đăng nhập", không gửi gì. |
| 4 | Hùng nhập "hoangg". Không có người chơi nào có tên đăng nhập này, hệ thống báo "Không tìm thấy người chơi", giữ nguyên nội dung ô để Hùng sửa. |
| 4 | Hùng nhập "hung". Hệ thống báo "Không thể kết bạn với chính mình". |
| 4 | Hùng nhập "minh" (đã là bạn) hoặc "lan" (LAN đã gửi lời mời cho HUNG). Hệ thống báo "Đã có lời mời hoặc đã là bạn bè", không tạo lời mời mới. Muốn kết bạn với LAN thì Hùng nhấn **Đồng ý** ở mục Lời mời kết bạn. |
| 4–6 | **Rút lời mời.** Trước khi Hoàng trả lời, Hùng nhấn × ở dòng "HOANG · Đang chờ trả lời" và xác nhận. Hệ thống xóa lời mời. Hoàng nhấn **Đồng ý** sau đó thì máy 2 báo "Lời mời không còn" và dòng HUNG biến khỏi mục Lời mời kết bạn. |
| 6 | **Từ chối.** Hoàng nhấn **Từ chối** ở dòng HUNG. Hệ thống xóa lời mời. Lần sau Hùng mở giao diện bạn bè, dòng "HOANG · Đang chờ trả lời" không còn. |
