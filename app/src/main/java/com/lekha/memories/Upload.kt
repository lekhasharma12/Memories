package com.lekha.memories

import android.widget.Button

class Upload {
    var caption: String? = null
    var imageUrl: String? = null
    var uname: String? = null
    var uid: String? = null
    var likes: Int? = 0
    var id:String? = null

    constructor() {
        //empty constructor needed
    }

    constructor(caption: String, imageUrl: String, uname: String, uid: String, likes: Int) {
        var caption = caption
        if (caption.trim { it <= ' ' } == "") {
            caption = ""
        }

        this.caption = caption
        this.imageUrl = imageUrl
        this.uname = uname
        this.likes = likes
        this.uid = uid
    }
}
