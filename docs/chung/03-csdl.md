# THIẾT KẾ CƠ SỞ DỮ LIỆU — MATH DASH 1v1

Hệ thống có 3 nơi lưu dữ liệu, mỗi nơi giữ một loại dữ liệu:

| Nơi lưu | Giữ gì | Vì sao |
|---|---|---|
| **MySQL** (server) | Tài khoản người chơi, các trận đối kháng cùng đề bài và bài làm, quan hệ bạn bè | Server lưu toàn bộ thông tin dùng chung giữa các máy |
| **Firebase Realtime Database** | Người chơi đang online và trạng thái, lời mời thách đấu, diễn biến trận đang đấu | Các máy cần thấy thay đổi của nhau ngay lập tức; chỉ server và người trong cuộc ghi |
| **SQLite** (app) | Các ván đấu với máy, bản sao người chơi đang đăng nhập | Chơi với máy phải chạy được khi không có mạng |

---

## 1. Thiết kế CSDL quan hệ MySQL và SQLite

### Bước 1: Mỗi lớp thực thể cần lưu đề xuất một bảng

| Lớp | Bảng | Nơi lưu |
|---|---|---|
| Player | tblPlayer | MySQL |
| Match | tblMatch | MySQL |
| MatchPlayer | tblMatchPlayer | MySQL |
| MathProblem | tblMathProblem | MySQL |
| Friendship | tblFriendship | MySQL |
| BotGame | tblBotGame | SQLite |
| Player (bản sao) | tblPlayer | SQLite |

Lớp **không** đề xuất bảng: Challenge.
- Lời mời chỉ có ý nghĩa trong vài giây từ lúc gửi tới lúc được trả lời.
- Sau đó hệ thống không cần tra lại lời mời nào.
- Vì vậy lời mời chỉ nằm trên Firebase (mục 2).

Câu hỏi của ván đấu với máy cũng không được lưu: ứng dụng sinh đề ngay khi bắt đầu ván, xong ván thì không dùng lại.

**Vì sao SQLite cần bảng tblPlayer bản sao:**
- BotGame thuộc về một Player, nhưng bảng tblPlayer chính nằm trên MySQL ở server.
- SQLite không đặt được khóa ngoại sang một CSDL khác, và khi mất mạng app không đọc được MySQL.
- Vì vậy mỗi lần đăng nhập thành công, app lưu một bản sao người chơi, cùng id với MySQL, vào SQLite. tblBotGame có khóa ngoại thật tới bản sao này.

### Bước 2: Đưa thuộc tính kiểu cơ bản thành cột

Bỏ qua các thuộc tính kiểu đối tượng:
- questions, players của Match.
- player của MatchPlayer.
- player, questions của BotGame.
- sender, receiver của Friendship.

Chuyển kiểu:

| Java | MySQL | SQLite |
|---|---|---|
| String | varchar(255) (toán tử: varchar(1)) | text |
| int | integer(10) | integer |
| long | bigint(20) | integer |
| Date | datetime(3), có mili giây để so thời gian làm bài | text (dạng yyyy-MM-dd HH:mm:ss) |
| boolean | tinyint(1) | integer (0 / 1) |

answerOptions : List\<Integer> luôn có đúng 4 phần tử nên tách thành 4 cột option1 … option4, giữ đúng thứ tự đã xáo trộn.

| Bảng | Cột |
|---|---|
| tblPlayer (MySQL) | id, username, passwordHash, displayName, role, createdAt, totalPoints, avgOpponentPoints, avgWinTime, status |
| tblMatch | id, status, startedAt, endedAt |
| tblMathProblem | id, operand1, operand2, operator, correctAnswer, option1, option2, option3, option4 |
| tblMatchPlayer | id, answers, correctCount, submittedAt, forfeited |
| tblBotGame | id, difficulty, correctCount, finishTime, botCorrectCount, botFinishTime, playedAt |
| tblFriendship | id, status, createdAt |
| tblPlayer (SQLite) | id, username, displayName |

