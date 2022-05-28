#!/usr/bin/env python3

import time

print("In python")

try:
    time.sleep(10)
except Exception:
    print("We're handling an exception!")
except KeyboardInterrupt:
    print("Handling keybaord interrupt")
except BaseException:
    print("Catching base exception")
except:
    print("We're swallowing any exception now!")

def annoy():
    while True:
        try:
            import time
            time.sleep(10)
        except KeyboardInterrupt:
            print("Handling keybaord interrupt")
            print("Try get out.")

# Comment this one back to trap sigint from exiting.
#
# annoy()

def annoy2(*args):
    while True:
        print("So you want to get out?")
        time.sleep(10)

# Now, register the sigterm handler too. Not just sigint (interrupt)

import signal

signal.signal(signal.SIGTERM, annoy2)
signal.signal(signal.SIGINT, annoy2)

while True:
    time.sleep(10)

# Expected behavior:
#
#   Sigint / C-c gets us into the annoy loop
#   Sigterm gets us into the annoy2 loop.
