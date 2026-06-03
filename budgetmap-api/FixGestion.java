import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public class FixGestion {
    public static void main(String[] args) throws IOException {
        Path start = Paths.get("src/main/resources/static/aliado");
        Files.walk(start)
             .filter(Files::isRegularFile)
             .filter(p -> p.toString().endsWith(".html"))
             .forEach(p -> {
                 try {
                     String content = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
                     char c = '\uFFFD';
                     String target = "GESTI" + c + "N";
                     if (content.contains(target)) {
                         content = content.replace(target, "GESTIÓN");
                         Files.write(p, content.getBytes(StandardCharsets.UTF_8));
                         System.out.println("Fixed " + p);
                     }
                 } catch (Exception e) {}
             });
        System.out.println("Done");
    }
}
