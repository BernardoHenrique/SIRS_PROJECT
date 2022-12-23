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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.xml.bind
    .DatatypeConverter;

import com.google.gson.*;


public class SecureServer {

	/**
	 * Maximum size for a UDP packet. The field size sets a theoretical limit of
	 * 65,535 bytes (8 byte header + 65,527 bytes of data) for a UDP datagram.
	 * However the actual limit for the data length, which is imposed by the IPv4
	 * protocol, is 65,507 bytes (65,535 − 8 byte UDP header − 20 byte IP header.
	 */
	private static final int MAX_UDP_DATA_SIZE = (64 * 1024 - 1) - 8 - 20;

	/** Buffer size for receiving a UDP packet. */
	private static final int BUFFER_SIZE = MAX_UDP_DATA_SIZE;
	private static final Charset UTF_8 = StandardCharsets.UTF_8;

    public static String do_RSADecryption(byte[] cipherText, Key key) throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA");
 
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] result = cipher.doFinal(cipherText);
 
        return new String(result);
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
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s port%n", SecureServer.class.getName());
			return;
		}
		final int port = Integer.parseInt(args[0]);

		final String keyPath = args[1];

		Key key = null;
		String decryptedText = null;
		byte[] secretKeyinByte = null;
		Integer token = -1, tokenRcvd = 0, preSecretMaster = 0;
		// Create server socket
		DatagramSocket socket = new DatagramSocket(port);
		System.out.printf("Server will receive packets on port %d %n", port);

		// Wait for client packets 
		byte[] buf = new byte[256];

		// Receive packet
		DatagramPacket clientPacket = new DatagramPacket(buf, buf.length);
		socket.receive(clientPacket);
		InetAddress clientAddress = clientPacket.getAddress();
		int clientPort = clientPacket.getPort();
		int clientLength = clientPacket.getLength();
		byte[] clientData = clientPacket.getData();
		System.out.printf("Received request packet from %s:%d!%n", clientAddress, clientPort);
		System.out.printf("%d bytes %n", clientLength);
		byte[] finalCipherText = clientData;

		//byte[] finalCipherText = Arrays.copyOf(clientData, clientLength);    	AQUI

		System.out.println("Recebi:" + DatatypeConverter.printHexBinary(finalCipherText));

		try{
			key = readPrivateKey(keyPath);
		} catch(Exception e){
			System.out.println("Errouuuuuuuuuuuuuuu");
		}		

		try{
			decryptedText = do_RSADecryption(finalCipherText, key);
		} catch(Exception e){
			System.out.println("Errou");
		}

		// Parse JSON and extract arguments
		JsonObject requestJson = JsonParser.parseString​(decryptedText).getAsJsonObject();
		String from = null, body = null;
		{
			JsonObject infoJson = requestJson.getAsJsonObject("info");
			from = infoJson.get("from").getAsString();
			body = requestJson.get("body").getAsString();
			preSecretMaster = Integer.parseInt(requestJson.get("preSecretMaster").getAsString());
			tokenRcvd = Integer.parseInt(requestJson.get("token").getAsString());
		}

		while (true) {

			if(token == -1 || (token + 1) == tokenRcvd){
				// ENCRIPTAR TUDO COM CHAVE SECRETA para mandar resposta
				String pSM = preSecretMaster.toString();

				try{
					secretKeyinByte = digest(pSM.getBytes(UTF_8), "SHA3-256");
				} catch(Exception e){
					System.out.println("errou no sha3");
				}
				System.out.println(String.format("%s",bytesToHex(secretKeyinByte)));

				token = tokenRcvd;

				System.out.printf("Message from '%s':%n%s%n%d%n", from, body, token);

				// Create response message
				JsonObject responseJson = JsonParser.parseString​("{}").getAsJsonObject();
				{
					JsonObject infoJson = JsonParser.parseString​("{}").getAsJsonObject();
					infoJson.addProperty("from", "Bob");
					responseJson.add("info", infoJson);

					String bodyText = "Yes. See you tomorrow!";
					responseJson.addProperty("body", bodyText);
				}
				System.out.println("Response message: " + responseJson);

				// Send response
				byte[] serverData = responseJson.toString().getBytes();
				System.out.printf("%d bytes %n", serverData.length);
				DatagramPacket serverPacket = new DatagramPacket(serverData, serverData.length, clientPacket.getAddress(), clientPacket.getPort());
				socket.send(serverPacket);
				System.out.printf("Response packet sent to %s:%d!%n", clientPacket.getAddress(), clientPacket.getPort());
			}
			else{
				JsonObject responseJsonBad = JsonParser.parseString​("{}").getAsJsonObject();
				{
					JsonObject infoJson = JsonParser.parseString​("{}").getAsJsonObject();
					infoJson.addProperty("from", "Alice");
					responseJsonBad.add("info", infoJson);

					String bodyText = "es um mau hacker";
					responseJsonBad.addProperty("body", bodyText);
				}

				byte[] responseBt = responseJsonBad.toString().getBytes();
				DatagramPacket serverPacket = new DatagramPacket(responseBt, responseBt.length, clientPacket.getAddress(), clientPacket.getPort());
				socket.send(serverPacket);
				System.out.printf("Response packet sent to %s:%d!%n", clientPacket.getAddress(), clientPacket.getPort());
			}

			//-------------------------------------------- RECEBER PEDIDOS E DESENCRIPTAR COM CHAVE SECRETA
		}
	}
}
