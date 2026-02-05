@"
Running dev profile (H2)
"@ | Write-Host

& .\mvnw.cmd "spring-boot:run" "-Dspring-boot.run.profiles=dev"
