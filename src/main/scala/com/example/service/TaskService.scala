package com.example.service

import akka.actor.{Actor, ActorLogging, Props}
import com.example.data._
import com.example.repository._
import java.time._
import akka.pattern.pipe
import com.example.config.Environment
import com.example.routes._
import com.example.utils.FileUtil.saveToFile
import com.example.utils.FileUtil.loadFromFile
import com.example.utils.Base64UrlValidUtil
import java.sql.Timestamp
import scala.concurrent.Future
import scala.sys.exit


object TaskService {
  def props: Props = Props[TaskService]
  final case class FixBase64Decode(startedAt: Timestamp, schedulerExpression: String, listDeviceInfo: Seq[DeviceInfo], taskRepository: TaskRepository, deviceInfoRepository: DeviceInfoRepository, tmpDeviceInfoRepository: TmpDeviceInfoRepository)
  var seqSchedulerTask: Int = loadFromFile.toInt
  var seqSchedulerPerTask: Int = 0
}

class TaskService extends Actor with ActorLogging {
  import TaskService._
  import context.dispatcher

  def receive: Receive = {
    case FixBase64Decode(startedAt, schedulerExpression, listDeviceInfo, taskRepository, deviceInfoRepository, tmpDeviceInfoRepository) =>
      for {
        deviceInfo          <- deviceInfoRepository.findAll(seqSchedulerTask, 1).map(o => o.head)
        decode              <- Future {
            deviceInfo.deviceName.map { o =>
              val (actionDecode, statusDecode) = Base64UrlValidUtil.isDecode(o)
              val fixDeviceName: Option[String] = Option(Base64UrlValidUtil.decode(o, actionDecode))
              (fixDeviceName, actionDecode, statusDecode)
            }
          }
        createTmpDeviceInfo <- tmpDeviceInfoRepository.create(deviceInfo) // TODO: TmpDeviceInfo is manual create as SQL-script
        createTask          <- {
          val (fixDeviceName, actionDecode, statusDecode) = decode.head
          val deviceInfoDto: DeviceInfoDto = DeviceInfoDto(deviceInfo.tokenRefId, deviceInfo.deviceName, fixDeviceName)
          val taskDto: TaskDto = TaskDto(deviceInfoDto, listDeviceInfo.length, seqSchedulerTask, schedulerExpression, statusDecode, startedAt)
          taskRepository.create( Mapper(taskDto) )
        }
      } yield {
        val (fixDeviceName, actionDecode, statusDecode) = decode.head
        log.info(s"$createTask")
        if (actionDecode) {
          deviceInfoRepository.update( deviceInfo.copy(deviceName=fixDeviceName) )
          log.info(s"Success updated DeviceInfo Table by ID=${deviceInfo.tokenRefId}")
        }
      }

      if (
        listDeviceInfo.length < seqSchedulerTask
        || Environment.maxPerTask.toInt <= seqSchedulerPerTask
      ) exit(0)

      seqSchedulerTask += 1
      seqSchedulerPerTask += 1
      saveToFile(seqSchedulerTask.toString)
    }
}
