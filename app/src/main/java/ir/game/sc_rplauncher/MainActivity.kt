package ir.game.sc_rplauncher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ir.game.sc_rplauncher.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        mBinding.bottomNavigation.selectedItemId = R.id.dashboardFragment
        mBinding.mToolbar.setTitle(R.string.dashboard)

        mBinding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.dashboardFragment -> {
                    navController.navigate(R.id.dashboardFragment)
                    mBinding.mToolbar.setTitle(R.string.dashboard)
                }
                R.id.notificationFragment -> {
                    navController.navigate(R.id.notificationFragment)
                    mBinding.mToolbar.setTitle(R.string.notification)
                }
                R.id.loginFragment ->{
                    val snackBar = Snackbar.make(
                        findViewById(android.R.id.content),
                        getText(R.string.update), Snackbar.LENGTH_LONG
                    )
                    snackBar.show()
                    ViewCompat.setLayoutDirection(snackBar.view,ViewCompat.LAYOUT_DIRECTION_RTL)
                }
            }

            true
        }

        val bottomNavigationViewBackground = mBinding.bottomNavigation.background as MaterialShapeDrawable
        bottomNavigationViewBackground.shapeAppearanceModel =
            bottomNavigationViewBackground.shapeAppearanceModel.toBuilder()
                .setTopRightCorner(CornerFamily.ROUNDED, 48f)
                .setTopLeftCorner(CornerFamily.ROUNDED, 48f)
                .build()
    }
}