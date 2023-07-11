package ir.game.sc_rplauncher

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar
import com.permissionx.guolindev.PermissionX
import dagger.hilt.android.AndroidEntryPoint
import ir.game.sc_rplauncher.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var mBinding: ActivityMainBinding
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        checkPermissions()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        mBinding.bottomNavigation.selectedItemId = R.id.dashboardFragment
        mBinding.mToolbar.setTitle(R.string.dashboard)

        mBinding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.dashboardFragment -> {
                    navController.navigate(R.id.dashboardFragment)
                }

                R.id.notificationFragment -> {
                    navController.navigate(R.id.notificationFragment)
                }

                R.id.loginFragment -> showSnackBar(R.string.update)

                R.id.aboutFragment -> navController.navigate(R.id.aboutFragment)
            }

            true
        }

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.loginFragment -> {}
                R.id.dashboardFragment -> mBinding.mToolbar.setTitle(R.string.dashboard)
                R.id.notificationFragment -> mBinding.mToolbar.setTitle(R.string.notification)
                R.id.aboutFragment -> mBinding.mToolbar.setTitle(R.string.about)
            }
        }

        val bottomNavigationViewBackground =
            mBinding.bottomNavigation.background as MaterialShapeDrawable
        bottomNavigationViewBackground.shapeAppearanceModel =
            bottomNavigationViewBackground.shapeAppearanceModel.toBuilder()
                .setTopRightCorner(CornerFamily.ROUNDED, 48f)
                .setTopLeftCorner(CornerFamily.ROUNDED, 48f)
                .build()
    }

    private fun checkPermissions() {
        val list = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            listOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        }else listOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        PermissionX
            .init(this@MainActivity)
            .permissions(list)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted.not()) {
                    showSnackBar(R.string.permission_deny)
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                        if (Environment.isExternalStorageManager().not()) {
                            launcher.launch(
                                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                                    addCategory("android.intent.category.DEFAULT")
                                    data = (Uri.parse(
                                        String.format(
                                            "package:%s",
                                            applicationContext.packageName
                                        )
                                    ))
                                }
                            )
                        }
                }
            }
    }

    private fun showSnackBar(@StringRes message: Int) {
        val snackBar = Snackbar.make(
            findViewById(android.R.id.content),
            getText(message), Snackbar.LENGTH_LONG
        )
        snackBar.show()
        ViewCompat.setLayoutDirection(snackBar.view, ViewCompat.LAYOUT_DIRECTION_RTL)
    }
}