package space.rodionov.financialsobriety.ui.categories

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.data.Category
import space.rodionov.financialsobriety.databinding.FragmentDialogCategoryDeletionBinding
import space.rodionov.financialsobriety.ui.shared.ChooseCategoryAdapter

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
            .setTitle(requireContext().resources.getString(R.string.choose_category))
            .setView(binding.root)
            .setNegativeButton(requireContext().resources.getString(R.string.cancel_action), null)
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
        val catName = viewModel.categoryName.value
        val catType = viewModel.categoryType.value ?: "Outcome"
        chooseCatAdapter = ChooseCategoryAdapter(this, catName)

        binding.apply {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = chooseCatAdapter
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.getCategoriesByType(enumValueOf(catType)).collect {
                    val catsByType = it ?: return@collect
                    chooseCatAdapter.submitList(catsByType)
                }
            }

            layoutNewCategory.setOnClickListener {

            }

            layoutDeleteAllContent.setOnClickListener {

            }

            layoutCancelDeletion.setOnClickListener {
                this@DeleteCategoryDialog.dismiss()
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