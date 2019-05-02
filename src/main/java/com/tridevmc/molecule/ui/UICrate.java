package com.tridevmc.molecule.ui;

import com.tridevmc.compound.ui.Rect2D;
import com.tridevmc.compound.ui.container.CompoundContainer;
import com.tridevmc.compound.ui.container.CompoundUIContainer;
import com.tridevmc.compound.ui.element.ElementBox;
import com.tridevmc.compound.ui.layout.LayoutCentered;
import com.tridevmc.compound.ui.layout.LayoutRelative;

public class UICrate extends CompoundUIContainer {
    public UICrate(CompoundContainer container) {
        super(container);
    }

    @Override
    public void initElements() {
        ElementBox bg = new ElementBox(new Rect2D(0, 0, 178, 180), new LayoutCentered(true, true));
        this.addElement(bg);

        for (int i = 0; i < this.inventorySlots.inventorySlots.size(); i++) {
            this.addSlotElement(new LayoutRelative(bg), i);
        }
    }
}
