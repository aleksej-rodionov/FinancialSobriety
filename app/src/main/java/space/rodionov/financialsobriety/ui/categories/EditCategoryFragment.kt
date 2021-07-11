package space.rodionov.financialsobriety.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.databinding.FragmentDialogRecyclerBinding
import space.rodionov.financialsobriety.databinding.FragmentEditCategoryBinding

@AndroidEntryPoint
class EditCategoryFragment : BottomSheetDialogFragment()/*(R.layout.fragment_edit_category)*/ {
    private val viewModel: EditCategoryViewModel by viewModels()
    private var _binding: FragmentEditCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditCategoryBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

}