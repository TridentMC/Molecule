# Tooltip Component

## Overview
A small popup that appears on hover to provide additional information or help text. Essential for explaining UI elements without cluttering the interface.

## Core Functionality

### Content
- Text content (single or multi-line)
- Optional title/header
- Optional icon
- Rich content (formatted text, images, etc.)
- Dynamic content via supplier

### Trigger Behavior
- Appears on mouse hover (after short delay)
- Disappears when mouse leaves
- Configurable delay before showing
- Configurable delay before hiding
- Keyboard trigger (focus + wait, or explicit key)

### Positioning
- Position relative to target element
- Common positions: top, bottom, left, right
- Auto-positioning (flip if doesn't fit on screen)
- Offset from target (gap between tooltip and element)
- Arrow/pointer pointing to target (optional)

### Visual States
- Showing (visible)
- Hidden (invisible)
- Fade in/out animation (optional)

## Event Handlers
- `onShow()` - Tooltip becomes visible
- `onHide()` - Tooltip becomes hidden

## Styling Properties
- Background color/sprite
- Border style
- Text color
- Padding
- Max width (word wrap for long text)
- Font size
- Arrow/pointer style and color
- Shadow/elevation
- Animation duration

## Component Parts

### Tooltip Container
- Background box with border
- Contains all tooltip content
- Positioned relative to target

### Content Area
- Text, images, or custom content
- Word wrapping for long text
- Multi-line support

### Arrow/Pointer (Optional)
- Small triangle pointing to target
- Position depends on tooltip placement
- Matches tooltip background color

## Implementation Considerations

### Hover Detection
- Use `onMouseEnter()` and `onMouseExit()` events
- Start timer on mouse enter
- Show tooltip after delay (e.g., 500ms)
- Cancel timer on mouse exit
- Hide tooltip immediately or after short delay

### Positioning Logic
- Calculate tooltip bounds
- Calculate available space in all directions
- Choose best position (prefer specified, fallback if no room)
- Adjust for screen edges (keep tooltip fully visible)
- Position arrow to point at target

### Rendering Layer
- Tooltip must render above all other UI
- Use highest z-index or separate "overlay" layer
- Ensure tooltip isn't clipped by parent containers

### Multiple Tooltips
- Only one tooltip visible at a time globally
- New hover closes previous tooltip
- Manage via singleton tooltip manager

### Performance
- Don't create/destroy tooltip element on every hover
- Reuse single tooltip component, update content
- Efficient positioning calculations

### Word Wrapping
- Set max width for tooltip
- Wrap text to multiple lines
- Calculate height based on wrapped content

## Minecraft-Specific Features
- Match vanilla Minecraft tooltip style (items, blocks)
- Support Minecraft text formatting (§ codes)
- Item stack tooltips (rarity colors, enchantments, etc.)
- Keybind display in tooltips

## Example Usage
```java
// Simple text tooltip
var button = Button.builder()
    .content(...)
    .tooltip("Click to save your changes")
    .build();

// Multi-line tooltip
var icon = ElementSprite.builder()
    .sprite(infoIcon)
    .tooltip("""
        This feature is experimental.
        Use with caution.
        """)
    .build();

// Rich tooltip with title
var item = ElementItem.builder()
    .item(itemStack)
    .tooltip(Tooltip.builder()
        .title("Diamond Sword")
        .line("Damage: 7")
        .line("Durability: 1561")
        .line("Sharpness IV")
        .build())
    .build();

// Dynamic tooltip
var statusIndicator = ElementRect.builder()
    .color(statusColor)
    .tooltip(() -> "Status: " + getStatusText())
    .build();

// Positioned tooltip
var helpIcon = ElementSprite.builder()
    .sprite(helpIcon)
    .tooltip(Tooltip.builder()
        .text("Click for more information")
        .position(TooltipPosition.RIGHT)
        .delay(200)
        .build())
    .build();
```

## Positioning Strategies

### Position Priority
1. Prefer specified position (top, bottom, left, right)
2. If doesn't fit, try opposite position
3. If still doesn't fit, try perpendicular positions
4. If nothing fits, show anyway and clip (rare)

### Auto-Positioning
- Calculate space in all 4 directions
- Choose direction with most space
- Ensure tooltip stays on screen

### Arrow Alignment
- Arrow points to center of target element
- Tooltip position adjusts to keep arrow aligned
- Arrow omitted if tooltip is far from target

## Tooltip Types

### Hover Tooltip (Standard)
- Appears on mouse hover
- Disappears when mouse leaves
- Most common type

### Focus Tooltip
- Appears when element receives keyboard focus
- Useful for accessibility
- Disappears when focus lost

### Click Tooltip
- Appears on click (more like popover)
- Remains until clicked away or close button
- Can contain interactive content

### Always Visible (Not a tooltip)
- For critical information
- Use label or badge instead

## Advanced Features

### Interactive Tooltips
- Tooltip contains clickable elements
- Keep tooltip open when mouse moves into it
- Close button within tooltip
- Use case: help text with links

### Delayed Hide
- Tooltip remains visible for a moment after mouse leaves
- Gives user time to move mouse to tooltip (if interactive)
- Configurable delay

### Tooltip Chaining
- Tooltip within tooltip (nested)
- Rare, can be confusing

### Keyboard Shortcuts
- Show tooltip on key press (e.g., hold Alt)
- Useful for accessibility
- Show all tooltips at once (debug mode)

## Accessibility Considerations
- ARIA attributes for screen readers (if applicable)
- Keyboard access to tooltip content
- Sufficient contrast for readability
- Font size accessibility

## Dependencies
- Event system (onMouseEnter, onMouseExit)
- Timing system (delays)
- Positioning utilities (calculate screen bounds)
- Overlay rendering layer (highest z-index)
- Label rendering (for text content)

## Priority
**MEDIUM** - Very useful for UX, already noted as TODO in ComposedSlot:182
