INSERT INTO users(id, dtype, name, surname, age, pesel, specialization)
VALUES (1, 'doctor','Jan', 'Kowalski', 30, 99999999999, 'cardiologist');

INSERT INTO users(id, dtype, name, surname, age, pesel, login, password)
VALUES (2, 'patient', 'Jan', 'Nowak', 23, 88888888888, 'login',
        '$2a$10$sT1ZI5pMJJsDe1tR2.6TjOBWi7VjixOdfRMvel2zjqJt/MiEFgi2.');