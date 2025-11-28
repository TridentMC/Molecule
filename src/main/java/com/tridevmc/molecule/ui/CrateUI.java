package com.tridevmc.molecule.ui;

import com.tridevmc.compound.ui.compose.animation.AnimatedState;
import com.tridevmc.compound.ui.compose.animation.Easing;
import com.tridevmc.compound.ui.compose.element.*;
import com.tridevmc.compound.ui.compose.layout.Alignment;
import com.tridevmc.compound.ui.compose.scope.ICompositionScope;
import com.tridevmc.compound.ui.compose.scope.RootScope;
import com.tridevmc.compound.ui.compose.screen.ComposedUIContainer;
import com.tridevmc.compound.ui.sprite.IScreenSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Comprehensive demo UI for the compose framework.
 * Demonstrates proper use of the measurement/placement system.
 */
public class CrateUI extends ComposedUIContainer<CrateMenu> {

    public CrateUI(CrateMenu menu, Inventory inventory, Component name) {
        super(menu);
    }

    @Override
    protected void compose(RootScope scope) {
        // Center everything on screen
        scope.e(new Stack(), stack -> {
            stack.layout().fillMax().contentAlignment(Alignment.CENTER);

            // Main row with inventory and demo panel
            stack.e(new Row(), mainRow -> {
                mainRow.layout().spacing(8);

                // Left: Inventory panel (ElementBox requires fixed size for sprite)
                this.composeInventoryPanel(mainRow);

                // Right: Demo features panel (ElementBox requires fixed size for sprite)
                this.composeDemoPanel(mainRow);
            });
        });
    }

    private void composeInventoryPanel(ICompositionScope scope) {
        // ElementBox needs fixed size because it renders a fixed-size sprite
        scope.e(new ElementBox(), box -> {
            box.layout().fixedSize(178, 190);

            box.fillSlot(ElementBox.CONTENT_SLOT, content -> {
                // Box sizes itself to fit child + padding
                content.e(new Box(), paddedContent -> {
                    paddedContent.layout().padding(8);

                    // Column sizes itself to fit all children + spacing
                    paddedContent.e(new Column(), column -> {
                        column.layout().spacing(4);

                        column.e(new ElementLabel(
                                Component.literal("Crate"),
                                0x404040,
                                false
                        ));

                        // Grid has intrinsic size based on slots
                        column.e(new Grid(9, 0, 0), crateGrid -> {
                            for (int i = 0; i < 27; i++) {
                                crateGrid.e(new ComposedSlot(this.getMenu(), i));
                            }
                        });

                        column.e(new ElementLabel(
                                Component.literal("Inventory"),
                                0x404040,
                                false
                        ));

                        column.e(new Grid(9, 0, 0), playerGrid -> {
                            for (int i = 0; i < 27; i++) {
                                playerGrid.e(new ComposedSlot(this.getMenu(), 27 + i));
                            }
                        });

                        // Spacer has intrinsic size
                        column.e(new ElementSpacer(0, 4));

                        column.e(new Grid(9, 0, 0), hotbarGrid -> {
                            for (int i = 0; i < 9; i++) {
                                hotbarGrid.e(new ComposedSlot(this.getMenu(), 54 + i));
                            }
                        });
                    });
                });
            });
        });
    }

    private void composeDemoPanel(ICompositionScope scope) {
        // ElementBox needs fixed size for sprite rendering
        scope.e(new ElementBox(), box -> {
            box.layout().fixedSize(180, 190);

            box.fillSlot(ElementBox.CONTENT_SLOT, content -> {
                // Box sizes to child + padding
                content.e(new Box(), paddedBox -> {
                    paddedBox.layout().padding(4);

                    // ScrollArea fills available space from parent
                    paddedBox.e(new ScrollArea(), scrollArea -> {
                        scrollArea.getElement().scrollSpeed(10);

                        scrollArea.fillSlot(ScrollArea.CONTENT_SLOT, scrollContent -> {
                            // Column sizes itself based on children
                            scrollContent.e(new Column(), column -> {
                                column.layout().spacing(8);

                                column.e(new ElementLabel(
                                        Component.literal("Framework Demo"),
                                        0xFFFFFF,
                                        true
                                ));

                                this.composeButtonsDemo(column);
                                this.composeLayeringDemo(column);
                                this.composeGradientDemo(column);
                                this.composeSpritesDemo(column);
                                this.composeItemsDemo(column);
                                this.composeAlignmentDemo(column);
                                this.composeAnimationDemo(column);
                            });
                        });
                    });
                });
            });
        });
    }

