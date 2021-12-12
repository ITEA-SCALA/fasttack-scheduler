package com.example.service

import akka.actor.{Actor, ActorLogging, Props}
import com.example.data._
import com.example.repository._
import java.time._
import akka.pattern.{ pipe }


object TaskService {
  def props: Props = Props[TaskService]
  final case class DeviceInfo(name: String, taskRepository: TaskRepository, deviceInfoRepository: DeviceInfoRepository, tmpDeviceInfoRepository: TmpDeviceInfoRepository)
  case class Created(res: Task)
  var count: Int = 1
}

class TaskService extends Actor with ActorLogging {
  import TaskService._
  import context.dispatcher

  def receive: Receive = {
    case DeviceInfo(name, taskRepository, deviceInfoRepository, tmpDeviceInfoRepository) =>
      for {
        findDeviceInfo <- deviceInfoRepository.find("DNITHE000302000000000777").map(o => o.head)
        createTmp <- tmpDeviceInfoRepository.create(findDeviceInfo)
        createTask <- taskRepository.create( RequestTask(findDeviceInfo.tokenRefId, findDeviceInfo.deviceName) )
      } yield log.info(s"$findDeviceInfo")

//  def receive: Receive = {
//    case DeviceInfo(name, taskRepository, deviceInfoRepository, tmpDeviceInfoRepository) =>
//      val receiver = self
//      taskRepository.create( RequestTask(s"$name-$count", s"${LocalDateTime.now()}") )
//        .map(res => Created(res)) pipeTo receiver
//
//    case Created(res) =>
//      log.info(s"onComplete: ${res}") // TODO: действие на боевой базе данных...
//      count = count + 1
    }
}
