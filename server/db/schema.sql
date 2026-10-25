-- Math Dash 1v1 - CSDL MySQL cho server
-- Thiết kế: docs/chung/03-csdl.md
-- Chạy: mysql -u root -p < server/db/schema.sql (xóa và tạo lại toàn bộ bảng)

CREATE DATABASE IF NOT EXISTS mathdash CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mathdash;

DROP TABLE IF EXISTS tblMatchPlayer;
DROP TABLE IF EXISTS tblMathProblem;
DROP TABLE IF EXISTS tblMatch;
DROP TABLE IF EXISTS tblPlayer;

CREATE TABLE tblPlayer (
    id           INT(10)      NOT NULL AUTO_INCREMENT,
    username     VARCHAR(255) NOT NULL,
    passwordHash VARCHAR(255) NOT NULL,                   -- mật khẩu đã băm BCrypt
    displayName  VARCHAR(255) NOT NULL,
    role         VARCHAR(10)  NOT NULL DEFAULT 'player',  -- player | admin
    createdAt    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_player_username (username)
);

CREATE TABLE tblMatch (
    id        INT(10)     NOT NULL AUTO_INCREMENT,
    startedAt DATETIME(3) NOT NULL,
    endedAt   DATETIME(3) NULL,                           -- rỗng khi trận đang đấu
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
    submittedAt  DATETIME(3) NULL,                        -- rỗng khi chưa nộp bài
    forfeited    TINYINT(1)  NOT NULL DEFAULT 0,
    tblMatchId   INT(10)     NOT NULL,
    tblPlayerId  INT(10)     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_matchplayer (tblMatchId, tblPlayerId),
    FOREIGN KEY (tblMatchId)  REFERENCES tblMatch(id)  ON DELETE CASCADE,
    FOREIGN KEY (tblPlayerId) REFERENCES tblPlayer(id)
);

-- Tài khoản quản trị tạo sẵn: admin / admin123 (đổi mật khẩu sau khi cài đặt)
INSERT INTO tblPlayer (username, passwordHash, displayName, role)
VALUES ('admin', '$2a$10$Je/kzmzUnx.uvhoJI/z1DekWXJ91nJokKmy5KtckJvE9jYUx1SRLi', 'Quản trị viên', 'admin');
