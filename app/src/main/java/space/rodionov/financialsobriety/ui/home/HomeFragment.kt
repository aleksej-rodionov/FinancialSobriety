package space.rodionov.financialsobriety.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.data.TransactionType
import space.rodionov.financialsobriety.databinding.FragmentHomeBinding
import space.rodionov.financialsobriety.util.exhaustive

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        binding.apply {
            cardViewSpend.setOnClickListener { viewModel.onSpendsClick() }
            cardViewIncome.setOnClickListener { viewModel.onIncomesClick() }
            cardViewDebt.setOnClickListener { viewModel.onDebtsClick() }
            fabAddSpend.setOnClickListener { viewModel.onAddSpendClick() }
            fabAddIncome.setOnClickListener { viewModel.onAddIncomeClick() }
            fabAddDebt.setOnClickListener { viewModel.onAddDebtClick() }
        }

        setFragmentResultListener("add_edit_result") { _, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        } // 2-Я ПОЛОВИНА ФРАГМЕНТ РЕЗАЛТА

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.homeEvent.collect { event ->
                when (event) {
                    is HomeViewModel.HomeEvent.NavigateToAddSpendScreen -> {
                        val action = HomeFragmentDirections.actionHomeFragmentToEditTransactionFragment(null, "New spending", TransactionType.OUTCOME.name)
                        findNavController().navigate(action)
                    }
                    is HomeViewModel.HomeEvent.NavigateToAddIncomeScreen -> {
                        val action = HomeFragmentDirections.actionHomeFragmentToEditTransactionFragment(null, "New income", TransactionType.INCOME.name)
                        findNavController().navigate(action)
                    }
                    is HomeViewModel.HomeEvent.NavigateToAddDebtScreen -> {
                        val action = HomeFragmentDirections.actionFrontFragmentToEditDebtFragment("New debt", null)
                        findNavController().navigate(action)
                    }
                    is HomeViewModel.HomeEvent.NavigateToSpendsScreen -> {
                        val action =
                            HomeFragmentDirections.actionFrontFragmentToTransactionsFragment() // ADD ONLY SPENDS VARIABLE
                        findNavController().navigate(action)
                    }
                    is HomeViewModel.HomeEvent.NavigateToIncomesScreen -> {
                        val action = HomeFragmentDirections.actionFrontFragmentToTransactionsFragment() // ADD ONLY INCOMES VARIABLE
                        findNavController().navigate(action)
                    }
                    is HomeViewModel.HomeEvent.NavigateToDebtsScreen -> {
                        val action = HomeFragmentDirections.actionFrontFragmentToDebtsFragment()
                        findNavController().navigate(action)
                    }
                    is HomeViewModel.HomeEvent.ShowTransactionSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                }.exhaustive
            }
        }


    }

}








