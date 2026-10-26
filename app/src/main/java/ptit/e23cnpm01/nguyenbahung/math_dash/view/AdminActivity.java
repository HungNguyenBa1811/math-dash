package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.SessionDAO;

/**
 * Màn quản lý người chơi của quản trị viên: tìm theo tên, danh sách người chơi (Hoàng, M2-07).
 */
public class AdminActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
        // TODO M2-07: ô tìm theo tên, danh sách người chơi
    }

    @Override
    public void onClick(View v) {
        if (v == btnLogout) {
            logout();
        }
    }

    // Đăng xuất: xóa phiên, quay về màn Đăng nhập
    private void logout() {
        new SessionDAO(this).clear();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
