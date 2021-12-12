name         := "akka-scheduler-example"
version      := "1.1.0-SNAPSHOT"
scalaVersion := "2.12.8"

val akkaVer           = "2.5.20"
val akkaHttpVer       = "10.1.7"
val slickVer          = "3.3.0"
val postgresVer       = "42.2.5"
val h2Ver             = "1.4.192"
val akkaHttpJson4sVer = "1.25.2"
val macwireVer        = "2.4.1"
val pureConfigVer     = "0.9.0"
val akkaQuartzVer     = "1.8.5-akka-2.6.x"
val logbackClassicVer = "1.2.3"
val scalaTestVer      = "3.0.8-RC4"

libraryDependencies ++= Seq(
  "com.typesafe.akka"        %% "akka-actor"            % akkaVer,
  "com.typesafe.akka"        %% "akka-http"             % akkaHttpVer,
  "com.typesafe.akka"        %% "akka-http-spray-json"  % akkaHttpVer,
  "com.typesafe.slick"       %% "slick"                 % slickVer,
  "com.typesafe.slick"       %% "slick-hikaricp"        % slickVer,
  "org.postgresql"           %  "postgresql"            % postgresVer,
  "com.h2database"           % "h2"                     % h2Ver,
  "de.heikoseeberger"        %% "akka-http-json4s"      % akkaHttpJson4sVer,
  "com.softwaremill.macwire" %% "macros"                % macwireVer         % "provided",
  "com.github.pureconfig"    %% "pureconfig"            % pureConfigVer,
  "com.enragedginger"        %% "akka-quartz-scheduler" % akkaQuartzVer,
  "ch.qos.logback"           %  "logback-classic"       % logbackClassicVer,
  "org.scalatest"            %% "scalatest"             % scalaTestVer       % Test
)