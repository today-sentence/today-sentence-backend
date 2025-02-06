package today.todaysentence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
@EnableAspectJAutoProxy
public class TodaySentenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodaySentenceApplication.class, args);
    }

}
