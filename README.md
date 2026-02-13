#  Restaurant Management System

![Java](https://img.shields.io/badge/Java-17%2B-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-17-blue?style=for-the-badge&logo=java)
![Hibernate](https://img.shields.io/badge/Hibernate-6.6-red?style=for-the-badge)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14%2B-blue?style=for-the-badge&logo=postgresql)
![Maven](https://img.shields.io/badge/Maven-3.8.1-C71A36?style=for-the-badge&logo=apache-maven)

Sistem complet de gestiune pentru restaurante, dezvoltat ca proiect academic pentru disciplina **Medii și Instrumente de Programare**. Aplicația acoperă întreg fluxul de business, de la vizualizarea meniului de către clienți, până la managementul personalului și exportul datelor.

---

##  Arhitectură Sistem (MVC)

Proiectul respectă cu strictețe pattern-ul **Model-View-Controller**, asigurând o separare clară a responsabilităților: 

* **Model**: Entități JPA (`Product`, `User`, `Order`) și Repository-uri pentru accesul la date.
* **View**: Interfețe grafice reactive dezvoltate în JavaFX (fără FXML, conform cerințelor de implementare programatică).
* **Controller**: Logica de business care coordonează fluxul de date între baza de date și interfață.

---

## Funcționalități Principale

### Securitate și Roluri
Aplicația dispune de un ecran de **Login** care direcționează utilizatorul în funcție de rolul său:
* **GUEST**: Vizualizare meniu, căutare (`Optional`) și filtrare (`Java Streams`).
* **STAFF (Ospătar)**: Gestiunea meselor, preluarea comenzilor și aplicarea automată a ofertelor .
* **ADMIN (Manager)**: Control total asupra personalului (CRUD), meniului și setărilor de oferte.

###  Motor de Oferte (Strategy Pattern)
Calculul totalului include aplicarea automată a regulilor de discount:
* **Happy Hour**: Reducere de 50% la fiecare a doua băutură comandată.
* **Meal Deal**: Reducere la cel mai ieftin desert dacă s-a comandat o Pizza.
* **Party Pack**: La 4 Pizza comandate, una este din partea casei.

### Persistență și Concurrență
* **Bază de Date**: Integrare completă cu **PostgreSQL** prin Hibernate (JPA).
* **Export/Import**: Funcționalități de backup prin fișiere **JSON** (Gson).
* **Performanță**: Operațiuni asincrone (`Task` & `ExecutorService`) pentru a preveni blocarea interfeței ("Not Responding") în timpul încărcărilor mari .

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
