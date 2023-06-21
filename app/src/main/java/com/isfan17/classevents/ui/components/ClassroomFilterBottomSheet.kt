package com.isfan17.classevents.ui.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.isfan17.classevents.R
import com.isfan17.classevents.ui.views.home.HomeViewModel
import com.isfan17.classevents.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClassroomFilterBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_filter, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val radioGroup: RadioGroup = view.findViewById(R.id.rg_filter)
        radioGroup.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.oldest -> viewModel.storeClassroomSort(CLASSROOM_SORT_DEFAULT)
                R.id.name_az -> viewModel.storeClassroomSort(CLASSROOM_SORT_NAME_ASC)
                R.id.name_za -> viewModel.storeClassroomSort(CLASSROOM_SORT_NAME_DESC)
                R.id.day_asc -> viewModel.storeClassroomSort(CLASSROOM_SORT_DAYTIME_ASC)
                R.id.day_desc -> viewModel.storeClassroomSort(CLASSROOM_SORT_DAYTIME_DESC)
            }
        }

        // Classroom sort type
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.classroomSortFlow.collectLatest {
                    when(it) {
                        CLASSROOM_SORT_DEFAULT -> radioGroup.check(R.id.oldest)
                        CLASSROOM_SORT_NAME_ASC -> radioGroup.check(R.id.name_az)
                        CLASSROOM_SORT_NAME_DESC -> radioGroup.check(R.id.name_za)
                        CLASSROOM_SORT_DAYTIME_ASC -> radioGroup.check(R.id.day_asc)
                        CLASSROOM_SORT_DAYTIME_DESC -> radioGroup.check(R.id.day_desc)
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}