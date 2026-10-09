# 🎨 Button States & Hover Effects Guide

## Sidebar Navigation Buttons

### State 1: Normal (Inactive)
```
Appearance:
- Background: Transparent
- Text Color: Gray (#8b949e)
- Font Weight: Normal
- No glow effect
```

### State 2: Hover (Mouse Over)
```
Appearance:
- Background: Dark gray (#21262d)
- Text Color: Bright blue (#58a6ff)
- Font Weight: Normal
- Scale: 1.02x (slightly larger)
- Cursor: Hand pointer
- Smooth transition: 0.3s
```

### State 3: Active/Selected (Clicked)
```
Appearance:
- Background: Blue (#1f6feb)
- Text Color: White
- Font Weight: Bold
- Persists until another button is clicked
- Previous button returns to Normal state
```

---

## Action Buttons (Log Weight, Start Workout, etc.)

### Normal State
```
Appearance:
- Background: Color based on type
  - Primary: Blue (#1f6feb)
  - Success: Green (#238636)
  - Danger: Red (#f85149)
- Text Color: White
- Font Weight: Bold
- Size: 180px x 45px
- Border Radius: 8px
```

### Hover State
```
Appearance:
- Background: Lighter shade of base color
- Scale: 1.05x (5% larger)
- Glow effect around button
- Cursor: Hand pointer
- Smooth animation
```

### Pressed State
```
Appearance:
- Scale: 0.98x (slightly smaller)
- Darker shade of base color
- Gives tactile feedback
```

---

## Notification Button

### Normal State
```
Appearance:
- Background: Dark gray (#21262d)
- Icon: Bell 🔔
- Text Color: Gray (#8b949e)
- Size: 50px x 50px
- Shape: Circle (border-radius: 25px)
- Badge: Red circle with count
```

### Hover State
```
Appearance:
- Background: Medium gray (#30363d)
- Text Color: Bright blue (#58a6ff)
- Scale: 1.1x (10% larger)
- Badge: Pulses slightly
- Cursor: Hand pointer
```

### With Notifications
```
Appearance:
- Badge visible with count
- Badge color: Red (#f85149)
- Badge position: Top-right corner
- Badge animates on new notification
```

---

## Menu Bar Items

### Normal State
```
Appearance:
- Background: Transparent
- Text Color: Light gray (#c9d1d9)
- Font Size: 13px
```

### Hover State
```
Appearance:
- Background: Dark gray (#21262d)
- Text Color: Same
- Smooth transition
```

### Menu Items (Dropdown)

#### Normal
```
Appearance:
- Background: Dark (#161b22)
- Text Color: Light gray (#c9d1d9)
```

#### Hover
```
Appearance:
- Background: Blue (#1f6feb)
- Text Color: White
- Full width highlight
```

---

## Quick Stats Cards

### Normal State
```
Appearance:
- Background: Semi-transparent dark
- Border: 2px colored (based on type)
- Shadow: Subtle
- Size: 200px x 140px
```

### Hover State
```
Appearance:
- Border: Brighter color
- Shadow: Enhanced glow
- Translate Y: -2px (lifts up)
- Smooth transition
```

---

## Plan Tracker Checkboxes

### Unchecked
```
Appearance:
- Box: Dark background (#0d1117)
- Border: Gray (#30363d)
- Border Width: 2px
```

### Hover
```
Appearance:
- Border: Blue (#58a6ff)
- Cursor: Hand pointer
```

### Checked
```
Appearance:
- Background: Green (#238636)
- Border: Green (#238636)
- Checkmark: White
- Triggers appreciation message
```

---

## Text Fields

### Normal State
```
Appearance:
- Background: Dark (#0d1117)
- Border: Gray (#30363d)
- Text Color: Light (#c9d1d9)
- Placeholder: Muted gray (#8b949e)
```

### Focused State
```
Appearance:
- Border: Blue (#58a6ff)
- Glow: Blue shadow effect
- Border width: Same
- Smooth transition
```

---

## Progress Period Selector (Dropdown)

### Normal State
```
Appearance:
- Background: Dark (#0d1117)
- Text Color: Light (#c9d1d9)
- Border: Gray
```

### Hover State
```
Appearance:
- Border: Blue
- Cursor: Hand pointer
```

### Opened State
```
Appearance:
- Dropdown list: Dark background
- Items: Light text
- Hover item: Blue background
```

---

## Animation Timings

```css
All Transitions: 0.3s ease
Scale Animations: 0.2s ease-in-out
Fade In: 0.5s
Slide Up: 0.4s
Pulse: 1s infinite
```

---

## Color Transitions

### Button Click Flow
```
1. Normal → Hover (on mouse enter)
   - Gray → Blue
   - 0.3s transition

2. Hover → Active (on click)
   - Blue → Darker Blue
   - Instant

3. Active → Normal (on mouse exit)
   - Darker Blue → Blue
   - 0.3s transition

4. Selected → Normal (when another button clicked)
   - Blue → Gray
   - 0.3s transition
```

---

## Glow Effects

### Blue Glow (Primary)
```
Color: rgba(88, 166, 255, 0.6)
Radius: 15px
Spread: 0.5
```

### Green Glow (Success)
```
Color: rgba(63, 185, 80, 0.6)
Radius: 15px
Spread: 0.5
```

### Red Glow (Danger)
```
Color: rgba(248, 81, 73, 0.6)
Radius: 15px
Spread: 0.5
```

### Purple Glow (Special)
```
Color: rgba(163, 113, 247, 0.6)
Radius: 15px
Spread: 0.5
```

---

## Testing Button States

### To Test Hover:
1. Move mouse over any button
2. Should see color change
3. Should see scale increase
4. Should see cursor change to hand

### To Test Active:
1. Click a sidebar button
2. Should turn blue with white text
3. Should stay blue even after mouse leaves
4. Click another button
5. First button should return to gray
6. New button should turn blue

### To Test Notifications:
1. Click bell icon
2. Should see notification panel
3. Badge should show count
4. Hover should scale button
5. Clear all should reset badge

---

**All button states are fully functional with smooth animations! 🎨✨**
