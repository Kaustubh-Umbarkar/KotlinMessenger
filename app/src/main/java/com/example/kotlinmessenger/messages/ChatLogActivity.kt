package com.example.kotlinmessenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.kotlinmessenger.NewMessageActivity
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.models.ChatMessage
import com.example.kotlinmessenger.models.User
import com.example.kotlinmessenger.views.ChatFromItem
import com.example.kotlinmessenger.views.ChatToItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    companion object{
        val TAG="Chatlog"
    }

    val adapter =GroupAdapter<GroupieViewHolder>()

    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        recyViewChatLogActivity.adapter=adapter

        toUser= intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        supportActionBar?.title=toUser?.username


        listenForMessages()


        btnSendChatLog.setOnClickListener {

            Log.d(TAG,"Attempt to send message" )
            performSendMessage()

        }


    }

    private fun listenForMessages() {
        val fromId=FirebaseAuth.getInstance().uid
        val toId=toUser?.uid
        val ref=FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        ref.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage=snapshot.getValue(ChatMessage::class.java)
                if (chatMessage!=null)
                {
                    if(chatMessage.fromId==FirebaseAuth.getInstance().uid)
                    {
                        val currentUser=LatestMessagesActivity.currentUser?: return
                        adapter.add(ChatFromItem(chatMessage.text,currentUser))
                    }
                    else{

                        adapter.add(ChatToItem(chatMessage.text,toUser!!))
                    }

                }
                recyViewChatLogActivity.scrollToPosition(adapter.itemCount -1)



            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
    }



    private fun performSendMessage() {

        val text=etChatLog.text.toString()


        val fromId=FirebaseAuth.getInstance().uid
        val user= intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId= user.uid

        if(fromId == null) return

        //val refrence=FirebaseDatabase.getInstance().getReference("/messages").push()
        val refrence=FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toRefrence=FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        val chatMessage= ChatMessage(refrence.key!!,text,fromId,toId,System.currentTimeMillis()/1000)
        refrence.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"Saved our chat message: ${refrence.key}")
                etChatLog.text.clear()
                recyViewChatLogActivity.scrollToPosition(adapter.itemCount-1)
            }
        toRefrence.setValue(chatMessage)

        val latestMessageRef=FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)
        val latestMessageToRef=FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }




}

