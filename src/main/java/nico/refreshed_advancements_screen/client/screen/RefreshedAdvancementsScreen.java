package nico.refreshed_advancements_screen.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.network.chat.Component;
import nico.refreshed_advancements_screen.client.config.RefreshedAdvancementsConfig;
import nico.refreshed_advancements_screen.client.config.RefreshedAdvancementsModMenuApiImpl;
import nico.refreshed_advancements_screen.client.screen.widget.ModConfigButton;
import nico.refreshed_advancements_screen.client.screen.widget.RefreshedAdvancementsTabList;
import nico.refreshed_advancements_screen.mixin.client.accessor.AdvancementTabAccessor;
import nico.refreshed_advancements_screen.mixin.client.accessor.AdvancementsScreenAccessor;
import nico.refreshed_advancements_screen.mixin.client.accessor.ScreenAccessor;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class RefreshedAdvancementsScreen extends AdvancementsScreen implements ClientAdvancements.Listener {
    private static final Component SAD_LABEL_TEXT = Component.translatable("advancements.sad_label");
    private static final Component EMPTY_TEXT = Component.translatable("advancements.empty");
    private static final Component ADVANCEMENTS_TEXT = Component.translatable("gui.advancements");

    private float fade;
    private RefreshedAdvancementsTabList tabWidgetList;
    private ModConfigButton modConfigButton;

    public RefreshedAdvancementsScreen(ClientAdvancements advancementHandler) {
        super(advancementHandler);
    }

    @Override
    protected void init() {
        this.tabWidgetList = new RefreshedAdvancementsTabList(this, 0, 0, getScrollAreaWidth(), getHeight());

        if (FabricLoader.getInstance().isModLoaded("cloth-config") && RefreshedAdvancementsConfig.getConfigButtonPosition() != ModConfigButton.Position.NONE) {
            modConfigButton = this.addRenderableWidget(new ModConfigButton(
                    this, RefreshedAdvancementsConfig.getConfigButtonPosition(),
                    getWidth() / 2 + getScrollAreaWidth() / 2 + 1, getHeight() / 2,
                    getWidth() - getScrollAreaWidth() - 1, getHeight(),
                    button -> {

                        RefreshedAdvancementsScreen screen = new RefreshedAdvancementsScreen(getManager());

                        //? >=26.2 {
                        /*Minecraft.getInstance().gui.setScreen(RefreshedAdvancementsModMenuApiImpl.CREATE.apply(screen));
                        *///?} else {
                        Minecraft.getInstance().setScreen(RefreshedAdvancementsModMenuApiImpl.CREATE.apply(screen));
                         //?}

                    })
            );
        }

        this.getTabs().clear();
        this.setSelectedTab(null);
        this.getManager().setListener(this);
        if (this.getSelectedTab() == null && !this.getTabs().isEmpty()) {
            AdvancementTab advancementTab = this.getTabs().values().iterator().next();

            //? >=26.3 {
            /*this.getManager().setSelectedTab(advancementTab.getRootAdvancement(), true);
            *///?} else {
            this.getManager().setSelectedTab(advancementTab.getRootNode().holder(), true);
             //?}
        } else {
            //? >=26.3 {
            /*this.getManager().setSelectedTab(this.getSelectedTab() == null ? null : this.getSelectedTab().getRootAdvancement(), true);
            *///?} else {
            this.getManager().setSelectedTab(this.getSelectedTab() == null ? null : this.getSelectedTab().getRootNode().holder(), true);
             //?}
        }

        this.getTabs().values().forEach(tab -> {
            this.tabWidgetList.addAdvancementTab(tab);
        });
    }

    @Override
    protected void repositionElements() {
        recalculateTabCenter(getSelectedTab(), true);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graphics.guiRenderState.nextStratum();

        if (RefreshedAdvancementsConfig.shouldChangeMouseCursor()) {
            if (this.isMovingTab() && this.getSelectedTab() != null && mouseX > getScrollAreaWidth()) {
                if (this.getSelectedTab().canScrollHorizontally() && this.getSelectedTab().canScrollVertically()) {
                    graphics.requestCursor(CursorTypes.RESIZE_ALL);
                } else if (this.getSelectedTab().canScrollHorizontally()) {
                    graphics.requestCursor(CursorTypes.RESIZE_EW);
                } else if (this.getSelectedTab().canScrollVertically()) {
                    graphics.requestCursor(CursorTypes.RESIZE_NS);
                }
            }
        }

        graphics.guiRenderState.nextStratum();
        graphics.verticalLine(getScrollAreaWidth(), -1, getHeight() + 1, RefreshedAdvancementsConfig.getScrollAreaShadowColor());
        graphics.verticalLine(getScrollAreaWidth() + 1, -1, getHeight() + 1, RefreshedAdvancementsConfig.getScrollAreaHighlightColor());
        graphics.fill(RefreshedAdvancementsConfig.getScrollbarWidth(), 0, getScrollAreaWidth(), getHeight(), RefreshedAdvancementsConfig.getScrollAreaColor());

        graphics.guiRenderState.nextStratum();
        this.tabWidgetList.extractRenderState(graphics, mouseX, mouseY, delta);

        if (RefreshedAdvancementsConfig.shouldDrawAdvancementsTitle())
            graphics.centeredText(font, ADVANCEMENTS_TEXT, getScrollAreaWidth() / 2 - 1, 3, getTextColor());

        graphics.guiRenderState.nextStratum();

        this.drawAdvancementTree(graphics, mouseX, mouseY, getScrollAreaWidth());
        graphics.guiRenderState.nextStratum();

        this.drawWidgetTooltip(graphics, mouseX, mouseY, getScrollAreaWidth(), 0);
        graphics.guiRenderState.nextStratum();

        for (Renderable drawable : ((ScreenAccessor) this).refreshed_advancements$getDrawables()) {
            drawable.extractRenderState(graphics, mouseX, mouseY, delta);
        }
    }

    private void drawAdvancementTree(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x) {
        AdvancementTab advancementTab = getSelectedTab();
        if (advancementTab == null) {
            int centerX = (getWidth() + x) / 2;
            int centerY = (getHeight() - font.lineHeight) / 2;

            graphics.fill(x, 0, getWidth(), getHeight(), RefreshedAdvancementsConfig.getAdvancementsAreaColor());

            graphics.guiRenderState.nextStratum();

            graphics.text(font, EMPTY_TEXT, centerX - font.width(EMPTY_TEXT.getVisualOrderText()) / 2 + 1, centerY + 1, getTextDropShadowColor(), false);
            graphics.text(font, EMPTY_TEXT, centerX - font.width(EMPTY_TEXT.getVisualOrderText()) / 2, centerY, getTextColor(), false);
            graphics.text(font, SAD_LABEL_TEXT, centerX - font.width(SAD_LABEL_TEXT.getVisualOrderText()) / 2 + 1, centerY + this.font.lineHeight + 3, getTextDropShadowColor(), false);
            graphics.text(font, SAD_LABEL_TEXT, centerX - font.width(SAD_LABEL_TEXT.getVisualOrderText()) / 2, centerY + this.font.lineHeight + 2, getTextColor(), false);
        } else {
            graphics.guiRenderState.nextStratum();

            drawAdvancementTabContent(advancementTab, graphics, x);
        }
    }

    private void drawWidgetTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y) {
        if (this.getSelectedTab() != null) {
            graphics.pose().pushMatrix();

            graphics.guiRenderState.nextStratum();
            drawAdvancementWidgetTooltip(this.getSelectedTab(), graphics, mouseX - x, mouseY - y, x, y);

            graphics.pose().popMatrix();
        }
    }

    public void drawAdvancementWidgetTooltip(AdvancementTab tab, GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y) {
        AdvancementTabAccessor accessor = ((AdvancementTabAccessor) tab);

        graphics.pose().pushMatrix();
        graphics.pose().translate(getScrollAreaWidth(), 0.0F);

        graphics.fill(0, 0, getWidth(), getHeight(), (int) Math.floor(fade * 255f) << 24);
        graphics.nextStratum();

        boolean isHoveringOver = false;
        int originX = (int) Math.floor(accessor.refreshed_advancements$getOriginX());
        int originY = (int) Math.floor(accessor.refreshed_advancements$getOriginY());
        if (mouseX > 0 && mouseX < getWidth() && mouseY > 0 && mouseY < getHeight()) {
            for (AdvancementWidget advancementWidget : accessor.refreshed_advancements$getWidgets().values()) {
                if (advancementWidget.isMouseOver(originX, originY, mouseX, mouseY)) {
                    isHoveringOver = true;

                    //? >=26.3 {
                    /*advancementWidget.extractHover(graphics, originX, originY, fade, x, y, width - getScrollAreaWidth());
                    *///?} else {
                    advancementWidget.extractHover(graphics, originX, originY, fade, x, y);
                     //?}

                    break;
                }
            }
        }

        graphics.pose().popMatrix();

        if (isHoveringOver) {
            fade = Math.clamp(fade + 0.02F, 0.0F, 0.3F);
        } else {
            fade = Math.clamp(fade - 0.04F, 0.0F, 1.0F);
        }
    }

    private void recalculateTabCenter(AdvancementTab advancementTab, boolean force) {
        if (advancementTab == null) return;

        AdvancementTabAccessor accessor = ((AdvancementTabAccessor) advancementTab);

        if (!accessor.refreshed_advancements$isInitialized() || force) {
            double viewWidth = this.getWidth() - getScrollAreaWidth();
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
    }

    private void drawAdvancementTabContent(AdvancementTab advancementTab, GuiGraphicsExtractor graphics, int xOffset) {
        AdvancementTabAccessor accessor = ((AdvancementTabAccessor) advancementTab);

        recalculateTabCenter(advancementTab, false);

        graphics.enableScissor(xOffset + 2, 0, width, height);

        graphics.pose().pushMatrix();
        graphics.pose().translate(xOffset, 0);

        int x = (int) Math.floor(accessor.refreshed_advancements$getOriginX());
        int y = (int) Math.floor(accessor.refreshed_advancements$getOriginY());

        accessor.refreshed_advancements$getRootWidget().extractConnectivity(graphics, x, y, true);
        accessor.refreshed_advancements$getRootWidget().extractConnectivity(graphics, x, y, false);
        accessor.refreshed_advancements$getRootWidget().extractRenderState(graphics, x, y);

        graphics.pose().popMatrix();

        graphics.disableScissor();
        graphics.nextStratum();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubleClick) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();

        if (modConfigButton.isMouseOver(mouseX, mouseY)) return modConfigButton.mouseClicked(click, doubleClick);

        if (mouseX < getScrollAreaWidth()) {
            return tabWidgetList.mouseClicked(click, doubleClick, mouseX, mouseY, button);
        }

        if (button == InputConstants.MOUSE_BUTTON_LEFT && mouseX > getScrollAreaWidth()) {
            this.setMovingTab(true);
            return true;
        }

        return super.mouseClicked(click, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent click, double deltaX, double deltaY) {
        double mouseX = click.x();
        int button = click.button();

        if (button != InputConstants.MOUSE_BUTTON_LEFT || !this.isMovingTab() || mouseX < getScrollAreaWidth()) {
            this.setMovingTab(false);
            return super.mouseDragged(click, deltaX, deltaY);
        }

        AdvancementTab tab = this.getSelectedTab();
        if (tab != null) {
            this.moveTabContent(tab,
                    deltaX * RefreshedAdvancementsConfig.getHorizontalDragMultiplier(),
                    deltaY * RefreshedAdvancementsConfig.getVerticalDragMultiplier()
            );
        }

        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        int button = click.button();

        if (button == 0 && this.isMovingTab()) {
            this.setMovingTab(false);
            return true;
        }

        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseX > getScrollAreaWidth()) {
            moveTabContent(getSelectedTab(), horizontalAmount * getHorizontalScrollAmount(), verticalAmount * getVerticalScrollAmount());
            return true;
        }

        return tabWidgetList.mouseScrolled(mouseX, mouseY, verticalAmount * getVerticalScrollAmount());
    }

    public void moveTabContent(AdvancementTab tab, double offsetX, double offsetY) {
        AdvancementTabAccessor accessor = (AdvancementTabAccessor) tab;

        if (accessor == null) return;

        double minX = accessor.refreshed_advancements$getMinPanX() - 10;
        double maxX = accessor.refreshed_advancements$getMaxPanX() + 10;
        double minY = accessor.refreshed_advancements$getMinPanY() - 10;
        double maxY = accessor.refreshed_advancements$getMaxPanY() + 10;

        double viewWidth = this.getWidth() - getScrollAreaWidth();
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
                Math.clamp(
                        accessor.refreshed_advancements$getOriginX() + offsetX,
                        minOriginX,
                        maxOriginX
                )
        );

        accessor.refreshed_advancements$setOriginY(
                Math.clamp(
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

    public ClientAdvancements getManager() {
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

    public float getHorizontalScrollAmount() {
        return RefreshedAdvancementsConfig.getHorizontalScrollMultiplier();
    }

    public float getVerticalScrollAmount() {
        return RefreshedAdvancementsConfig.getVerticalScrollMultiplier();
    }

    public int getTextColor() {
        return RefreshedAdvancementsConfig.getTextColor();
    }

    private int getTextDropShadowColor() {
        return RefreshedAdvancementsConfig.getTextShadowColor();
    }

    private int getScrollAreaWidth() {
        return RefreshedAdvancementsConfig.getEntriesAreaWidth();
    }
}
