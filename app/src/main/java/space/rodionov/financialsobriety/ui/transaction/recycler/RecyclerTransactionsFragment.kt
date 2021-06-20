package space.rodionov.financialsobriety.ui.transaction.recycler

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.data.Spend
import space.rodionov.financialsobriety.databinding.FragmentTransactionsRecyclerBinding

class RecyclerTransactionsFragment : Fragment(R.layout.fragment_transactions_recycler),
    RecyclerTransactionAdapter.OnItemClickListener {

    private lateinit var binding: FragmentTransactionsRecyclerBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTransactionsRecyclerBinding.bind(view)

        val adapter = RecyclerTransactionAdapter(this)


    }

    override fun onItemClick(spend: Spend) {
        TODO("Not yet implemented")
    }


}