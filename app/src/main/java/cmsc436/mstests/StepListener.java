package cmsc436.mstests;

// Will listen to step alerts
public interface StepListener {

    public void step(long timeNs);

}