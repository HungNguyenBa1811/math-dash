-- Bản 8: đổi kiểu cột theo quy ước String → VARCHAR(255), Date → DATETIME(3); bỏ bảng tblFriendship. Không xóa dữ liệu khác
-- Chạy: mysql -u <user> -p mathdash < server/db/migrate-8-types.sql

ALTER TABLE tblPlayer
    MODIFY role      VARCHAR(255) NOT NULL DEFAULT 'player',
    MODIFY createdAt DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3);

ALTER TABLE tblMathProblem
    MODIFY operator VARCHAR(255) NOT NULL;

DROP TABLE IF EXISTS tblFriendship;
