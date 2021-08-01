package space.rodionov.financialsobriety.ui.transaction.barchart

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.databinding.FragmentBarChartsBinding
import space.rodionov.financialsobriety.ui.transaction.TransactionsViewModel

@AndroidEntryPoint
class BarChartsFragment : Fragment(R.layout.fragment_bar_charts) {

    private val viewModel: TransactionsViewModel by viewModels({ requireParentFragment() })
    private var _binding: FragmentBarChartsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBarChartsBinding.bind(view)

        val barChartAdapter = BarChartsAdapter(
            viewModel.catsWithTransactionsByType,
            viewLifecycleOwner.lifecycleScope
        )

        binding.apply {
            viewPagerBarCharts.adapter = barChartAdapter

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.yearListFlow.collect {
                    val years = it ?: return@collect
                    barChartAdapter.submitList(years)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}





