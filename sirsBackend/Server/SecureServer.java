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
import javax.crypto.spec.SecretKeySpec;
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
			return;
		}
		final int port = Integer.parseInt(args[0]);

		final String keyPath = args[1];

		byte[] bufRSA = new byte[256];
		byte[] bufAES = new byte[48];
		DatagramSocket socket = new DatagramSocket(port);		

		DatagramPacket clientPacketAES = new DatagramPacket(bufAES, bufAES.length);
		DatagramPacket clientPacketRSA = new DatagramPacket(bufRSA, bufRSA.length);
		socket.receive(clientPacketRSA);


		Key key = null;
		String decryptedText = null, pSM = null;
		Integer tokenRcvd = 0, preSecretMaster = 0;
		// Create server socket
		InetAddress clientAddress = clientPacketRSA.getAddress();
		byte[] clientData = clientPacketRSA.getData(), clientDataWhile = null, responseBt = null, finalCipherText = clientData, serverData = null;
		byte[] secretKeyinByte = null;
		double tokenDouble = Math.abs(Math.random());
		Integer token = (int)tokenDouble;
		int clientPort = clientPacketRSA.getPort(), clientLength = clientPacketRSA.getLength();

		System.out.printf("Received request packet from %s:%d!%n", clientAddress, clientPort);

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
			body = requestJson.get("info").getAsString();
			preSecretMaster = Integer.parseInt(requestJson.get("preMasterSecret").getAsString());
			from = requestJson.get("from").getAsString();
		}

		pSM = preSecretMaster.toString();

		try{
			secretKeyinByte = digest(pSM.getBytes(UTF_8), "SHA3-256");
		} catch(Exception e){
			System.out.println("errou no sha3");
		}

		SecretKey secretKey = new SecretKeySpec(secretKeyinByte, 0, secretKeyinByte.length, "AES");

		System.out.println(String.format("PREMASTERSECRET: %s",bytesToHex(secretKeyinByte)));

		// Create response message
		JsonObject responseJson = JsonParser.parseString​("{}").getAsJsonObject();
		{
			responseJson.addProperty("token", token.toString());
			String bodyText = "Connection established";
			responseJson.addProperty("info", bodyText);
		}

		System.out.println(responseJson.toString());

		try{
			serverData = do_Encryption(responseJson.toString(), secretKey);
		} catch(Exception e){
			System.out.println("Errou1");
		}

		System.out.println(String.format("MENSAGEM ENCRYPTADA ENVIADA: %s %d",bytesToHex(serverData), serverData.length));

		// Send response
		DatagramPacket serverPacket = new DatagramPacket(serverData, serverData.length, clientPacketRSA.getAddress(), clientPacketRSA.getPort());
		socket.send(serverPacket);
		System.out.printf("Response packet sent to %s:%d!%n", clientPacketRSA.getAddress(), clientPacketRSA.getPort());

		while (true) {
			//-------------------------------------------- RECEBER PEDIDOS E DESENCRIPTAR COM CHAVE SECRETA

			socket.receive(clientPacketAES);
			clientAddress = clientPacketAES.getAddress();
			clientPort = clientPacketAES.getPort();
			clientLength = clientPacketAES.getLength();
			clientData = clientPacketAES.getData();
			System.out.printf("Received request packet from %s:%d!%n", clientAddress, clientPort);
			finalCipherText = clientData;

			System.out.println(String.format("Recebi: %s %d",bytesToHex(finalCipherText), finalCipherText.length));

			try{
				decryptedText = do_Decryption(finalCipherText, secretKey);
			} catch(Exception e){
				System.out.println("Errou");
			}

			// Parse JSON and extract arguments
			requestJson = JsonParser.parseString​(decryptedText).getAsJsonObject();
			{
				body = requestJson.get("info").getAsString();
				tokenRcvd = Integer.parseInt(requestJson.get("token").getAsString());
			}

			if((token + 1) == tokenRcvd){
				token = tokenRcvd;
				// ENCRIPTAR TUDO COM CHAVE SECRETA para mandar resposta

				token++;

				// Create response message
				JsonObject responseJsonWhile = JsonParser.parseString​("{}").getAsJsonObject();
				{
					responseJson.addProperty("token", token.toString());
					String bodyText = "Request done";
					responseJson.addProperty("info", bodyText);
				}

				try{
					serverData = do_Encryption(responseJsonWhile.toString(), secretKey);
				} catch(Exception e){
					System.out.println("Errou");
				}

				// Send response
				DatagramPacket serverPacketWhile = new DatagramPacket(serverData, serverData.length, clientPacketAES.getAddress(), clientPacketAES.getPort());
				socket.send(serverPacketWhile);
				System.out.printf("Response packet sent to %s:%d!%n", clientPacketAES.getAddress(), clientPacketAES.getPort());
			}
			else{
				System.out.println("Token errado");
			}
		}
	}
}
