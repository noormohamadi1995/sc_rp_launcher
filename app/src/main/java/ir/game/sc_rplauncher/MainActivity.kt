package ir.game.sc_rplauncher

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            when(it.itemId){
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

        navController.addOnDestinationChangedListener{controller, destination, arguments ->
            when(destination.id){
                R.id.loginFragment -> {}
                R.id.dashboardFragment -> mBinding.mToolbar.setTitle(R.string.dashboard)
                R.id.notificationFragment -> mBinding.mToolbar.setTitle(R.string.notification)
                R.id.aboutFragment -> mBinding.mToolbar.setTitle(R.string.about)
            }
        }

        val bottomNavigationViewBackground = mBinding.bottomNavigation.background as MaterialShapeDrawable
        bottomNavigationViewBackground.shapeAppearanceModel =
            bottomNavigationViewBackground.shapeAppearanceModel.toBuilder()
                .setTopRightCorner(CornerFamily.ROUNDED, 48f)
                .setTopLeftCorner(CornerFamily.ROUNDED, 48f)
                .build()
    }

    private fun checkPermissions(){
        PermissionX
            .init(this)
            .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request{allGranted, _, _ ->
                if(allGranted.not()){
                    showSnackBar(R.string.permission_deny)
                }
            }
    }

    private fun showSnackBar(@StringRes message : Int){
        val snackBar = Snackbar.make(
            findViewById(android.R.id.content),
            getText(message), Snackbar.LENGTH_LONG
        )
        snackBar.show()
        ViewCompat.setLayoutDirection(snackBar.view,ViewCompat.LAYOUT_DIRECTION_RTL)
    }
}