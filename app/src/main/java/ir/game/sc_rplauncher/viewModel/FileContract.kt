package ir.game.sc_rplauncher.viewModel

data class FileViewState(
    val isExitFolder : Boolean = false
)

sealed interface FileSideEffect{

}