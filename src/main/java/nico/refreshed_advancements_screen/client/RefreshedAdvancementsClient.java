package nico.refreshed_advancements_screen.client;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import nico.refreshed_advancements_screen.client.config.RefreshedAdvancementsConfig;

public class RefreshedAdvancementsClient implements ClientModInitializer {

    public static final String MOD_ID = "refreshed_advancements";

    @Override
    public void onInitializeClient() {
        AutoConfig.register(RefreshedAdvancementsConfig.class, JanksonConfigSerializer::new);
    }

    public static Identifier id(String path) {
        return Identifier.tryBuild(MOD_ID, path);
    }

    public static int lerpColors(int colorA, int colorB, float delta) {
        int alpha = Mth.lerpDiscrete(delta, colorA >> 24, colorB >> 24);
        int red = Mth.lerpDiscrete(delta, (colorA >> 16) & 0xFF, (colorB >> 16) & 0xFF);
        int green = Mth.lerpDiscrete(delta, (colorA >> 8) & 0xFF, (colorB >> 8) & 0xFF);
        int blue = Mth.lerpDiscrete(delta, colorA & 0xFF, colorB & 0xFF);

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}
