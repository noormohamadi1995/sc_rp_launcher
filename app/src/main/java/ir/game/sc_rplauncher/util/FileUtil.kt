package ir.game.sc_rplauncher.util

import android.os.Environment
import java.io.File


object FileUtil {
    fun getDirPath(dirName : String) : String{
        val file = File(Environment.getExternalStorageDirectory(),dirName)
        return if (file.exists())
            file.path
        else {
            file.mkdir()
            file.path
        }
    }

    fun getDirExist(dirName : String) = File(Environment.getExternalStorageDirectory(),dirName).exists()

    fun removeDir(dirName: String){
        val file = File(Environment.getExternalStorageDirectory(),dirName)
        file.delete()
    }
}