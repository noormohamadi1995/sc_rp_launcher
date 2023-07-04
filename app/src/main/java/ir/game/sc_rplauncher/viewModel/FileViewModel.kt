package ir.game.sc_rplauncher.viewModel

import android.content.Context
import android.os.Environment
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.game.sc_rplauncher.util.Utility.checkInstalledPackage
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FileViewModel @Inject constructor() : ViewModel(),ContainerHost<FileViewState,FileSideEffect> {
    override val container: Container<FileViewState, FileSideEffect> = container(
        initialState = FileViewState()
    )

    fun checkFolder(context: Context) = intent {
        val file = File(Environment.getExternalStorageDirectory(),"suncityrp")
        reduce {
            state.copy(
                isExitFolder = file.exists(),
                isInstalledGame = context.checkInstalledPackage("ir.suncityrp.client")
            )
        }
    }

    fun downloadFile() = intent {

    }
}