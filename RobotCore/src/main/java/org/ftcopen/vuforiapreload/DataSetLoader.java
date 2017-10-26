package org.ftcopen.vuforiapreload;

import com.vuforia.DataSet;
import com.vuforia.ObjectTracker;

import java.util.concurrent.Callable;

class DataSetLoader implements Callable<DataSet> {
  private final String name;
  private final ObjectTracker tracker;
  private final Callback callback;
  public DataSetLoader(String name, ObjectTracker tracker, Callback callback) {
    this.name = name;
    this.tracker = tracker;
    this.callback = callback;
  }
  @Override
  public DataSet call() {
    DataSet dataSet = tracker.createDataSet();
    //TODO: Load the data set and throw an exception if it's not there.
    return null;
  }

  interface Callback {
    void onDataSetRetrieved();
    void onDataSetFailed(Throwable throwable);
  }
}