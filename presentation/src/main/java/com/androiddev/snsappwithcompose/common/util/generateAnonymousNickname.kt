package com.androiddev.snsappwithcompose.common.util

fun generateAnonymousNickname(): String {
    val charlist = listOf("a", "b", "c", "d", "e","f","g","h","i","j","1","2","3","4","0","5","6","7","8","9")
    var tempNick = ""
    repeat(6) {
        tempNick+=charlist.random()
    }
    return tempNick
}