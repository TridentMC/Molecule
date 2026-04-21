# Migration Guide

## Overview

This guide helps migrate existing code to take advantage of the new optimized state binding and animation system.

## Breaking Changes

**Good news: There are NO breaking changes!**

All existing code will continue to work without modifications. The new features are additive.

## What's New

### 1. Typed State Binding

**Before (still works):**
```java
var state = StateImpl.of(value);
scope.bind(state);  // Generic binding
```

**After (optimal):**
```java
var state = StateImpl.of(value);

// Choose based on usage:
scope.bindComposition(state);  // For structural changes
// or
scope.bindLayout(state);       // For layout-only changes
```

**When to use which:**
- `bindComposition(state)`: State affects what elements are rendered (if statements, loops, etc.)
- `bindLayout(state)`: State only affects layout properties (size, position, spacing)
- No binding: State only read during draw (colors, textures, visual effects)

### 2. Deferred Layout Properties

**Before (still works):**
```java
var width = StateImpl.of(100);
scope.bind(width);

box.layout()
    .fixedWidth(width.get());  // Immediate evaluation
```

**After (optimal for animation):**
```java
var width = StateImpl.of(100);
scope.bindLayout(width);  // Layout binding, not composition

box.layout()
    .fixedWidth(() -> width.get());  // Deferred evaluation via supplier
```

**Benefits:**
- No recomposition when width changes
- Only triggers remeasure
- Much better performance for animated properties

### 3. Animation System

**Before (manual animation):**
```java
var width = StateImpl.of(0);
scope.bind(width);

// Manual timer needed to animate
new Timer().schedule(() -> {
    for (int i = 0; i <= 200; i += 10) {
        final int w = i;
        Minecraft.getInstance().execute(() -> width.set(w));
        Thread.sleep(16);
    }
}, 0);
```

**After (built-in animation):**
```java
var width = scope.animateInt(0, 300);  // 300ms duration
scope.bindLayout(width);

// Later, just set target:
width.set(200);  // Automatically animates from current to 200
```

## Migration Strategies

### Strategy 1: Leave It Alone (Zero Effort)

Your existing code works perfectly. No changes needed.

```java
// This code is fine and will continue to work
var state = StateImpl.of(value);
scope.bind(state);
box.layout().fixedWidth(state.get());
```

### Strategy 2: Optimize State Bindings (Low Effort)

Review your state bindings and use typed bindings for better performance.

**Find:**
```java
scope.bind(myState);
```

**Replace with one of:**
```java
scope.bindComposition(myState);  // If used in if/loop/structure
scope.bindLayout(myState);       // If only used in layout properties
// or remove binding entirely if only used in draw
```

**Example:**
```java
// Before
var bgColor = StateImpl.of(0xFF888888);
scope.bind(bgColor);  // Unnecessary recomposition
box.e(new ElementRect(() -> bgColor.get()));

// After
var bgColor = StateImpl.of(0xFF888888);
// No binding needed! Color is read during draw
box.e(new ElementRect(() -> bgColor.get()));
```

### Strategy 3: Add Layout Suppliers (Medium Effort)

For animated or frequently-changing layout properties, use suppliers.

**Before:**
```java
var width = StateImpl.of(100);
scope.bind(width);
box.layout().fixedWidth(width.get());  // Recomposes on every change
```

**After:**
```java
var width = StateImpl.of(100);
scope.bindLayout(width);
box.layout().fixedWidth(() -> width.get());  // Only remeasures
```

**When to do this:**
- Properties that change frequently
- Properties you plan to animate
- Properties where recomposition is expensive

### Strategy 4: Use Animation System (High Value)

Replace manual animations with built-in AnimatedState.

**Before:**
```java
var width = StateImpl.of(0);
scope.bind(width);

// Complex manual animation
Timer timer = new Timer();
timer.scheduleAtFixedRate(new TimerTask() {
    int current = 0;
    int target = 200;
    @Override
    public void run() {
        if (current < target) {
            current += 5;
            Minecraft.getInstance().execute(() -> width.set(current));
        } else {
            this.cancel();
        }
    }
}, 0, 16);
```

**After:**
```java
var width = scope.animateInt(0, 300);  // 300ms duration
scope.bindLayout(width);

// Simple one-liner
width.set(200);  // Automatically animates!
```

## Common Patterns

### Pattern 1: Button Hover Effect

**Before:**
```java
var hovered = StateImpl.of(false);
scope.bind(hovered);

box.e(new ElementRect(() -> hovered.get() ? 0xFFFFFFFF : 0xFF888888));
box.onMouseEnter(() -> hovered.set(true));
box.onMouseExit(() -> hovered.set(false));
```

