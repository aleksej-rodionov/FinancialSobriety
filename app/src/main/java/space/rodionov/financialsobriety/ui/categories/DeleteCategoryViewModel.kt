package space.rodionov.financialsobriety.ui.categories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.data.Category
import space.rodionov.financialsobriety.data.FinRepository
import space.rodionov.financialsobriety.data.TransactionType
import javax.inject.Inject

@HiltViewModel
class DeleteCategoryViewModel @Inject constructor(
    private val repo: FinRepository,
    private val state: SavedStateHandle
) : ViewModel() {
    val category = state.get<Category>("category")
    var categoryName = state.getLiveData("categoryName", category?.catName ?: "")
    var categoryType = state.getLiveData("categoryType", category?.catType?.name ?: "Outcome")

    fun getCategoriesByType(type: TransactionType) = repo.getCategoriesByType(type)
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    //=========================EVENT CHANNEL=====================================================



    sealed class DeleteCategoryEvent {

    }



    fun onUndoDeleteCat(category: Category) = viewModelScope.launch {
        repo.insertCategory(category)
    }
}





