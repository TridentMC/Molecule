# RadioButtonGroup Component

## Overview
A group of mutually exclusive radio buttons where only one option can be selected at a time. Used for choosing between 2-5 distinct options.

## Core Functionality

### Selection Management
- List of options
- Currently selected option (exactly one, always)
- Default/initial selection
- Programmatic selection change

### Interaction
- Click radio button to select
- Click label to select (larger hit area)
- Arrow keys to navigate between options (when focused)
- Spacebar to select focused option

### Visual States
- Unselected state (empty circle)
- Selected state (filled circle or dot in circle)
- Hover state (highlighted)
- Disabled state (for entire group or individual options)
- Focused state (for keyboard navigation)

### Layout
- Vertical layout (default, one option per line)
- Horizontal layout (options in a row)
- Grid layout (for many options)
- Spacing between options

## Event Handlers
- `onSelectionChanged(T oldValue, T newValue)` - Selection changes
- `onSelected(T value)` - Option becomes selected

## Styling Properties
- Radio button size (circle diameter)
- Unselected circle sprite/color
- Selected circle sprite/color
- Selected indicator (inner dot) sprite/color
- Hover overlay color
- Label text color
- Spacing between button and label
- Spacing between options
- Disabled appearance

## Component Parts

### Radio Button
- Outer circle
- Inner dot (when selected)
- Visual feedback on hover

### Label
- Text describing the option
- Clickable (selects the option)
- Positioned next to radio button

### Group Container
- Manages mutual exclusivity
- Layouts individual radio buttons
- Handles group-level state

## Implementation Considerations

### Mutual Exclusivity
- Only one option can be selected at a time
- Selecting a new option automatically deselects the previous
- Group manages the selected state, not individual buttons

### Component Structure Options

#### Option 1: Composite Component
- RadioButtonGroup manages list of RadioButton children
- Each RadioButton is a separate element
- Group coordinates selection state

#### Option 2: Single Rendered Component
- RadioButtonGroup renders all options itself
- More efficient, less flexible
- Custom layout logic in one place

#### Option 3: Builder Pattern
- RadioButtonGroup builder accepts option definitions
- Internally creates and manages RadioButton elements
- Balance of flexibility and efficiency

### State Management
- Group holds selected value state
- Individual buttons query group for selection state
- State changes trigger recomposition

### Keyboard Navigation
- Focus moves between radio buttons in group
- Arrow keys cycle through options
- Tab key moves focus to next UI component

## Minecraft-Specific Features
- Match vanilla Minecraft radio button style (if exists)
- Otherwise, create custom sprites matching UI aesthetic
- Play selection sound on option change

## Example Usage
```java
// Simple string options
var alignment = RadioButtonGroup.<String>builder()
    .options(List.of("Left", "Center", "Right"))
    .selected("Center")
    .onSelectionChanged((old, newAlign) -> {
        setAlignment(newAlign);
    })
    .layout(Layout.HORIZONTAL)
    .build();

// Enum options
var difficulty = RadioButtonGroup.<Difficulty>builder()
    .options(List.of(Difficulty.values()))
    .displayTextProvider(Difficulty::getDisplayName)
    .selected(Difficulty.NORMAL)
    .onSelectionChanged((old, diff) -> {
        changeDifficulty(diff);
    })
    .build();

// With custom content (icon + text)
var gameMode = RadioButtonGroup.<GameMode>builder()
    .option(GameMode.SURVIVAL, "Survival", survivalIcon)
    .option(GameMode.CREATIVE, "Creative", creativeIcon)
    .option(GameMode.ADVENTURE, "Adventure", adventureIcon)
    .selected(GameMode.SURVIVAL)
    .layout(Layout.VERTICAL)
    .build();
```

## When to Use

### RadioButtonGroup vs Dropdown
- **Use RadioButtonGroup when:**
  - 2-5 options
  - Options should be visible at all times
  - User needs to compare options
  - Compact vertical/horizontal layout

- **Use Dropdown when:**
  - Many options (6+)
  - Screen space is limited
  - Options are familiar and don't need comparison

## Advanced Features

### Option Disabling
- Disable specific options while keeping others enabled
- Visual indication of disabled options
- Prevent selection of disabled options

### Custom Option Content
- Beyond text labels: icons, images, formatted text
- Custom rendering per option
- Complex option layouts

### Description Text
- Additional help text per option
- Appears below option label
- Smaller, lighter colored text

## Dependencies
- Existing event system (MouseClickEvent, KeyInputEvent)
- Sprite system for radio button circles
- Label system for option text
- Focus management system
- State management system

## Priority
**MEDIUM** - Useful but can often be replaced with Dropdown for simpler implementation
