# Pixel-Kump

A [PixelFlut](https://github.com/defnull/pixelflut) canvas dumper written in Kotlin.

## Description

For now it makes a snapshot of the current pixelflut canvas on a remote server.

More coming soon...

## Commandline parameters

### `--single`, `--single-snapshot`

Make one snapshot and exit the application.

### `--host <host>`

Specify the host of the targeted pixelflut server. (Default: `localhost`)

### `-p`, `--port <port>`

Specify the port of the targeted pixelflut server. (Default: `1234`)

### `-c`, `--connections <number>`

Specify number of used connections. (Default: `3`)

## Docker

The application can also be run as docker container.

To execute the docker container run `docker run ghcr.io/poeschl/pixel-kump <commandline parameters>`.
All the generated files are created in the `/app/output` folder. To access it, create a volume mapping.
