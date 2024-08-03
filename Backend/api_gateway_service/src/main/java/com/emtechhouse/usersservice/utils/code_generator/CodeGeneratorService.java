package com.emtechhouse.usersservice.utils.code_generator;

import com.emtechhouse.usersservice.SaccoEntity.SaccoEntity;
import com.emtechhouse.usersservice.SaccoEntity.SaccoEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CodeGeneratorService {

    @Autowired
    private SaccoEntityRepository saccoEntityRepository;

    public String generateCode() {
        List<SaccoEntity> data = saccoEntityRepository.findAll();

        if (data.isEmpty()) {
            return "1";
        } else {
            List<String> codes = data.stream()
                    .map(SaccoEntity::getEntityId)
                    .collect(Collectors.toList());

            Collections.sort(codes, new CodeComparator(0));

            String lastCode = codes.get(0);
            int newNum = Integer.parseInt(lastCode) + 1;
            return String.valueOf(newNum);
        }
    }
}
