# KẾ HOẠCH ISSUE — MATH DASH 1v1 (BẢN 6: THEO ĐỀ 9 "GAME GIẢI TOÁN NHANH", DEMO 28/10)

> Nguồn: đề bài số 9 (dán nguyên văn ở `REPORT_CONVENTION.md` mục 1), `CONVENTION.md` (quy ước code Android), `REPORT_CONVENTION.md` (yêu cầu báo cáo + quy trình OOAD), `docs/chung/01-pham-vi-use-case.md` (phạm vi).
> Thành viên: **@HungNguyenBa1811** (Hùng – nhóm trưởng) · **@Minahn210** (Hoàng – thành viên 2).
> Mỗi mục `[Mx-yy]` bên dưới là 1 GitHub issue. Số thứ tự trong mỗi milestone là thứ tự nên làm.

**Thay đổi ở bản 6** (27/10, chia lại module để 2 người không trùng chức năng):
- **Đăng nhập / Đăng ký thuộc hẳn Hoàng** (module Tài khoản). Hùng không trình bày phần này nữa; code vẫn là code chung đã merge.
- **Hùng có 3 module, 3 chức năng:** Đấu đối kháng (UC03 Chơi online), Xem lịch sử đấu (UC04, gộp bảng xếp hạng), Bạn bè (UC07 Quản lý bạn bè).
- **UC04 đổi thành "Xem lịch sử đấu":** 2 thẻ Trận của tôi (biểu đồ tròn tỉ lệ thắng / hòa / thua + danh sách trận, bấm vào xem chi tiết trận) và Bảng xếp hạng. Chỉ gồm trận online với người, không gồm ván với máy. `RankingActivity` gộp vào `HistoryActivity`.
- **Thêm UC07 "Quản lý bạn bè":** lớp `Friendship`, bảng `tblFriendship` (khóa ngoại `ON DELETE CASCADE` tới `tblPlayer`, nên xóa người chơi ở UC06 không bị chặn).
- **Hoàng:** Tài khoản (UC01, UC02), Chơi với máy (UC05), Quản trị (UC06). Không đổi gì ở phần code của Hoàng.
- **Hoãn sau demo:** bảng `tblAnswer` lưu đáp án đã chọn (để màn chi tiết trận hiện bài làm), khi đó bỏ cột `tblMatchPlayer.correctCount`.

**Thay đổi so với bản 4** (đối chiếu lại với đề, ngày 26/10):
- **Chơi online theo đề:** danh sách người chơi online (tên, tổng điểm, rỗi / bận) → bấm tên để thách đấu → đồng ý / từ chối → **server sinh cùng một đề N = 10 câu trắc nghiệm** và gửi cho cả 2 → nộp bài → **server chấm và làm trọng tài**: đúng hết và nhanh hơn thì thắng, cả 2 có câu sai thì hòa; thắng 1, hòa 0,5, thua 0 → hỏi chơi tiếp sau mỗi ván.
- **Bỏ:** tạo / tìm phòng bằng mã 6 số, `questionSeed`, đấu 60 giây tính điểm combo, bàn phím số.
- **Thêm Xem bảng xếp hạng** theo 3 tiêu chí của đề: tổng điểm ↓, trung bình điểm đối thủ đã gặp ↓, trung bình thời gian thắng ↑.
- **Chơi offline đổi thành Chơi với máy:** cùng luật 10 câu trắc nghiệm, máy có 3 độ khó, chạy offline, lưu SQLite.
- **Thêm tác nhân Quản trị viên** và chức năng Quản lý thông tin người chơi.
- **Bỏ:** Cài đặt cấu hình game, Xem lịch sử đấu, chơi offline kiểu 3 mạng / kỷ lục.
- **Phân công bản 5** (đã thay ở bản 6): Hùng: Chơi online, Xem bảng xếp hạng, Đăng nhập / Đăng ký. Hoàng: Chơi với máy, Quản lý thông tin người chơi, Đăng nhập / Đăng ký.

---

## 0. Yêu cầu của môn học

| Hạng mục | Thời hạn | Nội dung |
|---|---|---|
| Demo cá nhân | **28/10/2026** | Mỗi người demo module của mình, **tối đa 3 chức năng** (30% điểm tổng kết) |
| Báo cáo thiết kế cá nhân | **15/11/2026** | Đặc tả, CSDL, lớp thực thể, rồi từng chức năng: giao diện, lớp chi tiết, tuần tự chi tiết (30% điểm tổng kết) |

Đề bài nguyên văn, khung báo cáo, cách làm từng bước OOAD và checklist trước khi nộp: xem **`REPORT_CONVENTION.md`**.

---

## 1. Các quyết định

| # | Vấn đề | Quyết định | Lý do |
|---|---|---|---|
| D1 | Phạm vi | 7 UC, 6 module, 2 tác nhân (Người chơi, Quản trị viên) như mục 2.1. Ngoài phạm vi ghi ở `docs/chung/01-pham-vi-use-case.md` mục 2.1 | 3 chức năng của đề + tài khoản + lịch sử đấu, bạn bè để mỗi người đủ 3 chức năng riêng |
| D2 | Kiến trúc | **App Android** (client) + **Server Spring Boot** (REST, MySQL) + **Firebase Realtime Database** (kênh báo tức thời: online, lời mời, trận đang đấu) | Đề yêu cầu server lưu toàn bộ dữ liệu và làm trọng tài; Firebase lo phần đẩy thay đổi tới các máy |
| D3 | Server mỏng | `RestController` → `DAO` (JDBC, lớp cha giữ `#conn: Connection`) → Entity. **Không có tầng Service, không dùng JPA** | Giữ đúng khuôn MVC của bài mẫu OOAD |
| D4 | Đăng nhập | Tên đăng nhập + mật khẩu, server so bản băm BCrypt và trả `Player` (có `role`), không cấp token. `role = admin` thì app mở màn quản trị. Tài khoản admin chèn sẵn trong `schema.sql` | Đơn giản, đủ cho demo |
| D5 | Luật trận online | 10 câu trắc nghiệm 4 phương án, tối đa 60 giây, hết giờ tự nộp. Kết quả theo thứ tự: bỏ trận thì thua → chỉ một người đúng hết thì người đó thắng → cả 2 có câu sai thì hòa → cả 2 đúng hết thì ai nộp sớm hơn thắng, bằng nhau hòa. Thắng 1, hòa 0,5, thua 0 | Đúng nguyên văn đề |
| D6 | Đề bài | **Server sinh đề** (`ProblemDAO` phía server), lưu vào `tblMathProblem`, gửi qua Firebase **không kèm đáp án** | "Server sinh tự động cùng một đề bài và gửi về cho cả hai" |
| D7 | Trọng tài | App gửi bài làm `POST /api/matches/{id}/submit`. Server ghi thời điểm nhận (đồng hồ server), chấm bằng `Match.countCorrect`, lưu; khi `Match.isOver()` thì ghi `endedAt`, cập nhật tổng điểm, báo qua Firebase. `Match.countCorrect`, `isOver`, `getResult`, `getPoints`, `getFinishTime` là **hàm Java thuần có unit test** | Luật nằm trong code của nhóm; thời gian đo ở server nên công bằng |
| D8 | Chơi với máy | Độ khó máy: Dễ / Trung bình / Khó. Mỗi câu máy trả lời đúng với xác suất 60% / 80% / 95%; thời gian nộp ngẫu nhiên 45–58 s / 35–48 s / 25–40 s. Cùng luật D5 | Có ý nghĩa thắng / thua, không cần mạng |
| D9 | Thuộc tính dẫn xuất | **Không lưu:** kết quả trận, điểm trận, tổng điểm, 2 tiêu chí còn lại của bảng xếp hạng, trạng thái trận (suy từ `endedAt`), kết quả ván với máy | Đúng bước 5 thiết kế CSDL |
| D10 | Lưu trữ phía app | SQLite: `tblPlayer` (bản sao), `tblBotGame`. SharedPreferences: phiên đăng nhập | Chơi với máy chạy khi mất mạng |
| D11 | Lớp DAO | Mỗi nơi lưu trữ có **một lớp cha giữ kết nối**: app `DAO` (SQLite), `ApiDAO` (HTTP), `FirebaseDAO` (Firebase); server `DAO` (JDBC). Các DAO con kế thừa | Đúng tài liệu OOAD của môn |
| D12 | Tên bảng | Tiền tố `tbl` + tên lớp; khóa ngoại `tbl<Bảng>Id` | Đúng bước 1 và 4 thiết kế CSDL |
| D13 | Chia việc | Theo module (mục 2.1), mỗi người 3 chức năng, không trùng nhau. Phần chung Hùng làm, Hoàng duyệt PR. Đăng nhập / Đăng ký là code chung nhưng chỉ Hoàng trình bày | Mỗi người tối đa 3 chức năng; báo cáo cá nhân không trùng nội dung |
| D14 | Thứ tự OOAD | Phần chung làm **trước khi code**. Phân tích và thiết kế từng chức năng: Hùng đã viết trước (thiết kế dẫn code), Hoàng làm sau demo | Kịp demo |
| D15 | Báo cáo | Mỗi người **một file riêng**, chỉ lấy phần chung liên quan chức năng của mình | Đề ghi "bài làm cá nhân" |
| D16 | Kiến trúc app | MVC 3 tầng `entity` – `dao` – `view`. Activity xử lý sự kiện, điều phối và hiển thị; **không có lớp Controller riêng**. Luật nghiệp vụ là hàm Java thuần trên entity; truy cập dữ liệu (SQLite, REST, Firebase) chỉ nằm trong DAO | Khớp mô hình boundary – DAO – entity của bài mẫu |

---

## 2. Hợp đồng chung (chốt ở M1, sau đó KHÔNG đổi tùy tiện)

### 2.1. Use case và người demo

| UC | Tên use case | Tác nhân | Module | Người code, demo, vẽ |
|---|---|---|---|---|
| UC01 | Đăng ký | Người chơi | Tài khoản | Code chung (Hùng làm, Hoàng duyệt). **Hoàng trình bày** |
| UC02 | Đăng nhập | Người chơi, Quản trị viên | Tài khoản | Code chung. **Hoàng trình bày** |
| UC03 | Chơi online (danh sách online, thách đấu, thi đấu, chơi tiếp) | Người chơi | Đấu đối kháng | **Hùng** |
| UC04 | Xem lịch sử đấu (trận của tôi, chi tiết trận, bảng xếp hạng) | Người chơi | Xem lịch sử đấu | **Hùng** |
| UC05 | Chơi với máy | Người chơi | Chơi với máy | **Hoàng** |
| UC06 | Quản lý thông tin người chơi | Quản trị viên | Quản trị | **Hoàng** |
| UC07 | Quản lý bạn bè | Người chơi | Bạn bè | **Hùng** |

