package today.todaysentence;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class Log4j2Test {

    public static void main(String[] args) {
        log.info("INFO: @Log4j2 테스트 메시지입니다.");
        log.warn("WARN: @Log4j2 경고 메시지입니다.");
        log.error("ERROR: @Log4j2 에러 메시지입니다.");

        System.out.println("Log4j2 테스트 완료");
    }
}
