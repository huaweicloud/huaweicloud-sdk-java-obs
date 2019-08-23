/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.obs.services.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.internal.io.ProgressInputStream;
import com.obs.services.internal.utils.SecureObjectInputStream;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.AbortMultipartUploadRequest;
import com.obs.services.model.CompleteMultipartUploadRequest;
import com.obs.services.model.CompleteMultipartUploadResult;
import com.obs.services.model.DownloadFileRequest;
import com.obs.services.model.DownloadFileResult;
import com.obs.services.model.GetObjectMetadataRequest;
import com.obs.services.model.GetObjectRequest;
import com.obs.services.model.HeaderResponse;
import com.obs.services.model.InitiateMultipartUploadRequest;
import com.obs.services.model.InitiateMultipartUploadResult;
import com.obs.services.model.ObjectMetadata;
import com.obs.services.model.ObsObject;
import com.obs.services.model.PartEtag;
import com.obs.services.model.UploadFileRequest;
import com.obs.services.model.UploadPartRequest;
import com.obs.services.model.UploadPartResult;

public class ResumableClient {
	
	private static final ILogger log = LoggerBuilder.getLogger("com.obs.services.ObsClient");
	private ObsClient obsClient;
	
	public ResumableClient(ObsClient obsClient) {
		this.obsClient = obsClient;
	}
	
	public CompleteMultipartUploadResult uploadFileResume(UploadFileRequest uploadFileRequest) {
		ServiceUtils.asserParameterNotNull(uploadFileRequest, "UploadFileRequest is null");
		ServiceUtils.asserParameterNotNull(uploadFileRequest.getBucketName(), "bucketName is null");
		ServiceUtils.asserParameterNotNull2(uploadFileRequest.getObjectKey(), "objectKey is null");
		ServiceUtils.asserParameterNotNull(uploadFileRequest.getUploadFile(), "uploadfile is null");
		if (uploadFileRequest.isEnableCheckpoint()) {
			if (!ServiceUtils.isValid(uploadFileRequest.getCheckpointFile())) {
				uploadFileRequest.setCheckpointFile(uploadFileRequest.getUploadFile() + ".uploadFile_record");
			}
		}
		try {
			return uploadFileCheckPoint(uploadFileRequest);
		} catch (ObsException e) {
			throw e;
		}catch (ServiceException e) {
			throw ServiceUtils.changeFromServiceException(e);
		}
		catch (Exception e) {
			throw new ObsException(e.getMessage(), e);
		}
	}
	
	protected void abortMultipartUploadSilent(String uploadId, String bucketName, String objectKey) {
		try {
			AbortMultipartUploadRequest request = new AbortMultipartUploadRequest(bucketName, objectKey, uploadId);
			this.obsClient.abortMultipartUpload(request);
		}catch (Exception e) {
			if(log.isWarnEnabled()) {
				log.warn("Abort multipart upload failed", e);
			}
		}
	}
	
	protected HeaderResponse abortMultipartUpload(String uploadId, String bucketName, String objectKey) {
		AbortMultipartUploadRequest request = new AbortMultipartUploadRequest(bucketName, objectKey, uploadId);
		return this.obsClient.abortMultipartUpload(request);
	}
 
	
	private CompleteMultipartUploadResult uploadFileCheckPoint(UploadFileRequest uploadFileRequest) throws Exception {
		UploadCheckPoint uploadCheckPoint = new UploadCheckPoint();
		if (uploadFileRequest.isEnableCheckpoint()) {
			boolean needRecreate = false;
			try {
				uploadCheckPoint.load(uploadFileRequest.getCheckpointFile());
			} catch (Exception e) {
				needRecreate = true;
			}

			if (!needRecreate) {
				if (!(uploadFileRequest.getBucketName().equals(uploadCheckPoint.bucketName)
						&& uploadFileRequest.getObjectKey().equals(uploadCheckPoint.objectKey)
						&& uploadFileRequest.getUploadFile().equals(uploadCheckPoint.uploadFile))) {
					needRecreate = true;
				} else if (!uploadCheckPoint.isValid(uploadFileRequest.getUploadFile())) {
					needRecreate = true;
				}
			}

			if (needRecreate) {
				if (uploadCheckPoint.bucketName != null && uploadCheckPoint.objectKey != null
						&& uploadCheckPoint.uploadID != null) {
					this.abortMultipartUploadSilent(uploadCheckPoint.uploadID, uploadCheckPoint.bucketName, uploadCheckPoint.objectKey);
				}
				File uploadCheckFile = new File(uploadFileRequest.getCheckpointFile());
				if (uploadCheckFile.exists() && uploadCheckFile.isFile()) {
					uploadCheckFile.delete();
				}
				prepare(uploadFileRequest, uploadCheckPoint);
			}
		} else {
			prepare(uploadFileRequest, uploadCheckPoint);
		}

		// 开始上传
		List<PartResult> partResults = uploadfile(uploadFileRequest, uploadCheckPoint);

		// 有错误抛出异常,无异常则合并多段
		for (PartResult partResult : partResults) {
			if (partResult.isFailed() && partResult.getException() != null) {
				// 未开启，取消多段
				if (!uploadFileRequest.isEnableCheckpoint()) {
					this.abortMultipartUploadSilent(uploadCheckPoint.uploadID, uploadFileRequest.getBucketName(), uploadFileRequest.getObjectKey());
				} else if (uploadCheckPoint.isAbort) {
					this.abortMultipartUploadSilent(uploadCheckPoint.uploadID, uploadFileRequest.getBucketName(), uploadFileRequest.getObjectKey());
					File uploadCheckFile = new File(uploadFileRequest.getCheckpointFile());
					if (uploadCheckFile.exists() && uploadCheckFile.isFile()) {
						uploadCheckFile.delete();
					}
				}
				throw partResult.getException();
			}
		}

		// 合并多段
		CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(
				uploadFileRequest.getBucketName(), uploadFileRequest.getObjectKey(), uploadCheckPoint.uploadID,
				uploadCheckPoint.partEtags);

		try {
			CompleteMultipartUploadResult result = this.obsClient.completeMultipartUpload(completeMultipartUploadRequest);
			if (uploadFileRequest.isEnableCheckpoint()) {
				File uploadFileTmp = new File(uploadFileRequest.getCheckpointFile());
				if (uploadFileTmp.isFile() && uploadFileTmp.exists()) {
					uploadFileTmp.delete();
				}
			}
			return result;
		} catch (ObsException e) {
			if (!uploadFileRequest.isEnableCheckpoint()) {
				this.abortMultipartUpload(uploadCheckPoint.uploadID, uploadFileRequest.getBucketName(), uploadFileRequest.getObjectKey());
			} else {
				if (e.getResponseCode() >= 300 && e.getResponseCode() < 500 && e.getResponseCode() != 408) {
					this.abortMultipartUploadSilent(uploadCheckPoint.uploadID, uploadFileRequest.getBucketName(), uploadFileRequest.getObjectKey());
					File uploadCheckFile = new File(uploadFileRequest.getCheckpointFile());
					if (uploadCheckFile.exists() && uploadCheckFile.isFile()) {
						uploadCheckFile.delete();
					}
				}
			}
			throw e;
		}
	}
	