Label theo UC: `uc-tai-khoan` (UC01, UC02), `uc-choi-online` (UC03), `uc-lich-su` (UC04), `uc-choi-voi-may` (UC05), `uc-quan-tri` (UC06), `uc-ban-be` (UC07).

### 2.2. Tên lớp và người sở hữu

**App Android** (`ptit.e23cnpm01.nguyenbahung.math_dash`):

| Loại | Lớp | Sở hữu |
|---|---|---|
| Entity | `Player`, `MathProblem`, `Challenge`, `Match`, `MatchPlayer`, `BotGame`, `Friendship` | **Chung** |
| DAO SQLite | `DAO` (cha), `DatabaseHelper`, `PlayerDAO` (bản sao người chơi) | **Chung** |
| DAO SQLite | `BotGameDAO` | Hoàng |
| DAO HTTP | `ApiDAO` (cha, có `get`, `post`, `getArray`, `delete`), `PlayerApiDAO` (đăng ký, đăng nhập, `getById`), `SessionDAO` (lưu phiên) | **Chung** |
| DAO HTTP | `PlayerApiDAO.getRanking`, `MatchApiDAO` (có `getHistory`), `FriendshipApiDAO` | Hùng |
| DAO HTTP | `PlayerApiDAO` các hàm quản trị (`search`, `update`, `resetPassword`, `delete`) | Hoàng |
| DAO Firebase | `FirebaseDAO` (cha), `PresenceFirebaseDAO`, `ChallengeFirebaseDAO`, `MatchFirebaseDAO` | Hùng |
| Sinh câu hỏi | `ProblemDAO` (không truy cập CSDL; app dùng cho Chơi với máy) | **Chung** |
| Giao diện | `LoginActivity`, `RegisterActivity`, `MainActivity` (khung) | **Chung** |
| Giao diện | `MainActivity` phần danh sách online + `PlayerAdapter`, `MatchActivity`, `QuestionAdapter` | Hùng |
| Giao diện | `HistoryActivity`, `PieChartView`, `MatchHistoryAdapter`, `RankingAdapter`, `MatchDetailActivity`, `ProblemAdapter` | Hùng |
| Giao diện | `FriendActivity`, `FriendRequestAdapter`, `FriendAdapter` | Hùng |
| Giao diện | `DifficultyActivity`, `BotGameActivity`, `AdminActivity`, `PlayerDetailActivity` | Hoàng |

**Server** (thư mục `server/`, package `ptit.e23cnpm01.nguyenbahung.mathdash`):

| Loại | Lớp | Sở hữu |
|---|---|---|
| Entity | `Player`, `MathProblem`, `Challenge`, `Match`, `MatchPlayer`, `Friendship` (cùng tên, cùng thuộc tính với phía app) | **Chung** |
| Controller (REST) | `AuthRestController` (`/api/auth/*`) | **Chung** |
| Controller (REST) | `MatchRestController` (`/api/matches/*`, có `getHistory`), `PlayerRestController.getById`, `getRanking`, `FriendshipRestController` (`/api/friendships/*`) | Hùng |
| Controller (REST) | `PlayerRestController` các hàm quản trị | Hoàng |
| DAO JDBC | `DAO` (cha), `PlayerDAO` (`findByUsername`, `insert`, `findById`, `getTotalPoints`) | **Chung** |
| DAO JDBC | `MatchDAO` (có `findByPlayer`), `PlayerDAO.getRanking`, `FriendshipDAO` | Hùng |
| DAO JDBC | `PlayerDAO` các hàm quản trị | Hoàng |
| Sinh đề | `ProblemDAO` (server) | Hùng |
| Firebase REST | `FirebaseAdminDAO` (đọc presence, ghi lời mời, trận, trạng thái) | Hùng |

- Lớp **Chung**: Hùng viết, Hoàng duyệt. Sau khi merge, muốn sửa thì mở PR riêng và người kia duyệt.
- Lớp của ai thì người đó viết thân hàm, demo và giải thích.

### 2.3. Lớp DAO cha giữ kết nối (D11)

```java
// App: dao/DAO.java — cha của mọi DAO SQLite
public abstract class DAO {
    protected static SQLiteDatabase con;
    public DAO(Context context) {
        if (con == null) con = new DatabaseHelper(context.getApplicationContext()).getWritableDatabase();
    }
}

// App: dao/ApiDAO.java — cha của mọi DAO gọi REST (HttpURLConnection, gọi trong Thread)
public abstract class ApiDAO {
    protected static final String BASE_URL = "https://math-dash-server.onrender.com/api"; // server trên Render; chạy local: "http://10.0.2.2:8080/api"
    protected JSONObject get(String path) throws IOException, JSONException { /* ... */ }
    protected JSONObject post(String path, JSONObject body) throws IOException, JSONException { /* ... */ }
    protected JSONArray getArray(String path) throws IOException, JSONException { /* ... */ }
}

// App: dao/FirebaseDAO.java — cha của mọi DAO Firebase
public abstract class FirebaseDAO {
    protected static DatabaseReference con;
}

// Server: dao/DAO.java — cha của mọi DAO JDBC (giống bài mẫu khách sạn)
public abstract class DAO {
    protected static Connection conn;
    public DAO() { /* nếu conn == null thì DriverManager.getConnection(url, user, pass) */ }
}
```

### 2.4. CSDL và REST API

Chi tiết cột, khóa, câu lệnh tạo bảng: `docs/chung/03-csdl.md`.

- **MySQL (server):** `tblPlayer` (có `role`), `tblMatch`, `tblMathProblem`, `tblMatchPlayer`, `tblFriendship`.
- **SQLite (app):** `tblPlayer` (bản sao), `tblBotGame`.
- **SharedPreferences (app):** `"session"` (phiên đăng nhập).
- **Firebase Realtime Database:**

```
presence/{playerId}        displayName, totalPoints, status ("free" | "busy")
challenges/{challengeId}   challengerId, challengerName, challengerPoints, opponentId, opponentName,
                           status ("pending" | "accepted" | "rejected"), createdAt, matchId
matches/{matchId}          status ("playing" | "finished"), startedAt,
    questions/{i}          operand1, operand2, operator, answerOptions     // không có correctAnswer
    players/{playerId}     displayName, submitted, forfeited, choice
    nextMatchId, stoppedBy
```

- **Chữ ký hàm chi tiết** của UC03, UC04, UC07 (app + server) chốt ở `docs/hung/4-lop-thiet-ke.md`; code M2-01 → M2-05, M2-09, M2-10 theo đúng tài liệu này.

- **REST API** (JSON, không cần token):

| Phương thức | Đường dẫn | Dùng cho | Kết quả |
|---|---|---|---|
| `POST` | `/api/auth/register` | UC01 | 201, hoặc 409 nếu trùng tên đăng nhập |
| `POST` | `/api/auth/login` | UC02 | `Player` (có `role`), hoặc 401 |
| `GET` | `/api/players/{id}` | UC03, UC06 | `Player` kèm `totalPoints` |
| `GET` | `/api/players/ranking` | UC04 | Danh sách `Player` đã xếp hạng, kèm 3 tiêu chí |
| `POST` | `/api/matches` | UC03 | Tạo trận từ lời mời: `Match` (đề không có đáp án), hoặc 409 nếu một người không online / đang bận |
| `POST` | `/api/matches/{id}/submit` | UC03 | Nộp bài: `Match` sau khi chấm |
| `POST` | `/api/matches/{id}/forfeit` | UC03 | Bỏ trận: `Match` đã kết thúc |
| `POST` | `/api/matches/{id}/continue` | UC03 | Chơi tiếp / dừng: `Match` hiện tại hoặc trận mới |
| `GET` | `/api/matches/{id}` | UC03, UC04 | `Match` đầy đủ, có đáp án khi trận đã xong |
| `GET` | `/api/matches?playerId={id}` | UC04 | Các trận đã xong của người chơi, mới nhất trước, kèm 2 người chơi, không kèm đề |
| `GET` | `/api/friendships?playerId={id}` | UC07 | Lời mời và bạn bè của người chơi, kèm tổng điểm của 2 bên |
| `POST` | `/api/friendships` | UC07 | Gửi lời mời (`senderId`, `receiverUsername`): `Friendship`; 404 không có người chơi, 400 tự kết bạn, 409 đã có lời mời hoặc đã là bạn |
| `POST` | `/api/friendships/{id}/accept?playerId={id}` | UC07 | Đồng ý: `Friendship` đã `accepted`; 403 nếu không phải người nhận |
| `DELETE` | `/api/friendships/{id}?playerId={id}` | UC07 | Từ chối, rút lời mời, hủy kết bạn: 204; 403 nếu không phải một trong 2 người |
| `GET` | `/api/players?name={tên}` | UC06 | Danh sách người chơi (role = player) |
| `PUT` | `/api/players/{id}` | UC06 | Sửa tên hiển thị |
| `PUT` | `/api/players/{id}/password` | UC06 | Đặt lại mật khẩu |
| `DELETE` | `/api/players/{id}` | UC06 | 204, hoặc 409 nếu người chơi đã có trận |

### 2.5. Ba phiên bản kịch bản

| Phiên bản | Issue | Đối tượng xuất hiện | Đi kèm |
|---|---|---|---|
| v1 | M3-01, M3-02 | Người chơi / Quản trị viên ↔ Hệ thống | — |
| v2 | M3-05, M3-06 | Tác nhân, lớp `XxxView`, lớp thực thể | Biểu đồ tuần tự phân tích, **khớp từng bước** |
| v3 | M4-03, M4-04 | Tác nhân, boundary (`XxxActivity`, `XxxRestController`), control (DAO), entity | Biểu đồ tuần tự thiết kế, **khớp từng bước** |

---

## 3. Quy ước chung cho mọi issue

- **Branch:** tài liệu `docs/M3-01-kich-ban-v1`; code `feat/M2-02-thach-dau`. Tạo nhanh bằng `gh issue develop <số> --checkout`.
- **PR:** mô tả ghi `Closes #<số>`. PR phần chung (M1) phải được **Hoàng approve**. PR module riêng: người kia review nếu kịp; trước demo, ưu tiên merge cho kịp.
- **Trạng thái issue:** không đóng issue bằng tay.
  1. Làm xong thì mở PR có `Closes #<số>`, gắn nhãn `need-review` cho cả issue và PR, request review người còn lại.
  2. Tick các mục checklist đã đạt; mục không áp dụng thì gạch ngang và ghi lý do.
  3. Người review approve → merge → issue tự đóng.
