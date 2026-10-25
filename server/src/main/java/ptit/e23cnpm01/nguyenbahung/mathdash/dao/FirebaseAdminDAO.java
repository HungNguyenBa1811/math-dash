package ptit.e23cnpm01.nguyenbahung.mathdash.dao;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

/**
 * Đọc / ghi Realtime Database qua REST API: presence, challenges, matches.
 * Cây dữ liệu: docs/chung/03-csdl.md mục 2. Thân các hàm làm ở M2-01 → M2-04.
 */
public class FirebaseAdminDAO {
    private String dbUrl;
    private HttpClient client = HttpClient.newHttpClient();
    private ObjectMapper mapper = new ObjectMapper();

    public FirebaseAdminDAO() {
        dbUrl = DAO.loadConfig().getProperty("firebase.databaseUrl");
    }

    // GET presence/{playerId}: người chơi kèm trạng thái; không online thì trả null
    public Player readPresence(int playerId) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("TODO M2-01");
    }

    // PATCH presence/{playerId}: status = free | busy
    public void updateStatus(int playerId, String status) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("TODO M2-01");
    }

    // PATCH presence/{playerId}: totalPoints
    public void updatePoints(int playerId, double totalPoints) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("TODO M2-03");
    }

    // PATCH challenges/{challengeId}: status, matchId
    public void updateChallenge(String challengeId, String status, int matchId) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("TODO M2-02");
    }

    // PUT matches/{matchId}: đề không kèm đáp án, 2 người chơi, status = playing
    public void writeMatch(Match match) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("TODO M2-02");
    }

    // PUT matches/{matchId}/players/{playerId}/submitted = true
    public void writeSubmitted(int matchId, int playerId) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("TODO M2-03");
    }

    // PUT matches/{matchId}/status = finished
    public void writeFinished(int matchId) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("TODO M2-03");
    }

    // PUT matches/{matchId}/players/{playerId}/choice = continue | stop
    public void writeChoice(int matchId, int playerId, boolean choice) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("TODO M2-04");
    }

    // PUT matches/{matchId}/nextMatchId
    public void writeNextMatch(int matchId, int nextMatchId) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("TODO M2-04");
    }

    // PUT matches/{matchId}/stoppedBy
    public void writeStopped(int matchId, int playerId) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("TODO M2-04");
    }

    // Gửi yêu cầu; Firebase trả mã khác 200 thì ném lỗi
    private String send(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Firebase trả lỗi " + response.statusCode() + ": " + response.body());
        }
        return response.body();
    }

    // Địa chỉ REST của một node, ví dụ path = "matches/37/status"
    private URI nodeUri(String path) {
        return URI.create(dbUrl + "/" + path + ".json");
    }
}
