package ptit.e23cnpm01.nguyenbahung.mathdash.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;

import ptit.e23cnpm01.nguyenbahung.mathdash.dao.PlayerDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

/**
 * API người chơi: thông tin kèm tổng điểm, bảng xếp hạng, và các hàm quản trị (UC06, Hoàng).
 */
@RestController
@RequestMapping("/api/players")
public class PlayerRestController {
    private PlayerDAO playerDAO = new PlayerDAO();

    // GET /api/players/{id}: người chơi kèm tổng điểm hiện có
    @GetMapping("/{id}")
    public Player getById(@PathVariable int id) throws SQLException {
        Player player = playerDAO.findById(id);
        if (player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người chơi");
        }
        player.setTotalPoints(playerDAO.getTotalPoints(id));
        return player;
    }

    // GET /api/players/ranking: mọi người chơi kèm 3 tiêu chí, đã xếp hạng
    @GetMapping("/ranking")
    public List<Player> getRanking() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Chưa làm: M2-05");
    }
}
