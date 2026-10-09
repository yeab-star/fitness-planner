package fitnessplanner;

public class UserMetrics {
    private int waterMl = 0;
    private int calories = 0;
    private double sleepHours = 0.0;

    public int getWaterMl() { return waterMl; }
    public void setWaterMl(int waterMl) { this.waterMl = waterMl; }
    
    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }
    
    public double getSleepHours() { return sleepHours; }
    public void setSleepHours(double sleepHours) { this.sleepHours = sleepHours; }
}