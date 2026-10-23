package ptit.e23cnpm01.nguyenbahung.mathdash.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import ptit.e23cnpm01.nguyenbahung.mathdash.dao.FirebaseAdminDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.dao.MatchDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Match;

/**
 * API trận đấu: kết thúc trận (server làm trọng tài), lịch sử đấu.
 */
@RestController
@RequestMapping("/api/matches")
public class MatchRestController {
    private MatchDAO matchDAO = new MatchDAO();
    private FirebaseAdminDAO firebaseAdminDAO = new FirebaseAdminDAO();

    // POST /api/matches/{roomCode}/finish
    // synchronized: 2 máy gọi cùng lúc thì máy gọi sau nhận lại đúng bản đã lưu
    @PostMapping("/{roomCode}/finish")
    public synchronized Match finish(@PathVariable String roomCode)
            throws SQLException, IOException, InterruptedException {
        Match match = firebaseAdminDAO.readMatch(roomCode);
        if (match == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy trận");
        }
        if ("finished".equals(match.getStatus())) {
            return matchDAO.findByRoomCode(roomCode, match.getCreatedAt());
        }
        // Kiểm tra lại ở server, không tin yêu cầu kết thúc trận gửi từ app
        if (!match.isOver()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Trận chưa kết thúc");
        }
        match.setEndedAt(new Date());
        if (!matchDAO.insert(match)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không lưu được trận");
        }
        firebaseAdminDAO.updateStatus(roomCode, "finished");
        match.setStatus("finished");
        return match;
    }

    // GET /api/matches?playerId={id}
    @GetMapping
    public List<Match> getByPlayer(@RequestParam int playerId) throws SQLException {
        return matchDAO.findByPlayer(playerId);
    }
}
