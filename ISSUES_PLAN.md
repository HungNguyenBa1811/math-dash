# KẾ HOẠCH ISSUE — MATH DASH 1v1 (BẢN 4: MVP, BACKEND SPRING BOOT, DEMO 28/10)

> Nguồn: `PLAN.md`, `CONVENTION.md` (quy ước code Android), `REPORT_CONVENTION.md` (yêu cầu báo cáo + quy trình OOAD), `docs/chung/01-pham-vi-use-case.md` (phạm vi MVP).
> Thành viên: **@HungNguyenBa1811** (Hùng – nhóm trưởng) · **@Minahn210** (Hoàng – thành viên 2).
> Mỗi mục `[Mx-yy]` bên dưới là 1 GitHub issue. Số thứ tự trong mỗi milestone là thứ tự nên làm.

**Thay đổi so với bản 3:**
- **Phạm vi MVP còn 6 use case**, một tác nhân là Người chơi: Đăng ký, Đăng nhập, Chơi offline, Cài đặt cấu hình game, Chơi online, Xem lịch sử đấu.
- Ngoài phạm vi: Sửa thông tin cá nhân, Quản lý người dùng, Quản lý lịch sử đấu (vai trò quản trị viên), Bảng vàng, xem danh sách ván offline.
- **Có backend Spring Boot + MySQL**, giữ tài khoản và lịch sử đấu. Backend làm **trọng tài** khi trận online kết thúc.
- Firebase Realtime Database chỉ còn giữ trận **đang diễn ra**.
- Chơi online có 2 chế độ: **Tạo phòng** (nhận mã phòng 6 chữ số) và **Tìm phòng** (nhập mã phòng).
- **Hoàng chỉ làm phía offline**, không đụng backend: Chơi offline và Cài đặt cấu hình game.

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
| D1 | Phạm vi | MVP 6 UC như trên. Ngoài phạm vi ghi ở `docs/chung/01-pham-vi-use-case.md` mục 2.1 | Kịp demo 28/10, mỗi chức năng làm trọn vẹn |
| D2 | Kiến trúc | **App Android** + **Backend Spring Boot** (REST, MySQL) + **Firebase Realtime Database** (chỉ trận đang diễn ra) | Backend giữ dữ liệu lâu dài và làm trọng tài; Firebase lo phần thời gian thực |
| D3 | Backend mỏng | `RestController` → `DAO` (JDBC, lớp cha giữ `#conn: Connection`) → Entity. **Không có tầng Service, không dùng JPA** | Giữ đúng khuôn MVC của bài mẫu OOAD; biểu đồ lớp và tuần tự không phình ra |
| D4 | Đăng nhập | Backend kiểm tra tài khoản, trả **token** (dùng cho REST) và **Firebase custom token** (Admin SDK). App đăng nhập Firebase bằng custom token nên `auth.uid` = `playerId`. App ghi nhớ phiên trong SharedPreferences | Một định danh dùng chung cho backend và Firebase; mất mạng vẫn vào được Chơi offline |
| D5 | Luật trận online | 60 giây, 2 người, cùng một dãy câu. Đúng: +10 + combo×2. Sai: 0 điểm, reset combo, **không mất mạng**. Người bỏ trận thua; nếu không ai bỏ thì so điểm, bằng điểm thì hòa | Đơn giản, dùng lại `GameSession` |
| D6 | Đồng bộ câu hỏi | Khi tạo phòng sinh `questionSeed` (long); 2 máy cùng tạo `new ProblemDAO(new Random(questionSeed))` | Không phải đẩy từng câu qua mạng |
| D7 | Trọng tài | Hết trận, app gọi `POST /api/matches/{roomCode}/finish`. Backend đọc trận từ Firebase bằng Admin SDK, gọi `Match.getResult(...)`, ghi **1 bản ghi** vào `tblMatch` + 2 dòng `tblMatchPlayer`. `Match.canJoin(...)` và `Match.getResult(...)` là **hàm Java thuần có unit test** | Luật nằm trong code của nhóm (trả lời được câu "Firebase có ăn hết module không"); chống gửi kết quả giả |
| D8 | Độ khó offline | Dễ / Trung bình / Khó: bắt đầu ở mức 1 / 2 / 3, tăng 1 mức sau mỗi 5 câu, tối đa mức 4 | Có ý nghĩa với kỷ lục theo từng độ khó |
| D9 | Thuộc tính dẫn xuất | **Không lưu:** kỷ lục (= MAX điểm theo người chơi + độ khó), kết quả trận (suy từ `forfeited` và điểm), mức, % chính xác | Đúng bước 5 thiết kế CSDL |
| D10 | Lưu trữ phía app | SQLite: `tblPlayer` (bản sao người chơi lưu khi đăng nhập), `tblGameRecord`. SharedPreferences: cấu hình game, phiên đăng nhập | Chơi offline chạy được khi mất mạng; kết quả ván có khóa ngoại thật trong SQLite |
| D11 | Lớp DAO | Mỗi nơi lưu trữ có **một lớp cha giữ kết nối**: app `DAO` (SQLite), `ApiDAO` (HTTP), `FirebaseDAO` (Firebase); server `DAO` (JDBC). Các DAO con kế thừa | Đúng tài liệu OOAD của môn |
| D12 | Tên bảng | Tiền tố `tbl` + tên lớp; khóa ngoại `tbl<Bảng>Id` | Đúng bước 1 và 4 thiết kế CSDL |
| D13 | Chia việc | Theo chức năng (mục 2.1). Phần chung Hùng làm (cùng Claude), Hoàng duyệt PR | Hoàng bận đến 25/10, không làm Spring Boot |
| D14 | Thứ tự OOAD | Phần chung (use case tổng quát, lớp thực thể, CSDL) làm **trước khi code**. Phân tích và thiết kế từng chức năng làm **sau demo**, đối chiếu code thật | Không đủ thời gian vẽ trước khi demo |
| D15 | Báo cáo | Mỗi người **một file riêng**, chỉ lấy phần chung liên quan chức năng của mình | Đề ghi "bài làm cá nhân" |

---

## 2. Hợp đồng chung (chốt ở M1, sau đó KHÔNG đổi tùy tiện)

### 2.1. Use case và người demo

| UC | Tên use case | Module | Người code, demo, vẽ |
|---|---|---|---|
| UC01 | Đăng ký | Tài khoản | Chung (Hùng làm, Hoàng duyệt). Hùng có thể demo thêm |
| UC02 | Đăng nhập | Tài khoản | Chung (Hùng làm, Hoàng duyệt). Hùng có thể demo thêm |
| UC03 | Chơi offline | Chơi offline | **Hoàng** |
| UC04 | Cài đặt cấu hình game | Chơi offline | **Hoàng** |
| UC05 | Chơi online (Tạo phòng / Tìm phòng / Thi đấu) | Chơi online | **Hùng** |
| UC06 | Xem lịch sử đấu | Chơi online | **Hùng** |

Label theo UC: `uc-tai-khoan` (UC01, UC02), `uc-choi-offline` (UC03), `uc-cai-dat` (UC04), `uc-choi-online` (UC05), `uc-lich-su` (UC06).

### 2.2. Tên lớp và người sở hữu

**App Android** (`ptit.e23cnpm01.nguyenbahung.math_dash`):

| Loại | Lớp | Sở hữu |
|---|---|---|
| Entity | `Player`, `MathProblem`, `GameSession`, `GameRecord`, `GameConfig`, `Match`, `MatchPlayer` | **Chung** |
| DAO SQLite | `DAO` (cha), `DatabaseHelper`, `PlayerDAO` (bản sao người chơi) | **Chung** |
| DAO SQLite / SharedPreferences | `GameRecordDAO`, `GameConfigDAO` | Hoàng |
| DAO HTTP | `ApiDAO` (cha: địa chỉ server, token, gửi/nhận JSON), `PlayerApiDAO` (đăng ký, đăng nhập), `SessionDAO` (lưu phiên) | **Chung** |
| DAO HTTP | `MatchApiDAO` (kết thúc trận, lịch sử đấu) | Hùng |
| DAO Firebase | `FirebaseDAO` (cha), `MatchFirebaseDAO` (tạo / tìm / theo dõi trận đang diễn ra) | Hùng |
| Sinh câu hỏi | `ProblemDAO` (không truy cập CSDL) | **Chung** |
| Điều khiển | `GameController` (offline) | Hoàng |
| Điều khiển | `MatchController` (online, Singleton) | Hùng |
| Giao diện | `FrmLoginActivity`, `FrmRegisterActivity`, `FrmMainActivity` | **Chung** |
| Giao diện | `FrmDifficultyActivity`, `FrmGameActivity`, `FrmSettingsActivity` | Hoàng |
| Giao diện | `FrmOnlineActivity` (Tạo phòng / nhập mã), `FrmLobbyActivity` (chờ đối thủ), `FrmOnlineGameActivity`, `FrmHistoryActivity`, `HistoryAdapter` | Hùng |
| Tiện ích | `SoundManager` (Singleton) | Hoàng |

