---
page_type: sample
languages:
- java
- html
products:
- azure-active-directory
Issues:
- https://github.com/Azure/azure-sdk-for-java/issues/28319
---

# OAuth 2.0 Example with Azure AD to reproduce problem with logout

## Build

* Enviroment: Win10
* Java: OpenJDK 64-Bit Server VM 18.9 (build 11.0.10+9, mixed mode)
* Maven: Apache Maven 3.6.3

checkout code and run mvn spring-boot:run

## Reproduce error

* run application
* open browser at localhost:8080
* You will be redirect to Microsoft site
* Microsoft will redirect back to localhost:8080
* Try to hit logout button

### Expected

The application must redirect to Microsoft site to logout procedure
