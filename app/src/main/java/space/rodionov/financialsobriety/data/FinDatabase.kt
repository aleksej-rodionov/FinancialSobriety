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
            database.execSQL("ALTER TABLE spend_table ADD COLUMN authorId NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE category_table ADD COLUMN authorId NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE debt_table ADD COLUMN authorId NOT NULL DEFAULT ''")
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
                dao.insertDebt(Debt("Tinkoff", 53692.27f))


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

                dao.insertSpend(Transaction(13479.95f, "Food", 1593550800000, null))
                dao.insertSpend(Transaction(12945.61f, "Food", 1596229200000, null))
                dao.insertSpend(Transaction(12772.02f, "Food", 1598907600000, null))
                dao.insertSpend(Transaction(14462.63f, "Food", 1601499600000, null))
                dao.insertSpend(Transaction(16105.11f, "Food", 1604178000000, null))
                dao.insertSpend(Transaction(15586.73f, "Food", 1606770000000, null))
                dao.insertSpend(Transaction(14737.91f, "Food", 1609448400000, null))
                dao.insertSpend(Transaction(17508.56f, "Food", 1612126800000, null))
                dao.insertSpend(Transaction(17616.7f, "Food", 1614546000000, null))
                dao.insertSpend(Transaction(17258.68f, "Food", 1617224400000, null))
                dao.insertSpend(Transaction(21121.23f, "Food", 1619816400000, null))
                dao.insertSpend(Transaction(18631.49f, "Food", 1622494800000, null))
                dao.insertSpend(Transaction(1171f, "Transport", 1593550800000, null))
                dao.insertSpend(Transaction(2482f, "Transport", 1596229200000, null))
                dao.insertSpend(Transaction(3376f, "Transport", 1598907600000, null))
                dao.insertSpend(Transaction(3693f, "Transport", 1601499600000, null))
                dao.insertSpend(Transaction(2215f, "Transport", 1604178000000, null))
                dao.insertSpend(Transaction(1571f, "Transport", 1606770000000, null))
                dao.insertSpend(Transaction(666f, "Transport", 1609448400000, null))
                dao.insertSpend(Transaction(417f, "Transport", 1612126800000, null))
                dao.insertSpend(Transaction(417f, "Transport", 1614546000000, null))
                dao.insertSpend(Transaction(627f, "Transport", 1617224400000, null))
                dao.insertSpend(Transaction(286f, "Transport", 1619816400000, null))
                dao.insertSpend(Transaction(0f, "Transport", 1622494800000, null))
                dao.insertSpend(Transaction(620f, "Communication", 1593550800000, null))
                dao.insertSpend(Transaction(700f, "Communication", 1596229200000, null))
                dao.insertSpend(Transaction(1322f, "Communication", 1598907600000, null))
                dao.insertSpend(Transaction(1358f, "Communication", 1601499600000, null))
                dao.insertSpend(Transaction(1339f, "Communication", 1604178000000, null))
                dao.insertSpend(Transaction(900f, "Communication", 1606770000000, null))
                dao.insertSpend(Transaction(500f, "Communication", 1609448400000, null))
                dao.insertSpend(Transaction(800f, "Communication", 1612126800000, null))
                dao.insertSpend(Transaction(650f, "Communication", 1614546000000, null))
                dao.insertSpend(Transaction(899f, "Communication", 1617224400000, null))
                dao.insertSpend(Transaction(949f, "Communication", 1619816400000, null))
                dao.insertSpend(Transaction(999f, "Communication", 1622494800000, null))
                dao.insertSpend(Transaction(100f, "Taxi", 1593550800000, null))
                dao.insertSpend(Transaction(141f, "Taxi", 1596229200000, null))
                dao.insertSpend(Transaction(1011f, "Taxi", 1598907600000, null))
                dao.insertSpend(Transaction(214f, "Taxi", 1601499600000, null))
                dao.insertSpend(Transaction(751f, "Taxi", 1604178000000, null))
                dao.insertSpend(Transaction(186f, "Taxi", 1606770000000, null))
                dao.insertSpend(Transaction(649f, "Taxi", 1609448400000, null))
                dao.insertSpend(Transaction(670f, "Taxi", 1612126800000, null))
                dao.insertSpend(Transaction(812f, "Taxi", 1614546000000, null))
                dao.insertSpend(Transaction(183f, "Taxi", 1617224400000, null))
                dao.insertSpend(Transaction(472f, "Taxi", 1619816400000, null))
                dao.insertSpend(Transaction(120f, "Taxi", 1622494800000, null))
                dao.insertSpend(Transaction(0f, "Restaurants", 1593550800000, null))
                dao.insertSpend(Transaction(0f, "Restaurants", 1596229200000, null))
                dao.insertSpend(Transaction(0f, "Restaurants", 1598907600000, null))
                dao.insertSpend(Transaction(320f, "Restaurants", 1601499600000, null))
                dao.insertSpend(Transaction(1291.5f, "Restaurants", 1604178000000, null))
                dao.insertSpend(Transaction(711.5f, "Restaurants", 1606770000000, null))
                dao.insertSpend(Transaction(3325.76f, "Restaurants", 1609448400000, null))
                dao.insertSpend(Transaction(2014.98f, "Restaurants", 1612126800000, null))
                dao.insertSpend(Transaction(1788.99f, "Restaurants", 1614546000000, null))
                dao.insertSpend(Transaction(2895.99f, "Restaurants", 1617224400000, null))
                dao.insertSpend(Transaction(3202f, "Restaurants", 1619816400000, null))
                dao.insertSpend(Transaction(261.96f, "Restaurants", 1622494800000, null))
                dao.insertSpend(Transaction(12201.4f, "Dwelling", 1593550800000, null))
                dao.insertSpend(Transaction(9500f, "Dwelling", 1596229200000, null))
                dao.insertSpend(Transaction(0f, "Dwelling", 1598907600000, null))
                dao.insertSpend(Transaction(0f, "Dwelling", 1601499600000, null))
                dao.insertSpend(Transaction(9500f, "Dwelling", 1604178000000, null))
                dao.insertSpend(Transaction(15500f, "Dwelling", 1606770000000, null))
                dao.insertSpend(Transaction(8000f, "Dwelling", 1609448400000, null))
                dao.insertSpend(Transaction(0f, "Dwelling", 1612126800000, null))
                dao.insertSpend(Transaction(8000f, "Dwelling", 1614546000000, null))
                dao.insertSpend(Transaction(8000f, "Dwelling", 1617224400000, null))
                dao.insertSpend(Transaction(8000f, "Dwelling", 1619816400000, null))
                dao.insertSpend(Transaction(8000f, "Dwelling", 1622494800000, null))
                dao.insertSpend(Transaction(1284f, "Other", 1593550800000, null))
                dao.insertSpend(Transaction(14444.37f, "Other", 1596229200000, null))
                dao.insertSpend(Transaction(664f, "Other", 1598907600000, null))
                dao.insertSpend(Transaction(1735.17f, "Other", 1601499600000, null))
                dao.insertSpend(Transaction(27229f, "Other", 1604178000000, null))
                dao.insertSpend(Transaction(1469.43f, "Other", 1606770000000, null))
                dao.insertSpend(Transaction(3072.89f, "Other", 1609448400000, null))
                dao.insertSpend(Transaction(10181.99f, "Other", 1612126800000, null))
                dao.insertSpend(Transaction(3833.8f, "Other", 1614546000000, null))
                dao.insertSpend(Transaction(396.48f, "Other", 1617224400000, null))
                dao.insertSpend(Transaction(3633.95f, "Other", 1619816400000, null))
                dao.insertSpend(Transaction(2285.59f, "Other", 1622494800000, null))
                dao.insertSpend(Transaction(6500.15f, "Credits", 1593550800000, null))
                dao.insertSpend(Transaction(5850f, "Credits", 1596229200000, null))
                dao.insertSpend(Transaction(6514.78f, "Credits", 1598907600000, null))
                dao.insertSpend(Transaction(6854.36f, "Credits", 1601499600000, null))
                dao.insertSpend(Transaction(11900f, "Credits", 1604178000000, null))
                dao.insertSpend(Transaction(11501.1f, "Credits", 1606770000000, null))
                dao.insertSpend(Transaction(12220.8f, "Credits", 1609448400000, null))
                dao.insertSpend(Transaction(14209.91f, "Credits", 1612126800000, null))
                dao.insertSpend(Transaction(7942.46f, "Credits", 1614546000000, null))
                dao.insertSpend(Transaction(11576.46f, "Credits", 1617224400000, null))
                dao.insertSpend(Transaction(4541.37f, "Credits", 1619816400000, null))
                dao.insertSpend(Transaction(4428.65f, "Credits", 1622494800000, null))
                dao.insertSpend(Transaction(0f, "Taxes", 1593550800000, null))
                dao.insertSpend(Transaction(0f, "Taxes", 1596229200000, null))
                dao.insertSpend(Transaction(0f, "Taxes", 1598907600000, null))
                dao.insertSpend(Transaction(0f, "Taxes", 1601499600000, null))
                dao.insertSpend(Transaction(5959f, "Taxes", 1604178000000, null))
                dao.insertSpend(Transaction(0f, "Taxes", 1606770000000, null))
                dao.insertSpend(Transaction(0f, "Taxes", 1609448400000, null))
                dao.insertSpend(Transaction(0f, "Taxes", 1612126800000, null))
                dao.insertSpend(Transaction(0f, "Taxes", 1614546000000, null))
                dao.insertSpend(Transaction(1474.8f, "Taxes", 1617224400000, null))
                dao.insertSpend(Transaction(2219.1f, "Taxes", 1619816400000, null))
                dao.insertSpend(Transaction(2223.9f, "Taxes", 1622494800000, null))
                dao.insertSpend(Transaction(19377.63f, "Communal", 1593550800000, null))
                dao.insertSpend(Transaction(10061.05f, "Communal", 1596229200000, null))
                dao.insertSpend(Transaction(0f, "Communal", 1598907600000, null))
                dao.insertSpend(Transaction(0f, "Communal", 1601499600000, null))
                dao.insertSpend(Transaction(5762.76f, "Communal", 1604178000000, null))
                dao.insertSpend(Transaction(7514.23f, "Communal", 1606770000000, null))
                dao.insertSpend(Transaction(17678.01f, "Communal", 1609448400000, null))
                dao.insertSpend(Transaction(18491.57f, "Communal", 1612126800000, null))
                dao.insertSpend(Transaction(17482.62f, "Communal", 1614546000000, null))
                dao.insertSpend(Transaction(12837.58f, "Communal", 1617224400000, null))
                dao.insertSpend(Transaction(12317.08f, "Communal", 1619816400000, null))
                dao.insertSpend(Transaction(12810.63f, "Communal", 1622494800000, null))
                dao.insertSpend(Transaction(544f, "Brother", 1593550800000, null))
                dao.insertSpend(Transaction(468f, "Brother", 1596229200000, null))
                dao.insertSpend(Transaction(0f, "Brother", 1598907600000, null))
                dao.insertSpend(Transaction(3000f, "Brother", 1601499600000, null))
                dao.insertSpend(Transaction(14872.4f, "Brother", 1604178000000, null))
                dao.insertSpend(Transaction(2822f, "Brother", 1606770000000, null))
                dao.insertSpend(Transaction(3045f, "Brother", 1609448400000, null))
                dao.insertSpend(Transaction(300f, "Brother", 1612126800000, null))
                dao.insertSpend(Transaction(14727.07f, "Brother", 1614546000000, null))
                dao.insertSpend(Transaction(1660f, "Brother", 1617224400000, null))
                dao.insertSpend(Transaction(14110.45f, "Brother", 1619816400000, null))
                dao.insertSpend(Transaction(20005.91f, "Brother", 1622494800000, null))
                dao.insertSpend(Transaction(1892.5f, "Myself", 1593550800000, null))
                dao.insertSpend(Transaction(8092f, "Myself", 1596229200000, null))
                dao.insertSpend(Transaction(2800f, "Myself", 1598907600000, null))
                dao.insertSpend(Transaction(3198f, "Myself", 1601499600000, null))
                dao.insertSpend(Transaction(3486f, "Myself", 1604178000000, null))
                dao.insertSpend(Transaction(188f, "Myself", 1606770000000, null))
                dao.insertSpend(Transaction(3238f, "Myself", 1609448400000, null))
                dao.insertSpend(Transaction(6243f, "Myself", 1612126800000, null))
                dao.insertSpend(Transaction(3491f, "Myself", 1614546000000, null))
                dao.insertSpend(Transaction(2398.99f, "Myself", 1617224400000, null))
                dao.insertSpend(Transaction(4498f, "Myself", 1619816400000, null))
                dao.insertSpend(Transaction(2199f, "Myself", 1622494800000, null))
                dao.insertSpend(Transaction(1956f, "Healthcare", 1593550800000, null))
                dao.insertSpend(Transaction(6194.6f, "Healthcare", 1596229200000, null))
                dao.insertSpend(Transaction(4686f, "Healthcare", 1598907600000, null))
                dao.insertSpend(Transaction(4280f, "Healthcare", 1601499600000, null))
                dao.insertSpend(Transaction(1854f, "Healthcare", 1604178000000, null))
                dao.insertSpend(Transaction(106.5f, "Healthcare", 1606770000000, null))
                dao.insertSpend(Transaction(4683.5f, "Healthcare", 1609448400000, null))
                dao.insertSpend(Transaction(3749.15f, "Healthcare", 1612126800000, null))
                dao.insertSpend(Transaction(2010.82f, "Healthcare", 1614546000000, null))
                dao.insertSpend(Transaction(2297.89f, "Healthcare", 1617224400000, null))
                dao.insertSpend(Transaction(1716.66f, "Healthcare", 1619816400000, null))
                dao.insertSpend(Transaction(8838.36f, "Healthcare", 1622494800000, null))



                dao.insertSpend(Transaction(18465.43f, "Food", 1625086800000, null))
                dao.insertSpend(Transaction(0f, "Transport", 1625086800000, null))
                dao.insertSpend(Transaction(899f, "Communication", 1625086800000, null))
                dao.insertSpend(Transaction(0f, "Taxi", 1625086800000, null))
                dao.insertSpend(Transaction(547.6f, "Restaurants", 1625086800000, null))
                dao.insertSpend(Transaction(8000f, "Dwelling", 1625086800000, null))
                dao.insertSpend(Transaction(1321.98f, "Other", 1625086800000, null))
                dao.insertSpend(Transaction(4535.85f, "Credits", 1625086800000, null))
                dao.insertSpend(Transaction(2175f, "Taxes", 1625086800000, null))
                dao.insertSpend(Transaction(11180.3f, "Communal", 1625086800000, null))
                dao.insertSpend(Transaction(19362.59f, "Brother", 1625086800000, null))
                dao.insertSpend(Transaction(1629f, "Myself", 1625086800000, null))
                dao.insertSpend(Transaction(182f, "Healthcare", 1625086800000, null))























            }
        }
    }
}