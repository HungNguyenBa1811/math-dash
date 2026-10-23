package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.controller.MatchController;
import ptit.e23cnpm01.nguyenbahung.math_dash.controller.RoomListener;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.SessionDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

/**
 * Màn Chơi online: Tạo phòng, hoặc nhập mã phòng để vào phòng.
 */
public class OnlineActivity extends AppCompatActivity implements View.OnClickListener, RoomListener {
    private Button btnCreateRoom;
    private EditText edtRoomCode;
    private Button btnJoinRoom;
    private Player me;

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
            MatchController.getInstance().createRoom(me, this);
            return;
        }
        String roomCode = edtRoomCode.getText().toString().trim();
        if (roomCode.length() != 6) {
            Toast.makeText(this, "Mã phòng gồm 6 chữ số", Toast.LENGTH_SHORT).show();
            return;
        }
        btnJoinRoom.setEnabled(false);
        MatchController.getInstance().findRoom(roomCode, me, this);
    }

    // Vào được phòng: mở phòng chờ
    @Override
    public void onRoomReady(Match match) {
        Intent intent = new Intent(this, LobbyActivity.class);
        intent.putExtra("roomCode", match.getRoomCode());
        intent.putExtra("playerId", me.getId());
        startActivity(intent);
        finish();
    }

    // Không vào được phòng: "Không tìm thấy phòng", "Phòng đã đầy"...
    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        btnCreateRoom.setEnabled(true);
        btnJoinRoom.setEnabled(true);
    }
}
