package com.mshz.microfilemanager.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.mshz.microfilemanager.domain.MshzFile;
import com.mshz.microfilemanager.repository.MshzFileRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.io.Resource;

@Service
@Transactional
public class CustomMshzFileService {
    

    private final Logger log = LoggerFactory.getLogger(CustomMshzFileService.class);

    private final MshzFileRepository mshzFileRepository;

    public CustomMshzFileService(MshzFileRepository mshzFileRepository) {
        this.mshzFileRepository = mshzFileRepository;
    }
    
    public ResponseEntity<Resource> downloadImageFile(Long id) {
        log.debug("Request to download file stream");
        try {
            MshzFile file = mshzFileRepository.findById(id).orElse(null);
            if (file != null) {
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(file.getfDataContentType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                        .body(new ByteArrayResource(file.getfData()));
            }
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }
		return null;
	}


    /**
     * get file on browser
     * @param id
     * @return InputStreamResource or null
     */
	public ResponseEntity<InputStreamResource> readStreamOnBrowser(Long id) {
        log.debug("Request to get file stream");
		try {
            MshzFile file = mshzFileRepository.findById(id).orElse(null);
            if(file != null){
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition", "inline; filename=" + file.getName());
                InputStream is = new ByteArrayInputStream(file.getfData());
                return ResponseEntity.ok().headers(headers)
                    .contentType(MediaType.parseMediaType(file.getfDataContentType()))
                        .body(new InputStreamResource(is));
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
		}
		return null;
	}

}
