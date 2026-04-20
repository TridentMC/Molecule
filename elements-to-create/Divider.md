# Divider / Separator Component

## Overview
A visual separator line to divide sections of UI content. Helps organize layouts and improve visual hierarchy.

## Core Functionality

### Visual Representation
- Horizontal line (most common)
- Vertical line
- Configurable thickness
- Configurable color
- Optional label/text in the middle

### Orientation
- Horizontal (divides top/bottom sections)
- Vertical (divides left/right sections)

### Styling
- Solid line
- Dashed line
- Dotted line
- Gradient line (fade in/out at edges)
- Custom sprite/texture

## Styling Properties
- Line thickness (1-3px typically)
- Line color
- Line style (solid, dashed, dotted)
- Length (full width/height or partial)
- Margin (spacing above/below for horizontal, left/right for vertical)
- Label text (optional)
- Label background color
- Label padding

## Component Parts

### Line
- The visual separator
- Can be a simple rectangle
- Or a sprite-based texture

### Label (Optional)
- Text in the middle of the divider
- Breaks the line into two segments
- Background color to cover line behind text
- Common for section headers

## Implementation Considerations

### Rendering
- Simple rectangle fill for solid line
- Dashed pattern for dashed line (repeat sprite or custom rendering)
- Gradient for fading edges
- Clip to bounds

### Sizing
- Horizontal divider: full width of parent, small fixed height
- Vertical divider: full height of parent, small fixed width
- Or configurable length (partial width/height)

### Layout Integration
- Works well in Column (horizontal dividers)
- Works well in Row (vertical dividers)
- Self-sizing based on parent container

### Label Rendering
- Render line in two parts (left and right of label)
- Or render line fully, then label on top with background

## Minecraft-Specific Features
- Match vanilla Minecraft menu divider style
- Use existing UI textures if available
- Simple gray line is common in Minecraft UIs

## Example Usage
```java
// Simple horizontal divider
var divider = Divider.horizontal()
    .color(Color.GRAY)
    .thickness(1)
    .build();

// In a layout
Column.builder()
    .child(headerContent)
    .child(Divider.horizontal())
    .child(bodyContent)
    .child(Divider.horizontal())
    .child(footerContent)
    .build();

// Divider with label
var sectionDivider = Divider.horizontal()
    .label("Advanced Settings")
    .color(Color.DARK_GRAY)
    .build();

// Vertical divider in a row
Row.builder()
    .child(leftPanel)
    .child(Divider.vertical())
    .child(rightPanel)
    .build();
```

## Divider Styles

### Solid Line (Default)
- Simple, clean
- Most common

### Dashed Line
- Less prominent
- Suggests less strong separation

### Dotted Line
- Subtle separation
- Rare in modern UIs

### Gradient Fade
- Fades in from edges to center
- Soft, modern look

### Inset/Outset (3D)
- Two parallel lines with light/shadow
- Creates depth effect
- Retro style

## With Label Variants

### Center Label
- Label in the center of line
- Line breaks around label
- Common for section headers

### Left/Top Aligned Label
- Label at start of divider
- Line follows label
- Good for subsection dividers

### Right/Bottom Aligned Label
- Label at end of divider
- Less common

## Use Cases

### Section Separation
- Divide major sections of a form
- Separate content areas

### List Item Separation
- Between list items (alternative to spacing)
- Creates clear boundaries

### Menu Separators
- In dropdown menus
- In context menus
- Groups related menu items

### Visual Hierarchy
- Break up dense content
- Guide user's eye

## Divider vs Spacing

### Use Divider when:
- Clear visual separation needed
- Sections are distinct and important
- Formal, structured layout

### Use Spacing (ElementSpacer) when:
- Subtle separation is enough
- Minimal visual clutter preferred
- Informal layout

## Advanced Features

### Thickness Variants
- Thin (1px) - subtle
- Medium (2px) - standard
- Thick (3-5px) - prominent, rare

### Decorative Dividers
- Ornamental patterns
- Custom sprites
- Icons in the middle
- More decorative than functional

### Animated Dividers
- Fade in when scrolling to section
- Gradient animation
- Rare, mostly for visual polish

## Dependencies
- Rendering system (rectangles or sprites)
- Label rendering (if label variant)
- Minimal dependencies overall

## Priority
**LOW** - Nice-to-have for visual polish; can use ElementRect or ElementSpacer initially
