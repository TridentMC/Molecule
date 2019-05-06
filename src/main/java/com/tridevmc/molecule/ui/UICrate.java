package com.tridevmc.molecule.ui;

import com.tridevmc.compound.ui.Rect2D;
import com.tridevmc.compound.ui.container.CompoundContainer;
import com.tridevmc.compound.ui.container.CompoundUIContainer;
import com.tridevmc.compound.ui.element.ElementBox;
import com.tridevmc.compound.ui.element.button.ElementButton;
import com.tridevmc.compound.ui.layout.*;

public class UICrate extends CompoundUIContainer {
    public UICrate(CompoundContainer container) {
        super(container);
    }

    @Override
    public void initElements() {
        ElementBox bg = new ElementBox(new Rect2D(0, 0, 178, 160), new LayoutCentered(true, true));
        this.addElement(bg);
        LayoutGrid crateGrid = new LayoutGrid(new Rect2D(8, 8, 19 * 9, 18 * 3));
        ILayout crateLayout = new LayoutMulti(crateGrid, new LayoutRelative(bg));

        LayoutGrid playerGrid = new LayoutGrid(new Rect2D(8, 76, 19 * 9, 18 * 3));
        ILayout playerLayout = new LayoutMulti(playerGrid, new LayoutRelative(bg));

        LayoutGrid hotbarGrid = new LayoutGrid(new Rect2D(8, 134, 19 * 9, 18));
        ILayout hotbarLayout = new LayoutMulti(hotbarGrid, new LayoutRelative(bg));

        for (int i = 0; i < this.inventorySlots.inventorySlots.size(); i++) {
            if (i < 27) {
                crateGrid.registerElement(this.addSlotElement(crateLayout, i));
            } else if (i < 54) {
                playerGrid.registerElement(this.addSlotElement(playerLayout, i));
            } else {
                hotbarGrid.registerElement(this.addSlotElement(hotbarLayout, i));
            }
        }

        ElementButton button = new ElementButton(new Rect2D(0, 0, 50, 50), new LayoutRelative(bg));
        this.addElement(button);
    }
}
