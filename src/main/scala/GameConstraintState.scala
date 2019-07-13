package main

import scala.io.Source
import scala.language.reflectiveCalls
import scala.collection.mutable.HashMap
import scala.util.matching.Regex

import main.Operator._
import main.Constraint
import main.Util


class GameConstraintState(
  override val name: Char,
  override val value: Int,
  override val operator: Operator,
  val cellLocations: List[(Int, Int)],
  val possibleCombinations: List[List[Int]]
) extends Constraint(name, value, operator) {

  override def toString(): String =
    "" + value + "" + operator + " (size: " + this.constraintSize + ")"

  def constraintSize(): Int =
    this.cellLocations.length

  def possibleValuesOfCell(): List[Int] =
    this.possibleCombinations
      .flatten
      .distinct
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
