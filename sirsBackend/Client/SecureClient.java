package pt.tecnico;

import java.io.*;
import java.net.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Scanner;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.Arrays.*;
import java.lang.Math;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.xml.bind
    .DatatypeConverter;
import javax.crypto.spec.SecretKeySpec;
import com.google.gson.*;


public class SecureClient {

	/** Buffer size for receiving a UDP packet. */
	private static final int BUFFER_SIZE = 65_507;
	private static final Charset UTF_8 = StandardCharsets.UTF_8;

    public static byte[] do_Encryption(String plainText,SecretKey key) throws Exception
    {
		Cipher cipher = Cipher.getInstance(key.getAlgorithm());

		cipher.init(Cipher.ENCRYPT_MODE, key);

		return cipher.doFinal(plainText.getBytes());
    }

    public static String do_Decryption(byte[] cipherText,SecretKey key) throws Exception
    {
		Cipher cipher = Cipher.getInstance(key.getAlgorithm());

		cipher.init(Cipher.DECRYPT_MODE, key);

		byte[] result = cipher.doFinal(cipherText);
		
		return new String(result);
    }

    public static byte[] do_RSAEncryption(String plainText,Key key) throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA");
 
        cipher.init(Cipher.ENCRYPT_MODE, key);
 
        return cipher.doFinal(plainText.getBytes());
    }

    private static byte[] readFile(String path) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(path);
        byte[] content = new byte[fis.available()];
        fis.read(content);
        fis.close();
        return content;
    }

    public static PublicKey readPublicKey(String publicKeyPath) throws Exception {
        System.out.println("Reading public key from file " + publicKeyPath + " ...");
        byte[] pubEncoded = readFile(publicKeyPath);
        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubEncoded);
        KeyFactory keyFacPub = KeyFactory.getInstance("RSA");
        PublicKey pub = keyFacPub.generatePublic(pubSpec);
        return pub;
    }

    public static byte[] digest(byte[] input, String algorithm) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        byte[] result = md.digest(input);
        return result;
    }
	public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
	public static void main(String[] args) throws IOException {
		// Check arguments
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			return;
		}
		final String serverHost = args[0];
		final InetAddress serverAddress = InetAddress.getByName(serverHost);
		final int serverPort = Integer.parseInt(args[1]);
		final String keyPath = args[2];
		
		Integer token = 0;
		Integer tokenRcvd = 0;
		Long pSM = Math.round(Math.abs(Math.random()) * 1000000);
		String decryptedText = null;

		Key key = null;
		byte[] cipherText = null, secretKeyinByte = null, serverData = null, rcvdMsg = null;

		// Create socket
		DatagramSocket socket = new DatagramSocket();

        // Create request message
		JsonObject requestJson = JsonParser.parseString​("{}").getAsJsonObject();
		{
			JsonObject infoJson = JsonParser.parseString​("{}").getAsJsonObject();
			infoJson.addProperty("from", "Alice");
			requestJson.add("info", infoJson); 
			requestJson.addProperty("preSecretMaster", pSM);
			String bodyText = "Hello." + System.lineSeparator() + "Do you want to meet tomorrow?";
			requestJson.addProperty("body", bodyText);
		}
		try{
			secretKeyinByte = digest(pSM.toString().getBytes(UTF_8), "SHA3-256");
		} catch(Exception e){
			System.out.println("errou no sha3");
		}
		System.out.println(String.format("PREMASTERSECRET: %s",bytesToHex(secretKeyinByte)));

		String plainText = requestJson.toString();

		try{
			key = readPublicKey(keyPath);
		}catch(Exception e){
			System.out.println("Errooooooooooooooooooouuuuuuuuuuuuuuu");
		}

		try{
			cipherText = do_RSAEncryption(plainText, key);
		} catch(Exception e){
			System.out.println("Errou");
		}

		// Send request
		DatagramPacket clientPacket = new DatagramPacket(cipherText, cipherText.length, serverAddress, serverPort);
		socket.send(clientPacket);
		System.out.printf("Request packet sent to %s:%d!%n", serverAddress, serverPort);

		SecretKey secretKey = new SecretKeySpec(secretKeyinByte, 0, secretKeyinByte.length, "AES");

		// Receive response
		serverData = new byte[48];
		DatagramPacket serverPacket = new DatagramPacket(serverData, serverData.length);
		System.out.println("Wait for response packet...");
		socket.receive(serverPacket);
		rcvdMsg = serverPacket.getData();

		byte[] msg = new byte[rcvdMsg.length];
		msg = rcvdMsg;

		System.out.println(String.format("MENSAGEM ENCRYPTADA RECEBIDA: %s",bytesToHex(rcvdMsg)));

		try{
			decryptedText = do_Decryption(rcvdMsg, secretKey);
		} catch(Exception e){
			System.out.println("Errou1");
		}

		System.out.println(decryptedText);
		// Parse JSON and extract arguments
		JsonObject responseJson = JsonParser.parseString​(decryptedText).getAsJsonObject();
		String from = null, body = null;
		{
			body = requestJson.get("body").getAsString();
			tokenRcvd = Integer.parseInt(requestJson.get("token").getAsString());
		}
		token = tokenRcvd + 1;

		//------------------------------- CICLO WHILE A RECEBER RESPOSTA SERVER E RECEBER PEDIDO TERMINAL--------------------------------------

		while(true){

			JsonObject requestJsonWhile = JsonParser.parseString​("{}").getAsJsonObject();
			{
				requestJsonWhile.addProperty("token", token.toString());
				String bodyText = "Yes. See you tomorrow!";
				requestJsonWhile.addProperty("body", bodyText);
			}

			String plainTextWhile = requestJsonWhile.toString();

			try{
				key = readPublicKey(keyPath);
			}catch(Exception e){
				System.out.println("Errooooooooooooooooooouuuuuuuuuuuuuuu");
			}

			try{
				cipherText = do_Encryption(plainTextWhile, secretKey);
			} catch(Exception e){
				System.out.println("Errou");
			}

			// Send request
			DatagramPacket clientPacketWhile = new DatagramPacket(cipherText, cipherText.length, serverAddress, serverPort);
			socket.send(clientPacketWhile);
			System.out.println(String.format("Enviei: %s",bytesToHex(cipherText)));

// -------------------------------------------------------- RECEBER Resposta ----------------------------------------------------------
			while(true){
				// Receive response
				byte[] serverDataWhile = new byte[BUFFER_SIZE];
				DatagramPacket serverPacketWhile = new DatagramPacket(serverDataWhile, serverDataWhile.length);
				System.out.println("Wait for response packet...");
				socket.receive(serverPacketWhile);
				rcvdMsg = serverPacketWhile.getData();

				try{
					decryptedText = do_Decryption(rcvdMsg, secretKey);
				} catch(Exception e){
					System.out.println("Errou");
				}

				// Parse JSON and extract arguments
				JsonObject responseJsonWhile = JsonParser.parseString​(decryptedText).getAsJsonObject();
				String bodyWhile = null;
				{
					bodyWhile = requestJson.get("body").getAsString();
					tokenRcvd = Integer.parseInt(requestJson.get("token").getAsString());
				}
				
				//if we received response from server send msg
				if((token + 1) == tokenRcvd){
					token = tokenRcvd + 1;
					break;
				}
				//else ignore response
			}
		}
	}
}
