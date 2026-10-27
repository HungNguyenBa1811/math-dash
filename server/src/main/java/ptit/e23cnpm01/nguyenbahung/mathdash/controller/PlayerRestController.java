package ptit.e23cnpm01.nguyenbahung.mathdash.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import ptit.e23cnpm01.nguyenbahung.mathdash.dao.PlayerDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

/**
 * API người chơi: thông tin kèm tổng điểm, bảng xếp hạng, và các hàm quản trị (UC06, Hoàng).
 */
@RestController
@RequestMapping("/api/players")
public class PlayerRestController {
    private static final String NOT_FOUND = "Không tìm thấy người chơi";

    private PlayerDAO playerDAO = new PlayerDAO();
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // GET /api/players/{id}: người chơi kèm tổng điểm và số trận thắng / hòa / thua
    @GetMapping("/{id}")
    public Player getById(@PathVariable int id) throws SQLException {
        Player player = playerDAO.findById(id);
        if (player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND);
        }
        player.setTotalPoints(playerDAO.getTotalPoints(id));
        playerDAO.countResults(player);
        return player;
    }

    // GET /api/players/ranking: mọi người chơi kèm 3 tiêu chí, đã xếp hạng
    @GetMapping("/ranking")
    public List<Player> getRanking() throws SQLException {
        return playerDAO.getRanking();
    }

    // ---------- Quản trị (UC06) ----------

    // GET /api/players?name=: người chơi có tên hiển thị chứa name, kèm tổng điểm; tài khoản admin không có
    @GetMapping
    public List<Player> search(@RequestParam(defaultValue = "") String name) throws SQLException {
        return playerDAO.search(name.trim());
    }

    // PUT /api/players/{id}: sửa tên hiển thị, body {"displayName": "..."}
    @PutMapping("/{id}")
    public void update(@PathVariable int id, @RequestBody Map<String, String> body) throws SQLException {
        String displayName = required(body, "displayName", "Tên hiển thị không được để trống").trim();
        if (!playerDAO.updateDisplayName(id, displayName)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND);
        }
    }

    // PUT /api/players/{id}/password: đặt lại mật khẩu, body {"password": "..."}; băm BCrypt trước khi lưu
    @PutMapping("/{id}/password")
    public void resetPassword(@PathVariable int id, @RequestBody Map<String, String> body) throws SQLException {
        String password = required(body, "password", "Mật khẩu mới không được để trống");
        if (!playerDAO.updatePassword(id, encoder.encode(password))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND);
        }
    }

    // DELETE /api/players/{id}: 204 nếu xóa được; người chơi đã có trận thì 409
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) throws SQLException {
        if (!playerDAO.delete(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Không xóa được người chơi đã có trận đấu");
        }
    }

    // Lấy một trường bắt buộc trong body; thiếu hoặc chỉ có khoảng trắng thì trả 400 kèm câu thông báo
    private static String required(Map<String, String> body, String key, String message) {
        String value = body.get(key);
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
        return value;
    }
}
