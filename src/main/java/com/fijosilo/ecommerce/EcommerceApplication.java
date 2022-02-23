package com.fijosilo.ecommerce;

import com.fijosilo.ecommerce.authentication.Client;
import com.fijosilo.ecommerce.authentication.JPAClientRepository;
import com.fijosilo.ecommerce.authentication.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.UUID;

@SpringBootApplication
public class EcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceApplication.class, args);
	}

}

@Component
class InitApplication {
	private final ClientService clientService;
	private final PasswordEncoder passwordEncoder;

	private static final Logger log = LoggerFactory.getLogger(JPAClientRepository.class);

	@Value("${com.fijosilo.ecommerce.admin_email}")
	private String adminEmail;
	@Value("${com.fijosilo.ecommerce.admin_password}")
	private String adminPassword;

	@Autowired
	public InitApplication(ClientService clientService, PasswordEncoder passwordEncoder) {
		this.clientService = clientService;
		this.passwordEncoder = passwordEncoder;
	}

	@PostConstruct
	void postConstruct(){
		// create an admin client the first time the app runs
		Client admin = clientService.readClientByEmail("admin@email.com");
		if (admin == null) {
			//String password = UUID.randomUUID().toString();

			admin = new Client();
			admin.setFirstName("Master");
			admin.setLastName("Admin");
			admin.setEmail(adminEmail);
			admin.setPassword(passwordEncoder.encode(adminPassword));
			admin.setRole("ADMIN");
			admin.setEnabled(true);

			if (clientService.createClient(admin)) {
				log.info(String.format("Master Admin\nemail: %s\npassword: %s", adminEmail, adminPassword));
			}
		}

		// create a dynamic resource folder for the app
		File file = new File(System.getenv("APPDATA") + "/SpringBoot/eCommerceApp");
		if (!file.mkdirs()) {
			log.warn("Failed to create the dynamic resource folder");
		}
		// create the image folder in the dynamic resource folder
		file = new File(System.getenv("APPDATA") + "/SpringBoot/eCommerceApp/image");
		if (!file.mkdir()) {
			log.warn("Failed to create the image folder in the dynamic resource folder");
		}
	}

}
