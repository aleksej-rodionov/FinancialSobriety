package space.rodionov.financialsobriety.util

import androidx.annotation.StringRes

interface ResourceManager {

    fun getString(@StringRes stringResId: Int): String
}