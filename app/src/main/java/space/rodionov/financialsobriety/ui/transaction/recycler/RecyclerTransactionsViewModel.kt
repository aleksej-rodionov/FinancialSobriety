package space.rodionov.financialsobriety.ui.transaction.recycler

import androidx.hilt.Assisted
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
import space.rodionov.financialsobriety.data.Spend
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




    fun onUndoDeleteClick(spend: Spend) = viewModelScope.launch {
        repo.insertSpend(spend)
    }


    //=======================================================================================

    fun onTransactionSelected(spend: Spend) = viewModelScope.launch {
        recTransEventChannel.send(RecTransEvent.NavigateToEditTransactionScreen(spend))
    }

    fun onTransactionLongTouched(spend: Spend) = viewModelScope.launch {
        recTransEventChannel.send(RecTransEvent.NavigateToDeleteTransactionScreen(spend))
    }

    fun onTransactionDeleted(spend: Spend) = viewModelScope.launch {
        repo.deleteSpend(spend)
        recTransEventChannel.send(RecTransEvent.ShowUndoDeleteTransactionMessage(spend))
    }

    fun addTransaction() = viewModelScope.launch {
        recTransEventChannel.send(RecTransEvent.NavigateToAddTransactionScreen)
    }

    sealed class RecTransEvent {
        object NavigateToAddTransactionScreen : RecTransEvent()
        data class NavigateToEditTransactionScreen(val spend: Spend) : RecTransEvent()
        data class ShowUndoDeleteTransactionMessage(val spend: Spend) : RecTransEvent()
        data class NavigateToDeleteTransactionScreen(val spend: Spend) : RecTransEvent()
    }
}











