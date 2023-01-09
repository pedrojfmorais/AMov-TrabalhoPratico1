package pt.isec.amov.a2018020733.trabalhopratico1.models

import java.io.Serializable


class GameBoard(level: Int) : Serializable {

    companion object {

        private fun operatorFromString(stringOperator: String): (Double, Double) -> Double {
            return when (stringOperator) {
                "+" -> { a, b -> a + b }
                "-" -> { a, b -> a - b }
                "/" -> { a, b -> a / b }
                "x" -> { a, b -> a * b }
                else -> throw Exception("That's not a supported operator")
            }
        }

        fun calculateValueOperation(operation: Array<String>): Double {

            var result: Double
            val mathPriority = ArrayList<String>()
            var flagSkip = false

            for (i in operation.indices) {
                if (operation[i] == "x" || operation[i] == "/") {
                    mathPriority[mathPriority.size - 1] = operatorFromString(operation[i])
                        .invoke(
                            mathPriority[mathPriority.size - 1].toDouble(),
                            operation[i + 1].toDouble()
                        ).toString()
                    flagSkip = true
                } else {
                    if (!flagSkip)
                        mathPriority.add(operation[i])
                    flagSkip = false
                }
            }

            result = mathPriority[0].toDouble()
            for (i in mathPriority.indices)
                if (mathPriority[i] == "+" || mathPriority[i] == "-")
                    result = operatorFromString(mathPriority[i])
                        .invoke(
                            result,
                            mathPriority[i + 1].toDouble()
                        )

            return result
        }
    }

    var gameBoard = ArrayList<ArrayList<String>>()
    val level: Int

    var maxValue: Double = 0.0
    var maxValueFound: Boolean = false
    var maxValuePosition: Pair<Int, Int> // direção, indice

    var secondMaxValue: Double = 0.0
    var secondMaxValueFound: Boolean = false
    var secondMaxValuePosition: Pair<Int, Int>

    init {

        for (i in 1..SIZE_GAME_BOARD) {
            val linha = ArrayList<String>()
            for (j in 1..SIZE_GAME_BOARD)
                linha.add("")
            gameBoard.add(linha)
        }

        this.level = level

        val maxNumber = if (level < 4)
            MAX_NUMBER_USED_IN_EQUATIONS_START[0]
        else if (level < 8)
            MAX_NUMBER_USED_IN_EQUATIONS_START[1]
        else
            MAX_NUMBER_USED_IN_EQUATIONS_START[2]

        val operators = if (level < 4)
            OPERATORS_USED_PER_LEVEL[(level % 4) - 1]
        else
            OPERATORS_USED_PER_LEVEL[3]

        for (i in gameBoard.indices)
            for (j in gameBoard[i].indices) {
                if (i % 2 == 0 && j % 2 == 0)
                    gameBoard[i][j] = (1..maxNumber).random().toString()
                else if ((i + j) % 2 == 1)
                    gameBoard[i][j] = operators[(operators.indices).random()]
                else
                    gameBoard[i][j] = " "
            }
        //Calcula linhas
        maxValuePosition = Pair(0, 0)
        secondMaxValuePosition = Pair(0, 0)
        for (i in gameBoard.indices) {
            if (i % 2 == 0) {
                val temp = calculateValueOperation(gameBoard[i].toTypedArray())

                if (temp > secondMaxValue) {
                    secondMaxValue = temp
                    secondMaxValuePosition = Pair(DIRECTION_HORIZONTAL, i)
                }

                if (temp > maxValue) {
                    secondMaxValue = maxValue
                    maxValue = temp

                    secondMaxValuePosition = Pair(maxValuePosition.first, maxValuePosition.second)
                    maxValuePosition = Pair(DIRECTION_HORIZONTAL, i)
                }
            }
        }

        //Calcula Colunas
        val column = ArrayList<String>()

        for (j in gameBoard[0].indices) {
            column.clear()
            for (i in gameBoard.indices) {
                if (j % 2 == 0) {
                    column.add(gameBoard[i][j])
                }
            }
            if (j % 2 == 0) {
                val temp = calculateValueOperation(column.toTypedArray())

                if (temp > secondMaxValue) {
                    secondMaxValue = temp
                    secondMaxValuePosition = Pair(DIRECTION_VERTICAL, j)
                }

                if (temp > maxValue) {
                    secondMaxValue = maxValue
                    maxValue = temp

                    secondMaxValuePosition = Pair(maxValuePosition.first, maxValuePosition.second)
                    maxValuePosition = Pair(DIRECTION_VERTICAL, j)
                }
            }
        }
    }

    fun pointsForResult(resultadoConta: Double): Int {
        if (resultadoConta == maxValue && !maxValueFound) {
            maxValueFound = true
            return 2
        }
        if (resultadoConta == secondMaxValue && !secondMaxValueFound) {
            secondMaxValueFound = true
            return 1
        }
        return 0
    }
}