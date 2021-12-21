package com.rumeysaozer.chatapp.model

class SonMesajlar(val id: String, val text: String, val fromId: String, val toId: String, val timestam: Long, val userName: String, val imageUrl : String) {
    constructor() : this("","", "","",-1,"", "")
}