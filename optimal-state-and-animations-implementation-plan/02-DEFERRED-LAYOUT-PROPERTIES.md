# Phase 2: Deferred Layout Properties

## Goal
Enable layout properties to accept suppliers for deferred evaluation during layout phase, avoiding recomposition for layout-only animations.

## Architecture

Instead of a complex builder with Object casting, we use an explicit `.deferred()` scope that:
1. Creates a DeferredLayoutProperties instance
2. Provides typed methods for binding and supplier-based setters
3. Returns to the parent element for further configuration

### Class Structure
```
LayoutProperties (current concrete class - mostly unchanged)
└── .deferred() method creates DeferredLayoutProperties

DeferredLayoutProperties (extends LayoutProperties)
└── Uses suppliers instead of concrete values
```

## Implementation

### 1. `LayoutProperties.java` (Minimal Changes)

Keep the current concrete implementation, just add one new method:

```java
public class LayoutProperties {
    // All existing fields and methods stay the same
    private Integer fixedWidth;
    private Integer fixedHeight;
    // ... etc

    // All existing methods unchanged
    public LayoutProperties fixedWidth(int width) {
        this.fixedWidth = width;
        return this;
    }
    // ... etc

    /**
     * Enter deferred layout scope for supplier-based properties.
     * Use this when layout properties need to be animated or change frequently.
     *
     * @param configurator the deferred configuration scope
     * @return this for continued chaining
     */
    public LayoutProperties deferred(Consumer<DeferredScope> configurator) {
        // Create deferred properties, copy current values
        DeferredLayoutProperties deferred = new DeferredLayoutProperties(this);

        // Run configuration
        DeferredScope scope = new DeferredScope(deferred);
        configurator.accept(scope);

        // Replace this instance with deferred version
        // This is a bit tricky - we need to return the deferred instance
        // but maintain the fluent chain
        return deferred;
    }

    // Static factory unchanged
    public static LayoutProperties create() {
        return new LayoutProperties();
    }
}
```

### 2. `DeferredLayoutProperties.java` (New File)

