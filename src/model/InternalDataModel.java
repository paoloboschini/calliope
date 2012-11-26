package model;

import java.util.NavigableSet;

public interface InternalDataModel extends DataModel {
	public void setData(NavigableSet<DataEvent> model);
	public void contentChanged();
}
