# Implementation Checklist

## Phase 1: Core State Refactor

### TreeNode Changes
- [ ] Add `tree` field to TreeNode
- [ ] Add `compositionStates` and `layoutStates` Sets
- [ ] Create `compositionObserver` lambda
- [ ] Create `layoutObserver` lambda
- [ ] Update constructor to accept UITree parameter
- [ ] Implement `bindCompositionState(State)`
- [ ] Implement `bindLayoutState(State)`
- [ ] Implement `unbindCompositionState(State)`
- [ ] Implement `unbindLayoutState(State)`
- [ ] Implement `getCompositionStates()`
- [ ] Implement `getLayoutStates()`
- [ ] Implement `dispose()` method
- [ ] Update deprecated `bindState()` to use `bindCompositionState()`
- [ ] Update `unbindState()` to try both types
- [ ] Update `isBoundToState()` to check both sets
- [ ] Update `getBoundStates()` to merge both sets

### ITreeNode Interface
- [ ] Add `bindCompositionState(State)` method signature
- [ ] Add `bindLayoutState(State)` method signature
- [ ] Add `unbindCompositionState(State)` method signature
- [ ] Add `unbindLayoutState(State)` method signature
- [ ] Add `getCompositionStates()` method signature
- [ ] Add `getLayoutStates()` method signature
- [ ] Add `dispose()` method signature

### UITree Changes
- [ ] Remove `onStateChanged(State)` method (delete lines 402-413)
- [ ] Remove `observedStates` field
- [ ] Remove `registerState(State)` method
- [ ] Remove `unregisterStateIfUnused(State)` method
- [ ] Add `requestRecompose(ITreeNode)` method
- [ ] Add `requestRemeasure(ITreeNode)` method
- [ ] Update `createNode(IElement)` to pass `this` to TreeNode constructor
- [ ] Update `unregisterNode(ITreeNode)` to call `dispose()` on TreeNode
- [ ] Simplify `bindNodeToState()` to call `bindCompositionState()`
- [ ] Update `unbindNodeFromState()` to call both unbind methods

### Scope Classes (RootScope, ContainerScope, ComposableElementScope)
- [ ] Keep existing `bind(State)` for backward compatibility
- [ ] Add `bindComposition(State)` method
- [ ] Add `bindLayout(State)` method
- [ ] Update all to call TreeNode methods instead of UITree

### Testing
- [ ] Test single node with composition state
- [ ] Test single node with layout state
- [ ] Test multiple nodes observing same state
- [ ] Test state unbinding and cleanup
- [ ] Test backward compatibility with `bind()`
- [ ] Performance test: measure state change time with 1000-node tree
- [ ] Verify no `walkDepthFirst` calls on state changes (add logging)

---

## Phase 2: Deferred Layout Properties

### Core Infrastructure
- [ ] Convert LayoutProperties to abstract class
- [ ] Create ImmediateLayoutProperties class
- [ ] Create DeferredLayoutProperties class
- [ ] Create LayoutProperties.Builder inner class

### LayoutProperties (Abstract)
- [ ] Make class abstract
- [ ] Convert all getters to abstract methods
- [ ] Add static `create()` factory method
- [ ] Keep all JavaDoc

### Builder Class
- [ ] Add Object fields for all properties
- [ ] Implement all immediate value setters (int, boolean, etc.)
- [ ] Implement all supplier setters (IntSupplier, BooleanSupplier, etc.)
- [ ] Implement `build()` method
- [ ] Implement `hasAnySuppliers()` helper
- [ ] Implement `isSupplier(Object)` helper
- [ ] Implement `buildImmediate()` method
- [ ] Implement `buildDeferred()` method
- [ ] Implement `toIntSupplier(Object)` helper
- [ ] Implement `toBooleanSupplier(Object)` helper
- [ ] Implement `toAlignmentSupplier(Object)` helper
- [ ] Implement `toFloatSupplier(Object)` helper

### ImmediateLayoutProperties
- [ ] Copy all fields from old LayoutProperties
- [ ] Implement all getters (simple field access)
- [ ] Add package-private constructor
- [ ] Add package-private field setters (for Builder)

### DeferredLayoutProperties
- [ ] Add supplier fields for all properties
- [ ] Implement all getters (evaluate suppliers)
- [ ] Handle null suppliers with defaults
- [ ] Add package-private constructor
- [ ] Add package-private field setters (for Builder)

### Element Integration
- [ ] Verify all elements use LayoutProperties getters
- [ ] Test that deferred properties evaluate during layout
- [ ] Ensure no elements cache property values incorrectly

### Testing
- [ ] Test immediate-only properties create ImmediateLayoutProperties
- [ ] Test supplier-only properties create DeferredLayoutProperties
- [ ] Test mixed immediate and supplier creates DeferredLayoutProperties
- [ ] Test all property types (int, boolean, Alignment, etc.)
- [ ] Test null values and defaults
- [ ] Test supplier evaluation timing (during layout, not composition)
- [ ] Performance test: verify ImmediateLayoutProperties has no overhead
- [ ] Test backward compatibility with existing code

---

## Phase 3: Animation System

### Animation Core Classes
- [ ] Create `AnimatedState.java`
- [ ] Create `AnimationScheduler.java`
- [ ] Create `Interpolator.java` interface
- [ ] Create `Interpolators.java` with FLOAT, INT, COLOR
- [ ] Create `Easing.java` interface with built-in functions

