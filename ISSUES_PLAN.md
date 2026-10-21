# KẾ HOẠCH ISSUE — MATH DASH 1v1 (BẢN 3: DEMO 28/10, BÁO CÁO 15/11)

> Nguồn: `PLAN.md`, `CONVENTION.md` (quy ước code), `REPORT_CONVENTION.md` (yêu cầu báo cáo + quy trình OOAD), khung stub trong `app/`.
> Thành viên: **@HungNguyenBa1811** (Hùng – nhóm trưởng) · **@Minahn210** (Hoàng – thành viên 2).
> Mỗi mục `[Mx-yy]` bên dưới là 1 GitHub issue. Số thứ tự trong mỗi milestone là thứ tự nên làm.

**Thay đổi so với bản 2:**
- **Demo cá nhân ngày 28/10/2026**, mỗi người demo module của mình, **tối đa 3 chức năng**. Code phải xong trước 28/10.
- **Phần chung** (dùng cho cả 2 module) do Hùng làm trước (cùng Claude), **Hoàng duyệt**. Xong trước 25/10 để Hoàng có nền mà code trong 26–27/10.
- **Thứ tự mới:** Phần chung → Code module (demo) → Phân tích cá nhân → Thiết kế cá nhân + báo cáo (nộp 15/11).
  Phân tích và thiết kế cá nhân làm sau demo, dựa trên code thật, nên tên trong báo cáo chắc chắn khớp code.

---

## 0. Yêu cầu của môn học

| Hạng mục | Thời hạn | Nội dung |
|---|---|---|
| Demo cá nhân | **28/10/2026** | Mỗi người demo module của mình, tối đa 3 chức năng (30% điểm tổng kết) |
| Báo cáo thiết kế cá nhân | **15/11/2026** | Đặc tả, CSDL, lớp thực thể, rồi từng chức năng: giao diện, lớp chi tiết, tuần tự chi tiết (30% điểm tổng kết) |

Đề bài nguyên văn, khung báo cáo, cách làm từng bước OOAD và checklist trước khi nộp: xem **`REPORT_CONVENTION.md`**.

---

## 1. Các quyết định

| # | Vấn đề | Quyết định | Lý do |
|---|---|---|---|
| D1 | Firebase Realtime Database hay Firestore | **Realtime Database** | Có sẵn `onDisconnect()`, listener đơn giản, độ trễ thấp |
| D2 | Luật trận 1v1 | **60 giây/trận**, 2 người làm chung một dãy câu. Đúng: +10 + combo×2. Sai: 0 điểm, reset combo, **không mất mạng**. Hết giờ ai nhiều điểm hơn thắng, bằng điểm thì hòa | Đơn giản, dùng lại được `GameSession` |
| D3 | Đồng bộ câu hỏi | Chủ phòng tạo `seed` (long) ghi lên phòng; 2 máy cùng tạo `new ProblemDAO(new Random(seed))` | Không phải đẩy từng câu lên Firebase |
| D4 | Đăng nhập | Firebase **ẩn danh** + tên nhập ở màn Chính | Không phải làm màn đăng ký |
| D5 | Chia việc | **Theo chức năng** (mục 2.1). Mỗi người code, demo và vẽ trọn 3 chức năng của mình | Đúng yêu cầu demo và báo cáo cá nhân |
| D6 | Phần chung | Hùng làm trước (milestone M1), **Hoàng duyệt PR**. Gồm: use case tổng quát, lớp thực thể, CSDL, khung code, màn Chính, `PlayerDAO`, lõi câu hỏi | Hoàng bận đến 25/10; phần chung phải có trước để Hoàng code kịp demo |
| D7 | Lớp DAO | Lớp cha giữ kết nối dùng chung (`DAO` cho SQLite, `FirebaseDAO` cho Firebase), các DAO con kế thừa | Đúng tài liệu OOAD của môn |
| D8 | Tên bảng | `tblPlayer`, `tblGameRecord`, `tblMatchHistory`. Khóa ngoại `tblPlayerId` | Đúng bước 1 và 4 thiết kế CSDL |
| D9 | Thuộc tính dẫn xuất | **Không lưu** `high_score` (= MAX điểm trong `tblGameRecord`), `result` (suy từ 2 điểm), `% chính xác` | Đúng bước 5 thiết kế CSDL |
| D10 | Cắt phạm vi khi trễ | Cắt UC6 Cài đặt âm thanh trước. Hoàng demo 2 chức năng vẫn hợp lệ | Thầy cho tối đa 3, không bắt buộc đủ 3 |
| D11 | Báo cáo | Mỗi người **một file riêng**. Mục CSDL và lớp thực thể lấy từ tài liệu chung, nhưng chỉ đưa phần liên quan chức năng của mình | Đề ghi "bài làm cá nhân" |
| D12 | Thứ tự OOAD | Phần chung (use case tổng quát, lớp thực thể, CSDL) làm **trước khi code**. Phân tích và thiết kế từng chức năng làm **sau demo**, đối chiếu với code thật | Không đủ thời gian vẽ trước khi demo |

---

## 2. Hợp đồng chung (chốt ở M1, sau đó KHÔNG đổi tùy tiện)

### 2.1. Use case và người demo

Tác nhân duy nhất: **Người chơi**. Đối thủ trong trận online cũng là một Người chơi.

| UC | Tên use case | Người code, demo, vẽ | Label |
|---|---|---|---|
| UC1 | Tìm trận | Hùng | `uc-tim-tran` |
| UC2 | Thi đấu online (gồm xử lý thoát trận, mất mạng) | Hùng | `uc-thi-dau-online` |
| UC3 | Quản lý lịch sử đấu (xem, xóa) | Hùng | `uc-lich-su` |
| UC4 | Chơi offline | Hoàng | `uc-choi-offline` |
| UC5 | Xem bảng vàng | Hoàng | `uc-bang-vang` |
| UC6 | Cài đặt âm thanh | Hoàng | `uc-cai-dat` |

### 2.2. Tên lớp và người sở hữu

| Loại | Lớp | Sở hữu |
|---|---|---|
| Thực thể | `Player`, `GameRecord`, `MatchHistory`, `Room`, `RoomPlayer`, `MathProblem`, `GameSession` | **Chung** (M1-02, M1-04, M1-05) |
| DAO SQLite | `DAO` (lớp cha), `DatabaseHelper`, `PlayerDAO` | **Chung** (M1-04) |
| DAO SQLite | `GameRecordDAO` | Hoàng (thân hàm) |
| DAO SQLite | `MatchHistoryDAO` | Hùng (thân hàm) |
| DAO Firebase | `FirebaseDAO` (lớp cha), `AuthDAO`, `RoomDAO` | Hùng |
| Sinh câu hỏi | `ProblemDAO` (không kế thừa `DAO`, không truy cập CSDL) | **Chung** (M1-05) |
| Điều khiển | `GameController` | Hoàng |
| Điều khiển | `MatchController` (Singleton) | Hùng |
| Giao diện | `FrmMainActivity` | **Chung** (M1-04) |
| Giao diện | `FrmGameActivity`, `FrmLeaderboardActivity`, `LeaderboardAdapter`, `FrmSettingsActivity` | Hoàng |
| Giao diện | `FrmLobbyActivity`, `FrmOnlineGameActivity`, `FrmHistoryActivity`, `HistoryAdapter` | Hùng |
| Tiện ích | `SoundManager` (Singleton) | Hoàng |

- Lớp **Chung**: Hùng viết, Hoàng duyệt. Sau khi merge, muốn sửa thì mở PR riêng và người kia phải duyệt.
- Lớp của ai thì người đó viết thân hàm, demo và giải thích.

Lớp giao diện ở bước **phân tích** đặt tên `XxxView`; sang bước **thiết kế** ánh xạ sang Activity thật:

| Phân tích | Thiết kế |
|---|---|
| `MainView` | `FrmMainActivity` |
| `GameView`, `GameOverView` | `FrmGameActivity` (Game Over là overlay trong cùng Activity) |
| `LeaderboardView` | `FrmLeaderboardActivity` |
| `SettingsView` | `FrmSettingsActivity` |
| `LobbyView` | `FrmLobbyActivity` |
| `OnlineGameView`, `MatchResultView` | `FrmOnlineGameActivity` (Kết quả là overlay trong cùng Activity) |
| `HistoryView` | `FrmHistoryActivity` |

