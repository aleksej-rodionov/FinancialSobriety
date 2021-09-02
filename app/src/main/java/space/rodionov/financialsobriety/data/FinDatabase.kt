package space.rodionov.financialsobriety.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.mikephil.charting.utils.ColorTemplate
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


















            }
        }
    }
}