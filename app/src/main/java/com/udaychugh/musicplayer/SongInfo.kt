package com.udaychugh.musicplayer

class SongInfo {
    var Title:String?=null
    var AuthorName:String?=null
    var SongUrl:String?=null
    constructor(Title:String, AuthorName:String, SongURL:String){
        this.Title=Title
        this.AuthorName=AuthorName
        this.SongUrl=SongUrl
    }
}