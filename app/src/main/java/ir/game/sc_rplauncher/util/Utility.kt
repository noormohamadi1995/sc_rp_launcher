package ir.game.sc_rplauncher.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File


object Utility {
    fun Context.checkInstalledPackage(packageName : String) : Boolean{
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pkgList = packageManager.queryIntentActivities(intent, 0)
        var isFound = false
        pkgList.forEach {
            if (it.activityInfo.packageName != null && it.activityInfo.packageName.equals(packageName))
                isFound = true
        }

        return isFound
    }

    fun String.getUriFromFile(context: Context): Uri {
       return FileProvider.getUriForFile(
            context,
            "${context.applicationContext.packageName}.provider",
            File(this)
        )
    }
}