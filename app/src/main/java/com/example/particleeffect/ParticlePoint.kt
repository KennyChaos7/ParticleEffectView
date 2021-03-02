package com.example.particleeffect

data class ParticlePoint(
        var x: Float,
        var y: Float,
        val maxX: Float,
        val maxY: Float,
        var speed: Float,
        //  位于第几象限
        //  1: 往左上 x- y-
        //  2: 往右上 x+ y-
        //  3: 往左下 x- y+
        //  4: 往右下 x+ y+
        var state: Int
)
