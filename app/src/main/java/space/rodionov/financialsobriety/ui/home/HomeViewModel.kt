package space.rodionov.financialsobriety.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.data.FinRepository
import space.rodionov.financialsobriety.data.PrefManager
import space.rodionov.financialsobriety.ui.ADD_TRANSACTION_RESULT_OK
import space.rodionov.financialsobriety.ui.EDIT_TRANSACTION_RESULT_OK
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: FinRepository,
    private val prefManager: PrefManager
) : ViewModel() {




    //==================EVENT CHANNEL AND SEALED CLASS==================
    private val homeEventChannel = Channel<HomeEvent>()
    val homeEvent = homeEventChannel.receiveAsFlow()

    sealed class HomeEvent {
        object NavigateToSpendsScreen : HomeEvent()
        object NavigateToIncomesScreen : HomeEvent()
        object NavigateToDebtsScreen : HomeEvent()
        object NavigateToAddSpendScreen : HomeEvent()
        object NavigateToAddIncomeScreen : HomeEvent()
        object NavigateToAddDebtScreen : HomeEvent()
        data class ShowTransactionSavedConfirmationMessage(val msg: String) : HomeEvent()
    }

    //========================================METHODS===================================

    fun onSpendsClick(typeName: String) = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.NavigateToSpendsScreen)
        prefManager.updateTypeName(typeName)
    }

    fun onIncomesClick(typeName: String) = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.NavigateToIncomesScreen)
        prefManager.updateTypeName(typeName)
    }

    fun onDebtsClick() = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.NavigateToDebtsScreen)
    }

    fun onAddSpendClick() = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.NavigateToAddSpendScreen)
    }

    fun onAddIncomeClick() = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.NavigateToAddIncomeScreen)
    }

    fun onAddDebtClick() = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.NavigateToAddDebtScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TRANSACTION_RESULT_OK ->showTransactionSavedConfirmationMessage("Transaction added")
            EDIT_TRANSACTION_RESULT_OK ->showTransactionSavedConfirmationMessage("Transaction updated")
        }
    }

    fun showTransactionSavedConfirmationMessage(text: String) = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.ShowTransactionSavedConfirmationMessage(text))
    }



}








