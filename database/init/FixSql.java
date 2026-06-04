import java.nio.file.*;
import java.util.regex.*;

public class FixSql {
    public static void main(String[] args) throws Exception {
        Path path = Paths.get("02-data.sql");
        String content = new String(Files.readAllBytes(path), "UTF-8");
        
        String[] tablesToFix = {"planes_suscripcion", "usuarios", "lugares", "eventos"};
        
        String[] parts = content.split("(?=INSERT INTO)");
        StringBuilder sb = new StringBuilder();
        
        for (String part : parts) {
            boolean needsFix = false;
            for (String table : tablesToFix) {
                if (part.startsWith("INSERT INTO " + table)) {
                    needsFix = true;
                    break;
                }
            }
            if (needsFix) {
                part = part.replaceAll(",\\s*1\\)", ")");
            }
            sb.append(part);
        }
        
        Files.write(path, sb.toString().getBytes("UTF-8"));
        System.out.println("Archivo 02-data.sql corregido exitosamente.");
    }
}
