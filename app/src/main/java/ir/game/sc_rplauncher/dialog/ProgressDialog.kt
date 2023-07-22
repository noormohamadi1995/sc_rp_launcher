package ir.game.sc_rplauncher.dialog

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ir.game.sc_rplauncher.R
import ir.game.sc_rplauncher.databinding.ProgressLayoutBinding

class ProgressDialog(
    val onClickClose : () -> Unit
) : DialogFragment() {
    private var mBinding : ProgressLayoutBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ProgressLayoutBinding.inflate(inflater,container,false).apply {
        mBinding =this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding?.apply {
            btnClose.setOnClickListener {
                onClickClose.invoke()
            }
        }
    }

    fun setProgress(progress : Int) = mBinding?.apply {
        progressBar.progress = progress
        txtProgress.text = progress.toString().plus("/").plus(100)
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