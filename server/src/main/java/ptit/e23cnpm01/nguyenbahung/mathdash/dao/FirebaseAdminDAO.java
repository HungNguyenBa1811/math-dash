package ptit.e23cnpm01.nguyenbahung.mathdash.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MathProblem;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

// Cây dữ liệu: docs/chung/03-csdl.md mục 2
public class FirebaseAdminDAO {
    private String dbUrl;
    // Tùy chọn: có thì ghi được cả khi Rules chặn app
    private String secret;
    private HttpClient client = HttpClient.newHttpClient();
    private ObjectMapper mapper = new ObjectMapper();

    public FirebaseAdminDAO() {
        dbUrl = DAO.loadConfig().getProperty("firebase.databaseUrl");
        secret = DAO.loadConfig().getProperty("firebase.secret", "");
    }

    // null nếu không online
    public Player readPresence(int playerId) throws IOException, InterruptedException {
        JsonNode node = mapper.readTree(send("GET", "presence/" + playerId, null));
        if (node == null || node.isNull()) {
            return null;
        }
        Player player = new Player(playerId, null, node.path("displayName").asText());
        player.setTotalPoints(node.path("totalPoints").asDouble());
        player.setStatus(node.path("status").asText(Player.FREE));
        return player;
    }

    // Người đã offline thì bỏ qua
    public void updateStatus(int playerId, String status) throws IOException, InterruptedException {
        updatePresence(playerId, "status", status);
    }

    public void updatePoints(int playerId, double totalPoints) throws IOException, InterruptedException {
        updatePresence(playerId, "totalPoints", totalPoints);
    }

    // null nếu lời mời đã bị xóa
    public String readChallengeStatus(String challengeId) throws IOException, InterruptedException {
        JsonNode node = mapper.readTree(send("GET", "challenges/" + challengeId + "/status", null));
        return node == null || node.isNull() ? null : node.asText();
    }

    public void updateChallenge(String challengeId, String status, int matchId) throws IOException, InterruptedException {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("status", status);
        data.put("matchId", matchId);
        send("PATCH", "challenges/" + challengeId, data);
    }

    public void writeMatch(Match match) throws IOException, InterruptedException {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("status", Match.PLAYING);
        data.put("startedAt", match.getStartedAt().getTime());
        data.put("questions", toQuestions(match.getQuestions()));
        data.put("players", toPlayers(match.getPlayers()));
        send("PUT", "matches/" + match.getId(), data);
    }

    public void writeSubmitted(int matchId, int playerId) throws IOException, InterruptedException {
        send("PUT", "matches/" + matchId + "/players/" + playerId + "/submitted", true);
    }

    public void writeFinished(int matchId) throws IOException, InterruptedException {
        send("PUT", "matches/" + matchId + "/status", Match.FINISHED);
    }

    public void writeChoice(int matchId, int playerId, boolean choice) throws IOException, InterruptedException {
        send("PUT", "matches/" + matchId + "/players/" + playerId + "/choice", choice ? "continue" : "stop");
    }

    public void writeNextMatch(int matchId, int nextMatchId) throws IOException, InterruptedException {
        send("PUT", "matches/" + matchId + "/nextMatchId", nextMatchId);
    }

    public void writeStopped(int matchId, int playerId) throws IOException, InterruptedException {
        send("PUT", "matches/" + matchId + "/stoppedBy", playerId);
    }

    private void updatePresence(int playerId, String field, Object value) throws IOException, InterruptedException {
        if (readPresence(playerId) == null) {
            return;
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put(field, value);
        send("PATCH", "presence/" + playerId, data);
    }

    // Không gửi correctAnswer cho app
    private List<Map<String, Object>> toQuestions(List<MathProblem> questions) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MathProblem p : questions) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("operand1", p.getOperand1());
            item.put("operand2", p.getOperand2());
            item.put("operator", p.getOperator());
            item.put("answerOptions", p.getAnswerOptions());
            list.add(item);
        }
        return list;
    }

    private Map<String, Object> toPlayers(List<MatchPlayer> players) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (MatchPlayer mp : players) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("displayName", mp.getPlayer().getDisplayName());
            item.put("submitted", false);
            item.put("forfeited", false);
            map.put(String.valueOf(mp.getPlayer().getId()), item);
        }
        return map;
    }

    private String send(String method, String path, Object body) throws IOException, InterruptedException {
        String query = secret.isEmpty() ? "" : "?auth=" + secret;
        HttpRequest.BodyPublisher publisher = body == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body));
        HttpRequest request = HttpRequest.newBuilder(URI.create(dbUrl + "/" + path + ".json" + query))
                .method(method, publisher)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Firebase trả lỗi " + response.statusCode() + ": " + response.body());
        }
        return response.body();
    }
}
