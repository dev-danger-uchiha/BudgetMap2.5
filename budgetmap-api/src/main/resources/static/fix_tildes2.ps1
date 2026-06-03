$dir = "c:\PROYECTO REAL\budgetmap-api\src\main\resources\static"
$files = Get-ChildItem -Path $dir -Filter "*.html" -Recurse

$c = [char]0xFFFD

$replacements = @{
    "Descripci$c`n" = "Descripción"
    "T$c`rminos" = "Términos"
    "Tel$c`fono" = "Teléfono"
    "M$c`ximo" = "Máximo"
    "Ubicaci$c`n" = "Ubicación"
    "Direcci$c`n" = "Dirección"
    "Gesti$c`n" = "Gestión"
    "Informaci$c`n" = "Información"
    "Configuraci$c`n" = "Configuración"
    "Contrase$c`a" = "Contraseña"
    "Validaci$c`n" = "Validación"
    "Pr$c`ximos" = "Próximos"
    "conexi$c`n" = "conexión"
    "cr$c`tico" = "crítico"
    "v$c`lida" = "válida"
    "C$c`DIGO" = "CÓDIGO"
    "C$c`digo" = "Código"
    "P$c`sBLICO" = "PÚBLICO"
    "A$c`adir" = "Añadir"
    "$c`Reserva" = "¡Reserva"
    "$c`Informaci$c`n" = "¡Información"
    "Categor$c`a" = "Categoría"
    "categor$c`a" = "categoría"
    "d$c`a" = "día"
    "D$c`a" = "Día"
    "A$c`o" = "Año"
    "a$c`o" = "año"
    "Acci$c`n" = "Acción"
    "acci$c`n" = "acción"
    "secci$c`n" = "sección"
    "Secci$c`n" = "Sección"
    "T$c`tulo" = "Título"
    "t$c`tulo" = "título"
    "M$c`s" = "Más"
    "m$c`s" = "más"
    "Tambi$c`n" = "También"
    "tambi$c`n" = "también"
}

foreach ($f in $files) {
    $content = Get-Content -Path $f.FullName -Encoding UTF8 -Raw
    
    $changed = $false
    if ($content.Contains($c)) {
        foreach ($key in $replacements.Keys) {
            if ($content.Contains($key)) {
                $content = $content.Replace($key, $replacements[$key])
                $changed = $true
            }
        }
        
        # Simple replacements without powershell variables inside strings that caused parsing errors
        $content = $content.Replace("Descripcin", "Descripción")
        $content = $content.Replace("Informacin", "Información")
        $content = $content.Replace("Ubicacin", "Ubicación")
        $content = $content.Replace("Direccin", "Dirección")
        $content = $content.Replace("Configuracin", "Configuración")
        $content = $content.Replace("Validacin", "Validación")
        $content = $content.Replace("Telfono", "Teléfono")
        $content = $content.Replace("Mximo", "Máximo")
        $content = $content.Replace("Aqu" + $c, "Aquí")
        $content = $content.Replace("aqu" + $c, "aquí")
        $content = $content.Replace("Inici" + $c, "Inició")
        $content = $content.Replace("inici" + $c, "inició")
        $content = $content.Replace("Est" + $c, "Está")
        $content = $content.Replace("est" + $c, "está")
        $content = $content.Replace("S" + $c, "Sí")
        $content = $content.Replace("s" + $c, "sí")
        $content = $content.Replace("Descripci" + $c, "Descripción")
        $content = $content.Replace("Informaci" + $c, "Información")
        $content = $content.Replace("Ubicaci" + $c, "Ubicación")
        $content = $content.Replace("Direcci" + $c, "Dirección")
        $content = $content.Replace("Configuraci" + $c, "Configuración")
        $content = $content.Replace("Validaci" + $c, "Validación")
        $content = $content.Replace("conexi" + $c, "conexión")
        $content = $content.Replace("secci" + $c, "sección")
        $content = $content.Replace("acci" + $c, "acción")
        $content = $content.Replace("Tel" + $c, "Telé")
        $content = $content.Replace("C" + $c + "`"DIGO", "CÓDIGO")
        $content = $content.Replace($c + "Y`"?", "📍")
        $content = $content.Replace($c + "Y`".", "📅")
        $content = $content.Replace($c + "Y??", "🏁")
        $content = $content.Replace($c + "YZY?", "🎫")
        $content = $content.Replace("Opciones de Gesti" + $c + "n", "Opciones de Gestión")
        
        Set-Content -Path $f.FullName -Value $content -Encoding UTF8
        Write-Output "Fixed $($f.FullName)"
    }
}
Write-Output "Done script"
