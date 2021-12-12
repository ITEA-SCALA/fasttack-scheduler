package com.fasttack

import com.typesafe.config.{Config, ConfigFactory}
import pureconfig.loadConfigOrThrow
import slick.jdbc.PostgresProfile.api._


package object config {

  private val config: Config       = ConfigFactory.load()
  val actorSystemName: String      = config.getString("akka.system.name")
  val appConfig: ApplicationConfig = loadConfigOrThrow[ApplicationConfig](config)
//  val postgreDB: Database          = Database.forConfig("db.localhost.postgre")
  // TODO:  "What's the difference between using DatabaseConfig and Database in Slick?"  https://stackoverflow.com/questions/35636436/whats-the-difference-between-using-databaseconfig-and-database-in-slick  ||  https://doc.akka.io/docs/alpakka/current/slick.html
//  val postgreDB: Database          = Database.forConfig("db.localhost.slick-postgres.db")
//  val postgreDB: Database          = Database.forURL(url = config.getString("db.localhost.slick-postgres.db.properties.url"), driver = "org.postgresql.Driver", user = "postgres", password = "postgres")
  val h2DB: Database               = Database.forURL(url = "jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

  case class ApplicationConfig(
    scheduler: SchedulerConfig,
    application: Application,
  )

  case class SchedulerConfig(
    expression: SchedulerExpressionConfig,
    saveAs: String,
    maxPerTask: Int,
    failurePerTask: Int,
    takeNumTask: Int,
  )

  case class SchedulerExpressionConfig(
    every5Sec: String,
    every3Sec: String,
    every2Sec: String,
    everySec: String,
  )

  case class Application(
    host: String,
    port: Int
  )
}