- **Thư mục:**
  ```
  app/      ứng dụng Android
  server/   backend Spring Boot (Maven, Java 17)
  docs/
    chung/  phạm vi + use case, lớp thực thể, CSDL
    hung/   tài liệu UC03, UC04, UC07
    hoang/  tài liệu UC01, UC02, UC05, UC06
  ```
  - Kịch bản viết Markdown; biểu đồ vẽ PlantUML / Visual Paradigm / draw.io, xuất PNG cạnh file Markdown.
  - Không commit bí mật: `server/src/main/resources/firebase-service-account.json`, mật khẩu MySQL thật.
- **Definition of Done:**
  - Issue tài liệu (label `ooad`): đạt hết *Requirement*, tick hết checklist OOAD.
  - Issue code: build pass (app và server), chạy được trên máy ảo, đạt hết *Requirement*, tick hết checklist CONVENTION.
- **Checklist CONVENTION** (tự thêm vào cuối issue code):
  - [ ] Không dùng Hilt/Dagger, Room, RxJava/LiveData/ViewModel, ViewBinding/DataBinding
  - [ ] Chỉ dùng `findViewById`, `SQLiteOpenHelper`, `Thread` + `runOnUiThread`
  - [ ] Mỗi hàm < 20 dòng, callback lồng tối đa 2 cấp
  - [ ] Dùng Anonymous Class, KHÔNG dùng lambda
  - [ ] Comment tiếng Việt cho các khối code quan trọng
- **Checklist OOAD** (tự thêm vào cuối issue tài liệu):
  - [ ] Làm đúng các bước của `REPORT_CONVENTION.md` mục 3
  - [ ] Tên UC, lớp, bảng khớp mục 2 của `ISSUES_PLAN.md`
  - [ ] Kịch bản và biểu đồ tuần tự khớp từng bước (cùng số bước, cùng thứ tự)
  - [ ] Mỗi hình có chú thích "Hình x: ..." và 3–5 dòng giải thích
  - [ ] Đã lưu Markdown + PNG vào `docs/` và dán vào báo cáo cá nhân
  - [ ] Người còn lại đã review

---

## 4. MILESTONE 1 — Phần chung · Hùng làm, Hoàng duyệt

### Mô tả milestone (dùng làm description cho Milestone `M1 - Phan chung`)

**Người làm:** @HungNguyenBa1811. Bản 4 đã xong 21/10 → 24/10; bản 5 làm lại phần lệch đề trong **26/10**.
**Người duyệt:** @Minahn210, chậm nhất **tối 26/10**.

**Mục tiêu.** Làm trước mọi thứ cả 2 module cùng dùng, theo đúng đề 9:
- 3 tài liệu OOAD chung: phạm vi + use case tổng quát (2 tác nhân), lớp thực thể, CSDL.
- Khung server và khung app theo entity mới.
- Đăng ký, đăng nhập (có vai trò quản trị) chạy được từ app tới MySQL.
- Lõi câu hỏi và luật thắng thua.

**Trong phạm vi:** Những gì thuộc cột "Chung" ở mục 2.2; chữ ký hàm (thân `// TODO`) cho lớp riêng của mỗi người.

**Ngoài phạm vi:** Thân hàm của lớp riêng (milestone Demo); kịch bản và biểu đồ của từng chức năng (milestone 3, 4).

**Sản phẩm bàn giao:**

| Loại | Nội dung |
|---|---|
| Tài liệu | `docs/chung/01-pham-vi-use-case.md`, `02-lop-thuc-the.md`, `03-csdl.md` + PNG |
| Server | `DAO` cha, `PlayerDAO`, `AuthRestController`, entity mới, `schema.sql` mới (4 bảng, có tài khoản admin) |
| App | 6 entity, `DAO`, `DatabaseHelper` (`tblPlayer`, `tblBotGame`), `PlayerDAO`, `ApiDAO`, `PlayerApiDAO`, `SessionDAO`, `ProblemDAO`; 3 màn Đăng nhập, Đăng ký, Chính; vỏ các Activity còn lại |
| Test | `ProblemDAOTest`, `MatchTest` (luật thắng thua) |

**Điều kiện hoàn thành milestone:**
1. 7 issue M1-01 → M1-07 đã đóng; mọi PR đã được Hoàng approve.
2. App build pass, `gradlew test` pass; server `mvn test` pass.
3. Trên máy ảo: đăng ký → đăng nhập → vào màn Chính; đăng nhập admin → vào màn quản trị; tắt app mở lại vẫn giữ phiên.

**Rủi ro đã biết:**
- Đổi entity giữa chừng làm vỡ code cũ của Chơi online (phòng mã 6 số). Xóa hẳn code cũ thay vì sửa dần.
- Server trên Render ngủ khi không có request; lần gọi đầu có thể chậm 30–50 giây. Gọi thử trước khi demo.

### [M1-01] Phạm vi hệ thống + biểu đồ use case tổng quát · @HungNguyenBa1811
- **Phụ thuộc:** không có. **LÀM ĐẦU TIÊN.**
- **Mô tả:** Bước "Yêu cầu" của OOAD theo đúng đề 9, dùng chung cho cả 2 báo cáo. Hoàng duyệt.
- **Input:** Đề bài (`REPORT_CONVENTION.md` mục 1); mục 1 và 2 của tài liệu này.
- **Output:** `docs/chung/01-pham-vi-use-case.md` + `uc-tong-quat.png`:
  - Bảng thuật ngữ (đề bài, trạng thái rỗi / bận, lời mời, bài làm, kết quả trận, điểm trận, tổng điểm, bảng xếp hạng…); mục tiêu, phạm vi, ngoài phạm vi.
  - 2 nhóm người dùng, chức năng, luồng từng chức năng dạng `A → B → C` (có nhánh ngoại lệ).
  - Đối tượng và quan hệ giữa các đối tượng.
  - Bảng UC và biểu đồ use case tổng quát: **2 tác nhân, 6 UC chính**; các bước con phân rã ở tài liệu phân tích cá nhân.
- **Requirement:** Đủ mọi ý của đề: danh sách online có tên, tổng điểm, trạng thái; thách đấu bằng cách bấm tên; OK / Reject; server sinh cùng đề; luật thắng / hòa; điểm 1 / 0,5 / 0; hỏi chơi tiếp; bảng xếp hạng 3 tiêu chí; chơi với máy; quản lý thông tin người chơi. Hoàng đã approve.
- **Constraint:** Tên 6 UC dùng nguyên văn trong code, tài liệu và báo cáo.

### [M1-02] Lớp thực thể: phân tích + thiết kế (toàn hệ thống) · @HungNguyenBa1811
- **Phụ thuộc:** M1-01.
- **Mô tả:** Nguồn cho mục 3 trong báo cáo của cả 2 người. Hoàng duyệt.
- **Input:** M1-01; `REPORT_CONVENTION.md` mục 3 (bước A2, D1).
- **Output:** `docs/chung/02-lop-thuc-the.md` + 2 PNG:
  - **Phân tích (A2):** đoạn văn mô tả, trích danh từ, đánh giá giữ / loại, quan hệ số lượng, quan hệ đối tượng, danh sách 6 lớp `Player`, `MathProblem`, `Challenge`, `Match`, `MatchPlayer`, `BotGame`, biểu đồ.
  - **Thiết kế (D1), đủ 4 bước:** thêm `id`; thêm kiểu Java; `MatchPlayer` là thành phần của `Match`, `MathProblem` là thành phần của `Match` / `BotGame`; thêm thuộc tính đối tượng.
  - **Bảng "lớp nào dùng cho chức năng nào".**
- **Requirement:** Không còn đường liên kết trơn trên biểu đồ thiết kế. Hoàng đã approve.
- **Constraint:** Thuộc tính dẫn xuất (D9) viết thành phương thức: `Match.countCorrect`, `isOver`, `getFinishTime`, `getResult`, `getPoints`; `BotGame.getResult`.

### [M1-03] Thiết kế CSDL: MySQL + SQLite + Firebase · @HungNguyenBa1811
- **Phụ thuộc:** M1-02.
- **Mô tả:** Từ lớp thực thể thiết kế, làm theo 5 bước thiết kế CSDL. Nguồn cho mục 2 trong báo cáo: Hùng dùng phần MySQL + Firebase, Hoàng dùng phần SQLite và `tblPlayer` của MySQL. Hoàng duyệt.
- **Input:** `docs/chung/02-lop-thuc-the.md`; mục 2.4; `REPORT_CONVENTION.md` mục 3 (bước D2).
- **Output:** `docs/chung/03-csdl.md` + PNG:
  - **MySQL, đủ 5 bước:** `tblPlayer` (có `role`), `tblMatch`, `tblMathProblem`, `tblMatchPlayer`.
  - **SQLite:** `tblPlayer` (bản sao), `tblBotGame`.
  - **Câu lệnh `CREATE TABLE`** cho cả 2 CSDL và bảng truy vấn chính.
  - **Firebase:** cây `presence`, `challenges`, `matches`; ai ghi node nào; vì sao đề trên Firebase không có đáp án.
- **Requirement:** Mỗi bảng có khóa chính; khóa ngoại đúng chiều 1-n; không có cột dẫn xuất (D9). Hoàng đã approve.
- **Constraint:** Mật khẩu lưu dạng băm (BCrypt), không lưu bản rõ.

### [M1-04] Khung server Spring Boot theo entity mới · @HungNguyenBa1811
- **Phụ thuộc:** M1-03.
- **Mô tả:** Server đã chạy được trên Render từ bản 4. Bản 5 đổi entity, schema và khung controller theo thiết kế mới.
- **Input:** `docs/chung/03-csdl.md`; mục 2.2, 2.3, 2.4.
- **Output:**
  - **`server/db/schema.sql`** mới: 4 bảng, chèn sẵn 1 tài khoản `admin` (role = admin).
  - **Entity** `Player` (thêm `role`, `totalPoints`, `avgOpponentPoints`, `avgWinTime`, `status`), `MathProblem`, `Challenge`, `Match`, `MatchPlayer` đúng M1-02; xóa `roomCode`, `questionSeed`, `score`, `timeUp`.
  - **Khung** `MatchRestController`, `PlayerRestController`, `MatchDAO`, `ProblemDAO`, `FirebaseAdminDAO`: đủ chữ ký theo `docs/hung/4-lop-thiet-ke.md`, thân `// TODO`.
- **Requirement:** `mvn spring-boot:run` khởi động được, kết nối MySQL; server trên Render deploy lại thành công.
- **Constraint:** Không dùng JPA, không thêm tầng Service (D3). SQL dùng `PreparedStatement`.

