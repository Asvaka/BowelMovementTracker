package com.example.bowelmovementtracker

import java.util.Date

class DayInfo {
    private var totalNumOfMovements = 0

    private val movementsList = mutableListOf<BowelMovement>()

    fun addMovement(time: Date) {
        totalNumOfMovements++
        val newMovement = BowelMovement(time)
        movementsList.add(newMovement)
    }
    
    fun getMovementsList() = movementsList as List<BowelMovement>

    fun getNumOfMovements() = totalNumOfMovements
}