```java
package com.tridevmc.compound.ui.compose.layout;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * Layout properties with deferred (supplier-based) evaluation.
 * Created via LayoutProperties.deferred() scope.
 */
public class DeferredLayoutProperties extends LayoutProperties {
    // Suppliers for all properties
    private Supplier<Integer> fixedWidthSupplier;
    private Supplier<Integer> fixedHeightSupplier;
    private BooleanSupplier fillMaxWidthSupplier;
    private BooleanSupplier fillMaxHeightSupplier;
    private Supplier<Float> weightSupplier;
    private Supplier<Integer> minWidthSupplier;
    private Supplier<Integer> minHeightSupplier;
    private Supplier<Integer> maxWidthSupplier;
    private Supplier<Integer> maxHeightSupplier;
    private IntSupplier paddingLeftSupplier;
    private IntSupplier paddingTopSupplier;
    private IntSupplier paddingRightSupplier;
    private IntSupplier paddingBottomSupplier;
    private IntSupplier marginLeftSupplier;
    private IntSupplier marginTopSupplier;
    private IntSupplier marginRightSupplier;
    private IntSupplier marginBottomSupplier;
    private Supplier<Alignment> contentAlignmentSupplier;
    private Supplier<Alignment> horizontalAlignmentSupplier;
    private Supplier<Alignment> verticalAlignmentSupplier;
    private IntSupplier spacingSupplier;
    private Supplier<Integer> gridColumnsSupplier;
    private Supplier<Integer> gridRowsSupplier;

    /**
     * Package-private constructor - only created via deferred() scope.
     * Copies existing immediate values from source.
     */
    DeferredLayoutProperties(LayoutProperties source) {
        super();
        // Wrap existing values in suppliers as defaults
        this.fixedWidthSupplier = () -> source.getFixedWidth();
        this.fixedHeightSupplier = () -> source.getFixedHeight();
        this.fillMaxWidthSupplier = source::isFillMaxWidth;
        this.fillMaxHeightSupplier = source::isFillMaxHeight;
        // ... wrap all existing values
    }

    // Override all getters to evaluate suppliers
    @Override
    public Integer getFixedWidth() {
        return fixedWidthSupplier != null ? fixedWidthSupplier.get() : null;
    }

    @Override
    public Integer getFixedHeight() {
        return fixedHeightSupplier != null ? fixedHeightSupplier.get() : null;
    }

    @Override
    public boolean isFillMaxWidth() {
        return fillMaxWidthSupplier != null ? fillMaxWidthSupplier.getAsBoolean() : false;
    }

    @Override
    public boolean isFillMaxHeight() {
        return fillMaxHeightSupplier != null ? fillMaxHeightSupplier.getAsBoolean() : false;
    }

    @Override
    public Float getWeight() {
        return weightSupplier != null ? weightSupplier.get() : null;
    }

    @Override
    public Integer getMinWidth() {
        return minWidthSupplier != null ? minWidthSupplier.get() : null;
    }

    @Override
    public Integer getMinHeight() {
        return minHeightSupplier != null ? minHeightSupplier.get() : null;
    }

    @Override
    public Integer getMaxWidth() {
        return maxWidthSupplier != null ? maxWidthSupplier.get() : null;
    }

    @Override
    public Integer getMaxHeight() {
        return maxHeightSupplier != null ? maxHeightSupplier.get() : null;
    }

    @Override
    public int getPaddingLeft() {
        return paddingLeftSupplier != null ? paddingLeftSupplier.getAsInt() : 0;
    }

    @Override
    public int getPaddingTop() {
        return paddingTopSupplier != null ? paddingTopSupplier.getAsInt() : 0;
    }

    @Override
    public int getPaddingRight() {
        return paddingRightSupplier != null ? paddingRightSupplier.getAsInt() : 0;
    }

    @Override
    public int getPaddingBottom() {
        return paddingBottomSupplier != null ? paddingBottomSupplier.getAsInt() : 0;
    }

    @Override
    public int getMarginLeft() {
        return marginLeftSupplier != null ? marginLeftSupplier.getAsInt() : 0;
    }

    @Override
    public int getMarginTop() {
        return marginTopSupplier != null ? marginTopSupplier.getAsInt() : 0;
    }

    @Override
    public int getMarginRight() {
        return marginRightSupplier != null ? marginRightSupplier.getAsInt() : 0;
    }

    @Override
    public int getMarginBottom() {
        return marginBottomSupplier != null ? marginBottomSupplier.getAsInt() : 0;
    }

    @Override
    public Alignment getContentAlignment() {
        return contentAlignmentSupplier != null ? contentAlignmentSupplier.get() : null;
    }

    @Override
    public Alignment getHorizontalAlignment() {
        return horizontalAlignmentSupplier != null ? horizontalAlignmentSupplier.get() : null;
    }

    @Override
    public Alignment getVerticalAlignment() {
        return verticalAlignmentSupplier != null ? verticalAlignmentSupplier.get() : null;
    }

    @Override
    public int getSpacing() {
        return spacingSupplier != null ? spacingSupplier.getAsInt() : 0;
    }

    @Override
    public Integer getGridColumns() {
        return gridColumnsSupplier != null ? gridColumnsSupplier.get() : null;
    }

    @Override
    public Integer getGridRows() {
        return gridRowsSupplier != null ? gridRowsSupplier.get() : null;
    }

    // Package-private setters for DeferredScope
    void setFixedWidthSupplier(Supplier<Integer> supplier) {
        this.fixedWidthSupplier = supplier;
    }

    void setFixedHeightSupplier(Supplier<Integer> supplier) {
        this.fixedHeightSupplier = supplier;
    }

    void setFillMaxWidthSupplier(BooleanSupplier supplier) {
        this.fillMaxWidthSupplier = supplier;
    }

    void setFillMaxHeightSupplier(BooleanSupplier supplier) {
        this.fillMaxHeightSupplier = supplier;
    }

    // ... setters for all other suppliers
}
```

### 3. `DeferredScope.java` (New File)