### 2.3. Lớp DAO dùng chung kết nối (D7)

```java
// dao/DAO.java — lớp cha của mọi DAO SQLite, chỉ giữ kết nối dùng chung
public abstract class DAO {
    protected static SQLiteDatabase con;

    public DAO(Context context) {
        if (con == null) {
            con = new DatabaseHelper(context.getApplicationContext()).getWritableDatabase();
        }
    }
}
// PlayerDAO, GameRecordDAO, MatchHistoryDAO: "extends DAO", dùng con để truy vấn

// dao/FirebaseDAO.java — lớp cha của mọi DAO Firebase
public abstract class FirebaseDAO {
    protected static DatabaseReference con;
    protected static FirebaseAuth auth;
    // constructor: nếu con == null thì lấy FirebaseDatabase.getInstance().getReference() và FirebaseAuth.getInstance()
}
// AuthDAO, RoomDAO: "extends FirebaseDAO"
```

### 2.4. CSDL

- **SQLite:** 3 bảng `tblPlayer`, `tblGameRecord`, `tblMatchHistory`. Cột cụ thể chốt ở M1-03.
- **Firebase Realtime Database:**

```
rooms/{roomId}
    status    : "waiting" | "playing" | "finished"
    seed      : long
    createdAt : ServerValue.TIMESTAMP
    players/
        {uid} : { name: String, score: int, online: bool, finished: bool }
```

### 2.5. Ba phiên bản kịch bản

| Phiên bản | Issue | Đối tượng xuất hiện | Đi kèm |
|---|---|---|---|
| v1 | M3-01, M3-02 | Người chơi ↔ Hệ thống | — |
| v2 | M3-05, M3-06 | Người chơi, lớp `XxxView`, lớp thực thể | Biểu đồ tuần tự phân tích, **khớp từng bước** |
| v3 | M4-03, M4-04 | Người chơi, `FrmXxxActivity` (boundary), Controller + DAO (control), thực thể (entity) | Biểu đồ tuần tự thiết kế, **khớp từng bước** |

---

## 3. Quy ước chung cho mọi issue

- **Branch:**
  - Tài liệu: `docs/M3-01-kich-ban-v1`.
  - Code: `feat/M2-02-tim-tran`.
  - Tạo nhanh bằng `gh issue develop <số> --checkout`.
- **PR:** mô tả ghi `Closes #<số>`.
  - PR phần chung (M1) phải được **Hoàng approve** rồi mới merge.
  - PR module riêng: người kia review nếu có thời gian; trước demo, ưu tiên merge cho kịp.
- **Thư mục tài liệu:**
  ```
  docs/
    chung/   use case tổng quát, lớp thực thể (phân tích + thiết kế), CSDL
    hung/    kịch bản v1/v2/v3, biểu đồ lớp và tuần tự cho UC1–UC3
    hoang/   kịch bản v1/v2/v3, biểu đồ lớp và tuần tự cho UC4–UC6
  ```
  - Kịch bản viết bằng Markdown để xem được thay đổi trên GitHub.
  - Biểu đồ vẽ bằng Visual Paradigm hoặc draw.io, xuất PNG lưu cạnh file Markdown.
  - Mỗi người giữ file dự án vẽ riêng (`docs/hung/*.vpp`, `docs/hoang/*.vpp`). File nhị phân không merge được, nên không sửa file của người kia.
- **Definition of Done:**
  - Issue tài liệu (label `ooad`): đạt hết *Requirement*, tick hết checklist OOAD ở cuối issue.
  - Issue code: build pass, chạy được trên máy ảo, đạt hết *Requirement*, tick hết checklist CONVENTION.
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

**Mục tiêu.** Làm trước mọi thứ cả 2 module cùng dùng, để từ 26/10 Hoàng chỉ việc code 3 chức năng của mình cho kịp demo 28/10:
- 3 tài liệu OOAD chung: use case tổng quát, lớp thực thể, CSDL.
- Khung code chung: entity, DAO cha, `DatabaseHelper`, `PlayerDAO`, màn Chính.
- Lõi câu hỏi và luật điểm.

**Trong phạm vi:** Những gì thuộc cột "Chung" ở mục 2.2; chữ ký hàm (thân để `// TODO`) cho các lớp riêng của mỗi người.

**Ngoài phạm vi:**
- Thân hàm của lớp riêng (`GameController`, `GameRecordDAO`, `MatchController`…) → milestone Demo.
- Kịch bản, biểu đồ lớp, biểu đồ tuần tự của từng chức năng → milestone Phân tích và Thiết kế.

**Sản phẩm bàn giao:**

| Loại | File |
|---|---|
| Tài liệu | `docs/chung/01-pham-vi-use-case.md`, `02-lop-thuc-the.md`, `03-csdl.md` + các PNG |
| Entity | `Player`, `GameRecord`, `MatchHistory`, `Room`, `RoomPlayer`, `MathProblem`, `GameSession` |
| DAO | `DAO`, `DatabaseHelper`, `PlayerDAO` (đầy đủ); `ProblemDAO` (đầy đủ); `GameRecordDAO`, `MatchHistoryDAO` (chữ ký) |
| Giao diện | `FrmMainActivity` hoàn chỉnh; vỏ rỗng của mọi Activity khác, đã khai báo trong Manifest |
| Test | `GameSessionTest`, `ProblemDAOTest` |

**Điều kiện hoàn thành milestone:**
1. 5 issue M1-01 → M1-05 đã đóng; mọi PR đã được Hoàng approve.
2. Build pass, `gradlew test` pass.
3. Từ màn Chính, 5 nút mở đúng 5 màn (dù màn còn rỗng).
4. Hoàng xác nhận: đọc tài liệu chung và khung code là biết phải viết gì cho 3 chức năng của mình.

**Rủi ro đã biết:** Hoàng duyệt muộn hơn 25/10 thì mất thời gian code. Hùng gửi link PR cho Hoàng ngay khi mở, không đợi xong cả milestone.

### [M1-01] Phạm vi hệ thống + biểu đồ use case tổng quát · @HungNguyenBa1811
- **Phụ thuộc:** không có. **LÀM ĐẦU TIÊN.**
- **Mô tả:** Bước "Yêu cầu" của OOAD, dùng chung cho cả 2 báo cáo. Hoàng duyệt.
- **Input:** README (luật chơi), `PLAN.md`, mục 1 và 2 của tài liệu này.
- **Output:** `docs/chung/01-pham-vi-use-case.md` + `docs/chung/uc-tong-quat.png`, gồm:
  - **Mục tiêu và phạm vi:** app Android, chơi offline trên 1 máy hoặc đấu 1v1 online qua Firebase.
  - **Người dùng và chức năng:** Người chơi dùng được 6 chức năng ở mục 2.1.
  - **Biểu đồ use case tổng quát:** tác nhân Người chơi, 6 UC, các quan hệ `<<include>>` / `<<extend>>` nếu có (ví dụ Tìm trận include Đăng nhập ẩn danh).
  - **Mô tả mỗi UC 1 dòng**, kèm bảng UC → người demo.
- **Requirement:** Hoàng đã approve. Từ đây tên 6 UC dùng nguyên văn trong code, tài liệu và báo cáo.
- **Constraint:** Không đi vào chi tiết từng UC; phần đó làm ở M3.

### [M1-02] Lớp thực thể: phân tích + thiết kế (toàn hệ thống) · @HungNguyenBa1811
- **Phụ thuộc:** M1-01.
- **Mô tả:** Làm trước khi code, vì entity là phần chung của 2 module. Đây là nguồn cho mục 3 trong báo cáo của cả 2 người. Hoàng duyệt.
- **Input:** M1-01; luật chơi D2; schema mục 2.4; `REPORT_CONVENTION.md` mục 3 (bước A2, D1).
- **Output:** `docs/chung/02-lop-thuc-the.md` + 2 PNG:
  - **Phân tích (A2):**
    - Bảng trích danh từ: mỗi danh từ được giữ thành lớp, thành thuộc tính, hay bị loại, kèm lý do.
    - Biểu đồ lớp thực thể phân tích: không có `id`, không cần đủ thuộc tính.
    - Quan hệ `A --<> B` khi thông tin của A nằm trong B. Ví dụ `Player --<> GameRecord`.
  - **Thiết kế (D1), trình bày đủ 4 bước:**
    1. Thêm khóa `id` (với `RoomPlayer` là `uid`).
    2. Thêm kiểu dữ liệu Java.
    3. Chuyển hết liên kết thành kết tập/hợp thành, kèm bội số.
    4. Thêm thuộc tính đối tượng (`GameRecord` có `Player player`, `Room` có `List<RoomPlayer> players`…).
  - **Bảng "lớp nào dùng cho chức năng nào"**, để mỗi người biết lấy lớp nào vào mục 3 báo cáo của mình.
