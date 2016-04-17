package com.quotify.subs

import com.sksamuel.elastic4s.{ElasticsearchClientUri, ElasticClient}
import com.typesafe.config.ConfigFactory

import scala.util.Try

/**
  * Created by Aleksandrov Vladimir on 03/04/16.
  */
object Elastic {

  def client(): Try[ElasticClient] = {
    Try {
      val config = ConfigFactory.load()
      val ip = config.getString("elasticsearch.ip")
      val port = config.getInt("elasticsearch.port")

      val uri = ElasticsearchClientUri(s"elasticsearch://$ip:$port")
      ElasticClient.transport(uri)
    }
  }
}
