package com.example

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.example.repository._
import com.example.routes._
import org.slf4j.LoggerFactory
import scala.concurrent.{ExecutionContext, Future}
import com.softwaremill.macwire.wire
import com.example.config.{Environment, actorSystemName, appConfig}
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension
import com.example.service.TaskService
import com.example.service.TaskService.FixBase64Decode
import java.sql.Timestamp
import java.time.LocalDateTime
import java.lang.System.{getProperty => JSystemProperty}


object SchedulerApp {

  type OptionMap = Map[Symbol, Any]
  val usage =
    """
    Usage: SchedulerApp [--application-host str] [--application-port num] [--postgre-server-name str] [--postgre-port-number num] [--postgre-database-name str] [--postgre-user str] [--postgre-password str] [--max-per-task num] filename
    """

  private var postgreServerName: String   = ""
  private var postgrePortNumber: Int      = 0
  private var postgreDatabaseName: String = ""

  def main(args: Array[String]) {
    implicit val system: ActorSystem = ActorSystem(actorSystemName)
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val ec: ExecutionContext = system.dispatcher

    val log = LoggerFactory.getLogger(getClass)

    if (args.length == 0) log.info(usage)
    nextOption(Map(), args.toList)
    setPostgreUrl(postgreServerName, postgreDatabaseName, postgrePortNumber)
    // TODO:  --application-port 8082 --postgre-server-name localhost --postgre-database-name postgres --postgre-user postgres --postgre-password postgres --max-per-task 10
    log.debug(s"application-host = ${Environment.applicationHost}")         // TODO:  DEBUG
    log.debug(s"application-port = ${Environment.applicationPort}")         // TODO:  DEBUG
    log.debug(s"postgre-url      = ${JSystemProperty("postgre-url")}")      // TODO:  DEBUG
    log.debug(s"postgre-user     = ${JSystemProperty("postgre-user")}")     // TODO:  DEBUG
    log.debug(s"postgre-password = ${JSystemProperty("postgre-password")}") // TODO:  DEBUG
    log.debug(s"max-per-task     = ${Environment.maxPerTask}")              // TODO:  DEBUG

    val deviceInfoRepository = wire[DeviceInfoRepositoryPostgre]
    val tmpDeviceInfoRepository = wire[TmpDeviceInfoRepositoryPostgre]
    val taskRepository = wire[TaskRepositoryH2]
    val taskRoute = wire[TaskRoute]

    tmpDeviceInfoRepository.prepareRepository()
    taskRepository.prepareRepository()

    val routes: Route = pathPrefix("scheduler") {
      taskRoute.route
    }
    Http().bindAndHandle(
      routes, Environment.applicationHost, Environment.applicationPort.toInt)

    val startedAt: Timestamp = Timestamp.valueOf(LocalDateTime.now)
    val schedulerExpression: String = appConfig.scheduler.expression.every3Sec
    QuartzSchedulerExtension(system).createSchedule("scheduler", Some("Fix Base64 decode"), schedulerExpression)
    for {
      listDeviceInfo <- deviceInfoRepository.list
      schedulerTask <- {
        QuartzSchedulerExtension(system).schedule(
          "scheduler",
          system.actorOf(TaskService.props, "task"),
          FixBase64Decode(startedAt, schedulerExpression, listDeviceInfo, taskRepository, deviceInfoRepository, tmpDeviceInfoRepository))
        Future()
      }
    } yield ()
  }

  def setPostgreUrl(postgreServerName: String, postgreDatabaseName: String, postgrePortNumber: Int) {
    if (postgreServerName.nonEmpty && postgreDatabaseName.nonEmpty && postgrePortNumber!=0)
      System.setProperty("postgre-url", s"jdbc:postgresql://$postgreServerName:$postgrePortNumber/$postgreDatabaseName")
    else if (postgreServerName.nonEmpty && postgreDatabaseName.nonEmpty)
      System.setProperty("postgre-url", s"jdbc:postgresql://$postgreServerName/$postgreDatabaseName")
  }

  def nextOption(map: OptionMap, list: List[String]): OptionMap = {
    list match {
      case Nil =>
        map
      case "--application-host" :: value :: tail =>
        System.setProperty("application-host", value)
        nextOption(map ++ Map('applicationHost -> value), tail)
      case "--application-port" :: value :: tail =>
        System.setProperty("application-port", value)
        nextOption(map ++ Map('applicationPort -> value), tail)
      case "--postgre-server-name" :: value :: tail =>
        postgreServerName = value
        nextOption(map ++ Map('postgreServerName -> value), tail)
      case "--postgre-port-number" :: value :: tail =>
        postgrePortNumber = value.toInt
        nextOption(map ++ Map('postgrePortNumber -> value), tail)
      case "--postgre-database-name" :: value :: tail =>
        postgreDatabaseName = value
        nextOption(map ++ Map('postgreDatabaseName -> value), tail)
      case "--postgre-user" :: value :: tail =>
        System.setProperty("postgre-user", value)
        nextOption(map ++ Map('postgreUser -> value), tail)
      case "--postgre-password" :: value :: tail =>
        System.setProperty("postgre-password", value)
        nextOption(map ++ Map('postgrePassword -> value), tail)
      case "--max-per-task" :: value :: tail =>
        System.setProperty("max-per-task", value)
        nextOption(map ++ Map('maxPerTask -> value), tail)
      case string :: Nil =>
        nextOption(map ++ Map('infile -> string), list.tail)
    }
  }
}