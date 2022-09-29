package nz.ac.vuw.ecs.swen225.gp6.recorder;

import nz.ac.vuw.ecs.swen225.gp6.app.App;
import nz.ac.vuw.ecs.swen225.gp6.recorder.datastructures.Pair;
import nz.ac.vuw.ecs.swen225.gp6.recorder.datastructures.RecordTimeline;
import nz.ac.vuw.ecs.swen225.gp6.recorder.datastructures.ReplayTimeline;
import nz.ac.vuw.ecs.swen225.gp6.app.utilities.Actions.Action;

public class Replay implements Runnable {
    private ReplayTimeline<Action> timeline;
    private Pair<Long, Action> queuedAction;
    private App app;
    private long time;
    private boolean step = false;
    private boolean isRunning = false;

    /**
     * Constructor takes in an app to observe
     * @param app
     */
    public Replay(App app){
        if(app == null) {
            throw new IllegalArgumentException("App cannot be null");
        }
        this.app = app;
    }

    @Override
    public void run() {
        time = app.getGameClock().getTime();
        if (!isRunning) {return;}
        autoPlayActions();
    }

    /**
     * Load method will load the game from the given file name
     * @param game the name of the game to load
     * @return this replay object to chain methods
     */
    public Replay load(String game){
        if(game == null) {
            System.out.println("Game cannot be null");
        }
        // this.timeline = Persistency.load(game);
        this.timeline = new ReplayTimeline<Action>(new RecordTimeline<Action>());
        return this;
    }

    /**
     * Method queues the next action in the timeline if available.
     * If there is no next action, the method will popup a message.
     * @return this replay object to chain methods
     */
    public Replay step(){
        // System.out.println("Stepping");
        checkGame();
        this.queuedAction = timeline.next();
        isRunning = true;
        return this;
    }

    /**
     * Method enables the autoPlay functionality.
     * @return this replay object to chain methods
     */
    public Replay autoPlay(){
        // System.out.println("Auto Replay started");
        checkGame();
        isRunning = true;
        return this;
    }

    /**
     * Method to pause the autoplay feature.
     * @return this replay object to chain methods
     */
    public Replay pauseReplay(){
        this.app.getActions().actionPause();
        isRunning = false;
        return this;
    }

    /**
     * Method sets the speed of the autoplay.
     * @param speed
     * @return this replay object to chain methods
     */
    public Replay setSpeed(int speed) {
        int delay = 34 / speed;    // default delay is 34ms
        this.app.getGameClock().setReplayDelay(delay);  
        return this;
    }

    /**
     * Method exits the replay mode.
     * @return this replay object to chain methods
     */
    public Replay stopReplay(){
        System.out.println("Replay stopped");
        return this;
    }
    
    
    //================================================================================================================//
    //=========================================== Helper Methods =====================================================//
    //================================================================================================================//

    /**
     * Method checks if a timeline has been initialized.
     * If not, it will display a popup message.
     */
    private void checkGame(){
        if (timeline == null){
            System.out.println("No game loaded");
            return;
        }
        if (!timeline.hasNext()){
            System.out.println("Please reload a game.");
            return;
        }
    }
    
    /**
     * Method checks if the next action in the timeline is ready to be executed.
     * If it is, it calls the executeAction method.
     */
    private void autoPlayActions() { 
        if(queuedAction != null && queuedAction.getKey() <= this.time){
            executeAction(queuedAction.getValue());
        }
        if(step) {
            isRunning = false;
            step = false;
        }
        if (!timeline.hasNext()){
            System.out.println("Replay finished"); 
            return; 
        }
        queuedAction = timeline.next();
    }

    /**
     * Method executes action.
     * Throws IllegalArgumentException if the action is null or not recognized.
     * @param action Action to be executed
     */
    private void executeAction(Action action) throws IllegalArgumentException {
        if(action == null) {throw new IllegalArgumentException("Null action encountered");}
        if(action.equals(Action.MOVE_UP)){
            this.app.getActions().actionUp();
        }
        if(action.equals(Action.MOVE_DOWN)){
            this.app.getActions().actionDown();
        }
        if(action.equals(Action.MOVE_LEFT)){
            this.app.getActions().actionLeft();
        }
        if(action.equals(Action.MOVE_RIGHT)){
            this.app.getActions().actionRight();
        }
        throw new IllegalArgumentException("No such action: " + action);
    }
}