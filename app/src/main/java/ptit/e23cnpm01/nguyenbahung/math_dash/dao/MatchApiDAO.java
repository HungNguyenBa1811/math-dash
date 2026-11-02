package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Answer;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Challenge;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MathProblem;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

// Blocking calls, must run on a background Thread
public class MatchApiDAO extends ApiDAO {

    // The invitee accepts, the server creates the match from the challenge
    public Match createMatch(Challenge challenge) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("id", challenge.getId());
        body.put("challenger", new JSONObject().put("id", challenge.getChallenger().getId()));
        body.put("opponent", new JSONObject().put("id", challenge.getOpponent().getId()));
        return toMatch(new JSONObject(request("POST", "/matches", body)));
    }

    // Sends the chosen option and answer time of every question, the server scores them
    public Match submit(int matchId, int playerId, List<Answer> answers) throws IOException, JSONException {
        JSONArray array = new JSONArray();
        for (Answer a : answers) {
            array.put(new JSONObject().put("selectedOption", a.getSelectedOption())
                    .put("answerTime", a.getAnswerTime()));
        }
        JSONObject body = new JSONObject();
        body.put("player", new JSONObject().put("id", playerId));
        body.put("answers", array);
        return toMatch(new JSONObject(request("POST", "/matches/" + matchId + "/submit", body)));
    }

    public Match forfeit(int matchId, int playerId) throws IOException, JSONException {
        String path = "/matches/" + matchId + "/forfeit?playerId=" + playerId;
        return toMatch(new JSONObject(request("POST", path, new JSONObject())));
    }

    // choice true = play again, false = stop
    public Match continueMatch(int matchId, int playerId, boolean choice) throws IOException, JSONException {
        String path = "/matches/" + matchId + "/continue?playerId=" + playerId + "&choice=" + choice;
        return toMatch(new JSONObject(request("POST", path, new JSONObject())));
    }

    // Finished matches of the player, without answers
    public List<Match> getHistory(int playerId) throws IOException, JSONException {
        JSONArray array = new JSONArray(request("GET", "/matches?playerId=" + playerId, null));
        List<Match> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            list.add(toMatch(array.getJSONObject(i)));
        }
        return list;
    }

    // Full match with problems and the answers of both players
    public Match getById(int matchId) throws IOException, JSONException {
        return toMatch(new JSONObject(request("GET", "/matches/" + matchId, null)));
    }

    // Maps the match with its problems, both players and their answers; answer i belongs to problem i
    private Match toMatch(JSONObject json) throws JSONException {
        Match match = new Match();
        match.setId(json.getInt("id"));
        match.setStatus(json.optString("status", Match.PLAYING));
        match.setStartedAt(new Date(json.getLong("startedAt")));
        match.setEndedAt(json.isNull("endedAt") ? null : new Date(json.getLong("endedAt")));

        JSONArray questions = json.getJSONArray("questions");
        for (int i = 0; i < questions.length(); i++) {
            JSONObject q = questions.getJSONObject(i);
            List<Integer> options = new ArrayList<>();
            JSONArray optionArray = q.getJSONArray("answerOptions");
            for (int j = 0; j < optionArray.length(); j++) {
                options.add(optionArray.getInt(j));
            }
            MathProblem problem = new MathProblem(q.getInt("operand1"), q.getInt("operand2"),
                    q.getString("operator"), q.getInt("correctAnswer"), options);
            problem.setId(q.getInt("id"));
            match.getQuestions().add(problem);
        }

        JSONArray players = json.getJSONArray("players");
        for (int i = 0; i < players.length(); i++) {
            JSONObject mpJson = players.getJSONObject(i);
            JSONObject p = mpJson.getJSONObject("player");
            Player player = new Player(p.getInt("id"), p.optString("username"), p.getString("displayName"));
            player.setTotalPoints(p.optDouble("totalPoints", 0));
            MatchPlayer mp = new MatchPlayer(player);
            mp.setId(mpJson.getInt("id"));
            mp.setCorrectCount(mpJson.getInt("correctCount"));
            mp.setSubmittedAt(mpJson.isNull("submittedAt") ? null : new Date(mpJson.getLong("submittedAt")));
            mp.setForfeited(mpJson.getBoolean("forfeited"));
            // History items have no answers
            JSONArray answers = mpJson.optJSONArray("answers");
            for (int j = 0; answers != null && j < answers.length(); j++) {
                JSONObject a = answers.getJSONObject(j);
                Answer answer = new Answer(a.getInt("selectedOption"), a.getLong("answerTime"));
                answer.setId(a.optInt("id"));
                if (j < match.getQuestions().size()) {
                    answer.setProblem(match.getQuestions().get(j));
                }
                mp.getAnswers().add(answer);
            }
            match.getPlayers().add(mp);
        }
        return match;
    }
}
