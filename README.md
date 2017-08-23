# NetBeans JSBeautify

This is a NetBeans v8.x plugin which provides alternative/better JavaScript formatting than that which is built-in to NetBeans. In addition to adding a new menu>Source option to JSBeautify, menu>Tools>Options>Miscellaneous includes configuration options for custom setting how code is formatted. This is the same formatting code underlying the online tools at [jsbeautifier.org][jsbsite].

See this [tracker item][nbtracker] which requested better JS formatting, and a request for the [js-beautify][jsb] script to get built-in to NetBeans to replace the existing formatter. That ticket was created by Drew Hamlett, who originally wrote this plugin in 2013 for NetBeans 7.x and hosted in his GitHub [repo][dh].

Years later, and not unexpectedly, it wouldn't work in v8 so Tony Gravagno [forked][tgfork] it, then ported to v8 with JRE 1.8, Nashorn, and the latest [JSBeautify][jsb] script. This became the v0.4 update.

## v0.4.2 ##

Working version is OK for development but has issues when installed from an NBM.
Options page has issues too.
Posting here just to get this into the repo and will fix issues locally before posting a v0.5.

## v0.4 ##

- Updated Nashorn usage.
- Includes a local version of the latest js-beautify script.
- Added many options available from JSBeautify command-line and on-line utility.
    - Requires testing/verification.
- The source has a simple toggle to reach out to a CDN to retrieve the script. There is not yet a Tools>Option to change that location or to enter a URL to get the latest version. This may be added later.
    - Since the CDN is versioned (1.6.14) it doesn't make sense to keep going out to CDN to get the same version all the time. If the latest version were posted there then it would make sense to use a version here or to get the latest online.
    - Accessing on-line by default may not be preferred because it requires a web access on every format. Not only is this slow and potentially costly for someone but it wouldn't work for offline developers. For this reason the local version is currently the hard-coded default.

## LICENSE ##

This software was forked from another project.
There is full intent to provide respectful and legal attribution for this code as specified here.
Errors will be corrected immediately upon notice.

The name "NetBeans JSBeautify" is intended purely for recognition and to convey the intent and source of this offering, and does not imply collaboration on any part by those associated with NetBeans or JSBeautify.

No license was provided by Drew Hamlett for the [original work][dh].

This software includes the JS Beautify code, also [published][jsb] under an MIT license:
Copyright (c) 2007-2017 Einar Lielmanis, Liam Newman, and contributors.
All rights and terms for the original software apply to this project as well.


[dh]: https://github.com/drewhamlett/netbeans-jsbeautify
[jsb]: https://github.com/beautify-web/js-beautify
[jsbsite]: http://jsbeautifier.org/
[tgfork]: https://github.com/TonyGravagno/netbeans-jsbeautify
[nbtracker]: https://netbeans.org/bugzilla/show_bug.cgi?id=218421
