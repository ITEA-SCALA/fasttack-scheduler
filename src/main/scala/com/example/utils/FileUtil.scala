package com.example.utils

import com.example.config.appConfig
import java.io.{File, FileWriter}
import scala.io.Source


object FileUtil {

  val seqFile = new File(appConfig.scheduler.saveAs + ".json")

  def loadFromFile: String = {
    if (seqFile.exists()) {
      val source = Source.fromFile(seqFile)
      val data = source.mkString
      if (data.isEmpty) return ""
      source.close()
      return data
    }
    "0"
  }

  def saveToFile(item: String) {
    if (!seqFile.exists())
      seqFile.createNewFile()
    val fileWriter = new FileWriter(seqFile)
    fileWriter.write(item)
    fileWriter.close()
  }
}
