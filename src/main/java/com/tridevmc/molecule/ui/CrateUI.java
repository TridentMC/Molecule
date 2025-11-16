package com.tridevmc.molecule.ui;

import com.tridevmc.compound.ui.compose.element.*;
import com.tridevmc.compound.ui.compose.layout.Alignment;
import com.tridevmc.compound.ui.compose.layout.LayoutProperties;
import com.tridevmc.compound.ui.compose.screen.ComposedUIContainer;
import com.tridevmc.compound.ui.compose.scope.RootScope;
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

            // Background box with default inventory sprite
            stack.e(new ElementBox(), box -> {
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

            // Test button (positioned relative to background)
            /*stack.e(new Button(), button -> {
                // Button specifies its size and margin
                button.layout()
                        .fixedSize(50, 50)
                        .margin(0, 0, 0, 128); // Left offset from center

                button.getElement().addPressListener((x, y) -> {
                    System.out.println("Button clicked at " + x + ", " + y);
                });
            });*/
        });
    }
}
