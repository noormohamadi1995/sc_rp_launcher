package ir.game.sc_rplauncher.util

import android.os.Environment
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipFile

object FileUtil {
    private const val BUFFER_SIZE = 4096

    @OptIn(DelicateCoroutinesApi::class)
    @Throws(IOException::class)
    fun unzip(zipFilePath: File, destDirectory: String) {
        File(destDirectory).run {
            if (!exists()) {
                mkdirs()
            }
        }

        GlobalScope.launch(Dispatchers.IO){
            ZipFile(zipFilePath).use { zip ->
                zip.entries().asSequence().forEach { entry ->
                    zip.getInputStream(entry).use { input ->
                        val filePath = destDirectory + File.separator + entry.name
                        if (!entry.isDirectory) {
                            extractFile(input, filePath)
                        } else {
                            val dir = File(filePath)
                            dir.mkdir()
                        }
                    }
                }
               zipFilePath.delete()
            }

        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Throws(IOException::class)
    private fun extractFile(inputStream: InputStream, destFilePath: String) {
        GlobalScope.launch(Dispatchers.IO){
            val bos = BufferedOutputStream(FileOutputStream(destFilePath))
            val bytesIn = ByteArray(BUFFER_SIZE)
            var read: Int
            while (inputStream.read(bytesIn).also { read = it } != -1) {
                bos.write(bytesIn, 0, read)
            }
            bos.close()
        }
    }

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