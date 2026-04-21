package com.tridevmc.molecule.ui;

import com.tridevmc.compound.ui.element.*;
import com.tridevmc.compound.ui.layout.Alignment;
import com.tridevmc.compound.ui.scope.RootScope;
import com.tridevmc.compound.ui.screen.ComposedUIContainer;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Composed version of the crate UI using the declarative composition system.
 */
public class CrateUI extends ComposedUIContainer<CrateMenu> {

    public CrateUI(CrateMenu menu, Inventory inventory, Component name) {
        super(menu, inventory, name);
    }

    @Override
    protected void compose(RootScope scope) {
        // Center everything on screen using Stack that fills the screen
        scope.e(new Stack(), stack -> {
            // Make stack fill the entire screen and center its children
            stack.layout()
                    .fillMax()
                    .contentAlignment(Alignment.CENTER);

            // Row to hold main UI and scroll area side by side
            stack.e(new Row(), row -> {
                row.layout().spacing(8); // 8px gap between main UI and scroll area

                // Background box with default inventory sprite
                row.e(new Panel(), box -> {
                    // ElementBox just specifies its size
                    box.layout().fixedSize(178, 190);

                    // Fill content slot with Box for padding
                    box.fillSlot(Panel.CONTENT_SLOT, content -> {
                        content.e(new Box(), paddedContent -> {
                            // Set padding on the box
                            paddedContent.layout().padding(8);

                            // Main content column
                            paddedContent.e(new Column(), column -> {
                                // Set spacing on the column
                                column.layout().spacing(4);
                                // "Crate" label - no shadow for Minecraft inventory style
                                column.e(new Label(
                                        Component.literal("Crate"),
                                        0x404040,
                                        false  // No shadow for inventory labels
                                ));

                                // Crate slots grid (9x3 = 27 slots)
                                column.e(new Grid(9, 0, 0), crateGrid -> {
                                    for (int i = 0; i < 27; i++) {
                                        crateGrid.e(new InventorySlot(this.getMenu(), i));
                                    }
                                });

                                // "Inventory" label - no shadow for Minecraft inventory style
                                column.e(new Label(
                                        Component.literal("Inventory"),
                                        0x404040,
                                        false  // No shadow for inventory labels
                                ));

                                // Player inventory grid (9x3 = 27 slots)
                                column.e(new Grid(9, 0, 0), playerGrid -> {
                                    for (int i = 0; i < 27; i++) {
                                        playerGrid.e(new InventorySlot(this.getMenu(), 27 + i));
                                    }
                                });

                                // Spacer before hotbar
                                column.e(new Spacer(0, 4));

                                // Hotbar grid (9x1 = 9 slots)
                                column.e(new Grid(9, 0, 0), hotbarGrid -> {
                                    for (int i = 0; i < 9; i++) {
                                        hotbarGrid.e(new InventorySlot(this.getMenu(), 54 + i));
                                    }
                                });
                            });
                        });
                    });
                });

                // Scrollable list of text inputs on the right side
                row.e(new Panel(), scrollBox -> {
                    scrollBox.layout().fixedSize(120, 190); // Match height of main UI

                    scrollBox.fillSlot(Panel.CONTENT_SLOT, content -> {
                        content.e(new Box(), paddedBox -> {
                            paddedBox.layout().padding(4);

                            // ScrollArea containing the list of text inputs
                            paddedBox.e(new ScrollArea(), scrollArea -> {
                                scrollArea.getElement().scrollSpeed(10);

                                // Fill scroll area's content slot with column of text inputs
                                scrollArea.fillSlot(ScrollArea.CONTENT_SLOT, scrollContent -> {
                                    scrollContent.e(new Column(), column -> {
                                        column.layout().spacing(2);

                                        // Create alternating buttons and text inputs
                                        for (int i = 1; i <= 50; i++) {
                                            final int index = i;
                                            if (i % 2 == 1) {
                                                // Odd indices: Text inputs
                                                column.e(new TextInput(), input -> {
                                                    input.layout()
                                                            .fillMaxWidth()
                                                            .fixedHeight(20);

                                                    input.getElement().setHint(Component.literal("Input " + index));
                                                    input.getElement().setResponder(text -> {
                                                        System.out.println("Input " + index + " changed: " + text);
                                                    });
                                                });
                                            } else {
                                                // Even indices: Buttons
                                                column.e(new Button(), button -> {
                                                    button.layout()
                                                            .fillMaxWidth()
                                                            .fixedHeight(20);

                                                    button.fillSlot(Button.CONTENT_SLOT, buttonContent -> {
                                                        buttonContent.e(new Label(Component.literal("Button " + index)));
                                                    });

                                                    button.getElement().addPressListener((x, y) -> {
                                                        System.out.println("Button " + index + " pressed at " + x + ", " + y);
                                                    });
                                                });
                                            }
                                        }
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });
    }
}
