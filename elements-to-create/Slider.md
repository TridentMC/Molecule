# Slider Component

## Overview
A draggable slider component for selecting numeric values from a continuous or discrete range. Essential for volume controls, brightness, ranges, etc.

## Core Functionality

### Value Management
- Current value (double or float)
- Minimum value
- Maximum value
- Step size (for discrete values, e.g., integers)
- Default/initial value
- Programmatic value setting

### Interaction
- Click track to jump to value
- Drag thumb to change value
- Arrow keys for fine adjustment (when focused)
- Mouse wheel for adjustment (optional)
- Page Up/Down for larger steps (optional)

### Visual States
- Idle state
- Hover state (thumb highlighted)
- Dragging state (active)
- Disabled state
- Focused state (keyboard navigation)

### Value Display
- Optional value label showing current value
- Label formatting (integer, decimal places, percentage, custom format)
- Label position (above, below, or on thumb)

## Event Handlers
- `onValueChanged(double oldValue, double newValue)` - Value changes
- `onDragStart(double value)` - User starts dragging
- `onDragEnd(double value)` - User stops dragging
- `onValueCommitted(double value)` - Final value (on mouse release or key press)

## Styling Properties
- Track width and height
- Track background color/sprite
- Track fill color (filled portion before thumb)
- Thumb size
- Thumb sprite/color
- Thumb hover color
- Thumb drag color
- Label text color and font
- Orientation (horizontal or vertical)

## Component Parts

### Track
- The background bar/line
- Can show filled vs unfilled portions
- Click anywhere to jump thumb to that position

### Thumb/Handle
- Draggable indicator of current position
- Visual feedback on hover/drag
- Can be circular, rectangular, or custom sprite

### Fill (Optional)
- Visual indicator of value (colored portion from start to thumb)
- Common in volume/brightness controls

### Tick Marks (Optional)
- Visual indicators for discrete steps
- Useful for sliders with few distinct values

## Implementation Considerations

### Value Calculation
- Map mouse/drag position to value range
- Handle step snapping (round to nearest step)
- Clamp value to min/max bounds
- Precision handling for floating point

### Drag Handling
- Track drag offset to prevent thumb jumping
- Continue dragging even if mouse leaves component bounds
- Release drag on mouse up anywhere

### Orientation
- Horizontal (default): left = min, right = max
- Vertical: bottom = min, top = max
- Shared logic with different axis calculations

### Rendering
- Draw track background
- Draw fill (if enabled)
- Draw tick marks (if enabled)
- Draw thumb at calculated position
- Clip all rendering to component bounds

### Accessibility
- Keyboard control (arrow keys for ±1 step)
- Focus indication
- Value announcements (if screen reader support exists)

## Minecraft-Specific Features
- Match vanilla Minecraft option slider style
- Use vanilla slider sprites if available
- Integration with vanilla sound system (play click sound on value change)

## Example Usage
```java
// Volume slider (0-100%)
var volumeSlider = Slider.builder()
    .min(0.0)
    .max(1.0)
    .step(0.01)
    .value(0.75)
    .formatter(v -> String.format("%.0f%%", v * 100))
    .onValueChanged((old, newVal) -> {
        audioManager.setVolume(newVal);
    })
    .build();

// Integer range slider
var renderDistance = Slider.builder()
    .min(2)
    .max(32)
    .step(1)
    .value(16)
    .showValue(true)
    .onValueCommitted(value -> {
        setRenderDistance((int) value);
    })
    .build();
```

## Variants

### Discrete Slider
- Snaps to specific values (step size > 0)
- Shows tick marks for each valid value

### Continuous Slider
- Smooth value changes (step size = 0 or very small)
- No snapping

### Range Slider (Advanced)
- Two thumbs for min/max range selection
- More complex, consider separate component

## Dependencies
- Existing event system (MouseClickEvent, MouseDragEvent, KeyInputEvent)
- Sprite system for visual components
- State management system

## Priority
**HIGH** - Common for settings and controls (volume, brightness, ranges)
