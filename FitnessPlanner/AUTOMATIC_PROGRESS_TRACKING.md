# 📊 Automatic Progress Tracking System

## ✅ New Feature: Real-Time Progress Graphs

### Automatic Data Collection & Visualization
When users input their daily metrics, the system automatically:
1. Saves data to database
2. Updates daily progress graph
3. Aggregates for weekly view
4. Aggregates for monthly view

---

## 🎯 How It Works

### 1. User Inputs Data
```
User enters in Wellness Tracker:
- Water: 2000 ml
- Calories: 1800 kcal
- Sleep: 7.5 hours
- Weight: 75 kg

Clicks "💾 Save Metrics"
```

### 2. Automatic Processing
```
System automatically:
✅ Saves to user_metrics table
✅ Saves to daily_progress table
✅ Updates today's entry (or creates new)
✅ Shows success message
✅ Refreshes dashboard
✅ Updates all graphs
```

### 3. Graph Updates
```
Daily View:   Shows last 7 days of data
Weekly View:  Aggregates daily data into weeks (last 8 weeks)
Monthly View: Aggregates weekly data into months (last 6 months)
```

---

## 📊 Progress Graph Features

### Period Selector
```
┌─────────────────────────────────────┐
│ 📈 Automatic Progress Tracker       │
│                    [Daily ▼]        │
│ 💡 Updates automatically when you   │
│    save metrics                     │
│                                     │
│ [Line Chart showing weight trend]   │
│                                     │
└─────────────────────────────────────┘
```

### Three Views:

#### 1. Daily View (Last 7 Days)
```
Shows: Individual daily entries
X-Axis: Dates (e.g., "01/12", "02/12", "03/12")
Y-Axis: Weight in kg
Data: Exact values from each day
```

#### 2. Weekly View (Last 8 Weeks)
```
Shows: Average of daily entries per week
X-Axis: Week numbers (e.g., "Week 1", "Week 2")
Y-Axis: Weight in kg
Data: Averaged from daily data
Calculation: SUM(daily_weight) / COUNT(days)
```

#### 3. Monthly View (Last 6 Months)
```
Shows: Average of all entries per month
X-Axis: Month names (e.g., "Jan", "Feb", "Mar")
Y-Axis: Weight in kg
Data: Averaged from all daily data in month
Calculation: SUM(daily_weight) / COUNT(days)
```

---

## 🗄️ Database Structure

### New Table: `daily_progress`
```sql
CREATE TABLE daily_progress (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    date DATE NOT NULL,
    weight DOUBLE,
    calories INT,
    water_ml INT,
    sleep_hours DOUBLE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_date (user_id, date),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Key Features:
- **Unique constraint** on (user_id, date) - One entry per day
- **Auto-update** - If entry exists, updates it
- **Foreign key** - Links to users table
- **Timestamp** - Tracks when data was saved

---

## 🔄 Data Flow

### Save Flow:
```
User Input
    ↓
Validate Numbers
    ↓
Save to user_metrics (existing)
    ↓
Save to daily_progress (new)
    ↓
Success Message
    ↓
Refresh Dashboard
    ↓
Graphs Update Automatically
```

### Graph Update Flow:
```
User Changes Period (Daily/Weekly/Monthly)
    ↓
Query Database
    ↓
Get Relevant Data
    ↓
Aggregate if needed
    ↓
Update Chart
    ↓
Display to User
```

---

## 📈 Data Aggregation

### Daily → Weekly
```sql
SELECT YEARWEEK(date) as week, 
       AVG(weight) as avg_weight
FROM daily_progress
WHERE user_id = ?
GROUP BY YEARWEEK(date)
ORDER BY week ASC
```

### Daily → Monthly
```sql
SELECT DATE_FORMAT(date, '%Y-%m') as month,
       DATE_FORMAT(date, '%b') as month_name,
       AVG(weight) as avg_weight
