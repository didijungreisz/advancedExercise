package RepoProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import Entities.DataObject;
import Exceptions.InvalidIdException;

public class CSVfileRepoProvider implements RepoProvider{
	private String csvFilePath;
	private FileWriter fileWriter;
	
	public CSVfileRepoProvider(String path) throws FileNotFoundException {
		File f = new File(path);
		if(!f.exists() || !f.isFile()) 
			throw new FileNotFoundException("The file is not exists yet. ");
		
		this.csvFilePath = path;
	}

	/**
	 * Method to add entity as a row to the CSV file
	 */
	@Override
	public void add(DataObject entity) {
		 String entityAsRow = dataObjectToCsvRow(entity);
		 try (FileWriter fileWriter = new FileWriter(csvFilePath, true)){
				fileWriter.append(entityAsRow);
			} catch (FileNotFoundException e) {
				System.out.println("Coldn't write to the following path. Please check that the file exists and path is correct");
			} catch (IOException e) {
				System.out.println(e);
			}
	}
	
	/**
	 * Method to remove entity by id
	 */
	@Override
	public void remove(int id) {
		removeOrUpdate("", "id : " + id);
	}
	
	/**
	 * Method to get entity by id
	 */
	@Override
	public DataObject getSingelEntity(int id) {
		DataObject entity = null;
		try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
			entity = csvRowToDataObject(reader.lines().filter(line -> line.contains("id : " + id)).findFirst().get());
		} catch (FileNotFoundException e) {
			System.out.println("Coldn't search in the following path. Please check that the file exists and path is correct");
		} catch (IOException e) {
			System.out.println(e);
		} 		
		return entity;
	}
	
	/**
	 * Method to get all the entities from the csv file
	 */
	@Override
	public List<DataObject> getAll() {
		List<DataObject> allEntities = new LinkedList<DataObject>();
		try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))){
			allEntities = reader.lines().map(this::csvRowToDataObject).collect(Collectors.toList());
		} catch (FileNotFoundException e) {
			System.out.println("Couldnt open the file. File not found");
		} catch (IOException e) {
			System.out.println(e);
		}
		return allEntities;	
	}

	/**
	 * Method to check if the file contains the given entity
	 */
	@Override
	public boolean contains(int id) {
		boolean isContains = false;
		try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))){
			 isContains =  reader.lines().anyMatch(line -> line.contains("id : " + id));
		} catch (FileNotFoundException e) {
			System.out.println("Couldnt open the file. File not found");
		} catch (IOException e) {
		System.out.println(e);
		}
		return isContains;
	}
	/**
	 * Method to update the given entity in the CSV file
	 */
	@Override
	public void update(DataObject entity) {
		String entityAsRow = dataObjectToCsvRow(entity);
		String id = "id : " + entity.getDataObject().get("id");
		removeOrUpdate(entityAsRow, id);		
	}
	
	/**
	 * Method to convert dataObject to CSV row
	 * CSV row is a string of properties separated by "," delimiters 
	 * @param entity
	 * @return String representation of CSV row 
	 */
	private String dataObjectToCsvRow(DataObject entity) {
		StringBuilder entityToCSVRow = new StringBuilder();
		 for (Map.Entry<String, String> entry : entity.getDataObject().entrySet()) {
			 entityToCSVRow.append(entry.getKey())
			          .append(" : ").append(entry.getValue()).append(" , ");
			  }		
		 return entityToCSVRow.replace(entityToCSVRow.length()-2, entityToCSVRow.length(), System.lineSeparator()).toString();		
	}
	
	/**
	 * Method to convert CSV row to dataObject
	 * the method takes each CSV cell and translate it to key-value pair
	 * @param entityAsString
	 * @return
	 */
	private DataObject csvRowToDataObject(String entityAsString) {
		DataObject entity = new DataObject();
		String[] pairs = entityAsString.split(",");
		for (String pair : pairs) {
		    String[] keyValue = pair.split(":");
		    entity.setDataObject(keyValue[0].trim(), keyValue[1].trim());
		}		
		return entity;
	}

	/**
	 * Helper method to the update & delete methods
	 * The method read the file and update/remove the correct row
	 * Then, it saves the updated content to the file 
	 * @param entityAsRow
	 * @param id
	 */
	private void removeOrUpdate(String entityAsRow, String id) {
		StringBuilder newContent = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(csvFilePath));
			// Reading all the lines of the CSV and building new file
			String line = reader.readLine();
			while (line != null) {
				if (line.contains(id))
					line = entityAsRow.replace(System.getProperty("line.separator"), "");
				if (line != "")
					newContent.append(line).append(System.lineSeparator());
				line = reader.readLine();
			}
			// Rewriting the input text file with newContent
			fileWriter = new FileWriter(csvFilePath);
			fileWriter.write(newContent.toString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (reader != null)
					reader.close();
				if (fileWriter != null)
					fileWriter.close();
			} catch (IOException e) {
				// TODO: handle exception
			}
		}
	}

}
