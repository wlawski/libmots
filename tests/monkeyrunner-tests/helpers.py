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
# This file provides a set of functions that are common for most of the
# monkeyrunner tests.
#

import os

from com.android.monkeyrunner import MonkeyDevice, MonkeyRunner
from java.lang import NullPointerException

actualDir = "actual-screenshots"
defaultEmulator = "emulator-5554"
numberOfTabs = 5
referenceDir = "reference-screenshots"
tabSize = 8

# Compares actual screenshot with the reference one and based on that prints
# result of test from the script that called this function.
def checkResult(result, currentDir, calledScript):
    fullScriptName = os.path.basename(calledScript)
    scriptName = os.path.splitext(fullScriptName)[0]
    referenceFile = currentDir + "/" + referenceDir + "/" + scriptName + ".png"
    actualFile = currentDir + "/" + actualDir + "/" + scriptName + ".png"

    # 1 character is added to script name (':')
    indentSize = numberOfTabs - ((len(scriptName) + 1) / tabSize)

    prefix = ""
    for i in range(0, indentSize):
        prefix = prefix + "\t"

    try:
        if (result.sameAs(MonkeyRunner.loadImageFromFile(referenceFile), 0.999)):
            print scriptName + ":" + prefix + "Passed"
        else:
            result.writeToFile(actualFile)
            print scriptName + ":" + prefix + "Failed"
    except(NullPointerException):
        result.writeToFile(actualFile)
        print "Reference file (" + referenceFile + ") missing!"
        print scriptName + ":" + prefix + "Failed"

# Starts 'Hello libmots' application and creates shared element using
# 'getInstance' button
def createSharedElement(device, showing):
    package = "net.wiktorlawski.hellolibmots"
    activity = package + ".HelloLibmotsActivity"
    runComponent = package + "/" + activity

    device.startActivity(component=runComponent)
    MonkeyRunner.sleep(2.5)

    # Tick checkbox, if necessary
    if (showing):
	device.touch(30, 110, MonkeyDevice.DOWN_AND_UP)

    # Touch 'getInstance' button
    device.touch(270, 115, MonkeyDevice.DOWN_AND_UP)
    MonkeyRunner.sleep(2.5)

# Returns device object
def getDevice():
    return MonkeyRunner.waitForConnection(10, defaultEmulator)

# Starts activity that sets device orientation to landscape
def setLandscape(device):
    package = "net.wiktorlawski.setorientation"
    activity = package + ".SetLandscape"
    runComponent = package + "/" + activity
    device.startActivity(component=runComponent)
    MonkeyRunner.sleep(2)

# Starts activity that sets device orientation to portrait
def setPortrait(device):
    package = "net.wiktorlawski.setorientation"
    activity = package + ".SetPortrait"
    runComponent = package + "/" + activity
    device.startActivity(component=runComponent)
    MonkeyRunner.sleep(1)