- **Requirement:** Không còn đường liên kết trơn trên biểu đồ thiết kế. Hoàng đã approve.
- **Constraint:** Thuộc tính dẫn xuất (D9) viết thành phương thức (`getResult()`, `getAccuracy()`), không lưu.

### [M1-03] Thiết kế CSDL: SQLite + Firebase · @HungNguyenBa1811
- **Phụ thuộc:** M1-02.
- **Mô tả:** Từ lớp thực thể thiết kế, làm theo 5 bước thiết kế CSDL. Đây là nguồn cho mục 2 trong báo cáo của cả 2 người: Hoàng dùng `tblPlayer` + `tblGameRecord`; Hùng dùng `tblMatchHistory` + `tblPlayer` + cây `rooms`. Hoàng duyệt.
- **Input:** `docs/chung/02-lop-thuc-the.md`; mục 2.4; `REPORT_CONVENTION.md` mục 3 (bước D2).
- **Output:** `docs/chung/03-csdl.md` + PNG:
  - **SQLite, đủ 5 bước:**
    1. Lớp thành bảng `tbl` + tên lớp.
    2. Thuộc tính không phải đối tượng thành cột.
    3. Quan hệ số lượng giữa các bảng.
    4. Khóa chính `id`; khóa ngoại `tblPlayerId`.
    5. Loại thuộc tính dẫn xuất theo D9.
  - **Câu lệnh `CREATE TABLE`** cho 3 bảng (M1-04 dán nguyên vào `DatabaseHelper`).
  - **Firebase:** ánh xạ `Room`, `RoomPlayer` sang cây `rooms` (lớp thành node, hợp thành thành node con).
  - Giải thích vì sao `MathProblem`, `GameSession` không lưu.
- **Requirement:** Mỗi bảng có khóa chính; khóa ngoại đúng chiều 1-n; cột khớp thuộc tính của lớp thực thể. Hoàng đã approve.
- **Constraint:** Không dùng Room ORM. Không có cột dẫn xuất.

### [M1-04] Khung code chung: entity, DAO cha, PlayerDAO, màn Chính · @HungNguyenBa1811
- **Phụ thuộc:** M1-02, M1-03.
- **Mô tả:** Code phần chung để 2 module dựng tiếp lên. Hoàng duyệt.
- **Input:** M1-02, M1-03; mục 2.2, 2.3; khung stub hiện tại.
- **Output:**
  - **7 entity:** đủ thuộc tính, kiểu, constructor, getter/setter theo M1-02. Thuộc tính dẫn xuất viết thành phương thức.
  - **`DatabaseHelper`:**
    - `onCreate` tạo 3 bảng bằng câu lệnh ở M1-03.
    - `onUpgrade`: DROP rồi tạo lại.
  - **`dao/DAO.java`** theo mục 2.3.
  - **`PlayerDAO extends DAO` (đầy đủ):** `findOrCreateByUsername(String)` trim tên, viết hoa; tên rỗng thì dùng `PLAYER_1`.
  - **`GameRecordDAO`, `MatchHistoryDAO` `extends DAO`:** chỉ có chữ ký hàm, thân `// TODO` cho chủ sở hữu.
  - **`FrmMainActivity` hoàn chỉnh:**
    - Tiêu đề game, ô nhập `etUsername`.
    - 5 nút, đều truyền tên qua Intent extra `"username"`:
      - `btnPlayOnline` → `FrmLobbyActivity`.
      - `btnPlayOffline` → `FrmGameActivity`.
      - `btnHistory` → `FrmHistoryActivity`.
      - `btnLeaderboard` → `FrmLeaderboardActivity`.
      - `btnSettings` → `FrmSettingsActivity`.
  - **Vỏ Activity:** `FrmLobbyActivity`, `FrmOnlineGameActivity`, `FrmHistoryActivity`, `FrmSettingsActivity` (chỉ `setContentView`), khai báo trong Manifest.
  - **Thư viện:** `androidx.recyclerview` trong `libs.versions.toml` và `app/build.gradle.kts`.
  - **Tài liệu:** `PLAN.md` và README dùng tên bảng `tbl*`.
- **Requirement:**
  - Build pass.
  - 5 nút mở đúng màn.
  - Log: gọi `findOrCreateByUsername("hung")` 2 lần thì trả về cùng id; tên `O'NEIL` không làm lỗi SQL.
  - Hoàng đã approve.
- **Constraint:**
  - `ContentValues` và placeholder `?`, không nối chuỗi.
  - Truy vấn SQLite trong `Thread`.
  - Không dùng lambda.

### [M1-05] Lõi câu hỏi và luật điểm: MathProblem, ProblemDAO, GameSession · @HungNguyenBa1811
- **Phụ thuộc:** M1-02.
- **Mô tả:** Lõi dùng chung cho UC2 (online) và UC4 (offline). Hoàng duyệt kỹ, vì 3 lớp này nằm trong biểu đồ lớp của "Chơi offline" và Hoàng phải giải thích được khi demo.
- **Input:** Stub hiện tại; luật trong README và D2; M1-02.
- **Output:**
  - **`MathProblem`:** `getExpression()` trả chuỗi kiểu `"12 + 7"`; `isCorrect(int)`.
  - **`GameSession`:**
    - `reset()`.
    - `recordCorrectAnswer()`: cộng `10 + streak * 2`, trả về số điểm vừa cộng.
    - `recordWrongAnswer()`: reset combo, trừ 1 mạng; hết mạng thì game over.
    - Chế độ online (D2): trả lời sai không trừ mạng.
  - **`ProblemDAO.generateProblem(int level)`**, với `level = min(questionIndex / 5 + 1, 4)`:

    | Level | Phép tính | Khoảng số |
    |---|---|---|
    | 1 | `+ −` | 1–10 |
    | 2 | `+ −` | 1–30 |
    | 3 | `+ − ×` | `+ −`: 1–50, `×`: 2–9 |
    | 4 | `+ − ×` | `+ −`: 1–100, `×`: 2–12 |

    4 đáp án khác nhau, không âm, có đáp án đúng, xáo trộn bằng `Collections.shuffle(list, random)`.
  - **`GameSessionTest`:** trạng thái ban đầu; combo +10 rồi +12; sai thì reset combo và trừ mạng; sai 3 lần thì game over; online sai không trừ mạng.
  - **`ProblemDAOTest`:**
    - Sinh 1000 câu mỗi level: không âm, 4 đáp án khác nhau, có đáp án đúng.
    - **2 `ProblemDAO` cùng seed sinh ra 30 câu giống hệt nhau.**
- **Requirement:** `gradlew test` pass. Hoàng đã approve.
- **Constraint:**
  - Entity không import `android.*`.
  - Mọi chỗ random đi qua `this.random`, không dùng `Math.random()` hay `new Random()` bên trong hàm.

---

## 5. MILESTONE 2 — Code module & Demo (28/10)

### Mô tả milestone (dùng làm description cho Milestone `M2 - Demo`)

**Người phụ trách:**
- Hùng: M2-01 → M2-05, làm từ 22/10.
- Hoàng: M2-06 → M2-08, làm trong 26–27/10.
- Cả 2: M2-09, tổng duyệt tối 27/10.

**Hạn:** **28/10/2026, ngày demo.**

**Mục tiêu.** Mỗi người có 3 chức năng của mình chạy được trên máy ảo, demo trơn tru và giải thích được code của chính mình.

**Trong phạm vi:** Thân hàm, layout, Adapter, câu SQL / truy vấn Firebase của 6 UC; âm thanh và rung.

