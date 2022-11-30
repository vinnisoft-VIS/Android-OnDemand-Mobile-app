package com.vrsidekick.utils

import android.os.SystemClock
import android.view.View
import com.facebook.appevents.suggestedevents.ViewOnClickListener

class SafeClickListener(
    private var defaultInterval : Int = 3000,
    private val onSafeClick : (View) -> Unit
) :View.OnClickListener{

    var lastTimeClicked : Long = 0L

    override fun onClick(v: View?) {

        if(SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval){
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()

        if(v != null){
            onSafeClick(v)
        }

    }
}