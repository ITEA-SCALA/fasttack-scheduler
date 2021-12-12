package com.fasttack

import com.fasttack.utils.Base64UrlUtil

object Test1App extends App {
  //  val text = "This is plaintext."
  //  val bytesEncoded = java.util.Base64.getEncoder.encode(text.getBytes())
  //  println(bytesEncoded)
  //
  //
  //  val src = "J\u0002,l".getBytes
  ////  val src = "iPhone".getBytes
  ////  val src = bytesEncoded
  //  // Base64 decode
  ////  val textDecoded = new String(java.util.Base64.getDecoder.decode(bytesEncoded))
  //  try {
  //    val textDecoded = new String(java.util.Base64.getDecoder.decode(src))
  //    println(textDecoded)
  //  } catch {
  //    case _ => println(src)
  //  }

  /**
   * @see https://www.base64decode.org
   */
//  var src = "aVBob25l"             // OK ........... 'iPhone'
  var src = "RXVnZW4ncyBpUGhvbmU." // OK ........... 'Eugen's iPhone'
//  var src = "RXVnZW4ncyBpUGhvbmU=" // OK ........... 'Eugen's iPhone'
//  var src = ""                     // OK ........... ''
//  var src = "iPhone"               // NO-OK ........ '��h�'
//  var src = "123"                  // 'm' NO-OK .... '�m'
//  var src = "MY BEST PHONE"        // NO-OK ........ Illegal base64 character
//  var src = "Eugen's iPhone"       // NO-OK ........ Illegal base64 character
//  var src = "Eugen's"              // NO-OK ........ Illegal base64 character
//  var src = "Телефон #1"           // NO-OK ........ Illegal base64 character
//  var src = "#1"                   // NO-OK ........ Illegal base64 character
//  var src = "Телефон"              // NO-OK ........ Illegal base64 character
//  var src = "[B@1698539"           // NO-OK ........ '^' Illegal base64 character
//  var src = "J\u0002,l"            // NO-OK ........ '&' Illegal base64 character
//  var src = null                   // NO-OK ........ java.lang.NullPointerException


  src = src.reverse.replace(".","=").reverse
  println(s"      : $src")
  val decodeSrc = Base64UrlUtil.decode(src)
  println(s"Decode: $decodeSrc")
  val encodeSrc = Base64UrlUtil.encode(decodeSrc)
  //  val encodeSrc = Base64UrlUtil.encode(src)
  println(s"Encode: $encodeSrc")
  println(s"Action: ${src.equals(encodeSrc)}")

}
