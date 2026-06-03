import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public class FixSyntax {
    public static void main(String[] args) throws IOException {
        Path start = Paths.get("src/main/resources/static");
        Files.walk(start)
             .filter(Files::isRegularFile)
             .filter(p -> p.toString().endsWith(".html"))
             .forEach(p -> {
                 try {
                     String content = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
                     boolean changed = false;
                     
                     if (content.contains("¡Reserva")) {
                         content = content.replace("¡Reserva", "Reserva");
                         changed = true;
                     }
                     if (content.contains("¡Información")) {
                         content = content.replace("¡Información", "Información");
                         changed = true;
                     }

                     if (changed) {
                         Files.write(p, content.getBytes(StandardCharsets.UTF_8));
                         System.out.println("Fixed syntax in " + p);
                     }
                 } catch (Exception e) {}
             });
        System.out.println("Syntax fix Done");
    }
}
