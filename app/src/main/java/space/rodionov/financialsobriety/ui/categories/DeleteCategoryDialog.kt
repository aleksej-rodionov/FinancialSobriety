package space.rodionov.financialsobriety.ui.categories

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import space.rodionov.financialsobriety.data.Category
import space.rodionov.financialsobriety.databinding.FragmentDialogCategoryDeletionBinding
import space.rodionov.financialsobriety.ui.shared.ChooseCategoryAdapter
import space.rodionov.financialsobriety.util.exhaustive

private const val TAG = "DeleteCatDialog LOGS"

@AndroidEntryPoint
class DeleteCategoryDialog : DialogFragment(), ChooseCategoryAdapter.OnCatClickListener {
    private val viewModel: DeleteCategoryViewModel by viewModels()
    private var _binding: FragmentDialogCategoryDeletionBinding? = null
    private val binding get() = _binding!!
    private lateinit var chooseCatAdapter: ChooseCategoryAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        _binding = FragmentDialogCategoryDeletionBinding.inflate(LayoutInflater.from(context))

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val catName = viewModel.categoryName
        val catType = viewModel.categoryType
        chooseCatAdapter = ChooseCategoryAdapter(this, catName)

        binding.apply {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = chooseCatAdapter
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.getCategoriesByTypeExcept(enumValueOf(catType), catName).collect {
                    val catsByType = it ?: return@collect
                    chooseCatAdapter.submitList(catsByType)
                }
            }

            layoutNewCategory.setOnClickListener {

            }

            layoutDeleteAllContent.setOnClickListener {
                viewModel.deleteTransactionsByCat()
            }

            layoutCancelDeletion.setOnClickListener {
//                viewModel.onUndoDeleteCat(viewModel.category)
                this@DeleteCategoryDialog.dismiss()
            }
        }

        setFragmentResultListener("confirm_del_request") { _, bundle ->
            val result = bundle.getInt("confirm_del_result")
            Log.d(TAG, "onViewCreated: FragmentResult = $result")
            viewModel.onConfirmDelResult(result)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.deleteCatEvent.collect { event ->
                when (event) {
                    is DeleteCategoryViewModel.DeleteCategoryEvent.NavigateToDeleteAllTransByCat -> {
                        val action = DeleteCategoryDialogDirections.actionDeleteCategoryDialogToDeleteAllTransInCatDialog(event.catName)
                        findNavController().navigate(action)
                    }
                    is DeleteCategoryViewModel.DeleteCategoryEvent.NavigateBackWithDeletionResult -> {
                        setFragmentResult(
                            "confirm_del_request",
                            bundleOf("confirm_del_request" to event.result)
                        )
                        findNavController().popBackStack()
//                        onDestroyView()
                    }
                }.exhaustive
            }
        }
    }

    override fun onItemClick(category: Category) {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        viewModel.onUndoDeleteCat(viewModel.category)
        _binding = null
    }
}