Bảng tblPlayer bản sao trên SQLite **chỉ lấy các cột app cần khi không có mạng**.

### Bước 3: Quan hệ số lượng giữa các bảng

- 1 tblMatch – n tblMatchPlayer (đúng 2 dòng cho mỗi trận).
- 1 tblMatch – n tblMathProblem (đúng 10 dòng cho mỗi trận).
- 1 tblPlayer – n tblMatchPlayer (MySQL).
- 1 tblPlayer – n tblBotGame (SQLite).
- 1 tblPlayer – n tblFriendship ở vai trò người gửi, và 1 tblPlayer – n tblFriendship ở vai trò người nhận (MySQL).

Không còn quan hệ n-n: quan hệ n-n tblPlayer – tblMatch đã được tách bằng bảng tblMatchPlayer từ pha thiết kế lớp thực thể; quan hệ bạn bè giữa người chơi với người chơi đã được tách bằng bảng tblFriendship. Không có quan hệ 1-1 nào cần gộp bảng.

### Bước 4: Bổ sung khóa

**Khóa chính:** cột id của mọi bảng.
- MySQL: AUTO_INCREMENT.
- SQLite:
  - tblBotGame.id tự tăng.
  - tblPlayer.id **không tự tăng** mà lấy đúng id của người chơi trên MySQL, để bản sao và bản chính là cùng một người.

**Khóa ngoại:**
- 1 tblMatch – n tblMatchPlayer → tblMatchPlayer có khóa ngoại tblMatchId.
- 1 tblMatch – n tblMathProblem → tblMathProblem có khóa ngoại tblMatchId.
- Xóa trận thì xóa luôn 2 người chơi trong trận và 10 câu hỏi (ON DELETE CASCADE), đúng với quan hệ hợp thành ở thiết kế lớp thực thể.
- 1 tblPlayer – n tblMatchPlayer → tblMatchPlayer có khóa ngoại tblPlayerId.
- 1 tblPlayer – n tblBotGame → tblBotGame có khóa ngoại tblPlayerId.
- tblFriendship có 2 khóa ngoại cùng trỏ tới tblPlayer, nên tên khóa thêm vai trò: tblPlayerSenderId (người gửi), tblPlayerReceiverId (người nhận). Xóa người chơi thì xóa luôn các quan hệ bạn bè của người đó (ON DELETE CASCADE), vì quan hệ bạn bè không còn ý nghĩa khi thiếu một bên.

**Ràng buộc duy nhất:**
- tblPlayer.username: không có 2 tài khoản trùng tên đăng nhập.
- tblPlayer.role chỉ nhận player hoặc admin. Tài khoản admin được chèn sẵn trong schema.sql.
- tblMatchPlayer (tblMatchId, tblPlayerId): một người chơi chỉ có một dòng trong một trận.
- tblFriendship (tblPlayerSenderId, tblPlayerReceiverId): không gửi trùng lời mời. Chiều ngược lại (B đã gửi cho A thì A không gửi cho B) được FriendshipDAO kiểm tra trước khi chèn.
- tblFriendship.status chỉ nhận pending hoặc accepted.

**Thứ tự câu hỏi:** thứ tự câu trong đề là thứ tự id tăng dần của tblMathProblem. Server chèn 10 câu theo đúng thứ tự đề trong một transaction, nên không cần cột số thứ tự riêng.

### Bước 5: Loại bỏ thuộc tính gây dư thừa

