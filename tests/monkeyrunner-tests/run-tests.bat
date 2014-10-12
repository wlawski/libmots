@ECHO OFF
REM
REM The MIT License (MIT)
REM
REM Copyright (c) 2014 Wiktor Lawski <wiktor.lawski@gmail.com>
REM
REM Permission is hereby granted, free of charge, to any person obtaining a copy
REM of this software and associated documentation files (the "Software"), to deal
REM in the Software without restriction, including without limitation the rights
REM to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
REM copies of the Software, and to permit persons to whom the Software is
REM furnished to do so, subject to the following conditions:
REM
REM The above copyright notice and this permission notice shall be included in
REM all copies or substantial portions of the Software.
REM
REM THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
REM IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
REM FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
REM AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
REM LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
REM OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
REM THE SOFTWARE.
REM

REM
REM This script makes running all of monkeyrunner tests fully automatic task.
REM

SET logs=.logs

DEL %logs% 2> nul

FOR /F %%i IN ('CD') DO SET current_dir=%%i
SETX LIBMOTS_TEST_DIR %current_dir% >> %logs%

CALL init.bat
TIMEOUT /T 60
ECHO.

SET test_script_files=0

FOR /F %%f IN ('DIR /B /O:N %current_dir%\t*') DO (
	CMD /C reinstall-packages.bat >> %logs% 2>&1
	ECHO %%f >> .logs
	CMD /C monkeyrunner -v ALL %current_dir%\%%f 2>> .logs
	SET /A test_script_files+=1 >> %logs%
)

SET failed=0

FOR /F %%f IN ('DIR /B %current_dir%\actual-screenshots') DO (
	SET /A failed+=1 >> %logs%
)

SET /A passed=%test_script_files%-%failed%

ECHO.
ECHO Executed %test_script_files% test(s).
ECHO %passed% test(s) passed.
ECHO %failed% test(s) failed.
ECHO.

IF %failed% GTR 0 (
	ECHO Not all of tests passed. You can find actual results
	ECHO ^(only screenshots for failures^) here:
	ECHO %current_dir%\actual-screenshots
	ECHO and compare them to reference screenshots located here:
	ECHO %current_dir%\reference-screenshots
	ECHO Screenshots use the same names as their tests
	ECHO ^(except for file extensions^).
	ECHO You can find more details in .logs file.
	ECHO.
)

REG DELETE HKCU\Environment /v LIBMOTS_TEST_DIR