	private List<PartResult> uploadfile(UploadFileRequest uploadFileRequest, UploadCheckPoint uploadCheckPoint)
			throws Exception {
		ArrayList<PartResult> pieceResults = new ArrayList<PartResult>();
		ExecutorService executorService = Executors.newFixedThreadPool(uploadFileRequest.getTaskNum());
		ArrayList<Future<PartResult>> futures = new ArrayList<Future<PartResult>>();
		
		ProgressManager progressManager = null;
		if(uploadFileRequest.getProgressListener() == null) {
			for (int i = 0; i < uploadCheckPoint.uploadParts.size(); i++) {
				UploadPart uploadPart = uploadCheckPoint.uploadParts.get(i);
				if (uploadPart.isCompleted) {
					PartResult pr = new PartResult(uploadPart.partNumber, uploadPart.offset, uploadPart.size);
					pr.setFailed(false);
					pieceResults.add(pr);
				} else {
					futures.add(executorService.submit(new Mission(i, uploadCheckPoint, i, uploadFileRequest, this.obsClient)));
				}
			}
		}else {
			long transferredBytes = 0L;
			List<Mission> unfinishedUploadMissions = new LinkedList<Mission>();
			for (int i = 0; i < uploadCheckPoint.uploadParts.size(); i++) {
				UploadPart uploadPart = uploadCheckPoint.uploadParts.get(i);
				if (uploadPart.isCompleted) {
					PartResult pr = new PartResult(uploadPart.partNumber, uploadPart.offset, uploadPart.size);
					pr.setFailed(false);
					pieceResults.add(pr);
					transferredBytes += uploadPart.size;
				} else {
					unfinishedUploadMissions.add(new Mission(i, uploadCheckPoint, i, uploadFileRequest, this.obsClient));
				}
			}
			progressManager = new ConcurrentProgressManager(uploadCheckPoint.uploadFileStatus.size, transferredBytes, uploadFileRequest.getProgressListener(), 
					uploadFileRequest.getProgressInterval() > 0 ? uploadFileRequest.getProgressInterval() : ObsConstraint.DEFAULT_PROGRESS_INTERVAL);
			for(Mission mission : unfinishedUploadMissions) {
				mission.setProgressManager(progressManager);
				futures.add(executorService.submit(mission));
			}
		}

		executorService.shutdown();
		executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		
		for (Future<PartResult> future : futures) {
			try {
				PartResult tr = future.get();
				pieceResults.add(tr);
			} catch (ExecutionException e) {
				if (!uploadFileRequest.isEnableCheckpoint()) {
					this.abortMultipartUploadSilent(uploadCheckPoint.uploadID, uploadFileRequest.getBucketName(), uploadFileRequest.getObjectKey());
				}
				throw e;
			}
		}
		
		if(progressManager != null) {
			progressManager.progressEnd();
		}
		
		return pieceResults;
	}

	static class Mission implements Callable<PartResult> {
		
		private int id;
		private UploadCheckPoint uploadCheckPoint;
		private int partIndex;
		private UploadFileRequest uploadFileRequest;
		private ObsClient obsClient;
		private ProgressManager progressManager;

		public Mission(int id, UploadCheckPoint uploadCheckPoint, int partIndex, UploadFileRequest uploadFileRequest,
				ObsClient obsClient) {
			this.id = id;
			this.uploadCheckPoint = uploadCheckPoint;
			this.partIndex = partIndex;
			this.uploadFileRequest = uploadFileRequest;
			this.obsClient = obsClient;
		}

		@Override
		public PartResult call() throws Exception {
			PartResult tr = null;
			UploadPart uploadPart = uploadCheckPoint.uploadParts.get(partIndex);
			tr = new PartResult(partIndex + 1, uploadPart.offset, uploadPart.size);
			if (!uploadCheckPoint.isAbort) {
				try {
					UploadPartRequest uploadPartRequest = new UploadPartRequest();
					uploadPartRequest.setBucketName(uploadFileRequest.getBucketName());
					uploadPartRequest.setObjectKey(uploadFileRequest.getObjectKey());
					uploadPartRequest.setUploadId(uploadCheckPoint.uploadID);
					uploadPartRequest.setPartSize(uploadPart.size);
					uploadPartRequest.setPartNumber(uploadPart.partNumber);
					
					if(this.progressManager == null) {
						uploadPartRequest.setFile(new File(uploadFileRequest.getUploadFile()));
						uploadPartRequest.setOffset(uploadPart.offset);
					}else {
						InputStream input = new FileInputStream(uploadFileRequest.getUploadFile());
						long offset = uploadPart.offset;
						long skipByte = input.skip(offset);
						if(offset < skipByte) {
							log.error(String.format("The actual number of skipped bytes (%d) is less than expected (%d): ", skipByte, offset));
						}
						uploadPartRequest.setInput(new ProgressInputStream(input, this.progressManager, false));
					}

					UploadPartResult result = obsClient.uploadPart(uploadPartRequest);

					PartEtag partEtag = new PartEtag(result.getEtag(), result.getPartNumber());
					uploadCheckPoint.update(partIndex, partEtag, true);
					tr.setFailed(false);

					if (uploadFileRequest.isEnableCheckpoint()) {
						uploadCheckPoint.record(uploadFileRequest.getCheckpointFile());
					}
					
				} catch (ObsException e) {
					if (e.getResponseCode() >= 300 && e.getResponseCode() < 500 && e.getResponseCode() != 408) {
						uploadCheckPoint.isAbort = true;
					}
					// 有异常打印到日志文件
					tr.setFailed(true);
					tr.setException(e);
					if(log.isErrorEnabled()) {
						log.error(String.format("Task %d:%s upload part %d failed: ", id, "upload" + id, partIndex + 1),
								e);
					}
				}catch(Exception e) {
					tr.setFailed(true);
					tr.setException(e);
					if(log.isErrorEnabled()) {
						log.error(String.format("Task %d:%s upload part %d failed: ", id, "upload" + id, partIndex + 1),
								e);
					}
				}
			} else {
				tr.setFailed(true);
			}
			return tr;
		}

