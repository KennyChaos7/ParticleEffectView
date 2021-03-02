package com.example.particleeffect

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import java.util.*

class ParticleEffectView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var pointPaint: Paint = Paint()
    private val ppList: MutableList<ParticlePoint> = mutableListOf()
    private var anim: ValueAnimator

    init {
        pointPaint.color = Color.WHITE
        pointPaint.strokeWidth = 2f

        anim = ValueAnimator.ofFloat(0f, 1f)
        anim.duration = 1000
        anim.repeatCount = -1
        anim.interpolator = LinearInterpolator()
        anim.addUpdateListener { va ->
            if (va.animatedValue != 0) {
                ppList.forEach {
                    var tmpY = 0f
                    val speedRandom = Random().nextInt(15).toFloat()
                    it.speed = speedRandom

                    if (it.animState == 1)
                        tmpY = it.y + it.speed
                    else if (it.animState == 2)
                        tmpY = it.y - it.speed
                    it.y = tmpY

                    if (tmpY > it.maxY)
                        it.animState = 2
                    else if (tmpY < (height / 2).toFloat())
                        it.animState = 1
                }
                invalidate()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val random = Random()
        for (index in 0..100)
            ppList.add(
                    ParticlePoint(
                            random.nextInt(width).toFloat(),
                            (height/2).toFloat(),
                            width.toFloat(),
                            height.toFloat(),
                            5f,
                            1))
        Log.e("height/2", "${height/2}")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawParticlePoint()
    }


    private fun Canvas.drawParticlePoint() {
        ppList.forEach {
            drawPoint(it.x, it.y, pointPaint)
        }
//        Paint().also {
//            it.color = Color.WHITE
//            drawCircle( (width/2).toFloat(), (height/2).toFloat(), (width/4).toFloat(), it)
//        }
        if (!anim.isRunning) {
            anim.start()
        }
    }

}