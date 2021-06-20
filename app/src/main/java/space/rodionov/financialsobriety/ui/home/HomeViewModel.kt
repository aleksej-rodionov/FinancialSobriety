package space.rodionov.financialsobriety.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.data.FinRepository
import space.rodionov.financialsobriety.ui.ADD_TRANSACTION_RESULT_OK
import space.rodionov.financialsobriety.ui.EDIT_TRANSACTION_RESULT_OK
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: FinRepository
) : ViewModel() {

    private val homeEventChannel = Channel<HomeEvent>()
    val homeEvent = homeEventChannel.receiveAsFlow()




    fun onAddSpendClick() = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.NavigateToAddSpendScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TRANSACTION_RESULT_OK ->showTransactionSavedConfirmationMessage("Task added")
            EDIT_TRANSACTION_RESULT_OK ->showTransactionSavedConfirmationMessage("Task updated")
        }
    }

    fun showTransactionSavedConfirmationMessage(text: String) = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.ShowTransactionSavedConfirmationMessage(text))
    }

    sealed class HomeEvent {
        object NavigateToAddSpendScreen : HomeEvent()
        data class ShowTransactionSavedConfirmationMessage(val msg: String) : HomeEvent()
    }

}








