package com.example.particleeffect

data class ParticlePoint(
        var pos: FloatArray,
        val centerPos: FloatArray,
        val minDistance: Double,
        val maxDistance: Double,
        var speed: Float,
        var state: Int,
        val quadrant: Int
)