# Optimal Layering Solution: Custom GuiRenderState

## Problem Solved ✅

**Issue:** Minecraft's `GuiRenderState` uses automatic bounds-based layering, which breaks composition order for siblings and complex layouts.

**Solution:** Create a custom `GuiRenderState` system that always respects submission order (composition order).

## The Elegant Solution: Adapter Pattern

Instead of fighting Minecraft's system, we intercept and replace it temporarily during our rendering phase.

### Architecture

```
UITree.renderTree()
├─ GuiRenderStateAdapter (intercepts calls)
│  ├─ CompositionOrderGuiRenderState (collects in order)
│  └─ Original GuiRenderState (receives final order)
└─ Elements rendered in composition order → Perfect layering
```

### Key Classes

#### 1. `CompositionOrderGuiRenderState`
- **Purpose:** Collects elements in submission order
- **Strategy:** One stratum per element for perfect ordering
- **Implementation:** Simple `ArrayList` per stratum

```java
private final List<RenderQueue> strata = new ArrayList<>();

public void submitGuiElement(GuiElementRenderState renderState) {
    this.current.addElement(renderState);  // Preserves order
}
```

#### 2. `GuiRenderStateAdapter`
- **Purpose:** Transparently intercepts `GuiRenderState` calls
- **Strategy:** Switch between intercepting and forwarding modes
- **Implementation:** Extends `GuiRenderState` and delegates to appropriate target

```java
public void submitGuiElement(GuiElementRenderState renderState) {
    if (intercepting) {
        compositionOrder.submitGuiElement(renderState);  // Collect
    } else {
        original.submitGuiElement(renderState);          // Forward
    }
}
```

#### 3. `UITree.renderTree()`
- **Purpose:** Orchestrates the interception process
- **Implementation:** Simple 3-step process

```java
public void renderTree(IScreenContext context) {
    var adapter = new GuiRenderStateAdapter(context.getGuiRenderState());

    adapter.startIntercepting();           // Step 1: Start collecting

    walkDepthFirst(this.root, node -> {   // Step 2: Render in order
        adapter.nextStratum();           // One layer per element
        primitive.draw(context);
    });

    adapter.flush();                     // Step 3: Submit in order
}
```

### How It Works

#### **Phase 1: Collection**
```java
adapter.startIntercepting();
// All calls go to CompositionOrderGuiRenderState
adapter.submitGuiElement(element1);  // Collected
adapter.submitGuiElement(element2);  // Collected
adapter.submitGuiElement(element3);  // Collected
```

#### **Phase 2: Submission**
```java
adapter.flush();
// Elements submitted to original GuiRenderState in collected order
original.submitGuiElement(element1);  // Bottom layer
original.submitGuiElement(element2);  // Middle layer
original.submitGuiElement(element3);  // Top layer
```

### Performance Characteristics

#### **Memory:** Minimal overhead
- One `ArrayList` per stratum
- Same number of strata as elements (worst case)
- No reflection or complex logic

#### **CPU:** O(N) collection + O(N) submission = O(N)
- Linear collection during rendering
- Linear submission to original GuiRenderState
- No bounds intersection calculations

#### **Compared to alternatives:**
- **Naïve one-stratum-per-element:** More strata but simpler
- **Complex depth tracking:** Harder to get right
- **Bounds-based workarounds:** Still unpredictable

### Guarantees

✅ **Composition order = Render order**
- First composed element = bottom layer
- Last composed element = top layer
- Perfect for any UI structure

✅ **No user API changes**
- Existing composition code unchanged
- Transparent to end users

✅ **Minimal complexity**
- Only 3 classes involved
- Clear separation of concerns
- Easy to understand and maintain

✅ **Framework integration**
- Works with Minecraft's rendering pipeline
- No reflection or private API access
- Compatible with debug tools and profilers

### Example

```java
@Override
public void compose(ICompositionScope scope) {
    scope.e(new Box(), box -> {
        // These now render in exact composition order:
        box.e(new ElementRect(0xFF0000FF)); // Bottom (first composed)
        box.e(new ElementRect(0xFF00FF00)); // Middle (second composed)
        box.e(new ElementRect(0xFFFF0000)); // Top (third composed)
    });
}
```

**Result:** Perfect layering regardless of bounds, positions, or layout complexity.

### Future Optimizations (Optional)

If needed, we can optimize further:

#### **Depth-based strata grouping**
```java
// Instead of one stratum per element:
if (newDepth != currentDepth) {
    adapter.nextStratum();  // New stratum for new depth
}
```

#### **Shared strata for siblings**
```java
// Elements at same depth could share strata
// Still maintains order via node tree building
```

But the current implementation already provides:
- **Perfect correctness** ✅
- **Predictable performance** ✅
- **Simple implementation** ✅

## Summary

The custom GuiRenderState solution provides:

🎯 **Perfect layering** that always respects composition order
⚡ **Minimal overhead** with no reflection or complex tricks
🔧 **Clean architecture** using adapter pattern and interception
📈 **Scalable performance** that's O(N) regardless of UI complexity
🎮 **Full compatibility** with Minecraft's rendering system

This solves the layering problem definitively while maintaining all existing functionality and performance characteristics.
