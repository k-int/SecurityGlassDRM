import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class SimpleDecrypter {

	
	public static void decrypt(String encKey, String inFileName, String outFileName) throws FileNotFoundException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, DecoderException {
		
		// Set up the input and output streams
		FileInputStream inStream = new FileInputStream(new File(inFileName));
		FileOutputStream outStream = new FileOutputStream(new File(outFileName));
		
		// Set up the encryption mechanism
		SecretKey key = convertKeyStringToSecret(encKey);
		Cipher cipherTool = Cipher.getInstance("AES");
		cipherTool.init(Cipher.DECRYPT_MODE, key);
		CipherOutputStream cipOutStream = new CipherOutputStream(outStream, cipherTool);
		
		// Copy the file and perform the encryption on the way
		copyFile(inStream, cipOutStream);
		
	}
	
	public static SecretKey convertKeyStringToSecret(String keyString) throws DecoderException {
		
		byte[] keyAsArray = Hex.decodeHex(keyString.toCharArray());
		SecretKey realKey = new SecretKeySpec(keyAsArray, "AES");

		return realKey;
	}
	
	public static void copyFile(InputStream inStream, OutputStream outStream) throws IOException {
		
		byte[] bytes = new byte[64];
		int numBytes;
		while ((numBytes = inStream.read(bytes)) != -1) {
			outStream.write(bytes, 0, numBytes);
		}
		outStream.flush();
		outStream.close();
		inStream.close();
	}
	
	public static void main(String[] args) {
		
		// Usage SimpleDecrypter encKey inFileName outFileName
		
		if ( args.length != 3 ) {
			System.err.println("Incorrect number of arguments found. " + args.length + " found and 3 expected");
			System.err.println("Usage: SimpleDecrypter <encryptionKey> <inFileName> <outFileName>");
			System.exit(1);
		} else {
			
			String encKey = args[0];
			String inFileName = args[1];
			String outFileName = args[2];
			
			// Check in file exists
			File testInFile = new File(inFileName);
			File testOutFile = new File(outFileName);
			
			if ( !testInFile.exists() ) {
				System.err.println("The specified input file doesn't exist!");
				System.exit(1);
			} else {
				// File exists - check there's not an output file that we're going to overwrite
				if ( testOutFile.exists() ) {
					System.err.println("The specified output file already exists and would be overwritten - stopping");
					System.exit(1);
				} else {
					// Run the decryption...
					try {
						decrypt(encKey, inFileName, outFileName);
						
						System.out.println("File successfully decrypted");
					} catch (Exception e) {
						System.err.println("Exception thrown when performing the actual decryption: " + e.getMessage());
						e.printStackTrace();
						System.exit(1);
					}
				}
				
			}
		}
	}
}
