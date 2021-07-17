package space.rodionov.financialsobriety.ui.debt

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import space.rodionov.financialsobriety.R

@AndroidEntryPoint
class EditDebtFragment : Fragment(R.layout.fragment_edit_debt) {
    private val viewModel: EditDebtViewModel by viewModels()


}