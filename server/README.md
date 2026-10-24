# Backend Math Dash 1v1

Spring Boot (Java 17, Maven), JDBC thuần tới MySQL, đăng nhập bằng tên đăng nhập + mật khẩu (băm BCrypt), đọc Firebase qua REST API.

## 1. Tạo CSDL

```
mysql -u root -p < db/schema.sql
```

## 2. Cấu hình

Sao chép `src/main/resources/application-local.properties.example` thành `application-local.properties`, điền mật khẩu MySQL. File này có trong `.gitignore`, không commit.

Địa chỉ Realtime Database (`firebase.databaseUrl`) nằm sẵn trong `application.properties`; server đọc và ghi trận qua REST API của Firebase, không cần service account.

## 3. Chạy

```
mvn spring-boot:run
```

Server chạy ở `http://localhost:8080`; máy ảo Android gọi về bằng `http://10.0.2.2:8080`.

## 4. Kiểm thử

```
mvn test
```

## 5. Phía app và Firebase

- Đặt `google-services.json` của app (`ptit.e23cnpm01.nguyenbahung.math_dash`) vào `app/`.
- Bật Realtime Database, dán nội dung `database.rules.json` ở thư mục gốc repo vào tab Rules. Không cần bật Authentication.

## 6. Deploy lên Render

- MySQL đặt ở dịch vụ ngoài (ví dụ Aiven, gói Free), tạo bảng bằng `db/schema.sql`.
- Render: Web Service, Root Directory `server`, Language `Docker` (dùng `server/Dockerfile`).
- Biến môi trường (ghi đè cấu hình trong file):

| Biến | Ví dụ |
|---|---|
| `DB_URL` | `jdbc:mysql://<host>:<port>/mathdash?sslMode=REQUIRED&serverTimezone=UTC&autoReconnect=true` (MySQL trên Aiven chạy giờ UTC) |
| `DB_USER` | `avnadmin` |
| `DB_PASSWORD` | mật khẩu MySQL |

- Cổng lấy từ biến `PORT` do Render cấp (`server.port=${PORT:8080}`).
- App: đổi `BASE_URL` trong `ApiDAO` thành `https://<tên-service>.onrender.com/api`.

## 7. API

| Phương thức | Đường dẫn | Kết quả |
|---|---|---|
| POST | /api/auth/register | 201, hoặc 409 nếu trùng tên đăng nhập |
| POST | /api/auth/login | Player, hoặc 401 nếu sai tên đăng nhập / mật khẩu |
| POST | /api/matches/{roomCode}/finish | Trận đã lưu; 409 nếu trận chưa kết thúc |
| GET | /api/matches?playerId={id} | Các trận đã kết thúc của người chơi, mới nhất trước |
