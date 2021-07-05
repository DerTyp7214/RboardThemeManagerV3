package de.dertyp7214.rboardthememanager.core


val Map<String, Any>.monet: Boolean
    get() {
        return this["use_silk_theme_by_default"] == true
                && this["silk_on_all_pixel"] == true
                && this["silk_theme"] == true
    }
val Map<String, Any>.logo: Boolean
    get() {
        return this["show_branding_on_space"] == true
                && this["show_branding_interval_seconds"] == 0L
                && this["branding_fadeout_delay_ms"] == 2147483647L
    }
val Map<String, Any>.border: Boolean
    get() {
        return this["enable_key_border"] == true
    }