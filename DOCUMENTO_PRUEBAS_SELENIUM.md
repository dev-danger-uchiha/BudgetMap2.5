# Documento de Pruebas Automatizadas con Selenium
## Proyecto: BudgetMap

Este documento detalla exclusivamente los **casos de prueba funcionales de interfaz gráfica (UI)** automatizados con **Selenium WebDriver (Java)** y **Selenium IDE** para las vistas específicas de las que se tomó evidencia visual. Se excluyen del alcance pruebas unitarias y pruebas de backend API.

---

## 1. Landing Page y Contenido Público
**Objetivo:** Verificar la correcta carga de la página de inicio, visibilidad de los carruseles y la navegación inicial.

![Landing Page (Home)](C:\Users\dange\.gemini\antigravity-ide\brain\8da6e3ab-9d9b-4904-8a1d-d2050492fa97\landing_page_full_1782096726150.png)

### Código Selenium WebDriver (Java)
```java
@Test
public void testLandingPageLoad() {
    driver.get("https://budgetmap-api.onrender.com/index.html");
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
    // Verificar título
    assertEquals("BudgetMap - Inicio", driver.getTitle());
    
    // Verificar que el contenedor principal o carrusel es visible
    WebElement carousel = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("hero-carousel")));
    assertTrue(carousel.isDisplayed());
    
    // Verificar botón de registro (Call to Action)
    WebElement ctaBtn = driver.findElement(By.cssSelector(".btn-cta-register"));
    assertTrue(ctaBtn.isDisplayed());
}
```

### Script Selenium IDE
| Command | Target | Value |
|---------|--------|-------|
| `open` | `/index.html` | |
| `setWindowSize` | `1920x1080` | |
| `assertTitle` | `BudgetMap - Inicio` | |
| `waitForElementVisible` | `id=hero-carousel` | `10000` |
| `assertElementPresent` | `css=.btn-cta-register` | |

---

## 2. Leaderboard Público
**Objetivo:** Verificar que la tabla de clasificación pública cargue correctamente los top usuarios.

![Leaderboard](C:\Users\dange\.gemini\antigravity-ide\brain\8da6e3ab-9d9b-4904-8a1d-d2050492fa97\evidencia_leaderboard_1782096801737.png)

### Código Selenium WebDriver (Java)
```java
@Test
public void testLeaderboardLoad() {
    driver.get("https://budgetmap-api.onrender.com/info/leaderboard.html");
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
    // Verificar que la tabla de posiciones existe
    WebElement table = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("leaderboard-table")));
    assertTrue(table.isDisplayed());
    
    // Verificar que al menos hay un registro en la tabla
    List<WebElement> rows = driver.findElements(By.cssSelector("#leaderboard-table tbody tr"));
    assertTrue(rows.size() > 0, "El leaderboard debe contener usuarios");
}
```

### Script Selenium IDE
| Command | Target | Value |
|---------|--------|-------|
| `open` | `/info/leaderboard.html` | |
| `waitForElementVisible` | `id=leaderboard-table` | `10000` |
| `assertElementPresent` | `css=#leaderboard-table tbody tr` | |

---

## 3. Módulo de Autenticación: Login
**Objetivo:** Validar que el formulario de inicio de sesión renderice y acepte credenciales.

![Login Page](C:\Users\dange\.gemini\antigravity-ide\brain\8da6e3ab-9d9b-4904-8a1d-d2050492fa97\evidencia_login_1782096769411.png)

### Código Selenium WebDriver (Java)
```java
@Test
public void testLoginFormSubmission() {
    driver.get("https://budgetmap-api.onrender.com/login.html");
    
    // Llenar formulario
    driver.findElement(By.id("email")).sendKeys("test@budgetmap.com");
    driver.findElement(By.id("password")).sendKeys("Password123!");
    
    // Enviar formulario
    driver.findElement(By.id("btn-login")).click();
    
    // Esperar redirección al dashboard (indicador de éxito)
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    wait.until(ExpectedConditions.urlContains("dashboard.html"));
    assertTrue(driver.getCurrentUrl().contains("dashboard.html"));
}
```

