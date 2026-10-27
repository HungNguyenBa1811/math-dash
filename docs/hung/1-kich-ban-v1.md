# USE CASE CHI TIẾT VÀ KỊCH BẢN v1 — CHƠI ONLINE, XEM LỊCH SỬ ĐẤU, QUẢN LÝ BẠN BÈ

**Dữ liệu mẫu:**

| Người chơi | Tên đăng nhập | Tên hiển thị | Tổng điểm | Trạng thái | Máy |
|---|---|---|---|---|---|
| Hùng | hung | HUNG | 12,5 | Rỗi | Máy 1, người mời |
| Hoàng | hoang | HOANG | 9 | Rỗi | Máy 2, người được mời |
| Minh | minh | MINH | 15 | Bận | Máy 3, đang đấu với người khác |
| Lan | lan | LAN | 4,5 | Rỗi | Máy 4 |

Trận số 37 bắt đầu lúc 20:15:03,0 ngày 25/10/2026. Đề bài server sinh cho trận 37:

| Câu | Phép tính | Ô A | Ô B | Ô C | Ô D | Đáp án đúng |
|---|---|---|---|---|---|---|
| 1 | 12 + 7 | 17 | 19 | 21 | 18 | B · 19 |
| 2 | 9 × 6 | 48 | 54 | 56 | 63 | B · 54 |
| 3 | 45 − 18 | 27 | 23 | 33 | 37 | A · 27 |
| 4 | 8 × 7 | 54 | 56 | 48 | 64 | B · 56 |
| 5 | 36 + 25 | 51 | 61 | 59 | 71 | B · 61 |
| 6 | 72 − 39 | 33 | 43 | 31 | 37 | A · 33 |
| 7 | 7 × 12 | 74 | 84 | 82 | 96 | B · 84 |
| 8 | 58 + 27 | 75 | 85 | 83 | 95 | B · 85 |
| 9 | 91 − 46 | 45 | 55 | 35 | 47 | A · 45 |
| 10 | 6 × 15 | 80 | 90 | 96 | 75 | B · 90 |

Bài làm của HUNG và HOANG trong trận 37. Thời gian làm câu tính từ lúc câu hiện ra tới lúc người chơi bấm chọn:

| Câu | HUNG chọn | Thời gian | HOANG chọn | Thời gian |
|---|---|---|---|---|
| 1 | 19 | 3,1 giây | 19 | 4,0 giây |
| 2 | 54 | 4,0 giây | 54 | 4,4 giây |
| 3 | 27 | 3,4 giây | 27 | 4,0 giây |
| 4 | 56 | 3,8 giây | 56 | 4,7 giây |
| 5 | 61 | 3,7 giây | 61 | 4,3 giây |
| 6 | 33 | 3,5 giây | 33 | 4,1 giây |
| 7 | 84 | 4,1 giây | 84 | 4,9 giây |
| 8 | 85 | 3,2 giây | 85 | 3,8 giây |
| 9 | 45 | 3,6 giây | 45 | 4,4 giây |
| 10 | 90 | 2,8 giây | 90 | 3,7 giây |
| Tổng | 10/10 câu đúng | 35,2 giây | 10/10 câu đúng | 42,3 giây |

HUNG nhấn Nộp bài ở giây 38,2, HOANG ở giây 45,6, tính từ lúc trận bắt đầu.

---

## 1. Chức năng "Chơi online"

### 1.1. Biểu đồ use case chi tiết module Đấu đối kháng

![Hình 5: Biểu đồ use case chi tiết module Đấu đối kháng](uc-chi-tiet-doi-khang.png)

*Hình 5: Biểu đồ use case chi tiết module Đấu đối kháng.*

Người chơi khởi phát trực tiếp use case "Chơi online" (UC03) của module. Mỗi use case con ứng với giao diện mà người chơi tương tác:
- **include "Đăng nhập"**: người chơi phải đăng nhập trước khi chơi online; use case này thuộc module Tài khoản.
- **include "Thách đấu"**: ở giao diện chơi online, người chơi xem danh sách người chơi online và bấm vào tên một người đang rỗi để mời; người được mời đồng ý hoặc từ chối trên hộp thoại lời mời.
- **include "Thi đấu"**: ở giao diện thi đấu, 2 người làm lần lượt 10 câu và nộp bài; giao diện kết quả hiện kết quả, điểm trận và hỏi chơi tiếp.

Các thao tác Hủy lời mời, Từ chối, Thoát trận, Chơi tiếp, Dừng là bước trong 2 use case con trên, không phải use case riêng.

| Use case | Mô tả |
|---|---|
| Chơi online (UC03) | Vào giao diện chơi online, thách đấu một người chơi online, thi đấu trên cùng đề do server sinh, nhận kết quả do server xác định |
| Đăng nhập | Người chơi nhập tên đăng nhập, mật khẩu để vào hệ thống |
| Thách đấu | Người chơi chọn một người đang rỗi trong danh sách online và gửi lời mời; người được mời đồng ý thì server sinh đề, tạo trận và đưa cả 2 vào trận, từ chối thì người mời nhận thông báo |
| Thi đấu | Hai người làm cùng đề 10 câu, mỗi lần một câu, trong tối đa 60 giây, nộp bài; server lưu bài làm từng câu, chấm, xác định kết quả, cộng điểm; sau trận hai người chọn chơi tiếp hoặc dừng |

