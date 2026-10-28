package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.IOException;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.PlayerApiDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.PlayerDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.SessionDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

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
        Player saved = sessionDAO.getPlayer();
        if (saved != null) {
            openHome(saved);
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

    private void login(final String username, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Player player = new PlayerApiDAO().login(username, password);
                    sessionDAO.save(player);
                    new PlayerDAO(LoginActivity.this).saveOrUpdate(player);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            openHome(player);
                        }
                    });
                } catch (IOException e) {
                    showError(e.getMessage());
                } catch (JSONException e) {
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

    private void openHome(Player player) {
        Class<?> home = Player.ROLE_ADMIN.equals(player.getRole()) ? AdminActivity.class : MainActivity.class;
        startActivity(new Intent(this, home));
        finish();
    }
}
