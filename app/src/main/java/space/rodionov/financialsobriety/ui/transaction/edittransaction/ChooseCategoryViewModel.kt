package space.rodionov.financialsobriety.ui.transaction.edittransaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.data.Category
import space.rodionov.financialsobriety.data.FinRepository
import space.rodionov.financialsobriety.di.ApplicationScope
import javax.inject.Inject

@HiltViewModel
class ChooseCategoryViewModel @Inject constructor(
    private val repo: FinRepository,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val state: SavedStateHandle
) : ViewModel() {

    val catName = state.get<String>("categoryName") ?: ""

    private val chooseCategoryEventChannel = Channel<ChooseCategoryEvent>()
    val chooseCategoryEvent = chooseCategoryEventChannel.receiveAsFlow()

    val categories = repo.getAllCategories()/*.asLiveData()*/
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

//=======================================================

    fun onCategoryChosen(category: Category) = viewModelScope.launch {
        chooseCategoryEventChannel.send(ChooseCategoryEvent.NavigateBackWithResult(category.catName))
    }

    sealed class ChooseCategoryEvent {
        data class NavigateBackWithResult(val catName: String) : ChooseCategoryEvent()
    }
}

/**
 *
 * 0.1. разобраться че с первого раза айтемы не отображаются + с пнрвого раза категория выбранная не отображается
 * 1. Чтобы календарь сохранялся при повороте экрана
 *
 * 3. rest of events in recycler fragm
 *
 *
 */














