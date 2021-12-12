package com.example


package object data {

  object OsName extends Enumeration {
    type OsNameType = Value
    val NO = Value("")
    val android = Value("ANDROID")
    val windows = Value("WINDOWS")
    val tizen = Value("TIZEN")
    val ios = Value("IOS")
    val blackberry = Value("BLACKBERRY")
  }

  object DecodeAction extends Enumeration {
    type Status = Value
    val EMPTY, OK, NO, ERR = Value
  }
}
