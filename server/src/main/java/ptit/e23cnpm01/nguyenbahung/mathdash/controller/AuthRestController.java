package ptit.e23cnpm01.nguyenbahung.mathdash.controller;

import com.google.firebase.auth.FirebaseAuthException;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import ptit.e23cnpm01.nguyenbahung.mathdash.dao.FirebaseAdminDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.dao.PlayerDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

/**
 * API tài khoản: đăng ký, đăng nhập.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
    // Token đăng nhập giữ trong bộ nhớ server: token -> playerId
    private static final Map<String, Integer> TOKENS = new ConcurrentHashMap<>();

    private PlayerDAO playerDAO = new PlayerDAO();
    private FirebaseAdminDAO firebaseAdminDAO = new FirebaseAdminDAO();
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Token có do server cấp không
    public static boolean isValidToken(String token) {
        return token != null && TOKENS.containsKey(token);
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

    // POST /api/auth/login: { player, token, firebaseToken }, hoặc 401
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) throws SQLException, FirebaseAuthException {
        Player player = playerDAO.findByUsername(body.get("username"));
        if (player == null || !encoder.matches(body.get("password"), player.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sai tên đăng nhập hoặc mật khẩu");
        }
        String token = UUID.randomUUID().toString();
        TOKENS.put(token, player.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("player", player);
        result.put("token", token);
        result.put("firebaseToken", firebaseAdminDAO.createCustomToken(player.getId()));
        return result;
    }
}
