package com.nilenso.swiftactivities.services;

import org.springframework.stereotype.Service;

@Service
public class HealthCheckServiceImpl implements HealthCheckService {

    @Override
    public String healthCheck() {
        return "OK";
    }
}
