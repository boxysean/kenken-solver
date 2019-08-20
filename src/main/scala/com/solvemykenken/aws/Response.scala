package com.solvemykenken.aws

import scala.beans.BeanProperty

case class Response(@BeanProperty message: String, @BeanProperty request: Request)
