package com.solvemykenken.aws

import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import scala.jdk.CollectionConverters._

class Handler extends RequestHandler[Request, Response] {

  def handleRequest(input: Request, context: Context): Response = {
    Response("Go Serverless v1.0! Your function executed successfully!", input)
  }
}

class ApiGatewayHandler extends RequestHandler[Request, ApiGatewayResponse] {

  def handleRequest(input: Request, context: Context): ApiGatewayResponse = {
    val headers = Map[String, Object]("x-custom-response-header" -> "my custom response header value").asJava
    ApiGatewayResponse(200, "Go Serverless v1.0! Your function executed successfully!",
      headers,
      true)
  }
}
