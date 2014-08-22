#!/bin/sh

echo "\n\n"
echo "*******************************************************************************************"
 echo "Apache installation"      
echo "*******************************************************************************************\n\n"

  echo "apache installation"
   apachelink=/etc/apache2
 if [ -d "$apachelink" ]; then
       apachectl -v
    echo "Apache Installed \n\n"
      else
        echo "not installed" 
        sudo apt-get install apache2   
fi

echo "*******************************************************************************************"
echo      " Mysql installation"      
echo "*******************************************************************************************\n\n"


 echo "mysql installation"
    mysqllink=/etc/mysql         
    if [ -d "$mysqllink" ]; then
    echo "installed"
       sudo /etc/init.d/mysql start
     mysqladmin -u root -pgooru123 status
     mysqladmin -u root -pgooru123 version
     echo "Mysql Installed \n\n"

else 
    echo "Not installed"
   sudo apt-get install mysql-server && sudo apt-get install mysql-client
      sudo /etc/init.d/mysql start

fi

echo "*******************************************************************************************"
 echo "Java installation"      
echo "*******************************************************************************************\n\n"
        
    echo "java installation"
           javalink=`which java`
        if [ -f $javalink ]; then
       java -version
      echo "Java Installed \n\n"

else
        echo "not installed"    
        sudo apt-get install openjdk-7-jdk
fi

echo "*******************************************************************************************"
 echo "Maven Installation"  
echo "*******************************************************************************************\n\n" 

echo "maven installation "
mavenlink=/etc/maven        
 if [ -d "$mavenlink" ]; then
        mvn -v
echo "Maven Installed \n\n"


else
        echo "not installed"    
        sudo apt-get install maven
fi

echo "*********************************************************************************************"
echo "Redis Installation"      
echo "*********************************************************************************************\n\n"

          echo "redis installation"
redislink=/etc/redis       
 if [ -d "$redislink"  ]; then
        redis-server --version

echo "Redis Installed \n\n"

else
        
        echo "not installed"  
       sudo apt-get install redis-server   
fi

echo "**********************************************************************************************"
echo "Tomcat Installation"      
echo "**********************************************************************************************\n\n"

echo "tomcat installation"
   
tomcat_pid() {
  echo `ps aux | grep org.apache.catalina.startup.Bootstrap | grep -v grep | awk '{ print $2 }'`
}

  pid=$(tomcat_pid)
tomcat=/usr/local/tomcat7
  if [  -d $tomcat ]; then
    echo "status pid:$pid"
    kill -9 $pid
    echo "tomcat start after killed "    

   /usr/local/tomcat7/bin/startup.sh
 echo "Tomcat Installed \n\n"
else
echo "not installed"
sudo ln -s  /opt/apache-tomcat-7.0.54   /usr/local/tomcat7

fi

echo "************************************************************************************************"
echo "Cassandra Installation"
echo "************************************************************************************************\n\n"

echo "cassandra installation" 
  cassandra=/usr/local/cassandra 
  if [ -d $cassandra ]; then
   
   echo "start cassadra" 
    
   echo "Cassandra Installed \n\n"
   else
   ln -s  /opt/apache-cassandra-1.2.8  /usr/local/cassandra
     /usr/local/cassandra/bin/cassandra
fi
echo "*********************************************************************************************************"
echo "username"
read user
echo "ip_addr"
read ip_addr

if [ -f  $user@$ip_addr:~/m2.tar.gz  ]; then

scp -r $user@$ip_addr:~/m2.tar.gz  ~/
tar zxvf ~/m2.tar.gz

else

ssh $user@$ip_addr  tar -cvf ~/m2.tar.gz  ~/.m2
scp -r $user@$ip_addr:~/m2.tar.gz  ~/
tar zxvf ~/m2.tar.gz
fi
echo "***********************************************************************************************************"


echo "*************************************************************************************************"
echo "Mysql dump Import to Database"
echo "*************************************************************************************************\n\n"   

mysql -u root -pgooru123 -e "CREATE DATABASE Gooru_local;"
echo "Database Created"
echo "dump name"
read dump
scp -r $user@$ip_addr:~/$dump  ~/

mysql -u root -pgooru123  Gooru_local  < ~/$dump
echo "Dump created \n\n"

echo "**************************************************************************************************"
echo "Set Environment"
echo "**************************************************************************************************"

echo "*************************************************************************************"
echo "/etc/profile"
echo "*************************************************************************************"
echo "export CATALINA_HOME=/usr/local/tomcat7 \n export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64 \n export JAVA_OPTS=".Djav::i:a.awt.headless=true -Dfile.encoding=UTF-8 -server -Xms1072m -Xmx1072m -XX:NewSize=256m -XX:MaxNewSize=512m -XX:PermSize=256m -XX:MaxPermSize=512m -XX:+DisableExplicitGC"  " >> /etc/profile
echo "************************************************************************************"

echo "************************************************************************************"
echo "etc/apache2/sites-enabled/000-default.txt"
echo "************************************************************************************"

