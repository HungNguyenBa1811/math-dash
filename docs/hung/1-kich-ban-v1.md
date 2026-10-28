# USE CASE CHI TIẾT VÀ KỊCH BẢN v1 — CHƠI ONLINE, XEM LỊCH SỬ ĐẤU

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
| **Tiền điều kiện** | Cả 2 người chơi đã đăng nhập, máy có kết nối mạng; Hùng đang ở giao diện chính trên máy 1; Hoàng đã vào giao diện chơi online trên máy 2; MINH đang đấu với người khác trên máy 3 |
| **Hậu điều kiện** | Trận 37 được lưu trên server cùng đề, bài làm từng câu, số câu đúng và thời gian nộp bài của 2 người; tổng điểm của HUNG thành 13,5, của HOANG giữ 9; cả 2 thấy cùng một kết quả; trận 38 bắt đầu |
| **Sự kiện chính** | 1. Trên máy 1, giao diện chính của Hùng hiện lên gồm: tiêu đề MATH DASH; lời chào "Xin chào, HUNG"; 2 nút **Chơi với máy**, **Chơi online**; nút Đăng xuất dạng biểu tượng ở góc phải trên.<br>2. Hùng nhấn **Chơi online**.<br>3. Hệ thống ghi HUNG vào danh sách online với trạng thái Rỗi. Giao diện chơi online hiện lên gồm: thanh trên có tiêu đề CHƠI ONLINE và 3 nút biểu tượng **Bạn bè**, **Lịch sử đấu**, **Bảng xếp hạng** ở góc phải trên; thẻ thông tin HUNG · 12,5 điểm; mục Người chơi online gồm 3 dòng MINH · 15 · Bận, HOANG · 9 · Rỗi, LAN · 4,5 · Rỗi. Trên máy 2 và máy 4, dòng HUNG · 12,5 · Rỗi xuất hiện trong danh sách.<br>4. Hùng bấm vào dòng HOANG.<br>5. Hệ thống thấy HOANG đang rỗi, gửi lời mời tới máy 2. Máy 1 hiện hộp thoại "Đang chờ HOANG trả lời..." với nút **Hủy**.<br>6. Trên máy 2, ở giao diện chơi online của Hoàng, hộp thoại "HUNG (12,5 điểm) thách đấu bạn" hiện lên với 2 nút **Đồng ý**, **Từ chối**.<br>7. Hoàng nhấn **Đồng ý**.<br>8. Server kiểm tra HUNG và HOANG vẫn online và đang rỗi. Server sinh đề 10 câu như bảng đề bài trận 37 ở phần Dữ liệu mẫu, tạo trận số 37 bắt đầu lúc 20:15:03,0 ngày 25/10/2026, chuyển HUNG và HOANG sang Bận, rồi gửi đề cho cả 2 máy.<br>9. Máy 2 chuyển sang giao diện thi đấu. Cùng lúc, máy 1 đóng hộp thoại chờ và chuyển sang giao diện thi đấu. Trên máy của Lan, HUNG và HOANG chuyển sang Bận.<br>10. Giao diện thi đấu trên máy 1 hiện câu 1, gồm: ô thời gian 01:00 đếm ngược mỗi giây; dòng trạng thái "HOANG đang làm bài"; dòng "Câu 1/10"; phép tính "12 + 7 = ?"; 4 ô phương án **A** 17 màu đỏ, **B** 19 màu xanh dương, **C** 21 màu vàng, **D** 18 màu xanh lá; nút **Thoát**. Máy 2 hiện cùng câu 1, dòng trạng thái là "HUNG đang làm bài".<br>11. Sau 3,1 giây, Hùng bấm ô **B** 19.<br>12. Hệ thống ghi câu 1 của HUNG: phương án 19, thời gian 3,1 giây. Ô B giữ nguyên, 3 ô còn lại mờ đi, không báo đúng hay sai. Câu 2 hiện ra: "Câu 2/10", "9 × 6 = ?", **A** 48, **B** 54, **C** 56, **D** 63.<br>13. Các bước 11–12 lặp lại tới câu 10. Hùng lần lượt chọn 54, 27, 56, 61, 33, 84, 85, 45, 90, thời gian từng câu như bảng bài làm trận 37 ở phần Dữ liệu mẫu.<br>14. Sau khi Hùng chọn ở câu 10, thẻ câu hỏi chỉ còn dòng "Đã làm xong 10 câu", 4 ô ẩn đi, nút **Nộp bài** hiện cạnh nút **Thoát**. Ô thời gian còn 00:22.<br>15. Hùng nhấn **Nộp bài**.<br>16. Máy 1 hiện "Đang nộp bài...". Server nhận bài làm của HUNG lúc 20:15:41,2, tức thời gian làm bài 38,2 giây, lưu phương án và thời gian làm từng câu, chấm đúng 10/10 câu. Máy 1 ẩn nút Nộp bài, hiện "Đã nộp bài, đang chờ đối thủ...". Trên máy 2, dòng trạng thái đổi thành "HUNG đã nộp bài".<br>17. Hoàng làm xong 10 câu và nhấn **Nộp bài** trên máy 2.<br>18. Server nhận bài làm của HOANG lúc 20:15:48,6, tức 45,6 giây, lưu bài làm từng câu, chấm đúng 10/10 câu.<br>19. Server xác định kết quả: không ai bỏ trận, cả 2 đúng hết, HUNG nộp nhanh hơn nên HUNG thắng. HUNG được 1 điểm, tổng điểm thành 13,5; HOANG được 0 điểm, tổng điểm giữ 9. Server lưu trận và báo cả 2 máy.<br>20. Giao diện kết quả hiện đè lên giao diện thi đấu trên máy 1, gồm: dòng kết quả **THẮNG!** màu xanh; dòng Bạn 10/10 câu đúng · 38,2 giây; dòng HOANG 10/10 câu đúng · 45,6 giây; dòng điểm +1 điểm · Tổng điểm: 13,5; câu hỏi "Chơi tiếp?"; 2 nút **Chơi tiếp**, **Dừng**. Máy 2 hiện **THUA!** màu đỏ, +0 điểm, tổng điểm 9.<br>21. Hùng nhấn **Chơi tiếp**.<br>22. Máy 1 ẩn 2 nút, hiện "Đang chờ đối thủ...".<br>23. Hoàng nhấn **Chơi tiếp** trên máy 2.<br>24. Server sinh đề mới, câu đầu là 23 + 48, tạo trận số 38 và gửi đề cho cả 2 máy.<br>25. Cả 2 máy hiện giao diện thi đấu của trận 38: ô thời gian 01:00, "Câu 1/10", "23 + 48 = ?". Trận 38 diễn ra như các bước 10–20. |
| **Ngoại lệ** | 3a. Không có ai khác online, chỉ có HUNG ở giao diện chơi online.<br>3a.1. Mục Người chơi online hiện "Chưa có ai khác online"; danh sách tự hiện người chơi khi có người vào. Kịch bản tiếp tục từ bước 4 khi có người vào giao diện chơi online.<br>3b. Hùng nhấn Back ở giao diện chơi online.<br>3b.1. Hệ thống xóa HUNG khỏi danh sách online; máy 1 về giao diện chính. Trên máy 2 và máy 4, dòng HUNG biến khỏi danh sách; không ai thách đấu được HUNG cho tới khi Hùng nhấn lại **Chơi online**. Use case kết thúc.<br>4a. Hùng bấm vào dòng MINH.<br>4a.1. MINH đang Bận nên hệ thống báo "MINH đang bận", không gửi lời mời, giữ nguyên giao diện chơi online. Kịch bản tiếp tục từ bước 4.<br>5a. Hùng nhấn **Hủy** trên hộp thoại chờ.<br>5a.1. Lời mời bị hủy, máy 1 ở lại giao diện chơi online.<br>5a.2. Nếu Hoàng nhấn **Đồng ý** sau đó thì server thấy lời mời đã hủy nên không tạo trận; máy 2 báo "Không bắt đầu được trận đấu". Use case kết thúc.<br>7a. Hoàng nhấn **Từ chối**.<br>7a.1. Máy 2 đóng hộp thoại. Máy 1 đóng hộp thoại chờ và báo "HOANG đã từ chối lời mời"; Hùng vẫn ở giao diện chơi online, có thể mời người khác. Kịch bản tiếp tục từ bước 4.<br>8a. Hùng rời giao diện chơi online hoặc tắt ứng dụng trước khi Hoàng nhấn **Đồng ý**.<br>8a.1. Server thấy HUNG không còn online nên không tạo trận. Máy 2 báo "Không bắt đầu được trận đấu" và ở lại giao diện chơi online. Use case kết thúc.<br>11a. Hùng mới làm xong 8 câu thì ô thời gian về 00:00.<br>11a.1. Thẻ câu hỏi hiện "Hết giờ", hệ thống tự nộp bài; câu 9 và câu 10 ghi là bỏ trống và tính là sai, HUNG đúng 8/10. Kịch bản tiếp tục từ bước 16 với thời gian làm bài của HUNG là 60 giây.<br>11b. Đang làm bài ở một trong các bước 11–15, Hùng nhấn **Thoát**.<br>11b.1. Hệ thống hỏi "Thoát sẽ bị xử thua?" với 2 nút **Ở lại**, **Thoát**.<br>11b.2. Hùng nhấn **Thoát**.<br>11b.3. Server xử HUNG thua, HOANG thắng và được 1 điểm, tổng điểm thành 10, rồi trả cả 2 về Rỗi. Máy 1 về giao diện chơi online. Máy 2 báo "Đối thủ đã thoát, bạn thắng" và hiện giao diện kết quả **THẮNG!** chỉ có nút **Về menu**, không hỏi chơi tiếp. Use case kết thúc.<br>11b.4. Nếu ở bước 11b.2 Hùng nhấn **Ở lại** thì hộp thoại đóng, Hùng tiếp tục làm bài ở câu đang làm, ô thời gian vẫn chạy. Kịch bản tiếp tục từ bước 11.<br>11c. Đang ở một trong các bước 11–17, máy 2 mất mạng khi Hoàng đang làm bài.<br>11c.1. HOANG biến khỏi danh sách online; hệ thống xử HOANG bỏ trận.<br>11c.2. Máy 1 báo "Đối thủ đã thoát, bạn thắng" và hiện giao diện kết quả **THẮNG!** chỉ có nút **Về menu**, như bước 11b.3. Use case kết thúc.<br>15a. Hùng đã làm xong 10 câu nhưng chưa nhấn **Nộp bài** thì ô thời gian về 00:00.<br>15a.1. Hệ thống tự nộp đủ 10 câu. Kịch bản tiếp tục từ bước 16 với thời gian làm bài của HUNG là 60 giây.<br>16a. Máy 1 không kết nối được server khi nộp bài.<br>16a.1. Hệ thống báo "Không kết nối được máy chủ, kiểm tra kết nối mạng", nút **Nộp bài** hiện lại để Hùng nộp lại; ô thời gian vẫn chạy. Kịch bản tiếp tục từ bước 15.<br>19a. Hoàng chọn ô **A** 74 ở câu 7 nên chỉ đúng 9/10, dù nộp ở giây 30,0.<br>19a.1. HUNG đúng hết nên server xác định HUNG thắng, không xét thời gian. Kịch bản tiếp tục từ bước 20.<br>19b. HUNG chọn ô **B** 23 ở câu 3, HOANG chọn ô **C** 35 ở câu 9; cả 2 đều có câu sai.<br>19b.1. Server xác định hòa, mỗi người được 0,5 điểm: tổng điểm HUNG thành 13, HOANG thành 9,5.<br>19b.2. Giao diện kết quả hiện **HÒA!** màu vàng. Kịch bản tiếp tục từ bước 21.<br>23a. Hoàng nhấn **Dừng**.<br>23a.1. Server trả HUNG và HOANG về Rỗi. Máy 2 về giao diện chơi online. Máy 1 báo "Đối thủ đã dừng chơi" và về giao diện chơi online. Use case kết thúc. |

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

