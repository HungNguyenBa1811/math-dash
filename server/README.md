# Backend Math Dash 1v1

Spring Boot (Java 17, Maven), JDBC thuần tới MySQL, Firebase Admin SDK.

## 1. Tạo CSDL

```
mysql -u root -p < db/schema.sql
```

## 2. Cấu hình

1. Sao chép `src/main/resources/application-local.properties.example` thành `application-local.properties`, điền mật khẩu MySQL và địa chỉ Realtime Database.
2. Firebase Console → Project settings → Service accounts → Generate new private key. Lưu file thành `src/main/resources/firebase-service-account.json`.

Hai file trên có trong `.gitignore`, không commit.

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

- Tải `google-services.json` của app (`ptit.e23cnpm01.nguyenbahung.math_dash`) từ Firebase Console, đặt vào `app/`.
- Bật Authentication (dùng custom token, không cần bật provider nào) và Realtime Database.
- Dán nội dung `database.rules.json` ở thư mục gốc repo vào tab Rules của Realtime Database.

## 6. API

| Phương thức | Đường dẫn | Kết quả |
|---|---|---|
| POST | /api/auth/register | 201, hoặc 409 nếu trùng tên đăng nhập |
| POST | /api/auth/login | { player, token, firebaseToken }, hoặc 401 |
| POST | /api/matches/{roomCode}/finish | Trận đã lưu; 409 nếu trận chưa kết thúc |
| GET | /api/matches?playerId={id} | Các trận đã kết thúc của người chơi, mới nhất trước |

Hai API trận đấu cần header `Authorization: Bearer <token>`.
