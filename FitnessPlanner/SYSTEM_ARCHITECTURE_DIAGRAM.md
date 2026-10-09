# Challenge Storage System - Architecture Diagram

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        USER INTERFACE LAYER                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌──────────────────────┐      ┌──────────────────────┐        │
│  │ CombinedChallengeView│      │ ChallengeHistoryView │        │
│  ├──────────────────────┤      ├──────────────────────┤        │
│  │ • Daily Tab          │      │ • Current Week Tab   │        │
│  │ • Weekly Tab         │      │ • Last 4 Weeks Tab   │        │
│  │ • Progress Bars      │      │ • All Time Stats Tab │        │
│  │ • Checkboxes         │      │ • Streak Display     │        │
│  │ • Points Display     │      │ • Best Week Display  │        │
│  └──────────────────────┘      └──────────────────────┘        │
│           │                              │                       │
│           └──────────────┬───────────────┘                       │
│                          │                                       │
└──────────────────────────┼───────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                      SERVICE/LOGIC LAYER                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌────────────────────────┐    ┌──────────────────────────┐    │
│  │CheckboxChallengeStorage│    │ DailyChallengeService    │    │
│  ├────────────────────────┤    ├──────────────────────────┤    │
│  │ • saveDailyStatus()    │    │ • saveDailyChallengeData()│   │
│  │ • getDailyStatus()     │    │ • getDailyChallengeData() │   │
│  │ • getWeeklyData()      │    │ • getWeeklyChallengeData()│   │
│  │ • getDateRangeData()   │    │ • calculateWeeklyProgress()│  │
│  │ • getAllTimeStats()    │    │ • updateStreaks()         │   │
│  │ • getCurrentStreak()   │    │ • getChallengeStreaks()   │   │
│  │ • getLongestStreak()   │    │ • initializeTables()      │   │
│  └────────────────────────┘    └──────────────────────────┘    │
│           │                              │                       │
│           └──────────────┬───────────────┘                       │
│                          │                                       │
└──────────────────────────┼───────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                      DATABASE LAYER (MySQL)                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌──────────────────────┐  ┌──────────────────────┐            │
│  │ checkbox_challenges  │  │ daily_challenge_data │            │
│  ├──────────────────────┤  ├──────────────────────┤            │
│  │ • user_id            │  │ • user_id            │            │
│  │ • date               │  │ • date               │            │
│  │ • category           │  │ • steps              │            │
│  │ • challenge_item     │  │ • workout_minutes    │            │
│  │ • completed          │  │ • calories_burned    │            │
│  │ • points_earned      │  │ • water_ml           │            │
│  │ • created_at         │  │ • sleep_hours        │            │
│  │ • updated_at         │  │ • total_points       │            │
│  └──────────────────────┘  └──────────────────────┘            │
│                                                                   │
│  ┌──────────────────────┐  ┌──────────────────────┐            │
│  │ weekly_progress      │  │ challenge_streaks    │            │
│  ├──────────────────────┤  ├──────────────────────┤            │
│  │ • user_id            │  │ • user_id            │            │
│  │ • week_start_date    │  │ • challenge_type     │            │
│  │ • week_end_date      │  │ • current_streak     │            │
│  │ • completed_days     │  │ • longest_streak     │            │
│  │ • total_points       │  │ • last_completed_date│            │
│  │ • avg_completion     │  │ • updated_at         │            │
│  └──────────────────────┘  └──────────────────────┘            │
│                                                                   │
└───────────────────────────────────────────────────────────────────┘
```

---

## 📊 Data Flow Diagram

### Daily Challenge Tracking Flow

```
┌─────────────┐
│    USER     │
│ Checks Box  │
└──────┬──────┘
       │
       ▼
┌─────────────────────────┐
│  CombinedChallengeView  │
│  • Update UI            │
│  • Calculate Progress   │
│  • Calculate Points     │
└──────┬──────────────────┘
       │
       ▼
