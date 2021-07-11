package space.rodionov.financialsobriety.ui.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.data.Category
import space.rodionov.financialsobriety.databinding.FragmentCategoriesBinding
import space.rodionov.financialsobriety.databinding.FragmentDialogRecyclerBinding
import space.rodionov.financialsobriety.util.exhaustive

@AndroidEntryPoint
class CategoriesFragment : Fragment(R.layout.fragment_categories),
    CategoriesAdapter.OnItemClickListener {
    private val viewModel: CategoriesViewModel by viewModels()

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCategoriesBinding.bind(view)

        val catAdapter = CategoriesAdapter(this)
        binding.apply {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = catAdapter
                setHasFixedSize(true)
            }

            btnAdd.setOnClickListener {
                viewModel.onNewCatClick()
            }
        }

        viewModel.categories.observe(viewLifecycleOwner) {
            catAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.categoriesEvent.collect {
                when (it) {
                    CategoriesViewModel.CategoriesEvent.NavigateToAddCatScreen -> {
                        val action =
                            CategoriesFragmentDirections.actionCategoriesFragmentToEditCategoryFragment(
                                null,
                                "New category"
                            )
                        findNavController().navigate(action)
                    }
                    is CategoriesViewModel.CategoriesEvent.NavigateToEditCatScreen -> TODO()
                    is CategoriesViewModel.CategoriesEvent.ShowCatSavedConfirmMessage -> TODO()
                    is CategoriesViewModel.CategoriesEvent.ShowUndoDeleteCatMessage -> TODO()
                }.exhaustive
            }
        }
    }


    override fun onItemClick(category: Category) {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}






