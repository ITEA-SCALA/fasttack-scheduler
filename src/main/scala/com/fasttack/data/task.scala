package com.fasttack.data

import java.sql.Timestamp
import java.time.LocalDateTime


case class Task(
  tokenRefId: String,
  deviceName: Option[String],
  fixDeviceName: Option[String],
  foundRecords: Int,
  seqSchedulerTask: Int,
  schedulerExpression: String,
  statusDecode: String,
  startedAt: Timestamp,
  finishedAt: Timestamp = Timestamp.valueOf(LocalDateTime.now),
  id: Int = 0
)