**Ngoài phạm vi:**
- Đăng nhập email, chơi từ 3 người trở lên, chống gian lận.
- Sửa lớp "Chung" mà không qua PR được người kia duyệt.
- Vẽ biểu đồ: làm sau demo, ở M3 và M4.

**Sản phẩm bàn giao:**
- Hùng: `FirebaseDAO`, `AuthDAO`, `RoomDAO`, `MatchController`, `MatchHistoryDAO`, `FrmLobbyActivity`, `FrmOnlineGameActivity`, `FrmHistoryActivity`, `HistoryAdapter` + layout.
- Hoàng: `GameController`, `GameRecordDAO`, `FrmGameActivity`, `FrmLeaderboardActivity`, `LeaderboardAdapter`, `FrmSettingsActivity`, `SoundManager` + layout + `res/raw/*.mp3`.

**Quy tắc sửa file (tránh conflict):**
- Mỗi người chỉ sửa file mình sở hữu ở mục 2.2.
- File dùng chung (`AndroidManifest.xml`, `build.gradle.kts`, `libs.versions.toml`): pull `main` trước khi sửa, chỉ thêm đúng dòng cần, merge sớm.

**Điều kiện hoàn thành milestone:**
1. Mọi issue M2 đã đóng, hoặc đã cắt theo D10.
2. Trên `main`, mỗi người chạy được trọn 3 chức năng của mình, bắt đầu từ màn Chính, không crash.
3. Tìm `"-> "` trong `app/src/main/java` không ra kết quả nào (không dùng lambda).

**Rủi ro đã biết:**
- Hoàng chỉ có 2 ngày, nên ưu tiên UC4 → UC5 → UC6; UC6 cắt đầu tiên (D10).
- Khi mất mạng đột ngột, Firebase có thể mất vài chục giây mới báo `onDisconnect`. Lúc demo nên dùng cách bấm Back để minh họa việc thoát trận.
- `VibrationEffect` chỉ có từ API 26, trong khi minSdk là 24. Phải rẽ nhánh theo `Build.VERSION.SDK_INT`.

### [M2-01] Hạ tầng Firebase: FirebaseDAO + đăng nhập ẩn danh · @HungNguyenBa1811
- **Phụ thuộc:** không có. Làm song song với M1 từ 22/10.
- **Mô tả:** Tạo project Firebase, nối với app, viết `FirebaseDAO` và `AuthDAO`. Phục vụ UC1 (Tìm trận include đăng nhập ẩn danh).
- **Input:** applicationId `ptit.e23cnpm01.nguyenbahung.math_dash`; mục 2.3.
- **Output:**
  - **Cấu hình:**
    - File `app/google-services.json`.
    - Plugin `com.google.gms.google-services`.
    - Firebase BoM + `firebase-auth` + `firebase-database`.
    - Quyền `INTERNET` và `ACCESS_NETWORK_STATE`.
  - **Database Rules:** `auth != null`; `.indexOn: "status"` cho `rooms`.
  - **Code:**
    - `dao/FirebaseDAO.java` theo mục 2.3.
    - `dao/AuthDAO.java extends FirebaseDAO` có `signIn(AuthCallback)` và `getUid()`.
    - Interface `AuthCallback { onSuccess(String uid); onError(String message); }`.
- **Requirement:**
  - Logcat in `Firebase OK`.
  - Tắt app mở lại vẫn giữ UID cũ.
  - Mất mạng thì gọi `onError` với thông báo tiếng Việt, app không crash.
- **Constraint:** Không để rules public. Không dùng Firestore.

### [M2-02] UC Tìm trận: FrmLobbyActivity + RoomDAO + MatchController · @HungNguyenBa1811
- **Phụ thuộc:** M1-04, M2-01.
- **Mô tả:** Người chơi bấm CHƠI NGAY thì app tìm phòng `waiting`: có thì vào, không có thì tạo phòng mới rồi chờ.
- **Input:** Tên người chơi từ Intent; schema mục 2.4.
- **Output:**
  - **`RoomDAO extends FirebaseDAO`:**
    - `findWaitingRoom()`: query `orderByChild("status").equalTo("waiting").limitToFirst(1)`, kết quả đóng gói vào `Room`.
    - `createRoom(seed)`.
    - `joinRoom(roomId)`: dùng `runTransaction`.
  - **`MatchController` (Singleton):**
    - `findMatch(name, listener)`.
    - `cancelFind()`: nếu mình là chủ phòng thì xóa phòng.
  - **Xử lý phòng chờ:**
    - Khi tạo phòng: `onDisconnect().removeValue()`.
    - Khi có người vào: `onDisconnect().cancel()`.
  - **`FrmLobbyActivity`** + layout: tên mình; tên đối thủ hoặc "Đang tìm đối thủ..." kèm ProgressBar; dòng trạng thái; nút HỦY. Có đối thủ thì sau 1.5s tự chuyển sang `FrmOnlineGameActivity`.
- **Requirement:**
  - 2 máy bấm gần như cùng lúc thì vào chung 1 phòng.
  - Máy thứ 3 phải tạo phòng mới.
  - Đủ 2 người thì phòng chuyển sang `playing`.
  - Bấm HỦY hoặc kill app khi đang chờ thì phòng biến mất trên Console.
- **Constraint:**
  - Join bắt buộc qua transaction, kiểm tra `status == "waiting"` và số người < 2 *bên trong* transaction.
  - Activity không gọi Firebase trực tiếp, chỉ gọi `MatchController`.

### [M2-03] UC Thi đấu online: FrmOnlineGameActivity + đồng bộ realtime · @HungNguyenBa1811
- **Phụ thuộc:** M1-05, M2-02.
- **Mô tả:** 2 máy chơi cùng một dãy câu theo seed, mỗi máy ghi điểm của mình và lắng nghe điểm đối thủ.
- **Input:** Phòng đang `playing`; `seed`; uid đối thủ.
- **Output:**
  - **`MatchController`:**
    - `startGame(listener)`: dựng `ProblemDAO(new Random(seed))`; `CountDownTimer` 60s tick mỗi 1s.
    - `submitAnswer(value)`: chấm bằng `GameSession`, rồi `setValue` vào `players/{myUid}/score`.
    - `addValueEventListener` trên `players/{opponentUid}/score`.
    - Hết giờ: set `finished = true`. Cả 2 cùng `finished` (hoặc quá 3s sau khi hết giờ) thì gọi `onMatchEnd(MatchHistory)`.
  - **`FrmOnlineGameActivity`** + layout: đồng hồ 60s, phép tính, lưới 2x2 nút đáp án, điểm **MÌNH vs ĐỐI THỦ**, overlay THẮNG / THUA / HÒA có tỉ số và nút "Về menu".
- **Requirement:**
  - 2 máy thấy cùng một dãy câu.
  - Điểm đối thủ cập nhật dưới 1s.
  - Kết quả 2 máy khớp nhau (A `WIN` thì B `LOSE`, hoặc cả 2 `DRAW`).
- **Constraint:**
  - Chỉ ghi vào node của chính mình.
  - `removeEventListener` và hủy timer khi kết thúc.
  - Không tính điểm trong Activity.

### [M2-04] UC Thi đấu online: xử lý thoát trận và mất mạng · @HungNguyenBa1811
- **Phụ thuộc:** M2-03.
- **Mô tả:** Các ngoại lệ của UC2, để không bao giờ để trận bị treo hay để lại phòng "rác".
- **Input:** Phòng hiện tại, uid.
- **Output:**
  - Khi vào trận: `onDisconnect().setValue(false)` cho `players/{uid}/online`.
  - Đối thủ `online == false` thì gọi `onOpponentLeft()` và tính mình thắng.
  - `leaveMatch()` (nút Back + `AlertDialog` "Thoát sẽ bị xử thua?"): set `online = false`, mình bị xử thua.
  - Lắng nghe `.info/connected`; mất kết nối thì gọi `onConnectionLost()`.
- **Requirement:** Pass cả 4 test case:
  1. Máy A đang chờ thì kill app → phòng biến mất.
  2. Đang chơi, B bấm Back → A thắng ngay.
  3. Đang chơi, B tắt Wi-Fi → A nhận "Đối thủ đã thoát". Có thể mất vài chục giây, chấp nhận được.
  4. A mất mạng → A thấy thông báo, app không crash.
