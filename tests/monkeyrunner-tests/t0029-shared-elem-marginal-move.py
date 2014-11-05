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
# This test checks that marginal move is acceptable and still triggers debug
# window to appear.
#

import inspect
import os
import sys

from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice

currentDir = os.environ["LIBMOTS_TEST_DIR"]
sys.path.insert(0, currentDir)

import helpers

calledScript = inspect.getfile(inspect.currentframe())
device = helpers.getDevice()

x = helpers.sharedElemX
y = helpers.sharedElemY

helpers.createSharedElement(device, True)
helpers.pressHome(device)
helpers.moveSharedElement(device, x, y, -168, 290)
helpers.moveSharedElement(device, x - 168, y + 290, -7, 0)

result = device.takeSnapshot().getSubImage(helpers.portraitRect)
helpers.checkResult(result, currentDir, calledScript)