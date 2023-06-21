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
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.isfan17.classevents.R
import com.isfan17.classevents.data.model.Classroom
import com.isfan17.classevents.databinding.FragmentAddUpdateClassroomBinding
import com.isfan17.classevents.utils.Helper.generateTimeText
import com.isfan17.classevents.utils.Helper.toast
import com.isfan17.classevents.utils.Resource
import com.isfan17.classevents.utils.TYPE_ADD_CLASSROOM
import com.isfan17.classevents.utils.TYPE_UPDATE_CLASSROOM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddUpdateClassroomFragment : Fragment() {

    private var _binding: FragmentAddUpdateClassroomBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddUpdateClassroomViewModel by viewModels()
    private val navArgs: AddUpdateClassroomFragmentArgs by navArgs()

    private var isEdit = false
    private var objClassroom: Classroom? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddUpdateClassroomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUi()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Add classroom result
                launch {
                    viewModel.addClassroomFlow.collectLatest {
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

                // Update classroom result
                launch {
                    viewModel.updateClassroomFlow.collectLatest {
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

        // Showing time picker dialog for class start time
        binding.edtStartTime.setOnClickListener {
            showTimePicker(
                getString(R.string.classroom_start_time),
                binding.edtStartTime
            )
        }

        // Showing time picker dialog for class end time
        binding.edtEndTime.setOnClickListener {
            showTimePicker(
                getString(R.string.classroom_end_time),
                binding.edtEndTime
            )
        }

        // Submit the data entry to firestore
        binding.btnSubmit.setOnClickListener {
            if (isEdit) {
                editClassroom()
            } else {
                addNewClassroom()
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun updateUi() {
        when(navArgs.type) {
            TYPE_ADD_CLASSROOM -> {
                isEdit = false
                binding.tvTitle.text = getString(R.string.add_classroom)
            }
            TYPE_UPDATE_CLASSROOM -> {
                isEdit = true
                binding.tvTitle.text = getString(R.string.edit_classroom)
                objClassroom = navArgs.classroom

                binding.edtName.setText(objClassroom?.name)
                binding.acDay.setText(objClassroom?.day, false)
                binding.edtStartTime.setText(objClassroom?.startTime)
                binding.edtEndTime.setText(objClassroom?.endTime)
            }
        }
    }

    private fun addNewClassroom() {
        val nameEntry = binding.edtName.text.toString()
        val dayEntry = binding.acDay.text.toString()
        val startTimeEntry = binding.edtStartTime.text.toString()
        val endTimeEntry = binding.edtEndTime.text.toString()

        when {
            nameEntry.isEmpty() -> binding.edtName.error = getString(R.string.msg_field_required)
            dayEntry.isEmpty() -> binding.acDay.error = getString(R.string.msg_field_required)
            startTimeEntry.isEmpty() -> binding.edtStartTime.error = getString(R.string.msg_field_required)
            endTimeEntry.isEmpty() -> binding.edtEndTime.error = getString(R.string.msg_field_required)

            else -> viewModel.addClassroom(
                Classroom(
                    id = "",
                    name = nameEntry,
                    day = dayEntry,
                    startTime = startTimeEntry,
                    endTime = endTimeEntry
                )
            )
        }
    }

    private fun editClassroom() {
        val nameEntry = binding.edtName.text.toString()
        val dayEntry = binding.acDay.text.toString()
        val startTimeEntry = binding.edtStartTime.text.toString()
        val endTimeEntry = binding.edtEndTime.text.toString()

        when {
            nameEntry.isEmpty() -> binding.edtName.error = getString(R.string.msg_field_required)
            dayEntry.isEmpty() -> binding.acDay.error = getString(R.string.msg_field_required)
            startTimeEntry.isEmpty() -> binding.edtStartTime.error = getString(R.string.msg_field_required)
            endTimeEntry.isEmpty() -> binding.edtEndTime.error = getString(R.string.msg_field_required)

            else -> {
                val classroomEntry = objClassroom?.let {
                    Classroom(
                        id = it.id,
                        name = nameEntry,
                        day = dayEntry,
                        startTime = startTimeEntry,
                        endTime = endTimeEntry
                    )
                }
                if (classroomEntry != null) viewModel.updateClassroom(classroomEntry)
            }
        }
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun showTimePicker(title: String, editText: EditText) {
        val timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(0)
                .setMinute(0)
                .setTitleText(title)
                .build()
        timePicker.show(parentFragmentManager, "AddUpdateClassroomFragment")

        timePicker.addOnPositiveButtonClickListener {
            val pickedHour: Int = timePicker.hour
            val pickedMinute: Int = timePicker.minute
            val time = generateTimeText(pickedHour, pickedMinute)
            editText.setText(time)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}