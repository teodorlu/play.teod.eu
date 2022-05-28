#!/usr/bin/env python3

import time

print("Python has started!")

################################################################################
#
# Here's how we can handle a single interrupt

try:
    time.sleep(10)
except Exception:
    print("We're handling an exception!")
except KeyboardInterrupt:
    print(" Handling keybaord interrupt")

try:
    time.sleep(10)
except BaseException:
    print(" Base exceptions include keyboard interrupts.")

try:
    time.sleep(10)
except:
    print(" Or we can use an empty except - that catches anything.")
    print("   Tired of trying to exit soon?")

################################################################################
#
# If we want to be really mean, we can prevent the user from ever exiting from a
# keyboard interrupt.
#
# comment in annoy() below.

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


################################################################################
#
# And if we want to be REALLY mean, we can prevent the user from ever exiting
# with either sigterm or sigint.
#
# comment in annoy2() to activate.

def exit_hook(*args):
    while True:
        print("So you want to get out?")
        time.sleep(10)

# Now, register the sigterm handler too. Not just sigint (interrupt)

def annoy2():
    import signal

    signal.signal(signal.SIGTERM, exit_hook)
    signal.signal(signal.SIGINT, exit_hook)

    while True:
        time.sleep(10)

# annoy2()

# Expected behavior:
#
#   Sigint / C-c gets us into the annoy loop
#   Sigterm gets us into the annoy2 loop.


try:
    time.sleep(10)
except KeyboardInterrupt:
    print(" Are you really sure you want to unsubscribe?")

try:
    time.sleep(10)
except KeyboardInterrupt:
    print(" Absolutely sure? You can have a discount. 20 % off your next Unix signal.")

try:
    time.sleep(10)
except KeyboardInterrupt:
    print(" I give up. Have it your way.")
