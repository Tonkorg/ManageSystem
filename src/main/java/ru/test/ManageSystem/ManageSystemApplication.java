package ru.test.ManageSystem;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ManageSystemApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.directory("./")
				.ignoreIfMissing()
				.load();


		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));


		SpringApplication.run(ManageSystemApplication.class, args);
	}

}
