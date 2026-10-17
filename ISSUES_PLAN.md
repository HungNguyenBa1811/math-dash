# KẾ HOẠCH ISSUE — MATH DASH 1v1 (BẢN NHÁP CHỜ DUYỆT)

> Nguồn: `PLAN.md`, `CONVENTION.md` và khung stub trong `app/`.
> Thành viên: **@HungNguyenBa1811** (Hùng – nhóm trưởng) · **@Minahn210** (Hoàng – thành viên 2).
> Sau khi duyệt, mỗi mục `[Mx-yy]` bên dưới sẽ thành 1 GitHub issue (title, label, milestone, assignee, body giữ nguyên).
> Số thứ tự trong mỗi module là thứ tự nên làm.

---

## 0. Hiện trạng code (điểm xuất phát)

Repo hiện chỉ còn **khung stub**: đủ package, class, interface và chữ ký các hàm public, thân hàm để `// TODO`. Project vẫn build được và `gradlew test` pass.

| File stub | Còn lại gì | Ai làm (issue) |
|---|---|---|
| `entity/MathProblem`, `entity/GameSession` | Field, hằng số luật điểm, chữ ký `getExpression`/`isCorrect`/`reset`/`recordCorrectAnswer`/`recordWrongAnswer` | Hùng – M1-02 |
| `dao/ProblemDAO` | Constructor nhận `Random` (để đồng bộ bằng seed), chữ ký `generateProblem(level)` | Hùng – M1-03 |
| `controller/GameController` | Interface `GameControllerListener`, chữ ký `startGame`/`submitAnswer`/`onPause`/`onResume`/`onDestroy` | Hùng – M1-04 |
| `dao/DatabaseHelper` | Constructor, `onCreate`/`onUpgrade` rỗng | Hoàng – M2-03 |
| `entity/Player`, `entity/GameRecord`, `dao/PlayerDAO`, `dao/GameRecordDAO` | Field, chữ ký hàm | Hoàng – M2-06 |
| `frm/FrmMainActivity`, `FrmGameActivity`, `FrmLeaderboardActivity` + 3 layout | Chỉ có `setContentView`; layout chỉ hiện tên màn | Hoàng – M2-01, M2-02, M2-06 |
| `res/values`, `res/drawable` | Giữ nguyên: bảng màu Retro, theme, nền nút `bg_retro_*` | Dùng chung |
| `test/GameSessionTest`, `test/ProblemDAOTest` | Class rỗng | Hùng – M1-02, M1-03 |

**Chưa có:** Firebase, quyền `INTERNET`, màn Phòng chờ / Gameplay online / Lịch sử / Cài đặt, âm thanh.
**README:** Mục Multiplayer đã đổi từ WebSocket sang Firebase, khớp với PLAN.

---

## 1. Các quyết định cần duyệt

| # | Vấn đề | Đề xuất | Lý do |
|---|---|---|---|
| D1 | Firebase Realtime Database hay Firestore | **Realtime Database** | Có sẵn `onDisconnect()` cho M1-09, listener đơn giản, độ trễ thấp |
| D2 | Luật trận 1v1 | **60 giây/trận**, cả 2 người làm chung một bộ câu hỏi, ai nhanh thì làm được nhiều câu hơn. Trả lời đúng được +10 + combo (giống offline). Trả lời sai được 0 điểm và reset combo, **không mất mạng**. Hết giờ, ai nhiều điểm hơn thì thắng, bằng điểm thì hòa | Đơn giản, dễ giải thích, dùng lại được `GameSession` |
| D3 | Đồng bộ câu hỏi | Chủ phòng tạo `seed` (kiểu long) và ghi lên phòng. Cả 2 máy tạo `new ProblemDAO(new Random(seed))` nên ra cùng một dãy câu | Không phải đẩy từng câu lên Firebase, ít lỗi đồng bộ |
| D4 | Đăng nhập | **Ẩn danh** + tên lấy từ ô nhập tên ở màn Chính. Đăng nhập email làm sau, không bắt buộc | Nhanh, không phải làm màn đăng ký |
| D5 | Lưu lịch sử đấu online | Tạo **bảng riêng `match_history`**, nằm cạnh 2 bảng `players` và `game_records` dùng cho Bảng vàng offline | Bảng đơn giản, tách biệt với dữ liệu offline |
| D6 | Mức độ của stub | Giữ chữ ký hàm public và interface, còn toàn bộ thân hàm do nhóm tự viết | Chữ ký cố định thì 2 người làm song song được mà không lệch nhau |
| D7 | Màn Cài đặt (PLAN có nút nhưng chưa có task) | Chỉ 2 công tắc **Âm thanh / Rung**, lưu bằng SharedPreferences, gộp vào M2-09 | Phạm vi nhỏ, liên quan trực tiếp tới Media |

---

## 2. Hợp đồng chung (chốt trong M1-01, sau đó KHÔNG đổi tùy tiện)

### 2.1. Cấu trúc dữ liệu trên Firebase Realtime Database

```
rooms/{roomId}
    status    : "waiting" | "playing" | "finished"
    seed      : long
    createdAt : ServerValue.TIMESTAMP
    players/
        {uid} : { name: String, score: int, online: bool, finished: bool }
```

### 2.2. Interface giữa logic (Hùng) và giao diện (Hoàng)

```java
// controller/LobbyListener.java — FrmLobbyActivity implement
public interface LobbyListener {
    void onWaiting(String myName);                          // Đã vào phòng, đang chờ đối thủ
    void onMatchFound(String myName, String opponentName);  // Đủ 2 người
    void onError(String message);
}

// controller/OnlineGameListener.java — FrmOnlineGameActivity implement
public interface OnlineGameListener {
    void onQuestion(MathProblem problem);
    void onTimerTick(long millisLeft);
    void onMyScore(int score, int streak, boolean isCorrect);
    void onOpponentScore(int score);
    void onOpponentLeft();
    void onConnectionLost();
    void onMatchEnd(MatchHistory result);
}

// controller/MatchController.java — Singleton (Pattern 4), giữ trạng thái qua 2 Activity
public void findMatch(String myName, LobbyListener listener);
public void cancelFind();
public void startGame(OnlineGameListener listener);
public void submitAnswer(int value);
public void leaveMatch();
```

### 2.3. Entity dùng chung

```java
// entity/MatchHistory.java
int id; String playedAt; String opponentName;
int myScore; int opponentScore; String result; // "WIN" | "LOSE" | "DRAW"
```

---

## 3. Quy ước chung cho mọi issue

- **Branch:** `feat/M1-03-question-level` (tạo nhanh bằng lệnh `gh issue develop <số> --checkout`).
- **PR:** mô tả ghi `Closes #<số>`. Người còn lại review rồi mới merge.
- **Definition of Done:** build pass + chạy được trên máy ảo + đạt hết mục *Requirement* + đánh dấu hết checklist convention.
- **Luyện live-coding:** mỗi người tự luyện, không tạo issue.
- **Checklist convention** (tự động thêm vào cuối body mỗi issue):
  - [ ] Không dùng Hilt/Dagger, Room, RxJava/LiveData/ViewModel, ViewBinding/DataBinding
  - [ ] Chỉ dùng `findViewById`, `SQLiteOpenHelper`, `Thread` + `runOnUiThread`
  - [ ] Mỗi hàm < 20 dòng, callback lồng tối đa 2 cấp
  - [ ] Dùng Anonymous Class, KHÔNG dùng lambda
  - [ ] Comment tiếng Việt cho các khối code quan trọng