### AnimatedState Implementation
- [ ] Add fields: currentValue, targetValue, startValue, etc.
- [ ] Implement constructor
- [ ] Implement `get()`
- [ ] Implement `set(T)` with animation start
- [ ] Implement `setImmediate(T)` for no animation
- [ ] Implement `update(Function)`
- [ ] Implement `updateAnimation(long)` package-private method
- [ ] Implement `isAnimating()`
- [ ] Implement `addObserver(StateObserver)`
- [ ] Implement `removeObserver(StateObserver)`
- [ ] Implement `dispose()`
- [ ] Implement `notifyObservers()` helper

### AnimationScheduler Implementation
- [ ] Add `activeAnimations` ConcurrentHashSet
- [ ] Implement `registerAnimation(AnimatedState)`
- [ ] Implement `unregisterAnimation(AnimatedState)`
- [ ] Implement `updateAnimations()`
- [ ] Implement `hasActiveAnimations()`
- [ ] Implement `dispose()`
- [ ] Implement `getActiveAnimationCount()` for debugging

### Interpolators Implementation
- [ ] Implement FLOAT interpolator
- [ ] Implement INT interpolator
- [ ] Implement COLOR interpolator (per-channel ARGB)

### Easing Implementation
- [ ] Implement LINEAR
- [ ] Implement EASE_IN
- [ ] Implement EASE_OUT
- [ ] Implement EASE_IN_OUT
- [ ] Implement EASE_IN_CUBIC
- [ ] Implement EASE_OUT_CUBIC
- [ ] Implement EASE_IN_OUT_CUBIC

### UITree Integration
- [ ] Add `animationScheduler` field
- [ ] Add `getAnimationScheduler()` getter
- [ ] Update `layoutAndRender()` to call `updateAnimations()` first
- [ ] Update remeasure logic to handle active animations

### Scope Integration
- [ ] Add `getTree()` to ICompositionScope interface
- [ ] Add `animateFloat(float, long)` default method
- [ ] Add `animateFloat(float, long, Easing)` default method
- [ ] Add `animateInt(int, long)` default method
- [ ] Add `animateInt(int, long, Easing)` default method
- [ ] Add `animateColor(int, long)` default method
- [ ] Add `animateColor(int, long, Easing)` default method
- [ ] Implement `getTree()` in RootScope
- [ ] Implement `getTree()` in ContainerScope
- [ ] Implement `getTree()` in ComposableElementScope

### Testing
- [ ] Test basic float animation
- [ ] Test basic int animation
- [ ] Test basic color animation
- [ ] Test animation completion
- [ ] Test setImmediate() cancels animation
- [ ] Test changing target mid-animation
- [ ] Test multiple simultaneous animations
- [ ] Test animation without binding (draw-only)
- [ ] Test animation with layout binding
- [ ] Test animation with composition binding
- [ ] Test all easing functions visually
- [ ] Test interpolators accuracy
- [ ] Performance test: 100 simultaneous animations
- [ ] Test cleanup on node detach

---

## Phase 4: Documentation & Examples

### Code Documentation
- [ ] JavaDoc for all public APIs
- [ ] Package-info.java for animation package
- [ ] Update existing LayoutProperties JavaDoc
- [ ] Document performance characteristics

### Usage Examples
- [ ] Create example: color animation
- [ ] Create example: width animation
- [ ] Create example: multi-property animation
- [ ] Create example: staggered animations
- [ ] Create example: easing comparison
- [ ] Create example: loading spinner
- [ ] Create example: smooth scrolling

### Migration Guide
- [ ] Document state binding changes
- [ ] Document layout property suppliers
- [ ] Document animation APIs
- [ ] Provide before/after code examples
- [ ] Document performance improvements

---

## Phase 5: Polish & Optimization

### Performance Optimization
- [ ] Profile state change notifications
- [ ] Profile animation updates
- [ ] Profile layout property evaluation
- [ ] Optimize hot paths
- [ ] Add fast paths for common cases

### Error Handling
- [ ] Validate animation parameters
- [ ] Handle null values gracefully
- [ ] Add clear error messages
- [ ] Prevent common mistakes

### Developer Experience
- [ ] Add helpful toString() methods
- [ ] Add debugging utilities
- [ ] Add animation inspector (optional)
- [ ] Improve error messages

---

## Final Validation

### Integration Testing
- [ ] Test complete UI with all features
- [ ] Test backward compatibility with existing UIs
- [ ] Test complex animation scenarios
- [ ] Test edge cases (rapid state changes, etc.)

### Performance Testing
- [ ] Benchmark state changes (before/after)
- [ ] Benchmark animations at 60fps
- [ ] Benchmark layout updates
- [ ] Memory usage analysis

### Code Quality
- [ ] Code review
- [ ] Clean up TODOs
- [ ] Verify no compiler warnings
- [ ] Run static analysis

### Documentation Review
- [ ] Review all JavaDoc
- [ ] Review examples
- [ ] Review implementation plan accuracy
- [ ] Update any outdated information

---

## Success Criteria

- [ ] All tests passing
- [ ] No performance regressions
- [ ] State changes are O(1) instead of O(n)
- [ ] Animations run at 60fps
- [ ] Layout animations don't trigger recomposition
- [ ] Draw animations have zero composition/layout overhead
- [ ] Backward compatible with existing code
- [ ] Documentation complete and accurate
