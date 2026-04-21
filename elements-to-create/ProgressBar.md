# ProgressBar Component

## Overview
A visual indicator showing the completion progress of a task or operation. Essential for loading screens, file operations, and any long-running process.

## Core Functionality

### Progress Tracking
- Current progress value (0.0 to 1.0, or 0 to max)
- Progress as percentage (0-100%)
- Determinate mode (known total) vs Indeterminate mode (unknown total)
- Programmatic progress updates

### Visual Representation
- Filled bar showing progress percentage
- Optional text label showing progress (e.g., "75%", "3/10", "Loading...")
- Optional animation for indeterminate mode
- Color coding (optional: green=good, yellow=warning, red=error)

### Modes

#### Determinate
- Shows specific progress (e.g., 75% complete)
- Fill bar grows from left to right
- Numeric feedback

#### Indeterminate
- Unknown completion time
- Animated "loading" indicator
- Pulsing, sliding, or bouncing animation
- Communicates "working..." without specific progress

### Orientation
- Horizontal (default, left to right)
- Vertical (bottom to top)

## Event Handlers
- `onProgressChanged(double oldProgress, double newProgress)` - Progress updates
- `onCompleted()` - Progress reaches 100%

## Styling Properties
- Bar width and height
- Background color (unfilled portion)
- Fill color (filled portion)
- Border style
- Text label color and position
- Animation speed (for indeterminate)
- Corner radius (rounded vs square)

## Component Parts

### Background Track
- The full bar outline/background
- Shows unfilled portion
- Fixed size

### Fill Bar
- Colored portion showing progress
- Width/height based on progress percentage
- Grows as progress increases

### Text Label (Optional)
- Shows progress as text
- Position: centered on bar, or to the right
- Format: percentage, fraction, or custom

### Animation (Indeterminate)
- Sliding bar segment
- Pulsing opacity
- Bouncing indicator
- Rotating spinner (alternative style)

## Implementation Considerations

### Progress Calculation
- Normalize progress to 0.0-1.0 range
- Handle values outside range (clamp to 0-1)
- Percentage calculation: `progress * 100`

### Rendering
- Draw background track
- Draw fill bar clipped to progress width
- Draw text label if enabled
- Smooth updates (interpolation optional)

### Indeterminate Animation
- Continuous animation loop
- Use game tick or delta time
- Sliding segment: position cycles across bar
- Smooth, non-distracting motion

### Text Formatting
- Percentage: "75%"
- Fraction: "3 / 10"
- Custom: "Loading chunks..." or "Saving world..."
- Support for both static and dynamic text

### Performance
- Efficient rendering (don't rebuild on every tick)
- Batch progress updates if many rapid changes
- Cache gradient/fill sprites if applicable

## Minecraft-Specific Features
- Match vanilla loading screen style
- Boss bar style (top of screen, purple bar)
- Experience bar style (bottom of screen)
- Chunk loading progress
- World generation progress

## Example Usage
```java
// Determinate progress bar
var downloadProgress = ProgressBar.builder()
    .progress(0.0)
    .showPercentage(true)
    .onCompleted(() -> {
        System.out.println("Download complete!");
    })
    .build();

// Update progress
downloadProgress.setProgress(0.75); // 75%

// Indeterminate loading bar
var loadingBar = ProgressBar.builder()
    .indeterminate(true)
    .label("Loading resources...")
    .build();

// Fraction display
var fileProgress = ProgressBar.builder()
    .progress(3, 10) // 3 out of 10
    .labelFormat((current, total) -> current + " / " + total)
    .build();
```

## Variants

### Linear Progress Bar
- Standard horizontal bar
- Most common

### Circular Progress Indicator
- Ring that fills clockwise
- Compact, good for small spaces
- More complex to implement

### Segmented Progress
- Discrete segments instead of smooth fill
- Shows steps in multi-stage process
- Each segment represents one stage

## Color Coding

### Semantic Colors
- Green: success, normal progress
- Yellow: warning, slow progress
- Red: error or critical
- Blue: info, neutral

### Dynamic Colors
- Change color based on progress
- E.g., red at 0%, yellow at 50%, green at 100%

## Advanced Features

### Estimated Time Remaining
- Calculate based on progress rate
- Display "5 minutes remaining"
- Requires tracking progress over time

### Multi-Stage Progress
- Different stages of a process
- Show current stage and overall progress
- E.g., "Stage 2 of 5: Compiling..."

### Cancellable Progress
- X button to cancel operation
- Show confirmation dialog
- Emit cancel event

## Dependencies
- Rendering system (for rectangles and gradients)
- Animation/timing system (for indeterminate mode)
- Label rendering (optional)
- State management

## Priority
**MEDIUM** - Very useful for feedback during long operations, but not interactive input
