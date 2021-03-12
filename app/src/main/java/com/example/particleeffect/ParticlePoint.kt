package com.example.particleeffect

data class ParticlePoint(
        var x: Float,
        var y: Float,
        var distance: Float,
        var positive: Boolean,
        var alpha: Int,
        val angle: Double,
        val maxDistance: Float,
        val centerX: Float,
        val centerY: Float
)