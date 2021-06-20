package space.rodionov.financialsobriety.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.databinding.FragmentHomeBinding
import space.rodionov.financialsobriety.util.exhaustive

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)

        binding.apply {





            fabAddSpend.setOnClickListener {
                viewModel.onAddSpendClick()
            }
        }

        setFragmentResultListener("add_edit_result") { _, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        } // 2-Я ПОЛОВИНА ФРАГМЕНТ РЕЗАЛТА

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.homeEvent.collect {
                when (it) {
                    is HomeViewModel.HomeEvent.NavigateToAddSpendScreen -> {
                        val action = HomeFragmentDirections.actionHomeFragmentToEditTransactionFragment(null, "Новая транзакция")
                        findNavController().navigate(action)
                    }
                    is HomeViewModel.HomeEvent.ShowTransactionSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), it.msg, Snackbar.LENGTH_SHORT).show()
                    }
                }.exhaustive
            }
        }


    }

}








