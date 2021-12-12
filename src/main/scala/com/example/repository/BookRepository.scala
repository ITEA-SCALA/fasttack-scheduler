package com.example.repository

import com.example.data._
import scala.concurrent.Future


trait BookRepository {
  def find(id: Int): Future[Option[Book]]
  def list: Future[Seq[Book]]
  def filter(author: Option[String], name: Option[String]): Future[Seq[Book]]
  def create(req: RequestBook): Future[Book]
  def update(book: Book): Future[Int]
  def remove(id: Int): Future[Int]

  def prepareRepository(): Future[Unit]
}