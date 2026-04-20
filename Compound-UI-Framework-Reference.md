# Compound UI Framework Reference

## Overview

The Compound UI framework is a modern, declarative UI system for Minecraft mod development. It uses a composition-based architecture with three distinct element categories, reactive state management, and a flexible layout system.

## Architecture Concepts

### Element Types

The framework categorizes elements into three distinct types, each with a specific purpose:

#### 1. Primitive Elements (`IPrimitiveElement`)
**Purpose**: Draw pixels to the screen
**Characteristics**:
- Cannot have children
- Implement `draw(IScreenContext)` method
- Extend `BasePrimitiveElement` for convenience
- Used for visual-only elements

**When to use**: For simple visual components that don't need to contain other elements

**Examples**: `ElementRect`, `ElementLabel`, `ElementSprite`, `ElementItem`

```java
// Creating a primitive element
scope.e(new ElementLabel(Component.literal("Hello World")), label -> {
    label.layout()
        .fixedSize(100, 20)
        .padding(10);
});
```

#### 2. Composable Elements (`IComposableElement`)
**Purpose**: Smart components with internal behavior and composition
**Characteristics**:
- Have internal composition via `compose(ICompositionScope)`
- Appear as single elements externally
- Can manage state and handle events
- Can have internal children via composition

**When to use**: For reusable components that encapsulate complex behavior

**Examples**: `Button`, `ScrollArea`, custom components

```java
// Creating a composable element
scope.e(new Button(), button -> {
    button.layout().fixedSize(100, 40);
    button.fillSlot(Button.CONTENT_SLOT, content -> {
        content.e(new ElementLabel(Component.literal("Click me")));
    });
});
```

#### 3. Container Elements (`IContainer`)
**Purpose**: Layout management for child elements
**Characteristics**:
- Extend `IGenericContainer` marker interface
- Implement `measure()` and `place()` for layout calculations
- Extend `BaseContainer` for convenience

**When to use**: For organizing elements in specific layouts

**Examples**: `Column`, `Row`, `Stack`, `Box`, `Grid`

```java
// Creating a container
scope.e(new Column(), column -> {
    column.layout()
        .fillMax()
        .spacing(10)
        .padding(20);

    column.e(new ElementLabel(Component.literal("Item 1")));
    column.e(new ElementLabel(Component.literal("Item 2")));
});
```

### Base Classes

#### BaseElement
Common functionality for all elements:
- Manages `UITree` node reference
- Implements lifecycle methods (`onAttached`, `onDetached`)
- Abstract `measure()` and `place()` methods
- Default bounds management

#### BasePrimitiveElement
Convenience base for primitives:
- Provides default `place()` implementation (empty list for no children)
- Wraps `drawElement()` with visibility checks
- Handles lifecycle for draw-only elements

#### BaseContainer
Marker base for containers:
- Currently minimal, just indicates container behavior
- Future: common container functionality

## Composition System

The composition system is declarative and hierarchical.

### Composition Scopes

#### ICompositionScope
Main interface for composition:
- `e(element, configurator)` - Add an element
- `bind(state)` - Trigger recomposition on state change
- `bindLayout(state)` - Trigger remeasurement on state change
- Event handlers: `onClick()`, `onScroll()`, `onMouseEnter()`, etc.

#### Specialized Scopes
- `IElementScope` - For primitive elements
- `IContainerScope` - For containers (adds `layout()` access)
- `IComposableElementScope` - For composable elements
- `IGenericElementScope` - Type-safe scope for element-specific configuration
- `RootScope` - Top-level scope for entire UI

### Composition Process

1. Elements are added via `scope.e(element, configurator)`
2. The configurator lambda receives the appropriate scope based on element type
3. Element properties are configured through the scope
4. Layout properties are set via `element.layout().property()`

```java
scope.e(new Column(), column -> {
    column.layout()                    // LayoutProperties access
        .fillMaxWidth()                // Size constraints
        .spacing(10)                   // Spacing between children
        .padding(20)                   // Internal padding
        .contentAlignment(Alignment.CENTER);  // Child alignment

    column.e(new Button(), button -> { // Nested composition
        button.layout().fixedSize(100, 40);
        // ... button configuration
    });
});
```

