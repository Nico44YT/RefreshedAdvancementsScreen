package nico.refreshed_advancements_screen.client.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.util.CommonColors;
import nico.refreshed_advancements_screen.client.RefreshedAdvancementsClient;

import java.util.function.Supplier;

@Config(name = RefreshedAdvancementsClient.MOD_ID)
public final class RefreshedAdvancementsConfig implements ConfigData {
    @ConfigEntry.Gui.Excluded
    private static final Supplier<RefreshedAdvancementsConfig> CONFIG = () -> AutoConfig.getConfigHolder(RefreshedAdvancementsConfig.class).getConfig();

    public boolean enable = true;
    public static boolean isEnabled() {
        return CONFIG.get().enable;
    }

    public float horizontal_scroll_multiplier = 12;
    public static float getHorizontalScrollMultiplier() {
        return CONFIG.get().horizontal_scroll_multiplier;
    }

    public float vertical_scroll_multiplier = 12;
    public static float getVerticalScrollMultiplier() {
        return CONFIG.get().vertical_scroll_multiplier;
    }

    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int text_color = CommonColors.WHITE;
    public static int getTextColor() {
        return CONFIG.get().text_color;
    }

    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int text_shadow_color = CommonColors.DARK_GRAY;
    public static int getTextShadowColor() {
        return CONFIG.get().text_shadow_color;
    }

    public boolean change_mouse_cursor = true;
    public static boolean shouldChangeMouseCursor() {
        return CONFIG.get().change_mouse_cursor;
    }

    public int entries_area_width = 80;
    public static int getEntriesAreaWidth() {
        return CONFIG.get().entries_area_width;
    }

    public int entry_height = 18;
    public static int getEntryHeight() {
        return CONFIG.get().entry_height;
    }

    public boolean draw_entry_icon = true;
    public static boolean shouldDrawEntryIcons() {
        return CONFIG.get().draw_entry_icon;
    }

    public float entry_icon_size = 0.8f;
    public static float getEntryIconSize() {
        return CONFIG.get().entry_icon_size;
    }

    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int selected_entry_start_color = 0x3F_FF_FF_FF;
    public static int getSelectedEntryStartColor() {
        return CONFIG.get().selected_entry_start_color;
    }

    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int selected_entry_end_color = 0x0;
    public static int getSelectedEntryEndColor() {
        return CONFIG.get().selected_entry_end_color;
    }

    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int hover_entry_start_color = 0x1F_FF_FF_FF;
    public static int getHoverEntryStartColor() {
        return CONFIG.get().hover_entry_start_color;
    }

    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int hover_entry_end_color = 0x0;
    public static int getHoverEntryEndColor() {
        return CONFIG.get().hover_entry_end_color;
    }

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public GradientOrientation selected_entry_gradient_orientation = GradientOrientation.HORIZONTAL;
    public static GradientOrientation getSelectedEntryGradientOrientation() {
        return CONFIG.get().selected_entry_gradient_orientation;
    }

    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int entry_start_color = 0xBF_00_00_00;
    public static int getEntryStartColor() {
        return CONFIG.get().entry_start_color;
    }

    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int entry_end_color = 0x0;
    public static int getEntryEndColor() {
        return CONFIG.get().entry_end_color;
    }

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public GradientOrientation entry_gradient_orientation = GradientOrientation.HORIZONTAL;
    public static GradientOrientation getEntryGradientOrientation() {
        return CONFIG.get().entry_gradient_orientation;
    }

    public int entry_margin = 2;
    public static int getEntryMargin() {
        return CONFIG.get().entry_margin;
    }

    public int scrollbar_width = 2;
    public static int getScrollbarWidth() {
        return CONFIG.get().scrollbar_width;
    }

    public int scrollbar_minimum_height = 8;
    public static int getScrollbarMinHeight() {
        return CONFIG.get().scrollbar_minimum_height;
    }

    public int scrollbar_fade_height = 10;
    public static int getScrollbarFadeHeight() {
        return CONFIG.get().scrollbar_fade_height;
    }

    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int scrollbar_color = 0x33_FF_FF_FF;

    public static int getScrollbarColor() {
        return CONFIG.get().scrollbar_color;
    }

    public boolean render_advancements_title = true;
    public static boolean shouldDrawAdvancementsTitle() {
        return CONFIG.get().render_advancements_title;
    }

    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int advancements_area_color = 0x50_00_00_00;
    public static int getAdvancementsAreaColor() {
        return CONFIG.get().advancements_area_color;
    }

    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int scroll_area_color = 0x70_00_00_00;
    public static int getScrollAreaColor() {
        return CONFIG.get().scroll_area_color;
    }

    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int scroll_area_shadow_color = 0xBF_00_00_00;
    public static int getScrollAreaShadowColor() {
        return CONFIG.get().scroll_area_shadow_color;
    }

    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int scroll_area_highlight_color = 0x33_FF_FF_FF;
    public static int getScrollAreaHighlightColor() {
        return CONFIG.get().scroll_area_highlight_color;
    }

    public float scrollbar_entries_spacing = 1;
    public static float getScrollbarEntriesSpacing() {
        return CONFIG.get().scrollbar_entries_spacing;
    }

    public enum GradientOrientation {
        HORIZONTAL,
        VERTICAL
    }
}
