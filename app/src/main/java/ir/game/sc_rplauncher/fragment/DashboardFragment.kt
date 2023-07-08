package ir.game.sc_rplauncher.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ir.game.sc_rplauncher.R
import ir.game.sc_rplauncher.databinding.FragmentDashboardBinding
import ir.game.sc_rplauncher.dialog.ProgressDialog
import ir.game.sc_rplauncher.util.Constant
import ir.game.sc_rplauncher.util.FileUtil
import ir.game.sc_rplauncher.viewModel.FileSideEffect
import ir.game.sc_rplauncher.viewModel.FileViewModel
import ir.game.sc_rplauncher.viewModel.FileViewState
import org.orbitmvi.orbit.viewmodel.observe
import java.io.File
import java.lang.Exception


@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private var mBinding: FragmentDashboardBinding? = null
    private val mViewModel by viewModels<FileViewModel>()
    private var mProgress : ProgressDialog = ProgressDialog()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentDashboardBinding.inflate(inflater, container, false).apply {
        mBinding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.checkFolder(requireContext())
        mBinding?.apply {
            setUpView()
            mViewModel.observe(
                viewLifecycleOwner,
                state = {
                    render(it)
                },
                sideEffect = {handleSideEffect(it)}
            )
        }
    }

    context(FragmentDashboardBinding)
            @SuppressLint("SuspiciousIndentation")
            private fun setUpView() {
        btnRulePlay.setOnClickListener {
            try {

                val file = Environment.getExternalStorageDirectory().path + File.separator + "suncityrp.zip"
                FileUtil.unzip(File(file),FileUtil.getDirPath("suncityrp"))
            }catch (ex : Exception){
                ex.printStackTrace()
            }
            //showRuleDialog()
        }
        btnDownloadData.setOnClickListener {
            mProgress.isCancelable = false
            mProgress.show(childFragmentManager,"progress_Dialog")
           mViewModel.downloadFile(requireContext(),Constant.ZIP_FILE_LINK_DOWNLOAD)
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

    context(FragmentDashboardBinding)
            private fun render(fileViewState: FileViewState) {
        btnDownloadData.isEnabled = fileViewState.isExitFolder.not()
        btnDownloadData.text =
            getString(if (fileViewState.isExitFolder) R.string.found_data else R.string.data_download)
        btnStartGame.isEnabled = fileViewState.isInstalledGame
    }

    context(FragmentDashboardBinding)
            private fun handleSideEffect(sideEffect: FileSideEffect){
                when(sideEffect){
                    FileSideEffect.CompleteDownload -> mProgress.dismiss()
                    is FileSideEffect.DownloadError -> showSnackBar(sideEffect.message)
                    is FileSideEffect.DownloadFile -> {
                        mProgress.setProgress(sideEffect.progress)
                    }
                }
            }

    private fun showRuleDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage(R.string.rule_play)
            .setPositiveButton(
                "بستن"
            ) { p0, p1 -> p0.dismiss() }
            .create()
        dialog.show()
        val textView = dialog.findViewById<View>(android.R.id.message) as TextView
        textView.textSize = 13f
    }

    private fun showSnackBar(@StringRes message : Int) {
        val snackBar = Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            getText(message), Snackbar.LENGTH_LONG
        )
        snackBar.show()
        ViewCompat.setLayoutDirection(snackBar.view, ViewCompat.LAYOUT_DIRECTION_RTL)
    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}