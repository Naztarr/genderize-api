package com.naz.profiler;

import com.naz.profiler.security.JwtService;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ProfilerApplicationTests {
	@MockitoBean // Changed from MockBean
	private ProxyManager<String> proxyManager;

	@MockitoBean // Changed from MockBean
	private JwtService jwtService;

	@Test
	void contextLoads() {
	}

}
