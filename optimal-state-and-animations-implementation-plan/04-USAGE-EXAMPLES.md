# Usage Examples

## Example 1: Simple Color Animation (Draw-only)

```java
@Override
protected void compose(RootScope scope) {
    // Create animated color state (200ms duration)
    var buttonColor = scope.animateColor(0xFF888888, 200);

    // NO binding needed - color is read during draw phase

    scope.e(new Button("Hover Me"), btn -> {
        // Background color rectangle
        btn.e(new ElementRect(() -> buttonColor.get()));

        // Animate on hover
        btn.onMouseEnter(() -> {
            buttonColor.set(0xFFFFFFFF);  // Fade to white
        });
        btn.onMouseExit(() -> {
            buttonColor.set(0xFF888888);  // Fade back to gray
        });

        btn.e(new ElementLabel("Hover Me"));
    });
}
```

**Performance**: No recomposition, no layout. Only redraw.

---

## Example 2: Width Animation (Layout)

```java
@Override
protected void compose(RootScope scope) {
    var expanded = StateImpl.of(false);
    var panelWidth = scope.animateInt(0, 300);  // 300ms

    // Bind as layout state (triggers remeasure, not recomposition)
    scope.bindLayout(panelWidth);

    // Also bind expanded for conditional rendering
    scope.bind(expanded);

    scope.e(new Row(), row -> {
        // Animated side panel
        row.e(new Box(), panel -> {
            panel.layout()
                .fixedWidth(() -> panelWidth.get())  // Deferred evaluation
                .fillMaxHeight();

            if (expanded.get()) {
                panel.e(new ElementLabel("Panel Content"));
            }
        });

        // Toggle button
        row.e(new Button("Toggle"), btn -> {
            btn.onClick(e -> {
                expanded.set(!expanded.get());
                panelWidth.set(expanded.get() ? 200 : 0);
            });
        });
    });
}
```

**Performance**: Remeasures during animation, but no recomposition (except when expanded changes).

---

## Example 3: Complex Multi-Property Animation

```java
@Override
protected void compose(RootScope scope) {
    var isOpen = StateImpl.of(false);
    var width = scope.animateInt(50, 250);
    var height = scope.animateInt(50, 250);
    var padding = scope.animateInt(5, 250);
    var bgColor = scope.animateColor(0xFF333333, 250);

    // Bind layout properties
    scope.bindLayout(width);
    scope.bindLayout(height);
    scope.bindLayout(padding);
    // bgColor not bound - draw only

    scope.bind(isOpen);

    scope.e(new Box(), box -> {
        // Background with animated color
        box.e(new ElementRect(() -> bgColor.get()));

        // Content with animated layout
        box.layout()
            .fixedWidth(() -> width.get())
            .fixedHeight(() -> height.get())
            .padding(() -> padding.get());

        if (isOpen.get()) {
            box.e(new Column(), col -> {
                col.e(new ElementLabel("Expanded!"));
                col.e(new Button("Close"), btn -> {
                    btn.onClick(e -> {
                        isOpen.set(false);
                        // Animate everything simultaneously
                        width.set(50);
                        height.set(50);
                        padding.set(5);
                        bgColor.set(0xFF333333);
                    });
                });
            });
        } else {
            box.e(new Button("+"), btn -> {
                btn.onClick(e -> {
                    isOpen.set(true);
                    width.set(200);
                    height.set(150);
                    padding.set(20);
                    bgColor.set(0xFF666666);
                });
            });
        }
    });
}
```

**Performance**:
- Color animates in draw phase only
- Width/height/padding trigger layout
- Only recomposes when isOpen changes (structure change)

---

## Example 4: Staggered Animations

```java
@Override
protected void compose(RootScope scope) {
    var showItems = StateImpl.of(false);
    scope.bind(showItems);

    scope.e(new Column(), col -> {
        col.layout().spacing(10);

        scope.e(new Button("Show Items"), btn -> {
            btn.onClick(e -> showItems.set(!showItems.get()));
        });

        if (showItems.get()) {
            // Create 5 items with staggered animations
            for (int i = 0; i < 5; i++) {
                final int index = i;

                var opacity = scope.animateInt(0, 300);
                var offsetX = scope.animateInt(-50, 300);
                scope.bindLayout(offsetX);

                col.e(new Box(), item -> {
                    item.layout()
                        .fixedWidth(() -> 200)
                        .fixedHeight(40)
                        .marginLeft(() -> offsetX.get());

                    // Animate with delay
                    new Thread(() -> {
                        try {
                            Thread.sleep(index * 100);  // Stagger by 100ms
                            opacity.set(255);
                            offsetX.set(0);
                        } catch (InterruptedException ex) {}
                    }).start();

                    item.e(new ElementRect(() -> {
                        int alpha = opacity.get();
                        return (alpha << 24) | 0x00FFFFFF;  // White with animated alpha
                    }));

                    item.e(new ElementLabel("Item " + (index + 1)));
                });
            }
        }
    });
}
```

