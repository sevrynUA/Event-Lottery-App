package com.example.celery_sticks;

import java.util.ArrayList;
import java.util.Date;

public class Event {
    private String event_name;
    private String event_id;
    private ArrayList<Entrant> waitlist;
    // implement after lottery
    private ArrayList<Entrant> entrants;
    private Date event_date;
    private QRCode details_qrcode;
    private QRCode signup_qrcode;

    public Event(String event_name, String event_id) {
        this.event_name = event_name;
        this.event_id = event_id;
        this.waitlist = new ArrayList<Entrant>();
    }

    public Date getEventDate() {
        return event_date;
    }

    public void setEventDate(Date date) {
        this.event_date = date;
    }

    public String getEventName() {
        return event_name;
    }

    public void setEventName(String string) {
        this.event_name = string;
    }

    public String getEventId() {
        return event_id;
    }

    public void setEventId(String string) {
        this.event_id = string;
    }

    public QRCode getDetailsQRCode() {
        return details_qrcode;
    }

    public void setDetailsQRCode(QRCode qrcode) {
        this.details_qrcode = qrcode;
    }

    public QRCode getSignupQRCode() {
        return signup_qrcode;
    }

    public void setSignupQRCode(QRCode qrcode) {
        this.signup_qrcode = qrcode;
    }

    public ArrayList<Entrant> getWaitlist() {
        return waitlist;
    }

    public void addToWaitlist(Entrant entrant) {
        if (! waitlist.contains(entrant)) {
            waitlist.add(entrant);
        }
    }

    public void removeFromWaitlist(Entrant entrant) {
        waitlist.remove(entrant);
    }

}
