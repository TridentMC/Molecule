package com.tridevmc.molecule.ui;

import com.tridevmc.compound.test.MinecraftMockExtension;
import com.tridevmc.compound.ui.element.ContextMenu;
import com.tridevmc.compound.ui.element.Label;
import com.tridevmc.compound.ui.element.ScrollArea;
import com.tridevmc.compound.ui.element.Tabs;
import com.tridevmc.compound.ui.element.TextArea;
import com.tridevmc.compound.ui.element.TextInput;
import com.tridevmc.compound.ui.layout.Constraints;
import com.tridevmc.compound.ui.layout.Position;
import com.tridevmc.compound.ui.screen.ComposedUI;
import com.tridevmc.compound.ui.tree.ITreeNode;
import com.tridevmc.compound.ui.tree.UITree;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MinecraftMockExtension.class)
class UIGalleryInputTest {
    private UIGallery screen;
    private UITree tree;

    @BeforeEach
    void setUp() throws ReflectiveOperationException {
        when(Minecraft.getInstance().font.getSplitter())
                .thenReturn(new StringSplitter((codePoint, style) -> 6F));
        clearInvocations(Minecraft.getInstance().textInputManager());
        when(Minecraft.getInstance().font.plainSubstrByWidth(anyString(), anyInt()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        var keyboardField = Minecraft.class.getDeclaredField("keyboardHandler");
        keyboardField.setAccessible(true);
        keyboardField.set(Minecraft.getInstance(), mock(net.minecraft.client.KeyboardHandler.class));
        this.screen = new UIGallery(null);
        this.screen.width = 800;
        this.screen.height = 600;
        var initMethod = ComposedUI.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        initMethod.invoke(this.screen);
        var treeField = ComposedUI.class.getDeclaredField("tree");
        treeField.setAccessible(true);
        this.tree = (UITree) treeField.get(this.screen);
        this.layout();
    }

    @Test
    void rightClickOpensActions() {
        var target = this.actionTarget();
        assertTrue(this.screen.mouseClicked(this.click(target, InputConstants.MOUSE_BUTTON_RIGHT), false));
        assertTrue(this.contextMenu().isVisible());
    }

    @Test
    void leftClickDoesNotOpenActions() {
        var target = this.actionTarget();
        this.screen.mouseClicked(this.click(target, InputConstants.MOUSE_BUTTON_LEFT), false);
        assertFalse(this.contextMenu().isVisible());
    }

    @Test
    void ordinaryTypingReachesTextInput() {
        var node = this.find(nodeToFind -> nodeToFind.getElement() instanceof TextInput);
        assertTrue(this.screen.mouseClicked(this.click(node, InputConstants.MOUSE_BUTTON_LEFT), false));
        verify(Minecraft.getInstance().textInputManager())
                .onTextInputFocusChange(node.getElement(), true);
        this.layout();
        assertTrue(this.screen.charTyped(new CharacterEvent('a')));
        assertTrue(this.screen.charTyped(new CharacterEvent('Z')));
        var input = (TextInput) node.getElement();
        assertEquals("aZ", input.getValue());
        input.setEditable(false);
        verify(Minecraft.getInstance().textInputManager())
                .onTextInputFocusChange(input, false);
        input.setEditable(true);
        this.screen.mouseClicked(new MouseButtonEvent(0, 0,
                new MouseButtonInfo(InputConstants.MOUSE_BUTTON_LEFT, 0)), false);
        verify(Minecraft.getInstance().textInputManager(), times(2))
                .onTextInputFocusChange(input, false);
    }

    @Test
    void ordinaryTypingReachesTextArea() {
        this.selectTextTab();
        var node = this.find(nodeToFind -> nodeToFind.getElement() instanceof TextArea);
        assertTrue(this.screen.mouseClicked(this.click(node, InputConstants.MOUSE_BUTTON_LEFT), false));
        verify(Minecraft.getInstance().textInputManager())
                .onTextInputFocusChange(node.getElement(), true);
        this.layout();
        assertTrue(this.screen.charTyped(new CharacterEvent('a')));
        assertTrue(this.screen.charTyped(new CharacterEvent(0x1F642)));
        var area = (TextArea) node.getElement();
        assertEquals("a\uD83D\uDE42", area.getValue());
        area.setEditable(false);
        verify(Minecraft.getInstance().textInputManager())
                .onTextInputFocusChange(area, false);
        area.setEditable(true);
        this.screen.removed();
        verify(Minecraft.getInstance().textInputManager(), times(2))
                .onTextInputFocusChange(area, false);
    }

    @Test
    void pasteStillReachesTextInputAndTextArea() {
        when(Minecraft.getInstance().keyboardHandler.getClipboard()).thenReturn("pasted");
        var input = this.find(node -> node.getElement() instanceof TextInput);
        this.screen.mouseClicked(this.click(input, InputConstants.MOUSE_BUTTON_LEFT), false);
        assertTrue(this.screen.keyPressed(this.paste()));
        assertEquals("pasted", ((TextInput) input.getElement()).getValue());
        this.selectTextTab();
        var area = this.find(node -> node.getElement() instanceof TextArea);
        this.screen.mouseClicked(this.click(area, InputConstants.MOUSE_BUTTON_LEFT), false);
        assertTrue(this.screen.keyPressed(this.paste()));
        assertEquals("pasted", ((TextArea) area.getElement()).getValue());
    }

    private KeyEvent paste() {
        return new KeyEvent(InputConstants.KEY_V, InputConstants.KEYCODE_V, InputConstants.MOD_CONTROL);
    }

    private void selectTextTab() {
        ((Tabs) this.find(node -> node.getElement() instanceof Tabs).getElement()).setSelectedTab(2);
        this.layout();
    }

    private ITreeNode actionTarget() {
        var scroll = (ScrollArea) this.find(node -> node.getElement() instanceof ScrollArea).getElement();
        scroll.scrollTo(0, 10000);
        this.layout();
        return this.find(node -> node.getElement() instanceof Label label
                && label.getText().getString().equals("Right-click here for actions"));
    }

    private ContextMenu contextMenu() {
        return (ContextMenu) this.find(node -> node.getElement() instanceof ContextMenu).getElement();
    }

    private MouseButtonEvent click(ITreeNode node, int button) {
        var bounds = node.getElement().getBounds();
        return new MouseButtonEvent(bounds.x() + 2, bounds.y() + 2, new MouseButtonInfo(button, 0));
    }

    private void layout() {
        this.tree.prepareFrame(800, 600, this.screen.getScreenContext());
        var constraints = new Constraints(0, 800, 0, 600);
        this.tree.measureTree(constraints);
        this.tree.placeTree(new Position(0, 0), constraints);
    }

    private ITreeNode find(Predicate<ITreeNode> predicate) {
        return this.find(this.tree.getRoot(), predicate);
    }

    private ITreeNode find(ITreeNode node, Predicate<ITreeNode> predicate) {
        if (predicate.test(node)) return node;
        for (var child : node.getChildren()) {
            var found = this.find(child, predicate);
            if (found != null) return found;
        }
        return null;
    }
}
