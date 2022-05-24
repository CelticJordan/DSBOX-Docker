# DSBOX

Editor y simulador de redes de equipos virtualizados.

# Requisitos previos

- Java SE 8
- Maven
- Instalación funcional de VirtualBox versión 5.0.x


# Puesta en marcha

1. Clonado del proyecto de GitHub`

```
git clone https://github.com/repossi/dsbox2.git
```

2. Descargar y descomprimir el SDK de VirtualBox (versión 5.0.x)`

```
wget http://download.virtualbox.org/virtualbox/5.0.20/VirtualBoxSDK-5.0.20-106931.zip
unzip VirtualBoxSDK-5.0.20-106931.zip
```

3. Registrar en el repositioro Maven local el archivo jar con el API JAX-WS

```
cd sdk/bindings/webservice/java/jax-ws

mvn install:install-file -Dfile=$PWD/vboxjws.jar \
                         -DgroupId=org.virtualbox \ 
                         -DartifactId=vboxjws \
                         -Dversion=5.0 \
                         -Dpackaging=jar \
                        -DgeneratedPom=true

```
**Nota:** En el `pom.xml` de los proyectos que usen esta API (`dsbox-core`) hay que añadir la dependencia 
```
<dependency> 
   <groupId>org.virtualbox</groupId> 
   <artifactId>vboxjws</artifactId> 
   <version>5.0</version> 
</dependency>

```

4. Compilación y empaquetado

  ```
  cd dsbox2
  mvn clean
  mvn install -DdescriptorId=jar-with-dependencies
  ```

5. Ejecución

  El paquete jar autoejecutable del interfaz gráfico de DSBOX se encuentra en `dsbox-gui/target/dsbox-gui-1.0-SNAPSHOT-jar-with-dependencies.jar`

  ```
  java -jar dsbox-gui-1.0-SNAPSHOT-jar-with-dependencies.jar
  ```

  Por defecto, se inicializará el directorio de configuración de la aplicación en `$HOME/DSBOX/` (puede especificarse otro diferente con la opción de línea de comandos `--dsbox-home`)

6. Imagen VDI preconfigurada disponible en <http://ccia.ei.uvigo.es/docencia/SSI-grado/DSBOX/default.vdi.zip>

  ```
  cd $HOME/DSBOX/IMAGES
  wget http://ccia.ei.uvigo.es/docencia/SSI-grado/DSBOX/default.vdi.zip
  unzip default.vdi.zip
  ```
