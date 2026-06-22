package com.budgetmap.ui;

// Importaciones de JUnit 5 (Framework de pruebas)
import org.junit.jupiter.api.AfterEach; // Anotación para ejecutar código DESPUÉS de cada prueba 
import org.junit.jupiter.api.BeforeEach; // Anotación para ejecutar código ANTES de cada prueba 
import org.junit.jupiter.api.Test; // Anotación que indica que un método es un caso de prueba ejecutable

// Importaciones base de Selenium WebDriver
import org.openqa.selenium.By; // Permite localizar elementos en el DOM--
import org.openqa.selenium.WebDriver; // Interfaz principal que representa el navegador web
import org.openqa.selenium.WebElement; // Representa un elemento HTML individual dentro de la página

// Importaciones específicas del navegador
import org.openqa.selenium.chrome.ChromeDriver; // Implementación del WebDriver específica para Google Chrome

// Importaciones para sincronización e interacción avanzada
import org.openqa.selenium.support.ui.ExpectedConditions; // Condiciones predefinidas para esperar (ej. que un elemento sea visible)
import org.openqa.selenium.support.ui.Select; // Utilidad para interactuar fácilmente con menús desplegables (<select>)
import org.openqa.selenium.support.ui.WebDriverWait; // Clase que implementa esperas explícitas (esperar un tiempo máximo)

// Utilidades estándar de Java
import java.time.Duration; // Manejo de duraciones en Java (usado para indicar tiempos de espera)
import java.util.List; // Interfaz estándar de Java para manejar listas de objetos

// Importaciones estáticas para aserciones (Validaciones)
import static org.junit.jupiter.api.Assertions.assertEquals; // Comprueba que un valor actual es igual a uno esperado
import static org.junit.jupiter.api.Assertions.assertTrue; // Comprueba que una condición o expresión sea verdadera

public class BasicUITests {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        // En Selenium 4.6+, Selenium Manager maneja el binario de ChromeDriver
        // automáticamente.
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testLandingPageLoad() {
        driver.get("https://budgetmap-api.onrender.com/index.html");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Verificar título
        assertEquals("BudgetMap - Inicio", driver.getTitle());

        // Verificar que el contenedor principal o carrusel es visible
        WebElement carousel = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("hero-carousel")));
        assertTrue(carousel.isDisplayed());

        // Verificar botón de registro
        WebElement ctaBtn = driver.findElement(By.cssSelector(".btn-cta-register"));
        assertTrue(ctaBtn.isDisplayed());
    }

    @Test
    public void testLeaderboardLoad() {
        driver.get("https://budgetmap-api.onrender.com/info/leaderboard.html");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement table = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("leaderboard-table")));
        assertTrue(table.isDisplayed());

        List<WebElement> rows = driver.findElements(By.cssSelector("#leaderboard-table tbody tr"));
        assertTrue(rows.size() > 0, "El leaderboard debe contener usuarios");
    }

    @Test
    public void testLoginFormSubmission() {
        driver.get("https://budgetmap-api.onrender.com/login.html");

        driver.findElement(By.id("email")).sendKeys("test@budgetmap.com");
        driver.findElement(By.id("password")).sendKeys("Password123!");

        driver.findElement(By.id("btn-login")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("dashboard.html"));
        assertTrue(driver.getCurrentUrl().contains("dashboard.html"));
    }

    @Test
    public void testRegisterFormValidation() {
        driver.get("https://budgetmap-api.onrender.com/register.html");

        WebElement btnSubmit = driver.findElement(By.id("btn-register"));
        btnSubmit.click();

        WebElement errorMsg = driver.findElement(By.cssSelector(".invalid-feedback"));
        assertTrue(errorMsg.isDisplayed());
    }

    @Test
    public void testPasswordRecoveryRequest() {
        driver.get("https://budgetmap-api.onrender.com/recuperar-password.html");

        driver.findElement(By.id("emailRecovery")).sendKeys("usuario@ejemplo.com");
        driver.findElement(By.id("btn-recover")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("msg-success")));

        assertTrue(successMsg.getText().contains("correo enviado"));
    }

    @Test
    public void testFaqAccordion() {
        driver.get("https://budgetmap-api.onrender.com/info/faq.html");

        WebElement firstQuestion = driver.findElement(By.cssSelector(".accordion-button:first-of-type"));
        firstQuestion.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        WebElement firstAnswer = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.cssSelector(".accordion-collapse.show .accordion-body")));

        assertTrue(firstAnswer.isDisplayed());
    }

    @Test
    public void testPqrsForm() {
        driver.get("https://budgetmap-api.onrender.com/info/pqrs.html");

        Select tipoPqrs = new Select(driver.findElement(By.id("tipoPqrs")));
        tipoPqrs.selectByValue("RECLAMO");

        driver.findElement(By.id("asunto")).sendKeys("Inconveniente con reserva");
        driver.findElement(By.id("descripcion")).sendKeys("El local estaba cerrado.");

        WebElement submitBtn = driver.findElement(By.id("btn-submit-pqrs"));
        assertTrue(submitBtn.isEnabled());
    }

    @Test
    public void testNosotrosPageLoad() {
        driver.get("https://budgetmap-api.onrender.com/info/nosotros.html");

        WebElement visionSection = driver.findElement(By.id("vision-mision"));
        assertTrue(visionSection.isDisplayed());
    }

    @Test
    public void testError403Render() {
        driver.get("https://budgetmap-api.onrender.com/error/403.html");

        WebElement errorTitle = driver.findElement(By.cssSelector("h1.error-code"));
        assertEquals("403", errorTitle.getText());

        WebElement backBtn = driver.findElement(By.id("btn-back-home"));
        assertTrue(backBtn.isDisplayed());
        backBtn.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("index.html"));
    }

    @Test
    public void testError404Render() {
        driver.get("https://budgetmap-api.onrender.com/ruta-que-no-existe-12345");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("404"));

        WebElement errorTitle = driver.findElement(By.cssSelector("h1.error-code"));
        assertEquals("404", errorTitle.getText());
    }
}
