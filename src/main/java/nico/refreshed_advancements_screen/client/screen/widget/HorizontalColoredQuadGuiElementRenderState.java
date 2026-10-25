package nico.refreshed_advancements_screen.client.screen.widget;

//? >=26.3 {
/*import com.mojang.renderpearl.api.pipeline.RenderPipeline;
*///?} else {
import com.mojang.blaze3d.pipeline.RenderPipeline;
//?}
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

public record HorizontalColoredQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup,
                                                         Matrix3x2f pose, int x0, int y0, int x1, int y1, int col1,
                                                         int col2, @Nullable ScreenRectangle scissorArea,
                                                         @Nullable ScreenRectangle bounds) implements GuiElementRenderState {
    public HorizontalColoredQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x0, int y0, int x1, int y1, int col1, int col2, @Nullable ScreenRectangle scissorArea) {
        this(pipeline, textureSetup, pose, x0, y0, x1, y1, col1, col2, scissorArea, createBounds(x0, y0, x1, y1, pose, scissorArea));
    }

    @Override
    public void buildVertices(VertexConsumer vertices) {
        vertices.addVertexWith2DPose(this.pose(), (float) this.x0(), (float) this.y0()).setColor(this.col1());
        vertices.addVertexWith2DPose(this.pose(), (float) this.x0(), (float) this.y1()).setColor(this.col1());
        vertices.addVertexWith2DPose(this.pose(), (float) this.x1(), (float) this.y1()).setColor(this.col2());
        vertices.addVertexWith2DPose(this.pose(), (float) this.x1(), (float) this.y0()).setColor(this.col2());
    }

    @Nullable
    private static ScreenRectangle createBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea) {
        ScreenRectangle screenRect = (new ScreenRectangle(x0, y0, x1 - x0, y1 - y0)).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }

    static void fillGradient(GuiGraphicsExtractor graphics, int startX, int startY, int endX, int endY, int z, int colorStart, int colorEnd) {
        graphics.guiRenderState.addGuiElement(new HorizontalColoredQuadGuiElementRenderState(RenderPipelines.GUI, TextureSetup.noTexture(), new Matrix3x2f(graphics.pose()), startX, startY, endX, endY, colorStart, colorEnd, graphics.scissorStack.peek()));
    }
}