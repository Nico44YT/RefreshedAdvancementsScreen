package nico.refreshed_advancements_screen.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import nico.refreshed_advancements_screen.client.screen.RefreshedAdvancementsScreen;
import nico.refreshed_advancements_screen.mixin.client.accessor.AdvancementTabAccessor;

public class RefreshedAdvancementsButton extends Button {
    protected final AdvancementTab advancementTab;
    protected final RefreshedAdvancementsTabList parent;

    protected RefreshedAdvancementsButton(RefreshedAdvancementsTabList parent, int x, int y, int width, int height, AdvancementTab advancementTab, OnPress onPress, CreateNarration narrationSupplier) {
        super(x, y, width, height, advancementTab.getTitle(), onPress, narrationSupplier);

        this.parent = parent;
        this.advancementTab = advancementTab;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        Minecraft minecraftClient = Minecraft.getInstance();

        if (this.parent.getScreen().getSelectedTab() == this.advancementTab) {
            this.fillGradient(graphics, getX(), getY(), getWidth(), getHeight(), 0x3F_FF_FF_FF, 0x0);
        } else {
            this.fillGradient(graphics, getX(), getY(), getWidth(), getHeight(), 0xBF_00_00_00, 0x0);
        }

        graphics.nextStratum();
        int i = this.active ? 0xFFFFFF : 0xA0A0A0;

        float iconScale = 0.8f;

        graphics.pose().pushMatrix();
        graphics.pose().translate(getX(), getY() + 1);
        graphics.pose().scale(iconScale, iconScale);
        graphics.pose().translate(1 + iconScale, 1 + iconScale);

        //? >= 26.3 {
        /*graphics.fakeItem(((AdvancementTabAccessor)advancementTab).refreshed_advancements$getIcon(), 0, 0);
        *///?} else {
        graphics.fakeItem(advancementTab.getDisplay().getIcon().create(), 0, 0);
        //?}

        graphics.pose().popMatrix();

        drawScrollableText(graphics, minecraftClient.font, 16, i | (int)Math.ceil(this.alpha * 255.0F) << 24);
    }

    protected void drawScrollableText(GuiGraphicsExtractor graphics, Font textRenderer, int xMargin, int color) {
        int startX = this.getX() + xMargin;
        int endX = this.getX() + this.getWidth();
        drawText(graphics, textRenderer, this.getMessage(), startX, startX, this.getY(), endX, this.getY() + this.getHeight(), color);
    }

    protected void drawText(
            GuiGraphicsExtractor graphics, Font textRenderer, Component text, int centerX, int startX, int startY, int endX, int endY, int color
    ) {
        int textWidth = textRenderer.width(text);
        int renderY = (startY + endY - 9) / 2 + 1;
        int middleX = endX - startX;
        if (textWidth > middleX) {
            int textStart = textWidth - middleX;
            double time = Util.getMillis() / 1000.0;
            double e = Math.max(textStart * 0.5, 3.0);
            double f = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * time / e)) / 2.0 + 0.5;
            double g = Mth.lerp(f, 0.0, textStart);
            graphics.enableScissor(startX, 0, endX, parent.getScreen().getHeight());
            graphics.text(textRenderer, text, startX - (int) g, renderY, color);
            graphics.disableScissor();
        } else {
            graphics.text(textRenderer, text, centerX, renderY, color);
        }
    }


    public void fillGradient(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int startColor, int endColor) {
        _fillGradient(graphics, x, y, x + width - 1, y + height - 1, startColor, endColor);
    }

    private void _fillGradient(GuiGraphicsExtractor graphics, int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
        fillGradient(graphics, startX, startY, endX, endY, 0, colorStart, colorEnd);
    }


    private void fillGradient(GuiGraphicsExtractor graphics, int startX, int startY, int endX, int endY, int z, int colorStart, int colorEnd) {
        HorizontalColoredQuadGuiElementRenderState.fillGradient(graphics, startX, startY, endX, endY, z, colorStart, colorEnd);
    }
}
