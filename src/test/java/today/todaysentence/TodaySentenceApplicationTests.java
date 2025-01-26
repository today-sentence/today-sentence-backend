package today.todaysentence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TodaySentenceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Value("${spring.datasource.url}")
    private String url;

    @Test
    void test() {
        System.out.println("url value: " + url);
    }


}
