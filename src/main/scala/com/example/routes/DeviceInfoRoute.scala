package com.example.routes

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.example.data._
import com.example.repository._


class DeviceInfoRoute(repository: DeviceInfoRepository) extends SprayJsonSupport {
  val route: Route = pathPrefix("devices") {
    list ~
    filter ~
    create ~
    update ~
    remove
  }

  /*
   * http://localhost:8082/api/devices/DNITHE000302000000000000
   * http://localhost:8082/api/devices/filter?deviceType=MOBILE_PHONE
   * http://localhost:8082/api/devices/filter?deviceName=MY BEST PHONE&deviceType=MOBILE_PHONE
   * ***
   * [
   *   {
	 *     "tokenRefId": "DNITHE000302000000000000",
	 *     "deviceName": "samsung - SM-J500FN",
	 *     "serialNumber": "",
	 *     "osName": "ANDROID",
	 *     "osVersion": "5.1.1",
	 *     "imei": "",
	 *     "storageTechnology": "SOFTWARE",
	 *     "deviceType": "MOBILEPHONE_OR_TABLET"
   *   },
   *   ...
   *   {
   *     "deviceName": "MY BEST PHONE",
   *     "deviceType": "MOBILE_PHONE",
   *     "imei": "",
   *     "osName": "",
   *     "osVersion": "8.1",
   *     "serialNumber": "",
   *     "storageTechnology": "TRUSTED_EXECUTION_ENVIRONMENT",
   *     "tokenRefId": "DNITHE000302000000000555"
   *   },
   *   {
   *     "deviceName": "Телефон #1",
   *     "deviceType": "MOBILE_PHONE",
   *     "imei": "04312E7B342C80014328036811932950DA075B1C4DC45F7C",
   *     "osName": "ANDROID",
   *     "osVersion": "8.1",
   *     "serialNumber": "",
   *     "storageTechnology": "TRUSTED_EXECUTION_ENVIRONMENT",
   *     "tokenRefId": "DNITHE000302000000000005"
   *   }
   * ]
   */
  def filter: Route = path("filter") {
    parameters('tokenRefId.as[String].?, 'deviceName.as[String].?, 'deviceType.as[String].?) { (tokenRefId, deviceName, deviceType) =>
      get {
        onSuccess(repository.filter(tokenRefId, deviceName, deviceType)) (
          complete(_))
      }
    }
  }

  /*
   * GET
   * http://localhost:8082/api/devices
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
 * http://localhost:8082/api/devices
 * {
	*  "tokenRefId": "DNITHE000302000000000000",
	*  "deviceName": "samsung - SM-J500FN",
	*  "serialNumber": "",
	*  "osName": "ANDROID",
	*  "osVersion": "5.1.1",
	*  "imei": "",
	*  "storageTechnology": "SOFTWARE",
	*  "deviceType": "MOBILEPHONE_OR_TABLET"
 * }
 * ***
 * {
	*  "tokenRefId": "DNITHE000302000000000000",
	*  "deviceName": "samsung - SM-J500FN",
	*  "serialNumber": "",
	*  "osName": "ANDROID",
	*  "osVersion": "5.1.1",
	*  "imei": "",
	*  "storageTechnology": "SOFTWARE",
	*  "deviceType": "MOBILEPHONE_OR_TABLET"
 * }
 */
  def create: Route = pathEndOrSingleSlash {
    entity(as[RequestDeviceInfo]) { req =>
      post {
        onSuccess(repository.create(req)) (
          complete(_))
      }
    }
  }

  /*
   * PUT
   * http://localhost:8082/api/books
   * {
   *   "id": 30,
   *   "name": "test update name",
   *   "author": "test update author"
   * }
   * ***
   * "1"
   */
  def update: Route = pathEndOrSingleSlash {
    entity(as[DeviceInfo]) { req =>
      put {
        onSuccess(repository.update(req)) ( res =>
          complete( OpSuccess(res) ))
      }
    }
  }

/*
 * DELETE
 * http://localhost:8082/api/books/30
 * ***
 * {
 *    "record": 1
 * }
 */
  def remove: Route = path(Segment) { tokenRefId =>
    delete {
      onSuccess(repository.remove(tokenRefId)) ( res =>
        complete( OpSuccess(res) ))
    }
  }
}
