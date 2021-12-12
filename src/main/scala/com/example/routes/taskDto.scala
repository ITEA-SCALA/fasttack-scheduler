package com.example.routes

import java.sql.Timestamp
import java.time.LocalDateTime


case class DeviceInfoDto(
  tokenRefId: String,
  deviceName: Option[String],
  fixDeviceName: Option[String],
)

case class TaskDto(
  deviceInfo: DeviceInfoDto,
  foundRecords: Int,
  seqSchedulerTask: Int,
  schedulerExpression: String,
  statusDecode: String,
  startedAt: Timestamp,
  finishedAt: Timestamp = Timestamp.valueOf(LocalDateTime.now),
)
