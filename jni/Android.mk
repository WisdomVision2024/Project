APP_ABI := all
APP_PLATFORM := android-21
# Workaround for MIPS toolchain linker being unable to find liblog dependency
# of shared object in NDK versions at least up to r9.
#
APP_LDFLAGS := -llog