### 2.3. Kịch bản các use case của module Xem lịch sử đấu

#### 2.3.1. Xem lịch sử đấu

| | |
|---|---|
| **Use case** | Xem lịch sử đấu |
| **Actor** | Người chơi: Hùng |
| **Tiền điều kiện** | Hùng đã đăng nhập, máy có kết nối mạng, đang ở giao diện chơi online với thẻ thông tin HUNG · 13,5 điểm; Hùng đã đấu 20 trận online, trận gần nhất là trận số 37 |
| **Hậu điều kiện** | Hùng thấy tỉ lệ thắng / hòa / thua và danh sách các trận đã đấu; dữ liệu không thay đổi; giao diện chơi online hiện lại |
| **Sự kiện chính** | 1. Ở giao diện chơi online, Hùng nhấn nút biểu tượng **Lịch sử đấu** ở góc phải trên.<br>2. Hệ thống lấy 20 trận đã kết thúc của HUNG, trận mới nhất đứng đầu. Giao diện lịch sử đấu hiện lên, thẻ **Trận của tôi** được mở sẵn, gồm: thanh trên có tiêu đề LỊCH SỬ ĐẤU; 2 thẻ **Trận của tôi** đang chọn và Bảng xếp hạng; biểu đồ tròn Thắng 60%, Hòa 15%, Thua 25%, giữa biểu đồ ghi "60% thắng"; chú giải "Thắng 12 · Hòa 3 · Thua 5 · 20 trận"; danh sách trận như bảng dữ liệu mẫu danh sách trận của HUNG bên dưới, cuộn xuống để xem các trận cũ hơn. Tổng điểm trận của 20 trận là 12 × 1 + 3 × 0,5 = 13,5, khớp với tổng điểm của HUNG.<br>3. Hùng xem danh sách rồi nhấn Back.<br>4. Giao diện chơi online hiện lại. |
| **Ngoại lệ** | 2a. Người chơi chưa đấu trận nào: TUAN vừa đăng ký, mở Lịch sử đấu.<br>2a.1. Biểu đồ tròn để trống, chú giải ghi "Thắng 0 · Hòa 0 · Thua 0 · 0 trận", danh sách hiện "Bạn chưa đấu trận nào". Kịch bản tiếp tục từ bước 3.<br>2b. Máy mất mạng hoặc server không phản hồi.<br>2b.1. Hệ thống báo "Không tải được lịch sử đấu, kiểm tra kết nối mạng", biểu đồ và danh sách để trống. Kịch bản tiếp tục từ bước 3.<br>2c. Trong danh sách có trận đối thủ bỏ trận: ở trận #28, HOANG nhấn Thoát giữa trận nên bị xử thua.<br>2c.1. Dòng của trận #28 ghi "THẮNG · Đối thủ bỏ trận"; trên máy của Hoàng, dòng này ghi "THUA · Bạn bỏ trận".<br>3a. Hùng bấm vào một dòng trận.<br>3a.1. Kịch bản chuyển sang use case Xem chi tiết trận.<br>3b. Hùng chọn thẻ Bảng xếp hạng.<br>3b.1. Kịch bản chuyển sang use case Xem bảng xếp hạng, từ bước 2. |