```java
package com.tridevmc.compound.ui.compose.layout;

import com.tridevmc.compound.ui.compose.state.State;
import com.tridevmc.compound.ui.compose.tree.ITreeNode;
import com.tridevmc.compound.ui.compose.tree.TreeNode;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * Scope for configuring deferred layout properties.
 * Provides type-safe supplier-based setters and layout state binding.
 */
public class DeferredScope {
    private final DeferredLayoutProperties properties;
    private ITreeNode boundNode;  // Set by element when scope is created

    DeferredScope(DeferredLayoutProperties properties) {
        this.properties = properties;
    }

    /**
     * Set by the element to enable state binding.
     */
    public void setBoundNode(ITreeNode node) {
        this.boundNode = node;
    }

    /**
     * Bind a state for layout-only updates.
     * When this state changes, only layout is triggered (no recomposition).
     */
    public void bindLayout(State<?> state) {
        if (boundNode instanceof TreeNode node) {
            node.bindLayoutState(state);
        }
    }

    // Supplier-based setters for all properties
    public void fixedWidth(Supplier<Integer> supplier) {
        properties.setFixedWidthSupplier(supplier);
    }

    public void fixedHeight(Supplier<Integer> supplier) {
        properties.setFixedHeightSupplier(supplier);
    }

    public void fillMaxWidth(BooleanSupplier supplier) {
        properties.setFillMaxWidthSupplier(supplier);
    }

    public void fillMaxHeight(BooleanSupplier supplier) {
        properties.setFillMaxHeightSupplier(supplier);
    }

    // Convenience: Allow direct state binding for common types
    public void fixedWidth(State<Integer> state) {
        bindLayout(state);
        fixedWidth(state::get);
    }

    public void fixedHeight(State<Integer> state) {
        bindLayout(state);
        fixedHeight(state::get);
    }

    // ... setters for all other properties
}
```

### 4. Integration with Elements

Elements need to provide the node reference to DeferredScope:

```java
// In BaseElement or wherever layout() is called
public LayoutProperties layout() {
    if (this.layoutProperties == null) {
        this.layoutProperties = LayoutProperties.create();
    }
    return this.layoutProperties;
}

// Override for elements that can set bound node
protected LayoutProperties layoutWithNode(ITreeNode node) {
    LayoutProperties props = layout();

    // Intercept deferred() calls to inject node
    return new LayoutProperties() {
        // Delegate all methods to props
        // Override deferred() to set node
        @Override
        public LayoutProperties deferred(Consumer<DeferredScope> configurator) {
            DeferredLayoutProperties deferred = new DeferredLayoutProperties(props);
            DeferredScope scope = new DeferredScope(deferred);
            scope.setBoundNode(node);  // Inject node!
            configurator.accept(scope);
            return deferred;
        }
    };
}
```

## Usage Examples

### Example 1: Simple Width Animation
```java
var width = scope.animateInt(0, 300);

box.layout()
    .fillMaxHeight()
    .deferred(deferred -> {
        deferred.bindLayout(width);
        deferred.fixedWidth(() -> width.get());
    });
```

### Example 2: Multiple Deferred Properties
```java
var width = scope.animateInt(50, 300);
var padding = scope.animateInt(5, 300);

box.layout()
    .contentAlignment(Alignment.CENTER)  // Immediate
    .deferred(deferred -> {
        deferred.bindLayout(width);
        deferred.bindLayout(padding);
        deferred.fixedWidth(() -> width.get());
        deferred.paddingLeft(() -> padding.get());
        deferred.paddingRight(() -> padding.get());
    });
```

### Example 3: Convenience Method
```java
var width = scope.animateInt(0, 300);

box.layout()
    .deferred(deferred -> {
        deferred.fixedWidth(width);  // Automatically binds and uses supplier!
    });
```

## Benefits

1. **Type-Safe**: No Object casting, all suppliers properly typed
2. **Explicit**: `.deferred()` makes it clear when deferred evaluation is used
3. **Clean Separation**: Immediate and deferred properties don't mix in confusing ways
4. **Binding Co-location**: State binding happens where it's used
5. **No Magic**: User explicitly chooses deferred evaluation

## Migration Path

Old code (still works):
```java
box.layout()
    .fixedWidth(100)
    .padding(10);
```

New code for animations:
```java
box.layout()
    .padding(10)  // Immediate still works
    .deferred(deferred -> {
        deferred.fixedWidth(animatedWidth);
    });
```

## Testing

1. Test immediate properties still work
2. Test deferred scope supplier evaluation
3. Test bindLayout() in deferred scope
4. Test mixing immediate and deferred
5. Test convenience methods (binding + supplier in one call)
6. Verify no recomposition for layout-only changes
