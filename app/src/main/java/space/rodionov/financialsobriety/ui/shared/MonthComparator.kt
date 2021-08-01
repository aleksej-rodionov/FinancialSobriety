package space.rodionov.financialsobriety.ui.shared

import androidx.recyclerview.widget.DiffUtil
import space.rodionov.financialsobriety.data.Month

class MonthComparator : DiffUtil.ItemCallback<Month>() {
    override fun areItemsTheSame(oldItem: Month, newItem: Month) =
        oldItem.mmSlashYear == newItem.mmSlashYear

    override fun areContentsTheSame(oldItem: Month, newItem: Month) = oldItem == newItem
}