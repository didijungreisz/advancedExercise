package EntitiesTranslator;
import Entities.DataObject;
import Entities.Entity;
import Entities.Stock;
import Exceptions.WrongEntityClass;

public class StockEntityTranslator implements EntitiesTranslator {

	/**
	 * Method to convert the given entity to DataObject 
	 * The given entity must be instance of Stock
	 */
	@Override
	public DataObject entityToDataObject(Entity stock) throws WrongEntityClass  {
		if (!(stock instanceof Stock)) 
			throw new WrongEntityClass("This translator can handle only Entities of type Stock");	
		Stock stockToConvert = (Stock) stock;
		DataObject convertedStock = new DataObject();
		convertedStock.setDataObject("id", String.valueOf(stockToConvert.getId()));
		convertedStock.setDataObject("name", stockToConvert.getName());
		convertedStock.setDataObject("value", String.valueOf(stockToConvert.getValue()));
		convertedStock.setDataObject("date", String.valueOf(stockToConvert.getDate()));

		return convertedStock;
	}

	/**
	 * Method to convert dataObject to entity
	 * The method return Stock Entity 
	 */
	@Override
	public Entity dataObjectToEntity(DataObject stock) {
		String id = stock.getDataObject().get("id");
		String name = stock.getDataObject().get("name");
		double value = Double.valueOf(stock.getDataObject().get("value"));
		String date = stock.getDataObject().get("date");
		return new Stock(Integer.parseInt(id),name,value,date);
	}
	

}
