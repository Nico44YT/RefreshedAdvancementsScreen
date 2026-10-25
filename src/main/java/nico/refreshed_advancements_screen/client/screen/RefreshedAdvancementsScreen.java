package nico.refreshed_advancements_screen.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import nico.refreshed_advancements_screen.client.screen.widget.RefreshedAdvancementsTabList;
import nico.refreshed_advancements_screen.mixin.client.accessor.AdvancementTabAccessor;
import nico.refreshed_advancements_screen.mixin.client.accessor.AdvancementsScreenAccessor;
import nico.refreshed_advancements_screen.mixin.client.accessor.ScreenAccessor;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class RefreshedAdvancementsScreen extends AdvancementsScreen implements ClientAdvancements.Listener {
    public static final int SCROLL_AREA_WIDTH = 80;

    private static final Component SAD_LABEL_TEXT = Component.translatable("advancements.sad_label");
    private static final Component EMPTY_TEXT = Component.translatable("advancements.empty");
    private static final Component ADVANCEMENTS_TEXT = Component.translatable("gui.advancements");

    private float fade;
    private RefreshedAdvancementsTabList tabWidgetList;

    public RefreshedAdvancementsScreen(ClientAdvancements advancementHandler) {
        super(advancementHandler);
    }

    @Override
    protected void init() {
        this.tabWidgetList = new RefreshedAdvancementsTabList(this, 0, 0, SCROLL_AREA_WIDTH, height);

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

        for (Renderable drawable : ((ScreenAccessor) this).refreshed_advancements$getDrawables()) {
            drawable.extractRenderState(graphics, mouseX, mouseY, delta);
        }

        if (this.isMovingTab() && this.getSelectedTab() != null && mouseX > SCROLL_AREA_WIDTH) {
            if (this.getSelectedTab().canScrollHorizontally() && this.getSelectedTab().canScrollVertically()) {
                graphics.requestCursor(CursorTypes.RESIZE_ALL);
            } else if (this.getSelectedTab().canScrollHorizontally()) {
                graphics.requestCursor(CursorTypes.RESIZE_EW);
            } else if (this.getSelectedTab().canScrollVertically()) {
                graphics.requestCursor(CursorTypes.RESIZE_NS);
            }
        }

        graphics.guiRenderState.nextStratum();
        graphics.verticalLine(SCROLL_AREA_WIDTH, -1, height + 1, 0xBF_00_00_00);
        graphics.verticalLine(SCROLL_AREA_WIDTH + 1, -1, height + 1, 0x33_FF_FF_FF);
        graphics.fill(2, 0, SCROLL_AREA_WIDTH, height, 0x70_00_00_00);

        graphics.guiRenderState.nextStratum();
        this.tabWidgetList.extractRenderState(graphics, mouseX, mouseY, delta);

        graphics.centeredText(font, ADVANCEMENTS_TEXT, SCROLL_AREA_WIDTH / 2 - 1, 3, getTextColor());

        graphics.guiRenderState.nextStratum();
        this.drawAdvancementTree(graphics, mouseX, mouseY, SCROLL_AREA_WIDTH);
        graphics.guiRenderState.nextStratum();
        this.drawWidgetTooltip(graphics, mouseX, mouseY, SCROLL_AREA_WIDTH, 0);
    }

    private void drawAdvancementTree(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x) {
        AdvancementTab advancementTab = getSelectedTab();
        if (advancementTab == null) {
            int centerX = (getWidth() + x) / 2;
            int centerY = (getHeight() - font.lineHeight) / 2;

            graphics.fill(x, 0, getWidth(), getHeight(), 0x55_00_00_00);

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
        graphics.pose().translate(SCROLL_AREA_WIDTH, 0.0F);

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
                    /*advancementWidget.extractHover(graphics, originX, originY, fade, x, y, width - SCROLL_AREA_WIDTH);
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
        if(advancementTab == null) return;

        AdvancementTabAccessor accessor = ((AdvancementTabAccessor) advancementTab);

        if (!accessor.refreshed_advancements$isInitialized() || force) {
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
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubleClick) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        if (button == InputConstants.MOUSE_BUTTON_LEFT && mouseX > SCROLL_AREA_WIDTH) {
            this.setMovingTab(true);
            return true;
        }

        if (mouseX < SCROLL_AREA_WIDTH) {
            return tabWidgetList.mouseClicked(click, doubleClick, mouseX, mouseY, button);
        }

        return super.mouseClicked(click, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent click, double deltaX, double deltaY) {
        double mouseX = click.x();
        int button = click.button();
        if (button != InputConstants.MOUSE_BUTTON_LEFT || !this.isMovingTab() || mouseX < SCROLL_AREA_WIDTH) {
            this.setMovingTab(false);
            return super.mouseDragged(click, deltaX, deltaY);
        }

        AdvancementTab tab = this.getSelectedTab();
        if (tab != null) {
            this.moveTabContent(tab, deltaX, deltaY);
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
        if (mouseX > SCROLL_AREA_WIDTH) {
            moveTabContent(getSelectedTab(), horizontalAmount * getHorizontalScrollAmount(), verticalAmount * getVerticalScrollAmount());
            return true;
        } else {
            tabWidgetList.mouseScrolled(mouseX, mouseY, verticalAmount * getVerticalScrollAmount());
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    public void moveTabContent(AdvancementTab tab, double offsetX, double offsetY) {
        AdvancementTabAccessor accessor = (AdvancementTabAccessor) tab;

        if (accessor == null) return;

        double minX = accessor.refreshed_advancements$getMinPanX() - 10;
        double maxX = accessor.refreshed_advancements$getMaxPanX() + 10;
        double minY = accessor.refreshed_advancements$getMinPanY() - 10;
        double maxY = accessor.refreshed_advancements$getMaxPanY() + 10;

        double viewWidth = this.width - SCROLL_AREA_WIDTH;
        double viewHeight = this.height;

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

    public int getHorizontalScrollAmount() {
        return 12;
    }

    public int getVerticalScrollAmount() {
        return 12;
    }

    public int getTextColor() {
        return CommonColors.WHITE;
    }

    private int getTextDropShadowColor() {
        return CommonColors.DARK_GRAY;
    }
}