### [M1-05] Khung app Android: entity, DAO cha, phiên đăng nhập, màn Chính · @HungNguyenBa1811
- **Phụ thuộc:** M1-03.
- **Mô tả:** Đổi phần chung phía app theo thiết kế mới để 2 module dựng tiếp lên. Hoàng duyệt.
- **Input:** M1-02, M1-03; mục 2.2, 2.3.
- **Output:**
  - **6 entity** theo M1-02; xóa `GameSession`, `GameRecord`, `GameConfig`.
  - **SQLite:** `DatabaseHelper` tạo `tblPlayer`, `tblBotGame` (tăng version, xóa bảng cũ); `PlayerDAO.saveOrUpdate(Player)`.
  - **HTTP:** `ApiDAO` thêm `get(path)`; `SessionDAO` lưu `playerId`, `username`, `displayName`.
  - **`MainActivity`:** dòng tên + tổng điểm, vùng danh sách online (`rvPlayers`, Hùng làm thân ở M2-02), nút Chơi với máy → `DifficultyActivity`, Bảng xếp hạng → `RankingActivity`, Đăng xuất.
  - **Xóa** `OnlineActivity`, `LobbyActivity`, `OnlineGameActivity`, `HistoryActivity`, `HistoryAdapter`, `SettingsActivity`, `GameActivity` và layout tương ứng; tạo vỏ `MatchActivity`, `RankingActivity`, `BotGameActivity`, `AdminActivity`, `PlayerDetailActivity`.
- **Requirement:** Build pass; các nút mở đúng màn; Đăng xuất xóa phiên và về màn Đăng nhập. Hoàng đã approve.
- **Constraint:** Gọi mạng và SQLite trong `Thread` + `runOnUiThread`; không dùng Retrofit, Volley, lambda.

### [M1-06] UC01 Đăng ký + UC02 Đăng nhập (app + server) · @HungNguyenBa1811
- **Phụ thuộc:** M1-04, M1-05.
- **Mô tả:** Chức năng tài khoản chạy trọn từ app tới MySQL. Code chung; **cả Hùng và Hoàng đều demo và trình bày** chức năng này.
- **Input:** Luồng "Đăng ký", "Đăng nhập" trong `docs/chung/01-pham-vi-use-case.md`; API mục 2.4.
- **Output:**
  - **Server:** `AuthRestController.register`, `login` (trả `role`); `PlayerDAO.findByUsername`, `insert` (role = player); BCrypt.
  - **App:** `LoginActivity`, `RegisterActivity`; `PlayerApiDAO.register`, `login`; đăng nhập xong lưu phiên + bản sao người chơi; `role = admin` thì mở `AdminActivity`, ngược lại mở `MainActivity`; có phiên thì vào thẳng.
- **Requirement:**
  - Trùng tên đăng nhập → "Tên đăng nhập đã được dùng".
  - Sai mật khẩu → "Sai tên đăng nhập hoặc mật khẩu".
  - Mật khẩu nhập lại không khớp → báo lỗi, không gọi server.
  - Đăng nhập tài khoản admin → màn quản trị.
  - Mất mạng → thông báo tiếng Việt, app không crash.
- **Constraint:** Không lưu mật khẩu ở app.

### [M1-07] Lõi câu hỏi và luật thắng thua: MathProblem, ProblemDAO, Match · @HungNguyenBa1811
- **Phụ thuộc:** M1-02.
- **Mô tả:** Lõi dùng chung cho UC03 (server sinh đề, chấm, phân thắng thua) và UC05 (app sinh đề, phân thắng thua với máy). Hoàng duyệt kỹ vì dùng lại trong Chơi với máy.
- **Input:** D5, D7, D8; M1-02.
- **Output:**
  - **`MathProblem`:** `getExpression()` (ví dụ `"12 + 7 = ?"`), `isCorrect(int)`.
  - **`ProblemDAO`** (bản app và bản server giống nhau): `generateProblems(int count)`, `generateProblem()`; toán tử `+ − ×`, `+ −` trong 1–99, `×` trong 2–15; 4 phương án khác nhau, không âm, có đáp án đúng, xáo trộn.
  - **`Match`:** `countCorrect(List<Integer>)`, `isOver()`, `getFinishTime(int)`, `getResult(int)`, `getPoints(int)` theo D5.
  - **`BotGame.getResult()`** cùng luật.
  - **`ProblemDAOTest`:** 1000 câu hợp lệ.
  - **`MatchTest`** (server): đúng hết + nhanh hơn → WIN; đúng hết + chậm hơn → LOSE; một người đúng hết, người kia sai → WIN dù chậm hơn; cả 2 sai → DRAW; cùng đúng hết, cùng thời gian → DRAW; bỏ trận → LOSE; `getPoints` 1 / 0,5 / 0; `isOver` khi mới 1 người nộp → false.
- **Requirement:** `gradlew test`, `mvn test` pass. Hoàng đã approve.
- **Constraint:** Entity không import `android.*`. Mọi chỗ random đi qua `this.random`.

---

## 5. MILESTONE 2 — Code module & Demo (28/10)

### Mô tả milestone (dùng làm description cho Milestone `M2 - Demo`)

**Người phụ trách:**
- Hùng: M2-01 → M2-05, M2-09, M2-10.
- Hoàng: M2-06, M2-07, làm trong 26–27/10.
- Cả 2: M2-08, tổng duyệt tối 27/10.

**Hạn:** **28/10/2026, ngày demo.**

**Mục tiêu.** Mỗi người có các chức năng của mình chạy được trên máy ảo, demo trơn tru và giải thích được code của chính mình.

**Trong phạm vi:** Thân hàm, layout, Adapter, câu SQL, API, truy vấn Firebase của UC03–UC07.

**Ngoài phạm vi:** Các chức năng ngoài phạm vi ở D1; sửa lớp "Chung" mà không qua PR được người kia duyệt; vẽ biểu đồ.

**Sản phẩm bàn giao:**
- Hùng:
  - App: `PresenceFirebaseDAO`, `ChallengeFirebaseDAO`, `MatchFirebaseDAO`, `MatchApiDAO`, `PlayerApiDAO.getById`, `getRanking`, `MainActivity` (danh sách online), `PlayerAdapter`, `MatchActivity`, `QuestionAdapter`; `HistoryActivity`, `PieChartView`, `MatchHistoryAdapter`, `RankingAdapter`, `MatchDetailActivity`, `ProblemAdapter`; `FriendshipApiDAO`, `FriendActivity`, `FriendRequestAdapter`, `FriendAdapter`.
  - Server: `MatchRestController`, `PlayerRestController.getById`, `getRanking`, `MatchDAO`, `ProblemDAO`, `FirebaseAdminDAO`, `FriendshipRestController`, `FriendshipDAO`.
- Hoàng:
  - App: `BotGameDAO`, `DifficultyActivity`, `BotGameActivity`, `AdminActivity`, `PlayerDetailActivity`, các hàm quản trị của `PlayerApiDAO`.
  - Server: các hàm quản trị của `PlayerRestController`, `PlayerDAO`.

**Điều kiện hoàn thành milestone:**
1. Mọi issue M2 đã đóng, hoặc đã cắt theo mục 8.
2. Trên `main`, mỗi người chạy được trọn các chức năng của mình, bắt đầu từ màn Đăng nhập, không crash.
3. Tìm `"-> "` trong `app/src/main/java` không ra kết quả nào (không dùng lambda).

**Rủi ro đã biết:**
- Chỉ còn 2 ngày, Hùng phải viết lại toàn bộ Chơi online. Ưu tiên: M2-01 → M2-02 → M2-03 → M2-05 → M2-04 → M2-09 → M1-08 → M2-10.
- Khi mất mạng đột ngột, Firebase có thể mất vài chục giây mới xóa node presence. Lúc demo nên dùng nút Thoát để minh họa bỏ trận.

### [M2-01] Hạ tầng Firebase: presence, lời mời, trận đang đấu + Security Rules · @HungNguyenBa1811
- **Phụ thuộc:** M1-06.
- **Mô tả:** Firebase đã nối từ bản 4. Bản 5 đổi cây dữ liệu sang `presence`, `challenges`, `matches/{matchId}` và viết lại Rules.
- **Input:** Mục 2.4; `docs/chung/03-csdl.md` mục 2.
- **Output:**
  - **App:** `PresenceFirebaseDAO` (`goOnline` + `onDisconnect().removeValue()`, `watchOnlinePlayers`, `stopWatching`, `toPlayer`); khung `ChallengeFirebaseDAO`, `MatchFirebaseDAO`.
  - **Server:** `FirebaseAdminDAO`: `readPresence`, `updateStatus`, `updatePoints`, `updateChallenge`, `writeMatch`, `writeSubmitted`, `writeFinished`, `writeChoice`, `writeNextMatch`, `writeStopped` qua REST API.
  - **Security Rules** (`database.rules.json`):
    - `presence/{id}`: chỉ app ghi tên và status free khi vào; status busy và totalPoints do server ghi.
    - `challenges`: tạo với status pending; người được mời chỉ đổi sang rejected.
    - `matches`: app chỉ đọc.
- **Requirement:** 2 máy ảo đăng nhập 2 tài khoản thì thấy nhau trong danh sách; tắt app 1 máy thì máy kia thấy người đó biến mất. Thử Rules Playground: app ghi vào `matches` → bị từ chối.
- **Constraint:** Không dùng Firestore, FirebaseUI, Cloud Functions, Firebase Authentication.

### [M2-02] UC03 Chơi online: danh sách người chơi online, thách đấu, nhận lời · @HungNguyenBa1811
- **Phụ thuộc:** M1-05, M2-01.
- **Mô tả:** Đoạn A, B của UC03 trong `docs/hung/5-kich-ban-v3.md`.
- **Input:** Luồng "Chơi online" trong `docs/chung/01-pham-vi-use-case.md`; `docs/hung/4-lop-thiet-ke.md`.
- **Output:**
  - **App:**
    - `MainActivity`: `onPlayersChanged` (thông tin của mình + `PlayerAdapter`), `onPlayerClick`, `onChallengeChanged`, `showChallengeDialog`, `acceptChallenge`, `openMatch`.
    - `PlayerAdapter` + `item_player.xml`: tên, tổng điểm, nhãn Rỗi (xanh) / Bận (xám); sắp tổng điểm giảm dần.
    - `ChallengeFirebaseDAO`: `sendChallenge`, `watchIncoming`, `watchChallenge`, `rejectChallenge`, `toChallenge`.
    - `MatchApiDAO.createMatch`; `PlayerApiDAO.getById`.
  - **Server:** `PlayerRestController.getById`; `MatchRestController.create` + `startMatch` (`readPresence` 2 người, `ProblemDAO.generateProblems(10)`, `MatchDAO.insert` trong transaction, `writeMatch`, `updateStatus` busy, `updateChallenge` accepted).
- **Requirement:**
  - Máy A bấm B (Rỗi) → máy B hiện hộp thoại lời mời → Đồng ý → cả 2 vào `MatchActivity` với cùng đề; trên máy C, A và B thành Bận.
  - Bấm người Bận → "<tên> đang bận".
  - B Từ chối → máy A "B đã từ chối lời mời".
  - MySQL có 1 dòng `tblMatch`, 10 dòng `tblMathProblem`, 2 dòng `tblMatchPlayer`.
