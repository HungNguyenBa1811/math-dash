# USE CASE CHI TIẾT VÀ KỊCH BẢN v1 — CHƠI ONLINE, XEM LỊCH SỬ ĐẤU

**Dữ liệu mẫu:**

| Người chơi | Tên đăng nhập | Tên hiển thị | Máy |
|---|---|---|---|
| Hùng | hung | HUNG | Máy 1, người tạo phòng |
| Hoàng | hoang | HOANG | Máy 2, người tìm phòng |
| Minh | minh | MINH | Máy 3, vào phòng khi phòng đã đầy |

Mã phòng 482915, tạo lúc 20:15 ngày 23/10/2026. Câu hỏi đầu tiên 12 + 7, tỉ số cuối 120 – 96.

---

## 1. Biểu đồ use case chi tiết module Chơi online

![Hình 5: Biểu đồ use case chi tiết module Chơi online](uc-chi-tiet-choi-online.png)

*Hình 5: Biểu đồ use case chi tiết module Chơi online.*

Người chơi khởi phát trực tiếp 2 use case của module: "Chơi online" (UC05) và "Xem lịch sử đấu" (UC06). UC05 được phân rã như sau:
- **include "Vào phòng đấu"**: use case trừu tượng (chữ nghiêng), có 2 use case con kế thừa:
  - "Tạo phòng": tạo phòng mới, nhận mã phòng và chờ đối thủ.
  - "Tìm phòng": nhập mã phòng do bạn gửi để vào phòng.
- **include "Thi đấu"**: 60 giây trả lời câu hỏi, xem điểm đối thủ, nhận kết quả.
- **"Hủy phòng" extend "Tạo phòng"**: người tạo phòng có thể hủy khi còn đang chờ.
- **"Thoát trận" extend "Thi đấu"**: người chơi có thể thoát giữa trận và bị xử thua.

| Use case | Mô tả |
|---|---|
| Chơi online (UC05) | Vào một phòng đấu (tạo mới hoặc tìm bằng mã), thi đấu 60 giây với một người chơi khác và nhận kết quả |
| Vào phòng đấu | Use case trừu tượng: đưa người chơi vào một phòng có đúng 2 người |
| Tạo phòng | Hệ thống tạo phòng với mã 6 chữ số chưa dùng, hiển thị mã và chờ người thứ hai |
| Tìm phòng | Người chơi nhập mã phòng; hệ thống kiểm tra phòng còn chỗ rồi đưa người chơi vào |
| Hủy phòng | Người tạo phòng hủy khi chưa có đối thủ; hệ thống xóa phòng |
| Thi đấu | Hai người trả lời cùng một dãy câu hỏi trong 60 giây; hết giờ hệ thống xác định thắng / thua / hòa và lưu trận |
| Thoát trận | Người chơi rời trận trước khi hết giờ (hoặc mất kết nối) và bị xử thua |
| Xem lịch sử đấu (UC06) | Xem danh sách các trận online đã đấu, mới nhất ở trên |

---

## 2. Mô tả chi tiết các chức năng

### 2.1. Chức năng "Chơi online"

Người chơi đã đăng nhập, đang ở giao diện chính → Giao diện chính có: dòng "Xin chào, \<tên hiển thị>", các nút Chơi offline, Chơi online, Lịch sử đấu, Cài đặt, Đăng xuất → Người chơi nhấn Chơi online → Giao diện chơi online hiện lên với: nút Tạo phòng; ô nhập mã phòng (6 chữ số) và nút Vào phòng

- Tạo phòng: Người chơi nhấn Tạo phòng → Hệ thống tạo phòng mới với một mã phòng 6 chữ số chưa được dùng → Giao diện phòng chờ hiện lên với: mã phòng (chữ lớn), dòng hướng dẫn "Gửi mã này cho bạn để vào phòng", dòng trạng thái "Đang chờ đối thủ...", tên đối thủ (đang trống), nút Hủy → Người chơi gửi mã phòng cho bạn → Khi bạn vào phòng: giao diện phòng chờ hiện tên đối thủ và "Trận đấu bắt đầu!", nút Hủy ẩn đi → Sau 2 giây chuyển sang giao diện thi đấu
  - Nếu người chơi nhấn Hủy khi đang chờ (hoặc tắt ứng dụng): Hệ thống xóa phòng → Quay về giao diện chính
- Tìm phòng: Người chơi nhập mã phòng bạn gửi vào ô mã phòng và nhấn Vào phòng → Hệ thống kiểm tra phòng → Đưa người chơi vào phòng, trận chuyển sang đang đấu → Giao diện phòng chờ hiện lên với: mã phòng, tên chủ phòng và "Trận đấu bắt đầu!" → Sau 2 giây chuyển sang giao diện thi đấu
  - Nếu mã phòng không đủ 6 chữ số: Hệ thống thông báo "Mã phòng gồm 6 chữ số"
  - Nếu không có phòng nào mang mã này: Hệ thống thông báo "Không tìm thấy phòng"
  - Nếu phòng đã đủ 2 người hoặc trận đã bắt đầu: Hệ thống thông báo "Phòng đã đầy"

