import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import Entities.DataObject;
import Entities.Entity;
import EntitiesTranslator.EntitiesTranslator;
import Enums.LoadingMode;
import Enums.Operation;
import Exceptions.InvalidIdException;
import Exceptions.WrongEntityClass;
import RepoProvider.RepoProvider;
import Subscribers.Subscriber;

public class Cache {
	private HashMap<Integer, Entity> entitiesTable = new HashMap<Integer, Entity>();
	private List<Subscriber> subscribers = new ArrayList<Subscriber>();
	private RepoProvider repoProvider;
	private Class entityCache;
	private EntitiesTranslator translator;
	private LoadingMode loadingMode;

	public Cache(RepoProvider repoProvider, Class entityCache, EntitiesTranslator translator, LoadingMode loadingMode) {
		this.repoProvider = repoProvider;
		this.entityCache = entityCache;
		this.translator = translator;
		this.loadingMode = loadingMode;
		// loading the data to the cache if its eager mode
		if (loadingMode == LoadingMode.EAGER)
			loadingDataToCache();
	}

	/**
	 * Method to load all the data to the cache The method called only in eager mode
	 * @throws IOException 
	 */
	private void loadingDataToCache() {
		for (DataObject dataObject : repoProvider.getAll()) {
			Entity converted = translator.dataObjectToEntity(dataObject);
			entitiesTable.put(converted.getId(), converted);
		}
	}

	/**
	 * Method to add subscribers to the cache subscribers list
	 * @param subscriber - the subscriber to add
	 */
	public void addSubscriber(Subscriber subscriber) {
		subscribers.add(subscriber);
	}

	/**
	 * Method to remove subscribers from the cache subscribers list
	 * @param subscriber - the subscriber to remove
	 */
	public void removeSubscriber(Subscriber subscriber) {
		subscribers.remove(subscriber);
	}
	
	/**
	 * Method to get the entity with the given ID 
	 * if the entity is not in the cache - it will save it to the cache
	 * @param id 
	 * @return the entity with the given ID
	 * @throws InvalidIdException
	 * @throws IOException 
	 */
	public synchronized Entity getEntity(int id) throws InvalidIdException {
		idValiditation(id);
		if(!(entityexists(id))) throw new InvalidIdException("There is no entity defined for this ID.");
		// if its eagerMode - it should be in the cache
		Entity entity = entitiesTable.get(id);	
		if(entity == null) {
		 entity = translator.dataObjectToEntity(repoProvider.getSingelEntity(id));
		 entitiesTable.put(id, entity);
		}
		return entity;
	}

	/**
	 * Method to add new Entity 
	 * the method adds the entity to the cache and to the repository
	 * The method notify the subscribers that the entity added  
	 * @param entity
	 * @throws InvalidIdException 
	 * @throws WrongEntityClass 
	 * @throws IOException 
	 */
	public synchronized void add(Entity entity) throws InvalidIdException, WrongEntityClass{
		entityClassCheck(entity); 
		idValiditation(entity.getId());
		// check if the entity is already exists
		if (entityexists(entity.getId())) 
			throw new InvalidIdException("Add operation failed! There exists entity with the given ID");	
		
		repoProvider.add(translator.entityToDataObject(entity));
		entitiesTable.put(entity.getId(), entity);
		notifySubscribers(entity, Operation.ADD);
	}

	/**
	 * Method to remove entity from the cache and from the repository
	 * The method notify the subscribers that the entity removed
	 * @param id
	 * @throws InvalidIdException
	 * @throws IOException 
	 */
	public synchronized void remove(int id) throws InvalidIdException{
		idValiditation(id);
		if (!entityexists(id))
			throw new InvalidIdException("Remove operation failed! There is no entity defined for this ID.");
		Entity e= getEntity(id); // only to deliver it to the subscribers
		repoProvider.remove(id);
		entitiesTable.remove(id);
		notifySubscribers(e, Operation.REMOVE);

	}
	
	/**
	 * Method to update entity 
	 * The method notify the subscribers that the entity updated
	 * @param id
	 * @throws InvalidIdException
	 * @throws WrongEntityClass 
	 * @throws IOException 
	 */
	public synchronized void update(Entity entity) throws InvalidIdException, WrongEntityClass{
		entityClassCheck(entity);
		idValiditation(entity.getId());
		if (!entityexists(entity.getId())) 
			throw new InvalidIdException("Update operation failed! There is no entity defined for this ID.");
		
		repoProvider.update(translator.entityToDataObject(entity));
		entitiesTable.put(entity.getId(), entity);
		notifySubscribers(entity, Operation.UPDATE);

	}

	/**
	 * Method to notify all the subscribers about ADD/REMOVE/UPDATE operations
	 * @param entity - the method get the full entity for a future operations (id needed)
	 * @param operation - could be ADD, REMOVE, UPDATE
	 */
	private void notifySubscribers(Entity entity, Operation operation) {
		subscribers.forEach(subscriber -> subscriber.Update(entity.getId(), operation));
	}

	/**
	 * Method to check if the entity that the user send for ADD or UPDATE, is from
	 * the same class as the Cache entity
	 * @param entity - get from the user to ADD/UPDATE
	 * @throws WrongEntityClass 
	 */
	private void entityClassCheck(Entity entity) throws WrongEntityClass {
		if(entity == null) throw new NullPointerException("You entered a NULL entity. You must assign value to the entity");
		if (entity.getClass() != entityCache) 
		throw new WrongEntityClass("The entity you entered is not supported by the Cache. The entity should be instance of : " + entityCache.getSimpleName());	
	}

	/**
	 * Method to check if the entity  is exists
	 * first, it check if it is eager loading, if so - check if the entity
	 * exists in the cache 
	 * second, if its lazy loading, check in the repoProvider
	 * @param id - the entity id to check
	 * @return true if exists
	 */
	private boolean entityexists(int id) {
		return (loadingMode == LoadingMode.EAGER) ? entitiesTable.containsKey(id) : 
			(entitiesTable.containsKey(id) || repoProvider.contains(id));
	}
	
	/**
	 * Method to check if the id is valid
	 * the id should be in a pattern of 9 digits 
	 * @param id
	 * @throws InvalidIdException
	 */
	private void idValiditation(int id) throws InvalidIdException {
		String idString = String.valueOf(id);
		if (!(Pattern.matches("\\d{9}", idString))) {
			throw new InvalidIdException("Invalid ID!  Please enter a 9 digit ID");
		}
	}
/**
 * Method only for test purpose to clean the data structures in the cache 
 */
	public void clear() {
		entitiesTable.clear();
		subscribers.clear();
	}
}