- **Constraint:** Activity không gọi Firebase SDK trực tiếp, chỉ gọi DAO.

### [M2-03] UC03 Chơi online: thi đấu, nộp bài, server chấm và trọng tài · @HungNguyenBa1811
- **Phụ thuộc:** M1-07, M2-02.
- **Mô tả:** Đoạn C của UC03.
- **Input:** Trận đang `playing`; API mục 2.4; `docs/hung/4-lop-thiet-ke.md` mục 1.
- **Output:**
  - **App:**
    - `MatchActivity`: `startMatch`, `CountDownTimer` 60 s (`onTick`, `onFinish` tự nộp), `submitAnswers`, `showOpponentStatus`, `loadResult`, `showResult` (dùng `Match.getResult`, `getFinishTime`, `getPoints` trên bản server trả về).
    - `QuestionAdapter` + `item_question.xml`: phép tính, `RadioGroup` 4 phương án 2 x 2; `getAnswers` (câu chưa chọn là −1), `setLocked`.
    - Overlay kết quả: THẮNG / THUA / HÒA, số câu đúng và thời gian của 2 người, điểm trận, tổng điểm mới, nút Chơi tiếp / Dừng.
    - `MatchFirebaseDAO.watchMatch`, `stopWatching`, `toMatch`; `MatchApiDAO.submit`, `getById`.
  - **Server:** `MatchRestController.submit` (ghi thời điểm nhận, `countCorrect`, `updateSubmission`, `writeSubmitted`; khi `isOver`: `updateEndedAt`, `getTotalPoints`, `updatePoints`, `writeFinished`), `getById`; `MatchDAO.findById`, `updateSubmission`, `updateEndedAt`; `PlayerDAO.getTotalPoints`.
- **Requirement:**
  - 2 máy thấy cùng đề; máy này nộp thì máy kia hiện "<tên> đã nộp bài" dưới 1 giây.
  - Kết quả 2 máy khớp nhau và đúng luật D5; tổng điểm trên danh sách online cập nhật.
  - Hết 60 giây chưa nộp thì tự nộp.
  - `mvn test` pass, có `MatchTest`.
- **Constraint:** Không chấm bài, không phân thắng thua trong Activity; đáp án không có trên Firebase. Hủy timer và gỡ listener khi rời màn.

### [M2-04] UC03 Chơi online: thoát trận, mất kết nối, chơi tiếp · @HungNguyenBa1811
- **Phụ thuộc:** M2-03.
- **Mô tả:** Đoạn D và các ngoại lệ của UC03.
- **Input:** Trận hiện tại, id người chơi.
- **Output:**
  - **Chơi tiếp:** `MatchActivity.sendChoice`, `onNextMatch`, `openNextMatch`, `onStopped`; `MatchApiDAO.continueMatch`; server `MatchRestController.continueMatch` (synchronized, `waitingChoices`), `writeChoice`, `writeNextMatch`, `writeStopped`, trả 2 người về free khi dừng.
  - **Thoát trận:** nút Thoát / Back + `AlertDialog` "Thoát sẽ bị xử thua?" → `leaveMatch` → `MatchApiDAO.forfeit`; server `MatchRestController.forfeit`, `MatchDAO.updateForfeit`.
  - **Đối thủ mất kết nối:** thấy đối thủ biến khỏi `presence` khi trận đang đấu thì gọi `forfeit` thay cho đối thủ.
- **Requirement:**
  1. Cả 2 chọn Chơi tiếp → cả 2 vào trận mới với đề mới.
  2. Một người chọn Dừng → cả 2 về màn chính, Rỗi; máy kia "Đối thủ đã dừng chơi".
  3. B bấm Thoát → A thắng, +1 điểm.
  4. B tắt mạng → A được xử thắng (có thể mất vài chục giây).
- **Constraint:** Kết quả vẫn do server tính bằng `Match.getResult`.

### [M2-05] Bảng xếp hạng (nay là thẻ thứ 2 của UC04 Xem lịch sử đấu) · @HungNguyenBa1811
- **Phụ thuộc:** M2-03.
- **Mô tả:** Bảng xếp hạng mọi người chơi theo 3 tiêu chí của đề, tính từ các trận chơi online đã xong.
- **Input:** `GET /api/players/ranking`; `docs/hung/4-lop-thiet-ke.md` mục 2.
- **Output:**
  - **Server:** `PlayerRestController.getRanking`; `PlayerDAO.getRanking` (đọc người chơi role = player, `MatchDAO.findFinished`, cộng `getPoints`, tính trung bình điểm đối thủ khác nhau đã gặp, trung bình `getFinishTime` các trận thắng, sắp xếp 3 tiêu chí); `MatchDAO.findFinished`.
  - **App:** `PlayerApiDAO.getRanking`; `RankingActivity` + `RankingAdapter` + `item_ranking.xml`: hạng, tên, tổng điểm, TB điểm đối thủ, TB thời gian thắng ("—" nếu chưa thắng); dòng của mình tô nổi bật.
- **Requirement:**
  - Đấu xong 1 trận thì bảng xếp hạng đổi đúng.
  - 2 người cùng tổng điểm thì xếp theo tiêu chí 2; trùng tiêu chí 2 thì theo tiêu chí 3.
  - Mất mạng thì báo lỗi, không crash.
- **Constraint:** Không lưu tiêu chí vào CSDL; dùng lại `Match.getPoints`, `getResult`, `getFinishTime`.

### [M2-09] UC04 Xem lịch sử đấu: trận của tôi, biểu đồ tròn, chi tiết trận · @HungNguyenBa1811
- **Phụ thuộc:** M2-03, M2-05.
- **Mô tả:** Người chơi xem tỉ lệ thắng / hòa / thua và các trận online đã đấu, bấm vào một trận để xem lại đề và kết quả. Bảng xếp hạng của M2-05 chuyển thành thẻ thứ 2 trên cùng giao diện.
- **Input:** `docs/chung/01-pham-vi-use-case.md` chức năng "Xem lịch sử đấu"; `docs/hung/4-lop-thiet-ke.md` mục 2; API mục 2.4.
- **Output:**
  - **Server:** `MatchDAO.findByPlayer(playerId)`; `MatchRestController.getHistory` (`GET /api/matches?playerId=`), trận mới nhất trước, không kèm đề.
  - **App:**
    - `MatchApiDAO.getHistory(playerId)`; `Match.getOpponent(playerId)` ở cả app và server.
    - `HistoryActivity` + `activity_history.xml`: `TabLayout` 2 thẻ Trận của tôi, Bảng xếp hạng.
    - Thẻ Trận của tôi: card tóm tắt có `PieChartView` (vành khuyên 3 màu, giữa ghi tỉ lệ thắng), số trận thắng / hòa / thua / tổng; `MatchHistoryAdapter` + `item_match_history.xml` (dải màu kết quả, đối thủ, THẮNG / HÒA / THUA, điểm trận, số câu đúng 2 bên, thời điểm kết thúc, "Bỏ trận").
    - Thẻ Bảng xếp hạng: dùng lại `RankingAdapter` của M2-05. Xóa `RankingActivity`.
    - `MatchDetailActivity` + `activity_match_detail.xml`: kết quả, điểm trận, số câu đúng và thời gian 2 bên, `ProblemAdapter` + `item_problem.xml` hiện 10 câu kèm đáp án đúng (lấy bằng `MatchApiDAO.getById`).
    - `MainActivity`: nút Lịch sử đấu thay nút Bảng xếp hạng.
- **Requirement:**
  - Đấu xong 1 trận thì mở lại lịch sử thấy trận mới ở đầu, biểu đồ đổi đúng.
  - Chưa có trận nào thì hiện "Bạn chưa đấu trận nào", không crash.
  - Trận có người bỏ trận hiện đúng kết quả và chữ "Bỏ trận".
  - Mất mạng thì báo lỗi, không crash.
- **Constraint:** Biểu đồ tròn tự vẽ bằng `Canvas` (không thêm thư viện); tỉ lệ đếm bằng `Match.getResult`, không lưu; gọi REST trong `Thread`.

### [M1-08] Bổ sung phần chung cho bản 6: `Friendship`, `tblFriendship`, `Match.getOpponent`, `ApiDAO.delete` · @HungNguyenBa1811
- **Phụ thuộc:** M1-02, M1-03.
- **Mô tả:** Phần chung cần cho UC04, UC07. Chỉ thêm, không sửa lớp hay bảng đã có. Hoàng duyệt.
- **Input:** `docs/chung/02-lop-thuc-the.md`, `docs/chung/03-csdl.md` bản 6.
- **Output:**
  - Entity `Friendship` (app + server): `id`, `status`, `createdAt`, `sender`, `receiver`; `getFriend(playerId)`, `canAccept(playerId)`; unit test.
  - `Match.getOpponent(playerId)` (app + server); unit test.
  - `server/db/schema.sql` thêm `tblFriendship`; file `server/db/migrate-6-friendship.sql` chỉ có `CREATE TABLE IF NOT EXISTS tblFriendship` để chạy trên CSDL đang dùng mà không xóa dữ liệu.
  - `ApiDAO.delete(path)` (app).
- **Requirement:** `gradlew test`, `mvn test` pass; chạy migrate trên Aiven không mất dữ liệu cũ; xóa người chơi có bạn bè không bị lỗi khóa ngoại.
- **Constraint:** Không đổi cột của `tblPlayer`; phần code của Hoàng không phải sửa.

### [M2-10] UC07 Quản lý bạn bè · @HungNguyenBa1811
- **Phụ thuộc:** M1-08, M2-01.
- **Mô tả:** Người chơi gửi lời mời kết bạn bằng tên đăng nhập, đồng ý hoặc từ chối lời mời, xem bạn bè kèm trạng thái online, hủy kết bạn.
- **Input:** `docs/chung/01-pham-vi-use-case.md` chức năng "Quản lý bạn bè"; `docs/hung/4-lop-thiet-ke.md` mục 3; API mục 2.4.
- **Output:**
  - **Server:** `FriendshipDAO` (`findByPlayer`, `findById`, `existsBetween`, `insert`, `updateStatus`, `delete`); `FriendshipRestController` (4 API ở mục 2.4).
  - **App:** `FriendshipApiDAO`; `FriendActivity` + `activity_friend.xml` (ô tên đăng nhập, nút Kết bạn, mục Lời mời kết bạn, mục Bạn bè); `FriendRequestAdapter` + `item_friend_request.xml`; `FriendAdapter` + `item_friend.xml` (chấm Online / Offline lấy từ `PresenceFirebaseDAO`, tổng điểm, nút hủy); `MainActivity` thêm nút Bạn bè.
