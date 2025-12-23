# Build VenikCraft variants for multiple Java/MC targets and copy to dist\VenikCraft
$profiles = @(
    'venikcraft',
    'mc-java8',
    'mc-java17',
    'mc-java21',
    'mc-1.12.2-java8',
    'mc-1.16.5-java8',
    'mc-1.17-java17',
    'mc-1.19.4-java17',
    'mc-1.20.2-java17',
    'mc-1.21.10-java21'
)

$dist = Join-Path -Path $PSScriptRoot -ChildPath 'dist\VenikCraft'
if (-not (Test-Path $dist)) { New-Item -ItemType Directory -Path $dist | Out-Null }

foreach ($p in $profiles) {
    Write-Host "Building profile: $p"
    $rc = & mvn -P$p -DskipTests clean package
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Build failed for profile $p" -ForegroundColor Red
        continue
    }
    # find produced jar in target
    $target = Join-Path -Path $PSScriptRoot -ChildPath 'target'
    $jar = Get-ChildItem -Path $target -Filter "*${project.version}*.jar" -File | Sort-Object LastWriteTime -Descending | Select-Object -First 1
    if (-not $jar) { # fallback by convention
        $candidates = Get-ChildItem -Path $target -Filter "*${p}*.jar" -File
        if ($candidates) { $jar = $candidates | Select-Object -First 1 }
    }
    if (-not $jar) {
        Write-Host "No jar artifact found for profile $p" -ForegroundColor Yellow
        continue
    }
    # create a safe name: VenikCraft-<profile>.jar
    $outName = "VenikCraft-$($p).jar"
    Copy-Item -Path $jar.FullName -Destination (Join-Path $dist $outName) -Force
    Write-Host "Copied $($jar.Name) -> $outName"
}
Write-Host "Done. Built artifacts placed into $dist" -ForegroundColor Green
