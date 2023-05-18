package com.kirubel.spring_boot_crud;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.beans.FeatureDescriptor;
import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
@RestController
@RequestMapping("api/v1/customers")
public class Application {
    private final CustomerRepository customerRepository;

    public Application(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    record NewCustomerRequest(String name, String email, Integer age) { }

    @GetMapping
    private List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    @PostMapping
    public void addCustomer(@RequestBody NewCustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setAge(request.age());
        customerRepository.save(customer);
    }

    @GetMapping("{customerId}")
    public Customer getCustomer(@PathVariable("customerId") Integer customerId) throws Exception {
        return customerRepository.findById(customerId).orElseThrow(() -> new Exception("Employee not exist with id: " + customerId));
    }

    @PutMapping("/{customerId}")
    void updateCustomer(@PathVariable("customerId") Integer customerId, @RequestBody() Customer customer) throws Exception {
        var c = getCustomer(customerId);
        BeanUtils.copyProperties(customer, c, getNullPropertyNames(customer));
        customerRepository.save(c);
    }

    @DeleteMapping("{customerId}")
    public void deleteCustomer(@PathVariable("customerId") Integer customerId) {
        customerRepository.deleteById(customerId);
    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null || propertyName.equals("id"))
                .toArray(String[]::new);
    }
}
