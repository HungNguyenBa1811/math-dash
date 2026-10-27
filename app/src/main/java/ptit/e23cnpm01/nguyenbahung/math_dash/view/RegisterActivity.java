package ptit.e23cnpm01.nguyenbahung.math_dash.view;

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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtUsername;
    private EditText edtPassword;
    private EditText edtConfirmPassword;
    private EditText edtDisplayName;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtDisplayName = findViewById(R.id.edtDisplayName);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString();
        String displayName = edtDisplayName.getText().toString().trim();
        if (username.isEmpty() || password.isEmpty() || displayName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(edtConfirmPassword.getText().toString())) {
            Toast.makeText(this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
            return;
        }
        btnRegister.setEnabled(false);
        register(username, password, displayName);
    }

    private void register(final String username, final String password, final String displayName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message = "Đăng ký thành công";
                boolean success = true;
                try {
                    new PlayerApiDAO().register(username, password, displayName);
                } catch (IOException e) {
                    message = e.getMessage();
                    success = false;
                } catch (JSONException e) {
                    message = "Đăng ký thất bại, hãy thử lại";
                    success = false;
                }
                showResult(message, success);
            }
        }).start();
    }

    private void showResult(final String message, final boolean success) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                btnRegister.setEnabled(true);
                if (success) {
                    finish();
                }
            }
        });
    }
}
