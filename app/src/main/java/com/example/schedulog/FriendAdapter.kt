import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.schedulog.databinding.ListFriendSystemBinding
import com.example.schedulog.FriendSystemFragment.Friend


data class Friend(val username: String, val email: String)

class FriendAdapter(private val friends: MutableList<Friend>) :
    RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    inner class FriendViewHolder(private val binding: ListFriendSystemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: Friend) {
            binding.friendUsernameTextView.text = friend.username
            binding.friendEmailTextView.text = friend.email
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
        val friend = friends[position]
        holder.bind(friend)
    }

    override fun getItemCount() = friends.size
}
