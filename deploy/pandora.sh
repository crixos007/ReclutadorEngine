#!/bin/bash
cd /home/$1
git clone https://rcaraveo:mUymejS_jWhAri75Ygiz@gitlab.venturessoft.com/human/desarrollo/micro/pandora/micro.git
#git checkout desarrollo
cd /home/$1/micro
mvn clean package -P $2 -DjarName=Pandora
mv /home/$1/micro/target/Pandora.jar /home/$1/PANDORA/Pandora
rm -rf /home/$1/micro
if [ "$(docker images -q pandora 2> /dev/null)" = "" ]; 
then
pwd no existe imagen activo.
else
pwd eliminado imagen activo actual:
	if [ "$(docker ps -aqf "name=pandora")" = "" ]; 
	then
	pwd no existe contenedor activo.
	else
	pwd eliminado contenedor activo actual:
	docker stop $(docker ps -aqf "name=pandora")
	docker rm $(docker ps -a -f status=exited -q)   
	fi
docker rmi pandora -f
fi
docker build -t pandora /home/$1/PANDORA/Pandora
if [ "$(docker ps -aqf "name=pandora")" = "" ]; 
then
pwd no existe contenedor activo.
else
pwd eliminado contenedor activo actual:
docker stop $(docker ps -aqf "name=pandora")
docker rm $(docker ps -a -f status=exited -q)   
fi
docker run -e "CONFIG_ENVIROMENT=env-var-conf-"$3 -e "SERVER_ENVIROMENT=http://192.168.0.62:1000/" --name pandora --mount type=bind,source=$4,target=$4 -d --restart unless-stopped -p 7001:7001 pandora