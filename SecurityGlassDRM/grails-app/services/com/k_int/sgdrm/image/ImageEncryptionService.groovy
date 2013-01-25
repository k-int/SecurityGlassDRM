package com.k_int.sgdrm.image

import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import org.apache.commons.codec.binary.Hex



class ImageEncryptionService {


	@javax.annotation.PostConstruct
	def init() {
		log.debug("ImageEncryptionService::init called");
	}

	/**
	 * Encrypt the specified file using AES 256
	 */
	def encrypt(encKey, workingDir, inFileName, outFileName) {
		log.debug("ImageEncryptionService::encrypt called");
		
		// Set up the input and output streams
		FileInputStream inStream = new FileInputStream(new File(workingDir, inFileName));
		FileOutputStream outStream = new FileOutputStream(new File(workingDir, outFileName));
		
		// Set up the encryption mechanism
		SecretKey key = convertKeyStringToSecret(encKey)
		Cipher cipherTool = Cipher.getInstance("AES");
		cipherTool.init(Cipher.ENCRYPT_MODE, key);
		CipherInputStream cipInStream = new CipherInputStream(inStream, cipherTool);
		
		// Copy the file and perform the encryption on the way
		//copyFile(cipInStream, outStream);
		copyFile(inStream, outStream);
		
	}
	
	def decrypt(encKey, workingDir, inFileName, outFileName) {
		log.debug("ImageEncryptionService::decrypt called");
		
		// Set up the input and output streams
		FileInputStream inStream = new FileInputStream(new File(workingDir, inFileName));
		FileOutputStream outStream = new FileOutputStream(new File(workingDir, outFileName));
		
		// Set up the encryption mechanism
		SecretKey key = convertKeyStringToSecret(encKey)
		Cipher cipherTool = Cipher.getInstance("AES");
		cipherTool.init(Cipher.DECRYPT_MODE, key);
		CipherOutputStream cipOutStream = new CipherOutputStream(outStream, cipherTool);
		
		// Copy the file and perform the encryption on the way
		copyFile(inStream, cipOutStream);
		
	}
	
	def generateKey() {
		log.debug("ImageEncryptionService::generateKey called");
		
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(256); // 256 bit encryption - requires special policy files installed in the jre
		SecretKey secretKey = keyGen.generateKey();
		
		log.debug("secretKey length = " + secretKey.getEncoded().length);
		
		String keyString = Hex.encodeHexString(secretKey.getEncoded());
		log.debug("encoded key = " + secretKey.getEncoded());
		log.debug("and key as string = " + keyString);
		
		return secretKey
	}
	
	def convertKeyStringToSecret(keyString) {
		
		byte[] keyAsArray = Hex.decodeHex(keyString.toCharArray());
		SecretKey realKey = new SecretKeySpec(keyAsArray, "AES")

		return realKey;
	}
	
	def convertSecretKeyToString(secretKey) {
		String keyString = Hex.encodeHexString(secretKey.getEncoded());
		
		return keyString;
	}
	
	def copyFile(inStream, outStream) throws IOException {
		log.debug("ImageEncryptionService::copyFile called");
		
		byte[] bytes = new byte[64];
		int numBytes;
		while ((numBytes = inStream.read(bytes)) != -1) {
			outStream.write(bytes, 0, numBytes);
		}
		
		outStream.flush();
		outStream.close();
		inStream.close();
	}

}
