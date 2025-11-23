# Animation System Usage Guide

## Overview

The animation system uses **Minecraft's tick system** (20 TPS) with **partial tick interpolation** for smooth rendering at any frame rate.

## Key Concepts

### Tick-Based Updates
- **Game logic runs at 20 TPS** (ticks per second) = 50ms per tick
- **Rendering can happen at any FPS** (60, 120, 144, 240, etc.)
- **Partial ticks** (0.0 to 1.0) allow smooth interpolation between discrete game ticks

### Two-Value Pattern
Like Minecraft's built-in animations (e.g., `LayoutMarquee`), AnimatedState stores:
- `lastTickValue`: Value at the start of the current tick
- `currentTickValue`: Target value for the current tick
- Renders interpolate between these using `partialTicks` from `IScreenContext`

## Basic Usage

### 1. Layout Animations (triggers remeasure, not recomposition)

```java
@Override
public void compose(ICompositionScope scope) {
    // Create animated state
    var width = scope.animateInt(0, 300); // 300ms duration = 6 ticks

    // Bind for layout updates only (O(1) performance!)
    scope.bindLayout(width);

    scope.e(new Box(), box -> {
        // Use regular get() for layout/measurement
        box.layout().fixedWidth(width.get());
    });

    // Trigger animation
    someButton.onClick(e -> width.set(200));
}
```

### 2. Draw-Only Animations (zero composition/layout cost)

```java
@Override
public void compose(ICompositionScope scope) {
    var buttonColor = scope.animateColor(0xFF888888, 200);

    // NO binding needed - pure rendering animation
    scope.e(new Box(), box -> {
        box.e(new ElementRect(() -> {
            // IMPORTANT: Use get(partialTicks) during draw for smooth rendering!
            var ctx = ...; // IScreenContext from draw method
            return buttonColor.get(ctx.getPartialTicks());
        }));
    });

    // Trigger animation
    someButton.onClick(e -> buttonColor.set(0xFFFFFFFF));
}
```

### 3. Composition Animations (full UI restructure)

```java
@Override
public void compose(ICompositionScope scope) {
    var itemCount = State.of(0);
    scope.bind(itemCount); // Regular composition binding

    // Use non-animated state for composition changes
    for (int i = 0; i < itemCount.get(); i++) {
        scope.e(new ListItem());
    }

    someButton.onClick(e -> itemCount.set(10)); // Triggers full recomposition
}
```

## Rendering Best Practices

### ✅ DO: Use `get(partialTicks)` in draw methods

```java
public class MyElement extends BasePrimitiveElement {
    private AnimatedState<Float> opacity;

    @Override
    public void draw(IScreenContext context) {
        // Use partial ticks for buttery-smooth rendering at any FPS!
        float currentOpacity = opacity.get(context.getPartialTicks());
        context.drawRect(getBounds(), withAlpha(color, currentOpacity));
    }
}
```

### ❌ DON'T: Use `get()` without partial ticks in draw

```java
// BAD - will look choppy at high FPS!
float currentOpacity = opacity.get(); // Only updates 20 times per second

// GOOD - smooth at any FPS
float currentOpacity = opacity.get(context.getPartialTicks());
```

### ✅ DO: Use regular `get()` for layout/measurement

```java
@Override
public void compose(ICompositionScope scope) {
    var width = scope.animateInt(100, 500);
    scope.bindLayout(width);

    // Regular get() is fine here - layout happens once per tick anyway
    box.layout().fixedWidth(width.get());
}
```

## Custom Easing

```java
// Built-in easing functions
var smooth = scope.animateFloat(0f, 300, Easing.EASE_IN_OUT);
var bouncy = scope.animateFloat(0f, 300, Easing.EASE_OUT_CUBIC);
var sharp = scope.animateFloat(0f, 300, Easing.EASE_IN);

// Custom easing
var custom = scope.animateFloat(0f, 300, t -> t * t * (3f - 2f * t));
```

## Performance Characteristics

### Layout Animations
- **Update frequency**: 20 TPS (once per tick)
- **Cost**: Triggers remeasurement only (no recomposition)
- **Use case**: Animating size, position, spacing, padding

### Draw Animations
- **Update frequency**: Your monitor's refresh rate (60/120/144 Hz)
- **Cost**: Zero composition/layout overhead
- **Use case**: Color transitions, opacity, visual effects

### Tick Budget
- **Duration examples**:
  - `100ms` = 2 ticks
  - `200ms` = 4 ticks
  - `300ms` = 6 ticks
  - `500ms` = 10 ticks
  - `1000ms` = 20 ticks

## Migration from Nano-Time System

If you previously used `System.nanoTime()` for animations:

```java
// OLD (frame-time based)
long elapsed = System.nanoTime() - startTime;
float progress = elapsed / (float) durationNanos;

// NEW (tick-based with partial tick interpolation)
long elapsedTicks = currentTick - startTick;
float progress = elapsedTicks / (float) durationTicks;
// Plus automatic partial tick interpolation for rendering!
```

## Example: Complete Button Hover Animation

```java
public class AnimatedButton extends ComposableElement {
    @Override
    public void compose(ICompositionScope scope) {
        var bgColor = scope.animateColor(0xFF444444, 150);
        var textColor = scope.animateColor(0xFFAAAAAA, 150);

        scope.e(new Box(), box -> {
            box.layout().padding(10).fixedSize(120, 40);

            // Background with animated color
            box.e(new ElementRect(() -> {
                var ctx = getCurrentContext(); // Your method to get context
                return bgColor.get(ctx.getPartialTicks());
            }));

            // Text with animated color
            box.e(new ElementText("Click Me", () -> {
                var ctx = getCurrentContext();
                return textColor.get(ctx.getPartialTicks());
            }));

            // Trigger animations on hover
            box.onMouseEnter(() -> {
                bgColor.set(0xFF666666);
                textColor.set(0xFFFFFFFF);
            });

            box.onMouseExit(() -> {
                bgColor.set(0xFF444444);
                textColor.set(0xFFAAAAAA);
            });
        });
    }
}
```

## Why Tick-Based?

1. **Minecraft's standard**: All game logic runs at 20 TPS
2. **Predictable performance**: Animations cost is bounded by tick rate, not frame rate
3. **Smooth rendering**: Partial tick interpolation gives buttery-smooth visuals at any FPS
4. **Tested pattern**: This is exactly how Minecraft animates entities, particles, etc.

Following this pattern ensures your animations integrate seamlessly with Minecraft's rendering pipeline!
