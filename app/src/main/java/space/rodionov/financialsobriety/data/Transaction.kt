package space.rodionov.financialsobriety.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "spend_table")
@Parcelize
data class Transaction(
    var sum: Float,
    var catName: String?,
    var timestamp: Long = 0L,
    var comment: String?,
    var type: TransactionType = TransactionType.OUTCOME,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable {
    val dateFormatted: String
        get() = sdf.format(timestamp)
}

val sdf = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
