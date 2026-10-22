package nico.refreshed_advancements_screen.client.screen.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import nico.refreshed_advancements_screen.client.screen.RefreshedAdvancementsScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RefreshedAdvancementsTabList implements Widget {
    public static final int BUTTON_MARGIN = 2;
    public static final int BUTTON_HEIGHT = 18;
    public static final int SCROLL_AMOUNT = 10;

    public final List<ClickableWidget> widgets;
    public final RefreshedAdvancementsScreen screen;

    public int x;
    public int y;
    public int width;
    public int height;

    private double scrollAmount = 0;

    public RefreshedAdvancementsTabList(RefreshedAdvancementsScreen screen, int x0, int y0, int x1, int y1) {
        this.x = x0;
        this.y = y0;

        this.width = x1 - x0;
        this.height = y1 - y0;

        this.widgets = new ArrayList<>();
        this.screen = screen;
    }

    public void addAdvancementTab(AdvancementTab tab) {
        int movedY = (BUTTON_HEIGHT + BUTTON_MARGIN) * this.widgets.size() + 13;

        RefreshedAdvancementsButton button = new RefreshedAdvancementsButton(
                this,
                BUTTON_MARGIN,
                movedY,
                width - BUTTON_MARGIN * 2,
                BUTTON_HEIGHT,
                tab,
                widget -> screen.selectTab(tab.getRoot().getRoot()),
                Supplier::get
        );

        widgets.add(button);
    }

    private int getContentHeight() {
        if (widgets.isEmpty()) {
            return 0;
        }

        ClickableWidget lastWidget = widgets.get(widgets.size() - 1);
        return lastWidget.getY() + lastWidget.getHeight();
    }

    private double getMaxScroll() {
        return Math.max(0, getContentHeight() - height);
    }

    private void setScrollAmount(double amount) {
        scrollAmount = Math.max(0, Math.min(amount, getMaxScroll()));
    }

    public void draw(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        context.enableScissor(x, y, x + width, y + height);

        var matrices = context.getMatrices();
        matrices.push();
        matrices.translate(0, -scrollAmount, 0);

        for (ClickableWidget widget : widgets) {
            widget.render(context, mouseX, mouseY, tickDelta);
        }

        matrices.pop();
        context.disableScissor();
    }



    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseX < x || mouseX >= x + width
                || mouseY < y || mouseY >= y + height) {
            return false;
        }

        if (getMaxScroll() <= 0) {
            return false;
        }

        setScrollAmount(scrollAmount - verticalAmount * SCROLL_AMOUNT);
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

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {
        this.widgets.forEach(consumer);
    }

    public RefreshedAdvancementsScreen getScreen() {
        return this.screen;
    }
}