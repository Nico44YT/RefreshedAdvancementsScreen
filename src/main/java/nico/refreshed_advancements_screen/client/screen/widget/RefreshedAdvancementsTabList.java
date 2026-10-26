package nico.refreshed_advancements_screen.client.screen.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import nico.refreshed_advancements_screen.client.config.RefreshedAdvancementsConfig;
import nico.refreshed_advancements_screen.client.screen.RefreshedAdvancementsScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RefreshedAdvancementsTabList extends AbstractWidget {
    public final List<RefreshedAdvancementsButton> widgets;
    public final RefreshedAdvancementsScreen screen;

    private double scrollAmount = 0;

    public RefreshedAdvancementsTabList(RefreshedAdvancementsScreen screen, int x0, int y0, int x1, int y1) {
        super(x0, y0, x1 - x0, y1 - y0, Component.empty());

        this.widgets = new ArrayList<>();
        this.screen = screen;
    }

    public void addAdvancementTab(AdvancementTab tab) {
        int movedY = (RefreshedAdvancementsConfig.getEntryHeight() + RefreshedAdvancementsConfig.getEntryMargin()) * this.widgets.size() + getTitleAreaHeight();

        RefreshedAdvancementsButton button = new RefreshedAdvancementsButton(
                this,
                RefreshedAdvancementsConfig.getEntryMargin(),
                movedY,
                width - RefreshedAdvancementsConfig.getEntryMargin() * 2,
                RefreshedAdvancementsConfig.getEntryHeight(),
                tab,
                widget -> screen.setSelectedTab(tab),
                Supplier::get
        );

        widgets.add(button);
    }

    private int getContentHeight() {
        if (widgets.isEmpty()) {
            return 0;
        }

        RefreshedAdvancementsButton lastWidget = widgets.getLast();
        return lastWidget.getY() + lastWidget.getHeight();
    }

    private double getMaxScroll() {
        return Math.max(0, getContentHeight() - height + getTitleAreaHeight() / 3);
    }

    private void setScrollAmount(double amount) {
        scrollAmount = Math.clamp(amount, 0, getMaxScroll());
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        graphics.pose().pushMatrix();

        drawScrollbar(graphics);

        graphics.enableScissor(getX(), getTitleAreaHeight(), getX() + getWidth(), getY() + getHeight());

        graphics.pose().pushMatrix();
        graphics.pose().translate(RefreshedAdvancementsConfig.getScrollbarEntriesSpacing(), (float)-scrollAmount);

        for (RefreshedAdvancementsButton widget : widgets) {
            graphics.pose().pushMatrix();
            widget.extractRenderState(graphics, mouseX, mouseY, tickDelta);
            graphics.pose().popMatrix();
        }
        graphics.disableScissor();

        graphics.pose().popMatrix();
        graphics.nextStratum();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {

    }

    private void drawScrollbar(GuiGraphicsExtractor graphics) {
        int contentHeight = getContentHeight();
        int viewportHeight = getHeight() - getTitleAreaHeight();

        int backgroundColor = RefreshedAdvancementsConfig.getScrollAreaColor();
        int scrollbarColor = RefreshedAdvancementsConfig.getScrollbarColor();

        if (contentHeight <= viewportHeight || viewportHeight <= 0) {
            graphics.fill(0, 0, RefreshedAdvancementsConfig.getScrollbarWidth(), getHeight(), backgroundColor);
            return;
        }

        int scrollbarX = getX();
        int scrollbarY = getY() + getTitleAreaHeight();
        int scrollbarHeight = Math.max(
                RefreshedAdvancementsConfig.getScrollbarMinHeight(),
                (int) ((double) viewportHeight * viewportHeight / contentHeight)
        );

        int scrollbarTrackHeight = viewportHeight;
        int maxThumbOffset = scrollbarTrackHeight - scrollbarHeight;

        double maxScroll = getMaxScroll();
        int thumbOffset = maxScroll > 0
                ? (int) (scrollAmount / maxScroll * maxThumbOffset)
                : 0;

        graphics.fill(0, 0, RefreshedAdvancementsConfig.getScrollbarWidth(), scrollbarY + thumbOffset, backgroundColor);
        graphics.fill(0, scrollbarY + thumbOffset + scrollbarHeight, RefreshedAdvancementsConfig.getScrollbarWidth(), getHeight(), backgroundColor);

        int scrollbarWidth = RefreshedAdvancementsConfig.getScrollbarWidth();
        int scrollbarFadeHeight = Mth.clamp(RefreshedAdvancementsConfig.getScrollbarFadeHeight(), 0, scrollbarHeight/2);

        graphics.fillGradient(scrollbarX,
                scrollbarY + thumbOffset,
                scrollbarX + scrollbarWidth,
                scrollbarY + thumbOffset + scrollbarFadeHeight,
                backgroundColor, scrollbarColor
                );

        graphics.fill(
                scrollbarX,
                scrollbarY + thumbOffset + scrollbarFadeHeight,
                scrollbarX + scrollbarWidth,
                scrollbarY + thumbOffset + scrollbarHeight - scrollbarFadeHeight,
                scrollbarColor
        );

        graphics.fillGradient(scrollbarX,
                scrollbarY + thumbOffset + scrollbarHeight - scrollbarFadeHeight,
                scrollbarX + scrollbarWidth,
                scrollbarY + thumbOffset + scrollbarHeight,
                scrollbarColor, backgroundColor
        );


    }

    public boolean mouseScrolled(double mouseX, double mouseY, double verticalAmount) {
        if (mouseX < getX() || mouseX >= getX() + getWidth()
                || mouseY < getY() || mouseY >= getY() + getHeight()) {
            return false;
        }

        if (getMaxScroll() <= 0) {
            return false;
        }

        setScrollAmount(scrollAmount - verticalAmount);
        return true;
    }

    public boolean mouseClicked(MouseButtonEvent click, boolean doubled, double mouseX, double mouseY, int button) {
        if (mouseX < getX() || mouseX >= getX() + getWidth()
                || mouseY < getY() || mouseY >= getY() + getHeight()) {
            return false;
        }

        double adjustedMouseY = mouseY + scrollAmount;

        for (RefreshedAdvancementsButton widget : this.widgets) {
            if (widget.isMouseOver(mouseX, adjustedMouseY)) {
                widget.onClick(new MouseButtonEvent(
                        mouseX, adjustedMouseY, click.buttonInfo()
                ), doubled);
                return true;
            }
        }

        return false;
    }

    public RefreshedAdvancementsScreen getScreen() {
        return this.screen;
    }

    private int getTitleAreaHeight() {
        return RefreshedAdvancementsConfig.shouldDrawAdvancementsTitle() ? 13 : 2;
    }
}