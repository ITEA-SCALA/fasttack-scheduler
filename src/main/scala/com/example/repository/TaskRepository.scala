package com.example.repository

import com.example.data._
import scala.concurrent.Future


trait TaskRepository {
  def prepareRepository(): Future[Unit]
  def find(id: Int): Future[Option[Task]]
  def list: Future[Seq[Task]]
  def length: Future[Int]
  def filter(deviceName: Option[String], tokenRefId: Option[String]): Future[Seq[Task]]
  def create(task: Task): Future[Task]
  def update(task: Task): Future[Int]
  def remove(id: Int): Future[Int]
}