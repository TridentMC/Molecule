# ContextMenu Component

## Overview
A popup menu that appears on right-click (or other trigger) showing contextual actions. Common in file managers, text editors, and complex UIs.

## Core Functionality

### Menu Structure
- List of menu items
- Menu item types: action, separator, submenu, checkbox, radio
- Dynamic menu items (enabled/disabled based on context)
- Nested submenus (menu items that open another menu)

### Trigger
- Right-click (most common)
- Long press (touch devices)
- Keyboard shortcut (Shift+F10, context menu key)
- Programmatic trigger

### Positioning
- Appear at mouse cursor position
- Adjust position if menu doesn't fit on screen
- Submenu positioning (to the right, or left if no room)

### Interaction
- Click item to execute action
- Hover over item to highlight
- Hover over submenu item to open submenu
- Click outside to close
- ESC to close
- Arrow keys to navigate

### Visual States
- Idle
- Hover (item highlighted)
- Active (item being clicked)
- Disabled (grayed out, not clickable)
- Open (for submenus)

## Menu Item Types

### Action Item
- Label and optional icon
- Click executes action
- Optional keyboard shortcut display

### Separator
- Visual divider between item groups
- Non-interactive

### Submenu Item
- Has arrow indicator (►)
- Hover opens nested menu
- Contains child menu items

### Checkbox Item
- Boolean toggle
- Shows checkmark when checked
- Click toggles state

### Radio Item
- Mutually exclusive options
- Shows indicator when selected
- Part of a radio group

## Event Handlers
- `onItemClick(MenuItem item)` - Menu item clicked
- `onOpen()` - Menu opened
- `onClose()` - Menu closed
- Per-item action callbacks

## Styling Properties
- Menu background color/sprite
- Menu border and shadow
- Item height
- Item hover background
- Item text color
- Item icon size
- Separator style
- Submenu arrow icon
- Disabled item appearance
- Keyboard shortcut text color/style

## Component Parts

### Menu Container
- Background panel
- Border and shadow
- Contains all menu items
- Overlays other UI

### Menu Item
- Icon (optional)
- Label text
- Keyboard shortcut text (optional)
- Submenu arrow (if submenu)
- Checkbox/radio indicator (if applicable)
- Hover highlight

### Separator
- Horizontal line
- Non-interactive
- Groups related items

## Implementation Considerations

### Rendering Layer
- Must render above all other UI (overlay)
- High z-index or separate render layer
- Not clipped by parent containers

### Positioning Logic
- Calculate menu bounds
- Detect screen edges
- Flip horizontally/vertically if doesn't fit
- Submenu positioning: prefer right, fallback to left

### Click-Outside Detection
- Close menu when clicking outside
- Don't close when clicking inside menu
- Propagate or stop click events?

### Submenu Timing
- Delay before opening submenu on hover (e.g., 200ms)
- Immediate open on click?
- Keep submenu open when mouse moves into it

### Keyboard Navigation
- Arrow up/down to navigate items
- Arrow right to open submenu
- Arrow left to close submenu
- Enter to activate item
- ESC to close menu

### Nested Menus
- Track menu stack (main menu → submenu → sub-submenu)
- Close all on item click
- Close child menus when hovering different parent item

## Minecraft-Specific Features
- Match vanilla GUI menu style
- Use Minecraft sound effects
- Common actions: Delete, Rename, Properties, etc.

## Example Usage
```java
// Simple context menu
var contextMenu = ContextMenu.builder()
    .item("Cut", cutIcon, this::onCut, "Ctrl+X")
    .item("Copy", copyIcon, this::onCopy, "Ctrl+C")
    .item("Paste", pasteIcon, this::onPaste, "Ctrl+V")
    .separator()
    .item("Delete", deleteIcon, this::onDelete, "Del")
    .build();

// Attach to an element
var fileElement = ...;
fileElement.onRightClick(() -> {
    contextMenu.show(mouseX, mouseY);
});

// With submenu
var menu = ContextMenu.builder()
    .item("New", newIcon, null)
        .submenu()
            .item("File", this::newFile)
            .item("Folder", this::newFolder)
            .item("Shortcut", this::newShortcut)
        .endSubmenu()
    .item("Open", openIcon, this::onOpen)
    .separator()
    .item("Properties", propertiesIcon, this::onProperties)
    .build();

// With checkboxes
var viewMenu = ContextMenu.builder()
    .checkbox("Show Hidden Files", showHidden, this::toggleHidden)
    .checkbox("List View", isListView, this::toggleView)
    .separator()
    .radio("Sort by Name", sortMode == NAME, () -> setSortMode(NAME))
    .radio("Sort by Date", sortMode == DATE, () -> setSortMode(DATE))
    .radio("Sort by Size", sortMode == SIZE, () -> setSortMode(SIZE))
    .build();
```

## Menu Item Properties

### Label
- Text displayed to user
- Can include keyboard shortcut hint

### Icon
- Optional icon before label
- Helps identify action visually

### Callback
- Function to execute on click
- Null for disabled items or submenus

### Keyboard Shortcut
- Text displayed on right side
- E.g., "Ctrl+C", "Del", "F2"
- Informational only (shortcut handling separate)

### Enabled/Disabled
- Dynamic based on context
- Disabled items grayed out and not clickable

## Advanced Features

### Dynamic Menus
- Menu items change based on selection
- E.g., "Delete 3 files" vs "Delete file"
- Generate menu on open, not statically

### Icons in Checkboxes/Radio
- Combine icon with checkbox
- Icon + checkmark indicator

### Menu Animations
- Fade in on open
- Slide out on close
- Subtle, quick animations

### Menu Sections with Headers
- Section label (non-clickable)
- Groups of related items
- Visual grouping beyond separators

### Recent Items
- Show recently used actions
- Dynamic list

### Search/Filter
- Type to filter menu items
- For very large context menus
- Rare, usually menus are small

## ContextMenu vs Dropdown

### Use ContextMenu when:
- Triggered by right-click
- Contextual to selected item(s)
- Actions specific to context
- Not always visible

### Use Dropdown when:
- Triggered by clicking a button
- Permanent UI element
- List of options or selections
- Always available

## Dependencies
- Menu item components (action, separator, checkbox, radio)
- Overlay rendering layer
- Positioning utilities
- Event system (right-click, hover, keyboard)
- Submenu management

## Priority
**LOW-MEDIUM** - Useful for advanced UIs, but not essential for basic forms and inputs
