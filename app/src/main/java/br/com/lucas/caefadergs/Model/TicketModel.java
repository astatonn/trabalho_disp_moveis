package br.com.lucas.caefadergs.Model;


import java.text.SimpleDateFormat;
import java.util.Date;

public class TicketModel {

    public String status;
    public String attendent;
    public String createdAt;
    public String type;
    private String id;

    public TicketModel() {

    }

    public TicketModel(String status, String attendent, String createdAt, String type) {
        this.status = status;
        this.attendent = attendent;
        this.createdAt = createdAt;
        this.type = type;
    }

    public TicketModel(String status, String attendent, String id) {
        this.status = status;
        this.attendent = attendent;
        this.id = id;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getAttendent() { return attendent; }

    public void setAttendent(String attendent) { this.attendent = attendent; }

    public String getCreatedAt() { return createdAt; }

    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }
}


