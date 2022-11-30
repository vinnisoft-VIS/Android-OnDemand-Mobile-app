package com.vrsidekick.utils

import android.util.Log

object LogHelper {
    private const val isDebug = true

    fun  print(tag:String,message:String,logType:Int = Log.DEBUG){

        if(isDebug){
            when(logType){
                Log.ERROR -> Log.e(tag, message)
                else -> Log.d(tag, message)
            }
        }
    }
}