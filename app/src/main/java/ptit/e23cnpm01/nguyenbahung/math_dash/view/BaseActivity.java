package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import ptit.e23cnpm01.nguyenbahung.math_dash.dao.SessionDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

// Lớp cha của các màn sau đăng nhập: giữ người chơi đang đăng nhập, nhận sự kiện bấm nút
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    protected Player me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        me = new SessionDAO(this).getPlayer();
    }

    @Override
    public void onClick(View v) {
    }
}
