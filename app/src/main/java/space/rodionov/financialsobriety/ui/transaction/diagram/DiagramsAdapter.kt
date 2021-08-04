package space.rodionov.financialsobriety.ui.transaction.diagram

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.Typeface.ITALIC
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.data.CategoryWithTransactions
import space.rodionov.financialsobriety.data.Month
import space.rodionov.financialsobriety.databinding.ItemDiagramBinding
import space.rodionov.financialsobriety.ui.shared.MonthComparator
import space.rodionov.financialsobriety.util.roundToTwoDecimals
import java.util.*


class DiagramsAdapter(
    private val catsWithTransactionsFlow: StateFlow<List<CategoryWithTransactions>?>,
    private val scope: CoroutineScope
) : ListAdapter<Month, DiagramsAdapter.DiagramViewHolder>(MonthComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagramViewHolder {
        val binding = ItemDiagramBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiagramViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiagramViewHolder, position: Int) {
        val curMonth = getItem(position)
        holder.bind(curMonth)
    }

    inner class DiagramViewHolder(private val binding: ItemDiagramBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(month: Month) {
            binding.apply {
                pieChart.setupPieChart()
                scope.launch {
                    catsWithTransactionsFlow.collect {
                        val catsWithTransactions = it ?: return@collect
                        pieChart.data?.clearValues()
                        val pieEntries = createMonthPieEntryList(catsWithTransactions, month)
                        pieChart.loadPieChartData(pieEntries, month, catsWithTransactions)
                    }
                }
            }
        }
    }

    //=======================CREATING ENTRIES FUNS==========================================

    private fun createMonthPieEntryList(
        allCatsWithTransactions: List<CategoryWithTransactions>,
        month: Month
    ): List<PieEntry> {
        val pieEntries = mutableListOf<PieEntry>()
        for (cwt in allCatsWithTransactions) {
            val monthTransactionsByCat = month.getTransactionsOfMonth(cwt.transactions)

                val monthSumByCat = monthTransactionsByCat.map {
                    it.sum
                }.sum().roundToTwoDecimals()
                pieEntries.add(PieEntry(monthSumByCat, cwt.category.catName))

        }
        return pieEntries
    }

    //=========================FUNS FOR PIECHART==============================

    private fun PieChart.setupPieChart() {
        this.isDrawHoleEnabled
        this.setUsePercentValues(false)
        this.setEntryLabelTextSize(12f)
        this.setEntryLabelColor(Color.WHITE)
        this.setCenterTextSize(24f)
        this.description.isEnabled = false
        this.isRotationEnabled = false

        this.setDrawEntryLabels(true)

        this.setTransparentCircleColor(Color.WHITE)
        this.setTransparentCircleAlpha(110)
        this.setHoleRadius(58f)
        this.setTransparentCircleRadius(61f)
        this.setExtraOffsets(12f, 12f, 12f, 12f)

        val legend = this.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.isWordWrapEnabled = true
        legend.setDrawInside(true)
        legend.isEnabled
    }

    private fun PieChart.loadPieChartData(
        entries: List<PieEntry>,
        month: Month,
        categories: List<CategoryWithTransactions>
    ) {
        val catColors = categories.map {
            it.category.catColor
        }

        val dataset = PieDataSet(entries, null)
        dataset.colors = catColors
        dataset.sliceSpace = 5f
        dataset.setSelectionShift(5f)

        //dataSet.setSelectionShift(0f);
        dataset.setValueLinePart1OffsetPercentage(80f)
        dataset.setValueLinePart1Length(0.2f)
        dataset.setValueLinePart2Length(0.4f)

        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataset.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE)

        val data = PieData(dataset)
        data.setDrawValues(true)
        data.setValueFormatter(PercentFormatter(this))
        data.setValueTextSize(13f)
        data.setValueTextColor(Color.BLACK)

        // formatter
        val formatter = (object: ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (value == 0.0f) "" else value.toString()
            }
        })
        data.setValueFormatter(formatter)

        this.data = data
        this.invalidate()

        // center text
        val monthSum = entries.map {
            it.value
        }.sumByDouble {
            it.toDouble()
        }.toFloat()
        val monthText = "${month.toAbbrString().capitalize(Locale.ROOT)}\n$monthSum"
        this.centerText = monthText
        this.setCenterTextSize(19f)
        this.setCenterTextColor(resources.getColor(R.color.blue))
        val font = Typeface.create(Typeface.SANS_SERIF, ITALIC)
        this.setCenterTextTypeface(font)
    }
}

