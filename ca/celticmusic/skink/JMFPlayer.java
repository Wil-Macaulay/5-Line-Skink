//

/**
 * Use Java Media Framework to play a sequence of abc
 *
 * @author WM 
 * @since 2001
 * @version 2.0a2
 * 
 * History
 *  1.0d1  tempo for playback
 *  2.0a2  multivoice playback WM 29mar2006
 */
package ca.celticmusic.skink;
import java.util.*;

import javax.sound.midi.*;

import java.io.*;

 
public class JMFPlayer implements Player, MetaEventListener{
    static final int PROGRAM = 192;
    static final int NOTE_ON = 144;
    static final int NOTE_OFF = 128;
    static final int DEFAULT_CHANNEL = 1;
    int velocity = 100;  
    int[] accidentals=new int[128];
    int[] ties = new int[128];
    Sequencer sequencer;
    Track track;
    int tickCount;
    KeySig curKey;
/**
 * Check if this interface flavour is supported.
 * FIXME: currently always returns true
 */
 
 public boolean canPlay(){
    return true;
    }

  
/**
 * Play a list of tunes n times each
 */ 
  public void play(List<Tune> tunes, int nTimes)throws Exception{
     Debug.output(1,"Playing ");
     Sequence sequence;
     if (tunes == null) throw new Exception("Please select tunes to play from the tune list");
     tickCount = 0; 

     sequence = initSequence();
     for(Tune t: tunes){
         tickCount = tuneToMidi(t,nTimes,sequence, tickCount);
         }
     //MidiSystem.write(sequence,0,new File("/Users/Wil/00miditest.mid"));
     sequencer.setSequence(sequence);
     sequencer.start();  
     }

/**
 * write the appropriate output file
 */
  public void write(File midiFile,List<Tune> tunes, int nTimes) throws Exception{
	  Sequence sequence;
	  Debug.output(1,"Exporting ");
	  if (tunes == null) throw new Exception("No tunes to export");

	  sequence = initSequence();
	  tickCount = 0;
	  for(Tune t: tunes){
		  tickCount = tuneToMidi(t,nTimes,sequence, tickCount);
	  }
	  int[] types = MidiSystem.getMidiFileTypes();
	  if (types.length==0){
		  System.out.println("can't create midi file");
	  }
	  else{
		  System.out.println("creating midi file of type "+types[0]);
		  try{
			  MidiSystem.write(sequence,types[0],midiFile);
		  }
		  catch (IOException e){
			  System.out.println("Exception writing midi file " +e);
		  }   
	  }
  }



 
                  
  protected int tuneToMidi(Tune tune, int nTimes, Sequence sequence, int startTick) {
	  int bpm = tune.getBPM();
	  sequencer.setTempoInBPM(bpm); // set default tempo

	  List l = tune.unrollAbc(); // list of unrolled voices
	  return writeTracks(l, sequence, startTick);

  }


/**
 * @return
 */
  private Sequence initSequence() throws Exception {
	  Sequence sequence = null;
	  open();
	  sequence = new Sequence(Sequence.PPQ, SkinkConstants.TICKS_PER_QUARTER);
	  //sequencer.setSequence(sequence);
	  return sequence;
  }


/**
 * @param l
 * @param sequence
 * @param startTick TODO
 * @return TODO
 */
private int writeTracks(List l, Sequence sequence, int startTick) {
	
	int endTick = 0;
	for (ListIterator i = l.listIterator(); i.hasNext();) {
		tickCount = startTick;	
		createTrack((List)i.next(),sequence);
		endTick = Math.max(endTick, tickCount);
	}
	return endTick;
}



/**
 * @param l unrolled abc
 * @param sequence MIDI sequence
 */
private void createTrack(List l, Sequence sequence) {
	track = sequence.createTrack();
    createEvent(PROGRAM, DEFAULT_CHANNEL, 1, 0);
    playAbc(l);
}  
/**
 * set key signature
 */
 
  public void setKeySig(KeySig theKey){
    curKey = theKey;
    }

/**
 * play an unwound list of abc elements (stress??)
 * @param abc List of music elements to play
 */
 
  public void playAbc(List abc){
      clearTies();
      setKeySig(KeySig.findKey("C"));
      for (ListIterator i = abc.listIterator(); i.hasNext();){
        AbcElement m = (AbcElement) i.next();
        m.updatePlayerContext(this);
        tickCount +=m.play(this,tickCount,1.0,curKey);  
       }  
    }
  /**
   * clear all ties in progress
   *
   */
   public void clearTies(){
     for (int i=0;i<128;i++){
        ties[i] = -1;  
        }
     }   
/**
 * play a note at a given time
 * @param midiNote midi note number
 * @param atTick tick count for start
 * @param duration duration of note
 */
 public void makeNote(int midiNote,int atTick,int duration){
  
    int onTick = atTick;
    int offTick = atTick+duration;
    
    if (ties[midiNote] >= 0){
       onTick = ties[midiNote];
       ties[midiNote]=-1;
       }
    createEvent(NOTE_ON,DEFAULT_CHANNEL,midiNote,onTick);
    createEvent(NOTE_OFF,DEFAULT_CHANNEL,midiNote,offTick);
    }

public void makeTiedNote(int midiNote, int atTick, int duration) {
  if (ties[midiNote] == -1){
    ties[midiNote] = atTick;
    }
  }            
   
  
/**
 * stop playing
 */  
  public void stopPlay(){
     if (sequencer != null) {
       sequencer.stop();
       sequencer.close();
        }
     sequencer = null;
     }
 
/**
 * pause
 */
  public void pausePlay(){
    sequencer.stop();
    };
     
/**
 * resume
 */
  public void resumePlay(){
    sequencer.start();
    };

/**
 * initialize synth
 */
    public void open() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
        } catch (Exception e) { e.printStackTrace(); }
        
        sequencer.addMetaEventListener(this);
    }

/**
 * got meta event - should only be the end track event.
 */
 
   public void meta(MetaMessage message) {
     Debug.output(3,"Meta message received " + message.getType());
     }
  
/**
 * create a MIDI event (from javaSound demo)
 */
 
  private void createEvent(int type, int chan, int num, long tick) {
        ShortMessage message = new ShortMessage();
        try {
            message.setMessage(type, chan, num, velocity); 
            MidiEvent event = new MidiEvent( message, tick );
            track.add(event);
        } catch (Exception ex) { ex.printStackTrace(); }
    }
  public void clearAccidentals(){
    for (int i=0;i<128;i++){
      accidentals[i] = 0;
      }
    }
      
  public void setAccidental(int midiNoteNum,int pitchMod) {
    accidentals[midiNoteNum] = pitchMod;
    }
    
  public int accidentalOn(int midiNoteNum){
    return accidentals[midiNoteNum];    
    }
      
}
