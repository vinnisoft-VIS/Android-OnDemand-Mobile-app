package com.vrsidekick.utils

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.vrsidekick.R


fun ImageView.loadFromUrl(
    url: String?,
    placeholder: Int = R.drawable.img_placeholder_post,
    errorPlaceholder: Int = R.drawable.img_placeholder_post
) {

    Glide.with(this)
        .load(Global.getCompleteUrl(url))
        .placeholder(placeholder)
        .error(errorPlaceholder)
        .into(this)
}

fun View.setSafeOnClickListener(interval: Int = 3000, onSafeClick: (View) -> Unit) {

    val safeClickListener = SafeClickListener(defaultInterval = interval) {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)

}


fun Activity.getRootView(): View {
    return findViewById(android.R.id.content)
}




fun Context.convertDpToPx(dp:Float) : Float{
    return  TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,dp,
        this.resources.displayMetrics

    )
}


fun  Activity.isKeyboardOpen() : Boolean{
    val visibleBounds = Rect()
    this.getRootView().getWindowVisibleDisplayFrame(visibleBounds)

    val heightDiff = getRootView().height - visibleBounds.height()
    val marginOfError = Math.round(this.convertDpToPx(50F))
    return heightDiff >marginOfError

}

fun Activity.isKeyboardClosed() : Boolean{
return  !this.isKeyboardOpen()

}


fun Group.setAllOnClickListener(listener: (view:View) -> Unit){
    referencedIds.forEach { id ->
        rootView.findViewById<View>(id).setOnClickListener(listener)
    }

}

