$errorActionPreference = "Stop"

$scriptRoot = Split-Path -Parent -Path $MyInvocation.MyCommand.Definition

$files = (Get-ChildItem "$scriptRoot" -Filter *.java -Recurse).FullName

if (Test-Path "$scriptRoot\bin") { rm "$scriptRoot\bin" -Recurse -Force }
mkdir "$scriptRoot\bin" | Out-Null

javac -d "$scriptRoot\bin" $files

jar cf "$scriptRoot\bin\Snowball.jar" "$scriptRoot\bin"