package space.rodionov.financialsobriety.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PrefManager LOGS"
private val Context.dataStore by preferencesDataStore("user_prefs")

@Singleton
class PrefManager @Inject constructor(@ApplicationContext context: Context) {

    //    private val dataStore = context.createDataStore("user_prefs")
    private val prefsDataStore = context.dataStore

    private object PrefKeys {
        val CAT_FILTER_JSON = stringPreferencesKey("cat_filter_json")
        val TYPE_NAME = stringPreferencesKey("type_name")
        val FIRST_DATE = stringPreferencesKey("first_date")
        val LAST_DATE = stringPreferencesKey("last_date")
    }

    //============================GETTERS=================================

    val catFilterJsonFlow = prefsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {
            val catFilterJson = it[PrefKeys.CAT_FILTER_JSON] ?: ""
            catFilterJson
        }

    val typeNameFlow = prefsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {
            val typeName = it[PrefKeys.TYPE_NAME] ?: TransactionType.OUTCOME.name
            typeName
        }

    val firstDateFlow = prefsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {
            val firstDate = it[PrefKeys.FIRST_DATE] ?: ""
            firstDate
        }

    val lastDateFlow = prefsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {
            val lastDate = it[PrefKeys.LAST_DATE] ?: ""
            lastDate
        }

    //==============================SETTERS==================================

    suspend fun updateCatFilterJson(catFilterJson: String) {
        prefsDataStore.edit {
            it[PrefKeys.CAT_FILTER_JSON] = catFilterJson
        }
    }

    suspend fun updateTypeName(typeName: String) {
        prefsDataStore.edit {
            it[PrefKeys.TYPE_NAME] = typeName
        }
        Timber.d("LOGS typeName now is ...")
    }

    suspend fun updateFirstDate(firstDate: String) {
        prefsDataStore.edit {
            it[PrefKeys.FIRST_DATE] = firstDate
        }
    }

    suspend fun updateLastDate(lastDate: String) {
        prefsDataStore.edit {
            it[PrefKeys.LAST_DATE] = lastDate
        }
    }
}










