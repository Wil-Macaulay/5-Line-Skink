/**
 * Keep track of bar position for a music element (after lines are assembled)
 */
package ca.celticmusic.skink;

/**
 * @author wil macaulay
 *
 */
public class BarPosition implements Comparable{
	int barNumber = 0;
	int pulseNumber = -1;
	int sequenceNumber = 0;
	public BarPosition(){
		this(0,-1,0);
	}
	public BarPosition(int bar, int pulse, int seq){
		barNumber = bar;
		pulseNumber = pulse;
		sequenceNumber = seq;
	}
	public BarPosition(BarPosition bpCurrent) {
		barNumber = bpCurrent.barNumber;
		pulseNumber = bpCurrent.pulseNumber;
		sequenceNumber = bpCurrent.sequenceNumber;
	}
	public void incrementPulse(int absLength) {
		if (pulseNumber < 0) pulseNumber = 0;
		pulseNumber += absLength;
		sequenceNumber = 0;
		
	}
	public void incrementBar(int nBars){
		barNumber += nBars;
		pulseNumber = -1;
		sequenceNumber = 0;
	}
	public void resetPulse(){
		pulseNumber++;
	}
	public void incrementSequence(int nSeq){
		pulseNumber -= 1;
		sequenceNumber += nSeq;
	}
	public String toString(){
		return new String ("B:"+barNumber+" P:"+pulseNumber+
				" S:"+sequenceNumber);
	}
	public boolean equals(Object o){
		BarPosition bpOther = (BarPosition)o;
		return (barNumber == bpOther.barNumber ) 
		&& (pulseNumber == bpOther.pulseNumber)
		&& (sequenceNumber == bpOther.sequenceNumber);
	}
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		BarPosition rhs = (BarPosition) arg0;
		if (equals(rhs)) {
			return 0;
		}
		else if (barNumber < rhs.barNumber){
			return -1;
		}
		else if (barNumber > rhs.barNumber){
			return +1;
		}
		// bars equal
		else if (pulseNumber <rhs.pulseNumber){
			return -1;
		}
		else if (pulseNumber > rhs.pulseNumber){
			return +1;
		}
		// pulse equal
		else return (sequenceNumber - rhs.sequenceNumber);
	}
	public void normalizePulse() {
		if (pulseNumber < 0){
			pulseNumber = 0;
		}
		sequenceNumber = 0;
		
	}

}