**Backend** (thư mục `server/`, package `ptit.e23cnpm01.nguyenbahung.mathdash`):

| Loại | Lớp | Sở hữu |
|---|---|---|
| Entity | `Player`, `Match`, `MatchPlayer` (cùng tên, cùng thuộc tính với phía app) | **Chung** |
| Controller (REST) | `AuthRestController` (`/api/auth/*`) | **Chung** |
| Controller (REST) | `MatchRestController` (`/api/matches/*`) | Hùng |
| DAO JDBC | `DAO` (cha, `#conn: Connection`), `PlayerDAO` | **Chung** |
| DAO JDBC | `MatchDAO` | Hùng |
| Firebase Admin | `FirebaseAdminDAO` (tạo custom token, đọc trận đang diễn ra) | Hùng |

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
    protected static final String BASE_URL = "http://10.0.2.2:8080/api"; // máy ảo gọi về máy tính
    protected static String token;
    protected JSONObject post(String path, JSONObject body) throws IOException, JSONException { /* ... */ }
    protected JSONArray getArray(String path) throws IOException, JSONException { /* ... */ }
}

// App: dao/FirebaseDAO.java — cha của mọi DAO Firebase
public abstract class FirebaseDAO {
    protected static DatabaseReference con;
    protected static FirebaseAuth auth;
}

// Server: dao/DAO.java — cha của mọi DAO JDBC (giống bài mẫu khách sạn)
public abstract class DAO {
    protected static Connection conn;
    public DAO() { /* nếu conn == null thì DriverManager.getConnection(url, user, pass) */ }
}
```

### 2.4. CSDL và REST API

- **MySQL (server):** `tblPlayer`, `tblMatch`, `tblMatchPlayer`. Cột cụ thể chốt ở M1-03.
- **SQLite (app):** `tblPlayer` (bản sao), `tblGameRecord`. Cột cụ thể chốt ở M1-03.
- **SharedPreferences (app):** `"config"` (cấu hình game), `"session"` (phiên đăng nhập).
- **Firebase Realtime Database:**

```
matches/{roomCode}                  // mã phòng 6 chữ số
    status       : "waiting" | "playing" | "finished"
    questionSeed : long
    createdAt    : ServerValue.TIMESTAMP
    players/
        {playerId} : { displayName: String, score: int, forfeited: bool, connected: bool, timeUp: bool }
```

- **REST API** (JSON, header `Authorization: Bearer <token>` trừ 2 API đầu):

| Phương thức | Đường dẫn | Dùng cho | Kết quả |
|---|---|---|---|
| `POST` | `/api/auth/register` | UC01 | 201, hoặc 409 nếu trùng tên đăng nhập |
| `POST` | `/api/auth/login` | UC02 | `{ player, token, firebaseToken }`, hoặc 401 |
| `POST` | `/api/matches/{roomCode}/finish` | UC05 | Kết quả trận đã lưu |
| `GET` | `/api/matches?playerId={id}` | UC06 | Danh sách trận đã kết thúc của người chơi, mới nhất trước |

### 2.5. Ba phiên bản kịch bản

| Phiên bản | Issue | Đối tượng xuất hiện | Đi kèm |
|---|---|---|---|
| v1 | M3-01, M3-02 | Người chơi ↔ Hệ thống | — |
| v2 | M3-05, M3-06 | Người chơi, lớp `XxxView`, lớp thực thể | Biểu đồ tuần tự phân tích, **khớp từng bước** |
| v3 | M4-03, M4-04 | Người chơi, boundary (`FrmXxx`, `XxxRestController`), control (Controller, DAO), entity | Biểu đồ tuần tự thiết kế, **khớp từng bước** |

---

## 3. Quy ước chung cho mọi issue

- **Branch:** tài liệu `docs/M3-01-kich-ban-v1`; code `feat/M2-02-tao-tim-phong`. Tạo nhanh bằng `gh issue develop <số> --checkout`.
- **PR:** mô tả ghi `Closes #<số>`. PR phần chung (M1) phải được **Hoàng approve**. PR module riêng: người kia review nếu kịp; trước demo, ưu tiên merge cho kịp.
- **Thư mục:**
  ```
  app/      ứng dụng Android
  server/   backend Spring Boot (Maven, Java 17)
  docs/
    chung/  phạm vi + use case, lớp thực thể, CSDL
    hung/   tài liệu UC05, UC06 (và UC01, UC02 nếu Hùng trình bày)
    hoang/  tài liệu UC03, UC04
  ```
  - Kịch bản viết Markdown; biểu đồ vẽ PlantUML / Visual Paradigm / draw.io, xuất PNG cạnh file Markdown.
  - Không commit bí mật: `server/src/main/resources/firebase-service-account.json`, mật khẩu MySQL thật (đặt trong `application-local.properties`, có trong `.gitignore`).
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

**Người làm:** @HungNguyenBa1811 (cùng Claude), 21/10 → 24/10.
**Người duyệt:** @Minahn210, chậm nhất **tối 25/10**.

**Mục tiêu.** Làm trước mọi thứ cả 2 module cùng dùng, để từ 26/10 Hoàng chỉ việc code 2 chức năng offline, còn Hùng code phần online:
- 3 tài liệu OOAD chung: phạm vi + use case tổng quát, lớp thực thể, CSDL.
- Khung backend và khung app.
- Đăng ký, đăng nhập chạy được từ app tới MySQL.
- Lõi câu hỏi và luật điểm.

**Trong phạm vi:** Những gì thuộc cột "Chung" ở mục 2.2; chữ ký hàm (thân `// TODO`) cho lớp riêng của mỗi người.

**Ngoài phạm vi:** Thân hàm của lớp riêng (milestone Demo); kịch bản và biểu đồ của từng chức năng (milestone 3, 4).

**Sản phẩm bàn giao:**

| Loại | Nội dung |
|---|---|
| Tài liệu | `docs/chung/01-pham-vi-use-case.md`, `02-lop-thuc-the.md`, `03-csdl.md` + PNG |
| Backend | Project `server/` chạy được, kết nối MySQL, `DAO` cha, `PlayerDAO`, `AuthRestController`, script `server/db/schema.sql` |
| App | 7 entity, `DAO`, `DatabaseHelper`, `PlayerDAO`, `ApiDAO`, `PlayerApiDAO`, `SessionDAO`, `ProblemDAO`, `GameSession`; 3 màn Đăng nhập, Đăng ký, Chính; vỏ các Activity còn lại |
| Test | `GameSessionTest`, `ProblemDAOTest` |

**Điều kiện hoàn thành milestone:**
1. 7 issue M1-01 → M1-07 đã đóng; mọi PR đã được Hoàng approve.
2. App build pass, `gradlew test` pass; server `mvn test` pass.
3. Trên máy ảo: đăng ký → đăng nhập → vào màn Chính; tắt app mở lại vẫn ở màn Chính.
4. Từ màn Chính, 4 nút mở đúng 4 màn (dù màn còn rỗng).

**Rủi ro đã biết:**
- Mỗi máy chạy demo phải cài MySQL. Viết sẵn `schema.sql` và hướng dẫn chạy trong `server/README.md`.
- Máy ảo gọi máy tính qua `10.0.2.2`; app cần cho phép HTTP (`usesCleartextTraffic`) vì server chạy local không có HTTPS.

### [M1-01] Phạm vi hệ thống + biểu đồ use case tổng quát · @HungNguyenBa1811
- **Phụ thuộc:** không có. **LÀM ĐẦU TIÊN.**
- **Mô tả:** Bước "Yêu cầu" của OOAD theo phạm vi MVP, dùng chung cho cả 2 báo cáo. Hoàng duyệt.
- **Input:** README (luật chơi), `PLAN.md`, mục 1 và 2 của tài liệu này.
- **Output:** `docs/chung/01-pham-vi-use-case.md` + `uc-tong-quat.png`:
  - Bảng thuật ngữ; mục tiêu, phạm vi, phần ngoài phạm vi.
  - Người dùng, chức năng, luồng từng chức năng dạng `A → B → C` (có nhánh ngoại lệ).
  - Đối tượng và quan hệ giữa các đối tượng.
  - Bảng UC và biểu đồ use case tổng quát: **chỉ 6 UC chính**; các bước con phân rã ở tài liệu phân tích cá nhân.
