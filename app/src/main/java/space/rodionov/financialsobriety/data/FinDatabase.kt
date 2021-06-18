package space.rodionov.financialsobriety.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.di.ApplicationScope
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Spend::class, Category::class], version = 1, exportSchema = false)
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
                dao.insertCategory(Category("Food"))
                dao.insertCategory(Category("Transport"))
                dao.insertCategory(Category("Communitaion"))
                dao.insertCategory(Category("Healthcare"))
                dao.insertCategory(Category("Other"))
                dao.insertCategory(Category("Brother"))

                dao.insertSpend(Spend(5267f, "Healthcare", 1623960680, "Элицея и др"))
                dao.insertSpend(Spend(2202.18f, "Food", 1623777078, null))
                dao.insertSpend(Spend(411.9f, "Food", 1623690678, null))
                dao.insertSpend(Spend(4060f, "Brother", 1623960680, null))

            }

        }
    }

}