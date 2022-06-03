use std::time::Duration;

#[tokio::main]
async fn main() {
    println!("Starting Tokio example");
    tokio::spawn(async {
        tokio::signal::ctrl_c().await.unwrap();
        println!("Ctrl-C detected, press it once more to exit!");
        tokio::signal::ctrl_c().await.unwrap();

        println!("Exiting...");
        std::process::exit(0);
    });

    tokio::time::sleep(Duration::from_secs(3600)).await;
}