- **Requirement:** Không có yêu cầu phi chức năng (nền tảng, công nghệ, hiệu năng). Hoàng đã approve.
- **Constraint:** Tên 6 UC dùng nguyên văn trong code, tài liệu và báo cáo.

### [M1-02] Lớp thực thể: phân tích + thiết kế (toàn hệ thống) · @HungNguyenBa1811
- **Phụ thuộc:** M1-01.
- **Mô tả:** Nguồn cho mục 3 trong báo cáo của cả 2 người. Hoàng duyệt.
- **Input:** M1-01; `REPORT_CONVENTION.md` mục 3 (bước A2, D1).
- **Output:** `docs/chung/02-lop-thuc-the.md` + 2 PNG:
  - **Phân tích (A2):** đoạn văn mô tả, trích danh từ, đánh giá giữ / loại, quan hệ số lượng, quan hệ đối tượng, danh sách lớp, biểu đồ (không `id`, chưa có kiểu).
  - **Thiết kế (D1), đủ 4 bước:**
    1. Thêm `id`.
    2. Thêm kiểu dữ liệu Java.
    3. Chuyển liên kết thành kết tập / hợp thành (`MatchPlayer` là thành phần của `Match`).
    4. Thêm thuộc tính đối tượng (`GameRecord` có `Player player`, `Match` có `List<MatchPlayer> players`, `MatchPlayer` có `Player player`).
  - **Bảng "lớp nào dùng cho chức năng nào".**
- **Requirement:** Không còn đường liên kết trơn trên biểu đồ thiết kế. Hoàng đã approve.
- **Constraint:** Thuộc tính dẫn xuất (D9) viết thành phương thức: `Match.getResult(playerId)`, `GameRecord.getAccuracy()`.

### [M1-03] Thiết kế CSDL: MySQL + SQLite + Firebase · @HungNguyenBa1811
- **Phụ thuộc:** M1-02.
- **Mô tả:** Từ lớp thực thể thiết kế, làm theo 5 bước thiết kế CSDL. Nguồn cho mục 2 trong báo cáo: Hoàng dùng phần SQLite, Hùng dùng phần MySQL + Firebase. Hoàng duyệt.
- **Input:** `docs/chung/02-lop-thuc-the.md`; mục 2.4; `REPORT_CONVENTION.md` mục 3 (bước D2).
- **Output:** `docs/chung/03-csdl.md` + PNG:
  - **MySQL, đủ 5 bước:** `tblPlayer`, `tblMatch`, `tblMatchPlayer` (khóa ngoại `tblMatchId`, `tblPlayerId`).
  - **SQLite:** `tblPlayer` (bản sao, giải thích vì sao cần), `tblGameRecord` (khóa ngoại `tblPlayerId`).
  - **Câu lệnh `CREATE TABLE`** cho cả 2 CSDL: `server/db/schema.sql` và đoạn tương ứng cho `DatabaseHelper`.
  - **Firebase:** ánh xạ `Match` / `MatchPlayer` sang cây `matches/{roomCode}`; giải thích thuộc tính nào chỉ có trên Firebase (`connected`, `timeUp`) và vì sao không lưu vào MySQL.
  - Giải thích vì sao `MathProblem`, `GameSession` không lưu và `GameConfig` lưu bằng SharedPreferences.
- **Requirement:** Mỗi bảng có khóa chính; khóa ngoại đúng chiều 1-n; không có cột dẫn xuất (D9). Hoàng đã approve.
- **Constraint:** Mật khẩu lưu dạng băm (BCrypt), không lưu bản rõ.

### [M1-04] Khung backend Spring Boot · @HungNguyenBa1811
- **Phụ thuộc:** M1-03.
- **Mô tả:** Dựng project backend theo D3, sẵn sàng cho các API của M1-06 và M2.
- **Input:** `server/db/schema.sql`; mục 2.2, 2.3, 2.4.
- **Output:**
  - **Project `server/`:** Maven, Java 17, Spring Boot (chỉ `spring-boot-starter-web`), MySQL Connector/J, `spring-security-crypto` (BCrypt).
  - **`dao/DAO.java`** theo mục 2.3; cấu hình kết nối đọc từ `application.properties`.
  - **Entity** `Player`, `Match`, `MatchPlayer` đúng thiết kế M1-02.
  - **Khung** `AuthRestController`, `MatchRestController`, `PlayerDAO`, `MatchDAO`: đủ chữ ký, thân `// TODO`.
  - **`server/README.md`:** cách tạo CSDL bằng `schema.sql` và chạy server.
- **Requirement:** `mvn spring-boot:run` khởi động được và kết nối được MySQL.
- **Constraint:** Không dùng JPA, không thêm tầng Service (D3). SQL dùng `PreparedStatement`.

### [M1-05] Khung app Android: entity, DAO cha, phiên đăng nhập, màn Chính · @HungNguyenBa1811
- **Phụ thuộc:** M1-03.
- **Mô tả:** Dựng phần chung phía app để 2 module dựng tiếp lên. Hoàng duyệt.
- **Input:** M1-02, M1-03; mục 2.2, 2.3; khung stub hiện tại.
- **Output:**
  - **7 entity** theo M1-02.
  - **SQLite:** `DatabaseHelper` tạo `tblPlayer`, `tblGameRecord`; `DAO` cha; `PlayerDAO.saveOrUpdate(Player)` (lưu bản sao khi đăng nhập).
  - **HTTP:** `ApiDAO` cha (gửi/nhận JSON bằng `HttpURLConnection` + `org.json`, gắn token); `SessionDAO` (lưu / đọc / xóa phiên trong SharedPreferences `"session"`).
  - **`FrmMainActivity`:** tên hiển thị; 5 nút: Chơi offline → `FrmDifficultyActivity`, Chơi online → `FrmOnlineActivity`, Lịch sử đấu → `FrmHistoryActivity`, Cài đặt → `FrmSettingsActivity`, Đăng xuất.
  - **Vỏ Activity** cho các màn còn lại, khai báo trong Manifest; quyền `INTERNET`; `usesCleartextTraffic`.
  - **Khung** `GameController`, `GameRecordDAO`, `GameConfigDAO`, `MatchController`, `MatchApiDAO`, `MatchFirebaseDAO`: đủ chữ ký, thân `// TODO`.
- **Requirement:** Build pass; 5 nút mở đúng màn; Đăng xuất xóa phiên và về màn Đăng nhập. Hoàng đã approve.
- **Constraint:** Gọi mạng và SQLite trong `Thread` + `runOnUiThread`; không dùng Retrofit, Volley, lambda.

### [M1-06] UC01 Đăng ký + UC02 Đăng nhập (app + server) · @HungNguyenBa1811
- **Phụ thuộc:** M1-04, M1-05.
- **Mô tả:** Chức năng tài khoản chạy trọn từ app tới MySQL.
- **Input:** Luồng "Đăng ký", "Đăng nhập" trong `docs/chung/01-pham-vi-use-case.md`; API mục 2.4.
- **Output:**
  - **Server:**
    - `AuthRestController`: `register`, `login`.
    - `PlayerDAO`: `findByUsername`, `insert`.
    - Mật khẩu băm BCrypt.
    - Token đăng nhập: chuỗi ngẫu nhiên lưu trong bộ nhớ server (Map token → playerId) là đủ cho MVP.
  - **App:**
    - `FrmLoginActivity` (màn mở đầu): ô tên đăng nhập, ô mật khẩu, nút Đăng nhập, nút Đăng ký.
    - `FrmRegisterActivity`: tên đăng nhập, mật khẩu, nhập lại mật khẩu, tên hiển thị.
    - `PlayerApiDAO`: `register`, `login`.
    - Đăng nhập xong lưu phiên (`SessionDAO`) + bản sao người chơi (`PlayerDAO`), vào màn Chính.
    - Mở app khi đã có phiên thì vào thẳng màn Chính.
- **Requirement:**
  - Trùng tên đăng nhập → "Tên đăng nhập đã được dùng".
  - Sai mật khẩu → "Sai tên đăng nhập hoặc mật khẩu".
  - Mật khẩu nhập lại không khớp → báo lỗi, không gọi server.
  - Mất mạng → thông báo tiếng Việt, app không crash.
- **Constraint:** Không lưu mật khẩu ở app; chỉ lưu token và thông tin người chơi.