- **Requirement:**
  - 2 máy: máy 1 gửi lời mời, máy 2 mở Bạn bè thấy lời mời, đồng ý thì cả 2 thấy nhau trong danh sách bạn bè.
  - Bạn đang mở app thì chấm xanh, tắt app thì chuyển xám.
  - Sai tên, tự kết bạn, gửi trùng: báo đúng câu ở `docs/chung/01-pham-vi-use-case.md`.
  - Hủy kết bạn có hỏi xác nhận.
- **Constraint:** Chỉ người nhận mới đồng ý được (`Friendship.canAccept`, server kiểm tra lại); gọi REST trong `Thread`; gỡ listener presence trong `onDestroy()`.

### [M2-06] UC05 Chơi với máy · @Minahn210
- **Phụ thuộc:** M1-05, M1-07.
- **Mô tả:** Người chơi chọn độ khó của máy, làm đề 10 câu như trận online, máy giả lập bài làm theo D8. Chạy được khi tắt mạng.
- **Input:** `SessionDAO`; `ProblemDAO`, `MathProblem`, `BotGame.getResult` (M1-07); bố cục màn thi đấu ở `docs/hung/4-lop-thiet-ke.md` mục 1.1 để thống nhất giao diện.
- **Output:**
  - **`DifficultyActivity`:** 3 nút Dễ / Trung bình / Khó.
  - **`BotGameActivity`** + layout: ô thời gian 60 s, dòng "Máy đang làm bài" / "Máy đã nộp bài" (đổi khi tới thời gian nộp của máy), danh sách 10 câu 4 phương án, nút Nộp bài, nút Thoát; overlay kết quả THẮNG / THUA / HÒA, số câu đúng và thời gian của mình và của máy, nút Chơi lại / Về menu.
  - Giả lập máy: số câu đúng theo xác suất, thời gian nộp ngẫu nhiên theo D8.
  - **`BotGameDAO extends DAO`:** `insert(BotGame)`.
- **Requirement:**
  - Kết quả đúng luật D5.
  - Ván được lưu vào `tblBotGame`.
  - Chơi được khi tắt mạng.
- **Constraint:** Luật thắng thua nằm ở `BotGame.getResult`, Activity chỉ gọi; SQLite trong `Thread`; hủy timer trong `onDestroy()`.

### [M2-07] UC06 Quản lý thông tin người chơi (quản trị viên) · @Minahn210
- **Phụ thuộc:** M1-06.
- **Mô tả:** Quản trị viên xem danh sách người chơi, tìm theo tên, sửa tên hiển thị, đặt lại mật khẩu, xóa tài khoản.
- **Input:** Luồng "Quản lý thông tin người chơi" trong `docs/chung/01-pham-vi-use-case.md`; API UC06 ở mục 2.4.
- **Output:**
  - **Server:** `PlayerRestController.search`, `update`, `resetPassword`, `delete`; `PlayerDAO.search`, `updateDisplayName`, `updatePassword`, `delete` (không xóa người đã có trận, trả 409).
  - **App:** `AdminActivity` (ô tìm, danh sách người chơi: tên đăng nhập, tên hiển thị, tổng điểm, ngày tạo; nút Đăng xuất); `PlayerDetailActivity` (tên đăng nhập, ô tên hiển thị, tổng điểm, số trận thắng / hòa / thua, nút Lưu, Đặt lại mật khẩu, Xóa tài khoản); các hàm tương ứng trong `PlayerApiDAO`.
- **Requirement:**
  - Sửa tên hiển thị thì danh sách online và bảng xếp hạng hiện tên mới.
  - Xóa người chưa có trận thành công; người đã có trận → "Không xóa được người chơi đã có trận đấu".
  - Đặt lại mật khẩu xong đăng nhập được bằng mật khẩu mới.
- **Constraint:** Mật khẩu mới băm BCrypt ở server; tài khoản admin không có trong danh sách.

### [M2-08] Tổng duyệt demo · @HungNguyenBa1811 @Minahn210
- **Phụ thuộc:** M2-01 → M2-07.
- **Mô tả:** Tối 27/10, chạy thử đúng như buổi demo trên bản `main` cuối cùng.
- **Input:** `main` đã merge đủ; server trên Render đã deploy bản mới.
- **Output:**
  - **Kịch bản demo của mỗi người** (dán vào comment issue), kèm dữ liệu chuẩn bị sẵn: 4 tài khoản người chơi, 1 tài khoản admin, vài trận để bảng xếp hạng có dữ liệu.
  - **Hùng** chạy UC03, UC04, UC07 trên 2–3 máy ảo (đăng nhập sẵn). Chuẩn bị phần giải thích, mở code ra nói được:
    1. Server sinh đề, đáp án không lên Firebase.
    2. Server chấm bằng `Match.countCorrect`, phân thắng thua bằng `Match.getResult`; chạy `MatchTest`.
    3. Thời gian làm bài đo bằng đồng hồ server.
    4. Bảng xếp hạng và tỉ lệ thắng trong lịch sử đấu dùng lại luật của trận.
    5. Quan hệ bạn bè lưu MySQL, trạng thái online lấy từ Firebase.
  - **Hoàng** chạy Đăng ký, Đăng nhập (cả tài khoản admin), UC05 (gồm lúc tắt mạng), UC06.
  - Lỗi phát hiện được thành issue `bug`, sửa ngay trong tối 27/10.
- **Requirement:** Mỗi người chạy trọn các chức năng của mình từ màn Đăng nhập, không crash, 2 lần liên tiếp.
- **Constraint:** Không thêm tính năng mới.

---

## 6. MILESTONE 3 — Phân tích cá nhân · label `ooad`

### Mô tả milestone (dùng làm description cho Milestone `M3 - Phan tich`)

**Người phụ trách:** Hùng làm UC03, UC04, UC07; Hoàng làm UC01, UC02, UC05, UC06.
**Thời gian:** 29/10 → **04/11/2026**. Phần UC03, UC04, UC07 của Hùng đã viết trước để dẫn code.

**Mục tiêu.** Bước phân tích OOAD cho từng chức năng demo:
- Biểu đồ use case chi tiết của module.
- Kịch bản v1.
- Biểu đồ lớp phân tích.
- Kịch bản v2 kèm biểu đồ tuần tự phân tích.

Lớp thực thể đã có ở M1-02; thiếu lớp thì mở PR bổ sung tài liệu chung.

**Ngoài phạm vi:** DAO, Activity, REST → milestone 4.

**Sản phẩm bàn giao:** `docs/hung/` và `docs/hoang/`: `1-kich-ban-v1.md`, `2-lop-phan-tich.md`, `3-kich-ban-v2.md` + PNG.

**Điều kiện hoàn thành milestone:**
1. 6 issue M3-01 → M3-06 đã đóng.
2. Mỗi phương thức trên biểu đồ tuần tự v2 đều có trên biểu đồ lớp phân tích tương ứng.

### [M3-01] Use case chi tiết + kịch bản v1: Chơi online, Xem lịch sử đấu, Quản lý bạn bè · @HungNguyenBa1811
- **Phụ thuộc:** M1-01.
- **Mô tả:** Mục 1 trong báo cáo của Hùng. Làm theo `REPORT_CONVENTION.md` bước A1.
- **Input:** M1-01; đề bài.
- **Output:** `docs/hung/1-kich-ban-v1.md`:
  - **Biểu đồ use case chi tiết module Đấu đối kháng:** UC03 include "Xem danh sách người chơi online", include "Vào trận đấu" (trừu tượng, 2 con "Gửi lời thách đấu", "Nhận lời thách đấu"), include "Thi đấu"; "Từ chối lời thách đấu" extend "Nhận lời thách đấu"; "Thoát trận", "Chơi tiếp" extend "Thi đấu".
  - **Biểu đồ use case chi tiết module Xem lịch sử đấu:** UC04 include "Xem trận của tôi"; "Xem chi tiết trận" extend "Xem trận của tôi"; "Xem bảng xếp hạng" extend UC04.
  - **Biểu đồ use case chi tiết module Bạn bè:** UC07 include "Xem danh sách bạn bè"; "Gửi lời mời kết bạn", "Đồng ý lời mời kết bạn", "Từ chối lời mời kết bạn", "Hủy kết bạn" extend UC07.
  - **Đặc tả + kịch bản chuẩn v1 + ngoại lệ** cho UC03, UC04, UC07. Ngoại lệ tối thiểu: mời người bận; từ chối; hết giờ tự nộp; thoát trận; đối thủ mất kết nối; một người đúng hết; hòa; dừng chơi; chưa có trận nào; trận có người bỏ; sai tên đăng nhập; tự kết bạn; đã có lời mời; từ chối; hủy kết bạn.
- **Requirement:** Không có tên lớp, tên hàm trong kịch bản v1; mỗi bước của hệ thống ghi rõ giao diện gồm thành phần gì.
- **Constraint:** Dùng đúng tên UC ở M1-01.

### [M3-02] Use case chi tiết + kịch bản v1: Chơi với máy, Quản lý thông tin người chơi, Đăng nhập · @Minahn210
- **Phụ thuộc:** M2-08.
- **Mô tả:** Mục 1 trong báo cáo của Hoàng. Làm theo `REPORT_CONVENTION.md` bước A1.
- **Input:** App thật sau demo; M1-01; `docs/hung/1-kich-ban-v1.md` để tham khảo cách viết.
- **Output:** `docs/hoang/1-kich-ban-v1.md`:
  - **Biểu đồ use case chi tiết:** module Tài khoản (Đăng ký, Đăng nhập), module Chơi với máy (UC05 include "Chọn độ khó", include "Thi đấu với máy") và module Quản trị (UC06 include "Tìm người chơi"; extend "Sửa thông tin", "Đặt lại mật khẩu", "Xóa tài khoản").
  - **Đặc tả + kịch bản chuẩn v1 + ngoại lệ** cho UC05, UC06, Đăng nhập, Đăng ký. Ngoại lệ tối thiểu: hết giờ tự nộp; máy nộp trước; hòa; chơi khi mất mạng; xóa người đã có trận; tên hiển thị bỏ trống.
- **Requirement:** Giống M3-01.
- **Constraint:** Dùng đúng tên UC ở M1-01.

### [M3-03] Biểu đồ lớp phân tích: Chơi online, Xem lịch sử đấu, Quản lý bạn bè · @HungNguyenBa1811
- **Phụ thuộc:** M3-01.
- **Mô tả:** Bước A3 trong `REPORT_CONVENTION.md`. Mỗi UC một biểu đồ gồm các lớp View và lớp thực thể liên quan.
- **Input:** Kịch bản v1 (M3-01); `docs/chung/02-lop-thuc-the.md`.
- **Output:** `docs/hung/2-lop-phan-tich.md` + PNG. View: `MainView`, `MatchView`, `MatchResultView`, `HistoryView`, `MatchDetailView`, `FriendView`; thành phần đặt tiền tố `in` / `out` / `sub`.
- **Requirement:** Mỗi thành phần giao diện trong kịch bản v1 đều có trong View tương ứng.
- **Constraint:** Chưa có DAO, Activity, REST.

