package com.tridevmc.molecule.ui;

import com.tridevmc.compound.ui.Rect2F;
import com.tridevmc.compound.ui.container.CompoundUIContainer;
import com.tridevmc.compound.ui.element.ElementBox;
import com.tridevmc.compound.ui.element.ElementLabel;
import com.tridevmc.compound.ui.element.button.ElementButton;
import com.tridevmc.compound.ui.layout.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CrateUI extends CompoundUIContainer<CrateMenu> {

    public CrateUI(CrateMenu container, Inventory inventory, Component name) {
        super(container);
    }

    @Override
    public void initElements() {
        // Background box - made taller to accommodate labels
        ElementBox bg = new ElementBox(new Rect2F(0, 0, 178, 190), new LayoutCentered(true, true));
        this.addElement(bg);

        // "Crate" label at the top (with shadow for visibility)
        ElementLabel crateLabel = new ElementLabel(new Rect2F(8, 6, 162, 10), new LayoutRelative(bg),
            Minecraft.getInstance().font, false, false, false);
        crateLabel.setText(Component.literal("Crate"));
        this.addElement(crateLabel);

        // Crate grid - moved down to make room for label
        LayoutGrid crateGrid = new LayoutGrid(new Rect2F(8, 18, 18 * 9, 18 * 3));
        ILayout crateLayout = new LayoutMulti(crateGrid, new LayoutRelative(bg));

        // "Inventory" label between crate and player inventory (with shadow for visibility)
        ElementLabel inventoryLabel = new ElementLabel(new Rect2F(8, 80, 162, 10), new LayoutRelative(bg),
            Minecraft.getInstance().font, false, false, false);
        inventoryLabel.setText(Component.literal("Inventory"));
        this.addElement(inventoryLabel);

        // Player inventory grid - adjusted for new label positions
        LayoutGrid playerGrid = new LayoutGrid(new Rect2F(8, 92, 18 * 9, 18 * 3));
        ILayout playerLayout = new LayoutMulti(playerGrid, new LayoutRelative(bg));

        // Hotbar grid - adjusted for new label positions
        LayoutGrid hotbarGrid = new LayoutGrid(new Rect2F(8, 154, 18 * 9, 18));
        ILayout hotbarLayout = new LayoutMulti(hotbarGrid, new LayoutRelative(bg));

        for (int i = 0; i < this.getMenu().slots.size(); i++) {
            if (i < 27) {
                crateGrid.registerElement(this.addSlotElement(crateLayout, i));
            } else if (i < 54) {
                playerGrid.registerElement(this.addSlotElement(playerLayout, i));
            } else {
                hotbarGrid.registerElement(this.addSlotElement(hotbarLayout, i));
            }
        }

        ElementButton button = new ElementButton(new Rect2F(-50, 50, 50, 50), new LayoutRelative(bg));
        this.addElement(button);
    }

}
