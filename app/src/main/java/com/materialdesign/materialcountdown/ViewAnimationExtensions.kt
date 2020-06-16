package com.materialdesign.materialcountdown

import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

const val INTERVAL = 1500L
const val VIEW_Y = 1269F
const val VIEW_X = 770F

fun ImageView.initCraneAnimation() {
    animate().translationYBy(100f).withEndAction {
        this.visibility = View.VISIBLE
        this.animate().translationYBy(-100f)
    }.start()
}

fun Button.hideCraneAndButtonAnimation(crane: ImageView) {
    animate().translationYBy(400f).setDuration(500L).withStartAction {
        crane.animate().translationYBy(100f)
    }.withEndAction {
        visibility = View.GONE
        animate().translationYBy(-400f)
        crane.visibility = View.GONE
        crane.animate().translationYBy(-100f)
    }.start()
}

fun ImageView.getAnimatedVectorDrawable(drawable: Drawable?): AnimatedVectorDrawable {
    setImageDrawable(drawable)
    return (drawable as AnimatedVectorDrawable)
}

fun Button.launchButtonAnimation(imageView: View): ViewPropertyAnimator {
    return animate().translationYBy((imageView.y + (imageView.width)) - this.y)
        .setInterpolator(OvershootInterpolator())
        .setDuration(INTERVAL)
}

fun Button.dropButtonToCraneAnimation(crane: ImageView): ViewPropertyAnimator {
    return animate().translationYBy((crane.y - this.y))
        .setDuration(INTERVAL)
        .setInterpolator(AccelerateInterpolator())
}

/** Trash Can Animation */
fun ImageView.initTrashAnimation() {
    animate().translationYBy(200f).withEndAction {
        this.visibility = View.VISIBLE
        this.animate().translationYBy(-200f)
    }.start()
}

fun View.resetTimerAnimation(startButton: Button, textView: TextView, spinner: View, delay: Long = 0L) {
    visibility = View.GONE
    animate().translationYBy(735f).setStartDelay(delay)
        .withStartAction {
            textView.visibility = View.INVISIBLE
            textView.animate().alpha(-1.0f).start()
            startButton.visibility = View.GONE
            startButton.animate().alpha(-1.0f).setDuration(1000L)
            spinner.animate().alpha(1.0f).setDuration(1500L).withEndAction {
                spinner.visibility = View.VISIBLE
            }.start()
        }.withEndAction {
            visibility = View.VISIBLE
            animate().translationYBy(-735f).setInterpolator(OvershootInterpolator())
                .setDuration(750L).withEndAction {
                    startButton.visibility = View.VISIBLE
                    startButton.animate().alpha(1.0f).setDuration(750L)
                }}.start()
}