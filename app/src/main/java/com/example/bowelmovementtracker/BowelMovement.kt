package com.example.bowelmovementtracker

import java.util.Date

class BowelMovement(val timeOfMovement: Date) {//, val movementType, val movementSize) {

    fun getMovementTime() = timeOfMovement
}