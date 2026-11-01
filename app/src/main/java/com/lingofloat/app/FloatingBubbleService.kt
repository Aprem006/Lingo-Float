
package com.lingofloat.app

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import kotlin.math.abs

class FloatingBubbleService : Service() {

    private lateinit var windowManager: WindowManager
    private var bubbleView: TextView? = null
    private lateinit var params: WindowManager.LayoutParams

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createBubble()
    }

    private fun createBubble() {

        val bubble = TextView(this).apply {
            text = "L"
            textSize = 23f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD

            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.rgb(117, 76, 255))
                setStroke(3, Color.WHITE)
            }

            elevation = 12f
        }

        val windowType =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }

        params = WindowManager.LayoutParams(
            58,
            58,
            windowType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 30
            y = 250
        }

        bubble.setOnTouchListener { view, event ->

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX +
                            (event.rawX - initialTouchX).toInt()

                    params.y = initialY +
                            (event.rawY - initialTouchY).toInt()

                    windowManager.updateViewLayout(view, params)
                    true
                }

                MotionEvent.ACTION_UP -> {

                    val moved = abs(event.rawX - initialTouchX) > 10 ||
                            abs(event.rawY - initialTouchY) > 10

                    if (!moved) {
                        Toast.makeText(
                            this,
                            "LingoFloat bubble is ready!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    true
                }

                else -> false
            }
        }

        windowManager.addView(bubble, params)
        bubbleView = bubble
    }

    override fun onDestroy() {
        super.onDestroy()

        bubbleView?.let {
            if (it.isAttachedToWindow) {
                windowManager.removeView(it)
            }
        }

        bubbleView = null
    }
}