- **Constraint:** Không còn phòng `waiting` nào tồn tại khi không có người chờ.

### [M2-05] UC Quản lý lịch sử đấu: MatchHistoryDAO + FrmHistoryActivity · @HungNguyenBa1811
- **Phụ thuộc:** M1-04, M2-03.
- **Mô tả:** Lưu kết quả khi trận online kết thúc, xem danh sách, xóa một trận.
- **Input:** `tblMatchHistory` và `DAO` (M1-04); `PlayerDAO` (M1-04).
- **Output:**
  - **`MatchHistoryDAO`:**
    - `insert(MatchHistory)`.
    - `getAll(Player)`: mới nhất xếp trước.
    - `delete(int id)`.
  - **Lưu kết quả:** khi `onMatchEnd`, `FrmOnlineGameActivity` gọi `insert` trong `Thread`.
  - **`FrmHistoryActivity`** + `HistoryAdapter` (Pattern 1) + `item_history.xml`:
    - Mỗi dòng gồm thời gian, đối thủ, tỉ số `X - Y`, kết quả: WIN màu `arcade_green`, LOSE màu `arcade_red`, DRAW màu vàng.
    - Kết quả lấy từ `getResult()`.
  - **Xóa:** nhấn giữ 1 dòng thì hiện `AlertDialog` "Xóa trận này?"; đồng ý thì `delete` + `notifyItemRemoved`.
- **Requirement:**
  - Đấu xong 1 trận thì Lịch sử có thêm dòng mới.
  - Chưa có trận thì hiện "Chưa có trận nào".
  - Xoay màn hình không crash.
- **Constraint:**
  - SQLite trong `Thread` + `runOnUiThread`.
  - `delete` dùng placeholder `"id = ?"`.
  - Không dùng DiffUtil, ListAdapter.

### [M2-06] UC Chơi offline: GameController + FrmGameActivity + lưu kết quả · @Minahn210
- **Phụ thuộc:** M1-04, M1-05.
- **Mô tả:** Chức năng chính Hoàng demo. Làm ngày 26/10.
- **Input:** Tên người chơi từ Intent (`"username"`); `PlayerDAO`, `GameSession`, `ProblemDAO` (M1); chữ ký `GameController`, `GameRecordDAO` có sẵn trong khung.
- **Output:**
  - **`GameController`:**
    - Constructor nhận `(GameSession, ProblemDAO, int bestScore, GameControllerListener)`.
    - `startGame()`.
    - `CountDownTimer` 6s mỗi câu; hết giờ tính như trả lời sai.
    - `submitAnswer()`.
    - Level tăng theo số câu đã ra.
    - `onPause`/`onResume`/`onDestroy`: dừng hoặc chạy lại timer.
    - Hết mạng thì gọi `onGameOver(session, score > bestScore)`.
  - **`GameRecordDAO`:**
    - `insertRecord(GameRecord)`.
    - `getBestScore(Player)`: `SELECT MAX(score)`.
  - **`FrmGameActivity`** + layout:
    - Hiển thị: tên, BEST, điểm, combo `xN`, mạng `♥ ♥ ♥`, thanh thời gian, phép tính.
    - Lưới 2x2 nút đáp án, cao ≥ 72dp, chữ monospace 26sp.
    - Dòng phản hồi `+12 PTS!` / `MISS!`.
    - Overlay Game Over: điểm cuối, kỷ lục, nút CHƠI LẠI / VỀ MENU.
    - Khi `onGameOver`: lưu `GameRecord` trong `Thread`.
- **Requirement:**
  - Hết giờ thì mất mạng; hết 3 mạng thì hiện Game Over.
  - Ván vừa chơi được lưu vào `tblGameRecord`.
  - Vượt kỷ lục thì hiện "KỶ LỤC MỚI".
- **Constraint:**
  - Không tính điểm trong Activity (logic nằm ở `GameController` / `GameSession`).
  - Hủy timer trong `onDestroy()`.

### [M2-07] UC Xem bảng vàng: FrmLeaderboardActivity + LeaderboardAdapter · @Minahn210
- **Phụ thuộc:** M2-06.
- **Mô tả:** Hiển thị top điểm offline. Làm ngày 27/10.
- **Input:** `tblGameRecord`, `tblPlayer`; Pattern 1 trong CONVENTION.
- **Output:**
  - **`GameRecordDAO.getTopRecords(int limit)`:**
    - JOIN `tblGameRecord` với `tblPlayer`, sắp `score DESC`.
    - Mỗi dòng đóng gói vào `GameRecord` có `Player` bên trong.
  - **`FrmLeaderboardActivity`** + `LeaderboardAdapter` + `item_leaderboard.xml`:
    - Hiện top 20.
    - Mỗi dòng gồm hạng, tên, điểm, combo cao nhất, % chính xác (`getAccuracy()`).
- **Requirement:**
  - Chơi 3 ván thì Bảng vàng xếp đúng thứ tự.
  - Chưa có dữ liệu thì hiện "Chưa có kỷ lục".
- **Constraint:**
  - Truy vấn trong `Thread`.
  - Không dùng DiffUtil, ListAdapter, ViewBinding.

### [M2-08] UC Cài đặt âm thanh: SoundManager + FrmSettingsActivity · @Minahn210
- **Phụ thuộc:** M2-06.
- **Mô tả:** Âm thanh và rung khi trả lời, người chơi tắt/bật được. Làm ngày 27/10. **Bị cắt đầu tiên** nếu không kịp (D10).
- **Input:** Pattern 4 (Singleton).
- **Output:**
  - `res/raw/correct.mp3`, `wrong.mp3`, `timeout.mp3`: lấy từ nguồn miễn phí, ghi lại nguồn.
  - **`util/SoundManager.java`** dạng Singleton:
    - `playSound(context, resId)` và `vibrate(context, ms)`.
    - Kiểm tra cờ trong SharedPreferences `"settings"` trước khi phát âm hoặc rung.
  - Quyền `VIBRATE` trong Manifest.
  - **`FrmSettingsActivity`** + layout: 2 Switch Âm thanh / Rung.
  - Gắn vào `FrmGameActivity` (khi trả lời và khi hết giờ).
- **Requirement:** Bật/tắt có hiệu lực ngay và vẫn giữ sau khi tắt app mở lại.
- **Constraint:**
  - `release()` MediaPlayer cũ trước khi tạo cái mới.
  - Kiểm tra `Build.VERSION.SDK_INT` trước khi dùng `VibrationEffect`.
  - Mỗi file âm thanh < 100KB.

### [M2-09] Tổng duyệt demo · @HungNguyenBa1811 @Minahn210
- **Phụ thuộc:** M2-01 → M2-08.
- **Mô tả:** Tối 27/10, chạy thử đúng như buổi demo trên bản `main` cuối cùng.
- **Input:** `main` đã merge đủ 2 module.
- **Output:**
  - **Kịch bản demo của mỗi người** (dán vào comment issue): thứ tự thao tác cho 3 chức năng, dữ liệu chuẩn bị sẵn. Ví dụ chơi trước 3 ván để Bảng vàng có dữ liệu; đấu trước 1 trận để Lịch sử có dữ liệu.
  - **Hùng:** chạy UC1–UC3 trên 2 máy ảo, gồm cả 4 test case của M2-04.
  - **Hoàng:** chạy UC4–UC6 trên 1 máy ảo.
  - Lỗi phát hiện được thành issue `bug`, sửa ngay trong tối 27/10.
- **Requirement:** Mỗi người chạy trọn 3 chức năng của mình từ màn Chính, không crash, 2 lần liên tiếp.
- **Constraint:** Không thêm tính năng mới trong issue này.

---

## 6. MILESTONE 3 — Phân tích cá nhân · label `ooad`

### Mô tả milestone (dùng làm description cho Milestone `M3 - Phan tich`)

**Người phụ trách:** Hùng làm UC1–UC3, Hoàng làm UC4–UC6.
**Thời gian:** 29/10 → **04/11/2026**.

