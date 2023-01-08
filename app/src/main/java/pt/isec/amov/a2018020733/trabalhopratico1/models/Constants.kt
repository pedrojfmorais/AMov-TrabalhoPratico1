package pt.isec.amov.a2018020733.trabalhopratico1.models

const val SIZE_GAME_BOARD = 5

const val STARTING_TIME_SECONDS = 60
const val DECREASE_TIME_PER_LEVEL = 10
const val MINIMUM_TIME_LEVEL = 10
const val ADDED_TIME_CORRECT_ANSWER = 5

val MAX_NUMBER_USED_IN_EQUATIONS_START = arrayOf(9,99,999)
const val INCREASE_NUMBER_USED_IN_EQUATIONS_PER_LEVEL = 5

val OPERATORS_USED_PER_LEVEL = arrayOf(arrayOf('+'), arrayOf('+','-'),
    arrayOf('+','-','x'), arrayOf('+','-','x','/'))

