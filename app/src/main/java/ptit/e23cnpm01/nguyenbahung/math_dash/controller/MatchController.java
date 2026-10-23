package ptit.e23cnpm01.nguyenbahung.math_dash.controller;

import android.app.Activity;
import android.os.CountDownTimer;
import android.os.SystemClock;

import org.json.JSONException;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;

import ptit.e23cnpm01.nguyenbahung.math_dash.dao.MatchApiDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.MatchCallback;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.MatchFirebaseDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.ProblemDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.GameSession;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MathProblem;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

/**
 * Điều phối cả trận online (Singleton): vào phòng, chấm điểm, đồng hồ 60 giây, kết thúc trận.
 * Activity không gọi Firebase hay REST trực tiếp mà đi qua lớp này.
 */
public class MatchController {
    private static final long GAME_MILLIS = 60000;
    private static final int ONLINE_DIFFICULTY = 1;
    private static final int FINISH_ATTEMPTS = 3;

    private static MatchController instance;

    private MatchFirebaseDAO matchFirebaseDAO = new MatchFirebaseDAO();
    private MatchApiDAO matchApiDAO = new MatchApiDAO();
    private ProblemDAO problemDAO;
    private GameSession session;
    private Match match;
    private Player me;
    private CountDownTimer timer;
    private boolean finishing;
    private GameListener gameListener;

    private MatchController() {
    }

    public static MatchController getInstance() {
        if (instance == null) {
            instance = new MatchController();
        }
        return instance;
    }

    // Tạo phòng: sinh mã phòng và questionSeed, người tạo là người chơi đầu tiên, trận ở trạng thái chờ
    public void createRoom(Player me, final RoomListener listener) {
        this.me = me;
        Match newMatch = new Match();
        newMatch.setRoomCode(generateRoomCode());
        newMatch.setStatus(Match.WAITING);
        newMatch.setQuestionSeed(new Random().nextLong());
        newMatch.getPlayers().add(new MatchPlayer(me));
        matchFirebaseDAO.createMatch(newMatch, new MatchCallback() {
            @Override
            public void onSuccess(Match created) {
                match = created;
                listener.onRoomReady(created);
            }

            @Override
            public void onFail(String message) {
                listener.onError(message);
            }
        });
    }

    // Tìm phòng theo mã do bạn gửi
    public void findRoom(String roomCode, Player me, final RoomListener listener) {
        this.me = me;
        matchFirebaseDAO.joinMatch(roomCode, me, new MatchCallback() {
            @Override
            public void onSuccess(Match joined) {
                match = joined;
                listener.onRoomReady(joined);
            }

            @Override
            public void onFail(String message) {
                listener.onError(message);
            }
        });
    }

    // Chờ đối thủ: trận đã bắt đầu (người tìm phòng) thì báo ngay, chưa thì theo dõi đến khi có người vào
    public void waitForOpponent(final RoomListener listener) {
        if (Match.PLAYING.equals(match.getStatus())) {
            listener.onRoomReady(match);
            return;
        }
        matchFirebaseDAO.watchMatch(match.getRoomCode(), new MatchCallback() {
            @Override
            public void onSuccess(Match changed) {
                match = changed;
                if (Match.PLAYING.equals(changed.getStatus())) {
                    matchFirebaseDAO.stopWatching(changed.getRoomCode());
                    listener.onRoomReady(changed);
                }
            }

            @Override
            public void onFail(String message) {
                listener.onError(message);
            }
        });
    }

    // Người tạo phòng hủy khi chưa có đối thủ: xóa phòng
    public void cancelRoom() {
        matchFirebaseDAO.stopWatching(match.getRoomCode());
        matchFirebaseDAO.deleteMatch(match.getRoomCode());
        match = null;
    }

