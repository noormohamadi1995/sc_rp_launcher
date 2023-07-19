package ir.game.sc_rplauncher.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.net.toFile
import androidx.core.view.ViewCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ir.game.sc_rplauncher.R
import ir.game.sc_rplauncher.databinding.FragmentDashboardBinding
import ir.game.sc_rplauncher.dialog.ProgressDialog
import ir.game.sc_rplauncher.util.Constant
import ir.game.sc_rplauncher.util.Decompress
import ir.game.sc_rplauncher.util.DialogHelper
import ir.game.sc_rplauncher.util.Utility.getUriFromFile
import ir.game.sc_rplauncher.viewModel.FileSideEffect
import ir.game.sc_rplauncher.viewModel.FileViewModel
import ir.game.sc_rplauncher.viewModel.FileViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.viewmodel.observe
import timber.log.Timber


@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private var mBinding: FragmentDashboardBinding? = null
    private val mViewModel by viewModels<FileViewModel>()
    private var mProgress: ProgressDialog? = null
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
            mViewModel.observe(
                viewLifecycleOwner,
                state = {
                    render(it)
                },
                sideEffect = { handleSideEffect(it) }
            )
            setUpView()
        }
    }

    context(FragmentDashboardBinding)
            @SuppressLint("SuspiciousIndentation")
            private fun setUpView() {
        btnRulePlay.setOnClickListener {
            showRuleDialog()
        }

        btnStartGame.setOnClickListener {
            val launchIntent =
                requireContext().packageManager.getLaunchIntentForPackage(Constant.GAME_PACKAGE)
            if (launchIntent != null) {
                startActivity(launchIntent)
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
            else R.string.data_download
        )
        btnStartGame.isEnabled = fileViewState.isExitFolder
        btnDownloadData.setOnClickListener {
            if (fileViewState.isExitFolder) {
                val username = edtAddress.text.toString()
                if (username.isNotEmpty()) {
                    mViewModel.setUsernameToFile(username)
                } else showSnackBar(R.string.username_empty)
            } else {
                mViewModel.downloadFile(Constant.ZIP_FILE_LINK_DOWNLOAD, true)
            }
        }
    }

    context(FragmentDashboardBinding)
            private fun handleSideEffect(sideEffect: FileSideEffect) {
        when (sideEffect) {
            is FileSideEffect.CompleteDownloadFile -> {
                hideProgressDialog()
                sideEffect.zipFile?.also { uri ->
                    showLoading()
                    lifecycleScope.launch(Dispatchers.IO) {
                        val unzip =
                            Decompress(requireContext().contentResolver.openInputStream(uri))
                        if (unzip.unzip()) {
                            val file = uri.toFile()
                            file.delete()
                            hideLoading()
                            mViewModel.downloadFile(Constant.APK_FILE_LINK_DOWNLOAD, false)
                        } else {
                            showSnackBar(R.string.zun_zip_error)
                            hideLoading()
                        }
                    }
                }
            }

            is FileSideEffect.DownloadError -> {
                hideLoading()
                showSnackBar(sideEffect.message)
            }

            is FileSideEffect.DownloadFile -> {
                hideLoading()
                mProgress?.setProgress(sideEffect.progress)
            }

            is FileSideEffect.DownloadCompleteApk -> {
                hideProgressDialog()
                Timber.tag("apk").e("apk")
                mViewModel.checkFolder(requireContext())
                try {
                    val uri = sideEffect.file.getUriFromFile(requireContext())
                    try {
                        val mime = requireContext().contentResolver.getType(uri)
                        val showIntent = Intent(Intent.ACTION_VIEW)
                        showIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        if (mime == "application/vnd.android.package-archive") {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                showIntent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                                showIntent.action = Intent.ACTION_INSTALL_PACKAGE
                                showIntent.data = uri
                            } else {
                                showIntent.action = Intent.ACTION_VIEW
                                showIntent.setDataAndType(uri, mime)
                            }
                        } else {
                            showIntent.setDataAndType(uri, mime)
                            showIntent.action = Intent.ACTION_VIEW
                        }
                        this.startActivity(showIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            FileSideEffect.StartDownload -> showProgressDownloadFile()
            is FileSideEffect.SuccessfullySetUsername -> showSnackBar(sideEffect.message)
        }
    }

    private fun showProgressDownloadFile() {
        requireActivity().runOnUiThread {
            mProgress = ProgressDialog()
            mProgress?.isCancelable = false
            mProgress?.show(this@DashboardFragment.childFragmentManager, "progress_dialog")
        }
    }

    private fun hideProgressDialog() {
        requireActivity().runOnUiThread {
            mProgress?.dismiss()
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