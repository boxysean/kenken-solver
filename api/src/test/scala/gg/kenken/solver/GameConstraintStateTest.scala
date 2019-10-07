import scala.collection.mutable.HashMap

import org.scalatest.FunSuite

import gg.kenken.solver.Operator
import gg.kenken.solver.Constraint
import gg.kenken.solver.GameState

class GameConstraintStateTest extends FunSuite {
  test("GameConstraintState.placementIsLegal") {
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

    assert(gameState.constraints('a').placementIsLegal(gameState))
  }
}
