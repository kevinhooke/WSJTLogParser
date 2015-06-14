CallsignSpotParser
==================

Log parser and uploader for Amateur Radio software, WSJT (http://physics.princeton.edu/pulsar/k1jt/). 

This is used to provide log visualization for stations received using WSJT, by uploading WSJT log files
to http://spotviz.info (the source of this project is here: https://github.com/kevinhooke/SpotViz).

Rereqs:
- requires Java SE 8. Download from here (http://www.java.com/en/) if you don't already have installed.

To use:
- download the jar from the dist folder
- from a command line:

java -jar SpotvizLogUploader-1.0.jar --calsign YOUR_CALL -- file /PATH/TO/WSJT-X/ALL.TXT

The parser runs once a minute on the minute to sync with new spots being logged to WSJT-X's
ALL.TXT file. If you leave it running, it will continue to watch the file for new updates
and upload them every minute. Otherwise you can run it one time, leave for a minute to ensure
it runs, and then Ctrl-C to end it.

After running, head to http://www.spotviz.info to playback your received signals!

