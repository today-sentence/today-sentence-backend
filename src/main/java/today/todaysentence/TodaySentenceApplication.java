package today.todaysentence;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@EnableScheduling
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableJpaAuditing
public class TodaySentenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodaySentenceApplication.class, args);
    }

    @PostConstruct
    public void setTimeZone(){
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

}
