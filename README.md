# Overview #

Aurora DMX is designed to be simple, intuitive, and easy to use. This Android app is used to control lighting devices on DMX-512 via WiFi using the ArtNet protocol or sACN/ E1.31 via multicast.
https://play.google.com/store/apps/details?id=com.AuroraByteSoftware.AuroraDMX

# Description #

Features:
- Simple UI
- Selectable channel color
- Cues
- Rename Cues
- Cue fade times
- Channel to dimmer patch
- ArtNet
- sACN/ E1.31
- Save multiple projects
- sACN Unicast protocol
- View channel level as 255 steps
- Name Channels
- Set specific channel level/ step
- RGB Color selector
- ArtNet Universe
- Preset channel levels
- Share your projects 
- Next Cue button.
- Cue Sheet


The main screen was designed to give the light board operator a similar feel to what a production full size board would give them. It contains channels with channel number, level in percent, a level slider, and an edit button. Along the bottom is the cue list. Add Cue will create a light cue of the current channel levels and append it to the end of the cue list. If the use long presses on an existing cue they are able to edit it. Edit features are insert a new cue, remove cue, rename the cue, and change fade up and down times. In the settings menu ArtNet server is selected from a discovered list of nodes also manual entry is allowed. Default cue fade times can be assigned along with the color of the channel faders. Channel to dimmer patching is allowed in the patch view. One channel can be assigned to as many dimmers as you want. 

Save project will save current state of channels, patch, and cues to a user assigned name. Load project will open a previously saved project. A long press on the project name will prompt for delete. The current project is saved when exiting or switching project. The current project name is displayed at the top right of the main page.

The free version allows only 5 channels, an in-app purchase allows all 512 channels. The paid and free version allows patching to all 512 dimmers. 

If you any new features or different DMX protocols you would like to see just send me an email and I would love to get them in there.

Two ways to get signal to a DMX512 line:
Easiest: ENTTEC's ODE with a wireless router. 
Cheapest: Raspberry Pi running Open Lighting Architecture with ENTTEC's Open DMX USB and a wireless router.

Beta: https://play.google.com/apps/testing/com.AuroraByteSoftware.AuroraDMX
Donate: https://www.paypal.me/DanFredell


![Screenshot](/Pictures/3.0/Screenshot_20170216-192855.png)

# Development Setup #

* Download Android Studio and v23 SDKS
* Open the AuroraDMX project
* `git clone https://github.com/dfredell/ArtNetStack` into a second directory
* Use File -> New -> Import Module to import the ArtNetStack module
* Then the project should build

# Contributions #

* Create a Pull Request

# Thanks #

[![BrowserStack](/Pictures/Browserstack-logo.svg?raw=true&sanitize=true "BrowserStack")](https://www.browserstack.com/)

# License # 

GNU General Public License v3 (GPL-3) 