### [M1-07] Lõi câu hỏi và luật điểm: MathProblem, ProblemDAO, GameSession · @HungNguyenBa1811
- **Phụ thuộc:** M1-02.
- **Mô tả:** Lõi dùng chung cho UC03 (offline) và UC05 (online). Hoàng duyệt kỹ, vì 3 lớp này nằm trong biểu đồ lớp của "Chơi offline" và Hoàng phải giải thích được khi demo.
- **Input:** Luật trong README, D5, D8; M1-02.
- **Output:**
  - **`MathProblem`:** `getExpression()` (ví dụ `"12 + 7"`), `isCorrect(int)`.
  - **`GameSession`:**
    - Constructor nhận độ khó và chế độ (offline / online).
    - `recordCorrectAnswer()`: cộng `10 + streak * 2`, trả về số điểm vừa cộng.
    - `recordWrongAnswer()`: reset combo; chỉ chế độ offline mới trừ mạng, hết mạng thì game over.
    - `getCurrentLevel()`: tính từ độ khó và số câu đã ra (D8).
  - **`ProblemDAO.generateProblem(int level)`:**

    | Level | Phép tính | Khoảng số |
    |---|---|---|
    | 1 | `+ −` | 1–10 |
    | 2 | `+ −` | 1–30 |
    | 3 | `+ − ×` | `+ −`: 1–50, `×`: 2–9 |
    | 4 | `+ − ×` | `+ −`: 1–100, `×`: 2–12 |

    4 đáp án khác nhau, không âm, có đáp án đúng, xáo trộn bằng `Collections.shuffle(list, random)`.
  - **`GameSessionTest`:** trạng thái ban đầu; combo +10 rồi +12; sai thì reset combo và trừ mạng; sai 3 lần thì game over; online sai không trừ mạng; mức bắt đầu đúng theo độ khó.
  - **`ProblemDAOTest`:** 1000 câu mỗi level hợp lệ; **2 `ProblemDAO` cùng seed sinh ra 30 câu giống hệt nhau.**
- **Requirement:** `gradlew test` pass. Hoàng đã approve.
- **Constraint:** Entity không import `android.*`. Mọi chỗ random đi qua `this.random`.

---

## 5. MILESTONE 2 — Code module & Demo (28/10)

### Mô tả milestone (dùng làm description cho Milestone `M2 - Demo`)

**Người phụ trách:**
- Hùng: M2-01 → M2-05, làm từ 22/10.
- Hoàng: M2-06, M2-07, làm trong 26–27/10.
- Cả 2: M2-08, tổng duyệt tối 27/10.

**Hạn:** **28/10/2026, ngày demo.**

**Mục tiêu.** Mỗi người có các chức năng của mình chạy được trên máy ảo, demo trơn tru và giải thích được code của chính mình.

**Trong phạm vi:** Thân hàm, layout, Adapter, câu SQL, API, truy vấn Firebase của UC03–UC06; âm thanh và rung.

**Ngoài phạm vi:** Các chức năng ngoài phạm vi ở D1; sửa lớp "Chung" mà không qua PR được người kia duyệt; vẽ biểu đồ (làm sau demo).

**Sản phẩm bàn giao:**
- Hùng:
  - App: `MatchController`, `MatchFirebaseDAO`, `MatchApiDAO`, `FrmOnlineActivity`, `FrmLobbyActivity`, `FrmOnlineGameActivity`, `FrmHistoryActivity`, `HistoryAdapter`.
  - Server: `MatchRestController`, `MatchDAO`, `FirebaseAdminDAO`.
- Hoàng:
  - App: `GameController`, `GameRecordDAO`, `GameConfigDAO`, `FrmDifficultyActivity`, `FrmGameActivity`, `FrmSettingsActivity`, `SoundManager`, `res/raw/*.mp3`.

**Điều kiện hoàn thành milestone:**
1. Mọi issue M2 đã đóng, hoặc đã cắt theo mục 8.
2. Trên `main`, mỗi người chạy được trọn các chức năng của mình, bắt đầu từ màn Đăng nhập, không crash.
3. Tìm `"-> "` trong `app/src/main/java` không ra kết quả nào (không dùng lambda).

**Rủi ro đã biết:**
- Hùng ôm nhiều việc nhất (backend + Firebase + 2 UC). Ưu tiên theo thứ tự: M2-02 → M2-03 → M2-05 → M2-04.
- Khi mất mạng đột ngột, Firebase có thể mất vài chục giây mới báo `onDisconnect`. Lúc demo nên dùng cách bấm Back để minh họa việc thoát trận.
- `VibrationEffect` chỉ có từ API 26, trong khi minSdk là 24. Phải rẽ nhánh theo `Build.VERSION.SDK_INT`.

### [M2-01] Hạ tầng Firebase: custom token + Security Rules · @HungNguyenBa1811
- **Phụ thuộc:** M1-06.
- **Mô tả:** Nối app và server với Firebase. Phục vụ UC05.
- **Input:** applicationId `ptit.e23cnpm01.nguyenbahung.math_dash`; D4; mục 2.3, 2.4.
- **Output:**
  - **Firebase Console:** tạo project, bật Realtime Database, tải service account cho server (không commit).
  - **App:**
    - `google-services.json`, plugin `com.google.gms.google-services`, `firebase-auth` + `firebase-database`.
    - `FirebaseDAO` cha.
    - Sau khi đăng nhập thì gọi `signInWithCustomToken(firebaseToken)`.
  - **Server:**
    - Thư viện `firebase-admin`.
    - `FirebaseAdminDAO.createCustomToken(playerId)`.
    - API login trả thêm `firebaseToken`.
  - **Security Rules** (`database.rules.json` trong repo):
    - Chỉ user đã đăng nhập được đọc/ghi; `.indexOn` không cần (tìm theo mã phòng).
    - Mỗi người chỉ sửa được `matches/{roomCode}/players/{auth.uid}`; riêng lúc tạo / vào phòng được ghi `status` và thêm chính mình.
    - `score` là số, ≥ 0, không giảm, mỗi lần tăng không quá 200.
    - `status` chỉ nhận `waiting` / `playing` / `finished`.
- **Requirement:**
  - Đăng nhập xong, Logcat in `Firebase uid = <playerId>`.
  - Thử trong **Rules Playground**, chụp màn hình dán vào issue: ghi điểm của người khác → bị từ chối; ghi điểm giảm → bị từ chối.
- **Constraint:** Không để rules public. Không dùng Firestore, FirebaseUI, Cloud Functions.

### [M2-02] UC05 Chơi online: Tạo phòng và Tìm phòng bằng mã · @HungNguyenBa1811
- **Phụ thuộc:** M1-05, M2-01.
- **Mô tả:** Hai chế độ vào trận. Tạo phòng: sinh mã phòng 6 chữ số, chờ đối thủ. Tìm phòng: nhập mã phòng để vào.
- **Input:** Luồng "Chơi online" trong `docs/chung/01-pham-vi-use-case.md`; schema mục 2.4.
- **Output:**
  - **`MatchFirebaseDAO extends FirebaseDAO`:**
    - `createMatch(roomCode, questionSeed, me)`: dùng transaction để mã phòng không trùng; tạo được thì `onDisconnect().removeValue()`.
    - `joinMatch(code, me)`: dùng `runTransaction`, bên trong gọi `Match.canJoin(myId)`.
    - `watchMatch(code, listener)`: lắng nghe trận.
    - `deleteMatch(code)`.
  - **Luật vào phòng là hàm Java thuần (D7):** `Match.canJoin(int playerId): boolean`, đúng khi trận `waiting`, chưa đủ 2 người, và người này chưa có trong trận. Unit test `MatchTest` cho 4 trường hợp: phòng chờ 1 người khác → `true`; đang đấu → `false`; đủ 2 người → `false`; phòng của chính mình → `false`.
  - **`MatchController` (Singleton):** `createRoom(listener)`, `findRoom(code, listener)`, `cancel()`.
  - **Giao diện:**
    - `FrmOnlineActivity`: nút Tạo phòng; ô nhập mã + nút Vào phòng.
    - `FrmLobbyActivity`: mã phòng (chữ lớn), "Đang chờ đối thủ...", tên đối thủ khi có, nút Hủy.
- **Requirement:**
  - Máy A tạo phòng, máy B nhập đúng mã → cả 2 thấy tên nhau, trận chuyển `playing`.
  - Nhập mã sai → "Không tìm thấy phòng".
  - Máy C nhập mã phòng đã đủ người → "Phòng đã đầy".
  - Bấm Hủy hoặc kill app khi đang chờ → trận biến mất trên Console.
  - `gradlew test` pass, có `MatchTest`.
- **Constraint:** Activity không gọi Firebase trực tiếp, chỉ gọi `MatchController`. `Match.canJoin` không import Android / Firebase.

