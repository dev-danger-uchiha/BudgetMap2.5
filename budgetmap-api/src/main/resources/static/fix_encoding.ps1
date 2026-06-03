$dir = "c:\PROYECTO REAL\budgetmap-api\src\main\resources\static"
$files = Get-ChildItem -Path $dir -Filter "*.html" -Recurse

$replacements = @{
    "Descripcin" = "Descripción"
    "Trminos" = "Términos"
    "Telfono" = "Teléfono"
    "Mximo" = "Máximo"
    "Ubicacin" = "Ubicación"
    "Direccin" = "Dirección"
    "Gestin" = "Gestión"
    "Informacin" = "Información"
    "Configuracin" = "Configuración"
    "Contrasea" = "Contraseña"
    "Validacin" = "Validación"
    "Inici" = "Inició"
    "Aqu" = "Aquí"
    "Prximos" = "Próximos"
    "conexin" = "conexión"
    "crtico" = "crítico"
    "vlida" = "válida"
    "CDIGO" = "CÓDIGO"
    "Cdigo" = "Código"
    "PsBLICO" = "PÚBLICO"
    "Reserva" = "¡Reserva"
    "Informacin" = "¡Información"
    "Y""?" = "📍"
    "Y""." = "📅"
    "Y??" = "🏁"
    "YZY?" = "🎫"
    "" = "í"
}

foreach ($f in $files) {
    # Read file as UTF-8
    $content = Get-Content -Path $f.FullName -Encoding UTF8 -Raw
    
    $changed = $false
    if ($content -match "") {
        foreach ($key in $replacements.Keys) {
            if ($content.Contains($key)) {
                $content = $content.Replace($key, $replacements[$key])
                $changed = $true
            }
        }
        
        if ($changed) {
            Set-Content -Path $f.FullName -Value $content -Encoding UTF8
        }
    }
}
Write-Output "Correccion ortografica completada"
