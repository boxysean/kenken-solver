package main


object Util {
  def factorize(x: Int): List[Int] =
    x match {
      case 1 => List()
      case y if (y % 2) == 0 => 2 +: Util.factorize(x / 2)
      case y if (y % 3) == 0 => 3 +: Util.factorize(x / 3)
      case y if (y % 5) == 0 => 5 +: Util.factorize(x / 5)
      case y if (y % 7) == 0 => 7 +: Util.factorize(x / 7)
    }

  def listCompare(listA: List[Int], listB: List[Int]): Boolean =
    (listA, listB) match {
      case (List(), _) => true
      case (_, List()) => false
      case (listA, listB) if listA(0) != listB(0) => listA(0) < listB(0)
      case (listA, listB) if listA(0) == listB(0) => listCompare(listA.drop(1), listB.drop(1))
    }

  def notPossible(combination: List[Int]): Boolean =
    combination.length >= 2 && combination.toSet.size == 1 // All values of a combination are the same

  def multiplicationCombinations(remainingValues: Int, maxValue: Int, targetValue: Int): List[List[Int]] =
    multiplicationCombinations(factorize(targetValue), remainingValues, maxValue, 1)
      .map(combination => combination.sorted)
      .sortWith((listA, listB) => listCompare(listA, listB))
      .distinct
      .filterNot(combination => notPossible(combination))

  def multiplicationCombinations(factors: List[Int], remainingValues: Int, maxValue: Int, currentMultiplier: Int): List[List[Int]] =
    (factors, remainingValues, maxValue, currentMultiplier) match {
      case (List(), remainingValues, _, currentMultiplier) if remainingValues >= 1 && currentMultiplier <= maxValue =>
        List(List(currentMultiplier) ++ List.fill(remainingValues-1)(1))
      case (factor :: remainingFactors, remainingValues, maxValue, 1) =>
        Util.multiplicationCombinations(remainingFactors, remainingValues, maxValue, factor)
          .map(combination => combination)
      case (factor :: remainingFactors, remainingValues, maxValue, currentMultiplier) if currentMultiplier <= maxValue =>
        Util.multiplicationCombinations(remainingFactors, remainingValues-1, maxValue, factor)
          .map(combination => currentMultiplier +: combination) ++ Util.multiplicationCombinations(remainingFactors, remainingValues, maxValue, currentMultiplier * factor)
          .map(combination => combination)
      case _ =>
        Nil
    }

  def additionCombinations(remainingValues: Int, maxValue: Int, targetValue: Int): List[List[Int]] =
    additionCombinations(remainingValues, maxValue, targetValue, 1)
      .filterNot(combination => notPossible(combination))

  def additionCombinations(remainingValues: Int, maxValue: Int, targetValue: Int, highestValue: Int): List[List[Int]] =
    (remainingValues, maxValue, targetValue, highestValue) match {
      case (1, maxValue, targetValue, highestValue) if targetValue <= maxValue && targetValue >= highestValue =>
        List(List(targetValue))
      case (remainingValues, maxValue, targetValue, highestValue) if remainingValues > 1 && highestValue <= maxValue && targetValue > 0 =>
        return Util.additionCombinations(remainingValues-1, maxValue, targetValue-highestValue, highestValue)
          .map(combination => highestValue +: combination) ++ Util.additionCombinations(remainingValues, maxValue, targetValue, highestValue+1)
          .map(combination => combination)
      case _ =>
        Nil
    }

  def subtractionCombinations(maxValue: Int, targetValue: Int): List[List[Int]] =
    (1 until maxValue + 1)
      .filter(lowerValue => targetValue + lowerValue <= maxValue)
      .map(lowerValue => List(lowerValue, targetValue + lowerValue))
      .filterNot(combination => notPossible(combination))
      .toList

  def divisionCombinations(maxValue: Int, targetValue: Int): List[List[Int]] =
    (1 until maxValue + 1)
      .filter(value => (value / targetValue) * targetValue == value)
      .map(value => List(value, value / targetValue).sorted)
      .filterNot(combination => notPossible(combination))
      .toList

  def generatePossibilities(constraint: Constraint, constraintSize: Int, boardSize: Int): List[List[Int]] =
    constraint.operator match {
      case Operator.Multiplication =>
        Util.multiplicationCombinations(constraintSize, boardSize, constraint.value)
      case Operator.Addition =>
        Util.additionCombinations(constraintSize, boardSize, constraint.value)
      case Operator.Subtraction =>
        Util.subtractionCombinations(boardSize, constraint.value)
      case Operator.Division =>
        Util.divisionCombinations(boardSize, constraint.value)
      case Operator.Constant =>
        List(List(constraint.value))
    }
}
