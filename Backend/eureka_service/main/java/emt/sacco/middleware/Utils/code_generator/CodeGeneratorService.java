package emt.sacco.middleware.Utils.code_generator;

import emt.sacco.middleware.SecurityImpl.SaccoEntity.SSaccoEntity;
import emt.sacco.middleware.SecurityImpl.SaccoEntity.SaccoEntityRepository;
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
        List<SSaccoEntity> data = saccoEntityRepository.findAll();

        if (data.isEmpty()) {
            return "1";
        } else {
            List<String> codes = data.stream()
                    .map(SSaccoEntity::getEntityId)
                    .collect(Collectors.toList());

            Collections.sort(codes, new CodeComparator(0));

            String lastCode = codes.get(0);
            int newNum = Integer.parseInt(lastCode) + 1;
            return String.valueOf(newNum);
        }
    }
}
