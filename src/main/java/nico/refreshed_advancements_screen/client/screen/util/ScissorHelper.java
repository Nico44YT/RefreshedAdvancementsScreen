package nico.refreshed_advancements_screen.client.screen.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.lwjgl.opengl.GL11;

public class ScissorHelper {

    public static void enableScissor(float x, float y, float width, float height) {
        Window window = MinecraftClient.getInstance().window;

        double scale = window.getScaleFactor();
        int framebufferHeight = window.getFramebufferHeight();

        int scissorX = (int) (x * scale);
        int scissorY = framebufferHeight - (int) ((y + height) * scale);
        int scissorWidth = (int) (width * scale);
        int scissorHeight = (int) (height * scale);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(scissorX, scissorY, scissorWidth, scissorHeight);
    }

    public static void disableScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
