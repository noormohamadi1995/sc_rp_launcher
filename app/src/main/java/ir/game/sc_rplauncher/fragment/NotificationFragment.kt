package ir.game.sc_rplauncher.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ir.game.sc_rplauncher.R
import ir.game.sc_rplauncher.databinding.FragmentNotificationBinding


@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private var mBinding: FragmentNotificationBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentNotificationBinding.inflate(inflater, container, false).apply {
        mBinding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
    }

    private fun setUpView() = mBinding?.apply {
        btnRulePlay.setOnClickListener {
            showDialog(getString(R.string.title_rule_play), R.string.rule_play_notification)
        }

        btnRulePublic.setOnClickListener {
            showDialog(getString(R.string.title_rule_public), R.string.rule_public)
        }

        btnNotification.setOnClickListener {
            showSnackBar()
        }

        btnUpdate.setOnClickListener {
            showSnackBar()
        }
    }

    private fun showDialog(title: String, @StringRes message: Int) {
        findNavController().navigate(
            NotificationFragmentDirections.actionNotificationFragmentToCustomAlertDialog(
                title = title,
                content = getString(message)
            )
        )
    }

    private fun showSnackBar() {
        val snackBar = Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            getText(R.string.update), Snackbar.LENGTH_LONG
        )
        snackBar.show()
        ViewCompat.setLayoutDirection(snackBar.view, ViewCompat.LAYOUT_DIRECTION_RTL)
    }
}