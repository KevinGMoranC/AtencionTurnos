$ErrorActionPreference = 'Stop'
Push-Location -LiteralPath $PSScriptRoot
try {
    & mvn test-compile exec:java '-Dexec.mainClass=edu.oficina.pruebas.PruebasFinales' '-Dexec.classpathScope=test'
    if ($LASTEXITCODE -ne 0) { throw 'La verificación final falló. Revise la salida anterior.' }
} finally { Pop-Location }