### Slot System

Slots allow composable elements to define replaceable content areas:

```java
// Defining slots in a composable element
public class MyPanel extends BaseElement implements IComposableElement {
    public static final SlotKey HEADER_SLOT = new SlotKey("header");
    public static final SlotKey CONTENT_SLOT = new SlotKey("content");

    @Override
    public void compose(ICompositionScope scope) {
        scope.e(new Column(), column -> {
            // Header slot with fallback
            column.slot(HEADER_SLOT, fallback -> {
                fallback.e(new ElementLabel(Component.literal("Default Header")));
            });

            // Main content slot
            column.slot(CONTENT_SLOT);
        });
    }
}

// Using slots
scope.e(new MyPanel(), panel -> {
    panel.fillSlot(MyPanel.HEADER_SLOT, header -> {
        header.e(new ElementLabel(Component.literal("Custom Header")));
    });

    panel.fillSlot(MyPanel.CONTENT_SLOT, content -> {
        content.e(new MyComplexContent());
    });
});
```

## Layout System

The layout system uses a two-phase approach: measurement then placement.

### Phase 1: Measurement (Bottom-up)
Elements calculate their intrinsic size based on:
- Constraints from parent
- Layout properties
- Measured sizes of children

```java
@Override
public Size measure(Constraints constraints, LayoutProperties props, List<Size> children) {
    // Calculate desired size within constraints
    int width = Math.min(calculatedWidth, constraints.maxWidth());
    int height = Math.min(calculatedHeight, constraints.maxHeight());
    return new Size(width, height);
}
```

### Phase 2: Placement (Top-down)
Parents calculate bounds for children based on:
- Parent bounds
- Layout properties (alignment, spacing, etc.)
- Measured sizes of children

```java
@Override
public List<Bounds> place(Bounds bounds, LayoutProperties props, List<Size> children) {
    List<Bounds> childBounds = new ArrayList<>();
    int currentY = bounds.y();

    for (Size childSize : children) {
        childBounds.add(new Bounds(bounds.x(), currentY, childSize.width(), childSize.height()));
        currentY += childSize.height() + props.getSpacing();
    }

    return childBounds;
}
```

### Constraints

Define available space for measurement:

```java
// Creating constraints
Constraints.unbounded()          // No limits (infinity)
Constraints.fixed(200, 100)      // Exact size required
Constraints.loose(200, 100)      // Up to this size
Constraints.tight(200, 100)      // Exactly this size
```

### Layout Properties

Configuration for element layout:

#### Size Constraints
```java
element.layout()
    .fixedSize(width, height)     // Exact size
    .fixedWidth(width)            // Fixed width, flexible height
    .fillMax()                    // Fill parent in both dimensions
    .fillMaxWidth()               // Fill parent width
    .minWidth/minHeight(value)    // Minimum size
    .maxWidth/maxHeight(value)    // Maximum size
```

#### Spacing
```java
element.layout()
    .padding(all)                 // All sides
    .padding(horizontal, vertical) // Horizontal/vertical
    .padding(top, right, bottom, left) // Individual sides
    .margin(...)                  // External spacing (same options)
```

#### Alignment
```java
element.layout()
    .contentAlignment(Alignment.CENTER)  // Align content within
    .horizontalAlignment(Alignment.START) // Horizontal alignment
    .verticalAlignment(Alignment.END)    // Vertical alignment
```

#### Container-specific
```java
column.layout().spacing(10);       // Space between children
stack.layout().clip(true);         // Enable clipping
```

## State Management

### State Interface

Observable state containers that trigger updates:

```java
// Creating state
State<String> text = State.of("Hello");
State<Integer> count = State.of(0);
State<Boolean> visible = State.of(true);

// Using state
String current = text.get();        // Get current value
text.set("New value");              // Set new value
count.update(c -> c + 1);           // Update with function
```

### State Binding

#### Full Recomposition (`bind`)
Triggers complete recomposition when state changes:

