package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.ApiDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.FirebaseDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.PlayerApiDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.PlayerDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.SessionDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

/**
 * Màn Đăng nhập (màn mở đầu). Đã có phiên từ lần trước thì vào thẳng màn Chính.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtUsername;
    private EditText edtPassword;
    private Button btnLogin;
    private Button btnRegister;
    private SessionDAO sessionDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionDAO = new SessionDAO(this);
        if (sessionDAO.getPlayer() != null) {
            ApiDAO.setToken(sessionDAO.getToken());
            openMain();
            return;
        }
        setContentView(R.layout.activity_login);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnRegister) {
            startActivity(new Intent(this, RegisterActivity.class));
            return;
        }
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString();
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên đăng nhập và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }
        btnLogin.setEnabled(false);
        login(username, password);
    }

    // Gọi server trong Thread: kiểm tra tài khoản, đăng nhập Firebase, ghi nhớ phiên, lưu bản sao người chơi
    private void login(final String username, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PlayerApiDAO playerApiDAO = new PlayerApiDAO();
                    Player player = playerApiDAO.login(username, password);
                    FirebaseDAO.signIn(playerApiDAO.getFirebaseToken());
                    sessionDAO.save(ApiDAO.getToken(), player);
                    new PlayerDAO(LoginActivity.this).saveOrUpdate(player);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            openMain();
                        }
                    });
                } catch (IOException e) {
                    showError(e.getMessage());
                } catch (Exception e) {
                    showError("Đăng nhập thất bại, hãy thử lại");
                }
            }
        }).start();
    }

    private void showError(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
