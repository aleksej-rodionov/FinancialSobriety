package space.rodionov.financialsobriety.ui.categories

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import space.rodionov.financialsobriety.data.FinRepository
import javax.inject.Inject

@HiltViewModel
class EditCategoryViewModel @Inject constructor(
    private val repo: FinRepository
) : ViewModel() {
}