**Note**: In production, you'd use a proper scheduler instead of Thread.sleep, but this demonstrates the concept.

---

## Example 5: Easing Comparison

```java
@Override
protected void compose(RootScope scope) {
    scope.e(new Column(), col -> {
        col.layout().spacing(20).padding(20);

        Easing[] easings = {
            Easing.LINEAR,
            Easing.EASE_IN,
            Easing.EASE_OUT,
            Easing.EASE_IN_OUT,
            Easing.EASE_IN_CUBIC,
            Easing.EASE_OUT_CUBIC
        };

        String[] names = {
            "Linear",
            "Ease In",
            "Ease Out",
            "Ease In Out",
            "Cubic In",
            "Cubic Out"
        };

        for (int i = 0; i < easings.length; i++) {
            final Easing easing = easings[i];
            final String name = names[i];

            col.e(new Row(), row -> {
                row.layout().spacing(10);

                var x = scope.animateInt(0, 1000, easing);
                scope.bindLayout(x);

                row.e(new ElementLabel(name));

                row.e(new Box(), box -> {
                    box.layout()
                        .fixedWidth(300)
                        .fixedHeight(20);

                    // Moving dot
                    box.e(new Box(), dot -> {
                        dot.layout()
                            .fixedSize(20, 20)
                            .marginLeft(() -> x.get());
                        dot.e(new ElementRect(() -> 0xFFFF0000));
                    });
                });

                row.e(new Button("Animate"), btn -> {
                    btn.onClick(e -> {
                        x.set(x.get() == 0 ? 280 : 0);
                    });
                });
            });
        }
    });
}
```

---

## Example 6: Loading Spinner

```java
@Override
protected void compose(RootScope scope) {
    var rotation = scope.animateFloat(0f, 1000, Easing.LINEAR);
    // Note: Would need graphicsLayer support for rotation
    // For now, this demonstrates continuous animation

    scope.e(new Box(), spinner -> {
        spinner.layout().fixedSize(50, 50);

        // Draw rotating spinner (pseudo-code, would need rotation support)
        spinner.e(new ElementSprite(...));

        // Continuous animation
        rotation.set(360f);
        // When animation completes, restart
        // (Would need onComplete callback in real implementation)
    });
}
```

---

## Example 7: Smooth Scrolling

```java
@Override
protected void compose(RootScope scope) {
    var scrollOffset = scope.animateInt(0, 200);
    scope.bindLayout(scrollOffset);

    scope.e(new ScrollContainer(), scroll -> {
        scroll.layout().fillMax();

        // Override scroll handling for smooth animation
        scroll.onScroll(event -> {
            int currentScroll = scrollOffset.get();
            int delta = (int) (event.scrollDelta() * 20);
            scrollOffset.set(Math.max(0, currentScroll + delta));
            event.consume();
        });

        // Content...
        for (int i = 0; i < 100; i++) {
            scroll.e(new ElementLabel("Item " + i));
        }
    });
}
```

---

## Best Practices

### 1. Choose the Right Binding Type

```java
// Draw-only (color, alpha, visual effects)
var color = scope.animateColor(...);
// NO binding needed

// Layout properties (size, position, spacing)
var width = scope.animateInt(...);
scope.bindLayout(width);  // Triggers layout, not composition

// Structural changes (conditional rendering, list changes)
var showContent = StateImpl.of(false);
scope.bind(showContent);  // Triggers full recomposition
```

### 2. Use Appropriate Durations

```java
// Fast: UI feedback (100-200ms)
var hoverColor = scope.animateColor(initial, 150);

// Medium: Panel animations (200-400ms)
var panelWidth = scope.animateInt(0, 300);

// Slow: Page transitions (400-600ms)
var pageOpacity = scope.animateFloat(0f, 500);
```

### 3. Choose Easing Wisely

```java
// Linear: Continuous animations (spinners, progress bars)
var rotation = scope.animateFloat(0f, 1000, Easing.LINEAR);

// Ease Out: Appearing elements (feels snappy)
var opacity = scope.animateFloat(0f, 200, Easing.EASE_OUT);

// Ease In Out: Size changes (smooth both ways)
var width = scope.animateInt(100, 300, Easing.EASE_IN_OUT);
```

### 4. Avoid Over-Animation

```java
// DON'T: Animate everything
var x = scope.animateInt(...);
var y = scope.animateInt(...);
var width = scope.animateInt(...);
var height = scope.animateInt(...);
var rotation = scope.animateFloat(...);
// Too much!

// DO: Animate 1-2 key properties
var scale = scope.animateFloat(1f, 200);
// Simple and effective
```

### 5. Cleanup Not Usually Needed

States are automatically cleaned up when nodes are detached. Only manually dispose if creating states outside composition:

```java
// In composition - automatic cleanup
var width = scope.animateInt(0, 300);

// Outside composition - manual cleanup needed
AnimatedState<Integer> width = new AnimatedState<>(...);
// Later:
width.dispose();
```
