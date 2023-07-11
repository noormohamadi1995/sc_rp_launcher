package ir.game.sc_rplauncher.viewModel

import android.content.Context
import android.os.Environment
import android.webkit.URLUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.FetchConfiguration
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2.NetworkType
import com.tonyodev.fetch2.Priority
import com.tonyodev.fetch2.Request
import com.tonyodev.fetch2core.DownloadBlock
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.game.sc_rplauncher.R
import ir.game.sc_rplauncher.util.Constant
import ir.game.sc_rplauncher.util.Decompress
import ir.game.sc_rplauncher.util.FileUtil
import ir.game.sc_rplauncher.util.Utility.checkInstalledPackage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import java.io.File
import javax.inject.Inject


@HiltViewModel
class FileViewModel @Inject constructor() : ViewModel(),
    ContainerHost<FileViewState, FileSideEffect> {
    override val container: Container<FileViewState, FileSideEffect> = container(
        initialState = FileViewState()
    )

    fun checkFolder(context: Context) = intent {
        reduce {
            state.copy(
                isExitFolder = FileUtil.getDirExist(Constant.DATA_FOLDER_NAME) && context.checkInstalledPackage(
                    Constant.GAME_PACKAGE
                ),
                isInstalledGame = context.checkInstalledPackage(Constant.GAME_PACKAGE)
            )
        }
    }

    fun downloadFile(context: Context, fileUrl: String) = intent {
        val fetchConfiguration = FetchConfiguration.Builder(context)
            .setDownloadConcurrentLimit(3)
            .build()
        val fetch = Fetch.Impl.getInstance(fetchConfiguration)
        val fileName = URLUtil.guessFileName(fileUrl, null, null)

        val file = Environment.getExternalStorageDirectory().path + File.separator + fileName

        val request = Request(fileUrl, file)
        request.priority = Priority.HIGH
        request.networkType = NetworkType.ALL
        fetch.enqueue(request,
            { updatedRequest: Request? ->
                Timber.tag("wwww").e(updatedRequest.toString())
            }
        ) { error: Error? ->
            viewModelScope.launch {
                postSideEffect(sideEffect = FileSideEffect.DownloadError(R.string.download_error))
            }
        }

        fetch.addListener(
            object : FetchListener {
                override fun onAdded(download: Download) {

                }

                override fun onCancelled(download: Download) {
                }

                override fun onCompleted(download: Download) {
                    viewModelScope.launch(Dispatchers.IO) {
                        postSideEffect(sideEffect = FileSideEffect.CompleteDownload)
                        postSideEffect(sideEffect = FileSideEffect.UnZipFile(false))
                        val unzip =
                            Decompress(context.contentResolver.openInputStream(download.fileUri))
                        postSideEffect(sideEffect = FileSideEffect.UnZipFile(unzip.unzip()))
                        reduce {
                            state.copy(
                                isExitFolder = FileUtil.getDirExist(Constant.DATA_FOLDER_NAME)
                            )
                        }
                    }
                }

                override fun onDeleted(download: Download) {
                }

                override fun onDownloadBlockUpdated(
                    download: Download,
                    downloadBlock: DownloadBlock,
                    totalBlocks: Int
                ) {
                }

                override fun onError(download: Download, error: Error, throwable: Throwable?) {
                    FileUtil.removeDir(download.file)
                    viewModelScope.launch {
                        postSideEffect(sideEffect = FileSideEffect.DownloadError(R.string.download_error))
                    }

                }

                override fun onPaused(download: Download) {
                }

                override fun onProgress(
                    download: Download,
                    etaInMilliSeconds: Long,
                    downloadedBytesPerSecond: Long
                ) {
                    viewModelScope.launch {
                        postSideEffect(
                            sideEffect = FileSideEffect.DownloadFile(
                                progress = download.progress
                            )
                        )
                    }
                }

                override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
                    viewModelScope.launch {
                        postSideEffect(sideEffect = FileSideEffect.StartDownload)
                    }
                }

                override fun onRemoved(download: Download) {
                }

                override fun onResumed(download: Download) {
                }

                override fun onStarted(
                    download: Download,
                    downloadBlocks: List<DownloadBlock>,
                    totalBlocks: Int
                ) {
                }

                override fun onWaitingNetwork(download: Download) {
                }

            },
            notify = true,
            autoStart = true
        )

    }

    fun downloadApk(context: Context, fileUrl: String) = intent {
        val fetchConfiguration = FetchConfiguration.Builder(context)
            .setDownloadConcurrentLimit(3)
            .build()
        val fetch = Fetch.Impl.getInstance(fetchConfiguration)
        val fileName = URLUtil.guessFileName(fileUrl, null, null)

        val file =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + File.separator + fileName

        val request = Request(fileUrl, file)
        request.priority = Priority.HIGH
        request.networkType = NetworkType.ALL
        fetch.enqueue(request,
            {
            }
        ) { error: Error? ->
            viewModelScope.launch {
                postSideEffect(sideEffect = FileSideEffect.DownloadError(R.string.download_error))
            }
        }

        fetch.addListener(
            object : FetchListener {
                override fun onAdded(download: Download) {
                }

                override fun onCancelled(download: Download) {
                }

                override fun onCompleted(download: Download) {
                    viewModelScope.launch {
                        postSideEffect(sideEffect = FileSideEffect.CompleteDownload)
                        postSideEffect(sideEffect = FileSideEffect.DownloadCompleteApk(file))
                    }
                }

                override fun onDeleted(download: Download) {
                }

                override fun onDownloadBlockUpdated(
                    download: Download,
                    downloadBlock: DownloadBlock,
                    totalBlocks: Int
                ) {
                }

                override fun onError(download: Download, error: Error, throwable: Throwable?) {
                    viewModelScope.launch {
                        postSideEffect(sideEffect = FileSideEffect.DownloadError(R.string.download_error))
                    }

                }

                override fun onPaused(download: Download) {
                }

                override fun onProgress(
                    download: Download,
                    etaInMilliSeconds: Long,
                    downloadedBytesPerSecond: Long
                ) {
                    viewModelScope.launch {
                        postSideEffect(
                            sideEffect = FileSideEffect.DownloadFile(
                                progress = download.progress
                            )
                        )
                    }
                }

                override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
                    viewModelScope.launch {
                        postSideEffect(sideEffect = FileSideEffect.StartDownload)
                    }
                }

                override fun onRemoved(download: Download) {
                }

                override fun onResumed(download: Download) {
                }

                override fun onStarted(
                    download: Download,
                    downloadBlocks: List<DownloadBlock>,
                    totalBlocks: Int
                ) {
                }

                override fun onWaitingNetwork(download: Download) {
                }

            },
            notify = true,
            autoStart = true
        )
    }

}