import scala.collection.mutable.HashMap

import org.scalatest.FunSuite

import main.Operator
import main.Constraint
import main.GameState

class GameStateTest extends FunSuite {
  test("GameState.newCellPlacements") {
    val constraints = List(Constraint('a', 1, Operator.Constant))
    val board = Array(Array('a'))

    val gameState = GameState(constraints, board)

    assert(gameState.newCellPlacements === Seq((0, 0)))
  }

  test("GameState.reducePossibilities") {
    /*
      123
      312
      231
    */
    val constraints = List(
      Constraint('a', 3, Operator.Addition),
      Constraint('b', 6, Operator.Addition),
      Constraint('c', 9, Operator.Addition)
    )
    
    val board = Array(Array('a', 'a', 'b'), Array('c', 'c', 'b'), Array('c', 'c', 'b'))

    val gameState = GameState(constraints, board).reducePossibilities

    assert(gameState.cellPossibilities(2)(2) === Seq(3))
  }
}
