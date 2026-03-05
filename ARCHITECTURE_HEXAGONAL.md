# Arquitectura Hexagonal (Backend)

## Objetivo
Organizar el backend en puertos y adaptadores para desacoplar la logica del framework.

## Estructura aplicada
- `com.monkeyclub.gym.application.port.in.*`
  - Puertos de entrada (use cases) por feature.
- `com.monkeyclub.gym.features.*.*Controller`
  - Adaptadores de entrada HTTP (REST).
  - Dependen de interfaces `UseCase`, no de implementaciones concretas.
- `com.monkeyclub.gym.features.*.*Service`
  - Aplicacion / casos de uso.
  - Implementan los puertos de entrada.
- `com.monkeyclub.gym.features.*.*Repository`
  - Adaptadores de salida hacia persistencia (JPA/PostgreSQL).
- `com.monkeyclub.gym.features.*` (entidades/enums/dto)
  - Modelo de cada feature.

## Puertos de entrada creados
- `AuthUseCase`
- `UserUseCase`
- `ClientUseCase`
- `PlanUseCase`
- `MembershipUseCase`
- `ProductUseCase`
- `SaleUseCase`
- `CashUseCase`
- `AttendanceUseCase`
- `ReportUseCase`
- `NotificationUseCase`

## Nota practica
Esta es una hexagonal pragmatica: el siguiente paso natural es introducir `application.port.out.*` para reemplazar dependencias directas de `Repository` dentro de los servicios.
