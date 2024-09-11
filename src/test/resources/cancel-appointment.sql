INSERT INTO doctors(id, name, surname, age, pesel, specialization)
VALUES (1, 'Jan', 'Kowalski', 30, 99999999999, 'cardiologist');

INSERT INTO patients(id, name, surname, age, pesel)
VALUES (1, 'Jan', 'Nowak', 23, 88888888888),
       (2, 'Michal', 'Nowak', 43, 77777777777);

INSERT INTO appointments(id, patient_id, doctor_id, date, status)
VALUES (1, 1, 1, '2004-10-19 10:23:54', 'SCHEDULED'),
       (2, 2, 1, '2004-11-19 10:23:54', 'SCHEDULED'),
       (3, 1, 1, '2004-12-19 10:23:54', 'CANCELLED');
