package br.com.sildu.ponto.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(value = "integrationtest")
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class PontoInteligenteApplicationTests {

	@Test
	public void contextLoads() {
	}

}
