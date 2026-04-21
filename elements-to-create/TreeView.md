# TreeView Component

## Overview
A hierarchical list component for displaying and navigating tree-structured data. Essential for file explorers, nested settings, and any hierarchical data.

## Core Functionality

### Data Structure
- Tree nodes (parent-child relationships)
- Root nodes (top-level)
- Child nodes (nested under parents)
- Leaf nodes (no children)
- Generic node data type

### Expansion/Collapse
- Expand node to show children
- Collapse node to hide children
- Expand/collapse all
- Remember expansion state per node
- Lazy loading of children (load on expand)

### Selection
- Single selection (one node at a time)
- Multi-selection (multiple nodes)
- Selection callbacks
- Keyboard navigation (arrow keys)

### Interaction
- Click expand/collapse icon
- Click node to select
- Double-click to expand/collapse
- Arrow keys: up/down to navigate, left to collapse, right to expand
- Enter to activate/open selected node

### Visual States
- Expanded node (showing children)
- Collapsed node (hiding children)
- Selected node(s)
- Hover node
- Focused state

## Event Handlers
- `onNodeExpanded(Node node)` - Node expanded
- `onNodeCollapsed(Node node)` - Node collapsed
- `onSelectionChanged(List<Node> selected)` - Selection changes
- `onNodeClick(Node node)` - Node clicked
- `onNodeDoubleClick(Node node)` - Node double-clicked
- `onNodeActivated(Node node)` - Node activated (Enter or double-click)

## Styling Properties
- Node height
- Indentation per level (e.g., 20px per depth)
- Expand/collapse icon (arrow, +/-, folder icons)
- Node background (normal, hover, selected)
- Node text color
- Icon size and spacing
- Connector lines (optional, show hierarchy)
- Scrollbar styling

## Component Parts

### Tree Node
- Expand/collapse button/icon
- Node icon (optional, e.g., folder/file icon)
- Node label text
- Node content (custom rendering)
- Children container (shown when expanded)

### Expand/Collapse Icon
- Arrow: right when collapsed, down when expanded
- Or +/- symbols
- Or folder icons (closed/open)
- Clickable to toggle expansion

### Indentation
- Each level indented more than parent
- Visual hierarchy
- Typically 20-30px per level

### Connector Lines (Optional)
- Lines connecting parent to children
- Vertical and horizontal lines
- Classic file explorer style

### Scrollbar
- Vertical scrolling for tall trees
- Horizontal scrolling if node content is wide

## Implementation Considerations

### Data Model
- Tree node structure: data + children
- Recursive data structure
- Generic type for node data

### Expansion State
- Track which nodes are expanded
- Store in Set or Map
- Persist state across rebuilds

### Rendering
- Recursive rendering of nodes and children
- Only render expanded nodes' children
- Virtualization for large trees (complex)

### Indentation Calculation
- Calculate depth from root
- Apply indentation: `depth * indentSize`

### Lazy Loading
- Don't load all children upfront
- Load children on first expand
- Useful for file systems, large datasets

### Performance
- Virtualization for very large trees (complex)
- Don't rebuild entire tree on expand/collapse
- Efficient state management

## Minecraft-Specific Features
- File system browsing (resource packs, screenshots)
- NBT data viewer (nested tags)
- World folder structure
- Mod configuration trees

## Example Usage
```java
// Simple file tree
record FileNode(String name, boolean isDirectory, List<FileNode> children) {}

var fileTree = TreeView.<FileNode>builder()
    .rootNodes(rootFolders)
    .childrenProvider(node -> node.children())
    .nodeRenderer(node ->
        Row.builder()
            .child(node.isDirectory() ? folderIcon : fileIcon)
            .child(ElementLabel.of(node.name()))
            .build()
    )
    .onNodeDoubleClick(node -> openFile(node))
    .build();

// Settings tree
record Setting(String label, List<Setting> subsettings) {}

var settingsTree = TreeView.<Setting>builder()
    .rootNodes(topLevelSettings)
    .childrenProvider(setting -> setting.subsettings())
    .nodeRenderer(setting -> ElementLabel.of(setting.label()))
    .onSelectionChanged(selected -> {
        showSettingDetails(selected.get(0));
    })
    .build();

// Lazy-loaded tree
var lazyTree = TreeView.<FolderNode>builder()
    .rootNodes(rootFolders)
    .childrenProvider(folder -> loadChildren(folder)) // Loads on expand
    .lazyLoad(true)
    .build();
```

## Node Types

### Branch Node
- Has children (or can have children)
- Can be expanded/collapsed
- Shows expand/collapse icon

### Leaf Node
- No children
- Cannot be expanded
- No expand/collapse icon

### Root Node
- Top-level node (no parent)
- Can be hidden (show only children)
- Or shown (visible root node)

## Expansion Modes

### Manual Expansion
- User clicks to expand/collapse
- State preserved

### Auto-Expand
- Automatically expand nodes on certain conditions
- E.g., expand to show selected node
- Expand all on search

### Initial Expansion
- Set which nodes are initially expanded
- E.g., expand first level only
- Or expand specific paths

## Visual Styles

### Modern (No Connector Lines)
- Clean, minimal
- Just indentation and icons
- Most common in modern UIs

### Classic (With Connector Lines)
- Lines connecting nodes
- Traditional file explorer style
- Helps visualize hierarchy

### Icon-Based
- Folder icons for branch nodes
- File icons for leaf nodes
- No separate expand/collapse icon (click icon to toggle)

## Advanced Features

### Searching/Filtering
- Search tree nodes by text
- Expand path to matching nodes
- Highlight matches

### Drag and Drop
- Drag nodes to reorder
- Drag to different parent (reparent)
- Drop zones for moving nodes

### Checkboxes
- Checkbox on each node
- Multi-select via checkboxes
- Parent checkbox checks all children

### Context Menu
- Right-click on node
- Contextual actions (rename, delete, etc.)

### Inline Editing
- Rename node in place
- Double-click or F2 to edit

### Badges/Annotations
- Show count of children
- Status indicators
- Unread counts

### Virtual Scrolling
- For extremely large trees
- Only render visible nodes
- Complex but necessary for huge datasets

## TreeView vs ListView

### Use TreeView when:
- Data is hierarchical (parent-child)
- Need to show/hide branches
- Navigation through nested structure

### Use ListView when:
- Data is flat (single level)
- No hierarchical relationships
- Simpler, more efficient

## Dependencies
- ListView concepts (selection, scrolling)
- Recursive rendering
- Expansion state management
- Event system
- Icon/sprite system

## Priority
**LOW-MEDIUM** - Specialized for hierarchical data; not needed for most UIs
