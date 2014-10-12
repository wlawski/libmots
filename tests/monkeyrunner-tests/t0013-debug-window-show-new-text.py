#
# The MIT License (MIT)
#
# Copyright (c) 2014 Wiktor Lawski <wiktor.lawski@gmail.com>
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.
#

#
# This test checks that debug window will show current content in multiple
# attemps when its value is changed between shows.
#

import inspect
import os
import sys

from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice

currentDir = os.environ["LIBMOTS_TEST_DIR"]
sys.path.insert(0, currentDir)

import helpers

calledScript = inspect.getfile(inspect.currentframe())
screenshotRect = (0, 35, 480, 765)
package = "net.wiktorlawski.hellolibmots"
activity = package + ".HelloLibmotsActivity"
runComponent = package + "/" + activity

device = MonkeyRunner.waitForConnection(5, "emulator-5554")

device.startActivity(component=runComponent)
MonkeyRunner.sleep(2.5)
device.touch(70, 110, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(2.5)
device.touch(25, 215, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(0.5)
device.type("text")
device.touch(50, 285, MonkeyDevice.DOWN_AND_UP)

device.press("KEYCODE_HOME", MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(2.5)
device.touch(405, 110, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(2.5)
device.touch(240, 485, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(2.5)

device.startActivity(component=runComponent)
MonkeyRunner.sleep(2.5)

for i in range(0, 4):
    device.press("KEYCODE_DEL", MonkeyDevice.DOWN_AND_UP)

device.type("newText")
device.touch(50, 285, MonkeyDevice.DOWN_AND_UP)

device.press("KEYCODE_HOME", MonkeyDevice.DOWN_AND_UP)
device.touch(405, 110, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(2.5)

result = device.takeSnapshot().getSubImage(screenshotRect)
helpers.checkResult(result, currentDir, calledScript)
