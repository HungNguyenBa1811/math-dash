# Math Dash 1v1 - Game giải toán nhanh (Android Java + Spring Boot)

> Bài tập lớn môn Phân tích thiết kế hệ thống, **đề 9 "Game giải toán nhanh"**. Hệ thống gồm một server và nhiều client: người chơi đăng nhập, thấy danh sách người chơi online, thách đấu nhau trên cùng một đề 10 câu trắc nghiệm do server sinh và chấm, hoặc chơi với máy; xem bảng xếp hạng toàn hệ thống. Giao diện phong cách **Retro Arcade**, app theo **MVC 3 tầng entity – dao – view**.

---

## Mục lục
1. [Luật chơi](#luật-chơi)
2. [Chức năng và phân công](#chức-năng-và-phân-công)
3. [Kiến trúc](#kiến-trúc)
4. [Cấu trúc thư mục](#cấu-trúc-thư-mục)
5. [Cài đặt và chạy](#cài-đặt-và-chạy)

---

## Luật chơi

- **Đề bài:** 10 câu cộng, trừ, nhân, mỗi câu 4 phương án. Tối đa 60 giây; hết giờ thì tự nộp, câu chưa chọn tính là sai.
- **Kết quả trận** (server xác định, đồng hồ server đo thời gian làm bài):
  1. Người bỏ trận (nhấn Thoát hoặc mất kết nối) thua.
  2. Chỉ một người đúng hết 10 câu thì người đó thắng.
  3. Cả 2 đều có câu sai thì hòa.
  4. Cả 2 đúng hết thì ai nộp nhanh hơn thắng; bằng nhau thì hòa.
- **Điểm trận:** thắng 1, hòa 0,5, thua 0. Tổng điểm là tổng điểm trận qua mọi trận đã xong.
- **Chơi tiếp:** sau mỗi trận, cả 2 chọn Chơi tiếp thì vào trận mới với đề mới; một người Dừng thì cả 2 về màn chính.
- **Bảng xếp hạng:** tổng điểm ↓, trung bình tổng điểm các đối thủ đã gặp ↓, trung bình thời gian làm bài trong các trận thắng ↑.
- **Chơi với máy:** cùng luật; máy có 3 độ khó (Dễ / Trung bình / Khó) quyết định tỉ lệ đúng và tốc độ. Chạy offline, không tính điểm.

---

## Chức năng và phân công

| UC | Chức năng | Tác nhân | Người làm |
|---|---|---|---|
| UC01, UC02 | Đăng ký, Đăng nhập | Người chơi, Quản trị viên | Code chung; cả 2 trình bày |
| UC03 | Chơi online: danh sách online, thách đấu, thi đấu, chơi tiếp | Người chơi | Hùng |
| UC04 | Xem bảng xếp hạng | Người chơi | Hùng |
| UC05 | Chơi với máy | Người chơi | Hoàng |
| UC06 | Quản lý thông tin người chơi | Quản trị viên | Hoàng |

Phạm vi và luồng chi tiết: [`docs/chung/01-pham-vi-use-case.md`](docs/chung/01-pham-vi-use-case.md). Kế hoạch issue: [`ISSUES_PLAN.md`](ISSUES_PLAN.md).

---

## Kiến trúc

```
 App Android (client)                 Server Spring Boot                 MySQL
 entity – dao – view   ── REST ──>    RestController → DAO → Entity ──>  tblPlayer, tblMatch,
                                      (sinh đề, chấm, trọng tài)         tblMathProblem, tblMatchPlayer
        │                                     │
        └──── Firebase Realtime Database <────┘
              presence (online, rỗi / bận), challenges (lời mời), matches (trận đang đấu)
```

- **App** (`app/`): Activity xử lý sự kiện và gọi thẳng DAO, không có lớp Controller. Luật nghiệp vụ là hàm Java thuần trên entity (`Match.getResult`, `Match.getPoints`, `BotGame.getResult`…). SQLite giữ bản sao người chơi và các ván chơi với máy; SharedPreferences giữ phiên đăng nhập.
- **Server** (`server/`): `RestController → DAO (JDBC) → Entity`, không có tầng Service, không dùng JPA. Server sinh đề, lưu đề cùng đáp án, chấm bài và xác định kết quả.
- **Firebase Realtime Database:** chỉ là kênh báo tức thời giữa các máy. Đề trên Firebase **không có đáp án**; mọi ghi vào `matches` do server làm.

Thiết kế chi tiết: lớp thực thể [`docs/chung/02-lop-thuc-the.md`](docs/chung/02-lop-thuc-the.md), CSDL [`docs/chung/03-csdl.md`](docs/chung/03-csdl.md), lớp chi tiết phần online [`docs/hung/4-lop-thiet-ke.md`](docs/hung/4-lop-thiet-ke.md).

---

## Cấu trúc thư mục

```
math-dash/
├── app/src/main/java/ptit/e23cnpm01/nguyenbahung/math_dash/
│   ├── entity/   BotGame
│   ├── dao/      DAO (SQLite), ApiDAO (REST), FirebaseDAO + các DAO con, SessionDAO, ProblemDAO
│   ├── view/     LoginActivity, RegisterActivity, MainActivity, MatchActivity, RankingActivity,
│   │             DifficultyActivity, BotGameActivity, AdminActivity, PlayerDetailActivity
│   └── util/     PointFormat
├── common/src/main/java/ptit/e23cnpm01/nguyenbahung/mathdash/entity/
│                 Player, MathProblem, Challenge, Match, MatchPlayer, Answer
│                 (lớp thực thể dùng chung, app và server cùng dùng jar này)
├── server/       Spring Boot (xem server/README.md)
├── docs/
│   ├── chung/    phạm vi + use case, lớp thực thể, CSDL
│   └── hung/     tài liệu phân tích, thiết kế, báo cáo của Hùng
├── database.rules.json   Security Rules của Realtime Database
├── ISSUES_PLAN.md · CONVENTION.md · REPORT_CONVENTION.md
```

---

## Cài đặt và chạy

**Yêu cầu:** JDK 17, Android Studio (minSdk 24), Maven, MySQL.

1. **Server:** làm theo [`server/README.md`](server/README.md) (tạo CSDL bằng `server/db/schema.sql`, cấu hình, chạy hoặc deploy lên Render).
2. **App:** đặt `google-services.json` vào `app/`, mở thư mục gốc bằng Android Studio và Run. `BASE_URL` trong `ApiDAO` trỏ tới server trên Render; chạy server local thì đổi thành `http://10.0.2.2:8080/api`.
3. **Tài khoản quản trị** tạo sẵn: `admin` / `admin123`.

Dòng lệnh:

```powershell
.\gradlew.bat test            # unit test của app
.\gradlew.bat assembleDebug   # app/build/outputs/apk/debug/app-debug.apk
mvn -f common/pom.xml install # cài jar lớp thực thể dùng chung, làm trước khi build server
cd server; mvn test           # unit test của server
```

---

## Tác giả
* **Lớp:** E23CNPM01 · **Học viện Công nghệ Bưu chính Viễn thông (PTIT)**
* Nguyễn Bá Hùng ([@HungNguyenBa1811](https://github.com/HungNguyenBa1811)) - Nhóm trưởng: Chơi online, Xem bảng xếp hạng, Đăng nhập – đăng ký, server
* Hoàng ([@Minahn210](https://github.com/Minahn210)) - Chơi với máy, Quản lý thông tin người chơi, Đăng nhập – đăng ký
