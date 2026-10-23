# KỊCH BẢN v3 VÀ BIỂU ĐỒ TUẦN TỰ CHI TIẾT — CHƠI ONLINE, XEM LỊCH SỬ ĐẤU

> Issue: #45 [M4-03] · Người làm: Hùng · Người duyệt: Hoàng
> Bước D4 trong `REPORT_CONVENTION.md`. Đây là **mục 4.x.3** trong báo cáo của Hùng.
> Đầu vào: kịch bản v2 (`3-kich-ban-v2.md`), biểu đồ lớp chi tiết (`4-lop-thiet-ke.md`).

> **Trạng thái:** viết trước khi code. Tên lớp và tên hàm theo `4-lop-thiet-ke.md`; lệch với code thật thì sửa tài liệu theo code trước khi nộp.

Quy ước:
- **Mỗi bước là đúng một thông điệp**, dùng tên lớp và tên hàm thật. Số bước trùng với số trên biểu đồ.
- Biểu đồ có đủ boundary – control – entity; chức năng gọi server có thêm 2 khung "Client (Android)" và "Server (Spring Boot)".
- **REST:** mỗi lời gọi API là **một** thông điệp từ `MatchApiDAO` tới `MatchRestController`, ghi phương thức + đường dẫn. Kết quả là một thông điệp trả về, ghi kiểu dữ liệu.
- **Firebase (bất đồng bộ):**
  - Lời gọi DAO là một thông điệp.
  - Kết quả là thông điệp từ callback (`onComplete`, `onDataChange`), ghi tên callback trên mũi tên.
- **`Thread` + `runOnUiThread`:** ghi chú `{Thread}` / `runOnUiThread` cạnh thông điệp.
- **`CountDownTimer`:** vẽ như một đối tượng điều khiển, gửi `onTick` / `onFinish`.
- **Đóng gói thực thể con được lược bỏ cho gọn** (giống bài mẫu). Ví dụ: khi đóng gói `Match` thì 2 `MatchPlayer` và 2 `Player` bên trong cũng được tạo, nhưng không vẽ thành thông điệp riêng.
- **Chỉ gồm kịch bản chuẩn**, không có ngoại lệ. Ngoại lệ đã mô tả ở kịch bản v1.

---

## 1. Chức năng "Chơi online" (UC05)

### 1.1. Đoạn A: Tạo phòng (Hùng, máy 1)

1. Hùng nhấn nút Chơi online trên `MainActivity`.
2. Phương thức `onClick` của `MainActivity` gọi `startActivity` mở `OnlineActivity`.
3. Phương thức `onCreate` của `OnlineActivity` gọi phương thức `getPlayer` của `SessionDAO`.
4. `getPlayer` đọc SharedPreferences `"session"` và gọi lớp `Player` để đóng gói thông tin.
5. Lớp `Player` trả về đối tượng `Player` (id `1`, tên `HUNG`) cho `getPlayer`.
6. `getPlayer` trả `Player` cho `onCreate`; `OnlineActivity` giữ nó trong thuộc tính `me`.
7. `OnlineActivity` hiển thị cho Hùng nút Tạo phòng, ô mã phòng và nút Vào phòng.
8. Hùng nhấn nút Tạo phòng.
9. `onClick` của `OnlineActivity` gọi phương thức `createRoom(me, this)` của `MatchController`.
10. `createRoom` sinh mã phòng bằng `generateRoomCode` (`482915`) và một `questionSeed` ngẫu nhiên, rồi gọi lớp `Match` để tạo trận ở trạng thái `waiting` với người chơi thứ nhất là HUNG.
11. Lớp `Match` trả đối tượng `Match` cho `createRoom`.
12. `createRoom` gọi phương thức `createMatch(match, callback)` của `MatchFirebaseDAO`.
13. `createMatch` chạy `runTransaction` trên node `matches/482915`: node chưa có (hoặc đã `finished`) thì ghi trận mới, đặt `onDisconnect().removeValue()` để phòng tự xóa nếu tắt app khi đang chờ.
14. Transaction xong, callback `onComplete` của `MatchFirebaseDAO` gọi `onSuccess(match)` của `MatchController`.
15. `MatchController` gọi phương thức `onRoomReady(match)` của `OnlineActivity`.
16. `onRoomReady` gọi `startActivity` mở `LobbyActivity`, kèm mã phòng.
17. Phương thức `onCreate` của `LobbyActivity` gọi phương thức `waitForOpponent(this)` của `MatchController`.
18. `waitForOpponent` gọi phương thức `watchMatch("482915", callback)` của `MatchFirebaseDAO`.
19. `LobbyActivity` hiển thị cho Hùng mã `482915` và "Đang chờ đối thủ...".
20. Khi Hoàng vào phòng (đoạn B), node thay đổi. Callback `onDataChange` của `MatchFirebaseDAO` gọi `toMatch("482915", data)` của chính nó.
21. `toMatch` gọi lớp `Match` để đóng gói trận: trạng thái `playing`, 2 người HUNG và HOANG.
22. Lớp `Match` trả đối tượng cho `toMatch`.
23. `MatchFirebaseDAO` gọi `onSuccess(match)` của `MatchController`.
24. `MatchController` thấy trận đã `playing` nên gọi `onRoomReady(match)` của `LobbyActivity`. Các lần `onDataChange` khi trận còn `waiting` thì bỏ qua.
25. `LobbyActivity` ẩn nút Hủy, hiển thị cho Hùng "Trận đấu bắt đầu!" và đối thủ HOANG.
26. Sau 2 giây, `LobbyActivity` gọi `startActivity` mở `OnlineGameActivity` (sang đoạn C).

