cmake_minimum_required(VERSION 3.4.1)

set(pathOPENCV C:/OpenCV-android-sdk)
set(pathPROJECT C:/Users/kc/AndroidStudioProjects/Cow)
set(pathLIBOPENCV_JAVA ${pathPROJECT}/app/src/main/JniLibs/${ANDROID_ABI}/libopencv_java3.so)

set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

include_directories(${pathOPENCV}/sdk/native/jni/include)

add_library(native-lib SHARED
            native-lib.cpp )

add_library(lib_opencv SHARED IMPORTED )

set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${pathLIBOPENCV_JAVA})

find_library(android-lib android)

target_link_libraries(native-lib
                      GLESv1_CM
                      lib_opencv
                      log
                       ${android-lib})