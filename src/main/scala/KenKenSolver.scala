package main

import scala.io.Source
import scala.language.reflectiveCalls
import scala.collection.mutable.HashMap
import scala.util.matching.Regex

import main.Util


object Control {
  def using[A <: { def close(): Unit }, B](resource: A)(f: A => B): B =
    try {
      f(resource)
    } finally {
      resource.close()
    }
}


object Operator extends Enumeration {
  type Operator = Value
  val Addition = Value("+")
  val Subtraction = Value("-")
  val Multiplication = Value("x")
  val Division = Value("/")
  val Constant = Value("")
}
import Operator._


class Constraint {
  var value: Int = 0
  var operator: Operator = null
  var cellLocations: List[(Int, Int)] = null
  var possibleCombinations: List[List[Int]] = null

  def this(value: Int, operator: Operator, cellLocations: List[(Int, Int)], boardSize: Int) {
    this()
    this.value = value
    this.operator = operator
    this.cellLocations = cellLocations
    this.possibleCombinations = generatePossibilities(boardSize)
  }

  override def toString(): String =
    "" + value + "" + operator + " (size: " + this.constraintSize + ")"

  def constraintSize(): Int =
    this.cellLocations.length

  def generatePossibilities(boardSize: Int): List[List[Int]] =
    this.operator match {
      case Operator.Multiplication =>
        Util.multiplicationCombinations(this.constraintSize, boardSize, this.value)
      case Operator.Addition =>
        Util.additionCombinations(this.constraintSize, boardSize, this.value)
      case Operator.Subtraction =>
        Util.subtractionCombinations(boardSize, this.value)
      case Operator.Division =>
        Util.divisionCombinations(boardSize, this.value)
      case Operator.Constant =>
        List(List(this.value))
    }

  def possibleValuesOfCell(): List[Int] =
    this.possibleCombinations
      .flatten
      .distinct
}

class GameState {
  var constraints: HashMap[Char, Constraint] = null
  var board: Array[Array[Char]] = null
  var cellPossibilities: Array[Array[List[Int]]] = null

  def this(constraints: HashMap[Char, Constraint], board: Array[Array[Char]], cellPossibilities: Array[Array[List[Int]]]) {
    this()
    this.constraints = constraints
    this.board = board
    this.cellPossibilities = cellPossibilities
  }

  def boardSize: Int =
    this.board.length

  def constraintAt(row: Int, column: Int): Constraint =
    this.constraints(this.board(row)(column))

  def place(placeRow: Int, placeColumn: Int, value: Int) {
    this.cellPossibilities(placeRow)(placeColumn) = List(value)

    for (row <- 0 to this.boardSize-1) {
      if (row != placeRow) {
        this.cellPossibilities(row)(placeColumn) = this.cellPossibilities(row)(placeColumn).filter(_ != value)
      }
    }

    for (column <- 0 to this.boardSize-1) {
      if (column != placeColumn) {
        this.cellPossibilities(placeRow)(column) = this.cellPossibilities(placeRow)(column).filter(_ != value)
      }
    }
  }

  def isSolved(): Boolean =
    !this.cellPossibilities
      .flatten
      .filter(possibility => possibility.length != 1)
      .isEmpty

  def printConstraints() {
    for ((constraintChar, constraint) <- this.constraints) {
      println("" + constraintChar + "->" + constraint)
    }
  }

  def printBoard() {
    for (row <- board) {
      for (cell <- row) {
        print(cell)
        print(' ')
      }
      println()
    }
  }

  def printPlacements() {
    for (row <- 0 to board.length-1) {
      for (column <- 0 to board.length-1) {
        val cell = cellPossibilities(row)(column)
        if (cell.length > 1) {
          print(". ")
        } else {
          print(cell(0))
          print(' ')
        }
      }
      println()
    }
  }

  def printPossibilities() {
    for (row <- 0 to board.length-1) {
      for (column <- 0 to board.length-1) {
        print(cellPossibilities(row)(column))
        print(' ')
      }
      println()
    }
  }
}


object KenKenSolver {
  def constantPlacements(gameState: GameState): GameState = {
    for (row <- 0 to gameState.boardSize-1) {
      for (column <- 0 to gameState.boardSize-1) {
        val constraint = gameState.constraintAt(row, column)
        if (constraint.operator == Operator.Constant) {
          gameState.place(row, column, constraint.value)
        }
      }
    }

    return gameState
  }

  def getConstraintCellLocations(board: Array[Array[Char]], constraint: Char, row: Int, column: Int, visited: Array[Array[Boolean]]): List[(Int, Int)] = {
    var cellLocations = List((row, column))
    visited(row)(column) = true

    for ((dr, dc) <- List(-1, 0, 1, 0) zip List(0, 1, 0, -1)) {
      if (0 <= row + dr && row + dr < board.length
        && 0 <= column + dc && column + dc < board.length
        && board(row + dr)(column + dc) == constraint
        && !visited(row + dr)(column + dc)
      ) {
        cellLocations = cellLocations ++ getConstraintCellLocations(board, constraint, row + dr, column + dc, visited)
      }
    }

    return cellLocations
  }


  def parseFile(fileName: String): GameState = {
    Control.using(Source.fromFile(fileName)) { source =>
      var lines = source.getLines
      var firstLine = lines.next

      val board = lines.map(line =>
        line.split(" ").map(charStr =>
          charStr(0)
        )
      ).toArray

      val constraintCellLocations: HashMap[Char, List[(Int, Int)]] = HashMap[Char, List[(Int, Int)]]()

      for (row <- 0 to board.length-1) {
        for (column <- 0 to board.length-1) {
          if (!constraintCellLocations.contains(board(row)(column))) {
            constraintCellLocations += (
              board(row)(column) ->
              this.getConstraintCellLocations(board, board(row)(column), row, column, Array.ofDim[Boolean](board.length, board.length))
            )
          }
        }
      }

      val constraintBlobPattern: Regex = "([^=]*)=([0-9]+)([\\+\\-x/\\.]?)".r
      val constraints: HashMap[Char, Constraint] = HashMap[Char, Constraint]()

      for (token <- firstLine.split(" ")) {
        val constraintBlobPattern(constraintChar, constraintValue, constraintOperator) = token
        constraints += (
          constraintChar(0) ->
          new Constraint(constraintValue.toInt, Operator.withName(constraintOperator), constraintCellLocations(constraintChar(0)), board.length)
        )
      }

      val initPossibilities = Array.ofDim[List[Int]](board.length, board.length)

      for (row <- 0 to board.length-1) {
        for (column <- 0 to board.length-1) {
          val constraint = constraints(board(row)(column))
          initPossibilities(row)(column) = constraint.possibleValuesOfCell
        }
      }

      return new GameState(constraints, board, initPossibilities)
    }
  }

  def solve(gameState: GameState) {
    while (!gameState.isSolved) {

    }
    // While not solved...
    // 1. Try to place cells
    // 2. If 1 worked, return to 1
    // 3. Try to reduce cell possibilities based on row and column scans
    // 4. If 3 worked, return to 1
    // 5. Cycle through the constraints, trying to reduce the constraint possibilities, which in turn reduces cell possibilities
    // 6. If 5 worked, return to 1
    // 7. Try a constraint possibility
    // 8. If a contradiction is found, eliminate the constraint possibility, reduce cell possibilities, and return to 1
    // 9. If a solution is found, that's it!
  }

  def main(args: Array[String]): Unit = {
    var gameState = this.parseFile("kenken1.in")
    gameState.printConstraints
    gameState.printBoard
    gameState.printPlacements

    solve(gameState)
  }
}
