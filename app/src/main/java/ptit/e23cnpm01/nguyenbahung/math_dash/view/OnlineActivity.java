package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.MatchCallback;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.MatchFirebaseDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.SessionDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

public class OnlineActivity extends AppCompatActivity implements View.OnClickListener, MatchCallback {
    private Button btnCreateRoom;
    private EditText edtRoomCode;
    private Button btnJoinRoom;
    private Player me;
    private MatchFirebaseDAO matchFirebaseDAO = new MatchFirebaseDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        btnCreateRoom = findViewById(R.id.btnCreateRoom);
        edtRoomCode = findViewById(R.id.edtRoomCode);
        btnJoinRoom = findViewById(R.id.btnJoinRoom);
        btnCreateRoom.setOnClickListener(this);
        btnJoinRoom.setOnClickListener(this);
        me = new SessionDAO(this).getPlayer();
    }

    @Override
    public void onClick(View v) {
        if (v == btnCreateRoom) {
            btnCreateRoom.setEnabled(false);
            matchFirebaseDAO.createMatch(me, this);
            return;
        }
        String roomCode = edtRoomCode.getText().toString().trim();
        if (roomCode.length() != 6) {
            Toast.makeText(this, "Mã phòng gồm 6 chữ số", Toast.LENGTH_SHORT).show();
            return;
        }
        btnJoinRoom.setEnabled(false);
        matchFirebaseDAO.joinMatch(roomCode, me, this);
    }

    // Tạo hoặc vào được phòng: mở phòng chờ
    @Override
    public void onSuccess(Match match) {
        Intent intent = new Intent(this, LobbyActivity.class);
        intent.putExtra("roomCode", match.getRoomCode());
        intent.putExtra("playerId", me.getId());
        startActivity(intent);
        finish();
    }

    // Không vào được phòng: "Không tìm thấy phòng", "Phòng đã đầy"...
    @Override
    public void onFail(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        btnCreateRoom.setEnabled(true);
        btnJoinRoom.setEnabled(true);
    }
}
