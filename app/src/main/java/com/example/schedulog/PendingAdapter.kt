package com.example.schedulog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.schedulog.databinding.ListFriendSystemBinding
import com.example.schedulog.databinding.ListPendingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class PendingAdapter(private val context: Context, private val friends: List<PendingFragment.Friend>) :
    RecyclerView.Adapter<PendingAdapter.FriendViewHolder>() {

    inner class FriendViewHolder(val binding: ListPendingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: PendingFragment.Friend) {
            binding.friendFullNameTextView.text = friend.fullName
            binding.friendUsernameTextView.text = friend.username
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ListPendingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val ur = FirebaseDatabase.getInstance().reference.child("users")
        val friend = friends[position]
        holder.binding.friendFrame.setOnClickListener{
            val userProfileFragment = UserProfileFragment.newInstance(friend.uid)
            userProfileFragment.show((context as AppCompatActivity).supportFragmentManager, "UserProfileFragment")
        }
        holder.binding.xbutton.setOnClickListener{
            val currentUser = FirebaseAuth.getInstance().currentUser
            ur
                .child(friend.uid)
                .child("friends")
                .child("requested")
                .child(currentUser?.uid.toString()).removeValue()
            ur
                .child(currentUser?.uid.toString())
                .child("friends")
                .child("pending")
                .child(friend.uid).removeValue()
            notifyItemRemoved(position)
        }
        holder.binding.accbutton.setOnClickListener{
            val currentUser = FirebaseAuth.getInstance().currentUser
            ur
                .child(friend.uid)
                .child("friends")
                .child("requested")
                .child(currentUser?.uid.toString()).removeValue()
            ur
                .child(friend.uid)
                .child("friends")
                .child(currentUser?.uid.toString()).setValue(true)
            ur
                .child(currentUser?.uid.toString())
                .child("friends")
                .child(friend.uid).setValue(true)
            ur
                .child(currentUser?.uid.toString())
                .child("friends")
                .child("pending")
                .child(friend.uid).removeValue()
            notifyItemRemoved(position)

        }

        holder.bind(friend)

    }

    override fun getItemCount(): Int {
        return friends.size
    }


}
