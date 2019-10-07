package gg.kenken.solver

object Operator extends Enumeration {
  type Operator = Value
  val Addition = Value("+")
  val Subtraction = Value("-")
  val Multiplication = Value("x")
  val Division = Value("/")
  val Constant = Value("")
}