```java
State<String> username = State.of("");

@Override
public void compose(ICompositionScope scope) {
    scope.bind(username);  // Recompose when username changes

    scope.e(new ElementLabel(Component.literal("User: " + username.get())));
}
```

#### Layout-only (`bindLayout`)
Triggers remeasurement only, more efficient for layout changes:

```java
State<Integer> columns = State.of(2);

@Override
public void compose(ICompositionScope scope) {
    scope.bindLayout(columns);  // Remeasure when columns change

    scope.e(new Grid(columns.get(), 1), grid -> {
        // Grid content...
    });
}
```

### Animated States

Built-in animation support:

```java
// Create animated state
AnimatedState<Float> rotation = scope.animateFloat(0f, 500,
    Interpolators.FLOAT, Easing.OUT_QUART);

// Animate to new value
rotation.set(360f);  // Will animate over 500ms

// Looping animation
AnimatedState<Color> color = scope.animateColor(
    0xFF0000, 0x0000FF, 1000,  // Red to Blue over 1 second
    Easing.IN_OUT_SINE, true   // Loop forever
);

// Use animated value in drawing
float currentRotation = rotation.get(partialTicks);
```

## Event System

### Event Types
- **Mouse**: `MouseClickEvent`, `MouseMoveEvent`, `MouseScrollEvent`, `MouseDragEvent`
- **Keyboard**: `KeyInputEvent`, `CharEvent`
- **Focus**: `FocusGained`, `FocusLost`

### Event Handlers

```java
scope.onClick(event -> {
    int x = event.x();
    int y = event.y();
    // Handle click
    return true;  // Consume event
});

scope.onScroll(event -> {
    double delta = event.scrollDelta();
    // Handle scroll
    return false;  // Let parent handle
});

scope.onMouseEnter(() -> {
    hovered.set(true);
});

scope.onMouseExit(() -> {
    hovered.set(false);
});

scope.onKey(event -> {
    if (event.key() == GLFW_KEY_ENTER) {
        // Handle enter key
        return true;
    }
    return false;
});
```

### Event Bubbling
Events bubble up the tree until consumed:
- Handlers return `true` to consume and stop bubbling
- Handlers return `false` to continue bubbling
- Order: Target → Parent → Grandparent → ...

## Implementing Elements

### Creating a Primitive Element

```java
public class ElementCircle extends BasePrimitiveElement {
    private final Supplier<Integer> colorSupplier;
    private final Supplier<Float> radiusSupplier;

    public ElementCircle(int color, float radius) {
        this(() -> color, () -> radius);
    }

    public ElementCircle(Supplier<Integer> colorSupplier, Supplier<Float> radiusSupplier) {
        this.colorSupplier = colorSupplier;
        this.radiusSupplier = radiusSupplier;
    }

    @Override
    public Size measure(Constraints constraints, LayoutProperties props, List<Size> children) {
        float radius = radiusSupplier.get();
        int diameter = (int) (radius * 2);

        // Respect constraints
        diameter = Math.min(diameter, constraints.maxWidth());
        diameter = Math.min(diameter, constraints.maxHeight());

        return new Size(diameter, diameter);
    }

    @Override
    protected void drawElement(IScreenContext context, Bounds bounds) {
        int color = colorSupplier.get();
        float radius = radiusSupplier.get();

        // Draw circle using context methods
        context.fillCircle(
            bounds.x() + bounds.width() / 2,
            bounds.y() + bounds.height() / 2,
            Math.min(bounds.width(), bounds.height()) / 2,
            color
        );
    }
}
```

### Creating a Container Element