**After (with animation):**
```java
var color = scope.animateColor(0xFF888888, 150);
// No binding needed for draw-only property

box.e(new ElementRect(() -> color.get()));
box.onMouseEnter(() -> color.set(0xFFFFFFFF));
box.onMouseExit(() -> color.set(0xFF888888));
```

### Pattern 2: Expand/Collapse Panel

**Before:**
```java
var expanded = StateImpl.of(false);
var width = StateImpl.of(50);
scope.bind(expanded);
scope.bind(width);

// Manual size change
btn.onClick(e -> {
    expanded.set(!expanded.get());
    width.set(expanded.get() ? 200 : 50);
});
```

**After:**
```java
var expanded = StateImpl.of(false);
var width = scope.animateInt(50, 300);
scope.bind(expanded);      // Composition binding
scope.bindLayout(width);   // Layout binding

// Animated size change
btn.onClick(e -> {
    expanded.set(!expanded.get());
    width.set(expanded.get() ? 200 : 50);  // Animates automatically
});
```

### Pattern 3: Loading Indicator

**Before:**
```java
// Complex manual color pulsing
var loadingColor = StateImpl.of(0xFF666666);
scope.bind(loadingColor);

Timer timer = new Timer();
AtomicInteger phase = new AtomicInteger(0);
timer.scheduleAtFixedRate(new TimerTask() {
    @Override
    public void run() {
        int p = phase.getAndIncrement() % 60;
        int brightness = (int) (102 + 51 * Math.sin(p * Math.PI / 30));
        int color = 0xFF000000 | (brightness << 16) | (brightness << 8) | brightness;
        Minecraft.getInstance().execute(() -> loadingColor.set(color));
    }
}, 0, 16);
```

**After:**
```java
var loadingColor = scope.animateColor(0xFF666666, 1000, Easing.EASE_IN_OUT);
// No binding needed

// Oscillate between colors
loadingColor.set(0xFFAAAAAA);
// When animation completes, could reverse (would need callback support)
```

## Performance Gains

### Before Optimization

```java
// 1000-node tree with 10 animated states
// Each state change: O(n) tree walk = 1000 nodes checked
// 10 states × 60fps = 600,000 nodes checked per second
// Plus 10 full recompositions per frame = 600 recompositions/sec
```

### After Optimization

```java
// Same 1000-node tree with 10 animated states
// Each state change: O(1) direct notification = 1 node notified
// 10 states × 60fps = 600 notifications per second
// With bindLayout: 0 recompositions, only layout
// ~1000x faster state handling
// ~∞ faster by avoiding recomposition
```

## Troubleshooting

### Issue: State changes don't trigger updates

**Problem:**
```java
var width = scope.animateInt(100, 300);
// No binding!
box.layout().fixedWidth(() -> width.get());
// Width changes but UI doesn't update
```

**Solution:**
```java
var width = scope.animateInt(100, 300);
scope.bindLayout(width);  // Add binding for layout updates
box.layout().fixedWidth(() -> width.get());
```

### Issue: Too many recompositions

**Problem:**
```java
var color = StateImpl.of(0xFF888888);
scope.bind(color);  // Causes recomposition
box.e(new ElementRect(() -> color.get()));
// Recomposes on every color change!
```

**Solution:**
```java
var color = StateImpl.of(0xFF888888);
// Remove binding for draw-only properties
box.e(new ElementRect(() -> color.get()));
// Only redraws, no recomposition
```

### Issue: Animation not smooth

**Problem:**
```java
var width = scope.animateInt(0, 50);  // Duration too short
```

**Solution:**
```java
var width = scope.animateInt(0, 300);  // Better duration for smooth animation
// Or adjust easing:
var width = scope.animateInt(0, 300, Easing.EASE_IN_OUT);
```

## Checklist for Migration

- [ ] Review all `scope.bind()` calls
- [ ] Identify composition vs layout vs draw-only states
- [ ] Update bindings to use typed methods
- [ ] Add suppliers to frequently-changing layout properties
- [ ] Replace manual animations with AnimatedState
- [ ] Test performance improvements
- [ ] Verify no regressions

## Questions?

Common questions:

**Q: Do I have to migrate?**
A: No! Existing code works fine. Migration is optional for better performance.

**Q: What should I migrate first?**
A: Start with animated properties or frequently-changing states for immediate gains.

**Q: Will migration break my code?**
A: No. All changes are backward compatible.

**Q: How much faster will it be?**
A: State changes: ~100-1000x faster. Animations: Eliminates unnecessary recomposition entirely.

**Q: Can I mix old and new styles?**
A: Yes! You can gradually migrate one component at a time.
