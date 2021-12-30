# Pixel-Recorder

A [PixelFlut](https://github.com/defnull/pixelflut) canvas dumper written in Kotlin.

It connects to a running Pixelflut server and takes a one-time or periodic snapshot of the canvas. This way snapshots can be made on a event
for example (as done on rC3).

## Commandline parameters

Example:

```shell
java -jar /app/pixel-recorder.jar --host 123.456.789.123 single

docker run ghcr.io/poeschl/pixel-recorder --host 123.456.789.123 single
```

To get a programmatic argument help use `--help` as parameter.

### Mode

The mode must be one of `single` or `record`. (Default: `single`)

`Single` takes one full snapshot and exits.
`Record` will make continious snapshots every 10 seconds (period can be changes with `--period`).

### `--host <host>`

Specify the host of the targeted Pixelflut server. (Default: `localhost`)

### `-p`, `--port <port>`

Specify the port of the targeted Pixelflut server. (Default: `1234`)

### `-c`, `--connections <number>`

Specify number of used connections. (Default: `3`)

### `--period`

Sets the period in seconds for snapshots on `record` mode. (Default: `10`)

### `-d`, `--debug`

Enables the debugging log output

## Docker

The application can also be run as docker container.

To execute the docker container run `docker run ghcr.io/poeschl/pixel-recorder <commandline parameters>`. All the generated files are
created in the `/app/output` folder. To access it, create a volume mapping. A sample docker-compose is available in the repository.
