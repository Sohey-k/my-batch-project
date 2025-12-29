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
                // job_execution に挿入
                DatabaseManager.insertJobExecution(conn, jobName, finalStatus.name(), start, end,
                        messages.toString(), totalRecords);

                if (finalStatus == StepResult.Status.SUCCESS) {
                    conn.commit();
                } else {
                    conn.rollback();
                }
            } catch (Exception e) {
                conn.rollback();
                messages.append("Exception: ").append(e.getMessage());
                end = Timestamp.from(Instant.now());
                DatabaseManager.insertJobExecution(conn, jobName, StepResult.Status.FAILURE.name(), start, end,
                        messages.toString(), totalRecords);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