		public void setProgressManager(ProgressManager progressManager) {
			this.progressManager = progressManager;
		}

	}

	private void prepare(UploadFileRequest uploadFileRequest, UploadCheckPoint uploadCheckPoint) throws Exception {
		uploadCheckPoint.uploadFile = uploadFileRequest.getUploadFile();
		uploadCheckPoint.bucketName = uploadFileRequest.getBucketName();
		uploadCheckPoint.objectKey = uploadFileRequest.getObjectKey();
		uploadCheckPoint.uploadFileStatus = FileStatus.getFileStatus(uploadCheckPoint.uploadFile,
				uploadFileRequest.isEnableCheckSum());
		uploadCheckPoint.uploadParts = splitUploadFile(uploadCheckPoint.uploadFileStatus.size,
				uploadFileRequest.getPartSize());
		uploadCheckPoint.partEtags = new ArrayList<PartEtag>();

		InitiateMultipartUploadRequest initiateUploadRequest = new InitiateMultipartUploadRequest(
				uploadFileRequest.getBucketName(), uploadFileRequest.getObjectKey());
		
		initiateUploadRequest.setExtensionPermissionMap(uploadFileRequest.getExtensionPermissionMap());
		initiateUploadRequest.setAcl(uploadFileRequest.getAcl());
		initiateUploadRequest.setSuccessRedirectLocation(uploadFileRequest.getSuccessRedirectLocation());
		initiateUploadRequest.setSseCHeader(uploadFileRequest.getSseCHeader());
		initiateUploadRequest.setSseKmsHeader(uploadFileRequest.getSseKmsHeader());
		initiateUploadRequest.setMetadata(uploadFileRequest.getObjectMetadata());
		
		InitiateMultipartUploadResult initiateUploadResult = this.obsClient.initiateMultipartUpload(initiateUploadRequest);
		uploadCheckPoint.uploadID = initiateUploadResult.getUploadId();
		if (uploadFileRequest.isEnableCheckpoint()) {
			try {
				uploadCheckPoint.record(uploadFileRequest.getCheckpointFile());
			} catch (Exception e) {
				this.abortMultipartUploadSilent(uploadCheckPoint.uploadID, uploadCheckPoint.bucketName, uploadCheckPoint.objectKey);
				throw e;
			}
		}
	}

	private ArrayList<UploadPart> splitUploadFile(long size, long partSize) {
		ArrayList<UploadPart> parts = new ArrayList<UploadPart>();

		long partNum = size / partSize;
		if (partNum >= 10000) {
			partSize = size % 10000 == 0? size / 10000 : size / 10000  + 1;
			partNum = size / partSize;
		}
		if (size % partSize > 0) {
			partNum++;
		}
		if(partNum == 0) {
			UploadPart part = new UploadPart();
			part.partNumber = 1;
			part.offset = 0;
			part.size = 0;
			part.isCompleted = false;
			parts.add(part);
		}else {
			for (long i = 0; i < partNum; i++) {
				UploadPart part = new UploadPart();
				part.partNumber = (int) (i + 1);
				part.offset = i * partSize;
				part.size = partSize;
				part.isCompleted = false;
				parts.add(part);
			}
			if (size % partSize > 0) {
				parts.get(parts.size() - 1).size = size % partSize;
			}
		}
		
		
		
		return parts;
	}
	
	/**
	 * 断点续传上传所需类
	 */
	static class UploadCheckPoint implements Serializable {

		private static final long serialVersionUID = 5564757792864743464L;

		/**
		 * 从checkpoint文件中加载checkpoint数据
		 * 
		 * @param checkPointFile
		 * @throws Exception
		 */
		public void load(String checkPointFile) throws Exception {
			FileInputStream fileInput = null;
			SecureObjectInputStream in = null;
			try {
				fileInput = new FileInputStream(checkPointFile);
				in = new SecureObjectInputStream(fileInput);
				UploadCheckPoint tmp = (UploadCheckPoint) in.readObject();
				assign(tmp);
			} finally {
				ServiceUtils.closeStream(in);
				ServiceUtils.closeStream(fileInput);
			}
		}

		/**
		 * 把checkpoint数据写到checkpoint文件
		 * 
		 * @param checkPointFile
		 * @throws IOException
		 */
		public synchronized void record(String checkPointFile) throws IOException {
			this.md5 = hashCode();
			FileOutputStream fileOutput = null;
			ObjectOutputStream outStream = null;
			try {
				fileOutput = new FileOutputStream(checkPointFile);
				outStream = new ObjectOutputStream(fileOutput);
				outStream.writeObject(this);
			} finally {
				ServiceUtils.closeStream(outStream);
				ServiceUtils.closeStream(fileOutput);
			}
		}

		/**
		 * 分块上传完成，更新状态
		 * 
		 * @param partIndex
		 * @param partETag
		 * @param completed
		 */
		public synchronized void update(int partIndex, PartEtag partETag, boolean completed) {
			partEtags.add(partETag);
			uploadParts.get(partIndex).isCompleted = completed;
		}

