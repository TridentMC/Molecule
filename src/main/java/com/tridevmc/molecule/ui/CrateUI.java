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
        super(menu);
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
                row.e(new ElementBox(), box -> {
                    // ElementBox just specifies its size
                    box.layout().fixedSize(178, 190);

                    // Fill content slot with Box for padding
                    box.fillSlot(ElementBox.CONTENT_SLOT, content -> {
                        content.e(new Box(), paddedContent -> {
                            // Set padding on the box
                            paddedContent.layout().padding(8);

                            // Main content column
                            paddedContent.e(new Column(), column -> {
                                // Set spacing on the column
                                column.layout().spacing(4);
                                // "Crate" label - no shadow for Minecraft inventory style
                                column.e(new ElementLabel(
                                        Component.literal("Crate"),
                                        0x404040,
                                        false  // No shadow for inventory labels
                                ));

                                // Crate slots grid (9x3 = 27 slots)
                                column.e(new Grid(9, 0, 0), crateGrid -> {
                                    for (int i = 0; i < 27; i++) {
                                        crateGrid.e(new ComposedSlot(this.getMenu(), i));
                                    }
                                });

                                // "Inventory" label - no shadow for Minecraft inventory style
                                column.e(new ElementLabel(
                                        Component.literal("Inventory"),
                                        0x404040,
                                        false  // No shadow for inventory labels
                                ));

                                // Player inventory grid (9x3 = 27 slots)
                                column.e(new Grid(9, 0, 0), playerGrid -> {
                                    for (int i = 0; i < 27; i++) {
                                        playerGrid.e(new ComposedSlot(this.getMenu(), 27 + i));
                                    }
                                });

                                // Spacer before hotbar
                                column.e(new ElementSpacer(0, 4));

                                // Hotbar grid (9x1 = 9 slots)
                                column.e(new Grid(9, 0, 0), hotbarGrid -> {
                                    for (int i = 0; i < 9; i++) {
                                        hotbarGrid.e(new ComposedSlot(this.getMenu(), 54 + i));
                                    }
                                });
                            });
                        });
                    });
                });

                // Scrollable list of buttons on the right side
                row.e(new ElementBox(), scrollBox -> {
                    scrollBox.layout().fixedSize(120, 190); // Match height of main UI

                    scrollBox.fillSlot(ElementBox.CONTENT_SLOT, content -> {
                        content.e(new Box(), paddedBox -> {
                            paddedBox.layout().padding(4);

                            // ScrollArea containing the list of buttons
                            paddedBox.e(new ScrollArea(), scrollArea -> {
                                scrollArea.getElement().scrollSpeed(10);

                                // Fill scroll area's content slot with column of buttons
                                scrollArea.fillSlot(ScrollArea.CONTENT_SLOT, scrollContent -> {
                                    scrollContent.e(new Column(), column -> {
                                        column.layout().spacing(2);

                                        // Create 50 items, alternating between normal buttons and custom themed buttons
                                        for (int i = 1; i <= 50; i++) {
                                            final int index = i;

                                            if (i % 2 == 1) {
                                                // Odd indices: custom themed buttons
                                                column.e(new Button(), button -> {
                                                    button.layout()
                                                            .fillMaxWidth()
                                                            .fixedHeight(20);

                                                    button.getElement().addPressListener((x, y) -> {
                                                        System.out.println("Clicked themed button " + index);
                                                    });

                                                    // Add label to the button via slot
                                                    button.fillSlot(Button.CONTENT_SLOT, buttonContent -> {
                                                        buttonContent.e(new ElementLabel(
                                                                Component.literal("Themed Button " + index),
                                                                0xFFFFFF,
                                                                true
                                                        ));
                                                    });
                                                });
                                            } else {
                                                // Even indices: buttons
                                                column.e(new Button(), button -> {
                                                    button.layout()
                                                            .fillMaxWidth()
                                                            .fixedHeight(20);

                                                    button.getElement().addPressListener((x, y) -> {
                                                        System.out.println("Clicked button " + index);
                                                    });

                                                    // Add label to the button via slot
                                                    button.fillSlot(Button.CONTENT_SLOT, buttonContent -> {
                                                        buttonContent.e(new ElementLabel(
                                                                Component.literal("Button " + index),
                                                                0xFFFFFF,
                                                                true
                                                        ));
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

            // DVD Logo overlay - bounces around the entire screen
            // ElementDVDLogo missing
            // The logo will position itself absolutely within the container
            // Don't give it any layout bounds since it positions itself
        });
    }
}
