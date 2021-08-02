package space.rodionov.financialsobriety.ui.categories

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import space.rodionov.financialsobriety.data.Category
import space.rodionov.financialsobriety.databinding.FragmentDialogCategoryDeletionBinding
import space.rodionov.financialsobriety.ui.shared.DialogChooseItemAdapter
import space.rodionov.financialsobriety.util.exhaustive

private const val TAG = "DeleteCatDialog LOGS"

@AndroidEntryPoint
class DeleteCategoryDialog : DialogFragment(), DialogChooseItemAdapter.OnCatClickListener {
    private val viewModel: DeleteCategoryViewModel by viewModels()
    private var _binding: FragmentDialogCategoryDeletionBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialogChooseCatAdapter: DialogChooseItemAdapter

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
        dialogChooseCatAdapter = DialogChooseItemAdapter(this, catName)

        binding.apply {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = dialogChooseCatAdapter
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.getCategoriesByTypeExcept(enumValueOf(catType), catName).collect {
                    val catsByType = it ?: return@collect
                    dialogChooseCatAdapter.submitList(catsByType)
                }
            }

            layoutDeleteAllContent.setOnClickListener {
                viewModel.deleteTransactionsByCat()
            }

            layoutCancelDeletion.setOnClickListener {
                viewModel.onUndoDeleteCat()
                this@DeleteCategoryDialog.dismiss()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.deleteCatEvent.collect { event ->
                when (event) {
                    is DeleteCategoryViewModel.DeleteCategoryEvent.NavigateBackWithDeletionResult -> {
                        setFragmentResult("cat_del_request", bundleOf("cat_del_result" to event.result))
                        findNavController().popBackStack()
                    }
                }.exhaustive
            }
        }
    }

    override fun onItemClick(category: Category) {
        viewModel.onAlterCatChosen(category)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}