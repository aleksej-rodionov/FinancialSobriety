package space.rodionov.financialsobriety.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "category_table")
@Parcelize
data class Category(
    @PrimaryKey(autoGenerate = false)
    val catName: String,
    val catType: TransactionType,
    val catColor: Int,
    val catShown: Boolean = true
) : Parcelable {

    override fun toString(): String {
        return "$catName ($catType)"
    }
}

enum class TransactionType {
    INCOME, OUTCOME
}

fun getColors() : IntArray = ColorTemplate.VORDIPLOM_COLORS + ColorTemplate.LIBERTY_COLORS

