package com.fasttack.config

import com.typesafe.config.{Config, ConfigFactory}
import slick.jdbc.PostgresProfile.api._
import scala.{Option => SOption}
import java.lang.System.{getProperty => JSystemProperty}


object Environment {
  private val config: Config = ConfigFactory.load()

  def createDatabase: Database = {
    val postgreUrl: String = Option("postgre-url")
      .getOrElse(config.getString("db.localhost.slick-postgres.db.properties.url"))
    val postgreUser: String = Option("postgre-user")
      .getOrElse(config.getString("db.localhost.slick-postgres.db.properties.user"))
    val postgrePassword: String = Option("postgre-password")
      .getOrElse(config.getString("db.localhost.slick-postgres.db.properties.password"))

    Database.forURL(driver = "org.postgresql.Driver", url = postgreUrl, user = postgreUser, password = postgrePassword)
  }

  def applicationHost: String = Option("application-host")
    .getOrElse(appConfig.application.host)

  def applicationPort: String = Option("application-port")
    .getOrElse(appConfig.application.port.toString)

  def maxPerTask: String = Option("max-per-task")
    .getOrElse(appConfig.scheduler.maxPerTask.toString)

  def takeNumTask: String = Option("take-num-task")
    .getOrElse(appConfig.scheduler.takeNumTask.toString)

  object Option {
    def apply(property: String): Option[String] = SOption( JSystemProperty(property) )
  }
}
