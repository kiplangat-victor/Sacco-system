package com.emtechhouse.System.SystemParameters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemParametersService {

    @Autowired
    private SystemParametersRepository systemParametersRepository;

    public SystemParameters findSystemParameters(){
        return systemParametersRepository.findAll().get(0);
    }

    public SystemParameters saveParameters(SystemParameters systemParameters){
        return systemParametersRepository.save(systemParameters);
    }
}
