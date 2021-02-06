package com.example.kotlinmessenger.views

import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatFromItem(val text:String,val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.tvFromRow.text=text

        //load imnage in star
        val uri=user.profileImageUrl
        val targetImageView=viewHolder.itemView.ivChatFromRow
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }


}
class ChatToItem(val text:String,val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.tvToRow.text=text

        //load imnage in star
        val uri=user.profileImageUrl
        val targetImageView=viewHolder.itemView.ivChatToRow
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }


}