### 1.2. Mô tả chi tiết chức năng "Chơi online"

Người chơi đăng nhập vào hệ thống → Giao diện chính xuất hiện với: chữ MATH DASH, lời chào "Xin chào, <tên>", 2 nút Chơi với máy, Chơi online, nút biểu tượng Đăng xuất ở góc phải trên → Người chơi nhấn Chơi online → Hệ thống ghi người chơi vào danh sách online với trạng thái Rỗi → Giao diện chơi online xuất hiện với: tiêu đề Chơi online, 3 nút biểu tượng ở góc phải trên là Bạn bè, Lịch sử đấu, Bảng xếp hạng, thẻ tên và tổng điểm của mình, danh sách những người khác đang ở giao diện chơi online xếp theo tổng điểm giảm dần, mỗi dòng gồm: tên, tổng điểm, trạng thái Rỗi màu xanh hoặc Bận màu xám, danh sách tự cập nhật khi có người vào, người rời hoặc đổi trạng thái → Người chơi bấm vào tên một người đang rỗi → Hệ thống gửi lời mời tới máy người đó và hiện hộp thoại "Đang chờ <tên> trả lời..." với nút Hủy → Máy người được mời hiện hộp thoại "<tên> (<tổng điểm> điểm) thách đấu bạn" với 2 nút Đồng ý, Từ chối → Người được mời nhấn Đồng ý → Server kiểm tra cả 2 vẫn online và rỗi, sinh đề 10 câu, tạo trận, chuyển cả 2 sang Bận và gửi đề cho cả 2 máy → Giao diện thi đấu xuất hiện trên cả 2 máy, mỗi lần một câu, với: ô thời gian đếm ngược từ 01:00, dòng trạng thái "<tên đối thủ> đang làm bài", dòng "Câu 1/10", phép tính chữ lớn, 4 ô phương án xếp 2 × 2 là A màu đỏ, B màu xanh dương, C màu vàng, D màu xanh lá, nút Thoát → Người chơi bấm một ô → Hệ thống ghi phương án đã chọn và thời gian làm câu tính từ lúc câu hiện ra, chưa báo đúng hay sai, rồi hiện câu tiếp theo → Người chơi làm lần lượt tới câu thứ 10 → Giao diện ẩn 4 ô, hiện dòng "Đã làm xong 10 câu" và nút Nộp bài → Người chơi nhấn Nộp bài → Server ghi thời điểm nộp, lưu bài làm từng câu và chấm bài → Giao diện hiện "Đã nộp bài, đang chờ đối thủ...", máy đối thủ đổi dòng trạng thái thành "<tên> đã nộp bài" → Khi cả 2 đã nộp, server xác định kết quả, cộng điểm trận, cập nhật tổng điểm và báo cả 2 máy → Giao diện kết quả hiện đè lên giao diện thi đấu với: dòng THẮNG! màu xanh, THUA! màu đỏ hoặc HÒA! màu vàng, số câu đúng và thời gian làm bài của 2 người, điểm trận được cộng và tổng điểm mới, câu hỏi "Chơi tiếp?" với 2 nút Chơi tiếp, Dừng → Người chơi nhấn Chơi tiếp → Giao diện hiện "Đang chờ đối thủ..." → Đối thủ cũng nhấn Chơi tiếp → Server sinh đề mới, tạo trận mới → Cả 2 máy vào giao diện thi đấu của trận mới.

### 1.3. Kịch bản UC03 "Chơi online"

| | |
|---|---|
| **Use case** | Chơi online (UC03), gồm Thách đấu, Thi đấu |
| **Actor** | Người chơi: Hùng ở máy 1 là người mời, Hoàng ở máy 2 là người được mời; mỗi người là actor trên máy của mình |
| **Tiền điều kiện** | Cả 2 người chơi đã đăng nhập, máy có kết nối mạng; Hùng đang ở giao diện chính trên máy 1; Hoàng đã vào giao diện chơi online trên máy 2 |
| **Hậu điều kiện** | Trận đấu được lưu trên server cùng đề, bài làm từng câu, số câu đúng và thời gian nộp bài của 2 người; tổng điểm của 2 người được cập nhật; cả 2 thấy cùng một kết quả |

Kịch bản chuẩn chia 4 đoạn; đoạn A, B thuộc use case con Thách đấu, đoạn C, D thuộc use case con Thi đấu:
- A. Gửi lời thách đấu (máy 1).
- B. Nhận lời thách đấu (máy 2).
- C. Thi đấu (máy 1; máy 2 diễn ra tương tự).
- D. Chơi tiếp.

#### Kịch bản chuẩn

**A. Gửi lời thách đấu**