┌──────────────────────────────┐
│ CheckboxChallengeStorage     │
│ saveDailyChallengeStatus()   │
│  • Prepare data              │
│  • Validate input            │
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────────────────┐
│      DATABASE                │
│  INSERT/UPDATE               │
│  checkbox_challenges         │
│  • user_id, date, item       │
│  • completed, points         │
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────────────────┐
│  UI UPDATE                   │
│  • Progress bar fills        │
│  • Points display updates    │
│  • Motivation message shows  │
└──────────────────────────────┘
```

### Weekly Progress Retrieval Flow

```
┌─────────────┐
│    USER     │
│ Opens Weekly│
│     Tab     │
└──────┬──────┘
       │
       ▼
┌─────────────────────────┐
│  CombinedChallengeView  │
│  createWeeklyContent()  │
└──────┬──────────────────┘
       │
       ▼
┌──────────────────────────────┐
│ CheckboxChallengeStorage     │
│ getWeeklyChallengeData()     │
│  • Query last 7 days         │
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────────────────┐
│      DATABASE                │
│  SELECT * FROM               │
│  checkbox_challenges         │
│  WHERE date BETWEEN          │
│  startDate AND endDate       │
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────────────────┐
│  DATA AGGREGATION            │
│  • Group by date             │
│  • Calculate completion %    │
│  • Sum points per day        │
│  • Calculate averages        │
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────────────────┐
│  UI DISPLAY                  │
│  • Weekly summary card       │
│  • Daily breakdown list      │
│  • Category averages         │
│  • Performance message       │
└──────────────────────────────┘
```

### Historical Data Access Flow

```
┌─────────────┐
│    USER     │
│ Clicks View │
│   History   │
└──────┬──────┘
       │
       ▼
┌─────────────────────────┐
│  ChallengeHistoryView   │
│  Opens with 3 tabs      │
└──────┬──────────────────┘
       │
       ├──────────────────────────────┐
       │                              │
       ▼                              ▼
┌──────────────────┐      ┌──────────────────┐
│ Current Week Tab │      │ Last 4 Weeks Tab │
│ • Last 7 days    │      │ • 4 week compare │
└──────┬───────────┘      └──────┬───────────┘
       │                         │
       └────────┬────────────────┘
                │
                ▼
┌──────────────────────────────┐
│ CheckboxChallengeStorage     │
│ • getDateRangeChallengeData()│
│ • getAllTimeStats()          │
│ • getCurrentStreak()         │
│ • getLongestStreak()         │
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────────────────┐
│      DATABASE QUERIES        │
│  • Date range queries        │
│  • Aggregation queries       │
│  • Statistical calculations  │
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────────────────┐
│  DISPLAY RESULTS             │
│  • Weekly summaries          │
│  • Comparison charts         │
│  • All-time statistics       │
│  • Streak information        │
│  • Best week display         │
└──────────────────────────────┘
```

---

## 🔄 Component Interaction Diagram

```
┌────────────────────────────────────────────────────────────────┐
│                         MAIN DASHBOARD                          │
└────────────────────────┬───────────────────────────────────────┘
                         │
                         │ User clicks "Daily Challenges"
                         ▼
┌────────────────────────────────────────────────────────────────┐
│                   COMBINED CHALLENGE VIEW                       │
├────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────────┐              ┌─────────────────┐          │
│  │  DAILY TAB      │              │  WEEKLY TAB     │          │
│  │                 │              │                 │          │
│  │  ┌───────────┐  │              │  ┌───────────┐  │          │
│  │  │ Exercise  │  │              │  │  Summary  │  │          │
│  │  │ Checkboxes│  │              │  │   Card    │  │          │
│  │  └─────┬─────┘  │              │  └─────┬─────┘  │          │
│  │        │        │              │        │        │          │
│  │  ┌─────▼─────┐  │              │  ┌─────▼─────┐  │          │
│  │  │ Nutrition │  │              │  │   Daily   │  │          │
│  │  │ Checkboxes│  │              │  │ Breakdown │  │          │
│  │  └─────┬─────┘  │              │  └───────────┘  │          │
│  │        │        │              │                 │          │
│  │  ┌─────▼─────┐  │              │                 │          │
│  │  │ Hydration │  │              │                 │          │
│  │  │ Checkboxes│  │              │                 │          │
│  │  └─────┬─────┘  │              │                 │          │
│  │        │        │              │                 │          │
│  │  ┌─────▼─────┐  │              │                 │          │
│  │  │ Recovery  │  │              │                 │          │
│  │  │ Checkboxes│  │              │                 │          │
│  │  └─────┬─────┘  │              │                 │          │
│  │        │        │              │                 │          │
│  │  ┌─────▼─────┐  │              │                 │          │
│  │  │ Progress  │  │              │                 │          │
│  │  │    Bar    │  │              │                 │          │
│  │  └───────────┘  │              │                 │          │
│  └─────────────────┘              └─────────────────┘          │
│                                                                  │
│  ┌──────────────────────────────────────────────────┐          │
│  │         [View History Button]                     │          │
│  └────────────────────┬─────────────────────────────┘          │
└───────────────────────┼──────────────────────────────────────────┘
                        │
                        │ User clicks "View History"
                        ▼
