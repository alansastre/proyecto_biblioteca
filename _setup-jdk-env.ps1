# _setup-jdk-env.ps1 -- Configura JAVA_HOME y PATH, limpiando JDKs antiguos.
# Llamado automaticamente por setup-jdk.cmd. No ejecutar manualmente.
param(
    [Parameter(Mandatory)][string]$JdkDir
)

$jdkBin = Join-Path $JdkDir 'bin'

# Patron para identificar entradas de Java en el PATH.
# Detecta: .jdks\ (IntelliJ), jdk-17 (versiones), Java\ (Oracle), javapath, vendors
# La exclusion de nuestro JDK se hace por comparacion exacta de ruta (no por regex)
$javaPattern = '[\\/]\.?jdks?[\\/]|[\\/]jdk-\d|[Jj]ava[\\/]|javapath|[\\/]adoptium|[\\/]corretto|[\\/]liberica|[\\/]semeru|[\\/]graalvm|[\\/]zulu'

function Clean-PathEntries {
    param([string]$PathStr, [string]$Scope)

    if (-not $PathStr) { return '' }

    $entries = $PathStr -split ';' | Where-Object { $_.Trim() -ne '' }
    $cleaned = @()
    $removed = @()

    foreach ($entry in $entries) {
        # Mantener si: es nuestro JDK, O no es una ruta de Java
        if ($entry -eq $jdkBin -or $entry -ieq $jdkBin -or -not ($entry -match $javaPattern)) {
            $cleaned += $entry
        } else {
            $removed += $entry
        }
    }

    foreach ($r in $removed) {
        Write-Host "      Eliminado de $Scope PATH: $r"
    }

    return ($cleaned -join ';')
}

# === USUARIO ===
Write-Host '      --- Variables de USUARIO ---'

[Environment]::SetEnvironmentVariable('JAVA_HOME', $JdkDir, 'User')
Write-Host "      JAVA_HOME = $JdkDir"

$userPath = [Environment]::GetEnvironmentVariable('Path', 'User')
$cleanUser = Clean-PathEntries -PathStr $userPath -Scope 'Usuario'

# Agregar nuestro JDK al principio si no esta
if ($cleanUser -and $cleanUser -inotlike "*$jdkBin*") {
    $cleanUser = "$jdkBin;$cleanUser"
} elseif (-not $cleanUser) {
    $cleanUser = $jdkBin
}

# Eliminar duplicados (case-insensitive), manteniendo el orden
$seen = @{}
$deduped = @()
foreach ($e in $cleanUser -split ';') {
    $k = $e.ToLower()
    if ($k -and -not $seen[$k]) { $seen[$k] = $true; $deduped += $e }
}
[Environment]::SetEnvironmentVariable('Path', ($deduped -join ';'), 'User')
Write-Host '      PATH de usuario actualizado.'

# === SISTEMA (requiere admin) ===
Write-Host ''
Write-Host '      --- Variables del SISTEMA ---'

try {
    $sysChanged = $false

    # JAVA_HOME del sistema
    $sysJH = [Environment]::GetEnvironmentVariable('JAVA_HOME', 'Machine')
    if ($sysJH -and ($sysJH -inotlike '*temurin-25*')) {
        [Environment]::SetEnvironmentVariable('JAVA_HOME', $JdkDir, 'Machine')
        Write-Host "      JAVA_HOME del sistema actualizado (antes: $sysJH)"
        $sysChanged = $true
    }

    # PATH del sistema
    $sysPath = [Environment]::GetEnvironmentVariable('Path', 'Machine')
    if ($sysPath -match $javaPattern) {
        $cleanSys = Clean-PathEntries -PathStr $sysPath -Scope 'Sistema'
        [Environment]::SetEnvironmentVariable('Path', $cleanSys, 'Machine')
        $sysChanged = $true
    }

    if (-not $sysChanged) {
        Write-Host '      Sin cambios necesarios.'
    }
} catch {
    Write-Host ''
    Write-Host '      AVISO: No se pudo limpiar el JDK antiguo del SISTEMA.'
    Write-Host '      Hay un JDK antiguo en el PATH del sistema que causa conflictos.'
    Write-Host '      Para solucionarlo, haz clic derecho en setup-jdk.cmd'
    Write-Host '      y selecciona: Ejecutar como administrador'
    Write-Host ''
    exit 1
}
