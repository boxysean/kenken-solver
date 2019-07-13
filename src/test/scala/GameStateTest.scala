import scala.collection.mutable.HashMap

import org.scalatest.FunSuite

import main.Operator
import main.Constraint
import main.GameState

class GameStateTest extends FunSuite {
  test("GameState.newCellPlacements") {
    val gameState = GameState(
      List(Constraint('a', 1, Operator.Constant)),
      Array(Array('a'))
    )

    assert(gameState.newCellPlacements === Seq((0, 0)))
  }

  test("GameState.reducePossibilities") {
    /*
      123
      312
      231
    */
    val gameState = GameState(List(
      Constraint('a', 3, Operator.Addition),
      Constraint('b', 6, Operator.Addition),
      Constraint('c', 9, Operator.Addition)
    ), Array(
      Array('a', 'a', 'b'),
      Array('c', 'c', 'b'),
      Array('c', 'c', 'b')
    )).reducePossibilities

    assert(gameState.cellPossibilities(0)(0) === Set(1, 2))
    assert(gameState.cellPossibilities(0)(1) === Set(1, 2))
    assert(gameState.cellPossibilities(0)(2) === Set(3))
  }
}
