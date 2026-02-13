#  Restaurant Management System

![Java](https://img.shields.io/badge/Java-17%2B-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-17-blue?style=for-the-badge&logo=java)
![Hibernate](https://img.shields.io/badge/Hibernate-6.6-red?style=for-the-badge)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14%2B-blue?style=for-the-badge&logo=postgresql)
![Maven](https://img.shields.io/badge/Maven-3.8.1-C71A36?style=for-the-badge&logo=apache-maven)

Sistem complet de gestiune pentru restaurante, dezvoltat ca proiect academic pentru disciplina **Medii și Instrumente de Programare**. Aplicația acoperă întreg fluxul de business, de la vizualizarea meniului de către clienți, până la managementul personalului și exportul datelor.

---

##  Arhitectură Sistem (MVC)

[cite_start]Proiectul respectă cu strictețe pattern-ul **Model-View-Controller**, asigurând o separare clară a responsabilităților: [cite: 275, 276]

* [cite_start]**Model**: Entități JPA (`Product`, `User`, `Order`) și Repository-uri pentru accesul la date[cite: 193, 276].
* [cite_start]**View**: Interfețe grafice reactive dezvoltate în JavaFX (fără FXML, conform cerințelor de implementare programatică)[cite: 168, 169].
* [cite_start]**Controller**: Logica de business care coordonează fluxul de date între baza de date și interfață[cite: 276].

---

## Funcționalități Principale

### Securitate și Roluri
[cite_start]Aplicația dispune de un ecran de **Login** care direcționează utilizatorul în funcție de rolul său[cite: 228, 276]:
* [cite_start]**GUEST**: Vizualizare meniu, căutare (`Optional`) și filtrare (`Java Streams`) [cite: 231-238, 276].
* [cite_start]**STAFF (Ospătar)**: Gestiunea meselor, preluarea comenzilor și aplicarea automată a ofertelor [cite: 241-253, 276].
* [cite_start]**ADMIN (Manager)**: Control total asupra personalului (CRUD), meniului și setărilor de oferte [cite: 256-271, 276].

###  Motor de Oferte (Strategy Pattern)
[cite_start]Calculul totalului include aplicarea automată a regulilor de discount[cite: 70, 81]:
* [cite_start]**Happy Hour**: Reducere de 50% la fiecare a doua băutură comandată[cite: 213, 267].
* [cite_start]**Meal Deal**: Reducere la cel mai ieftin desert dacă s-a comandat o Pizza[cite: 214, 268].
* [cite_start]**Party Pack**: La 4 Pizza comandate, una este din partea casei[cite: 215, 269].

### Persistență și Concurrență
* [cite_start]**Bază de Date**: Integrare completă cu **PostgreSQL** prin Hibernate (JPA)[cite: 182, 193].
* [cite_start]**Export/Import**: Funcționalități de backup prin fișiere **JSON** (Gson) [cite: 128-137, 193].
* [cite_start]**Performanță**: Operațiuni asincrone (`Task` & `ExecutorService`) pentru a preveni blocarea interfeței ("Not Responding") în timpul încărcărilor mari [cite: 287-299].

---

##  Structura Proiectului

Aplicația este organizată modular, respectând principiile de separare a responsabilităților (Separation of Concerns):

```text
src/main/java/org/example/
├──  controller/  # Logica de control (Admin, Staff, Login)
├──  model/       # Entități JPA și ierarhia de produse (Sealed/Abstract)
├──  repository/  # Stratul de date (JPA/Hibernate)
├──  util/         # Helperi JSON (Configurații și Export)
├──  view/         # Componente UI JavaFX (construite programatic)
└──  Launch.java   # Punctul de intrare în aplicație
