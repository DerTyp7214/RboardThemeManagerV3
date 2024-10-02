package de.dertyp7214.rboardthememanager.dialogs

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentDialog
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.card.MaterialCardView
import de.dertyp7214.rboardcomponents.utils.ThemeUtils
import de.dertyp7214.rboardthememanager.R

class UsageDialog(
    private val onMagisk: (DialogFragment) -> Unit,
    private val onGboard: (DialogFragment) -> Unit
) : DialogFragment() {
    companion object {
        fun open(
            activity: FragmentActivity,
            onMagisk: (DialogFragment) -> Unit,
            onGboard: (DialogFragment) -> Unit
        ): UsageDialog =
            UsageDialog(onMagisk, onGboard).also {
                it.isCancelable = false
                it.show(activity.supportFragmentManager, "NoRoot")
            }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        return ComponentDialog(
            context,
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
        return inflater.inflate(R.layout.usage_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        @Suppress("DEPRECATION")
        dialog?.window?.setDecorFitsSystemWindows(false)

        dialog?.window?.isNavigationBarContrastEnforced = false
        @Suppress("DEPRECATION")
        dialog?.window?.navigationBarColor = Color.TRANSPARENT

        val magiskCard: MaterialCardView = view.findViewById(R.id.cardMagisk)
        val gboardCard: MaterialCardView = view.findViewById(R.id.cardGboard)

        magiskCard.setOnClickListener { onMagisk(this) }
        gboardCard.setOnClickListener { onGboard(this) }
    }

    override fun onStart() {
        super.onStart()
        dialog?.let { dialog ->
            val matchParent = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(matchParent, matchParent)
        }
    }
}