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

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ptit.e23cnpm01.nguyenbahung.mathdash.dao.FirebaseAdminDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.dao.MatchDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.dao.PlayerDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.dao.ProblemDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Challenge;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MathProblem;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

// Hàm ghi synchronized: 2 yêu cầu đến cùng lúc không lẫn nhau
@RestController
@RequestMapping("/api/matches")
public class MatchRestController {
    private MatchDAO matchDAO = new MatchDAO();
    private PlayerDAO playerDAO = new PlayerDAO();
    private ProblemDAO problemDAO = new ProblemDAO();
    private FirebaseAdminDAO firebaseAdminDAO = new FirebaseAdminDAO();
    // id trận → id người đã chọn chơi tiếp
    private Map<Integer, Integer> waitingChoices = new HashMap<>();

    // 409 nếu lời mời đã hủy hoặc một người không rỗi
    @PostMapping
    public synchronized Match create(@RequestBody Challenge challenge)
            throws IOException, InterruptedException, SQLException {
        Player p1 = firebaseAdminDAO.readPresence(challenge.getChallenger().getId());
        Player p2 = firebaseAdminDAO.readPresence(challenge.getOpponent().getId());
        boolean pending = Challenge.PENDING.equals(firebaseAdminDAO.readChallengeStatus(challenge.getId()));
        if (!pending || !isFree(p1) || !isFree(p2)) {
            firebaseAdminDAO.updateChallenge(challenge.getId(), Challenge.REJECTED, 0);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Không bắt đầu được trận đấu");
        }
        Match match = startMatch(p1, p2);
        firebaseAdminDAO.updateChallenge(challenge.getId(), Challenge.ACCEPTED, match.getId());
        return hideAnswers(match);
    }

    @PostMapping("/{id}/submit")
    public synchronized Match submit(@PathVariable int id, @RequestBody MatchPlayer matchPlayer)
            throws SQLException, IOException, InterruptedException {
        Date receivedAt = new Date();
        Match match = findMatch(id);
        MatchPlayer me = findPlayer(match, matchPlayer.getPlayer().getId());
        if (match.getEndedAt() == null && me.getSubmittedAt() == null) {
            me.setCorrectCount(match.countCorrect(matchPlayer.getAnswers()));
            me.setSubmittedAt(receivedAt);
            matchDAO.updateSubmission(id, me);
            firebaseAdminDAO.writeSubmitted(id, me.getPlayer().getId());
            if (match.isOver()) {
                endMatch(match);
                firebaseAdminDAO.writeFinished(id);
            }
        }
        return hideAnswers(match);
    }

    @PostMapping("/{id}/forfeit")
    public synchronized Match forfeit(@PathVariable int id, @RequestParam int playerId)
            throws SQLException, IOException, InterruptedException {
        Match match = findMatch(id);
        if (match.getEndedAt() == null) {
            // Lưu kết quả và tổng điểm trước, rồi mới báo máy đối thủ, để máy đó đọc được kết quả đã lưu
            matchDAO.updateForfeit(id, playerId);
            findPlayer(match, playerId).setForfeited(true);
            endMatch(match);
            firebaseAdminDAO.writeStopped(id, playerId);
            firebaseAdminDAO.writeFinished(id);
            setStatus(match, Player.FREE);
        }
        return match;
    }

    @PostMapping("/{id}/continue")
    public synchronized Match continueMatch(@PathVariable int id, @RequestParam int playerId,
                                            @RequestParam boolean choice)
            throws SQLException, IOException, InterruptedException {
        Match match = findMatch(id);
        firebaseAdminDAO.writeChoice(id, playerId, choice);
        Integer waiting = waitingChoices.remove(id);
        if (!choice) {
            firebaseAdminDAO.writeStopped(id, playerId);
            setStatus(match, Player.FREE);
            return match;
        }
        if (waiting == null || waiting == playerId) {
            waitingChoices.put(id, playerId);
            return match;
        }
        Match next = startMatch(match.getPlayers().get(0).getPlayer(), match.getPlayers().get(1).getPlayer());
        firebaseAdminDAO.writeNextMatch(id, next.getId());
        return hideAnswers(next);
    }

    @GetMapping
    public List<Match> getHistory(@RequestParam int playerId) throws SQLException {
        return matchDAO.findByPlayer(playerId);
    }

    @GetMapping("/{id}")
    public Match getById(@PathVariable int id) throws SQLException {
        Match match = findMatch(id);
        for (MatchPlayer mp : match.getPlayers()) {
            mp.getPlayer().setTotalPoints(playerDAO.getTotalPoints(mp.getPlayer().getId()));
        }
        return match.getEndedAt() == null ? hideAnswers(match) : match;
    }

    private Match startMatch(Player p1, Player p2) throws IOException, InterruptedException {
        Match match = new Match();
        match.setStatus(Match.PLAYING);
        match.setStartedAt(new Date());
        match.setQuestions(problemDAO.generateProblems(Match.QUESTION_COUNT));
        match.getPlayers().add(new MatchPlayer(p1));
        match.getPlayers().add(new MatchPlayer(p2));
        if (!matchDAO.insert(match)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không lưu được trận");
        }
        firebaseAdminDAO.writeMatch(match);
        setStatus(match, Player.BUSY);
        return match;
    }

    private void endMatch(Match match) throws SQLException, IOException, InterruptedException {
        match.setEndedAt(new Date());
        match.setStatus(Match.FINISHED);
        matchDAO.updateEndedAt(match.getId(), match.getEndedAt());
        for (MatchPlayer mp : match.getPlayers()) {
            int playerId = mp.getPlayer().getId();
            firebaseAdminDAO.updatePoints(playerId, playerDAO.getTotalPoints(playerId));
        }
    }

    private void setStatus(Match match, String status) throws IOException, InterruptedException {
        for (MatchPlayer mp : match.getPlayers()) {
            firebaseAdminDAO.updateStatus(mp.getPlayer().getId(), status);
        }
    }

    private boolean isFree(Player player) {
        return player != null && Player.FREE.equals(player.getStatus());
    }

    private Match findMatch(int id) throws SQLException {
        Match match = matchDAO.findById(id);
        if (match == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy trận");
        }
        return match;
    }

    private MatchPlayer findPlayer(Match match, int playerId) {
        for (MatchPlayer mp : match.getPlayers()) {
            if (mp.getPlayer().getId() == playerId) {
                return mp;
            }
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Người chơi không ở trong trận");
    }

    private Match hideAnswers(Match match) {
        if (match.getEndedAt() == null) {
            for (MathProblem p : match.getQuestions()) {
                p.setCorrectAnswer(-1);
            }
        }
        return match;
    }
}
