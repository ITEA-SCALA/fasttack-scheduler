package com.example.repository

import com.example.config._
import com.example.data._
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape
import scala.concurrent.{ExecutionContext, Future}


class TmpDeviceInfoRepositoryPostgre(implicit ec: ExecutionContext)
  extends TmpDeviceInfoEntity(TableQuery[TmpDeviceInfoTable])
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

abstract class TmpDeviceInfoEntity[E <: TmpDeviceInfoTable](val entity: TableQuery[E])
{
  def getBookById(tokenRefId: String): Future[Seq[DeviceInfo]] = {
    val query: Query[E, DeviceInfo, Seq] = for {
      v <- entity if v.tokenRefId === tokenRefId
    } yield v
    postgreDB.run(query.result)
  }

  /*
   * tokenRefId=DNITHE000302000000000777
   * deviceName=MY BEST PHONE  deviceType=MOBILE_PHONE
   * deviceName=MY BEST PHONE  deviceType=
   * deviceName=  deviceType=
   */
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

  /*
   * deviceName=MY BEST PHONE  deviceType=MOBILE_PHONE
   * deviceName=MY BEST PHONE  deviceType=
   * deviceName=  deviceType=
   */
  def queryOr(tokenRefId: Option[String], deviceName: Option[String], deviceType: Option[String]): Query[E, DeviceInfo, Seq] = {
    entity.filter(_.deviceName === deviceName)
      .union( entity.filter(_.deviceType === deviceType) )
      .union( entity.filter(_.tokenRefId === tokenRefId) )
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

class TmpDeviceInfoTable(tag: Tag) extends Table[DeviceInfo](tag, "TMP_DEVICE_INFO") {
  implicit val osNameColumn = MappedColumnType.base[OsName.OsNameType, String](_.toString, OsName.withName)

  def tokenRefId: Rep[String] = column[String]("TOKEN_REF_ID", O.PrimaryKey)
  def deviceName: Rep[String] = column[String]("DEVICE_NAME")
  def serialNumber: Rep[String] = column[String]("SERIAL_NUMBER")
  def osName: Rep[OsName.OsNameType] = column[OsName.OsNameType]("OS_NAME")
  def osVersion: Rep[String] = column[String]("OS_VERSION")
  def imei: Rep[String] = column[String]("IMEI")
  def storageTechnology: Rep[String] = column[String]("STORAGE_TECHNOLOGY")
  def deviceType: Rep[String] = column[String]("DEVICE_TYPE")

  def * : ProvenShape[DeviceInfo] = (tokenRefId, deviceName, serialNumber, osName, osVersion, imei, storageTechnology, deviceType) <> (DeviceInfo.tupled, DeviceInfo.unapply)
}
