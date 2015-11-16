# beep.brake
Senior Project for RITs Forward Collision Application

<Add description here>

# Downloads and setup


To run and / or develop this application you will need to download

1.  [Android Studio (_AS_)](https://developer.android.com/sdk/index.html#) or [Intellij IDEA (_II_)](https://www.jetbrains.com/idea/download/)
2. [Java Development Kit 7 (JDK7)](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)
3. [SDK Manager](http://developer.android.com/tools/help/sdk-manager.html)
  - _Note:_ This is packaged with _AS_. It can be accessed through
  
    ``Settings -> Appearance & Behavior -> System Settings -> Android SDK``
  - Download Android 4.0 (_android-14_)
    - Click 'Obsolete' located at the bottom left side of the manager to have it appear.
  - DownloadAndroid 4.4.2 (_android-19_)
    - This is readily visible
  - Download Build-tools 19.1
    - This is readily visible under 'Tools'.
4. [NDK](http://developer.android.com/ndk/downloads/index.html)
  - If you are not using _AS_, use this link to download the NDK
  - If you are using _AS_, go through **Project Structure** _->_ **SDK Location** _->_ **Android NDK Location**
    you will see a 'Download' button that will automatically download and unzip the NDK to the default location.

If you are not using _AS_, you will need to go to the **local.properties** file located directly under the project and add in the path to the NDK as such. If that file does not contain the path to the sdk, be sure to add it

```
sdk.dir=C\:\\path\\to\\sdk
ndk.dir=C\:\\path\\to\\ndk
```

At this point, you should be set to run this application using an android 4.4.2 image or above!

##More Information

(Addressing size concern)
The application includes the [OpenCV 3.00 Android](http://opencv.org/downloads.html) sdk\java module and sdk\native folder underneath ``opencv_java3``. This allows for an easier project setup but causes it to be a much larger project.


##Creating Native Files

To increase the workflow of creating native files, you will create an external tool.

``Settings -> Tools -> External Tools -> +``

**Name:** javah

**Group:** Android Tools (You type this in)
  
**Description:** Android Tool -JDK javah tool

Check each checkbox under **Options** & **Show in**

**Tool settings:**
- Program: ``C\:path\to\jdk\bin\javah``
  - For windows, add ``.exe`` to the end
- Parameters: ``-v -jni -d $ModuleFileDir$/src/main/jni $FileClass$``
- Working directory: ``$SourcepathEntry$``

Now simply select the ``.java`` file that contains the abstracted class that is to be implemented in C or C++ code -> right click -> Android Tools -> javah

This will generate the header file under the ``jni`` folder.

## Resources
* [Hujiaweibujidao GitHub](http://hujiaweibujidao.github.io/blog/2014/10/22/android-ndk-and-opencv-development-with-android-studio/)
* [Quanhua92 Github](https://github.com/quanhua92/NDK_OpenCV_AndroidStudio)
* [Ph0b](http://ph0b.com/android-studio-gradle-and-ndk-integration/)
* [Stackoverflow Question](http://stackoverflow.com/questions/17767557/how-to-use-opencv-in-android-studio-using-gradle-build-tool?answertab=active#tab-top)
* [Gaku.net](http://blog.gaku.net/ndk/)
* [Build.Gradle Variables](http://stackoverflow.com/questions/21999829/how-do-i-read-properties-defined-in-local-properties-in-build-gradle/22012018#22012018)
* [OpenCV Question](http://answers.opencv.org/question/14546/how-to-work-with-opencv4android-in-android-studio/)
* [Android.mk Issue](http://stackoverflow.com/questions/6942730/android-ndk-how-to-include-android-mk-into-another-android-mk-hierarchical-pro)
