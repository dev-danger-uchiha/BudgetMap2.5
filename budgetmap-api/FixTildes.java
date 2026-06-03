import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public class FixTildes {
    public static void main(String[] args) throws IOException {
        Path start = Paths.get("src/main/resources/static");
        Files.walk(start)
             .filter(Files::isRegularFile)
             .filter(p -> p.toString().endsWith(".html"))
             .forEach(p -> {
                 try {
                     String content = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
                     boolean changed = false;
                     
                     String[] bad = { "Descripcin", "Trminos", "Telfono", "Mximo", "Ubicacin", "Direccin", "Gestin", "Informacin", "Configuracin", "Contrasea", "Validacin", "Prximos", "conexin", "crtico", "vlida", "CDIGO", "Cdigo", "PsBLICO", "Aadir", "Reserva", "Informacin", "Categora", "categora", "Ttulo", "ttulo", "Opciones de Gestin", "Previsualizacin" };
                     String[] good = { "Descripción", "Términos", "Teléfono", "Máximo", "Ubicación", "Dirección", "Gestión", "Información", "Configuración", "Contraseña", "Validación", "Próximos", "conexión", "crítico", "válida", "CÓDIGO", "Código", "PÚBLICO", "Añadir", "¡Reserva", "¡Información", "Categoría", "categoría", "Título", "título", "Opciones de Gestión", "Previsualización" };

                     for (int i=0; i<bad.length; i++) {
                         if (content.contains(bad[i])) {
                             content = content.replace(bad[i], good[i]);
                             changed = true;
                         }
                     }
                     
                     char c = '\uFFFD';
                     String[] badC = { "Descripci"+c+"n", "Descripci"+c, "T"+c+"rminos", "Tel"+c+"fono", "M"+c+"ximo", "Ubicaci"+c+"n", "Direcci"+c+"n", "Gesti"+c+"n", "Informaci"+c+"n", "Configuraci"+c+"n", "Contrase"+c+"a", "Validaci"+c+"n", "Pr"+c+"ximos", "conexi"+c+"n", "cr"+c+"tico", "v"+c+"lida", "C"+c+"DIGO", "C"+c+"digo", "P"+c+"sBLICO", "A"+c+"adir", c+"Reserva", c+"Informaci"+c+"n", "C"+c+"\"DIGO", "Categor"+c+"a", "categor"+c+"a", "Acci"+c+"n", "acci"+c+"n", "secci"+c+"n", "Secci"+c+"n", "T"+c+"tulo", "t"+c+"tulo", "Opciones de Gesti"+c+"n", "Previsualizaci"+c+"n" };
                     String[] goodC = { "Descripción", "Descripción", "Términos", "Teléfono", "Máximo", "Ubicación", "Dirección", "Gestión", "Información", "Configuración", "Contraseña", "Validación", "Próximos", "conexión", "crítico", "válida", "CÓDIGO", "Código", "PÚBLICO", "Añadir", "¡Reserva", "¡Información", "CÓDIGO", "Categoría", "categoría", "Acción", "acción", "sección", "Sección", "Título", "título", "Opciones de Gestión", "Previsualización" };

                     for (int i=0; i<badC.length; i++) {
                         if (content.contains(badC[i])) {
                             content = content.replace(badC[i], goodC[i]);
                             changed = true;
                         }
                     }

                     String[] badC2 = { c+"Y\"?", c+"Y\".", c+"Y??", c+"YZY?" };
                     String[] goodC2 = { "📍", "📅", "🏁", "🎫" };
                     for (int i=0; i<badC2.length; i++) {
                         if (content.contains(badC2[i])) {
                             content = content.replace(badC2[i], goodC2[i]);
                             changed = true;
                         }
                     }

                     if (changed) {
                         Files.write(p, content.getBytes(StandardCharsets.UTF_8));
                         System.out.println("Fixed " + p);
                     }
                 } catch (Exception e) {}
             });
        System.out.println("Done");
    }
}
