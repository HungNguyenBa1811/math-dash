package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.FriendshipApiDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.PlayerListCallback;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.PresenceFirebaseDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.SessionDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Friendship;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

public class FriendActivity extends AppCompatActivity implements View.OnClickListener, PlayerListCallback {
    private EditText edtUsername;
    private Button btnAddFriend;
    private TextView txtRequestsTitle;
    private RecyclerView rvRequests;
    private TextView txtFriendsTitle;
    private TextView txtEmpty;
    private RecyclerView rvFriends;

    private Player me;
    private List<Friendship> requests = new ArrayList<>();
    private List<Friendship> friends = new ArrayList<>();
    private Set<Integer> onlineIds = new HashSet<>();
    private FriendRequestAdapter requestAdapter;
    private FriendAdapter friendAdapter;
    private SessionDAO sessionDAO;
    private FriendshipApiDAO friendshipApiDAO = new FriendshipApiDAO();
    private PresenceFirebaseDAO presenceFirebaseDAO = new PresenceFirebaseDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        sessionDAO = new SessionDAO(this);
        me = sessionDAO.getPlayer();
        bindViews();
        presenceFirebaseDAO.watchOnlinePlayers(this);
        loadFriendships();
    }

    private void bindViews() {
        edtUsername = findViewById(R.id.edtUsername);
        btnAddFriend = findViewById(R.id.btnAddFriend);
        txtRequestsTitle = findViewById(R.id.txtRequestsTitle);
        rvRequests = findViewById(R.id.rvRequests);
        txtFriendsTitle = findViewById(R.id.txtFriendsTitle);
        txtEmpty = findViewById(R.id.txtEmpty);
        rvFriends = findViewById(R.id.rvFriends);
        requestAdapter = new FriendRequestAdapter(this);
        friendAdapter = new FriendAdapter(this, me.getId());
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        rvRequests.setAdapter(requestAdapter);
        rvFriends.setLayoutManager(new LinearLayoutManager(this));
        rvFriends.setAdapter(friendAdapter);
        btnAddFriend.setOnClickListener(this);
        refreshTitles();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenceFirebaseDAO.stopWatching();
    }

    @Override
    public void onClick(View v) {
        if (v != btnAddFriend) {
            return;
        }
        final String username = edtUsername.getText().toString().trim();
        if (username.isEmpty()) {
            showToast(getString(R.string.friend_msg_empty_username));
            return;
        }
        btnAddFriend.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Friendship friendship = friendshipApiDAO.sendRequest(me.getId(), username);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            friends.add(friendship);
                            friendAdapter.setFriendships(friends);
                            refreshTitles();
                            edtUsername.setText("");
                            btnAddFriend.setEnabled(true);
                            showToast(getString(R.string.friend_msg_sent, friendship.getReceiver().getDisplayName()));
                        }
                    });
                } catch (IOException | JSONException e) {
                    enableAddButton();
                    showToast(e.getMessage());
                }
            }
        }).start();
    }

    private void loadFriendships() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<Friendship> list = friendshipApiDAO.getFriendships(me.getId());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showFriendships(list);
                        }
                    });
                } catch (IOException | JSONException e) {
                    showToast(e.getMessage());
                }
            }
        }).start();
    }

    // Lời mời mình đã gửi nằm cuối danh sách bạn bè
    private void showFriendships(List<Friendship> list) {
        requests = new ArrayList<>();
        friends = new ArrayList<>();
        List<Friendship> sent = new ArrayList<>();
        for (Friendship f : list) {
            if (f.canAccept(me.getId())) {
                requests.add(f);
            } else if (Friendship.ACCEPTED.equals(f.getStatus())) {
                friends.add(f);
            } else {
                sent.add(f);
            }
        }
        friends.addAll(sent);
        requestAdapter.setFriendships(requests);
        friendAdapter.setFriendships(friends);
        refreshTitles();
    }

    @Override
    public void onPlayersChanged(List<Player> players) {
        onlineIds = new HashSet<>();
        for (Player p : players) {
            onlineIds.add(p.getId());
        }
        friendAdapter.setOnlineIds(onlineIds);
    }

    public void onAccept(final Friendship friendship) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Friendship accepted = friendshipApiDAO.accept(friendship.getId(), me.getId());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            requests.remove(friendship);
                            requestAdapter.setFriendships(requests);
                            friends.add(0, accepted);
                            friendAdapter.setFriendships(friends);
                            refreshTitles();
                            showToast(getString(R.string.friend_msg_accepted, accepted.getSender().getDisplayName()));
                        }
                    });
                } catch (IOException | JSONException e) {
                    showToast(e.getMessage());
                    loadFriendships();
                }
            }
        }).start();
    }

    public void onReject(final Friendship friendship) {
        deleteFriendship(friendship);
    }

    public void onUnfriend(final Friendship friendship) {
        String name = friendship.getFriend(me.getId()).getDisplayName();
        boolean pending = Friendship.PENDING.equals(friendship.getStatus());
        new AlertDialog.Builder(this)
                .setMessage(getString(pending ? R.string.friend_confirm_cancel : R.string.friend_confirm_unfriend, name))
                .setPositiveButton(pending ? R.string.friend_cancel_request : R.string.friend_unfriend,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteFriendship(friendship);
                            }
                        })
                .setNegativeButton(R.string.friend_no, null)
                .show();
    }

    private void deleteFriendship(final Friendship friendship) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    friendshipApiDAO.delete(friendship.getId(), me.getId());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            requests.remove(friendship);
                            friends.remove(friendship);
                            requestAdapter.setFriendships(requests);
                            friendAdapter.setFriendships(friends);
                            refreshTitles();
                        }
                    });
                } catch (IOException | JSONException e) {
                    showToast(e.getMessage());
                }
            }
        }).start();
    }

    private void refreshTitles() {
        int requestCount = requests.size();
        txtRequestsTitle.setText(getString(R.string.friend_requests_title, requestCount));
        txtRequestsTitle.setVisibility(requestCount > 0 ? View.VISIBLE : View.GONE);
        rvRequests.setVisibility(requestCount > 0 ? View.VISIBLE : View.GONE);
        int friendCount = 0;
        for (Friendship f : friends) {
            if (Friendship.ACCEPTED.equals(f.getStatus())) {
                friendCount++;
            }
        }
        txtFriendsTitle.setText(getString(R.string.friend_friends_title, friendCount));
        txtEmpty.setVisibility(friends.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void enableAddButton() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnAddFriend.setEnabled(true);
            }
        });
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FriendActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