![Hình 16: Tuần tự thiết kế Chơi online – Tạo phòng](tuan-tu-thiet-ke-uc05a.png)

*Hình 16: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn A: Tạo phòng.*

- Biểu đồ có 26 thông điệp, khớp 26 bước ở trên.
- Activity không gọi Firebase trực tiếp. Mọi thao tác đi qua `MatchController` rồi tới `MatchFirebaseDAO`.
- Kết quả bất đồng bộ đi ngược lại qua callback: `onComplete` / `onDataChange` → `onSuccess` → `onRoomReady`.
- Bước 13 dùng transaction nên 2 người không thể tạo trùng một mã phòng.

### 1.2. Đoạn B: Tìm phòng (Hoàng, máy 2)

1. Hoàng nhấn nút Chơi online trên `MainActivity`.
2. `onClick` của `MainActivity` gọi `startActivity` mở `OnlineActivity`.
3. `onCreate` của `OnlineActivity` gọi `getPlayer` của `SessionDAO`.
4. `getPlayer` gọi lớp `Player` để đóng gói thông tin.
5. Lớp `Player` trả về `Player` (id `2`, tên `HOANG`) cho `getPlayer`.
6. `getPlayer` trả `Player` cho `onCreate`.
7. `OnlineActivity` hiển thị cho Hoàng nút Tạo phòng, ô mã phòng và nút Vào phòng.
8. Hoàng nhập `482915` và nhấn nút Vào phòng.
9. `onClick` kiểm tra mã đủ 6 chữ số rồi gọi `findRoom("482915", me, this)` của `MatchController`.
10. `findRoom` gọi `joinMatch("482915", me, callback)` của `MatchFirebaseDAO`.
11. `joinMatch` chạy `runTransaction` trên node `matches/482915`. Trong `doTransaction`, nó gọi `toMatch("482915", data)` của chính nó.
12. `toMatch` gọi lớp `Match` để đóng gói trận hiện tại: `waiting`, 1 người là HUNG.
13. Lớp `Match` trả đối tượng cho `toMatch`.
14. `doTransaction` gọi phương thức `canJoin(2)` của `Match`.
15. `canJoin` trả về `true`: trận đang `waiting`, mới có 1 người, người đó không phải HOANG.
16. `doTransaction` ghi thêm node `players/2` (HOANG, điểm 0) và đặt `status = "playing"`.
17. Transaction xong, callback `onComplete` gọi `onSuccess(match)` của `MatchController`.
18. `MatchController` gọi `onRoomReady(match)` của `OnlineActivity`.
19. `onRoomReady` gọi `startActivity` mở `LobbyActivity`.
20. `onCreate` của `LobbyActivity` gọi `waitForOpponent(this)` của `MatchController`.
21. `waitForOpponent` thấy trận đã `playing` nên gọi ngay `onRoomReady(match)` của `LobbyActivity`.
22. `LobbyActivity` hiển thị cho Hoàng "Trận đấu bắt đầu!" và đối thủ HUNG.
23. Sau 2 giây, `LobbyActivity` gọi `startActivity` mở `OnlineGameActivity` (sang đoạn C, máy 2 chạy giống máy 1).

![Hình 17: Tuần tự thiết kế Chơi online – Tìm phòng](tuan-tu-thiet-ke-uc05b.png)

