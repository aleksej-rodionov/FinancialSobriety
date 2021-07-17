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
import space.rodionov.financialsobriety.ui.ADD_CATEGORY_RESULT_OK
import space.rodionov.financialsobriety.ui.EDIT_CATEGORY_RESULT_OK
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val repo: FinRepository
) : ViewModel() {
    val categories = repo.getAllCategories().asLiveData()


    //=================EVENT CHANNEL================================

    private val categoriesEventChannel = Channel<CategoriesEvent>() // SEALED_EVENT 2,
    val categoriesEvent = categoriesEventChannel.receiveAsFlow()

    sealed class CategoriesEvent {
        object NavigateToAddCatScreen : CategoriesEvent()
        data class NavigateToEditCatScreen(val category: Category) : CategoriesEvent()
        data class ShowCatSavedConfirmMessage(val msg: String) : CategoriesEvent()
        data class ShowUndoDeleteCatMessage(val category: Category) : CategoriesEvent()
    }

    //=========FUNS==============================================

    fun onNewCatClick() = viewModelScope.launch {
        categoriesEventChannel.send(CategoriesEvent.NavigateToAddCatScreen)
    }

    fun onCatItemClick(category: Category) = viewModelScope.launch {
        categoriesEventChannel.send(CategoriesEvent.NavigateToEditCatScreen(category))
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_CATEGORY_RESULT_OK -> showCatSavedConfirmSnackbar("Category saved")
            EDIT_CATEGORY_RESULT_OK -> showCatSavedConfirmSnackbar("Category updated")
        }
    }

    private fun showCatSavedConfirmSnackbar(msg: String) = viewModelScope.launch {
        CategoriesEvent.ShowCatSavedConfirmMessage(msg)
    }

    fun onDeleteCat(category: Category) = viewModelScope.launch {
        repo.deleteCategory(category)
        categoriesEventChannel.send(CategoriesEvent.ShowUndoDeleteCatMessage(category))
    }

    fun onUndoDeleteCat(category: Category) = viewModelScope.launch {
        repo.insertCategory(category)
    }
}




