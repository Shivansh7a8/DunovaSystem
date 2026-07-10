# mailform-backend

Spring Boot backend that, on form submission:
1. Sends a **thank-you email** to the person who submitted the form.
2. Sends a **notification email with the form details** to the company inbox.

Java 17 · Spring Boot 3.2.5 · Maven

## 1. Configure Brevo SMTP

Edit `src/main/resources/application.properties`:

```properties
spring.mail.username=YOUR_BREVO_SMTP_LOGIN
spring.mail.password=YOUR_BREVO_SMTP_KEY

app.mail.from=your-verified-sender@yourdomain.com
app.mail.company-inbox=company@yourdomain.com
```

Get your SMTP login and SMTP key from **Brevo Dashboard → SMTP & API → SMTP**.
`app.mail.from` must be a sender/domain verified in your Brevo account, otherwise sending will fail.

You can also override these via environment variables instead of editing the file, e.g.:

```bash
export SPRING_MAIL_USERNAME=your_brevo_login
export SPRING_MAIL_PASSWORD=your_brevo_key
export APP_MAIL_FROM=your-verified-sender@yourdomain.com
export APP_MAIL_COMPANY-INBOX=company@yourdomain.com
```

## 2. Run

```bash
mvn spring-boot:run
```

The app starts on `http://localhost:8080`.

## 3. Test the endpoint

`POST /api/contact`

```bash
curl -X POST http://localhost:8080/api/contact \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Shivansh",
    "email": "your-email@gmail.com",
    "company": "Dunova Systems",
    "message": "Testing Brevo SMTP"
  }'
```

### Success response
```json
{
  "status": "success",
  "message": "Thank you! Your message has been received and a confirmation email has been sent."
}
```

### Validation error response (400)
```json
{
  "email": "Email must be valid"
}
```

## Project structure

```
src/main/java/com/dunova/mailform
├── MailFormApplication.java          # main entry point
├── controller/ContactController.java # REST endpoint (/api/contact)
├── service/EmailService.java         # sends both emails via JavaMailSender
├── model/ContactRequest.java         # request DTO with validation
└── config/GlobalExceptionHandler.java# clean JSON error responses
```

## Notes
- Uses `spring-boot-starter-mail` (JavaMailSender) over SMTP — works with Brevo, Gmail App Passwords, or any SMTP provider by changing `spring.mail.*` properties.
- Both emails are sent synchronously in the request thread. For production, consider making `EmailService` async (`@Async`) or queue-backed so the API responds instantly even if the SMTP call is slow.
- CORS is not configured. If you're calling this from a frontend on a different origin, add a `WebMvcConfigurer` CORS mapping or `@CrossOrigin` on the controller.
