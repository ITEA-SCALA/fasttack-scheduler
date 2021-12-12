package com.example.repository

import com.example.config._
import com.example.data._
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape
import scala.concurrent.{ExecutionContext, Future}


class BookRepositoryPostgre(implicit ec: ExecutionContext)
  extends BookEntity(TableQuery[BookTable])
    with BookRepository
{
  override def find(id: Int) = getBookById(id).map(_.headOption)

  override def list = postgreDB.run(entity.result)

  override def filter(author: Option[String], name: Option[String]) = {
    postgreDB.run{
//      queryAnd(author,name).result
      queryOr(author,name).result
    }
  }

  def create(req: RequestBook) = {
    val book = Book(req.name, req.author)
    saveAutoInc(req).map( id =>
      book.copy(id = id) )
  }

  override def update(book: Book) = {
    postgreDB.run {
      entity.filter(_.id === book.id)
        .map(b => (b.name, b.author))
        .update((book.name, book.author))
    }
  }

  override def remove(id: Int) = {
    postgreDB.run {
      entity.filter(_.id === id)
        .delete
    }
  }

  override def prepareRepository() = {
    postgreDB.run {
      entity.schema.createIfNotExists
    }
  }
}

abstract class BookEntity[E <: BookTable](val entity: TableQuery[E])
{
  def getBookById(id: Int): Future[Seq[Book]] = {
    val query: Query[E, Book, Seq] = for {
      book <- entity if book.id === id
    } yield book
    postgreDB.run(query.result)
  }

  /*
   * author=author_19  name=name_19
   * author=author_19  name=
   * author=  name=
   */
  def queryAnd(author: Option[String], name: Option[String]): Query[E, Book, Seq] = (author, name) match {
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
  def queryOr(author: Option[String], name: Option[String]): Query[E, Book, Seq] = {
    entity.filter(_.author === author)
      .union( entity.filter(_.name === name) )
  }

  def exists(id: Int): Future[Boolean] = {
    val query: Query[E, Book, Seq] = for {
      book <- entity if book.id === id
    } yield book
    postgreDB.run(query.exists.result)
  }

  def save(book: Book): Future[Int] = {
    postgreDB.run {
      entity += book  // entities.insertOrUpdate(book)
    }
  }

  def saveAutoInc(req: RequestBook): Future[Int] = {
    postgreDB.run(entity returning entity.map(_.id) += Book(req.name, req.author))
  }
}

class BookTable(tag: Tag) extends Table[Book](tag, "BOOKS") {
  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name: Rep[String] = column[String]("name")
  def author: Rep[String] = column[String]("author")

  def * : ProvenShape[Book] = (name, author, id) <> (Book.tupled, Book.unapply)
}
