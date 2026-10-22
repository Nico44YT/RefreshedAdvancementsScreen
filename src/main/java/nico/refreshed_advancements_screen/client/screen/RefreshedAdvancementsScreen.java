package nico.refreshed_advancements_screen.client.screen;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.MathHelper;
import nico.refreshed_advancements_screen.client.screen.widget.RefreshedAdvancementsTabList;
import nico.refreshed_advancements_screen.mixin.client.accessor.AdvancementTabAccessor;
import nico.refreshed_advancements_screen.mixin.client.accessor.ScreenAccessor;
import org.jetbrains.annotations.Nullable;

//? >= 1.20.2 {
// import net.minecraft.advancement.AdvancementEntry;
// import net.minecraft.advancement.PlacedAdvancement;
//?}

import java.util.Map;

public class RefreshedAdvancementsScreen extends AdvancementsScreen implements ClientAdvancementManager.Listener {
    protected static final int SCROLL_AREA_WIDTH = 80;

    protected final ClientAdvancementManager manager;

    private static final Text SAD_LABEL_TEXT = Text.translatable("advancements.sad_label");
    private static final Text EMPTY_TEXT = Text.translatable("advancements.empty");
    private static final Text ADVANCEMENTS_TEXT = Text.translatable("gui.advancements");

    //? = 1.20 {
     private final Map<Advancement, AdvancementTab> tabs = Maps.newLinkedHashMap();
    //?} else{
    /*private final Map<AdvancementEntry, AdvancementTab> tabs = Maps.newLinkedHashMap();
    *///?}

    @Nullable
    private AdvancementTab selectedTab;
    private boolean movingTab;

    private RefreshedAdvancementsTabList tabWidgetList;

    public RefreshedAdvancementsScreen(ClientAdvancementManager advancementHandler) {
        super(advancementHandler);

        this.manager = advancementHandler;
    }

    @Override
    protected void init() {
        this.tabWidgetList = new RefreshedAdvancementsTabList(this, 0, 0, SCROLL_AREA_WIDTH, height);

        this.tabs.clear();
        this.selectedTab = null;
        this.manager.setListener(this);
        if (this.selectedTab == null && !this.tabs.isEmpty()) {
            AdvancementTab advancementTab = this.tabs.values().iterator().next();

            //? = 1.20 {
             this.manager.selectTab(advancementTab.getRoot().getRoot(), true);
            //?} else {
            /*this.manager.selectTab(advancementTab.getRoot().getAdvancementEntry(), true);
            *///?}

        } else {
            //? = 1.20 {
             this.manager.selectTab(this.selectedTab == null ? null : this.selectedTab.getRoot().getRoot(), true);
            //?} else {
            /*this.manager.selectTab(this.selectedTab == null ? null : this.selectedTab.getRoot().getAdvancementEntry(), true);
            *///?}
        }

        this.tabs.values().forEach(tab -> {
            this.tabWidgetList.addAdvancementTab(tab);
        });
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        //? =1.20 {
         this.renderBackground(context);
        //?} else {
        /*this.renderBackground(context, mouseX, mouseY, delta);
        *///?}


        for (Drawable drawable : ((ScreenAccessor) this).refreshed_advancements$getDrawables()) {
            drawable.render(context, mouseX, mouseY, delta);
        }

        context.drawTextWithShadow(textRenderer, ADVANCEMENTS_TEXT, (SCROLL_AREA_WIDTH - textRenderer.getWidth(ADVANCEMENTS_TEXT)) / 2, 3, 0xFF_FF_FF_FF);

        context.drawVerticalLine(SCROLL_AREA_WIDTH, -1, height + 1, 0xBF_00_00_00);
        context.drawVerticalLine(SCROLL_AREA_WIDTH + 1, -1, height + 1, 0x33_FF_FF_FF);
        context.fill(2, 0, SCROLL_AREA_WIDTH, height, 0x70_00_00_00);

        this.tabWidgetList.draw(context, mouseX, mouseY, delta);

        this.drawAdvancementTree(context, mouseX, mouseY, SCROLL_AREA_WIDTH);
        this.drawWidgetTooltip(context, mouseX, mouseY, SCROLL_AREA_WIDTH, 0);
    }

    @Override
    public void removed() {
        this.manager.setListener(null);
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
        if (clientPlayNetworkHandler != null) {
            clientPlayNetworkHandler.sendPacket(AdvancementTabC2SPacket.close());
        }
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
        if (this.selectedTab != null) {
            context.getMatrices().push();
            context.getMatrices().translate(x, y, 400.0F);
            RenderSystem.enableDepthTest();
            drawAdvancementWidgetTooltip(selectedTab, context, mouseX - x, mouseY - y, x, y);
            RenderSystem.disableDepthTest();
            context.getMatrices().pop();
        }

//        if (this.tabs.size() > 1) {
//            for (AdvancementTab advancementTab : this.tabs.values()) {
//                if (advancementTab.isClickOnTab(x, y, mouseX, mouseY)) {
//                    context.drawTooltip(this.textRenderer, advancementTab.getTitle(), mouseX, mouseY);
//                }
//            }
//        }
    }

