package nico.refreshed_advancements_screen.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import nico.refreshed_advancements_screen.client.config.RefreshedAdvancementsConfig;
import nico.refreshed_advancements_screen.mixin.client.accessor.AdvancementTabAccessor;

import static nico.refreshed_advancements_screen.client.RefreshedAdvancementsClient.lerpColors;

public class RefreshedAdvancementsButton extends Button {
    protected final AdvancementTab advancementTab;
    protected final RefreshedAdvancementsTabList parent;

    protected boolean isHoveringOver;
    protected float fade;

    protected RefreshedAdvancementsButton(RefreshedAdvancementsTabList parent, int x, int y, int width, int height, AdvancementTab advancementTab, OnPress onPress, CreateNarration narrationSupplier) {
        super(x, y, width, height, advancementTab.getTitle(), onPress, narrationSupplier);

        this.parent = parent;
        this.advancementTab = advancementTab;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        Minecraft minecraftClient = Minecraft.getInstance();

        this.isHoveringOver = isMouseOver(mouseX, mouseY);

        if (this.parent.getScreen().getSelectedTab() == this.advancementTab) { // Selected
            this.fillGradient(graphics, getX(), getY(), getWidth(), getHeight(),
                    RefreshedAdvancementsConfig.getSelectedEntryStartColor(),
                    RefreshedAdvancementsConfig.getSelectedEntryEndColor(),
                    RefreshedAdvancementsConfig.getSelectedEntryGradientOrientation()
            );
        } else { // Unselected
            this.fillGradient(graphics, getX(), getY(), getWidth(), getHeight(),
                    lerpColors(RefreshedAdvancementsConfig.getEntryStartColor(), RefreshedAdvancementsConfig.getHoverEntryStartColor(), fade),
                    lerpColors(RefreshedAdvancementsConfig.getEntryEndColor(), RefreshedAdvancementsConfig.getHoverEntryEndColor(), fade),
                    RefreshedAdvancementsConfig.getEntryGradientOrientation()
            );
        }

        graphics.nextStratum();

        if (RefreshedAdvancementsConfig.shouldDrawEntryIcons()) {
            float iconScale = RefreshedAdvancementsConfig.getEntryIconSize();

            graphics.pose().pushMatrix();
            graphics.pose().translate(getX(), getY() + 1);
            graphics.pose().scale(iconScale, iconScale);
            graphics.pose().translate(1 + iconScale, 1 + iconScale);

            //? >=26.3 {
            /*graphics.fakeItem(((AdvancementTabAccessor)advancementTab).refreshed_advancements$getIcon(), 0, 0);
             *///?} else {
            graphics.fakeItem(advancementTab.getDisplay().getIcon().create(), 0, 0);
            //?}

            graphics.pose().popMatrix();
        }
        drawScrollableText(graphics, minecraftClient.font, RefreshedAdvancementsConfig.shouldDrawEntryIcons() ? (int) (20 * RefreshedAdvancementsConfig.getEntryIconSize()) : 2,
                RefreshedAdvancementsConfig.getTextColor(),
                RefreshedAdvancementsConfig.getTextShadowColor()
        );

        if (isHoveringOver) {
            fade = Math.clamp(fade + 0.2f, 0, 0.3f);
        } else {
            fade = Math.clamp(fade - 0.2f, 0, 1.0f);
        }
    }

    //region # Helper Methods #
    protected void drawScrollableText(GuiGraphicsExtractor graphics, Font textRenderer, int xMargin, int color, int shadowColor) {
        int startX = this.getX() + xMargin;
        int endX = this.getX() + this.getWidth();
        drawText(graphics, textRenderer, this.getMessage(), startX, startX, this.getY(), endX, this.getY() + this.getHeight(), color, shadowColor);
    }

    protected void drawText(
            GuiGraphicsExtractor graphics, Font textRenderer, Component text, int centerX, int startX, int startY, int endX, int endY, int color, int shadowColor
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
            graphics.text(textRenderer, text, startX - (int) g + 1, renderY + 1, shadowColor, false);
            graphics.text(textRenderer, text, startX - (int) g, renderY, color, false);
            graphics.disableScissor();
        } else {
            graphics.text(textRenderer, text, centerX + 1, renderY + 1, shadowColor, false);
            graphics.text(textRenderer, text, centerX, renderY, color, false);
        }
    }


    public void fillGradient(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int startColor, int endColor, RefreshedAdvancementsConfig.GradientOrientation gradientOrientation) {
        _fillGradient(graphics, x, y, x + width - 1, y + height - 1, startColor, endColor, gradientOrientation);
    }

    private void _fillGradient(GuiGraphicsExtractor graphics, int startX, int startY, int endX, int endY, int colorStart, int colorEnd, RefreshedAdvancementsConfig.GradientOrientation gradientOrientation) {
        fillGradient(graphics, startX, startY, endX, endY, 0, colorStart, colorEnd, gradientOrientation);
    }


    private void fillGradient(GuiGraphicsExtractor graphics, int startX, int startY, int endX, int endY, int z, int colorStart, int colorEnd, RefreshedAdvancementsConfig.GradientOrientation gradientOrientation) {
        HorizontalColoredQuadGuiElementRenderState.fillGradient(graphics, startX, startY, endX, endY, z, colorStart, colorEnd, gradientOrientation);
    }
    //endregion
}
