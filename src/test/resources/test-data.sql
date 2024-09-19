INSERT INTO users(id, dtype, name, surname, age, pesel, specialization, login, password)
VALUES (1, 'doctor', 'Jan', 'Kowalski', 30, 99999999999, 'cardiologist', 'login-doctor',
        '$2a$10$sT1ZI5pMJJsDe1tR2.6TjOBWi7VjixOdfRMvel2zjqJt/MiEFgi2.'),
       (4, 'doctor', 'Jacek', 'Kowalski', 30, 66666666666, 'psychiatrist', 'other-doc', '122');

INSERT INTO users(id, dtype, name, surname, age, pesel, login, password)
VALUES (2, 'patient', 'Jan', 'Nowak', 23, 88888888888, 'login',
        '$2a$10$sT1ZI5pMJJsDe1tR2.6TjOBWi7VjixOdfRMvel2zjqJt/MiEFgi2.'),
       (3, 'patient', 'Michal', 'Nowak', 43, 77777777777, 'login-taken', '12334');

INSERT INTO appointments(id, patient_id, doctor_id, date, status)
VALUES (1, 2, 1, '2016-03-16 13:56:39.492000', 'SCHEDULED'),
       (2, 3, 1, '2016-03-17 13:56:39.492000', 'SCHEDULED'),
       (3, 2, 1, '2016-03-18 13:56:39.492000', 'CANCELLED'),
       (4, 1, 4, '2016-03-19 13:56:39.492000', 'SCHEDULED');
