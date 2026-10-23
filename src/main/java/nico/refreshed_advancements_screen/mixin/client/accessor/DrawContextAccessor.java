package nico.refreshed_advancements_screen.mixin.client.accessor;

import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DrawContext.class)
public interface DrawContextAccessor {
    //? <1.21.6 {
         @Accessor("vertexConsumers")
        VertexConsumerProvider.Immediate refreshed_advancements$getVertexConsumers();
    //?}
}
