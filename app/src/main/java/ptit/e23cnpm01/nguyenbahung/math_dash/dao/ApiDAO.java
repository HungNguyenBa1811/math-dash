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
    protected static final String BASE_URL = "https://math-dash-server.onrender.com/api"; // local: http://10.0.2.2:8080/api
    protected static final String NETWORK_ERROR = "Không kết nối được máy chủ, kiểm tra kết nối mạng";

    protected JSONObject post(String path, JSONObject body) throws IOException, JSONException {
        HttpURLConnection conn = open(path, "POST");
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

    protected void delete(String path) throws IOException, JSONException {
        HttpURLConnection conn = open(path, "DELETE");
        int code;
        try {
            code = conn.getResponseCode();
        } catch (IOException e) {
            throw new IOException(NETWORK_ERROR, e);
        }
        read(conn, code);
    }

    protected JSONObject get(String path) throws IOException, JSONException {
        return new JSONObject(readGet(path));
    }

    protected JSONArray getArray(String path) throws IOException, JSONException {
        return new JSONArray(readGet(path));
    }

    private String readGet(String path) throws IOException, JSONException {
        HttpURLConnection conn = open(path, "GET");
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

    // Lỗi HTTP: ném IOException kèm thông báo của server
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
