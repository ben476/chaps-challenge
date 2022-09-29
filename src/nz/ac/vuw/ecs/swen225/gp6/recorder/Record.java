package nz.ac.vuw.ecs.swen225.gp6.recorder;

import nz.ac.vuw.ecs.swen225.gp6.recorder.datastructures.RecordTimeline;
import nz.ac.vuw.ecs.swen225.gp6.app.utilities.Actions.Action;

public class Record {
    private RecordTimeline<Action> timeline;

    /**
     * Create a new Record object.
     */
    public Record() {
        this.timeline = new RecordTimeline<>();
    }

    /**
     * Starts a new recording.
     */
    public void startRecording(){
        this.timeline = new RecordTimeline<Action>();
    }

    /**
     * Adds a time and action to the timeline
     * @param time the time the action is executed
     * @param actions the action executed
     */
    public void addActions(long time, Action actions) {
        this.timeline.add(time, actions); 
        System.out.println("Added action: " + actions.toString());        
    }

    /**
     * Stops the recording and saves it to a file
     */
    public void stopRecording(){
        if(timeline == null){
            System.out.println("Recording was not started");
            return;
        }
        if(!timeline.hasNext()){
            System.out.println("No events to save");
            return;
        }
        // Persistency.save(timeline);
    }
}
