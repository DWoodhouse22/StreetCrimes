package com.dwoodhouse.sleuth;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class ObservingService  
{
	private static ObservingService instance = null;
	private final static String TAG = "ObservingService";
	Map<String, MyObserverable> observables;
		
	protected ObservingService() {
		observables = new HashMap<String, MyObserverable>();
	}

	public static ObservingService getInstance() 
	{
		if (instance == null)
			instance = new ObservingService();

		return instance;
	}
	
	public void addObserver(String notification, Observer observer) {
		MyObserverable observable = observables.get(notification);
        if (observable==null) {
            observable = new MyObserverable();
            observables.put(notification, observable);
        }
        observable.addObserver(observer);
    }
	
	public void removeObserver(String notification, Observer observer) {
		MyObserverable observable = observables.get(notification);
        if (observable!=null) {         
            observable.deleteObserver(observer);
        }
    }    

	public void postNotification(final String notificationID) {
		//if (notificationID.equals(Notification.kGameLaunched))
		//	Log.i(TAG, notificationID);
		
		Notification notificationObject = new Notification();
		postNotification(notificationID, notificationObject);
	}
	
	public void postNotification(String notificationID, Notification data) {		
		data.put("notificationID", notificationID);
		MyObserverable observable = observables.get(notificationID);	
        if (observable!=null) {
            observable.triggerObservers(data);
        }
    }
	
	private class MyObserverable extends Observable {
		
		public void triggerObservers(Object object) {
			setChanged();
			notifyObservers(object);
		}
	}
}