1. Trên máy 1, giao diện chính của Hùng gồm:

   | Thành phần | Giá trị |
   |---|---|
   | Tiêu đề | MATH DASH |
   | Lời chào | Xin chào, HUNG |
   | Nút | **Chơi với máy**, **Chơi online** |
   | Góc phải trên | Nút Đăng xuất dạng biểu tượng |

2. Hùng nhấn **Chơi online**.
3. Hệ thống ghi HUNG vào danh sách online với trạng thái Rỗi. Giao diện chơi online hiện lên gồm:

   | Thành phần | Giá trị |
   |---|---|
   | Thanh trên | Tiêu đề CHƠI ONLINE; góc phải trên có 3 nút biểu tượng **Bạn bè**, **Lịch sử đấu**, **Bảng xếp hạng** |
   | Thẻ thông tin | HUNG · 12,5 điểm |
   | Mục Người chơi online | Bảng dưới đây |

   | Tên | Tổng điểm | Trạng thái |
   |---|---|---|
   | MINH | 15 | Bận |
   | HOANG | 9 | Rỗi |
   | LAN | 4,5 | Rỗi |

   Trên máy 2 và máy 4, dòng HUNG · 12,5 · Rỗi xuất hiện trong danh sách.
4. Hùng bấm vào dòng HOANG.
5. Hệ thống thấy HOANG đang rỗi, gửi lời mời tới máy 2. Máy 1 hiện hộp thoại "Đang chờ HOANG trả lời..." với nút **Hủy**.

**B. Nhận lời thách đấu**

6. Trên máy 2, Hoàng đang ở giao diện chơi online. Hộp thoại "HUNG (12,5 điểm) thách đấu bạn" hiện lên với 2 nút **Đồng ý**, **Từ chối**.
7. Hoàng nhấn **Đồng ý**.
8. Server kiểm tra HUNG và HOANG vẫn online và đang rỗi. Server sinh đề 10 câu như bảng dữ liệu mẫu, tạo trận số 37 bắt đầu lúc 20:15:03,0, chuyển HUNG và HOANG sang Bận, rồi gửi đề cho cả 2 máy.
9. Máy 2 chuyển sang giao diện thi đấu. Cùng lúc, máy 1 đóng hộp thoại chờ và chuyển sang giao diện thi đấu. Trên máy của Lan, HUNG và HOANG chuyển sang Bận.

**C. Thi đấu**

10. Giao diện thi đấu trên máy 1 hiện câu 1, gồm:

    | Thành phần | Giá trị |
    |---|---|
    | Ô thời gian | 01:00, đếm ngược mỗi giây |
    | Trạng thái đối thủ | HOANG đang làm bài |
    | Số thứ tự câu | Câu 1/10 |
    | Phép tính | 12 + 7 = ? |
    | 4 ô phương án | **A** 17 (đỏ), **B** 19 (xanh dương), **C** 21 (vàng), **D** 18 (xanh lá) |
    | Nút | **Thoát** |

    Máy 2 hiện cùng câu 1, dòng trạng thái là "HUNG đang làm bài".
11. Sau 3,1 giây, Hùng bấm ô **B** 19.
12. Hệ thống ghi câu 1 của HUNG: phương án 19, thời gian 3,1 giây. Ô B giữ nguyên, 3 ô còn lại mờ đi, không báo đúng hay sai. Câu 2 hiện ra: "Câu 2/10", "9 × 6 = ?", **A** 48, **B** 54, **C** 56, **D** 63.
13. Các bước 11–12 lặp lại cho đến câu thứ 10. Hùng lần lượt chọn 54, 27, 56, 61, 33, 84, 85, 45, 90, thời gian từng câu như bảng bài làm ở đầu tài liệu.
14. Sau khi Hùng chọn ở câu 10, thẻ câu hỏi chỉ còn dòng "Đã làm xong 10 câu", 4 ô ẩn đi, nút **Nộp bài** hiện cạnh nút **Thoát**. Ô thời gian còn 00:22.
15. Hùng nhấn **Nộp bài**.
16. Máy 1 hiện "Đang nộp bài...". Server nhận bài làm của HUNG lúc 20:15:41,2, tức thời gian làm bài 38,2 giây, lưu phương án và thời gian làm từng câu, chấm đúng 10/10 câu. Máy 1 ẩn nút Nộp bài, hiện "Đã nộp bài, đang chờ đối thủ...". Trên máy 2, dòng trạng thái đổi thành "HUNG đã nộp bài".
17. Hoàng làm xong 10 câu và nộp bài trên máy 2. Server nhận lúc 20:15:48,6, tức 45,6 giây, lưu bài làm từng câu, đúng 10/10 câu.
18. Server xác định kết quả: không ai bỏ trận, cả 2 đúng hết, HUNG nộp nhanh hơn nên HUNG thắng. HUNG được 1 điểm, tổng điểm thành 13,5; HOANG được 0 điểm, tổng điểm giữ 9. Server lưu trận và báo cả 2 máy.
19. Giao diện kết quả hiện lên trên máy 1 gồm:

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