    private void composeButtonsDemo(ICompositionScope scope) {
        scope.e(new ElementLabel(
                Component.literal("Buttons:"),
                0xFFFF00,
                false
        ));

        // Buttons size themselves based on content
        for (int i = 1; i <= 5; i++) {
            final int buttonNum = i;
            scope.e(new Button(), button -> {
                // Button expands width, has minimum height from content
                button.layout().fillMaxWidth().fixedHeight(20);

                button.getElement().addPressListener((x, y) -> {
                    System.out.println("Clicked button " + buttonNum);
                });

                button.fillSlot(Button.CONTENT_SLOT, buttonContent -> {
                    buttonContent.e(new ElementLabel(
                            Component.literal("Button " + buttonNum),
                            0xFFFFFF,
                            true
                    ));
                });
            });
        }
    }

    private void composeLayeringDemo(ICompositionScope scope) {
        scope.e(new ElementLabel(
                Component.literal("Layering:"),
                0xFFFF00,
                false
        ));

        // Box sizes to child
        scope.e(new Box(), box -> {
            // Stack sizes based on children, so we need something to set size
            // Use padding to create space
            box.layout().padding(5);

            box.e(new Stack(), stack -> {
                // Red background (sizes to 80x30)
                stack.e(new Box(), redBox -> {
                    redBox.layout().padding(40, 15, 40, 15);
                    redBox.e(new ElementRect(0xFFFF0000));
                });

                // Green offset (padding creates the offset)
                stack.e(new Box(), greenBox -> {
                    greenBox.layout().padding(10, 5, 0, 0);
                    greenBox.e(new Box(), greenInner -> {
                        greenInner.layout().padding(30, 10, 50, 20);
                        greenInner.e(new ElementRect(0xFF00FF00));
                    });
                });

                // Blue offset
                stack.e(new Box(), blueBox -> {
                    blueBox.layout().padding(20, 10, 0, 0);
                    blueBox.e(new Box(), blueInner -> {
                        blueInner.layout().padding(20, 5, 60, 25);
                        blueInner.e(new ElementRect(0xFF0000FF));
                    });
                });
            });
        });
    }

    private void composeGradientDemo(ICompositionScope scope) {
        scope.e(new ElementLabel(
                Component.literal("Gradients:"),
                0xFFFF00,
                false
        ));

        // Box sizes to child + padding
        scope.e(new Box(), box -> {
            box.layout().padding(80, 10, 10, 10);
            box.e(new ElementGradientRect(0xFFFF0000, 0xFF0000FF));
        });
    }

    private void composeSpritesDemo(ICompositionScope scope) {
        scope.e(new ElementLabel(
                Component.literal("Sprites:"),
                0xFFFF00,
                false
        ));

        // Row sizes based on children + spacing
        scope.e(new Row(), row -> {
            row.layout().spacing(4);

            // Sprites have intrinsic size, but we can set explicit size
            row.e(new Box(), spriteBox1 -> {
                spriteBox1.layout().padding(20, 10, 20, 10);
                spriteBox1.e(new ElementSprite(
                        IScreenSprite.of(ResourceLocation.withDefaultNamespace("widget/button"))
                ));
            });

            row.e(new Box(), spriteBox2 -> {
                spriteBox2.layout().padding(20, 10, 20, 10);
                spriteBox2.e(new ElementSprite(
                        IScreenSprite.of(ResourceLocation.withDefaultNamespace("widget/button_highlighted"))
                ));
            });

            row.e(new Box(), spriteBox3 -> {
                spriteBox3.layout().padding(20, 10, 20, 10);
                spriteBox3.e(new ElementSprite(
                        IScreenSprite.of(ResourceLocation.withDefaultNamespace("widget/button_disabled"))
                ));
            });
        });
    }

