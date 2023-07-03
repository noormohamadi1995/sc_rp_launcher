package ir.game.sc_rplauncher.dialog

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ir.game.sc_rplauncher.R
import ir.game.sc_rplauncher.databinding.AlertDialogLayoutBinding

@AndroidEntryPoint
class CustomAlertDialog : DialogFragment() {
    private var mBinding : AlertDialogLayoutBinding? = null
    private val mArgs by navArgs<CustomAlertDialogArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogStyle)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = AlertDialogLayoutBinding.inflate(inflater,container,false).apply {
        mBinding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding?.apply {
            txtAlertTitle.text = mArgs.title
            txtAlertContent.text = mArgs.content
            btnClose.setOnClickListener { dismiss() }
        }
    }

    override fun onStart() {
        super.onStart()
        requireContext().apply {
            dialog?.window?.setBackgroundDrawable(
                GradientDrawable().apply {
                    setColor(requireContext().getColor(R.color.white))
                    this.cornerRadius = 20f
                }
            )
        }
    }
}