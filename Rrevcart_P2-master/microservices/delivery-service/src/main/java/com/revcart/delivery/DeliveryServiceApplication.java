package com.revcart.delivery;

import com.revcart.delivery.model.DeliveryAgent;
import com.revcart.delivery.repository.DeliveryAgentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class DeliveryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeliveryServiceApplication.class, args);
    }
    
    @Bean
    CommandLineRunner initAgents(DeliveryAgentRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(createAgent("Rajesh Kumar", "+91-9876543210", "rajesh@revcart.com", "Bike"));
                repository.save(createAgent("Priya Sharma", "+91-9876543211", "priya@revcart.com", "Scooter"));
                repository.save(createAgent("Amit Patel", "+91-9876543212", "amit@revcart.com", "Bike"));
                repository.save(createAgent("Sneha Reddy", "+91-9876543213", "sneha@revcart.com", "Car"));
                repository.save(createAgent("Vikram Singh", "+91-9876543214", "vikram@revcart.com", "Bike"));
                repository.save(createAgent("Arjun Mehta", "+91-9876543215", "arjun@revcart.com", "Bike"));
                repository.save(createAgent("Kavya Nair", "+91-9876543216", "kavya@revcart.com", "Scooter"));
                repository.save(createAgent("Rohit Verma", "+91-9876543217", "rohit@revcart.com", "Car"));
                repository.save(createAgent("Ananya Das", "+91-9876543218", "ananya@revcart.com", "Bike"));
                repository.save(createAgent("Karan Joshi", "+91-9876543219", "karan@revcart.com", "Scooter"));
                System.out.println("✅ Initialized 10 delivery agents");
            }
        };
    }
    
    private DeliveryAgent createAgent(String name, String phone, String email, String vehicleType) {
        DeliveryAgent agent = new DeliveryAgent();
        agent.setName(name);
        agent.setPhone(phone);
        agent.setEmail(email);
        agent.setVehicleType(vehicleType);
        agent.setStatus("AVAILABLE");
        agent.setCurrentLat(0.0);
        agent.setCurrentLng(0.0);
        return agent;
    }
}
