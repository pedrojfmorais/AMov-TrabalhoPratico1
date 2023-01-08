package pt.isec.amov.a2018020733.trabalhopratico1.models

class Game {

    private var gameLevels: ArrayList<List<GameBoard>> = ArrayList()
    private var currentLevel: Int = 1
    private var currentEquation: Int = 0
    private var points: Int = 0
    private var timePlayedSeconds: Int = 0
    private var timeLeftLevel: Int = STARTING_TIME_SECONDS

    init {

        val equations: ArrayList<GameBoard> = ArrayList()

        for (i in 1..NUMBER_EQUATIONS_LEVEL)
            equations.add(GameBoard(currentLevel))

        gameLevels.add(equations)
    }

    fun nextEquation() {
        if (currentEquation < NUMBER_EQUATIONS_LEVEL - 1)
            currentEquation++
    }

    fun previousEquation() {
        if (currentEquation > 0)
            currentEquation--
    }

    fun nextLevel() {

        timeLeftLevel = STARTING_TIME_SECONDS - (DECREASE_TIME_PER_LEVEL * currentLevel)
        if (timeLeftLevel < MINIMUM_TIME_LEVEL)
            timeLeftLevel = MINIMUM_TIME_LEVEL

        currentLevel++
        currentEquation = 0

        val equations: ArrayList<GameBoard> = ArrayList()

        for (i in 1..NUMBER_EQUATIONS_LEVEL)
            equations.add(GameBoard(currentLevel))

        gameLevels.add(equations)

    }

    fun verifyEquationSelected(direction: Int, indice: Int) {

        val currentEquationBoard = gameLevels[currentLevel - 1][currentEquation]
        var selectedEquation = ArrayList<String>()


        if (direction == DIRECTION_VERTICAL) {
            for (i in 1..SIZE_GAME_BOARD) {
                selectedEquation.add(currentEquationBoard.gameBoard[i - 1][indice])
            }
        } else
            selectedEquation = currentEquationBoard.gameBoard[indice]

        val pointsReceived = currentEquationBoard.pointsForResult(
            GameBoard.calculateValueOperation(selectedEquation.toTypedArray())
        )

        if(pointsReceived == 0)
            wrongAnswer()
        else
            correctAnswer()

        points += pointsReceived
    }

    fun getCurrentBoard(): GameBoard{
        return gameLevels[currentLevel - 1][currentEquation]
    }

    fun getPoints(): Int {
        return points
    }

    fun getTimeLeftLevel(): Int {
        return timeLeftLevel
    }

    fun decrementTimeLeft() {
        timeLeftLevel--
    }

    fun wrongAnswer() {
        timeLeftLevel -= REMOVED_TIME_WRONG_ANSWER
    }

    fun correctAnswer() {
        timeLeftLevel += ADDED_TIME_CORRECT_ANSWER
    }

    fun getCurrentEquationNumber(): Int {
        return currentEquation + 1
    }

    fun getCurrentLevel(): Int {
        return currentLevel
    }

    fun incrementTimePlayed(){
        timePlayedSeconds++
    }

    fun getTimePlayed(): Int {
        return timePlayedSeconds
    }
}
