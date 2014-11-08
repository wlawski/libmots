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
landscapeRect = (0, 0, 440, 800)
numberOfTabs = 5
portraitRect = (0, 35, 480, 765)
referenceDir = "reference-screenshots"
tabSize = 8

package = "net.wiktorlawski.hellolibmots"
activity = package + ".HelloLibmotsActivity"
runComponent = package + "/" + activity

shortWaitTime = 0.5
waitTime = 2.5
longWaitTime = 7.0

sharedElemX = 405
sharedElemY = 110

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
# "getInstance" button
def createSharedElement(device, showing):
    device.startActivity(component=runComponent)
    MonkeyRunner.sleep(waitTime)

    # Tick checkbox, if necessary
    if (showing):
        device.touch(30, 110, MonkeyDevice.DOWN_AND_UP)
        MonkeyRunner.sleep(waitTime)

    # Touch 'getInstance' button
    device.touch(270, 115, MonkeyDevice.DOWN_AND_UP)
    MonkeyRunner.sleep(waitTime)

# Returns device object
def getDevice():
    return MonkeyRunner.waitForConnection(10, defaultEmulator)

# Moves shared element (moving "finger" away from the screen is optional)
def moveSharedElement(device, x, y, deltaX, deltaY, moveFingerAway=True):
    device.touch(x, y, MonkeyDevice.DOWN)
    device.touch(x + deltaX, y + deltaY, MonkeyDevice.MOVE)

    if (moveFingerAway):
        device.touch(x + deltaX, y + deltaY, MonkeyDevice.UP)
        MonkeyRunner.sleep(waitTime)

# Presses Back button
def pressBack(device):
    device.press("KEYCODE_BACK", MonkeyDevice.DOWN_AND_UP)
    MonkeyRunner.sleep(waitTime)

# Presses Home button
def pressHome(device):
    device.press("KEYCODE_HOME", MonkeyDevice.DOWN_AND_UP)
    MonkeyRunner.sleep(waitTime)

# Starts activity that sets device orientation to landscape
def setLandscape(device):
    package = "net.wiktorlawski.setorientation"
    activity = package + ".SetLandscape"
    runComponent = package + "/" + activity
    device.startActivity(component=runComponent)
    MonkeyRunner.sleep(longWaitTime)

# Starts activity that sets device orientation to portrait
def setPortrait(device):
    package = "net.wiktorlawski.setorientation"
    activity = package + ".SetPortrait"
    runComponent = package + "/" + activity
    device.startActivity(component=runComponent)
    MonkeyRunner.sleep(longWaitTime)

# Starts 'Hello libmots' application
def startHelloLibmots(device):
    device.startActivity(component=runComponent)
    MonkeyRunner.sleep(waitTime)

# Toggles "startShowing" checkbox
def toggleStartShowing(device):
    device.touch(30, 110, MonkeyDevice.DOWN_AND_UP)

# Touches "addMessage" button
def touchAddMessage(device):
    device.touch(185, 255, MonkeyDevice.DOWN_AND_UP)
    MonkeyRunner.sleep(shortWaitTime)

# Touches "Cancel" button in debug window
def touchCancel(device):
    device.touch(350, 485, MonkeyDevice.DOWN_AND_UP)
    MonkeyRunner.sleep(waitTime)

# Touches "Clear" button in debug window
def touchClear(device):
    device.touch(130, 485, MonkeyDevice.DOWN_AND_UP)
    MonkeyRunner.sleep(waitTime)

# Touches "getInstance" button
def touchGetInstance(device):
    device.touch(270, 115, MonkeyDevice.DOWN_AND_UP)
    MonkeyRunner.sleep(waitTime)

# Touches new message edit text
def touchNewMessageEditText(device):
    device.touch(25, 215, MonkeyDevice.DOWN_AND_UP)
    MonkeyRunner.sleep(shortWaitTime)

# Touches "setText" button
def touchSetText(device):
    device.touch(50, 285, MonkeyDevice.DOWN_AND_UP)
    MonkeyRunner.sleep(shortWaitTime)

# Touches shared element (using default shared element position)
def touchSharedElement(device):
    device.touch(sharedElemX, sharedElemY, MonkeyDevice.DOWN_AND_UP)
    MonkeyRunner.sleep(longWaitTime)
