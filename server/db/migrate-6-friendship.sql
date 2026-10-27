-- Bản 6: thêm bảng quan hệ bạn bè vào CSDL đang chạy, không xóa dữ liệu cũ
-- Chạy: mysql -u <user> -p mathdash < server/db/migrate-6-friendship.sql

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
