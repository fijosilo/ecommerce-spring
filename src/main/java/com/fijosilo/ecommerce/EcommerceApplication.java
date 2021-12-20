package com.fijosilo.ecommerce;

import com.fijosilo.ecommerce.dto.Client;
import com.fijosilo.ecommerce.repository.JPAClientRepository;
import com.fijosilo.ecommerce.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
			String password = UUID.randomUUID().toString();

			admin = new Client();
			admin.setFirstName("Master");
			admin.setLastName("Admin");
			admin.setEmail("admin@email.com");
			admin.setPassword(passwordEncoder.encode(password));
			admin.setRole("ADMIN");
			admin.setEnabled(true);

			if (clientService.createClient(admin)) {
				log.info("Master Admin\nemail: admin@email.com\npassword: " + password);
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
		// create the thumbnail folder in the dynamic resource folder
		file = new File(System.getenv("APPDATA") + "/SpringBoot/eCommerceApp/thumbnail");
		if (!file.mkdir()) {
			log.warn("Failed to create the thumbnail folder in the dynamic resource folder");
		}
	}

}
