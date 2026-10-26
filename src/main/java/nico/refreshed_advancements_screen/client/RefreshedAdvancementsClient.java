package nico.refreshed_advancements_screen.client;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.resources.Identifier;
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
}
