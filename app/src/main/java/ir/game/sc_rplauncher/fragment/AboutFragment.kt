package ir.game.sc_rplauncher.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ir.game.sc_rplauncher.databinding.FragmentAboutBinding

@AndroidEntryPoint
class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentAboutBinding.inflate(inflater,container,false).apply {
       val pInfo = requireActivity().packageManager.getPackageInfo(requireActivity().packageName,0)
        txtVersion.text = pInfo.versionName

    }.root
}