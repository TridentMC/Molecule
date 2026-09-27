package com.tridevmc.molecule.ui;

import com.tridevmc.compound.ui.element.Accordion;
import com.tridevmc.compound.ui.animation.Easing;
import com.tridevmc.compound.ui.element.Box;
import com.tridevmc.compound.ui.element.Button;
import com.tridevmc.compound.ui.element.Checkbox;
import com.tridevmc.compound.ui.element.Column;
import com.tridevmc.compound.ui.element.ContextMenu;
import com.tridevmc.compound.ui.element.CycleButton;
import com.tridevmc.compound.ui.element.Dropdown;
import com.tridevmc.compound.ui.element.Label;
import com.tridevmc.compound.ui.element.ListView;
import com.tridevmc.compound.ui.element.Modal;
import com.tridevmc.compound.ui.element.NumberInput;
import com.tridevmc.compound.ui.element.Panel;
import com.tridevmc.compound.ui.element.ProgressBar;
import com.tridevmc.compound.ui.element.RadioButtonGroup;
import com.tridevmc.compound.ui.element.Row;
import com.tridevmc.compound.ui.element.ScrollArea;
import com.tridevmc.compound.ui.element.Slider;
import com.tridevmc.compound.ui.element.Stack;
import com.tridevmc.compound.ui.element.Tabs;
import com.tridevmc.compound.ui.element.TextArea;
import com.tridevmc.compound.ui.element.TextInput;
import com.tridevmc.compound.ui.element.Tooltip;
import com.tridevmc.compound.ui.element.ToggleSwitch;
import com.tridevmc.compound.ui.element.TreeView;
import com.tridevmc.compound.ui.layout.Alignment;
import com.tridevmc.compound.ui.scope.ICompositionScope;

import com.tridevmc.compound.ui.screen.ComposedUI;
import com.tridevmc.compound.ui.state.State;
import com.tridevmc.compound.ui.state.StateImpl;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.platform.InputConstants;

import java.util.List;
import java.util.stream.IntStream;

public class UIGallery extends ComposedUI {
    private final Screen previousScreen;
    private final Tabs tabs = new Tabs();
    private final ListView<String> entries = new ListView<>();
    private final ScrollArea controlsScroll = new ScrollArea();
    private final ScrollArea moreScroll = new ScrollArea();
    private final Dropdown<String> access = new Dropdown<>(List.of("Public", "Private", "Friends only"));
    private final TextInput name = new TextInput();
    private final NumberInput quantity = new NumberInput(50);
    private final Slider volume = new Slider(0, 100, 1);
    private final TextArea notes = new TextArea();
    private final State<Boolean> narrowNotes = new StateImpl<>(false);
    private final CycleButton<TextArea.CursorAnimationMode> cursorStyle = new CycleButton<>(
            Component.literal("Cursor"), List.of(TextArea.CursorAnimationMode.values()),
            mode -> Component.literal(mode == TextArea.CursorAnimationMode.INSTANT ? "Blink" : "Ease in/out"));
    private final Checkbox enabled = new Checkbox(Component.literal("Auto-sort items"), true);
    private final CycleButton<String> mode = new CycleButton<>(Component.literal("Transfer"),
            List.of("Stack", "Single", "Half stack"), Component::literal);
    private final Accordion advanced = new Accordion(Component.literal("Advanced settings"));
    private final ContextMenu contextMenu = new ContextMenu();
    private final TreeView<String> storageTree = new TreeView<>();
    private final RadioButtonGroup sorting = new RadioButtonGroup();
    private final ToggleSwitch automatic = new ToggleSwitch(true);
    private final ProgressBar progress = new ProgressBar();
    private final ProgressBar loading = new ProgressBar();
    private final Dropdown<String> destinations = new Dropdown<>(IntStream.rangeClosed(1, 40)
            .mapToObj(index -> "Destination " + index).toList());
    private final Modal dialog = new Modal(Component.literal("Confirm action"),
            Component.literal("Controls behind this dialog should be blocked. "
                    + "This deliberately long message must scroll while the action buttons stay visible. ".repeat(12)));
    private int clicks;
    private final UIRegressionGallery checks = new UIRegressionGallery();

