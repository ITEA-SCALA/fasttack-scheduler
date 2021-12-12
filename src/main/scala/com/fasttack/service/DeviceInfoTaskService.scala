package com.fasttack.service

import akka.actor.{Actor, ActorLogging, Props}
import com.fasttack.data._
import com.fasttack.repository._
import java.time._
import akka.pattern.pipe
import com.fasttack.config.Environment
import com.fasttack.routes._
import com.fasttack.utils.FileUtil.saveToFile
import com.fasttack.utils.FileUtil.loadFromFile
import com.fasttack.utils.Base64UrlValidUtil
import java.sql.Timestamp
import scala.concurrent.Future
import scala.sys.exit
import scala.util.{Failure, Success}


object DeviceInfoTaskService {
  def props: Props = Props[DeviceInfoTaskService]
  final case class FixBase64Decode(startedAt: Timestamp, schedulerExpression: String, lengthDeviceInfo: Int, taskRepository: TaskRepository, deviceInfoRepository: DeviceInfoRepository, tmpDeviceInfoRepository: TmpDeviceInfoRepository)
  val takeFindNumTask: Int = Environment.takeNumTask.toInt
  var seqSchedulerTask: Int = loadFromFile.toInt
  var seqSchedulerPerTask: Int = 0
}

class DeviceInfoTaskService extends Actor with ActorLogging {
  import DeviceInfoTaskService._
  import context.dispatcher

  def receive: Receive = {
    case FixBase64Decode(startedAt, schedulerExpression, lengthDeviceInfo, taskRepository, deviceInfoRepository, tmpDeviceInfoRepository) =>
      val finalFuture: Future[Unit] = for {
        deviceInfoList      <- deviceInfoRepository.findAll(seqSchedulerTask, takeFindNumTask)
//        _                   <- tmpDeviceInfoRepository.create(deviceInfo) // TODO: TmpDeviceInfo is manual create as SQL-Script
      } yield {
        updates(deviceInfoList.toList, startedAt, schedulerExpression, lengthDeviceInfo, taskRepository, deviceInfoRepository)
      }

      // TODO: https://stackoverflow.com/questions/26357588/whats-the-difference-between-oncomplete-and-flatmap-of-future
      finalFuture onComplete {
        case Success(value) =>
          if (
            lengthDeviceInfo <= seqSchedulerTask
              || Environment.maxPerTask.toInt < seqSchedulerPerTask
          ) exit(0)
        case Failure(t) =>
          if (
            lengthDeviceInfo <= seqSchedulerTask
              || Environment.maxPerTask.toInt < seqSchedulerPerTask
          ) exit(1)
      }
  }

  // TODO: @see https://www.reddit.com/r/scala/comments/4nz4o5/how_does_scala_pattern_match_headtail_in_list
  private def updates(deviceInfoList: List[DeviceInfo],
              startedAt: Timestamp,
              schedulerExpression: String,
              lengthDeviceInfo: Int,
              taskRepository: TaskRepository,
              deviceInfoRepository: DeviceInfoRepository,
             ): Unit = deviceInfoList match {
    case head::tail => {
      seqSchedulerTask += 1
      seqSchedulerPerTask += 1
      saveToFile(seqSchedulerTask.toString)
      update(head, startedAt, schedulerExpression, lengthDeviceInfo, seqSchedulerTask, taskRepository, deviceInfoRepository)
      updates(tail, startedAt, schedulerExpression, lengthDeviceInfo, taskRepository, deviceInfoRepository)
    }
    case Nil => ()
  }

  private def update(deviceInfo: DeviceInfo,
                     startedAt: Timestamp,
                     schedulerExpression: String,
                     lengthDeviceInfo: Int,
                     seq: Int,
                     taskRepository: TaskRepository,
                     deviceInfoRepository: DeviceInfoRepository
                    ): Unit = {
    val (fixDeviceName, actionDecode, statusDecode) = decode(deviceInfo.deviceName)
    val task: Task = newTask(deviceInfo, startedAt, schedulerExpression, lengthDeviceInfo, seq, fixDeviceName, statusDecode)
    taskRepository.create(task)
    log.info(s"$task")
    if (actionDecode) {
      deviceInfoRepository.update( deviceInfo.copy(deviceName=fixDeviceName) )
      log.info(s"Success updated DeviceInfo Table by ID=${deviceInfo.tokenRefId}")
    }
  }

  private def decode(deviceName: Option[String]): (Option[String], Boolean, String) = {
    deviceName.map { o =>
      val (actionDecode, statusDecode) = Base64UrlValidUtil.isDecode(o)
      val fixDeviceName: Option[String] = Option(Base64UrlValidUtil.decode(o, actionDecode))
      (fixDeviceName, actionDecode, statusDecode)
    }.getOrElse(deviceName, false, "Illegal base64 character")
  }

  private def newTask(deviceInfo: DeviceInfo,
                      startedAt: Timestamp,
                      schedulerExpression: String,
                      lengthDeviceInfo: Int,
                      seq: Int,
                      fixDeviceName: Option[String],
                      statusDecode: String
                     ): Task = {
    val deviceInfoDto: DeviceInfoDto = DeviceInfoDto(deviceInfo.tokenRefId, deviceInfo.deviceName, fixDeviceName)
    val taskDto: TaskDto = TaskDto(deviceInfoDto, lengthDeviceInfo, seq, schedulerExpression, statusDecode, startedAt)
    Mapper(taskDto)
  }
}
