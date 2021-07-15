package space.rodionov.financialsobriety.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "category_table")
@Parcelize
data class Category(
    val catName: String,
    val catType: TransactionType,
    @PrimaryKey(autoGenerate = true) val catId: Int = 0
) : Parcelable {

    override fun toString(): String {
        return "$catName ($catType)"
    }
}

enum class TransactionType {
    INCOME, OUTCOME
}