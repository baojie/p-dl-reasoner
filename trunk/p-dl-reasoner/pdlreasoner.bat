@echo off
set PDLBASE=D:\Workspace\p-dl-reasoner
set CLASSPATH=.;%PDLBASE%\lib\jgrapht\jgrapht-jdk1.6.jar;%PDLBASE%\lib\log4j\log4j-1.2.15.jar;%PDLBASE%\lib\jgroups\jgroups-all.jar;%PDLBASE%\lib\jgroups\commons-logging.jar;%PDLBASE%\lib\jgroups\log4j.jar;%PDLBASE%\lib\commons-lang\commons-lang-2.4.jar;%PDLBASE%\lib\owlapi\owlapi-bin.jar
D:
cd %PDLBASE%\bin
java edu.iastate.pdlreasoner.PDLReasoner %1 %2 %3 %4 %5 %6 %7 %8 %9
cd ..