---

## 4. MODULE 1 — Core Logic & Realtime · @HungNguyenBa1811 · label `module-1-core`

### Mô tả module (dùng làm description cho Milestone `M1 - Core & Realtime`)

**Người phụ trách:** @HungNguyenBa1811 (Hùng). Bắt đầu ngay, không phải chờ ai.
**Thời gian:** 19/10 → **01/11/2026** (2 tuần). Riêng M1-01 phải merge **trước 21/10** để Hoàng có hợp đồng mà làm.

**Mục tiêu.** Làm toàn bộ phần chạy ngầm của game, tức mọi thứ không phải giao diện:
- Luật tính điểm, sinh câu hỏi, đồng hồ đếm ngược, chấm đúng/sai cho cả 2 chế độ offline và online.
- Kết nối Firebase: đăng nhập ẩn danh, ghép trận, đồng bộ điểm giữa 2 máy, xử lý mất mạng hoặc thoát trận.

Module này được kiểm tra bằng **unit test và Logcat**, không cần màn hình hoàn chỉnh.

**Trong phạm vi:**
- Hợp đồng chung giữa 2 module: interface `LobbyListener`, `OnlineGameListener`; entity `MatchHistory`; khung `MatchController` (mục 2).
- Entity `MathProblem`, `GameSession` và luật điểm: +10 + combo×2; offline sai thì mất 1 mạng; online sai không mất mạng.
- `ProblemDAO`: sinh câu theo 4 level, kết quả không âm, cùng seed thì cùng dãy câu.
- `GameController`: ván offline, 6s mỗi câu, 3 mạng.
- `MatchController`: ván online 60s, ghép trận, đồng bộ điểm, xử lý sự cố.
- Firebase: project, Anonymous Auth, Realtime Database, Database Rules.

**Ngoài phạm vi** (KHÔNG làm trong module này):
- Layout, Activity, Adapter → Module 2.
- SQLite và việc lưu điểm sau khi kết thúc ván → Module 2 và M3-01. `GameController` / `MatchController` không gọi DAO.
- Đăng nhập email, chơi từ 3 người trở lên, chống gian lận (mỗi máy tự ghi điểm của mình lên Firebase).

**Sản phẩm bàn giao:**

| Loại | File / Hạng mục |
|---|---|
| Entity | `entity/MatchHistory.java`, `entity/MathProblem.java`, `entity/GameSession.java` |
| DAO | `dao/ProblemDAO.java`, `dao/FirebaseHelper.java`, `dao/AuthDAO.java`, `dao/RoomDAO.java` |
| Controller | `controller/GameController.java`, `controller/MatchController.java`, `controller/LobbyListener.java`, `controller/OnlineGameListener.java` |
| Unit test | `test/.../GameSessionTest.java`, `test/.../ProblemDAOTest.java` |
| Cấu hình | `app/google-services.json`; plugin + thư viện Firebase trong `build.gradle.kts` và `libs.versions.toml`; quyền `INTERNET`, `ACCESS_NETWORK_STATE` trong Manifest |
| Firebase Console | Project đã tạo, Anonymous Auth đã bật, Realtime Database + Rules (`auth != null`, `.indexOn: status`), @Minahn210 đã được thêm vào project |

**Quy tắc sửa file (tránh conflict với Hoàng):**
- Module 1 chỉ sửa các file trong bảng trên.
- **Không** sửa `frm/`, `res/layout/`, `dao/DatabaseHelper.java` và các DAO SQLite.
- `AndroidManifest.xml`, `build.gradle.kts` và `libs.versions.toml` là file dùng chung với Module 2:
  - Mỗi lần chỉ thêm đúng dòng mình cần.
  - Pull `main` trước khi sửa.
  - Merge PR có sửa các file này sớm, không ôm lâu.

**Giao tiếp với Module 2:**
- M1-01 là việc **chặn** Module 2: M2-03, M2-04, M2-07, M2-08 đều cần M1-01 đã merge vào `main`.
- Sau khi merge M1-01, chữ ký trong mục 2 bị khóa. Muốn đổi thì phải báo Hoàng và sửa cả 2 phía trong cùng một PR.

**Thứ tự làm:** M1-01 → M1-02 → M1-03 → M1-04 (lõi chạy local), song song với M1-05 → M1-06 → M1-07 → M1-08 → M1-09 (Firebase).

**Điều kiện hoàn thành module** (đủ tất cả mới được đóng milestone):
1. 9 issue M1-01 → M1-09 đã đóng, mỗi issue có PR đã merge.
2. `gradlew test` pass: có đủ các test liệt kê trong M1-02 và M1-03.
3. Logcat 1 ván offline: hết giờ thì mất mạng, hết 3 mạng thì game over.
4. Trên 2 máy ảo cùng lúc:
   - Vào chung một phòng.
   - Hiện cùng một dãy câu.
   - Điểm đối thủ cập nhật dưới 1 giây.
   - Kết quả 2 bên khớp nhau (A `WIN` thì B `LOSE`, hoặc cả 2 `DRAW`).
5. Pass 4 test case sự cố của M1-09. Trên Console không còn phòng `waiting` nào khi không có ai chờ.
6. Database Rules không ở chế độ public.

**Rủi ro đã biết:**
- Khi mất mạng đột ngột, Firebase cần tới vài chục giây mới báo `onDisconnect`. Chấp nhận điều này và ghi vào báo cáo; không cố tự viết heartbeat.
- Nếu `ProblemDAO` có chỗ random không đi qua `this.random`, 2 máy sẽ lệch câu. Test cùng seed trong M1-03 là để bắt lỗi này.

### [M1-01] Chốt hợp đồng chung (interface, entity, schema)
- **Phụ thuộc:** không có. **LÀM ĐẦU TIÊN**, vì đây là việc gỡ chặn cho Hoàng.
- **Mô tả:** Tạo trước các file "khung" ở mục 2 để Hoàng làm UI và SQLite bằng dữ liệu giả (mock) song song. Khi ghép chỉ cần thay mock bằng lời gọi thật.
- **Input:** Mục 2 của tài liệu này; các quyết định D1–D5 đã được duyệt.
- **Output:**
  - `entity/MatchHistory.java` có đủ constructor, getter/setter.
  - `controller/LobbyListener.java` và `controller/OnlineGameListener.java`.
  - `controller/MatchController.java` dạng singleton: các hàm public có thân rỗng và comment `// TODO M1-0x`.
  - Schema Firebase (mục 2.1) ghi vào `PLAN.md`.
- **Requirement:** Build pass. Hoàng đọc là biết Activity nào implement interface nào.
- **Constraint:** Sau khi merge, không đổi chữ ký hàm. Nếu bắt buộc phải đổi thì báo Hoàng và sửa cả phía UI trong cùng PR.