**Thuộc tính dẫn xuất bị loại bỏ** (không có cột, tính khi cần):
- tblPlayer.totalPoints: tổng Match.getPoints() của người chơi qua các trận đã xong.
- tblPlayer.avgOpponentPoints, tblPlayer.avgWinTime: tính từ các trận đã xong khi lấy bảng xếp hạng.
- tblMatch.status: trận đã xong khi và chỉ khi endedAt có giá trị.
- Kết quả và điểm trận của mỗi người: suy ra từ correctCount, submittedAt, forfeited của 2 dòng tblMatchPlayer và startedAt của tblMatch → Match.getResult(playerId), Match.getPoints(playerId).
- Thời gian làm bài: submittedAt − startedAt → Match.getFinishTime(playerId).
- Các tiêu chí của bảng xếp hạng: tính từ các trận đã xong.
- Kết quả ván đấu với máy → BotGame.getResult().
- Tỉ lệ thắng / hòa / thua trong lịch sử đấu: đếm Match.getResult(playerId) trên các trận của người chơi.

**Thuộc tính bị loại vì không cần lưu lâu dài:**
- tblPlayer.status: trạng thái rỗi / bận chỉ có ý nghĩa khi người chơi đang online, nên chỉ nằm trên Firebase.
- tblMatchPlayer.answers: bài làm chỉ dùng để chấm. Server chấm ngay khi nhận và lưu kết quả vào correctCount; sau đó không chức năng nào đọc lại bài làm.

**Các thuộc tính trông giống trùng lặp nhưng được giữ lại:**
- username, displayName trong tblPlayer của SQLite so với tblPlayer của MySQL: đây là bản sao có chủ đích để chơi với máy khi mất mạng (bước 1). Bản sao được ghi đè mỗi lần đăng nhập nên luôn khớp bản chính.
- correctAnswer trong tblMathProblem: suy ra được từ operand1, operator, operand2. Tuy vậy, server cần nó ở mọi lần chấm, và nó là căn cứ đối chiếu nếu có khiếu nại kết quả, nên vẫn giữ.

### Ràng buộc NULL

- Hai cột được phép rỗng:
  - tblMatch.endedAt rỗng khi trận đang đấu.
  - tblMatchPlayer.submittedAt rỗng khi người chơi chưa nộp bài, hoặc đã bỏ trận mà chưa nộp.
- Mọi cột khác đều NOT NULL. correctCount và forfeited có mặc định 0; tblFriendship.status có mặc định pending.

### Kết quả

![Hình 4: Biểu đồ thiết kế CSDL](csdl.png)

*Hình 4: Biểu đồ thiết kế CSDL của Math Dash 1v1.*

- **Phía MySQL có 5 bảng.** tblMatch là trung tâm của phần đối kháng:
  - 10 dòng tblMathProblem là đề bài.
  - 2 dòng tblMatchPlayer nối trận với 2 dòng tblPlayer.
- tblFriendship nối 2 dòng tblPlayer với nhau, một dòng là người gửi, một dòng là người nhận.
- **Phía SQLite có 2 bảng.** tblBotGame nối tới bản sao tblPlayer bằng khóa ngoại thật.
- Hai CSDL không nối trực tiếp với nhau; chúng chỉ chung id người chơi.

Mã nguồn PlantUML: [csdl.puml](csdl.puml).

### Câu lệnh tạo bảng

**MySQL** (lưu ở server/db/schema.sql):