### [M2-03] UC05 Chơi online: Thi đấu realtime + backend trọng tài · @HungNguyenBa1811
- **Phụ thuộc:** M1-07, M2-02.
- **Mô tả:** 2 máy chơi cùng dãy câu theo seed, thấy điểm nhau; hết giờ backend xác định kết quả và lưu trận (D7).
- **Input:** Trận đang `playing`; `questionSeed`; id đối thủ; API mục 2.4.
- **Output:**
  - **App – `MatchController`:**
    - `startGame(listener)`: `ProblemDAO(new Random(questionSeed))`, `GameSession` chế độ online, `CountDownTimer` 60s.
    - `submitAnswer(value)`: chấm bằng `GameSession`, ghi `players/{myId}/score`.
    - Lắng nghe điểm đối thủ.
    - Hết giờ: ghi `finished = true` rồi gọi `MatchApiDAO.finishMatch(code)`, nhận kết quả, gọi `onMatchEnd(Match)`.
  - **Server:**
    - `MatchRestController.finish(code)`: `FirebaseAdminDAO.readMatch(code)`.
    - Nếu trận đã được lưu thì trả lại bản đã lưu (máy thứ hai gọi sau).
    - Nếu chưa: `MatchDAO.insert(match)` (1 dòng `tblMatch` + 2 dòng `tblMatchPlayer`), set `status = finished` trên Firebase.
  - **Luật thắng thua là hàm Java thuần (D7):** `Match.getResult(int playerId): String` trả `WIN` / `LOSE` / `DRAW`: ai `forfeited` thì thua; không ai bỏ thì so điểm. Unit test `MatchResultTest` (phía server) cho 5 trường hợp: thắng điểm, thua điểm, hòa, đối thủ bỏ khi đang dẫn điểm, mình bỏ khi đang dẫn điểm.
  - **`FrmOnlineGameActivity`:** đồng hồ 60s, phép tính, lưới 2x2 nút đáp án, điểm MÌNH vs ĐỐI THỦ, overlay THẮNG / THUA / HÒA có tỉ số và nút "Về menu".
- **Requirement:**
  - 2 máy thấy cùng dãy câu.
  - Điểm đối thủ cập nhật dưới 1 giây.
  - Kết quả 2 máy khớp nhau.
  - MySQL có đúng 1 dòng `tblMatch` cho mỗi trận.
  - `mvn test` pass, có `MatchResultTest`.
- **Constraint:** Không phân thắng thua trong Activity hay callback Firebase; chỉ server gọi `Match.getResult`. `removeEventListener` và hủy timer khi kết thúc.

### [M2-04] UC05 Chơi online: thoát trận và mất kết nối · @HungNguyenBa1811
- **Phụ thuộc:** M2-03.
- **Mô tả:** Các ngoại lệ của UC05, để không bao giờ để trận bị treo.
- **Input:** Trận hiện tại, id người chơi.
- **Output:**
  - Khi vào trận: `onDisconnect()` cho `players/{myId}` set `connected = false`, `forfeited = true`.
  - `leaveMatch()` (nút Back + `AlertDialog` "Thoát sẽ bị xử thua?"): set `forfeited = true`, gọi `finishMatch`.
  - Thấy đối thủ `forfeited == true` thì thông báo "Đối thủ đã thoát" và gọi `finishMatch`.
  - Lắng nghe `.info/connected`; mất kết nối thì thông báo, app không crash.
- **Requirement:** Pass cả 3 test case:
  1. Đang chơi, B bấm Back → A thắng ngay, MySQL lưu A thắng.
  2. Đang chơi, B tắt mạng → A nhận "Đối thủ đã thoát" (có thể mất vài chục giây).
  3. A mất mạng → A thấy thông báo, app không crash.
- **Constraint:** Kết quả vẫn do server tính bằng `Match.getResult` (dựa vào `forfeited`).

### [M2-05] UC06 Xem lịch sử đấu · @HungNguyenBa1811
- **Phụ thuộc:** M2-03.
- **Mô tả:** Người chơi xem các trận online đã đấu, lấy từ MySQL qua API.
- **Input:** `GET /api/matches?playerId={id}`; Pattern 1 trong CONVENTION.
- **Output:**
  - **Server:** `MatchRestController.getByPlayer(playerId)`, `MatchDAO.findByPlayer(playerId)` (JOIN `tblMatch`, `tblMatchPlayer`, `tblPlayer`), mới nhất trước.
  - **App:**
    - `MatchApiDAO.getHistory(playerId)`.
    - `FrmHistoryActivity` + `HistoryAdapter` + `item_history.xml`: thời gian, tên đối thủ, tỉ số `X - Y`, kết quả. Màu: WIN `arcade_green`, LOSE `arcade_red`, DRAW vàng.
- **Requirement:**
  - Đấu xong 1 trận thì lịch sử có thêm dòng mới ở cả 2 máy.
  - Chưa có trận thì hiện "Chưa có trận nào".
  - Mất mạng thì báo lỗi, không crash.
- **Constraint:** Gọi API trong `Thread` + `runOnUiThread`; không dùng DiffUtil, ListAdapter.

### [M2-06] UC03 Chơi offline: chọn độ khó, chơi, kỷ lục · @Minahn210
- **Phụ thuộc:** M1-05, M1-07.
- **Mô tả:** Chức năng chính Hoàng demo. Làm ngày 26/10.
- **Input:** Người chơi đang đăng nhập (`SessionDAO`); `GameSession`, `ProblemDAO` (M1-07); `GameConfigDAO` (đọc thời gian mỗi câu; nếu M2-07 chưa xong thì mặc định 6 giây); chữ ký `GameController`, `GameRecordDAO` trong khung.
- **Output:**
  - **`FrmDifficultyActivity`:** 3 nút Dễ / Trung bình / Khó, mỗi nút hiện kỷ lục ở độ khó đó.
  - **`GameController`:**
    - Constructor nhận `(GameSession, ProblemDAO, int bestScore, int secondsPerQuestion, GameControllerListener)`.
    - `startGame()`, `submitAnswer()`.
    - `CountDownTimer` theo thời gian mỗi câu; hết giờ tính như sai.
    - `onPause` / `onResume` / `onDestroy`.
    - Hết mạng gọi `onGameOver(session, score > bestScore)`.
  - **`GameRecordDAO extends DAO`:**
    - `insert(GameRecord)`.
    - `getBestScore(playerId, difficulty)`: `SELECT MAX(score) ... WHERE tblPlayerId = ? AND difficulty = ?`.
  - **`FrmGameActivity`** + layout:
    - Hiển thị: tên, kỷ lục, điểm, combo `xN`, mạng `♥ ♥ ♥`, thanh thời gian, phép tính.
    - Lưới 2x2 nút đáp án, cao ≥ 72dp, chữ monospace 26sp.
    - Dòng phản hồi `+12 PTS!` / `MISS!`.
    - Overlay Game Over: điểm cuối, kỷ lục, "KỶ LỤC MỚI", nút Chơi lại / Về menu.
- **Requirement:**
  - Hết giờ thì mất mạng; hết 3 mạng thì Game Over.
  - Ván được lưu vào `tblGameRecord`.
  - Kỷ lục tách riêng theo độ khó.
  - Chơi được khi tắt mạng.
- **Constraint:** Không tính điểm trong Activity; SQLite trong `Thread`; hủy timer trong `onDestroy()`.

### [M2-07] UC04 Cài đặt cấu hình game + âm thanh, rung · @Minahn210
- **Phụ thuộc:** M2-06.
- **Mô tả:** Người chơi chỉnh âm thanh, rung, thời gian mỗi câu; ván offline dùng cấu hình này. Làm ngày 27/10.
- **Input:** Pattern 4 (Singleton); entity `GameConfig`.
- **Output:**
  - **`GameConfigDAO`:** `load(): GameConfig`, `save(GameConfig)` trên SharedPreferences `"config"`. Mặc định: âm thanh bật, rung bật, 6 giây.
  - **`FrmSettingsActivity`:** Switch Âm thanh, Switch Rung, lựa chọn 4 / 6 / 8 / 10 giây, nút Lưu.
  - **`util/SoundManager`** (Singleton):
    - `playSound(context, resId)`, `vibrate(context, ms)`.
    - Kiểm tra cấu hình trước khi phát.
  - `res/raw/correct.mp3`, `wrong.mp3`, `timeout.mp3` (ghi lại nguồn); quyền `VIBRATE`.
  - Gắn vào `FrmGameActivity`: phát âm / rung khi trả lời và khi hết giờ; `GameController` nhận thời gian mỗi câu từ cấu hình.
- **Requirement:**
  - Lưu xong có hiệu lực từ ván tiếp theo.
  - Vẫn giữ sau khi tắt app mở lại.
  - Đổi thành 4 giây thì đồng hồ mỗi câu còn 4 giây.