### [M1-02] Entity MathProblem + GameSession (luật tính điểm)
- **Phụ thuộc:** không có (làm song song với M1-01).
- **Mô tả:** Viết phần thân cho 2 entity lõi. `GameSession` giữ luật tính điểm, dùng chung cho cả offline và online.
- **Input:** Stub `entity/MathProblem.java`, `entity/GameSession.java`; luật chơi trong README (mục "Giới thiệu & Lối chơi") và D2.
- **Output:**
  - `MathProblem`: constructor, getter/setter; `getExpression()` trả chuỗi kiểu `"12 + 7"`; `isCorrect(int)`.
  - `GameSession`: constructor, getter/setter và các hàm:
    - `reset()`: đưa ván về trạng thái ban đầu.
    - `recordCorrectAnswer()`: cộng `10 + streak * 2`, tăng `streak`, `maxStreak`, `totalCorrect`; trả về số điểm vừa cộng.
    - `recordWrongAnswer()`: reset `streak`, tăng `totalWrong`, trừ 1 mạng; mạng về 0 thì `isGameOver = true`.
  - Thêm cờ hoặc hàm riêng cho chế độ online: trả lời sai **không trừ mạng** (D2).
- **Requirement:** `GameSessionTest` có các test sau và `gradlew test` pass:
  - Trạng thái ban đầu đúng.
  - Combo cộng dồn: lần đúng thứ nhất +10, lần thứ hai +12.
  - Trả lời sai thì reset combo và trừ mạng.
  - Sai 3 lần thì game over.
  - Chế độ online: trả lời sai không trừ mạng.
- **Constraint:** Entity không import `android.*`, để chạy được unit test thuần Java. Mỗi hàm < 20 dòng.

### [M1-03] Sinh câu hỏi tăng dần độ khó
- **Phụ thuộc:** M1-02 (cần entity `MathProblem`).
- **Mô tả:** PLAN mục M1.2. Sinh phép tính ngẫu nhiên, kết quả không âm, độ khó tăng 1 level sau mỗi 5 câu.
- **Input:** Stub `dao/ProblemDAO.java` (đã có constructor nhận `Random`); `questionIndex` (số câu đã ra).
- **Output:**
  - Hàm `MathProblem generateProblem(int level)`, với `level = min(questionIndex / 5 + 1, 4)`.
  - Hàm private sinh 4 đáp án: khác nhau, không âm, có đáp án đúng, xáo trộn bằng `Collections.shuffle(list, random)`.

    | Level | Phép tính | Khoảng số |
    |---|---|---|
    | 1 | `+ −` | 1–10 |
    | 2 | `+ −` | 1–30 |
    | 3 | `+ − ×` | `+ −`: 1–50, `×`: 2–9 |
    | 4 | `+ − ×` | `+ −`: 1–100, `×`: 2–12 |

  - Viết `ProblemDAOTest`:
    - Sinh 1000 câu mỗi level: không có kết quả âm, có 4 đáp án khác nhau, trong đó có đáp án đúng.
    - **Hai `ProblemDAO` cùng seed sinh ra 30 câu giống hệt nhau.**
- **Requirement:** `gradlew test` pass.
- **Constraint:** Mọi chỗ random phải đi qua `this.random`; không dùng `Math.random()` hay `new Random()` bên trong hàm. Nếu vi phạm, 2 máy cùng seed sẽ ra câu khác nhau.

### [M1-04] Timer & chấm điểm: ván offline + ván online (chạy local)
- **Phụ thuộc:** M1-01, M1-02, M1-03.
- **Mô tả:** PLAN mục M1.3. Hai chế độ dùng chung `GameSession` và `ProblemDAO`:
  - Offline: 6s mỗi câu, 3 mạng (theo README).
  - Online: đồng hồ 60s cho cả trận, không mạng (D2).

  Test bằng log, chưa cần giao diện và Firebase.
- **Input:** Stub `controller/GameController.java` (đã có interface `GameControllerListener`); `GameSession`, `ProblemDAO`.
- **Output:**
  - `GameController` (offline):
    - Constructor nhận `(GameSession, ProblemDAO, int highScore, GameControllerListener)`.
    - `startGame()`.
    - Timer 6s mỗi câu theo Pattern 5; hết giờ tính như trả lời sai.
    - `submitAnswer()`.
    - Level tăng theo số câu đã ra.
    - `onPause`/`onResume`/`onDestroy`: dừng hoặc chạy lại timer.
    - Hết mạng thì gọi `onGameOver(session, score > highScore)`.
  - `MatchController` (online, chạy local):
    - Timer 60s, tick mỗi 1s và gọi `onTimerTick`.
    - `submitAnswer()`: chấm điểm, gọi `onMyScore`, rồi sinh câu tiếp theo và gọi `onQuestion`.
    - Hết giờ thì ngừng nhận đáp án.
- **Requirement:**
  - Log 1 ván offline: hết giờ thì mất mạng, hết 3 mạng thì game over.
  - Log 1 ván online 60s: điểm và combo đúng, trả lời sai không mất mạng.
- **Constraint:**
  - `GameController` **không gọi DAO/SQLite**. Việc lưu điểm làm ở M3-01, để phần này không phải chờ phần của Hoàng.
  - Hủy timer trong `onDestroy()` / `leaveMatch()` để tránh leak.

### [M1-05] Khởi tạo Firebase cho project
- **Phụ thuộc:** không có (làm song song được với M1-01 → M1-04).
- **Mô tả:** Tạo project trên Firebase Console, thêm app Android, bật Anonymous Auth và Realtime Database.
- **Input:** applicationId `ptit.e23cnpm01.nguyenbahung.math_dash`; tài khoản Google của nhóm.
- **Output:**
  - File `app/google-services.json`.
  - Plugin `com.google.gms.google-services` trong `build.gradle.kts` gốc và `app/build.gradle.kts`.
  - Firebase BoM + `firebase-auth` + `firebase-database` khai báo trong `gradle/libs.versions.toml`.
  - Quyền `INTERNET` và `ACCESS_NETWORK_STATE` trong Manifest.
  - `dao/FirebaseHelper.java` dạng singleton (Pattern 4), trả về `FirebaseAuth` và `DatabaseReference` gốc.
  - Database Rules: chỉ user đã đăng nhập (`auth != null`) mới được đọc/ghi; có `.indexOn: "status"` cho `rooms`.
- **Requirement:** Chạy app thì Logcat in `Firebase OK`, và ghi thử một node `test` lên DB thì thấy trên Console.
- **Constraint:**
  - KHÔNG để rules public (`.read/.write: true`), vì như vậy ai cũng xóa được DB.
  - Không dùng Firestore (D1).
  - Thêm @Minahn210 vào Firebase project để Hoàng tự xem dữ liệu khi test.

### [M1-06] Đăng nhập Firebase ẩn danh
- **Phụ thuộc:** M1-05.
- **Mô tả:** PLAN mục M1.4. Mỗi máy có một UID cố định để định danh trong phòng đấu.
- **Input:** `FirebaseHelper`.
- **Output:** `dao/AuthDAO.java`:
  - Hàm `signIn(AuthCallback cb)`: nếu `currentUser` khác null thì trả UID luôn; nếu null thì gọi `signInAnonymously()`.
  - Hàm `getUid()`.
  - Interface `AuthCallback { onSuccess(String uid); onError(String message); }`.
- **Requirement:**
  - Lần đầu mở app tạo UID mới; tắt app mở lại thì **giữ nguyên UID**.
  - Mất mạng thì gọi `onError` với thông báo tiếng Việt, app không crash.
- **Constraint:** Chưa làm email (D4). Callback lồng tối đa 2 cấp.

