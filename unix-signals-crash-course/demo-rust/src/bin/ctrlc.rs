use std::{thread, time::Duration};

fn main() {
    println!("Starting CtrlC example");
    let mut has_clicked_ctrlc = false;
    ctrlc::set_handler(move || {
        if has_clicked_ctrlc {
            println!("Exiting...");
            std::process::exit(0);
        }

        has_clicked_ctrlc = true;
        println!("Ctrl-C detected, press it once more to exit!");
        // process::exit(1);
    })
    .expect("Error setting Ctrl-C handler");

    thread::sleep(Duration::from_secs(3600));
}