20. Hùng nhấn **Chơi tiếp**. Máy 1 ẩn 2 nút, hiện "Đang chờ đối thủ...".
21. Hoàng nhấn **Chơi tiếp** trên máy 2.
22. Server sinh đề mới, câu đầu là 23 + 48, tạo trận số 38 và gửi đề cho cả 2 máy.
23. Cả 2 máy hiện giao diện thi đấu của trận 38: ô thời gian 01:00, "Câu 1/10", "23 + 48 = ?". Trận diễn ra như các bước 10–19.

#### Kịch bản ngoại lệ

| Bước | Tình huống ngoại lệ và cách xử lý |
|---|---|
| 3 | **Không có ai khác online.** Chỉ có HUNG ở giao diện chơi online. Mục Người chơi online hiện "Chưa có ai khác online"; danh sách tự hiện người chơi khi có người vào. |
| 3 | **Rời giao diện chơi online.** Hùng nhấn Back. Hệ thống xóa HUNG khỏi danh sách online; máy 1 về giao diện chính. Trên máy 2 và máy 4, dòng HUNG biến khỏi danh sách; không ai thách đấu được HUNG cho tới khi Hùng nhấn lại **Chơi online**. |
| 4 | Hùng bấm vào dòng MINH. MINH đang Bận nên hệ thống báo "MINH đang bận", không gửi lời mời, giữ nguyên giao diện chơi online. |
| 5 | **Hủy lời mời.** Hùng nhấn **Hủy** trên hộp thoại chờ. Lời mời bị hủy, máy 1 ở lại giao diện chơi online. Nếu Hoàng nhấn Đồng ý sau đó, server thấy lời mời đã hủy nên không tạo trận; máy 2 báo "Không bắt đầu được trận đấu". |
| 7 | **Từ chối.** Hoàng nhấn **Từ chối**. Máy 2 đóng hộp thoại. Máy 1 đóng hộp thoại chờ và báo "HOANG đã từ chối lời mời"; Hùng vẫn ở giao diện chơi online, có thể mời người khác. |
| 8 | Hùng rời giao diện chơi online hoặc tắt ứng dụng trước khi Hoàng nhấn Đồng ý. Server thấy HUNG không còn online nên không tạo trận. Máy 2 báo "Không bắt đầu được trận đấu" và ở lại giao diện chơi online. |
| 11–15 | **Hết giờ.** Hùng mới làm 8 câu thì ô thời gian về 00:00. Thẻ câu hỏi hiện "Hết giờ", hệ thống tự nộp bài; câu 9 và câu 10 ghi là bỏ trống và tính là sai, HUNG đúng 8/10. Nếu Hùng đã làm xong 10 câu nhưng chưa nhấn Nộp bài thì hệ thống cũng tự nộp đủ 10 câu. |
| 11–15 | **Thoát trận.** Hùng nhấn **Thoát**. Hệ thống hỏi "Thoát sẽ bị xử thua?" với 2 nút **Ở lại**, **Thoát**. Hùng nhấn **Thoát**: server xử HUNG thua, HOANG thắng và được 1 điểm (tổng 10), trả cả 2 về Rỗi. Máy 1 về giao diện chơi online. Máy 2 báo "Đối thủ đã thoát, bạn thắng" và hiện giao diện kết quả THẮNG! chỉ có nút **Về menu**, không hỏi chơi tiếp. Nhấn **Ở lại** thì tiếp tục làm bài ở câu đang làm, ô thời gian vẫn chạy. |
| 11–17 | **Đối thủ mất kết nối.** Máy 2 mất mạng khi đang làm bài. HOANG biến khỏi danh sách online; hệ thống xử HOANG bỏ trận. Máy 1 báo "Đối thủ đã thoát, bạn thắng" như ngoại lệ trên. |
| 16 | Máy 1 không kết nối được server khi nộp bài. Hệ thống báo "Không kết nối được máy chủ, kiểm tra kết nối mạng", nút Nộp bài hiện lại để Hùng nộp lại; ô thời gian vẫn chạy. |
| 18 | **Một người đúng hết.** Hoàng chọn ô **A** 74 ở câu 7, đúng 9/10, dù nộp ở giây 30,0. HUNG đúng hết nên HUNG thắng, không xét thời gian. |
| 18 | **Hòa.** HUNG chọn ô **B** 23 ở câu 3, HOANG chọn ô **C** 35 ở câu 9; cả 2 đều có câu sai nên hòa. Mỗi người được 0,5 điểm: HUNG 13, HOANG 9,5. Giao diện kết quả hiện **HÒA!** (màu vàng). |
| 20–21 | **Dừng chơi.** Hoàng nhấn **Dừng**. Server trả HUNG và HOANG về Rỗi. Máy 2 về giao diện chơi online. Máy 1 báo "Đối thủ đã dừng chơi" và về giao diện chơi online. |

---

## 2. Chức năng "Xem lịch sử đấu"

### 2.1. Biểu đồ use case chi tiết module Xem lịch sử đấu

