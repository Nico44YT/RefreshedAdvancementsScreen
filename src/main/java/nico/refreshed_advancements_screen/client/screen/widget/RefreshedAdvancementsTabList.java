package nico.refreshed_advancements_screen.client.screen.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import nico.refreshed_advancements_screen.client.screen.RefreshedAdvancementsScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RefreshedAdvancementsTabList extends AbstractWidget {
    public static final int BUTTON_MARGIN = 2;
    public static final int BUTTON_HEIGHT = 18;
    public static final int TITLE_AREA_HEIGHT = 13;

    private static final int SCROLLBAR_WIDTH = 2;
    private static final int SCROLLBAR_MIN_HEIGHT = 8;

    public final List<RefreshedAdvancementsButton> widgets;
    public final RefreshedAdvancementsScreen screen;

    private double scrollAmount = 0;

    public RefreshedAdvancementsTabList(RefreshedAdvancementsScreen screen, int x0, int y0, int x1, int y1) {
        super(x0, y0, x1 - x0, y1 - y0, Component.empty());

        this.widgets = new ArrayList<>();
        this.screen = screen;
    }

    public void addAdvancementTab(AdvancementTab tab) {
        int movedY = (BUTTON_HEIGHT + BUTTON_MARGIN) * this.widgets.size() + TITLE_AREA_HEIGHT;

        RefreshedAdvancementsButton button = new RefreshedAdvancementsButton(
                this,
                BUTTON_MARGIN,
                movedY,
                width - BUTTON_MARGIN * 2,
                BUTTON_HEIGHT,
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
        return Math.max(0, getContentHeight() - height + TITLE_AREA_HEIGHT / 3);
    }

    private void setScrollAmount(double amount) {
        scrollAmount = Math.clamp(amount, 0, getMaxScroll());
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        drawScrollbar(graphics);

        graphics.enableScissor(getX(), TITLE_AREA_HEIGHT, getX() + getWidth(), getY() + getHeight());

        var matrices = graphics.pose();
        matrices.pushMatrix();
        matrices.translate(1, (float) -scrollAmount);

        for (RefreshedAdvancementsButton widget : widgets) {
            widget.extractRenderState(graphics, mouseX, mouseY, tickDelta);
        }

        matrices.pushMatrix();
        graphics.disableScissor();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {

    }

    private void drawScrollbar(GuiGraphicsExtractor graphics) {
        int contentHeight = getContentHeight();
        int viewportHeight = height - TITLE_AREA_HEIGHT;

        if (contentHeight <= viewportHeight || viewportHeight <= 0) {
            graphics.fill(0, 0, SCROLLBAR_WIDTH, height, 0x70_00_00_00);
            return;
        }

        int scrollbarX = getX();
        int scrollbarY = getY() + TITLE_AREA_HEIGHT;
        int scrollbarHeight = Math.max(
                SCROLLBAR_MIN_HEIGHT,
                (int) ((double) viewportHeight * viewportHeight / contentHeight)
        );

        int scrollbarTrackHeight = viewportHeight;
        int maxThumbOffset = scrollbarTrackHeight - scrollbarHeight;

        double maxScroll = getMaxScroll();
        int thumbOffset = maxScroll > 0
                ? (int) (scrollAmount / maxScroll * maxThumbOffset)
                : 0;

        graphics.fill(0, 0, SCROLLBAR_WIDTH, scrollbarY + thumbOffset, 0x70_00_00_00);
        graphics.fill(0, scrollbarY + thumbOffset + scrollbarHeight, SCROLLBAR_WIDTH, height, 0x70_00_00_00);

        graphics.fill(
                scrollbarX,
                scrollbarY + thumbOffset,
                scrollbarX + SCROLLBAR_WIDTH,
                scrollbarY + thumbOffset + scrollbarHeight,
                0x33_FF_FF_FF
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
}