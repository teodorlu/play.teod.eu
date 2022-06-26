package main

import (
	"log"
	"os"
	"os/signal"
	"syscall"
)

func main() {
	sigs := make(chan os.Signal, 1)
	signal.Notify(sigs)
	for {
		s := <-sigs
		log.Printf("Received %s", s)

		switch s {
		case syscall.SIGINT, syscall.SIGTERM:
			os.Exit(1)
		}
	}
}
