@echo off 
echo "start iautos spider"
java -Xms512m -Xmx1024m -Djava.ext.dirs=iautos_lib -Dlogback.configurationFile=logback.xml pp.corleone.service.iautos.IautosService
pause