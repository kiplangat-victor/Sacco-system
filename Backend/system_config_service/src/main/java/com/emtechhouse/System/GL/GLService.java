package com.emtechhouse.System.GL;

import com.emtechhouse.System.Utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GLService {
    private final GLRepository glRepo;

    public GLService(GLRepository glRepo) {
        this.glRepo = glRepo;
    }

    public GL addGL(GL gl) {
        try {
            return glRepo.save(gl);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<GL> findAllGLs() {
        try {
            return glRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public GL findById(Long id){
        try{
            return glRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }

    public GL updateGL(GL gl) {
        try {
            return glRepo.save(gl);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteGL(Long id) {
        try {
            glRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }
}
