-- Math Dash 1v1 - CSDL MySQL cho backend
-- Thiết kế: docs/chung/03-csdl.md
-- Chạy: mysql -u root -p < server/db/schema.sql

CREATE DATABASE IF NOT EXISTS mathdash CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mathdash;

CREATE TABLE tblPlayer (
    id          INT(10)      NOT NULL AUTO_INCREMENT,
    username    VARCHAR(255) NOT NULL,
    password    VARCHAR(255) NOT NULL,          -- mật khẩu đã băm BCrypt
    displayName VARCHAR(255) NOT NULL,
    createdAt   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_player_username (username)
);

CREATE TABLE tblMatch (
    id        INT(10)    NOT NULL AUTO_INCREMENT,
    code      VARCHAR(6) NOT NULL,
    seed      BIGINT(20) NOT NULL,
    createdAt DATETIME   NOT NULL,
    endedAt   DATETIME   NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_match_code_created (code, createdAt)
);

CREATE TABLE tblMatchPlayer (
    id          INT(10)    NOT NULL AUTO_INCREMENT,
    score       INT(10)    NOT NULL DEFAULT 0,
    hasLeft     TINYINT(1) NOT NULL DEFAULT 0,
    tblMatchId  INT(10)    NOT NULL,
    tblPlayerId INT(10)    NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_matchplayer (tblMatchId, tblPlayerId),
    FOREIGN KEY (tblMatchId)  REFERENCES tblMatch(id)  ON DELETE CASCADE,
    FOREIGN KEY (tblPlayerId) REFERENCES tblPlayer(id)
);
