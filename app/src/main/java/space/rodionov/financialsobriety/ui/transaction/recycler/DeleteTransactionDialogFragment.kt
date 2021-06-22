package space.rodionov.financialsobriety.ui.transaction.recycler

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import space.rodionov.financialsobriety.R
import timber.log.Timber

@AndroidEntryPoint
class DeleteTransactionDialogFragment : DialogFragment() {

    private val viewModel: DeleteTransactionViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle(requireContext().resources.getString(R.string.confirm_deletion))
            .setMessage(requireContext().resources.getString(R.string.really_delete_transaction))
            .setNegativeButton(requireContext().resources.getString(R.string.cancel_action), null)
            .setPositiveButton(requireContext().resources.getString(R.string.yes)) { _, _ ->
                if (viewModel.spend != null) {
                    viewModel.onConfirmClick(viewModel.spend!!)
                } else {
                    Timber.d("The Spend? id currently null")
                }
            }
            .create()
}