### [M1-07] Matchmaking: tạo phòng / tìm phòng / xử lý phòng đầy
- **Phụ thuộc:** M1-06.
- **Mô tả:** PLAN mục M1.5. Người chơi bấm "Chơi ngay" thì app tìm phòng đang `waiting`. Có phòng thì join, không có thì tạo phòng mới và chờ.
- **Input:** uid, name; schema ở mục 2.1.
- **Output:**
  - `dao/RoomDAO.java` gồm:
    - `findWaitingRoom()`: query `orderByChild("status").equalTo("waiting").limitToFirst(1)`.
    - `createRoom(seed)`.
    - `joinRoom(roomId)`: dùng `runTransaction`.
  - Trong `MatchController`:
    - `findMatch(name, LobbyListener)`: gọi `onWaiting` hoặc `onMatchFound`.
    - `cancelFind()`: nếu mình là chủ phòng thì xóa phòng.
- **Requirement:**
  - 2 máy bấm gần như cùng lúc thì vào chung 1 phòng.
  - Máy thứ 3 không join được phòng đã đủ 2 người mà phải tạo phòng mới.
  - Đủ 2 người thì phòng chuyển sang `playing`.
- **Constraint:**
  - Join **bắt buộc** qua transaction: kiểm tra `status == "waiting"` và `players < 2` *bên trong* transaction.
  - Không tự join phòng của chính mình.

### [M1-08] Realtime gameplay: đồng bộ câu hỏi & điểm
- **Phụ thuộc:** M1-04, M1-07.
- **Mô tả:** PLAN mục M1.6. 2 máy chơi cùng một bộ câu (theo seed), mỗi máy đẩy điểm của mình lên phòng và lắng nghe điểm đối thủ.
- **Input:** Phòng đang `playing`, `seed`, uid của đối thủ.
- **Output:** `MatchController.startGame(OnlineGameListener)` làm các việc sau:
  - Dựng `ProblemDAO` theo seed, chạy timer của M1-04.
  - Mỗi lần trả lời thì `setValue` vào `players/{myUid}/score`.
  - `addValueEventListener` trên `players/{opponentUid}/score`, có thay đổi thì gọi `onOpponentScore`.
  - Hết giờ thì set `finished = true`. Khi cả 2 đều `finished` (hoặc đã quá 3s sau khi hết giờ) thì tính kết quả và gọi `onMatchEnd(MatchHistory)`.
- **Requirement:**
  - 2 máy thấy cùng một dãy câu.
  - Điểm đối thủ cập nhật dưới 1s khi mạng bình thường.
  - Kết quả 2 máy khớp nhau (A `WIN` thì B `LOSE`).
- **Constraint:**
  - Chỉ ghi vào node của chính mình.
  - Gọi `removeEventListener` khi kết thúc hoặc thoát trận.

### [M1-09] Xử lý sự cố: mất mạng, thoát app, bỏ trận
- **Phụ thuộc:** M1-08.
- **Mô tả:** PLAN mục M1.7. Không để trận bị treo hoặc để lại phòng "rác" trên DB.
- **Input:** Phòng hiện tại, uid.
- **Output:**
  - Khi tạo phòng: `onDisconnect().removeValue()` cho cả phòng. Có người join thì **gọi `onDisconnect().cancel()`**, nếu không phòng sẽ tự bị xóa giữa trận.
  - Khi vào trận: `onDisconnect().setValue(false)` cho `players/{uid}/online`.
  - Đang chơi mà thấy đối thủ `online == false` thì gọi `onOpponentLeft()` và tính mình thắng.
  - `leaveMatch()` (nút Back/Thoát): set `online = false`, mình bị xử thua.
  - Lắng nghe `.info/connected` của chính mình; mất kết nối thì gọi `onConnectionLost()`.
- **Requirement:** Pass cả 4 test case:
  1. Máy A đang chờ thì kill app → phòng biến mất trên Console.
  2. Đang chơi, B bấm Back → A thắng ngay.
  3. Đang chơi, B tắt Wi-Fi → A nhận "Đối thủ đã thoát". Lưu ý: khi mất mạng đột ngột, Firebase có thể mất tới vài chục giây mới phát hiện, điều này chấp nhận được.
  4. A mất mạng → A thấy thông báo, app không crash.
- **Constraint:** Không còn phòng `waiting` nào tồn tại khi không có người chờ.

---

## 5. MODULE 2 — Giao diện & Offline Data · @Minahn210 · label `module-2-ui`

### Mô tả module (dùng làm description cho Milestone `M2 - UI & Offline`)

**Người phụ trách:** @Minahn210 (Hoàng). Tuần 19/10–25/10 Hoàng bận (theo PLAN.md).
**Thời gian:** 26/10 → **08/11/2026** (2 tuần). Phần offline M2-01 → M2-06 xong trước 01/11.

**Mục tiêu.** Làm toàn bộ những gì người chơi nhìn và chạm vào, cùng với dữ liệu lưu trên máy:
- Tất cả màn hình.
- Database SQLite cho lịch sử đấu và Bảng vàng.
- Âm thanh, rung và màn Cài đặt.

Module này làm **hoàn toàn bằng dữ liệu giả (mock)**, không cần Firebase hay logic thật của Module 1. Việc nối với logic thật làm ở M3-01.

**Trong phạm vi:**
- 8 màn hình: Chính, Gameplay offline, Lịch sử, Bảng vàng, Phòng chờ, Gameplay online (kèm overlay kết quả), Cài đặt. Overlay Game Over nằm trong màn Gameplay offline.
- SQLite: `DatabaseHelper` với 3 bảng `players`, `game_records`, `match_history`, cùng 3 DAO tương ứng.
- 2 RecyclerView: Lịch sử và Bảng vàng.
- `SoundManager`: âm đúng / sai / hết giờ và rung, bật/tắt bằng SharedPreferences.

**Ngoài phạm vi** (KHÔNG làm trong module này):
- Tính điểm, đếm giờ theo luật chơi, sinh câu hỏi → đã có ở `GameController` / `MatchController` / `ProblemDAO` của Module 1. Activity chỉ hiển thị những gì listener truyền vào.
- Gọi Firebase dưới bất kỳ hình thức nào.
- Thay mock bằng lời gọi thật → M3-01.

**Sản phẩm bàn giao:**

| Loại | File / Hạng mục |
|---|---|
| Activity | `frm/FrmMainActivity`, `FrmGameActivity`, `FrmHistoryActivity`, `FrmLeaderboardActivity`, `FrmLobbyActivity`, `FrmOnlineGameActivity`, `FrmSettingsActivity` |
| Adapter | `frm/HistoryAdapter.java`, `frm/LeaderboardAdapter.java` |
| Layout | `activity_frm_main`, `activity_frm_game`, `activity_frm_history`, `item_history`, `activity_frm_leaderboard`, `item_leaderboard`, `activity_frm_lobby`, `activity_frm_online_game`, `activity_frm_settings` |
| Entity | `entity/Player.java`, `entity/GameRecord.java` |
| DAO SQLite | `dao/DatabaseHelper.java`, `dao/MatchHistoryDAO.java`, `dao/PlayerDAO.java`, `dao/GameRecordDAO.java` |
| Media | `util/SoundManager.java`; `res/raw/correct.mp3`, `wrong.mp3`, `timeout.mp3` |
| Cấu hình | Khai báo 4 Activity mới và quyền `VIBRATE` trong Manifest; thư viện `recyclerview` trong `libs.versions.toml` và `app/build.gradle.kts` |

