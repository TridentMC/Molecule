# ToggleSwitch Component

## Overview
An alternative to Checkbox with an animated sliding switch visual. Modern UI element for boolean on/off states. Common in mobile and modern desktop UIs.

## Core Functionality

### State Management
- Boolean on/off state
- Initial state configuration
- Programmatic state setting
- State change callbacks

### Interaction
- Click anywhere on switch to toggle
- Drag thumb left/right to toggle
- Spacebar to toggle when focused
- Visual feedback on hover
- Smooth animation when toggling

### Visual States
- Off state (thumb on left, track not highlighted)
- On state (thumb on right, track highlighted)
- Hover state
- Disabled state
- Focused state
- Transitioning state (animation in progress)

### Animation
- Smooth thumb slide animation (off ↔ on)
- Track color transition
- Configurable animation duration
- Spring/easing animation curve

## Event Handlers
- `onToggled(boolean isOn)` - State changes
- `onTurnedOn()` - Becomes on
- `onTurnedOff()` - Becomes off

## Styling Properties
- Switch width and height
- Track background color (off state)
- Track background color (on state)
- Track border
- Thumb size
- Thumb color
- Thumb shadow/border
- Hover overlay
- Disabled appearance (transparency/grayscale)
- Animation duration
- Optional label text and position

## Visual Design

### Track
- Rounded rectangle background (pill shape)
- Color changes based on state: gray (off) → accent color (on)
- Typical ratio: width ≈ 2× height

### Thumb
- Circular button that slides left/right
- Slightly smaller than track height
- White or light colored
- Drop shadow for depth

### Dimensions
- Common size: 40×20px (track), 16px (thumb)
- Minecraft-sized: 32×16px (track), 12px (thumb)

## Implementation Considerations

### Animation System
- Interpolate thumb position over time
- Interpolate track color over time
- Use existing rendering tick/delta time
- Animation state: start position, end position, progress
- Easing function for smooth motion

### Rendering
- Draw track (rounded rect)
- Draw thumb (circle) at interpolated position
- Clip to bounds
- Layer order: track → thumb

### Interaction
- Click detection: entire switch is clickable
- Drag detection: thumb can be dragged
- Drag threshold: small movement might not toggle
- Release behavior: snap to nearest state

### State vs Animation
- State changes immediately on click
- Animation plays to reflect new state
- Don't allow interaction during animation? (or do?)

## Minecraft-Specific Features
- Match Minecraft UI aesthetic (less modern, more blocky?)
- Use Minecraft sound on toggle
- Optional: use sprite-based animation instead of smooth interpolation

## Example Usage
```java
var enableParticles = ToggleSwitch.builder()
    .label("Particles")
    .on(true)
    .onToggled(isOn -> {
        particleManager.setEnabled(isOn);
    })
    .build();

// Minimal
var toggle = ToggleSwitch.of(false, this::onSoundToggled);
```

## ToggleSwitch vs Checkbox

### Use ToggleSwitch when:
- Immediate effect on toggle (like a light switch)
- Modern/mobile UI aesthetic desired
- Space for wider component
- On/off is more appropriate than checked/unchecked

### Use Checkbox when:
- Form submissions (state committed later)
- Traditional desktop UI aesthetic
- Compact vertical lists of options
- "Checked" metaphor makes more sense

## Advanced Features

### Labels
- "On" / "Off" text labels on track
- External label to the left or right
- Dynamic label text based on state

### Colors
- Custom track colors for on/off states
- Semantic colors (red=off, green=on)
- Match application theme

### Sizes
- Small, medium, large variants
- Configurable dimensions

## Dependencies
- Existing event system (MouseClickEvent, MouseDragEvent)
- Rendering system (for circles and rounded rects)
- Animation/timing system
- State management system

## Priority
**LOW** - Nice-to-have alternative to Checkbox; not essential
