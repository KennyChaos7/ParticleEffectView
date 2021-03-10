package com.example.particleeffect

data class ParticlePoint(
        var x: Float,
        var y: Float,
        var distance: Float,
        var state: Int,
        val angle: Double,
        val maxDistance: Float,
        val centerX: Float,
        val centerY: Float
)