package de.dertyp7214.rboardthememanager.utils

import android.app.Activity
import android.content.Intent
import de.dertyp7214.rboardthememanager.screens.PreferencesActivity
import org.json.JSONObject

class Navigations(val activity: Activity) {
    enum class LINKS {
        INFO,
        ABOUT,
        FLAGS,
        ALL_FLAGS,
        ALL_PREFERENCES,
        PROPS,
        REPOS,
        SETTINGS
    }

    fun <T : Activity> goBackTo(clazz: Class<T>) {
        Intent(activity, clazz).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            activity.startActivity(this)
        }
    }

    fun deepLinkWithArgs(links: Map<LINKS, JSONObject>) {
        links.forEach { (link, args) ->
            val intent = Intent(activity, PreferencesActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("type", link.name.lowercase())
            intent.putExtra("args", args.toString())
            if (links.keys.indexOf(link) != links.keys.toList().lastIndex) intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            activity.startActivity(intent)
        }
    }
}