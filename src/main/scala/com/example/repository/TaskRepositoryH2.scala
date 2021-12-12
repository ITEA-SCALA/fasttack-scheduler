package com.example.repository

import com.example.config._
import com.example.data._
import slick.driver.H2Driver.api._
import slick.lifted.ProvenShape
import scala.concurrent.{ExecutionContext, Future}


class TaskRepositoryH2(implicit ec: ExecutionContext)
  extends TaskEntity(TableQuery[TaskTable])
    with TaskRepository
{
  override def find(id: Int) = getBookById(id).map(_.headOption)

  override def list = h2DB.run(entity.result)

  override def filter(author: Option[String], name: Option[String]) = {
    h2DB.run{
//      queryAnd(author,name).result
      queryOr(author,name).result
    }
  }

  def create(req: RequestTask) = {
    val book = Task(req.name, req.author)
    saveAutoInc(req).map( id =>
      book.copy(id = id))
  }

  override def update(book: Task) = {
    h2DB.run {
      entity.filter(_.id === book.id)
        .map(b => (b.name, b.author))
        .update((book.name, book.author))
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
      book <- entity if book.id === id
    } yield book
    h2DB.run(query.result)
  }

  /*
   * author=author_19  name=name_19
   * author=author_19  name=
   * author=  name=
   */
  def queryAnd(author: Option[String], name: Option[String]): Query[E, Task, Seq] = (author, name) match {
    case (a, n) if a.nonEmpty && n.nonEmpty => entity.filter(_.author === a).filter(_.name === n)
    case (a, _) if a.nonEmpty => entity.filter(_.author === a)
    case (_, n) if n.nonEmpty => entity.filter(_.name === n)
    case _ => entity
  }

  /*
   * name=name_19  author=test update author
   * name=  author=test update author
   * author=  name=
   */
  def queryOr(author: Option[String], name: Option[String]): Query[E, Task, Seq] = {
    entity.filter(_.author === author)
      .union( entity.filter(_.name === name) )
  }

  def exists(id: Int): Future[Boolean] = {
    val query: Query[E, Task, Seq] = for {
      book <- entity if book.id === id
    } yield book
    h2DB.run(query.exists.result)
  }

  def save(book: Task): Future[Int] = {
    h2DB.run {
      entity += book
    }
  }

  def saveAutoInc(req: RequestTask): Future[Int] = {
    h2DB.run(entity returning entity.map(_.id) += Task(req.name, req.author))
  }
}

class TaskTable(tag: Tag) extends Table[Task](tag, "TASKS") {
  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name: Rep[String] = column[String]("name")
  def author: Rep[String] = column[String]("author")

  def * : ProvenShape[Task] = (name, author, id) <> (Task.tupled, Task.unapply)
}
