package space.rodionov.financialsobriety.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import space.rodionov.financialsobriety.data.CategoryWithTransactions
import space.rodionov.financialsobriety.data.Month
import space.rodionov.financialsobriety.databinding.ItemMonthHomeBinding
import space.rodionov.financialsobriety.ui.shared.MonthComparator

class HomeAdapter : ListAdapter<Month, HomeAdapter.HomeMonthViewHolder>(MonthComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeMonthViewHolder {
        val binding = ItemMonthHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeMonthViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeMonthViewHolder, position: Int) {
        val curMonth = getItem(position)
        holder.bind(curMonth)
    }

    inner class HomeMonthViewHolder(private val binding: ItemMonthHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(month: Month) {
            binding.apply {
                tvMonth.text = "Данные за ${month}"
                tvMonth.setRotationY(180f);
            }
        }
    }
}





