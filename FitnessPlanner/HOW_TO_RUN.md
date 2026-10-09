# 🏋️ FitPlan - Fitness Planner Application

## ✅ What's Been Fixed & Updated

### 🐛 Bug Fixes
1. **LoginView.java** - Removed invalid label syntax (`questionError:`)
2. **LoginView.java** - Changed background to white as requested
3. **DashboardView.java** - Added missing imports (`FXCollections`, `ActionEvent`)
4. **DashboardView.java** - Fixed `primaryStage` reference issue

### 🎨 New Features & Design
1. **Dark Theme with Animations** - Modern dark theme with smooth transitions
2. **Gym Background** - Professional gym-themed background image
3. **Professional Sidebar** with:
   - 👤 User Profile (with avatar, name, goal, streak)
   - 🏠 Dashboard
   - 🥗 Today's Nutrition
   - 🏋️ Today's Exercise
   - 👤 User Profile View
   - ✏️ Edit Profile
   - 🚪 Logout
4. **Enhanced Dashboard** with:
   - Quick stats cards (Calories, Water, Sleep, Weight)
   - Daily wellness tracker
   - Weekly sleep rhythm chart
   - Quick action buttons

---

## 🚀 How to Run the Project

### Option 1: Using NetBeans (Recommended)
1. Open NetBeans IDE
2. Click **File → Open Project**
3. Navigate to the FitnessPlanner folder
4. Click **Open Project**
5. Right-click on the project in the Projects panel
6. Select **Run** (or press F6)

### Option 2: Using Command Line with Ant
```cmd
cd path\to\FitnessPlanner
ant clean
ant compile
ant run
```

### Option 3: Using Command Line with Java directly
```cmd
cd path\to\FitnessPlanner

REM Compile
javac -d build\classes -cp "lib\*" src\fitnessplanner\*.java

REM Run
java -cp "build\classes;lib\*" fitnessplanner.MainApp
```

---

## 📋 Prerequisites

### Required:
- **Java JDK 11 or higher**
- **JavaFX SDK** (if not included in your JDK)
- **MySQL Database** (for user data storage)
- **BCrypt library** (for password hashing)

### Database Setup:
Make sure your MySQL database is running and configured in `DatabaseConnection.java`

---

## 🎨 New UI Features

### Login Page
- ✅ White background (as requested)
- Clean, modern design
- Password visibility toggle
- Forgot password functionality

### Dashboard
- 🖼️ **Gym-themed background image**
- 📊 **Professional sidebar navigation**
- 💪 **Quick stats overview**
- 📈 **Interactive charts**
- 🎯 **Easy access to all features**

### Sidebar Navigation
- **Dashboard** - Overview of your fitness journey
- **Today's Nutrition** - Log your meals and track calories
- **Today's Exercise** - Record your workouts
- **User Profile** - View your complete profile
- **Edit Profile** - Update your information
- **Logout** - Safely exit the application

---

## 🎯 Quick Start Guide

1. **Launch the app** using one of the methods above
2. **Login** with your credentials (or register if new)
3. **Explore the sidebar** - Click any menu item to navigate
4. **Log your daily metrics** - Water, calories, sleep
5. **Track your workouts** - Use the Exercise section
6. **Monitor your progress** - Check the dashboard stats

---

## 🛠️ Troubleshooting

### If the app doesn't start:
- Verify Java is installed: `java -version`
- Check JavaFX is properly configured
- Ensure MySQL database is running
- Check database connection settings

### If you see compilation errors:
- Make sure all dependencies are in the `lib` folder
- Verify JavaFX libraries are included
- Check that BCrypt library is available

---

## 📱 Features Overview

✅ User authentication with BCrypt encryption  
✅ Dark theme with smooth animations  
✅ Professional gym-themed design  
✅ Daily wellness tracking  
✅ Nutrition logging  
✅ Exercise tracking  
✅ Sleep rhythm monitoring  
✅ Weight logging  
✅ Profile management  
✅ Streak tracking  

---

Enjoy your fitness journey with FitPlan! 💪🏋️‍♂️
