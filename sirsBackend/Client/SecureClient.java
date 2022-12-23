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

import com.google.gson.*;


public class SecureClient {

	/** Buffer size for receiving a UDP packet. */
	private static final int BUFFER_SIZE = 65_507;
	private static final Charset UTF_8 = StandardCharsets.UTF_8;

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

    public static PrivateKey readPrivateKey(String privateKeyPath) throws Exception {
        byte[] privEncoded = readFile(privateKeyPath);
        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privEncoded);
        KeyFactory keyFacPriv = KeyFactory.getInstance("RSA");
        PrivateKey priv = keyFacPriv.generatePrivate(privSpec);
		return priv;
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
			System.err.printf("Usage: java %s host port%n", SecureClient.class.getName());
			return;
		}
		final String serverHost = args[0];
		final InetAddress serverAddress = InetAddress.getByName(serverHost);
		final int serverPort = Integer.parseInt(args[1]);

		final String keyPath = args[2];

		double tokenDouble = Math.abs(Math.random());
		Integer token = (int)tokenDouble;

		Key key = null;
		byte[] cipherText = null;
		byte[] secretKeyinByte = null;

		// Create socket
		DatagramSocket socket = new DatagramSocket();

		Long pSM = Math.round(Math.abs(Math.random()) * 1000000);

        // Create request message
		JsonObject requestJson = JsonParser.parseString​("{}").getAsJsonObject();
		{
			JsonObject infoJson = JsonParser.parseString​("{}").getAsJsonObject();
			infoJson.addProperty("from", "Alice");
			requestJson.addProperty("token", token.toString());
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
		System.out.println(String.format("%s",bytesToHex(secretKeyinByte)));

		System.out.println("Request message: " + requestJson);

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

		System.out.println("Enviei:" + DatatypeConverter.printHexBinary(cipherText));

		// Send request
		DatagramPacket clientPacket = new DatagramPacket(cipherText, cipherText.length, serverAddress, serverPort);
		socket.send(clientPacket);
		System.out.printf("Request packet sent to %s:%d!%n", serverAddress, serverPort);
		token++;

		//------------------------------- CICLO WHILE A RECEBER RESPOSTA SERVER E RECEBER PEDIDO TERMINAL--------------------------------------

		// Receive response
		byte[] serverData = new byte[BUFFER_SIZE];
		DatagramPacket serverPacket = new DatagramPacket(serverData, serverData.length);
		System.out.println("Wait for response packet...");
		socket.receive(serverPacket);
		System.out.printf("Received packet from %s:%d!%n", serverPacket.getAddress(), serverPacket.getPort());
		System.out.printf("%d bytes %n", serverPacket.getLength());

		// Convert response to string
		String serverText = new String(serverPacket.getData(), 0, serverPacket.getLength());
		System.out.println("Received response: " + serverText);

		// Parse JSON and extract arguments
		JsonObject responseJson = JsonParser.parseString​(serverText).getAsJsonObject();
		String from = null, body = null;
		{
			JsonObject infoJson = responseJson.getAsJsonObject("info");
			from = infoJson.get("from").getAsString();;
			body = responseJson.get("body").getAsString();
		}
		System.out.printf("Message from '%s':%n%s%n", from, body);

		// Close socket
		socket.close();
		System.out.println("Socket closed");
	}

}
