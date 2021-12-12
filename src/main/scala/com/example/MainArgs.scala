//package com.example
//
//import scala.sys.exit
//
////
//// --min-size 100 --max-size 500
////
//
///**
// * @see https://www.geeksforgeeks.org/scala-option/
// *      https://newbedev.com/best-way-to-parse-command-line-parameters
// */
//
//object MmlAlnApp {
//  val usage =
//    """
//    Usage: mmlaln [--min-size num] [--max-size num] filename
//  """
//
//  def main(args: Array[String]) {
//    if (args.length == 0) println(usage)
//    val arglist = args.toList
//    type OptionMap = Map[Symbol, Any]
////    type OptionMap = Map[String, Int]
//
//    var maxSize: String = ""
//    var minSize: String = ""
//
//    def nextOption(map: OptionMap, list: List[String]): OptionMap = {
//      def isSwitch(s: String) = (s(0) == '-')
//
//      list match {
//        case Nil => map
//        case "--max-size" :: value :: tail => {
////          println(s"--max-size = $value")
//          maxSize = value
//          nextOption(map ++ Map('maxsize -> value), tail)
//        }
//        case "--min-size" :: value :: tail => {
////          println(s"--min-size = $value")
//          minSize = value
//          nextOption(map ++ Map('minsize -> value), tail)
//        }
//        case string :: opt2 :: tail if isSwitch(opt2) => {
//          nextOption(map ++ Map('infile -> string), list.tail)
//        }
//        case string :: Nil => {
//          nextOption(map ++ Map('infile -> string), list.tail)
//        }
//        case option :: tail => println("Unknown option " + option)
//          exit(1)
//      }
//    }
//
//    val options: OptionMap = nextOption(Map(), arglist)
////    println(options)
//
//
//    println(s"--max-size = $maxSize")
//    println(s"--min-size = $minSize")
//
//
////    // Creating a Map
////    val name = Map("minsize" -> 100, "maxsize" -> 500)
////
////    // Accessing keys of the map
////    val x = name.get("minsize")
////    val y = name.get("maxsize")
////
////    // Displays Some if the key is found else None
////    println(x)
////    println(y)
////
////
////    //Accessing keys of the map
////    println(patrn(name.get("minsize")))
////    println(patrn(name.get("maxsize")))
//  }
//
//  // Using Option with Pattern matching
//  def patrn(z: Option[Int]) = z match {
//
//    // for 'Some' class the key for the given value is displayed
//    case Some(s) => (s)
//
//    // for 'None' class the below string is displayed
//    case None => ("key not found")
//  }
//
//}