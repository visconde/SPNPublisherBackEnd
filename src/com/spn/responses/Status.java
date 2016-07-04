/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spn.responses;

/**
 *
 * @author pedrocosta
 */
public class Status {
    private Integer spnStatus;
    private Integer systemStatus;

    public Integer getSystemStatus() {
        return systemStatus;
    }

    public void setSystemStatus(Integer systemStatus) {
        this.systemStatus = systemStatus;
    }
    private Integer responseTime;
    
    public void setSpnStatus(Integer spnStatus){
        this.spnStatus=spnStatus;
    }
    public Integer getSpnStatus(){
        return this.spnStatus;
    }
     public void setResponseTime(Integer responseTime){
        this.responseTime=responseTime;
    }
     public Integer getResponseTime(){
        return this.responseTime;
    }
   
}
