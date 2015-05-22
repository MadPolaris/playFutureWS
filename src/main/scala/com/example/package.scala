package com

import com.example.ChainedFutureSamples.SampleData
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.ws.DefaultWSClientConfig
import play.api.libs.ws.ning.NingAsyncHttpClientConfigBuilder

/**
 * Created by Barry on 5/22/15.
 */
package object example {
  implicit val readFormJson = (
    (__ \ "id").read[String] ~
      (__ \ "emailAddress").readNullable[String]
    )(SampleData.apply _)

  implicit val writeToJson = (
    (__ \ "id").write[String] ~
      (__ \ "emailAddress").writeNullable[String]
    )(
      unlift(SampleData.unapply)
    )

  val clientConfig = new DefaultWSClientConfig()
  val secureDefaults: com.ning.http.client.AsyncHttpClientConfig = new NingAsyncHttpClientConfigBuilder(clientConfig).build()
  val builder = new com.ning.http.client.AsyncHttpClientConfig.Builder(secureDefaults)
  builder.setCompressionEnabled(true)
  val secureDefaultsWithSpecificOptions: com.ning.http.client.AsyncHttpClientConfig = builder.build()
  implicit val implicitClient = new play.api.libs.ws.ning.NingWSClient(secureDefaultsWithSpecificOptions)

}
