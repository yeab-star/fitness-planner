package fitnessplanner;

public class User {
    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private int age;
    private double height;
    private double weight;
    private String gender;
    private String goal;
    private int streak;

    public User() {}

    public User(String name, String email, int age, double height, double weight, String gender, String goal) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.goal = goal;
        this.passwordHash = "";
        this.streak = 0;
    }

    public User(int id, String name, String email, String passwordHash, int age, double height, double weight, String gender, String goal) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.goal = goal;
        this.streak = 0;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return passwordHash; }
    public int getAge() { return age; }
    public double getHeight() { return height; }
    public double getWeight() { return weight; }
    public String getGender() { return gender; }
    public String getGoal() { return goal; }
    public int getStreak() { return streak; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String passwordHash) { this.passwordHash = passwordHash; }
    public void setAge(int age) { this.age = age; }
    public void setHeight(double height) { this.height = height; }
    public void setWeight(double weight) { this.weight = weight; }
    public void setGender(String gender) { this.gender = gender; }
    public void setGoal(String goal) { this.goal = goal; }
    public void setStreak(int streak) { this.streak = streak; }
}