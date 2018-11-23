# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [4.2.0-52]
### Added
- GH-#12 Color chase buttons for easier finding
- GH-#11 Reorder chase and redesigned the popup reorder for cues

## [4.1.0-51]
### Added
- GH-#10 Add an import button in case your Android can't auto open the .AuroraDMX file

## [4.0.5-50]
### Fixed
- Screen rotate looses channel levels

## [4.0.4-49]
### Fixed
- NPE on chase fade.
- Better chase fade handling with call backs.
- Prevent NPE on alCues.
- Look and feel black menu text fix.

## [4.0.3-48]
### Fixed
- Crash when deleting cues from IndexOutOfBounds
- Crash when loading ArtNet scan from IllegalState

## [4.0.2-47]
### Changed
- Updated icon to support different shapes

## [4.0.1-46]
### Fixed
- ConcurrentModificationException when viewing ArtNet Servers

### Changed
- How Billing is loaded in Settings so instant install can work
- Optimized imports

## [4.0.0-45]
### Added
- Chases that will chain together cues for a continuous fade or dramatic event.

### Changed
- Merged the cue fade up and down into one value

### Fixed
- SecurityException when opening a file
- Invalid manual ArtNET address
- NPE when sorting ArtNET servers
- More reliable UI during fade by using Handler instead of TimerTask

### Security
- Billing API updates
- Prevent file path traversal when sharing projects

## [3.1.1-39] 2017-06-17
### Fixed
- ArtNet scanning
- Modifying cues on Cue Sheet

## [3.1-38] 2017-05-24
### Added
- Cue Sheet

## [3.0-35] 2017-02-24
### Added
- Next Cue button.
- Allow Interrupting a fading Cue.
- Font Awesome icons

### Fixed
- Nexus 10 only showing one column

### Changed
- Redesign layout. 
- Use icons instead of names Update and Reorder existing Cues. 
- Use XML for UI 

## [2.8-30] 2016-11-27
### Added
- CID
- Improved sACN

## [2.7-28] 2016-10-24
### Fixed
- Fix 2 Console Errors java.lang.ArrayIndexOutOfBoundsException: length=512; index=512 	at com.AuroraByteSoftware.AuroraDMX.MainActivity.getCurrentDimmerLevels(MainActivity.java:352) 
 

## [2.3]
### Changed
- Raise max fixtures back to 512

## [2.2]
### Added
- ArtNet Port/Universe

## [2.1] 
### Added
- Fade time of 0.

### Changed 
- Improved fading performance.

## [2.0]
### Added
- RGB Fixtures

## [1.8-16]
### Added
- Edit button to channels
- Names to channels

### Remmoved
- Bump button

## [1.7-15]
### Fixed
- Bump button bug

## [1.7-14]
### Added
- View the brightness as 255 steps instead of just 100%

## [1.6-13]
### Added
- Network folder