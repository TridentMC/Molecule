# Phase 1: Core State Binding Refactor

## Goal
Replace UITree-as-observer pattern with TreeNode-as-observer pattern for O(1) state change handling.

## Files to Modify

### 1. `TreeNode.java`

**Add Fields:**
```java
private UITree tree;
private final Set<State<?>> compositionStates = new HashSet<>();
private final Set<State<?>> layoutStates = new HashSet<>();

// Two dedicated observers
private final StateObserver compositionObserver = state -> {
    if (this.tree != null) {
        this.tree.requestRecompose(this);
    }
};

private final StateObserver layoutObserver = state -> {
    if (this.tree != null) {
        this.tree.requestRemeasure(this);
    }
};
```

**Modify Constructor:**
```java
public TreeNode(IElement element, UITree tree) {
    this.element = element;
    this.tree = tree;
}
```

**Add New Methods:**
```java
public void bindCompositionState(State<?> state) {
    if (this.compositionStates.add(state)) {  // Set.add returns true if added
        state.addObserver(this.compositionObserver);
    }
}

public void bindLayoutState(State<?> state) {
    if (this.layoutStates.add(state)) {
        state.addObserver(this.layoutObserver);
    }
}

public void unbindCompositionState(State<?> state) {
    if (this.compositionStates.remove(state)) {
        state.removeObserver(this.compositionObserver);
    }
}

public void unbindLayoutState(State<?> state) {
    if (this.layoutStates.remove(state)) {
        state.removeObserver(this.layoutObserver);
    }
}

public Set<State<?>> getCompositionStates() {
    return new HashSet<>(this.compositionStates);
}

public Set<State<?>> getLayoutStates() {
    return new HashSet<>(this.layoutStates);
}

public void dispose() {
    // Remove all state observers
    for (State<?> state : this.compositionStates) {
        state.removeObserver(this.compositionObserver);
    }
    for (State<?> state : this.layoutStates) {
        state.removeObserver(this.layoutObserver);
    }
    this.compositionStates.clear();
    this.layoutStates.clear();
}
```

**Update Existing Methods:**
```java
// DEPRECATED - keep for backward compatibility initially
@Override
public void bindState(State<?> state) {
    // Default to composition binding for backward compatibility
    this.bindCompositionState(state);
}

@Override
public void unbindState(State<?> state) {
    // Try both types
    this.unbindCompositionState(state);
    this.unbindLayoutState(state);
}

@Override
public boolean isBoundToState(State<?> state) {
    return this.compositionStates.contains(state) || this.layoutStates.contains(state);
}

@Override
public List<State<?>> getBoundStates() {
    // Merge both sets for backward compatibility
    Set<State<?>> all = new HashSet<>();
    all.addAll(this.compositionStates);
    all.addAll(this.layoutStates);
    return new ArrayList<>(all);
}
```

### 2. `ITreeNode.java`

**Add Interface Methods:**
```java
// New typed binding methods
void bindCompositionState(State<?> state);
void bindLayoutState(State<?> state);
void unbindCompositionState(State<?> state);
void unbindLayoutState(State<?> state);

Set<State<?>> getCompositionStates();
Set<State<?>> getLayoutStates();

void dispose();
```

### 3. `UITree.java`

**Remove Old Implementation:**
```java
// DELETE: onStateChanged method (lines 402-413)
// DELETE: observedStates field
// DELETE: registerState method
// DELETE: unregisterStateIfUnused method
```

**Add New Methods:**
```java
/**
 * Request recomposition of a specific node.
 * Called directly by TreeNode observers.
 */
public void requestRecompose(ITreeNode node) {
    this.recomposeNode(node);
}

/**
 * Request remeasurement of a specific node without recomposition.
 * Called directly by TreeNode observers for layout-only state changes.
 */
public void requestRemeasure(ITreeNode node) {
    this.requestRemeasurement(node);
}
```

**Update Node Creation:**
```java
public ITreeNode createNode(IElement element) {
    TreeNode node = new TreeNode(element, this);  // Pass tree reference
    element.setTree(this);
    this.registerNode(node);
    return node;
}
```

**Update Node Cleanup:**
```java
private void unregisterNode(ITreeNode node) {
    this.elementToNode.remove(node.getElement());
    node.getElement().setTree(null);

    // Clean up state observers
    if (node instanceof TreeNode treeNode) {
        treeNode.dispose();
    }

    for (ITreeNode child : node.getChildren()) {
        this.unregisterNode(child);
    }
}
```

**Simplify Binding Methods:**
```java
// DEPRECATED - keep for backward compatibility
public void bindNodeToState(ITreeNode node, State<?> state) {
    // Default to composition binding
    if (node instanceof TreeNode treeNode) {
        treeNode.bindCompositionState(state);
    }
}

public void unbindNodeFromState(ITreeNode node, State<?> state) {
    if (node instanceof TreeNode treeNode) {
        treeNode.unbindCompositionState(state);
        treeNode.unbindLayoutState(state);
    }
}
```

### 4. Update Scope Classes

**All scope classes (RootScope, ContainerScope, ComposableElementScope):**

Keep existing `bind()` for backward compatibility:
```java
@Override
public void bind(State<?> state) {
    if (this.currentNode != null && this.currentNode instanceof TreeNode node) {
        node.bindCompositionState(state);
    }
}
```

Add new typed binding methods:
```java
public void bindComposition(State<?> state) {
    if (this.currentNode != null && this.currentNode instanceof TreeNode node) {
        node.bindCompositionState(state);
    }
}

public void bindLayout(State<?> state) {
    if (this.currentNode != null && this.currentNode instanceof TreeNode node) {
        node.bindLayoutState(state);
    }
}
```

## Testing Strategy

1. **Verify no tree walks**: Add logging to confirm `walkDepthFirst` is never called on state changes
2. **Test state change handling**: Ensure bound nodes still recompose correctly
3. **Test cleanup**: Verify state observers are removed when nodes are detached
4. **Test multiple nodes**: Multiple nodes can observe same state
5. **Backward compatibility**: Existing `bind()` calls still work

## Performance Validation

Before/After comparison:
- Measure time for state change with 1000-node tree
- Expected: ~100x faster (O(n) → O(1))
- Profile to confirm no tree walks

## Migration Notes

- Existing code using `scope.bind(state)` continues to work (defaults to composition binding)
- New code can use `scope.bindComposition(state)` or `scope.bindLayout(state)` for clarity
- No breaking changes to public API
