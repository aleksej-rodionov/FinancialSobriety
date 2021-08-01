package space.rodionov.financialsobriety.ui.transaction.diagram

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.databinding.FragmentDiagramsBinding
import space.rodionov.financialsobriety.ui.transaction.TransactionsViewModel

@AndroidEntryPoint
class DiagramsFragment : Fragment(R.layout.fragment_diagrams) {

    private val viewModel: TransactionsViewModel by viewModels({requireParentFragment()})
    private var _binding: FragmentDiagramsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDiagramsBinding.bind(view)

        val diagramAdapter = DiagramsAdapter(
            viewModel.catsWithTransactionsByType,
            viewLifecycleOwner.lifecycleScope
        )

        binding.apply {
            viewPagerDiagram.adapter = diagramAdapter

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.monthListFlow.collect {
                    val months = it ?: return@collect
                    diagramAdapter.submitList(months)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




