# TextField Component

## Overview
A single-line text input component for user text entry. Essential for forms, search boxes, and any text-based input.

## Core Functionality

### Text Management
- Store and display current text value
- Support for initial/default text
- Placeholder text when empty
- Maximum length constraint (optional)
- Text validation (optional, via predicate)

### Cursor & Selection
- Visible text cursor with blinking animation
- Cursor positioning via mouse click
- Cursor movement via arrow keys (left/right)
- Home/End key support (jump to start/end)
- Text selection via mouse drag
- Shift+arrow key selection
- Double-click to select word
- Triple-click to select all

### Editing Operations
- Character insertion at cursor
- Backspace (delete before cursor)
- Delete key (delete after cursor)
- Cut/Copy/Paste support (Ctrl+X/C/V)
- Select all (Ctrl+A)
- Undo/Redo support (optional for v1)

### Visual States
- Focused state (actively receiving input)
- Unfocused/idle state
- Disabled state
- Error state (for validation failures)
- Different border/background for each state

### Scrolling (for long text)
- Horizontal text scrolling when text exceeds width
- Auto-scroll to cursor position
- Mouse wheel horizontal scroll support (optional)

## Event Handlers

### Input Events
- `onTextChanged(String oldText, String newText)` - Text modified
- `onSubmit(String text)` - Enter key pressed
- `onFocusGained()` - Component receives focus
- `onFocusLost()` - Component loses focus
- `onValidationChanged(boolean valid)` - Validation state changes

## Styling Properties
- Background color/sprite (per state)
- Border color/thickness (per state)
- Text color
- Placeholder text color (lighter/grayed)
- Cursor color
- Selection highlight color
- Padding (internal spacing)
- Font (use existing label font system)

## Implementation Considerations

### Focus Management
- Only one TextField can be focused at a time globally
- Click to focus
- Tab key to cycle focus between fields (needs global focus manager)
- ESC to unfocus (optional)

### Text Rendering
- Use existing font rendering from ElementLabel
- Clip text to component bounds
- Render selection highlight behind text
- Render cursor as vertical line

### Performance
- Don't rebuild component on every character typed
- Use mutable state for text content
- Efficient string operations for insert/delete

### Integration with Existing Framework
- Extend `Element` base class
- Use `onCharTyped()` for character input
- Use `onClick()` for cursor positioning
- Use `onKeyPress()` for special keys (arrows, delete, etc.)
- Use `onMouseDrag()` for text selection

## Minecraft-Specific Features
- Support for Minecraft color codes (optional)
- Support for formatting codes (optional)
- Resource location validation mode (for mod developers)
- Numeric-only mode (for number entry)

## Example Usage
```java
var nameField = TextField.builder()
    .placeholder("Enter your name...")
    .maxLength(32)
    .onTextChanged((old, newText) -> {
        System.out.println("Name: " + newText);
    })
    .onSubmit(text -> {
        savePlayerName(text);
    })
    .build();
```

## Dependencies
- Existing event system (CharEvent, KeyInputEvent, MouseClickEvent)
- Existing font rendering (from ElementLabel)
- Focus management system (may need to be created)

## Priority
**HIGH** - Essential form input component used in almost every UI
