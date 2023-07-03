package ir.game.sc_rplauncher.viewModel

import android.os.Environment
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun checkFolder() = intent {
        val path = Environment.getExternalStorageDirectory().path + File.separator + "suncityrp"
        val file = File(path)
        reduce {
            state.copy(
                isExitFolder = file.exists()
            )
        }
    }
}