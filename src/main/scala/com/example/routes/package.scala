package com.example

import com.example.data._
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}


package object routes extends DefaultJsonProtocol {
  /**
   * Success message
   * @param record the message
   */
  case class OpSuccess(record : Int)

  /**
   * Failure message
   * @param error the message
   */
  case class OpFailure(error : String)

  /**
   * Json formatters
   */
  implicit object JsonOsNameType extends RootJsonFormat[OsName.OsNameType] {
    def write(obj: OsName.OsNameType): JsValue = JsString(obj.toString)
    def read(json: JsValue): OsName.OsNameType = json match {
      case JsString(s) => OsName.withName(s)
      case _ => OsName.withName("")
    }
  }
  implicit val jsonRequestBook       = jsonFormat2(RequestBook)
  implicit val jsonRequestDeviceInfo = jsonFormat8(RequestDeviceInfo)
  implicit val jsonRequestTask       = jsonFormat2(RequestTask)
  implicit val jsonBook              = jsonFormat3(Book)
  implicit val jsonDeviceInfo        = jsonFormat8(DeviceInfo)
  implicit val jsonTask              = jsonFormat3(Task)
  implicit val jsonOpSuccess         = jsonFormat1(OpSuccess)
  implicit val jsonOpFailure         = jsonFormat1(OpFailure)
}