**Quy tắc mock (bắt buộc, để M3-01 xóa nhanh):**
- Mỗi Activity gom toàn bộ dữ liệu giả vào **đúng một hàm private `loadMockData()`**, gọi ở cuối `onCreate`.
- Dòng đầu hàm có comment `// MOCK - xóa ở M3-01`.
- Dữ liệu giả phải đi qua chính các hàm của listener (ví dụ gọi `onScoreUpdated(120, 3, 200)`), không gán thẳng vào View.

**Quy tắc sửa file (tránh conflict với Hùng):**
- Module 2 chỉ sửa các file trong bảng trên.
- **Không** sửa `controller/`, `entity/MathProblem`, `entity/GameSession`, `entity/MatchHistory`, `dao/ProblemDAO`, các file Firebase.
- Nếu thấy interface ở mục 2.2 thiếu hàm hoặc sai, mở comment trong issue M1-01 để Hùng sửa; không tự sửa.
- `AndroidManifest.xml`, `build.gradle.kts` và `libs.versions.toml` là file dùng chung: chỉ thêm đúng dòng cần thiết, pull `main` trước khi sửa.

**Phong cách:** Dùng bảng màu trong `res/values/colors.xml` và nền nút `drawable/bg_retro_*.xml`. Không thêm màu mã hex trực tiếp vào layout.

**Phụ thuộc vào Module 1:**
- M2-01, M2-02 làm được ngay.
- M2-03, M2-04, M2-07, M2-08 cần M1-01 (interface + `MatchHistory`) đã merge vào `main`.

**Thứ tự làm:** phần offline M2-01 → M2-06, rồi phần online M2-07 → M2-08, cuối cùng là Media M2-09.

**Điều kiện hoàn thành module** (đủ tất cả mới được đóng milestone):
1. 9 issue M2-01 → M2-09 đã đóng, mỗi issue có PR đã merge.
2. Từ màn Chính mở được cả 7 màn còn lại; bấm Back ở mọi màn không crash.
3. Mỗi màn hiển thị đúng dữ liệu mock, và xoay màn hình không crash.
4. Logcat CRUD `match_history` đúng như Requirement của M2-03. Bảng vàng sắp đúng thứ tự điểm.
5. Tắt Âm thanh hoặc Rung trong Cài đặt thì có hiệu lực ngay, và vẫn giữ sau khi tắt app mở lại.
6. Tìm `"-> "` trong `app/src/main/java` không ra kết quả nào (không dùng lambda).
7. Đã chạy thử trên 1 máy ảo Android 8.0 (API 26) trở lên.

**Rủi ro đã biết:**
- Máy ảo không rung được. Kiểm tra rung bằng log, hoặc trên điện thoại thật nếu có.
- `VibrationEffect` chỉ có từ API 26, trong khi minSdk là 24. Phải rẽ nhánh theo `Build.VERSION.SDK_INT` như ghi trong M2-09.

### [M2-01] Giao diện màn hình Chính
- **Phụ thuộc:** không có.
- **Mô tả:** PLAN mục M2.1. Màn menu đầu tiên khi mở app.
- **Input:** Stub `FrmMainActivity.java` và `activity_frm_main.xml` (hiện chỉ có tên màn); bảng màu trong `colors.xml` và các nền nút `bg_retro_*`.
- **Output:**
  - Tiêu đề game, ô nhập tên `etUsername`.
  - 5 nút:
    - `btnPlayOnline` "CHƠI NGAY" → `FrmLobbyActivity`, truyền tên qua Intent.
    - `btnPlayOffline` "CHƠI OFFLINE" → `FrmGameActivity`, truyền tên qua Intent.
    - `btnHistory` "LỊCH SỬ" → `FrmHistoryActivity`.
    - `btnLeaderboard` "BẢNG VÀNG" → `FrmLeaderboardActivity`.
    - `btnSettings` "CÀI ĐẶT" → `FrmSettingsActivity`.
- **Requirement:** Mỗi nút mở đúng màn. Màn nào chưa làm thì hiện `Toast` "Đang phát triển".
- **Constraint:** Dùng `findViewById` và `new View.OnClickListener()`, không dùng lambda.

### [M2-02] Giao diện màn Gameplay offline
- **Phụ thuộc:** không có (interface `GameControllerListener` đã có sẵn trong stub).
- **Mô tả:** PLAN mục M2.3. Màn chơi offline theo luật trong README: 6s mỗi câu, 3 mạng, combo.
- **Input:** Stub `FrmGameActivity` (đã implement `GameControllerListener`, các hàm để trống); `activity_frm_game.xml` (hiện chỉ có tên màn); mục "Giao diện & Phong cách Retro" trong README.
- **Output:**
  - Layout gồm:
    - Tên người chơi, BEST, điểm, combo `xN`, mạng `♥ ♥ ♥`.
    - ProgressBar thời gian kèm số giây còn lại.
    - Phép tính chữ to.
    - Lưới 2x2 nút đáp án: cao ≥ 72dp, chữ monospace 26sp.
    - Dòng phản hồi `+12 PTS!` / `MISS!`.
    - Overlay Game Over: điểm cuối, kỷ lục, nút CHƠI LẠI / VỀ MENU.
  - Viết thân 6 hàm của listener để cập nhật đúng View.
  - Mock trong `loadMockData()`: gọi lần lượt cả 6 hàm listener với dữ liệu giả; thêm 1 nút tạm hoặc `Handler` để gọi `onGameOver` sau 3s, nhằm kiểm tra overlay.
- **Requirement:** Mỗi hàm listener cập nhật đúng View. Overlay hiện/ẩn đúng lúc.
- **Constraint:** Không tính điểm trong Activity (logic nằm ở `GameController`). Dùng `findViewById` và anonymous class.

### [M2-03] SQLite: DatabaseHelper + bảng `match_history` + CRUD
- **Phụ thuộc:** M1-01 (cần entity `MatchHistory`).
- **Mô tả:** PLAN mục M2.5. Tạo database của app và phần lưu kết quả trận online (D5).
- **Input:** Stub `dao/DatabaseHelper.java`; Pattern 2.
- **Output:**
  - `DatabaseHelper.onCreate` tạo cả 3 bảng:
    - `players (id, username UNIQUE, high_score, created_at)`
    - `game_records (id, player_id, score, max_streak, total_correct, total_wrong, played_at)`
    - `match_history`:
      ```sql
      CREATE TABLE match_history (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          opponent_name TEXT,
          my_score INTEGER,
          opponent_score INTEGER,
          result TEXT
      )
      ```
  - `onUpgrade`: DROP rồi tạo lại, giống Pattern 2.
  - `dao/MatchHistoryDAO.java` với 4 hàm:
    - `insert(MatchHistory)`
    - `getAll()`: mới nhất xếp trước
    - `update(MatchHistory)`
    - `delete(int id)`
- **Requirement:** Test bằng log: insert 3 bản ghi → `getAll()` trả về 3 → update 1 → delete 1 → còn 2, đúng dữ liệu.
- **Constraint:**
  - Dùng `ContentValues` và `rawQuery`/`query`, không dùng Room.
  - `update`/`delete` dùng placeholder `"id = ?"`, không nối chuỗi.

