package com.example.servicemate.util

object ServiceIntervals {
    data class Interval(val miles: Int, val days: Int)

    private val intervals = mapOf(
        "Oil Change" to Interval(5000, 180),
        "Tire Rotation" to Interval(6000, 180),
        "Brake Service" to Interval(12000, 365),
        "Battery Replacement" to Interval(24000, 730),
        "Air Filter" to Interval(12000, 365),
        "General Inspection" to Interval(6000, 180)
    )

    private val default = Interval(5000, 180)

    fun get(serviceType: String): Interval = intervals[serviceType] ?: default
}