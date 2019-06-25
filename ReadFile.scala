import scala.io.Source
import scala.language.reflectiveCalls
import scala.collection.immutable.HashMap


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
  val Multiplication = Value("*")
  val Division = Value("/")
  val Constant = Value(".")
}
import Operator._


// class Constraint {
//   var value: Int = 0
//   var operator: Operator.Val = null
//
//   def this(value: Int, operator: Operator.Val) {
//     this()
//     this.value = value
//     this.operator = operator
//   }
// }
// object Constraint {
//   def
// }


object ReadFile {
  // def parseFirstLine(tokens: Array[String]): Array[Operator.Val] = {
  //
  // }

  def main(args: Array[String]): Unit = {
    Control.using(Source.fromFile("kenken1.in")) { source =>
      var lines = source.getLines

      var firstLine = lines.next

      for (token <- firstLine.split(" ")) {
        var t = token.split("=")
        var cellChar = t(0).charAt(0)
        var operator = Operator.withName("" + cellChar)
        println(cellChar)
        println(operator)
      }

      for (line <- lines) {
          println(line)
      }
    }
  }
}
