use std::net::SocketAddr;

use axum::{routing::get, Router};

#[tokio::main]
async fn main() {
    // Create a channel to signal ctrlc
    let (tx, rx) = tokio::sync::oneshot::channel::<()>();

    tokio::spawn(async {
        tokio::signal::ctrl_c().await.unwrap();
        tx.send(()).ok();
    });

    let app = Router::new()
        // `GET /` goes to `root`
        .route("/", get(|| async { "Hello World!" }))
        // `POST /users` goes to `create_user`
        .route("/users", get(|| async { "this is users" }));

    // run our app with hyper
    // `axum::Server` is a re-export of `hyper::Server`
    let addr = SocketAddr::from(([127, 0, 0, 1], 3008));
    eprintln!("listening on {}", addr);
    axum::Server::bind(&addr)
        .serve(app.into_make_service())
        .with_graceful_shutdown(async {
            rx.await.ok();
            eprintln!("Shutting down gracefully...");
        })
        .await
        .unwrap();
}
