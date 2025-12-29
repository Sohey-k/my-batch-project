package com.example.batch;

import java.sql.Connection;

public class LoggingStep implements JobStep {

    private final String name;

    public LoggingStep(String name) {
        this.name = name;
    }

    @Override
    public StepResult execute(Connection conn) {
        System.out.println("[LoggingStep] " + name + " - 実行開始");
        // ダミー処理（ここに実処理を追加する）
        System.out.println("[LoggingStep] " + name + " - 実行終了");
        return StepResult.success("Logged step executed", 0);
    }

    @Override
    public String getName() {
        return name;
    }
}
