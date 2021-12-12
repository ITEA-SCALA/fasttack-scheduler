package com.example.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Route
import com.example.data._
import com.example.repository._


class TaskRoute(repository: TaskRepository) extends SprayJsonSupport {
  val route: Route = pathPrefix("tasks") {
    find ~
    list ~
    filter ~
    create ~
    update ~
    remove
  }

  /*
   * GET
   * http://localhost:8082/api/tasks/30
   * ***
   * {
   *   "author": "test update author",
   *   "id": 32,
   *   "name": "test update name"
   * }
   */
  def find: Route = path(IntNumber) { id =>
    get {
      onSuccess(repository.find(id)) (
        complete(_))
    }
  }

  /*
   * http://localhost:8082/api/tasks/filter?author=author_19r&name=name_19
   * http://localhost:8082/api/tasks/filter?name=name_19&author=test update author
   * ***
   * [
   *   {
   *     "author": "author_19",
   *     "id": 5,
   *     "name": "name_19"
   *   },
   *   {
   *     "author": "author_19",
   *     "id": 6,
   *     "name": "name_19"
   *   }
   * ]
   */
  def filter: Route = path("filter") {
    parameters('author.as[String].?, 'name.as[String].?) { (author, name) =>
      get {
        onSuccess(repository.filter(author, name)) (
          complete(_))
      }
    }
  }

  /*
   * GET
   * http://localhost:8082/api/tasks
   * ***
   * {
   *    "author": "test author",
   *    "id": 5,
   *    "name": "test name"
   * },
   * {
   *   "author": "test author",
   *   "id": 6,
   *   "name": "test name"
   * },
   * {
   *   "author": "test author",
   *   "id": 7,
   *   "name": "test name"
   * },
   * ...
   * {
   *   "author": "test update author",
   *   "id": 20,
   *   "name": "test update name"
   * }
   */
  def list: Route = pathEndOrSingleSlash {
    get {
      onSuccess(repository.list) (
        complete(_))
    }
  }

/*
 * POST
 * http://localhost:8082/api/tasks
 * {
 *   "name": "test name",
 *   "author": "test author"
 * }
 * ***
 * {
 *   "id": 16,
 *   "author": "test author",
 *   "name": "test name"
 * }
 */
  def create: Route = pathEndOrSingleSlash {
    entity(as[RequestTask]) { req =>
      post {
        onSuccess(repository.create(req)) (
          complete(_))
      }
    }
  }

  /*
   * PUT
   * http://localhost:8082/api/tasks
   * {
   *   "id": 30,
   *   "name": "test update name",
   *   "author": "test update author"
   * }
   * ***
   * "1"
   */
  def update: Route = pathEndOrSingleSlash {
    entity(as[Task]) { req =>
      put {
        onSuccess(repository.update(req)) ( res =>
          complete( OpSuccess(res) ))
      }
    }
  }

/*
 * DELETE
 * http://localhost:8082/api/tasks/30
 * ***
 * {
 *    "record": 1
 * }
 */
  def remove: Route = path(IntNumber) { id =>
    delete {
      onSuccess(repository.remove(id)) ( res =>
        complete( OpSuccess(res) ))
    }
  }
}
