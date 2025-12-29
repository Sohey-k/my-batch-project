package com.example.batch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String JDBC_URL = "jdbc:h2:./batchdb;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    static {
        try {
            // H2 Driver をロード
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // データベース接続を取得
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    // テーブル作成（初回のみ）
    public static void createJobExecutionTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS job_execution (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    job_name VARCHAR(255),
                    status VARCHAR(20),
                    start_time TIMESTAMP,
                    end_time TIMESTAMP,
                    message CLOB,
                    record_count INT
                );
                """;

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("job_execution テーブルを作成しました。");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
