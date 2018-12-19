#!/bin/bash
if [ "$1" = "log" ] || [ "$1" = "l" ]; then
	adb logcat
elif [ "$1" = "server" ] || [ "$1" = "s" ]; then
	adb kill-server
	adb start-server
elif [ "$1" = "main" ] || [ "$1" = "dev" ]; then
    echo -e "\n===>>>update lqwawa"
    if [ "$1" = "main" ]; then
        git checkout wawa3-note
        git pull origin wawa3-note
    else
        git checkout wawa3-note-dev
        git pull origin wawa3-note-dev
    fi

    echo -e "\n===>>>update ecourse-maker"
    cd ../ecourse-maker
    if [ "$1" = "main" ]; then
        git checkout ecourse_phone
        git pull origin ecourse_phone
    else
        git checkout ecourse_phone_dev
        git pull origin ecourse_phone_dev
    fi

    echo -e "\n===>>>update intl-education"
    cd ../intl-education
    if [ "$1" = "main" ]; then
        git checkout lib-for-lqwawa
        git pull origin lib-for-lqwawa
    else
        git checkout lib-for-lqwawa-dev
        git pull origin lib-for-lqwawa-dev
    fi
            
	echo -e "\n===>>>update media-paper"
	cd ../media-paper
	git checkout media-paper-lqwawa
	git pull origin media-paper-lqwawa

    echo -e "\n===>>>update onekey-share-lib"
    cd ../onekey-share-lib
       git checkout umeng-share
       git pull origin umeng-share

	echo -e "\n===>>>update elearning-library"
	cd ../elearning-library
	if [ "$1" = "main" ]; then
	    git checkout multidex-common-downloader-pensdk1219
	    git pull origin multidex-common-downloader-pensdk1219
    else
	    git checkout multidex-common-downloader-pensdk1219-dev
	    git pull origin multidex-common-downloader-pensdk1219-dev
    fi

	echo -e "\n===>>>update video-recorder"
	cd ../video-recorder
	git checkout library
	git pull origin library

	echo -e "\n===>>>update robot-pen"
	cd ../robot-pen
	git checkout pensdk1219-not-disconnect
	git pull origin pensdk1219-not-disconnect

	echo -e "\n===>>>update scanner-lib"
	cd ../scanner-lib
	git checkout zxing-ar
	git pull origin zxing-ar

	echo -e "\n===>>>update letv-sdk"
	cd ../letv-sdk
	git checkout master
	git pull origin master

	echo -e "\n===>>>update lqbase-lib"
	cd ../lqbase-lib
	git checkout master
	git pull origin master
elif [ "$1" = "assembleDebug" ] || [ "$1" = "aD" ] ; then
	./gradlew assembleDebug
elif [ "$1" = "assembleRelease" ] || [ "$1" = "aR" ]; then
	./gradlew assembleRelease
elif [ "$1" = "assembleStaging" ] || [ "$1" = "aS" ]; then
	./gradlew assembleStaging
elif [ "$1" = "clean" ]; then
	./gradlew clean
elif [ "$1" = "edit" ]; then
    vim ./elearning-library/src/com/osastudio/apps/Config.java ./app/build.gradle
elif [ "$1" = "uninstall" ] || [ "$1" = "uni" ]; then
    adb uninstall com.galaxyschool.app.wawaschool
    adb uninstall com.lqwawa.internationalstudy
elif [ "$1" = "apk" ]; then
    cp ./build/outputs/apk/* ~/Desktop/
    rm ~/Desktop/*unaligned.apk
    rm ~/Desktop/*debug.apk
elif [ "$1" = "map" ]; then
    cp ./build/outputs/mapping/release/* ./proguard/
fi
