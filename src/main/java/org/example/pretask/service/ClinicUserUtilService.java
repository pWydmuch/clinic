package org.example.pretask.service;

public interface ClinicUserUtilService {

    void checkIfUserAlreadyExists(String login, Long pesel);
    String encodePassword(String password);
}
