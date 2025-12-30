package com.example.batch.controller;

import com.example.batch.DatabaseManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/jobs")
public class JobPageController {

    @GetMapping
    public String jobs(Model model) {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(
                        "SELECT job_name, status, start_time, end_time, record_count FROM job_execution ORDER BY id DESC")) {
            while (rs.next()) {
                Map<String, Object> r = new HashMap<>();
                r.put("jobName", rs.getString("job_name"));
                r.put("status", rs.getString("status"));
                r.put("startTime", rs.getTimestamp("start_time"));
                r.put("endTime", rs.getTimestamp("end_time"));
                r.put("recordCount", rs.getInt("record_count"));
                rows.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("executions", rows);
        return "jobs"; // templates/jobs.html
    }
}
