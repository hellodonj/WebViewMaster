#!/bin/bash
if [ "$1" = "log" ] || [ "$1" = "l" ]; then
	adb logcat
elif [ "$1" = "server" ] || [ "$1" = "s" ]; then
	adb kill-server
	adb start-server
elif [ "$1" = "assembleDebug" ] || [ "$1" = "aD" ] ; then
	./gradlew assembleDebug
elif [ "$1" = "assembleRelease" ] || [ "$1" = "aR" ]; then
	./gradlew assembleRelease
elif [ "$1" = "assembleStaging" ] || [ "$1" = "aS" ]; then
	./gradlew assembleStaging
elif [ "$1" = "clean" ]; then
	./gradlew clean
elif [ "$1" = "edit" ]; then
    vim ./elearning-library/src/main/java/com/osastudio/apps/Config.java ./app/build.gradle
elif [ "$1" = "uninstall" ] || [ "$1" = "uni" ]; then
    adb uninstall com.galaxyschool.app.wawaschool
    adb uninstall com.lqwawa.internationalstudy
elif [ "$1" = "apk" ]; then
    cp -r ./app/build/outputs/apk/* ~/Desktop/
elif [ "$1" = "map" ]; then
    cp ./app/build/outputs/mapping/release/* ./app/proguard/
fi
