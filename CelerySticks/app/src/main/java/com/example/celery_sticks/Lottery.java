package com.example.celery_sticks;

import java.util.ArrayList;
import java.util.Collections;

public class Lottery {
    private ArrayList<Entrant> waitlist;
    private int sample;

    public Lottery(ArrayList<Entrant> waitlist, int sample) {
        this.waitlist = waitlist;
        this.sample = sample;
    }

    public ArrayList<Entrant> runLottery() {
        ArrayList<Entrant> waitlist_copy = new ArrayList<>(waitlist);
        Collections.shuffle(waitlist_copy);

        return new ArrayList<>(waitlist_copy.subList(0, sample));
    }

}
