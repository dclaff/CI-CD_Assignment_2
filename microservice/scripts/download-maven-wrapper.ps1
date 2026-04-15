Param()
$root = Split-Path -Parent $PSScriptRoot
$dir = Join-Path $root '.mvn\wrapper'
If (-not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir | Out-Null }
$url = 'https://repo1.maven.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar'
$out = Join-Path $dir 'maven-wrapper.jar'
Write-Host "Downloading maven-wrapper.jar to $out"
try {
  $wc = New-Object System.Net.WebClient
  $wc.DownloadFile($url, $out)
  Write-Host "Downloaded successfully."
} catch {
  Write-Error "Failed to download: $_"
  exit 1
}
