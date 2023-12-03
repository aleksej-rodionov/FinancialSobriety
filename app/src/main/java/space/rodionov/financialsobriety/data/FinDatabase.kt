package space.rodionov.financialsobriety.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.di.ApplicationScope
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Transaction::class, Category::class, Debt::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class FinDatabase : RoomDatabase() {

    abstract fun finDao() : FinDao

    object MIGRATION_1_2  : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE spend_table ADD COLUMN authorId TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE category_table ADD COLUMN authorId TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE debt_table ADD COLUMN authorId TEXT NOT NULL DEFAULT ''")
        }
    }

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
                dao.insertCategory(Category("Taxi", TransactionType.OUTCOME, getColors()[2]))
                dao.insertCategory(Category("Restaurants", TransactionType.OUTCOME, getColors()[4]))
                dao.insertCategory(Category("Dwelling", TransactionType.OUTCOME, getColors()[5]))
                dao.insertCategory(Category("Other", TransactionType.OUTCOME, getColors()[6]))
                dao.insertCategory(Category("Credits", TransactionType.OUTCOME, getColors()[7]))
                dao.insertCategory(Category("Taxes", TransactionType.OUTCOME, getColors()[9]))
                dao.insertCategory(Category("Communal", TransactionType.OUTCOME, getColors()[4]))
                dao.insertCategory(Category("Brother", TransactionType.OUTCOME, getColors()[8]))
                dao.insertCategory(Category("Myself", TransactionType.OUTCOME, getColors()[3]))
                dao.insertCategory(Category("Healthcare", TransactionType.OUTCOME, getColors()[2]))
            }
        }
    }
}