### [M2-04] Màn Lịch sử + HistoryAdapter
- **Phụ thuộc:** M1-01 (cần entity `MatchHistory`).
- **Mô tả:** PLAN mục M2.4. Hiển thị danh sách các trận online đã đấu.
- **Input:** Pattern 1 trong CONVENTION; entity `MatchHistory`.
- **Output:**
  - Thêm `androidx.recyclerview` vào `libs.versions.toml` và `app/build.gradle.kts`.
  - `FrmHistoryActivity` + layout `activity_frm_history.xml` (RecyclerView, nút Back).
  - Layout `item_history.xml`: thời gian, tên đối thủ, tỉ số `X - Y`, kết quả (WIN xanh `arcade_green`, LOSE đỏ `arcade_red`, DRAW vàng).
  - `frm/HistoryAdapter.java` theo Pattern 1, dùng `List<MatchHistory>`.
  - Mock trong `loadMockData()`: tự viết tay một `List<MatchHistory>` 5 phần tử, có đủ 3 kết quả WIN / LOSE / DRAW.
- **Requirement:** Danh sách cuộn được. Danh sách rỗng thì hiện "Chưa có trận nào".
- **Constraint:** Theo đúng Pattern 1: không dùng DiffUtil, ListAdapter hay ViewBinding.

### [M2-05] Đổ dữ liệu SQLite lên màn Lịch sử
- **Phụ thuộc:** M2-03, M2-04.
- **Mô tả:** PLAN mục M2.6. Thay mock bằng dữ liệu thật đọc từ SQLite.
- **Input:** `MatchHistoryDAO`, `HistoryAdapter`.
- **Output:**
  - `FrmHistoryActivity` gọi `getAll()` trong `Thread`, rồi dùng `runOnUiThread` để gán cho adapter (Pattern 3).
  - Nhấn giữ 1 dòng thì hiện `AlertDialog` "Xóa trận này?"; đồng ý thì gọi `delete`, xóa khỏi list và `notifyItemRemoved`.
- **Requirement:**
  - Insert thêm bản ghi rồi mở lại màn thì thấy bản ghi mới.
  - Xoay màn hình không crash.
- **Constraint:** Không truy vấn SQLite trên main thread.

### [M2-06] Bảng vàng offline: Player, GameRecord, DAO + màn hình
- **Phụ thuộc:** M2-03 (bảng đã được tạo), M2-04 (dùng lại cách làm RecyclerView).
- **Mô tả:** PLAN mục M2.4 (phần Bảng xếp hạng). Lưu và hiển thị top điểm của chế độ offline.
- **Input:** Stub `entity/Player`, `entity/GameRecord`, `dao/PlayerDAO`, `dao/GameRecordDAO`, `frm/FrmLeaderboardActivity`.
- **Output:**
  - Constructor, getter/setter cho `Player` và `GameRecord`.
  - `PlayerDAO`:
    - `findOrCreateByUsername()`: trim tên, viết hoa; tên rỗng thì dùng `PLAYER_1`.
    - `updateHighScoreIfHigher()`.
  - `GameRecordDAO`:
    - `insertRecord()`.
    - `getTopRecords(limit)`: JOIN với `players`, sắp `score DESC`.
  - `FrmLeaderboardActivity`: RecyclerView top 20, mỗi dòng gồm hạng, tên, điểm, combo cao nhất, % chính xác.
  - `frm/LeaderboardAdapter.java` (theo Pattern 1) + layout `item_leaderboard.xml`.
- **Requirement:** Insert 3 ván giả thì Bảng vàng xếp đúng thứ tự. Chưa có dữ liệu thì hiện "Chưa có kỷ lục".
- **Constraint:**
  - Truy vấn chạy trong `Thread` (Pattern 3).
  - Dùng placeholder `?` thay vì nối chuỗi tên người chơi, nếu không tên có dấu `'` sẽ làm lỗi câu SQL.

### [M2-07] Giao diện màn Phòng chờ
- **Phụ thuộc:** M1-01.
- **Mô tả:** PLAN mục M2.2. Màn hiển thị trong lúc ghép trận.
- **Input:** `LobbyListener` (mục 2.2), tên người chơi lấy từ Intent.
- **Output:**
  - `FrmLobbyActivity implements LobbyListener` + layout `activity_frm_lobby.xml`.
  - Màn hiển thị: tên mình; tên đối thủ (hoặc dòng "Đang tìm đối thủ..." kèm ProgressBar); dòng trạng thái; nút HỦY.
  - Đăng ký Activity trong Manifest.
  - Mock trong `loadMockData()`: gọi `onWaiting(name)`, rồi `new Handler().postDelayed(new Runnable(){...}, 2000)` để gọi `onMatchFound(name, "HOANG")`.
  - Khi `onMatchFound` được gọi: hiện tên đối thủ, sau 1.5s tự chuyển sang `FrmOnlineGameActivity`.
- **Requirement:** Hiển thị đúng 2 trạng thái (đang chờ / đã có đối thủ). Bấm HỦY thì quay về màn Chính.
- **Constraint:** Activity **không gọi Firebase trực tiếp**, chỉ được gọi `MatchController` (phần này ghép ở M3-01).

### [M2-08] Giao diện màn Gameplay online
- **Phụ thuộc:** M1-01, M2-02 (dùng lại layout màn offline).
- **Mô tả:** PLAN mục M2.3. Màn thi đấu 1v1 hiển thị điểm 2 bên.
- **Input:** `OnlineGameListener` (mục 2.2); layout `activity_frm_game.xml` làm xong ở M2-02.
- **Output:**
  - `FrmOnlineGameActivity implements OnlineGameListener` + layout `activity_frm_online_game.xml`.
  - Màn gồm: đồng hồ 60s, phép tính, lưới 2x2 nút đáp án, điểm **MÌNH vs ĐỐI THỦ**.
  - Overlay kết quả THẮNG / THUA / HÒA, có tỉ số và nút "Về menu".
  - Mock trong `loadMockData()`:
    - Mỗi lần bấm đáp án gọi `onQuestion(...)` với một câu hỏi giả.
    - Một `Handler` cứ 3s gọi `onOpponentScore` với điểm tăng dần.
    - Một `CountDownTimer` 60s gọi `onTimerTick`; hết giờ gọi `onMatchEnd` với dữ liệu giả.
- **Requirement:**
  - Mỗi hàm trong `OnlineGameListener` cập nhật đúng View.
  - Bấm Back thì hiện `AlertDialog` "Thoát sẽ bị xử thua?".
- **Constraint:** **Không tính điểm trong Activity** (logic tính điểm nằm ở `MatchController`). Mỗi hàm < 20 dòng.

### [M2-09] Media: âm thanh, rung + màn Cài đặt
- **Phụ thuộc:** M2-01, M2-02.
- **Mô tả:** PLAN mục M2.7. Phản hồi âm thanh/rung khi trả lời; người chơi tắt/bật được (D7).
- **Input:** Pattern 4 (SoundManager).
- **Output:**
  - `res/raw/correct.mp3`, `wrong.mp3`, `timeout.mp3`: lấy từ nguồn miễn phí và ghi lại nguồn để đưa vào báo cáo.
  - `util/SoundManager.java` dạng singleton, gồm `playSound(context, resId)` và `vibrate(context, ms)`.
  - Quyền `VIBRATE` trong Manifest.
  - `FrmSettingsActivity` + layout `activity_frm_settings.xml`: 2 Switch (Âm thanh / Rung) lưu vào SharedPreferences `"settings"`.
  - `SoundManager` kiểm tra cờ trong SharedPreferences trước khi phát âm hoặc rung.
  - Gắn ngay vào màn offline (`FrmGameActivity.onFeedback`, khi hết giờ) để test được luôn.
