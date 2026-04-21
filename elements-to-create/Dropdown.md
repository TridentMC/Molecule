# Dropdown / Select Component

## Overview
A dropdown menu component for selecting a single option from a list of choices. Essential for configuration UIs and forms.

## Core Functionality

### Selection Management
- List of available options
- Currently selected option
- Default/initial selection
- Programmatic selection
- Optional "no selection" state

### Interaction
- Click to open dropdown menu
- Click outside to close menu
- Click option to select and close
- Arrow keys to navigate options (when focused)
- Enter to select highlighted option
- ESC to close without selecting
- Type to search/filter options (optional)

### Visual States
- Closed state (showing selected value)
- Open state (showing option list)
- Hover state (button highlight)
- Disabled state
- Focused state

### Option List Display
- Scrollable list (if many options)
- Highlighted option on hover
- Selected option indicator
- Maximum visible items (then scroll)
- List positioning (below or above if no room)

## Event Handlers
- `onSelectionChanged(T oldValue, T newValue)` - Selection changes
- `onOpen()` - Dropdown opens
- `onClose()` - Dropdown closes

## Styling Properties
- Button width and height
- Button background (per state)
- Button border
- Arrow/indicator icon
- List background
- List border
- Option height
- Option hover background
- Option selected indicator
- Text color
- Scrollbar styling
- Max dropdown height

## Component Parts

### Selection Button
- Displays currently selected value
- Down arrow icon indicating dropdown
- Click to toggle open/closed
- Can show placeholder text if no selection

### Dropdown List
- Overlays above other UI (higher z-index/render layer)
- Appears below button (or above if insufficient space)
- Contains scrollable list of options
- Closes when clicking outside

### Option Items
- Individual selectable items in list
- Hover highlighting
- Selected item indicator (checkmark, highlight, etc.)
- Custom rendering per option (icon + text)

### Scrollbar (if needed)
- Appears when options exceed max visible count
- Scroll with mouse wheel or drag

## Implementation Considerations

### Rendering Layers
- Dropdown list must render above all other UI
- Use Stack or similar for overlay rendering
- Or render dropdown in a separate "popup layer"

### Click-Outside Detection
- Detect clicks outside dropdown bounds
- Close dropdown when clicking elsewhere
- Don't close when clicking scrollbar

### List Positioning
- Calculate available space below button
- If insufficient, render above button instead
- Clamp to screen bounds

### Option Types
- Generic type parameter for option values
- Display text provider (Function<T, String>)
- Optional icon provider (Function<T, Sprite>)

### Performance
- Lazy render only visible options (viewport culling)
- Efficient option lookup
- Don't rebuild entire list on hover changes

### Focus Management
- When opened, focus moves to dropdown list
- Restore focus to button when closed
- Keyboard navigation within list

## Minecraft-Specific Features
- Match vanilla button style for closed state
- Use vanilla scrollbar styling
- Play Minecraft UI sounds on open/select
- Support for Minecraft text components (formatted text)

## Example Usage
```java
// Simple string dropdown
var difficulty = Dropdown.<String>builder()
    .options(List.of("Peaceful", "Easy", "Normal", "Hard"))
    .selected("Normal")
    .onSelectionChanged((old, newDiff) -> {
        setDifficulty(newDiff);
    })
    .build();

// Enum dropdown with custom display
var gameMode = Dropdown.<GameMode>builder()
    .options(List.of(GameMode.values()))
    .displayTextProvider(mode -> mode.getDisplayName())
    .selected(GameMode.SURVIVAL)
    .onSelectionChanged((old, mode) -> {
        changeGameMode(mode);
    })
    .build();

// With icons
var biome = Dropdown.<Biome>builder()
    .options(biomeList)
    .displayTextProvider(Biome::getName)
    .iconProvider(Biome::getIcon)
    .selected(Biomes.PLAINS)
    .maxVisibleItems(10)
    .build();
```

## Advanced Features

### Searchable Dropdown
- Type to filter options
- Shows only matching options
- Clears filter on selection or close

### Grouped Options
- Option groups with headers
- Visual separation between groups
- Collapse/expand groups (advanced)

### Multi-Select Dropdown (Different Component?)
- Checkboxes for each option
- Multiple selections allowed
- "Select All" / "Clear All" options
- Should this be a separate component?

## Dependencies
- Button component (for selection button)
- ScrollArea component (for scrollable list)
- Existing event system
- Focus management system
- Overlay/popup rendering system

## Priority
**HIGH** - Very common for configuration and selection UIs
