# Backend Math Dash 1v1

Spring Boot (Java 17, Maven), JDBC thuần tới MySQL, đăng nhập bằng tên đăng nhập + mật khẩu (băm BCrypt), đọc Firebase qua REST API.

Lớp thực thể (`Player`, `Match`, `MatchPlayer`, `MathProblem`, `Answer`, `Challenge`) nằm ở project `common/` tại thư mục gốc repo, dùng chung với app. Các lệnh dưới chạy trong thư mục `server/`.

## 1. Tạo CSDL

```
mysql -u root -p < db/schema.sql
```

Script **xóa và tạo lại** 5 bảng `tblPlayer`, `tblMatch`, `tblMathProblem`, `tblMatchPlayer`, `tblAnswer`, rồi chèn sẵn tài khoản quản trị `admin` / `admin123`.

CSDL đang chạy (không muốn mất dữ liệu) thì chạy lần lượt các bản cập nhật:

```
mysql -u <user> -p mathdash < db/migrate-7-answer.sql
mysql -u <user> -p mathdash < db/migrate-8-drop-friendship.sql
```

## 2. Cấu hình

Sao chép `src/main/resources/application-local.properties.example` thành `application-local.properties`, điền mật khẩu MySQL. File này có trong `.gitignore`, không commit.

Địa chỉ Realtime Database (`firebase.databaseUrl`) nằm sẵn trong `application.properties`; server đọc và ghi trận qua REST API của Firebase, không cần service account.

## 3. Chạy

Cài jar lớp thực thể dùng chung vào kho Maven trên máy (làm lại mỗi khi sửa `common/`):

```
mvn -f ../common/pom.xml install
mvn spring-boot:run
```

Server chạy ở `http://localhost:8080`; máy ảo Android gọi về bằng `http://10.0.2.2:8080`.

## 4. Kiểm thử

```
mvn -f ../common/pom.xml install
mvn test
```

## 5. Phía app và Firebase

- Đặt `google-services.json` của app (`ptit.e23cnpm01.nguyenbahung.math_dash`) vào `app/`.
- Bật Realtime Database, dán nội dung `database.rules.json` ở thư mục gốc repo vào tab Rules. Không cần bật Authentication.

## 6. Deploy lên Render

- MySQL đặt ở dịch vụ ngoài (ví dụ Aiven, gói Free), tạo bảng bằng `db/schema.sql`.
- Render: Web Service, Language `Docker`. Server cần project `common/` nên build context là gốc repo:
  - Root Directory để trống.
  - Dockerfile Path `server/Dockerfile`.
  - Docker Build Context Directory `.` (`.dockerignore` ở gốc repo chỉ gửi `common/` và `server/`).
  - Build Filters, Included Paths: `server/**` và `common/**`, để commit chỉ sửa app không deploy lại.
- **Environment → Secret Files** thêm file `application-local.properties` (Dockerfile chép vào `server/src/main/resources/` lúc build):

```
db.url=jdbc:mysql://<host>:<port>/mathdash?sslMode=REQUIRED&serverTimezone=UTC&autoReconnect=true
db.user=avnadmin
db.password=<mật khẩu MySQL>
```

- MySQL trên Aiven chạy giờ UTC nên dùng `serverTimezone=UTC`.
- Cổng lấy từ biến `PORT` do Render cấp (`server.port=${PORT:8080}`).
- App: đổi `BASE_URL` trong `ApiDAO` thành `https://<tên-service>.onrender.com/api`.

## 7. API

| Phương thức | Đường dẫn | Kết quả |
|---|---|---|
| POST | /api/auth/register | 201, hoặc 409 nếu trùng tên đăng nhập |
| POST | /api/auth/login | Player (có `role`), hoặc 401 nếu sai tên đăng nhập / mật khẩu |
| GET | /api/players/{id} | Player kèm `totalPoints` |
| GET | /api/players/ranking | Danh sách Player đã xếp hạng theo 3 tiêu chí |
| POST | /api/matches | Tạo trận từ lời mời đã đồng ý; 409 nếu một người không online hoặc đang bận |
| POST | /api/matches/{id}/submit | Nộp bài làm `{player: {id}, answers: [{selectedOption, answerTime}]}`, server chấm và lưu bài làm từng câu |
| POST | /api/matches/{id}/forfeit?playerId= | Bỏ trận |
| POST | /api/matches/{id}/continue?playerId=&choice= | Chơi tiếp hoặc dừng |
| GET | /api/matches/{id} | Trận đầy đủ; đáp án và bài làm từng câu của 2 người chỉ có khi trận đã xong |

Các API quản trị (UC06) bổ sung ở M2-07. Danh sách đầy đủ: `ISSUES_PLAN.md` mục 2.4.