*Hình 17: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn B: Tìm phòng.*

- Biểu đồ có 23 thông điệp.
- **Bước 14–16** là trọng tâm:
  - Luật vào phòng `Match.canJoin` chạy **bên trong transaction** của Firebase.
  - Nếu 2 người cùng nhập mã một lúc, Firebase chạy lại transaction cho người đến sau, nên phòng không bao giờ có 3 người.
- `canJoin` là hàm Java thuần nên kiểm thử được bằng `MatchTest` mà không cần Firebase.

### 1.3. Đoạn C: Thi đấu và kết thúc trận (Hùng, máy 1)

1. `onCreate` của `OnlineGameActivity` gọi `startGame(this)` của `MatchController`.
2. `startGame` gọi `registerDisconnect("482915", 1)` của `MatchFirebaseDAO`: nếu máy 1 mất kết nối, máy chủ Firebase tự ghi `connected = false`, `forfeited = true` cho HUNG.
3. `startGame` tạo `ProblemDAO` với `new Random(questionSeed)`, nên 2 máy có cùng dãy câu hỏi.
4. `startGame` gọi lớp `GameSession` để tạo ván chấm điểm `new GameSession(1, true)`: bắt đầu ở mức 1, chế độ online (sai không trừ mạng).
5. `startGame` gọi `watchMatch("482915", callback)` của `MatchFirebaseDAO` để theo dõi điểm đối thủ.
6. `startGame` gọi `start()` của `CountDownTimer` 60 giây.
7. `startGame` gọi `getCurrentLevel()` của `GameSession`.
8. `GameSession` trả về mức `1`.
9. `startGame` gọi `generateProblem(1)` của `ProblemDAO`.
10. `generateProblem` gọi lớp `MathProblem` để đóng gói câu hỏi `12 + 7`.
11. Lớp `MathProblem` trả đối tượng cho `generateProblem`.
12. `generateProblem` trả `MathProblem` cho `startGame`; `startGame` lưu nó làm câu hiện tại của `GameSession`.
13. `startGame` trả `MathProblem` cho `onCreate`.
14. `OnlineGameActivity` (hàm `showProblem`) hiển thị cho Hùng: hàng điểm `0` VS `0`, đồng hồ `60`, `12 + 7 = ?` và bàn phím số.
15. Mỗi giây, `CountDownTimer` gọi `onTick(42000)` của `MatchController`.
16. `MatchController` gọi `onTick(42)` của `OnlineGameActivity`.
17. `OnlineGameActivity` hiển thị đồng hồ `00:42`.
18. Hùng bấm `1`, `9` rồi nhấn OK. Mỗi lần bấm số, `onClick` gọi `appendDigit` của chính Activity để cập nhật `txtAnswer` (`12 + 7 = 19`); thao tác này chỉ nằm trong giao diện nên gộp vào bước này.
19. `onClick` của nút OK đọc số `19` từ `txtAnswer` và gọi `submitAnswer(19)` của `MatchController`.
20. `submitAnswer` gọi `isCorrect(19)` của câu hiện tại (`MathProblem`).
21. `MathProblem` trả về `true`.
22. `submitAnswer` gọi `recordCorrectAnswer()` của `GameSession`.
23. `GameSession` tăng chuỗi đúng lên 1, cộng `10 + 1 × 2 = 12` điểm, trả về `12` cho `submitAnswer`. Điểm mới là `12`.
24. `submitAnswer` gọi `updateScore("482915", 1, 12)` của `MatchFirebaseDAO` để máy đối thủ thấy điểm mới.
25. `submitAnswer` trả `12` cho `onClick`.
26. `onClick` gọi `nextProblem()` của `MatchController`.
27. `nextProblem` gọi `getCurrentLevel()` của `GameSession`.
28. `GameSession` trả về mức `1`.
29. `nextProblem` gọi `generateProblem(1)` của `ProblemDAO`.
30. `generateProblem` gọi lớp `MathProblem` để đóng gói câu `9 − 4`.
31. Lớp `MathProblem` trả đối tượng cho `generateProblem`.
32. `generateProblem` trả `MathProblem` cho `nextProblem`.
33. `nextProblem` trả `MathProblem` cho `onClick`.
34. `OnlineGameActivity` gọi `showScorePopup(12)` cho chữ "+12" bay lên từ điểm của mình, hiển thị điểm `12`, xóa `txtAnswer` và hiện `9 − 4 = ?`.
35. Khi Hoàng ghi điểm, node thay đổi. Callback `onDataChange` của `MatchFirebaseDAO` gọi `toMatch("482915", data)`.
36. `toMatch` gọi lớp `Match` để đóng gói trận mới nhất.
37. Lớp `Match` trả đối tượng cho `toMatch`.
38. `MatchFirebaseDAO` gọi `onSuccess(match)` của `MatchController`.
39. `MatchController` gọi `isOver()` của `Match`.
40. `Match` trả về `false`: không ai bỏ trận, chưa ai hết giờ.
41. `MatchController` gọi `onMatchChanged(match)` của `OnlineGameActivity`.
42. `OnlineGameActivity` hiển thị điểm HOANG `12`.

    *(Các bước 15–42 lặp lại đến khi `CountDownTimer` hết giờ. Trả lời sai thì bước 22 là `recordWrongAnswer()`, không có bước 24, và `showScorePopup(0)` hiện chữ "MISS" màu đỏ.)*