**Mục tiêu.** Bước phân tích OOAD cho từng chức năng mình đã demo:
- Đặc tả và kịch bản v1.
- Biểu đồ lớp phân tích.
- Kịch bản v2 kèm biểu đồ tuần tự phân tích.

Lớp thực thể đã có ở M1-02. Ở bước này, mỗi người đối chiếu lại với kịch bản của mình; thiếu lớp thì mở PR bổ sung `docs/chung/02-lop-thuc-the.md`.

**Trong phạm vi:** 3 loại đối tượng: tác nhân, lớp `XxxView`, lớp thực thể.

**Ngoài phạm vi:** DAO, Controller, Activity → milestone 4.

**Sản phẩm bàn giao:**
- Hùng: `docs/hung/1-kich-ban-v1.md`, `2-lop-phan-tich.md` + 3 PNG, `3-kich-ban-v2.md` + 3 PNG.
- Hoàng: `docs/hoang/` tương tự.

**Điều kiện hoàn thành milestone:**
1. 6 issue M3-01 → M3-06 đã đóng.
2. Mỗi phương thức trên biểu đồ tuần tự v2 đều có trên biểu đồ lớp phân tích tương ứng.
3. Mỗi lớp thực thể trên biểu đồ lớp phân tích đều có trong `docs/chung/02-lop-thuc-the.md`.

### [M3-01] Đặc tả + kịch bản v1: Tìm trận, Thi đấu online, Quản lý lịch sử đấu · @HungNguyenBa1811
- **Phụ thuộc:** M2-09.
- **Mô tả:** Mục 1 trong báo cáo của Hùng. Làm theo `REPORT_CONVENTION.md` bước A1, mô tả đúng như app đã demo.
- **Input:** App thật sau demo; M1-01.
- **Output:** `docs/hung/1-kich-ban-v1.md`. Với mỗi UC:
  - **Đặc tả** theo mẫu luồng nghiệp vụ "… → …".
  - **Kịch bản chuẩn v1** với dữ liệu cụ thể.
  - **Kịch bản ngoại lệ**, tối thiểu:
    - Tìm trận: không có mạng; bấm Hủy khi đang chờ; 2 người bấm cùng lúc.
    - Thi đấu online: đối thủ thoát; mình mất mạng; hòa.
    - Quản lý lịch sử đấu: chưa có trận; bấm xóa rồi chọn Hủy.
- **Requirement:** Không có tên lớp hay tên hàm trong kịch bản v1. Mỗi bước của hệ thống ghi rõ giao diện hiện ra gồm thành phần gì.
- **Constraint:** Dùng đúng tên UC ở M1-01.

### [M3-02] Đặc tả + kịch bản v1: Chơi offline, Xem bảng vàng, Cài đặt âm thanh · @Minahn210
- **Phụ thuộc:** M2-09.
- **Mô tả:** Mục 1 trong báo cáo của Hoàng. Làm theo `REPORT_CONVENTION.md` bước A1.
- **Input:** App thật sau demo; M1-01; `docs/hung/1-kich-ban-v1.md` để tham khảo cách viết.
- **Output:** `docs/hoang/1-kich-ban-v1.md`. Ngoại lệ tối thiểu:
  - Chơi offline: bỏ trống tên (dùng `PLAYER_1`); hết giờ 1 câu; hết 3 mạng; đạt kỷ lục mới; bấm Back giữa ván.
  - Xem bảng vàng: chưa có ván nào.
  - Cài đặt âm thanh: tắt âm thanh rồi vào chơi.
- **Requirement:** Giống M3-01.
- **Constraint:** Dùng đúng tên UC ở M1-01.

### [M3-03] Biểu đồ lớp phân tích: Tìm trận, Thi đấu online, Quản lý lịch sử đấu · @HungNguyenBa1811
- **Phụ thuộc:** M3-01.
- **Mô tả:** Bước A3 trong `REPORT_CONVENTION.md`. Mỗi UC một biểu đồ, gồm các lớp View và lớp thực thể liên quan.
- **Input:** Kịch bản v1 (M3-01); `docs/chung/02-lop-thuc-the.md`; bảng tên View ở mục 2.2.
- **Output:** `docs/hung/2-lop-phan-tich.md` + 3 PNG:
  - Lớp `XxxView` có thành phần đặt tiền tố `in` / `out` / `sub`.
  - Mỗi phương thức trả lời 4 câu hỏi: tên, đầu vào, đầu ra, gán vào lớp thực thể nào.
- **Requirement:** Mỗi thành phần giao diện nêu trong kịch bản v1 đều có trong View tương ứng.
- **Constraint:** Chưa có DAO, Controller, Activity.

### [M3-04] Biểu đồ lớp phân tích: Chơi offline, Xem bảng vàng, Cài đặt âm thanh · @Minahn210
- **Phụ thuộc:** M3-02.
- **Mô tả:** Như M3-03, cho UC4–UC6.
- **Input:** Kịch bản v1 (M3-02); `docs/chung/02-lop-thuc-the.md`; `docs/hung/2-lop-phan-tich.md` để tham khảo cách làm.
- **Output:** `docs/hoang/2-lop-phan-tich.md` + 3 PNG. View dự kiến: `MainView`, `GameView`, `GameOverView`, `LeaderboardView`, `SettingsView`.
- **Requirement:** Giống M3-03.
- **Constraint:** Giống M3-03.

### [M3-05] Kịch bản v2 + biểu đồ tuần tự phân tích: Tìm trận, Thi đấu online, Quản lý lịch sử đấu · @HungNguyenBa1811
- **Phụ thuộc:** M3-03.
- **Mô tả:** Bước A4 trong `REPORT_CONVENTION.md`.
- **Input:** Kịch bản v1 (M3-01); biểu đồ lớp phân tích (M3-03).
- **Output:** `docs/hung/3-kich-ban-v2.md` + 3 PNG. Với mỗi UC:
  - Kịch bản v2: mỗi bước đúng một thông điệp giữa Người chơi, `XxxView` và lớp thực thể.
  - Biểu đồ tuần tự đánh số giống kịch bản.
  - Ngoại lệ chính vẽ bằng khung `alt`.
- **Requirement:** Số bước bằng số thông điệp, cùng thứ tự. Mọi phương thức trên biểu đồ đã có ở M3-03.
- **Constraint:** Chỉ có 3 loại đối tượng: tác nhân, View, thực thể.

### [M3-06] Kịch bản v2 + biểu đồ tuần tự phân tích: Chơi offline, Xem bảng vàng, Cài đặt âm thanh · @Minahn210
- **Phụ thuộc:** M3-04.
- **Mô tả:** Như M3-05, cho UC4–UC6.
- **Input:** Kịch bản v1 (M3-02); biểu đồ lớp phân tích (M3-04); `docs/hung/3-kich-ban-v2.md` để tham khảo cách làm.
- **Output:** `docs/hoang/3-kich-ban-v2.md` + 3 PNG.
- **Requirement:** Giống M3-05.
- **Constraint:** Giống M3-05.

---

## 7. MILESTONE 4 — Thiết kế cá nhân & Báo cáo

### Mô tả milestone (dùng làm description cho Milestone `M4 - Thiet ke & Bao cao`)

**Người phụ trách:** Hùng làm UC1–UC3 và báo cáo của Hùng; Hoàng làm UC4–UC6 và báo cáo của Hoàng.
**Thời gian:** 05/11 → **15/11/2026**. Biểu đồ thiết kế xong trước 11/11; 12–15/11 hoàn thiện báo cáo và đọc chéo.

**Mục tiêu.** Biểu đồ lớp chi tiết và biểu đồ tuần tự chi tiết cho từng chức năng, **vẽ theo code thật**, rồi ghép thành báo cáo cá nhân đúng khung đề.

**Trong phạm vi:**
- Thiết kế giao diện (ảnh app thật + bảng thành phần).
- Biểu đồ lớp chi tiết (boundary – control – entity, client + server).
- Kịch bản v3 + biểu đồ tuần tự chi tiết.
- Hai bài báo cáo.

**Ngoài phạm vi:**
- Thêm tính năng.
- Sửa code chỉ để biểu đồ đẹp hơn. Nếu code và biểu đồ lệch nhau, sửa biểu đồ theo code; trừ khi phát hiện bug thì mở issue `bug`.

