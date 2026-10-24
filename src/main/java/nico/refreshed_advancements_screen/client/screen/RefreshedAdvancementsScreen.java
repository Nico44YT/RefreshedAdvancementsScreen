package nico.refreshed_advancements_screen.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.advancement.Advancement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import nico.refreshed_advancements_screen.client.screen.util.ScissorHelper;
import nico.refreshed_advancements_screen.client.screen.widget.RefreshedAdvancementsTabList;
import nico.refreshed_advancements_screen.mixin.client.accessor.AdvancementTabAccessor;
import nico.refreshed_advancements_screen.mixin.client.accessor.AdvancementsScreenAccessor;
import nico.refreshed_advancements_screen.mixin.client.accessor.ScreenAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class RefreshedAdvancementsScreen extends AdvancementsScreen implements ClientAdvancementManager.Listener {
    public static final int SCROLL_AREA_WIDTH = 80;

    private static final Text SAD_LABEL_TEXT = new LiteralText(":(");
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
            this.getManager().selectTab(this.getTabs().values().iterator().next().method_2307(), true);
        } else {
            this.getManager().selectTab(this.getSelectedTab() == null ? null : this.getSelectedTab().method_2307(), true);
        }

        this.getTabs().values().forEach(tab -> {
            this.tabWidgetList.addAdvancementTab(tab);
        });
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground(0);

        for (Element element : ((ScreenAccessor) this).refreshed_advancements$getChildren()) {
            if (element instanceof Drawable drawable) drawable.render(mouseX, mouseY, delta);
        }

        vLine(SCROLL_AREA_WIDTH, -1, height + 1, 0xBF_00_00_00);
        vLine(SCROLL_AREA_WIDTH + 1, -1, height + 1, 0x33_FF_FF_FF);
        fill(2, 0, SCROLL_AREA_WIDTH, height, 0x70_00_00_00);

        drawCenteredString(font, ADVANCEMENTS_TEXT.asFormattedString(), SCROLL_AREA_WIDTH / 2, 3, 0xFF_FF_FF_FF);

        this.tabWidgetList.render(mouseX, mouseY, delta);

        this.drawAdvancementTree(mouseX, mouseY, SCROLL_AREA_WIDTH);
        this.drawWidgetTooltip(mouseX, mouseY, SCROLL_AREA_WIDTH, 0);
    }

    @Override
    public void removed() {
        this.getManager().setListener(null);
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.minecraft.getNetworkHandler();
        if (clientPlayNetworkHandler != null) {
            clientPlayNetworkHandler.sendPacket(AdvancementTabC2SPacket.close());
        }
    }

    private void drawAdvancementTree(int mouseX, int mouseY, int offsetX) {
        AdvancementTab advancementTab = getSelectedTab();
        if (advancementTab == null) {
            GlStateManager.pushMatrix();
            int centerX = getWidth() / 2 + offsetX / 2;
            int centerY = getHeight() / 2 - this.font.fontHeight / 2;

            fill(offsetX, 0, getWidth(), getHeight(), 0x55_00_00_00);

            drawCenteredString(this.font, EMPTY_TEXT.asFormattedString(), centerX, centerY, 0xFF_FF_FF_FF);
            drawCenteredString(this.font, SAD_LABEL_TEXT.asFormattedString(), centerX, centerY + this.font.fontHeight + 2, 0xFF_FF_FF_FF);
            GlStateManager.popMatrix();
        } else {
            GlStateManager.pushMatrix();
            drawAdvancementTabContent(advancementTab, offsetX);
            GlStateManager.popMatrix();
        }
    }

    private void drawWidgetTooltip(int mouseX, int mouseY, int offsetX, int offsetY) {
        if (this.getSelectedTab() != null) {
            //GlStateManager.translated(offsetX, offsetY, 400);
            GlStateManager.enableDepthTest();
            GlStateManager.pushMatrix();
            GlStateManager.translated(RefreshedAdvancementsScreen.SCROLL_AREA_WIDTH, 0, 400);
            drawAdvancementWidgetTooltip(getSelectedTab(), mouseX - offsetX, mouseY - offsetY, offsetX, offsetY);
            GlStateManager.popMatrix();
            GlStateManager.disableDepthTest();
        }
    }

    public void drawAdvancementWidgetTooltip(AdvancementTab tab, int mouseX, int mouseY, int x, int y) {
        AdvancementTabAccessor accessor = ((AdvancementTabAccessor) tab);
        fill(0, 0, getWidth(), getHeight(), MathHelper.floor(accessor.refreshed_advancements$getAlpha() * 255f) << 24);
        boolean isTooltipShown = false;
        int originX = MathHelper.floor(accessor.refreshed_advancements$getOriginX());
        int originY = MathHelper.floor(accessor.refreshed_advancements$getOriginY());
        if (mouseX > 0 && mouseX < getWidth() && mouseY > 0 && mouseY < getHeight()) {
            for (AdvancementWidget advancementWidget : accessor.refreshed_advancements$getWidgets().values()) {
                if (advancementWidget.method_2329(originX, originY, mouseX, mouseY)) { //shouldRender()
                    isTooltipShown = true;

                    GlStateManager.pushMatrix();

                    advancementWidget.method_2331(originX, originY, accessor.refreshed_advancements$getAlpha(), x, y); //drawTooltip

                    GlStateManager.popMatrix();

                    break;
                }
            }
        }

        float alpha = accessor.refreshed_advancements$getAlpha();
        if (isTooltipShown) {
            alpha = MathHelper.clamp(alpha + 0.02F, 0.0F, 0.3F);
        } else {
            alpha = MathHelper.clamp(alpha - 0.04F, 0.0F, 1.0F);
        }
        accessor.refreshed_advancements$setAlpha(alpha);
    }

    private void drawAdvancementTabContent(AdvancementTab advancementTab, int xOffset) {
        AdvancementTabAccessor accessor = ((AdvancementTabAccessor) advancementTab);

        GlStateManager.pushMatrix();

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

        ScissorHelper.enableScissor(
                xOffset + 2,
                0,
                getWidth(),
                getHeight()
        );

        GlStateManager.pushMatrix();
        int x = MathHelper.floor(accessor.refreshed_advancements$getOriginX());
        int y = MathHelper.floor(accessor.refreshed_advancements$getOriginY());

        accessor.refreshed_advancements$getRootWidget().method_2323(x + xOffset, y, true); //renderLines()
        accessor.refreshed_advancements$getRootWidget().method_2323(x + xOffset, y, false); //renderLines()
        accessor.refreshed_advancements$getRootWidget().method_2325(x + xOffset, y); //renderWidgets()

        GlStateManager.popMatrix();
        ScissorHelper.disableScissor();

        GlStateManager.popMatrix();
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
