package ir.game.sc_rplauncher.util

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File


object Utility {
    fun Context.checkInstalledPackage(packageName : String) : Boolean{
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                packageManager.getPackageInfo(packageName, 0)
            }
            true
        }catch (e: PackageManager.NameNotFoundException){
            false
        }
    }

    fun String.getUriFromFile(context: Context): Uri {
       return FileProvider.getUriForFile(
            context,
            "${context.applicationContext.packageName}.provider",
            File(this)
        )
    }
}