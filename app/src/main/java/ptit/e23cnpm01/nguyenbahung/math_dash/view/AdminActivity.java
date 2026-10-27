package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.PlayerApiDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.SessionDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

/**
 * Màn quản lý người chơi của quản trị viên: tìm theo tên, danh sách người chơi (Hoàng, M2-07).
 */
public class AdminActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtSearch;
    private Button btnSearch;
    private RecyclerView rvPlayers;
    private Button btnLogout;
    private AdminPlayerAdapter adminPlayerAdapter;
    private PlayerApiDAO playerApiDAO = new PlayerApiDAO();
    private SessionDAO sessionDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        edtSearch = findViewById(R.id.edtSearch);
        btnSearch = findViewById(R.id.btnSearch);
        rvPlayers = findViewById(R.id.rvPlayers);
        btnLogout = findViewById(R.id.btnLogout);
        sessionDAO = new SessionDAO(this);
        adminPlayerAdapter = new AdminPlayerAdapter(this);
        rvPlayers.setLayoutManager(new LinearLayoutManager(this));
        rvPlayers.setAdapter(adminPlayerAdapter);
        btnSearch.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    // Mỗi lần quay lại màn này (mở lần đầu, sau khi sửa / xóa) thì tải lại danh sách theo ô tìm
    @Override
    protected void onResume() {
        super.onResume();
        loadPlayers(edtSearch.getText().toString().trim());
    }

    @Override
    public void onClick(View v) {
        if (v == btnSearch) {
            loadPlayers(edtSearch.getText().toString().trim());
        } else if (v == btnLogout) {
            logout();
        }
    }

    // Gọi server trong Thread lấy người chơi có tên hiển thị chứa name; name rỗng thì lấy tất cả
    private void loadPlayers(final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<Player> players = playerApiDAO.search(name);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adminPlayerAdapter.setPlayers(players);
                            if (players.isEmpty()) {
                                Toast.makeText(AdminActivity.this, R.string.msg_no_players, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (IOException | JSONException e) {
                    showError(e.getMessage());
                }
            }
        }).start();
    }

    // Bấm một dòng: mở màn thông tin của người chơi đó
    public void onPlayerClick(Player player) {
        Intent intent = new Intent(this, PlayerDetailActivity.class);
        intent.putExtra(PlayerDetailActivity.EXTRA_PLAYER_ID, player.getId());
        startActivity(intent);
    }

    // Đăng xuất: xóa phiên, quay về màn Đăng nhập
    private void logout() {
        sessionDAO.clear();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void showError(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AdminActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
