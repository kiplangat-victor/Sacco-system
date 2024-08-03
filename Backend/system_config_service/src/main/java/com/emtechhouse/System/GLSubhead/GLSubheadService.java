package com.emtechhouse.System.GLSubhead;

import com.emtechhouse.System.Utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GLSubheadService {
    private final GLSubheadRepo glSubheadRepo;
    public GLSubheadService(GLSubheadRepo glSubheadRepo) {
        this.glSubheadRepo = glSubheadRepo;
    }

    public GLSubhead addGLSubhead(GLSubhead glSubhead) {
        try {
            return glSubheadRepo.save(glSubhead);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<GLSubhead> findAllGLSubheads() {
        try {
            return glSubheadRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public GLSubhead findById(Long id){
        try{
            return glSubheadRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }

    public GLSubhead updateGLSubhead(GLSubhead glSubhead) {
        try {
            return glSubheadRepo.save(glSubhead);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteGLSubhead(Long id) {
        try {
            glSubheadRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }
}

