package main

import (
	"context"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"
)

func main() {
	// Create a channel to receive signals. Use a buffered channel with a size
	// of 1, see https://pkg.go.dev/os/signal#Notify.
	sigs := make(chan os.Signal, 1)
	// Instruct Go to send SIGINT and SIGTERM to this channel.
	signal.Notify(sigs, syscall.SIGINT, syscall.SIGTERM)

	// Create an HTTP server for us to terminate.
	server := http.Server{Addr: ":http"}

	// Listen for shutdown in a separate process to avoid blocking the
	// ListenAndServe call below.
	go func() {
		// Read from sigs.
		s := <-sigs
		log.Printf("Received %s", s)

		// Create a 10 second timeout for gracefully shutting down the server.
		ctx, ccl := context.WithTimeout(context.Background(), time.Second * 10)
		defer ccl()
		if err := server.Shutdown(ctx); err != nil {
			log.Printf("Shutting down gracefully: %v", err)
		}
	}()

	// Run until the server shuts down.
	log.Fatal(server.ListenAndServe())
}
