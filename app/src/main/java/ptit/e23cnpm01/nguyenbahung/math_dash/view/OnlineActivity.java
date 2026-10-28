package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.ChallengeCallback;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.ChallengeFirebaseDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.MatchApiDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.PlayerApiDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.PlayerListCallback;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.PresenceFirebaseDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.SessionDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Challenge;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;
import ptit.e23cnpm01.nguyenbahung.math_dash.util.PointFormat;

// Vào màn này mới online: người khác thấy mình trong danh sách và thách đấu được
public class OnlineActivity extends AppCompatActivity
        implements View.OnClickListener, PlayerListCallback, ChallengeCallback {
    private TextView txtMyName;
    private TextView txtMyPoints;
    private TextView txtEmpty;
    private RecyclerView rvPlayers;
    private ImageButton btnFriends;
    private ImageButton btnHistory;
    private ImageButton btnRanking;
    private Player me;
    private PlayerAdapter playerAdapter;
    private AlertDialog waitingDialog;
    private Challenge sentChallenge;
    private Set<String> shownChallenges = new HashSet<>();
    private PlayerApiDAO playerApiDAO = new PlayerApiDAO();
    private PresenceFirebaseDAO presenceFirebaseDAO = new PresenceFirebaseDAO();
    private ChallengeFirebaseDAO challengeFirebaseDAO = new ChallengeFirebaseDAO();
    private MatchApiDAO matchApiDAO = new MatchApiDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        bindViews();
        me = new SessionDAO(this).getPlayer();
        txtMyName.setText(me.getDisplayName());
        presenceFirebaseDAO.watchOnlinePlayers(this);
        challengeFirebaseDAO.watchIncoming(me.getId(), this);
        goOnline();
    }

    private void bindViews() {
        txtMyName = findViewById(R.id.txtMyName);
        txtMyPoints = findViewById(R.id.txtMyPoints);
        txtEmpty = findViewById(R.id.txtEmpty);
        rvPlayers = findViewById(R.id.rvPlayers);
        btnFriends = findViewById(R.id.btnFriends);
        btnHistory = findViewById(R.id.btnHistory);
        btnRanking = findViewById(R.id.btnRanking);
        playerAdapter = new PlayerAdapter(this);
        rvPlayers.setLayoutManager(new LinearLayoutManager(this));
        rvPlayers.setAdapter(playerAdapter);
        btnFriends.setOnClickListener(this);
        btnHistory.setOnClickListener(this);
        btnRanking.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnFriends) {
            startActivity(new Intent(this, FriendActivity.class));
        } else if (v == btnHistory) {
            startActivity(new Intent(this, HistoryActivity.class));
        } else if (v == btnRanking) {
            Intent intent = new Intent(this, HistoryActivity.class);
            intent.putExtra(HistoryActivity.EXTRA_TAB, HistoryActivity.TAB_RANKING);
            startActivity(intent);
        }
    }

    // Rời màn Chơi online là offline
    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenceFirebaseDAO.stopWatching();
        challengeFirebaseDAO.stopWatching();
        presenceFirebaseDAO.goOffline(me.getId());
    }

    private void goOnline() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Player player = playerApiDAO.getById(me.getId());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            me.setTotalPoints(player.getTotalPoints());
                            presenceFirebaseDAO.goOnline(me);
                        }
                    });
                } catch (IOException | JSONException e) {
                    showToast(e.getMessage());
                }
            }
        }).start();
    }

    @Override
    public void onPlayersChanged(List<Player> players) {
        List<Player> others = new ArrayList<>();
        for (Player p : players) {
            if (p.getId() == me.getId()) {
                me.setTotalPoints(p.getTotalPoints());
                txtMyPoints.setText(getString(R.string.label_points, PointFormat.format(p.getTotalPoints())));
            } else {
                others.add(p);
            }
        }
        Collections.sort(others, new Comparator<Player>() {
            @Override
            public int compare(Player a, Player b) {
                return Double.compare(b.getTotalPoints(), a.getTotalPoints());
            }
        });
        playerAdapter.setPlayers(others);
        txtEmpty.setVisibility(others.isEmpty() ? View.VISIBLE : View.GONE);
    }

    public void onPlayerClick(Player opponent) {
        if (Player.BUSY.equals(opponent.getStatus())) {
            showToast(getString(R.string.msg_busy, opponent.getDisplayName()));
            return;
        }
        if (sentChallenge != null) {
            return;
        }
        sentChallenge = challengeFirebaseDAO.sendChallenge(me, opponent);
        challengeFirebaseDAO.watchChallenge(sentChallenge.getId(), this);
        showWaitingDialog(opponent);
    }

    @Override
    public void onChallengeChanged(Challenge challenge) {
        if (challenge.getOpponent().getId() == me.getId()) {
            if (shownChallenges.add(challenge.getId())) {
                showChallengeDialog(challenge);
            }
            return;
        }
        if (Challenge.ACCEPTED.equals(challenge.getStatus())) {
            closeSentChallenge();
            openMatch(challenge.getMatchId());
        } else if (Challenge.REJECTED.equals(challenge.getStatus())) {
            closeSentChallenge();
            showToast(getString(R.string.msg_rejected, challenge.getOpponent().getDisplayName()));
        }
    }

    private void showWaitingDialog(Player opponent) {
        waitingDialog = new AlertDialog.Builder(this)
                .setMessage(getString(R.string.msg_waiting_answer, opponent.getDisplayName()))
                .setCancelable(false)
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Hủy lời mời: gỡ theo dõi trước để không báo nhầm là bị từ chối
                        String id = sentChallenge.getId();
                        closeSentChallenge();
                        challengeFirebaseDAO.rejectChallenge(id);
                    }
                })
                .show();
    }

    private void showChallengeDialog(final Challenge challenge) {
        String message = getString(R.string.msg_challenge, challenge.getChallenger().getDisplayName(),
                PointFormat.format(challenge.getChallenger().getTotalPoints()));
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        acceptChallenge(challenge);
                    }
                })
                .setNegativeButton(R.string.btn_reject, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        challengeFirebaseDAO.rejectChallenge(challenge.getId());
                    }
                })
                .show();
    }

    private void acceptChallenge(final Challenge challenge) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Match match = matchApiDAO.createMatch(challenge);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            openMatch(match.getId());
                        }
                    });
                } catch (IOException | JSONException e) {
                    showToast(e.getMessage());
                }
            }
        }).start();
    }

    private void closeSentChallenge() {
        challengeFirebaseDAO.stopWatchingSent();
        sentChallenge = null;
        if (waitingDialog != null) {
            waitingDialog.dismiss();
            waitingDialog = null;
        }
    }

    private void openMatch(int matchId) {
        Intent intent = new Intent(this, MatchActivity.class);
        intent.putExtra(MatchActivity.EXTRA_MATCH_ID, matchId);
        startActivity(intent);
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(OnlineActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
