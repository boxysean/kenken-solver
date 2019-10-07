package gg.kenken.aws

import scala.jdk.CollectionConverters._
import scala.beans.BeanProperty
import scala.collection.immutable.Map
import java.text
import java.util
import java.util.UUID.randomUUID

import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import spray.json._
import spray.json.DefaultJsonProtocol._

import gg.kenken.KenKenSolver

case class ApiGatewayResponse(
  @BeanProperty statusCode: Integer,
  @BeanProperty body: String,
  @BeanProperty headers: java.util.Map[String, Object],
  @BeanProperty base64Encoded: Boolean = false
)

class ApiGatewayHandler extends RequestHandler[util.Map[String, Any], ApiGatewayResponse] {
  def recordSubmission(boardInput: String, constraintString: String, boardOutput: String, error: String): Unit = {
    var record = new java.util.HashMap[String, AttributeValue]

    var tz = java.util.TimeZone.getTimeZone("UTC")
    var df = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
    df.setTimeZone(tz)

    record.put("SubmissionId", new AttributeValue(randomUUID().toString))
    record.put("SubmittedAt", new AttributeValue(df.format(new java.util.Date())))
    record.put("BoardInput", new AttributeValue(boardInput))
    record.put("ConstraintString", new AttributeValue(constraintString))

    if (boardOutput != null) {
      record.put("BoardOutput", new AttributeValue(boardOutput))
    }

    if (error != null) {
      record.put("Error", new AttributeValue(error))
    }

    var ddb = AmazonDynamoDBClientBuilder.defaultClient()
    ddb.putItem("KenkenSubmissions", record)
  }

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

    var corsHeaders: java.util.Map[String, Object] = Map(
      "Access-Control-Allow-Origin" -> "*".asInstanceOf[Object],
      "Access-Control-Allow-Credentials" -> "true".asInstanceOf[Object],
    ).asJava;

    val boardInput = boardStrings.mkString("\n")

    try {
      var boardOutput = KenKenSolver.solveFromAPI(boardStrings.iterator, constraintString)

      this.recordSubmission(boardInput, constraintString, boardOutput, null)

      return ApiGatewayResponse(
        200,
        Map(
          "constraintString" -> constraintString,
          "boardInput" -> boardInput,
          "boardOutput" -> boardOutput,
        ).toJson.toString,
        corsHeaders,
        true
      )
    } catch {
      case e: Throwable => {
        this.recordSubmission(boardInput, constraintString, null, e.toString)

        return ApiGatewayResponse(
          500,
          Map(
            "error" -> e.toString,
            "constraintString" -> constraintString,
            "boardInput" -> boardInput,
          ).toJson.toString,
          corsHeaders,
          true
        )
      }
    }
  }
}
