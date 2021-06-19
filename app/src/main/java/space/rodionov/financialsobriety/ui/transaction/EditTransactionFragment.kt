package space.rodionov.financialsobriety.ui.transaction

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.databinding.FragmentEditTransactionBinding

@AndroidEntryPoint
class EditTransactionFragment : Fragment(R.layout.fragment_edit_transaction) {

    private val viewModel: EditTransactionViewModel by viewModels()

    private lateinit var binding: FragmentEditTransactionBinding


}