```sql
CREATE DATABASE IF NOT EXISTS mathdash CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mathdash;

CREATE TABLE tblPlayer (
    id           INT(10)      NOT NULL AUTO_INCREMENT,
    username     VARCHAR(255) NOT NULL,
    passwordHash VARCHAR(255) NOT NULL,          -- mật khẩu đã băm BCrypt
    displayName  VARCHAR(255) NOT NULL,
    role         VARCHAR(10)  NOT NULL DEFAULT 'player',  -- player | admin
    createdAt    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_player_username (username)
);

CREATE TABLE tblMatch (
    id        INT(10)     NOT NULL AUTO_INCREMENT,
    startedAt DATETIME(3) NOT NULL,
    endedAt   DATETIME(3) NULL,                  -- rỗng khi trận đang đấu
    PRIMARY KEY (id)
);

CREATE TABLE tblMathProblem (
    id            INT(10)    NOT NULL AUTO_INCREMENT,
    operand1      INT(10)    NOT NULL,
    operand2      INT(10)    NOT NULL,
    operator      VARCHAR(1) NOT NULL,
    correctAnswer INT(10)    NOT NULL,
    option1       INT(10)    NOT NULL,
    option2       INT(10)    NOT NULL,
    option3       INT(10)    NOT NULL,
    option4       INT(10)    NOT NULL,
    tblMatchId    INT(10)    NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (tblMatchId) REFERENCES tblMatch(id) ON DELETE CASCADE
);

CREATE TABLE tblMatchPlayer (
    id           INT(10)     NOT NULL AUTO_INCREMENT,
    correctCount INT(10)     NOT NULL DEFAULT 0,
    submittedAt  DATETIME(3) NULL,               -- rỗng khi chưa nộp bài
    forfeited    TINYINT(1)  NOT NULL DEFAULT 0,
    tblMatchId   INT(10)     NOT NULL,
    tblPlayerId  INT(10)     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_matchplayer (tblMatchId, tblPlayerId),
    FOREIGN KEY (tblMatchId)  REFERENCES tblMatch(id)  ON DELETE CASCADE,
    FOREIGN KEY (tblPlayerId) REFERENCES tblPlayer(id)
);

CREATE TABLE IF NOT EXISTS tblFriendship (
    id                  INT(10)     NOT NULL AUTO_INCREMENT,
    status              VARCHAR(10) NOT NULL DEFAULT 'pending',  -- pending | accepted
    createdAt           DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tblPlayerSenderId   INT(10)     NOT NULL,
    tblPlayerReceiverId INT(10)     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_friendship (tblPlayerSenderId, tblPlayerReceiverId),
    FOREIGN KEY (tblPlayerSenderId)   REFERENCES tblPlayer(id) ON DELETE CASCADE,
    FOREIGN KEY (tblPlayerReceiverId) REFERENCES tblPlayer(id) ON DELETE CASCADE
);
```

**SQLite** (trong DatabaseHelper.onCreate; bật khóa ngoại bằng db.setForeignKeyConstraintsEnabled(true) trong onConfigure):

```sql
CREATE TABLE tblPlayer (
    id          INTEGER PRIMARY KEY,            -- cùng id với MySQL, không tự tăng
    username    TEXT    NOT NULL,
    displayName TEXT    NOT NULL
);

CREATE TABLE tblBotGame (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    difficulty      INTEGER NOT NULL CHECK (difficulty BETWEEN 1 AND 3),
    correctCount    INTEGER NOT NULL,
    finishTime      INTEGER NOT NULL,             -- mili giây
    botCorrectCount INTEGER NOT NULL,
    botFinishTime   INTEGER NOT NULL,             -- mili giây
    playedAt        TEXT    NOT NULL,
    tblPlayerId     INTEGER NOT NULL REFERENCES tblPlayer(id)
);
```

### Các truy vấn chính

