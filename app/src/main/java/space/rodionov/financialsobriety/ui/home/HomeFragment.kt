package space.rodionov.financialsobriety.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.data.TransactionType
import space.rodionov.financialsobriety.databinding.FragmentHomeBinding
import space.rodionov.financialsobriety.util.exhaustive
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        val homeMonthAdapter = HomeAdapter()

        binding.apply { cardViewSpend.setOnClickListener { viewModel.onSpendsClick(TransactionType.OUTCOME.name) }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.monthNames.collect {
                    tvBalance.text = "Баланс за ${it.first}"
                    tvMonth.text = "За ${it.second}"
                    tvIncomeMonth.text = "За ${it.second}"
                }
                viewModel.monthValues.collect {
                    tvSumMonth.text = it.first.toString()
                    tvIncomeSumMonth.text = it.second.toString()
                    tvDebtSumMonth.text = it.third.toString()
                }
            }

            cardViewIncome.setOnClickListener { viewModel.onIncomesClick(TransactionType.INCOME.name) }
            cardViewDebt.setOnClickListener { viewModel.onDebtsClick() }
            fabAddSpend.setOnClickListener { viewModel.onAddSpendClick() }
            fabAddIncome.setOnClickListener { viewModel.onAddIncomeClick() }
            fabAddDebt.setOnClickListener { viewModel.onAddDebtClick() }

            viewPagerHome.adapter = homeMonthAdapter
            viewPagerHome.setRotationY(180f);

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.monthListFlow.collect {
                    val months = it?: return@collect
                    homeMonthAdapter.submitList(months)
                }
            }

            viewPagerHome.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    viewModel.setMonthValues(position)
                }
            })
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
                            HomeFragmentDirections.actionFrontFragmentToTransactionsFragment()
                        findNavController().navigate(action)
                    }
                    is HomeViewModel.HomeEvent.NavigateToIncomesScreen -> {
                        val action = HomeFragmentDirections.actionFrontFragmentToTransactionsFragment()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}








