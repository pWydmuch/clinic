INSERT INTO doctors(id, name, surname, age, pesel, specialization)
VALUES (1, 'Jan', 'Kowalski', 30, 99999999999, 'cardiologist');

INSERT INTO patients(id, name, surname, age, pesel)
VALUES (1, 'Jan', 'Nowak', 23, 88888888888);

INSERT INTO appointments(patient_id, doctor_id, date, status)
VALUES (1, 1, '2004-10-19 10:23:54', 'SCHEDULED');
