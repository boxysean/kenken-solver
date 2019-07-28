import org.scalatest.FunSuite

import main.Util

class UtilTest extends FunSuite {
  test("Util.factorize") {
    assert(Util.factorize(3) === List(3))
    assert(Util.factorize(6) === List(2, 3))
    assert(Util.factorize(27) === List(3, 3, 3))
    assert(Util.factorize(45) === List(3, 3, 5))
  }

  test("Util.multiplicationCombinations") {
    assert(Util.multiplicationCombinations(2, 6, 6) === List(List(1, 6), List(2, 3)))
    assert(Util.multiplicationCombinations(3, 6, 6) === List(List(1, 1, 6), List(1, 2, 3)))
    assert(Util.multiplicationCombinations(3, 6, 60) === List(List(2, 5, 6), List(3, 4, 5)))
    assert(Util.multiplicationCombinations(3, 6, 45) === List(List(3, 3, 5)))
    assert(Util.multiplicationCombinations(3, 6, 40) === List(List(2, 4, 5)))
    assert(Util.multiplicationCombinations(3, 6, 72) === List(List(3, 4, 6)))
    assert(Util.multiplicationCombinations(3, 6, 4) === List(List(1, 1, 4), List(1, 2, 2)))
    assert(Util.multiplicationCombinations(3, 6, 12) === List(List(1, 2, 6), List(1, 3, 4), List(2, 2, 3)))
  }

  test("Util.additionCombinations") {
    assert(Util.additionCombinations(2, 5, 4) === List(List(1, 3)))
    assert(Util.additionCombinations(2, 5, 5) === List(List(1, 4), List(2, 3)))
    assert(Util.additionCombinations(3, 6, 4) === List(List(1, 1, 2)))
    assert(Util.additionCombinations(3, 6, 16) === List(List(4, 6, 6), List(5, 5, 6)))
    assert(Util.additionCombinations(3, 6, 11) === List(List(1, 4, 6), List(1, 5, 5), List(2, 3, 6), List(2, 4, 5), List(3, 3, 5), List(3, 4, 4)))
    assert(Util.additionCombinations(3, 6, 12) === List(List(1, 5, 6), List(2, 4, 6), List(2, 5, 5), List(3, 3, 6), List(3, 4, 5)))
    assert(Util.additionCombinations(2, 6, 8) === List(List(2, 6), List(3, 5)))
  }

  test("Util.subtractionCombinations") {
    assert(Util.subtractionCombinations(5, 1) === List(List(1, 2), List(2, 3), List(3, 4), List(4, 5)))
    assert(Util.subtractionCombinations(5, 2) === List(List(1, 3), List(2, 4), List(3, 5)))
    assert(Util.subtractionCombinations(5, 3) === List(List(1, 4), List(2, 5)))
    assert(Util.subtractionCombinations(5, 4) === List(List(1, 5)))
    assert(Util.subtractionCombinations(5, 5) === List())
  }

  test("Util.divisionCombinations") {
    assert(Util.divisionCombinations(6, 2) === List(List(1, 2), List(2, 4), List(3, 6)))
    assert(Util.divisionCombinations(6, 3) === List(List(1, 3), List(2, 6)))
    assert(Util.divisionCombinations(6, 4) === List(List(1, 4)))
    assert(Util.divisionCombinations(6, 5) === List(List(1, 5)))
    assert(Util.divisionCombinations(6, 6) === List(List(1, 6)))
  }

  test("Util.combinationIsPossible") {
    assert(Util.combinationIsPossible(List(1, 2), List(Set(1, 2), Set(1, 2))))
    assert(Util.combinationIsPossible(List(1, 2), List(Set(2), Set(1))))
    assert(Util.combinationIsPossible(List(2, 1, 3), List(Set(1), Set(3), Set(1, 2))))
    assert(Util.combinationIsPossible(List(1, 2), List(Set(1, 2), Set(1, 3))))
    assert(!Util.combinationIsPossible(List(1, 3), List(Set(1, 2), Set(1, 2))))
  }
}
