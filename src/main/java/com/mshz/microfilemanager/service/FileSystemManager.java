package com.mshz.microfilemanager.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import com.mshz.microfilemanager.domain.MshzFile;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.io.IOException;

@Service
public class FileSystemManager {

    private final Logger log = LoggerFactory.getLogger(FileSystemManager.class);

    @Value("${my-config.file.upload-root-path}")
    private String rootPathName;

    public FileSystemManager(){}

    @Async
    public void upload(String fileName, byte[] fileData, String folder) throws IOException{
        if(fileName != null && fileData != null && rootPathName != null){
            String pathName = "";
            if(folder == null)
                pathName = StringUtils.cleanPath(rootPathName + '/'+fileName);
            else
                pathName = StringUtils.cleanPath(rootPathName + '/'+ folder+'/'+fileName);
            if(!fileExists(pathName)){
                try {
                    FileUtils.writeByteArrayToFile(new File(pathName), fileData);
                } catch (Exception e) {
                    log.debug(e.getMessage());
                    throw new IOException(e.getMessage());
                }
            }
        }
    }

    public MshzFile downLoadFile(MshzFile file){
        if(file != null && file.getName() != null){
            String pathName = StringUtils.cleanPath(rootPathName + '/' + file.getName());
            try {
               if(fileExists(pathName)){
                byte[] data = FileUtils.readFileToByteArray(new File(pathName));
                file.setfData(data);
                return file;               }
            } catch (java.io.IOException e) {
                log.error("download file error {}", e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    @Async
    public void deleteFile(String fileName){
        try {
            String pathName = StringUtils.cleanPath(rootPathName + '/' + fileName);
            (new File(pathName)).delete();
        } catch (Exception e) {
            log.error("delete file error {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Async
    public void combineFileChunks(String chunksFolder, String originalFileName) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        File tempFilePath = null;
        File[] files = null;
        try {
            if(originalFileName != null && chunksFolder != null){
                String dirPath = StringUtils.cleanPath(rootPathName + '/' + chunksFolder);
                // Get the number of small files to cut
                tempFilePath = new File(dirPath);
                files = tempFilePath.listFiles();
                if (files != null) {
                    int bufferSize = getFilesBufferSizeInFolder(tempFilePath);
                    int fileNum = files.length;
            
                    // Restored large file path
                    String outputFile = rootPathName + "/" + originalFileName;
                    fos = new FileOutputStream(outputFile);
            
                    // File read cache
                    byte[] buffer = new byte[bufferSize];
                    int len = 0;
            
                    // Restore all cut small files to one large file
                    for (int i = 0; i < fileNum; i++) {
                        String chunkName = "chunk"+i+"_"+originalFileName;
                        fis = new FileInputStream(dirPath + "/" + chunkName);
                        len = fis.read(buffer);
                        fos.write(buffer, 0, len);
                    }
                    System.out.println("Merge catalog file:" +rootPathName + " Complete, the generated file is:" + outputFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
                // deleting chunks 
                if(files != null){
                    for(File chunFile: files){
                        chunFile.delete();
                    }
                }
                if(tempFilePath != null && tempFilePath.exists())
                    tempFilePath.delete(); // deleting chunks content folder

            // close all opened streams
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String createFolderIfNotExists(String originalFileName){
        if(originalFileName != null){
            try {
                String dirPath = originalFileName.replace(".", "_");
                String pathName = StringUtils.cleanPath(rootPathName + '/' + dirPath);
                if(!fileExists(pathName))
                    (new File(pathName)).mkdirs();
                return dirPath;
            } catch (Exception e) {
                log.debug("create folder failed with error: {}", e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean fileExists(String pathName){
        try {
            String cleanPath = StringUtils.cleanPath(pathName);
            return (new File(cleanPath)).exists();
        } catch (Exception e) {
            e.printStackTrace();
            log.debug(e.getMessage());
        }
        return false;
    }
    
    public int getFilesBufferSizeInFolder(File directory) {
        int size = 0;
        FileInputStream fi = null;
        BufferedInputStream bis = null;
        if(directory != null){
            for (File file : directory.listFiles()) {
                if (file.isFile()){
                    try {
                        fi = new FileInputStream(file);
                        bis = new BufferedInputStream(fi);
                        size += bis.readAllBytes().length;
                        if(bis != null)
                            bis.close();
                        if(fi != null)
                            fi.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            }
        }
        try {
            if(bis != null)
                bis.close();
            if(fi != null)
                fi.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }
    
    public long getFolderSize(File directory) {
        long length = 0;
        try {
            for (File file : directory.listFiles()) {
                if (file.isFile())
                    length += file.length();
                else
                    length += getFolderSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return length;
    }
    
}
