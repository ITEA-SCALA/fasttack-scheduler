package com.example

import com.typesafe.config.{Config, ConfigFactory}
import pureconfig.loadConfigOrThrow
import slick.jdbc.PostgresProfile.api._


package object config {

  private val config: Config       = ConfigFactory.load()
  val actorSystemName: String      = config.getString("akka.system.name")
  val appConfig: ApplicationConfig = loadConfigOrThrow[ApplicationConfig](config)
  val postgreDB: Database          = Database.forConfig("db.localhost.postgre")
  val h2DB: Database               = Database.forURL(url = "jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

  case class ApplicationConfig(
    schedulerExpression: SchedulerExpressionConfig,
    application: Application,
  )

  case class SchedulerExpressionConfig(
    every5Sec: String
  )

  case class Application(
    host: String,
    port: Int
  )
}
