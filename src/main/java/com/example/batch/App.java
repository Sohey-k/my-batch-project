package com.example.batch;

import java.nio.file.Path;
import java.util.List;

public class App {
    public static void main(String[] args) {
        System.out.println("バッチ処理のテスト実行");

        // DB 初期化
        DatabaseManager.createJobExecutionTable();

        // ジョブのステップを構築
        CSVImportStep csvStep = new CSVImportStep("CSVインポート", Path.of("data/sample.csv"));

        JobRunner runner = new JobRunner("sample-job");
        runner.run(List.of(csvStep));

        System.out.println("バッチ処理が完了しました。");
    }
}
