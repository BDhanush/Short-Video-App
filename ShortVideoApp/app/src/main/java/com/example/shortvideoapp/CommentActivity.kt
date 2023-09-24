import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shortvideoapp.R
import com.example.shortvideoapp.model.Comment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CommentActivity : AppCompatActivity() {
    private lateinit var commentAdapter: CommentAdapter
    private val commentsList: MutableList<Comment> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comment_activity)

        val recyclerView: RecyclerView = findViewById(R.id.commentRecyclerView)
        val commentEditText: EditText = findViewById(R.id.commentEditText)
        val postButton: Button = findViewById(R.id.postButton)

        commentAdapter = CommentAdapter(commentsList)
        recyclerView.adapter = commentAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val videoId = intent.getStringExtra("videoId")

        // Load comments related to the video (adjust this based on your data structure and database)
        loadComments(videoId)

        postButton.setOnClickListener {
            val newCommentText = commentEditText.text.toString()
            if (newCommentText.isNotEmpty()) {
                // Create a new Comment object and add it to the commentsList
                val user = Firebase.auth.currentUser
                if (user != null) {
                    val newComment = Comment(user.uid, newCommentText)
                    commentsList.add(newComment)
                        CommentAdapter.notifyDataSetChanged()

                    // Save the new comment to your database (adjust this based on your database setup)
                    saveCommentToDatabase(videoId, newCommentText)

                    // Clear the EditText after posting
                    commentEditText.text.clear()
                }
            }
        }
    }

    // Load comments related to the video from your database
    private fun loadComments(videoId: String?) {
        // Fetch comments related to the video from your database and populate commentsList
        // You may use Firebase Realtime Database, Firestore, or any other database system
        // Update the commentsList and call commentAdapter.notifyDataSetChanged() when done
    }

    // Save a new comment to your database
    private fun saveCommentToDatabase(videoId: String?, commentText: String) {
        // Save the comment to your database (e.g., Firebase Realtime Database, Firestore)
        // Be sure to associate the comment with the video using the videoId
        // Implement the appropriate database operations here
    }
}