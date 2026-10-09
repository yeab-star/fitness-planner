package fitnessplanner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProgressService {
    
    public List<WeightEntry> getWeightHistory(int userId, int days) {
        List<WeightEntry> weights = new ArrayList<>();
        String query = "SELECT weight, log_date FROM weight_logs " +
                      "WHERE user_id = ? AND log_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY) " +
                      "ORDER BY log_date ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, days);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                WeightEntry entry = new WeightEntry();
                entry.setWeight(rs.getDouble("weight"));
                entry.setDate(rs.getDate("log_date").toLocalDate());
                weights.add(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weights;
    }
    
    public double getWeeklyWeightLoss(int userId) {
        List<WeightEntry> weights = getWeightHistory(userId, 7);
        if (weights.size() < 2) return 0.0;
        double first = weights.get(0).getWeight();
        double last = weights.get(weights.size() - 1).getWeight();
        return first - last;
    }
}