43. Hết 60 giây, `CountDownTimer` gọi `onFinish()` của `MatchController`.
44. `onFinish` gọi `setTimeUp("482915", 1)` của `MatchFirebaseDAO`.
45. `onFinish` gọi `onTimeUp()` của `OnlineGameActivity`.
46. `OnlineGameActivity` khóa bàn phím số, `txtTimer` hiển thị "Hết giờ! Đang chờ kết quả...".
47. Khi máy 2 cũng ghi `timeUp`, callback `onDataChange` gọi `toMatch("482915", data)`.
48. `toMatch` gọi lớp `Match` để đóng gói trận.
49. Lớp `Match` trả đối tượng cho `toMatch`.
50. `MatchFirebaseDAO` gọi `onSuccess(match)` của `MatchController`.
51. `MatchController` gọi `isOver()` của `Match`.
52. `Match` trả về `true` vì cả 2 người đã `timeUp`.
53. `MatchController` gọi `finishMatch()` của chính nó trong một `Thread`; cờ `finishing` đảm bảo chỉ gọi một lần.
54. `finishMatch` gọi `finishMatch("482915")` của `MatchApiDAO`.
55. `MatchApiDAO` gửi `POST /api/matches/482915/finish` (kèm token) tới phương thức `finish("482915")` của `MatchRestController`.
56. `finish` gọi `readMatch("482915")` của `FirebaseAdminDAO`.
57. `readMatch` đọc node bằng Admin SDK và gọi lớp `Match` (server) để đóng gói.
58. Lớp `Match` trả đối tượng cho `readMatch`.
59. `readMatch` trả `Match` (trạng thái `playing`, HUNG `120`, HOANG `96`) cho `finish`.
60. `finish` gọi `isOver()` của `Match`. Server **kiểm tra lại**, không tin vào việc app đã gọi.
61. `Match` trả về `true`.
62. `finish` gán `endedAt` là thời điểm hiện tại rồi gọi `insert(match)` của `MatchDAO`.
63. `insert` ghi 1 dòng `tblMatch` và 2 dòng `tblMatchPlayer` trong một transaction JDBC.
64. `insert` trả `true` cho `finish`.
65. `finish` gọi `updateStatus("482915", "finished")` của `FirebaseAdminDAO`.
66. `finish` trả `200 OK` kèm `Match` dạng JSON cho `MatchApiDAO`.
67. `MatchApiDAO` gọi `toMatch(json)`, tạo đối tượng lớp `Match` (client).
68. Lớp `Match` trả đối tượng cho `MatchApiDAO`.
69. `MatchApiDAO` trả `Match` cho `finishMatch` của `MatchController`.
70. `finishMatch` gọi `stopWatching("482915")` của `MatchFirebaseDAO` để gỡ listener; đồng hồ đã dừng.
71. `finishMatch` gọi `onMatchEnd(match)` của `OnlineGameActivity` trong `runOnUiThread`.
72. `onMatchEnd` gọi `showResult`, và `showResult` gọi `getResult(1)` của `Match`.
73. `Match` trả về `"WIN"`: không ai bỏ trận, `120 > 96`.
74. `OnlineGameActivity` hiện overlay `layoutResult`: "THẮNG!" màu xanh, tỉ số `120 - 96`.
75. Hùng nhấn nút Về menu.
76. `onClick` gọi `finish()`, quay về `MainActivity`.
77. `MainActivity` hiển thị cho Hùng.