![Hình 6: Biểu đồ use case chi tiết module Xem lịch sử đấu](uc-chi-tiet-lich-su-dau.png)

*Hình 6: Biểu đồ use case chi tiết module Xem lịch sử đấu.*

Người chơi khởi phát trực tiếp use case "Xem lịch sử đấu" (UC04). Mỗi use case con ứng với giao diện mà người chơi tương tác:
- **include "Đăng nhập"**: người chơi phải đăng nhập trước khi xem lịch sử đấu; use case này thuộc module Tài khoản.
- **"Xem chi tiết trận" extend "Xem lịch sử đấu"**: ở giao diện lịch sử đấu, người chơi có thể bấm vào một trận để mở giao diện chi tiết trận.
- **"Xem bảng xếp hạng" extend "Xem lịch sử đấu"**: người chơi có thể mở thẻ Bảng xếp hạng, bằng nút Bảng xếp hạng ở giao diện chơi online hoặc chọn thẻ khi đang xem lịch sử đấu.

| Use case | Mô tả |
|---|---|
| Xem lịch sử đấu (UC04) | Xem tỉ lệ thắng / hòa / thua dạng biểu đồ tròn và danh sách các trận chơi online mình đã đấu, trận mới nhất đứng đầu |
| Đăng nhập | Người chơi nhập tên đăng nhập, mật khẩu để vào hệ thống |
| Xem chi tiết trận | Xem đối thủ, kết quả, điểm trận, số câu đúng và thời gian làm bài của 2 bên; 10 câu của đề, mỗi câu có phép tính kèm đáp án đúng và bài làm của từng người: đúng / sai, phương án đã chọn, thời gian làm câu |
| Xem bảng xếp hạng | Xem thứ hạng mọi người chơi theo tổng điểm, trung bình điểm đối thủ, trung bình thời gian thắng |

### 2.2. Mô tả chi tiết chức năng "Xem lịch sử đấu"

Người chơi đăng nhập vào hệ thống → Giao diện chính xuất hiện với 2 nút Chơi với máy, Chơi online → Người chơi nhấn Chơi online → Giao diện chơi online xuất hiện với: 3 nút biểu tượng Bạn bè, Lịch sử đấu, Bảng xếp hạng ở góc phải trên, thẻ tên và tổng điểm của mình, danh sách người chơi online → Người chơi nhấn nút Lịch sử đấu → Hệ thống lấy các trận chơi online đã kết thúc mà người chơi tham gia, trận mới nhất đứng đầu → Giao diện lịch sử đấu xuất hiện với: tiêu đề Lịch sử đấu, 2 thẻ Trận của tôi và Bảng xếp hạng, thẻ Trận của tôi mở sẵn gồm biểu đồ tròn chia 3 phần Thắng màu xanh lá, Hòa màu vàng, Thua màu đỏ, giữa biểu đồ ghi tỉ lệ thắng, bên cạnh ghi số trận thắng, hòa, thua và tổng số trận, dưới là danh sách trận, mỗi dòng gồm: dải màu theo kết quả, tên đối thủ, số trận, THẮNG / HÒA / THUA, điểm trận, số câu đúng của mình và của đối thủ, thời điểm kết thúc → Người chơi bấm vào một trận → Hệ thống lấy trận kèm đề, đáp án đúng và bài làm từng câu của 2 người → Giao diện chi tiết trận xuất hiện với: tiêu đề Chi tiết trận, dòng "Trận #<số trận> · <ngày giờ kết thúc>", dòng kết quả THẮNG! / THUA! / HÒA! và điểm trận, khung so sánh tên, số câu đúng, thời gian làm bài của mình và của đối thủ, mục Từng câu gồm hàng tiêu đề cột #, Câu, tên mình, tên đối thủ và 10 dòng, mỗi dòng gồm: số thứ tự, phép tính kèm đáp án đúng, với mỗi người chơi một ô ghi ✓ màu xanh hoặc ✗ màu đỏ kèm phương án đã chọn và thời gian làm câu, câu bỏ trống ghi "Bỏ trống" → Người chơi nhấn Back → Giao diện lịch sử đấu hiện lại → Người chơi chọn thẻ Bảng xếp hạng, hoặc từ giao diện chơi online nhấn nút Bảng xếp hạng để mở thẳng thẻ này → Hệ thống lấy mọi người chơi và các trận đã kết thúc, xếp hạng theo tổng điểm giảm dần, cùng tổng điểm thì theo trung bình tổng điểm các đối thủ đã gặp giảm dần, cùng cả 2 thì theo trung bình thời gian làm bài trong các trận thắng tăng dần, người chưa thắng trận nào ghi "—" và đứng sau → Thẻ Bảng xếp hạng hiện danh sách mọi người chơi, mỗi dòng gồm: hạng, tên, tổng điểm, trung bình điểm đối thủ, trung bình thời gian thắng, dòng của chính người chơi được tô nổi bật → Người chơi nhấn Back → Hệ thống quay về giao diện chơi online.

### 2.3. Kịch bản UC04 "Xem lịch sử đấu"

