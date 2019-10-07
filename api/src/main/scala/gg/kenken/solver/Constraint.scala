package gg.kenken.solver

import scala.io.Source
import scala.language.reflectiveCalls
import scala.collection.mutable.HashMap
import scala.util.matching.Regex
import scala.util.control.Breaks._

import gg.kenken.solver.Operator._
import gg.kenken.solver.Util


class Constraint (
  val name: Char,
  val value: Int,
  val operator: Operator
) {

  override def toString(): String =
    "" + name + "=" + value + "" + operator

  def getConstraintCellLocations(board: Array[Array[Char]], row: Int, column: Int, visited: Array[Array[Boolean]]): List[(Int, Int)] = {
    var cellLocations = List((row, column))
    visited(row)(column) = true

    for ((dr, dc) <- List(-1, 0, 1, 0) zip List(0, 1, 0, -1)) {
      if (0 <= row + dr && row + dr < board.length
        && 0 <= column + dc && column + dc < board.length
        && board(row + dr)(column + dc) == this.name
        && !visited(row + dr)(column + dc)
      ) {
        cellLocations = cellLocations ++ getConstraintCellLocations(board, row + dr, column + dc, visited)
      }
    }

    return cellLocations
  }

  def getConstraintCellLocations(board: Array[Array[Char]], row: Int, column: Int): List[(Int, Int)] =
    return getConstraintCellLocations(board, row, column, Array.ofDim[Boolean](board.length, board.length))

  def getGameConstraintState(board: Array[Array[Char]]): GameConstraintState = {
    var cellLocations: List[(Int, Int)] = List[(Int, Int)]()

    breakable {
      for (row <- 0 to board.length-1) {
        for (column <- 0 to board.length-1) {
          if (board(row)(column) == this.name) {
            cellLocations = this.getConstraintCellLocations(board, row, column)
            break
          }
        }
      }
    }

    return GameConstraintState(this.name, this.value, this.operator, cellLocations, Util.generatePossibilities(this, cellLocations.length, board.length))
  }
}

object Constraint {
  def apply(
    name: Char,
    value: Int,
    operator: Operator
  ): Constraint = new Constraint(name, value, operator)
}
