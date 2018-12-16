package email;

/**
 * Created by chaojiewang on 11/18/17.
 */
public interface IEmailProxy {
    void sendEmail(String to, String subject, String content) throws Exception;
}
