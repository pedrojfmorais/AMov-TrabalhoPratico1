package pt.isec.amov.a2018020733.trabalhopratico1


class GameBoard(level: Int) {

    companion object {

        private fun operatorFromChar(charOperator: Char): (Double, Double) -> Double {
            return when (charOperator) {
                '+' -> { a, b -> a + b }
                '-' -> { a, b -> a - b }
                '/' -> { a, b -> a / b }
                'x' -> { a, b -> a * b }
                else -> throw Exception("That's not a supported operator")
            }
        }

        fun calculateValueOperation(operation: Array<Char>): Double {

            var result: Double
            val mathPriority = ArrayList<String>()
            var flagSkip = false

            for (i in operation.indices) {
                if (operation[i] == 'x' || operation[i] == '/') {
                    mathPriority[mathPriority.size - 1] = operatorFromChar(operation[i])
                        .invoke(
                            mathPriority[mathPriority.size - 1].toDouble(),
                            operation[i + 1].digitToInt().toDouble()
                        ).toString()
                    flagSkip = true;
                } else {
                    if (!flagSkip)
                        mathPriority.add(operation[i].toString())
                    flagSkip = false
                }
            }

            result = mathPriority[0].toDouble()
            for (i in mathPriority.indices)
                if (mathPriority[i] == "+" || mathPriority[i] == "-")
                    result = operatorFromChar(mathPriority[i][0])
                        .invoke(
                            result,
                            mathPriority[i + 1].toDouble()
                        )

            return result
        }
    }

    var gameBoard = Array(SIZE_GAME_BOARD) { CharArray(SIZE_GAME_BOARD) }
    val level: Int

    //TODO:
    var maxValue: Double = 0.0
    var secondMaxValue: Double = 0.0

    init {
        this.level = level

        val maxNumber = if (level < 4)
            MAX_NUMBER_USED_IN_EQUATIONS_START[0]
        else if (level < 8)
            MAX_NUMBER_USED_IN_EQUATIONS_START[1]
        else
            MAX_NUMBER_USED_IN_EQUATIONS_START[2]

        val operators = if (level < 16)
            OPERATORS_USED_PER_LEVEL[(level % 4) - 1]
        else
            OPERATORS_USED_PER_LEVEL[3]

        for (i in gameBoard.indices)
            for (j in gameBoard[i].indices) {
                if (i % 2 == 0 && j % 2 == 0)
                    gameBoard[i][j] = Character.forDigit((1..maxNumber).random(), 10)
                else if ((i + j) % 2 == 1)
                    gameBoard[i][j] = operators[(operators.indices).random()]
                else
                    gameBoard[i][j] = ' '
            }

        //Calcula linhas
        for (i in gameBoard.indices) {
            if (i % 2 == 0) {
                val temp = calculateValueOperation(gameBoard[i].toTypedArray())
                if (temp > maxValue) {
                    secondMaxValue = maxValue
                    maxValue = temp
                }
            }

        }

        //Calcula Colunas
        val column = ArrayList<Char>()

        for (j in gameBoard[0].indices) {
            column.clear()
            for (i in gameBoard.indices) {
                if (j % 2 == 0) {
                    column.add(gameBoard[i][j])
                }
            }
            if (j % 2 == 0) {
                val temp = calculateValueOperation(column.toTypedArray())
                if (temp > maxValue) {
                    secondMaxValue = maxValue
                    maxValue = temp
                }
            }
        }
    }

    fun pointForResult(resultadoConta: Double) : Int{
        if(resultadoConta == maxValue)
            return 2
        if(resultadoConta == secondMaxValue)
            return 1
        return 0
    }
}