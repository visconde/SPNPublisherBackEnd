package com.spn.demo;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.DatatypeConverter;

import com.cryptutils.engines.aes256.AES256Engine;
import com.cryptutils.engines.aes256.AESKeyEngine;
import com.cryptutils.interfaces.CryptoEngine;
import com.cryptutils.utils.CompressorUtils;
import com.spn.utils.Configs;
import javax.servlet.http.HttpServletRequest;


import pt.ul.fc.di.navigators.trone.data.Event;
import pt.ul.fc.di.navigators.trone.mgt.MessageBrokerClient;

@WebServlet("/uploadfile")
@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB
maxFileSize=1024*1024*10,      // 10MB
maxRequestSize=1024*1024*10)   // 10MB
public class Upload extends HttpServlet {

	private Configs conf;
	private MessageBrokerClient mbc;
	private boolean canSend;
	private int sequenceNumber;
	private AESKeyEngine ake;
	private CryptoEngine aes256_for_enc;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		try {
			conf = new Configs();
		} catch (IOException e) {
			System.out.println("Problems reading configs");
			System.out.println(e.toString());
		}

		this.canSend = (this.connectToSPN() && this.registerToSPN() && this.initCrypto());

		if(this.canSend){
			System.out.println("We are ready to upload to SPN");
			this.sequenceNumber = 0;
		}else{
			System.out.println("Ups something went wrong. Going to disconnect form SPN.");
			this.disconnectFromSPN();
		}
		
		
	}
	
	private boolean initCrypto(){
		boolean result = false;
		try{
			String password = this.conf.getProperty("password");
			byte[] salt = DatatypeConverter.parseBase64Binary(this.conf.getProperty("passwordSalt"));
			this.ake = new AESKeyEngine(password, salt);
			this.ake.genKey();
			this.aes256_for_enc = new AES256Engine();
			result = !result;
		}catch(Exception e){
			System.out.println("Problems initing crypto");
			System.out.println(e.toString());
		}
		return result;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		if(this.canSend){
			this.sendToSPN(request);
		}else{
			System.out.println("Cant upload");
		}
	}

	private void disconnectFromSPN(){
		try{
			this.mbc.unRegister(this.conf.getProperty("channelTag"));
		}catch(Exception e){
			System.out.println("Problems unregistering.");
			System.out.println(e.toString());
		}finally{
			try{
				this.mbc.closeConnection();
			}catch(Exception ex){
				System.out.println("Problems closing the connection.");
				System.out.println(ex.toString());
			}
		}
	}

	private boolean connectToSPN(){
		boolean output = true;
		int myId = Integer.parseInt(this.conf.getProperty("spnClientID"));
		String configPath = this.conf.getProperty("spnConfigPath");

		try{
			this.mbc = new MessageBrokerClient(myId, configPath);
		}catch(Exception e){
			System.out.println("Problems connecting to SPN");
			System.out.println(e.toString());
			output = false;
		}
		return output;

	}

	private boolean registerToSPN() {
		boolean output = false;
		try{
			output = this.mbc.register(this.conf.getProperty("channelTag")).isOpSuccess();
		}catch (Exception e){
			System.out.println("Problems registering");
			System.out.println(e.toString());
			output = false;
		}
		return output;
	}

	private void sendToSPN(HttpServletRequest request){
		try {
                   
			// ---> buffer is written in the format: [ [NAME SIZE 4byte] [FILE NAME Nbytes] [FILE Nbytes] ]
		
                       final Part p = request.getPart("file");
			InputStream is = p.getInputStream();
			byte[] filename = "teste".getBytes();
			Event e = new Event();
			e.setClientId(this.conf.getProperty("spnClientID"));
			e.setPayload(this.encrypt(filename));
			e.setId(this.sequenceNumber);
			if(mbc.publish(e, this.conf.getProperty("channelTag")).isOpSuccess()){
				System.out.println("Published: "+e.getPayload().length+" bytes with sucess");
				this.sequenceNumber++;
			}else{
				System.out.println("Content was not published");
			}
		} catch (Exception ex) {
			System.out.println("Issues publishing: \n"+ex.toString());
		}
		

	}
	
	private byte[] compress(byte[] target) throws IOException{
		return CompressorUtils.compress(target);
	}
	
	private byte[] encrypt(byte[] target){
		/*String password = this.conf.getProperty("password");
		byte[] salt = DatatypeConverter.parseBase64Binary(this.conf.getProperty("passwordSalt"));
		AESKeyEngine ake = new AESKeyEngine(password, salt);
		ake.genKey();
		CryptoEngine aes256_for_enc = new AES256Engine();*/
		return aes256_for_enc.encrypt(ake.getKey(), target);
	}

	private byte[] getBytes(InputStream is) throws IOException{
		byte [] buffer = new byte[is.available()];
		int bytesRead;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		while ((bytesRead = is.read(buffer)) != -1){
			output.write(buffer, 0, bytesRead);
		}
		return output.toByteArray();
	}
/**
	private void saveFile(HttpServletRequest request) throws IOException, ServletException{


		String savePath = this.conf.getProperty("savePath");

		// creates the save directory if it does not exists
		File fileSaveDir = new File(savePath);
		if (!fileSaveDir.exists()) {
			fileSaveDir.mkdir();
		}
		for (Part part : request.getParts()) {
			String fileName = extractFileName(part);
			part.write(savePath + File.separator + fileName);
		}
	}
**/
	private String extractFileName(Part part) {
		String contentDisp = part.getHeader("content-disposition");
		String[] items = contentDisp.split(";");
		for (String s : items) {
			if (s.trim().startsWith("filename")) {
				return s.substring(s.indexOf("=") + 2, s.length()-1);
			}
		}
		return "";
	}

}
