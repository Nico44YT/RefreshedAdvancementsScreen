package nico.refreshed_advancements_screen.client.screen.widget;

//? >=1.21.6 {
/*import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

public record HorizontalColoredQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x0, int y0, int x1, int y1, int col1, int col2, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SimpleGuiElementRenderState {
    public HorizontalColoredQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x0, int y0, int x1, int y1, int col1, int col2, @Nullable ScreenRect scissorArea) {
        this(pipeline, textureSetup, pose, x0, y0, x1, y1, col1, col2, scissorArea, createBounds(x0, y0, x1, y1, pose, scissorArea));
    }

    //? = 1.21.6 {
    /^@Override
    public void setupVertices(VertexConsumer vertices, float depth) {
        vertices.vertex(this.pose(), (float)this.x0(), (float)this.y0(), depth).color(this.col1());
        vertices.vertex(this.pose(), (float)this.x0(), (float)this.y1(), depth).color(this.col1());
        vertices.vertex(this.pose(), (float)this.x1(), (float)this.y1(), depth).color(this.col2());
        vertices.vertex(this.pose(), (float)this.x1(), (float)this.y0(), depth).color(this.col2());
    }
    ^///?}

    //? >=1.21.9 {
    /^@Override
    public void setupVertices(VertexConsumer vertices) {
        vertices.vertex(this.pose(), (float)this.x0(), (float)this.y0()).color(this.col1());
        vertices.vertex(this.pose(), (float)this.x0(), (float)this.y1()).color(this.col1());
        vertices.vertex(this.pose(), (float)this.x1(), (float)this.y1()).color(this.col2());
        vertices.vertex(this.pose(), (float)this.x1(), (float)this.y0()).color(this.col2());
    }
    ^///?}

    @Nullable
    private static ScreenRect createBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRect scissorArea) {
        ScreenRect screenRect = (new ScreenRect(x0, y0, x1 - x0, y1 - y0)).transformEachVertex(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }

    static void fillGradient(DrawContext context, int startX, int startY, int endX, int endY, int z, int colorStart, int colorEnd) {
        context.state.addSimpleElement(new HorizontalColoredQuadGuiElementRenderState(RenderPipelines.GUI, TextureSetup.empty(), new Matrix3x2f(context.getMatrices()), startX, startY, endX, endY, colorStart, colorEnd, context.scissorStack.peekLast()));
    }
}
*///?} else {
public class HorizontalColoredQuadGuiElementRenderState {

}
//?}
