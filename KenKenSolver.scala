import scala.io.Source
import scala.language.reflectiveCalls
import scala.collection.mutable.HashMap
import scala.util.matching.Regex


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


abstract class CombineState

case class MultiplyCombineState(factors: List[Int], remainingValues: Int, maxValue: Int, currentMultiplier: Int) extends CombineState

class Constraint {
  var value: Int = 0
  var operator: Operator = null
  var constraintSize: Int = 0

  def this(value: Int, operator: Operator, constraintSize: Int) {
    this()
    this.value = value
    this.operator = operator
    this.constraintSize = constraintSize
  }

  override def toString(): String =
    "" + value + "" + operator + " (size: " + this.constraintSize + ")"

  def generatePossibilities(boardSize: Int): List[List[Int]] =
    this.operator match {
      case Operator.Multiplication =>
        this.combine(MultiplyCombineState(this.factorize(this.value), this.constraintSize, boardSize, 1))
    }

  def factorize(x: Int): List[Int] =
    x match {
      case 1 => List()
      case y if (y % 2) == 0 => 2 +: this.factorize(x / 2)
      case y if (y % 3) == 0 => 3 +: this.factorize(x / 3)
      case y if (y % 5) == 0 => 5 +: this.factorize(x / 5)
      case y if (y % 7) == 0 => 7 +: this.factorize(x / 7)
    }

  def combine(state: CombineState): List[List[Int]] =
    state match {
      case MultiplyCombineState(_, _, maxValue, currentMultiplier) if currentMultiplier > maxValue => {
        // println("HERE1 " + state)
        return Nil
      }
      case MultiplyCombineState(List(), remainingValues, _, currentMultiplier) if remainingValues >= 1 => {
        // println("HERE2 " + state)
        return List(List(currentMultiplier) ++ List.fill(remainingValues-1)(1))
      }
      // case MultiplyCombineState(List(), remainingValues, _, 1) if remainingValues > 1 => {
      //   return List(List.fill(remainingValues)(1))
      // }
      case MultiplyCombineState(factor :: remainingFactors, remainingValues, maxValue, currentMultiplier) => {
        println(state)
        return this.combine(MultiplyCombineState(remainingFactors, remainingValues-1, maxValue, factor)).map(combination => currentMultiplier +: combination) ++ this.combine(MultiplyCombineState(remainingFactors, remainingValues, maxValue, currentMultiplier * factor)).map(combination => combination)
      }
      case _ => {
        // println("HERE3 " + state)
        return Nil
      }
    }
}

class GameState {
  var constraints: HashMap[Char, Constraint] = null
  var board: Array[Array[Char]] = null
  var possibilities: Array[Array[List[Int]]] = null

  def this(constraints: HashMap[Char, Constraint], board: Array[Array[Char]], possibilities: Array[Array[List[Int]]]) {
    this()
    this.constraints = constraints
    this.board = board
    this.possibilities = possibilities
  }

  def boardSize: Int =
    this.board.length

  def constraintAt(row: Int, column: Int): Constraint =
    this.constraints(this.board(row)(column))

  def place(placeRow: Int, placeColumn: Int, value: Int) {
    this.possibilities(placeRow)(placeColumn) = List(value)

    for (row <- 0 to this.boardSize-1) {
      if (row != placeRow) {
        this.possibilities(row)(placeColumn) = this.possibilities(row)(placeColumn).filter(_ != value)
      }
    }

    for (column <- 0 to this.boardSize-1) {
      if (column != placeColumn) {
        this.possibilities(placeRow)(column) = this.possibilities(placeRow)(column).filter(_ != value)
      }
    }
  }

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
    for (row <- possibilities) {
      for (cell <- row) {
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
    for (row <- possibilities) {
      for (cell <- row) {
        print(cell)
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

  def getConstraintSize(board: Array[Array[Char]], constraint: Char, row: Int, column: Int, visited: Array[Array[Boolean]]): Int = {
    var size = 1
    visited(row)(column) = true

    for ((dr, dc) <- List(-1, 0, 1, 0) zip List(0, 1, 0, -1)) {
      if (0 <= row + dr && row + dr < board.length
        && 0 <= column + dc && column + dc < board.length
        && board(row + dr)(column + dc) == constraint
        && !visited(row + dr)(column + dc)
      ) {
        size += getConstraintSize(board, constraint, row + dr, column + dc, visited)
      }
    }

    return size
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

      val constraintSizes: HashMap[Char, Int] = HashMap[Char, Int]()

      for (row <- 0 to board.length-1) {
        for (column <- 0 to board.length-1) {
          if (!constraintSizes.contains(board(row)(column))) {
            constraintSizes += (
              board(row)(column) ->
              this.getConstraintSize(board, board(row)(column), row, column, Array.ofDim[Boolean](board.length, board.length))
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
          new Constraint(constraintValue.toInt, Operator.withName(constraintOperator), constraintSizes(constraintChar(0)))
        )
      }

      val initPossibilities = Array.ofDim[List[Int]](board.length, board.length)

      for (row <- 0 to board.length-1) {
        for (column <- 0 to board.length-1) {
          initPossibilities(row)(column) = (1 to board.length).toList
        }
      }

      return this.constantPlacements(new GameState(constraints, board, initPossibilities))
    }
  }

  def solve(gameState: GameState) {
    println(gameState.constraints('a').factorize(6))
    println(gameState.constraints('e').generatePossibilities(gameState.boardSize))
  }

  def main(args: Array[String]): Unit = {
    var gameState = this.parseFile("kenken1.in")
    gameState.printConstraints
    gameState.printBoard
    gameState.printPlacements

    solve(gameState)
  }
}
