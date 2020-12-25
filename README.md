# PixelFlut-Kump

A [PixelFlut](https://github.com/defnull/pixelflut) canvas dumper written in Kotlin.

## Description

For now it makes a snapshot of the current pixelflut canvas on a remote server.

More coming soon...

## Commandline parameters

### `--host`

Specify the host of the targeted pixelflut server. (Default: `localhost`)

### `-p`, `--port`

Specify the port of the targeted pixelflut server. (Default: `1234`)

### `-c`, `--connections`

Specify number of used connections. (Default: `3`)

## Docker

The application can also be build as docker container. (Image on GitHub coming soon...)

### Build

For compiling, make sure you have a `JDK 11` installed on your machine. After that run `./gradlew jar shadowJar` in the project folder 
to build the application.
After that it can be started already as jar artifact from `/build/libs/pixelflut-kump-1.0-SNAPSHOT.jar`.

To create the docker image out of it, execute `docker build -t kump .` again in the project folder. A `kump` image will be created.

### Execute

To execute the docker container run `docker run kump <commandline parameters>`.
All the generated files are created in the `/app/output` folder. To access it, create a volume mapping.
