# Usage
http://localhost:8080/service/1-10/normal/nodata


# To install manually:
sudo apt-get -y install openjdk-7-jdk git maven g++ make

## Obtain lookbusy:
wget http://www.devin.com/lookbusy/download/lookbusy-1.4.tar.gz
tar -xzf lookbusy-1.4.tar.gz
cd lookbusy-1.4
./configure
make
sudo make install

## Obtain viepepevaluation


## compile viepepevaluation
mvn install

## deploy viepepevaluation on a tomcat server

# To install using a docker container
## Build docker container
docker build -t <repo-name> .

## Run the container
docker run -it --rm -p 8080:8080 <repo-name>