FROM daily_progress
WHERE user_id = ?
GROUP BY DATE_FORMAT(date, '%Y-%m')
ORDER BY month ASC
```

---

## 🎨 Visual Features

### Graph Styling:
```css
Background: Transparent
Border: Blue (#58a6ff), 2px
Shadow: Black shadow (15px blur)
Height: 350px
Title: White text with shadow
Axes: White labels with shadow
Lines: Blue (#58a6ff), 3px width
Points: Blue circles
```

### No Data State:
```
When no data exists:
Shows: "📊 No data yet. Start tracking by saving your metrics above!"
Style: Large white text with shadow
Padding: 50px
Centered in graph area
```

### Info Label:
```
Text: "💡 Updates automatically when you save metrics"
Color: Gray (#8b949e)
Size: 11px
Position: Below title
```

---

## 🎯 User Experience

### First Time User:
1. Sees empty graph with message
2. Enters metrics in wellness tracker
3. Clicks save
4. Graph immediately shows first data point
5. Message disappears, chart appears

### Regular User:
1. Opens dashboard
2. Sees current progress graph (default: Daily)
3. Can switch to Weekly or Monthly
4. Enters new metrics
5. Clicks save
6. Graph updates automatically
7. New data point appears

### Long-term User:
1. Has months of data
2. Can view trends over time
3. Daily: See this week's progress
4. Weekly: See last 2 months trend
5. Monthly: See last 6 months trend
6. Track progress toward goals

---

## 🔧 Technical Implementation

### ProgressTracker Class Methods:

#### 1. `saveDailyProgress()`
```java
Purpose: Save or update today's progress
Parameters: userId, weight, calories, waterMl, sleepHours
Returns: boolean (success/failure)
SQL: INSERT ... ON DUPLICATE KEY UPDATE
```

#### 2. `getDailyProgress()`
```java
Purpose: Get last 7 days of data
Parameters: userId, metric
Returns: Map<String, Double> (date → value)
SQL: SELECT with DATE_SUB(CURDATE(), INTERVAL 7 DAY)
```

#### 3. `getWeeklyProgress()`
```java
Purpose: Get last 8 weeks aggregated
Parameters: userId, metric
Returns: Map<String, Double> (week → avg_value)
SQL: SELECT with YEARWEEK() and AVG()
```

#### 4. `getMonthlyProgress()`
```java
Purpose: Get last 6 months aggregated
Parameters: userId, metric
Returns: Map<String, Double> (month → avg_value)
SQL: SELECT with DATE_FORMAT() and AVG()
```

#### 5. `initializeTable()`
```java
Purpose: Create table if not exists
Parameters: None
Returns: void
SQL: CREATE TABLE IF NOT EXISTS
```

---

## 🎯 Benefits

### For Users:
- ✅ No manual graph updates needed
- ✅ See progress immediately
- ✅ Multiple time views
- ✅ Automatic aggregation
- ✅ Historical data preserved
- ✅ Visual motivation

### For System:
- ✅ Efficient data storage
- ✅ Automatic aggregation
- ✅ Scalable design
- ✅ Clean separation of concerns
- ✅ Reusable components

---

## 📊 Example Scenarios

### Scenario 1: Weight Loss Journey
```
Day 1:  User enters 80 kg → Graph shows 1 point
Day 2:  User enters 79.8 kg → Graph shows 2 points
Day 7:  User enters 78.5 kg → Graph shows 7 points
Week 2: Switch to Weekly → Shows Week 1 avg: 79.2 kg
Month 2: Switch to Monthly → Shows Jan avg: 78 kg
```

### Scenario 2: Muscle Gain
```
Day 1:  User enters 70 kg → Graph shows 1 point
Day 7:  User enters 70.5 kg → Graph shows upward trend
Week 4: Weekly view shows steady increase
Month 3: Monthly view shows 2 kg gain
```

### Scenario 3: Maintenance
```
Daily: Shows fluctuations (±0.5 kg)
Weekly: Shows stable average
Monthly: Shows consistent maintenance
```

---

## ✅ Testing Checklist

- [x] Table creates automatically
- [x] Data saves on metrics input
- [x] Daily graph shows last 7 days
- [x] Weekly graph shows last 8 weeks
- [x] Monthly graph shows last 6 months
- [x] Period selector works
- [x] Graph updates on save
- [x] No data message shows when empty
- [x] Chart appears when data exists
- [x] Aggregation calculates correctly
- [x] Duplicate dates update (not insert)
- [x] Success message shows
- [x] Dashboard refreshes
- [x] All styling applied

---

## 🎉 Result

**Fully automatic progress tracking system with:**
- ✅ Real-time data collection
- ✅ Automatic graph updates
- ✅ Three time period views
- ✅ Smart data aggregation
- ✅ Visual progress tracking
- ✅ No manual updates needed
- ✅ Historical data preserved
- ✅ Professional visualization

**Track your fitness journey automatically! 📊💪**