```java
public class FlowLayout extends BaseContainer {
    private final int horizontalSpacing;
    private final int verticalSpacing;

    public FlowLayout() {
        this(5, 5);
    }

    public FlowLayout(int horizontalSpacing, int verticalSpacing) {
        this.horizontalSpacing = horizontalSpacing;
        this.verticalSpacing = verticalSpacing;
    }

    @Override
    public Size measure(Constraints constraints, LayoutProperties props, List<Size> children) {
        if (children.isEmpty()) {
            return new Size(0, 0);
        }

        int maxWidth = constraints.maxWidth();
        int currentX = 0;
        int currentY = 0;
        int rowHeight = 0;

        for (Size child : children) {
            if (currentX + child.width() > maxWidth && currentX > 0) {
                // Wrap to next line
                currentX = 0;
                currentY += rowHeight + verticalSpacing;
                rowHeight = 0;
            }

            currentX += child.width() + horizontalSpacing;
            rowHeight = Math.max(rowHeight, child.height());
        }

        return new Size(maxWidth, currentY + rowHeight);
    }

    @Override
    public List<Bounds> place(Bounds bounds, LayoutProperties props, List<Size> children) {
        List<Bounds> result = new ArrayList<>();

        int currentX = bounds.x();
        int currentY = bounds.y();
        int rowHeight = 0;

        for (Size child : children) {
            if (currentX + child.width() > bounds.x() + bounds.width() && currentX > bounds.x()) {
                // Wrap to next line
                currentX = bounds.x();
                currentY += rowHeight + verticalSpacing;
                rowHeight = 0;
            }

            result.add(new Bounds(currentX, currentY, child.width(), child.height()));
            currentX += child.width() + horizontalSpacing;
            rowHeight = Math.max(rowHeight, child.height());
        }

        return result;
    }
}
```

### Creating a Composable Element

```java
public class Card extends BaseElement implements IComposableElement {
    public static final SlotKey CONTENT_SLOT = new SlotKey("content");
    public static final SlotKey ACTIONS_SLOT = new SlotKey("actions");

    private final State<Boolean> hovered = State.of(false);
    private final State<Boolean> elevated = State.of(false);

    @Override
    public void compose(ICompositionScope scope) {
        // Bind states for recomposition
        scope.bind(hovered);
        scope.bind(elevated);

        // Mouse interactions
        scope.onMouseEnter(() -> {
            hovered.set(true);
            elevated.set(true);
        });

        scope.onMouseExit(() -> {
            hovered.set(false);
            elevated.set(false);
        });

        // Compose internal structure
        scope.e(new Stack(), stack -> {
            stack.layout().fillMax();

            // Background
            int bgColor = elevated.get() ? 0xFF333333 : 0xFF222222;
            if (hovered.get()) {
                bgColor = 0xFF444444;
            }

            stack.e(new ElementRect(bgColor), bg -> {
                bg.layout().fillMax();
            });

            // Content column
            stack.e(new Column(), column -> {
                column.layout().fillMax().padding(16);

                // Main content slot
                column.slot(CONTENT_SLOT);

                // Spacer
                column.e(new ElementSpacer(), spacer -> {
                    spacer.layout().fixedHeight(16);
                });

                // Actions row
                column.e(new Row(), row -> {
                    row.layout()
                        .fillMaxWidth()
                        .contentAlignment(Alignment.END);

                    row.slot(ACTIONS_SLOT);
                });
            });
        });
    }

    @Override
    public Size measure(Constraints constraints, LayoutProperties props, List<Size> children) {
        // Card size determined by content
        if (!children.isEmpty()) {
            return children.get(0);
        }
        return new Size(200, 100); // Default size
    }

    @Override
    public List<Bounds> place(Bounds bounds, LayoutProperties props, List<Size> children) {
        // Single child fills the card
        if (!children.isEmpty()) {
            return List.of(bounds);
        }
        return List.of();
    }
}
```

## Best Practices

### 1. Choose the Right Element Type
- **Primitive**: For simple visual elements without children
- **Container**: For layout management only
- **Composable**: For components with behavior and internal structure

### 2. Efficient State Management
- Use `bindLayout()` for layout-affecting changes
- Use `bind()` only when composition needs to change
- Keep state as local as possible
- Use `AnimatedState` for smooth transitions

### 3. Layout Performance
- Avoid deep nesting when possible
- Use constraints efficiently to limit measurement
- Implement `measure()` and `place()` correctly to prevent infinite loops
- Use `clip()` for ScrollArea and other clipping containers

### 4. Event Handling
- Return correct boolean to control event bubbling
- Use specific event handlers (e.g., `onClick` vs `onMouseClick`)
- Clean up event listeners in `onDetached()`

