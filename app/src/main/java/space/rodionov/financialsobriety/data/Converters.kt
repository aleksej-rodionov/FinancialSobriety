package space.rodionov.financialsobriety.data

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }

    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return enumValueOf(value)
    }
}