package com.lightricks

import com.typesafe.scalalogging.LazyLogging
import io.netty.handler.codec.http.{HttpHeaderNames, HttpHeaderValues}
import org.apache.flink.statefun.sdk.reqreply.generated.ToFunction
import zio._
import zhttp.http._
import zhttp.service.Server

import scala.util.{Failure, Success, Try}

object HelloWorld
  extends App
  with LazyLogging {
  private val app = Http.collect[Request] {
    case request: Request if request.method == Method.POST =>

      val requestBody = request.content match {
        case HttpData.CompleteData(data) => Success(data.toArray)
        case HttpData.StreamData(_)      => Failure(InvalidStreamingRequestException)
        case HttpData.Empty              => Failure(InvalidEmptyRequestException)
      }

      requestBody.flatMap(bytes => Try {
        ToFunction.parseFrom(bytes)
      }).flatMap(StateFunc.handle) match {
        case Failure(exception) =>
          logger.error("failed to process request", exception)

          Response.HttpResponse(
            status = Status.INTERNAL_SERVER_ERROR,
            headers = List(),
            content = HttpData.CompleteData(Chunk.empty)
          )
        case Success(value) => Response.http(
          status = Status.OK,
          headers = List(
            Header(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_OCTET_STREAM)
          ),
          content = HttpData.CompleteData(Chunk.fromArray(value.toByteArray))
        )

      }
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    Server
      .start(1108, app)
      .exitCode

  sealed trait InvalidRequestException extends Throwable
  object InvalidStreamingRequestException extends InvalidRequestException
  object InvalidEmptyRequestException extends InvalidRequestException

}