    public UIGallery(Screen previousScreen) {
        this.previousScreen = previousScreen;
        this.tabs.addTab("Controls", this::controls);
        this.tabs.addTab("Lists", this::lists);
        this.tabs.addTab("Text", this::text);
        this.tabs.addTab("More", this::more);
        this.tabs.addTab("Scrollbars", this::scrollbars);
        this.tabs.addTab("Checks", this.checks::compose);
        this.entries.setItems(IntStream.rangeClosed(1, 100)
                .mapToObj(index -> "Storage entry " + index).toList());
        this.access.setSelected("Public");
        this.enabled.setLabelColor(0x404040);
        this.name.setHint(Component.literal("Item name"));
        this.quantity.setRange(0, 100);
        this.volume.setValue(75);
        this.volume.setShowValue(true);
        this.volume.setFormatter(value -> "Volume: " + Math.round(value) + "%");
        this.notes.setHint(Component.literal("Write a note..."));
        this.notes.setMaxLength(4000);
        this.cursorStyle.setOnValueChanged(mode -> {
            Easing easing = mode == TextArea.CursorAnimationMode.INSTANT ? Easing.STEP : Easing.EASE_IN_OUT;
            this.name.setCursorAnimation(600, easing);
            this.notes.setCursorAnimation(600, easing);
        });
        this.dialog.addButton("Cancel", this.dialog::close);
        this.dialog.addButton("Confirm", () -> {
            this.clicks++;
            this.dialog.close();
        });
        this.dialog.close();
        this.contextMenu.addItem("Count action", () -> this.clicks++);
        this.contextMenu.addItem("Reset actions", () -> this.clicks = 0);
        this.contextMenu.addSeparator();
        this.contextMenu.addItem("Cancel", this.contextMenu::hide);
        this.sorting.addOption("By name");
        this.sorting.addOption("By quantity");
        this.sorting.addOption("By item type");
        this.sorting.setSelectedIndex(0);
        this.sorting.setLabelColor(0x404040);
        this.progress.setProgress(65, 100);
        this.progress.setShowPercentage(true);
        this.loading.setIndeterminate(true);
        this.loading.setLabel(Component.literal("Loading..."));
        this.destinations.setSelected("Destination 30");
        var building = this.storageTree.getRoot().addChild("Building materials");
        building.addChild("Stone");
        building.addChild("Wood", wood -> {
            wood.addChild("Oak");
            wood.addChild("Spruce");
        });
        building.setExpanded(true);
        var tools = this.storageTree.getRoot().addChild("Tools");
        tools.addChild("Pickaxe");
        tools.addChild("Axe");
    }

    @Override
    protected void compose(ICompositionScope scope) {
        scope.e(new Stack(), root -> {
            root.layout().fillMax().contentAlignment(Alignment.CENTER);
            root.e(new Panel(), panel -> {
                panel.layout().fixedSize(Math.min(440, this.width - 16),
                        Math.min(330, this.height - 16));
                panel.fillSlot(Panel.CONTENT_SLOT, content -> content.e(new Box(), padding -> {
                    padding.layout().fillMax().padding(10);
                    padding.e(new Column(), column -> {
                        column.layout().fillMax().spacing(6);
                        column.e(new Label(Component.literal("Compound UI Gallery"), 0x404040, false));
                        column.e(new Label(Component.literal("Tab: focus | Enter: activate | Wheel: scroll"),
                                0x404040, false));
                        column.e(this.tabs, tabScope -> tabScope.layout().fillMaxWidth().weight(1));
                        this.button(column, "Done", this::onClose);
                    });
                }));
            });
            root.e(this.contextMenu, menu -> menu.layout().fillMax().layer(100));
            root.e(this.dialog, modal -> modal.layout().fillMax());
        });
    }