┌────────────────────────────────────────────────────────────────┐
│                   CHALLENGE HISTORY VIEW                        │
├────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │ Current Week │  │ Last 4 Weeks │  │ All Time     │         │
│  │     Tab      │  │     Tab      │  │  Stats Tab   │         │
│  │              │  │              │  │              │         │
│  │ • 7 days     │  │ • Week 1     │  │ • Total days │         │
│  │ • Summary    │  │ • Week 2     │  │ • Streaks    │         │
│  │ • Breakdown  │  │ • Week 3     │  │ • Best week  │         │
│  │              │  │ • Week 4     │  │ • Averages   │         │
│  └──────────────┘  └──────────────┘  └──────────────┘         │
│                                                                  │
└────────────────────────────────────────────────────────────────┘
```

---

## 🗃️ Database Relationships

```
┌─────────────────────┐
│       users         │
│  (existing table)   │
├─────────────────────┤
│ • id (PK)           │
│ • name              │
│ • email             │
│ • gender            │
└──────────┬──────────┘
           │
           │ 1:N relationship
           │
           ▼
┌─────────────────────────────────────────────────────────────┐
│                  checkbox_challenges                         │
├─────────────────────────────────────────────────────────────┤
│ • id (PK)                                                    │
│ • user_id (FK) ────────────────────────┐                    │
│ • date                                  │                    │
│ • category (EXERCISE, NUTRITION, etc)   │                    │
│ • challenge_item                        │                    │
│ • completed (BOOLEAN)                   │                    │
│ • points_earned                         │                    │
│ • UNIQUE(user_id, date, challenge_item) │                    │
└─────────────────────────────────────────┘                    │
                                                               │
┌─────────────────────────────────────────────────────────────┤
│                  daily_challenge_data                        │
├─────────────────────────────────────────────────────────────┤
│ • id (PK)                                                    │
│ • user_id (FK) ────────────────────────┐                    │
│ • date                                  │                    │
│ • steps, workout_minutes, etc           │                    │
│ • total_points                          │                    │
│ • completion_percentage                 │                    │
│ • UNIQUE(user_id, date)                 │                    │
└─────────────────────────────────────────┘                    │
                                                               │
┌─────────────────────────────────────────────────────────────┤
│                    weekly_progress                           │
├─────────────────────────────────────────────────────────────┤
│ • id (PK)                                                    │
│ • user_id (FK) ────────────────────────┐                    │
│ • week_start_date                       │                    │
│ • week_end_date                         │                    │
│ • total_points                          │                    │
│ • avg_completion_percentage             │                    │
│ • UNIQUE(user_id, week_start_date)      │                    │
└─────────────────────────────────────────┘                    │
                                                               │
┌─────────────────────────────────────────────────────────────┤
│                   challenge_streaks                          │
├─────────────────────────────────────────────────────────────┤
│ • id (PK)                                                    │
│ • user_id (FK) ────────────────────────┘                    │
│ • challenge_type (EXERCISE, OVERALL, etc)                   │
│ • current_streak                                            │
│ • longest_streak                                            │
│ • last_completed_date                                       │
│ • UNIQUE(user_id, challenge_type)                           │
└─────────────────────────────────────────────────────────────┘
```

---

## 🎯 Points Calculation Flow

```
┌─────────────────────────────────────────────────────────────┐
│                    POINTS CALCULATION                        │
└─────────────────────────────────────────────────────────────┘

