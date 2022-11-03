package com.example.proiect_licenta_2023.Model

class Post {

    private var postid:String=""
    private var description:String=""
    private var postimage:String=""
    private var publisher:String=""

    constructor()

    constructor(postid: String, description: String, postimage: String, publisher: String) {
        this.postid = postid
        this.description = description
        this.postimage = postimage
        this.publisher = publisher
    }

    fun getPostId(): String{
        return postid
    }

    fun getDescription(): String{
        return description
    }

    fun getPostImage(): String{
        return postimage
    }

    fun getPublisher(): String{
        return publisher
    }


    fun setPostId(postid: String){
        this.postid=postid
    }

    fun setDescription(description: String){
        this.description=description
    }

    fun setImage(postimage: String){
        this.postimage=postimage
    }

    fun setPublisher(publisher: String){
        this.publisher=publisher
    }
}