package ptit.e23cnpm01.nguyenbahung.mathdash.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

import ptit.e23cnpm01.nguyenbahung.mathdash.dao.FirebaseAdminDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.dao.MatchDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.dao.PlayerDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.dao.ProblemDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Challenge;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

/**
 * Trọng tài của mọi trận: sinh đề, chấm bài, xác định kết quả, chơi tiếp.
 * Thiết kế: docs/hung/4-lop-thiet-ke.md mục 1.2. Thân các hàm làm ở M2-02 → M2-04.
 */
@RestController
@RequestMapping("/api/matches")
public class MatchRestController {
    private MatchDAO matchDAO = new MatchDAO();
    private PlayerDAO playerDAO = new PlayerDAO();
    private ProblemDAO problemDAO = new ProblemDAO();
    private FirebaseAdminDAO firebaseAdminDAO = new FirebaseAdminDAO();
    // Trận đang có một người chọn chơi tiếp: id trận → id người đã chọn
    private Map<Integer, Integer> waitingChoices = new HashMap<>();

    // POST /api/matches: tạo trận từ lời mời đã đồng ý
    @PostMapping
    public synchronized Match create(@RequestBody Challenge challenge) {
        throw notYet("M2-02");
    }

    // POST /api/matches/{id}/submit: nộp bài, server chấm và ghi thời điểm nhận
    @PostMapping("/{id}/submit")
    public synchronized Match submit(@PathVariable int id, @RequestBody MatchPlayer matchPlayer) {
        throw notYet("M2-03");
    }

    // POST /api/matches/{id}/forfeit?playerId=: bỏ trận, bị xử thua
    @PostMapping("/{id}/forfeit")
    public synchronized Match forfeit(@PathVariable int id, @RequestParam int playerId) {
        throw notYet("M2-04");
    }

    // POST /api/matches/{id}/continue?playerId=&choice=: chơi tiếp hoặc dừng
    @PostMapping("/{id}/continue")
    public synchronized Match continueMatch(@PathVariable int id, @RequestParam int playerId,
                                            @RequestParam boolean choice) {
        throw notYet("M2-04");
    }

    // GET /api/matches/{id}: trận đầy đủ, có đáp án khi trận đã xong
    @GetMapping("/{id}")
    public Match getById(@PathVariable int id) {
        throw notYet("M2-03");
    }

    // Sinh đề, tạo trận, lưu MySQL, gửi đề qua Firebase, chuyển 2 người sang bận
    private Match startMatch(Player p1, Player p2) {
        throw notYet("M2-02");
    }

    private ResponseStatusException notYet(String issue) {
        return new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Chưa làm: " + issue);
    }
}
