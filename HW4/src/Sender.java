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

public class Sender {
	private static int BUFFER_SIZE = 32 * 1024;

	public static void main(String[] args) throws Exception {
		PublicKey pubKey = readPubKeyFromFile("YPublic.key");

		System.out.print("Enter message filename: ");
		Scanner reader = new Scanner(System.in);
		String messageFile = reader.nextLine();
		reader.close();
		
		// Write Kxy||M||Kxy to file "message.kmk"
		FileOutputStream kmk = new FileOutputStream("message.kmk");
		byte[] Kxy = Files.readAllBytes(Paths.get("symmetric.key"));
		kmk.write(Kxy);
		System.out.println("\nKxy:");
		for (int k = 0, j = 0; k < Kxy.length; k++, j++) {
			System.out.format("%2X ", new Byte(Kxy[k]));
			if (j >= 15) {
				System.out.println("");
				j = -1;
			}
		}
		FileInputStream message = new FileInputStream(messageFile);
		byte[] binary = new byte[message.available()];
		while (message.available() > 0) {
			binary = new byte[message.available()];
			message.read(binary);
			kmk.write(binary);
		}
		kmk.write(Kxy);
		kmk.close();

		// Read file "message.kmk" and get its keyed hash MAC
		binary = md("message.kmk");
		FileOutputStream khmac = new FileOutputStream("message.khmac");
		khmac.write(binary);
		khmac.close();
		System.out.println("\nkeyed hash MAC:");
		for (int k = 0, j = 0; k < binary.length; k++, j++) {
			System.out.format("%2X ", new Byte(binary[k]));
			if (j >= 15) {
				System.out.println("");
				j = -1;
			}
		}
		
		//AES Encryption with Kxy
		Cipher cipherText = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
		// Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding", "SunJCE");
		// Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding", "SunJCE");
		String sym = readSymmetricFromFile("symmetric.key");
		SecretKeySpec key = new SecretKeySpec(sym.getBytes("UTF-8"), "AES");
		String IV = "AAAAAAAAAAAAAAAA";
		cipherText.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
		message.close();
		message = new FileInputStream(messageFile);
		binary = new byte[16];
		FileOutputStream aes = new FileOutputStream("message.aescipher");
		while (message.available() > 16) {
			message.read(binary);
			aes.write(cipherText.update(binary));
		}
		if (message.available() != 0) {
			binary = new byte[message.available()];
			message.read(binary);
			aes.write(cipherText.doFinal(binary));
		}
		aes.close();
		
		//Encrypt with YPubkey
		FileOutputStream rsa = new FileOutputStream("kxy.rsacipher");
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		byte[] cipherText2 = cipher.doFinal(Kxy);
		rsa.write(cipherText2);
		rsa.close();
		message.close();
		System.out.println("\nFinished.");
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

	public static String readSymmetricFromFile(String fileName) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(fileName));
		return new String(encoded, StandardCharsets.UTF_8);
	}

	// read key parameters from a file and generate the public key
	public static PublicKey readPubKeyFromFile(String keyFileName) throws IOException {

		InputStream in = Sender.class.getClassLoader().getResourceAsStream(keyFileName);
		ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));
		try {
			in = Sender.class.getClassLoader().getResourceAsStream(keyFileName);
			oin = new ObjectInputStream(new BufferedInputStream(in));
			BigInteger m = (BigInteger) oin.readObject();
			BigInteger e = (BigInteger) oin.readObject();

			System.out.println(
					"Read from " + keyFileName + ": modulus = " + m.toString() + ", exponent = " + e.toString() + "\n");

			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
			KeyFactory factory = KeyFactory.getInstance("RSA");
			PublicKey key = factory.generatePublic(keySpec);
			return key;
		} catch (Exception e) {
			throw new RuntimeException("Spurious serialisation error", e);
		} finally {
			oin.close();
		}
	}

	// read key parameters from a file and generate the private key
	public static PrivateKey readPrivKeyFromFile(String keyFileName) throws IOException {

		InputStream in = Sender.class.getResourceAsStream(keyFileName);
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

}
