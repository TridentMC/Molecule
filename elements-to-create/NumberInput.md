# NumberInput Component

## Overview
A specialized text input for numeric values with optional increment/decrement buttons (spinners). Ensures only valid numbers can be entered.

## Core Functionality

### Value Management
- Current numeric value (int, long, float, or double)
- Minimum value constraint
- Maximum value constraint
- Step size for increment/decrement
- Default/initial value
- Decimal places (for floating point)

### Input Validation
- Only allow numeric characters
- Allow minus sign for negatives (if min < 0)
- Allow decimal point for floats (only one)
- Prevent invalid input (non-numeric chars)
- Validate value is within min/max range
- Parse and clamp value on blur/submit

### Interaction
- Text entry (like TextField)
- Up/Down arrow keys to increment/decrement
- Mouse wheel to adjust value (optional)
- Click and hold on spinner buttons for continuous change

### Spinner Buttons (Optional)
- Up button (increment)
- Down button (decrement)
- Position: right side of input, stacked vertically
- Visual feedback on hover/press
- Hold to repeat increment/decrement

### Visual States
- Focused/unfocused
- Hover state
- Error state (invalid value)
- Disabled state

## Event Handlers
- `onValueChanged(Number oldValue, Number newValue)` - Value changes
- `onValidationError(String errorMessage)` - Invalid input attempted
- `onSubmit(Number value)` - Enter key pressed

## Styling Properties
- Input width and height
- Background color/sprite
- Border styling
- Text color
- Text alignment (right-aligned common for numbers)
- Spinner button size and styling
- Spinner up/down icons
- Error state styling

## Component Parts

### Text Input
- Extends or reuses TextField
- Numeric-only validation
- Custom parser for the numeric type

### Spinner Up Button
- Small button with up arrow
- Increments value by step size
- Position: top-right of input

### Spinner Down Button
- Small button with down arrow
- Decrements value by step size
- Position: bottom-right of input

### Optional Unit Label
- Suffix text like "px", "%", "items"
- Non-editable, for context

## Implementation Considerations

### Number Type Handling
- Generic over number types? Or separate components for int/float?
- Type conversion and validation
- Precision handling for floats

### Input Parsing
- Parse on every keystroke (with validation)?
- Or parse on blur/submit only?
- Handle partially typed numbers (e.g., "-" or "1.")

### Range Clamping
- Clamp on input or show error?
- Visual indication when at min/max (disable buttons)

### Spinner Button Behavior
- Single click: change by 1 step
- Hold: continuous change with acceleration
- Timing: delay before repeat, then faster

### Decimal Places
- For floats: limit displayed decimal places
- Format display value (e.g., "1.50" vs "1.5")
- Parse allows more precision than display?

## Minecraft-Specific Features
- Common for config screens (render distance, FOV, etc.)
- Match vanilla numeric input styling
- Integer mode for block counts, IDs, etc.
- Float mode for scales, multipliers, etc.

## Example Usage
```java
// Integer input with spinners
var renderDistance = NumberInput.ofInt()
    .min(2)
    .max(32)
    .step(1)
    .value(16)
    .showSpinners(true)
    .onValueChanged((old, newVal) -> {
        setRenderDistance(newVal);
    })
    .build();

// Float input with 2 decimal places
var scale = NumberInput.ofDouble()
    .min(0.5)
    .max(2.0)
    .step(0.1)
    .decimals(2)
    .value(1.0)
    .unit("x")
    .onValueChanged((old, newVal) -> {
        setScale(newVal);
    })
    .build();

// Simple integer input (no spinners)
var count = NumberInput.ofInt()
    .min(1)
    .max(64)
    .value(1)
    .build();
```

## Variants

### With Spinners (SpinBox)
- Up/down buttons visible
- Takes more horizontal space
- Better for mouse-heavy UIs

### Without Spinners
- Just the text input
- More compact
- Keyboard-friendly (arrow keys still work)

### Slider + NumberInput Combo
- Slider for quick adjustment
- NumberInput for precise value
- Both sync to same value

## NumberInput vs Slider

### Use NumberInput when:
- Precise values needed
- User knows the exact number they want
- Wide range of possible values
- Decimal precision required

### Use Slider when:
- Approximate values acceptable
- Visual feedback of relative value important
- Limited screen space
- Touch/mouse interaction preferred

## Dependencies
- TextField component (extends or composes)
- Button component (for spinners)
- Numeric parsing and validation
- Event system
- State management

## Priority
**MEDIUM** - Very useful for config UIs but can initially use TextField with manual validation
