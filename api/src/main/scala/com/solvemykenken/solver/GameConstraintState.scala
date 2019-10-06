package com.solvemykenken.solver

import scala.io.Source
import scala.language.reflectiveCalls
import scala.collection.mutable.HashMap
import scala.util.matching.Regex

import com.solvemykenken.solver.Operator._
import com.solvemykenken.solver.Constraint
import com.solvemykenken.solver.Util


class GameConstraintState(
  override val name: Char,
  override val value: Int,
  override val operator: Operator,
  val cellLocations: List[(Int, Int)],
  val possibleCombinations: List[List[Int]]
) extends Constraint(name, value, operator) {

  override def toString(): String =
    "" + name + "=" + value + "" + operator + " (size: " + this.constraintSize + ")"

  def constraintSize(): Int =
    this.cellLocations.length

  def possibleValuesOfCell(): Set[Int] =
    this.possibleCombinations
      .flatten
      .distinct
      .toSet

  def isFullyPlaced(gameState: GameState): Boolean =
    this.cellLocations.map(x => x match {
      case (row, column) => (gameState.cellPossibilities(row)(column).size == 1)
    }).forall(_ == true)

  def placementIsLegal(gameState: GameState): Boolean = {
    val placement: List[Int] = this.cellLocations.map(x => x match {
      case (row, column) => gameState.cellPossibilities(row)(column).head
    }).sorted
    val r = this.possibleCombinations.contains(placement)
    return r
  }
}

object GameConstraintState {
  def apply(
    name: Char,
    value: Int,
    operator: Operator,
    cellLocations: List[(Int, Int)],
    possibleCombinations: List[List[Int]]
  ): GameConstraintState = {
    return new GameConstraintState(name, value, operator, cellLocations, possibleCombinations)
  }
}
