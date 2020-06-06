# Nyan Tray IntelliJ Plugin

A little plugin for IntelliJ (and Android Studio, and other IntelliJ based IDEs)
to let you know when work is ongoing.

![Illustration](/illus.gif?raw=true "Illustration")

It shows a Nyan Cat **in your macOS menu bar** whenever something is "ongoing" (loading, compiling, building, searching, analyzing...)

This will help you switch to another window temporarily while knowing at a glance when the task is finished.

When clicking on it, the total waiting time (today, this week, this month, this year, overall) is shown.

**Note: this has only been tested on macOS.** This uses the Swing <code>SystemTray</code> API so it may work on other OSes.

      
## Why?
To optimize your work/nonwork task switching ;)

A lot of people switch to their browser/slack/minesweeper/whatever window while the project
is compiling. Having a system-wide "ongoing progress" indicator allows to 
know right away when it's time to go back to work, even from another window or space.

Also, knowing how much time you spend doing nothing waiting for your IDE may 
be useful when asking your boss for a more powerful machine ;)

That's it!


## Install / Download
- Install it directly inside your IDE from the *Plugins* preferences (click on *Browse repositories...* and search for *Nyan Tray*).
- Or get the zip and install it manually (click on *Install plugin from disk...*): https://github.com/BoD/NyanTrayIntelliJPlugin/releases/latest
- The plugin page is here: https://plugins.jetbrains.com/plugin/11286-nyan-tray


## See also
If you like this plugin, you'll probably also like [this one](https://github.com/batya239/NyanProgressBar) ;)

There's also [this gradle plugin](https://github.com/passy/build-time-tracker-plugin) to count time spent waiting for gradle.


## Misc
The time counting data is stored in `~/.nyantray`.


## Licence

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