| | |
|---|---|
| **Use case** | Xem lịch sử đấu (UC04), gồm Xem chi tiết trận, Xem bảng xếp hạng |
| **Actor** | Người chơi (Hùng) |
| **Tiền điều kiện** | Hùng đã đăng nhập, máy có kết nối mạng, đang ở giao diện chơi online; Hùng đã đấu 20 trận online, trận gần nhất là trận số 37 |
| **Hậu điều kiện** | Hùng thấy tỉ lệ thắng / hòa / thua, các trận đã đấu, chi tiết một trận và thứ hạng của mọi người chơi; dữ liệu không thay đổi |

Kịch bản chuẩn chia 2 đoạn; đoạn A gồm phần chính của use case và use case con Xem chi tiết trận, đoạn B là use case con Xem bảng xếp hạng:
- A. Xem trận của tôi và chi tiết trận.
- B. Xem bảng xếp hạng.

#### Kịch bản chuẩn

**A. Xem trận của tôi và chi tiết trận**

1. Ở giao diện chơi online (thẻ thông tin "HUNG · 13,5 điểm"), Hùng nhấn nút biểu tượng **Lịch sử đấu** ở góc phải trên.
2. Hệ thống lấy 20 trận đã kết thúc của HUNG, trận mới nhất đứng đầu. Giao diện lịch sử đấu hiện lên, thẻ **Trận của tôi** được mở sẵn, gồm:

   | Thành phần | Giá trị |
   |---|---|
   | Thanh trên | Tiêu đề LỊCH SỬ ĐẤU |
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
4. Hệ thống lấy trận 37 kèm đề, đáp án đúng và bài làm từng câu của HUNG và HOANG. Giao diện chi tiết trận hiện lên gồm:

   | Thành phần | Giá trị |
   |---|---|
   | Thanh trên | Tiêu đề CHI TIẾT TRẬN |
   | Dòng trận | Trận #37 · 25/10/2026 20:15 |
   | Dòng kết quả | **THẮNG!** (màu xanh) · +1 điểm |
   | Cột HUNG | 10/10 câu · 38,2 giây |
   | Cột HOANG | 10/10 câu · 45,6 giây |
   | Mục Từng câu | Bảng dưới đây |

   | # | Câu | HUNG | HOANG |
   |---|---|---|---|
   | 1 | 12 + 7 = 19 | ✓ 19 · 3,1 s | ✓ 19 · 4,0 s |
   | 2 | 9 × 6 = 54 | ✓ 54 · 4,0 s | ✓ 54 · 4,4 s |
   | 3 | 45 − 18 = 27 | ✓ 27 · 3,4 s | ✓ 27 · 4,0 s |
   | 4 | 8 × 7 = 56 | ✓ 56 · 3,8 s | ✓ 56 · 4,7 s |
   | 5 | 36 + 25 = 61 | ✓ 61 · 3,7 s | ✓ 61 · 4,3 s |
   | 6 | 72 − 39 = 33 | ✓ 33 · 3,5 s | ✓ 33 · 4,1 s |
   | 7 | 7 × 12 = 84 | ✓ 84 · 4,1 s | ✓ 84 · 4,9 s |
   | 8 | 58 + 27 = 85 | ✓ 85 · 3,2 s | ✓ 85 · 3,8 s |
   | 9 | 91 − 46 = 45 | ✓ 45 · 3,6 s | ✓ 45 · 4,4 s |
   | 10 | 6 × 15 = 90 | ✓ 90 · 2,8 s | ✓ 90 · 3,7 s |

   Mỗi ô bài làm hiện 2 dòng: dòng trên là dấu ✓ màu xanh kèm phương án đã chọn, dòng dưới là thời gian làm câu. Ví dụ câu 1: HUNG chọn đúng 19 sau 3,1 giây, HOANG chọn đúng 19 sau 4,0 giây.
5. Hùng xem xong và nhấn Back.
6. Giao diện lịch sử đấu hiện lại ở thẻ Trận của tôi, danh sách giữ nguyên vị trí.
7. Hùng nhấn Back.
8. Giao diện chơi online hiện lại.

**B. Xem bảng xếp hạng**

9. Ở giao diện chơi online, Hùng nhấn nút biểu tượng **Bảng xếp hạng** ở góc phải ngoài cùng.
10. Hệ thống lấy mọi người chơi và các trận đã kết thúc, tính 3 tiêu chí rồi sắp xếp. Giao diện lịch sử đấu hiện lên với thẻ **Bảng xếp hạng** được mở sẵn, thẻ Trận của tôi nằm bên cạnh để chọn qua lại. Thẻ Bảng xếp hạng hiện danh sách:

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
11. Hùng xem danh sách rồi nhấn Back.
12. Giao diện chơi online hiện lại.

#### Kịch bản ngoại lệ

