package nico.refreshed_advancements_screen.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.advancement.Advancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import nico.refreshed_advancements_screen.client.screen.widget.RefreshedAdvancementsTabList;
import nico.refreshed_advancements_screen.mixin.client.accessor.AdvancementTabAccessor;
import nico.refreshed_advancements_screen.mixin.client.accessor.AdvancementsScreenAccessor;
import nico.refreshed_advancements_screen.mixin.client.accessor.ScreenAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class RefreshedAdvancementsScreen extends AdvancementsScreen implements ClientAdvancementManager.Listener {
    protected static final int SCROLL_AREA_WIDTH = 80;

    private static final Text SAD_LABEL_TEXT = new TranslatableText("advancements.sad_label");
    private static final Text EMPTY_TEXT = new TranslatableText("advancements.empty");
    private static final Text ADVANCEMENTS_TEXT = new TranslatableText("gui.advancements");

    private RefreshedAdvancementsTabList tabWidgetList;

    public RefreshedAdvancementsScreen(ClientAdvancementManager advancementHandler) {
        super(advancementHandler);
    }

    @Override
    protected void init() {
        this.tabWidgetList = new RefreshedAdvancementsTabList(this, 0, 0, SCROLL_AREA_WIDTH, height);

        this.getTabs().clear();
        this.setSelectedTab(null);
        this.getManager().setListener(this);
        if (this.getSelectedTab() == null && !this.getTabs().isEmpty()) {
            this.getManager().selectTab(this.getTabs().values().iterator().next().getRoot(), true);
        } else {
            this.getManager().selectTab(this.getSelectedTab() == null ? null : this.getSelectedTab().getRoot(), true);
        }

        this.getTabs().values().forEach(tab -> {
            this.tabWidgetList.addAdvancementTab(tab);
        });
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        for (Drawable drawable : ((ScreenAccessor) this).refreshed_advancements$getDrawables()) {
            drawable.render(matrices, mouseX, mouseY, delta);
        }

        drawVerticalLine(matrices, SCROLL_AREA_WIDTH, -1, height + 1, 0xBF_00_00_00);
        drawVerticalLine(matrices, SCROLL_AREA_WIDTH + 1, -1, height + 1, 0x33_FF_FF_FF);
        fill(matrices, 2, 0, SCROLL_AREA_WIDTH, height, 0x70_00_00_00);

        drawTextWithShadow(matrices, textRenderer, ADVANCEMENTS_TEXT, (SCROLL_AREA_WIDTH - textRenderer.getWidth(ADVANCEMENTS_TEXT)) / 2, 3, 0xFF_FF_FF_FF);

        this.tabWidgetList.render(matrices, mouseX, mouseY, delta);

        this.drawAdvancementTree(matrices, mouseX, mouseY, SCROLL_AREA_WIDTH);
        this.drawWidgetTooltip(matrices, mouseX, mouseY, SCROLL_AREA_WIDTH, 0);
    }

    @Override
    public void removed() {
        this.getManager().setListener(null);
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
        if (clientPlayNetworkHandler != null) {
            clientPlayNetworkHandler.sendPacket(AdvancementTabC2SPacket.close());
        }
    }

    private void drawAdvancementTree(MatrixStack matrices, int mouseX, int mouseY, int offsetX) {
        AdvancementTab advancementTab = getSelectedTab();
        if (advancementTab == null) {
            matrices.push();
            int centerX = getWidth() / 2 + offsetX / 2;
            int centerY = getHeight() / 2 - this.textRenderer.fontHeight / 2;

            fill(matrices, offsetX, 0, getWidth(), getHeight(), 0x55_00_00_00);

            drawCenteredTextWithShadow(matrices, this.textRenderer, EMPTY_TEXT.asOrderedText(), centerX, centerY, 0xFF_FF_FF_FF);
            drawCenteredTextWithShadow(matrices, this.textRenderer, SAD_LABEL_TEXT.asOrderedText(), centerX, centerY + this.textRenderer.fontHeight + 2, 0xFF_FF_FF_FF);
            matrices.pop();
        } else {
            matrices.push();
            drawAdvancementTabContent(advancementTab, matrices, offsetX);
            matrices.pop();
        }
    }

    private void drawWidgetTooltip(MatrixStack matrices, int mouseX, int mouseY, int offsetX, int offsetY) {
        if (this.getSelectedTab() != null) {
            matrices.push();
            matrices.translate(offsetX, offsetY, 400);
            RenderSystem.enableDepthTest();
            drawAdvancementWidgetTooltip(getSelectedTab(), matrices, mouseX - offsetX, mouseY - offsetY, offsetX, offsetY);
            RenderSystem.disableDepthTest();
            matrices.pop();
        }
    }

    public void drawAdvancementWidgetTooltip(AdvancementTab tab, MatrixStack matrices, int mouseX, int mouseY, int x, int y) {
        AdvancementTabAccessor accessor = ((AdvancementTabAccessor) tab);

        matrices.push();
        matrices.translate(0.0F, 0.0F, -200.0F);
        fill(matrices, 0, 0, getWidth(), getHeight(), MathHelper.floor(accessor.refreshed_advancements$getAlpha() * 255f) << 24);
        boolean isTooltipShown = false;
        int originX = MathHelper.floor(accessor.refreshed_advancements$getOriginX());
        int originY = MathHelper.floor(accessor.refreshed_advancements$getOriginY());
        if (mouseX > 0 && mouseX < getWidth() && mouseY > 0 && mouseY < getHeight()) {
            for (AdvancementWidget advancementWidget : accessor.refreshed_advancements$getWidgets().values()) {
                if (advancementWidget.shouldRender(originX, originY, mouseX, mouseY)) {
                    isTooltipShown = true;

                    var itemMatrix = RenderSystem.getModelViewStack();
                    itemMatrix.push();

                    itemMatrix.translate(SCROLL_AREA_WIDTH, 0, 400);
                    advancementWidget.drawTooltip(matrices, originX, originY, accessor.refreshed_advancements$getAlpha(), x, y);

                    itemMatrix.pop();
                    RenderSystem.applyModelViewMatrix();

                    break;
                }
            }
        }

        matrices.pop();
        float alpha = accessor.refreshed_advancements$getAlpha();
        if (isTooltipShown) {
            alpha = MathHelper.clamp(alpha + 0.02F, 0.0F, 0.3F);
        } else {
            alpha = MathHelper.clamp(alpha - 0.04F, 0.0F, 1.0F);
        }
        accessor.refreshed_advancements$setAlpha(alpha);
    }

    private void drawAdvancementTabContent(AdvancementTab advancementTab, MatrixStack matrices, int xOffset) {
        AdvancementTabAccessor accessor = ((AdvancementTabAccessor) advancementTab);
        matrices.push();

        if (!accessor.refreshed_advancements$isInitialized()) {
            double viewWidth = this.getWidth() - SCROLL_AREA_WIDTH;
            double viewHeight = this.getHeight();

            double centerX = viewWidth / 2.0;
            double centerY = viewHeight / 2.0;

            double contentCenterX = (
                    accessor.refreshed_advancements$getMinPanX()
                            + accessor.refreshed_advancements$getMaxPanX()
            ) / 2.0;

            double contentCenterY = (
                    accessor.refreshed_advancements$getMinPanY()
                            + accessor.refreshed_advancements$getMaxPanY()
            ) / 2.0;

            accessor.refreshed_advancements$setOriginX(centerX - contentCenterX);
            accessor.refreshed_advancements$setOriginY(centerY - contentCenterY);
            accessor.refreshed_advancements$setInitialized(true);
        }

        Window window = MinecraftClient.getInstance().getWindow();

        double scale = window.getScaleFactor();
        int framebufferHeight = window.getFramebufferHeight();

        int scissorX = (int) ((xOffset + 2) * scale);
        int scissorY = (int) (framebufferHeight - (getHeight() * scale));
        int scissorWidth = (int) (getWidth() * scale);
        int scissorHeight = (int) (getHeight() * scale);

        RenderSystem.enableScissor(
                scissorX,
                scissorY,
                scissorWidth,
                scissorHeight
        );

        matrices.push();
        int x = MathHelper.floor(accessor.refreshed_advancements$getOriginX());
        int y = MathHelper.floor(accessor.refreshed_advancements$getOriginY());

        accessor.refreshed_advancements$getRootWidget().renderLines(matrices, x + xOffset, y, true);
        accessor.refreshed_advancements$getRootWidget().renderLines(matrices, x + xOffset, y, false);
        accessor.refreshed_advancements$getRootWidget().renderWidgets(matrices, x + xOffset, y);

        matrices.pop();
        RenderSystem.disableScissor();

        matrices.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX > SCROLL_AREA_WIDTH) {
            this.setMovingTab(true);
            return true;
        }

        if (mouseX < SCROLL_AREA_WIDTH) {
            this.setMovingTab(false);
            return tabWidgetList.mouseClicked(mouseX, mouseY, button);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button,
            double deltaX,
            double deltaY
    ) {
        if (button != 0 || !this.isMovingTab() || mouseX < SCROLL_AREA_WIDTH) {
            this.setMovingTab(false);
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        AdvancementTab tab = this.getSelectedTab();
        if (tab != null && mouseX > SCROLL_AREA_WIDTH && isMovingTab()) {
            this.moveTabContent(tab, deltaX, deltaY);
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && this.isMovingTab()) {
            this.setMovingTab(false);
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (mouseX > SCROLL_AREA_WIDTH) {
            moveTabContent(getSelectedTab(), 0, amount * 12);
            return true;
        } else {
            tabWidgetList.mouseScrolled(mouseX, mouseY, amount * 12);
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    public void moveTabContent(AdvancementTab tab, double offsetX, double offsetY) {
        if (tab == null) return;

        AdvancementTabAccessor accessor = (AdvancementTabAccessor) tab;

        double minX = accessor.refreshed_advancements$getMinPanX() - 10;
        double maxX = accessor.refreshed_advancements$getMaxPanX() + 10;
        double minY = accessor.refreshed_advancements$getMinPanY() - 10;
        double maxY = accessor.refreshed_advancements$getMaxPanY() + 10;

        double viewWidth = this.getWidth() - SCROLL_AREA_WIDTH;
        double viewHeight = this.getHeight();

        double minOriginX = viewWidth - maxX;
        double maxOriginX = -minX;

        double minOriginY = viewHeight - maxY;
        double maxOriginY = -minY;

        if (minOriginX > maxOriginX) {
            double center = (minOriginX + maxOriginX) / 2.0;
            minOriginX = center;
            maxOriginX = center;
        }

        if (minOriginY > maxOriginY) {
            double center = (minOriginY + maxOriginY) / 2.0;
            minOriginY = center;
            maxOriginY = center;
        }

        accessor.refreshed_advancements$setOriginX(
                MathHelper.clamp(
                        accessor.refreshed_advancements$getOriginX() + offsetX,
                        minOriginX,
                        maxOriginX
                )
        );

        accessor.refreshed_advancements$setOriginY(
                MathHelper.clamp(
                        accessor.refreshed_advancements$getOriginY() + offsetY,
                        minOriginY,
                        maxOriginY
                )
        );
    }

    public @Nullable AdvancementTab getSelectedTab() {
        return ((AdvancementsScreenAccessor) this).refreshed_advancements$getSelectedTab();
    }

    public void setSelectedTab(AdvancementTab tab) {
        ((AdvancementsScreenAccessor) this).refreshed_advancements$setSelectedTab(tab);
    }

    public Map<Advancement, AdvancementTab> getTabs() {
        return ((AdvancementsScreenAccessor) this).refreshed_advancements$getTabs();
    }

    public ClientAdvancementManager getManager() {
        return ((AdvancementsScreenAccessor) this).refreshed_advancements$getAdvancementHandler();
    }

    public boolean isMovingTab() {
        return ((AdvancementsScreenAccessor) this).refreshed_advancements$isMovingTab();
    }

    public boolean setMovingTab(boolean bl) {
        ((AdvancementsScreenAccessor) this).refreshed_advancements$setMovingTab(bl);
        return bl;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