- **Constraint:** `release()` MediaPlayer cũ trước khi tạo cái mới; kiểm tra `Build.VERSION.SDK_INT` trước khi dùng `VibrationEffect`; mỗi file âm thanh < 100KB.

### [M2-08] Tổng duyệt demo · @HungNguyenBa1811 @Minahn210
- **Phụ thuộc:** M2-01 → M2-07.
- **Mô tả:** Tối 27/10, chạy thử đúng như buổi demo trên bản `main` cuối cùng.
- **Input:** `main` đã merge đủ; MySQL và server chạy trên laptop demo.
- **Output:**
  - **Kịch bản demo của mỗi người** (dán vào comment issue), kèm dữ liệu chuẩn bị sẵn: 2 tài khoản, vài ván offline để có kỷ lục, 1 trận để lịch sử có dữ liệu.
  - **Hùng** chạy UC05, UC06 trên 2 máy ảo, gồm 3 test case của M2-04. Chuẩn bị phần giải thích, mở code ra nói được:
    1. Firebase chỉ giữ trận đang diễn ra và báo thay đổi; chỉ `MatchFirebaseDAO`, `FirebaseAdminDAO` gọi Firebase.
    2. `Match.canJoin` trong transaction xử lý 2 người vào cùng lúc; chạy `MatchTest`.
    3. Đồng bộ câu hỏi bằng `questionSeed`; chạy test cùng seed.
    4. Server làm trọng tài bằng `Match.getResult`; chạy `MatchResultTest`.
    5. Security Rules chặn ghi điểm của người khác và điểm giảm.
  - **Hoàng** chạy UC03, UC04 trên 1 máy ảo, gồm cả lúc tắt mạng.
  - Lỗi phát hiện được thành issue `bug`, sửa ngay trong tối 27/10.
- **Requirement:** Mỗi người chạy trọn các chức năng của mình từ màn Đăng nhập, không crash, 2 lần liên tiếp.
- **Constraint:** Không thêm tính năng mới.

---

## 6. MILESTONE 3 — Phân tích cá nhân · label `ooad`

### Mô tả milestone (dùng làm description cho Milestone `M3 - Phan tich`)

**Người phụ trách:** Hùng làm UC05, UC06; Hoàng làm UC03, UC04. Nếu Hùng trình bày thêm UC01, UC02 thì làm cùng format.
**Thời gian:** 29/10 → **04/11/2026**.

**Mục tiêu.** Bước phân tích OOAD cho từng chức năng đã demo:
- Biểu đồ use case chi tiết của module.
- Kịch bản v1.
- Biểu đồ lớp phân tích.
- Kịch bản v2 kèm biểu đồ tuần tự phân tích.

Lớp thực thể đã có ở M1-02; thiếu lớp thì mở PR bổ sung tài liệu chung.

**Ngoài phạm vi:** DAO, Controller, Activity, REST → milestone 4.

**Sản phẩm bàn giao:** `docs/hung/` và `docs/hoang/`: `1-kich-ban-v1.md`, `2-lop-phan-tich.md`, `3-kich-ban-v2.md` + PNG.

**Điều kiện hoàn thành milestone:**
1. 6 issue M3-01 → M3-06 đã đóng.
2. Mỗi phương thức trên biểu đồ tuần tự v2 đều có trên biểu đồ lớp phân tích tương ứng.

### [M3-01] Use case chi tiết + kịch bản v1: Chơi online, Xem lịch sử đấu · @HungNguyenBa1811
- **Phụ thuộc:** M2-08.
- **Mô tả:** Mục 1 trong báo cáo của Hùng. Làm theo `REPORT_CONVENTION.md` bước A1, mô tả đúng như app đã demo.
- **Input:** App thật sau demo; M1-01.
- **Output:** `docs/hung/1-kich-ban-v1.md`:
  - **Biểu đồ use case chi tiết module Chơi online:**
    - UC05 include "Vào phòng đấu" (UC trừu tượng) với 2 UC con kế thừa "Tạo phòng", "Tìm phòng".
    - UC05 include "Thi đấu"; "Thi đấu" có extend "Thoát trận".
    - "Tạo phòng" có extend "Hủy phòng".
  - **Đặc tả + kịch bản chuẩn v1 + kịch bản ngoại lệ** cho UC05, UC06 (bảng 2 cột theo mẫu skill `cnpm`). Ngoại lệ tối thiểu:
    - Mã phòng sai; phòng đã đầy; hủy khi đang chờ; đối thủ thoát; mất mạng; hòa.
    - Chưa có trận nào.
- **Requirement:** Không có tên lớp, tên hàm trong kịch bản v1; mỗi bước của hệ thống ghi rõ giao diện gồm thành phần gì.
- **Constraint:** Dùng đúng tên UC ở M1-01.

### [M3-02] Use case chi tiết + kịch bản v1: Chơi offline, Cài đặt cấu hình game · @Minahn210
- **Phụ thuộc:** M2-08.
- **Mô tả:** Mục 1 trong báo cáo của Hoàng. Làm theo `REPORT_CONVENTION.md` bước A1.
- **Input:** App thật sau demo; M1-01; `docs/hung/1-kich-ban-v1.md` để tham khảo cách viết.
- **Output:** `docs/hoang/1-kich-ban-v1.md`:
  - **Biểu đồ use case chi tiết module Chơi offline:** UC03 include "Chọn độ khó", include "Trả lời câu hỏi".
  - **Đặc tả + kịch bản chuẩn v1 + ngoại lệ** cho UC03, UC04. Ngoại lệ tối thiểu:
    - Hết giờ 1 câu; hết 3 mạng; đạt kỷ lục mới; bấm Back giữa ván; chơi khi mất mạng.
    - Lưu cấu hình rồi vào chơi.
- **Requirement:** Giống M3-01.
- **Constraint:** Dùng đúng tên UC ở M1-01.

### [M3-03] Biểu đồ lớp phân tích: Chơi online, Xem lịch sử đấu · @HungNguyenBa1811
- **Phụ thuộc:** M3-01.
- **Mô tả:** Bước A3 trong `REPORT_CONVENTION.md`. Mỗi UC một biểu đồ gồm các lớp View và lớp thực thể liên quan.
- **Input:** Kịch bản v1 (M3-01); `docs/chung/02-lop-thuc-the.md`.
- **Output:** `docs/hung/2-lop-phan-tich.md` + PNG:
  - View: `OnlineView`, `LobbyView`, `OnlineGameView`, `MatchResultView`, `HistoryView`; thành phần đặt tiền tố `in` / `out` / `sub`.
  - Phương thức gán vào thực thể theo quy tắc 4 câu hỏi.
- **Requirement:** Mỗi thành phần giao diện trong kịch bản v1 đều có trong View tương ứng.
- **Constraint:** Chưa có DAO, Controller, Activity, REST.

### [M3-04] Biểu đồ lớp phân tích: Chơi offline, Cài đặt cấu hình game · @Minahn210
- **Phụ thuộc:** M3-02.
- **Mô tả:** Như M3-03, cho UC03, UC04.
- **Input:** Kịch bản v1 (M3-02); `docs/chung/02-lop-thuc-the.md`; `docs/hung/2-lop-phan-tich.md` để tham khảo.
- **Output:** `docs/hoang/2-lop-phan-tich.md` + PNG. View dự kiến: `MainView`, `DifficultyView`, `GameView`, `GameOverView`, `SettingsView`.
- **Requirement:** Giống M3-03.
- **Constraint:** Giống M3-03.

### [M3-05] Kịch bản v2 + biểu đồ tuần tự phân tích: Chơi online, Xem lịch sử đấu · @HungNguyenBa1811
- **Phụ thuộc:** M3-03.
- **Mô tả:** Bước A4 trong `REPORT_CONVENTION.md`.
- **Input:** Kịch bản v1 (M3-01); biểu đồ lớp phân tích (M3-03).
- **Output:** `docs/hung/3-kich-ban-v2.md` + PNG. Với mỗi UC:
  - Kịch bản v2, mỗi bước đúng một thông điệp giữa Người chơi, View, thực thể.
  - Biểu đồ tuần tự đánh số giống kịch bản.
  - Ngoại lệ chính vẽ bằng khung `alt`.
- **Requirement:** Số bước bằng số thông điệp, cùng thứ tự.
- **Constraint:** Chỉ 3 loại đối tượng: tác nhân, View, thực thể.

