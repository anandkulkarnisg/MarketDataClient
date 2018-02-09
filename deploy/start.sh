#!/bin/bash

backupcwd=$PWD
cwd=$PWD
if [ $cwd != "/home/anand/git/MarketDataClient/deploy" ]; then
	cd /home/anand/git/MarketDataClient/deploy
	cwd=$PWD
fi

nohup ~/runq5000 $cwd/kdb/$1start.q &
nohup java -jar $cwd/MarketDataClient.jar $1 &
cd $backupcwd
