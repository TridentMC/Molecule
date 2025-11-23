# Phase 3: Animation System

## Goal
Provide ergonomic animation APIs that integrate seamlessly with the state and layout systems.

## Core Components

### 1. AnimatedState<T>
Self-updating state that interpolates between values over time.

**File: `com/tridevmc/compound/ui/compose/animation/AnimatedState.java`**

```java
package com.tridevmc.compound.ui.compose.animation;

import com.tridevmc.compound.ui.compose.state.State;
import com.tridevmc.compound.ui.compose.state.StateObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * A state that automatically animates to new target values using interpolation.
 *
 * Unlike StateImpl, this state updates itself over time via the AnimationScheduler.
 * Set a new target value and it smoothly animates from current to target.
 */
public class AnimatedState<T> implements State<T> {
    private T currentValue;
    private T targetValue;
    private T startValue;
    private long startTimeNanos;
    private final long durationNanos;
    private final Interpolator<T> interpolator;
    private final Easing easing;
    private final AnimationScheduler scheduler;
    private final List<StateObserver> observers = new ArrayList<>();
    private boolean isAnimating = false;

    public AnimatedState(T initialValue, long durationMillis,
                         Interpolator<T> interpolator, Easing easing,
                         AnimationScheduler scheduler) {
        this.currentValue = initialValue;
        this.targetValue = initialValue;
        this.startValue = initialValue;
        this.durationNanos = durationMillis * 1_000_000L;
        this.interpolator = interpolator;
        this.easing = easing;
        this.scheduler = scheduler;
    }

    @Override
    public T get() {
        return this.currentValue;
    }

    @Override
    public void set(T value) {
        if (Objects.equals(this.targetValue, value)) {
            return;  // Already animating to this value
        }

        // Start new animation
        this.startValue = this.currentValue;
        this.targetValue = value;
        this.startTimeNanos = System.nanoTime();
        this.isAnimating = true;

        this.scheduler.registerAnimation(this);
    }

    @Override
    public void update(Function<T, T> updater) {
        this.set(updater.apply(this.currentValue));
    }

    /**
     * Set value immediately without animation.
     */
    public void setImmediate(T value) {
        this.currentValue = value;
        this.targetValue = value;
        this.startValue = value;
        this.isAnimating = false;
        this.scheduler.unregisterAnimation(this);
        this.notifyObservers();
    }

    /**
     * Called by AnimationScheduler each frame.
     * Package-private.
     */
    void updateAnimation(long currentTimeNanos) {
        if (!this.isAnimating) {
            return;
        }

        long elapsed = currentTimeNanos - this.startTimeNanos;
        float progress = Math.min(1.0f, elapsed / (float) this.durationNanos);

        T newValue;
        if (progress >= 1.0f) {
            // Animation complete
            newValue = this.targetValue;
            this.isAnimating = false;
            this.scheduler.unregisterAnimation(this);
        } else {
            // Interpolate
            float easedProgress = this.easing.apply(progress);
            newValue = this.interpolator.interpolate(this.startValue, this.targetValue, easedProgress);
        }

        // Update and notify if changed
        if (!Objects.equals(this.currentValue, newValue)) {
            this.currentValue = newValue;

            // Only notify if there are observers (i.e., someone called bindLayout())
            if (!this.observers.isEmpty()) {
                this.notifyObservers();
            }
        }
    }

    public boolean isAnimating() {
        return this.isAnimating;
    }

    @Override
    public void addObserver(StateObserver observer) {
        if (!this.observers.contains(observer)) {
            this.observers.add(observer);
        }
    }

    @Override
    public void removeObserver(StateObserver observer) {
        this.observers.remove(observer);
    }

    @Override
    public void dispose() {
        this.observers.clear();
        this.scheduler.unregisterAnimation(this);
    }

    private void notifyObservers() {
        // Create copy to avoid concurrent modification
        List<StateObserver> observersCopy = new ArrayList<>(this.observers);
        for (StateObserver observer : observersCopy) {
            observer.onStateChanged(this);
        }
    }
}
```

### 2. AnimationScheduler
Manages all active animations, updating them each frame.

**File: `com/tridevmc/compound/ui/compose/animation/AnimationScheduler.java`**

