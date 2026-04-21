# TextArea Component

## Overview
A multi-line text input component for longer text entry. Extends TextField concept to support multiple lines with vertical scrolling.

## Core Functionality

### Text Management
- Multi-line text storage (List of lines or single string with \n)
- Line wrapping (word wrap vs character wrap)
- Maximum line count (optional)
- Maximum total character count (optional)
- Placeholder text on first line when empty

### Cursor & Selection
- 2D cursor position (line index, column index)
- Visible blinking cursor
- Cursor movement via arrow keys (up/down/left/right)
- Home/End for line start/end
- Ctrl+Home/End for document start/end
- Page Up/Down for scrolling by viewport height
- Mouse click positioning (calculate line and column)
- Text selection across multiple lines
- Shift+arrow key selection
- Mouse drag selection across lines

### Editing Operations
- Character insertion with line wrapping
- Backspace across line boundaries
- Delete across line boundaries
- Enter/Return creates new line
- Tab key insertion (or focus change - configurable)
- Cut/Copy/Paste (multi-line support)
- Select all (Ctrl+A)

### Visual States
- Focused/unfocused states
- Disabled state
- Error state
- State-based styling

### Scrolling
- Vertical scrollbar (when content exceeds height)
- Horizontal scrollbar (optional, if no word wrap)
- Auto-scroll to cursor on edit
- Mouse wheel scrolling
- Dragging scrollbar support

## Event Handlers
- `onTextChanged(String oldText, String newText)` - Text modified
- `onLineCountChanged(int oldCount, int newCount)` - Line count changes
- `onFocusGained()` / `onFocusLost()` - Focus events

## Styling Properties
- Background color/sprite
- Border styling
- Text color
- Placeholder color
- Line spacing
- Padding
- Scrollbar styling
- Selection highlight color
- Cursor color

## Implementation Considerations

### Line Management
- Efficient line storage (ArrayList<String> or rope data structure)
- Line height calculation (use font metrics)
- Visible line range calculation (viewport clipping)

### Rendering
- Only render visible lines (viewport culling)
- Clip content to bounds
- Render line numbers (optional feature)
- Word wrap calculation per line

### Scrolling Integration
- Reuse ScrollArea component infrastructure?
- Or implement custom scrollbar widget
- Smooth scrolling vs discrete line scrolling

### Performance
- Don't rebuild on every keystroke
- Efficient text insertion/deletion
- Lazy line wrapping calculation

### Text Wrapping
- Word-based wrapping (break at spaces)
- Character-based wrapping (break anywhere)
- Respect word boundaries when possible
- Cache wrapped line positions

## Minecraft-Specific Features
- Support for formatting codes across lines
- Book & Quill style editing mode
- Sign editing mode (4 lines, character limits)

## Example Usage
```java
var description = TextArea.builder()
    .placeholder("Enter description...")
    .maxLines(10)
    .wordWrap(true)
    .onTextChanged((old, newText) -> {
        saveDescription(newText);
    })
    .build();
```

## Dependencies
- TextField component (shares many concepts)
- Scrollbar component (or ScrollArea integration)
- Focus management system
- Font rendering system

## Priority
**MEDIUM** - Useful but less common than single-line TextField
