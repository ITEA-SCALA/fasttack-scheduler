# Akka Quartz Scheduler Example

* `Repo`: https://github.com/tarangbhalodia/akka-scheduler-example

Простой пример расписания Akka Actor с помощью [akka-quartz-scheduler](https://github.com/enragedginger/akka-quartz-scheduler)

Решение вопроса StackOverflow: [How to implement time-based scheduled jobs like run at 12 AM in Play Framework 2.6?](https://stackoverflow.com/questions/53538199/how-to-implement-time-based-scheduled-jobs-like-run-at-12-am-in-play-framework-2)


Reference: https://blog.knoldus.com/code-dissection-akka-quartz-scheduler-scalas-way-of-scheduling/



# Как можно планировать задачи с помощью Akka Scheduler (планировщик Akka-Quartz).

* `Tutorial`: https://www.baeldung.com/scala/akka-scheduler
* `Repo`: https://github.com/enragedginger/akka-quartz-scheduler
* `Cron expression generator - Quartz`: https://www.freeformatter.com/cron-expression-generator-quartz.html


# Scala Akka

This module contains articles about the Akka library in Scala.

* https://github.com/Baeldung/scala-tutorials/tree/master/scala-akka

### Scala Tutorial: Create CRUD with Slick and MySQL

* https://codequs.com/p/B1IogRLY
* https://github.com/dwickern/slick-examples
* https://scalatra.org/guides/2.4/persistence/slick.html


1. Есть испорченная таблица на боевой базе данных `DeviceInfo` (`Book`) 
2. Есть шедулер, который в установленное время в `application.conf`, будет выполнять проверку таблицы `DeviceInfo` а результаты попыток-проверки будут сохраняться во временную базу приложения `H2`, в таблицу `Task`
3. Информацию о выполненных попыток-проверки можно будет прочитать из базы `H2` через `REST` роутеры
4. Из сервиса `TaskService` будут выполняться транзакции на боевую базу в таблицу `DeviceInfo` - с целью проверки состояния, а результаты данных-проверки будут сохраняться в темповую таблицу на боевой базе данных (для последующего исправления данных...)


---


* **(** `Alt` + `Ctrl` + `Shift` + `установить позицию курсора` **)** параллельно редактировать несколько строк одновременно  
* **(** `Ctrl` + `Alt` + `Shift` + `+` **)** подсветить implicit параметры
* **(** `Ctrl` + `Alt` + `Shift` + `-` **)** скрыть implicit параметры