User completes challenge item
         │
         ▼
┌─────────────────────┐
│ Identify Category   │
│ • EXERCISE          │
│ • NUTRITION         │
│ • HYDRATION         │
│ • RECOVERY          │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Assign Points       │
│ • Exercise: 6 pts   │
│ • Nutrition: 4 pts  │
│ • Hydration: 4 pts  │
│ • Recovery: 5 pts   │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Update Total        │
│ Sum all completed   │
│ items' points       │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Calculate %         │
│ (completed/total)   │
│ × 100               │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Save to Database    │
│ • Individual points │
│ • Total points      │
│ • Completion %      │
└─────────────────────┘
```

---

## 🔥 Streak Tracking Logic

```
┌─────────────────────────────────────────────────────────────┐
│                    STREAK CALCULATION                        │
└─────────────────────────────────────────────────────────────┘

Check today's completion
         │
         ▼
┌─────────────────────┐
│ Is completion       │
│    ≥ 75%?           │
└──────┬──────┬───────┘
       │      │
    YES│      │NO
       │      │
       ▼      ▼
┌──────────┐ ┌──────────┐
│ Check    │ │ Reset    │
│ yesterday│ │ current  │
│ status   │ │ streak   │
└────┬─────┘ │ to 0     │
     │       └──────────┘
     ▼
┌──────────────────┐
│ Was yesterday    │
│ also ≥75%?       │
└────┬─────┬───────┘
     │     │
  YES│     │NO
     │     │
     ▼     ▼
┌─────────┐ ┌─────────┐
│Increment│ │ Start   │
│ streak  │ │ new     │
│ by 1    │ │ streak  │
│         │ │ at 1    │
└────┬────┘ └────┬────┘
     │           │
     └─────┬─────┘
           ▼
┌──────────────────┐
│ Update longest   │
│ if current >     │
│ longest          │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ Save to database │
│ • current_streak │
│ • longest_streak │
│ • last_date      │
└──────────────────┘
```

---

## 📱 UI Component Hierarchy

```
CombinedChallengeView
│
├── BackButtonBox
│   ├── BackButton
│   └── HistoryButton
│
├── HeaderBox
│   ├── TitleLabel
│   └── DateLabel
│
└── TabPane
    │
    ├── DailyTab
    │   ├── MotivationLabel
    │   ├── ProgressBox
    │   │   ├── ProgressTitle
    │   │   ├── DailyProgressBar
    │   │   ├── ProgressLabel
    │   │   └── PointsLabel
    │   │
    │   └── ChallengeContainer
    │       ├── ExerciseColumn
    │       │   └── CheckBoxes (4)
    │       ├── NutritionColumn
    │       │   └── CheckBoxes (5)
    │       ├── HydrationColumn
    │       │   └── CheckBoxes (4)
    │       └── RecoveryColumn
    │           └── CheckBoxes (4)
    │
    └── WeeklyTab
        ├── WeeklyTitle
        ├── SummaryCard
        │   ├── SummaryTitle
        │   ├── StatsGrid
        │   │   ├── TotalPointsBox
        │   │   ├── AvgCompletionBox
        │   │   ├── ActiveDaysBox
        │   │   ├── ExerciseAvgBox
        │   │   ├── NutritionAvgBox
        │   │   └── HydrationAvgBox
        │   └── PerformanceLabel
        │
        └── DailyBreakdown (7 days)
            └── DayProgressCard (each)
                ├── DayLabel
                ├── OverallProgressBar
                ├── OverallLabel
                ├── PointsLabel
                └── CategoriesBox
                    ├── ExerciseProgress
                    ├── NutritionProgress
                    ├── HydrationProgress
                    └── RecoveryProgress
```

---

This architecture provides a clear, maintainable, and scalable system for tracking fitness challenges with comprehensive data storage and visualization capabilities.
