# Optimal State and Animations Implementation Plan

This directory contains a comprehensive plan for implementing an optimized state binding system and animation framework for the composable UI.

## 📋 Documents

1. **[00-OVERVIEW.md](./00-OVERVIEW.md)** - High-level architecture and design principles
2. **[01-CORE-STATE-REFACTOR.md](./01-CORE-STATE-REFACTOR.md)** - Phase 1: Optimize state binding (TreeNode observers)
3. **[02-DEFERRED-LAYOUT-PROPERTIES.md](./02-DEFERRED-LAYOUT-PROPERTIES.md)** - Phase 2: Supplier-based layout properties
4. **[03-ANIMATION-SYSTEM.md](./03-ANIMATION-SYSTEM.md)** - Phase 3: AnimatedState and animation framework
5. **[04-USAGE-EXAMPLES.md](./04-USAGE-EXAMPLES.md)** - Practical code examples and patterns
6. **[05-IMPLEMENTATION-CHECKLIST.md](./05-IMPLEMENTATION-CHECKLIST.md)** - Complete checklist for implementation
7. **[06-MIGRATION-GUIDE.md](./06-MIGRATION-GUIDE.md)** - How to migrate existing code

## 🎯 Key Improvements

### Performance
- **State changes: O(n) → O(1)** - No more tree walks on every state change
- **Layout animations: Zero recomposition** - Only remeasure, don't rebuild UI tree
- **Draw animations: Zero overhead** - No composition or layout, just redraw
- **Smooth 60fps animations** - Frame-aligned with proper interpolation

### Developer Experience
- **Ergonomic API** - Simple, intuitive animation creation
- **Zero breaking changes** - 100% backward compatible
- **Type-safe** - Compile-time safety for interpolators
- **Flexible** - Works with any value type

### Architecture
- **Phase-aware** - Composition, layout, and draw phases properly separated
- **Direct observers** - TreeNodes observe states directly (no indirection)
- **Smart builders** - Automatically optimize based on usage
- **Clean separation** - Clear distinction between state types

## 🚀 Quick Start

### Read This First
Start with **00-OVERVIEW.md** to understand the architecture, then review the phase documents in order.

### Implementation Order
1. **Phase 1** (01-CORE-STATE-REFACTOR.md) - Foundation for everything else
2. **Phase 2** (02-DEFERRED-LAYOUT-PROPERTIES.md) - Enables efficient layout animations
3. **Phase 3** (03-ANIMATION-SYSTEM.md) - Complete animation framework

### Before You Start
- Review all documents thoroughly
- Understand the current implementation (especially UITree and TreeNode)
- Read the usage examples to understand the end goal
- Use the checklist to track progress

## 📊 Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                        User Code                             │
│  scope.animateFloat(0f, 300) → width                        │
│  scope.bindLayout(width)                                     │
│  box.layout().fixedWidth(() -> width.get())                 │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ↓
┌─────────────────────────────────────────────────────────────┐
│                     TreeNode                                 │
│  • Observes state directly via layoutObserver               │
│  • Calls tree.requestRemeasure(this) on change              │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ↓
┌─────────────────────────────────────────────────────────────┐
│                      UITree                                  │
│  • requestRemeasure() - triggers layout only                │
│  • animationScheduler.updateAnimations() - updates values   │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ↓
┌─────────────────────────────────────────────────────────────┐
│                 AnimationScheduler                           │
│  • Updates all active AnimatedState instances               │
│  • Called every frame from layoutAndRender()                │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ↓
┌─────────────────────────────────────────────────────────────┐
│                   AnimatedState<T>                           │
│  • Interpolates between values over time                    │
│  • Notifies observers if value changes                      │
│  • Self-registers with scheduler                            │
└─────────────────────────────────────────────────────────────┘
```

## 🎨 Usage Examples

### Simple Color Animation
```java
var color = scope.animateColor(0xFF888888, 200);
box.e(new ElementRect(() -> color.get()));
box.onMouseEnter(() -> color.set(0xFFFFFFFF));
```

### Layout Animation
```java
var width = scope.animateInt(0, 300);
scope.bindLayout(width);
box.layout().fixedWidth(() -> width.get());
width.set(200);  // Animates smoothly!
```

See **04-USAGE-EXAMPLES.md** for more patterns.

## ✅ Testing Strategy

Each phase has specific tests:
- **Phase 1**: State notification performance, cleanup, backward compatibility
- **Phase 2**: Builder logic, supplier evaluation, type safety
- **Phase 3**: Animation smoothness, interpolators, easing functions

See **05-IMPLEMENTATION-CHECKLIST.md** for complete test list.

## 📈 Success Metrics

- ✅ State changes are O(1) instead of O(n)
- ✅ Layout animations skip recomposition
- ✅ Draw animations have zero composition/layout cost
- ✅ Animations run at 60fps on complex UIs
- ✅ No breaking changes to existing code
- ✅ Performance improvement measurable (>100x for state changes)

## 🤝 Contributing

When implementing:
1. Follow the phase order (1 → 2 → 3)
2. Use the checklist to track progress
3. Write tests as you go
4. Validate performance at each phase
5. Update documentation if design changes

## 📝 Notes

- All phases are designed to be backward compatible
- Existing code continues to work without modification
- New features are opt-in for better performance
- The design is extensible for future enhancements

## 🔗 Related Files

### Files to Modify
- `UITree.java` - All phases
- `TreeNode.java` - Phase 1
- `ITreeNode.java` - Phase 1
- `LayoutProperties.java` - Phase 2
- All scope classes - All phases

### Files to Create
- `ImmediateLayoutProperties.java` - Phase 2
- `DeferredLayoutProperties.java` - Phase 2
- `AnimatedState.java` - Phase 3
- `AnimationScheduler.java` - Phase 3
- `Interpolator.java` - Phase 3
- `Interpolators.java` - Phase 3
- `Easing.java` - Phase 3

## 💡 Design Decisions

### Why TreeNode as Observer?
- Eliminates O(n) tree walk on every state change
- Direct notification is simpler and faster
- No need for reverse index or complex bookkeeping

### Why Two Observer Types?
- Avoids conditional checks on every notification
- State directly invokes the right behavior
- Clear separation of concerns

### Why Supplier-Based Layout Properties?
- Enables deferred evaluation during layout phase
- Avoids recomposition for layout-only changes
- Compatible with Jetpack Compose's approach
- Transparent to the user (builder handles complexity)

### Why Frame-Time Based Animation?
- Game UIs don't need tick-alignment for visual effects
- Simpler implementation (no partialTicks handling)
- Smoother animations (true 60fps vs 20 TPS)
- Lower CPU usage (updates only when needed)

## 📚 Further Reading

- Jetpack Compose documentation on phases and recomposition
- React reconciliation and render phases
- Game loop optimization patterns
- UI animation best practices

---

**Ready to implement?** Start with **00-OVERVIEW.md** and work through the phases in order!
