package com.example.batch;

import java.sql.Connection;

public interface JobStep {
    // 実行時に DB コネクションを渡す（トランザクションは Job 単位で JobRunner が管理）
    StepResult execute(Connection conn);
    String getName();
}