### Script Selenium IDE
| Command | Target | Value |
|---------|--------|-------|
| `open` | `/login.html` | |
| `type` | `id=email` | `test@budgetmap.com` |
| `type` | `id=password` | `Password123!` |
| `click` | `id=btn-login` | |
| `waitForElementPresent` | `css=.dashboard-container` | `5000` |

---

## 4. Módulo de Autenticación: Registro
**Objetivo:** Comprobar la estructura del formulario de creación de nuevas cuentas.

![Register Page](C:\Users\dange\.gemini\antigravity-ide\brain\8da6e3ab-9d9b-4904-8a1d-d2050492fa97\evidencia_registro_1782096778629.png)

### Código Selenium WebDriver (Java)
```java
@Test
public void testRegisterFormValidation() {
    driver.get("https://budgetmap-api.onrender.com/register.html");
    
    // Dejar vacío y enviar para testear validación HTML5 o JS
    WebElement btnSubmit = driver.findElement(By.id("btn-register"));
    btnSubmit.click();
    
    // Buscar mensajes de error de validación
    WebElement errorMsg = driver.findElement(By.cssSelector(".invalid-feedback"));
    assertTrue(errorMsg.isDisplayed());
}
```

### Script Selenium IDE
| Command | Target | Value |
|---------|--------|-------|
| `open` | `/register.html` | |
| `click` | `id=btn-register` | |
| `assertElementPresent` | `css=.invalid-feedback` | |

---

## 5. Módulo de Autenticación: Recuperar Contraseña
**Objetivo:** Verificar la funcionalidad de solicitud de restablecimiento de clave.

![Recuperar Contraseña](C:\Users\dange\.gemini\antigravity-ide\brain\8da6e3ab-9d9b-4904-8a1d-d2050492fa97\evidencia_recuperar_1782096787219.png)

### Código Selenium WebDriver (Java)
```java
@Test
public void testPasswordRecoveryRequest() {
    driver.get("https://budgetmap-api.onrender.com/recuperar-password.html");
    
    driver.findElement(By.id("emailRecovery")).sendKeys("usuario@ejemplo.com");
    driver.findElement(By.id("btn-recover")).click();
    
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("msg-success")));
    
    assertTrue(successMsg.getText().contains("correo enviado"));
}
```

### Script Selenium IDE
| Command | Target | Value |
|---------|--------|-------|
| `open` | `/recuperar-password.html` | |
| `type` | `id=emailRecovery` | `usuario@ejemplo.com` |
| `click` | `id=btn-recover` | |
| `waitForElementVisible` | `id=msg-success` | `5000` |

---

## 6. Páginas Informativas: FAQ
**Objetivo:** Comprobar que los acordeones de preguntas frecuentes se desplieguen correctamente.

![FAQ - Preguntas Frecuentes](C:\Users\dange\.gemini\antigravity-ide\brain\8da6e3ab-9d9b-4904-8a1d-d2050492fa97\evidencia_faq_1782096793956.png)

### Código Selenium WebDriver (Java)
```java
@Test
public void testFaqAccordion() {
    driver.get("https://budgetmap-api.onrender.com/info/faq.html");
    
    // Clic en la primera pregunta
    WebElement firstQuestion = driver.findElement(By.cssSelector(".accordion-button:first-of-type"));
    firstQuestion.click();
    
    // Esperar a que el texto de la respuesta sea visible
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
    WebElement firstAnswer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".accordion-collapse.show .accordion-body")));
    
    assertTrue(firstAnswer.isDisplayed());
}
```

### Script Selenium IDE
| Command | Target | Value |
|---------|--------|-------|
| `open` | `/info/faq.html` | |
| `click` | `css=.accordion-button:nth-child(1)` | |
| `waitForElementVisible` | `css=.accordion-collapse.show` | `3000` |

---

## 7. Páginas Informativas: PQRS Público
**Objetivo:** Verificar que el formulario público de peticiones cargue y se pueda interactuar.

