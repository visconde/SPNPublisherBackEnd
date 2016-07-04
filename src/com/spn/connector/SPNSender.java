/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spn.connector;

import com.cryptutils.engines.aes256.AES256Engine;
import com.cryptutils.engines.aes256.AESKeyEngine;
import com.cryptutils.interfaces.CryptoEngine;
import com.cryptutils.utils.CompressorUtils;
import com.google.gson.Gson;
import com.spn.responses.AbstractMessage;
import com.spn.utils.Configs;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.bind.DatatypeConverter;
import pt.ul.fc.di.navigators.trone.data.Event;
import pt.ul.fc.di.navigators.trone.mgt.MessageBrokerClient;

/**
 *
 * @author pedrocosta
 */
 public class SPNSender {

    private MessageBrokerClient mbc;
    private int myID;
    private String configPath;
    private String channelTag;
    private long timeout;
    private int sequenceNumber;
    private Configs conf;

    private AESKeyEngine ake;
    private CryptoEngine aes256_for_enc;

    public SPNSender(Configs conf) throws Exception {

        this.myID = Integer.parseInt(conf.getProperty("spnClientID"));
        this.configPath = conf.getProperty("spnConfigPath");
        this.channelTag = conf.getProperty("channelTag");
        //this.timeout = Long.parseLong(conf.getProperty("timeoutForReplicaCounterReset"));
        this.conf = conf;
        this.sequenceNumber = 0;

    }

    public boolean connectAndRegister() {
        if (!(this.connectToSPN() && this.registerToChannel())) {
            return false;
        }
        return true;

    }

    public int sendMessage(AbstractMessage object) {
        Date d = new Date();
        Calendar c = new GregorianCalendar();
        System.out.println(c.getTime().toString() + "SPN: Sending: " + object.getPayload());

        Event e = new Event();

        int status = 0;
        boolean output = false;
        //   Event e = new Event();
        e.setClientId("" + this.myID);
        e.setPayload(new Gson().toJson(object).getBytes());
        e.setId(this.sequenceNumber);

        try {
            if (mbc.publish(e, channelTag).isOpSuccess()) {
                System.out.println("Published: " + e.getPayload().length + " bytes with sucess");
                this.sequenceNumber++;
            } else {
                System.out.println("Content was not published");
            }
        } catch (Exception ex) {
            status--;
            System.out.println("[Sender] Problems publishing");
            System.out.println(ex.toString());
        }

        return status;

    }

    private boolean connectToSPN() {
        System.out.println("Connecting to spn....id: " + this.myID + " configpath: " + configPath);
        boolean output = true;
        try {

            this.mbc = new MessageBrokerClient(this.myID, configPath);
            System.out.println("Connected with success!");
        } catch (Exception e) {
            System.out.println("[Sender] Problems connecting to SPN");
            System.out.println(e.toString());
            output = !output;
        }

        return output;
    }

    private boolean registerToChannel() {
        System.out.println("[Sender] Registering to channel: " + channelTag);
        boolean output = true;
        try {
            output = mbc.register(this.channelTag).isOpSuccess();
            System.out.println("Registered with success");
        } catch (Exception e) {
            System.out.println("[Sender] Problems subscribing.");
            System.out.println(e.toString());
            output = false;
        }
        return output;
    }

    private byte[] compress(byte[] target) throws IOException {
        return CompressorUtils.compress(target);
    }

    private byte[] encrypt(byte[] target) {
        if (!this.initCrypto()) {
            return null;
        }
        /*String password = this.conf.getProperty("password");
         byte[] salt = DatatypeConverter.parseBase64Binary(this.conf.getProperty("passwordSalt"));
         AESKeyEngine ake = new AESKeyEngine(password, salt);
         ake.genKey();
         CryptoEngine aes256_for_enc = new AES256Engine();*/
        return aes256_for_enc.encrypt(ake.getKey(), target);
    }

    private boolean initCrypto() {
        boolean result = false;
        try {
            String password = this.conf.getProperty("password");
            byte[] salt = DatatypeConverter.parseBase64Binary(conf.getProperty("passwordSalt"));
            this.ake = new AESKeyEngine(password, salt);
            this.ake.genKey();
            this.aes256_for_enc = new AES256Engine();
            result = !result;
        } catch (Exception e) {
            System.out.println("Problems initing crypto");
            System.out.println(e.toString());
        }
        return result;
    }

    public void disconnectFromSPN() {
        try {
            this.mbc.unRegister(this.conf.getProperty("channelTag"));
        } catch (Exception e) {
            System.out.println("Problems unregistering.");
            System.out.println(e.toString());
        } finally {
            try {
                this.mbc.closeConnection();
            } catch (Exception ex) {
                System.out.println("Problems closing the connection.");;
                System.out.println(ex.toString());;;
            }
        }
    }

}
