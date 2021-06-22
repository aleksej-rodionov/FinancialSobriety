package space.rodionov.financialsobriety.ui.transaction.edittransaction

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.data.Category
import space.rodionov.financialsobriety.databinding.FragmentChooseCategoryBinding

@AndroidEntryPoint
class ChooseCategoryDialogFragment : DialogFragment(), ChooseCategoryAdapter.OnItemClickListener {

    private val viewModel: ChooseCategoryViewModel by viewModels()

    lateinit var binding: FragmentChooseCategoryBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_choose_category, null)
        binding = FragmentChooseCategoryBinding.bind(view)
        val chooseCatAdapter = ChooseCategoryAdapter(this)

        binding.apply {
            recyclerView.apply {
                adapter = chooseCatAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            return AlertDialog.Builder(requireContext())
                .setTitle(requireContext().resources.getString(R.string.choose_category))
                .setView(binding.recyclerView)
                .setNegativeButton(
                    requireContext().resources.getString(R.string.cancel_action),
                    null
                )
                .create()

        }
    }

    override fun onItemClick(category: Category) {
        TODO("Not yet implemented")
    }
}














