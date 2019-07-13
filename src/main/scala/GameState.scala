package main

import scala.io.Source
import scala.language.reflectiveCalls
import scala.collection.mutable.HashMap
import scala.util.matching.Regex

import main.Operator
import main.Constraint


class GameState (
  val constraints: HashMap[Char, GameConstraintState],
  val board: Array[Array[Char]],
  val cellPossibilities: Array[Array[Set[Int]]],
  val placed: Array[Array[Boolean]]
) {

  def boardSize: Int =
    this.board.length

  def constraintAt(row: Int, column: Int): Constraint =
    this.constraints(this.board(row)(column))

  override def clone(): GameState =
    new GameState(constraints, board.map(_.clone), cellPossibilities.map(_.clone), placed.map(_.clone))

  def place(placeRow: Int, placeColumn: Int): GameState = {
    val newGameState: GameState = this.clone

    assert(this.cellPossibilities(placeRow)(placeColumn).size == 1)

    val value = this.cellPossibilities(placeRow)(placeColumn)(0)

    for (row <- 0 to this.boardSize-1) {
      if (row != placeRow) {
        newGameState.cellPossibilities(row)(placeColumn) = this.cellPossibilities(row)(placeColumn).filter(_ != value)
      }
    }

    for (column <- 0 to this.boardSize-1) {
      if (column != placeColumn) {
        newGameState.cellPossibilities(placeRow)(column) = this.cellPossibilities(placeRow)(column).filter(_ != value)
      }
    }

    newGameState.placed(placeRow)(placeColumn) = true

    return newGameState
  }

  def isSolved(): Boolean =
    !this.cellPossibilities
      .flatten
      .filter(possibility => possibility.size != 1)
      .isEmpty

  def newCellPlacements(): Seq[(Int, Int)] =
    for {
      row <- 0 to this.boardSize-1
      column <- 0 to this.boardSize-1
      if this.cellPossibilities(row)(column).size == 1 && !this.placed(row)(column)
    } yield (row, column)

  def reducePossibilities(): GameState = {
    val newGameState: GameState = this.clone

    // Histogram of possibilities, for each cell in a row

    for (row <- 0 to newGameState.boardSize-1) {
      val histogram = new HashMap[Set[Int], Int]() { override def default(key: Set[Int]) = 0 }
      // val newCellPossibilities = new

      for (column <- 0 to newGameState.boardSize-1) {
        histogram(newGameState.cellPossibilities(row)(column)) += 1
      }

      var killEm = Vector[Set[Int]]()

      for ((possibilities, count) <- histogram) {
        if (possibilities.size == count) {
          killEm = killEm :+ possibilities
        }
      }

      for (killPossibilities <- killEm) {
        for (column <- 0 to this.boardSize-1) {
          if (newGameState.cellPossibilities(row)(column) != killPossibilities) {
            newGameState.cellPossibilities(row)(column) = newGameState.cellPossibilities(row)(column).filterNot(killPossibilities)
          }
        }
      }
    }

    return newGameState
  }

  def solve(): GameState = {
    if (this.isSolved) {
      return this
    }

    // While not solved...
    // 1. Try to place cells
    // 2. If 1 worked, return to 1

    val ncp = this.newCellPlacements

    if (ncp.length > 0) {
      return this.place(ncp(0)._1, ncp(0)._2)
    }

    return null

    // 3. Try to reduce cell possibilities based on row and column scans



    // 4. If 3 worked, return to 1
    // 5. Cycle through the constraints, trying to reduce the constraint possibilities, which in turn reduces cell possibilities
    // 6. If 5 worked, return to 1
    // 7. Try a constraint possibility
    // 8. If a contradiction is found, eliminate the constraint possibility, reduce cell possibilities, and return to 1
    // 9. If a solution is found, that's it!
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
    println(this.toString)
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

  override def toString =
    this.cellPossibilities.map(row =>
      row.map(possibilities => possibilities match {
        case List(x) => x.toString
        case _ => "."
      }).mkString(" ")
    ).mkString("\n")
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