		/**
		 * 判读本地文件与checkpoint中记录的信息是否相符合，校验一致性
		 * 
		 * @param uploadFile
		 * @return boolean
		 * @throws IOException
		 */
		public boolean isValid(String uploadFile) throws IOException {
			if (this.md5 != hashCode()) {
				return false;
			}

			File upload = new File(uploadFile);
			if (!this.uploadFile.equals(uploadFile) || this.uploadFileStatus.size != upload.length()
					|| this.uploadFileStatus.lastModified != upload.lastModified()) {
				return false;
			}

			if (this.uploadFileStatus.checkSum != null) {
				try {
					return this.uploadFileStatus.checkSum
							.equals(ServiceUtils.toBase64(ServiceUtils.computeMD5Hash(new FileInputStream(upload))));
				} catch (NoSuchAlgorithmException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}

			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((objectKey == null) ? 0 : objectKey.hashCode());
			result = prime * result + ((bucketName == null) ? 0 : bucketName.hashCode());
			result = prime * result + ((partEtags == null) ? 0 : partEtags.hashCode());
			result = prime * result + ((uploadFile == null) ? 0 : uploadFile.hashCode());
			result = prime * result + ((uploadFileStatus == null) ? 0 : uploadFileStatus.hashCode());
			result = prime * result + ((uploadID == null) ? 0 : uploadID.hashCode());
			result = prime * result + ((uploadParts == null) ? 0 : uploadParts.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			} else {
				if (obj instanceof UploadCheckPoint) {
					UploadCheckPoint uploadCheckPoint = (UploadCheckPoint) obj;
					if (uploadCheckPoint.hashCode() == obj.hashCode()) {
						return true;
					}
				}
			}
			return false;
		}

		private void assign(UploadCheckPoint tmp) {
			this.md5 = tmp.md5;
			this.bucketName = tmp.bucketName;
			this.uploadFile = tmp.uploadFile;
			this.uploadFileStatus = tmp.uploadFileStatus;
			this.objectKey = tmp.objectKey;
			this.uploadID = tmp.uploadID;
			this.uploadParts = tmp.uploadParts;
			this.partEtags = tmp.partEtags;
		}

		public int md5;
		public String uploadFile;
		public FileStatus uploadFileStatus;
		public String bucketName;
		public String objectKey;
		public String uploadID;
		public ArrayList<UploadPart> uploadParts;
		public ArrayList<PartEtag> partEtags;
		public transient volatile boolean isAbort = false;

	}

	static class FileStatus implements Serializable {
		private static final long serialVersionUID = -3135754191745936521L;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((checkSum == null) ? 0 : checkSum.hashCode());
			result = prime * result + (int) (lastModified ^ (lastModified >>> 32));
			result = prime * result + (int) (size ^ (size >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			} else {
				if (obj instanceof FileStatus) {
					FileStatus fileStatus = (FileStatus) obj;
					if (fileStatus.hashCode() == obj.hashCode()) {
						return true;
					}
				}
			}
			return false;
		}