```java
package com.tridevmc.compound.ui.compose.animation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages all active animations, updating them each frame.
 * Lives in UITree and is called during layoutAndRender.
 */
public class AnimationScheduler {
    // HashSet is sufficient - animations only updated on main thread
    private final Set<AnimatedState<?>> activeAnimations = new HashSet<>();

    /**
     * Register an animation to be updated each frame.
     */
    void registerAnimation(AnimatedState<?> animation) {
        this.activeAnimations.add(animation);
    }

    /**
     * Unregister an animation (called when animation completes).
     */
    void unregisterAnimation(AnimatedState<?> animation) {
        this.activeAnimations.remove(animation);
    }

    /**
     * Update all active animations.
     * Called by UITree at the start of each frame.
     */
    public void updateAnimations() {
        if (this.activeAnimations.isEmpty()) {
            return;  // Fast path - no animations
        }

        long now = System.nanoTime();

        // Copy to avoid concurrent modification during iteration
        for (AnimatedState<?> animation : new ArrayList<>(this.activeAnimations)) {
            animation.updateAnimation(now);
        }
    }

    /**
     * Check if there are any active animations.
     */
    public boolean hasActiveAnimations() {
        return !this.activeAnimations.isEmpty();
    }

    /**
     * Clear all animations (called on cleanup).
     */
    public void dispose() {
        this.activeAnimations.clear();
    }

    /**
     * Get count of active animations (for debugging).
     */
    public int getActiveAnimationCount() {
        return this.activeAnimations.size();
    }
}
```

### 3. Interpolator<T>
Interface for interpolating between values.

**File: `com/tridevmc/compound/ui/compose/animation/Interpolator.java`**

```java
package com.tridevmc.compound.ui.compose.animation;

/**
 * Interpolates between two values based on progress.
 *
 * @param <T> the type of value to interpolate
 */
@FunctionalInterface
public interface Interpolator<T> {
    /**
     * Interpolate between start and end values.
     *
     * @param start the starting value
     * @param end the ending value
     * @param progress the interpolation progress (0.0 to 1.0, already eased)
     * @return the interpolated value
     */
    T interpolate(T start, T end, float progress);
}
```

### 4. Built-in Interpolators

**File: `com/tridevmc/compound/ui/compose/animation/Interpolators.java`**

```java
package com.tridevmc.compound.ui.compose.animation;

/**
 * Common interpolators for basic types.
 */
public class Interpolators {

    public static final Interpolator<Float> FLOAT = (start, end, progress) -> {
        return start + (end - start) * progress;
    };

    public static final Interpolator<Integer> INT = (start, end, progress) -> {
        return Math.round(start + (end - start) * progress);
    };

    /**
     * Interpolates ARGB color values, handling each channel independently.
     */
    public static final Interpolator<Integer> COLOR = (start, end, progress) -> {
        int startA = (start >> 24) & 0xFF;
        int startR = (start >> 16) & 0xFF;
        int startG = (start >> 8) & 0xFF;
        int startB = start & 0xFF;

        int endA = (end >> 24) & 0xFF;
        int endR = (end >> 16) & 0xFF;
        int endG = (end >> 8) & 0xFF;
        int endB = end & 0xFF;

        int a = (int) (startA + (endA - startA) * progress);
        int r = (int) (startR + (endR - startR) * progress);
        int g = (int) (startG + (endG - startG) * progress);
        int b = (int) (startB + (endB - startB) * progress);

        return (a << 24) | (r << 16) | (g << 8) | b;
    };

    private Interpolators() {}
}
```

### 5. Easing Functions

**File: `com/tridevmc/compound/ui/compose/animation/Easing.java`**

```java
package com.tridevmc.compound.ui.compose.animation;

/**
 * Easing functions for animation curves.
 * Takes linear progress (0.0 to 1.0) and returns eased progress.
 */
@FunctionalInterface
public interface Easing {
    /**
     * Apply easing to linear progress.
     *
     * @param t linear progress from 0.0 to 1.0
     * @return eased progress from 0.0 to 1.0
     */
    float apply(float t);

    // Common easing functions
    Easing LINEAR = t -> t;

    Easing EASE_IN = t -> t * t;

    Easing EASE_OUT = t -> t * (2 - t);

    Easing EASE_IN_OUT = t -> t < 0.5f
        ? 2 * t * t
        : -1 + (4 - 2 * t) * t;

    Easing EASE_IN_CUBIC = t -> t * t * t;

    Easing EASE_OUT_CUBIC = t -> {
        float f = t - 1;
        return f * f * f + 1;
    };

    Easing EASE_IN_OUT_CUBIC = t -> t < 0.5f
        ? 4 * t * t * t
        : (t - 1) * (2 * t - 2) * (2 * t - 2) + 1;
}
```

