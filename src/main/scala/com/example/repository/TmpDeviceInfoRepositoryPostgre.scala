package com.example.repository

import com.example.config.Environment
import com.example.data._
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape
import scala.concurrent.{ExecutionContext, Future}


class TmpDeviceInfoRepositoryPostgre(implicit ec: ExecutionContext)
  extends TmpDeviceInfoEntity(TableQuery[TmpDeviceInfoTable], Environment.createDatabase)
      with TmpDeviceInfoRepository
  {
    override def find(tokenRefId: String) = getBookById(tokenRefId).map(_.headOption)

    override def list = postgreDB.run(entity.result)

    override def filter(tokenRefId: Option[String], deviceName: Option[String], deviceType: Option[String]) = {
      postgreDB.run{
        queryAnd(tokenRefId,deviceName,deviceType).result
  //      queryOr(tokenRefId,deviceName,deviceType).result
      }
    }

    override def findAll(dropNum: Int, takeNum: Int) = {
      postgreDB.run {
        entity
          .drop(dropNum)
          .take(takeNum)
          .result
      }
    }

    def create(req: DeviceInfo) = {
      val v = DeviceInfo(req.tokenRefId, req.deviceName, req.serialNumber, req.osName, req.osVersion, req.imei, req.storageTechnology, req.deviceType)
      save(v).map(_ => v)
    }

    override def update(req: DeviceInfo) = {
      postgreDB.run {
        entity.filter(_.tokenRefId === req.tokenRefId)
          .map(v => (v.deviceName, v.deviceType))
          .update(req.deviceName, req.deviceType)
      }
    }

    override def remove(tokenRefId: String) = {
      postgreDB.run {
        entity.filter(_.tokenRefId === tokenRefId)
          .delete
      }
    }

    override def prepareRepository() = {
      postgreDB.run {
        entity.schema.createIfNotExists
      }
    }
  }

abstract class TmpDeviceInfoEntity[E <: TmpDeviceInfoTable](val entity: TableQuery[E], val postgreDB: Database)
{
  def getBookById(tokenRefId: String): Future[Seq[DeviceInfo]] = {
    val query: Query[E, DeviceInfo, Seq] = for {
      v <- entity if v.tokenRefId === tokenRefId
    } yield v
    postgreDB.run(query.result)
  }

  def queryAnd(tokenRefId: Option[String], deviceName: Option[String], deviceType: Option[String]): Query[E, DeviceInfo, Seq] = (tokenRefId, deviceName, deviceType) match {
    case (v0, v1, v2) if v0.nonEmpty && v1.isEmpty && v2.isEmpty => entity.filter(_.tokenRefId === v0)
    case _ => queryAnd(deviceName, deviceType)
  }

  def queryAnd(deviceName: Option[String], deviceType: Option[String]): Query[E, DeviceInfo, Seq] = (deviceName, deviceType) match {
    case (v1, v2) if v1.nonEmpty && v2.nonEmpty => entity.filter(_.deviceName === v1).filter(_.deviceType === v2)
    case (v1, _) if v1.nonEmpty => entity.filter(_.deviceName === v1)
    case (_, v2) if v2.nonEmpty => entity.filter(_.deviceType === v2)
    case _ => entity
  }

  def queryOr(tokenRefId: Option[String], deviceName: Option[String], deviceType: Option[String]): Query[E, DeviceInfo, Seq] = {
    entity.filter(_.deviceName === deviceName)
      .union( entity.filter(_.deviceType === deviceType) )
      .union( entity.filter(_.tokenRefId === tokenRefId) )
  }

  def queryLength: Future[Int] = {
    val query: Query[E, DeviceInfo, Seq] = for {
      task <- entity
    } yield task
    postgreDB.run(query.length.result)
  }

  def exists(tokenRefId: String): Future[Boolean] = {
    val query: Query[E, DeviceInfo, Seq] = for {
      v <- entity if v.tokenRefId === tokenRefId
    } yield v
    postgreDB.run(query.exists.result)
  }

  def save(req: DeviceInfo): Future[Int] = {
    postgreDB.run {
      entity += req
    }
  }
}

class TmpDeviceInfoTable(tag: Tag) extends Table[DeviceInfo](tag, "tmp_device_info") {

  def tokenRefId: Rep[String] = column[String]("token_ref_id", O.PrimaryKey)
  def deviceName: Rep[Option[String]] = column[Option[String]]("device_name")
  def serialNumber: Rep[Option[String]] = column[Option[String]]("serial_number")
  def osName: Rep[Option[String]] = column[Option[String]]("os_name")
  def osVersion: Rep[Option[String]] = column[Option[String]]("os_version")
  def imei: Rep[Option[String]] = column[Option[String]]("imei")
  def storageTechnology: Rep[Option[String]] = column[Option[String]]("storage_technology")
  def deviceType: Rep[Option[String]] = column[Option[String]]("device_type")

  def * : ProvenShape[DeviceInfo] = (tokenRefId, deviceName, serialNumber, osName, osVersion, imei, storageTechnology, deviceType).mapTo[DeviceInfo]
}
