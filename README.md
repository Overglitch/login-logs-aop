# Aplicación Web de Login con Logs de Seguridad usando Spring Boot y AOP

Este proyecto es una aplicación web simple de inicio de sesión construida con **Spring Boot**. La funcionalidad principal es permitir a los usuarios autenticarse y registrar los intentos de inicio de sesión fallidos mediante **AOP (Aspect-Oriented Programming)**. La interfaz de usuario está desarrollada con **Thymeleaf** y estilos básicos de **CSS**.

---

## **Características**

- Interfaz web para que los usuarios ingresen sus credenciales.
- Validación de credenciales con una base de datos simulada en memoria.
- Registro automático de intentos de inicio de sesión fallidos en los logs.
- Uso de AOP para separar la lógica de negocio de la funcionalidad de logging.

---

## **Requisitos Previos**

Antes de ejecutar este proyecto, asegúrate de tener lo siguiente instalado:

- **JDK 17** o superior.
- **Maven** para la gestión de dependencias.

---

## **Cómo Ejecutar el Proyecto**

1. Clona este repositorio:
   ```bash
   git clone https://github.com/Overglitch/login-logs-aop.git
   ```
2. Accede al directorio del proyecto:
   ```bash
   cd login-aop
   ```
3. Construye el proyecto usando Maven:
   ```bash
   mvn clean install
   ```
4. Inicia el servidor:
   ```bash
   mvn spring-boot:run
   ```
5. Abre tu navegador y accede a `http://localhost:8080`.

---

## **Arquitectura del Proyecto**

### **1. Controlador (`UserController`)**
El controlador maneja las solicitudes HTTP. Tiene dos funcionalidades principales:

- Mostrar el formulario de login.
- Procesar las credenciales enviadas por el usuario y devolver un resultado de éxito o error.

```java
@PostMapping("/login")
public String login(@RequestParam String username, 
                    @RequestParam String password, 
                    Model model) {
    boolean isAuthenticated = userService.authenticate(username, password);
    if (isAuthenticated) {
        model.addAttribute("message", "Login successful!");
        return "result";
    } else {
        model.addAttribute("message", "Invalid credentials!");
        return "result";
    }
}
```

### **2. Servicio (`UserService`)**
El servicio contiene la lógica de autenticación. Simula una base de datos en memoria donde se almacenan los usuarios y sus contraseñas.

- La función `authenticate` verifica si las credenciales proporcionadas son correctas.

```java
public boolean authenticate(String username, String password) {
    String storedPassword = userDatabase.get(username);
    return storedPassword != null && storedPassword.equals(password);
}
```

### **3. Aspecto (`SecurityAspect`)**
Este es el núcleo del uso de **AOP** en el proyecto. La clase `SecurityAspect` registra automáticamente los intentos de inicio de sesión fallidos.

#### ¿Qué es AOP?
**AOP (Programación Orientada a Aspectos)** permite separar preocupaciones transversales (como logging, seguridad o transacciones) de la lógica de negocio principal. Esto se logra mediante la creación de aspectos que interceptan puntos específicos en el flujo de ejecución del programa.

#### Implementación del Aspecto
- El aspecto intercepta el método `authenticate` del servicio.
- Usa la anotación `@AfterReturning` para capturar el resultado del método después de su ejecución.
- Si el resultado indica un fallo en la autenticación (`result == false`), se genera un log de advertencia.

```java
@Aspect
@Component
public class SecurityAspect {

    private static final Logger logger = LoggerFactory.getLogger(SecurityAspect.class);

    @AfterReturning(
        pointcut = "execution(* com.example.loginapp.service.UserService.authenticate(..)) && args(username, password)",
        returning = "result"
    )
    public void logFailedLogin(String username, String password, boolean result) {
        if (!result) {
            logger.warn("Failed login attempt for username: {}", username);
        }
    }
}
```

#### Explicación de las anotaciones:
1. **@Aspect**: Marca la clase como un aspecto.
2. **@Component**: Permite que Spring gestione esta clase como un bean.
3. **@AfterReturning**:
    - **pointcut**: Define en qué método se aplicará el aspecto. En este caso, el método `authenticate`.
    - **args(username, password)**: Permite capturar los argumentos del método interceptado.
    - **returning**: Captura el valor de retorno del método (`result`).

### **4. Interfaz Web**
La interfaz utiliza **Thymeleaf** para renderizar las vistas.

- `login.html`: Muestra el formulario de inicio de sesión.
- `result.html`: Muestra el resultado de la autenticación.

Ejemplo de `login.html`:
```html
<form action="/login" method="POST">
    <label for="username">Username:</label>
    <input type="text" id="username" name="username" required>
    <label for="password">Password:</label>
    <input type="password" id="password" name="password" required>
    <button type="submit">Login</button>
</form>
```

---

## **Logs de Seguridad Generados**

Cuando se produce un intento de inicio de sesión fallido, el aspecto `SecurityAspect` registra un mensaje en los logs del sistema:

```
2024-11-22 12:34:56 - Failed login attempt for username: user1
```

Esto permite a los administradores del sistema monitorear fácilmente los intentos sospechosos de inicio de sesión.

---

## **Beneficios del Uso de AOP**

1. **Separación de responsabilidades**:
    - La lógica de autenticación permanece en `UserService`.
    - Los logs de seguridad se manejan en `SecurityAspect`.

2. **Mantenibilidad**:
    - Es fácil modificar o ampliar la funcionalidad de logging sin alterar la lógica de negocio.

3. **Reutilización**:
    - El mismo aspecto puede usarse en diferentes métodos o servicios con un punto de corte ajustado.

---

## **Mejoras Futuras**

- Implementar bloqueo de cuenta después de varios intentos fallidos.
- Usar una base de datos real para almacenar usuarios.
- Enviar notificaciones por correo electrónico sobre intentos de inicio de sesión sospechosos.

---

¡Gracias por usar este proyecto! Si tienes alguna pregunta o sugerencia, no dudes en contribuir. 😊