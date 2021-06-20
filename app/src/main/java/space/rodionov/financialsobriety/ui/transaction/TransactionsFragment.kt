package space.rodionov.financialsobriety.ui.transaction

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.databinding.FragmentTransactionsBinding

@AndroidEntryPoint
class TransactionsFragment : Fragment(R.layout.fragment_transactions) {
//    private val viewModel: TransactionsViewModel by viewModels()

    private lateinit var binding: FragmentTransactionsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTransactionsBinding.bind(view)




    }

}







