# Archiver
Projected intended to make a GUI to streamline extracting of archive files, especially password  protected ones on Linux.

This is also an excuse to have a first experience in having a Linux based environment for both dev process and for the release. Doubles as a refresher for JavaFX (and possibly FXML) for handling the views.

## Requirements
At this stage the app internally relies on p7zip (.7z and .zip) and the unrar package from the RPM Fusion (.rar).

## How it works
The app simply compose the command that will be executed based on parameters inputted by the user.

The goal is to not have to bother anymore with trying to extract password-protected archives in terminal with constant invisible password text input and the terminal throwing a fit if there is a space in the file name or path (not a limiting factor with ProcessBuilder and a list of strings for command input) 