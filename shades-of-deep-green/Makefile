# Why have a makefile here?
#
# We don't want any specific build logic. We just want to use the same "make"
# things as the toplevel. Yet, we wish to treat this as a subproject.
#
# So we just dispatch "up".
#
# Calling =make= simply runs make in the parent directory.

stuff:
	(cd .. && make)
