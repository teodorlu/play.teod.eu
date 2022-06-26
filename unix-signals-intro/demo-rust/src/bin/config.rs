use std::time::Duration;

use tokio::signal::unix::SignalKind;

#[tokio::main]
async fn main() {
    println!("My PID is {}", nix::unistd::getpid());

    let (config_tx, config_rx) = tokio::sync::watch::channel(read_config().await);

    tokio::spawn(async move {
        let mut sighup = tokio::signal::unix::signal(SignalKind::hangup()).unwrap();

        loop {
            sighup.recv().await;
            eprintln!("Reloading config...");
            config_tx.send(read_config().await).unwrap();
        }
    });

    // Imagine this is some complicated long-running daemon
    loop {
        let config = config_rx.borrow();
        println!("My current config is: {:?}", &*config);
        tokio::time::sleep(Duration::from_secs(2)).await;
    }
}

async fn read_config() -> String {
    tokio::fs::read_to_string("config_file.txt").await.unwrap()
}
