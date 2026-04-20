# ListView Component

## Overview
A scrollable list component for displaying and selecting items from a dynamic collection. Essential for file browsers, settings lists, inventories, etc.

## Core Functionality

### Data Management
- List of items (generic type)
- Item renderer (function to create UI for each item)
- Dynamic item addition/removal
- Item sorting
- Item filtering

### Selection
- Single selection mode (one item at a time)
- Multi-selection mode (multiple items)
- No selection mode (display only)
- Programmatic selection
- Selection callbacks

### Interaction
- Click to select item
- Double-click to activate/open item
- Arrow keys to navigate (when focused)
- Ctrl+Click for multi-select toggle
- Shift+Click for range selection
- Ctrl+A to select all

### Scrolling
- Vertical scrolling (most common)
- Horizontal scrolling (less common)
- Mouse wheel scrolling
- Keyboard scrolling (arrow keys, page up/down)
- Scroll to specific item

### Visual States
- Selected item(s) highlight
- Hover item highlight
- Focused state
- Disabled state

## Event Handlers
- `onSelectionChanged(List<T> selected)` - Selection changes
- `onItemClick(T item)` - Item clicked
- `onItemDoubleClick(T item)` - Item double-clicked
- `onItemActivated(T item)` - Item activated (Enter key or double-click)

## Styling Properties
- Item height (fixed or dynamic)
- Item background (normal, hover, selected)
- Item text color
- Selection highlight color
- Border style
- Scrollbar styling
- Padding between items
- Alternating row colors (optional)

## Component Parts

### List Container
- Scrollable container
- Clips content to bounds
- Manages visible item range

### List Items
- Individual item renderers
- Clickable, selectable
- Hover and selection states
- Custom content per item

### Scrollbar
- Vertical scrollbar (if content exceeds height)
- Drag thumb or click track
- Mouse wheel support

### Empty State (Optional)
- Message when list is empty
- E.g., "No items to display"

## Implementation Considerations

### Virtualization (Important!)
- Only render visible items (viewport culling)
- Reuse item elements as user scrolls
- Essential for large lists (1000+ items)
- Calculate visible item range based on scroll position

### Item Height
- Fixed height: all items same size (efficient)
- Dynamic height: items can vary (more complex)
- Calculate total list height for scrollbar

### Selection Management
- Track selected items (Set or List)
- Single selection: clear previous on new selection
- Multi-selection: toggle or add to selection
- Range selection: select all between anchor and target

### Rendering
- Only render visible items + small buffer
- Update visible items on scroll
- Efficient item recycling

### Performance
- Don't rebuild entire list on selection change
- Use state management for selection
- Efficient scroll event handling
- Lazy load item content if expensive

## Minecraft-Specific Features
- Server list style (with icons, MOTD, player count)
- Resource pack list style
- Language selection list
- Key bindings list

## Example Usage
```java
// Simple string list
var fileList = ListView.<String>builder()
    .items(List.of("file1.txt", "file2.txt", "file3.txt"))
    .itemRenderer(fileName -> ElementLabel.of(fileName))
    .selectionMode(SelectionMode.SINGLE)
    .onItemDoubleClick(fileName -> openFile(fileName))
    .build();

// Custom item type with complex rendering
record PlayerInfo(String name, int level, String status) {}

var playerList = ListView.<PlayerInfo>builder()
    .items(players)
    .itemRenderer(player ->
        Row.builder()
            .child(ElementLabel.of(player.name()))
            .child(ElementLabel.of("Lv " + player.level()))
            .child(ElementLabel.of(player.status()))
            .build()
    )
    .itemHeight(30)
    .onSelectionChanged(selected -> {
        selectedPlayers.setAll(selected);
    })
    .build();

// Multi-select list
var optionsList = ListView.<String>builder()
    .items(availableOptions)
    .selectionMode(SelectionMode.MULTI)
    .onSelectionChanged(selected -> {
        updateSelectedOptions(selected);
    })
    .build();
```

## Selection Modes

### Single Selection
- Only one item can be selected
- Click new item deselects previous
- Common for "choose one" scenarios

### Multi Selection
- Multiple items can be selected
- Ctrl+Click to toggle selection
- Shift+Click for range selection
- Ctrl+A for select all
- Common for batch operations

### No Selection (Display Only)
- Items cannot be selected
- List is read-only
- Can still trigger click/double-click events

## Item Rendering

### Simple Text Items
- Just a label per item
- Efficient, minimal

### Complex Items
- Icons + text
- Multiple columns
- Badges, status indicators
- Custom layouts per item

### Alternating Row Colors
- Helps readability in long lists
- Subtle background color difference

## Advanced Features

### Grouping/Headers
- Group items under headers
- Non-selectable header rows
- Collapsible groups (expand/collapse)

### Filtering
- Show subset of items based on criteria
- Search/filter text input
- Live filtering as user types

### Sorting
- Sort by column (if multi-column)
- Ascending/descending
- Custom sort comparators

### Drag and Drop
- Reorder items by dragging
- Drag items to other lists
- Drop zones

### Context Menu
- Right-click on item
- Show contextual actions
- Delete, rename, properties, etc.

### Inline Editing
- Double-click or F2 to edit item
- Edit in place (like renaming)
- Confirm/cancel editing

### Virtual Scrolling
- Handle extremely large lists (10,000+ items)
- Only create DOM elements for visible items
- Essential for performance

## ListView vs Grid vs Table

### Use ListView when:
- Single column of items
- Simple item layouts
- Vertical scrolling
- Selection is primary interaction

### Use Grid when:
- Items are uniformly sized (like icons)
- Multi-column, multi-row layout
- Visual browsing (images, thumbnails)

### Use Table when:
- Multi-column data
- Each column has header
- Sorting by column needed
- Structured, tabular data

## Dependencies
- ScrollArea component (for scrolling)
- Item rendering system (custom per item)
- Selection state management
- Event system (click, keyboard)
- Virtualization utilities

## Priority
**MEDIUM-HIGH** - Very useful for many UIs, especially configuration and browsers
