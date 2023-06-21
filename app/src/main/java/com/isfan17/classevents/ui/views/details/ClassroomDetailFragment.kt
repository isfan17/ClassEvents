package com.isfan17.classevents.ui.views.details

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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.isfan17.classevents.R
import com.isfan17.classevents.data.model.Classroom
import com.isfan17.classevents.databinding.FragmentClassroomDetailBinding
import com.isfan17.classevents.ui.adapters.EventListAdapter
import com.isfan17.classevents.ui.components.EventFilterBottomSheet
import com.isfan17.classevents.utils.Helper.toast
import com.isfan17.classevents.utils.Resource
import com.isfan17.classevents.utils.TYPE_ADD_EVENT
import com.isfan17.classevents.utils.TYPE_UPDATE_CLASSROOM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClassroomDetailFragment : Fragment() {

    private var _binding: FragmentClassroomDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ClassroomDetailViewModel by viewModels()
    private val navArgs: ClassroomDetailFragmentArgs by navArgs()

    private var classroomId: String? = null
    private var objClassroom: Classroom? = null

    private val adapter by lazy {
        EventListAdapter(
            onItemClicked = { event ->
                val action = ClassroomDetailFragmentDirections.actionClassroomDetailFragmentToEventDetailFragment(
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
        _binding = FragmentClassroomDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        // Get classroom data
        classroomId = navArgs.classroomId
        viewModel.getClassroom(classroomId!!)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.classroomFlow.collectLatest {
                        if (it != null) {
                            when (it) {
                                Resource.Loading -> showClassroomLoading(true)
                                is Resource.Failure -> {
                                    showClassroomLoading(false)
                                    toast(it.error.toString())
                                }
                                is Resource.Success -> {
                                    showClassroomLoading(false)
                                    objClassroom = it.data
                                    updateUi()
                                }
                            }
                        }
                    }
                }

                launch {
                    viewModel.deleteClassroomFlow.collectLatest {
                        if (it != null) {
                            when (it) {
                                Resource.Loading -> showClassroomLoading(true)
                                is Resource.Failure -> {
                                    showClassroomLoading(false)
                                    toast(it.error.toString())
                                }
                                is Resource.Success -> {
                                    showClassroomLoading(false)
                                    toast(it.data)
                                    findNavController().navigateUp()
                                }
                            }
                        }
                    }
                }

                launch {
                    viewModel.eventsSortFlow.collectLatest {
                        if (it != null) viewModel.getEvents(classroomId!!, it)
                    }
                }

                launch {
                    viewModel.eventsFlow.collectLatest {
                        if (it != null) {
                            when (it) {
                                Resource.Loading -> showEventsLoading(true)
                                is Resource.Failure -> {
                                    showEventsLoading(false)
                                    toast(it.error.toString())
                                }
                                is Resource.Success -> {
                                    showEmptyMsg(it.data.isEmpty())
                                    showEventsLoading(false)
                                    adapter.submitList(it.data)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Move to search page
        binding.svEvents.isEnabled = false
        binding.svEvents.isSubmitButtonEnabled = false
        binding.cardSearch.setOnClickListener {
            val action = objClassroom?.let { it1 ->
                ClassroomDetailFragmentDirections.actionClassroomDetailFragmentToSearchEventsFragment(it1)
            }
            if (action != null) findNavController().navigate(action)
        }

        // Go to edit class form
        binding.btnEdit.setOnClickListener {
            val action =
                ClassroomDetailFragmentDirections.actionClassroomDetailFragmentToAddUpdateClassroomFragment(
                    TYPE_UPDATE_CLASSROOM, objClassroom
                )
            findNavController().navigate(action)
        }

        // Delete the classroom
        binding.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // Show filter list
        binding.btnFilter.setOnClickListener {
            showFilterList()
        }

        // Go to add event form
        binding.btnAdd.setOnClickListener {
            val action = objClassroom?.let { it1 ->
                ClassroomDetailFragmentDirections.actionClassroomDetailFragmentToAddUpdateEventFragment(
                    TYPE_ADD_EVENT, null, it1.id
                )
            }
            if (action != null) { findNavController().navigate(action) }
        }

        // Back to home page
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun updateUi() {
        binding.tvName.text = objClassroom?.name
        binding.tvDay.text = getString(R.string.value_classroom_day, objClassroom?.day)
        binding.tvStartTime.text = getString(R.string.value_classroom_start_time, objClassroom?.startTime)
        binding.tvEndTime.text = objClassroom?.endTime
    }

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.msg_delete_classroom))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                objClassroom?.let { viewModel.deleteClassroom(it) }
            }
            .show()
    }

    private fun showFilterList() {
        val modalBottomSheet = EventFilterBottomSheet()
        modalBottomSheet.show(parentFragmentManager, EventFilterBottomSheet.TAG)
    }

    private fun setupRecyclerView() {
        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.adapter = adapter
    }

    private fun showClassroomLoading(state: Boolean) {
        binding.classroomProgressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun showEventsLoading(state: Boolean) {
        binding.eventsProgressBar.visibility = if (state) View.VISIBLE else View.GONE
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