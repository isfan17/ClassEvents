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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.isfan17.classevents.data.model.Classroom
import com.isfan17.classevents.databinding.FragmentSearchEventsBinding
import com.isfan17.classevents.ui.adapters.EventListAdapter
import com.isfan17.classevents.utils.Helper.toast
import com.isfan17.classevents.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchEventsFragment : Fragment() {

    private var _binding: FragmentSearchEventsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchEventsViewModel by viewModels()
    private val navArgs: SearchEventsFragmentArgs by navArgs()

    private var objClassroom: Classroom? = null

    private val adapter by lazy {
        EventListAdapter(
            onItemClicked = { event ->
                val action = SearchEventsFragmentDirections.actionSearchEventsFragmentToEventDetailFragment(
                    event
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
        _binding = FragmentSearchEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        objClassroom = navArgs.classroom
        binding.tvTitle.text = objClassroom!!.name

        // Events search result
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchEventsFlow.collectLatest {
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
        binding.svEvents.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.searchEventsClassroom(query, objClassroom!!.id)
                binding.svEvents.clearFocus()
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
        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.adapter = adapter
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