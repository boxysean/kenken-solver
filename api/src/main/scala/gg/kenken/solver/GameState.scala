package gg.kenken.solver

import scala.io.Source
import scala.language.reflectiveCalls
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap
import scala.util.matching.Regex

import com.typesafe.scalalogging.LazyLogging

import gg.kenken.solver.Operator
import gg.kenken.solver.Constraint


class GameState (
  val constraints: HashMap[Char, GameConstraintState],
  val board: Array[Array[Char]],
  val cellPossibilities: Array[Array[Set[Int]]],
  val placed: Array[Array[Boolean]]
) extends LazyLogging {

  def boardSize: Int =
    this.board.length

  def constraintAt(row: Int, column: Int): Constraint =
    this.constraints(this.board(row)(column))

  override def clone(): GameState =
    new GameState(constraints, board.map(_.clone), cellPossibilities.map(_.clone), placed.map(_.clone))

  def isSolved(): Boolean =
    this.cellPossibilities
      .flatten
      .filter(possibility => possibility.size != 1)
      .isEmpty

  def allCellsHaveAChoice(): Boolean =
    this.cellPossibilities
      .flatten
      .filter(possibility => possibility.size == 0)
      .isEmpty

  def allPlacementsAreLegal(): Boolean =
    this.constraints
      .values
      .filter(_.isFullyPlaced(this))
      .forall(_.placementIsLegal(this))

  def isPossible(): Boolean =
    this.allCellsHaveAChoice && this.allPlacementsAreLegal

  def reduceCellPossibilities(): GameState = {
    logger.debug("REDUCING ( ｀皿´)｡ﾐ/")

    val newGameState: GameState = this.clone
    var modified = false

    // For each row, count how many similar sets of possibilities there are.
    // If there are the same number of sets as the number of elements in the set
    // then go ahead and remove those elements from the other cells in the row.

    for (row <- 0 to newGameState.boardSize-1) {
      val histogram = new HashMap[Set[Int], ArrayBuffer[(Int, Int)]]()

      for (column <- 0 to newGameState.boardSize-1) {
        if (!histogram.contains(newGameState.cellPossibilities(row)(column))) {
          histogram(newGameState.cellPossibilities(row)(column)) = new ArrayBuffer()
        }
        histogram(newGameState.cellPossibilities(row)(column)) += ((row, column))
      }

      for ((possibilities, cells) <- histogram if possibilities.size == cells.size) {
        for (column <- 0 to newGameState.boardSize-1) {
          if (!cells.contains((row, column))) {
            modified = modified || (newGameState.cellPossibilities(row)(column).intersect(possibilities).size > 0)
            newGameState.cellPossibilities(row)(column) = newGameState.cellPossibilities(row)(column).filterNot(possibilities)
          }
        }
      }
    }

    for (column <- 0 to newGameState.boardSize-1) {
      val histogram = new HashMap[Set[Int], ArrayBuffer[(Int, Int)]]()

      for (row <- 0 to newGameState.boardSize-1) {
        if (!histogram.contains(newGameState.cellPossibilities(row)(column))) {
          histogram(newGameState.cellPossibilities(row)(column)) = new ArrayBuffer()
        }
        histogram(newGameState.cellPossibilities(row)(column)) += ((row, column))
      }

      for ((possibilities, cells) <- histogram if possibilities.size == cells.size) {
        for (row <- 0 to newGameState.boardSize-1) {
          if (!cells.contains((row, column))) {
            modified = modified || (newGameState.cellPossibilities(row)(column).intersect(possibilities).size > 0)
            newGameState.cellPossibilities(row)(column) = newGameState.cellPossibilities(row)(column).filterNot(possibilities)
          }
        }
      }
    }

    if (modified) {
      return newGameState
    } else {
      return null
    }
  }

  def cellsWithoutCertainty(): Seq[(Int, Int)] =
    for {
      row <- 0 to this.boardSize-1
      column <- 0 to this.boardSize-1
      if this.cellPossibilities(row)(column).size > 1
    } yield (row, column)

  def pickSomething(): (Int, Int, Int) = {
    val picked: (Int, Int) = cellsWithoutCertainty.head
    return (picked._1, picked._2, this.cellPossibilities(picked._1)(picked._2).head)
  }

  def tryIt(row: Int, column: Int, value: Int): GameState = {
    val newGameState: GameState = this.clone
    logger.debug("PICKING ¯\\_( ͡° ͜ʖ ͡°)_/¯ {} {} {}", row, column, value)
    newGameState.cellPossibilities(row)(column) = Set(value)
    return newGameState
  }

  def definitelyNotThis(row: Int, column: Int, value: Int): GameState = {
    val newGameState: GameState = this.clone
    logger.debug("DEFINITELY NOT ヽ(｀Д´)ﾉ {} {} {}", row, column, value)
    newGameState.cellPossibilities(row)(column) = newGameState.cellPossibilities(row)(column).filterNot(_ == value)
    return newGameState
  }

  def solve(): GameState = {
    logger.debug("Solve cycle \\_(-_-)_/")

    logger.debug("{}", this)
    logger.debug("{}", this.possibilitiesToString)

    if (!this.isPossible) {
      logger.debug("NOT POSSIBLE (̿▀̿ ̿Ĺ̯̿̿▀̿ ̿)̄ {} {}", this.allCellsHaveAChoice, this.allPlacementsAreLegal)
      return null
    }

    if (this.isSolved) {
      return this
    }

    // - Try to reduce cell possibilities based on row and column scans

    val reduce = this.reduceCellPossibilities

    if (reduce != null) {
      return reduce.solve
    }

    // - Try a constraint possibility
    // - If a contradiction is found, eliminate the constraint possibility, reduce cell possibilities, and return to 1
    // - If a solution is found, that's it!

    val (row, column, value) = this.pickSomething

    val tryItBoard = this.tryIt(row, column, value).solve

    if (tryItBoard != null) {
      return tryItBoard
    } else {
      return this.definitelyNotThis(row, column, value).solve
    }
  }

  def constraintsToString(): String = {
    var stringBuilder = new StringBuilder

    for ((constraintChar, constraint) <- this.constraints) {
      stringBuilder ++= "" + constraintChar + "->" + constraint
    }

    return stringBuilder.toString()
  }

  def boardToString(): String = {
    var stringBuilder = new StringBuilder

    for (row <- board) {
      for (cell <- row) {
        stringBuilder += cell
        stringBuilder += ' '
      }
      stringBuilder += '\n'
    }

    return stringBuilder.toString()
  }

  def placementsToString(): String = {
    return this.toString
  }

  def cellPossibilityToString(possibility: Set[Int]): String =
    (1 to this.boardSize).map(x => x match {
      case x if possibility.contains(x) => x
      case _ => " "
    }).mkString("")

  def possibilitiesToString(): String = {
    val stringBuilder = new StringBuilder

    for (row <- 0 to board.length-1) {
      for (column <- 0 to board.length-1) {
        stringBuilder ++= this.cellPossibilityToString(cellPossibilities(row)(column))
        stringBuilder += '|'
      }
      stringBuilder += '\n'
    }

    return stringBuilder.toString()
  }

  override def toString =
    this.cellPossibilities.map(row =>
      row.map(possibilities => possibilities.toList match {
        case List(x) => x.toString
        case _ => "."
      }).mkString(" ")
    ).mkString("\n")

  def toJson(): Array[Array[String]] =
    this.cellPossibilities.map(row =>
      row.map(possibilities => possibilities.toList match {
        case List(x) => x.toString
        case _ => "."
      })
    )
}

object GameState {
  def apply(
    constraints: HashMap[Char, GameConstraintState],
    board: Array[Array[Char]],
    cellPossibilities: Array[Array[Set[Int]]],
    placed: Array[Array[Boolean]]
  ): GameState = new GameState(constraints, board, cellPossibilities, placed)

  def apply(
    constraints: List[Constraint],
    board: Array[Array[Char]]
  ): GameState = {
    val initPossibilities = Array.ofDim[Set[Int]](board.length, board.length)
    val gameConstraintStates = HashMap[Char, GameConstraintState]()

    for (constraint <- constraints) {
      gameConstraintStates += (constraint.name -> constraint.getGameConstraintState(board))
    }

    for (row <- 0 to board.length-1) {
      for (column <- 0 to board.length-1) {
        val constraint = gameConstraintStates(board(row)(column))
        initPossibilities(row)(column) = constraint.possibleValuesOfCell
      }
    }

    return new GameState(gameConstraintStates, board, initPossibilities, Array.ofDim[Boolean](board.length, board.length))
  }
}
