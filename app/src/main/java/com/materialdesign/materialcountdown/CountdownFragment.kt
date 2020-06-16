package com.materialdesign.materialcountdown

import android.content.res.AssetManager
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_countdown.*
import kotlinx.android.synthetic.main.fragment_countdown.view.*
import kotlinx.android.synthetic.main.layout_time_spinner.view.*
import kotlinx.android.synthetic.main.layout_timer.view.*
import java.util.*

const val TIME_INTERVAL = 1000L
class CountdownFragment : Fragment() {

    var initY = 0F
    var sampleX = 0F
    var positiveDirection = false
    var started = false
    lateinit var countDownTimer: CountDownTimer
    var loaded = false
    lateinit var assets: AssetManager
    lateinit var soundPool: SoundPool
    var soundId = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_countdown, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        countDownTimerImg.resetTimerAnimation(startCountDownBtn, countDownTimerImg.timerTV, timePicker)
        timePicker.numPickerSeconds.minValue = 0
        timePicker.numPickerMinutes.minValue = 0
        timePicker.numPickerSeconds.maxValue = 59
        timePicker.numPickerMinutes.maxValue = 59
        countDownTimerImg.setOnLongClickListener {
            trashAnimation()
            true
        }
        countDownTimerImg.setOnClickListener {
            if(countDownTimerImg.timerTV.text == "DONE") {
                soundPool.stop(soundId)
                countDownTimerImg.resetTimerAnimation(startCountDownBtn, countDownTimerImg.timerTV, timePicker)
            }
        }
        startCountDownBtn.setOnClickListener {
            initCountdown()
        }
        assets = context!!.assets
        soundPool = SoundPool.Builder().setMaxStreams(5).setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()).build()//SoundPool(5, AudioManager.STREAM_MUSIC)
        soundId = soundPool.load(assets.openFd("alarm.mp3"), 1)
        soundPool.setOnLoadCompleteListener(object : SoundPool.OnLoadCompleteListener {
            override fun onLoadComplete(soundPool: SoundPool?, sampleId: Int, status: Int) {
                loaded = true
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun initCountdown() {
        countDownTimerImg.timerTV.text = ""
        countDownTimerImg.timerTV.visibility = View.VISIBLE
        countDownTimerImg.timerTV.animate().alpha(1.0f).setDuration(1500L).withStartAction {
            timePicker.animate().alpha(-1.0f).setDuration(1500L).withEndAction {
                timePicker.visibility = View.INVISIBLE
        }.withEndAction {
            countDownTimer.start()
            }}.start()
        val timeInFuture = (timePicker.numPickerSeconds.value + (timePicker.numPickerMinutes.value * 60)) * TIME_INTERVAL
         setTimerTextView(countDownTimerImg.timerTV, timeInFuture)
        countDownTimer = object : CountDownTimer(timeInFuture, TIME_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / TIME_INTERVAL).toInt()
                if(secondsLeft == 11 && (timeInFuture > 45000)) {
                    val finishX = VIEW_X / 2
                    val finishY = VIEW_Y / 2
                    countDownTimerImg.animate().translationYBy(-(countDownTimerImg.y - finishY))
                        .translationXBy(-(countDownTimerImg.x - finishX)).setDuration(500L).rotation(0f).start()
                }
                if(secondsLeft <= 10) {
                    countDownTimerImg.animate().scaleXBy(.25f).scaleYBy(.25f).setDuration(500L)
                        .withStartAction {
                            countDownTimerImg.imgBackgroundCircle2.setFillColor(context!!.getColor(android.R.color.white))
                            countDownTimerImg.timerTV.setTextColor(context!!.getColor(R.color.magenta))
                            countDownTimerImg.timerTV.animate().scaleXBy(.25f).scaleYBy(.25f) }
                        .withEndAction {
                            countDownTimerImg.animate().scaleXBy(-.25f).scaleYBy(-.25f).setDuration(500L).withStartAction {
                                countDownTimerImg.imgBackgroundCircle2.setFillColor(context!!.getColor(R.color.magenta))
                                countDownTimerImg.timerTV.setTextColor(context!!.getColor(android.R.color.white))
                                countDownTimerImg.timerTV.animate().scaleXBy(-.25f).scaleYBy(-.25f)
                            }
                    }.start()
                }
                if(secondsLeft == 0) {
                    countDownTimerImg?.timerTV?.text = "DONE"
                    if(loaded) {soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)}
                } else {
                    setTimerTextView(countDownTimerImg.timerTV, millisUntilFinished)
                }
            }

            override fun onFinish() {
            }
        }

        if(timeInFuture > 45000) {
            beginningAnimation()
            sampleX = randomizeXDirection()
            initY = -countDownTimerImg.y
        }
    }

    private fun setTimerTextView(timerTV: TextView, l: Long) {
        var l_seconds = (l % 60000).toString()
        val l_minutes = (l / 60000).toString()
        val l_hours = (l / 3600000).toString()
        l_seconds =
            if (l_seconds.length == 5) l_seconds.substring(
                0,
                2
            ) else if (l_seconds.length == 3) "00" else "0" + l_seconds.substring(
                0,
                1
            )
        val a = if(l_minutes.toInt() == 0) "$l_seconds" else "$l_minutes$l_seconds"
        if (a.length == 1) {
            timerTV.setText("0" + l_seconds.substring(0, 2))
        } else if (a.length == 2) {
            timerTV.setText(l_seconds.substring(0, 2))
        } else if (a.length == 3) {
            timerTV.setText(l_minutes + ":" + l_seconds.substring(0, 2))
        } else if (a.length == 4) {
            timerTV.setText(l_minutes + ":" + l_seconds.substring(0, 2))
        } else if (a.length == 5) {
            timerTV.setText(l_hours + ":" + l_minutes + ":" + l_seconds.substring(0, 2))
        } else if (a.length == 6) {
            timerTV.setText(l_hours + ":" + l_minutes + ":" + l_seconds.substring(0, 2))
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun trashAnimation() {
        countDownTimerImg.animate().cancel()
        countDownTimer.cancel()
        trashCanImg.initTrashAnimation()
        var trashAnimation = trashCanImg.getAnimatedVectorDrawable(activity!!.getDrawable(R.drawable.avd_trash_open))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            trashAnimation.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                override fun onAnimationStart(drawable: Drawable?) {
                    super.onAnimationStart(drawable)
                    countDownTimerImg.animate().translationXBy(-(countDownTimerImg.x - trashCanImg.x))
                        .translationYBy(-(countDownTimerImg.y - (trashCanImg.y - 200f))).setDuration(750L).rotation(0f)
                        .apply { interpolator = AnticipateInterpolator() }.scaleYBy(-.99f).scaleXBy(-.99f)
                        .withStartAction {
                            if(startCountDownBtn.visibility == View.VISIBLE) {
                                startCountDownBtn.animate().alpha(-1.0f).start()}
                        }.withEndAction {
                            countDownTimerImg.visibility = View.GONE
                            trashAnimation = trashCanImg.getAnimatedVectorDrawable(activity!!.getDrawable(R.drawable.avd_trash_close))
                            trashAnimation.start()
                            trashAnimation.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                                override fun onAnimationEnd(drawable: Drawable?) {
                                    super.onAnimationEnd(drawable)
                                    trashCanImg.animate().translationYBy(200f)
                                        .withEndAction {
                                            resetObjectPosition() }.start()
                                }
                            })
                        }.start()
                }})}
        trashAnimation.start()

    }

    private fun resetObjectPosition() {
        trashCanImg.visibility = View.GONE
        trashCanImg.animate().translationYBy(-200f)
        countDownTimerImg.animate().scaleXBy(.99f).scaleYBy(.99f)
            .translationXBy((VIEW_X / 2) - countDownTimerImg.x)
            .translationYBy(((VIEW_Y / 2) - countDownTimerImg.y))
            .withEndAction { countDownTimerImg.resetTimerAnimation(startCountDownBtn, countDownTimerImg.timerTV, timePicker) }
    }

    fun beginningAnimation() {

        crane.initCraneAnimation()
        countDownTimerImg.timerTV.visibility = View.VISIBLE
        var craneAnimation: AnimatedVectorDrawable? = crane.getAnimatedVectorDrawable(activity!!.getDrawable(R.drawable.crane_setup_2))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            craneAnimation!!.registerAnimationCallback (@RequiresApi(Build.VERSION_CODES.M) object : Animatable2.AnimationCallback(){
                override fun onAnimationEnd(drawable: Drawable?) {
                    super.onAnimationEnd(drawable)
                    craneAnimation = crane.getAnimatedVectorDrawable(activity!!.getDrawable(R.drawable.crane_ext_3))
                    craneAnimation!!.registerAnimationCallback(object: Animatable2.AnimationCallback() {

                        override fun onAnimationEnd(drawable: Drawable?) {
                            super.onAnimationEnd(drawable)
                            craneAnimation = crane.getAnimatedVectorDrawable(activity!!.getDrawable(R.drawable.crane_full_4))
                            craneAnimation!!.start()
                            startCountDownBtn.launchButtonAnimation(countDownTimerImg).withStartAction {
                                // countdown timer animation continuing
                                continueTimerAnimation()
                            }.withEndAction {

                                // drop crane from extension
                                startCountDownBtn.dropButtonToCraneAnimation(crane).withStartAction {

                                    // set animator to crane collapse
                                    craneAnimation = crane.getAnimatedVectorDrawable(activity!!.getDrawable(R.drawable.crane_collapse_5))
                                    craneAnimation!!.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                                        override fun onAnimationEnd(drawable: Drawable?) {
                                            super.onAnimationEnd(drawable)

                                            // hide and drop start button and crane
                                            startCountDownBtn.hideCraneAndButtonAnimation(crane)
                                        }})
                                    craneAnimation!!.start()
                                }}.start()
                        }})
                    craneAnimation!!.start()
                }})}
        craneAnimation!!.start()
    }

    private fun continueTimerAnimation() {
        countDownTimerImg.animate().translationXBy(sampleX).translationYBy(initY)
            .setStartDelay(500L)
            .rotationBy(determineRotation(initY))
            .withEndAction{ calcNextAnimation(countDownTimerImg)}
            .setInterpolator(LinearInterpolator())
            .setDuration(Math.abs(initY * 4).toLong())
            .start()
    }

    //TODO be able to handle initial sampleX to be random within a certain scope
    // Clean up!
    private fun calcNextAnimation(view: View) {
        var modifier = 0f
        var legX = 0f
        var legY = 0f
        val currentX = view.x
        val currentY = view.y
        println("CURRENT_XY: $currentX, $currentY")

        // Error cause we're off the screen
        if(currentX > VIEW_X || currentX < 0f || currentY > VIEW_Y || currentY < 0f) {
            Log.e("ERROR: Out of Bounds", "cx:$currentX, cy:$currentY")
        }

        // We're in 1 of the 4 corners
        if((currentX == 0f && currentY == 0f) || (currentX == VIEW_X && currentY == VIEW_Y)
            || (currentX == 0f && currentY == VIEW_Y) || (currentX == VIEW_X && currentY == 0f)) {
            initY *= -1
            sampleX *= -1

            view.animate().translationYBy(Math.floor(initY.toDouble()).toFloat())
                //.rotationBy(countDownTimerAnimation.determineRotation(initY))
                .rotationBy(determineRotation(initY))
                .setStartDelay(0L)
                .translationXBy(Math.floor(sampleX.toDouble()).toFloat())
                .setDuration(Math.abs(initY * 4).toLong())
                .withEndAction { calcNextAnimation(countDownTimerImg) }

            return
        }

        // If image is on top or bottom calculate next X point and flip sign
        if(currentX == 0f || currentX == VIEW_X) {
            sampleX = -sampleX

            if(initY > 0f) {
                legY = VIEW_Y - currentY
            } else {
                legY = 0f - currentY
            }
            modifier = Math.abs(legY /  initY)
            sampleX *= modifier
            initY = legY
            println("X1: $sampleX")
        } else {println("HERE: NO SIGN CHANGE X")}
        // If image is on left or right wall calculate next Y point and flip sign
        if(currentY == 0f || currentY == VIEW_Y) {
            initY = -initY

            if(sampleX > 0f) {
                legX = VIEW_X - currentX
            } else {
                legX = 0f - currentX
            }
            modifier = Math.abs(legX / sampleX)
            sampleX = legX
            initY *= modifier
        } else {println("HERE: NO SIGN CHANGE Y")}


        // If next calculated Y point is off screen, recalculate
        if(Math.abs(initY) > VIEW_Y) {
            println("Y2B: $initY, $sampleX")
            val alt_Y = Math.abs(initY) - VIEW_Y
            val alt_X = sampleX * Math.abs(alt_Y / initY)
            sampleX = VIEW_X - alt_X
            initY = -VIEW_Y
            if(currentY == 0f) {initY *= -1}
        }

        // If next calculated X point is off screen, recalculate
        sampleX = checkXOutOfView(currentX)
        /*if(Math.abs(sampleX) > VIEW_X) {
            val alt_X = Math.abs(sampleX) - VIEW_X
            val alt_y = initY * Math.abs(alt_X / sampleX)
            sampleX = -VIEW_X
            if(currentX == 0f) {sampleX *= -1}
            if(sampleX < 0f && Math.abs(sampleX) > currentX) {
                sampleX = 0f - currentX
            }
        }*/

        if( (sampleX + currentX) > VIEW_X) {
            val xRight = VIEW_X - currentX
            initY *= (xRight / sampleX)
            sampleX = xRight
        }

        // Set off next animation with rounded x & y translations
        performTimerAnimation(countDownTimerImg, Math.floor(sampleX.toDouble()).toFloat(),
            Math.floor(initY.toDouble()).toFloat())
    }

    private fun checkXOutOfView(currentX: Float): Float {
        if(Math.abs(sampleX) > VIEW_X) {
            val alt_X = Math.abs(sampleX) - VIEW_X
            val alt_y = initY * Math.abs(alt_X / sampleX)
            sampleX = -VIEW_X
            if(currentX == 0f) {sampleX *= -1}
            if(sampleX < 0f && Math.abs(sampleX) > currentX) {
                sampleX = 0f - currentX
            }
        }
        return sampleX
    }

    //Move to class
    fun performTimerAnimation(view: View, x: Float, y: Float, delay: Long = 0L) {
        view.animate().translationXBy(x).translationYBy(y)
            .setStartDelay(delay).rotationBy(determineRotation(y))
            .withEndAction{ calcNextAnimation(view)}
            .setDuration(Math.abs(y * 4).toLong())
            .apply { if(!started) {
                    started = true
                    interpolator = LinearInterpolator()
                    start()
                }}
    }

    //Move to class
    private fun randomizeXDirection(): Float {
        var initX = (Random().nextInt(100).toFloat()) * 2
        if(!positiveDirection) {
            initX *= -1
            positiveDirection = true
        } else { positiveDirection = false}
        return initX
    }

    //Move to class
    private fun determineRotation(y: Float): Float {
        var result = (360f * Math.abs(y / VIEW_Y) * .125f)
        if(!positiveDirection) {result *= -1}
        return result
    }
}