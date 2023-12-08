package com.example.schedulog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.schedulog.databinding.ListFriendSystemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay


class FriendAdapter(private val context: Context, private val friends: List<FriendSystemFragment.Friend>) :
    RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    inner class FriendViewHolder(val binding: ListFriendSystemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: FriendSystemFragment.Friend) {
            binding.friendFullNameTextView.text = friend.fullName
            binding.friendUsernameTextView.text = friend.username
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ListFriendSystemBinding.inflate(
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
                .child(currentUser?.uid.toString()).removeValue()
            ur
                .child(currentUser?.uid.toString())
                .child("friends")
                .child(friend.uid).removeValue()
            notifyItemRemoved(position)

        }
        holder.bind(friend)

    }

    override fun getItemCount(): Int {
        return friends.size
    }
}
