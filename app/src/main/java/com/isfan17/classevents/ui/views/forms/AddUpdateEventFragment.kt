package com.isfan17.classevents.ui.views.forms

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.isfan17.classevents.R
import com.isfan17.classevents.data.model.Event
import com.isfan17.classevents.databinding.FragmentAddUpdateEventBinding
import com.isfan17.classevents.utils.Helper
import com.isfan17.classevents.utils.Helper.toast
import com.isfan17.classevents.utils.Resource
import com.isfan17.classevents.utils.TYPE_ADD_EVENT
import com.isfan17.classevents.utils.TYPE_UPDATE_EVENT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddUpdateEventFragment : Fragment() {

    private var _binding: FragmentAddUpdateEventBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddUpdateEventViewModel by viewModels()
    private val navArgs: AddUpdateEventFragmentArgs by navArgs()

    private var isEdit = false
    private var classroomId: String = ""
    private var objEvent: Event? = null

    override fun onCreateView (
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddUpdateEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUi()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Add event result
                launch {
                    viewModel.addEventFlow.collectLatest {
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

                // Update event result
                launch {
                    viewModel.updateEventFlow.collectLatest {
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

        // Showing date picker dialog for event date
        binding.edtDate.setOnClickListener {
            showDatePicker(
                getString(R.string.event_date),
                binding.edtDate
            )
        }

        // Showing time picker dialog for event time
        binding.edtTime.setOnClickListener {
            showTimePicker(
                getString(R.string.event_time),
                binding.edtTime
            )
        }

        // Submit data entry to firestore
        binding.btnSubmit.setOnClickListener {
            if (isEdit) {
                editEvent()
            } else {
                addNewEvent()
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun updateUi() {
        classroomId = navArgs.classroomId
        when (navArgs.type) {
            TYPE_ADD_EVENT -> {
                isEdit = false
                binding.tvTitle.text = getString(R.string.add_event)
            }
            TYPE_UPDATE_EVENT -> {
                isEdit = true
                binding.tvTitle.text = getString(R.string.edit_event)
                objEvent = navArgs.event

                binding.edtName.setText(objEvent?.name)
                binding.edtDate.setText(objEvent?.date)
                binding.edtTime.setText(objEvent?.time)
            }
        }
    }

    private fun addNewEvent() {
        val nameEntry = binding.edtName.text.toString()
        val dateEntry = binding.edtDate.text.toString()
        val timeEntry = binding.edtTime.text.toString()

        when {
            nameEntry.isEmpty() -> binding.edtName.error = getString(R.string.msg_field_required)
            dateEntry.isEmpty() -> binding.edtDate.error = getString(R.string.msg_field_required)
            timeEntry.isEmpty() -> binding.edtTime.error = getString(R.string.msg_field_required)

            else -> {
                viewModel.addEvent(
                    Event(
                        id = "",
                        classroom_id = classroomId,
                        name = nameEntry,
                        date = dateEntry,
                        time = timeEntry
                    )
                )
            }
        }
    }

    private fun editEvent() {
        val nameEntry = binding.edtName.text.toString()
        val dateEntry = binding.edtDate.text.toString()
        val timeEntry = binding.edtTime.text.toString()

        when {
            nameEntry.isEmpty() -> binding.edtName.error = getString(R.string.msg_field_required)
            dateEntry.isEmpty() -> binding.edtDate.error = getString(R.string.msg_field_required)
            timeEntry.isEmpty() -> binding.edtTime.error = getString(R.string.msg_field_required)

            else -> {
                val eventEntry = objEvent?.let {
                    Event(
                        id = it.id,
                        classroom_id = it.classroom_id,
                        name = nameEntry,
                        date = dateEntry,
                        time = timeEntry
                    )
                }
                if (eventEntry != null) viewModel.updateEvent(eventEntry)
            }
        }
    }

    private fun showDatePicker(title: String, editText: EditText) {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(title)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        datePicker.show(parentFragmentManager, "AddUpdateEventFragment")

        datePicker.addOnPositiveButtonClickListener {
            Timber.d(datePicker.selection.toString())
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val selectedDateInMillis = datePicker.selection ?: System.currentTimeMillis()
            val formattedDate = dateFormat.format(Date(selectedDateInMillis))
            editText.setText(formattedDate)
        }
    }

    private fun showTimePicker(title: String, editText: EditText) {
        val timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(0)
                .setMinute(0)
                .setTitleText(title)
                .build()
        timePicker.show(parentFragmentManager, "AddUpdateEventFragment")

        timePicker.addOnPositiveButtonClickListener {
            val pickedHour: Int = timePicker.hour
            val pickedMinute: Int = timePicker.minute
            val time = Helper.generateTimeText(pickedHour, pickedMinute)
            editText.setText(time)
        }
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}