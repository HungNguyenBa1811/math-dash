package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public abstract class ApiDAO {
    protected static final String BASE_URL = "https://math-dash-server.onrender.com/api"; // server trên Render; chạy local: "http://10.0.2.2:8080/api"
    protected static final String NETWORK_ERROR = "Không kết nối được máy chủ, kiểm tra kết nối mạng";

    protected JSONObject post(String path, JSONObject body) throws IOException, JSONException {
        return send(path, "POST", body);
    }

    // PUT kèm body JSON (UC06: sửa tên hiển thị, đặt lại mật khẩu)
    protected JSONObject put(String path, JSONObject body) throws IOException, JSONException {
        return send(path, "PUT", body);
    }

    protected JSONObject get(String path) throws IOException, JSONException {
        return new JSONObject(request(path, "GET"));
    }

    protected JSONArray getArray(String path) throws IOException, JSONException {
        return new JSONArray(request(path, "GET"));
    }

    // DELETE không có body (UC06: xóa tài khoản); mã lỗi thì ném IOException như các hàm khác
    protected void delete(String path) throws IOException, JSONException {
        request(path, "DELETE");
    }

    // Gửi body JSON bằng POST / PUT; server trả rỗng thì coi như đối tượng rỗng
    private JSONObject send(String path, String method, JSONObject body) throws IOException, JSONException {
        HttpURLConnection conn = open(path, method);
        int code;
        try {
            conn.setDoOutput(true);
            OutputStream out = conn.getOutputStream();
            out.write(body.toString().getBytes(StandardCharsets.UTF_8));
            out.close();
            code = conn.getResponseCode();
        } catch (IOException e) {
            throw new IOException(NETWORK_ERROR, e);
        }
        String text = read(conn, code);
        return text.isEmpty() ? new JSONObject() : new JSONObject(text);
    }

    // Gọi GET / DELETE không có body, trả nội dung dạng chuỗi
    private String request(String path, String method) throws IOException, JSONException {
        HttpURLConnection conn = open(path, method);
        int code;
        try {
            code = conn.getResponseCode();
        } catch (IOException e) {
            throw new IOException(NETWORK_ERROR, e);
        }
        return read(conn, code);
    }

    private HttpURLConnection open(String path, String method) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + path).openConnection();
        conn.setRequestMethod(method);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(10000);
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        return conn;
    }

    // Đọc nội dung trả về; mã lỗi (>= 400) thì ném IOException mang câu thông báo của server
    private String read(HttpURLConnection conn, int code) throws IOException, JSONException {
        InputStream in = code < 400 ? conn.getInputStream() : conn.getErrorStream();
        StringBuilder text = new StringBuilder();
        if (in != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line);
            }
            reader.close();
        }
        conn.disconnect();
        if (code >= 400) {
            throw new IOException(new JSONObject(text.toString()).optString("message", NETWORK_ERROR));
        }
        return text.toString();
    }
}
