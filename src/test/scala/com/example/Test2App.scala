package com.example

import com.example.utils.Base64UrlValidUtil

object Test2App extends App {

  val deviceName = "RXVnZW4ncyBpUGhvbmU." // Success been done reverse encode from Base64
//  val deviceName = "RXVnZW4ncyBpUGhvbmU=" // Success been done reverse encode from Base64
//  val deviceName = "RXVnZW4ncyBpUGhvbmU"  // Success been done reverse encode from Base64
//  val deviceName = "aVBob25l"             // Success been done reverse encode from Base64
//    val deviceName = "iPhone"             // Has already been done Base64 decoded before
//    val deviceName = "123"                // Has already been done Base64 decoded before
//    val deviceName = "J\u0002,l"          // Illegal base64 character
//    val deviceName = "#123"               // Illegal base64 character
//    val deviceName = "&"                  // Illegal base64 character
//    val deviceName = "."                  // Illegal base64 character
//    val deviceName = "="                  // Illegal base64 character
//    val deviceName = " "                  // Illegal base64 character
//    val deviceName = ""                   //
//    val deviceName = null                 //
//  val deviceName = "aVBob25l."            // TODO: Illegal base64 character
//  val deviceName = "aVBob25l="            // TODO: Illegal base64 character

  val (decodeAction, decodeDescription) = Base64UrlValidUtil.isDecode(deviceName)

  val fixDeviceName = Base64UrlValidUtil.decode(deviceName, decodeAction)

  println(s"deviceName:        '$deviceName';")
  println(s"fixDeviceName:     '$fixDeviceName';  decodeAction: '$decodeAction';")
  println(s"decodeDescription: '$decodeDescription';")
}
