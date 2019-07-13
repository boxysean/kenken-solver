package main

import scala.io.Source
import scala.language.reflectiveCalls
import scala.collection.mutable.HashMap
import scala.util.matching.Regex

import main._


object Control {
  def using[A <: { def close(): Unit }, B](resource: A)(f: A => B): B =
    try {
      f(resource)
    } finally {
      resource.close()
    }
}

object KenKenSolver {
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

      return new GameState(constraints, board, initPossibilities, Array.ofDim[Boolean](board.length, board.length))
    }
  }

  def main(args: Array[String]): Unit = {
    var gameState = this.parseFile("kenken1.in")
    gameState.printConstraints
    println()
    gameState.printBoard
    println()
    println(gameState)
    println()
    println(gameState.solve)
  }
}