### [M3-06] Kịch bản v2 + biểu đồ tuần tự phân tích: Chơi offline, Cài đặt cấu hình game · @Minahn210
- **Phụ thuộc:** M3-04.
- **Mô tả:** Như M3-05, cho UC03, UC04.
- **Input:** Kịch bản v1 (M3-02); biểu đồ lớp phân tích (M3-04); `docs/hung/3-kich-ban-v2.md` để tham khảo.
- **Output:** `docs/hoang/3-kich-ban-v2.md` + PNG.
- **Requirement:** Giống M3-05.
- **Constraint:** Giống M3-05.

---

## 7. MILESTONE 4 — Thiết kế cá nhân & Báo cáo

### Mô tả milestone (dùng làm description cho Milestone `M4 - Thiet ke & Bao cao`)

**Người phụ trách:** Hùng làm UC05, UC06 và báo cáo của Hùng; Hoàng làm UC03, UC04 và báo cáo của Hoàng.
**Thời gian:** 05/11 → **15/11/2026**. Biểu đồ thiết kế xong trước 11/11; 12–15/11 hoàn thiện báo cáo và đọc chéo.

**Mục tiêu.** Biểu đồ lớp chi tiết và tuần tự chi tiết cho từng chức năng, **vẽ theo code thật**, rồi ghép thành báo cáo cá nhân đúng khung đề.
- Chức năng có gọi server (UC05, UC06) vẽ theo quy ước 2 package "Client (Android)" và "Server (Spring Boot)" ở `REPORT_CONVENTION.md`.
- Chức năng offline (UC03, UC04) vẽ MVC thuần.

**Ngoài phạm vi:** Thêm tính năng; sửa code chỉ để biểu đồ đẹp hơn (lệch thì sửa biểu đồ theo code).

**Sản phẩm bàn giao:**
- `docs/hung/4-lop-thiet-ke.md`, `5-kich-ban-v3.md`; `docs/hoang/` tương tự.
- `docs/BaoCao_Hung.docx`, `docs/BaoCao_Hoang.docx`.

**Điều kiện hoàn thành milestone:**
1. 6 issue M4-01 → M4-06 đã đóng.
2. Mỗi báo cáo tick đủ checklist ở `REPORT_CONVENTION.md` mục 5.
3. Mọi tên lớp, hàm, bảng, API trong báo cáo đều có trong code ở `main`.

### [M4-01] Thiết kế giao diện + biểu đồ lớp chi tiết: Chơi online, Xem lịch sử đấu · @HungNguyenBa1811
- **Phụ thuộc:** M3-03, M2-08.
- **Mô tả:** Bước D3 trong `REPORT_CONVENTION.md`. Mục 4.x.1 và 4.x.2 trong báo cáo của Hùng.
- **Input:** Code thật trên `main`; biểu đồ lớp phân tích (M3-03); M1-02, M1-03.
- **Output:** `docs/hung/4-lop-thiet-ke.md` + PNG. Với mỗi UC:
  - **Thiết kế giao diện:** ảnh app thật; bảng thành phần (`id` trong layout, loại View, chức năng, tên ở bước phân tích).
  - **Biểu đồ lớp chi tiết, 2 package:**
    - **Client:**
      - Boundary: `FrmOnlineActivity`, `FrmLobbyActivity`, `FrmOnlineGameActivity`, `FrmHistoryActivity`, `HistoryAdapter`.
      - Control: `MatchController`; `FirebaseDAO` ← `MatchFirebaseDAO`; `ApiDAO` ← `MatchApiDAO`; `ProblemDAO`.
      - Entity: `Match`, `MatchPlayer`, `Player`, `MathProblem`, `GameSession`.
    - **Server:**
      - Boundary: `MatchRestController`.
      - Control: `DAO` ← `MatchDAO`; `FirebaseAdminDAO`.
      - Entity: `Match`, `MatchPlayer`, `Player`.
    - Nối 2 package bằng một mũi tên `HTTP/JSON` từ `MatchApiDAO` tới `MatchRestController`.
  - **Mô tả từng lớp 1–2 dòng.**
- **Requirement:** Mỗi View ở M3-03 ánh xạ sang đúng một Activity hoặc overlay; tên lớp, hàm, API trùng với code.
- **Constraint:** Không vẽ lớp của framework (Spring, Firebase SDK) ngoài kiểu dữ liệu của thuộc tính.

### [M4-02] Thiết kế giao diện + biểu đồ lớp chi tiết: Chơi offline, Cài đặt cấu hình game · @Minahn210
- **Phụ thuộc:** M3-04, M2-08.
- **Mô tả:** Như M4-01, cho UC03, UC04. Mục 4.x.1 và 4.x.2 trong báo cáo của Hoàng.
- **Input:** Code thật trên `main`; biểu đồ lớp phân tích (M3-04); M1-02, M1-03; `docs/hung/4-lop-thiet-ke.md` để tham khảo.
- **Output:** `docs/hoang/4-lop-thiet-ke.md` + PNG. Với mỗi UC:
  - Thiết kế giao diện: ảnh app thật và bảng thành phần.
  - Biểu đồ lớp chi tiết (chỉ phía app):
    - Boundary: `FrmMainActivity`, `FrmDifficultyActivity`, `FrmGameActivity`, `FrmSettingsActivity`.
    - Control: `GameController`; `DAO` ← `GameRecordDAO`; `GameConfigDAO`; `DatabaseHelper`; `ProblemDAO`; `SoundManager`.
    - Entity: `Player`, `GameRecord`, `GameConfig`, `MathProblem`, `GameSession`.
  - Mô tả từng lớp 1–2 dòng.
- **Requirement:** Giống M4-01.
- **Constraint:** Tên lớp và hàm trùng với code.

### [M4-03] Kịch bản v3 + biểu đồ tuần tự chi tiết: Chơi online, Xem lịch sử đấu · @HungNguyenBa1811
- **Phụ thuộc:** M3-05, M4-01.
- **Mô tả:** Bước D4 trong `REPORT_CONVENTION.md`. Mục 4.x.3 trong báo cáo của Hùng.
- **Input:** Kịch bản v2 (M3-05); biểu đồ lớp chi tiết (M4-01); code thật.
- **Output:** `docs/hung/5-kich-ban-v3.md` + PNG. Với mỗi UC:
  - Kịch bản v3 đánh số, tên hàm thật (`onClick`, `createRoom`, `joinMatch`, `onDataChange`, `finishMatch`, `POST /api/matches/{roomCode}/finish`…).
  - Biểu đồ tuần tự đủ boundary – control – entity ở cả client và server, khớp 1-1 với kịch bản.
  - Ngoại lệ thoát trận, mất kết nối vẽ bằng khung `alt`.
- **Requirement:** Số bước bằng số thông điệp, cùng thứ tự.
- **Constraint:** Mỗi lời gọi REST vẽ là **một** thông điệp từ `MatchApiDAO` tới `MatchRestController`; callback Firebase theo quy ước ở `REPORT_CONVENTION.md`.

### [M4-04] Kịch bản v3 + biểu đồ tuần tự chi tiết: Chơi offline, Cài đặt cấu hình game · @Minahn210
- **Phụ thuộc:** M3-06, M4-02.
- **Mô tả:** Như M4-03, cho UC03, UC04. Mục 4.x.3 trong báo cáo của Hoàng.
- **Input:** Kịch bản v2 (M3-06); biểu đồ lớp chi tiết (M4-02); code thật; `docs/hung/5-kich-ban-v3.md` để tham khảo.
- **Output:** `docs/hoang/5-kich-ban-v3.md` + PNG. `CountDownTimer` và `Thread` vẽ theo quy ước ở `REPORT_CONVENTION.md`.
- **Requirement:** Giống M4-03.
- **Constraint:** Mọi hàm trên biểu đồ có trong M4-02.

### [M4-05] Báo cáo cá nhân – Hùng (Chơi online, Xem lịch sử đấu) · @HungNguyenBa1811
- **Phụ thuộc:** M1-02, M1-03, M3-01, M4-01, M4-03.
- **Mô tả:** Báo cáo thiết kế cá nhân của Hùng, đúng khung ở `REPORT_CONVENTION.md` mục 2. Mỗi mục dán vào ngay khi issue nguồn được đóng. Nếu trình bày thêm Đăng ký / Đăng nhập thì thêm mục 4.3 cùng format.
- **Input:** `REPORT_CONVENTION.md`; tài liệu trong `docs/chung/` và `docs/hung/`.
- **Output:**
  - Một file báo cáo online của riêng Hùng (dán link vào issue); bản cuối `docs/BaoCao_Hung.docx`.
  - Thứ tự mục bắt buộc:

    | Mục | Nội dung | Lấy từ |
    |---|---|---|
    | 1 | Mô tả chi tiết các chức năng bằng ngôn ngữ tự nhiên | M3-01 |
    | 2 | Thiết kế CSDL: MySQL `tblPlayer`, `tblMatch`, `tblMatchPlayer` và cây `matches` trên Firebase | M1-03 |
    | 3 | Thiết kế lớp thực thể: `Player`, `Match`, `MatchPlayer`, `MathProblem`, `GameSession` | M1-02 |
    | 4.1.1 | Chơi online: thiết kế giao diện | M4-01 |
    | 4.1.2 | Chơi online: biểu đồ lớp chi tiết (client + server) | M4-01 |
    | 4.1.3 | Chơi online: kịch bản v3 + biểu đồ tuần tự chi tiết | M4-03 |
    | 4.2.1 | Xem lịch sử đấu: thiết kế giao diện | M4-01 |
    | 4.2.2 | Xem lịch sử đấu: biểu đồ lớp chi tiết (client + server) | M4-01 |
    | 4.2.3 | Xem lịch sử đấu: kịch bản v3 + biểu đồ tuần tự chi tiết | M4-03 |