    public void drawAdvancementWidgetTooltip(AdvancementTab tab, DrawContext context, int mouseX, int mouseY, int x, int y) {
        AdvancementTabAccessor accessor = ((AdvancementTabAccessor) tab);

        context.getMatrices().push();
        context.getMatrices().translate(0.0F, 0.0F, -200.0F);
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

        context.getMatrices().pop();
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

        context.getMatrices().push();
        context.getMatrices().translate(xOffset, 0, 0.0F);
        int x = MathHelper.floor(accessor.refreshed_advancements$getOriginX());
        int y = MathHelper.floor(accessor.refreshed_advancements$getOriginY());

        accessor.refreshed_advancements$getRootWidget().renderLines(context, x, y, true);
        accessor.refreshed_advancements$getRootWidget().renderLines(context, x, y, false);
        accessor.refreshed_advancements$getRootWidget().renderWidgets(context, x, y);
        context.getMatrices().pop();
        context.disableScissor();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX > SCROLL_AREA_WIDTH) {
            this.movingTab = true;
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
        if (button != 0 || !this.movingTab || mouseX < SCROLL_AREA_WIDTH) {
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
        if (button == 0 && this.movingTab) {
            this.movingTab = false;
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    //? =1.20 {
        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
            if(mouseX > SCROLL_AREA_WIDTH) {
                moveTabContent(getSelectedTab(), 0, amount * 12);
                return true;
            } else {
                tabWidgetList.mouseScrolled(mouseX, mouseY, amount * 12);
            }
    
            return super.mouseScrolled(mouseX, mouseY, amount);
        }
    //?} else {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseX > SCROLL_AREA_WIDTH) {
            moveTabContent(getSelectedTab(), horizontalAmount * 12, verticalAmount * 12);
            return true;
        } else {
            tabWidgetList.mouseScrolled(mouseX, mouseY, verticalAmount * 12);
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    *///?}

    public void moveTabContent(AdvancementTab tab, double offsetX, double offsetY) {
        AdvancementTabAccessor accessor = (AdvancementTabAccessor) tab;

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
        return selectedTab;
    }

    //region # ClientAdvancementManager.Listener #
    //? =1.20 {
     public void onRootAdded(Advancement root) {
            AdvancementTab advancementTab = AdvancementTab.create(this.client, this, this.tabs.size(), root);
            if (advancementTab != null) {
                this.tabs.put(root, advancementTab);
            }
        }
    
        public void onRootRemoved(Advancement root) {
        }
    
        public void onDependentAdded(Advancement dependent) {
            AdvancementTab advancementTab = this.getTab(dependent);
            if (advancementTab != null) {
                advancementTab.addAdvancement(dependent);
            }
    
        }
    
        public void onDependentRemoved(Advancement dependent) {
        }
    
        public void setProgress(Advancement advancement, AdvancementProgress progress) {
            AdvancementWidget advancementWidget = this.getAdvancementWidget(advancement);
            if (advancementWidget != null) {
                advancementWidget.setProgress(progress);
            }
    
        }
    
        public void selectTab(@Nullable Advancement advancement) {
            this.selectedTab = this.tabs.get(advancement);
        }
    
        public void onClear() {
            this.tabs.clear();
            this.selectedTab = null;
        }
    
        @Nullable
        public AdvancementWidget getAdvancementWidget(Advancement advancement) {
            AdvancementTab advancementTab = this.getTab(advancement);
            return advancementTab == null ? null : advancementTab.getWidget(advancement);
        }
    
        @Nullable
        private AdvancementTab getTab(Advancement advancement) {
            while(advancement.getParent() != null) {
                advancement = advancement.getParent();
            }
    
            return this.tabs.get(advancement);
        }
    //?} else {
    /*public void onRootAdded(PlacedAdvancement root) {
        AdvancementTab advancementTab = AdvancementTab.create(this.client, this, this.tabs.size(), root);
        if (advancementTab != null) {
            this.tabs.put(root.getAdvancementEntry(), advancementTab);
        }
    }

    public void onRootRemoved(PlacedAdvancement root) {
    }

    public void onDependentAdded(PlacedAdvancement dependent) {
        AdvancementTab advancementTab = this.getTab(dependent);
        if (advancementTab != null) {
            advancementTab.addAdvancement(dependent);
        }

    }

    public void onDependentRemoved(PlacedAdvancement dependent) {
    }

    public void setProgress(PlacedAdvancement advancement, AdvancementProgress progress) {
        AdvancementWidget advancementWidget = this.getAdvancementWidget(advancement);
        if (advancementWidget != null) {
            advancementWidget.setProgress(progress);
        }

    }

    public void selectTab(@Nullable AdvancementEntry advancement) {
        this.selectedTab = this.tabs.get(advancement);
    }

    public void onClear() {
        this.tabs.clear();
        this.selectedTab = null;
    }

    @Nullable
    public AdvancementWidget getAdvancementWidget(PlacedAdvancement advancement) {
        AdvancementTab advancementTab = this.getTab(advancement);
        return advancementTab == null ? null : advancementTab.getWidget(advancement.getAdvancementEntry());
    }

    @Nullable
    private AdvancementTab getTab(PlacedAdvancement advancement) {
        PlacedAdvancement placedAdvancement = advancement.getRoot();
        return (AdvancementTab)this.tabs.get(placedAdvancement.getAdvancementEntry());
    }
    *///?}
    //endregion
}
