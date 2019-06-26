import scala.io.Source
import scala.language.reflectiveCalls
import scala.collection.mutable.HashMap
import scala.util.matching.Regex


object Control {
    def using[A <: { def close(): Unit }, B](resource: A)(f: A => B): B =
        try {
            f(resource)
        } finally {
            resource.close()
        }
}


object Operator extends Enumeration {
  // protected case class Val(operatorChar: Character) extends super.Val
  // type Operator = Value
  type Operator = Value
  val Addition = Value("+")
  val Subtraction = Value("-")
  val Multiplication = Value("x")
  val Division = Value("/")
  val Constant = Value(".")
}
import Operator._


class Constraint {
  var value: Int = 0
  var operator: Operator = null

  def this(value: Int, operator: Operator) {
    this()
    this.value = value
    this.operator = operator
  }
}
import Constraint._


object ReadFile {
  // def parseFirstLine(tokens: Array[String]): Array[Operator.Val] = {
  //
  // }

  def main(args: Array[String]): Unit = {
    Control.using(Source.fromFile("kenken1.in")) { source =>
      var lines = source.getLines

      var firstLine = lines.next

      val constraintBlobPattern: Regex = "([^=]*)=([0-9]+)([\\+\\-x/\\.])".r
      val constraintMap: HashMap[Character, Constraint] = HashMap[Character, Constraint]()

      for (token <- firstLine.split(" ")) {
        val constraintBlobPattern(constraintChar, constraintValue, constraintOperator) = token
        constraintMap += (constraintChar -> Constraint(constraintValue, constraintOperator))
      }


      println(constraintMap)
      // for (line <- lines) {
      //     println(line)
      // }
    }
  }
}
