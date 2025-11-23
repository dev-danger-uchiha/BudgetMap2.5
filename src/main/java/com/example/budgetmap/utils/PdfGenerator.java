package com.example.budgetmap.utils;

import freemarker.template.Template;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import jakarta.servlet.http.HttpServletResponse;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.OutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilidad para generar archivos PDF a partir de plantillas FreeMarker.
 */
@Component
public class PdfGenerator {

    private final FreeMarkerConfigurer configurer;

    public PdfGenerator(FreeMarkerConfigurer configurer) {
        this.configurer = configurer;
    }

    /**
     * Genera un archivo PDF a partir de una plantilla FreeMarker y una lista de
     * datos.
     *
     * @param templateName nombre de la plantilla (sin extensión) que se usará para
     *                     generar el PDF
     * @param datos        lista de objetos que se inyectarán en la plantilla
     * @param desde        fecha desde (puede ser null)
     * @param hasta        fecha hasta (puede ser null)
     * @param response     objeto HttpServletResponse donde se escribirá el PDF
     *                     generado
     * @throws Exception si ocurre algún error durante la carga de la plantilla o la
     *                   generación del PDF
     */
    public void generarPdf(String templateName, List<?> datos, LocalDate desde, LocalDate hasta,
            HttpServletResponse response) throws Exception {

        Map<String, Object> model = new HashMap<>();
        model.put("clientes", datos);
        model.put("desde", desde);
        model.put("hasta", hasta);

        Template template = configurer.getConfiguration().getTemplate(templateName + ".html");
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"documento.pdf\"");

        // Determinar base URL para recursos relativos (css, img). Intenta cargar desde
        // /templates o classpath root.
        String baseUrl = determineBaseUrl();

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html, baseUrl);
        renderer.layout();

        try (OutputStream out = response.getOutputStream()) {
            renderer.createPDF(out);
            out.flush();
        }
    }

    private String determineBaseUrl() {
        // Intenta obtener la carpeta templates en classpath para que rutas relativas
        // funcionen (p.ej. <link href="css/style.css">)
        try {
            URL templatesUrl = this.getClass().getResource("/templates/");
            if (templatesUrl != null) {
                return templatesUrl.toString();
            }
        } catch (Exception ignored) {
        }

        // Fallback al classpath root
        try {
            URL root = this.getClass().getResource("/");
            if (root != null) {
                return root.toString();
            }
        } catch (Exception ignored) {
        }

        // Si no existe, dejar vacío; ITextRenderer intentará resolver rutas absolutas
        return "";
    }
}