### 5. Composition Patterns
- Use slots for customizable content
- Keep composition functions pure and deterministic
- Avoid side effects in `measure()` and `place()`
- Use configurator lambdas for clean element configuration

## Common Patterns

### Stateful Component
```java
public class Counter extends BaseElement implements IComposableElement {
    private final State<Integer> count = State.of(0);

    @Override
    public void compose(ICompositionScope scope) {
        scope.bind(count);

        scope.e(new Column(), column -> {
            column.layout().contentAlignment(Alignment.CENTER);

            column.e(new ElementLabel(Component.literal("Count: " + count.get())));

            column.e(new Row(), row -> {
                row.layout().spacing(10);

                row.e(new Button(), button -> {
                    button.fillSlot(Button.CONTENT_SLOT, content -> {
                        content.e(new ElementLabel(Component.literal("-")));
                    });
                    button.onClick(event -> {
                        count.update(c -> Math.max(0, c - 1));
                        return true;
                    });
                });

                row.e(new Button(), button -> {
                    button.fillSlot(Button.CONTENT_SLOT, content -> {
                        content.e(new ElementLabel(Component.literal("+")));
                    });
                    button.onClick(event -> {
                        count.update(c -> c + 1);
                        return true;
                    });
                });
            });
        });
    }

    // measure() and place() implementations...
}
```

### Conditional Rendering
```java
State<Boolean> showDetails = State.of(false);

scope.e(new Column(), column -> {
    column.e(new Button(), button -> {
        button.fillSlot(Button.CONTENT_SLOT, content -> {
            content.e(new ElementLabel(
                Component.literal(showDetails.get() ? "Hide" : "Show")
            ));
        });
        button.onClick(event -> {
            showDetails.set(!showDetails.get());
            return true;
        });
    });

    // Conditional content
    if (showDetails.get()) {
        column.e(new ElementLabel(Component.literal("Detailed information")));
    }
});
```

### List Rendering
```java
State<List<String>> items = State.of(List.of("Item 1", "Item 2", "Item 3"));

scope.e(new Column(), column -> {
    column.bind(items);  // Recompose when list changes

    for (String item : items.get()) {
        column.e(new ElementLabel(Component.literal(item)), label -> {
            label.layout().fillMaxWidth();
        });
    }
});
```

### Custom Layout with Animation
```java
public class AnimatedAccordion extends BaseElement implements IComposableElement {
    private final State<Boolean> expanded = State.of(false);
    private final AnimatedState<Float> height = null;  // Set in compose

    @Override
    public void compose(ICompositionScope scope) {
        // Initialize animated state
        if (height == null) {
            height = scope.animateFloat(0f, 300, Interpolators.FLOAT, Easing.OUT_QUART);
        }

        scope.bind(expanded);

        scope.onMouseClick(event -> {
            expanded.set(!expanded.get());
            height.set(expanded.get() ? 200f : 0f);
            return true;
        });

        scope.e(new Column(), column -> {
            // Header
            column.e(new ElementRect(0xFF333333), header -> {
                header.layout().fillMaxWidth().fixedHeight(40);
            });

            // Animated content area
            column.e(new Box(), box -> {
                box.layout()
                    .fillMaxWidth()
                    .fixedHeight(height.get());

                // Content with opacity based on expansion
                float opacity = expanded.get() ? 1f : 0f;
                box.e(new ElementLabel(Component.literal("Accordion Content")), label -> {
                    label.layout().padding(20);
                    // Apply opacity in real implementation
                });
            });
        });
    }
}
```

## Integration with Minecraft

### Creating a UI Screen
```java
public class MyUIScreen extends ComposedUI {
    @Override
    protected void compose(RootScope scope) {
        scope.e(new Column(), column -> {
            column.layout().fillMax().padding(20);

            column.e(new ElementLabel(
                Component.literal("My UI Screen").withStyle(TextStyle.BOLD)
            ), title -> {
                title.layout().contentAlignment(Alignment.CENTER);
            });

            column.e(new ElementSpacer(), spacer -> {
                spacer.layout().fixedHeight(20);
            });

            // Add more UI elements...
        });
    }
}
```

