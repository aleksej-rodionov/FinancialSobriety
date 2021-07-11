package space.rodionov.financialsobriety.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.data.Category
import space.rodionov.financialsobriety.data.FinRepository
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val repo: FinRepository
) : ViewModel() {
    val categories = repo.getAllCategories().asLiveData()

    private val categoriesEventChannel = Channel<CategoriesEvent>() // SEALED_EVENT 2,
    val categoriesEvent = categoriesEventChannel.receiveAsFlow()


    //=========EVENTS==============================================

    fun onNewCatClick() = viewModelScope.launch {
        categoriesEventChannel.send(CategoriesEvent.NavigateToAddCatScreen)
    }


    sealed class CategoriesEvent {
        object NavigateToAddCatScreen : CategoriesEvent()
        data class NavigateToEditCatScreen(val category: Category) : CategoriesEvent()
        data class ShowCatSavedConfirmMessage(val msg: String) : CategoriesEvent()
        data class ShowUndoDeleteCatMessage(val category: Category) : CategoriesEvent()
    }
}




