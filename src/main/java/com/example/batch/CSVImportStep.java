package com.example.batch;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class CSVImportStep implements JobStep {

    private final String name;
    private final Path csvPath;

    public CSVImportStep(String name, Path csvPath) {
        this.name = name;
        this.csvPath = csvPath;
    }

    @Override
    public StepResult execute(Connection conn) {
        try {
            if (!Files.exists(csvPath)) {
                // サンプル CSV を作成
                List<String> sample = List.of("id,name","1,Alice","2,Bob","3,Carol");
                Files.write(csvPath, sample, StandardCharsets.UTF_8);
            }

            List<String> lines = Files.readAllLines(csvPath, StandardCharsets.UTF_8);
            int count = Math.max(0, lines.size() - 1); // ヘッダを除いた件数

            // 例として読み込んだデータを一時テーブルに挿入する（任意）
            try (PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS imported_data (id INT, name VARCHAR(255))")) {
                ps.execute();
            }

            try (PreparedStatement del = conn.prepareStatement("DELETE FROM imported_data")) {
                del.execute();
            }

            try (PreparedStatement ins = conn.prepareStatement("INSERT INTO imported_data (id, name) VALUES (?, ?)")) {
                for (int i = 1; i < lines.size(); i++) {
                    String[] cols = lines.get(i).split(",", -1);
                    int id = Integer.parseInt(cols[0].trim());
                    String name = cols.length > 1 ? cols[1].trim() : "";
                    ins.setInt(1, id);
                    ins.setString(2, name);
                    ins.addBatch();
                }
                ins.executeBatch();
            }

            return StepResult.success("Imported " + count + " records from " + csvPath.getFileName(), count);
        } catch (IOException | SQLException e) {
            return StepResult.failure("Error importing CSV: " + e.getMessage(), 0);
        }
    }

    @Override
    public String getName() {
        return name;
    }
}
