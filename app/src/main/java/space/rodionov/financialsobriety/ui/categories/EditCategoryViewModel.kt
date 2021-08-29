package space.rodionov.financialsobriety.ui.categories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.data.Category
import space.rodionov.financialsobriety.data.FinRepository
import space.rodionov.financialsobriety.data.TransactionType
import space.rodionov.financialsobriety.data.getColors
import space.rodionov.financialsobriety.di.ApplicationScope
import space.rodionov.financialsobriety.ui.ADD_CATEGORY_RESULT_OK
import space.rodionov.financialsobriety.ui.EDIT_CATEGORY_RESULT_OK
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EditCategoryViewModel @Inject constructor(
    private val repo: FinRepository,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val state: SavedStateHandle
) : ViewModel() {
    val categories = state.getLiveData<List<Category>>(
        "categories",
        repo.getAllCategories().asLiveData().value
    )
    val title = state.get<String>("title")
    private val onlyType = state.get<String>("onlyType")
    val category = state.get<Category>("category")
    var catName = state.get<String>("catName") ?: category?.catName ?: ""
        set(value) {
            field = value
            state.set("catName", value)
        }
    var catColor = state.get<Int>("catColor") ?: category?.catColor ?: getColors()[0]
        set(value) {
            field = value
            state.set("catColor", value)
        }
    var catType =
        state.get<TransactionType>("catType") ?: category?.catType ?: enumValueOf(onlyType ?: TransactionType.OUTCOME.name)
        set(value) {
            field = value
            state.set("catType", value)
        }

    //==============FLOWS==============================================

    val catListFlow = repo.getAllCategories().stateIn(viewModelScope, SharingStarted.Lazily, null)


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
        categories.value?.let {
            if (it.map {
                it.catName
                }.contains(catName)) {
                showInvalidInputMsg("Category with such name already exists")
                return
            }
        }
        if (category != null) {
            val updatedCat = category.copy(
                catName = catName,
                catType = catType,
                catColor = catColor
            )
//            updateCatNameInTransactions(updatedCat, category)
            updateCategory(updatedCat, category)
            Timber.d("updatedCat.name = ${updatedCat.catName}, oldCat.name = ${category.catName}")
        } else {
//            val newCat = Category(
//                catName = catName,
//                catType = catType,
//                catColor = chooseCatColor()
//            )
            createCategory()
        }
    }

//    private fun updateCatNameInTransactions(newCat: Category, oldCat: Category) = viewModelScope.launch {
//        repo.moveContentFromCatToCat(oldCat.catName, newCat.catName)
//    }

    private fun updateCategory(newCat: Category, oldCat: Category) = applicationScope.launch {

        repo.updateCategory(newCat)
        repo.moveContentFromCatToCat(oldCat.catName, newCat.catName)
        editCatEventChannel.send(EditCatEvent.NavigateBackWithResult(EDIT_CATEGORY_RESULT_OK))
    }

    private fun createCategory(category: Category) = viewModelScope.launch {
        repo.insertCategory(category)
        editCatEventChannel.send(EditCatEvent.NavigateBackWithResult(ADD_CATEGORY_RESULT_OK))
    }

    private fun createCategory() = viewModelScope.launch {
        val newCat = Category(
            catName = catName,
            catType = catType,
            catColor = chooseCatColor()
        )
        repo.insertCategory(newCat)
        Timber.d("logs color of new category = ${newCat.catColor}")
        editCatEventChannel.send(EditCatEvent.NavigateBackWithResult(ADD_CATEGORY_RESULT_OK))
    }

    private fun showInvalidInputMsg(msg: String) = viewModelScope.launch {
        editCatEventChannel.send(EditCatEvent.ShowInvalidInputMsg(msg))
    }

    private suspend fun chooseCatColor() : Int {
        val numberOfCategories = repo.getNumberOfCatsByType(catType)
        val colorIndex: Int
        if (numberOfCategories >= getColors().size) {
            colorIndex = numberOfCategories.toString().removeRange(0, numberOfCategories.toString().length - 1).toInt()
        } else {
            colorIndex = numberOfCategories
        }
        Timber.d("logs colorIndex = $colorIndex")
        return getColors()[colorIndex]
    }
}

