package space.rodionov.financialsobriety.ui.transaction.edittransaction

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.data.Category
import space.rodionov.financialsobriety.databinding.FragmentDialogRecyclerBinding

private const val TAG = "ChooseCategoryDialog LOGS"

@AndroidEntryPoint
class ChooseCategoryDialogFragment : DialogFragment(), ChooseCategoryAdapter.OnItemClickListener {

    companion object {
        const val TAG = "chooseCategoryDialog"
        const val KEY_CAT_LIST = "keyCatList"
    }

    private val viewModel: EditTransactionViewModel by viewModels({ requireParentFragment() })
    private var _binding: FragmentDialogRecyclerBinding? = null
    private val binding get() = _binding!!
    private lateinit var chooseCatAdapter: ChooseCategoryAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        _binding = FragmentDialogRecyclerBinding.inflate(LayoutInflater.from(context))

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
        val category = viewModel.tCategoryName.value
        chooseCatAdapter = ChooseCategoryAdapter(this, category)


        binding.apply {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = chooseCatAdapter
            }
            viewModel.getCategoriesByType(enumValueOf(viewModel.tType)).observe(viewLifecycleOwner) {
                chooseCatAdapter.submitList(it)
                tvNoItems.isVisible = it.isNullOrEmpty()
            }
        }
    }

    override fun onItemClick(category: Category) {
        viewModel.onCategoryResult(category)
        this.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}














