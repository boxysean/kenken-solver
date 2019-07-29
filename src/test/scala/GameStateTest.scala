import scala.collection.mutable.HashMap

import org.scalatest.FunSuite

import main.Operator
import main.Constraint
import main.GameState

class GameStateTest extends FunSuite {
  // test("GameState.newCellPlacements") {
  //   val gameState = GameState(
  //     List(Constraint('a', 1, Operator.Constant)),
  //     Array(Array('a'))
  //   )
  //
  //   assert(gameState.newCellPlacements === Seq((0, 0)))
  // }

  test("GameState.reduceCellPossibilities") {
    /*
      123 aab
      312 cdb
      231 cdb
    */
    val gameState = GameState(List(
      Constraint('a', 3, Operator.Addition),
      Constraint('b', 6, Operator.Addition),
      Constraint('c', 5, Operator.Addition),
      Constraint('d', 4, Operator.Addition)
    ), Array(
      Array('a', 'a', 'b'),
      Array('c', 'd', 'b'),
      Array('c', 'd', 'b')
    )).reduceCellPossibilities

    assert(gameState.cellPossibilities(0)(0) === Set(1))
    assert(gameState.cellPossibilities(0)(1) === Set(2))
    assert(gameState.cellPossibilities(0)(2) === Set(3))
    assert(gameState.cellPossibilities(1)(1) === Set(1, 3))
    assert(gameState.cellPossibilities(2)(1) === Set(1, 3))
  }

  test("GameState.cellsWithoutCertainty") {
    /*
      123 aab
      312 cdb
      231 cdb
    */
    val gameState = GameState(List(
      Constraint('a', 3, Operator.Addition),
      Constraint('b', 6, Operator.Addition),
      Constraint('c', 5, Operator.Addition),
      Constraint('d', 4, Operator.Addition)
    ), Array(
      Array('a', 'a', 'b'),
      Array('c', 'd', 'b'),
      Array('c', 'd', 'b')
    )).reduceCellPossibilities

    assert(gameState.cellsWithoutCertainty == List(
      (1, 0), (1, 1), (1, 2), (2, 0), (2, 1), (2, 2)
    ))
  }

  test("GameState.tryIt") {
    /*
      123 aab  also...  123
      312 cdb           231
      231 cdb           312
    */
    val gameState = GameState(List(
      Constraint('a', 3, Operator.Addition),
      Constraint('b', 6, Operator.Addition),
      Constraint('c', 5, Operator.Addition),
      Constraint('d', 4, Operator.Addition)
    ), Array(
      Array('a', 'a', 'b'),
      Array('c', 'd', 'b'),
      Array('c', 'd', 'b')
    ))
      .reduceCellPossibilities
      .tryIt
      .reduceCellPossibilities
      .reduceCellPossibilities

    assert(gameState.cellPossibilities(0)(0) === Set(1))
    assert(gameState.cellPossibilities(0)(1) === Set(2))
    assert(gameState.cellPossibilities(0)(2) === Set(3))
    assert(gameState.cellPossibilities(1)(0) === Set(2))
    assert(gameState.cellPossibilities(1)(1) === Set(3))
    assert(gameState.cellPossibilities(1)(2) === Set(1))
    assert(gameState.cellPossibilities(2)(0) === Set(3))
    assert(gameState.cellPossibilities(2)(1) === Set(1))
    assert(gameState.cellPossibilities(2)(2) === Set(2))
  }
}
