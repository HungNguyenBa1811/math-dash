package ptit.e23cnpm01.nguyenbahung.mathdash.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Lớp cha của mọi DAO JDBC: giữ một kết nối MySQL dùng chung.
 */
public abstract class DAO {
    protected static Connection conn;

    public DAO() {
        if (conn != null) {
            return;
        }
        try {
            Properties config = loadConfig();
            conn = DriverManager.getConnection(config.getProperty("db.url"),
                    config.getProperty("db.user"), config.getProperty("db.password"));
        } catch (SQLException e) {
            throw new IllegalStateException("Không kết nối được MySQL", e);
        }
    }

    // Đọc application.properties, rồi ghi đè bằng application-local.properties nếu có (mật khẩu thật)
    public static Properties loadConfig() {
        Properties config = new Properties();
        try {
            for (String name : new String[]{"/application.properties", "/application-local.properties"}) {
                InputStream in = DAO.class.getResourceAsStream(name);
                if (in != null) {
                    config.load(in);
                    in.close();
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Không đọc được cấu hình", e);
        }
        overrideFromEnv(config);
        return config;
    }

    // Khi chạy trên Render: biến môi trường ghi đè cấu hình trong file
    private static void overrideFromEnv(Properties config) {
        String[][] keys = {{"DB_URL", "db.url"}, {"DB_USER", "db.user"}, {"DB_PASSWORD", "db.password"},
                {"FIREBASE_DATABASE_URL", "firebase.databaseUrl"}};
        for (String[] key : keys) {
            String value = System.getenv(key[0]);
            if (value != null && !value.isEmpty()) {
                config.setProperty(key[1], value);
            }
        }
    }
}
