package space.rodionov.financialsobriety.ui.transaction.edittransaction

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.data.Category
import space.rodionov.financialsobriety.databinding.FragmentChooseCategoryBinding
import space.rodionov.financialsobriety.util.exhaustive
import timber.log.Timber

@AndroidEntryPoint
class ChooseCategoryDialogFragment : DialogFragment(), ChooseCategoryAdapter.OnItemClickListener {

    private val viewModel: ChooseCategoryViewModel by viewModels()

    private var _binding: FragmentChooseCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var customView: View
//    private lateinit var binding: FragmentChooseCategoryBinding

    private lateinit var chooseCatAdapter: ChooseCategoryAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        Timber.d("LOGS onCreateDialog vizvan")
        _binding = FragmentChooseCategoryBinding.inflate(LayoutInflater.from(context))
        customView = binding.root

//        val builder: AlertDialog.Builder = AlertDialog.Builder(getActivity())

        Timber.d("LOGS customView = $customView")
        return AlertDialog.Builder(requireContext())
            .setTitle(requireContext().resources.getString(R.string.choose_category))
            .setView(customView)
            .setNegativeButton(
                requireContext().resources.getString(R.string.cancel_action),
                null
            )
            .create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
//        _binding = FragmentChooseCategoryBinding.inflate(inflater, container, false)
        Timber.d("LOGS onCreateView vizvan")
//        chooseCatAdapter = ChooseCategoryAdapter(this, viewModel.catName)
//
//        binding.apply {
//            recyclerView.apply {
//                adapter = chooseCatAdapter
//                layoutManager = LinearLayoutManager(requireContext())
//                setHasFixedSize(true)
//
//                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
//                    viewModel.categories.collect {
//                        val categories = it ?: return@collect
//
//                        chooseCatAdapter.submitList(categories)
//                        Timber.d("LOGS categories.size = ${categories.size}")
//                        tvNoCategories.isVisible = categories.isEmpty()
////                        recyclerView.isVisible = categories.isNotEmpty()
//                    }
//                }
//
//                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
//                    viewModel.chooseCategoryEvent.collect {
//                        when (it) {
//                            is ChooseCategoryViewModel.ChooseCategoryEvent.navigateBackWithResult -> {
//                                setFragmentResult(
//                                    "cat_name_request",
//                                    bundleOf("cat_name_result" to it.catName)
//                                )
////                                dismissDialog()
//                            }
//                        }.exhaustive
//                    }
//                }
//            }
//        }
        customView = binding.root
        return customView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("LOGS onViewCreated vizvan")

        chooseCatAdapter = ChooseCategoryAdapter(this, viewModel.catName)

        binding.apply {
            recyclerView.apply {
                adapter = chooseCatAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)

                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    viewModel.categories.collect {
                        val categories = it ?: return@collect

                        chooseCatAdapter.submitList(categories)
                        Timber.d("LOGS categories.size = ${categories.size}")
                        tvNoCategories.isVisible = categories.isEmpty()
//                        recyclerView.isVisible = categories.isNotEmpty()
                    }
                }

//                viewModel.categories.observe(viewLifecycleOwner) {
//                    chooseCatAdapter.submitList(it)
//                }

                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    viewModel.chooseCategoryEvent.collect {
                        when (it) {
                            is ChooseCategoryViewModel.ChooseCategoryEvent.NavigateBackWithResult -> {
                                setFragmentResult(
                                    "cat_name_request",
                                    bundleOf("cat_name_result" to it.catName)
                                )
                            }
                        }.exhaustive
                    }
                }
            }
        }
    }

    override fun onItemClick(category: Category) {
        viewModel.onCategoryChosen(category)
        this.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}














