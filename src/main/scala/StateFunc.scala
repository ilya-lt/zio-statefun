package com.lightricks

import com.typesafe.scalalogging.LazyLogging
import org.apache.flink.statefun.sdk.reqreply.generated.FromFunction.InvocationResponse
import org.apache.flink.statefun.sdk.reqreply.generated.ToFunction.Invocation
import org.apache.flink.statefun.sdk.reqreply.generated.{FromFunction, ToFunction}

import scala.util.Try

object StateFunc extends LazyLogging {
  def handle(toFunction:ToFunction): Try[FromFunction] = Try {
    val responseBuilder = FromFunction.newBuilder()

    if(toFunction.hasInvocation){
      val invocationResult = InvocationResponse.newBuilder()
      val invocation = toFunction.getInvocation
      val target = invocation.getTarget

      if(target.getNamespace == "eventricks.fns" && target.getType == "subscriptions"){
        invocation.getInvocationsList
          .forEach(subscriptionsInvocation(invocationResult, target.getId) _)
      } else {
        throw UnknownFunction(target.getNamespace, target.getType)
      }

      responseBuilder.setInvocationResult(invocationResult.build())
    }

    responseBuilder.build()
  }

  def subscriptionsInvocation(builder:InvocationResponse.Builder, id:String)(invocation: Invocation): Unit = {
    logger.info("subscription:" + invocation.getArgument)
  }

  case class UnknownFunction(nameSpace:String, typeName:String)
    extends Exception(s"Unknown function $nameSpace/$typeName")
}
