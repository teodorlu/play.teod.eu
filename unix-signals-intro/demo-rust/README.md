# Signal handling in Rust

We use one of multiple libraries (crates) to handle signals in Rust. It's not (yet?) a part of the
standard library. Probably because how to handle signals will change depending on stuff like if your
app is async. Those libraries use low level libc-stuff that we won't look into.

## The code

The different examples is in `src/bin/name.rs`. Run the example with `cargo run --bin name`

## Simple and cross-platform

`ctrlc` is a nice package to just handle Ctrl-C in a cross platform way. On Unix, this means
handling SigInt.

```
cargo run --bin ctrlc
```

## Handling with tokio

When using tokio for async (tokio is a "framework" for doing async programming in Rust), we can use
tokio's own signal handling.

```
cargo run --bin tokio
```

## Handling with a http server using channels

It's possible to signal other parts of the application to shutdown. This example uses axum as the
http-server.

```
cargo run --bin http_channels
```

## Reloading config on SIGHUP

A signal-pattern is reloading the config when receiving a SIGHUP. This is impleented in config.rs

```
cargo run --bin config
```

To reload the config, look at the PID printed by the config, and then run:

```
kill -HUP $PID
```

## Lower level handling of all unix signals using tokio

It's possible to listen to all signals, not just sigint. However that is not cross-platform and will
only work on Unixes like Linux and Mac OS.

```
cargo run --bin tokio_all_signals
```
