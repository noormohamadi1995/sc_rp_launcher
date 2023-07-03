package ir.game.sc_rplauncher.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ir.game.sc_rplauncher.R
import ir.game.sc_rplauncher.databinding.FragmentDashboardBinding
import ir.game.sc_rplauncher.viewModel.FileViewModel
import org.orbitmvi.orbit.viewmodel.observe

@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private var mBinding: FragmentDashboardBinding? = null
    private val mViewModel by viewModels<FileViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentDashboardBinding.inflate(inflater, container, false).apply {
        mBinding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.checkFolder()
        mBinding?.apply {
            setUpView()
            mViewModel.observe(
                viewLifecycleOwner,
                state = {
                    btnDownloadData.isEnabled = it.isExitFolder.not()
                    btnDownloadData.text = getString(if (it.isExitFolder) R.string.found_data else R.string.data_download)
                }
            )
        }
    }

    context(FragmentDashboardBinding)
            @SuppressLint("SuspiciousIndentation")
            private fun setUpView() {
        btnRulePlay.setOnClickListener {
            showRuleDialog()
        }
        btnDownloadData.setOnClickListener {

        }

        btnStartGame.setOnClickListener {

        }

        edtAddress.doAfterTextChanged {
            val result: String = it.toString().replace(" ", "_")
            if (it.toString() != result) {
                edtAddress.setText(result)
                edtAddress.setSelection(result.length)
            }
        }
    }

    private fun showRuleDialog(){
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage(R.string.rule_play)
            .setPositiveButton("بستن"
            ) { p0, p1 -> p0.dismiss()}
            .create()
        dialog.show()
        val textView = dialog.findViewById<View>(android.R.id.message) as TextView
        textView.textSize = 13f
    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}