### [M3-04] Biểu đồ lớp phân tích: Chơi với máy, Quản lý thông tin người chơi, Đăng nhập · @Minahn210
- **Phụ thuộc:** M3-02.
- **Mô tả:** Như M3-03, cho UC05, UC06, Đăng nhập, Đăng ký.
- **Input:** Kịch bản v1 (M3-02); `docs/chung/02-lop-thuc-the.md`; `docs/hung/2-lop-phan-tich.md` để tham khảo.
- **Output:** `docs/hoang/2-lop-phan-tich.md` + PNG. View dự kiến: `DifficultyView`, `BotGameView`, `BotResultView`, `AdminView`, `PlayerDetailView`, `LoginView`, `RegisterView`.
- **Requirement:** Giống M3-03.
- **Constraint:** Giống M3-03.

### [M3-05] Kịch bản v2 + biểu đồ tuần tự phân tích: Chơi online, Xem lịch sử đấu, Quản lý bạn bè · @HungNguyenBa1811
- **Phụ thuộc:** M3-03.
- **Mô tả:** Bước A4 trong `REPORT_CONVENTION.md`.
- **Input:** Kịch bản v1 (M3-01); biểu đồ lớp phân tích (M3-03).
- **Output:** `docs/hung/3-kich-ban-v2.md` + PNG. UC03 chia 4 đoạn: Gửi lời thách đấu, Nhận lời thách đấu, Thi đấu, Chơi tiếp. UC04 chia 2 đoạn: Xem trận của tôi và chi tiết trận, Xem bảng xếp hạng. UC07 chia 2 đoạn: Gửi lời mời kết bạn, Đồng ý lời mời kết bạn.
- **Requirement:** Số bước bằng số thông điệp, cùng thứ tự.
- **Constraint:** Chỉ 3 loại đối tượng: tác nhân, View, thực thể. Biểu đồ chỉ vẽ kịch bản chuẩn.

### [M3-06] Kịch bản v2 + biểu đồ tuần tự phân tích: Chơi với máy, Quản lý thông tin người chơi, Đăng nhập · @Minahn210
- **Phụ thuộc:** M3-04.
- **Mô tả:** Như M3-05, cho UC05, UC06, Đăng nhập, Đăng ký.
- **Input:** Kịch bản v1 (M3-02); biểu đồ lớp phân tích (M3-04); `docs/hung/3-kich-ban-v2.md` để tham khảo.
- **Output:** `docs/hoang/3-kich-ban-v2.md` + PNG.
- **Requirement:** Giống M3-05.
- **Constraint:** Giống M3-05.

---

## 7. MILESTONE 4 — Thiết kế cá nhân & Báo cáo

### Mô tả milestone (dùng làm description cho Milestone `M4 - Thiet ke & Bao cao`)

**Người phụ trách:** Hùng làm UC03, UC04, UC07 và báo cáo của Hùng; Hoàng làm UC01, UC02, UC05, UC06 và báo cáo của Hoàng.
**Thời gian:** 05/11 → **15/11/2026**. Biểu đồ thiết kế xong trước 11/11; 12–15/11 hoàn thiện báo cáo và đọc chéo.

**Mục tiêu.** Biểu đồ lớp chi tiết và tuần tự chi tiết cho từng chức năng, **khớp code thật**, rồi ghép thành báo cáo cá nhân đúng khung đề.
- Chức năng có gọi server (UC03, UC04, UC06, UC07, Đăng nhập) vẽ theo quy ước 2 package "Client (Android)" và "Server (Spring Boot)" ở `REPORT_CONVENTION.md`.
- Chức năng chỉ chạy trên app (UC05) vẽ MVC thuần.

**Ngoài phạm vi:** Thêm tính năng; sửa code chỉ để biểu đồ đẹp hơn (lệch thì sửa biểu đồ theo code).

**Sản phẩm bàn giao:**
- `docs/hung/4-lop-thiet-ke.md`, `5-kich-ban-v3.md`; `docs/hoang/` tương tự.
- `docs/BaoCao_Hung.docx`, `docs/BaoCao_Hoang.docx`.

**Điều kiện hoàn thành milestone:**
1. 6 issue M4-01 → M4-06 đã đóng.
2. Mỗi báo cáo tick đủ checklist ở `REPORT_CONVENTION.md` mục 5.
3. Mọi tên lớp, hàm, bảng, API trong báo cáo đều có trong code ở `main`.

### [M4-01] Thiết kế giao diện + biểu đồ lớp chi tiết: Chơi online, Xem lịch sử đấu, Quản lý bạn bè · @HungNguyenBa1811
- **Phụ thuộc:** M3-03.
- **Mô tả:** Bước D3 trong `REPORT_CONVENTION.md`. Mục 4.x.1 và 4.x.2 trong báo cáo của Hùng. Bản đầu viết trước code; sau demo thay ảnh phác thảo bằng ảnh app thật và đối chiếu chữ ký với code.
- **Input:** Biểu đồ lớp phân tích (M3-03); M1-02, M1-03; code thật trên `main`.
- **Output:** `docs/hung/4-lop-thiet-ke.md` + PNG. Với mỗi UC:
  - **Thiết kế giao diện:** ảnh app thật; bảng thành phần (`id` trong layout, loại View, chức năng, tên ở bước phân tích).
  - **Biểu đồ lớp chi tiết, 2 package:**
    - **Client:** `MainActivity`, `PlayerAdapter`, `MatchActivity`, `QuestionAdapter`, `HistoryActivity`, `PieChartView`, `MatchHistoryAdapter`, `RankingAdapter`, `MatchDetailActivity`, `ProblemAdapter`, `FriendActivity`, `FriendRequestAdapter`, `FriendAdapter`; `FirebaseDAO` ← `PresenceFirebaseDAO`, `ChallengeFirebaseDAO`, `MatchFirebaseDAO`; `ApiDAO` ← `PlayerApiDAO`, `MatchApiDAO`, `FriendshipApiDAO`; `SessionDAO`; entity.
    - **Server:** `PlayerRestController`, `MatchRestController`, `FriendshipRestController`; `DAO` ← `PlayerDAO`, `MatchDAO`, `FriendshipDAO`; `ProblemDAO`; `FirebaseAdminDAO`; entity.
    - Nối 2 package bằng liên kết `HTTP/JSON` giữa các cặp `XxxApiDAO` – `XxxRestController`.
  - **Mô tả từng lớp 1–2 dòng.**
- **Requirement:** Mỗi View ở M3-03 ánh xạ sang đúng một Activity hoặc overlay; tên lớp, hàm, API trùng với code.
- **Constraint:** Không vẽ lớp của framework (Spring, Firebase SDK) ngoài kiểu dữ liệu của thuộc tính.

### [M4-02] Thiết kế giao diện + biểu đồ lớp chi tiết: Chơi với máy, Quản lý thông tin người chơi, Đăng nhập · @Minahn210
- **Phụ thuộc:** M3-04, M2-08.
- **Mô tả:** Như M4-01, cho UC05, UC06, Đăng nhập. Mục 4.x.1 và 4.x.2 trong báo cáo của Hoàng.
- **Input:** Code thật trên `main`; biểu đồ lớp phân tích (M3-04); M1-02, M1-03; `docs/hung/4-lop-thiet-ke.md` để tham khảo.
- **Output:** `docs/hoang/4-lop-thiet-ke.md` + PNG:
  - Chơi với máy (1 package): `DifficultyActivity`, `BotGameActivity`; `DAO` ← `BotGameDAO`; `ProblemDAO`; `SessionDAO`; `Player`, `BotGame`, `MathProblem`.
  - Quản lý thông tin người chơi (2 package): `AdminActivity`, `PlayerDetailActivity`; `ApiDAO` ← `PlayerApiDAO`; server `PlayerRestController`, `DAO` ← `PlayerDAO`; `Player`, `Match`, `MatchPlayer`.
  - Mô tả từng lớp 1–2 dòng.
- **Requirement:** Giống M4-01.
- **Constraint:** Tên lớp và hàm trùng với code.

### [M4-03] Kịch bản v3 + biểu đồ tuần tự chi tiết: Chơi online, Xem lịch sử đấu, Quản lý bạn bè · @HungNguyenBa1811
- **Phụ thuộc:** M3-05, M4-01.
- **Mô tả:** Bước D4 trong `REPORT_CONVENTION.md`. Mục 4.x.3 trong báo cáo của Hùng.
- **Input:** Kịch bản v2 (M3-05); biểu đồ lớp chi tiết (M4-01); code thật.
- **Output:** `docs/hung/5-kich-ban-v3.md` + PNG. UC03, UC04, UC07 chia đoạn như v2; kịch bản v3 đánh số, tên hàm thật; biểu đồ đủ boundary – control – entity ở cả client và server.
- **Requirement:** Số bước bằng số thông điệp, cùng thứ tự.
- **Constraint:** Mỗi lời gọi REST vẽ là **một** thông điệp từ `XxxApiDAO` tới `XxxRestController`; callback Firebase theo quy ước ở `REPORT_CONVENTION.md`.

### [M4-04] Kịch bản v3 + biểu đồ tuần tự chi tiết: Chơi với máy, Quản lý thông tin người chơi, Đăng nhập · @Minahn210
- **Phụ thuộc:** M3-06, M4-02.
- **Mô tả:** Như M4-03, cho UC05, UC06, Đăng nhập. Mục 4.x.3 trong báo cáo của Hoàng.
- **Input:** Kịch bản v2 (M3-06); biểu đồ lớp chi tiết (M4-02); code thật; `docs/hung/5-kich-ban-v3.md` để tham khảo.
- **Output:** `docs/hoang/5-kich-ban-v3.md` + PNG. `CountDownTimer` và `Thread` vẽ theo quy ước ở `REPORT_CONVENTION.md`.
- **Requirement:** Giống M4-03.
- **Constraint:** Mọi hàm trên biểu đồ có trong M4-02.

