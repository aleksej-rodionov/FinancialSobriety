package space.rodionov.financialsobriety.ui.shared

import space.rodionov.financialsobriety.data.Month
import space.rodionov.financialsobriety.data.Year
import java.util.*

//=======================BARCHARTS FUNCTIONS==================================

fun createYearList() : List<Year> {
    val yearList = mutableListOf<Year>()
    val calToday = Calendar.getInstance()
    calToday.timeInMillis = System.currentTimeMillis()
    val curYear = calToday.get(Calendar.YEAR)
    for (yyyy in 1970..curYear) {
        yearList.add(Year(yyyy.toString()))
    }
    yearList.reverse()
    return yearList
}

//========================PIECHARTS FUNCTIONS=====================================

fun createMonthList(): List<Month> {
    val monthList = mutableListOf<Month>()
    val calToday = Calendar.getInstance()
    calToday.timeInMillis = System.currentTimeMillis()
    val curYear = calToday.get(Calendar.YEAR)
    val curMonth = calToday.get(Calendar.MONTH)

    for (y in 1970..curYear) {
        if (y < curYear) {
            for (m in 1..12) {
                var mString = m.toString()
                if (mString.length < 2) mString = "0$mString"
                val month = Month("$mString/$y")
                monthList.add(month)
            }
        } else {
            for (m in 1..curMonth + 1) {
                var mString = m.toString()
                if (mString.length < 2) mString = "0$mString"
                val month = Month("$mString/$y")
                monthList.add(month)
            }
        }
    }
    monthList.reverse()
    return monthList
}