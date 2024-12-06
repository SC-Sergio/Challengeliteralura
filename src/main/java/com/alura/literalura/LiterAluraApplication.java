package com.alura.literalura;

import com.alura.literalura.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class LiterAluraApplication {

    @Autowired
    private MenuService menuService;

    public static void main(String[] args) {
        SpringApplication.run(LiterAluraApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        menuService.displayMenu();
    }
}
