# Package script - pack build artifacts into zip
param(
    [string]$OutputName = "hs-script-native"
)

$ErrorActionPreference = "Stop"

# Define paths
$ProjectRoot = $PSScriptRoot
$NativeDir = Join-Path $ProjectRoot "hs-script-app\target\native"
$ResourcesDir = Join-Path $ProjectRoot "hs-script-app\src\main\resources"
$HsCardsDb = Join-Path $ProjectRoot "hs-script-app\hs_cards.db"

# Check if hs-script.exe exists, build if not
$HsScriptExe = Join-Path $NativeDir "hs-script.exe"
if (-not (Test-Path $HsScriptExe)) {
    Write-Host "hs-script.exe not found, starting build..." -ForegroundColor Yellow

    $MvnCmd = Join-Path $ProjectRoot "mvnw.cmd"
    if (-not (Test-Path $MvnCmd)) {
        $MvnCmd = "mvn"
    }

    Push-Location $ProjectRoot
    try {
        & $MvnCmd clean package -DskipTests -Pnative -pl hs-script-app -am
        if ($LASTEXITCODE -ne 0) {
            Write-Error "Maven build failed"
            exit 1
        }
    } finally {
        Pop-Location
    }

    Write-Host "Build completed" -ForegroundColor Green
}

# Check if native directory exists
if (-not (Test-Path $NativeDir)) {
    Write-Error "Native directory not found: $NativeDir"
    exit 1
}

# Create temp directory
$TempDir = Join-Path $env:TEMP "hs-script-package-$(Get-Date -Format 'yyyyMMddHHmmss')"
New-Item -ItemType Directory -Path $TempDir -Force | Out-Null

try {
    Write-Host "Packaging..." -ForegroundColor Green

    # 1. Copy hs-script.exe to root
    if (Test-Path $HsScriptExe) {
        Copy-Item $HsScriptExe -Destination $TempDir
        Write-Host "  Added: hs-script.exe"
    } else {
        Write-Warning "Not found: hs-script.exe"
    }

    # 2. Copy all dlls to root
    $DllFiles = Get-ChildItem -Path $NativeDir -Filter "*.dll"
    foreach ($dll in $DllFiles) {
        Copy-Item $dll.FullName -Destination $TempDir
        Write-Host "  Added: $($dll.Name)"
    }

    # 3. Copy resources/dll to lib/dll
    $SrcDllDir = Join-Path $ResourcesDir "dll"
    if (Test-Path $SrcDllDir) {
        $DestLibDllDir = Join-Path $TempDir "lib\dll"
        New-Item -ItemType Directory -Path $DestLibDllDir -Force | Out-Null
        Copy-Item (Join-Path $SrcDllDir "*") -Destination $DestLibDllDir -Recurse
        Write-Host "  Added: lib/dll directory"
    } else {
        Write-Warning "Not found: resources/dll directory"
    }

    # 4. Copy resources/resources/img directory (exclude startup.jpg)
    $SrcImgDir = Join-Path $ResourcesDir "resources\img"
    if (Test-Path $SrcImgDir) {
        $DestImgDir = Join-Path $TempDir "resources\img"
        New-Item -ItemType Directory -Path $DestImgDir -Force | Out-Null
        Get-ChildItem -Path $SrcImgDir | Where-Object { $_.Name -ne "startup.jpg" } | ForEach-Object {
            Copy-Item $_.FullName -Destination $DestImgDir -Recurse
        }
        Write-Host "  Added: resources/img directory (excluded startup.jpg)"
    } else {
        Write-Warning "Not found: resources/resources/img directory"
    }

    # 5. Copy exe files to root
    $ExeDir = Join-Path $ResourcesDir "exe"
    $ExeFiles = @("card-update-util.exe", "inject-util.exe", "install-drive.exe")
    foreach ($exe in $ExeFiles) {
        $ExePath = Join-Path $ExeDir $exe
        if (Test-Path $ExePath) {
            Copy-Item $ExePath -Destination $TempDir
            Write-Host "  Added: $exe"
        } else {
            Write-Warning "Not found: $exe"
        }
    }

    # 6. Copy hs_cards.db to root
    if (Test-Path $HsCardsDb) {
        Copy-Item $HsCardsDb -Destination $TempDir
        Write-Host "  Added: hs_cards.db"
    } else {
        Write-Warning "Not found: hs_cards.db"
    }

    # Create zip file
    $ZipPath = Join-Path $ProjectRoot "$OutputName.zip"

    # Remove if exists
    if (Test-Path $ZipPath) {
        Remove-Item $ZipPath -Force
    }

    # Compress
    Compress-Archive -Path (Join-Path $TempDir "*") -DestinationPath $ZipPath -Force

    Write-Host ""
    Write-Host "Package completed: $ZipPath" -ForegroundColor Green

    # Show zip contents
    Write-Host ""
    Write-Host "Zip contents:" -ForegroundColor Cyan
    $shell = New-Object -ComObject Shell.Application
    $zipFolder = $shell.Namespace($ZipPath)

    function Show-ZipContents {
        param($folder, $indent = "")
        foreach ($item in $folder.Items()) {
            Write-Host "$indent$($item.Name)"
            if ($item.IsFolder) {
                Show-ZipContents -folder $item.GetFolder -indent "$indent  "
            }
        }
    }
    Show-ZipContents -folder $zipFolder

} finally {
    # Clean up temp directory
    if (Test-Path $TempDir) {
        Remove-Item $TempDir -Recurse -Force
    }
}
