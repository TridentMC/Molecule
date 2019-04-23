package com.tridevmc.molecule.ui;

import com.tridevmc.compound.ui.CompoundUI;
import com.tridevmc.compound.ui.Rect2D;
import com.tridevmc.compound.ui.element.ElementBox;
import com.tridevmc.compound.ui.element.ElementContainerGrid;
import com.tridevmc.compound.ui.layout.LayoutCentered;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.function.Consumer;

public class MoleculeUI extends CompoundUI {
    @Override
    public void initElements() {
        ElementContainerGrid grid = new ElementContainerGrid(new Rect2D(0, 0, this.width, this.height), true, 1, 1);
        grid.setLayout(new LayoutCentered(true, true));
        for (int i = 0; i < 10; i++) {
            grid.addElement(new ElementBox(new Rect2D(0, 0, 10 + i, 10 + i)));
        }
        this.addElement(grid);
        // Have to use this because we don't actually register a gui with Forge.
        // In a real implementation we would add a listener to the UI itself.
        MinecraftForge.EVENT_BUS.addListener((Consumer<GuiScreenEvent.KeyboardCharTypedEvent>) e -> {
            if (e.getCodePoint() == '+') {
                grid.getElements().forEach(element -> {
                    element.getDimensions().setWidth(element.getDimensions().getWidth() + 1);
                    element.getDimensions().setHeight(element.getDimensions().getHeight() + 1);
                });
            } else if (e.getCodePoint() == '-') {
                grid.getElements().forEach(element -> {
                    element.getDimensions().setWidth(element.getDimensions().getWidth() - 1);
                    element.getDimensions().setHeight(element.getDimensions().getHeight() - 1);
                });
            }
        });
    }
}