		public static FileStatus getFileStatus(String uploadFile, boolean checkSum) throws IOException {
			FileStatus fileStatus = new FileStatus();
			File file = new File(uploadFile);
			fileStatus.size = file.length();
			fileStatus.lastModified = file.lastModified();
			if (checkSum) {
				try {
					fileStatus.checkSum = ServiceUtils.toBase64(ServiceUtils.computeMD5Hash(new FileInputStream(file)));
				} catch (NoSuchAlgorithmException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
			return fileStatus;
		}

		public long size; // 文件大小
		public long lastModified; // 文件最后修改时间
		public String checkSum; // 文件checkSum
	}

	static class UploadPart implements Serializable {

		private static final long serialVersionUID = 751520598820222785L;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (isCompleted ? 1 : 0);
			result = prime * result + partNumber;
			result = prime * result + (int) (offset ^ (offset >>> 32));
			result = prime * result + (int) (size ^ (size >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			} else {
				if (obj instanceof UploadPart) {
					UploadPart uploadPart = (UploadPart) obj;
					if (uploadPart.hashCode() == obj.hashCode()) {
						return true;
					}
				}
			}
			return false;
		}

		public int partNumber; // 分片序号
		public long offset; // 分片在文件中的偏移量
		public long size; // 分片大小
		public boolean isCompleted; // 该分片上传是否完成
	}

	static class PartResult {

		public PartResult(int partNumber, long offset, long length) {
			this.partNumber = partNumber;
			this.offset = offset;
			this.length = length;
		}

		public int getpartNumber() {
			return partNumber;
		}

		public void setpartNumber(int partNumber) {
			this.partNumber = partNumber;
		}

		public long getOffset() {
			return offset;
		}

		public void setOffset(long offset) {
			this.offset = offset;
		}

		public long getLength() {
			return length;
		}

		public void setLength(long length) {
			this.length = length;
		}

		public boolean isFailed() {
			return isfailed;
		}

		public void setFailed(boolean isfailed) {
			this.isfailed = isfailed;
		}

		public Exception getException() {
			return exception;
		}

		public void setException(Exception exception) {
			this.exception = exception;
		}

		private int partNumber; // 分片序号
		private long offset; // 分片在文件中的偏移
		private long length; // 分片长度
		private boolean isfailed; // 分片上传是否失败
		private Exception exception; // 分片上传异常
	}
	
	public DownloadFileResult downloadFileResume(DownloadFileRequest downloadFileRequest) {
		ServiceUtils.asserParameterNotNull(downloadFileRequest, "DownloadFileRequest is null");
		ServiceUtils.asserParameterNotNull(downloadFileRequest.getBucketName(), "the bucketName is null");
		String key = downloadFileRequest.getObjectKey();
		ServiceUtils.asserParameterNotNull2(key, "the objectKey is null");

		if (downloadFileRequest.getDownloadFile() == null) {
			downloadFileRequest.setDownloadFile(key);
		}
		if (downloadFileRequest.isEnableCheckpoint()) {
			if (downloadFileRequest.getCheckpointFile() == null || downloadFileRequest.getCheckpointFile().isEmpty()) {
				downloadFileRequest.setCheckpointFile(downloadFileRequest.getDownloadFile() + ".downloadFile_record");
			}
		}
		try {
			return downloadCheckPoint(downloadFileRequest);
		} catch (ObsException e) {
			throw e;
		}catch (ServiceException e) {
			throw ServiceUtils.changeFromServiceException(e);
		} 
		catch (Exception e) {
			throw new ObsException(e.getMessage(), e);
		}
	}
	
	private DownloadFileResult downloadCheckPoint(DownloadFileRequest downloadFileRequest) throws Exception {
		
		ObjectMetadata objectMetadata;
		DownloadFileResult downloadFileResult = new DownloadFileResult();
		try {
			GetObjectMetadataRequest request = new GetObjectMetadataRequest(downloadFileRequest.getBucketName(), downloadFileRequest.getObjectKey(), downloadFileRequest.getVersionId());
			objectMetadata = this.obsClient.getObjectMetadata(request);
		} catch (ObsException e) {
			if (e.getResponseCode() >= 300 && e.getResponseCode() < 500 && e.getResponseCode() != 408) {
				File tmpfile = new File(downloadFileRequest.getTempDownloadFile());
				if (tmpfile.exists() && tmpfile.isFile()) {
					tmpfile.delete();
				}
				File file = new File(downloadFileRequest.getCheckpointFile());
				if (file.isFile() && file.exists()) {
					file.delete();
				}
			}
			throw e;
		}
		
		downloadFileResult.setObjectMetadata(objectMetadata);
		
		if(objectMetadata.getContentLength() == 0) {
			File tmpfile = new File(downloadFileRequest.getTempDownloadFile());
			if (tmpfile.exists() && tmpfile.isFile()) {
				tmpfile.delete();
			}
			File file = new File(downloadFileRequest.getCheckpointFile());
			if (file.isFile() && file.exists()) {
				file.delete();
			}
			File dfile = new File(downloadFileRequest.getDownloadFile());
			dfile.getParentFile().mkdirs();
			new RandomAccessFile(dfile, "rw").close();
			if(downloadFileRequest.getProgressListener() != null) {
				downloadFileRequest.getProgressListener().
				progressChanged(new DefaultProgressStatus(0, 0, 0, 0, 0));
			}
			return downloadFileResult;
		}
		
		DownloadCheckPoint downloadCheckPoint = new DownloadCheckPoint();
		if (downloadFileRequest.isEnableCheckpoint()) {
			boolean needRecreate = false;
			try {
				downloadCheckPoint.load(downloadFileRequest.getCheckpointFile());
			} catch (Exception e) {
				needRecreate = true;
			}
			if (!needRecreate) {
				if (!(downloadFileRequest.getBucketName().equals(downloadCheckPoint.bucketName)
						&& downloadFileRequest.getObjectKey().equals(downloadCheckPoint.objectKey)
						&& downloadFileRequest.getDownloadFile().equals(downloadCheckPoint.downloadFile))) {
					needRecreate = true;
				}else if (!downloadCheckPoint.isValid(downloadFileRequest.getTempDownloadFile(), objectMetadata)) {
					needRecreate = true;
				}else if(downloadFileRequest.getVersionId() == null) {
					if(downloadCheckPoint.versionId != null) {
						needRecreate = true;
					}
				}else if(!downloadFileRequest.getVersionId().equals(downloadCheckPoint.versionId)) {
					needRecreate = true;
				}
			}
			if (needRecreate) {
				if (downloadCheckPoint.tmpFileStatus != null) {
					File tmpfile = new File(downloadCheckPoint.tmpFileStatus.tmpFilePath);
					if (tmpfile.exists() && tmpfile.isFile()) {
						tmpfile.delete();
					}
				}
				File file = new File(downloadFileRequest.getCheckpointFile());
				if (file.isFile() && file.exists()) {
					file.delete();
				}
				prepare(downloadFileRequest, downloadCheckPoint, objectMetadata);
			}
		} else {
			prepare(downloadFileRequest, downloadCheckPoint, objectMetadata);
		}

		// 并发下载分片
		DownloadResult downloadResult = download(downloadCheckPoint, downloadFileRequest);
		for (PartResultDown partResult : downloadResult.getPartResults()) {
			if (partResult.isFailed() && partResult.getException() != null) {
				if (!downloadFileRequest.isEnableCheckpoint()) {
					File tmpfile = new File(downloadCheckPoint.tmpFileStatus.tmpFilePath);
					if (tmpfile.exists() && tmpfile.isFile()) {
						tmpfile.delete();
					}
				} else if (downloadCheckPoint.isAbort) {
					File tmpfile = new File(downloadCheckPoint.tmpFileStatus.tmpFilePath);
					if (tmpfile.exists() && tmpfile.isFile()) {
						tmpfile.delete();
					}
					File file = new File(downloadFileRequest.getCheckpointFile());
					if (file.isFile() && file.exists()) {
						file.delete();
					}
				}
				throw partResult.getException();
			}
		}

		// 重命名临时文件
		renameTo(downloadFileRequest.getTempDownloadFile(), downloadFileRequest.getDownloadFile());

		// 开启了断点下载，成功上传后删除checkpoint文件
		if (downloadFileRequest.isEnableCheckpoint()) {
			File file = new File(downloadFileRequest.getCheckpointFile());
			if (file.isFile() && file.exists()) {
				file.delete();
			}
		}

		return downloadFileResult;
	}

	private void renameTo(String tempDownloadFilePath, String downloadFilePath) throws IOException {
		File tmpfile = new File(tempDownloadFilePath);
		File downloadFile = new File(downloadFilePath);
		if (!tmpfile.exists()) {
			throw new FileNotFoundException("tmpFile '" + tmpfile + "' does not exist");
		}
		if (downloadFile.exists()) {
			if (!downloadFile.delete()) {
				throw new IOException("downloadFile '" + downloadFile + "' is exist");
			}
		}
		if (tmpfile.isDirectory() || downloadFile.isDirectory()) {
			throw new IOException("downloadPath is a directory");
		}
		final boolean renameFlag = tmpfile.renameTo(downloadFile);
		if (!renameFlag) {
			InputStream input = null;
			OutputStream output = null;
			try {
				input = new FileInputStream(tmpfile);
				output = new FileOutputStream(downloadFile);
				byte[] buffer = new byte[1024 * 8];
				int length;
				while ((length = input.read(buffer)) > 0) {
					output.write(buffer, 0, length);
				}
			} finally {
				ServiceUtils.closeStream(input);
				ServiceUtils.closeStream(output);
			}
			if (!tmpfile.delete()) {
				throw new IOException("the tmpfile '" + tmpfile
						+ "' can not delete, please delete it to ensure the download finish.");
			}
		}
	}

	private DownloadResult download(DownloadCheckPoint downloadCheckPoint, DownloadFileRequest downloadFileRequest)
			throws Exception {
		ArrayList<PartResultDown> taskResults = new ArrayList<PartResultDown>();
		DownloadResult downloadResult = new DownloadResult();
		ExecutorService service = Executors.newFixedThreadPool(downloadFileRequest.getTaskNum());
		ArrayList<Future<PartResultDown>> futures = new ArrayList<Future<PartResultDown>>();
		
		ProgressManager progressManager = null;
		if(downloadFileRequest.getProgressListener() == null) {
			for (int i = 0; i < downloadCheckPoint.downloadParts.size(); i++) {
				DownloadPart downloadPart = downloadCheckPoint.downloadParts.get(i);
				if (!downloadPart.isCompleted) {
					Task task = new Task(i, "download-" + i, downloadCheckPoint, i, downloadFileRequest, this.obsClient);
					futures.add(service.submit(task));
				} else {
					taskResults.add(new PartResultDown(i + 1, downloadPart.offset, downloadPart.end));
				}
			}
		}else {
			List<Task> unfinishedTasks = new LinkedList<Task>();
			long transferredBytes = 0L;
			for (int i = 0; i < downloadCheckPoint.downloadParts.size(); i++) {
				DownloadPart downloadPart = downloadCheckPoint.downloadParts.get(i);
				if (!downloadPart.isCompleted) {
					Task task = new Task(i, "download-" + i, downloadCheckPoint, i, downloadFileRequest, this.obsClient);
					unfinishedTasks.add(task);
				} else {
					transferredBytes += downloadPart.end - downloadPart.offset + 1;
					taskResults.add(new PartResultDown(i + 1, downloadPart.offset, downloadPart.end));
				}
			}
			
			progressManager = new ConcurrentProgressManager(downloadCheckPoint.objectStatus.size, transferredBytes, downloadFileRequest.getProgressListener(), 
					downloadFileRequest.getProgressInterval() > 0 ? downloadFileRequest.getProgressInterval() : ObsConstraint.DEFAULT_PROGRESS_INTERVAL);
			for(Task task : unfinishedTasks) {
				task.setProgressManager(progressManager);
				futures.add(service.submit(task));
			}
		}
		
		service.shutdown();
		service.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		
		
		for (Future<PartResultDown> future : futures) {
			try {
				PartResultDown tr = future.get();
				taskResults.add(tr);
			} catch (ExecutionException e) {
				throw e;
			}
		}

		downloadResult.setPartResults(taskResults);
		if(progressManager != null) {
			progressManager.progressEnd();
		}
		
		return downloadResult;
	}

	static class Task implements Callable<PartResultDown> {

		private int id;
		private String name;
		private DownloadCheckPoint downloadCheckPoint;
		private int partIndex;
		private DownloadFileRequest downloadFileRequest;
		private ObsClient obsClient;
		private ProgressManager progressManager;

		public Task(int id, String name, DownloadCheckPoint downloadCheckPoint, int partIndex,
				DownloadFileRequest downloadFileRequest, ObsClient obsClient) {
			this.id = id;
			this.name = name;
			this.downloadCheckPoint = downloadCheckPoint;
			this.partIndex = partIndex;
			this.downloadFileRequest = downloadFileRequest;
			this.obsClient = obsClient;
		}

		@Override
		public PartResultDown call() throws Exception {
			RandomAccessFile output = null;
			InputStream content = null;
			DownloadPart downloadPart = downloadCheckPoint.downloadParts.get(partIndex);
			PartResultDown tr = new PartResultDown(partIndex + 1, downloadPart.offset, downloadPart.end);
			if (!downloadCheckPoint.isAbort) {
				try {
					output = new RandomAccessFile(downloadFileRequest.getTempDownloadFile(), "rw");
					output.seek(downloadPart.offset);

					GetObjectRequest getObjectRequest = new GetObjectRequest(downloadFileRequest.getBucketName(),
							downloadFileRequest.getObjectKey());

					getObjectRequest.setIfMatchTag(downloadFileRequest.getIfMatchTag());
					getObjectRequest.setIfNoneMatchTag(downloadFileRequest.getIfNoneMatchTag());
					getObjectRequest.setIfModifiedSince(downloadFileRequest.getIfModifiedSince());
					getObjectRequest.setIfUnmodifiedSince(downloadFileRequest.getIfUnmodifiedSince());
					getObjectRequest.setRangeStart(downloadPart.offset);
					getObjectRequest.setRangeEnd(downloadPart.end);
					getObjectRequest.setVersionId(downloadFileRequest.getVersionId());
					getObjectRequest.setCacheOption(downloadFileRequest.getCacheOption());
					getObjectRequest.setTtl(downloadFileRequest.getTtl());
					
					
					ObsObject object = obsClient.getObject(getObjectRequest);
					content = object.getObjectContent();
					if(this.progressManager != null) {
						content = new ProgressInputStream(content, this.progressManager, false);
					}

					byte[] buffer = new byte[ObsConstraint.DEFAULT_CHUNK_SIZE];
					int bytesOffset;
					while ((bytesOffset = content.read(buffer)) != -1) {
						output.write(buffer, 0, bytesOffset);
					}
					downloadCheckPoint.update(partIndex, true, downloadFileRequest.getTempDownloadFile());
				} catch (ObsException e) {
					if (e.getResponseCode() >= 300 && e.getResponseCode() < 500 && e.getResponseCode() != 408) {
						downloadCheckPoint.isAbort = true;
					}
					tr.setFailed(true);
					tr.setException(e);
					if(log.isErrorEnabled()) {
						log.error(String.format("Task %d:%s download part %d failed: ", id, name, partIndex), e);
					}
				} catch(Exception e) {
					tr.setFailed(true);
					tr.setException(e);
					if(log.isErrorEnabled()) {
						log.error(String.format("Task %d:%s download part %d failed: ", id, name, partIndex), e);
					}
				}finally {
					ServiceUtils.closeStream(output);
					ServiceUtils.closeStream(content);
					if (downloadFileRequest.isEnableCheckpoint()) {
						downloadCheckPoint.updateTmpFile(downloadFileRequest.getTempDownloadFile());
						downloadCheckPoint.record(downloadFileRequest.getCheckpointFile());
					}
				}
			} else {
				tr.setFailed(true);
			}
			return tr;
		}

		public void setProgressManager(ProgressManager progressManager) {
			this.progressManager = progressManager;
		}
	}

	private void prepare(DownloadFileRequest downloadFileRequest, DownloadCheckPoint downloadCheckPoint, ObjectMetadata objectMetadata)
			throws Exception {
		downloadCheckPoint.bucketName = downloadFileRequest.getBucketName();
		downloadCheckPoint.objectKey = downloadFileRequest.getObjectKey();
		downloadCheckPoint.versionId = downloadFileRequest.getVersionId();
		downloadCheckPoint.downloadFile = downloadFileRequest.getDownloadFile();
		ObjectStatus objStatus = new ObjectStatus();
		objStatus.size = objectMetadata.getContentLength();
		objStatus.lastModified = objectMetadata.getLastModified();
		objStatus.Etag = objectMetadata.getEtag();
		downloadCheckPoint.objectStatus = objStatus;
		downloadCheckPoint.downloadParts = splitObject(downloadCheckPoint.objectStatus.size,
				downloadFileRequest.getPartSize());
		File tmpfile = new File(downloadFileRequest.getTempDownloadFile());
		tmpfile.getParentFile().mkdirs();
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(tmpfile, "rw");
			randomAccessFile.setLength(downloadCheckPoint.objectStatus.size);
		} finally {
			ServiceUtils.closeStream(randomAccessFile);
		}
		downloadCheckPoint.tmpFileStatus = new TmpFileStatus(downloadCheckPoint.objectStatus.size,
				new Date(tmpfile.lastModified()), downloadFileRequest.getTempDownloadFile());
		
		if (downloadFileRequest.isEnableCheckpoint()) {
			try {
				downloadCheckPoint.record(downloadFileRequest.getCheckpointFile());
			} catch (Exception e) {
				if (tmpfile.exists() && tmpfile.isFile()) {
					tmpfile.delete();
				}
				throw e;
			}
		}
	}