| Chức năng | Câu truy vấn | DAO |
|---|---|---|
| Đăng nhập | SELECT \* FROM tblPlayer WHERE username = ?, so mật khẩu bằng BCrypt ở Java; role quyết định mở giao diện nào | PlayerDAO (server) |
| Danh sách người chơi, quản trị | SELECT \* FROM tblPlayer WHERE role = 'player' AND displayName LIKE ? | PlayerDAO (server) |
| Sửa người chơi | UPDATE tblPlayer SET displayName = ? WHERE id = ?; đặt lại mật khẩu: UPDATE tblPlayer SET passwordHash = ? WHERE id = ? | PlayerDAO (server) |
| Xóa người chơi | DELETE FROM tblPlayer WHERE id = ? AND NOT EXISTS (SELECT 1 FROM tblMatchPlayer WHERE tblPlayerId = ?) | PlayerDAO (server) |
| Tạo trận | 1 INSERT INTO tblMatch + 10 INSERT INTO tblMathProblem + 2 INSERT INTO tblMatchPlayer trong một transaction | MatchDAO (server) |
| Nộp bài | UPDATE tblMatchPlayer SET correctCount = ?, submittedAt = ? WHERE tblMatchId = ? AND tblPlayerId = ? AND submittedAt IS NULL | MatchDAO (server) |
| Bỏ trận | UPDATE tblMatchPlayer SET forfeited = 1 WHERE tblMatchId = ? AND tblPlayerId = ? | MatchDAO (server) |
| Kết thúc trận | UPDATE tblMatch SET endedAt = ? WHERE id = ? AND endedAt IS NULL | MatchDAO (server) |
| Lấy trận kèm đề và 2 người | tblMatch JOIN tblMathProblem; tblMatch JOIN tblMatchPlayer JOIN tblPlayer, theo tblMatch.id | MatchDAO (server) |
| Tổng điểm | Lấy các trận đã xong (endedAt IS NOT NULL) mà người chơi tham gia, kèm 2 dòng tblMatchPlayer, rồi cộng Match.getPoints(playerId) ở Java | PlayerDAO (server) |
| Các trận đã xong | tblMatch JOIN tblMatchPlayer JOIN tblPlayer WHERE endedAt IS NOT NULL | MatchDAO (server) |
| Bảng xếp hạng | Lấy mọi tblPlayer có role = 'player' và mọi trận đã xong kèm 2 dòng tblMatchPlayer, tính 3 tiêu chí ở Java rồi sắp xếp | PlayerDAO (server) |
| Lịch sử đấu | tblMatch JOIN tblMatchPlayer JOIN tblPlayer WHERE endedAt IS NOT NULL AND tblMatch.id IN (SELECT tblMatchId FROM tblMatchPlayer WHERE tblPlayerId = ?) ORDER BY endedAt DESC | MatchDAO (server) |
| Bạn bè của người chơi | tblFriendship JOIN tblPlayer (2 lần, cho người gửi và người nhận) WHERE tblPlayerSenderId = ? OR tblPlayerReceiverId = ? | FriendshipDAO (server) |
| Gửi lời mời kết bạn | Kiểm tra chưa có dòng nào giữa 2 người theo cả 2 chiều, rồi INSERT INTO tblFriendship | FriendshipDAO (server) |
| Đồng ý kết bạn | UPDATE tblFriendship SET status = 'accepted' WHERE id = ? AND status = 'pending' | FriendshipDAO (server) |
| Từ chối, hủy kết bạn | DELETE FROM tblFriendship WHERE id = ? | FriendshipDAO (server) |
| Lưu ván với máy | INSERT INTO tblBotGame | BotGameDAO (app) |

Tổng điểm và bảng xếp hạng được tính ở Java thay vì bằng một câu SQL, vì luật thắng thua có nhiều nhánh: bỏ trận, đúng hết, so thời gian. Luật này đã có trong Match.getResult(), và một luật chỉ nên viết ở một nơi.

---

## 2. Thiết kế dữ liệu trên Firebase Realtime Database

Firebase là CSDL dạng cây JSON, không có bảng. Ánh xạ từ lớp thực thể theo cùng tinh thần 5 bước ở trên:
- Mỗi lớp là một node; mỗi đối tượng là một node con có khóa riêng.
- Thuộc tính kiểu cơ bản là field.
- Quan hệ hợp thành là node con nằm bên trong.

| Lớp | Node | Khóa |
|---|---|---|
| Player (đang online) | presence/{playerId} | id người chơi trên MySQL |
| Challenge | challenges/{challengeId} | push id do Firebase sinh |
| Match (đang đấu) | matches/{matchId} | id trận trên MySQL |
| MathProblem (thành phần của Match) | matches/{matchId}/questions/{i} | Thứ tự câu trong đề, 0 – 9 |
| MatchPlayer (thành phần của Match) | matches/{matchId}/players/{playerId} | id người chơi |

