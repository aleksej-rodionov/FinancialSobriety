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
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.ui.CAT_DEL_RESULT_COMPLETE_DELETION

@AndroidEntryPoint
class DeleteAllTransInCatDialog : DialogFragment() {

    private val viewModel: DeleteAllTransInCatViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle(resources.getString(R.string.confirm_deletion))
            .setMessage(
                if (viewModel.result == CAT_DEL_RESULT_COMPLETE_DELETION) resources.getString(R.string.really_delete_transaction)
                else "${resources.getString(R.string.move_content_to_category)} ${viewModel.alterCatName}?"
            )
            .setNegativeButton(resources.getString(R.string.cancel_action), null)
            .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                viewModel.onConfirmClick()
                setFragmentResult("cat_del_request", bundleOf("cat_del_result" to viewModel.result))
                val action =
                    DeleteAllTransInCatDialogDirections.actionDeleteAllTransInCatDialogToCategoriesFragment()
                findNavController().navigate(action)
            }
            .create()
}