package nico.refreshed_advancements_screen.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

public class RefreshedAdvancementsButton extends ButtonWidget {
    protected final AdvancementTab advancementTab;
    protected final RefreshedAdvancementsTabList parent;

    protected RefreshedAdvancementsButton(RefreshedAdvancementsTabList parent, int x, int y, int width, int height, AdvancementTab advancementTab, PressAction onPress, NarrationSupplier narrationSupplier) {
        super(x, y, width, height, advancementTab.getTitle(), onPress, narrationSupplier);

        this.parent = parent;
        this.advancementTab = advancementTab;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        context.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        if(this.parent.getScreen().getSelectedTab() == this.advancementTab) {
            this.fillGradient(context, getX(), getY(), getWidth(), getHeight(), 0x3F_FF_FF_FF, 0x0);
        } else {
            this.fillGradient(context, getX(), getY(), getWidth(), getHeight(), 0xBF_00_00_00, 0x0);
        }

        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.active ? 0xFFFFFF : 0xA0A0A0;

        context.getMatrices().push();
        context.getMatrices().translate(getX(), getY() + 1, 0);
        context.getMatrices().scale(0.8f, 0.8f, 1);
        context.getMatrices().translate(1 + 0.8f, 1 + 0.8f, 0);
        context.drawItem(advancementTab.getDisplay().getIcon(), 0, 0);
        context.getMatrices().pop();

        this.drawMessage(context, minecraftClient.textRenderer, i | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }

    @Override
    public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {
        drawScrollableText(context, textRenderer, 16, color);
    }

    protected void drawScrollableText(DrawContext context, TextRenderer textRenderer, int xMargin, int color) {
        int startX = this.getX() + xMargin;
        int endX = this.getX() + this.getWidth();
        drawText(context, textRenderer, this.getMessage(), startX, startX, this.getY(), endX, this.getY() + this.getHeight(), color);
    }

    protected void drawText(
            DrawContext context, TextRenderer textRenderer, Text text, int centerX, int startX, int startY, int endX, int endY, int color
    ) {
        int textWidth = textRenderer.getWidth(text);
        int renderY = (startY + endY - 9) / 2 + 1;
        int middleX = endX - startX;
        if (textWidth > middleX) {
            int textStart = textWidth - middleX;
            double time = Util.getMeasuringTimeMs() / 1000.0;
            double e = Math.max(textStart * 0.5, 3.0);
            double f = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * time / e)) / 2.0 + 0.5;
            double g = MathHelper.lerp(f, 0.0, textStart);
            context.enableScissor(startX, 0, endX, parent.getScreen().height);
            context.drawTextWithShadow(textRenderer, text, startX - (int) g, renderY, color);
            context.disableScissor();
        } else {
            context.drawTextWithShadow(textRenderer, text, centerX, renderY, color);
        }
    }

    public void fillGradient(DrawContext context, int x, int y, int width, int height, int startColor, int endColor) {
        _fillGradient(context, x, y, x + width - 1, y + height - 1, startColor, endColor);
    }

    private void _fillGradient(DrawContext context, int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
        fillGradient(context, startX, startY, endX, endY, 0, colorStart, colorEnd);
    }

    public void fillGradient(DrawContext context, int startX, int startY, int endX, int endY, int z, int colorStart, int colorEnd) {
        fillGradient(context, RenderLayer.getGui(), startX, startY, endX, endY, colorStart, colorEnd, z);
    }

    public void fillGradient(DrawContext context, RenderLayer layer, int startX, int startY, int endX, int endY, int colorStart, int colorEnd, int z) {
        VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(layer);
        fillGradient(context, vertexConsumer, startX, startY, endX, endY, z, colorStart, colorEnd);
        context.draw();
    }

    private void fillGradient(DrawContext context, VertexConsumer vertexConsumer, int startX, int startY, int endX, int endY, int z, int colorStart, int colorEnd) {
        float startAlpha = (float) ColorHelper.Argb.getAlpha(colorStart) / 255.0F;
        float startRed = (float) ColorHelper.Argb.getRed(colorStart) / 255.0F;
        float startGreen = (float) ColorHelper.Argb.getGreen(colorStart) / 255.0F;
        float startBlue = (float) ColorHelper.Argb.getBlue(colorStart) / 255.0F;

        float endAlpha = (float) ColorHelper.Argb.getAlpha(colorEnd) / 255.0F;
        float endRed = (float) ColorHelper.Argb.getRed(colorEnd) / 255.0F;
        float endGreen = (float) ColorHelper.Argb.getGreen(colorEnd) / 255.0F;
        float endBlue = (float) ColorHelper.Argb.getBlue(colorEnd) / 255.0F;
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();

        vertexConsumer.vertex(matrix4f, (float)startX, (float)startY, (float)z).color(startRed, startGreen, startBlue, startAlpha).next();
        vertexConsumer.vertex(matrix4f, (float)startX, (float)endY, (float)z).color(startRed, startGreen, startBlue, startAlpha).next();
        vertexConsumer.vertex(matrix4f, (float)endX, (float)endY, (float)z).color(endRed, endGreen, endBlue, endAlpha).next();
        vertexConsumer.vertex(matrix4f, (float)endX, (float)startY, (float)z).color(endRed, endGreen, endBlue, endAlpha).next();
    }
}