```
presence/{playerId}
    displayName : String
    totalPoints : double
    status      : "free" | "busy"

challenges/{challengeId}
    challengerId   : int
    challengerName : String
    challengerPoints : double
    opponentId     : int
    opponentName   : String
    status         : "pending" | "accepted" | "rejected"
    createdAt      : ServerValue.TIMESTAMP
    matchId        : int                         (có khi status = accepted)

matches/{matchId}
    status    : "playing" | "finished"
    startedAt : long
    questions/
        {i} : { operand1 : int, operand2 : int, operator : String, answerOptions : [int, int, int, int] }
    players/
        {playerId} : {
            displayName : String
            submitted   : bool
            forfeited   : bool
            choice      : "continue" | "stop"        (có sau khi trận xong)
        }
    nextMatchId : int                            (có khi cả 2 chơi tiếp)
    stoppedBy   : int                            (có khi một người dừng hoặc thoát)
```

**Ai ghi node nào:**
- presence/{playerId}:
  - App của chính người chơi ghi khi vào giao diện chính, và đăng ký onDisconnect().removeValue() để node tự mất khi app tắt hoặc mất mạng. Danh sách online chính là các node con của presence.
  - Server đổi status khi trận bắt đầu và khi 2 người dừng chơi, và cập nhật totalPoints sau mỗi trận.
- challenges:
  - App người mời tạo lời mời.
  - App người được mời ghi rejected.
  - Server ghi accepted cùng matchId khi tạo trận.
- matches: **chỉ server ghi.** App chỉ đọc để biết đề, đối thủ đã nộp bài chưa, trận đã xong chưa, có trận tiếp theo không.

**Thuộc tính có trên Firebase nhưng không lưu vào MySQL:**
- status của presence và của matches: chỉ có ý nghĩa khi đang online, đang đấu.
- submitted: cờ cho máy đối thủ hiện "Đối thủ đã nộp bài". Thời điểm nộp thật nằm ở tblMatchPlayer.submittedAt.
- choice, nextMatchId, stoppedBy: dữ liệu kỹ thuật để 2 máy cùng biết kết quả của câu hỏi "Chơi tiếp?". Chúng không có trong lớp thực thể.

**Thuộc tính cố ý không đưa lên Firebase:**
- correctAnswer của câu hỏi. Người chơi có thể đọc dữ liệu Firebase của trận mình, nên đáp án chỉ nằm ở MySQL. App lấy đáp án qua REST khi trận đã xong.

**Thuộc tính sao chép:** displayName, totalPoints được chép từ Player vào presence, challenges, matches để máy khác hiện được tên và điểm mà không phải gọi server.

**Vòng đời một trận:**
1. Lời mời được đồng ý: server tạo trận, lưu MySQL, ghi node matches/{matchId} với status = playing, đổi presence của 2 người thành busy.
2. Mỗi lần một người nộp bài: server lưu MySQL và ghi submitted = true.
3. Khi Match.isOver() đúng (cả 2 đã nộp hoặc có người bỏ trận): server ghi endedAt vào MySQL, đặt status = finished, cập nhật totalPoints trong presence của 2 người.
4. Hai người chọn chơi tiếp:
   - Cả 2 chơi tiếp: server tạo trận mới như bước 1 và ghi nextMatchId.
   - Một người dừng: server ghi stoppedBy và trả presence của 2 người về free.
5. Node của trận đã xong không còn được dùng và có thể xóa. Dữ liệu lâu dài nằm ở MySQL.

---

## 3. Dữ liệu lưu bằng SharedPreferences trên app

| Tệp | Khóa | Kiểu | Ý nghĩa |
|---|---|---|---|
| session | playerId | int | id người chơi đang đăng nhập |
| session | username | String | Tên đăng nhập |
| session | displayName | String | Tên hiển thị |
| session | role | String | Vai trò: player hoặc admin, quyết định mở giao diện chính hay giao diện quản lý người chơi |

session không phải lớp thực thể. Đây là dữ liệu kỹ thuật để app nhớ người chơi đang đăng nhập; đăng xuất thì xóa toàn bộ tệp này.
