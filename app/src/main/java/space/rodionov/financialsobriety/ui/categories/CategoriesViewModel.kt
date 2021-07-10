package space.rodionov.financialsobriety.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import space.rodionov.financialsobriety.data.FinRepository
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val repo: FinRepository
) : ViewModel() {
    val categories = repo.getAllCategories().asLiveData()

}




