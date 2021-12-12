#!/bin/bash
java -jar fasttack-scheduler-1.0.0-SNAPSHOT.jar --application-port 8081 --postgre-server-name 192.168.1.115 --postgre-database-name ft_al --postgre-user ft --postgre-password ft --max-per-task 10 >/dev/null 2>&1 &
