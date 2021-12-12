package com.fasttack.data


case class RequestDeviceInfo(
  tokenRefId: String,
  deviceName: Option[String],
  serialNumber: Option[String],
  osName: Option[String],
  osVersion: Option[String],
  imei: Option[String],
  storageTechnology: Option[String],
  deviceType: Option[String],
)

case class DeviceInfo(
  tokenRefId: String,
  deviceName: Option[String],
  serialNumber: Option[String],
  osName: Option[String],
  osVersion: Option[String],
  imei: Option[String],
  storageTechnology: Option[String],
  deviceType: Option[String],
)