**Sản phẩm bàn giao:**
- `docs/hung/4-lop-thiet-ke.md`, `5-kich-ban-v3.md` + PNG.
- `docs/hoang/` tương tự.
- `docs/BaoCao_Hung.docx`, `docs/BaoCao_Hoang.docx`.

**Điều kiện hoàn thành milestone:**
1. 6 issue M4-01 → M4-06 đã đóng.
2. Mỗi báo cáo tick đủ checklist ở `REPORT_CONVENTION.md` mục 5.
3. Mọi tên lớp, hàm, bảng trong báo cáo đều có trong code ở `main`.

### [M4-01] Thiết kế giao diện + biểu đồ lớp chi tiết: Tìm trận, Thi đấu online, Quản lý lịch sử đấu · @HungNguyenBa1811
- **Phụ thuộc:** M3-03, M2-09.
- **Mô tả:** Bước D3 trong `REPORT_CONVENTION.md`. Đây là mục 4.x.1 và 4.x.2 trong báo cáo của Hùng.
- **Input:** Code thật trên `main`; biểu đồ lớp phân tích (M3-03); M1-02, M1-03.
- **Output:** `docs/hung/4-lop-thiet-ke.md` + 3 PNG. Với mỗi UC:
  - **Thiết kế giao diện:** ảnh chụp app thật; bảng thành phần (`id` trong layout, loại View, chức năng, tên ở bước phân tích).
  - **Biểu đồ lớp chi tiết:**
    - Boundary: `FrmLobbyActivity`, `FrmOnlineGameActivity`, `FrmHistoryActivity`, `HistoryAdapter`, các listener.
    - Control: `MatchController`, `FirebaseDAO` ← `AuthDAO`, `RoomDAO`; `DAO` ← `MatchHistoryDAO`, `PlayerDAO`; `ProblemDAO`.
    - Entity: `Room`, `RoomPlayer`, `MatchHistory`, `Player`, `MathProblem`, `GameSession`.
    - Mọi phương thức ghi đủ tham số và kiểu trả về.
  - **Mô tả từng lớp 1–2 dòng.**
- **Requirement:** Mỗi View ở M3-03 ánh xạ sang đúng một Activity hoặc overlay. Tên lớp và hàm trùng với code.
- **Constraint:** Phía server (Firebase) vẽ chung trên biểu đồ.

### [M4-02] Thiết kế giao diện + biểu đồ lớp chi tiết: Chơi offline, Xem bảng vàng, Cài đặt âm thanh · @Minahn210
- **Phụ thuộc:** M3-04, M2-09.
- **Mô tả:** Như M4-01, cho UC4–UC6. Đây là mục 4.x.1 và 4.x.2 trong báo cáo của Hoàng.
- **Input:** Code thật trên `main`; biểu đồ lớp phân tích (M3-04); M1-02, M1-03; `docs/hung/4-lop-thiet-ke.md` để tham khảo cách làm.
- **Output:** `docs/hoang/4-lop-thiet-ke.md` + 3 PNG. Với mỗi UC:
  - Thiết kế giao diện: ảnh app thật và bảng thành phần.
  - Biểu đồ lớp chi tiết:
    - Boundary: `FrmMainActivity`, `FrmGameActivity`, `FrmLeaderboardActivity`, `LeaderboardAdapter`, `FrmSettingsActivity`.
    - Control: `GameController`, `DAO` ← `PlayerDAO`, `GameRecordDAO`; `DatabaseHelper`, `ProblemDAO`, `SoundManager`.
    - Entity: `Player`, `GameRecord`, `MathProblem`, `GameSession`.
  - Mô tả từng lớp 1–2 dòng.
- **Requirement:** Giống M4-01.
- **Constraint:** Tên lớp và hàm trùng với code.

### [M4-03] Kịch bản v3 + biểu đồ tuần tự chi tiết: Tìm trận, Thi đấu online, Quản lý lịch sử đấu · @HungNguyenBa1811
- **Phụ thuộc:** M3-05, M4-01.
- **Mô tả:** Bước D4 trong `REPORT_CONVENTION.md`. Đây là mục 4.x.3 trong báo cáo của Hùng.
- **Input:** Kịch bản v2 (M3-05); biểu đồ lớp chi tiết (M4-01); code thật.
- **Output:** `docs/hung/5-kich-ban-v3.md` + 3 PNG. Với mỗi UC:
  - Kịch bản v3 đánh số, mỗi bước một thông điệp, tên hàm thật (`onClick`, `findMatch`, `findWaitingRoom`, `onDataChange`…).
  - Biểu đồ tuần tự đủ boundary – control – entity, khớp 1-1 với kịch bản.
  - Ngoại lệ thoát trận, mất mạng vẽ bằng khung `alt`.
- **Requirement:** Số bước bằng số thông điệp, cùng thứ tự. Mọi hàm trên biểu đồ có trong M4-01.
- **Constraint:** Callback Firebase vẽ theo quy ước ở `REPORT_CONVENTION.md` bước D4.

### [M4-04] Kịch bản v3 + biểu đồ tuần tự chi tiết: Chơi offline, Xem bảng vàng, Cài đặt âm thanh · @Minahn210
- **Phụ thuộc:** M3-06, M4-02.
- **Mô tả:** Như M4-03, cho UC4–UC6. Đây là mục 4.x.3 trong báo cáo của Hoàng.
- **Input:** Kịch bản v2 (M3-06); biểu đồ lớp chi tiết (M4-02); code thật; `docs/hung/5-kich-ban-v3.md` để tham khảo cách làm.
- **Output:** `docs/hoang/5-kich-ban-v3.md` + 3 PNG. `CountDownTimer` và `Thread` vẽ theo quy ước ở `REPORT_CONVENTION.md` bước D4.
- **Requirement:** Giống M4-03.
- **Constraint:** Mọi hàm trên biểu đồ có trong M4-02.

### [M4-05] Báo cáo cá nhân – Hùng (Tìm trận, Thi đấu online, Quản lý lịch sử đấu) · @HungNguyenBa1811
- **Phụ thuộc:** M1-02, M1-03, M3-01, M4-01, M4-03.
- **Mô tả:** Báo cáo thiết kế cá nhân của Hùng, đúng khung ở `REPORT_CONVENTION.md` mục 2. Mỗi mục dán vào ngay khi issue nguồn được đóng.
- **Input:** `REPORT_CONVENTION.md` (khung + checklist trước khi nộp); tài liệu trong `docs/chung/` và `docs/hung/`.
- **Output:**
  - Một file báo cáo online của riêng Hùng; dán link vào issue này.
  - Bản cuối xuất ra `docs/BaoCao_Hung.docx`.
  - Thứ tự mục bắt buộc:

    | Mục | Nội dung | Lấy từ |
    |---|---|---|
    | 1 | Mô tả chi tiết 3 chức năng bằng ngôn ngữ tự nhiên | M3-01 |
    | 2 | Thiết kế CSDL: `tblMatchHistory`, `tblPlayer` và cây `rooms` trên Firebase | M1-03 |
    | 3 | Thiết kế lớp thực thể: `Room`, `RoomPlayer`, `MatchHistory`, `Player`, `MathProblem`, `GameSession` | M1-02 |
    | 4.1.1 | Tìm trận: thiết kế giao diện | M4-01 |
    | 4.1.2 | Tìm trận: biểu đồ lớp chi tiết | M4-01 |
    | 4.1.3 | Tìm trận: kịch bản v3 + biểu đồ tuần tự chi tiết | M4-03 |
    | 4.2.1 | Thi đấu online: thiết kế giao diện | M4-01 |
    | 4.2.2 | Thi đấu online: biểu đồ lớp chi tiết | M4-01 |
    | 4.2.3 | Thi đấu online: kịch bản v3 + biểu đồ tuần tự chi tiết (cả ngoại lệ thoát trận, mất mạng) | M4-03 |
    | 4.3.1 | Quản lý lịch sử đấu: thiết kế giao diện | M4-01 |
    | 4.3.2 | Quản lý lịch sử đấu: biểu đồ lớp chi tiết | M4-01 |
    | 4.3.3 | Quản lý lịch sử đấu: kịch bản v3 + biểu đồ tuần tự chi tiết | M4-03 |

