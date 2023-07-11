package ir.game.sc_rplauncher.viewModel

import androidx.annotation.StringRes

data class FileViewState(
    val isExitFolder : Boolean = false,
    val isInstalledGame : Boolean = false
)

sealed interface FileSideEffect{
    class DownloadFile(val progress : Int) : FileSideEffect
    object StartDownload : FileSideEffect
    class UnZipFile(val unZipState : Boolean) : FileSideEffect
    object CompleteDownload : FileSideEffect
    class DownloadError(@StringRes val message : Int) : FileSideEffect

    class DownloadCompleteApk(val file : String) : FileSideEffect
}