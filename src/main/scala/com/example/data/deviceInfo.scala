package com.example.data


case class RequestDeviceInfo(
  tokenRefId: String,
  deviceName: String,
  serialNumber: String,
  osName: OsName.OsNameType,
  osVersion: String,
  imei: String,
  storageTechnology: String,
  deviceType: String,
)

case class DeviceInfo(
  tokenRefId: String,
  deviceName: String,
  serialNumber: String,
  osName: OsName.OsNameType,
  osVersion: String,
  imei: String,
  storageTechnology: String,
  deviceType: String,
)