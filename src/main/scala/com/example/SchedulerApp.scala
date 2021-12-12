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
import com.example.service.DeviceInfoTaskService
import com.example.service.DeviceInfoTaskService.FixBase64Decode
import java.sql.Timestamp
import java.time.LocalDateTime
import java.lang.System.{getProperty => JSystemProperty}


object SchedulerApp {

  type OptionMap = Map[Symbol, Any]
  val usage =
    """
    Usage: SchedulerApp [--application-host str] [--application-port num] [--postgre-server-name str] [--postgre-port-number num] [--postgre-database-name str] [--postgre-user str] [--postgre-password str] [--max-per-task num] [--take-num-task num] filename
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
    initPostgreUrl(postgreServerName, postgrePortNumber, postgreDatabaseName)
    // TODO:  --application-port 8082 --postgre-server-name localhost --postgre-database-name postgres --postgre-user postgres --postgre-password postgres --max-per-task 10 --take-num-task 3
    log.debug(s"application-host = ${Environment.applicationHost}")         // TODO:  DEBUG
    log.debug(s"application-port = ${Environment.applicationPort}")         // TODO:  DEBUG
    log.debug(s"postgre-url      = ${JSystemProperty("postgre-url")}")      // TODO:  DEBUG
    log.debug(s"postgre-user     = ${JSystemProperty("postgre-user")}")     // TODO:  DEBUG
    log.debug(s"postgre-password = ${JSystemProperty("postgre-password")}") // TODO:  DEBUG
    log.debug(s"max-per-task     = ${Environment.maxPerTask}")              // TODO:  DEBUG
    log.debug(s"take-num-task    = ${Environment.takeNumTask}")             // TODO:  DEBUG

    val deviceInfoRepository: DeviceInfoRepository = wire[DeviceInfoRepositoryPostgre]
    val tmpDeviceInfoRepository: TmpDeviceInfoRepository = wire[TmpDeviceInfoRepositoryPostgre]
    val taskRepository: TaskRepository = wire[TaskRepositoryH2]
    val deviceInfoTaskRoute: DeviceInfoTaskRoute = wire[DeviceInfoTaskRoute]

    tmpDeviceInfoRepository.prepareRepository()
    taskRepository.prepareRepository()

    val routes: Route = pathPrefix("fasttack-scheduler-json") {
      deviceInfoTaskRoute.route
    }
    Http().bindAndHandle(
      routes, Environment.applicationHost, Environment.applicationPort.toInt)

    val startedAt: Timestamp = Timestamp.valueOf(LocalDateTime.now)
    val schedulerExpression: String = appConfig.scheduler.expression.every2Sec
    QuartzSchedulerExtension(system).createSchedule("device-info-task", Some("Fix Base64 decode"), schedulerExpression)
    for {
      lengthDeviceInfo <- deviceInfoRepository.length
      _ <- {
        Future {
          QuartzSchedulerExtension(system).schedule(
            "device-info-task",
            system.actorOf(DeviceInfoTaskService.props, "device-info-task"),
            FixBase64Decode(startedAt, schedulerExpression, lengthDeviceInfo, taskRepository, deviceInfoRepository, tmpDeviceInfoRepository))
        }
      }
    } ()
  }

  def initPostgreUrl(postgreServerName: String, postgrePortNumber: Int, postgreDatabaseName: String): Unit = {
    if (postgreServerName.nonEmpty && postgreDatabaseName.nonEmpty && postgrePortNumber!=0)
      System.setProperty("postgre-url", s"jdbc:postgresql://$postgreServerName:$postgrePortNumber/$postgreDatabaseName")
    else if (postgreServerName.nonEmpty && postgreDatabaseName.nonEmpty)
      System.setProperty("postgre-url", s"jdbc:postgresql://$postgreServerName/$postgreDatabaseName")
  }

  /**
   * @see https://www.geeksforgeeks.org/scala-option
   *      https://newbedev.com/best-way-to-parse-command-line-parameters
   */
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
      case "--take-num-task" :: value :: tail =>
        System.setProperty("take-num-task", value)
        nextOption(map ++ Map('takeNumTask -> value), tail)
      case string :: Nil =>
        nextOption(map ++ Map('infile -> string), list.tail)
    }
  }
}