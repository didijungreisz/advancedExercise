package EntitiesTranslator;

import Entities.DataObject;
import Entities.Entity;
import Exceptions.WrongEntityClass;

public interface EntitiesTranslator {

	public DataObject entityToDataObject(Entity e) throws WrongEntityClass;
	public Entity dataObjectToEntity(DataObject o); 
}
