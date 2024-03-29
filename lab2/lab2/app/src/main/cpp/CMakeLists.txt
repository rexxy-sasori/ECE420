cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -Wall -Werror")

add_library(lab2 SHARED
            audio_common.cpp
            audio_main.cpp
            audio_player.cpp
            audio_recorder.cpp
            debug_utils.cpp
            ece420_main.cpp)

# include libraries needed for lab2 lib
target_link_libraries(lab2
                      android
                      atomic
                      log
                      OpenSLES)

# Android Studio 2.2.0 with CMake support does not pack stl shared libraries,
# so app needs to pack the right shared lib into APK. This sample uses solution
# from https://github.com/jomof/ndk-stl to find the right stl shared lib to use
# and copy it to the right place for Android Studio to pack
# Usage: download ndk-stl-config.cmake into app's directory hosting CMakeLists.txt
#        and just use it with the following line
include(ndk-stl-config.cmake)
