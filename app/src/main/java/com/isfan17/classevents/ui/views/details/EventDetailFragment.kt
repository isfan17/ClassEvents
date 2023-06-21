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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.isfan17.classevents.R
import com.isfan17.classevents.data.model.Classroom
import com.isfan17.classevents.data.model.Event
import com.isfan17.classevents.databinding.FragmentEventDetailBinding
import com.isfan17.classevents.utils.Helper.toast
import com.isfan17.classevents.utils.Resource
import com.isfan17.classevents.utils.TYPE_UPDATE_EVENT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventDetailViewModel by viewModels()
    private val navArgs: EventDetailFragmentArgs by navArgs()

    private var objEvent: Event? = null
    private var objClassroom: Classroom? = null

    override fun onCreateView (
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get event and classroom data
        viewModel.getClassroom(navArgs.event.classroom_id)
        viewModel.getEvent(navArgs.event.id)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Event Data
                launch {
                    viewModel.eventFlow.collectLatest {
                        if (it != null) {
                            when (it) {
                                Resource.Loading -> showLoading(true)
                                is Resource.Failure -> {
                                    showLoading(false)
                                    toast(it.error.toString())
                                }
                                is Resource.Success -> {
                                    showLoading(false)
                                    objEvent = it.data
                                    binding.tvName.text = it.data.name
                                    binding.tvDate.text = it.data.date
                                    binding.tvTime.text = it.data.time
                                }
                            }
                        }
                    }
                }

                // Classroom Name
                launch {
                    viewModel.classroomFlow.collectLatest {
                        if (it != null) {
                            when (it) {
                                Resource.Loading -> showLoading(true)
                                is Resource.Failure -> {
                                    showLoading(false)
                                    toast(it.error.toString())
                                }
                                is Resource.Success -> {
                                    showLoading(false)
                                    objClassroom = it.data
                                    binding.tvClassroomName.text = it.data.name
                                }
                            }
                        }
                    }
                }

                // Delete event result
                launch {
                    viewModel.deleteEventFlow.collectLatest {
                        if (it != null) {
                            when (it) {
                                Resource.Loading -> showLoading(true)
                                is Resource.Failure -> {
                                    showLoading(false)
                                    toast(it.error.toString())
                                }
                                is Resource.Success -> {
                                    showLoading(false)
                                    toast(it.data)
                                    findNavController().navigateUp()
                                }
                            }
                        }
                    }
                }
            }
        }

        // Edit event
        binding.btnUpdate.setOnClickListener {
            val action = objEvent?.let { it1 ->
                EventDetailFragmentDirections.actionEventDetailFragmentToAddUpdateEventFragment(
                    TYPE_UPDATE_EVENT, objEvent, it1.classroom_id
                )
            }
            if (action != null) findNavController().navigate(action)
        }

        // Delete event
        binding.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // Back
        binding.btnClose.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.msg_delete_event))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                objEvent?.let { viewModel.deleteEvent(it) }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}