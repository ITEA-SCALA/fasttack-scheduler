package com.fasttack.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Route
import com.fasttack.data._
import com.fasttack.repository._


class DeviceInfoTaskRoute(repository: TaskRepository) extends SprayJsonSupport {
  val route: Route = pathPrefix("device-info-tasks") {
    /*
     * GET
     * http://127.0.0.1:8082/fasttack-scheduler-json/device-info-tasks/30
     * ***
     * {
     *   "author": "test update author",
     *   "id": 32,
     *   "name": "test update name"
     * }
     */
    path(IntNumber) { id =>
      get {
        onSuccess(repository.find(id)) (
          complete(_))
      }
    } ~
    /*
     * GET
     * http://127.0.0.1:8082/fasttack-scheduler-json/device-info-tasks
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
    pathEndOrSingleSlash {
      get {
        onSuccess(repository.list) ( res =>
          complete( Response(res) ))
      }
    } ~
    /*
     * http://127.0.0.1:8082/fasttack-scheduler-json/device-info-tasks/filter?deviceName=author_19r&tokenRefId=name_19
     * http://127.0.0.1:8082/fasttack-scheduler-json/device-info-tasks/filter?tokenRefId=name_19&deviceName=test update author
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
    path("filter") {
      parameters('deviceName.as[String].?, 'tokenRefId.as[String].?) { (deviceName, tokenRefId) =>
        get {
          onSuccess(repository.filter(deviceName, tokenRefId)) (
            complete(_))
        }
      }
    } ~
    /*
     * POST
     * http://127.0.0.1:8082/scheduler/device-info-tasks
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
    pathEndOrSingleSlash {
      entity(as[TaskDto]) { dto =>
        post {
          onSuccess(repository.create( Mapper(dto) )) (
            complete(_))
        }
      }
    } ~
    /*
     * PUT
     * http://127.0.0.1:8082/scheduler/device-info-tasks
     * {
     *   "id": 30,
     *   "name": "test update name",
     *   "author": "test update author"
     * }
     * ***
     * "1"
     */
    pathEndOrSingleSlash {
      entity(as[Task]) { req =>
        put {
          onSuccess(repository.update(req)) ( res =>
            complete( OpSuccess(res) ))
        }
      }
    } ~
    /*
     * DELETE
     * http://127.0.0.1:8082/scheduler/device-info-tasks/30
     * ***
     * {
     *    "record": 1
     * }
     */
    path(IntNumber) { id =>
      delete {
        onSuccess(repository.remove(id)) ( res =>
          complete( OpSuccess(res) ))
      }
    }
  }
}
