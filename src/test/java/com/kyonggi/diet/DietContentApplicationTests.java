package com.kyonggi.diet;

import com.kyonggi.diet.translation.OpenAiClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class DietContentApplicationTests {

    @Test
    @Disabled("CI 환경에서는 외부 Bean 때문에 실패할 수 있어 제외")
    void contextLoads() {
    }
}

