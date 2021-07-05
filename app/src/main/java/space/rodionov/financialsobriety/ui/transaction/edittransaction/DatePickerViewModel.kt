package space.rodionov.financialsobriety.ui.transaction.edittransaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.data.FinRepository
import space.rodionov.financialsobriety.di.ApplicationScope
import javax.inject.Inject

@HiltViewModel
class DatePickerViewModel @Inject constructor(
    val repo: FinRepository,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val state: SavedStateHandle
) : ViewModel() {

    var dateFormatted = state.get<String>("dateFormatted") ?: ""

    private val datePickerEventChannel = Channel<DatePickerEvent>()
    val datePickerEvent = datePickerEventChannel.receiveAsFlow()

//==============================================================

    fun onDateChosen(dateFormatted: String) = viewModelScope.launch {
        datePickerEventChannel.send(DatePickerEvent.NavigateBackWithResult(dateFormatted))
    }

    sealed class DatePickerEvent {
        data class NavigateBackWithResult(val dateFormatted: String) : DatePickerEvent()
    }
}





