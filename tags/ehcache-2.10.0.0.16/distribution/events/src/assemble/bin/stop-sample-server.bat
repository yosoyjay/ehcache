@echo off

rem All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.

cd "%~d0%~p0"
for /f "tokens=*" %%a in ('call relative-paths.bat tc_install_dir') do set tc_install_dir=%%a
pushd
cd %tc_install_dir%
set tc_install_dir=%CD%
popd
call "%tc_install_dir%\bin\stop-tc-server.bat"
