-- Bản 7: thêm bảng bài làm từng câu vào CSDL đang chạy, không xóa dữ liệu cũ
-- Chạy: mysql -u <user> -p mathdash < server/db/migrate-7-answer.sql

CREATE TABLE IF NOT EXISTS tblAnswer (
    id               INT(10)    NOT NULL AUTO_INCREMENT,
    selectedOption   INT(10)    NOT NULL DEFAULT -1,        -- -1 khi bỏ trống
    answerTime       BIGINT(20) NOT NULL DEFAULT 0,         -- mili giây từ lúc câu hiện ra tới lúc chọn
    tblMatchPlayerId INT(10)    NOT NULL,
    tblMathProblemId INT(10)    NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_answer (tblMatchPlayerId, tblMathProblemId),
    FOREIGN KEY (tblMatchPlayerId) REFERENCES tblMatchPlayer(id) ON DELETE CASCADE,
    FOREIGN KEY (tblMathProblemId) REFERENCES tblMathProblem(id) ON DELETE CASCADE
);