sed -i '/access.log combined/a # Proxy requests to /gooru and /gooruapi to tomcat using AJP \n  ProxyRequests Off \n  ProxyPreserveHost On \n  ProxyPass /classic ajp://localhost:8009/classic imin=0 smax=20 max=25 ttl=120 timeout=120 \n  ProxyPass /gooruapi ajp://localhost:8009/gooruapi min=0 smax=20 max=25 ttl=120 timeout=120 \n   ProxyPass /gooruv2api ajp://localhost:8009/gooruv2api min=0 smax=20 max=25 ttl=120 timeout=120 \n  ProxyPass /crawler ajp://localhost:8009/crawler min=0 smax=20 max=25 ttl=120 timeout=120 \n ProxyPass /migrationapi ajp://localhost:8009/migrationapi min=0 smax=20 max=25 ttl=120 timeout=120 \n  ProxyPass /auth-sso ajp://localhost:8009/auth-sso min=0 smax=20 max=25 ttl=120 timeout=120 \n  ProxyPass /messaging-app ajp://localhost:8009/messaging-app min=0 smax=20 max=25 ttl=120 timeout=120 \n  ProxyPass /gooru-migration ajp://localhost:8009/gooru-migration min=0 smax=20 max=25 ttl=120 timeout=120 \n  ProxyPass /gmarketplace ajp://localhost:8009/gmarketplace min=0 smax=20 max=25 ttl=120 timeout=120 \n   ProxyPass /gooru-messaging ajp://localhost:8009/gooru-messaging min=0 smax=20 max=25 ttl=120 timeout=120 \n  ProxyPass /gooru-search ajp://localhost:8009/gooru-search min=0 smax=20 max=25 ttl=120 timeout=120 \n # enable expirations \n ExpiresActive On \n # expire GIF images after a month in the clients cache \n ExpiresByType image/gif A2592000 \n ExpiresByType image/png A2592000 \n ExpiresByType text/css  A2592000 \n Options +FollowSymlinks \n RewriteEngine on \n RewriteRule (.*)(-g[0-9a-zA-Z]+)(\.template)(.*) \n RewriteRule (.*)(-g[0-9a-zA-Z]+)(\.png)(.*) \n RewriteRule (.*)(-g[0-9a-zA-Z]+)(\.jpg)(.*i)' /etc/apache2/sites-enabled/000-default.conf



echo "***********************************************************"
echo "/etc/hosts"
echo "***********************************************************"

sed -i '/ localhost/a 127.0.0.1 gooru.goorulearning.com' /etc/hosts



echo "***********************************************************"
echo "var/www/filter-local.properties"
echo "***********************************************************"


echo " #Gooru Portal 
gooru.home = http://gooru.goorulearning.com
gooru.domain=gooru.goorulearning.com
gooru.googleapps.login=http://gooru.goorulearning.com/gmarketplace/sakai/auth/?from=google&amp;domain=goorudemo.org

#REST API Endpoint
gooru.services.endpoint = goorulocal/rest
gooru.search.endpoint=http://gooru.goorulearning.com/gooru-search/rest

config-setting.profile=default-profile

#Content S3 Configuration
s3.accessKey=[replacekey]
s3.secureKey=[replacekey]
s3.gooruBucket=profile-demo

email.templates.path=/home/gooruapp/tomcat7/webapps/gooruapi/emailTemplate/

redis.server.host=localhost

 " >> /var/www/filter-local.properties

echo "*************************************************************************************************************"
echo " /usr/local/tomcat7/conf/context.xml"
echo "***************************************************************************************************************"

sed -i '/<Context>/a  <Resource name="jdbc/gooruapi" \n  auth="Container" \n  type="com.mchange.v2.c3p0.ComboPooledDataSource" \n  description="DB Connection"  \n  jdbcUrl="jdbc:mysql://localhost:3306/Gooru_local?autoReconnect=true" \n  driverClass="com.mysql.jdbc.Driver" \n  user="root" \n  password="gooru123" \n maxPoolSize="100" \n  minPoolSize="5" \n  initialPoolSize="1" \n  acquireIncrement="1" maxIdleTime="15"  idleConnectionTestPeriod="5"  testConnectionOnCheckout="true" \n  factory="org.apache.naming.factory.BeanFactory" \n /> \n <Environment name="configSettings" value="/var/www/filter-local.properties" type="java.lang.String" override="false"/>' /usr/local/tomcat7/conf/context.xml

echo "*************************************************************************************************************************"
echo "mime.conf"
echo "*************************************************************************************************************************"

sed -i '/AddOutputFilter INCLUDES .shtml/a AddType text/html .g' /etc/apache2/mods-enabled/mime.conf


echo "#!/bin/sh \n kill -9 `pgrep -f tomcat` \n mvn clean install -P api -Dmaven.test.skip=true  \n /usr/local/tomcat7/bin/startup.sh \n tail -f /usr/local/tomcat7/logs/* /usr/local/tomcat7/gooru_logs/* " >> api_build.sh
 sh api_build.sh



