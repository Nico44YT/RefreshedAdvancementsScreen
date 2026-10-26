package nico.refreshed_advancements_screen.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import nico.refreshed_advancements_screen.client.config.RefreshedAdvancementsModMenuApiImpl;

public class ModConfigButton extends Button {

    private final Screen parent;
    private static final int SIZE = 32;

    public ModConfigButton(Screen parent, Position position, int middleX, int middleY, int screenWidth, int screenHeight) {
        super(middleX - position.getShiftX() * (screenWidth - SIZE) / 2 + SIZE * position.getOffsetX(), middleY - position.getShiftY() * (screenHeight - SIZE) / 2 + SIZE * position.getOffsetY(),
                SIZE, SIZE, Component.translatable("widget.refreshed_advancements.config"), button -> {

                    //? >=26.2 {
                    /*Minecraft.getInstance().gui.setScreen(RefreshedAdvancementsModMenuApiImpl.CREATE.apply(parent));
                    *///?} else {
                    Minecraft.getInstance().setScreen(RefreshedAdvancementsModMenuApiImpl.CREATE.apply(parent));
                     //?}

                }, $ -> Component.translatable("widget.refreshed_advancements.config"));

        this.parent = parent;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        graphics.pose().pushMatrix();

        graphics.pose().translate(getX(), getY());

        graphics.fill(0, 0, getWidth(), getHeight(), 0xFF_FF_00_FF);

        graphics.pose().popMatrix();
    }

    public enum Position {
        TOP_RIGHT(-1, 1, -1, 0),
        BOTTOM_RIGHT(-1, -1, -1, -1),
        TOP_LEFT(1, 1, 0, 0),
        BOTTOM_LEFT(1, -1, 0, -1);

        private final int shiftX;
        private final int shiftY;
        private final int offsetX;
        private final int offsetY;

        Position(int shiftX, int shiftY, int offsetX, int offsetY) {
            this.shiftX = shiftX;
            this.shiftY = shiftY;

            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        public int getShiftX() {
            return this.shiftX;
        }

        public int getShiftY() {
            return this.shiftY;
        }

        public int getOffsetX() {
            return offsetX;
        }

        public int getOffsetY() {
            return offsetY;
        }
    }
}
