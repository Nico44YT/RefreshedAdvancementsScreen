package nico.refreshed_advancements_screen.client.screen.widget;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import nico.refreshed_advancements_screen.client.screen.RefreshedAdvancementsScreen;
import nico.refreshed_advancements_screen.client.screen.util.ScissorStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RefreshedAdvancementsTabList implements Drawable {
    public static final int BUTTON_MARGIN = 2;
    public static final int BUTTON_HEIGHT = 18;
    public static final int TITLE_AREA_HEIGHT = 13;

    private static final int SCROLLBAR_WIDTH = 2;
    private static final int SCROLLBAR_MIN_HEIGHT = 8;

    public final List<ClickableWidget> widgets;
    public final RefreshedAdvancementsScreen screen;

    public int x;
    public int y;
    public int width;
    public int height;

    public double scrollAmount = 0;

    public RefreshedAdvancementsTabList(RefreshedAdvancementsScreen screen, int x0, int y0, int x1, int y1) {
        this.x = x0;
        this.y = y0;

        this.width = x1 - x0;
        this.height = y1 - y0;

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
                widget -> screen.setSelectedTab(tab)
        );

        widgets.add(button);
    }

    private int getContentHeight() {
        if (widgets.isEmpty()) {
            return 0;
        }

        ClickableWidget lastWidget = widgets.get(widgets.size() - 1);
        return lastWidget.y + lastWidget.getHeight();
    }

    private double getMaxScroll() {
        return Math.max(0, getContentHeight() - height + TITLE_AREA_HEIGHT / 3);
    }

    private void setScrollAmount(double amount) {
        scrollAmount = Math.max(0, Math.min(amount, getMaxScroll()));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
        drawScrollbar(matrices);

        matrices.push();
        matrices.translate(1, -scrollAmount, 0);


        ScissorStack.enableScissoring(
                x,
                TITLE_AREA_HEIGHT,
                width,
                height
        );
        for (ClickableWidget widget : widgets) {
            widget.render(matrices, mouseX, mouseY, tickDelta);
        }

        ScissorStack.enableScissoring(
                (float) (x + 16),
                (float) (y),
                width - 16,
                height
        );

        for (ClickableWidget widget : widgets) {
            if(widget instanceof RefreshedAdvancementsButton button) button.renderText(matrices, mouseX, mouseY, tickDelta);
        }

        matrices.pop();
    }

    private void drawScrollbar(MatrixStack matrices) {
        int contentHeight = getContentHeight();
        int viewportHeight = height - TITLE_AREA_HEIGHT;

        if (contentHeight <= viewportHeight || viewportHeight <= 0) {
            DrawableHelper.fill(matrices, 0, 0, SCROLLBAR_WIDTH, height, 0x70_00_00_00);
            return;
        }

        int scrollbarX = x;
        int scrollbarY = y + TITLE_AREA_HEIGHT;
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

        DrawableHelper.fill(matrices, 0, 0, SCROLLBAR_WIDTH, scrollbarY + thumbOffset, 0x70_00_00_00);
        DrawableHelper.fill(matrices, 0, scrollbarY + thumbOffset + scrollbarHeight, SCROLLBAR_WIDTH, height, 0x70_00_00_00);

        DrawableHelper.fill(
                matrices,
                scrollbarX,
                scrollbarY + thumbOffset,
                scrollbarX + SCROLLBAR_WIDTH,
                scrollbarY + thumbOffset + scrollbarHeight,
                0x33_FF_FF_FF
        );

    }

    public boolean mouseScrolled(double mouseX, double mouseY, double verticalAmount) {
        if (mouseX < x || mouseX >= x + width
                || mouseY < y || mouseY >= y + height) {
            return false;
        }

        if (getMaxScroll() <= 0) {
            return false;
        }

        setScrollAmount(scrollAmount - verticalAmount);
        return true;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX < x || mouseX >= x + width
                || mouseY < y || mouseY >= y + height) {
            return false;
        }

        double adjustedMouseY = mouseY + scrollAmount;

        for (ClickableWidget widget : this.widgets) {
            if (widget.isMouseOver(mouseX, adjustedMouseY)) {
                widget.onClick(mouseX, adjustedMouseY);
                return true;
            }
        }

        return false;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void forEachChild(Consumer<ClickableWidget> consumer) {
        this.widgets.forEach(consumer);
    }

    public RefreshedAdvancementsScreen getScreen() {
        return this.screen;
    }
}