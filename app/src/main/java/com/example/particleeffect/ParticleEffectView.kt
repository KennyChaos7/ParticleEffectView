package com.example.particleeffect

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import java.util.*
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class ParticleEffectView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    //  渐隐模式
    val FadeMode = 1
    //  回弹模式
    val ReboundMode = 2

    private var radius: Float = 0f
    private val pointCount = 200
    private var pointPaint: Paint = Paint()
    private val ppList: MutableList<ParticlePoint> = mutableListOf()
    private var anim: ValueAnimator
    private val insideCirclePath = Path()
    private val insideCircle: Paint = Paint()

    private var mParticleType = ReboundMode

    init {
        pointPaint.color = Color.WHITE
        pointPaint.strokeWidth = 2f
        insideCircle.color = Color.GRAY
        anim = ValueAnimator.ofFloat(0f, 1f)
        anim.duration = 10000
        anim.repeatCount = -1
        anim.interpolator = LinearInterpolator()
        anim.addUpdateListener { va ->
            if (va.animatedValue != 0) {
                ppList.forEach {
                    val speedRandom = Random().nextInt(15).toFloat()
                    when (mParticleType) {
                        FadeMode -> {
                            //  核心算法来自https://mp.weixin.qq.com/s/p2nGt1g1doT1O8NwRqRW_Q
                            //  原算法需要分成四个象限来分别处理,过于繁琐
                            if (it.distance > it.maxDistance) {
                                it.distance = 0f
                            }
                            it.alpha = ((1f - it.distance / it.maxDistance)  * 225f).toInt()
                            it.distance += speedRandom
                        }
                        ReboundMode -> {
                            if (it.distance > it.maxDistance) {
                                it.positive = false
                            }
                            else if (it.distance <= 0) {
                                it.positive = true
                            }
                            it.distance = if (it.positive) {
                                it.distance + speedRandom
                            } else {
                                it.distance - speedRandom
                            }
                        }
                    }
                    //  斜边
                    val hypotenuse = radius + it.distance
                    it.x = (it.centerX + cos(it.angle) * hypotenuse).toFloat()
                    if (it.y > it.centerY) {
                        it.y = (sin(it.angle) * hypotenuse + it.centerY).toFloat()
                    } else {
                        it.y = (it.centerY - sin(it.angle) * hypotenuse).toFloat()
                    }
                }
                invalidate()
            }
        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        //  内圆半径
        radius = (w/4).toFloat()
        //  内圆中心坐标
        val centerX = (w / 2).toFloat()
        val centerY = (h / 2).toFloat()
        //  内圆，粒子起点
        insideCirclePath.addCircle(centerX, centerY, radius, Path.Direction.CCW)

        //  随机值
        val random = Random()

        val insidePathMeasure = PathMeasure(insideCirclePath, false)
        for (index in 0..pointCount) {
            //  当前点的坐标
            val nowPos = insidePathMeasure.getPosTan(index)
            //  生成随机值, 用来点的随机分布
            val randomX = random.nextInt(5)
            val randomY = random.nextInt(5)
            //  计算角度
            val angle = acos(((centerX - nowPos[0]) / radius).toDouble())
            //  生成随机x, y
            val x = nowPos[0] + randomX.toFloat()
            val y = nowPos[1] + randomY.toFloat()
            ppList.add(
                    ParticlePoint(
                            x,
                            y,
                            0f,
                            true,//    撞壁回弹模式使用
                            255,
                            angle,
                            radius,
                            centerX,
                            centerY
                    ))
        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawParticlePoint()
    }

    private fun Canvas.drawParticlePoint() {
        drawPath(insideCirclePath, insideCircle)

        ppList.forEach {
            pointPaint.alpha = it.alpha
            drawPoint(it.x, it.y, pointPaint)
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