	private ArrayList<DownloadPart> splitObject(long size, long partSize) {
		ArrayList<DownloadPart> parts = new ArrayList<DownloadPart>();

		long piece = size / partSize;
		if (piece >= 10000) {
			partSize = size % 10000 == 0? size/10000 : size /10000 + 1;
		}

		long offset = 0l;
		for (int i = 0; offset < size; offset += partSize, i++) {
			DownloadPart downloadPart = new DownloadPart();
			downloadPart.partNumber = i;
			downloadPart.offset = offset;
			if (offset + partSize > size) {
				downloadPart.end = size - 1;
			} else {
				downloadPart.end = offset + partSize - 1;
			}
			parts.add(downloadPart);
		}
		return parts;
	}
	
	/**
	 * 断点续传的下载所需类
	 */
	static class DownloadCheckPoint implements Serializable {
		private static final long serialVersionUID = 2282950186694419179L;

		public int md5;
		public String bucketName;
		public String objectKey;
		public String versionId;
		public String downloadFile;
		public ObjectStatus objectStatus;
		public TmpFileStatus tmpFileStatus;
		ArrayList<DownloadPart> downloadParts;
		public transient volatile boolean isAbort = false;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((bucketName == null) ? 0 : bucketName.hashCode());
			result = prime * result + ((downloadFile == null) ? 0 : downloadFile.hashCode());
			result = prime * result + ((versionId == null) ? 0 : versionId.hashCode());
			result = prime * result + ((objectKey == null) ? 0 : objectKey.hashCode());
			result = prime * result + ((objectStatus == null) ? 0 : objectStatus.hashCode());
			result = prime * result + ((tmpFileStatus == null) ? 0 : tmpFileStatus.hashCode());
			result = prime * result + ((downloadParts == null) ? 0 : downloadParts.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			} else {
				if (obj instanceof DownloadCheckPoint) {
					DownloadCheckPoint downloadCheckPoint = (DownloadCheckPoint) obj;
					if (downloadCheckPoint.hashCode() == this.hashCode()) {
						return true;
					}
				}
			}
			return false;
		}

