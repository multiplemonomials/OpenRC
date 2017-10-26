package org.ftcopen.vuforiapreload;

import com.vuforia.DataSet;
import com.vuforia.ObjectTracker;
import com.vuforia.TrackerManager;
import com.vuforia.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class DataSetProvider implements DataSetLoader.Callback {
  private final String[] supportedDataSets = {"RelicVuMark", "StonesAndChips"};

  private final Map<String, FutureTask<DataSet>> loadTasks;

  private final TrackerManager manager = TrackerManager.getInstance();
  private Type objectTrackerType = ObjectTracker.getClassType();
  private final ObjectTracker tracker = getObjectTracker();

  public DataSetProvider() {
    loadTasks = new HashMap<>(supportedDataSets.length);
    for(String dataSetName: supportedDataSets) {
      FutureTask<DataSet> task = new FutureTask<>(new DataSetLoader(dataSetName, tracker, this));
      loadTasks.put(dataSetName, task);
    }
  }

  public void preload() {
    for(FutureTask<DataSet> task : loadTasks.values()) {
      task.run();
    }
  }

  //TODO: make sure this class can't be used before init is done

  /**
   * @param name The name of the DataSet to load
   * @return The requested DataSet, or null if it's not available
   */
  public DataSet getDataSet(String name) throws ExecutionException, InterruptedException { //TODO: consider catching these
    return loadTasks.get(name).get();
  }

  private ObjectTracker getObjectTracker()
  {
    //TODO: Benchmark this stuff. Init needs to happen before the camera is touched.
    //TODO: Investigate if there is any performance penalty to not de-initing the tracker
    ObjectTracker tracker = (ObjectTracker) manager.initTracker(objectTrackerType);
    if(tracker == null) {
      // This function will return NULL if the tracker of the given type has already been initialized, if the tracker is not supported on the current device, or if the CameraDevice is currently initialized.
    }
    return null;
  }

  @Override
  public void onDataSetRetrieved() {
    boolean allFinished = true;
    for(FutureTask loadTask: loadTasks.values()) {
      allFinished = loadTask.isDone() && allFinished;
    }
    if(allFinished) {
      manager.deinitTracker(objectTrackerType);
    }
  }

  @Override
  public void onDataSetFailed(Throwable throwable) {
    //TODO: handle
  }
}
