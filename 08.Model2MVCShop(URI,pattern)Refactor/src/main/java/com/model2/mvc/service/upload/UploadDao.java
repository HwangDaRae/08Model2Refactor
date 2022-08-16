package com.model2.mvc.service.upload;

import java.util.List;

import com.model2.mvc.service.domain.Upload;
import com.model2.mvc.service.domain.Upload_Sub;

public interface UploadDao {
	
	public void addUpload(Upload upload) throws Exception;
	
	public List<Upload> getUploadFile(String fileName) throws Exception;
	
	public Upload updateUpload(Upload upload, Upload_Sub upload_sub) throws Exception;

}
