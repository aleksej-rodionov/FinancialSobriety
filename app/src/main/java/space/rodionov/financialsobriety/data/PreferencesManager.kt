package space.rodionov.financialsobriety.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PrefManager LOGS"
private val Context.dataStore by preferencesDataStore("user_prefs")

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

//    private val dataStore = context.createDataStore("user_prefs")
    private val prefsDataStore = context.dataStore



    private object PreferencesKeys {

    }
}