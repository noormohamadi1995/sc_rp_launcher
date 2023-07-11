package ir.game.sc_rplauncher.util

import android.app.ActionBar
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ir.game.sc_rplauncher.databinding.CustomLoadingBinding

object DialogHelper {

    context(Context)
    fun createLoadingDialog(): AlertDialog = MaterialAlertDialogBuilder(this@Context)
        .setView(
            CustomLoadingBinding.inflate(
                LayoutInflater.from(this@Context)
            ).apply {
                this.root.layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT
                )
            }.root
        )
        .setCancelable(false)
        .show()
}