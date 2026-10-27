package ptit.e23cnpm01.nguyenbahung.mathdash.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import ptit.e23cnpm01.nguyenbahung.mathdash.dao.FriendshipDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.dao.PlayerDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Friendship;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

/**
 * Quản lý bạn bè: lấy lời mời và bạn bè, gửi lời mời, đồng ý, từ chối / hủy kết bạn.
 * Các hàm ghi đều synchronized để 2 người gửi lời mời cho nhau cùng lúc không tạo 2 quan hệ.
 */
@RestController
@RequestMapping("/api/friendships")
public class FriendshipRestController {
    private FriendshipDAO friendshipDAO = new FriendshipDAO();
    private PlayerDAO playerDAO = new PlayerDAO();

    // GET /api/friendships?playerId={id}: lời mời và bạn bè, kèm tổng điểm của 2 bên
    @GetMapping
    public List<Friendship> getFriendships(@RequestParam int playerId) throws SQLException {
        List<Friendship> list = friendshipDAO.findByPlayer(playerId);
        for (Friendship f : list) {
            f.getSender().setTotalPoints(playerDAO.getTotalPoints(f.getSender().getId()));
            f.getReceiver().setTotalPoints(playerDAO.getTotalPoints(f.getReceiver().getId()));
        }
        return list;
    }

    // POST /api/friendships: gửi lời mời theo tên đăng nhập của người nhận
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public synchronized Friendship sendRequest(@RequestBody Map<String, String> body) throws SQLException {
        String username = body.get("receiverUsername") == null ? "" : body.get("receiverUsername").trim();
        if (username.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nhập tên đăng nhập của người muốn kết bạn");
        }
        Player sender = playerDAO.findById(parseId(body.get("senderId")));
        Player receiver = playerDAO.findByUsername(username);
        if (sender == null || receiver == null || !Player.ROLE_PLAYER.equals(receiver.getRole())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người chơi");
        }
        if (sender.getId() == receiver.getId()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể kết bạn với chính mình");
        }
        if (friendshipDAO.existsBetween(sender.getId(), receiver.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Đã có lời mời hoặc đã là bạn bè");
        }
        Friendship friendship = new Friendship(sender, receiver);
        if (!friendshipDAO.insert(friendship)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Đã có lời mời hoặc đã là bạn bè");
        }
        receiver.setTotalPoints(playerDAO.getTotalPoints(receiver.getId()));
        return friendship;
    }

    // POST /api/friendships/{id}/accept?playerId={id}: người nhận đồng ý lời mời
    @PostMapping("/{id}/accept")
    public synchronized Friendship accept(@PathVariable int id, @RequestParam int playerId) throws SQLException {
        Friendship friendship = findFriendship(id);
        if (!Friendship.PENDING.equals(friendship.getStatus())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lời mời không còn");
        }
        if (!friendship.canAccept(playerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chỉ người nhận mới đồng ý được lời mời");
        }
        if (!friendshipDAO.updateStatus(id, Friendship.ACCEPTED)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lời mời không còn");
        }
        friendship.setStatus(Friendship.ACCEPTED);
        friendship.getSender().setTotalPoints(playerDAO.getTotalPoints(friendship.getSender().getId()));
        return friendship;
    }

    // DELETE /api/friendships/{id}?playerId={id}: từ chối, rút lời mời hoặc hủy kết bạn
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public synchronized void delete(@PathVariable int id, @RequestParam int playerId) throws SQLException {
        Friendship friendship = findFriendship(id);
        if (friendship.getSender().getId() != playerId && friendship.getReceiver().getId() != playerId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Người chơi không thuộc quan hệ này");
        }
        friendshipDAO.delete(id);
    }

    private Friendship findFriendship(int id) throws SQLException {
        Friendship friendship = friendshipDAO.findById(id);
        if (friendship == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lời mời không còn");
        }
        return friendship;
    }

    private int parseId(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu người gửi lời mời");
        }
    }
}
