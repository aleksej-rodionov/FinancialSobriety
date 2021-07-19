package space.rodionov.financialsobriety.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
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
                dao.insertCategory(Category("Food", TransactionType.OUTCOME))
                dao.insertCategory(Category("Transport", TransactionType.OUTCOME))
                dao.insertCategory(Category("Communication", TransactionType.OUTCOME))
                dao.insertCategory(Category("Healthcare", TransactionType.OUTCOME))
                dao.insertCategory(Category("Other", TransactionType.OUTCOME))
                dao.insertCategory(Category("Brother", TransactionType.OUTCOME))

                dao.insertCategory(Category("Hata Holmy", TransactionType.INCOME))
                dao.insertCategory(Category("Hata O1", TransactionType.INCOME))
                dao.insertCategory(Category("Hata 2shka", TransactionType.INCOME))

                dao.insertSpend(Transaction(5267f, "Healthcare", 1623960680, "Витаминки с iHerbs"))
                dao.insertSpend(Transaction(2202.18f, "Food", 1623777078, null))
                dao.insertSpend(Transaction(411.9f, "Food", 1623690678, null))
                dao.insertSpend(Transaction(4060f, "Brother", 1623960680, null))

                dao.insertDebt(Debt("Tinkoff", 57542.27f))
            }
        }
    }

}