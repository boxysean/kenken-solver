import org.scalatest.FunSuite

class UtilTest extends FunSuite {
    test("Util.factorize") {
      assert(Util.factorize(3) === List(3))
      assert(Util.factorize(6) === List(2, 3))
      assert(Util.factorize(27) === List(3, 3, 3))
      assert(Util.factorize(45) === List(3, 3, 5))
    }
}
