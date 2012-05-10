//package org.secmem.remoteroid.database;
//
//import java.util.ArrayList;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
//
//public class DocDataAdapter {
//	private DataManager dataManager;
//	private SQLiteDatabase db;
//	private final Context context;
//
//	private static final String DATABASE_NAME = "package.db";
//	private static final int DATABASE_VERSION = 1;
//	
//	private static final String _TABLENAME = "packages";
//	
//	private static final String INDEX = "index";
//	private static final String NUMBER = "number";
//	
//	private static final String _CREATE = "create table " + _TABLENAME + " ( "
//	+ INDEX + " text not null, " + NUMBER + " text not null);";
//
//	private static final String _DROP = "drop table if exists "+ _TABLENAME;
//
//	public DocDataAdapter(Context context) {
//		this.context = context;
//	}
//
//	public DocDataAdapter open() throws SQLException {
//		dataManager = new DataManager(context);
//		db = dataManager.getWritableDatabase();
//		return this;
//	}
//	
//	public void close(){
//		dataManager.close();
//	}
//	
//	public long insertContact(String name, String number){
//		ContentValues values = new ContentValues();
//		
//		values.put(this.NAME, name);
//		values.put(this.NUMBER, number);
//		
//		return db.insert(_TABLENAME, null, values);
//	}
//	
//	public void removeContact(String number){
//		
//		int i = db.delete(_TABLENAME, "number = '" + number + "'", null);
//		
//	}
//	
//	public ArrayList<BiSyncContact> getContacts() {
//		Cursor mCursor = db.query(_TABLENAME, null, null, null, null, null,	null);
//		ArrayList<BiSyncContact> contactList = new ArrayList<BiSyncContact>();
//
//		if (mCursor != null) {
//			Log.i("contact","Cursor         "+mCursor.getCount());
//			mCursor.moveToFirst();
//
//			for (int i = 0; i < mCursor.getCount(); i++) {
//				
//				String name = mCursor.getString(mCursor.getColumnIndex(NAME));
//				String number = mCursor.getString(mCursor.getColumnIndex(NUMBER));
//				
//				contactList.add(new BiSyncContact(name, number));
//
//				mCursor.moveToNext();
//			}
//		}
//		
//		if(contactList.size()==0){
//			Log.i("contact","0");
//		}
//		else
//			Log.i("contact","1");
//		
//
//		return contactList;
//	}
//	
//	public void dropTable() throws SQLException{
//		db.execSQL(_DROP);
//	}
//	
//	
//	public void createTable() throws SQLException{
//		db.execSQL(_CREATE +" IF NOT EXISTS " + _TABLENAME);
//	}
//	
//	public class DataManager extends SQLiteOpenHelper{
//
//		public DataManager(Context context) {
//			super( context, DATABASE_NAME, null, DATABASE_VERSION );
//		}
//
//		@Override
//		public void onCreate( SQLiteDatabase db ) {
//			Log.i("qq","onCreate");
//			db.execSQL(_CREATE +" IF NOT EXISTS " + _TABLENAME);
//		}
//
//		@Override
//		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//			Log.i("qq","onUpgrade");
//			Log.w( "INFO : ", "Upgrading db from version" + oldVersion + " to" +
//					newVersion + ", which will destroy all old data");
//			db.execSQL("DROP TABLE IF EXISTS " + _TABLENAME );
//			//onCreate(db);
//		}
//	}
// }