    private void controls(ICompositionScope scope) {
        scope.e(this.controlsScroll, scroll -> {
            scroll.layout().fillMax();
            scroll.fillSlot(ScrollArea.CONTENT_SLOT, content -> content.e(new Column(), column -> {
                column.layout().fillMaxWidth().spacing(6);
                column.e(this.name, input -> input.layout().fillMaxWidth().fixedHeight(20));
                column.e(this.quantity, input -> input.layout().fillMaxWidth().fixedHeight(20));
                column.e(this.enabled, checkbox -> checkbox.layout().fillMaxWidth().fixedHeight(20));
                column.e(this.mode, cycle -> cycle.layout().fillMaxWidth().fixedHeight(20));
                column.e(new Label(Component.literal("Shift+click cycles backwards"), 0x404040, false));
                column.e(this.volume, slider -> slider.layout().fillMaxWidth().fixedHeight(20));
                column.e(this.access, dropdown -> dropdown.layout().fillMaxWidth().fixedHeight(20));
                column.e(this.advanced, accordion -> {
                    accordion.layout().fillMaxWidth();
                    accordion.fillSlot(Accordion.CONTENT_SLOT, body -> {
                        body.e(new Label(Component.literal("Custom content inside the accordion"), 0x404040, false));
                        this.button(body, "Open confirmation", this.dialog::show);
                    });
                });
                this.button(column, "Count click", () -> this.clicks++);
                column.e(new Label(() -> Component.literal("Actions: " + this.clicks),
                        () -> 0x404040, () -> false));
                this.button(column, "Open confirmation", this.dialog::show);
                column.e(new Tooltip(Component.literal("A vanilla tooltip, with wrapping for longer explanations.")), tooltip -> {
                    tooltip.layout().fillMaxWidth().fixedHeight(20);
                    tooltip.fillSlot(Tooltip.CONTENT_SLOT,
                            body -> this.button(body, "Hover for help", () -> this.clicks++));
                });
                column.e(new Box(), target -> {
                    target.layout().fillMaxWidth().fixedHeight(20);
                    target.e(new Label(Component.literal("Right-click here for actions"), 0x404040, false));
                    target.onClick(event -> {
                        if (event.button() != 1) return false;
                        this.contextMenu.show(event.x(), event.y());
                        return true;
                    });
                });
                column.e(new Label(Component.literal("40 destinations: open near the bottom edge"),
                        0x404040, false));
                column.e(this.destinations, dropdown -> dropdown.layout().fillMaxWidth().fixedHeight(20));
            }));
        });
    }

    private void lists(ICompositionScope scope) {
        scope.e(this.entries, list -> list.layout().fillMax());
    }

    private void text(ICompositionScope scope) {
        scope.e(new Column(), column -> {
            column.layout().fillMax().spacing(6);
            column.onKeyPress(event -> {
                if (event.keyCode() != InputConstants.KEY_F6) return false;
                this.narrowNotes.set(!this.narrowNotes.get());
                return true;
            });
            column.e(this.cursorStyle, control -> {
                control.layout().fillMaxWidth().fixedHeight(20);
                control.fillSlot(Button.CONTENT_SLOT, content -> content.e(new Label(
                        () -> Component.literal(this.cursorStyle.getValue() == TextArea.CursorAnimationMode.INSTANT
                                ? "Caret animation: Blink" : "Caret animation: Ease in/out"), () -> 0xFFFFFF)));
            });
            column.e(new Label(Component.literal("F6: resize notes | Type, select, paste, and scroll."),
                    0x404040, false));
            column.e(this.notes, area -> {
                area.layout().fillMaxWidth().weight(1).deferred(layout -> {
                    layout.bind(this.narrowNotes);
                    layout.layout().maxWidth(this.narrowNotes.get() ? 190 : 440);
                });
            });
        });
    }

