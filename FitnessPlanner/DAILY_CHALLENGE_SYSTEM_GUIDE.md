# Daily Challenge System Guide

## Overview
The Daily Challenge System provides users with daily fitness goals across four main categories:
- 🏋️ **Exercise & Activity** (Steps, Workout Minutes, Calories Burned)
- 🥗 **Nutrition & Diet** (Vegetables, Fruits, Water)
- 💧 **Hydration** (Water intake tracking)
- 😴 **Sleep & Recovery** (Sleep hours and quality)

## Features

### ✅ Daily Challenge Tracking
- **Checkbox System**: Each challenge has a checkbox that auto-completes when targets are met
- **Input Fields**: Users can enter their daily values (steps, minutes, servings, etc.)
- **Real-time Progress**: Progress bar updates instantly as challenges are completed
- **Points System**: Earn points for completing challenges (15-25 points each)

### 📊 Progress Tracking
- **Daily Progress Bar**: Shows completion percentage (0-100%)
- **Points Counter**: Displays total points earned today
- **Visual Feedback**: Color-coded progress indicators

### 🎯 Challenge Targets
- **Steps**: 10,000 steps daily
- **Workout**: 30 minutes of exercise
- **Calories Burned**: 300 calories through activity
- **Vegetables**: 5 servings daily
- **Fruits**: 3 servings daily
- **Water**: 8 glasses (2000ml) daily
- **Sleep**: 8 hours with good quality

### 💧 Quick Water Tracker
- **Glass Buttons**: Click numbered buttons (1-10) to quickly log water intake
- **Auto-conversion**: Automatically converts glasses to ml and updates progress

## Implementation

### Two Versions Available

#### 1. Full Database Version (`DailyChallengeView.java`)
- **Complete Data Persistence**: Stores all data in database tables
- **Weekly Progress Calculation**: Automatically calculates 7-day summaries
- **Streak Tracking**: Monitors consecutive completion days
- **Historical Data**: Maintains complete challenge history

**Database Tables:**
- `daily_challenge_data`: Stores daily challenge values
- `weekly_progress`: Calculated weekly summaries
- `challenge_streaks`: Tracks completion streaks

#### 2. Simple In-Memory Version (`SimpleDailyChallengeView.java`)
- **Session-based Storage**: Data persists during app session
- **Immediate Functionality**: Works without database setup
- **Lightweight**: Minimal resource usage
- **Fallback Option**: Used if database version fails

### Integration with Dashboard

The daily challenge system is integrated into the main dashboard with:
- **Navigation Button**: "🎯 Daily Challenges" in sidebar
- **Error Handling**: Automatically falls back to simple version if needed
- **CSS Styling**: Uses the existing fitness.css theme

## Usage Instructions

### For Users
1. **Access**: Click "🎯 Daily Challenges" in the dashboard sidebar
2. **Input Data**: Enter your daily values in the input fields
3. **Update**: Click "Update" button to save and calculate progress
4. **Track Progress**: Watch the progress bar and points counter update
5. **Complete Challenges**: Checkboxes auto-complete when targets are met

### Quick Water Logging
1. **Click Glass Buttons**: Use the 💧 buttons (1-10) for quick water logging
2. **Instant Update**: Water intake updates immediately
3. **Target Achievement**: Checkbox completes automatically at 8 glasses

### Weekly Progress (Full Version Only)
- **Automatic Calculation**: System calculates weekly summaries after 7 days
- **Progress History**: View past 7 days of challenge completion
- **Streak Tracking**: Monitor consecutive completion streaks

## Points System

| Challenge Category | Points | Target |
|-------------------|--------|---------|
| Exercise (Steps) | 25 | 10,000 steps |
| Exercise (Workout) | 25 | 30 minutes |
| Exercise (Calories) | 25 | 300 calories |
| Nutrition (Vegetables) | 20 | 5 servings |
| Nutrition (Fruits) | 20 | 3 servings |
| Hydration (Water) | 15 | 8 glasses |
| Sleep (Quality Rest) | 20 | 8 hours + good quality |

**Maximum Daily Points**: 150 points

## Technical Features

### Error Handling
- **Database Fallback**: Automatically switches to simple version if database issues occur
- **Input Validation**: Validates numeric inputs and shows error messages
- **Connection Safety**: Handles database connection failures gracefully

### Data Persistence
- **Daily Storage**: Each day's data is stored separately
- **Weekly Aggregation**: Automatic weekly summary calculation
- **Streak Calculation**: Tracks consecutive completion days

### UI Features
- **Responsive Design**: Works on different screen sizes
- **Glass Morphism**: Modern transparent card design
- **Progress Animations**: Smooth progress bar updates
- **Color Coding**: Visual feedback for completion status

## Troubleshooting

### If Daily Challenges Don't Load
1. **Check Database**: Ensure database connection is working
2. **Simple Version**: System automatically falls back to simple version
3. **Console Logs**: Check console for error messages

### If Data Doesn't Save
1. **Database Tables**: Ensure tables are created properly
2. **User ID**: Verify user has valid ID in database
3. **Permissions**: Check database write permissions

### If Progress Doesn't Update
1. **Input Values**: Ensure valid numbers are entered
2. **Target Values**: Check if targets are being met
3. **Refresh**: Try closing and reopening the challenge view

## Future Enhancements

### Planned Features
- **Custom Targets**: Allow users to set personal challenge targets
- **Challenge Variety**: Rotating daily challenges for variety
- **Social Features**: Share progress with friends
- **Achievements**: Unlock badges for streaks and milestones
- **Notifications**: Reminders for incomplete challenges

### Data Analytics
- **Trend Analysis**: Long-term progress trends
- **Performance Insights**: Identify best/worst performing areas
- **Goal Recommendations**: AI-suggested improvements

## File Structure

```
src/fitnessplanner/
├── DailyChallengeView.java          # Full database version
├── SimpleDailyChallengeView.java    # Simple in-memory version
├── DailyChallengeService.java       # Database operations
├── DailyChallengeData.java          # Data model
├── WeeklyProgressData.java          # Weekly summary model
├── WeeklyProgressTracker.java       # Weekly analysis view
└── DailyChallengeTest.java          # Testing utilities
```

The Daily Challenge System provides a comprehensive, user-friendly way to track daily fitness goals with both immediate feedback and long-term progress monitoring.