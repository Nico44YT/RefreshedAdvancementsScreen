package nico.refreshed_advancements_screen.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.client.util.math.MatrixStack;
import nico.refreshed_advancements_screen.client.screen.RefreshedAdvancementsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AdvancementWidget.class)
public abstract class AdvancementWidgetMixin {
    @Inject(method = "drawTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderInGui(Lnet/minecraft/item/ItemStack;II)V"))
    public void refreshed_advancements$drawTooltip$renderInGui(MatrixStack matrixStack, int i, int j, float f, int y, int k, CallbackInfo ci) {
        RenderSystem.translated(RefreshedAdvancementsScreen.SCROLL_AREA_WIDTH, 0, 400);
    }
}
