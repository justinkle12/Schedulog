package com.example.schedulog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.schedulog.databinding.ListFriendSystemBinding
import com.example.schedulog.databinding.ListRequestedBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class RequestedAdapter(private val context: Context, private val friends: List<RequestedFragment.Friend>) :
    RecyclerView.Adapter<RequestedAdapter.FriendViewHolder>() {

    inner class FriendViewHolder(val binding: ListRequestedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: RequestedFragment.Friend) {
            binding.friendFullNameTextView.text = friend.fullName
            binding.friendUsernameTextView.text = friend.username
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ListRequestedBinding.inflate(
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
                .child(currentUser?.uid.toString())
                .child("friends")
                .child("requested")
                .child(friend.uid).removeValue()
            ur
                .child(friend.uid)
                .child("friends")
                .child("pending")
                .child(currentUser?.uid.toString()).removeValue()
            notifyDataSetChanged()

        }
        holder.bind(friend)

    }

    override fun getItemCount(): Int {
        return friends.size
    }


}
