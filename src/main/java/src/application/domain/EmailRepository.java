package src.application.domain;

public interface EmailRepository {

    public boolean sendEmail(Email email);
}