![Hình 18: Tuần tự thiết kế Chơi online – Thi đấu và kết thúc trận](tuan-tu-thiet-ke-uc05c.png)

*Hình 18: Biểu đồ tuần tự chi tiết chức năng Chơi online, đoạn C: Thi đấu và kết thúc trận (client + server).*

- Biểu đồ có 77 thông điệp.
- Ba lớp nghiệp vụ là hàm Java thuần, nằm trong code của nhóm:
  - **Chấm điểm** (`MathProblem.isCorrect`, `GameSession.recordCorrectAnswer`) chạy trên app.
  - **Điều kiện kết thúc** (`Match.isOver`) chạy ở cả app (để biết lúc gọi server) và server (để từ chối yêu cầu sớm).
  - **Thắng thua** (`Match.getResult`) chạy trên bản trận server đã lưu.
- **Firebase** chỉ chuyển điểm giữa 2 máy (bước 24, 35) và báo trạng thái trận.
- **Lời gọi REST** duy nhất là bước 55; phía server đi đúng khuôn Controller → DAO → Entity, không có tầng Service.

---

## 2. Chức năng "Xem lịch sử đấu" (UC06)

1. Hùng nhấn nút Lịch sử đấu trên `MainActivity`.
2. `onClick` của `MainActivity` gọi `startActivity` mở `HistoryActivity`.
3. `onCreate` của `HistoryActivity` gọi `getPlayer` của `SessionDAO`.
4. `getPlayer` gọi lớp `Player` để đóng gói thông tin.
5. Lớp `Player` trả về `Player` (id `1`, tên `HUNG`) cho `getPlayer`.
6. `getPlayer` trả `Player` cho `onCreate`.
7. `onCreate` gọi `loadHistory(1)` của chính nó.
8. `loadHistory` mở một `Thread` và gọi `getHistory(1)` của `MatchApiDAO`.
9. `MatchApiDAO` gửi `GET /api/matches?playerId=1` (kèm token) tới phương thức `getByPlayer(1)` của `MatchRestController`.
10. `getByPlayer` gọi `findByPlayer(1)` của `MatchDAO`.
11. `findByPlayer` chạy câu truy vấn `tblMatch` JOIN `tblMatchPlayer` JOIN `tblPlayer` của người chơi `1`, sắp xếp `endedAt DESC`.
12. Với mỗi dòng kết quả, `findByPlayer` gọi lớp `Match` để đóng gói một trận cùng 2 `MatchPlayer`.
13. Lớp `Match` trả đối tượng cho `findByPlayer`.
14. `findByPlayer` trả `List<Match>` (4 trận) cho `getByPlayer`.
15. `getByPlayer` trả `200 OK` kèm danh sách dạng JSON cho `MatchApiDAO`.
16. Với mỗi phần tử JSON, `getHistory` gọi `toMatch(json)` để tạo đối tượng lớp `Match` (client).
17. Lớp `Match` trả đối tượng cho `getHistory`.
18. `getHistory` trả `List<Match>` cho `loadHistory`.
19. Trong `runOnUiThread`, `loadHistory` tạo `new HistoryAdapter(list, 1)` và gắn vào `rvHistory`.
20. Với mỗi dòng, `onBindViewHolder` của `HistoryAdapter` gọi `getResult(1)` của `Match`.
21. `Match` trả về `"WIN"`, `"DRAW"`, `"LOSE"` hoặc `"WIN"` tùy trận.
22. `HistoryActivity` hiển thị cho Hùng 4 dòng: thời gian, `vs` đối thủ, tỉ số, kết quả có màu.
23. Hùng nhấn Back.
24. `HistoryActivity` gọi `finish()`, quay về `MainActivity`.
25. `MainActivity` hiển thị cho Hùng.

![Hình 19: Tuần tự thiết kế Xem lịch sử đấu](tuan-tu-thiet-ke-uc06.png)

*Hình 19: Biểu đồ tuần tự chi tiết chức năng Xem lịch sử đấu (client + server).*

- Biểu đồ có 25 thông điệp.
- **Lời gọi REST** duy nhất là bước 9. Server đi theo khuôn `MatchRestController` → `MatchDAO` → `Match`.
- **Lời gọi mạng** chạy trong `Thread` (bước 8). Phần vẽ giao diện chạy trong `runOnUiThread` (bước 19).
- **Kết quả không lưu trong CSDL** mà tính lại ở bước 20–21 bằng `Match.getResult`. Dữ liệu lấy từ MySQL nên kết quả trùng với lúc trận kết thúc.
