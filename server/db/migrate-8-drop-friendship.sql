-- Bản 8: bỏ bảng quan hệ bạn bè khỏi CSDL đang chạy, không xóa dữ liệu khác
-- Chạy: mysql -u <user> -p mathdash < server/db/migrate-8-drop-friendship.sql

DROP TABLE IF EXISTS tblFriendship;
