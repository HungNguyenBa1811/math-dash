package ptit.e23cnpm01.nguyenbahung.mathdash.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

/**
 * Đọc / ghi trận đang diễn ra trên Firebase qua REST API của Realtime Database.
 * Không dùng JDBC nên không kế thừa DAO.
 */
public class FirebaseAdminDAO {
    private String dbUrl;
    private HttpClient client = HttpClient.newHttpClient();
    private ObjectMapper mapper = new ObjectMapper();

    public FirebaseAdminDAO() {
        dbUrl = DAO.loadConfig().getProperty("firebase.databaseUrl");
    }

    // GET matches/{roomCode}.json và đóng gói thành Match; không có trận thì trả null
    public Match readMatch(String roomCode) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(nodeUri(roomCode, "")).GET().build();
        JsonNode node = mapper.readTree(send(request));
        return node.isNull() ? null : toMatch(roomCode, node);
    }

    // PUT matches/{roomCode}/status.json
    public void updateStatus(String roomCode, String status) throws IOException, InterruptedException {
        String body = mapper.writeValueAsString(status);
        HttpRequest request = HttpRequest.newBuilder(nodeUri(roomCode, "/status"))
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        send(request);
    }

    private String send(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Firebase trả lỗi " + response.statusCode() + ": " + response.body());
        }
        return response.body();
    }

    private URI nodeUri(String roomCode, String child) {
        return URI.create(dbUrl + "/matches/" + roomCode + child + ".json");
    }

    private Match toMatch(String roomCode, JsonNode node) {
        Match match = new Match();
        match.setRoomCode(roomCode);
        match.setStatus(node.path("status").asText());
        match.setQuestionSeed(node.path("questionSeed").asLong());
        match.setCreatedAt(new Date(node.path("createdAt").asLong()));
        addPlayers(match, node.path("players"));
        return match;
    }

    // Firebase đổi node có khóa là số (id người chơi) thành mảng, nên đọc được cả 2 dạng
    private void addPlayers(Match match, JsonNode players) {
        if (players.isArray()) {
            for (int id = 0; id < players.size(); id++) {
                if (!players.get(id).isNull()) {
                    match.getPlayers().add(toMatchPlayer(id, players.get(id)));
                }
            }
            return;
        }
        Iterator<Map.Entry<String, JsonNode>> fields = players.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            match.getPlayers().add(toMatchPlayer(Integer.parseInt(field.getKey()), field.getValue()));
        }
    }

    private MatchPlayer toMatchPlayer(int playerId, JsonNode node) {
        Player player = new Player();
        player.setId(playerId);
        player.setDisplayName(node.path("displayName").asText());
        MatchPlayer mp = new MatchPlayer(player);
        mp.setScore(node.path("score").asInt());
        mp.setForfeited(node.path("forfeited").asBoolean());
        mp.setTimeUp(node.path("timeUp").asBoolean());
        return mp;
    }
}
