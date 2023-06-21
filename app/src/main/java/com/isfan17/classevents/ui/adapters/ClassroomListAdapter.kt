package com.isfan17.classevents.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.isfan17.classevents.data.model.Classroom
import com.isfan17.classevents.databinding.ItemClassroomBinding

class ClassroomListAdapter(
    val onItemClicked: (Classroom) -> Unit
): ListAdapter<Classroom, ClassroomListAdapter.ClassroomViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassroomViewHolder {
        val binding = ItemClassroomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClassroomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClassroomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ClassroomViewHolder(private val binding: ItemClassroomBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Classroom) {
            binding.tvName.text = data.name
            binding.tvDay.text = "${data.day}, "
            binding.tvStartTime.text = data.startTime
            binding.tvEndTime.text = " - ${data.endTime}"
            binding.root.setOnClickListener { onItemClicked.invoke(data) }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<Classroom>() {
        override fun areItemsTheSame(oldItem: Classroom, newItem: Classroom): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Classroom, newItem: Classroom): Boolean {
            return oldItem == newItem
        }
    }
}