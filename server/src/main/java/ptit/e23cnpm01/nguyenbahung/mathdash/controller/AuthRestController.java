package ptit.e23cnpm01.nguyenbahung.mathdash.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import ptit.e23cnpm01.nguyenbahung.mathdash.dao.DAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.dao.PlayerDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

/**
 * API tài khoản: đăng ký, đăng nhập. Mật khẩu băm BCrypt, token đăng nhập là JWT.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
    private static final long TOKEN_MILLIS = 24L * 60 * 60 * 1000;
    // Khóa ký JWT lấy từ jwt.secret trong cấu hình
    private static final SecretKey KEY = Keys.hmacShaKeyFor(
            DAO.loadConfig().getProperty("jwt.secret").getBytes(StandardCharsets.UTF_8));

    private PlayerDAO playerDAO = new PlayerDAO();
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Token hợp lệ: đúng chữ ký của server và chưa hết hạn
    public static boolean isValidToken(String token) {
        if (token == null) {
            return false;
        }
        try {
            Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // POST /api/auth/register: 201, hoặc 409 nếu trùng tên đăng nhập
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody Map<String, String> body) {
        Player player = new Player(0, body.get("username"), body.get("displayName"));
        player.setPasswordHash(encoder.encode(body.get("password")));
        if (!playerDAO.insert(player)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên đăng nhập đã được dùng");
        }
    }

    // POST /api/auth/login: { player, token }, hoặc 401
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) throws SQLException {
        Player player = playerDAO.findByUsername(body.get("username"));
        if (player == null || !encoder.matches(body.get("password"), player.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sai tên đăng nhập hoặc mật khẩu");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("player", player);
        result.put("token", createToken(player));
        return result;
    }

    // JWT mang id người chơi, hết hạn sau 24 giờ
    private String createToken(Player player) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(player.getId()))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + TOKEN_MILLIS))
                .signWith(KEY)
                .compact();
    }
}