### Container Menu Integration
```java
public class MyContainerMenu extends AbstractContainerMenu {
    private final ComposedUIContainer uiContainer;

    public MyContainerMenu(int containerId, Inventory playerInv) {
        super(MyMenuTypes.CONTAINER.get(), containerId);
        this.uiContainer = new ComposedUIContainer(new MyUI(this));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // Handle slot transfers...
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        uiContainer.render(graphics, mouseX, mouseY, partialTick);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return uiContainer.mouseClicked(mouseX, mouseY, button);
    }

    // Other event handlers...
}
```

## Testing UI Components

### Unit Testing
```java
@Test
public void testButtonInteraction() {
    var pressed = new AtomicBoolean(false);

    var button = new Button();
    button.addPressListener((x, y) -> pressed.set(true));

    // Simulate click
    var event = new MouseClickEvent(50, 50, 1);
    boolean handled = button.handleClick(event);

    assertTrue(handled);
    assertTrue(pressed.get());
}

@Test
public void testStateRecomposition() {
    var recompositions = new AtomicInteger(0);
    var state = State.of("initial");

    var ui = new ComposedUI() {
        @Override
        protected void compose(RootScope scope) {
            recompositions.incrementAndGet();
            scope.bind(state);
            // UI content...
        }
    };

    assertEquals(1, recompositions.get());

    state.set("changed");
    assertEquals(2, recompositions.get());
}
```

### Integration Testing
```java
@Test
public void testUILayout() {
    var ui = new MyTestUI();
    var tree = new UITree(ui);

    // Measure and place
    tree.measure(Constraints.fixed(400, 300));
    tree.place(new Bounds(0, 0, 400, 300));

    // Verify layout
    var root = tree.getRoot();
    assertEquals(400, root.getBounds().width());
    assertEquals(300, root.getBounds().height());

    // Verify child positions
    var children = root.getChildren();
    assertEquals(expectedX, children.get(0).getBounds().x());
    assertEquals(expectedY, children.get(0).getBounds().y());
}
```

## Performance Considerations

1. **Minimize Recomposition**
   - Use `bindLayout()` for layout-only changes
   - Keep state scoped to specific components
   - Avoid unnecessary state dependencies

2. **Efficient Measurement**
   - Cache expensive calculations
   - Respect constraints to limit measurement
   - Avoid O(n²) algorithms in layout

3. **Drawing Optimization**
   - Use clipping to limit drawing area
   - Batch similar drawing operations
   - Avoid redundant state changes

4. **Animation Performance**
   - Use appropriate easing functions
   - Limit concurrent animations
   - Consider animation priority

## Troubleshooting

### Common Issues

1. **UI Not Updating**
   - Forgot to bind state: `scope.bind(state)`
   - State change not triggering: Use `set()` instead of direct field modification
   - Layout not recalculating: Use `bindLayout()` for layout changes

2. **Incorrect Layout**
   - Measurement not respecting constraints
   - Placement not using correct bounds
   - Spacing/padding applied incorrectly

3. **Events Not Working**
   - Event handler not registered in `compose()`
   - Event consumed by child before reaching parent
   - Wrong event type used

4. **Performance Issues**
   - Too many recompositions
   - Complex layout calculations
   - Unnecessary drawing outside clip bounds

### Debugging Tips

1. Use UITree inspection to verify tree structure
2. Add logging to `measure()` and `place()` to trace layout
3. Check event bubbling with return values
4. Profile state changes and recompositions

## Migration from Legacy Systems

When migrating from existing UI systems:

1. **Identify Element Types**
   - Visual-only → Primitive
   - Layout containers → Container
   - Complex components → Composable

2. **Convert State Management**
   - Replace direct field access with `State<T>`
   - Add state binding where needed
   - Convert listeners to state observers

3. **Refactor Layout**
   - Replace absolute positioning with constraint-based layout
   - Convert manual event bubbling to framework system
   - Extract reusable components

4. **Incremental Migration**
   - Start with leaf components
   - Gradually replace containers
   - Maintain compatibility during transition

This reference provides a comprehensive guide to using and extending the Compound UI framework. For specific implementation details, refer to the source code and existing element examples.