package space.rodionov.financialsobriety.ui.transaction.recycler

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.data.FinRepository
import space.rodionov.financialsobriety.data.Transaction
import space.rodionov.financialsobriety.ui.ADD_TRANSACTION_RESULT_OK
import space.rodionov.financialsobriety.ui.EDIT_TRANSACTION_RESULT_OK
import javax.inject.Inject

@HiltViewModel
class RecyclerTransactionsViewModel @Inject constructor(
    private val repo: FinRepository,
//private val preferencesRepo: PreferencesRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    private val recTransEventChannel = Channel<RecTransEvent>()
    val recTransEvent = recTransEventChannel.receiveAsFlow()



    val spends = repo.getAllSpends()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)




    fun onUndoDeleteClick(transaction: Transaction) = viewModelScope.launch {
        repo.insertSpend(transaction)
    }


    //=======================================================================================

    fun onTransactionSelected(transaction: Transaction) = viewModelScope.launch {
        recTransEventChannel.send(RecTransEvent.NavigateToEditTransactionScreen(transaction))
    }

    fun onTransactionDeleted(transaction: Transaction) = viewModelScope.launch {
        repo.deleteSpend(transaction)
        recTransEventChannel.send(RecTransEvent.ShowUndoDeleteTransactionMessage(transaction))
    }

    fun addTransaction() = viewModelScope.launch {
        recTransEventChannel.send(RecTransEvent.NavigateToAddTransactionScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            EDIT_TRANSACTION_RESULT_OK -> showEditTransConfirmSnackbar("Transaction successfully edited")
            ADD_TRANSACTION_RESULT_OK -> showEditTransConfirmSnackbar("Transaction successfully added")
        }
    }

    private fun showEditTransConfirmSnackbar(msg: String) = viewModelScope.launch {
        recTransEventChannel.send(RecTransEvent.ShowEditTransConfirmMsg(msg))
    }

    fun onDeleteTransaction(transaction: Transaction) = viewModelScope.launch {
        repo.deleteSpend(transaction)
        recTransEventChannel.send(RecTransEvent.ShowUndoDeleteTransactionMessage(transaction))
    }

    fun undoDeleteClick(transaction: Transaction) = viewModelScope.launch {
        repo.insertSpend(transaction)
    }

    sealed class RecTransEvent {
        object NavigateToAddTransactionScreen : RecTransEvent()
        data class NavigateToEditTransactionScreen(val transaction: Transaction) : RecTransEvent()
        data class ShowUndoDeleteTransactionMessage(val transaction: Transaction) : RecTransEvent()
        data class ShowEditTransConfirmMsg(val msg: String) : RecTransEvent()
    }
}











