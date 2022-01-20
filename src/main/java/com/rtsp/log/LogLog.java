package com.rtsp.log;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class LogLog {

	@GetMapping("/testlog")
	public void testlog() {
		log.info("------info------");
    	log.warn("------warn------");
    	log.error("------error------");
    	log.debug("------debug------");
    	log.trace("------trace------");
	}
	
	
	@GetMapping("/testlogMethod")
	public void testlogMethod() {
		log.info("------info------");
    	log.warn("------warn------");
    	log.error("------error------");
	}
}
