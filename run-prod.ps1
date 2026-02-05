@"
Running prod profile (MySQL)
"@ | Write-Host

& .\mvnw.cmd "spring-boot:run" "-Dspring-boot.run.profiles=prod"