		/**
		 * 从checkpoint文件中加载checkpoint数据
		 * 
		 * @param checkPointFile
		 * @throws Exception
		 */
		public void load(String checkPointFile) throws Exception {
			FileInputStream fileIn = null;
			SecureObjectInputStream in = null;
			try {
				fileIn = new FileInputStream(checkPointFile);
				in = new SecureObjectInputStream(fileIn);
				DownloadCheckPoint info = (DownloadCheckPoint) in.readObject();
				assign(info);
			} finally {
				ServiceUtils.closeStream(in);
				ServiceUtils.closeStream(fileIn);
			}
		}

		private void assign(DownloadCheckPoint info) {
			this.md5 = info.md5;
			this.downloadFile = info.downloadFile;
			this.bucketName = info.bucketName;
			this.objectKey = info.objectKey;
			this.versionId = info.versionId;
			this.objectStatus = info.objectStatus;
			this.tmpFileStatus = info.tmpFileStatus;
			this.downloadParts = info.downloadParts;
		}

		/**
		 * 判断序列化文件、临时文件和实际信息是否一致
		 * 
		 * @param tmpFilePath
		 * @param obsClient
		 * @return
		 */
		public boolean isValid(String tmpFilePath, ObjectMetadata objectMetadata) {
			if (this.md5 != hashCode()) {
				return false;
			}
			if (objectMetadata.getContentLength() != this.objectStatus.size
					|| !objectMetadata.getLastModified().equals(this.objectStatus.lastModified)
					|| !objectMetadata.getEtag().equals(this.objectStatus.Etag)) {
				return false;
			}

			File tmpfile = new File(tmpFilePath);
			if (this.tmpFileStatus.size != tmpfile.length()) {
				return false;
			}
			return true;
		}

