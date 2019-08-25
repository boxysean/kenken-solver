package com.solvemykenken

class KenKenSolverException(
  message: String = "",
  cause: Throwable = None.orNull
) extends Exception(message, cause)
