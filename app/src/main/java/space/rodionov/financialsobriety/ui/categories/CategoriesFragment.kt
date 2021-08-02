package space.rodionov.financialsobriety.ui.categories

import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
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
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.flow.collect
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.data.Category
import space.rodionov.financialsobriety.databinding.FragmentCategoriesBinding
import space.rodionov.financialsobriety.util.exhaustive

private const val TAG = "CategoriesFragment LOGS"

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
                ItemTouchHelper.SimpleCallback(
                    0, ItemTouchHelper.RIGHT
                ) {
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

                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                    RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                        .addActionIcon(R.drawable.ic_delete)
                        .create()
                        .decorate();

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
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

        setFragmentResultListener("cat_del_request") { _, bundle ->
            val result = bundle.getInt("cat_del_result")
            viewModel.onCatDelResult(result)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.categoriesEvent.collect { event ->
                when (event) {
                    CategoriesViewModel.CategoriesEvent.NavigateToAddCatScreen -> {
                        val action =
                            CategoriesFragmentDirections.actionCategoriesFragmentToEditCategoryFragment(
                                null, "New category", null
                            )
                        findNavController().navigate(action)
                    }
                    is CategoriesViewModel.CategoriesEvent.NavigateToEditCatScreen -> {
                        val action =
                            CategoriesFragmentDirections.actionCategoriesFragmentToEditCategoryFragment(
                                event.category, "Edit category", null
                            )
                        findNavController().navigate(action)
                    }
                    is CategoriesViewModel.CategoriesEvent.ShowCatSavedConfirmMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    is CategoriesViewModel.CategoriesEvent.ShowUndoDeleteCatMessage -> { // THIS WILL BE REPLACED BY DEL CAT DIALOG
                        Snackbar.make(
                            requireView(),
                            requireContext().resources.getString(R.string.category_deleted),
                            Snackbar.LENGTH_LONG
                        )
                            .setAction(requireContext().resources.getString(R.string.undo)) {
                                viewModel.onUndoDeleteCat(event.category)
                            }.show()
                    }
                    is CategoriesViewModel.CategoriesEvent.NavigateToDelCatDialog -> {
                        val action = CategoriesFragmentDirections.actionCategoriesFragmentToDeleteCategoryDialog(event.category)
                        findNavController().navigate(action)
                    }
                    is CategoriesViewModel.CategoriesEvent.ShowCatDeletedConfirmMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
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