		/**
		 * 分片下载成功后，更新分片和临时文件信息
		 * 
		 * @param index
		 * @param completed
		 * @param tmpFilePath
		 * @throws IOException
		 */
		public synchronized void update(int index, boolean completed, String tmpFilePath) throws IOException {
			downloadParts.get(index).isCompleted = completed;
			File tmpfile = new File(tmpFilePath);
			this.tmpFileStatus.lastModified = new Date(tmpfile.lastModified());
		}

		/**
		 * 出现网络异常时,更新临时文件的修改时间
		 * 
		 * @param tmpFilePath
		 * @throws IOException
		 */
		public synchronized void updateTmpFile(String tmpFilePath) throws IOException {
			File tmpfile = new File(tmpFilePath);
			this.tmpFileStatus.lastModified = new Date(tmpfile.lastModified());
		}

		/**
		 * 把DownloadCheckPoint数据写到序列化文件
		 * 
		 * @throws IOException
		 */
		public synchronized void record(String checkPointFilePath) throws IOException {
			FileOutputStream fileOutStream = null;
			ObjectOutputStream objOutStream = null;
			this.md5 = hashCode();
			try {
				fileOutStream = new FileOutputStream(checkPointFilePath);
				objOutStream = new ObjectOutputStream(fileOutStream);
				objOutStream.writeObject(this);
			} finally {
				if (objOutStream != null) {
					try {
						objOutStream.close();
					} catch (Exception e) {
					}
				}
				if (fileOutStream != null) {
					try {
						fileOutStream.close();
					} catch (Exception e) {
					}
				}
			}
		}
	}

	static class ObjectStatus implements Serializable {

		private static final long serialVersionUID = -6267040832855296342L;

		public long size; // 桶中对象大小
		public Date lastModified; // 对象的最后修改时间
		public String Etag; // 对象的Etag

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((Etag == null) ? 0 : Etag.hashCode());
			result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
			result = prime * result + (int) (size ^ (size >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			} else {
				if (obj instanceof ObjectStatus) {
					ObjectStatus objectStatus = (ObjectStatus) obj;
					if (objectStatus.hashCode() == this.hashCode()) {
						return true;
					}
				}
			}
			return false;
		}
	}

	static class TmpFileStatus implements Serializable {
		private static final long serialVersionUID = 4478330948103112660L;

		public long size; // 对象大小
		public Date lastModified; // 对象的最后修改时间
		public String tmpFilePath;

		public TmpFileStatus(long size, Date lastMoidified, String tmpFilePath) {
			this.size = size;
			this.lastModified = lastMoidified;
			this.tmpFilePath = tmpFilePath;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
			result = prime * result + ((tmpFilePath == null) ? 0 : tmpFilePath.hashCode());
			result = prime * result + (int) (size ^ (size >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			} else {
				if (obj instanceof TmpFileStatus) {
					TmpFileStatus tmpFileStatus = (TmpFileStatus) obj;
					if (tmpFileStatus.hashCode() == this.hashCode()) {
						return true;
					}
				}
			}
			return false;
		}
	}

	static class DownloadPart implements Serializable {

		private static final long serialVersionUID = 961987949814206093L;

		public int partNumber; // 分片序号，从0开始编号
		public long offset; // 分片起始位置
		public long end; // 分片片结束位置
		public boolean isCompleted; // 该分片下载是否完成

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + partNumber;
			result = prime * result + (isCompleted ? 0 : 8);
			result = prime * result + (int) (end ^ (end >>> 32));
			result = prime * result + (int) (offset ^ (offset >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			} else {
				if (obj instanceof DownloadPart) {
					DownloadPart downloadPart = (DownloadPart) obj;
					if (downloadPart.hashCode() == this.hashCode()) {
						return true;
					}
				}
			}
			return false;
		}
	}

	static class PartResultDown {
		private int partNumber; // 分片序号，从1开始编号
		private long start; // 分片开始位置
		private long end; // 分片结束位置
		private boolean isFailed; // 分片上传是否失败
		private Exception exception; // 分片上传异常
		
		public PartResultDown(int partNumber, long start, long end) {
			this.partNumber = partNumber;
			this.start = start;
			this.end = end;
		}

		/**
		 * 获取分片的起始位置
		 * 
		 * @return 分片的起始位置
		 */
		public long getStart() {
			return start;
		}

		/**
		 * 设置分片的起始位置
		 * 
		 * @param start
		 *            分片起始位置
		 */
		public void setStart(long start) {
			this.start = start;
		}

		/**
		 * 获取分片的结束位置
		 * 
		 * @return 分片的结束位置
		 */
		public long getEnd() {
			return end;
		}

		/**
		 * 设置分片的结束位置
		 * 
		 * @param end
		 *            分片结束位置
		 */
		public void setEnd(long end) {
			this.end = end;
		}

		/**
		 * 获取分片的编号
		 * 
		 * @return 分片的编号
		 */
		public int getpartNumber() {
			return partNumber;
		}

		/**
		 * 获取分片的下载状态
		 * 
		 * @return 分片的下载状态
		 */
		public boolean isFailed() {
			return isFailed;
		}

		/**
		 * 设置分片的下载状态
		 * 
		 * @param failed
		 *            分片的下载状态
		 */
		public void setFailed(boolean failed) {
			this.isFailed = failed;
		}

		/**
		 * 获取分片的下载异常
		 * 
		 * @return 分片的下载异常
		 */
		public Exception getException() {
			return exception;
		}

		/**
		 * 设置分片的下载异常
		 * 
		 * @param exception
		 *            分片的下载异常
		 */
		public void setException(Exception exception) {
			this.exception = exception;
		}

	}

	static class DownloadResult {
		
		private List<PartResultDown> partResults;
		
		/**
		 * 获取分片的上传最终结果
		 * 
		 * @return 分片的上传汇总结果
		 */
		public List<PartResultDown> getPartResults() {
			return partResults;
		}

		/**
		 * 设置分片的上传最终结果
		 * 
		 * @param partResults
		 *            分片的上传汇总结果
		 */
		public void setPartResults(List<PartResultDown> partResults) {
			this.partResults = partResults;
		}


	}
}
