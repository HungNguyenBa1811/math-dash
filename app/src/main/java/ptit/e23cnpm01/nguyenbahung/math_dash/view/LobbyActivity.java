package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.controller.MatchController;
import ptit.e23cnpm01.nguyenbahung.math_dash.controller.RoomListener;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MatchPlayer;

/**
 * Màn Phòng chờ: hiện mã phòng, chờ đối thủ; có đối thủ thì sau 2 giây vào màn thi đấu.
 */
public class LobbyActivity extends AppCompatActivity implements View.OnClickListener, RoomListener {
    private static final long START_DELAY_MILLIS = 2000;

    private TextView txtRoomCode;
    private TextView txtStatus;
    private TextView txtOpponentName;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        txtRoomCode = findViewById(R.id.txtRoomCode);
        txtStatus = findViewById(R.id.txtStatus);
        txtOpponentName = findViewById(R.id.txtOpponentName);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        txtRoomCode.setText(getIntent().getStringExtra("roomCode"));
        MatchController.getInstance().waitForOpponent(this);
    }

    // Có đối thủ: hiện tên đối thủ, ẩn nút Hủy, sau 2 giây mở màn thi đấu
    @Override
    public void onRoomReady(Match match) {
        int myId = getIntent().getIntExtra("playerId", 0);
        for (MatchPlayer mp : match.getPlayers()) {
            if (mp.getPlayer().getId() != myId) {
                txtOpponentName.setText("Đối thủ: " + mp.getPlayer().getDisplayName());
            }
        }
        txtStatus.setText("Trận đấu bắt đầu!");
        btnCancel.setVisibility(View.GONE);
        txtStatus.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LobbyActivity.this, OnlineGameActivity.class));
                finish();
            }
        }, START_DELAY_MILLIS);
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

    // Hủy phòng khi chưa có đối thủ, quay về màn Chính
    @Override
    public void onClick(View v) {
        MatchController.getInstance().cancelRoom();
        finish();
    }

    // Nhấn Back khi đang chờ cũng là hủy phòng; đã có đối thủ thì chờ vào trận
    @Override
    public void onBackPressed() {
        if (btnCancel.getVisibility() == View.VISIBLE) {
            onClick(btnCancel);
        }
    }
}
