package pe.edu.ulima.tiendacapas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Punto de entrada: arranca Spring Boot, que crea cada capa y la inyecta en la de arriba. */
@SpringBootApplication
public class TiendaCapasApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiendaCapasApplication.class, args);
	}

}
