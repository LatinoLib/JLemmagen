$errorActionPreference = "Stop"

$scriptRoot = Split-Path -Parent -Path $MyInvocation.MyCommand.Definition

$files = (Get-ChildItem "$scriptRoot" -Filter *.java -Recurse).FullName

if (Test-Path "$scriptRoot\lib") { rm "$scriptRoot\lib" -Recurse -Force }
mkdir "$scriptRoot\lib" | Out-Null

javac -d "$scriptRoot\lib" $files

jar cf "$scriptRoot\lib\Snowball.jar" "$scriptRoot\lib"