![PQRS Público](C:\Users\dange\.gemini\antigravity-ide\brain\8da6e3ab-9d9b-4904-8a1d-d2050492fa97\evidencia_pqrs_1782096811166.png)

### Código Selenium WebDriver (Java)
```java
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
```

### Script Selenium IDE
| Command | Target | Value |
|---------|--------|-------|
| `open` | `/info/pqrs.html` | |
| `select` | `id=tipoPqrs` | `value=RECLAMO` |
| `type` | `id=asunto` | `Inconveniente con reserva` |
| `type` | `id=descripcion` | `El local estaba cerrado.` |
| `assertElementPresent` | `id=btn-submit-pqrs` | |

---

## 8. Páginas Informativas: Nosotros
**Objetivo:** Verificar que la página de identidad visual e información corporativa responda 200 OK y renderice texturas.

![Nosotros](C:\Users\dange\.gemini\antigravity-ide\brain\8da6e3ab-9d9b-4904-8a1d-d2050492fa97\evidencia_nosotros_1782096818554.png)

### Código Selenium WebDriver (Java)
```java
@Test
public void testNosotrosPageLoad() {
    driver.get("https://budgetmap-api.onrender.com/info/nosotros.html");
    
    // Verificar que se muestre la sección del equipo o misión
    WebElement visionSection = driver.findElement(By.id("vision-mision"));
    assertTrue(visionSection.isDisplayed());
}
```

### Script Selenium IDE
| Command | Target | Value |
|---------|--------|-------|
| `open` | `/info/nosotros.html` | |
| `assertElementPresent` | `id=vision-mision` | |

---

## 9. Manejo de Errores: 403 (Acceso Denegado)
**Objetivo:** Verificar que la interfaz de error por falta de permisos (JWT inválido/Rol incorrecto) se muestre correctamente.

![Error 403 - Acceso Denegado](C:\Users\dange\.gemini\antigravity-ide\brain\8da6e3ab-9d9b-4904-8a1d-d2050492fa97\evidencia_403_1782096833187.png)

### Código Selenium WebDriver (Java)
```java
@Test
public void testError403Render() {
    // Forzando el acceso a una vista prohibida simulando un retorno 403
    driver.get("https://budgetmap-api.onrender.com/error/403.html");
    
    WebElement errorTitle = driver.findElement(By.cssSelector("h1.error-code"));
    assertEquals("403", errorTitle.getText());
    
    WebElement backBtn = driver.findElement(By.id("btn-back-home"));
    assertTrue(backBtn.isDisplayed());
    backBtn.click();
    
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    wait.until(ExpectedConditions.urlContains("index.html"));
}
```

### Script Selenium IDE
| Command | Target | Value |
|---------|--------|-------|
| `open` | `/error/403.html` | |
| `assertText` | `css=h1.error-code` | `403` |
| `click` | `id=btn-back-home` | |
| `waitForElementPresent` | `id=hero-carousel` | `5000` |

---

## 10. Manejo de Errores: 404 (Página No Encontrada)
**Objetivo:** Verificar que las rutas inexistentes redirijan/muestren la UI personalizada de 404.

![Error 404 - Página No Encontrada](C:\Users\dange\.gemini\antigravity-ide\brain\8da6e3ab-9d9b-4904-8a1d-d2050492fa97\evidencia_404_1782096825529.png)

### Código Selenium WebDriver (Java)
```java
@Test
public void testError404Render() {
    driver.get("https://budgetmap-api.onrender.com/ruta-que-no-existe-12345");
    
    // Si el router del servidor redirige a 404.html
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    wait.until(ExpectedConditions.urlContains("404"));
    
    WebElement errorTitle = driver.findElement(By.cssSelector("h1.error-code"));
    assertEquals("404", errorTitle.getText());
}
```

### Script Selenium IDE
| Command | Target | Value |
|---------|--------|-------|
| `open` | `/ruta-que-no-existe-12345` | |
| `waitForText` | `css=h1.error-code` | `404` |
| `assertElementPresent` | `id=btn-back-home` | |

