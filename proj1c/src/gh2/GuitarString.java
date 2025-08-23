package gh2;

// TODO: maybe more imports

import deque.ArrayDeque61B;
import deque.Deque61B;

//Note: This file will not compile until you complete the Deque61B implementations
public class GuitarString {
    /** Constants. Do not change. In case you're curious, the keyword final
     * means the values cannot be changed at runtime. We'll discuss this and
     * other topics in lecture on Friday. */
    private static final int SR = 44100;      // Sampling Rate
    private static final double DECAY = .996; // energy decay factor

    /* Buffer for storing sound data. */
    // TODO: uncomment the following line once you're ready to start this portion
    private ArrayDeque61B<Double> buffer=new ArrayDeque61B<>();

    /* Create a guitar string of the given frequency.  */
    public GuitarString(double frequency) {
        int capacity = (int) Math.round(SR / frequency);
        buffer.initialize(0.0,capacity);
    }


    /* Pluck the guitar string by replacing the buffer with white noise. */
    public void pluck() {
        for(int i=0;i<buffer.getUsed();i++){
            buffer.change(i,Math.random() - 0.5);
        }
    }

    /* Advance the simulation one time step by performing one iteration of
     * the Karplus-Strong algorithm.
     */
    public void tic() {
        double new_sample =DECAY * ((buffer.get(0)+buffer.get(1))/2);
        buffer.addLast(new_sample);
        buffer.removeFirst();
    }

    /* Return the double at the front of the buffer. */
    public double sample() {
        // TODO: Return the correct thing.
        return buffer.get(0);
    }
}