    private void more(ICompositionScope scope) {
        scope.e(this.moreScroll, scroll -> {
            scroll.layout().fillMax();
            scroll.fillSlot(ScrollArea.CONTENT_SLOT, content -> content.e(new Column(), column -> {
                column.layout().fillMaxWidth().spacing(6);
                column.e(new Label(Component.literal("Expand folders and select an item"), 0x404040, false));
                column.e(this.storageTree, tree -> tree.layout().fillMaxWidth().fixedHeight(80));
                column.e(this.sorting, radio -> radio.layout().fillMaxWidth());
                column.e(new Label(Component.literal("Automatic transfer"), 0x404040, false));
                column.e(this.automatic, toggle -> toggle.layout().fixedSize(50, 20));
                column.e(this.progress, bar -> bar.layout().fillMaxWidth().fixedHeight(16));
                this.button(column, "Advance progress", () -> this.progress.setProgress(
                        this.progress.getProgress() >= 100 ? 0 : this.progress.getProgress() + 5));
                column.e(this.loading, bar -> bar.layout().fillMaxWidth().fixedHeight(16));
                column.e(new Button(false), button -> {
                    button.layout().fillMaxWidth().fixedHeight(20);
                    button.fillSlot(Button.CONTENT_SLOT, body -> body.e(new Label(
                            Component.literal("Unavailable action"), 0xA0A0A0)));
                    button.getElement().addPressListener((x, y) -> this.clicks++);
                });
            }));
        });
    }

    private void scrollbars(ICompositionScope scope) {
        scope.e(new Row(), row -> {
            row.layout().fillMax().spacing(12);
            this.scrollbarSample(row, "Selection list", ScrollArea.ScrollbarStyle.LIST);
            this.scrollbarSample(row, "Grippy", ScrollArea.ScrollbarStyle.GRIPPY);
        });
    }

    private void scrollbarSample(ICompositionScope scope, String title, ScrollArea.ScrollbarStyle style) {
        scope.e(new Column(), column -> {
            column.layout().weight(1).fillMaxHeight().spacing(6);
            column.e(new Label(Component.literal(title), 0x404040, false));
            column.e(new ScrollArea().scrollbarStyle(style), scroll -> {
                scroll.layout().fillMaxWidth().weight(1);
                scroll.fillSlot(ScrollArea.CONTENT_SLOT, content -> content.e(new Column(), entries -> {
                    entries.layout().fillMaxWidth().spacing(4);
                    for (int index = 1; index <= 40; index++) {
                        entries.e(new Label(Component.literal("Entry " + index), 0x404040, false));
                    }
                }));
            });
            column.e(new Label(Component.literal("No overflow"), 0x404040, false));
            column.e(new ScrollArea().scrollbarStyle(style), scroll -> {
                scroll.layout().fillMaxWidth().fixedHeight(24);
                scroll.fillSlot(ScrollArea.CONTENT_SLOT,
                        content -> content.e(new Label(Component.literal("All items fit"), 0x404040, false)));
            });
            column.e(new ScrollArea(ScrollArea.Direction.HORIZONTAL).scrollbarStyle(style), scroll -> {
                scroll.layout().fillMaxWidth().fixedHeight(34);
                scroll.fillSlot(ScrollArea.CONTENT_SLOT, content -> content.e(new Label(
                        Component.literal("Horizontal content: beginning, middle, and end."), 0x404040, false)));
            });
        });
    }

    private void button(ICompositionScope scope, String label, Runnable action) {
        scope.e(new Button(), button -> {
            button.layout().fillMaxWidth().fixedHeight(20);
            button.fillSlot(Button.CONTENT_SLOT, content -> content.e(new Label(Component.literal(label))));
            button.getElement().addPressListener((x, y) -> action.run());
        });
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.previousScreen);
    }
}