Bảng dữ liệu mẫu danh sách trận của HUNG:

| Trận | Đối thủ | Kết quả | Điểm | Câu đúng | Kết thúc |
|---|---|---|---|---|---|
| #37 | HOANG | THẮNG | +1 | 10 – 10 | 25/10 20:15 |
| #35 | LAN | HÒA | +0,5 | 9 – 8 | 25/10 19:58 |
| #33 | MINH | THUA | +0 | 10 – 10 | 25/10 19:40 |
| #28 | HOANG | THẮNG · Đối thủ bỏ trận | +1 | 6 – 3 | 24/10 20:47 |
| … | … | … | … | … | … |

#### 2.3.2. Xem chi tiết trận

| | |
|---|---|
| **Use case** | Xem chi tiết trận |
| **Actor** | Người chơi: Hùng |
| **Tiền điều kiện** | Hùng đã đăng nhập, máy có kết nối mạng, đang ở thẻ Trận của tôi của giao diện lịch sử đấu với danh sách trận như bảng dữ liệu mẫu danh sách trận của HUNG ở use case Xem lịch sử đấu |
| **Hậu điều kiện** | Hùng thấy kết quả, điểm trận, số câu đúng, thời gian làm bài của 2 bên và bài làm từng câu của trận 37; dữ liệu không thay đổi; giao diện lịch sử đấu hiện lại |
| **Sự kiện chính** | 1. Hùng bấm vào dòng trận #37.<br>2. Hệ thống lấy trận 37 kèm đề, đáp án đúng và bài làm từng câu của HUNG và HOANG. Giao diện chi tiết trận hiện lên gồm: thanh trên có tiêu đề CHI TIẾT TRẬN; dòng "Trận #37 · 25/10/2026 20:15"; dòng kết quả **THẮNG!** màu xanh · +1 điểm; khung so sánh gồm cột HUNG 10/10 câu · 38,2 giây và cột HOANG 10/10 câu · 45,6 giây; mục Từng câu như bảng dữ liệu mẫu bài làm từng câu trận 37 bên dưới. Mỗi ô bài làm hiện 2 dòng: dòng trên là dấu ✓ màu xanh kèm phương án đã chọn, dòng dưới là thời gian làm câu. Ví dụ câu 1: HUNG chọn đúng 19 sau 3,1 giây, HOANG chọn đúng 19 sau 4,0 giây.<br>3. Hùng xem xong và nhấn Back.<br>4. Giao diện lịch sử đấu hiện lại ở thẻ Trận của tôi, danh sách giữ nguyên vị trí. |
| **Ngoại lệ** | 1a. Hùng bấm vào trận #35 với LAN, HÒA 9 – 8, có câu sai và câu bỏ trống.<br>1a.1. Giao diện chi tiết trận hiện như bước 2. Ở câu 3 của trận này, HUNG chọn sai nên ô của HUNG hiện "✗ 23" màu đỏ, dòng dưới "5,2 s". LAN chưa làm câu 10 khi hết giờ nên ô của LAN ở câu 10 hiện "Bỏ trống" màu xám. Kịch bản tiếp tục từ bước 3.<br>1b. Hùng cuộn xuống và bấm vào trận #12 với AN. AN nhấn Thoát ở câu 4 khi chưa nộp bài nên không có bài làm.<br>1b.1. Cột AN ở khung so sánh ghi "Bỏ trận", mọi ô bài làm của AN ở mục Từng câu ghi "—". Kịch bản tiếp tục từ bước 3.<br>2a. Máy mất mạng khi Hùng bấm vào trận #37.<br>2a.1. Hệ thống báo "Không tải được chi tiết trận, kiểm tra kết nối mạng"; giao diện chi tiết trận chỉ có dòng "Trận #37", các mục kết quả và từng câu để trống. Kịch bản tiếp tục từ bước 3. |

