package com.fasttack.repository

import com.fasttack.data._
import scala.concurrent.Future


trait DeviceInfoRepository {
  def prepareRepository(): Future[Unit]
  def find(tokenRefId: String): Future[Option[DeviceInfo]]
  @Deprecated def list: Future[Seq[DeviceInfo]]
  def findOne(dropNum: Int): Future[DeviceInfo]
  def findAll(dropNum: Int, takeNum: Int): Future[Seq[DeviceInfo]]
  def length: Future[Int]
  def filter(tokenRefId: Option[String], deviceName: Option[String], deviceType: Option[String]): Future[Seq[DeviceInfo]]
  def create(req: DeviceInfo): Future[DeviceInfo]
  def update(req: DeviceInfo): Future[Int]
  def remove(tokenRefId: String): Future[Int]
}