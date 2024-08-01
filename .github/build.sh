version=$(git rev-parse HEAD)
sbt compile pack
docker build -t "myapiz/microapis:$version" .
