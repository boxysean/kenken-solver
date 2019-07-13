package main

import scala.io.Source
import scala.language.reflectiveCalls
import scala.collection.mutable.HashMap
import scala.util.matching.Regex

import main.Operator._


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

object Constraint {
  def apply(
    value: Int,
    operator: Operator,
    cellLocations: List[(Int, Int)],
    boardSize: Int
  ): Constraint = new Constraint(value, operator, cellLocations, boardSize)
}
