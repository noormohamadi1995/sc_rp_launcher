package ir.game.sc_rplauncher.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build


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
}