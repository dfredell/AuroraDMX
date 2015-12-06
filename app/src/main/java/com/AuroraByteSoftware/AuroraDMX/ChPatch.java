package com.AuroraByteSoftware.AuroraDMX;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents one channel, that is associated with X dimmers
 * Created by furtchet on 12/3/15.
 */
public class ChPatch implements Serializable {

    private List<Integer> dimmers = new ArrayList<>(MainActivity.ALLOWED_PATCHED_DIMMERS);

    public ChPatch() {
    }

    public ChPatch(int dimmer) {
        dimmers.add(dimmer);
    }

    public List<Integer> getDimmers() {
        return dimmers;
    }

    public boolean contains(Integer dimmer) {
        return dimmers.contains(dimmer);
    }

    public void setDimmers(List<Integer> dimmers) {
        this.dimmers = dimmers;
    }

    public void addDimmer(Integer dimmer) {
        if (!dimmers.contains(dimmer))
            dimmers.add(dimmer);
    }

}
