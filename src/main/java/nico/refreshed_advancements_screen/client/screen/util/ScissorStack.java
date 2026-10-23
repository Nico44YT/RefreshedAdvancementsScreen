package nico.refreshed_advancements_screen.client.screen.util;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

import java.util.Deque;

public class ScissorStack {
    private static final Deque<Entry> ENTRIES = Queues.newArrayDeque();

    public static void applyAll() {
        ENTRIES.forEach(Entry::apply);
    }

    public static void applyCurrent() {
        if(ENTRIES.isEmpty()) return;
        ENTRIES.peekLast().apply();
    }

    protected static void pushGl(int x, int y, int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Scissor dimensions cannot be negative");
        }

        ENTRIES.addLast(new Entry(x, y, width, height));
    }

    public static void push(float x, float y, float width, float height) {
        Window window = MinecraftClient.getInstance().getWindow();

        double scale = window.getScaleFactor();
        int framebufferHeight = window.getFramebufferHeight();

        int scissorX = (int) (x * scale);
        int scissorY = framebufferHeight - (int) ((y + height) * scale);
        int scissorWidth = (int) (width * scale);
        int scissorHeight = (int) (height * scale);

        pushGl(scissorX, scissorY, scissorWidth, scissorHeight);
    }

    public static void pop() {
        if (ENTRIES.isEmpty()) return;

        ENTRIES.pollLast();

        if (ENTRIES.isEmpty()) {
            RenderSystem.disableScissor();
        } else {
            ENTRIES.peekLast().apply();
        }
    }

    public static void enableScissoring(float x, float y, float width, float height) {
        Window window = MinecraftClient.getInstance().getWindow();

        double scale = window.getScaleFactor();
        int framebufferHeight = window.getFramebufferHeight();

        int scissorX = (int) (x * scale);
        int scissorY = framebufferHeight - (int) ((y + height) * scale);
        int scissorWidth = (int) (width * scale);
        int scissorHeight = (int) (height * scale);

        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
    }

    public static class Entry {
        private final int x, y, width, height;

        public Entry(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
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

        public Entry apply() {
            RenderSystem.enableScissor(x, y, width, height);
            return this;
        }
    }
}
