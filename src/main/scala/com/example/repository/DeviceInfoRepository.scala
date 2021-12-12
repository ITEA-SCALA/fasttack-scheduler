package com.example.repository

import com.example.data._
import scala.concurrent.Future


trait DeviceInfoRepository {
  def find(tokenRefId: String): Future[Option[DeviceInfo]]
  def list: Future[Seq[DeviceInfo]]
  def filter(tokenRefId: Option[String], deviceName: Option[String], deviceType: Option[String]): Future[Seq[DeviceInfo]]
  def create(req: RequestDeviceInfo): Future[DeviceInfo]
  def update(req: DeviceInfo): Future[Int]
  def remove(tokenRefId: String): Future[Int]

  def prepareRepository(): Future[Unit]
}