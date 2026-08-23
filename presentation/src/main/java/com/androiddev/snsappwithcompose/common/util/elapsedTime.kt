package com.androiddev.snsappwithcompose.common.util

import java.text.SimpleDateFormat

fun elapsedTime(
    postedTime: String
): String{
    val formatter= SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val trans_date=formatter.parse(postedTime)
    val postedmillis=trans_date.time
    val curTime=System.currentTimeMillis()
    var diffTime=(curTime-postedmillis)/1000
    var diffstr="방금 전"
    if(diffTime>= TimeValue.SEC.value){
        for(i in TimeValue.values()){
            diffTime/=i.value
            if(diffTime<i.maximum){
                diffstr=diffTime.toString()+i.msg
                break
            }
        }
    }
    return diffstr
}
enum class TimeValue(val value: Int,val maximum : Int, val msg : String) {
    SEC(60,60,"분 전"),
    MIN(60,24,"시간 전"),
    HOUR(24,30,"일 전"),
    DAY(30,12,"달 전"),
    MONTH(12,Int.MAX_VALUE,"년 전")
}