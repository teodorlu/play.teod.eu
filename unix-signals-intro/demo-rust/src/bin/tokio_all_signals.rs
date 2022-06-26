use std::{process, time::Duration};

use tokio::signal::unix::SignalKind;

#[tokio::main]
async fn main() {
    println!("My PID is {}", nix::unistd::getpid());

    tokio::spawn(async {
        let mut signals = tokio::signal::unix::signal(SignalKind::interrupt()).unwrap();

        signals.recv().await;
        eprintln!("Got SIGINT (one more to exit)");
        signals.recv().await;
        eprintln!("Got SIGINT (exiting)");
        process::exit(0);
    });

    tokio::spawn(async {
        let mut signals = tokio::signal::unix::signal(SignalKind::hangup()).unwrap();
        loop {
            signals.recv().await;
            eprintln!("Got SIGHUP");
        }
    });

    tokio::spawn(async {
        let mut signals = tokio::signal::unix::signal(SignalKind::terminate()).unwrap();
        loop {
            signals.recv().await;
            eprintln!("Got SIGTERM");
        }
    });

    tokio::spawn(async {
        let mut signals = tokio::signal::unix::signal(SignalKind::user_defined1()).unwrap();
        loop {
            signals.recv().await;
            eprintln!("Got SIGUSR1");
        }
    });

    tokio::spawn(async {
        let mut signals = tokio::signal::unix::signal(SignalKind::user_defined2()).unwrap();
        loop {
            signals.recv().await;
            eprintln!("Got SIGUSR2");
        }
    });

    tokio::time::sleep(Duration::from_secs(3600)).await;
}
