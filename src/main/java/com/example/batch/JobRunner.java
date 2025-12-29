package com.example.batch;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

public class JobRunner {

    private final String jobName;

    public JobRunner(String jobName) {
        this.jobName = jobName;
    }

    public void run(List<JobStep> steps) {
        Timestamp start = Timestamp.from(Instant.now());
        Timestamp end = null;
        StringBuilder messages = new StringBuilder();
        int totalRecords = 0;
        StepResult.Status finalStatus = StepResult.Status.SUCCESS;

        try (Connection conn = DatabaseManager.getConnection()) {
            try {
                conn.setAutoCommit(false);

                for (JobStep step : steps) {
                    StepResult res = step.execute(conn);
                    messages.append("[").append(step.getName()).append("] ")
                            .append(res.getStatus()).append(": ")
                            .append(res.getMessage()).append("; ");
                    totalRecords += res.getRecordCount();
                    if (res.getStatus() == StepResult.Status.FAILURE) {
                        finalStatus = StepResult.Status.FAILURE;
                        break; // 失敗したらジョブ内で打ち切る
                    }
                }

                end = Timestamp.from(Instant.now());

                // トランザクション制御: ジョブ単位で commit/rollback を行う
                if (finalStatus == StepResult.Status.SUCCESS) {
                    conn.commit();
                } else {
                    conn.rollback();
                }

                // 監査（job_execution）はジョブのトランザクションとは別に必ず永続化したいため、
                // 別の Connection（autoCommit=true）を開いて挿入する。
                // これにより、ジョブの rollback によって監査レコードが消えることを防ぐ。
                try (Connection auditConn = DatabaseManager.getConnection()) {
                    auditConn.setAutoCommit(true);
                    DatabaseManager.insertJobExecution(auditConn, jobName, finalStatus.name(), start, end,
                            messages.toString(), totalRecords);
                } catch (SQLException ae) {
                    // 監査挿入に失敗した場合もジョブの状態は変えられないため、ログに出力する
                    ae.printStackTrace();
                }
            } catch (Exception e) {
                conn.rollback();
                messages.append("Exception: ").append(e.getMessage());
                end = Timestamp.from(Instant.now());
                // 例外発生時も必ず監査レコードを残す（別コネクションで挿入）
                try (Connection auditConn = DatabaseManager.getConnection()) {
                    auditConn.setAutoCommit(true);
                    DatabaseManager.insertJobExecution(auditConn, jobName, StepResult.Status.FAILURE.name(), start, end,
                            messages.toString(), totalRecords);
                } catch (SQLException ae) {
                    ae.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
