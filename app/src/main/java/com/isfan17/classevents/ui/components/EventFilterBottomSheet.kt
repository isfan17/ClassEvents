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
import com.isfan17.classevents.ui.views.details.ClassroomDetailViewModel
import com.isfan17.classevents.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EventFilterBottomSheet: BottomSheetDialogFragment() {

    private val viewModel: ClassroomDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.event_bottom_sheet_filter, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val radioGroup: RadioGroup = view.findViewById(R.id.rg_filter)
        radioGroup.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.oldest -> viewModel.storeEventSort(EVENT_SORT_DEFAULT)
                R.id.name_az -> viewModel.storeEventSort(EVENT_SORT_NAME_ASC)
                R.id.name_za -> viewModel.storeEventSort(EVENT_SORT_NAME_DESC)
                R.id.time_asc -> viewModel.storeEventSort(EVENT_SORT_TIME_ASC)
                R.id.time_desc -> viewModel.storeEventSort(EVENT_SORT_TIME_DESC)
            }
        }

        // Event sort type
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventsSortFlow.collectLatest {
                    when(it) {
                        EVENT_SORT_DEFAULT -> radioGroup.check(R.id.oldest)
                        EVENT_SORT_NAME_ASC -> radioGroup.check(R.id.name_az)
                        EVENT_SORT_NAME_DESC -> radioGroup.check(R.id.name_za)
                        EVENT_SORT_TIME_ASC -> radioGroup.check(R.id.time_asc)
                        EVENT_SORT_TIME_DESC -> radioGroup.check(R.id.time_desc)
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "EventBottomSheet"
    }
}