package de.dertyp7214.rboardthememanager.dialogs

import android.app.Dialog
import android.os.Bundle
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentDialog
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.button.MaterialButton
import de.dertyp7214.rboardcomponents.utils.ThemeUtils
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.openUrl

class NoRootDialog : DialogFragment() {
    companion object {
        fun open(activity: FragmentActivity): NoRootDialog =
            NoRootDialog().also {
                it.isCancelable = false
                it.show(activity.supportFragmentManager, "NoRoot")
            }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        return ComponentDialog(context,
            ThemeUtils.getTheme(context)
                ?: com.google.android.material.R.attr.dynamicColorThemeOverlay
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.dialog_no_root, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            activity?.enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
            )

            dialog?.window?.setDecorFitsSystemWindows(false)

            dialog?.window?.isNavigationBarContrastEnforced = false
            dialog?.window?.navigationBarColor = Color.TRANSPARENT

        val getMagiskButton: MaterialButton = view.findViewById(R.id.magisk_button)

        getMagiskButton.setOnClickListener {
            requireActivity().openUrl(getString(R.string.magisk_github_url))
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.let { dialog ->
            val matchParent = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(matchParent, matchParent)
        }
    }
}