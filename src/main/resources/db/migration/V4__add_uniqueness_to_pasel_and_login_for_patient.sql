ALTER TABLE patients
    ADD CONSTRAINT uc_patients_login UNIQUE (login);

ALTER TABLE patients
    ADD CONSTRAINT uc_patients_pesel UNIQUE (pesel);