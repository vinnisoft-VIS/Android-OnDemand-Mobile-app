package com.vrsidekick.utils

import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import kotlin.contracts.contract


enum class WindowSizeClass {COMPACT,MEDIUM,EXPANDED}
object SizeConfig {
    var screenWidth : Int = 0
    var screenHeight : Int = 0
    var screenWidthDP : Float = 0F
    var screenHeightDP : Float = 0F



    fun init(context:AppCompatActivity){

        val outMetrics = DisplayMetrics()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            val display = context.display
            display?.getMetrics(outMetrics)
        }else{
            val display = (context.windowManager.defaultDisplay)
            display.getMetrics(outMetrics)
        }

        screenWidth = outMetrics.widthPixels
        screenWidthDP = outMetrics.widthPixels/outMetrics.density
        screenHeight = outMetrics.heightPixels
        screenHeightDP = outMetrics.heightPixels /outMetrics.density

        LogHelper.print(tag =  "SizeConfig", message = "SizeWidth = $screenWidth")
        LogHelper.print(tag =  "SizeConfig", message = "SizeHeight = $screenHeight")


        LogHelper.print(tag =  "SizeConfig", message = "SizeWidthDP = $screenWidthDP")
        LogHelper.print(tag =  "SizeConfig", message = "SizeHeightDP = $screenHeightDP")


    }


}