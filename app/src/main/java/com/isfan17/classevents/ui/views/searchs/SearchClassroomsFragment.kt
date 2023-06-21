package com.isfan17.classevents.ui.views.searchs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.isfan17.classevents.databinding.FragmentSearchClassroomsBinding
import com.isfan17.classevents.ui.adapters.ClassroomListAdapter
import com.isfan17.classevents.utils.Helper.toast
import com.isfan17.classevents.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchClassroomsFragment : Fragment() {

    private var _binding: FragmentSearchClassroomsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchClassroomsViewModel by viewModels()

    private val adapter by lazy {
        ClassroomListAdapter(
            onItemClicked = { classroom ->
                val action = SearchClassroomsFragmentDirections.actionSearchClassroomsFragmentToClassroomDetailFragment(
                    classroom.id
                )
                findNavController().navigate(action)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchClassroomsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        // Classrooms search result
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchClassroomsFlow.collectLatest {
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

        // Search Function
        binding.svClassrooms.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.searchClassrooms(query)
                binding.svClassrooms.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?) = false
        })

        // Back btn
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
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