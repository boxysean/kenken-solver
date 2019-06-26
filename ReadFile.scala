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
  val Constant = Value("")
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

  override def toString(): String =
    "" + value + "" + operator
}

class GameState {
  var constraints: HashMap[Char, Constraint] = null
  var board: List[List[String]] = null

  def this(constraints: HashMap[Char, Constraint], board: List[List[String]]) {
    this()
    this.constraints = constraints
    this.board = board
  }

  def boardSize: Int =
    this.board.length

  def printConstraints() {
    for ((constraintChar, constraint) <- this.constraints) {
      println("" + constraintChar + "->" + constraint)
    }
  }

  def printBoard() {
    for (row <- board) {
      for (cell <- row) {
        print(cell)
        print(' ')
      }
      println()
    }
  }
}


object ReadFile {
  // def parseFirstLine(tokens: Array[String]): Array[Operator.Val] = {
  //
  // }

  def main(args: Array[String]): Unit = {
    Control.using(Source.fromFile("kenken1.in")) { source =>
      var lines = source.getLines

      var firstLine = lines.next

      val constraintBlobPattern: Regex = "([^=]*)=([0-9]+)([\\+\\-x/\\.]?)".r
      val constraints: HashMap[Char, Constraint] = HashMap[Char, Constraint]()

      for (token <- firstLine.split(" ")) {
        val constraintBlobPattern(constraintChar, constraintValue, constraintOperator) = token
        constraints += (constraintChar(0) -> new Constraint(constraintValue.toInt, Operator.withName(constraintOperator)))
      }

      val board = lines.map(line => line.split(" ").toList).toList

      val game = new GameState(constraints, board)

      game.printConstraints
      game.printBoard
    }
  }
}