- **Requirement:** Mọi mục đã tick; tick đủ checklist `REPORT_CONVENTION.md` mục 5; Hoàng đã đọc lại.
- **Constraint:** Bài cá nhân, tự viết. Phần CSDL và lớp thực thể chỉ lấy phần liên quan chức năng của mình.

### [M4-06] Báo cáo cá nhân – Hoàng (Chơi offline, Xem bảng vàng, Cài đặt âm thanh) · @Minahn210
- **Phụ thuộc:** M1-02, M1-03, M3-02, M4-02, M4-04.
- **Mô tả:** Báo cáo thiết kế cá nhân của Hoàng, đúng khung ở `REPORT_CONVENTION.md` mục 2. Mỗi mục dán vào ngay khi issue nguồn được đóng.
- **Input:** `REPORT_CONVENTION.md` (khung + checklist trước khi nộp); tài liệu trong `docs/chung/` và `docs/hoang/`.
- **Output:**
  - Một file báo cáo online của riêng Hoàng; dán link vào issue này.
  - Bản cuối xuất ra `docs/BaoCao_Hoang.docx`.
  - Thứ tự mục bắt buộc:

    | Mục | Nội dung | Lấy từ |
    |---|---|---|
    | 1 | Mô tả chi tiết 3 chức năng bằng ngôn ngữ tự nhiên | M3-02 |
    | 2 | Thiết kế CSDL: `tblPlayer`, `tblGameRecord` | M1-03 |
    | 3 | Thiết kế lớp thực thể: `Player`, `GameRecord`, `MathProblem`, `GameSession` | M1-02 |
    | 4.1.1 | Chơi offline: thiết kế giao diện | M4-02 |
    | 4.1.2 | Chơi offline: biểu đồ lớp chi tiết | M4-02 |
    | 4.1.3 | Chơi offline: kịch bản v3 + biểu đồ tuần tự chi tiết | M4-04 |
    | 4.2.1 | Xem bảng vàng: thiết kế giao diện | M4-02 |
    | 4.2.2 | Xem bảng vàng: biểu đồ lớp chi tiết | M4-02 |
    | 4.2.3 | Xem bảng vàng: kịch bản v3 + biểu đồ tuần tự chi tiết | M4-04 |
    | 4.3.1 | Cài đặt âm thanh: thiết kế giao diện | M4-02 |
    | 4.3.2 | Cài đặt âm thanh: biểu đồ lớp chi tiết | M4-02 |
    | 4.3.3 | Cài đặt âm thanh: kịch bản v3 + biểu đồ tuần tự chi tiết | M4-04 |

- **Requirement:** Giống M4-05; Hùng đã đọc lại.
- **Constraint:** Giống M4-05. Nếu UC6 bị cắt (D10), bỏ các mục 4.3.x.

---

## 8. Thứ tự, lịch & cắt phạm vi

```
Phần chung (Hùng, Hoàng duyệt):  M1-01 ─> M1-02 ─┬─> M1-03 ─> M1-04 ─┐
                                                 └─> M1-05 ───────────┤
Hùng :  M2-01 (song song từ 22/10) ─> M2-02 ─> M2-03 ─> M2-04 ─> M2-05 ─┐
Hoàng:  (sau M1) M2-06 ─> M2-07 ─> M2-08 ───────────────────────────────┴─> M2-09 ─> DEMO 28/10
Sau demo: M3-01/02 ─> M3-03/04 ─> M3-05/06 ─> M4-01/02 ─> M4-03/04 ─> M4-05/06 ─> NỘP 15/11
```

| Ngày | Hùng | Hoàng |
|---|---|---|
| 21–22/10 | M1-01, M1-02, bắt đầu M2-01 | — |
| 23–24/10 | M1-03, M1-04, M1-05; xong M2-01 | — |
| 25/10 (CN) | M2-02 | **Duyệt toàn bộ PR của M1** |
| 26/10 | M2-03 | M2-06 Chơi offline |
| 27/10 | M2-04, M2-05; tối: M2-09 | M2-07 Bảng vàng, M2-08 Cài đặt; tối: M2-09 |
| **28/10** | **DEMO** | **DEMO** |
| 29/10 – 04/11 | M3-01, M3-03, M3-05 | M3-02, M3-04, M3-06 |
| 05/11 – 11/11 | M4-01, M4-03 | M4-02, M4-04 |
| 12/11 – 15/11 | M4-05, đọc báo cáo của Hoàng | M4-06, đọc báo cáo của Hùng → **nộp 15/11** |

**Mốc kiểm tra:**
- Tối 25/10: Hoàng duyệt xong M1.
- Tối 27/10: tổng duyệt demo.
- Tối Chủ nhật 01/11 và 08/11: xem tiến độ phân tích và thiết kế.

**Nếu phải cắt phạm vi để kịp demo, bỏ theo thứ tự:**
1. UC6 Cài đặt âm thanh (M2-08). Hoàng demo 2 chức năng.
2. Level 4 của câu hỏi.
3. Chức năng xóa trong UC3 (giữ chức năng xem).

Không cắt M2-04 (xử lý sự cố): đây là phần thầy dễ hỏi nhất ở chức năng online.

---

## 9. Tổng hợp nhanh

| Mã | Tiêu đề | Assignee | Label |
|---|---|---|---|
| M1-01 | Phạm vi + use case tổng quát | Hùng | ooad, chung |
| M1-02 | Lớp thực thể phân tích + thiết kế | Hùng | ooad, chung |
| M1-03 | Thiết kế CSDL | Hùng | ooad, chung |
| M1-04 | Khung code chung | Hùng | chung |
| M1-05 | Lõi câu hỏi và luật điểm | Hùng | chung |
| M2-01 | Hạ tầng Firebase | Hùng | uc-tim-tran |
| M2-02 | UC Tìm trận | Hùng | uc-tim-tran |
| M2-03 | UC Thi đấu online | Hùng | uc-thi-dau-online |
| M2-04 | UC Thi đấu online: sự cố | Hùng | uc-thi-dau-online |
| M2-05 | UC Quản lý lịch sử đấu | Hùng | uc-lich-su |
| M2-06 | UC Chơi offline | Hoàng | uc-choi-offline |
| M2-07 | UC Xem bảng vàng | Hoàng | uc-bang-vang |
| M2-08 | UC Cài đặt âm thanh | Hoàng | uc-cai-dat |
| M2-09 | Tổng duyệt demo | Cả 2 | chung |
| M3-01 | Kịch bản v1 UC1–3 | Hùng | ooad, uc-tim-tran, uc-thi-dau-online, uc-lich-su |
| M3-02 | Kịch bản v1 UC4–6 | Hoàng | ooad, uc-choi-offline, uc-bang-vang, uc-cai-dat |
| M3-03 | Lớp phân tích UC1–3 | Hùng | ooad, uc-tim-tran, uc-thi-dau-online, uc-lich-su |
| M3-04 | Lớp phân tích UC4–6 | Hoàng | ooad, uc-choi-offline, uc-bang-vang, uc-cai-dat |
| M3-05 | Kịch bản v2 + tuần tự phân tích UC1–3 | Hùng | ooad, uc-tim-tran, uc-thi-dau-online, uc-lich-su |
| M3-06 | Kịch bản v2 + tuần tự phân tích UC4–6 | Hoàng | ooad, uc-choi-offline, uc-bang-vang, uc-cai-dat |
| M4-01 | Giao diện + lớp chi tiết UC1–3 | Hùng | ooad, uc-tim-tran, uc-thi-dau-online, uc-lich-su |
| M4-02 | Giao diện + lớp chi tiết UC4–6 | Hoàng | ooad, uc-choi-offline, uc-bang-vang, uc-cai-dat |
| M4-03 | Kịch bản v3 + tuần tự chi tiết UC1–3 | Hùng | ooad, uc-tim-tran, uc-thi-dau-online, uc-lich-su |
| M4-04 | Kịch bản v3 + tuần tự chi tiết UC4–6 | Hoàng | ooad, uc-choi-offline, uc-bang-vang, uc-cai-dat |
| M4-05 | Báo cáo cá nhân Hùng | Hùng | docs |
| M4-06 | Báo cáo cá nhân Hoàng | Hoàng | docs |
