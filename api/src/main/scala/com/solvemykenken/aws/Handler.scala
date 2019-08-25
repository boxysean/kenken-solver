package com.solvemykenken.aws

import scala.jdk.CollectionConverters._
import scala.beans.BeanProperty
import scala.collection.immutable.Map
import java.util

import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import spray.json._
import spray.json.DefaultJsonProtocol._

import com.solvemykenken.KenKenSolver

case class ApiGatewayResponse(
  @BeanProperty statusCode: Integer,
  @BeanProperty body: String,
  @BeanProperty headers: java.util.Map[String, Object],
  @BeanProperty base64Encoded: Boolean = false
)

class ApiGatewayHandler extends RequestHandler[util.Map[String, Any], ApiGatewayResponse] {
  def handleRequest(input: util.Map[String, Any], context: Context): ApiGatewayResponse = {
    val bodyJson = input.get("body").asInstanceOf[String].parseJson

    var boardStrings = bodyJson.asJsObject.getFields("boardStrings") match {
      case Seq(JsArray(boardStrings)) => boardStrings.map(_.convertTo[String])
      case _ => throw new Exception("parser error")
    }

    var constraintString = bodyJson.asJsObject.getFields("constraintString") match {
      case Seq(JsString(constraintString)) => constraintString
      case _ => throw new Exception("parser error")
    }

    try {
      var board = KenKenSolver.solveFromAPI(boardStrings.iterator, constraintString)
      var boardOutput = board.map(row => row.mkString(""))

      ApiGatewayResponse(
        200,
        Map(
          "constraintString" -> constraintString,
          "boardInput" -> boardStrings.mkString("\n"),
          "boardOutput" -> boardOutput.mkString("\n"),
        ).toJson.toString,
        null,
        true
      )
    } catch {
      case e: Throwable =>
        ApiGatewayResponse(
          500,
          Map(
            "error" -> e.toString,
            "constraintString" -> constraintString,
            "boardInput" -> boardStrings.mkString("\n"),
          ).toJson.toString,
          null,
          true
        )
    }
  }
}
