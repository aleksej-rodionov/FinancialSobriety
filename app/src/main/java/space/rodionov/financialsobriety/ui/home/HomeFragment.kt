package space.rodionov.financialsobriety.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.children
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
import space.rodionov.financialsobriety.util.roundToTwoDecimals
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        val homeMonthAdapter = HomeAdapter(requireContext())

        binding.apply {
            cardViewSpend.setOnClickListener { viewModel.onSpendsClick(TransactionType.OUTCOME.name) }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.monthData.collect {
                    val monthData = it ?: return@collect
                    tvBalance.text = "${resources.getString(R.string.balance_for)} ${monthData.first.first}"
                    tvMonth.text = "${resources.getString(R.string.for_)} ${monthData.first.second}"
                    tvIncomeMonth.text = "${resources.getString(R.string.for_)} ${monthData.first.second}"
                    tvSumMonth.text = monthData.second.first.roundToTwoDecimals().toString()
                    tvIncomeSumMonth.text = monthData.second.second.roundToTwoDecimals().toString()
                    tvBalanceSumMonth.text = monthData.second.third.roundToTwoDecimals().toString()
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.debtsSum.collect {
                    val debtSum = it ?: return@collect
                    tvDebtSumMonth.text = debtSum.toString()
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
                    val months = it ?: return@collect
                    homeMonthAdapter.submitList(months)
                }
            }

            ivLeft.setOnClickListener {
                viewPagerHome.setCurrentItem(viewPagerHome.currentItem + 1, true)
                Timber.d("logs left click called")
            }

            ivRight.setOnClickListener {
                viewPagerHome.setCurrentItem(viewPagerHome.currentItem - 1, true)
                Timber.d("logs right click called")
            }

            viewPagerHome.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    viewModel.setMonthIndex(position)
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
                        val action =
                            HomeFragmentDirections.actionHomeFragmentToEditTransactionFragment(
                                null,
                                getString(R.string.new_spend),
                                TransactionType.OUTCOME.name
                            )
                        findNavController().navigate(action)
                    }
                    is HomeViewModel.HomeEvent.NavigateToAddIncomeScreen -> {
                        val action =
                            HomeFragmentDirections.actionHomeFragmentToEditTransactionFragment(
                                null,
                                getString(R.string.new_income),
                                TransactionType.INCOME.name
                            )
                        findNavController().navigate(action)
                    }
                    is HomeViewModel.HomeEvent.NavigateToAddDebtScreen -> {
                        val action = HomeFragmentDirections.actionFrontFragmentToEditDebtFragment(
                            getString(R.string.new_debt),
                            null
                        )
                        findNavController().navigate(action)
                    }
                    is HomeViewModel.HomeEvent.NavigateToSpendsScreen -> {
                        val action =
                            HomeFragmentDirections.actionFrontFragmentToTransactionsFragment()
                        findNavController().navigate(action)
                    }
                    is HomeViewModel.HomeEvent.NavigateToIncomesScreen -> {
                        val action =
                            HomeFragmentDirections.actionFrontFragmentToTransactionsFragment()
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








