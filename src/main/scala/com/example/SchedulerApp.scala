package com.example

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.example.repository._
import com.example.routes._
import org.slf4j.LoggerFactory
import scala.concurrent.ExecutionContext
import com.softwaremill.macwire.wire
import com.example.config.{actorSystemName, appConfig}
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension
import com.example.service.TaskService
import com.example.service.TaskService.DeviceInfo


object SchedulerApp extends App {
  implicit val system: ActorSystem             = ActorSystem(actorSystemName)
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext            = system.dispatcher

  val log = LoggerFactory.getLogger(getClass)

  val bookRepository          = wire[BookRepositoryPostgre]
  val deviceInfoRepository    = wire[DeviceInfoRepositoryPostgre]
  val tmpDeviceInfoRepository = wire[TmpDeviceInfoRepositoryPostgre]
  val taskRepository          = wire[TaskRepositoryH2]
  val bookRoute               = wire[BookRoute]
  val deviceInfoRoute         = wire[DeviceInfoRoute]
  val taskRoute               = wire[TaskRoute]

//  deviceInfoRepository.prepareRepository()
  tmpDeviceInfoRepository.prepareRepository()
  taskRepository.prepareRepository()

  val routes: Route = pathPrefix("api") {
    bookRoute.route ~
    deviceInfoRoute.route ~
    taskRoute.route
  }
  Http().bindAndHandle(
    routes, appConfig.application.host, appConfig.application.port)

  // TODO: каждые 5-секунд
  val taskService: ActorRef = system.actorOf(TaskService.props, "task")
  QuartzSchedulerExtension(system).createSchedule("scheduleDeviceInfo", Some("DeviceInfo"), appConfig.schedulerExpression.every5Sec)
  QuartzSchedulerExtension(system).schedule("scheduleDeviceInfo", taskService, DeviceInfo("DeviceInfo", taskRepository, deviceInfoRepository, tmpDeviceInfoRepository))
}