## Integration with UITree

**Modify `UITree.java`:**

```java
public class UITree {
    private AnimationScheduler animationScheduler = new AnimationScheduler();

    public AnimationScheduler getAnimationScheduler() {
        return this.animationScheduler;
    }

    public void layoutAndRender(int width, int height, IScreenContext context) {
        if (this.root == null) {
            return;
        }

        // Update animations FIRST (updates state values)
        this.animationScheduler.updateAnimations();

        // Check if window size changed
        var sizeChanged = this.lastWindowSize.width() != width ||
                         this.lastWindowSize.height() != height;

        // IMPORTANT: Do NOT check hasActiveAnimations() here!
        // Layout state bindings already trigger requestRemeasure() when needed.
        // Checking hasActiveAnimations() would remeasure the ENTIRE tree
        // for every animation, even draw-only color animations.
        if (sizeChanged) {
            this.lastWindowSize = new Size(width, height);
            var constraints = Constraints.loose(width, height);
            this.measureTree(constraints);
            this.placeTree(Position.ORIGIN, constraints);
        }

        this.renderTree(context);
    }

    // ... rest of UITree
}
```

## Integration with Composition Scopes

**Add to `ICompositionScope.java`:**

```java
/**
 * Create an animated float state.
 */
default AnimatedState<Float> animateFloat(float initialValue, long durationMs) {
    return animateFloat(initialValue, durationMs, Easing.EASE_IN_OUT);
}

default AnimatedState<Float> animateFloat(float initialValue, long durationMs, Easing easing) {
    AnimationScheduler scheduler = getTree().getAnimationScheduler();
    return new AnimatedState<>(initialValue, durationMs, Interpolators.FLOAT, easing, scheduler);
}

/**
 * Create an animated integer state.
 */
default AnimatedState<Integer> animateInt(int initialValue, long durationMs) {
    return animateInt(initialValue, durationMs, Easing.EASE_IN_OUT);
}

default AnimatedState<Integer> animateInt(int initialValue, long durationMs, Easing easing) {
    AnimationScheduler scheduler = getTree().getAnimationScheduler();
    return new AnimatedState<>(initialValue, durationMs, Interpolators.INT, easing, scheduler);
}

/**
 * Create an animated color state (ARGB).
 */
default AnimatedState<Integer> animateColor(int initialValue, long durationMs) {
    return animateColor(initialValue, durationMs, Easing.EASE_IN_OUT);
}

default AnimatedState<Integer> animateColor(int initialValue, long durationMs, Easing easing) {
    AnimationScheduler scheduler = getTree().getAnimationScheduler();
    return new AnimatedState<>(initialValue, durationMs, Interpolators.COLOR, easing, scheduler);
}

/**
 * Get the UITree for accessing the animation scheduler.
 */
UITree getTree();
```

**Implement in all scope classes (RootScope, ContainerScope, ComposableElementScope):**

```java
@Override
public UITree getTree() {
    return this.tree;
}
```

## Usage Examples

See `04-USAGE-EXAMPLES.md` for detailed usage patterns.

## Performance Notes

1. **Zero overhead when idle**: If no animations are active, `updateAnimations()` is a single isEmpty() check
2. **Frame-aligned**: Uses System.nanoTime() for smooth, frame-independent animation
3. **Efficient updates**: Only notifies observers if value actually changed
4. **Optional binding**: Animations work without binding (for draw-only properties)
5. **No unnecessary remeasures**: Layout bindings handle selective remeasures, not global check

## Future Enhancements

Potential additions (not in initial implementation):
- Spring physics interpolator
- Chained animations
- Animation callbacks (onComplete, onUpdate)
- Keyframe animations
- Custom interpolators via lambda
