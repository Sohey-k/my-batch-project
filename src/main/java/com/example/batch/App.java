package com.example.batch;

import java.util.List;

public class App {
    public static void main(String[] args) {
        System.out.println("バッチ処理のテスト実行");

        // DB 初期化
        DatabaseManager.createJobExecutionTable();

        // ジョブのステップを構築（最初はダミーでログ出力するステップ）
        LoggingStep dummy = new LoggingStep("ダミーステップ");

        JobRunner runner = new JobRunner("sample-job");
        runner.run(List.of(dummy));

        System.out.println("バッチ処理が完了しました。");
    }
}
