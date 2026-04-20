# Modal / Dialog Component

## Overview
A popup dialog window that overlays the main UI, requiring user interaction before continuing. Essential for confirmations, alerts, forms, and important messages.

## Core Functionality

### Content
- Title/header
- Body content (text, forms, custom UI)
- Footer with action buttons (OK, Cancel, etc.)
- Icon (for alert/warning/info dialogs)

### Behavior
- Blocks interaction with underlying UI (modal backdrop)
- Focus trapped within modal (tab cycles through modal elements)
- Can be closed via buttons, X button, ESC key, or backdrop click
- Prevents scrolling of background content

### Visual States
- Open (visible)
- Closed (hidden)
- Opening/closing animation (optional)

### Backdrop
- Semi-transparent overlay
- Darkens background UI
- Prevents clicks on background
- Optional: click backdrop to close

## Modal Types

### Alert
- Simple message with OK button
- Icon indicating type (info, warning, error, success)
- One action: acknowledge

### Confirm
- Question or action confirmation
- Two buttons: OK/Cancel, Yes/No, etc.
- Returns true/false or executes callback on confirm

### Prompt
- Input from user (like window.prompt)
- Text field for user input
- OK/Cancel buttons
- Returns input string or null

### Custom
- Arbitrary content
- Custom buttons and actions
- Full flexibility

### Form Dialog
- Contains form elements
- Submit/Cancel buttons
- Validation before close

## Event Handlers
- `onOpen()` - Modal opened
- `onClose()` - Modal closed
- `onConfirm()` - Confirm button clicked
- `onCancel()` - Cancel button clicked
- `onBackdropClick()` - Backdrop clicked

## Styling Properties
- Modal width and height
- Background color
- Border and shadow
- Backdrop color and opacity
- Title style (font, color)
- Button styling
- Padding and spacing
- Animation type and duration
- Corner radius

## Component Parts

### Backdrop
- Full-screen overlay
- Semi-transparent (e.g., 50% black)
- Prevents interaction with background
- Optional click-to-close

### Modal Window
- Centered container
- Elevated above backdrop
- Contains header, body, footer

### Header
- Title text
- Optional icon
- Close button (X) in top-right

### Body
- Main content area
- Scrollable if content is tall
- Can contain any UI elements

### Footer
- Action buttons (OK, Cancel, etc.)
- Right-aligned typically
- Spacing between buttons

### Close Button (X)
- Top-right corner of modal
- Always visible
- Closes modal

## Implementation Considerations

### Rendering Layer
- Render above all other UI
- Backdrop below modal, above main UI
- Use Stack or layering system

### Focus Management
- Trap focus within modal
- Tab cycles through modal elements only
- Restore focus to trigger element on close

### Scroll Locking
- Prevent scrolling background while modal open
- Re-enable on close

### Keyboard Handling
- ESC to close (unless prevented)
- Enter to confirm (if appropriate)
- Tab navigation within modal

### Animation
- Fade in backdrop and modal
- Slide down or scale up modal
- Fade out on close
- Smooth, quick transitions

### Stacking Modals
- Allow multiple modals (rare)
- Track modal stack
- Close top modal first

## Minecraft-Specific Features
- Match vanilla dialog style (death screen, disconnect screen)
- Use Minecraft fonts and colors
- Pause game when modal is open (if applicable)

## Example Usage
```java
// Simple alert
Modal.alert("Save Complete", "Your world has been saved successfully.")
    .show();

// Confirmation dialog
Modal.confirm("Delete World", "Are you sure you want to delete this world? This cannot be undone.")
    .onConfirm(() -> deleteWorld())
    .onCancel(() -> System.out.println("Cancelled"))
    .show();

// Prompt dialog
Modal.prompt("Enter Name", "Please enter your player name:")
    .defaultValue("Steve")
    .onConfirm(name -> setPlayerName(name))
    .show();

// Custom modal
var customModal = Modal.builder()
    .title("Settings")
    .body(
        Column.builder()
            .child(TextField.of("Username", username))
            .child(Checkbox.of("Enable notifications", notificationsEnabled))
            .build()
    )
    .button("Save", this::saveSettings)
    .button("Cancel", this::cancel)
    .closeOnBackdropClick(false)
    .show();

// Form modal
var formModal = Modal.builder()
    .title("Create Account")
    .body(accountForm)
    .confirmButton("Create")
    .cancelButton("Cancel")
    .onConfirm(() -> {
        if (validateForm()) {
            createAccount();
            return true; // close modal
        }
        return false; // keep modal open
    })
    .build();
```

## Modal Sizes

### Small
- Short message
- Few buttons
- Minimal content

### Medium
- Standard forms
- Moderate content
- Most common size

### Large
- Complex forms
- Lots of content
- Full settings panels

### Fullscreen
- Takes entire screen
- Close button or back navigation
- Mobile-friendly alternative

## Button Configurations

### Single Button
- "OK", "Close", "Got it"
- Just acknowledgment

### Two Buttons
- "OK" / "Cancel"
- "Yes" / "No"
- "Confirm" / "Cancel"
- Primary and secondary actions

### Three Buttons
- "Yes" / "No" / "Cancel"
- "Save" / "Don't Save" / "Cancel"
- Less common, can be confusing

### Custom Buttons
- Any number of buttons
- Custom labels
- Custom actions

## Advanced Features

### Non-Modal Dialogs
- Allow interaction with background
- Movable/draggable window
- More like a floating panel

### Persistent Modals
- Cannot be dismissed easily
- No close button or ESC
- Forces user action (use sparingly)

### Modal Stacks
- Open modal from within modal
- Track stack of open modals
- Close in LIFO order

### Draggable Modals
- Click and drag to reposition
- Useful for non-blocking modals

### Resizable Modals
- Drag edges to resize
- Useful for large content
- Complex to implement

### Multi-Step Modals (Wizards)
- Multiple pages/steps
- Next/Previous buttons
- Progress indicator
- Guide user through process

## Modal vs Other Overlays

### Use Modal when:
- Requires immediate attention
- Blocks further interaction
- Critical decision or input needed
- Short-lived interaction

### Use Popover/Tooltip when:
- Contextual information
- Non-blocking
- Dismissible easily

### Use Sidebar/Panel when:
- Persistent additional content
- Can interact with main UI
- Long-term reference

## Accessibility Considerations
- Focus trap (tab stays within modal)
- ESC to close
- ARIA role="dialog"
- Screen reader announcements
- Keyboard navigation

## Dependencies
- Backdrop component (semi-transparent overlay)
- Button component (for actions)
- Focus management system
- Event system (keyboard, mouse)
- Animation system (optional)
- Rendering layer system (overlay)

## Priority
**MEDIUM** - Very useful for many interactions, especially confirmations and alerts