| Bước | Tình huống ngoại lệ và cách xử lý |
|---|---|
| 2 | **Chưa đấu trận nào.** TUAN vừa đăng ký, mở Lịch sử đấu. Biểu đồ tròn để trống, chú giải ghi "Thắng 0 · Hòa 0 · Thua 0 · 0 trận", danh sách hiện "Bạn chưa đấu trận nào". |
| 2 | Máy mất mạng hoặc server không phản hồi. Hệ thống báo "Không tải được lịch sử đấu, kiểm tra kết nối mạng", biểu đồ và danh sách để trống; Hùng nhấn Back để về giao diện chơi online. |
| 2 | **Trận có người bỏ trận.** Ở trận #28, HOANG nhấn Thoát giữa trận nên bị xử thua. Dòng của trận ghi "THẮNG · Đối thủ bỏ trận"; trên máy của Hoàng, dòng này ghi "THUA · Bạn bỏ trận". |
| 4 | Máy mất mạng khi Hùng bấm vào trận #37. Hệ thống báo "Không tải được chi tiết trận, kiểm tra kết nối mạng"; giao diện chi tiết trận chỉ có dòng "Trận #37", các mục kết quả và từng câu để trống. Hùng nhấn Back để quay lại giao diện lịch sử đấu. |
| 4 | **Có câu sai hoặc bỏ trống.** Hùng bấm vào trận #35 với LAN, HÒA 9 – 8. Ở câu 3 của trận này, HUNG chọn sai nên ô của HUNG hiện "✗ 23" màu đỏ, dòng dưới "5,2 s". LAN chưa làm câu 10 khi hết giờ nên ô của LAN ở câu 10 hiện "Bỏ trống" màu xám. |
| 4 | **Người bỏ trận chưa nộp bài.** Hùng cuộn xuống và bấm vào trận #12 với AN. AN nhấn Thoát ở câu 4 khi chưa nộp bài nên không có bài làm: cột AN ở khung so sánh ghi "Bỏ trận", mọi ô bài làm của AN ở mục Từng câu ghi "—". |
| 10 | Máy mất mạng khi Hùng mở thẻ Bảng xếp hạng. Hệ thống báo "Không tải được bảng xếp hạng, kiểm tra kết nối mạng", danh sách để trống; Hùng chọn thẻ Trận của tôi rồi chọn lại thẻ Bảng xếp hạng để tải lại. |
| 10 | **Chọn qua lại 2 thẻ.** Đang ở thẻ Bảng xếp hạng, Hùng chọn thẻ Trận của tôi. Giao diện hiện biểu đồ tròn và danh sách trận như bước 2; Hùng bấm vào một trận để xem chi tiết như bước 3–4. |

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

Người chơi khởi phát trực tiếp use case "Quản lý bạn bè" (UC07). Cả module chỉ có một giao diện bạn bè; use case con là các việc làm thay đổi quan hệ bạn bè:
- **include "Đăng nhập"**: người chơi phải đăng nhập trước khi quản lý bạn bè; use case này thuộc module Tài khoản.
- **"Kết bạn" extend "Quản lý bạn bè"**: người chơi gửi lời mời bằng tên đăng nhập của một người; người nhận đồng ý thì 2 người thành bạn bè, từ chối thì lời mời bị xóa.
- **"Hủy kết bạn" extend "Quản lý bạn bè"**: một trong 2 người xóa quan hệ bạn bè; người gửi cũng rút lại lời mời chưa được trả lời theo cách này.

Đồng ý, Từ chối là bước của use case Kết bạn, không phải use case riêng.

| Use case | Mô tả |
|---|---|
| Quản lý bạn bè (UC07) | Mở giao diện bạn bè để xem lời mời đã nhận, các bạn bè kèm tổng điểm và trạng thái Online / Offline, các lời mời mình đã gửi |
| Đăng nhập | Người chơi nhập tên đăng nhập, mật khẩu để vào hệ thống |
| Kết bạn | Người chơi gửi lời mời bằng tên đăng nhập; người nhận đồng ý thì hai người trở thành bạn bè, từ chối thì lời mời bị xóa |
| Hủy kết bạn | Xóa quan hệ bạn bè, hoặc rút lại lời mời mình đã gửi |

### 3.2. Mô tả chi tiết chức năng "Quản lý bạn bè"

