package com.example.convo.Data

data class USERDATA(
    var uid:String?="",
    var name:String?="",
    var number:String?="",
    var imageURL:String?="",
    var UserChoice:String?=""
)
data class DOCTORINFO(
    var duid:String?="",
    var name:String?="",
    var number:String?="",
    var description:String="",
    var imageURL:String?="",
    var UserChoice:String?=""
)

data class Message(
    var sentBy:String?="",
    var message:String?="",
    var timestamp: String ="",
    var recipientId: String=""
)
data class Conversation(
    var userId: String = "",
    var doctorId: String = "",
    var lastMessage: String = "",
    var timestamp: String?=""
)
data class Note(
    val noteId:String="",
    val title: String = "",
    val content: String = "",
    val timestamp: String = "",
)
fun main(){
    USERDATA("userId","name","number","imageURL","UserChoice")
    DOCTORINFO("duid","name","number","description","imageUrl","UserChoice")
    Message("sentBy","message","timestamp","recipientId")
    Conversation("userId","doctorId","lastMessage","timestamp")
    Note("noteId","title","content","timestamp")

}
