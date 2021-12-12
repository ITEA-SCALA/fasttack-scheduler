package com.example.repository

import com.example.config._
import com.example.data._
import slick.driver.H2Driver.api._
import slick.lifted.ProvenShape
import java.sql.Timestamp
import scala.concurrent.{ExecutionContext, Future}


class TaskRepositoryH2(implicit ec: ExecutionContext)
  extends TaskEntity(TableQuery[TaskTable])
    with TaskRepository
{
  override def find(taskId: Int) = getBookById(taskId).map(_.headOption)

  override def list = h2DB.run(entity.result)

  override def length = queryLength

  override def filter(deviceName: Option[String], tokenRefId: Option[String]) = {
    h2DB.run{
//      queryAnd(deviceName,tokenRefId).result
      queryOr(deviceName,tokenRefId).result
    }
  }

  def create(task: Task) = {
    saveAutoInc(task).map( id =>
      task.copy(id = id))
  }

  override def update(task: Task) = {
    h2DB.run {
      entity.filter(_.id === task.id)
        .map(t => (t.tokenRefId, t.deviceName))
        .update((task.tokenRefId, task.deviceName))
    }
  }

  override def remove(id: Int) = {
    h2DB.run {
      entity.filter(_.id === id)
        .delete
    }
  }

  override def prepareRepository() = {
    h2DB.run {
      entity.schema.createIfNotExists
    }
  }
}

abstract class TaskEntity[E <: TaskTable](val entity: TableQuery[E])
{
  def getBookById(id: Int): Future[Seq[Task]] = {
    val query: Query[E, Task, Seq] = for {
      task <- entity if task.id === id
    } yield task
    h2DB.run(query.result)
  }

  /*
   * author=author_19  name=name_19
   * author=author_19  name=
   * author=  name=
   */
  def queryAnd(deviceName: Option[String], tokenRefId: Option[String]): Query[E, Task, Seq] = (deviceName, tokenRefId) match {
    case (v1, v2) if v1.nonEmpty && v2.nonEmpty => entity.filter(_.deviceName === v1).filter(_.tokenRefId === v2)
    case (v1, _) if v1.nonEmpty => entity.filter(_.deviceName === v1)
    case (_, v2) if v2.nonEmpty => entity.filter(_.tokenRefId === v2)
    case _ => entity
  }

  /*
   * name=name_19  author=test update author
   * name=  author=test update author
   * author=  name=
   */
  def queryOr(deviceName: Option[String], tokenRefId: Option[String]): Query[E, Task, Seq] = {
    entity.filter(_.deviceName === deviceName)
      .union( entity.filter(_.tokenRefId === tokenRefId) )
  }

  def queryLength: Future[Int] = {
    val query: Query[E, Task, Seq] = for {
      task <- entity
    } yield task
    h2DB.run(query.length.result)
  }

  def exists(id: Int): Future[Boolean] = {
    val query: Query[E, Task, Seq] = for {
      task <- entity if task.id === id
    } yield task
    h2DB.run(query.exists.result)
  }

  def save(book: Task): Future[Int] = {
    h2DB.run {
      entity += book
    }
  }

  def saveAutoInc(task: Task): Future[Int] = {
    h2DB.run(entity returning entity.map(_.id) += task)
  }
}

class TaskTable(tag: Tag) extends Table[Task](tag, "tasks") {
  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def tokenRefId: Rep[String] = column[String]("tokenRefId")
  def deviceName: Rep[Option[String]] = column[Option[String]]("deviceName")
  def fixDeviceName: Rep[Option[String]] = column[Option[String]]("fixDeviceName")
  def foundRecords: Rep[Int] = column[Int]("foundRecords")
  def seqSchedulerTask: Rep[Int] = column[Int]("seqSchedulerTask")
  def schedulerExpression: Rep[String] = column[String]("schedulerExpression")
  def statusDecode: Rep[String] = column[String]("statusDecode")
  def startedAt: Rep[Timestamp] = column[Timestamp]("startedAt")
  def finishedAt: Rep[Timestamp] = column[Timestamp]("finishedAt")

  def * : ProvenShape[Task] = (tokenRefId, deviceName, fixDeviceName, foundRecords, seqSchedulerTask, schedulerExpression, statusDecode, startedAt, finishedAt, id).mapTo[Task]
}
