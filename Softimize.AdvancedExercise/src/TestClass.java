
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import Entities.Entity;
import Entities.Person;
import Entities.Stock;
import EntitiesTranslator.EntitiesTranslator;
import EntitiesTranslator.StockEntityTranslator;
import Enums.LoadingMode;
import Exceptions.InvalidIdException;
import Exceptions.WrongEntityClass;
import RepoProvider.CSVfileRepoProvider;
import RepoProvider.RepoProvider;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class TestClass {
	    private File file;
	    private RepoProvider repoProvider;
	    private EntitiesTranslator stockTranslator;
	    private Entity stock;
	    private Cache cache;
			
	    public TestClass(){
			file = new File("test.csv");
			try {
				repoProvider = new CSVfileRepoProvider("test.csv");
			} catch (FileNotFoundException e) {
				System.out.println(e);
			}
			stockTranslator = new StockEntityTranslator();
			stock = new Stock(111111111, "Softimize", 1009.98, "01/06/2019");
			cache = new Cache(repoProvider, Stock.class, stockTranslator, LoadingMode.EAGER);
		}
	    
	    
	    /**
	     * operation tested - add
	     */
	    @Test
	    public void a() {
	    	file.delete();
	        cache.clear();
	    	try {
				cache.add(stock);
			} catch (InvalidIdException | WrongEntityClass e) {
				System.out.println(e);
			}
    	
	    }
	    
	    /**
	     * operation tested - getEntity
	     */
	    @Test
	    public void b() {
	    	try {
				assertEquals(cache.getEntity(stock.getId()).toString(), stock.toString());
			} catch (InvalidIdException e) {
				e.printStackTrace();
			}	
	    }

	    /**
	     * operation tested - fail to add
	     * the test try to add already exists entity
	     */
	    @Test
	    public void c(){
	    	Throwable exception = Assertions.assertThrows(InvalidIdException.class, () -> cache.add(stock));
	        assertEquals("Add operation failed! There exists entity with the given ID", exception.getMessage());
			}
	    
	    /**
	     * operation tested - invalid ID error
	     * the test try to get entity by inserting id with wrong format
	     */
	    @Test
	    public void d(){
	    	Throwable exception = Assertions.assertThrows(InvalidIdException.class, () -> cache.getEntity(123456));
	        assertEquals("Invalid ID!  Please enter a 9 digit ID", exception.getMessage());
			}
	    
	    /**
	     * operation tested - remove
	     */
	    @Test
	    public void e() {
			stock = new Stock(222222222, "Google", 1005.43, "01/06/2019");
			try {
				cache.add(stock);
			} catch (InvalidIdException | WrongEntityClass e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
	    	try {	    		
				cache.remove(222222222);
			} catch (InvalidIdException e1) {
				e1.printStackTrace();
			}
	    	Throwable exception = Assertions.assertThrows(InvalidIdException.class, () -> cache.getEntity(222222222));
	    	assertEquals("There is no entity defined for this ID.", exception.getMessage());
	    }
	    
	    /**
	     * operation tested - fail to remove
	     * the test try to remove entity that doesn't exists
	     */
	    @Test
	    public void f() {
	    	Throwable exception = Assertions.assertThrows(InvalidIdException.class, () -> cache.remove(222222222));
	    	assertEquals("Remove operation failed! There is no entity defined for this ID.", exception.getMessage());	    	    	
	    }
	    
	    /**
	     * operation tested - update
	     */ 
	    @Test
	    public void g() {
			stock = new Stock(111111111, "Bio_T", 1020.16, "03/06/2019");
			String updated = null;
	    	try {
				cache.update(stock);
				updated = cache.getEntity(111111111).toString();
			} catch (InvalidIdException | WrongEntityClass e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	assertEquals(updated, stock.toString());
	    }
	    
	    /**
	     * operation tested - fail to update
	     * the test try to update entity that doesn't exists 
	     */
	    @Test
	    public void h() {
			stock = new Stock(999999999, "Jungreisz_CO", 1009.98, "01/06/2019");
			Throwable exception = Assertions.assertThrows(InvalidIdException.class, () -> cache.update(stock));
	    	assertEquals("Update operation failed! There is no entity defined for this ID.", exception.getMessage());	    	    	
	    }
	    
	    
	    /**
	     * operation tested - wrong entity error
	     * the test try to add entity from another type to the cache 
	     */
	    @Test
	    public void i() {
			Entity wrongEntity = new Person(333333333);
			Throwable exception = Assertions.assertThrows(WrongEntityClass.class, () -> cache.add(wrongEntity));
	    	assertEquals("The entity you entered is not supported by the Cache. The entity should be instance of : Stock", exception.getMessage());	
	    }
	    
	    /**
	     * operation tested - null entity error
	     * the test try to add null entity 
	     */
	    @Test
	    public void j() {		
	    	Throwable exception = Assertions.assertThrows(NullPointerException.class, () -> cache.add(null));
	    	assertEquals("You entered a NULL entity. You must assign value to the entity", exception.getMessage());			
	    }
	    
	    /**
	     * operation tested - eager loading
	     * the test try to get entity, after eager loading
	     * if the eager loading failed, the cache would not find the entity  
	     */	    
	    @Test
	    public void k() {
			Cache eagerLoadingCacheCheck = new Cache(repoProvider, Stock.class, stockTranslator, LoadingMode.EAGER);
			try {
				assertNotEquals(InvalidIdException.class, eagerLoadingCacheCheck.getEntity(stock.getId()).toString());
			} catch (InvalidIdException e) {
				e.printStackTrace();
			}	
	    }
	    
	    
	    /**
	     * operation tested - add Subscriber
	     * the test try to add subscriber to the subscribers list
	     * then, the test add entity  and the subscriber should print message after it notified
	     */
	    @Test
	    public void l() {
	    	final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	        System.setOut(new PrintStream(outContent));
	    	cache.addSubscriber((id, Operation) -> System.out.print(id + Operation.getAction()));
			stock = new Stock(123456789, "Facebook", 100.78, "01/06/2019");
	    	try {
				cache.add(stock);
			} catch (InvalidIdException | WrongEntityClass e) {
				e.printStackTrace();
			}
	    	assertEquals("123456789 added to", outContent.toString());
	    	try {
				outContent.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	   
	    
	    
	    
	    
	    
	    	
	    }        
	  	    
	                    
        
        
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	        
	


