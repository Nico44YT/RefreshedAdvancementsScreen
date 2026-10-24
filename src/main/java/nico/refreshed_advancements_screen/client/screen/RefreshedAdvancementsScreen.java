package nico.refreshed_advancements_screen.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.advancement.Advancement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.MathHelper;
import nico.refreshed_advancements_screen.client.screen.widget.RefreshedAdvancementsTabList;
import nico.refreshed_advancements_screen.mixin.client.accessor.AdvancementTabAccessor;
import nico.refreshed_advancements_screen.mixin.client.accessor.AdvancementsScreenAccessor;
import nico.refreshed_advancements_screen.mixin.client.accessor.ScreenAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
//? >= 1.21.9 {
/*import net.minecraft.client.gui.Click;
*///?}

public class RefreshedAdvancementsScreen extends AdvancementsScreen implements ClientAdvancementManager.Listener {
    protected static final int SCROLL_AREA_WIDTH = 80;

    private static final Text SAD_LABEL_TEXT = Text.translatable("advancements.sad_label");
    private static final Text EMPTY_TEXT = Text.translatable("advancements.empty");
    private static final Text ADVANCEMENTS_TEXT = Text.translatable("gui.advancements");

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
            AdvancementTab advancementTab = this.getTabs().values().iterator().next();

