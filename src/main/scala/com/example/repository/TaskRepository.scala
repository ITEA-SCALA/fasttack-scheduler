package com.example.repository

import com.example.data._
import scala.concurrent.Future


trait TaskRepository {
  def find(id: Int): Future[Option[Task]]
  def list: Future[Seq[Task]]
  def filter(author: Option[String], name: Option[String]): Future[Seq[Task]]
  def create(req: RequestTask): Future[Task]
  def update(book: Task): Future[Int]
  def remove(id: Int): Future[Int]

  def prepareRepository(): Future[Unit]
}