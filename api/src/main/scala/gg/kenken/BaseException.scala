package gg.kenken

class KenKenSolverException(
  message: String = "",
  cause: Throwable = None.orNull
) extends Exception(message, cause)
