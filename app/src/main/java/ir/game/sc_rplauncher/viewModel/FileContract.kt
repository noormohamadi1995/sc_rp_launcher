package ir.game.sc_rplauncher.viewModel

import android.net.Uri
import androidx.annotation.StringRes

data class FileViewState(
    val isExitFolder: Boolean = false
)

sealed interface FileSideEffect {
    class DownloadFile(val progress: Int) : FileSideEffect
    object StartDownload : FileSideEffect
    class CompleteDownloadFile(val zipFile: Uri? = null) : FileSideEffect
    class DownloadError(@StringRes val message: Int) : FileSideEffect

    class DownloadCompleteApk(val file: String) : FileSideEffect

    object CancelDownload : FileSideEffect

    class SuccessfullySetUsername(@StringRes val message: Int) : FileSideEffect
}