Thi đấu: Giao diện thi đấu hiện lên với: hàng điểm (điểm của mình bên trái, được làm nổi bật, chữ VS, điểm của đối thủ bên phải; không hiện tên), đồng hồ đếm ngược 60 giây ở giữa, câu hỏi kèm số đang nhập, bàn phím số 0–9 cùng nút xóa (biểu tượng ⌫) và nút OK → Hai máy nhận cùng một dãy câu hỏi → Người chơi bấm các số để nhập đáp án (nút xóa bỏ số cuối, tối đa 3 chữ số) và nhấn OK

- Nếu đúng: Hệ thống cộng 10 + chuỗi đúng × 2 điểm, chữ "+12" màu xanh bay lên từ điểm của mình rồi mờ dần → Điểm mới được gửi để máy đối thủ thấy → Xóa ô nhập, hiển thị câu tiếp theo
- Nếu sai: Hệ thống không cộng điểm, đưa chuỗi đúng về 0, chữ "MISS" màu đỏ bay lên tương tự (không mất mạng) → Xóa ô nhập, hiển thị câu tiếp theo
- Nếu nhấn OK khi chưa nhập số nào: Hệ thống không làm gì

→ (lặp lại đến khi hết giờ, cứ 5 câu thì câu hỏi khó lên 1 mức) → Mỗi khi đối thủ ghi điểm, hệ thống cập nhật điểm đối thủ trên giao diện → Hết 60 giây: Hệ thống khóa bàn phím số, đồng hồ đổi thành "Hết giờ! Đang chờ kết quả..." → Khi cả 2 máy đều hết giờ: Hệ thống xác định kết quả và lưu trận vào lịch sử đấu của cả 2 người → Giao diện kết quả hiện lên với: dòng kết quả THẮNG! / THUA! / HÒA!, tỉ số "\<điểm mình> - \<điểm đối thủ>", nút Về menu → Người chơi nhấn Về menu → Quay về giao diện chính

- Nếu người chơi nhấn Back khi đang thi đấu: Hệ thống hỏi "Thoát sẽ bị xử thua?" với 2 nút Ở lại, Thoát → Thoát: người chơi bị xử thua, hệ thống lưu trận, quay về giao diện chính → Ở lại: tiếp tục thi đấu
- Nếu đối thủ thoát hoặc mất kết nối: Hệ thống thông báo "Đối thủ đã thoát" → Người chơi được xử thắng, hiện giao diện kết quả
- Nếu máy của người chơi mất kết nối: Hệ thống thông báo "Mất kết nối, trận đấu đã kết thúc" → Quay về giao diện chính; người chơi bị xử thua


### 2.2. Chức năng "Xem lịch sử đấu"

Người chơi đã đăng nhập, đang ở giao diện chính → Người chơi nhấn Lịch sử đấu → Hệ thống lấy các trận online đã kết thúc của người chơi → Giao diện lịch sử đấu hiện lên với: tiêu đề "LỊCH SỬ ĐẤU", danh sách trận mới nhất ở trên; mỗi dòng gồm thời gian kết thúc, tên đối thủ, tỉ số (điểm của mình - điểm đối thủ), kết quả THẮNG (màu xanh) / THUA (màu đỏ) / HÒA (màu vàng) → Người chơi nhấn Back → Quay về giao diện chính

- Nếu người chơi chưa đấu trận nào: Hệ thống hiển thị "Chưa có trận nào"
- Nếu không kết nối được máy chủ: Hệ thống thông báo "Không tải được lịch sử đấu, kiểm tra kết nối mạng", danh sách để trống


---

## 3. Kịch bản UC05 "Chơi online"

| | |
|---|---|
| **Use case** | Chơi online (UC05), gồm Tạo phòng, Tìm phòng, Thi đấu |
| **Actor** | Người chơi (Hùng ở máy 1 tạo phòng; Hoàng ở máy 2 tìm phòng; mỗi người là actor trên máy của mình) |
| **Tiền điều kiện** | Cả 2 người chơi đã đăng nhập, máy có kết nối mạng, đang ở giao diện chính |
| **Hậu điều kiện** | Trận đấu được lưu vào lịch sử đấu của cả 2 người với tỉ số và người bỏ trận (nếu có); cả 2 người thấy cùng một kết quả |

Kịch bản chuẩn chia 3 đoạn, khớp với 3 use case con: **A. Tạo phòng** (máy 1), **B. Tìm phòng** (máy 2), **C. Thi đấu** (máy 1; máy 2 diễn ra tương tự).

