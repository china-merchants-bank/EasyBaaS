#! /bin/bash
docker stop $1
if [ $? -eq 0 ]
then
    echo "SUC0000"
else
    ehco "FAIL"
fi
