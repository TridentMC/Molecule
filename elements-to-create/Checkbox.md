# Checkbox Component

## Overview
A boolean toggle component with a box and checkmark. Used for enabling/disabling options and multi-select scenarios.

## Core Functionality

### State Management
- Boolean checked state (checked/unchecked)
- Initial state configuration
- Programmatic state setting
- State change callbacks

### Interaction
- Click to toggle state
- Spacebar to toggle when focused (accessibility)
- Visual feedback on hover
- Click sound effect (Minecraft UI sound)

### Visual States
- Unchecked state
- Checked state (with checkmark)
- Hover state (highlighted)
- Disabled state (grayed out, non-interactive)
- Focused state (for keyboard navigation)

### Label Support
- Optional text label next to checkbox
- Label position (left or right of box)
- Label click toggles checkbox (larger hit area)
- Label styling (color, font)

## Event Handlers
- `onCheckedChanged(boolean checked)` - State changes
- `onChecked()` - Becomes checked
- `onUnchecked()` - Becomes unchecked

## Styling Properties
- Box size (default 16x16 or 20x20)
- Box background (unchecked sprite/color)
- Box border
- Checkmark sprite/color
- Hover overlay color
- Disabled appearance (transparency/grayscale)
- Label text color
- Spacing between box and label

## Visual Design Options

### Checkmark Style
- Vanilla Minecraft checkmark sprite
- Unicode checkmark character (✓)
- Custom sprite
- Filled box (alternative style)

### Box Style
- Vanilla Minecraft button-style box
- Flat modern box with border
- Custom texture

## Implementation Considerations

### Component Structure
- Extend `Element` base class
- Compose with ElementSprite (for box) and ElementLabel (for label)
- Or create custom rendering

### State Management
- Use `IStateManager` for checked state
- Trigger recomposition on state change

### Accessibility
- Keyboard navigation support
- Focus indication
- Screen reader support (if applicable to Minecraft)

### Layout
- Fixed size for checkbox box
- Label wrapping (if text is long)
- Horizontal layout: [Box] [Label] or [Label] [Box]

## Minecraft-Specific Features
- Use vanilla Minecraft checkbox sprites (if they exist)
- Alternative: create custom sprites matching Minecraft UI style
- Integration with vanilla GUI widgets texture atlas

## Example Usage
```java
var enableSounds = Checkbox.builder()
    .label("Enable Sounds")
    .checked(true)
    .onCheckedChanged(checked -> {
        config.setSoundsEnabled(checked);
    })
    .build();

// Or more concise
var checkbox = Checkbox.of("Auto-save", false, this::onAutoSaveToggled);
```

## Group Behavior
- Multiple checkboxes can be checked simultaneously (unlike radio buttons)
- No built-in group behavior needed
- Parent component handles multiple checkbox state if needed

## Dependencies
- Existing event system (MouseClickEvent, KeyInputEvent)
- Sprite system (ElementSprite)
- Label system (ElementLabel)
- State management system

## Priority
**HIGH** - Very common UI component for boolean options