### 3.1. Kịch bản chuẩn

**A. Tạo phòng**

1. Ở giao diện chính (dòng "Xin chào, HUNG"; các nút Chơi offline, Chơi online, Lịch sử đấu, Cài đặt, Đăng xuất), Hùng nhấn nút **Chơi online**.
2. Giao diện chơi online hiện lên gồm: nút **Tạo phòng**; ô nhập mã phòng (gợi ý "Nhập mã 6 chữ số") và nút **Vào phòng**.
3. Hùng nhấn nút **Tạo phòng**.
4. Hệ thống tạo phòng mới mã 482915 ở trạng thái đang chờ, Hùng là người chơi thứ nhất. Giao diện phòng chờ hiện lên gồm:

   | Thành phần | Giá trị |
   |---|---|
   | Mã phòng (chữ lớn) | 482915 |
   | Dòng hướng dẫn | Gửi mã này cho bạn để vào phòng |
   | Trạng thái | Đang chờ đối thủ... |
   | Đối thủ | — |
   | Nút | **Hủy** |

5. Hùng đọc mã 482915 cho Hoàng.

**B. Tìm phòng**

6. Ở giao diện chính của máy 2 ("Xin chào, HOANG"), Hoàng nhấn nút **Chơi online**.
7. Giao diện chơi online hiện lên như bước 2.
8. Hoàng nhập mã 482915 vào ô mã phòng và nhấn nút **Vào phòng**.
9. Hệ thống kiểm tra phòng 482915: có tồn tại, đang chờ, mới có 1 người (HUNG) và người đó không phải Hoàng. Hệ thống đưa Hoàng vào phòng và chuyển trận sang trạng thái đang đấu. Giao diện phòng chờ hiện lên trên máy 2 gồm: mã phòng 482915; trạng thái "Trận đấu bắt đầu!"; đối thủ HUNG; nút Hủy bị ẩn.
10. Cùng lúc, giao diện phòng chờ trên máy 1 cập nhật: trạng thái "Trận đấu bắt đầu!", đối thủ HOANG, nút Hủy bị ẩn.

**C. Thi đấu**

11. Sau 2 giây, giao diện thi đấu hiện lên trên cả 2 máy. Trên máy 1 gồm:

    | Thành phần | Giá trị |
    |---|---|
    | Hàng điểm (trên cùng) | **0** (của mình, nổi bật) · VS · 0 (đối thủ) |
    | Đồng hồ (giữa) | 60 |
    | Câu hỏi | 12 + 7 = ? (số đang nhập hiện thay cho dấu ?) |
    | Bàn phím số | 1 2 3 / 4 5 6 / 7 8 9 / ⌫ 0 **OK** |

    Máy 2 hiện cùng câu hỏi 12 + 7.
12. Hùng bấm 1, 9 (câu hỏi hiện 12 + 7 = 19) rồi nhấn **OK**.
13. Hệ thống chấm đúng: chuỗi đúng thành 1, cộng 10 + 1 × 2 = 12 điểm. Chữ "+12" màu xanh bay lên từ điểm của mình rồi mờ dần, điểm của mình thành 12. Hệ thống gửi điểm mới để máy đối thủ thấy, xóa số đã nhập và hiện câu tiếp theo 9 − 4 = ?.
14. Hoàng trả lời đúng câu đầu tiên trên máy 2. Trên máy 1, điểm đối thủ cập nhật thành 12.

    *(Lặp lại các bước 12–14 cho đến khi đồng hồ về 0. Trả lời sai thì chữ "MISS" màu đỏ bay lên, không cộng điểm, chuỗi đúng về 0, không mất mạng. Nhấn ⌫ thì xóa số cuối; nhấn OK khi chưa nhập gì thì không có gì xảy ra. Cứ 5 câu thì câu hỏi khó lên 1 mức.)*
15. Đồng hồ về 0. Hệ thống khóa bàn phím số, đồng hồ đổi thành "Hết giờ! Đang chờ kết quả...". Điểm cuối trên máy 1: HUNG 120, HOANG 96.
16. Máy 2 cũng hết giờ. Hệ thống xác định kết quả: không ai bỏ trận, 120 > 96 nên Hùng thắng. Hệ thống lưu trận vào lịch sử đấu của cả 2 người.
17. Giao diện kết quả hiện lên trên máy 1 gồm: dòng **THẮNG!** (màu xanh), tỉ số 120 - 96, nút **Về menu**. Trên máy 2 hiện **THUA!** với tỉ số 96 - 120.
18. Hùng nhấn nút **Về menu**.
19. Giao diện chính hiện lại.

### 3.2. Kịch bản ngoại lệ

