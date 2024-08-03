package co.ke.emtechhouse.UniversalNode.MailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailListener {
    @Autowired
    MailService mailService;

    @Value("${topic.name.consumer")
    private String topicName;

    @KafkaListener(topics = "${topic.name.consumer}", groupId = "group_id")
    public void getEmails(ConsumerRecord<String, String> payload) throws MessagingException {
        log.info("--------------------New Request at : "+ LocalDateTime.now() +"---------------------------");
        JSONObject jo = new JSONObject(payload.value());
        String toMail = jo.getString("email");
        String subject = jo.getString("subject");
        String message = jo.getString("message");
        mailService.sendEmail(toMail,subject,message);
        log.info("--------------------Sent Successfull at : "+ LocalDateTime.now() + "---------------------------");
    }
}
