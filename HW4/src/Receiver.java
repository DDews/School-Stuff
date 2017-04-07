import java.io.*;

import java.security.Key;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Scanner;
import java.security.spec.RSAPrivateKeySpec;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;


public class Receiver {
	private static int BUFFER_SIZE = 32 * 1024;
	
	public static void main(String[] args) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		PrivateKey privKey = readPrivKeyFromFile("YPrivate.key");
		
		System.out.print("Enter message output filename: ");
		Scanner reader = new Scanner(System.in);
		String outputFile = reader.nextLine();
		reader.close();
		
		//Read Kxy ciphertext and decrypt it using the private key
		byte[] Kxy = Files.readAllBytes(Paths.get("kxy.rsacipher"));
		cipher.init(Cipher.DECRYPT_MODE, privKey);
		Kxy = cipher.doFinal(Kxy);
		FileOutputStream kmk = new FileOutputStream("message.kmk");
		kmk.write(Kxy);
		//Display Kxy
		System.out.println("\nKxy:");
		for (int k = 0, j = 0; k < Kxy.length; k++, j++) {
			System.out.format("%2X ", new Byte(Kxy[k]));
			if (j >= 15) {
				System.out.println("");
				j = -1;
			}
		}
		
		//Read AES Ciphertext and decrypt block by block into output file
		Cipher cipherText = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
		SecretKeySpec key = new SecretKeySpec(Kxy, "AES");
		String IV = "AAAAAAAAAAAAAAAA";
		cipherText.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
		FileInputStream aes = new FileInputStream("message.aescipher");
		byte[] binary = new byte[16];
		FileOutputStream message = new FileOutputStream(outputFile);
		while (aes.available() > 16) {
			aes.read(binary);
			byte[] output = cipherText.update(binary);
			message.write(output);
			kmk.write(output);
		}
		if (aes.available() > 0) {
			binary = new byte[aes.available()];
			aes.read(binary);
			byte[] output = cipherText.doFinal(binary);
			message.write(output);
			kmk.write(output);
		}
		kmk.write(Kxy);
		aes.close();
		message.close();
		kmk.close();
		
		//Read message.kmk block by block 
		binary = md("message.kmk");
		byte[] compare = readBytesFromFile("message.khmac");
		if (Arrays.equals(binary,compare)) System.out.println("\nThe message is authentic.\n");
		else System.out.println("\nThe message is not authentic.\n");
		
		System.out.println("computated keyed hash MAC:");
		for (int k = 0, j = 0; k < binary.length; k++, j++) {
			System.out.format("%2X ", new Byte(binary[k]));
			if (j >= 15) {
				System.out.println("");
				j = -1;
			}
		}
		
		System.out.println("\nkeyed hash MAC from file:");
		for (int k = 0, j = 0; k < compare.length; k++, j++) {
			System.out.format("%2X ", new Byte(compare[k]));
			if (j >= 15) {
				System.out.println("");
				j = -1;
			}
		}
		
	}
	public static byte[] readBytesFromFile(String filename) throws Exception {
		FileInputStream is = new FileInputStream(filename);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[1024 * 3];

		while ((nRead = is.read(data, 0, data.length)) != -1) {
		  buffer.write(data, 0, nRead);
		}

		buffer.flush();

		return buffer.toByteArray();
	}
	public static PrivateKey readPrivKeyFromFile(String keyFileName) throws IOException {

		InputStream in = Receiver.class.getResourceAsStream(keyFileName);
		ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));

		try {
			BigInteger m = (BigInteger) oin.readObject();
			BigInteger e = (BigInteger) oin.readObject();

			System.out.println(
					"Read from " + keyFileName + ": modulus = " + m.toString() + ", exponent = " + e.toString() + "\n");

			RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
			KeyFactory factory = KeyFactory.getInstance("RSA");
			PrivateKey key = factory.generatePrivate(keySpec);

			return key;
		} catch (Exception e) {
			throw new RuntimeException("Spurious serialisation error", e);
		} finally {
			oin.close();
		}
	}
	public static byte[] md(String f) throws Exception {
		BufferedInputStream file = new BufferedInputStream(new FileInputStream(f));
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		DigestInputStream in = new DigestInputStream(file, md);
		int i;
		byte[] buffer = new byte[BUFFER_SIZE];
		do {
			i = in.read(buffer, 0, BUFFER_SIZE);
		} while (i == BUFFER_SIZE);
		md = in.getMessageDigest();
		in.close();

		byte[] hash = md.digest();
		return hash;
	}
}
