# Clipaholic

## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Features](#features)
* [Illustrations](#illustrations)
* [Roadmap](#roadmap)

## General info
The app will allow you to download videos in the format of your choice to the location you specify and optionally convert them to the music format of your choice. Initially it will support services such as YouTube, Facebook, Vimeo. It will be possible to create an account that will store the download history. Before downloading you will be able to watch a movie in the application and view information about it (title, description, views).

## Technologies
Project is created with:
* Java
* Firebase
* Retrofit
* Android Jetpack

## Features
* Create an account(or log in using your Google account). Create a user in the database and create a table for him with download history(Firebase)
* Log in with your account
* Choosing where the movie will be downloaded from
* Entering the URL of the video and checking if it is correct. After successful verification, you will be able to view the video (YouTube API for YouTube, the rest -> VideoView) and basic information such as title, description, etc.
* Download selected video (choose format, quality), specify location (default /download or possibility of direct download to Google drive) -> YouTube/ Facebook/ Vimeo Extractor:
  - https://github.com/HaarigerHarald/android-youtubeExtractor
  - https://github.com/Ashusolanki/FacebookUrlExtractor
  - https://github.com/ed-george/AndroidVimeoExtractor
* Notifications on the status and completion of the download.

## Illustrations
* This will be updated on a regular basis

## Roadmap
Work Schedule:
* Report I (31.03):
  - Basic user interface (welcome screen, login/registration panel)
  - Implement database and check basic functionality (account creation and login) and create table for user
  - User panel
* Report II (21.04):
  - Creating interface for selecting website from which video will be downloaded and possibility to enter URL
  - After successful check, display summary (Information about movie, video player)
  - Choice of recording format and quality
  - Notification of successful download/conversion in notification panel
* Report III(12.05):
  - Play music file via external applications
  - Creation of download history
  - Implementation of additional functionality like:
    - Sign in with Google account
    - Save to Google Drive
    - Application settings
    - The list will be developed during the development of the project
* Prototype(26.05):
  - ll the above points plus additional functionality that will be implemented
  - In case of delays or difficulties with the implementation of a given functionality, it will be moved here.

