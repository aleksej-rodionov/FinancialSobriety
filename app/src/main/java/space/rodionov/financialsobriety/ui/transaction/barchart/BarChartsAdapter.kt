package space.rodionov.financialsobriety.ui.transaction.barchart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.StackedValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.data.CategoryWithTransactions
import space.rodionov.financialsobriety.data.Month
import space.rodionov.financialsobriety.data.Transaction
import space.rodionov.financialsobriety.data.Year
import space.rodionov.financialsobriety.databinding.ItemBarChartBinding
import space.rodionov.financialsobriety.util.roundToTwoDecimals
import timber.log.Timber
import java.util.*


class BarChartsAdapter(
    private val catsWithTransactionsFlow: StateFlow<List<CategoryWithTransactions>?>,
    private val scope: CoroutineScope
) : ListAdapter<Year, BarChartsAdapter.BarChartViewHolder>(BarChartComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarChartViewHolder {
        val binding = ItemBarChartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BarChartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BarChartViewHolder, position: Int) {
        val curYear = getItem(position)
        holder.bind(curYear)
    }

    inner class BarChartViewHolder(private val binding: ItemBarChartBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(year: Year) {
            binding.apply {
                tvYear.text = year.yyyy
                barChart.setupBarChart()
                scope.launch {
                    catsWithTransactionsFlow.collect {
                        val catsWithTransactions = it ?: return@collect
                        val barEntries = createYearBarEntryList(catsWithTransactions, year)
                        barChart.data?.clearValues()
                        barChart.loadBarChartData(barEntries, catsWithTransactions)
                    }
                }
            }
        }
    }

    class BarChartComparator : DiffUtil.ItemCallback<Year>() {
        override fun areItemsTheSame(oldItem: Year, newItem: Year) =
            oldItem.yyyy == newItem.yyyy

        override fun areContentsTheSame(oldItem: Year, newItem: Year) = oldItem == newItem
    }

//==========================CREATING ENTRIES FUNS===================================

    fun createYearBarEntryList(
        categoriesWithTransactions: List<CategoryWithTransactions>,
        year: Year
    ): List<BarEntry> {
        val monthsOfYear = year.getMonthsOfYear()
        val barEntries = mutableListOf<BarEntry>()

        monthsOfYear.forEachIndexed { index, month ->
            val sumsByCats = mutableListOf<Float>()
            for (cwt in categoriesWithTransactions) {
                val sumByCat = month.getSumOfTransactionsInMonth(cwt.transactions)
                sumsByCats.add(sumByCat)
            }
            barEntries.add(BarEntry((index + 1).toFloat(), sumsByCats.toFloatArray()))
        }
        return barEntries
    }

    private fun Month.getSumOfTransactionsInMonth(transactions: List<Transaction>) : Float {
        val transactionsOfMonth = this.getTransactionsOfMonth(transactions)
        val sumOfMonth = transactionsOfMonth.map {
            it.sum
        }.sum().roundToTwoDecimals()
        return sumOfMonth
    }

    //====================================BARCHART FUNCTIONS==========================================

    private fun BarChart.setupBarChart() {

//        barChart.getAxisLeft().setAxisMaximum(100000f);      //Maximum value of Y axis
        this.axisLeft.axisMinimum = 0f;      //Y-axis minimum value
        //The number of Y-axis coordinates The second parameter is generally false true to indicate that the number of labels is mandatory, which may cause problems such as incomplete display of X-axis coordinates.
//        barChart.getAxisLeft().setLabelCount(18,false);
        this.xAxis.axisMaximum = 12f;      //Maximum value of X axis
        this.xAxis.axisMinimum = 0f;      //X-axis minimum value
        //Number of X-axis coordinates The second parameter is generally filled with false true, which means that the number of labels is mandatory, which may cause problems such as incomplete display of X-axis coordinates.
        this.xAxis.setLabelCount(12, false);
        this.description.isEnabled = false;        //A string of English letters in the lower right corner is not displayed
        this.xAxis.position =
            XAxis.XAxisPosition.TOP;      //The position of the X axis is set to down, the default is up
        this.axisRight.isEnabled = false;

        this.xAxis.labelRotationAngle = -45f
//        this.xAxis.yOffset = 5f   // отступ от оси Х до графика

        this.xAxis.setDrawGridLines(false)
        this.axisLeft.setDrawGridLines(true)
        this.axisRight.setDrawGridLines(false)


        val labels = arrayOf(
            "",
            resources.getString(R.string.jan),
            resources.getString(R.string.feb),
            resources.getString(R.string.mar),
            resources.getString(R.string.apr),
            resources.getString(R.string.may),
            resources.getString(R.string.jun),
            resources.getString(R.string.jul),
            resources.getString(R.string.aug),
            resources.getString(R.string.sep),
            resources.getString(R.string.oct),
            resources.getString(R.string.nov),
            resources.getString(R.string.dec),
        )

        val formatterX = (object: ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return labels.get(value.toInt())
            }

        })
        this.xAxis.valueFormatter = formatterX;
        this.xAxis.granularity = 1f;
        this.xAxis.isGranularityEnabled = true;




        val legend = this.getLegend();
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.isWordWrapEnabled = true
        legend.setDrawInside(false)
        legend.formSize = 8f
        legend.formToTextSpace = 4f
        legend.xEntrySpace = 6f
    }

    private fun BarChart.loadBarChartData(
        entries: List<BarEntry>,
        categories: List<CategoryWithTransactions>
    ) {

        val catNames = categories.map {
            it.category.catName
        }.toTypedArray()
        Timber.d("logs catNames.size = ${catNames.size}")

        val catColors = categories.map {
            it.category.catColor
        }
        Timber.d("logs catColors.size = ${catColors.size}")

        if (this.getData() != null &&
            this.getData().getDataSetCount() > 0
        ) {
            val barDataSet = this.getData().getDataSetByIndex(0) as BarDataSet
            barDataSet.values = entries
            this.data.notifyDataChanged()
            this.notifyDataSetChanged()
        } else {
            val barDataSet = BarDataSet(entries, null)
            barDataSet.setDrawIcons(false)
//            barDataSet.setDrawValues(false)
            barDataSet.colors = catColors
            barDataSet.stackLabels = catNames

            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(barDataSet)

            this.data = null
            val barData = BarData(dataSets)

            this.data = barData
            barData.barWidth = 0.9f;//The width of the column
        }

        val formatterStacked = StackedValueFormatter(false, "", 2)

        val formatterY = (object: ValueFormatter() {


            override fun getBarStackedLabel(value: Float, stackedEntry: BarEntry?): String {
                return if (value == 0.0f) "" else super.getBarStackedLabel(value, stackedEntry)
            }

            override fun getBarLabel(barEntry: BarEntry?): String {
                return ""
            }
        })
        this.data.setValueFormatter(formatterStacked)


        this.xAxis.axisMaximum = this.data.xMax + 0.75f
//        this.xAxis.axisMinimum = this.data.xMin - 0.25f

        this.setFitBars(true);
        this.invalidate()
    }
}






