CallsignSpotParser
==================

Log parser and uploader for Amateur Radio software, WSJT (http://physics.princeton.edu/pulsar/k1jt/). 

This is used to provide log visualization for stations received using WSJT, by uploading WSJT log files
to http://www.spotviz.info (the source of this project is here: https://github.com/kevinhooke/SpotViz).

Pre-reqs:
- requires Java SE 8. Download from here (http://www.java.com/en/) if you don't already have installed.

To use:
- download SpotvizLogUploader-1.0.jar from the release page: https://github.com/kevinhooke/WSJTLogParser/releases/tag/1.0
- save the downloaded jar to a folder locally
- start a command prompt/shell/terminal, and from the command line cd into the folder 
containing the downloaded jar and then type:

java -jar SpotvizLogUploader-1.0.jar --callsign YOUR_CALL --file /PATH/TO/WSJT-X/ALL.TXT

where:
YOUR_CALL is your callsign - this will be used to store your uploaded data against, so
when searching for the call on the http://www.spotvi.info site you can find your
uploaded data.

For example paths to the ALL.TXT file on different platforms, see the online docs
here: http://www.spotviz.info/#/upload

The parser runs once a minute on the minute to sync with new spots being logged to WSJT-X's
ALL.TXT file. If you leave it running, it will continue to watch the file for new updates
and upload them every minute. Otherwise you can run it one time, leave for a minute to ensure
it runs, and then Ctrl-C to end it.

After running, head to http://www.spotviz.info to playback your received signals!

