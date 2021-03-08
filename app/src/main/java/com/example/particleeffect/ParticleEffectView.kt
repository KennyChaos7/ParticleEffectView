package com.example.particleeffect

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import java.util.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class ParticleEffectView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val pointCount = 500
    private var pointPaint: Paint = Paint()
    private val ppList: MutableList<ParticlePoint> = mutableListOf()
    private var anim: ValueAnimator
    private val insideCirclePath = Path()
    private val outsideCirclePath = Path()

    init {
        pointPaint.color = Color.WHITE
        pointPaint.strokeWidth = 2f

        anim = ValueAnimator.ofFloat(0f, 1f)
        anim.duration = 5000
        anim.repeatCount = -1
        anim.interpolator = LinearInterpolator()
        anim.addUpdateListener { va ->
            if (va.animatedValue != 0) {
                ppList.forEach {
                    val speedRandom = Random().nextInt(15).toFloat()
                    it.speed = speedRandom

                    it.pos = calcPos(it.pos, it.speed, it.quadrant, it.state)
                    it.state = checkState(it.pos, it.centerPos,it.minDistance, it.maxDistance)

                }
                invalidate()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        //  内圆，粒子起点
        insideCirclePath.addCircle((width / 2).toFloat(), (height / 2).toFloat(), (width / 4).toFloat(), Path.Direction.CCW)
        val insidePathMeasure = PathMeasure(insideCirclePath, false)
        //  外圆，粒子终点
        outsideCirclePath.addCircle((width / 2).toFloat(), (height / 2).toFloat(), (width / 2).toFloat(), Path.Direction.CCW)
        val outsidePathMeasure = PathMeasure(outsideCirclePath, false)

        val random = Random()
        for (index in 0..pointCount) {
            val startPos = insidePathMeasure.getPosTan(index)
            val endPos = outsidePathMeasure.getPosTan(index)
            val randomX = random.nextInt(5)
            val randomY = random.nextInt(5)
            startPos[0] += randomX.toFloat()
            startPos[1] += randomY.toFloat()
            endPos[0] += randomX.toFloat()
            endPos[1] += randomY.toFloat()
            val centerPos = FloatArray(2)
            centerPos[0] = (width/2).toFloat()
            centerPos[1] = (height/2).toFloat()
            val quadrant =  if (startPos[0] < (width / 2).toFloat())
                if (startPos[1] < (height / 2).toFloat())
                    1
                else
                    2
            else if (startPos[1] < (height / 2).toFloat())
                3
            else
                4
            val distanceCTS = abs(
                    sqrt(
                            centerPos[0].toDouble().pow(2.0) + centerPos[1].toDouble().pow(2.0)
                    )      - sqrt(
                            endPos[0].toDouble().pow(2.0) + endPos[1].toDouble().pow(2.0)
                    )
            )
            val distanceCTE = abs(
                    sqrt(
                            centerPos[0].toDouble().pow(2.0) + centerPos[1].toDouble().pow(2.0)

                    )
                            - sqrt(
                                    endPos[0].toDouble().pow(2.0) + endPos[1].toDouble().pow(2.0)
                    )
            )
            val pos = startPos.copyOf()
            ppList.add(
                    ParticlePoint(
                            pos,
                            centerPos,
                            distanceCTS,
                            distanceCTE,
                            5f,
                            0,
                            quadrant
                    ))
        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawParticlePoint()
    }

    private fun calcPos(pos: FloatArray, speed: Float, quadrant: Int, state: Int): FloatArray {
        when(quadrant) {
            1 -> {
                if (state == 0) {
                    pos[0] -= speed
                    pos[1] -= speed
                } else {
                    pos[0] += speed
                    pos[1] += speed
                }
                Log.e("-", "${pos[0]} - ${pos[1]}")

            }

            2 -> {
                if (state == 0) {
                    pos[0] -= speed
                    pos[1] += speed
                } else {
                    pos[0] -= speed
                    pos[1] += speed
                }
            }

            3 -> {
                if (state == 0) {
                    pos[0] += speed
                    pos[1] -= speed
                } else {
                    pos[0] -= speed
                    pos[1] += speed
                }
            }

            4 -> {
                if (state == 0) {
                    pos[0] += speed
                    pos[1] += speed
                } else {
                    pos[0] -= speed
                    pos[1] -= speed
                }
            }
        }
        return pos
    }

    private fun checkState(pos: FloatArray, centerPos: FloatArray,minDistance: Double,  maxDistance: Double): Int {
        val distance = abs(
                        sqrt(
                                pos[0].toDouble().pow(2.0) + pos[1].toDouble().pow(2.0)
                        ) - sqrt(
                                centerPos[0].toDouble().pow(2.0) + centerPos[1].toDouble().pow(2.0)
                        )
        )
        var result =  if (distance > maxDistance)
            1
        else
            0

        return result
    }


    private fun Canvas.drawParticlePoint() {

        val outsideCircle: Paint = Paint()
        outsideCircle.color = Color.RED
        drawPath(outsideCirclePath, outsideCircle)
        val insideCircle: Paint = Paint()
        outsideCircle.color = Color.GREEN
        drawPath(insideCirclePath, insideCircle)

        ppList.forEach {
            drawPoint(it.pos[0], it.pos[1], pointPaint)
        }
        if (!anim.isRunning) {
            anim.start()
        }
    }

    private fun PathMeasure.getPosTan(index: Int): FloatArray {
        val pos = FloatArray(2)
        val tan = FloatArray(2)
        getPosTan((index / pointCount.toFloat()) * length, pos, tan)
        return pos
    }

}