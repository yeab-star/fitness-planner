package fitnessplanner;

import java.time.LocalDate;

public class WeightEntry {
    private LocalDate date;
    private double weight;

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
}