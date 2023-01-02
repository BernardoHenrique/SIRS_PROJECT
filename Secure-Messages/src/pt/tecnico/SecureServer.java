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
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SecureServer {

	/* Database stuff */
	static Connection con = null;
	static PreparedStatement p = null;
	static ResultSet rs = null;

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


	/*Encryption function with secret key */
    public static byte[] do_Encryption(String plainText, SecretKey key) throws Exception
    {
		Cipher cipher = Cipher.getInstance(key.getAlgorithm());

		cipher.init(Cipher.ENCRYPT_MODE, key);

		return cipher.doFinal(plainText.getBytes());
    }

	/*Decryption function with secret key */
    public static String do_Decryption(byte[] cipherText, SecretKey key) throws Exception
    {
		Cipher cipher = Cipher.getInstance(key.getAlgorithm());

		cipher.init(Cipher.DECRYPT_MODE, key);

		byte[] result = cipher.doFinal(cipherText);
		
		return new String(result);
    }

	/*Decryption function using RSA algorithm */
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

	/*Read private key sent by client */
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

	/*Database initialization */
	public static void InitializeDB(){

		try{
			Class.forName("org.postgresql.Driver");
		}catch (Exception e){
			System.out.println("Error in creating database");
		}

		int port = 5432;
		String database = "thecork";
		String username = "t048";
		String password = "1234";

		String url = "jdbc:postgresql://localhost" + ":" + port + "/" + database;

		//192.168.2.4

		try (Connection conn = DriverManager.getConnection(url, username, password)) {
			System.out.println("Connected to the PostgreSQL server successfully.");
		} catch (Exception e) {
			System.out.println("Failed to connect to database");
		}
	}

	/*Send querys to the database */
	public static void SendQuery(String sql){

		try{
			p = con.prepareStatement(sql);
			rs = p.executeQuery();
			while (rs.next())
			{
				int id = rs.getInt("cusid");
				String name = rs.getString("cusname");
				String email = rs.getString("email");
				System.out.println(id + "\t\t" + name + 
										"\t\t" + email);
			}
		} catch (Exception e){
			System.out.println("Error sending query to the database");
		}
	}

	public static void main(String[] args) throws IOException {
		// Check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			return;
		}

		//Initialize database
		InitializeDB();


		//Parse arguments and initialize variables
		final int port = Integer.parseInt(args[0]);

		final String keyPath = args[1];

		byte[] bufRSA = new byte[BUFFER_SIZE];
		byte[] bufAES = new byte[BUFFER_SIZE];

		//Create server socket
		DatagramSocket socket = new DatagramSocket(port);		

		DatagramPacket clientPacketAES = new DatagramPacket(bufAES, bufAES.length);
		DatagramPacket clientPacketRSA = new DatagramPacket(bufRSA, bufRSA.length);

		//receive first connection request
		socket.receive(clientPacketRSA);

		Key key = null;
		String decryptedText = null, preMasterSecret = null;
		Integer preSecretMaster = 0;
		InetAddress clientAddress = clientPacketRSA.getAddress();
		byte[] clientData = clientPacketRSA.getData(), clientDataWhile = null, responseBt = null, serverData = null;
		byte[] secretKeyinByte = null;

		/*Create the token that will be responsible for freshness and initialize it */
		double tokenDouble = Math.round(Math.abs(Math.random()) * 1000000);
		Integer token = (int)tokenDouble;

		int clientPort = clientPacketRSA.getPort(), clientLength = clientPacketRSA.getLength();

		byte[] finalCipherText = new byte[clientPacketRSA.getLength()];
		System.arraycopy(clientData, 0, finalCipherText, 0, clientPacketRSA.getLength());

		try{
			key = readPrivateKey(keyPath);
		} catch(Exception e){
			System.out.println("Error reading the server's private key");
		}		

		//Decrypt information with server's private key
		try{
			decryptedText = do_RSADecryption(finalCipherText, key);
		} catch(Exception e){
			System.out.println("Error decrypting with server's private key");
		}

		// Parse JSON and extract arguments
		JsonObject requestJson = JsonParser.parseString​(decryptedText).getAsJsonObject();
		String from = null, body = null;
		{
			body = requestJson.get("info").getAsString();
			preSecretMaster = Integer.parseInt(requestJson.get("preMasterSecret").getAsString());
			from = requestJson.get("from").getAsString();
		}

		preMasterSecret = preSecretMaster.toString();

		//Create secret key with preMasterSecret
		try{
			secretKeyinByte = digest(preMasterSecret.getBytes(UTF_8), "SHA3-256");
		} catch(Exception e){
			System.out.println("Error in SHA3");
		}
		SecretKey secretKey = new SecretKeySpec(secretKeyinByte, 0, secretKeyinByte.length, "AES");

		// Create response message with connection established and send new token to check freshness of future messages
		JsonObject responseJson = JsonParser.parseString​("{}").getAsJsonObject();
		{
				JsonObject infoJson = JsonParser.parseString​("{}").getAsJsonObject();
				infoJson.addProperty("token", token.toString());
				responseJson.add("info", infoJson);
				String bodyText = "Connection established";
				responseJson.addProperty("body", bodyText);
		}

		//Encrypt data with secret key
		try{
			serverData = do_Encryption(responseJson.toString(), secretKey);
		} catch(Exception e){
			System.out.println("Error encrypting with secret key");
		}

		System.out.printf("Enviei %s", responseJson.toString());

		// Send response
		DatagramPacket serverPacket = new DatagramPacket(serverData, serverData.length, clientPacketRSA.getAddress(), clientPacketRSA.getPort());
		socket.send(serverPacket);

		while (true) {
			// -------------------------------------------------- Receive requests ------------------------------------------
			// Receive requests from client

			socket.receive(clientPacketAES);
			clientAddress = clientPacketAES.getAddress();
			clientPort = clientPacketAES.getPort();

			byte[] finalCipherTextWhile = new byte[clientPacketAES.getLength()];
			System.arraycopy(clientPacketAES.getData(), 0, finalCipherTextWhile, 0, clientPacketAES.getLength());

			//Decrypt request information
			try{
				decryptedText = do_Decryption(finalCipherTextWhile, secretKey);
			} catch(Exception e){
				System.out.println("Error decrypting with secret key");
			}

			System.out.printf("Recebi %s", decryptedText);

			// Parse JSON and extract arguments
			String restaurant = null, date = null, time = null, tokenRcvd = null, numberPeople = null;
			requestJson = JsonParser.parseString​(decryptedText).getAsJsonObject();
			{
				JsonObject infoJsonWhile = requestJson.getAsJsonObject("info");
				tokenRcvd = infoJsonWhile.get("token").getAsString();
				restaurant = requestJson.get("restaurant").getAsString();
				numberPeople = requestJson.get("numberPeople").getAsString();
				date = requestJson.get("date").getAsString();
				time = requestJson.get("time").getAsString();
			}

			// -------------------------------------------------- Send responses ------------------------------------------

			//Check fressness of the message
			if((token + 1) == Integer.parseInt(tokenRcvd)){
				token = Integer.parseInt(tokenRcvd);

				//Process information received and query it to the database

				//Sendo query de acordo com pedido recebido
				//SendQuery();

				token++;

				// Create response message
				JsonObject responseJsonWhile = JsonParser.parseString​("{}").getAsJsonObject();
				{
					JsonObject infoJson = JsonParser.parseString​("{}").getAsJsonObject();
					infoJson.addProperty("token", token.toString());
					responseJsonWhile.add("info", infoJson);
					String bodyText = "Table reservation succeded";
					responseJsonWhile.addProperty("body", bodyText);
				}

				//Encrypt response message with secret key
				try{
					serverData = do_Encryption(responseJsonWhile.toString(), secretKey);
				} catch(Exception e){
					System.out.println("Error encrypting with secret key");
				}

				System.out.printf("Enviei %s", responseJsonWhile.toString());

				// Send response
				DatagramPacket serverPacketWhile = new DatagramPacket(serverData, serverData.length, clientPacketAES.getAddress(), clientPacketAES.getPort());
				socket.send(serverPacketWhile);
			}
			else{
				System.out.println("Not fresh request");
				break;
			}
		}
		try{
			con.close();
		}catch (Exception e){
			System.out.println("Can't close database");
		}
	}
}