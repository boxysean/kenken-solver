package main

import scala.io.Source
import scala.language.reflectiveCalls
import scala.collection.mutable.ArrayBuffer
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

  // def place(placeRow: Int, placeColumn: Int): GameState = {
  //   val newGameState: GameState = this.clone
  //
  //   assert(this.cellPossibilities(placeRow)(placeColumn).size == 1)
  //
  //   val value: Int = this.cellPossibilities(placeRow)(placeColumn).head  // First element
  //
  //   for (row <- 0 to this.boardSize-1) {
  //     if (row != placeRow) {
  //       newGameState.cellPossibilities(row)(placeColumn) = this.cellPossibilities(row)(placeColumn).filter(_ != value)
  //     }
  //   }
  //
  //   for (column <- 0 to this.boardSize-1) {
  //     if (column != placeColumn) {
  //       newGameState.cellPossibilities(placeRow)(column) = this.cellPossibilities(placeRow)(column).filter(_ != value)
  //     }
  //   }
  //
  //   newGameState.placed(placeRow)(placeColumn) = true
  //
  //   return newGameState
  // }
  //
  def isSolved(): Boolean =
    this.cellPossibilities
      .flatten
      .filter(possibility => possibility.size != 1)
      .isEmpty

  def isImpossible(): Boolean =
    !this.cellPossibilities
      .flatten
      .filter(possibility => possibility.size == 0)
      .isEmpty

  // def newCellPlacements(): Seq[(Int, Int)] =
  //   for {
  //     row <- 0 to this.boardSize-1
  //     column <- 0 to this.boardSize-1
  //     if this.cellPossibilities(row)(column).size == 1 && !this.placed(row)(column)
  //   } yield (row, column)

  def reduceCellPossibilities(): GameState = {
    println("REDUCING ( ｀皿´)｡ﾐ/")

    val newGameState: GameState = this.clone
    var modified = false

    for (row <- 0 to newGameState.boardSize-1) {
      val histogram = new HashMap[Set[Int], ArrayBuffer[(Int, Int)]]()

      for (column <- 0 to newGameState.boardSize-1) {
        if (!histogram.contains(newGameState.cellPossibilities(row)(column))) {
          histogram(newGameState.cellPossibilities(row)(column)) = new ArrayBuffer()
        }
        histogram(newGameState.cellPossibilities(row)(column)) += ((row, column))

        // if (histogram(newGameState.cellPossibilities(row)(column)).size > newGameState.cellPossibilities(row)(column).size) {
        //   println("CONTRADICTION")
        //   newGameState.printPossibilities
        //   return null
        // }
      }

      for ((possibilities, cells) <- histogram if possibilities.size == cells.size) {
        // println("YEP ROW", possibilities, cells)
        for (column <- 0 to newGameState.boardSize-1) {
          // println("checking...", row, column)
          if (!cells.contains((row, column))) {
            // println("yaya?...", row, column, newGameState.cellPossibilities(row)(column))
            // println(newGameState.cellPossibilities(row)(column).intersect(possibilities))
            modified = modified || (newGameState.cellPossibilities(row)(column).intersect(possibilities).size > 0)
            newGameState.cellPossibilities(row)(column) = newGameState.cellPossibilities(row)(column).filterNot(possibilities)
            // println(newGameState.cellPossibilities(row)(column))
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

        // if (histogram(newGameState.cellPossibilities(row)(column)).size > newGameState.cellPossibilities(row)(column).size) {
        //   println("CONTRADICTION")
        //   newGameState.printPossibilities
        //   return null
        // }
      }

      for ((possibilities, cells) <- histogram if possibilities.size == cells.size) {
        // println("YEP COLUMN", possibilities, cells)
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

  def reduceConstraintPossibilities(): GameState = {
    val newGameState: GameState = this.clone
    var modified = false

    // for (constraint <- newGameState.constraints.values) {
    //   var cellPossibilities = Set[Int]()
    //   for ((cellRow, cellColumn) <- constraint.cellLocations) {
    //     cellPossibilities = cellPossibilities.union(newGameState.cellPossibilities)
    //
    //   }
    // }

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
    println("PICKING ¯\\_( ͡° ͜ʖ ͡°)_/¯", row, column, value)
    newGameState.cellPossibilities(row)(column) = Set(value)
    return newGameState
  }

  def definitelyNotThis(row: Int, column: Int, value: Int): GameState = {
    val newGameState: GameState = this.clone
    println("DEFINITELY NOT ヽ(｀Д´)ﾉ", row, column, value)
    newGameState.cellPossibilities(row)(column) = newGameState.cellPossibilities(row)(column).filterNot(_ == value)
    return newGameState
  }

  def solve(): GameState = {
    println("Solve cycle \\_(-_-)_/")

    if (this.isSolved) {
      return this
    }

    if (this.isImpossible) {
      return null
    }

    // While not solved...

    // 1. Try to reduce cell possibilities based on row and column scans
    val reduce = this.reduceCellPossibilities

    if (reduce != null) {
      // 2. If 3 worked, return to 1
      return reduce.solve
    }

    // 5. Cycle through the constraints, trying to reduce the constraint possibilities, which in turn reduces cell possibilities
    // 6. If 5 worked, return to 1

    val reduce2 = this.reduceConstraintPossibilities

    if (reduce2 != null) {
      return reduce2.solve
    }

    // 7. Try a constraint possibility
    // 8. If a contradiction is found, eliminate the constraint possibility, reduce cell possibilities, and return to 1
    // 9. If a solution is found, that's it!

    val (row, column, value) = this.pickSomething

    val tryItBoard = this.tryIt(row, column, value).solve

    if (tryItBoard != null) {
      return tryItBoard
    } else {
      return this.definitelyNotThis(row, column, value).solve
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
    println(this.toString)
  }

  def cellPossibilityToString(possibility: Set[Int]): String =
    (1 to this.boardSize).map(x => x match {
      case x if possibility.contains(x) => x
      case _ => " "
    }).mkString("")

  def printPossibilities() {
    for (row <- 0 to board.length-1) {
      for (column <- 0 to board.length-1) {
        print(this.cellPossibilityToString(cellPossibilities(row)(column)))
        print("|")
      }
      println()
    }
  }

  override def toString =
    this.cellPossibilities.map(row =>
      row.map(possibilities => possibilities.toList match {
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
