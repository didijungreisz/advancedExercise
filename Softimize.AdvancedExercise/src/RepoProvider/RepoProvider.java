package RepoProvider;

import java.util.List;

import Entities.DataObject;

public interface RepoProvider {

	public void add(DataObject entity);
	public void update(DataObject entity);
	public void remove(int id);
	public DataObject getSingelEntity(int id);
	public List<DataObject> getAll();
	public boolean contains(int id);
}
