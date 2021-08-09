/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guiapplication;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author ADMIN
 */
public class Reporter implements Serializable, IObject{
    private int id;
    private String name;
    private Address address;
    private String jobs;
    private Date dateJoin;
    private static int currentId = 10000;

    public Reporter() {
        this.id = currentId++;      
    }
    
    public Reporter(String name, String jobs) {
        this();
        this.name = name;
        this.jobs = jobs;
    }
   
       
    public Reporter(String name, Address address, String jobs) {
        this();
        this.name = name;
        this.address = address;
        this.jobs = jobs;
    }

    public Reporter(String name, Address address, String jobs, Date dateJoin) {
        this();             
        this.name = name;
        this.address = address;
        this.jobs = jobs;
        this.dateJoin = dateJoin;
    }
     
    public static void setCurrentId(int currentId){
        Reporter.currentId = currentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Date getDateJoin() {
        return dateJoin;
    }

    public void setDateJoin(Date dateJoin) {
        this.dateJoin = dateJoin;
    }
    
    public String getJobs() {
        return jobs;
    }

    public void setJobs(String jobs) {
        this.jobs = jobs;
    }

    public static int getCurrentId() {
        return currentId;
    }
    @Override
    public Object[] toObject(){
        return new Object[]{String.valueOf(id), name, address.toString(), jobs, 
        (new SimpleDateFormat("dd/MM/yyyy")).format(dateJoin)};
    }
    
    @Override
    public String toString(){
        return String.valueOf(id) + "-" + name + "-" + address.getCity() + "-" + jobs;
    }
    
}

class Address implements Serializable{
    private String city;

    public Address(String city) {
        this.city = city;
    }

    public Address() {
        
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    
    
    
    @Override
    public String toString(){
        return city;
    }
    
}