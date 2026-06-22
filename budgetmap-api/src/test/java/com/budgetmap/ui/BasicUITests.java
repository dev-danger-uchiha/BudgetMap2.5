package com.budgetmap.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BasicUITests {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Pausa de 5 segundos para rutas estáticas
    private void pausaEstatica() {
        try { Thread.sleep(5000); } catch (InterruptedException e) {}
    }

    // Pausa de 10 segundos para formularios y flujos interactivos
    private void pausaVisual() {
        try { Thread.sleep(10000); } catch (InterruptedException e) {}
    }

    // Método utilitario para hacer clics mediante JavaScript y evitar la excepción "ElementClickInterceptedException"
    private void clickWithJS(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    @Test
    @Order(1)
    public void testLandingPageLoad() {
        driver.get("https://budgetmap-api.onrender.com/index.html");
        
        // Inyectamos token temporal para que al dar clic en la tarjeta no nos bloquee la alerta de seguridad
        ((JavascriptExecutor) driver).executeScript("window.localStorage.setItem('jwt_token', 'token_dummy');");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        assertEquals("BudgetMap - Inicio", driver.getTitle());

        WebElement carousel = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("grid-destacados")));
        assertTrue(carousel.isDisplayed());

        // 1. Interacción en "Establecimientos" y "Lugares"
        WebElement btnEstablecimientos = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("btn-establecimientos")));
        clickWithJS(btnEstablecimientos);
        // Esperamos que termine el fetch
        wait.until(d -> !d.findElement(By.id("grid-destacados")).getText().contains("Cargando"));

        WebElement btnLugares = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("btn-lugares")));
        clickWithJS(btnLugares);
        // Esperamos que termine el fetch
        wait.until(d -> !d.findElement(By.id("grid-destacados")).getText().contains("Cargando"));

        // 2. Mover el carrusel mediante JS
        WebElement btnDerecha = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[onclick='deslizarCarrusel(1)']")));
        clickWithJS(btnDerecha);
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        // 3. Interactuar con las tarjetas (Destacados) - Clic en el botón "Ver más"
        List<WebElement> btnVerMas = driver.findElements(By.xpath("//button[contains(text(), 'Ver más')]"));
        if (!btnVerMas.isEmpty()) {
            clickWithJS(btnVerMas.get(0)); // Abre el modal
            
            // Esperar a que el modal abra y sea visible
            WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modal-destacado")));
            assertTrue(modal.isDisplayed());
            
            try { Thread.sleep(2000); } catch (InterruptedException e) {} // Pausa breve para ver el modal abierto
            
            // Cerrar el modal
            WebElement btnCerrarModal = driver.findElement(By.cssSelector("button[onclick='cerrarModalDestacado()']"));
            clickWithJS(btnCerrarModal);
        }

        // Limpiamos el localStorage para no afectar el flujo normal de registro y login que siguen
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");

        pausaVisual(); // 10s
    }

    @Test
    @Order(2)
    public void testRegisterFormValidation() {
        driver.get("https://budgetmap-api.onrender.com/register.html");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement roleCard = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".role-card")));
        clickWithJS(roleCard);

        WebElement btnSubmit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button[type='submit']")));
        
        driver.findElement(By.id("nombre")).sendKeys("Juan Selenium");
        driver.findElement(By.id("apellido")).sendKeys("Pérez");
        driver.findElement(By.id("email")).sendKeys("juan_selenium@budgetmap.com");
        driver.findElement(By.id("telefono")).sendKeys("3001234567");
        driver.findElement(By.id("password")).sendKeys("TestPassword123!");
        driver.findElement(By.id("confirmPassword")).sendKeys("TestPassword123!");

        clickWithJS(btnSubmit);

        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("alertContainer")));
        assertTrue(alert.isDisplayed());
        
        pausaVisual(); // 10s
    }

    @Test
    @Order(3)
    public void testLoginFormSubmission() {
        driver.get("https://budgetmap-api.onrender.com/login.html");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Inicia sesión con el MISMO usuario con el que se registra
        driver.findElement(By.id("email")).sendKeys("juan_selenium@budgetmap.com");
        driver.findElement(By.id("password")).sendKeys("TestPassword123!");

        WebElement btnSubmit = driver.findElement(By.cssSelector("button[type='submit']"));
        clickWithJS(btnSubmit);

        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("alertContainer")));
        assertTrue(alert.isDisplayed());
        
        pausaVisual(); // 10s
    }

    @Test
    @Order(4)
    public void testPasswordRecoveryRequest() {
        driver.get("https://budgetmap-api.onrender.com/recuperar-password.html");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.findElement(By.id("email")).sendKeys("juan_selenium@budgetmap.com");
        
        WebElement btnSubmit = driver.findElement(By.cssSelector("button[type='submit']"));
        clickWithJS(btnSubmit);

        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("alertContainer")));
        assertTrue(successMsg.isDisplayed());
        
        pausaVisual(); // 10s
    }

    @Test
    @Order(5)
    public void testPqrsForm() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // 1. Iniciar sesión REAL para obtener un token JWT válido de la base de datos
        driver.get("https://budgetmap-api.onrender.com/login.html");
        driver.findElement(By.id("email")).sendKeys("juan_selenium@budgetmap.com");
        driver.findElement(By.id("password")).sendKeys("TestPassword123!");
        clickWithJS(driver.findElement(By.cssSelector("button[type='submit']")));
        
        // Esperar a que el login procese y asigne el token
        wait.until(d -> ((JavascriptExecutor) d).executeScript("return window.localStorage.getItem('jwt_token');") != null);

        // 2. Entrar a PQRS
        driver.get("https://budgetmap-api.onrender.com/info/pqrs.html");

        WebElement selectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pqrs-tipo")));
        Select tipoPqrs = new Select(selectElement);
        tipoPqrs.selectByValue("RECLAMO");

        driver.findElement(By.id("pqrs-asunto")).sendKeys("Inconveniente automatizado");
        driver.findElement(By.id("pqrs-descripcion")).sendKeys("Prueba de envío de PQRS con usuario Juan Selenium tras Login.");

        WebElement btnEnviar = driver.findElement(By.id("btn-enviar"));
        clickWithJS(btnEnviar);
        
        // Esperar alerta (éxito o error)
        wait.until(d -> d.findElement(By.id("error-envio")).isDisplayed() || d.findElement(By.id("mensaje-exito")).isDisplayed());
        
        try { Thread.sleep(2000); } catch (InterruptedException e) {} 
        
        // 3. Cambiar a "Mis Solicitudes"
        WebElement tabHistorial = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("tab-historial")));
        clickWithJS(tabHistorial);
        
        // 4. Verificar que se cargue la sección "Mis Solicitudes / Historial"
        WebElement historial = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sec-historial")));
        assertTrue(historial.isDisplayed());

        // Esperar a que la lista termine de cargar los datos de la API
        wait.until(d -> {
            String text = d.findElement(By.id("pqrs-lista")).getText();
            return !text.contains("Cargando solicitudes...");
        });
        
        pausaVisual(); // 10s
    }

    @Test
    @Order(6)
    public void testFaqAccordion() {
        driver.get("https://budgetmap-api.onrender.com/info/faq.html");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        WebElement firstQuestion = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h3.text-emerald-700")));
        assertTrue(firstQuestion.isDisplayed());
        
        pausaEstatica(); // 5s
    }

    @Test
    @Order(7)
    public void testNosotrosPageLoad() {
        driver.get("https://budgetmap-api.onrender.com/info/nosotros.html");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement visionSection = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(text(), 'Por qué BudgetMap')]")));
        assertTrue(visionSection.isDisplayed());
        
        pausaEstatica(); // 5s
    }

    @Test
    @Order(8)
    public void testLeaderboardLoad() {
        driver.get("https://budgetmap-api.onrender.com/info/leaderboard.html");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement table = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("leaderboard-body")));
        assertTrue(table.isDisplayed());

        List<WebElement> rows = driver.findElements(By.cssSelector("#leaderboard-body tr"));
        assertTrue(rows.size() > 0, "El leaderboard debe contener usuarios o mensaje de carga");
        
        pausaEstatica(); // 5s
    }

    @Test
    @Order(9)
    public void testError403Render() {
        driver.get("https://budgetmap-api.onrender.com/error/403.html");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement errorTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(), 'Error 403')]")));
        assertTrue(errorTitle.isDisplayed());

        pausaEstatica(); // 5s

        // Botón regresar o navegación atrás
        List<WebElement> backBtns = driver.findElements(By.xpath("//a[contains(text(), 'Ir al Mapa')]"));
        if (!backBtns.isEmpty()) {
            clickWithJS(backBtns.get(0));
            wait.until(ExpectedConditions.urlToBe("https://budgetmap-api.onrender.com/"));
        } else {
            driver.navigate().back();
        }
    }

    @Test
    @Order(10)
    public void testError404Render() {
        driver.get("https://budgetmap-api.onrender.com/ruta-que-no-existe-12345");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        WebElement errorContent = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), '404')]")));
        assertTrue(errorContent.isDisplayed());
        
        pausaEstatica(); // 5s

        // Botón regresar o navegación atrás (por si responde Spring Boot Whitelabel Page sin botón custom)
        List<WebElement> backBtns = driver.findElements(By.xpath("//a[contains(text(), 'Volver al Explorador')]"));
        if (!backBtns.isEmpty()) {
            clickWithJS(backBtns.get(0));
            wait.until(ExpectedConditions.urlToBe("https://budgetmap-api.onrender.com/"));
        } else {
            driver.navigate().back();
        }
    }
}