    private void composeItemsDemo(ICompositionScope scope) {
        scope.e(new ElementLabel(
                Component.literal("Items:"),
                0xFFFF00,
                false
        ));

        // Row sizes based on items (16x16 each) + spacing
        scope.e(new Row(), row -> {
            row.layout().spacing(4);

            row.e(new ElementItem(new ItemStack(Items.DIAMOND_SWORD)));
            row.e(new ElementItem(new ItemStack(Items.GOLDEN_APPLE)));
            row.e(new ElementItem(new ItemStack(Items.ENDER_PEARL)));
            row.e(new ElementItem(new ItemStack(Items.EMERALD, 64)));
        });
    }

    private void composeAlignmentDemo(ICompositionScope scope) {
        scope.e(new ElementLabel(
                Component.literal("Alignment (TL/C/BR):"),
                0xFFFF00,
                false
        ));

        // Row sizes based on children
        scope.e(new Row(), row -> {
            row.layout().spacing(4);

            this.addAlignmentExample(row, Alignment.TOP_LEFT, 0xFFFF0000);
            this.addAlignmentExample(row, Alignment.CENTER, 0xFF00FF00);
            this.addAlignmentExample(row, Alignment.BOTTOM_RIGHT, 0xFF0000FF);
        });
    }

    private void addAlignmentExample(ICompositionScope scope, Alignment alignment, int color) {
        // Box with padding creates fixed area
        scope.e(new Box(), container -> {
            container.layout().padding(25, 20, 25, 20);

            // Stack fills the padded area
            container.e(new Stack(), stack -> {
                // Background rect
                stack.e(new Box(), bgBox -> {
                    bgBox.layout().padding(25, 20, 25, 20);
                    bgBox.e(new ElementRect(0xFF333333));
                });

                // Aligned colored box
                stack.e(new Box(), alignedBox -> {
                    alignedBox.layout().contentAlignment(alignment);

                    alignedBox.e(new Box(), colorBox -> {
                        colorBox.layout().padding(5, 5, 5, 5);
                        colorBox.e(new ElementRect(color));
                    });
                });
            });
        });
    }

    private void composeAnimationDemo(ICompositionScope scope) {
        scope.e(new ElementLabel(
                Component.literal("Animation:"),
                0xFFFF00,
                false
        ));

        AnimatedState<Float> alpha = scope.animateFloat(1.0f, 1000, Easing.EASE_IN_OUT_CUBIC);
        scope.bind(alpha);

        scope.e(new Button(), button -> {
            button.layout().fillMaxWidth().fixedHeight(30);

            button.getElement().addPressListener((x, y) -> {
                float current = alpha.get();
                alpha.set(current < 0.5f ? 1.0f : 0.3f);
            });

            button.fillSlot(Button.CONTENT_SLOT, buttonContent -> {
                buttonContent.e(new Stack(), stack -> {
                    // Animated background
                    int baseColor = 0x00FF00;
                    int alphaValue = (int) (alpha.get() * 255);
                    int animatedColor = (alphaValue << 24) | baseColor;
                    stack.e(new ElementRect(animatedColor));

                    // Label
                    stack.e(new Box(), labelBox -> {
                        labelBox.layout().contentAlignment(Alignment.CENTER);
                        labelBox.e(new ElementLabel(
                                Component.literal("Click to Animate!"),
                                0xFFFFFF,
                                true
                        ));
                    });
                });
            });
        });
    }
}
