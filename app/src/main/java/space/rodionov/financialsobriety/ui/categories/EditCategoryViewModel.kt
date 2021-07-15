package space.rodionov.financialsobriety.ui.categories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.data.Category
import space.rodionov.financialsobriety.data.FinRepository
import space.rodionov.financialsobriety.data.TransactionType
import space.rodionov.financialsobriety.ui.ADD_CATEGORY_RESULT_OK
import space.rodionov.financialsobriety.ui.EDIT_CATEGORY_RESULT_OK
import javax.inject.Inject

@HiltViewModel
class EditCategoryViewModel @Inject constructor(
    private val repo: FinRepository,
    private val state: SavedStateHandle
) : ViewModel() {
    val title = state.get<String>("title")
    val category = state.get<Category>("category")
    var catName = state.get<String>("catName") ?: category?.catName ?: ""
        set(value) {
            field = value
            state.set("catName", value)
        }
    var catType =
        state.get<TransactionType>("catType") ?: category?.catType ?: TransactionType.OUTCOME
        set(value) {
            field = value
            state.set("catType", value)
        }

    //==========EVENT CHANNEL==========================================
    private val editCatEventChannel = Channel<EditCatEvent>()
    val editCatEvent = editCatEventChannel.receiveAsFlow()

    sealed class EditCatEvent {
        data class NavigateBackWithResult(val result: Int) : EditCatEvent()
        data class ShowInvalidInputMsg(val msg: String) : EditCatEvent()
    }

    //=======================METHODS=======================================
    fun onSaveClick() {
        if (catName.isBlank()) {
            showInvalidInputMsg("Enter category name")
            return
        }
        if (category != null) {
            val updatedCat = category.copy(catName = catName, catType = catType)
            updateCategory(updatedCat)
        } else {
            val newCat = Category(catName, catType)
            createCategory(newCat)
        }
    }

    private fun createCategory(category: Category) = viewModelScope.launch {
        repo.insertCategory(category)
        editCatEventChannel.send(EditCatEvent.NavigateBackWithResult(ADD_CATEGORY_RESULT_OK))
    }

    private fun updateCategory(category: Category) = viewModelScope.launch {
        repo.updateCategory(category)
        editCatEventChannel.send(EditCatEvent.NavigateBackWithResult(EDIT_CATEGORY_RESULT_OK))
    }

    private fun showInvalidInputMsg(msg: String) = viewModelScope.launch {
        editCatEventChannel.send(EditCatEvent.ShowInvalidInputMsg(msg))
    }
}