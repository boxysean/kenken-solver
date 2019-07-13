package main

import scala.io.Source
import scala.language.reflectiveCalls
import scala.collection.mutable.HashMap
import scala.util.matching.Regex

import main.Operator
import main.Constraint


class GameState {
  var constraints: HashMap[Char, Constraint] = null
  var board: Array[Array[Char]] = null
  var cellPossibilities: Array[Array[List[Int]]] = null
  var placed: Array[Array[Boolean]] = null

  def this(constraints: HashMap[Char, Constraint], board: Array[Array[Char]], cellPossibilities: Array[Array[List[Int]]], placed: Array[Array[Boolean]]) {
    this()
    this.constraints = constraints
    this.board = board
    this.cellPossibilities = cellPossibilities
    this.placed = placed
  }

  def boardSize: Int =
    this.board.length

  def constraintAt(row: Int, column: Int): Constraint =
    this.constraints(this.board(row)(column))

  override def clone(): GameState =
    new GameState(constraints, board.map(_.clone), cellPossibilities.map(_.clone), placed.map(_.clone))

  def place(placeRow: Int, placeColumn: Int): GameState = {
    val newGameState: GameState = this.clone

    assert(this.cellPossibilities(placeRow)(placeColumn).length == 0)

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
      .filter(possibility => possibility.length != 1)
      .isEmpty

  def newCellPlacements(): Seq[(Int, Int)] =
    for {
      row <- 0 to this.boardSize-1
      column <- 0 to this.boardSize-1
      if this.cellPossibilities(row)(column).length == 1 && !this.placed(row)(column)
    } yield (row, column)

  def solve(): GameState = {
    if (this.isSolved) {
      return this
    }

    // While not solved...
    // 1. Try to place cells
    // 2. If 1 worked, return to 1

    val ncp = newCellPlacements()

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

object GameState {
  def apply(
    constraints: HashMap[Char, Constraint],
    board: Array[Array[Char]],
    cellPossibilities: Array[Array[List[Int]]],
    placed: Array[Array[Boolean]]
  ): GameState = new GameState(constraints, board, cellPossibilities, placed)
}
