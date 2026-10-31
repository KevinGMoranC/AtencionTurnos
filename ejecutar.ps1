$ErrorActionPreference = 'Stop'
Push-Location -LiteralPath $PSScriptRoot
try {
    & mvn compile exec:java
    if ($LASTEXITCODE -ne 0) { throw 'La aplicación terminó con error. Revise la salida anterior.' }
} finally { Pop-Location }
