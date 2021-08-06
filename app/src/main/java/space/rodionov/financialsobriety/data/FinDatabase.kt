package space.rodionov.financialsobriety.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.di.ApplicationScope
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Transaction::class, Category::class, Debt::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class FinDatabase : RoomDatabase() {

    abstract fun finDao() : FinDao

    class Callback @Inject constructor(
        private val database: Provider<FinDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope // CoroutineScope from Dagger, but we added @ApplicationScope to tell  dagger "it's not just any coroutineScope here, it's the ApplicationScope"
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().finDao()

            applicationScope.launch {
                dao.insertCategory(Category("Food", TransactionType.OUTCOME, getColors()[0]))
                dao.insertCategory(Category("Transport", TransactionType.OUTCOME, getColors()[1]))
                dao.insertCategory(Category("Communication", TransactionType.OUTCOME, getColors()[3]))


            }
        }
    }

}