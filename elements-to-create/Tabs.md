# Tabs Component

## Overview
A tabbed container for organizing content into separate sections that can be switched between. Essential for complex UIs with multiple logical pages.

## Core Functionality

### Tab Management
- List of tab definitions (label, content, optional icon)
- Currently active/selected tab
- Default/initial tab selection
- Programmatic tab switching
- Dynamic tab addition/removal (optional)

### Interaction
- Click tab button to switch
- Keyboard navigation (Ctrl+Tab, Ctrl+Shift+Tab)
- Arrow keys to navigate between tab buttons when focused
- Close button on tabs (optional, for closeable tabs)

### Visual States
- Active tab (selected, showing content)
- Inactive tabs (not selected)
- Hover state (for tab buttons)
- Disabled tabs (cannot be selected)
- Focused state (keyboard navigation)

### Content Area
- Shows content of active tab only
- Smooth transition between tabs (optional animation)
- Content preserved when switching (or lazy loaded)

### Tab Button Layout
- Horizontal row at top (most common)
- Horizontal row at bottom
- Vertical column on left
- Vertical column on right

## Event Handlers
- `onTabChanged(Tab oldTab, Tab newTab)` - Tab selection changes
- `onTabClosed(Tab tab)` - Tab closed (if closeable)
- `onTabAdded(Tab tab)` - New tab added dynamically
- `onTabRemoved(Tab tab)` - Tab removed dynamically

## Styling Properties
- Tab button width (fixed or auto)
- Tab button height
- Tab button background (active vs inactive)
- Tab button border
- Active tab indicator (underline, highlight, etc.)
- Tab label text color
- Icon size and position
- Content area background
- Content area padding
- Tab spacing

## Component Parts

### Tab Bar
- Container for all tab buttons
- Horizontal or vertical layout
- Scrollable if many tabs

### Tab Button
- Clickable button to switch tabs
- Shows tab label and optional icon
- Visual distinction for active tab
- Optional close button (X icon)

### Content Area
- Container showing active tab's content
- Only one tab content visible at a time
- Full width/height of container

### Active Tab Indicator
- Underline, highlight, or color change
- Shows which tab is selected
- Animated transition between tabs (optional)

## Implementation Considerations

### Content Rendering
- Only render active tab content (efficient)
- Or render all tabs but hide inactive (preserves state)
- Or lazy load content on first activation

### Tab Switching
- Instant switch (no animation)
- Fade transition (crossfade old/new content)
- Slide transition (slide content left/right)

### Many Tabs Handling
- Scrollable tab bar with arrows
- Dropdown "more tabs" menu
- Wrap to multiple rows (less common)

### Tab State Preservation
- Keep inactive tab content in memory
- Or recreate content on tab activation
- Important for forms with unsaved changes

### Dynamic Tabs
- Allow adding/removing tabs at runtime
- Closeable tabs (X button)
- What happens when active tab is closed? (select adjacent tab)

## Minecraft-Specific Features
- Match vanilla Creative inventory tab style
- Match vanilla Anvil/Enchanting table tabs (if any)
- Use Minecraft UI sprites for tab buttons

## Example Usage
```java
// Static tabs with pre-defined content
var settings = Tabs.builder()
    .tab("General", generalSettingsContent)
    .tab("Graphics", graphicsSettingsContent)
    .tab("Audio", audioSettingsContent)
    .defaultTab("General")
    .onTabChanged((oldTab, newTab) -> {
        System.out.println("Switched to: " + newTab.getLabel());
    })
    .build();

// Tabs with icons
var inventory = Tabs.builder()
    .tab("Weapons", weaponIcon, weaponsContent)
    .tab("Armor", armorIcon, armorContent)
    .tab("Items", itemIcon, itemsContent)
    .position(TabPosition.TOP)
    .build();

// Dynamic/closeable tabs
var documents = Tabs.builder()
    .closeable(true)
    .onTabClosed(tab -> {
        closeDocument(tab.getId());
    })
    .build();

documents.addTab("Document1.txt", docContent1);
documents.addTab("Document2.txt", docContent2);
```

## Tab Positioning

### Top (Most Common)
- Tab buttons above content
- Natural left-to-right reading order
- Minecraft Creative inventory style

### Bottom
- Tab buttons below content
- Less common, but valid

### Left
- Vertical tab buttons on left side
- Good for wide content areas
- Labels can be longer

### Right
- Vertical tab buttons on right side
- Less common

## Advanced Features

### Scrollable Tab Bar
- When too many tabs to fit
- Left/right arrow buttons
- Or horizontal scroll

### Tab Groups/Separators
- Visual grouping of related tabs
- Separator lines between groups

### Tab Badges
- Notification count on tab (e.g., "Messages (3)")
- Colored dot indicator
- Attention-grabbing for updates

### Nested Tabs
- Tabs within tab content
- Creates hierarchical navigation
- Can be complex to manage

### Tab Context Menu
- Right-click on tab for options
- Close, close others, close all to right, etc.
- Pin/unpin tabs

## Tabs vs Other Navigation

### Use Tabs when:
- 2-7 distinct sections
- All sections are equally important
- User might switch between sections frequently
- Content in sections is independent

### Use Accordion/Expandable instead when:
- Many sections (8+)
- Hierarchical content
- Multiple sections can be open simultaneously

### Use Dropdown Menu instead when:
- Many navigation options
- Space constrained
- Options are more like commands than sections

## Dependencies
- Button component (for tab buttons)
- Layout system (Row or Column for tab bar)
- Content container (Stack for switching content)
- Event system
- State management

## Priority
**MEDIUM** - Very useful for complex UIs, but can defer until basic inputs are complete
