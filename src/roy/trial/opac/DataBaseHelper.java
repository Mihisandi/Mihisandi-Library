package roy.trial.opac;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    @SuppressLint("SdCardPath")
	private static String DB_PATH = "/data/data/roy.trial.opac/databases/";
 
    private static String DB_NAME = "library";
 
    private SQLiteDatabase myDataBase; 
    
    //private static final String[] COLUMNS = { "_id", "name", "author", "year", "shelf", "publisher", "copies" };
 
    private final Context myContext;
 
 
    public DataBaseHelper(Context context) {
 
    	super(context, DB_NAME, null, 1);
        this.myContext = context;
    }	
 

    public void createDataBase() throws IOException{
 
    	boolean dbExist = checkDataBase();
 
    	if(dbExist){
    		
    	}else{
 
    		
        	this.getReadableDatabase();
 
        	try {
 
    			copyDataBase();
 
    		} catch (IOException e) {
 
        		throw new Error("Error copying database");
 
        	}
    	}
 
    }
 
  
    private boolean checkDataBase(){
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
 
    	
 
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
    }
 
   
    private void copyDataBase() throws IOException{
 
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
    	
    	String outFileName = DB_PATH + DB_NAME;
 
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }
 
    public void openDataBase() throws SQLException{
 
        String myPath = DB_PATH + DB_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    }
 
    @Override
	public synchronized void close() {
 
    	    if(myDataBase != null)
    		    myDataBase.close();
 
    	    super.close();
 
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
 
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
	}
	
	public Cursor search(String name) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = 
	            db.query("library", 
	            new String[] {"_id", "name", "author"}, 
	            "name LIKE ?",
	            new String[] { "%" + name + "%" }, 
	            null,
	            null, 
	            null, 
	            null); 
	
		
		
		return cursor;
	}
	
	public Cursor searchAuthor(String author, String exceptions) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = 
	            db.query("library", 
	            new String[] {"_id", "name", "author"}, 
	            "author LIKE ? and _id NOT IN (?)",  
	            new String[] { "%" + author + "%", exceptions }, 
	            null, 
	            null,
	            null, 
	            null);
	
		return cursor;
	}
	
	public book search(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		book b;
		b = new book();
		Cursor cursor = 
	            db.query("library", 
	            new String[] {"_id", "name", "author", "year", "shelf", "publisher", "copies"}, 
	            "_id = ?",  
	            new String[] { String.valueOf(id) }, 
	            null, 
	            null, 
	            null, 
	            null); 
	
		
		if (cursor != null && cursor.getCount() > 0) {
	        cursor.moveToFirst();
			b = new book(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
					cursor.getString(5), cursor.getString(6));
		}
		return b;
	}
	
	public String[] getAll() {
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query("library", new String[] {"name", "author"}, null, null, null, null, null);
		
		if(cursor.getCount() > 0) {
			String[] str = new String[cursor.getCount() * 2];
			int i=0;
			 while(cursor.moveToNext()) {
				str[i] = cursor.getString(0);
				i++;
				str[i] = cursor.getString(1);
				i++;
			}
			Set<String> temp = new HashSet<String>(Arrays.asList(str));
			String[] unique = temp.toArray(new String[temp.size()]);
			return unique;
		}
		
		return new String[] {};
	}
 
	
}
