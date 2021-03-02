package com.example.particleeffect

data class ParticlePoint(
        var x: Float,
        var y: Float,
        val maxX: Float,
        val maxY: Float,
        var speed: Float,
        //  1: 往下
        //  2: 往上
        var animState: Int
)
