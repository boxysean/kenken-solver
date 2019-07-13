import scala.collection.mutable.HashMap

import org.scalatest.FunSuite

import main.Operator
import main.Constraint
import main.GameState

class GameStateTest extends FunSuite {
  test("GameState.newCellPlacements") {
    val constraints = HashMap('a' -> Constraint(1, Operator.Constant, List((0, 0)), 1))
    val board = Array(Array('a'))
    val cellPossibilities = Array(Array(List(1)))
    val placed = Array(Array(false))

    val gameState = GameState(constraints, board, cellPossibilities, placed)

    assert(gameState.newCellPlacements === Seq((0, 0)))
  }
}
