package space.rodionov.financialsobriety.ui.categories

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import space.rodionov.financialsobriety.ui.CAT_DEL_RESULT_COMPLETE_DELETION

@AndroidEntryPoint
class DeleteAllTransInCatDialog : DialogFragment() {

    private val viewModel: DeleteAllTransInCatViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm deletion")
            .setMessage(
                if (viewModel.result == CAT_DEL_RESULT_COMPLETE_DELETION) "Do you really want to delete all transaction from the deleted category?"
                else "Move all transactions from deleted category to category ${viewModel.alterCatName}?"
            )
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Yes") { _, _ ->
                viewModel.onConfirmClick()
                setFragmentResult("cat_del_request", bundleOf("cat_del_result" to viewModel.result))
                val action =
                    DeleteAllTransInCatDialogDirections.actionDeleteAllTransInCatDialogToCategoriesFragment()
                findNavController().navigate(action)
            }
            .create()


}