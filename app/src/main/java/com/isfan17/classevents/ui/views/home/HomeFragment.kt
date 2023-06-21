package com.isfan17.classevents.ui.views.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.isfan17.classevents.R
import com.isfan17.classevents.databinding.FragmentHomeBinding
import com.isfan17.classevents.ui.adapters.ClassroomListAdapter
import com.isfan17.classevents.ui.components.ClassroomFilterBottomSheet
import com.isfan17.classevents.utils.Helper.toast
import com.isfan17.classevents.utils.Resource
import com.isfan17.classevents.utils.TYPE_ADD_CLASSROOM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private val adapter by lazy {
        ClassroomListAdapter(
            onItemClicked = { classroom ->
                val action =
                    HomeFragmentDirections.actionHomeFragmentToClassroomDetailFragment(classroom.id)
                findNavController().navigate(action)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        // Observe all observable needed
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // User name
                launch {
                    viewModel.userFlow.collectLatest {
                        if (it != null) {
                            binding.tvName.text = getString(R.string.value_greetings_name, it.name)
                        }
                    }
                }

                // Classrooms Sort Type
                launch {
                    viewModel.classroomSortFlow.collectLatest {
                        if (it != null) {
                            viewModel.getClassrooms(it)
                        }
                    }
                }

                // Classrooms
                launch {
                    viewModel.classroomsFlow.collectLatest {
                        if (it != null) {
                            when(it) {
                                Resource.Loading -> {
                                    showLoading(true)
                                }
                                is Resource.Failure -> {
                                    showLoading(false)
                                    toast(it.error.toString())
                                }
                                is Resource.Success -> {
                                    showEmptyMsg(it.data.isEmpty())
                                    showLoading(false)
                                    adapter.submitList(it.data)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Move to add class form
        binding.btnAdd.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAddUpdateClassroomFragment(
                TYPE_ADD_CLASSROOM,
                null
            )
            findNavController().navigate(action)
        }

        // Move to settings page
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }

        // Move to search page
        binding.svClassrooms.isEnabled = false
        binding.svClassrooms.isSubmitButtonEnabled = false
        binding.cardSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchClassroomsFragment)
        }

        // Show filter list
        binding.btnFilter.setOnClickListener {
            showFilterList()
        }
    }

    private fun showFilterList() {
        val modalBottomSheet = ClassroomFilterBottomSheet()
        modalBottomSheet.show(parentFragmentManager, ClassroomFilterBottomSheet.TAG)
    }

    private fun setupRecyclerView() {
        binding.rvClassrooms.layoutManager = LinearLayoutManager(requireContext())
        binding.rvClassrooms.adapter = adapter
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun showEmptyMsg(state: Boolean) {
        if (state) {
            binding.ivNoData.visibility = View.VISIBLE
            binding.tvTitleNoData.visibility = View.VISIBLE
            binding.tvSubtitleNoData.visibility = View.VISIBLE
        } else {
            binding.ivNoData.visibility = View.GONE
            binding.tvTitleNoData.visibility = View.GONE
            binding.tvSubtitleNoData.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