            this.getManager().selectTab(advancementTab.getRoot().getAdvancementEntry(), true);
        } else {
            this.getManager().selectTab(this.getSelectedTab() == null ? null : this.getSelectedTab().getRoot().getAdvancementEntry(), true);
        }

        this.getTabs().values().forEach(tab -> {
            this.tabWidgetList.addAdvancementTab(tab);
        });
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        //? >=1.21.6 {
         /*context.state.clear();
        *///?}
        this.renderBackground(context, mouseX, mouseY, delta);

        for (Drawable drawable : ((ScreenAccessor) this).refreshed_advancements$getDrawables()) {
            drawable.render(context, mouseX, mouseY, delta);
        }

        context.drawVerticalLine(SCROLL_AREA_WIDTH, -1, height + 1, 0xBF_00_00_00);
        context.drawVerticalLine(SCROLL_AREA_WIDTH + 1, -1, height + 1, 0x33_FF_FF_FF);
        context.fill(2, 0, SCROLL_AREA_WIDTH, height, 0x70_00_00_00);

        this.tabWidgetList.draw(context, mouseX, mouseY, delta);

        context.drawTextWithShadow(textRenderer, ADVANCEMENTS_TEXT, (SCROLL_AREA_WIDTH - textRenderer.getWidth(ADVANCEMENTS_TEXT)) / 2, 3, 0xFF_FF_FF_FF);

        this.drawAdvancementTree(context, mouseX, mouseY, SCROLL_AREA_WIDTH);
        this.drawWidgetTooltip(context, mouseX, mouseY, SCROLL_AREA_WIDTH, 0);
    }

    private void drawAdvancementTree(DrawContext context, int mouseX, int mouseY, int x) {
        AdvancementTab advancementTab = getSelectedTab();
        if (advancementTab == null) {
            int centerX = width / 2 + x / 2;
            int centerY = height / 2 - this.textRenderer.fontHeight / 2;

            context.fill(x, 0, width, height, 0x55_00_00_00);
            context.drawCenteredTextWithShadow(this.textRenderer, EMPTY_TEXT, centerX, centerY, Colors.WHITE);
            context.drawCenteredTextWithShadow(this.textRenderer, SAD_LABEL_TEXT, centerX, centerY + this.textRenderer.fontHeight + 2, Colors.WHITE);
        } else {
            drawAdvancementTabContent(advancementTab, context, x);
        }
    }

    private void drawWidgetTooltip(DrawContext context, int mouseX, int mouseY, int x, int y) {
        if (this.getSelectedTab() != null) {
            //? <1.21.6 {
            context.getMatrices().push();
            context.getMatrices().translate(0.0F, 0.0F, 400.0F);
            //?} else {
            /*context.getMatrices().pushMatrix();
            *///?}

            //? <1.21.5 {
            RenderSystem.enableDepthTest();
             //?}
            drawAdvancementWidgetTooltip(this.getSelectedTab(), context, mouseX - x, mouseY - y, x, y);
            //? <1.21.5 {
            RenderSystem.disableDepthTest();
             //?}

            //? <1.21.6 {
            context.getMatrices().pop();
             //?} else {
            /*context.getMatrices().popMatrix();
            *///?}
        }
    }

    public void drawAdvancementWidgetTooltip(AdvancementTab tab, DrawContext context, int mouseX, int mouseY, int x, int y) {
        AdvancementTabAccessor accessor = ((AdvancementTabAccessor) tab);

        //? <1.21.6 {
        context.getMatrices().push();
        context.getMatrices().translate(SCROLL_AREA_WIDTH, 0.0F, -200.0F);
        //?} else {
        /*context.getMatrices().pushMatrix();
        context.getMatrices().translate(SCROLL_AREA_WIDTH, 0.0F);
        *///?}
        context.fill(0, 0, width, height, MathHelper.floor(accessor.refreshed_advancements$getAlpha() * 255f) << 24);
        boolean bl = false;
        int originX = MathHelper.floor(accessor.refreshed_advancements$getOriginX());
        int originY = MathHelper.floor(accessor.refreshed_advancements$getOriginY());
        if (mouseX > 0 && mouseX < width && mouseY > 0 && mouseY < height) {
            for (AdvancementWidget advancementWidget : accessor.refreshed_advancements$getWidgets().values()) {
                if (advancementWidget.shouldRender(originX, originY, mouseX, mouseY)) {
                    bl = true;
                    advancementWidget.drawTooltip(context, originX, originY, accessor.refreshed_advancements$getAlpha(), x, y);
                    break;
                }
            }
        }

        //? <1.21.6 {
        context.getMatrices().pop();
         //?} else {
        /*context.getMatrices().popMatrix();
        *///?}
        float alpha = accessor.refreshed_advancements$getAlpha();
        if (bl) {
            alpha = MathHelper.clamp(alpha + 0.02F, 0.0F, 0.3F);
        } else {
            alpha = MathHelper.clamp(alpha - 0.04F, 0.0F, 1.0F);
        }
        accessor.refreshed_advancements$setAlpha(alpha);
    }

    private void drawAdvancementTabContent(AdvancementTab advancementTab, DrawContext context, int xOffset) {
        AdvancementTabAccessor accessor = ((AdvancementTabAccessor) advancementTab);

        if (!accessor.refreshed_advancements$isInitialized()) {
            double viewWidth = this.width - SCROLL_AREA_WIDTH;
            double viewHeight = this.height;

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

        context.enableScissor(xOffset + 2, 0, width, height);

        //? <1.21.6 {
        context.getMatrices().push();
        context.getMatrices().translate(xOffset, 0, 0.0F);
        //?} else {
        /*context.getMatrices().pushMatrix();
        context.getMatrices().translate(xOffset, 0);
        *///?}


        int x = MathHelper.floor(accessor.refreshed_advancements$getOriginX());
        int y = MathHelper.floor(accessor.refreshed_advancements$getOriginY());

        accessor.refreshed_advancements$getRootWidget().renderLines(context, x, y, true);
        accessor.refreshed_advancements$getRootWidget().renderLines(context, x, y, false);
        accessor.refreshed_advancements$getRootWidget().renderWidgets(context, x, y);

        //? <1.21.6 {
        context.getMatrices().pop();
         //?} else {
        /*context.getMatrices().popMatrix();
        *///?}

        context.disableScissor();
    }

    //? <1.21.9 {
         @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0 && mouseX > SCROLL_AREA_WIDTH) {
                this.setMovingTab(true);
                return true;
            }
    
            if (mouseX < SCROLL_AREA_WIDTH) {
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
            if (tab != null) {
                this.moveTabContent(tab, deltaX, deltaY);
            }
    
            return true;
        }
    
        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            if (button == 0 && this.isMovingTab()) {
                this.setMovingTab(false);
                return true;
            }
    
            return super.mouseReleased(mouseX, mouseY, button);
        }
    //?} else {
    /*@Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        if (button == 0 && mouseX > SCROLL_AREA_WIDTH) {
            this.setMovingTab(true);
            return true;
        }

        if (mouseX < SCROLL_AREA_WIDTH) {
            return tabWidgetList.mouseClicked(click, doubled, mouseX, mouseY, button);
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        if (button != 0 || !this.isMovingTab() || mouseX < SCROLL_AREA_WIDTH) {
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
    public boolean mouseReleased(Click click) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        if (button == 0 && this.isMovingTab()) {
            this.setMovingTab(false);
            return true;
        }

        return super.mouseReleased(click);
    }
    *///?}

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseX > SCROLL_AREA_WIDTH) {
            moveTabContent(getSelectedTab(), horizontalAmount * 12, verticalAmount * 12);
            return true;
        } else {
            tabWidgetList.mouseScrolled(mouseX, mouseY, verticalAmount * 12);
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    public void moveTabContent(AdvancementTab tab, double offsetX, double offsetY) {
        AdvancementTabAccessor accessor = (AdvancementTabAccessor) tab;

        if(accessor == null) return;

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

    public int getScrollAmountHorizontal() {
        return 12;
    }

    public int getScrollAmountVertical() {
        return 12;
    }
}
