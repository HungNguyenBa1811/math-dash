package ptit.e23cnpm01.nguyenbahung.mathdash.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.Map;

import ptit.e23cnpm01.nguyenbahung.mathdash.dao.PlayerDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;


@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
    private PlayerDAO playerDAO = new PlayerDAO();
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // POST /api/auth/register
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody Map<String, String> body) {
        Player player = new Player(0, body.get("username"), body.get("displayName"));
        player.setPasswordHash(encoder.encode(body.get("password")));
        if (!playerDAO.insert(player)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên đăng nhập đã được dùng");
        }
    }

    // POST /api/auth/login: trả Player, hoặc 401 nếu sai tên đăng nhập / mật khẩu
    @PostMapping("/login")
    public Player login(@RequestBody Map<String, String> body) throws SQLException {
        Player player = playerDAO.findByUsername(body.get("username"));
        if (player == null || !encoder.matches(body.get("password"), player.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sai tên đăng nhập hoặc mật khẩu");
        }
        return player;
    }
}