| Bước | Tình huống ngoại lệ và cách xử lý |
|---|---|
| 5 | **Hủy phòng.** Hùng nhấn **Hủy** khi chưa có đối thủ. Hệ thống xóa phòng 482915 và quay về giao diện chính. Nếu Hùng tắt ứng dụng khi đang chờ, phòng cũng bị xóa; ai nhập mã này sau đó sẽ nhận "Không tìm thấy phòng". |
| 8 | Hoàng nhập 48291 (5 chữ số). Hệ thống báo "Mã phòng gồm 6 chữ số", không kiểm tra phòng, giữ nguyên giao diện bước 7. |
| 9 | Hoàng nhập 482910, không có phòng nào mang mã này. Hệ thống báo "Không tìm thấy phòng", giữ nguyên giao diện bước 7 để nhập lại. |
| 9 | Sau khi Hoàng đã vào, Minh (máy 3) nhập 482915. Phòng đã đủ 2 người, trận đã bắt đầu. Hệ thống báo "Phòng đã đầy", giữ nguyên giao diện chơi online của Minh. |
| 12–14 | **Thoát trận.** Hùng nhấn Back. Hệ thống hiện hộp thoại "Thoát sẽ bị xử thua?" với 2 nút **Ở lại**, **Thoát**. Hùng nhấn **Thoát**: Hùng bị xử thua, hệ thống lưu trận (Hùng thua dù đang dẫn điểm) và quay về giao diện chính. Nhấn **Ở lại** thì tiếp tục thi đấu, đồng hồ vẫn chạy. |
| 12–14 | **Đối thủ thoát.** Hoàng thoát trận hoặc máy 2 mất kết nối khi tỉ số đang 36 – 72. Máy 1 hiện thông báo "Đối thủ đã thoát", hệ thống xác định Hùng thắng (người bỏ trận thua, không xét điểm), lưu trận và hiện giao diện kết quả **THẮNG!** 36 - 72. |
| 12–15 | **Máy của mình mất kết nối.** Máy 1 mất mạng. Hệ thống báo "Mất kết nối, trận đấu đã kết thúc" và quay về giao diện chính. Hùng bị xử thua; máy 2 nhận "Đối thủ đã thoát" như ngoại lệ trên. |
| 16 | **Hòa.** Hết giờ với tỉ số 84 – 84, không ai bỏ trận. Hệ thống xác định hòa; giao diện kết quả hiện **HÒA!** (màu vàng) trên cả 2 máy. |
| 16 | Không kết nối được máy chủ khi xác định kết quả. Hệ thống báo "Không lưu được kết quả trận, kiểm tra kết nối mạng" và hiện nút **Về menu**. Trận vẫn được lưu khi máy đối thủ gửi yêu cầu kết thúc trận. |

---

## 4. Kịch bản UC06 "Xem lịch sử đấu"

| | |
|---|---|
| **Use case** | Xem lịch sử đấu (UC06) |
| **Actor** | Người chơi (Hùng) |
| **Tiền điều kiện** | Hùng đã đăng nhập, máy có kết nối mạng, đang ở giao diện chính |
| **Hậu điều kiện** | Hùng thấy danh sách các trận online đã đấu; dữ liệu không thay đổi |

### 4.1. Kịch bản chuẩn

1. Ở giao diện chính ("Xin chào, HUNG"), Hùng nhấn nút **Lịch sử đấu**.
2. Hệ thống lấy các trận online đã kết thúc mà Hùng tham gia. Giao diện lịch sử đấu hiện lên gồm tiêu đề "LỊCH SỬ ĐẤU" và danh sách trận, mới nhất ở trên:

   | Thời gian | Đối thủ | Tỉ số (mình - đối thủ) | Kết quả |
   |---|---|---|---|
   | 23/10/2026 20:16 | HOANG | 120 - 96 | THẮNG (xanh) |
   | 22/10/2026 21:42 | HOANG | 84 - 84 | HÒA (vàng) |
   | 22/10/2026 21:35 | HOANG | 60 - 132 | THUA (đỏ) |
   | 21/10/2026 19:05 | MINH | 36 - 72 | THẮNG (xanh) |

   Dòng cuối là trận Minh bỏ giữa chừng: Hùng thắng dù ít điểm hơn.
3. Hùng xem danh sách rồi nhấn Back.
4. Giao diện chính hiện lại.

### 4.2. Kịch bản ngoại lệ

| Bước | Tình huống ngoại lệ và cách xử lý |
|---|---|
| 2 | Hùng chưa đấu trận nào. Giao diện lịch sử đấu hiện dòng "Chưa có trận nào" thay cho danh sách. |
| 2 | Máy mất mạng hoặc máy chủ không phản hồi. Hệ thống báo "Không tải được lịch sử đấu, kiểm tra kết nối mạng", danh sách để trống; Hùng nhấn Back để về giao diện chính. |
