package nico.refreshed_advancements_screen.client.screen.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import nico.refreshed_advancements_screen.client.RefreshedAdvancementsClient;
import nico.refreshed_advancements_screen.client.config.RefreshedAdvancementsConfig;

import java.util.function.Supplier;

import static nico.refreshed_advancements_screen.client.RefreshedAdvancementsClient.lerpColors;

public class ModConfigButton extends Button {
    private static final Identifier ICON_TEXTURE = RefreshedAdvancementsClient.id("textures/gui/settings.png");

    private final Screen parent;
    private static final Supplier<Integer> SIZE = RefreshedAdvancementsConfig::getConfigButtonSize;
    private static final Supplier<Integer> MARGIN = () -> SIZE.get() / 2 + RefreshedAdvancementsConfig.getConfigButtonMargin();

    private float fade;
    private boolean isHoveringOver;

    public ModConfigButton(Screen parent, Position position, int middleX, int middleY, int screenWidth, int screenHeight, OnPress onPress) {
        super(middleX - SIZE.get() / 2 - (MARGIN.get() - screenWidth / 2) * position.getShiftX(),
                middleY - SIZE.get() / 2 + (MARGIN.get() - screenHeight / 2) * position.getShiftY(),
                SIZE.get(), SIZE.get(), Component.translatable("widget.refreshed_advancements.config"), onPress, $ -> Component.translatable("widget.refreshed_advancements.config"));

        this.parent = parent;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        this.isHoveringOver = isMouseOver(mouseX, mouseY);

        graphics.pose().pushMatrix();

        graphics.pose().translate(getX(), getY());

        graphics.fill(0, 0, getWidth(), getHeight(), lerpColors(
                RefreshedAdvancementsConfig.getConfigButtonColor(),
                RefreshedAdvancementsConfig.getConfigButtonHoverColor(),
                this.fade
        ));

        graphics.nextStratum();

        graphics.blit(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, 1, 1, 0, 0,
                getWidth() - 2, getHeight() - 2, getWidth() - 2, getHeight() - 2,
                lerpColors(
                        RefreshedAdvancementsConfig.getConfigButtonIconColor(),
                        RefreshedAdvancementsConfig.getConfigButtonIconHoverColor(),
                        this.fade
                )
        );

        graphics.pose().popMatrix();

        if (isHoveringOver) {
            fade = Math.clamp(fade + 0.2f, 0, 0.3f);
        } else {
            fade = Math.clamp(fade - 0.2f, 0, 1.0f);
        }
    }

    public enum Position {
        TOP_RIGHT(1, 1),
        BOTTOM_RIGHT(1, -1),
        TOP_LEFT(-1, 1),
        BOTTOM_LEFT(-1, -1),
        NONE(0, 0);

        private final int shiftX;
        private final int shiftY;

        Position(int shiftX, int shiftY) {
            this.shiftX = shiftX;
            this.shiftY = shiftY;
        }

        public int getShiftX() {
            return this.shiftX;
        }

        public int getShiftY() {
            return this.shiftY;
        }
    }
}