    // Bắt đầu thi đấu: 2 máy cùng questionSeed nên cùng dãy câu hỏi; trả câu hỏi đầu tiên
    public MathProblem startGame(final GameListener listener) {
        gameListener = listener;
        finishing = false;
        matchFirebaseDAO.registerDisconnect(match.getRoomCode(), me.getId());
        problemDAO = new ProblemDAO(new Random(match.getQuestionSeed()));
        session = new GameSession(me, ONLINE_DIFFICULTY, true);
        matchFirebaseDAO.watchMatch(match.getRoomCode(), new MatchCallback() {
            @Override
            public void onSuccess(Match changed) {
                match = changed;
                if (changed.isOver()) {
                    finishMatch();
                } else {
                    listener.onMatchChanged(changed);
                }
            }

            @Override
            public void onFail(String message) {
                if (finishing) {
                    return;
                }
                finishing = true;
                timer.cancel();
                matchFirebaseDAO.stopWatching(match.getRoomCode());
                listener.onError(message);
            }
        });
        timer = createTimer();
        timer.start();
        return nextProblem();
    }

    // Chấm đáp án bằng MathProblem + GameSession; đúng thì ghi điểm lên Firebase. Trả số điểm được cộng
    public int submitAnswer(int value) {
        if (session.getCurrentProblem().isCorrect(value)) {
            int points = session.recordCorrectAnswer();
            matchFirebaseDAO.updateScore(match.getRoomCode(), me.getId(), session.getScore());
            return points;
        }
        session.recordWrongAnswer();
        return 0;
    }

    // Sinh câu tiếp theo theo mức hiện tại và lưu làm câu hiện tại
    public MathProblem nextProblem() {
        int level = session.getCurrentLevel();
        MathProblem problem = problemDAO.generateProblem(level);
        session.setCurrentProblem(problem);
        return problem;
    }

    // Người chơi thoát giữa trận: bị xử thua; Firebase báo trận đã xong nên finishMatch được gọi tiếp
    public void leaveMatch() {
        timer.cancel();
        matchFirebaseDAO.forfeit(match.getRoomCode(), me.getId());
    }

    public GameSession getSession() {
        return session;
    }

    // Trận đã xong: gọi server kết thúc trận trong Thread, chỉ gọi một lần nhờ cờ finishing
    private void finishMatch() {
        if (finishing) {
            return;
        }
        finishing = true;
        timer.cancel();
        final String roomCode = match.getRoomCode();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Match saved = null;
                // Lần ghi timeUp / forfeited của máy này có thể chưa tới Firebase khi server đọc, nên thử lại
                for (int attempt = 0; saved == null && attempt < FINISH_ATTEMPTS; attempt++) {
                    try {
                        saved = matchApiDAO.finishMatch(roomCode);
                    } catch (IOException | JSONException e) {
                        SystemClock.sleep(1000);
                    }
                }
                matchFirebaseDAO.stopWatching(roomCode);
                showMatchEnd(saved);
            }
        }).start();
    }

    // Về luồng giao diện để báo kết quả; gameListener chính là OnlineGameActivity
    private void showMatchEnd(final Match saved) {
        ((Activity) gameListener).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (saved == null) {
                    gameListener.onError("Không lưu được kết quả trận, kiểm tra kết nối mạng");
                } else {
                    gameListener.onMatchEnd(saved);
                }
            }
        });
    }

    // Đồng hồ 60 giây: mỗi giây báo thời gian còn lại; hết giờ thì ghi timeUp lên Firebase
    private CountDownTimer createTimer() {
        return new CountDownTimer(GAME_MILLIS, 1000) {
            @Override
            public void onTick(long millisLeft) {
                gameListener.onTick((int) ((millisLeft + 999) / 1000));
            }

            @Override
            public void onFinish() {
                session.setGameOver(true);
                matchFirebaseDAO.setTimeUp(match.getRoomCode(), me.getId());
                gameListener.onTimeUp();
            }
        };
    }

    // Mã phòng 6 chữ số
    private String generateRoomCode() {
        return String.format(Locale.US, "%06d", new Random().nextInt(1000000));
    }
}
