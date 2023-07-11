package ir.game.sc_rplauncher.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
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
import ir.game.sc_rplauncher.util.DialogHelper
import ir.game.sc_rplauncher.util.Utility.getUriFromFile
import ir.game.sc_rplauncher.viewModel.FileSideEffect
import ir.game.sc_rplauncher.viewModel.FileViewModel
import ir.game.sc_rplauncher.viewModel.FileViewState
import org.orbitmvi.orbit.viewmodel.observe


@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private var mBinding: FragmentDashboardBinding? = null
    private val mViewModel by viewModels<FileViewModel>()
    private var mProgress: ProgressDialog = ProgressDialog()
    private var loadingDialog: androidx.appcompat.app.AlertDialog? = null


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
                sideEffect = { handleSideEffect(it) }
            )
        }
    }

    context(FragmentDashboardBinding)
            @SuppressLint("SuspiciousIndentation")
            private fun setUpView() {
        val state = mViewModel.container.stateFlow.value
        btnRulePlay.setOnClickListener {
            showRuleDialog()
        }
        btnDownloadData.setOnClickListener {
            if(state.isExitFolder){

            }else
                mViewModel.downloadFile(requireContext(), Constant.ZIP_FILE_LINK_DOWNLOAD)
        }

        btnStartGame.setOnClickListener {
            if (state.isInstalledGame) {
                val launchIntent =
                    requireContext().packageManager.getLaunchIntentForPackage("ir.suncityrp.client")
                if (launchIntent != null) {
                    startActivity(launchIntent)
                }
            } else {
                mViewModel.downloadApk(requireContext(), Constant.APK_FILE_LINK_DOWNLOAD)
            }
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
        btnDownloadData.text = getString(
            if (fileViewState.isExitFolder) R.string.found_data
            else R.string.data_download)
        btnStartGame.text = if (fileViewState.isInstalledGame) "شروع بازی" else "دانلود بازی"
    }

    context(FragmentDashboardBinding)
            private fun handleSideEffect(sideEffect: FileSideEffect) {
        when (sideEffect) {
            FileSideEffect.CompleteDownload -> mProgress.dismiss()
            is FileSideEffect.DownloadError -> {
                hideLoading()
                showSnackBar(sideEffect.message)
            }

            is FileSideEffect.DownloadFile -> {
                hideLoading()
                mProgress.setProgress(sideEffect.progress)
            }

            is FileSideEffect.DownloadCompleteApk -> {
                try {
                    hideLoading()
                    val uri = sideEffect.file.getUriFromFile(requireContext())
                    try {
                        requireContext().contentResolver.getType(uri)
                        val showIntent = Intent(Intent.ACTION_VIEW)
                        showIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        showIntent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                        showIntent.action = Intent.ACTION_INSTALL_PACKAGE
                        showIntent.data = uri
                        this.startActivity(showIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            is FileSideEffect.UnZipFile -> Unit
            FileSideEffect.StartDownload -> {
                mProgress.isCancelable = false
                mProgress.show(childFragmentManager, "progress_Dialog")
            }
        }
    }

    private fun showRuleDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage(R.string.rule_play)
            .setPositiveButton(
                "بستن"
            ) { p0, _ -> p0.dismiss() }
            .create()
        dialog.show()
        val textView = dialog.findViewById<View>(android.R.id.message) as TextView
        textView.textSize = 13f
    }

    private fun showSnackBar(@StringRes message: Int) {
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

    private fun showLoading() {
        hideLoading()
        requireActivity().runOnUiThread {
            requireContext().apply {
                loadingDialog = DialogHelper.createLoadingDialog()
            }
        }
    }

    private fun hideLoading() {
        requireActivity().runOnUiThread {
            loadingDialog?.dismiss()
        }
    }
}