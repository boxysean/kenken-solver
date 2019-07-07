package main

abstract class CombineState

case class MultiplyCombineState(factors: List[Int], remainingValues: Int, maxValue: Int, currentMultiplier: Int) extends CombineState

object Util {
  def factorize(x: Int): List[Int] =
    x match {
      case 1 => List()
      case y if (y % 2) == 0 => 2 +: Util.factorize(x / 2)
      case y if (y % 3) == 0 => 3 +: Util.factorize(x / 3)
      case y if (y % 5) == 0 => 5 +: Util.factorize(x / 5)
      case y if (y % 7) == 0 => 7 +: Util.factorize(x / 7)
    }

  def combine(state: CombineState): List[List[Int]] =
    state match {
      case MultiplyCombineState(_, _, maxValue, currentMultiplier) if currentMultiplier > maxValue => {
        // println("HERE1 " + state)
        return Nil
      }
      case MultiplyCombineState(List(), remainingValues, _, currentMultiplier) if remainingValues >= 1 => {
        // println("HERE2 " + state)
        return List(List(currentMultiplier) ++ List.fill(remainingValues-1)(1))
      }
      // case MultiplyCombineState(List(), remainingValues, _, 1) if remainingValues > 1 => {
      //   return List(List.fill(remainingValues)(1))
      // }
      case MultiplyCombineState(factor :: remainingFactors, remainingValues, maxValue, 1) => {
        println(state)
        return Util.combine(MultiplyCombineState(remainingFactors, remainingValues, maxValue, factor)).map(combination => combination)
      }
      case MultiplyCombineState(factor :: remainingFactors, remainingValues, maxValue, currentMultiplier) => {
        println(state)
        return Util.combine(MultiplyCombineState(remainingFactors, remainingValues-1, maxValue, factor)).map(combination => currentMultiplier +: combination) ++ Util.combine(MultiplyCombineState(remainingFactors, remainingValues, maxValue, currentMultiplier * factor)).map(combination => combination)
      }
      case _ => {
        // println("HERE3 " + state)
        return Nil
      }
    }
}
