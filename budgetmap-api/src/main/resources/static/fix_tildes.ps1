$dir = "c:\PROYECTO REAL\budgetmap-api\src\main\resources\static"
$files = Get-ChildItem -Path $dir -Filter "*.html" -Recurse

$c = [char]0xFFFD

$replacements = @{
    "Descripci$c`n" = "Descripción"
    "Descripci$c" = "Descripción"
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
    "C$c`""DIGO" = "CÓDIGO"
    "$c`Y""?" = "📍"
    "$c`Y""." = "📅"
    "$c`Y??" = "🏁"
    "$c`YZY?" = "🎫"
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
    "Aqu$c`" = "Aquí"
    "aqu$c`" = "aquí"
    "A$c`adir" = "Añadir"
    "a$c`adir" = "añadir"
    "Inici$c`" = "Inició"
    "inici$c`" = "inició"
    "Tambi$c`n" = "También"
    "tambi$c`n" = "también"
    "Est$c`" = "Está"
    "est$c`" = "está"
    "S$c`" = "Sí"
    "s$c`" = "sí"
    "Opciones de Gesti$c`n" = "Opciones de Gestión"
    "Previsualizaci$c`n" = "Previsualización"
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
        
        # General pass for remaining common words if they slipped through
        $content = $content.Replace("Descripci$c", "Descripción")
        $content = $content.Replace("Informaci$c", "Información")
        $content = $content.Replace("Ubicaci$c", "Ubicación")
        $content = $content.Replace("Direcci$c", "Dirección")
        $content = $content.Replace("Configuraci$c", "Configuración")
        $content = $content.Replace("Validaci$c", "Validación")
        $content = $content.Replace("conexi$c", "conexión")
        $content = $content.Replace("secci$c", "sección")
        $content = $content.Replace("acci$c", "acción")
        $content = $content.Replace("Tel$c", "Telé")
        
        Set-Content -Path $f.FullName -Value $content -Encoding UTF8
    }
}
Write-Output "Done script"
