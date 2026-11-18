<# : Urna Eletronica - abre a urna de votacao.
@echo off
rem Funciona sozinho, em qualquer Windows 10 ou 11: na primeira vez baixa a urna
rem (com o Java incluido) do GitHub para %LOCALAPPDATA%\UrnaEletronica; depois so atualiza.
set "URNA_PROGRAMA=Urna"
powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-Expression ([IO.File]::ReadAllText('%~f0'))"
exit /b
#>
$ErrorActionPreference = 'Stop'
$repositorio = 'joaovsbarros10/urna-eletronica'
$pasta = Join-Path $env:LOCALAPPDATA 'UrnaEletronica'
$programa = Join-Path $pasta "Urna\$env:URNA_PROGRAMA.exe"
$arquivoVersao = Join-Path $pasta 'versao.txt'
$versaoInstalada = if (Test-Path $arquivoVersao) { (Get-Content $arquivoVersao -Raw).Trim() } else { '' }
try {
    $versao = (Invoke-RestMethod "https://api.github.com/repos/$repositorio/releases/latest" -TimeoutSec 15).tag_name
    if ($versao -ne $versaoInstalada -or -not (Test-Path $programa)) {
        Write-Host "Baixando a Urna Eletronica $versao (cerca de 27 MB, so na primeira vez)..."
        $zip = Join-Path $env:TEMP 'Urna-Windows.zip'
        $ProgressPreference = 'SilentlyContinue'
        Invoke-WebRequest "https://github.com/$repositorio/releases/download/$versao/Urna-Windows.zip" -OutFile $zip -UseBasicParsing
        if (Test-Path $pasta) { Remove-Item $pasta -Recurse -Force }
        Expand-Archive $zip $pasta -Force
        Remove-Item $zip
        Set-Content $arquivoVersao $versao
    }
} catch {
    if (-not (Test-Path $programa)) {
        Write-Host 'Nao foi possivel baixar a urna. Verifique a internet e tente de novo.'
        Read-Host 'Aperte Enter para sair'
        exit 1
    }
}
Start-Process $programa