- **Requirement:** Bật/tắt có hiệu lực ngay và vẫn giữ sau khi tắt app mở lại.
- **Constraint:**
  - Gọi `release()` MediaPlayer cũ trước khi tạo cái mới.
  - minSdk là 24 nhưng `VibrationEffect` cần API 26, nên phải kiểm tra `Build.VERSION.SDK_INT`.
  - Mỗi file âm thanh < 100KB.

---

## 6. MODULE 3 — Ghép nối, Kiểm thử & Báo cáo · label `module-3-integration`

### Mô tả module (dùng làm description cho Milestone `M3 - Tich hop`)

**Người phụ trách:** Cả 2 (@HungNguyenBa1811, @Minahn210).
**Thời điểm:**
- M3-03 (báo cáo) mở ngay từ đầu dự án.
- M3-01 bắt đầu khi milestone M1 và M2 đều đã đóng.
- M3-02 bắt đầu sau M3-01.

**Thời gian:** 09/11 → **15/11/2026** (1 tuần). Báo cáo M3-03 viết dần từ 19/10.

**Mục tiêu.** Biến 2 nửa đã làm riêng thành một app chạy thật trên 2 máy, chứng minh bằng kết quả kiểm thử, và hoàn thành báo cáo nộp thầy.

**Trong phạm vi:**
- Xóa toàn bộ `loadMockData()`.
- Nối Activity với `GameController` / `MatchController`.
- Lưu kết quả vào SQLite khi ván kết thúc; gắn âm thanh vào các sự kiện đúng / sai / hết giờ.
- Kiểm thử trên 2 máy và đo độ trễ.
- Báo cáo chung trên một file online.

**Ngoài phạm vi:**
- **Không thêm tính năng mới.** Phát hiện lỗi thì mở issue mới với label `bug` và gắn vào milestone này.
- Ý tưởng tính năng mới thì ghi vào issue riêng, label `enhancement`, không gắn milestone.

**Sản phẩm bàn giao:**

| Loại | File / Hạng mục |
|---|---|
| App | APK debug (`app/build/outputs/apk/debug/app-debug.apk`) chạy được luồng đầy đủ trên 2 máy |
| Kiểm thử | `docs/TEST_REPORT.md`: bảng test case + độ trễ trung bình của 10 lần đo |
| Báo cáo | Link file online dùng chung (dán trong issue M3-03) và bản cuối `docs/BaoCao.docx` |

**Cách làm việc:**
- M3-01: ngồi cùng nhau trên một nhánh `feat/M3-01-integration`. Hùng nối phần controller, Hoàng nối phần Activity. Commit nhỏ, mỗi commit một màn.
- M3-03: ai xong task nào ở M1/M2 thì viết luôn mục báo cáo tương ứng (bảng phân công trong M3-03), không dồn đến cuối.

**Điều kiện hoàn thành module** (đủ tất cả mới coi là xong dự án):
1. 3 issue M3-01 → M3-03 và mọi issue `bug` trong milestone đều đã đóng.
2. Trên 2 máy chạy được luồng: Chính → Phòng chờ → Chơi online → Kết quả → Lịch sử có dòng mới. Thêm luồng: Chơi offline → Game Over → Bảng vàng có dòng mới.
3. Tìm `MOCK` trong `app/src` không ra kết quả nào.
4. `docs/TEST_REPORT.md` có đủ test case của M1-07, M1-09 và luồng chính; mỗi case đều Pass hoặc có issue `bug` đã đóng.
5. Báo cáo: mọi mục trong checklist M3-03 đã tick; mỗi người đã đọc lại phần của người kia; `docs/BaoCao.docx` đã có trong `main`.

### [M3-01] Ghép giao diện với logic · @HungNguyenBa1811 @Minahn210
- **Phụ thuộc:** Toàn bộ M1 và M2-01 → M2-09.
- **Mô tả:** PLAN mục M3.1. Xóa mock, nối các Activity với `GameController` và `MatchController`.
- **Input:** Các Activity đang dùng mock; `GameController`, `MatchController` đã hoàn chỉnh.
- **Output:**
  - `FrmLobbyActivity` gọi `signIn` rồi `findMatch`; nút HỦY gọi `cancelFind`.
  - `FrmOnlineGameActivity` gọi `startGame`, `submitAnswer`, `leaveMatch`.
  - Khi `onMatchEnd`: lưu `MatchHistoryDAO.insert` trong `Thread`.
  - Offline: `FrmGameActivity` lấy `Player` qua `PlayerDAO.findOrCreateByUsername` rồi tạo `GameController` thật. Khi `onGameOver`: gọi `GameRecordDAO.insertRecord` và `PlayerDAO.updateHighScoreIfHigher` trong `Thread`.
  - Gọi `SoundManager` khi trả lời đúng/sai và khi hết giờ.
- **Requirement:** Chạy được luồng đầy đủ trên 2 máy: Chính → Phòng chờ → Chơi → Kết quả → Lịch sử có thêm dòng mới.
- **Constraint:** Ngồi làm cùng nhau trên một nhánh `feat/M3-01-integration` để tránh conflict. Không còn đoạn mock nào trong code.

### [M3-02] Kiểm thử chéo trên 2 máy + đo độ trễ · @HungNguyenBa1811 @Minahn210
- **Phụ thuộc:** M3-01.
- **Mô tả:** PLAN mục M3.2. Test trên 2 máy ảo (hoặc 2 điện thoại thật).
- **Input:** Các test case trong M1-07, M1-09 và luồng chính của M3-01.
- **Output:**
  - `docs/TEST_REPORT.md`: bảng test case gồm Bước / Kỳ vọng / Thực tế / Pass-Fail.
  - Độ trễ: log `System.currentTimeMillis()` lúc máy A gửi điểm và lúc máy B nhận được. 2 máy ảo trên cùng 1 PC dùng chung đồng hồ nên trừ trực tiếp được. Đo 10 lần, ghi trung bình.
  - Mỗi bug phát hiện được tạo thành một issue mới, có label `bug`.
- **Requirement:** Pass toàn bộ test case, hoặc các case Fail đều đã có issue bug tương ứng.
- **Constraint:** Ghi rõ môi trường test (máy ảo hay máy thật, Android version, loại mạng).

