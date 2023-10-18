#! /bin/bash
#将grafana中的配置文件复制出来
docker cp $1:/etc/grafana/provisioning/datasources/datasources.yaml /home/userClient/
#计算行数
line=`awk '{print NR}' /home/userClient/datasources.yaml  | tail -n1`
startLine=$(($line-2))
for i in $(seq $startLine $line)
do
    sed -i $i' s/^/#/' /home/userClient/datasources.yaml
done

sed -i '$a- {name: Prometheus, type: prometheus, access: proxy, url: \x27http://'$2':9090\x27, basicAuth: false}' /home/userClient/datasources.yaml

docker cp /home/userClient/datasources.yaml $1:/etc/grafana/provisioning/datasources/datasources.yaml
docker restart $1
rm /home/userClient/datasources.yaml
