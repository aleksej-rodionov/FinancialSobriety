package space.rodionov.financialsobriety.ui.front

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.ui.spendandincome.SpendViewModel

@AndroidEntryPoint
class FrontFragment : Fragment(R.layout.fragment_front) {

    private val viewModel: FrontViewModel by viewModels()

}