Người chơi đăng nhập vào hệ thống → Giao diện chính xuất hiện với 2 nút Chơi với máy, Chơi online → Người chơi nhấn Chơi online → Giao diện chơi online xuất hiện với: 3 nút biểu tượng Bạn bè, Lịch sử đấu, Bảng xếp hạng ở góc phải trên, thẻ tên và tổng điểm của mình, danh sách người chơi online → Người chơi nhấn nút Bạn bè → Hệ thống lấy các lời mời kết bạn, các quan hệ bạn bè của người chơi và danh sách người đang online → Giao diện bạn bè xuất hiện với: tiêu đề Bạn bè, ô nhập tên đăng nhập và nút Kết bạn, mục "Lời mời kết bạn (số lời mời)" chỉ hiện khi có lời mời đã nhận, mỗi dòng gồm tên người gửi, nút Đồng ý, nút Từ chối, mục "Bạn bè (số người)" mỗi dòng gồm chấm trạng thái xanh Online hoặc xám Offline, tên, tổng điểm, nút Hủy kết bạn dạng biểu tượng ×, các lời mời mình đã gửi nằm cuối mục với chữ xám "Đang chờ trả lời", trạng thái Online / Offline tự cập nhật → Người chơi nhập tên đăng nhập của một người và nhấn Kết bạn → Hệ thống tìm người chơi theo tên đăng nhập, kiểm tra giữa 2 người chưa có lời mời hay quan hệ bạn bè nào và tạo lời mời đang chờ → Hệ thống thông báo "Đã gửi lời mời kết bạn tới <tên>", xóa trống ô tên đăng nhập và thêm dòng "<tên> · Đang chờ trả lời" vào cuối mục Bạn bè → Người nhận mở giao diện bạn bè trên máy của mình, thấy lời mời ở mục Lời mời kết bạn và nhấn Đồng ý → Hệ thống kiểm tra lời mời vẫn đang chờ, ghi nhận 2 người là bạn bè → Lời mời biến khỏi mục Lời mời kết bạn, người gửi hiện trong mục Bạn bè với tổng điểm và trạng thái → Người chơi nhấn × ở một người bạn → Hệ thống hỏi "Hủy kết bạn với <tên>?" với 2 nút Không, Hủy kết bạn → Người chơi nhấn Hủy kết bạn → Hệ thống xóa quan hệ bạn bè, dòng đó biến khỏi danh sách → Người chơi nhấn Back → Hệ thống quay về giao diện chơi online.

### 3.3. Kịch bản UC07 "Quản lý bạn bè"

| | |
|---|---|
| **Use case** | Quản lý bạn bè (UC07), gồm Kết bạn |
| **Actor** | Người chơi: Hùng ở máy 1 là người gửi lời mời, Hoàng ở máy 2 là người nhận; mỗi người là actor trên máy của mình |
| **Tiền điều kiện** | Cả 2 người chơi đã đăng nhập, máy có kết nối mạng, đang ở giao diện chơi online; giữa HUNG và HOANG chưa có lời mời hay quan hệ bạn bè nào |
| **Hậu điều kiện** | Server lưu quan hệ bạn bè giữa HUNG và HOANG; mỗi người thấy người kia trong danh sách bạn bè |

Kịch bản chuẩn chia 2 đoạn, cùng thuộc use case con Kết bạn:
- A. Gửi lời mời kết bạn (máy 1).
- B. Đồng ý lời mời kết bạn (máy 2).

#### Kịch bản chuẩn

**A. Gửi lời mời kết bạn**

1. Ở giao diện chơi online trên máy 1 (thẻ thông tin "HUNG · 13,5 điểm"), Hùng nhấn nút biểu tượng **Bạn bè** ở góc phải trên.
2. Hệ thống lấy lời mời và quan hệ bạn bè của HUNG, cùng danh sách người chơi đang online. Giao diện bạn bè hiện lên gồm:

   | Thành phần | Giá trị |
   |---|---|
   | Thanh trên | Tiêu đề BẠN BÈ |
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

5. Trên máy 2, Hoàng nhấn nút biểu tượng **Bạn bè** ở góc phải trên giao diện chơi online. Giao diện bạn bè hiện lên gồm:

   | Thành phần | Giá trị |
   |---|---|
   | Thanh trên | Tiêu đề BẠN BÈ |
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
| 2 | Máy 1 không kết nối được server. Hệ thống báo "Không kết nối được máy chủ, kiểm tra kết nối mạng", 2 mục để trống; Hùng nhấn Back để về giao diện chơi online. |
| 3 | Hùng nhấn **Kết bạn** khi ô tên đăng nhập trống. Hệ thống báo "Vui lòng nhập tên đăng nhập", không gửi gì. |
| 4 | Hùng nhập "hoangg". Không có người chơi nào có tên đăng nhập này, hệ thống báo "Không tìm thấy người chơi", giữ nguyên nội dung ô để Hùng sửa. |
| 4 | Hùng nhập "hung". Hệ thống báo "Không thể kết bạn với chính mình". |
| 4 | Hùng nhập "minh" (đã là bạn) hoặc "lan" (LAN đã gửi lời mời cho HUNG). Hệ thống báo "Đã có lời mời hoặc đã là bạn bè", không tạo lời mời mới. Muốn kết bạn với LAN thì Hùng nhấn **Đồng ý** ở mục Lời mời kết bạn. |
| 4–6 | **Rút lời mời.** Trước khi Hoàng trả lời, Hùng nhấn × ở dòng "HOANG · Đang chờ trả lời" và xác nhận. Hệ thống xóa lời mời. Hoàng nhấn **Đồng ý** sau đó thì máy 2 báo "Lời mời không còn" và dòng HUNG biến khỏi mục Lời mời kết bạn. |
| 6 | **Từ chối.** Hoàng nhấn **Từ chối** ở dòng HUNG. Hệ thống xóa lời mời. Lần sau Hùng mở giao diện bạn bè, dòng "HOANG · Đang chờ trả lời" không còn. |
