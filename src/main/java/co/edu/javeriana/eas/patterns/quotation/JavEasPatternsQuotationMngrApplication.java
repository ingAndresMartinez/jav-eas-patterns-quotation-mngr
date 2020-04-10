package co.edu.javeriana.eas.patterns.quotation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"co.edu.javeriana.eas", "co"})
public class JavEasPatternsQuotationMngrApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavEasPatternsQuotationMngrApplication.class, args);
	}

}
