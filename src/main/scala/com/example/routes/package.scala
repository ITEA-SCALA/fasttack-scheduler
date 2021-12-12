package com.example

import com.example.data._
import spray.json.{DefaultJsonProtocol, DeserializationException, JsNumber, JsString, JsValue, JsonFormat, RootJsonFormat}
import java.sql.Timestamp


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

  object Response {
    def apply(entity: Seq[Task]): Seq[TaskDto] = entity.map(Mapper(_))
  }

  object Mapper {
    def apply(entity: Task): TaskDto = {
      TaskDto(
        DeviceInfoDto(entity.tokenRefId, entity.deviceName, entity.fixDeviceName),
        entity.foundRecords,
        entity.seqSchedulerTask,
        entity.schedulerExpression,
        entity.statusDecode,
        entity.startedAt,
        entity.finishedAt,
      )
    }
    def apply(dto: TaskDto): Task = {
      Task(
        dto.deviceInfo.tokenRefId,
        dto.deviceInfo.deviceName,
        dto.deviceInfo.fixDeviceName,
        dto.foundRecords,
        dto.seqSchedulerTask,
        dto.schedulerExpression,
        dto.statusDecode,
        dto.startedAt,
        dto.finishedAt,
      )
    }
  }

  /**
   * Json formatters
   */
  /*
   * @see https://www.programcreek.com/scala/spray.json.JsonFormat
   * @see https://queirozf.com/entries/scala-slick-dealing-with-datetime-timestamp-attributes
   *      https://stackoverflow.com/questions/8947240/convert-json-timestamp-to-normal-date-and-time-in-javascript
   */
  implicit val timestampFormat: JsonFormat[Timestamp] = new JsonFormat[Timestamp] {
    override def write(obj: Timestamp): JsValue = JsNumber(obj.getTime)
    override def read(json: JsValue): Timestamp = json match {
      case JsNumber(n) => new Timestamp(n.longValue)
    }
  }
  implicit val jsonRequestDeviceInfo = jsonFormat8(RequestDeviceInfo)
  implicit val jsonDeviceInfo        = jsonFormat8(DeviceInfo)
  implicit val jsonDeviceInfoDto     = jsonFormat3(DeviceInfoDto)
  implicit val jsonTask              = jsonFormat10(Task)
  implicit val jsonTaskDto           = jsonFormat7(TaskDto)
  implicit val jsonOpSuccess         = jsonFormat1(OpSuccess)
  implicit val jsonOpFailure         = jsonFormat1(OpFailure)
}
