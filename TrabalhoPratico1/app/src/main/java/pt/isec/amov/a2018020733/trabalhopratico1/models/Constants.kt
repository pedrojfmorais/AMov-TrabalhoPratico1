package pt.isec.amov.a2018020733.trabalhopratico1.models

const val COLLECTION_PATH = "TopScoresSingleplayer"
const val COLLECTION_FIELD_POINTS = "points"
const val COLLECTION_FIELD_TIME_PLAYED = "timePlayed"

const val DIRECTION_HORIZONTAL = 1
const val DIRECTION_VERTICAL = 2

const val SIZE_GAME_BOARD = 5
const val NUMBER_EQUATIONS_LEVEL = 5

const val TRANSITION_TIME_SECONDS = 5

const val STARTING_TIME_SECONDS = 60
const val DECREASE_TIME_PER_LEVEL = 10
const val MINIMUM_TIME_LEVEL = 10
const val ADDED_TIME_CORRECT_ANSWER = 5
const val REMOVED_TIME_WRONG_ANSWER = 5

val MAX_NUMBER_USED_IN_EQUATIONS_START = arrayOf(9,99,999)

val OPERATORS_USED_PER_LEVEL = arrayOf(arrayOf("+"), arrayOf("+","-"),
    arrayOf("+","-","x"), arrayOf("+","-","x","/"))