### [M4-05] Báo cáo cá nhân – Hùng (Chơi online, Xem lịch sử đấu, Quản lý bạn bè) · @HungNguyenBa1811
- **Phụ thuộc:** M1-02, M1-03, M3-01, M4-01, M4-03.
- **Mô tả:** Báo cáo thiết kế cá nhân của Hùng, đúng khung ở `REPORT_CONVENTION.md` mục 2. Bản nháp Markdown: `docs/hung/BaoCao_Hung.md`.
- **Input:** `REPORT_CONVENTION.md`; tài liệu trong `docs/chung/` và `docs/hung/`.
- **Output:**
  - Một file báo cáo online của riêng Hùng (dán link vào issue); bản cuối `docs/BaoCao_Hung.docx`.
  - Thứ tự mục bắt buộc:

    | Mục | Nội dung | Lấy từ |
    |---|---|---|
    | 1 | Mô tả chi tiết các chức năng bằng ngôn ngữ tự nhiên | M3-01 |
    | 2 | Thiết kế CSDL: MySQL `tblPlayer`, `tblMatch`, `tblMathProblem`, `tblMatchPlayer`, `tblFriendship` và cây Firebase | M1-03 |
    | 3 | Thiết kế lớp thực thể: `Player`, `Challenge`, `Match`, `MatchPlayer`, `MathProblem`, `Friendship` | M1-02 |
    | 4.1.1 | Chơi online: thiết kế giao diện | M4-01 |
    | 4.1.2 | Chơi online: biểu đồ lớp chi tiết (client + server) | M4-01 |
    | 4.1.3 | Chơi online: kịch bản v3 + biểu đồ tuần tự chi tiết | M4-03 |
    | 4.2.1 | Xem lịch sử đấu: thiết kế giao diện | M4-01 |
    | 4.2.2 | Xem lịch sử đấu: biểu đồ lớp chi tiết (client + server) | M4-01 |
    | 4.2.3 | Xem lịch sử đấu: kịch bản v3 + biểu đồ tuần tự chi tiết | M4-03 |
    | 4.3.1 | Quản lý bạn bè: thiết kế giao diện | M4-01 |
    | 4.3.2 | Quản lý bạn bè: biểu đồ lớp chi tiết (client + server) | M4-01 |
    | 4.3.3 | Quản lý bạn bè: kịch bản v3 + biểu đồ tuần tự chi tiết | M4-03 |

- **Requirement:** Mọi mục đã tick; tick đủ checklist `REPORT_CONVENTION.md` mục 5; Hoàng đã đọc lại.
- **Constraint:** Bài cá nhân, tự viết; phần CSDL và lớp thực thể chỉ lấy phần liên quan chức năng của mình.

### [M4-06] Báo cáo cá nhân – Hoàng (Chơi với máy, Quản lý thông tin người chơi, Đăng nhập) · @Minahn210
- **Phụ thuộc:** M1-02, M1-03, M3-02, M4-02, M4-04.
- **Mô tả:** Báo cáo thiết kế cá nhân của Hoàng, đúng khung ở `REPORT_CONVENTION.md` mục 2.
- **Input:** `REPORT_CONVENTION.md`; tài liệu trong `docs/chung/` và `docs/hoang/`.
- **Output:**
  - Một file báo cáo online của riêng Hoàng (dán link vào issue); bản cuối `docs/BaoCao_Hoang.docx`.
  - Thứ tự mục bắt buộc:

    | Mục | Nội dung | Lấy từ |
    |---|---|---|
    | 1 | Mô tả chi tiết các chức năng bằng ngôn ngữ tự nhiên | M3-02 |
    | 2 | Thiết kế CSDL: SQLite `tblPlayer`, `tblBotGame`; MySQL `tblPlayer` | M1-03 |
    | 3 | Thiết kế lớp thực thể: `Player`, `BotGame`, `MathProblem`, `Match`, `MatchPlayer` | M1-02 |
    | 4.1.1 | Chơi với máy: thiết kế giao diện | M4-02 |
    | 4.1.2 | Chơi với máy: biểu đồ lớp chi tiết | M4-02 |
    | 4.1.3 | Chơi với máy: kịch bản v3 + biểu đồ tuần tự chi tiết | M4-04 |
    | 4.2.1 | Quản lý thông tin người chơi: thiết kế giao diện | M4-02 |
    | 4.2.2 | Quản lý thông tin người chơi: biểu đồ lớp chi tiết (client + server) | M4-02 |
    | 4.2.3 | Quản lý thông tin người chơi: kịch bản v3 + biểu đồ tuần tự chi tiết | M4-04 |
    | 4.3.1 | Đăng nhập: thiết kế giao diện | M4-02 |
    | 4.3.2 | Đăng nhập: biểu đồ lớp chi tiết (client + server) | M4-02 |
    | 4.3.3 | Đăng nhập: kịch bản v3 + biểu đồ tuần tự chi tiết | M4-04 |

- **Requirement:** Giống M4-05; Hùng đã đọc lại.
- **Constraint:** Giống M4-05.

---

## 8. Thứ tự, lịch & cắt phạm vi

```
Phần chung (Hùng, Hoàng duyệt):  M1-01 ─> M1-02 ─┬─> M1-03 ─┬─> M1-04 ─┐
                                                 │          └─> M1-05 ─┴─> M1-06
                                                 └─> M1-07
Hùng :  M1-06 ─> M2-01 ─> M2-02 ─> M2-03 ─┬─> M2-04 ──────────────────────┐
                                          └─> M2-05 ─> M2-09 ─> M1-08 ─> M2-10 ─┤
Hoàng:  (M1-05 + M1-07) ─> M2-06 ─> M2-07 ───────────┴─> M2-08 ─> DEMO 28/10
Sau demo: M3-01/02 ─> M3-03/04 ─> M3-05/06 ─> M4-01/02 ─> M4-03/04 ─> M4-05/06 ─> NỘP 15/11
```

| Ngày | Hùng | Hoàng |
|---|---|---|
| 26/10 | Tài liệu bản 5 (M1-01 → M1-03, M3, M4 phần UC03, UC04); M1-04, M1-05, M1-07 theo entity mới; M2-01 | Duyệt tài liệu chung; M2-06 Chơi với máy |
| 27/10 | M2-02, M2-03, M2-05, M2-04; tài liệu bản 6; M2-09, M1-08, M2-10; tối: M2-08 | M2-07 Quản lý thông tin người chơi; tối: M2-08 |
| **28/10** | **DEMO** | **DEMO** |
| 29/10 – 04/11 | Đối chiếu tài liệu UC03, UC04, UC07 với code (M3-01, M3-03, M3-05) | M3-02, M3-04, M3-06 |
| 05/11 – 11/11 | M4-01, M4-03 (ảnh app thật); `tblAnswer` nếu kịp | M4-02, M4-04 |
| 12/11 – 15/11 | M4-05, đọc báo cáo của Hoàng | M4-06, đọc báo cáo của Hùng → **nộp 15/11** |

**Nếu phải cắt phạm vi để kịp demo, bỏ theo thứ tự:**
1. Đối thủ mất kết nối tự xử thua (giữ "bấm Thoát thì bị xử thua").
2. Security Rules chi tiết (giữ "app không ghi được `matches`").
3. Tìm theo tên ở màn quản trị.
4. UC07 Quản lý bạn bè (đưa vào báo cáo, demo nếu kịp).
5. Màn chi tiết trận của UC04 (giữ danh sách và biểu đồ tròn).

Không cắt: server sinh đề + chấm + trọng tài, chơi tiếp, bảng xếp hạng 3 tiêu chí, vì đây là yêu cầu nguyên văn của đề.

---

## 9. Tổng hợp nhanh

| Mã | Tiêu đề | Assignee | Label |
|---|---|---|---|
| M1-01 | Phạm vi + use case tổng quát | Hùng | ooad, chung |
| M1-02 | Lớp thực thể phân tích + thiết kế | Hùng | ooad, chung |
| M1-03 | Thiết kế CSDL | Hùng | ooad, chung |
| M1-04 | Khung server theo entity mới | Hùng | chung |
| M1-05 | Khung app Android | Hùng | chung |
| M1-06 | Đăng ký + Đăng nhập | Hùng | uc-tai-khoan |
| M1-07 | Lõi câu hỏi và luật thắng thua | Hùng | chung |
| M2-01 | Hạ tầng Firebase | Hùng | uc-choi-online |
| M2-02 | Danh sách online, thách đấu | Hùng | uc-choi-online |
| M2-03 | Thi đấu, nộp bài, trọng tài | Hùng | uc-choi-online |
| M2-04 | Thoát trận, mất kết nối, chơi tiếp | Hùng | uc-choi-online |
| M2-05 | Bảng xếp hạng | Hùng | uc-lich-su |
| M2-09 | Xem lịch sử đấu | Hùng | uc-lich-su |
| M1-08 | Phần chung bản 6: Friendship, tblFriendship | Hùng | chung |
| M2-10 | Quản lý bạn bè | Hùng | uc-ban-be |
| M2-06 | Chơi với máy | Hoàng | uc-choi-voi-may |
| M2-07 | Quản lý thông tin người chơi | Hoàng | uc-quan-tri |
| M2-08 | Tổng duyệt demo | Cả 2 | chung |
| M3-01 | Kịch bản v1 UC03, UC04, UC07 | Hùng | ooad, uc-choi-online, uc-lich-su, uc-ban-be |
| M3-02 | Kịch bản v1 UC05, UC06, Đăng nhập | Hoàng | ooad, uc-choi-voi-may, uc-quan-tri, uc-tai-khoan |
| M3-03 | Lớp phân tích UC03, UC04, UC07 | Hùng | ooad, uc-choi-online, uc-lich-su, uc-ban-be |
| M3-04 | Lớp phân tích UC05, UC06, Đăng nhập | Hoàng | ooad, uc-choi-voi-may, uc-quan-tri, uc-tai-khoan |
| M3-05 | Kịch bản v2 + tuần tự UC03, UC04, UC07 | Hùng | ooad, uc-choi-online, uc-lich-su, uc-ban-be |
| M3-06 | Kịch bản v2 + tuần tự UC05, UC06, Đăng nhập | Hoàng | ooad, uc-choi-voi-may, uc-quan-tri, uc-tai-khoan |
| M4-01 | Giao diện + lớp chi tiết UC03, UC04, UC07 | Hùng | ooad, uc-choi-online, uc-lich-su, uc-ban-be |
| M4-02 | Giao diện + lớp chi tiết UC05, UC06, Đăng nhập | Hoàng | ooad, uc-choi-voi-may, uc-quan-tri, uc-tai-khoan |
| M4-03 | Kịch bản v3 + tuần tự UC03, UC04, UC07 | Hùng | ooad, uc-choi-online, uc-lich-su, uc-ban-be |
| M4-04 | Kịch bản v3 + tuần tự UC05, UC06, Đăng nhập | Hoàng | ooad, uc-choi-voi-may, uc-quan-tri, uc-tai-khoan |
| M4-05 | Báo cáo cá nhân Hùng | Hùng | docs |
| M4-06 | Báo cáo cá nhân Hoàng | Hoàng | docs |
