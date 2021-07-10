package space.rodionov.financialsobriety.ui.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.data.Category
import space.rodionov.financialsobriety.databinding.FragmentCategoriesBinding

@AndroidEntryPoint
class CategoriesFragment : Fragment(R.layout.fragment_categories), CategoriesAdapter.OnItemClickListener {
    private val viewModel: CategoriesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentCategoriesBinding.bind(view)
        val catAdapter = CategoriesAdapter(this)
        binding.apply {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = catAdapter
                setHasFixedSize(true)
            }
        }

        viewModel.categories.observe(viewLifecycleOwner) {
            catAdapter.submitList(it)
        }
    }



    override fun onItemClick(category: Category) {
        TODO("Not yet implemented")
    }
}






