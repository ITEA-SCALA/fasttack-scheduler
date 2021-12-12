package com.fasttack.utils

import com.fasttack.data.DecodeAction

object Base64UrlValidUtil {

  def isDecode(txt: String): (Boolean, String) = {
    var decodeActionStatus: DecodeAction.Value = DecodeAction.NO

    def base64Decode(value: String): String = {
      try {
        val decode = Base64UrlUtil.decode(value)
        Base64UrlUtil.encode(decode)
      } catch {
        case _ =>
          decodeActionStatus = DecodeAction.ERR
          ""
      }
    }

    if (txt==null || (txt!=null && txt.isEmpty))
      decodeActionStatus = DecodeAction.EMPTY
    else {
      val tmpTxt    = txt.reverse.replace(".", "=").reverse
      val tmpDecode = base64Decode(tmpTxt)
      if (tmpDecode.contains(tmpTxt))
        decodeActionStatus = DecodeAction.OK
    }

    decodeActionStatus match {
      case DecodeAction.OK  => (true, "Success been done reverse encode from Base64")
      case DecodeAction.ERR => (false, "Illegal base64 character")
      case DecodeAction.NO  => (false, "Has already been done Base64 decoded before")
      case _ => (false, "")
    }
  }

  val decode: (String, Boolean) => String = (txt, action) => {
    action match {
      case true => Base64UrlUtil.decode(txt)
      case _ => txt
    }
  }
}
