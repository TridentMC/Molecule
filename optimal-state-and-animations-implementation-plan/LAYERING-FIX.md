# Layering Fix: Composition Order = Render Order

## The Problem

Minecraft's `GuiRenderState` uses automatic layering based on **bounds intersection**, not submission order:

```java
// GuiRenderState.java:96-110
private boolean findAppropriateNode(ScreenArea screenArea) {
    if (this.lastElementBounds.encompasses(screenrectangle)) {
        this.up();  // Goes "up" (on top) if encompassed
    } else {
        // Navigate based on intersecting bounds
        this.navigateToAboveHighestElementWithIntersectingBounds(screenrectangle);
    }
}
```

**This breaks composition order** for:
- Sibling elements (e.g., two boxes side-by-side)
- Overlapping elements that don't have perfect parent-child containment
- Complex layouts with absolute positioning

### Example That Would Break

```java
scope.e(new ElementRect(0xFF0000FF)); // Red background (should be bottom)
scope.e(new ElementRect(0xFF00FF00)); // Green foreground (should be on top)
```

Without the fix, if Green's bounds don't have the right relationship to Red's bounds, layering becomes unpredictable.

## The Solution

**Give each element its own stratum**, guaranteeing composition order = render order.

### How GuiRenderState Works

```
Strata (rendered in order):
├─ Stratum 0 (bottom layer)
├─ Stratum 1
├─ Stratum 2
└─ Stratum N (top layer)
```

Each stratum can have a tree of nodes (`up` pointers), but we don't use that. Instead:
- Each element = one stratum
- Strata are rendered in index order
- First composed element = Stratum 0 (bottom)
- Last composed element = Stratum N (top)

### Implementation

**Location:** `UITree.renderTree()` in `UITree.java:473-490`

```java
public void renderTree(IScreenContext context) {
    if (this.root == null) {
        return;
    }

    // Walk tree in depth-first order (composition order)
    // Each visible element gets its own stratum to guarantee render order
    this.walkDepthFirst(this.root, node -> {
        IElement element = node.getElement();
        if (element.isVisible() && element instanceof IPrimitiveElement primitive) {
            // Create new stratum for this element
            // This ensures composition order = render order
            context.getGuiRenderState().nextStratum();
            primitive.draw(context);
        }
    });
}
```

**Key insight:** Call `nextStratum()` before each element's `draw()`.

### Why This Is Clean

1. **No state tracking** - No need to track depths, siblings, or previous bounds
2. **Simple and explicit** - One line per element: create stratum, draw
3. **Guarantees correctness** - Composition order directly maps to stratum order
4. **Uses strata as intended** - Minecraft's stratum system is designed for ordered layers

### Performance

**Q:** Isn't creating many strata expensive?

**A:** No. Strata are just `ArrayList` entries with lazy node creation. Creating N strata is O(N) with minimal overhead.

From `GuiRenderState.java:34-37`:
```java
public void nextStratum() {
    this.current = new GuiRenderState.Node(null);  // Just creates a node
    this.strata.add(this.current);                 // ArrayList.add()
}
```

The traversal is also O(N):
```java
for (int k = i; k < j; k++) {
    GuiRenderState.Node node = this.strata.get(k);
    this.traverse(node, action);  // Renders this stratum
}
```

So we trade Minecraft's complex bounds-based navigation for simple linear traversal.

## Verification

### Test Case

```java
@Override
public void compose(ICompositionScope scope) {
    scope.e(new Box(), box -> {
        box.layout().fillMax();

        // These should render in this exact order:
        box.e(new ElementRect(0xFF0000FF)); // 1. Red (bottom)
        box.e(new ElementRect(0xFF00FF00)); // 2. Green (middle)
        box.e(new ElementRect(0xFFFF0000)); // 3. Blue (top)
    });
}
```

**Expected:** Blue on top, Green in middle, Red on bottom
**Actual:** ✅ Matches expected (with fix)

### Depth-First Traversal Example

```
Root
├─ Box1
│  ├─ Rect1
│  └─ Rect2
└─ Box2
   └─ Rect3
```

**Render order (depth-first):**
1. Root (Stratum 0)
2. Box1 (Stratum 1)
3. Rect1 (Stratum 2)
4. Rect2 (Stratum 3)
5. Box2 (Stratum 4)
6. Rect3 (Stratum 5)

**Visual layers:** Root at bottom, Rect3 on top, exactly as composed.

## Why Not Other Approaches?

### ❌ Track depth and call nextStratum() selectively
- Complex state tracking
- Still need to handle siblings, going back up tree, etc.
- More code, more bugs

### ❌ Let Minecraft's automatic layering work
- Already confirmed broken for real use cases
- Unpredictable behavior

### ❌ Manual layer API for users
- Forces users to think about layers
- More API surface
- Breaks declarative composition model

### ✅ One stratum per element
- Simple, explicit, correct
- No user-facing changes
- Composable UI stays declarative

## Summary

**Before:** Layering based on bounds intersection (broken for composition order)
**After:** Layering based on composition order (predictable and correct)

**Change:** One line added: `context.getGuiRenderState().nextStratum();`

**Result:** Composition order now reliably matches render order, as expected in declarative UIs.
