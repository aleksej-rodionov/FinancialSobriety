package space.rodionov.financialsobriety.ui.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.data.Category
import space.rodionov.financialsobriety.databinding.FragmentCategoriesBinding
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

            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, /*ItemTouchHelper.LEFT or */ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val category = catAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onDeleteCat(category)
                }
            }).attachToRecyclerView(recyclerView)

            btnAdd.setOnClickListener {
                viewModel.onNewCatClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.categories.collect {
                val cats = it ?: return@collect
                catAdapter.submitList(cats)
            }
        }

        setFragmentResultListener("add_edit_request") { _, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.categoriesEvent.collect { event ->
                when (event) {
                    CategoriesViewModel.CategoriesEvent.NavigateToAddCatScreen -> {
                        val action =
                            CategoriesFragmentDirections.actionCategoriesFragmentToEditCategoryFragment(
                                null,
                                "New category"
                            )
                        findNavController().navigate(action)
                    }
                    is CategoriesViewModel.CategoriesEvent.NavigateToEditCatScreen -> {
                        val action =
                            CategoriesFragmentDirections.actionCategoriesFragmentToEditCategoryFragment(
                                event.category,
                                "Edit category"
                            )
                        findNavController().navigate(action)
                    }
                    is CategoriesViewModel.CategoriesEvent.ShowCatSavedConfirmMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                    is CategoriesViewModel.CategoriesEvent.ShowUndoDeleteCatMessage -> {
                        Snackbar.make(requireView(), requireContext().resources.getString(R.string.category_deleted), Snackbar.LENGTH_LONG)
                            .setAction(requireContext().resources.getString(R.string.undo)) {
                                viewModel.onUndoDeleteCat(event.category)
                            }.show()
                    }
                }.exhaustive
            }
        }
    }

    override fun onItemClick(category: Category) {
        viewModel.onCatItemClick(category)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}






