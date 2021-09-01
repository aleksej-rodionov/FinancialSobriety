package space.rodionov.financialsobriety.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "debt_table")
@Parcelize
data class Debt(
    var debtName: String,
    var debtSum: Float,
    @PrimaryKey(autoGenerate = true) val debtId: Int = 0,
    var authorId: String = ""
) : Parcelable {
    override fun toString(): String {
        return "$debtName (-$debtSum)"
    }
}