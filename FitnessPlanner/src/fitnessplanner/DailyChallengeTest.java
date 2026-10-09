package fitnessplanner;

import java.time.LocalDate;

public class DailyChallengeTest {
    
    public static void testDailyChallengeSystem() {
        System.out.println("Testing Daily Challenge System...");
        
        try {
            // Create test user
            User testUser = new User("testuser", "test@example.com", 25, 170.0, 70.0, "Male", "Lose Weight");
            testUser.setId(1); // Assuming user ID 1 exists
            
            // Initialize service
            DailyChallengeService service = new DailyChallengeService();
            service.initializeTables();
            System.out.println("✓ Tables initialized successfully");
            
            // Create test data
            DailyChallengeData testData = new DailyChallengeData(LocalDate.now());
            testData.setSteps(8000);
            testData.setWorkoutMinutes(45);
            testData.setVegetablesServings(4);
            testData.setWaterMl(1800);
            testData.setSleepHours(7.5);
            testData.setSleepQuality(true);
            
            System.out.println("✓ Test data created: " + testData);
            
            // Save data
            service.saveDailyChallengeData(testUser.getId(), testData);
            System.out.println("✓ Data saved successfully");
            
            // Retrieve data
            DailyChallengeData retrievedData = service.getDailyChallengeData(testUser.getId(), LocalDate.now());
            System.out.println("✓ Data retrieved: " + retrievedData);
            
            // Test weekly progress
            service.calculateWeeklyProgress(testUser.getId());
            System.out.println("✓ Weekly progress calculated");
            
            System.out.println("All tests passed! ✅");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        testDailyChallengeSystem();
    }
}