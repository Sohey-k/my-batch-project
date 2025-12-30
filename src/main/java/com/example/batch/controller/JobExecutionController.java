package com.example.batch.controller;

import com.example.batch.DatabaseManager;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/job-executions")
public class JobExecutionController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> list() {
        List<Map<String, Object>> out = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, job_name, status, start_time, end_time, message, record_count FROM job_execution ORDER BY id DESC")) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getLong("id"));
                row.put("jobName", rs.getString("job_name"));
                row.put("status", rs.getString("status"));
                row.put("startTime", rs.getTimestamp("start_time"));
                row.put("endTime", rs.getTimestamp("end_time"));
                row.put("message", rs.getString("message"));
                row.put("recordCount", rs.getInt("record_count"));
                out.add(row);
            }
        } catch (Exception e) {
            // return empty list on error; controller is read-only and non-intrusive
            e.printStackTrace();
        }
        return out;
    }
}
