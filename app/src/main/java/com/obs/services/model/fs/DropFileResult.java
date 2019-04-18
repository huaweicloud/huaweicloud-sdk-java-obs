package com.obs.services.model.fs;

import com.obs.services.model.DeleteObjectResult;

public class DropFileResult extends DeleteObjectResult{

    public DropFileResult(boolean deleteMarker, String versionId) {
        super(deleteMarker, versionId);
    }

    public DropFileResult(boolean deleteMarker, String objectKey, String versionId) {
        super(deleteMarker, objectKey, versionId);
    }
    
    

}
