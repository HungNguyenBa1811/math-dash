package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.IOException;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.PlayerApiDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;
import ptit.e23cnpm01.nguyenbahung.math_dash.util.PointFormat;

/**
 * Thông tin một người chơi: sửa tên hiển thị, đặt lại mật khẩu, xóa tài khoản (Hoàng, M2-07).
 * Mọi lời gọi server chạy trong Thread, kết quả báo trên luồng giao diện.
 */
public class PlayerDetailActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_PLAYER_ID = "playerId";

    private TextView txtUsername;
    private EditText edtDisplayName;
    private TextView txtPoints;
    private TextView txtResults;
    private Button btnSave;
    private Button btnResetPassword;
    private Button btnDelete;
    private Player player;
    private PlayerApiDAO playerApiDAO = new PlayerApiDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_detail);
        txtUsername = findViewById(R.id.txtUsername);
        edtDisplayName = findViewById(R.id.edtDisplayName);
        txtPoints = findViewById(R.id.txtPoints);
        txtResults = findViewById(R.id.txtResults);
        btnSave = findViewById(R.id.btnSave);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnDelete = findViewById(R.id.btnDelete);
        btnSave.setOnClickListener(this);
        btnResetPassword.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        loadPlayer(getIntent().getIntExtra(EXTRA_PLAYER_ID, 0));
    }

    @Override
    public void onClick(View v) {
        if (player == null) {
            return; // đang tải thông tin, chưa cho thao tác
        }
        if (v == btnSave) {
            save();
        } else if (v == btnResetPassword) {
            showResetPasswordDialog();
        } else if (v == btnDelete) {
            confirmDelete();
        }
    }

    // ---------- Xem thông tin ----------
    // Lấy người chơi kèm tổng điểm, số trận thắng / hòa / thua; lỗi thì báo và quay về danh sách
    private void loadPlayer(final int id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Player loaded = playerApiDAO.getById(id);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            player = loaded;
                            showPlayer();
                        }
                    });
                } catch (IOException | JSONException e) {
                    closeWith(e.getMessage());
                }
            }
        }).start();
    }

    private void showPlayer() {
        txtUsername.setText(getString(R.string.label_username, player.getUsername()));
        edtDisplayName.setText(player.getDisplayName());
        txtPoints.setText(getString(R.string.label_total_points, PointFormat.format(player.getTotalPoints())));
        txtResults.setText(getString(R.string.label_results,
                player.getWinCount(), player.getDrawCount(), player.getLoseCount()));
    }

    // ---------- Sửa tên hiển thị ----------
    // Tên bỏ trống thì báo lỗi; lưu xong báo "Đã lưu" và quay về danh sách (danh sách tự tải lại)
    private void save() {
        final String displayName = edtDisplayName.getText().toString().trim();
        if (displayName.isEmpty()) {
            showToast(getString(R.string.msg_display_name_empty));
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    player.setDisplayName(displayName);
                    playerApiDAO.update(player.getId(), player.getDisplayName());
                    closeWith(getString(R.string.msg_saved));
                } catch (IOException | JSONException e) {
                    showToast(e.getMessage());
                }
            }
        }).start();
    }

    // ---------- Đặt lại mật khẩu ----------
    // Hộp thoại có ô mật khẩu mới và ô nhập lại; kiểm tra 2 ô rồi mới gọi server
    private void showResetPasswordDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_reset_password, null);
        final EditText edtNewPassword = view.findViewById(R.id.edtNewPassword);
        final EditText edtConfirmPassword = view.findViewById(R.id.edtConfirmPassword);
        new AlertDialog.Builder(this)
                .setTitle(R.string.btn_reset_password)
                .setView(view)
                .setNegativeButton(R.string.btn_cancel, null)
                .setPositiveButton(R.string.btn_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = edtNewPassword.getText().toString();
                        if (password.isEmpty()) {
                            showToast(getString(R.string.msg_password_empty));
                        } else if (!password.equals(edtConfirmPassword.getText().toString())) {
                            showToast(getString(R.string.msg_password_mismatch));
                        } else {
                            resetPassword(password);
                        }
                    }
                })
                .show();
    }

    private void resetPassword(final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    playerApiDAO.resetPassword(player.getId(), password);
                    showToast(getString(R.string.msg_password_reset));
                } catch (IOException | JSONException e) {
                    showToast(e.getMessage());
                }
            }
        }).start();
    }

    // ---------- Xóa tài khoản ----------
    // Hỏi xác nhận trước khi xóa
    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.msg_confirm_delete, player.getUsername()))
                .setNegativeButton(R.string.btn_cancel, null)
                .setPositiveButton(R.string.btn_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete();
                    }
                })
                .show();
    }

    // Người chơi đã có trận thì server trả 409, hiện câu "Không xóa được người chơi đã có trận đấu"
    private void delete() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    playerApiDAO.delete(player.getId());
                    closeWith(getString(R.string.msg_deleted));
                } catch (IOException | JSONException e) {
                    showToast(e.getMessage());
                }
            }
        }).start();
    }

    // Báo câu thông báo rồi đóng màn này, quay về danh sách; chạy trên luồng giao diện
    private void closeWith(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PlayerDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PlayerDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
