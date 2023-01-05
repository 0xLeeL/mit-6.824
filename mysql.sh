docker run --name mysql -v /home/lee/software/mysql/conf:/etc/mysql/conf.d  -v /home/lee/software/mysql/data:/var/lib/mysql  -e MYSQL_ROOT_PASSWORD=666666 MYSQL_USER=root  -d mysql:latest
