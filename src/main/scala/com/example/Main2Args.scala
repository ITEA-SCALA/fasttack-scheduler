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
//object MmlAln2App {
//  val usage =
//    """
//    Usage: mmlaln [--min-size num] [--max-size num] filename
//    """
//
//  def main(args: Array[String]) {
//    if (args.length == 0) println(usage)
//    type OptionMap = Map[Symbol, Any]
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
//          maxSize = value
//          nextOption(map ++ Map('maxsize -> value), tail)
//        }
//        case "--min-size" :: value :: tail =>
//          minSize = value
//          nextOption(map ++ Map('minsize -> value), tail)
//        case string :: opt2 :: tail if isSwitch(opt2) => nextOption(map ++ Map('infile -> string), list.tail)
//        case string :: Nil => nextOption(map ++ Map('infile -> string), list.tail)
//        case option :: tail => println("Unknown option " + option)
//          exit(1)
//      }
//    }
//
//    nextOption(Map(), args.toList)
//
//
//    println(s"--max-size = $maxSize")
//    println(s"--min-size = $minSize")
//
//  }
//}