- **Requirement:** Mọi mục đã tick; tick đủ checklist `REPORT_CONVENTION.md` mục 5; Hoàng đã đọc lại.
- **Constraint:** Bài cá nhân, tự viết; phần CSDL và lớp thực thể chỉ lấy phần liên quan chức năng của mình.

### [M4-06] Báo cáo cá nhân – Hoàng (Chơi offline, Cài đặt cấu hình game) · @Minahn210
- **Phụ thuộc:** M1-02, M1-03, M3-02, M4-02, M4-04.
- **Mô tả:** Báo cáo thiết kế cá nhân của Hoàng, đúng khung ở `REPORT_CONVENTION.md` mục 2.
- **Input:** `REPORT_CONVENTION.md`; tài liệu trong `docs/chung/` và `docs/hoang/`.
- **Output:**
  - Một file báo cáo online của riêng Hoàng (dán link vào issue); bản cuối `docs/BaoCao_Hoang.docx`.
  - Thứ tự mục bắt buộc:

    | Mục | Nội dung | Lấy từ |
    |---|---|---|
    | 1 | Mô tả chi tiết các chức năng bằng ngôn ngữ tự nhiên | M3-02 |
    | 2 | Thiết kế CSDL: SQLite `tblPlayer`, `tblGameRecord`; cấu hình trong SharedPreferences | M1-03 |
    | 3 | Thiết kế lớp thực thể: `Player`, `GameRecord`, `GameConfig`, `MathProblem`, `GameSession` | M1-02 |
    | 4.1.1 | Chơi offline: thiết kế giao diện | M4-02 |
    | 4.1.2 | Chơi offline: biểu đồ lớp chi tiết | M4-02 |
    | 4.1.3 | Chơi offline: kịch bản v3 + biểu đồ tuần tự chi tiết | M4-04 |
    | 4.2.1 | Cài đặt cấu hình game: thiết kế giao diện | M4-02 |
    | 4.2.2 | Cài đặt cấu hình game: biểu đồ lớp chi tiết | M4-02 |
    | 4.2.3 | Cài đặt cấu hình game: kịch bản v3 + biểu đồ tuần tự chi tiết | M4-04 |

- **Requirement:** Giống M4-05; Hùng đã đọc lại.
- **Constraint:** Giống M4-05.

---

## 8. Thứ tự, lịch & cắt phạm vi

```
Phần chung (Hùng, Hoàng duyệt):  M1-01 ─> M1-02 ─┬─> M1-03 ─┬─> M1-04 ─┐
                                                 │          └─> M1-05 ─┴─> M1-06
                                                 └─> M1-07
Hùng :  M1-06 ─> M2-01 ─> M2-02 ─> M2-03 ─┬─> M2-04 ─┐
                                          └─> M2-05 ─┤
Hoàng:  (M1-05 + M1-07) ─> M2-06 ─> M2-07 ───────────┴─> M2-08 ─> DEMO 28/10
Sau demo: M3-01/02 ─> M3-03/04 ─> M3-05/06 ─> M4-01/02 ─> M4-03/04 ─> M4-05/06 ─> NỘP 15/11
```

| Ngày | Hùng | Hoàng |
|---|---|---|
| 22/10 | M1-01, M1-02, M1-03 (tài liệu), M1-04 (khung server) | — |
| 23/10 | M1-05 (khung app), M1-07 (lõi), M1-06 (đăng ký, đăng nhập) | — |
| 24/10 | M2-01 (Firebase), M2-02 (tạo / tìm phòng) | — |
| 25/10 (CN) | M2-03 (thi đấu + trọng tài) | **Duyệt toàn bộ PR của M1** |
| 26/10 | M2-05 (lịch sử), M2-04 (thoát trận) | M2-06 Chơi offline |
| 27/10 | Sửa lỗi; tối: M2-08 | M2-07 Cài đặt; tối: M2-08 |
| **28/10** | **DEMO** | **DEMO** |
| 29/10 – 04/11 | M3-01, M3-03, M3-05 | M3-02, M3-04, M3-06 |
| 05/11 – 11/11 | M4-01, M4-03 | M4-02, M4-04 |
| 12/11 – 15/11 | M4-05, đọc báo cáo của Hoàng | M4-06, đọc báo cáo của Hùng → **nộp 15/11** |

**Nếu phải cắt phạm vi để kịp demo, bỏ theo thứ tự:**
1. Mức 4 của câu hỏi và lựa chọn thời gian 4 / 10 giây (giữ 6 / 8).
2. Test case "mất mạng" của M2-04 (giữ "bấm Back thì bị xử thua").
3. Security Rules kiểm tra mức tăng điểm (giữ "chỉ sửa node của mình").

Không cắt: backend trọng tài (M2-03) và `MatchTest` / `MatchResultTest`, vì đây là bằng chứng logic online nằm trong code của nhóm.

---

## 9. Tổng hợp nhanh

| Mã | Tiêu đề | Assignee | Label |
|---|---|---|---|
| M1-01 | Phạm vi + use case tổng quát | Hùng | ooad, chung |
| M1-02 | Lớp thực thể phân tích + thiết kế | Hùng | ooad, chung |
| M1-03 | Thiết kế CSDL | Hùng | ooad, chung |
| M1-04 | Khung backend Spring Boot | Hùng | chung |
| M1-05 | Khung app Android | Hùng | chung |
| M1-06 | Đăng ký + Đăng nhập | Hùng | uc-tai-khoan |
| M1-07 | Lõi câu hỏi và luật điểm | Hùng | chung |
| M2-01 | Hạ tầng Firebase | Hùng | uc-choi-online |
| M2-02 | Tạo phòng, Tìm phòng | Hùng | uc-choi-online |
| M2-03 | Thi đấu + trọng tài | Hùng | uc-choi-online |
| M2-04 | Thoát trận, mất kết nối | Hùng | uc-choi-online |
| M2-05 | Xem lịch sử đấu | Hùng | uc-lich-su |
| M2-06 | Chơi offline | Hoàng | uc-choi-offline |
| M2-07 | Cài đặt cấu hình game | Hoàng | uc-cai-dat |
| M2-08 | Tổng duyệt demo | Cả 2 | chung |
| M3-01 | Kịch bản v1 UC05, UC06 | Hùng | ooad, uc-choi-online, uc-lich-su |
| M3-02 | Kịch bản v1 UC03, UC04 | Hoàng | ooad, uc-choi-offline, uc-cai-dat |
| M3-03 | Lớp phân tích UC05, UC06 | Hùng | ooad, uc-choi-online, uc-lich-su |
| M3-04 | Lớp phân tích UC03, UC04 | Hoàng | ooad, uc-choi-offline, uc-cai-dat |
| M3-05 | Kịch bản v2 + tuần tự UC05, UC06 | Hùng | ooad, uc-choi-online, uc-lich-su |
| M3-06 | Kịch bản v2 + tuần tự UC03, UC04 | Hoàng | ooad, uc-choi-offline, uc-cai-dat |
| M4-01 | Giao diện + lớp chi tiết UC05, UC06 | Hùng | ooad, uc-choi-online, uc-lich-su |
| M4-02 | Giao diện + lớp chi tiết UC03, UC04 | Hoàng | ooad, uc-choi-offline, uc-cai-dat |
| M4-03 | Kịch bản v3 + tuần tự UC05, UC06 | Hùng | ooad, uc-choi-online, uc-lich-su |
| M4-04 | Kịch bản v3 + tuần tự UC03, UC04 | Hoàng | ooad, uc-choi-offline, uc-cai-dat |
| M4-05 | Báo cáo cá nhân Hùng | Hùng | docs |
| M4-06 | Báo cáo cá nhân Hoàng | Hoàng | docs |
