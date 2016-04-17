package com.quotify.subs.elastic

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType.StringType
import com.sksamuel.elastic4s.{ElasticClient, ElasticsearchClientUri}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success}

/**
  * Created by Aleksandrov Vladimir on 03/04/16.
  */
object Elastic {

  val logger = LoggerFactory.getLogger(this.getClass)

  val INDEX_NAME = "movies"
  val TYPE_NAME = "subtitleType"

  lazy val client = createClient()

  private def createClient(): ElasticClient = {
    val config = ConfigFactory.load()
    val host = config.getString("elasticsearch.host")
    val port = config.getInt("elasticsearch.port")

    val uri = ElasticsearchClientUri(host, port)
    ElasticClient.transport(uri)
  }

  def createMoviesIndexIfNotExist() = {

    import scala.concurrent.ExecutionContext.Implicits._

    val indexExist = client.execute(index exists INDEX_NAME)

    indexExist onComplete {
      case Success(res) => if (!res.isExists) createMovieIndex()
      case Failure(e) => throw e
    }
  }

  private def createMovieIndex() = {
    logger.debug("Creating index")

    val req = create index Elastic.INDEX_NAME indexSetting ("mapper_dynamic", false) mappings
      mapping(Elastic.TYPE_NAME).fields(
        "text" typed StringType
      )

    client.execute(req)
  }
}
