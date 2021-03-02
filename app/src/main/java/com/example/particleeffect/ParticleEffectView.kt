package com.example.particleeffect

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import java.util.*

class ParticleEffectView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val pointCount = 500
    private var maxWidth = 0f
    private var maxHeight = 0f

    private var pointPaint: Paint = Paint()
    private val ppList: MutableList<ParticlePoint> = mutableListOf()
    private var anim: ValueAnimator
    private val circlePath = Path()

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
                    val speedRandom = Random().nextInt(15).toFloat()
                    it.speed = speedRandom

                    var tmpY = 0f
                    if (it.state == 1)
                        tmpY = it.y + it.speed
                    else if (it.state == 2)
                        tmpY = it.y - it.speed
                    it.y = tmpY
                    if (tmpY > it.maxY)
                        it.state = 2
                    else if (tmpY < (height / 2).toFloat())
                        it.state = 1

//                    var tmpX = 0f
//                    var tmpY = 0f
//                    when(it.state){
//                        1 -> {
//                            tmpX = it.x - it.speed
//                            tmpY = it.y - it.speed
//                        }
//                        2 -> {
//                            tmpX = it.x + it.speed
//                            tmpY = it.y - it.speed
//                        }
//                        3 -> {
//                            tmpX = it.x - it.speed
//                            tmpY = it.y + it.speed
//                        }
//                        4 -> {
//                            tmpX = it.x + it.speed
//                            tmpY = it.y + it.speed
//                        }
//                    }
//                    it.x = tmpX
//                    it.y = tmpY


                }
                invalidate()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        circlePath.addCircle((width/2).toFloat(), (height/2).toFloat(), (width/4).toFloat(), Path.Direction.CCW)
        val pathMeasure = PathMeasure(circlePath, false)
        val pos = FloatArray(2)
        val tan = FloatArray(2)
        val random = Random()
        for (index in 0..pointCount) {
            pathMeasure.getPosTan((index / pointCount.toFloat()) * pathMeasure.length, pos, tan)
            val x = pos[0] + random.nextInt(5)
            val y = pos[1] + random.nextInt(5)
            val bolder = FloatArray(4)
            bolder[0] = (width/4).toFloat()
            bolder[1] = (width/4).toFloat()
            bolder[2] = width.toFloat()
            bolder[3] = height.toFloat() / 2 + width.toFloat() / 2
            ppList.add(
                    ParticlePoint(
                            x,
                            y,
                            width.toFloat(),
                            height.toFloat() / 2 + width.toFloat() / 2,
                            5f,
                           1))
            Log.e("height/2", "${height / 2}")
        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawParticlePoint()
    }


    private fun Canvas.drawParticlePoint() {
        ppList.forEach {
            drawPoint(it.x, it.y, pointPaint)
        }
//        drawPath(circlePath, pointPaint)
        if (!anim.isRunning) {
            anim.start()
        }
    }

//    private fun checkState(x: Float, y: Float, bolder: FloatArray) : Int{
//        return if (x > bolder[0] && y < bolder[1])
//                1
//        else if (x > bolder[0] && x < bolder[3])
//            2
//    }

}