Bảng dữ liệu mẫu bài làm từng câu trận 37:

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

#### 2.3.3. Xem bảng xếp hạng

| | |
|---|---|
| **Use case** | Xem bảng xếp hạng |
| **Actor** | Người chơi: Hùng |
| **Tiền điều kiện** | Hùng đã đăng nhập, máy có kết nối mạng, đang ở giao diện chơi online; hệ thống có 7 người chơi với tổng điểm và các trận đã kết thúc như bảng dữ liệu mẫu bảng xếp hạng bên dưới |
| **Hậu điều kiện** | Hùng thấy thứ hạng của mọi người chơi; dữ liệu không thay đổi; giao diện chơi online hiện lại |
| **Sự kiện chính** | 1. Ở giao diện chơi online, Hùng nhấn nút biểu tượng **Bảng xếp hạng** ở góc phải ngoài cùng.<br>2. Hệ thống lấy mọi người chơi và các trận đã kết thúc, tính 3 tiêu chí rồi sắp xếp. Giao diện lịch sử đấu hiện lên với thẻ **Bảng xếp hạng** được mở sẵn, thẻ Trận của tôi nằm bên cạnh để chọn qua lại. Thẻ Bảng xếp hạng hiện danh sách như bảng dữ liệu mẫu bảng xếp hạng bên dưới, dòng HUNG được tô nổi bật. HUNG và LAN cùng 13,5 điểm, HUNG đã gặp các đối thủ mạnh hơn, 11 so với 9, nên đứng trên. AN và NAM trùng cả 2 tiêu chí đầu, AN thắng nhanh hơn nên đứng trên.<br>3. Hùng xem danh sách rồi nhấn Back.<br>4. Giao diện chơi online hiện lại. |
| **Ngoại lệ** | 1a. Đang ở thẻ Trận của tôi của giao diện lịch sử đấu, Hùng chọn thẻ Bảng xếp hạng.<br>1a.1. Kịch bản tiếp tục từ bước 2.<br>2a. Máy mất mạng khi Hùng mở thẻ Bảng xếp hạng.<br>2a.1. Hệ thống báo "Không tải được bảng xếp hạng, kiểm tra kết nối mạng", danh sách để trống.<br>2a.2. Hùng chọn thẻ Trận của tôi rồi chọn lại thẻ Bảng xếp hạng để tải lại. Kịch bản tiếp tục từ bước 2.<br>3a. Đang ở thẻ Bảng xếp hạng, Hùng chọn thẻ Trận của tôi.<br>3a.1. Giao diện hiện biểu đồ tròn và danh sách trận như bước 2 của use case Xem lịch sử đấu; Hùng bấm vào một trận để xem chi tiết như use case Xem chi tiết trận. |

Bảng dữ liệu mẫu bảng xếp hạng:

| Hạng | Tên | Tổng điểm | TB điểm đối thủ | TB thời gian thắng |
|---|---|---|---|---|
| 1 | MINH | 15 | 10,5 | 41,3 giây |
| 2 | **HUNG** (dòng nổi bật) | 13,5 | 11 | 39,8 giây |
| 3 | LAN | 13,5 | 9 | 44,0 giây |
| 4 | HOANG | 9 | 13,5 | 47,2 giây |
| 5 | AN | 4,5 | 12 | 49,0 giây |
| 6 | NAM | 4,5 | 12 | 52,1 giây |
| 7 | TUAN | 0 | 13,5 | — |
