package de.dertyp7214.rboardthememanager.core


val Map<String, Any>.monet: Boolean
    get() {
        return this["use_silk_theme_by_default"] != false
                && this["silk_on_all_pixel"] != false
                && this["silk_theme"] != false
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

fun Map<String, Boolean>.toSet(): Set<String> {
    return map { (key, value) -> "${value.toString().lowercase()}:$key" }.toSet()
}