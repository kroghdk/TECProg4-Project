@echo off
::set arg1=%1

set dbUsername=
set dbPassword=

:enterDB
@echo Set Database name:
set /P db_name=

if %db_name%==list (goto listDB)
if %db_name%==exit (goto EXIT)

cd %db_name%
::cd args/

goto GETDBConnection


:GETDBConnection
@echo Set a Database Username:
set /P dbUsername=
@echo Set a Database Password:
set /P dbPassword=
goto RunDBFile

:RunDBFile

@echo Enter file name
set /P file=

if %file%==login (goto GETDBConnection)
if %file%==exit (goto enterDB)

for /r . %%a in (*) do if "%%~nxa"=="%file%" set p=%%~dpnxa
if defined p (
	@echo %p%
) else (
	@echo File not found
	goto RunDBFile
)

mysql --host=35.195.66.28 --port 3306 --force --user=%dbUsername% --password=%dbPassword% --database=%db_name% < %p%
echo Exit Code is %errorlevel%
if errorlevel 1 (
   @echo Failed to execute
)else (
	@echo success
)
goto RunDBFile

:EXIT
exit

:listDB
for /d %%d in (*.*) do (
    @echo %%d
)
goto enterDB