### [M3-03] Báo cáo chung (1 file, cả 2 cùng viết) · @HungNguyenBa1811 @Minahn210 · label `docs`
- **Phụ thuộc:** Mở issue và tạo file ngay từ đầu. Mỗi người viết phần của mình **ngay khi xong task liên quan**, không dồn đến cuối. Ảnh giao diện và kết quả test chốt sau M3-01 và M3-02.
- **Mô tả:** PLAN mục M2.8 + M3.3, gộp thành một báo cáo duy nhất. Cả 2 cùng soạn trên **một file online dùng chung** (Google Docs hoặc Word Online), không mỗi người một file rồi ghép.
- **Input:** Code và sơ đồ thật trong repo; schema mục 2.1; kết quả M3-02.
- **Output:**
  - **Một file báo cáo dùng chung.** Dán link vào phần mô tả issue, cả 2 đều có quyền sửa.
  - Bản cuối xuất `.docx`, lưu vào `docs/BaoCao.docx`.
  - Phân công theo mục, ghi thành checklist trong issue:

    | Mục | Nội dung | Người viết |
    |---|---|---|
    | 1 | Giới thiệu đề tài, luật chơi | Hùng |
    | 2 | Phân tích yêu cầu: sơ đồ Use case (Đăng nhập ẩn danh, Chơi online, Chơi offline, Xem/Xóa lịch sử, Xem bảng vàng, Cài đặt) | Hùng |
    | 3.1 | Kiến trúc: MVC, các package `entity` / `dao` / `controller` / `frm` | Hùng |
    | 3.2 | CSDL SQLite: ERD 3 bảng `players`, `game_records`, `match_history` | Hoàng |
    | 3.3 | Firebase: schema `rooms`; Activity diagram cho Matchmaking, Realtime gameplay, Xử lý mất kết nối | Hùng |
    | 3.4 | Thiết kế giao diện: ảnh từng màn (Chính, Phòng chờ, Gameplay online, Kết quả, Gameplay offline, Lịch sử, Bảng vàng, Cài đặt) | Hoàng |
    | 3.5 | Media: âm thanh, rung, nguồn file âm thanh | Hoàng |
    | 4 | Kiểm thử: bảng test case, độ trễ đo được (từ M3-02) | Cả 2 |
    | 5 | **Hướng dẫn sử dụng sản phẩm**: các bước đánh số, gồm cài APK → nhập tên → chơi online → chơi offline → xem/xóa lịch sử → bảng vàng → cài đặt | Hoàng |
    | 6 | Kết luận, bảng phân công công việc | Cả 2 |

- **Requirement:**
  - Mọi mục trong checklist đều đã được tick.
  - Mỗi ảnh và sơ đồ có chú thích "Hình x: ...", kèm 3–5 dòng giải thích.
  - Hướng dẫn sử dụng đủ để người chưa từng dùng app tự chơi được.
  - Trước khi nộp, mỗi người đọc lại phần của người kia.
- **Constraint:**
  - Tên trạng thái và tên bước trong sơ đồ phải trùng với code (`waiting`/`playing`/`finished`, `onDisconnect`...).
  - Ảnh chụp từ bản mock chỉ dùng tạm; trước khi nộp phải thay bằng ảnh bản thật.
  - Không sửa file `.docx` trong repo song song (file nhị phân không merge được). Chỉ soạn trên file online, xong mới xuất ra repo một lần.

---

## 7. Thứ tự & phụ thuộc

```
Hùng :  M1-01 ──────────────────┐
        M1-02 ─> M1-03 ─────────┴─> M1-04 ─┐
        M1-05 ─> M1-06 ─> M1-07 ───────────┴─> M1-08 ─> M1-09 ─┐
                                                               ├─> M3-01 ─> M3-02 ─> M3-03
Hoàng:  M2-01 + M2-02 ─> M2-09 ────────────────────────────────┤
        M2-02 ─> M2-08 (sau M1-01) ────────────────────────────┤
        M2-03 (sau M1-01) ─┬─> M2-05 ──────────────────────────┤
        M2-04 (sau M1-01) ─┴─> M2-06 ──────────────────────────┤
        M2-07 (sau M1-01) ─────────────────────────────────────┘

Cả 2:   M3-03 Báo cáo chung: mở từ đầu, mỗi người viết dần sau mỗi task, chốt sau M3-02
```

### Lịch theo tuần (tổng 4 tuần, 19/10 → 15/11/2026)

| Tuần | Hùng | Hoàng | Báo cáo (M3-03) |
|---|---|---|---|
| **1** · 19/10 – 25/10 | M1-01 (**merge trước 21/10**), M1-02, M1-03, M1-04, M1-05, M1-06 | Bận. Nếu có thời gian: đọc `CONVENTION.md` + mục 2, làm thử M2-01 | Hùng tạo file online, dựng khung mục lục, dán link vào issue |
| **2** · 26/10 – 01/11 | M1-07, M1-08, M1-09 → **đóng milestone M1 ngày 01/11** | M2-01 → M2-06 (phần offline) | Hùng: mục 1, 2, 3.1 · Hoàng: mục 3.2 |
| **3** · 02/11 – 08/11 | Review PR của Hoàng; hỗ trợ khi Hoàng bị kẹt | M2-07, M2-08, M2-09 → **đóng milestone M2 ngày 08/11** | Hùng: mục 3.3 · Hoàng: mục 3.4 (ảnh mock), 3.5 |
| **4** · 09/11 – 15/11 | M3-01 (09–11/11) → M3-02 (12–13/11) → chốt báo cáo (14–15/11) | Cùng làm với Hùng | Cả 2: mục 4, 6; Hoàng: mục 5, thay ảnh bản thật; đọc chéo → **nộp 15/11** |

**Mốc kiểm tra:** Tối Chủ nhật hằng tuần (25/10, 01/11, 08/11), cả 2 xem board GitHub. Issue nào trễ hơn lịch trên 2 ngày thì quyết định ngay: dồn việc, cắt bớt phạm vi, hoặc chuyển người làm.

**Nếu phải cắt phạm vi để kịp, bỏ theo thứ tự:**
1. Màn Cài đặt (giữ âm thanh, luôn bật).
2. Level 4 của câu hỏi.
3. Bảng vàng offline (giữ Lịch sử online).

Không cắt M1-09 (xử lý sự cố) và M3-02 (kiểm thử): đây là 2 phần thầy dễ hỏi nhất.

---

## 8. Tổng hợp nhanh

| Mã | Tiêu đề | Assignee | Label |
|---|---|---|---|
| M1-01 | Chốt hợp đồng chung | Hùng | module-1-core |
| M1-02 | Entity MathProblem + GameSession | Hùng | module-1-core |
| M1-03 | Câu hỏi tăng độ khó | Hùng | module-1-core |
| M1-04 | Timer & chấm điểm (offline + online) | Hùng | module-1-core |
| M1-05 | Khởi tạo Firebase | Hùng | module-1-core |
| M1-06 | Đăng nhập ẩn danh | Hùng | module-1-core |
| M1-07 | Matchmaking | Hùng | module-1-core |
| M1-08 | Realtime gameplay | Hùng | module-1-core |
| M1-09 | Xử lý sự cố | Hùng | module-1-core |
| M2-01 | UI màn Chính | Hoàng | module-2-ui |
| M2-02 | UI Gameplay offline | Hoàng | module-2-ui |
| M2-03 | DatabaseHelper + match_history CRUD | Hoàng | module-2-ui |
| M2-04 | Màn Lịch sử + HistoryAdapter | Hoàng | module-2-ui |
| M2-05 | Đổ SQLite lên Lịch sử | Hoàng | module-2-ui |
| M2-06 | Bảng vàng offline | Hoàng | module-2-ui |
| M2-07 | UI Phòng chờ | Hoàng | module-2-ui |
| M2-08 | UI Gameplay online | Hoàng | module-2-ui |
| M2-09 | Âm thanh, rung, Cài đặt | Hoàng | module-2-ui |
| M3-01 | Ghép nối | Cả 2 | module-3-integration |
| M3-02 | Test chéo + độ trễ | Cả 2 | module-3-integration |
| M3-03 | Báo cáo chung | Cả 2 | docs |
