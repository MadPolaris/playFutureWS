package com.example

import play.api.libs.json.Json
import play.api.libs.ws.WS

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ChainedFutureSamples {

  type UnappliedFuture[T] = () => Future[T]

  abstract class Sample

  case class SampleData(id: String, email: Option[String]) extends Sample {
    println(toString)
  }

  case class Empty(url: String) extends Sample {
    println(toString)
  }

  object SampleData {
    implicit val sampleDataFormat = Json.format[SampleData]
  }

  def main(args: Array[String]): Unit = 1 to 100 map toUrl map toUnappliedFuture reduce toChainedUnappliedFuture apply

  private def toUrl: Int => String = (i: Int) => s"http://www.baidu.com/${i}"

  private def toUnappliedFuture(url: String): UnappliedFuture[SampleData] = () => Future(SampleData(url, None))

  private def toChainedUnappliedFuture: (UnappliedFuture[SampleData], UnappliedFuture[SampleData]) => UnappliedFuture[SampleData] = (a1, a2) => () => {
    a1 apply() flatMap { _ => a2 apply } recoverWith { case _ => a2 apply }
  }

  private def packageFuture(url: String): () => Future[Sample] = () => WS.clientUrl(url)
    .withQueryString(("key", ""))
    .withRequestTimeout(1000)
    .get().map(response => response.json.validate[SampleData].getOrElse(Empty(url)))


  def toChainUppliedFutureWithFold : (UnappliedFuture[SampleData], UnappliedFuture[SampleData]) => UnappliedFuture[SampleData] = ???

  def retry[T](maximizeRetries: Int, future: UnappliedFuture[T]): Future[T] = ???

}
