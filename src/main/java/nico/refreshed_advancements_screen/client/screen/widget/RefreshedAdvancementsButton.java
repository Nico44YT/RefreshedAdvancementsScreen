package nico.refreshed_advancements_screen.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import nico.refreshed_advancements_screen.client.screen.util.ScissorStack;
import net.minecraft.util.math.Matrix4f;


public class RefreshedAdvancementsButton extends ButtonWidget {
    protected final AdvancementTab advancementTab;
    protected final RefreshedAdvancementsTabList parent;

    protected RefreshedAdvancementsButton(RefreshedAdvancementsTabList parent, int x, int y, int width, int height, AdvancementTab advancementTab, PressAction onPress) {
        super(x, y, width, height, advancementTab.getTitle(), onPress);

        this.parent = parent;
        this.advancementTab = advancementTab;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        if (this.parent.getScreen().getSelectedTab() == this.advancementTab) {
            this.refreshed_advancements$fillGradient(matrices, getX(), getY(), getWidth(), getHeight(), 0x3F_FF_FF_FF, 0x0);
        } else {
            this.refreshed_advancements$fillGradient(matrices, getX(), getY(), getWidth(), getHeight(), 0xBF_00_00_00, 0x0);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        var itemMatrix = RenderSystem.getModelViewStack();
        itemMatrix.push();
        itemMatrix.translate(getX(), getY() + 1 - parent.scrollAmount, 0);
        itemMatrix.scale(0.8f, 0.8f, 1);
        itemMatrix.translate(1 + 0.8f, 1 + 0.8f, 0);

        minecraftClient.getItemRenderer().renderInGui(advancementTab.getDisplay().getIcon(), 0, 0);

        itemMatrix.pop();
        RenderSystem.applyModelViewMatrix();

        ScissorStack.pop();
    }

    public void renderText(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();

        int i = this.active ? 0xFFFFFF : 0xA0A0A0;
        this.drawMessage(matrices, minecraftClient.textRenderer, i | MathHelper.ceil(this.alpha * 255.0F) << 24);

    }

    public void drawMessage(MatrixStack matrices, TextRenderer textRenderer, int color) {
        drawScrollableText(matrices, textRenderer, 16, color);
    }

    protected void drawScrollableText(MatrixStack matrixStack, TextRenderer textRenderer, int xMargin, int color) {
        int startX = this.getX() + xMargin;
        int endX = this.getX() + this.getWidth();
        drawText(matrixStack, textRenderer, this.getMessage(), xMargin, startX, this.getY(), endX, this.getY() + this.getHeight(), color);
    }

    protected void drawText(
            MatrixStack matrices, TextRenderer textRenderer, Text text, int centerX, int startX, int startY, int endX, int endY, int color
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

            drawTextWithShadow(matrices, textRenderer, text, startX - (int) g, renderY, color);
        } else {
            drawTextWithShadow(matrices, textRenderer, text, centerX, renderY, color);
        }
    }


    public void refreshed_advancements$fillGradient(MatrixStack matrixStack, int x, int y, int width, int height, int startColor, int endColor) {
        refreshed_advancements$fillGradient(matrixStack, x, y, x + width - 1, y + height - 1, startColor, endColor, 0);
    }

    protected static void refreshed_advancements$fillGradient(MatrixStack matrices, int startX, int startY, int endX, int endY, int colorStart, int colorEnd, int z) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        refreshed_advancements$fillGradient(matrices.peek().getModel(), bufferBuilder, startX, startY, endX, endY, z, colorStart, colorEnd);
        tessellator.draw();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    protected static void refreshed_advancements$fillGradient(Matrix4f matrix, BufferBuilder builder, int startX, int startY, int endX, int endY, int z, int colorStart, int colorEnd) {
        float startAlpha = (float) getAlpha(colorStart) / 255.0F;
        float startRed = (float) getRed(colorStart) / 255.0F;
        float startGreen = (float) getGreen(colorStart) / 255.0F;
        float startBlue = (float) getBlue(colorStart) / 255.0F;

        float endAlpha = (float) getAlpha(colorEnd) / 255.0F;
        float endRed = (float) getRed(colorEnd) / 255.0F;
        float endGreen = (float) getGreen(colorEnd) / 255.0F;
        float endBlue = (float) getBlue(colorEnd) / 255.0F;

        builder.vertex(matrix, (float) startX, (float) startY, (float) z).color(startRed, startGreen, startBlue, startAlpha).next();
        builder.vertex(matrix, (float) startX, (float) endY, (float) z).color(startRed, startGreen, startBlue, startAlpha).next();
        builder.vertex(matrix, (float) endX, (float) endY, (float) z).color(endRed, endGreen, endBlue, endAlpha).next();
        builder.vertex(matrix, (float) endX, (float) startY, (float) z).color(endRed, endGreen, endBlue, endAlpha).next();
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    private static int getAlpha(int color) {
        return color >> 24 & 0xFF;
    }

    private static int getRed(int color) {
        return color >> 16 & 0xFF;
    }

    private static int getGreen(int color) {
        return color >> 8 & 0xFF;
    }

    private static int getBlue(int color) {
        return color & 0xFF;
    }
}