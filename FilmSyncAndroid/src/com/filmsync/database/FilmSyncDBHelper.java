package com.filmsync.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.filmSynclib.model.Card;
import com.filmSynclib.model.Project;

/**
 * Class to create,update and delete SQLite database.
 * @author fingent
 *
 */
public class FilmSyncDBHelper extends SQLiteOpenHelper{

	//Database Name
	public static final String DB_NAME="FILMSYNC_DB";

	//Project table name.
	public static final String PROJECT_TABLE_NAME="Project_Table";
	//Card table name.
	public static final String CARD_TABLE_NAME="Card_Table";

	// Project Table Fields
	public static final String PROJECT_ID="Project_Id"; //Project identifier.
	public static final String PROJECT_TITLE="Project_title";//Project Title.
	public static final String PROJECT_DESC="Project_Desc";//Project description.
	public static final String PROJECT_TWITTER="Project_Twitter";//Project twitter tag.

	//Card Table Fields
	public static final String CARD_ID="Card_Id"; //Card identifier
	public static final String CARD_TITLE="Card_title"; //Card title.
	public static final String CARD_DESC="Card_Desc";// Card description.
	public static final String CARD_TWITTER="Card_Twitter";//Card twitter tag.
	public static final String CARD_CONTENT="Card_Content";//Card content url.

	//Project Table Create Statement
	public static final String PROJECT_TABLE_CREATE="create table "+PROJECT_TABLE_NAME+"("
			+PROJECT_ID+" integer PRIMARY KEY,"
			+PROJECT_TITLE+" text,"
			+PROJECT_DESC+" text,"
			+PROJECT_TWITTER+" text)";
	//Card Table Create Statement
	public static final String CARD_TABLE_CREATE="create table "+CARD_TABLE_NAME+"("
			+PROJECT_ID+" integer refrences "+PROJECT_TABLE_NAME+","	
			+CARD_ID+" text,"
			+CARD_TITLE+" text,"
			+CARD_DESC+" text,"
			+CARD_CONTENT+" text,"
			+CARD_TWITTER+" text)";

	SQLiteDatabase database; //SQLite DB instance 

	/**
	 * Constructor 
	 * @param context - context where object created
	 * @param name    - Name of the data base.
	 * @param factory - Used to allow returning sub-classes of Cursor when calling query.
	 * @param version - Data base version.
	 */
	public FilmSyncDBHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		//database=this.getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//create project table
		db.execSQL(PROJECT_TABLE_CREATE);
		System.out.println("Project Table_CREATED");
		Log.d("Project Table", "CREATED!!!!");
		
		//Create card table.
		db.execSQL(CARD_TABLE_CREATE);
		System.out.println("Card Table_CREATED");
		Log.d("Card Table", "CREATED!!!!");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	/**
	 * Function to check project exist in the local DB
	 * @param id - Project Identifier
	 * @return True if project exist else False
	 */

	public boolean isProjectExist(int id){
		
		database=this.getWritableDatabase();
		
		//Query the project table to get project with id.
		Cursor cr=database.query(PROJECT_TABLE_NAME, new String[]{PROJECT_ID}, PROJECT_ID+"="+id, null, null,null, null);

		//Check cursor row count greater than zero.
		if(cr.getCount()>0){
			return true;
		}
		
		//close database object.
		database.close();
		
		return false;
	}

	/**
	 * Function to check card exist in the local DB
	 * @param projectID - Project Identifier of the Card
	 * @param cardID    - Card Identifier
	 * @return True if card exist else False
	 */

	public boolean isCardExist(int projectID,String cardID)
	{
		database=this.getWritableDatabase();
		
		//Query the card table to get card details with project id and card id.
		Cursor cr=database.query(CARD_TABLE_NAME, new String[]{CARD_ID}, PROJECT_ID+"="+projectID+" AND "+CARD_ID+"='"+cardID+"'", null, null,null, null);

		//Check cursor row count greater than zero.
		if(cr.getCount()>0){
			return true;
		}
		
		//close database object.
		database.close();
		return false;
	}

	/**
	 * Function to Insert Project details to DB
	 * @param project - Project Object
	 */
	public void inserProject(Project project){
		database=this.getWritableDatabase();
		if(project!=null){
			ContentValues cv=new ContentValues();
			cv.put(PROJECT_ID, Integer.parseInt(project.getProjectID()));
			cv.put(PROJECT_TITLE, project.getProjectTitle());
			cv.put(PROJECT_DESC, project.getDecription());
			cv.put(PROJECT_TWITTER, project.getTwitterSearch());

			//Insert the project
			database.insert(PROJECT_TABLE_NAME, null, cv);

			//insert cards for the project.
			for(int i=0;i<project.getCards().size();i++){
				insertCard(project.getCards().get(i));
			}
		}

		//close database object.
		database.close();
	}

	/**
	 * Function to card details to DB
	 * @param card - Card object
	 */

	public void insertCard(Card card){
		database=this.getWritableDatabase();
		if(card!=null){
			ContentValues cv=new ContentValues();
			cv.put(PROJECT_ID, Integer.parseInt(card.getCardProjectID()));
			cv.put(CARD_ID, card.getCardID());
			cv.put(CARD_TITLE, card.getCardTitle());
			cv.put(CARD_CONTENT, card.getCardContent());

			//insert the card to local database
			database.insert(CARD_TABLE_NAME, null, cv);
		}
		
		//close database object.
		database.close();
	}

	/**
	 * Function to update the project details
	 * @param project - Project Object
	 */

	public void updateProject(Project project){
		
		database=this.getWritableDatabase();

		ContentValues cv=new ContentValues();
		//cv.put(PROJECT_ID, Integer.parseInt(project.getProjectID()));
		cv.put(PROJECT_TITLE, project.getProjectTitle());
		cv.put(PROJECT_DESC, project.getDecription());
		cv.put(PROJECT_TWITTER, project.getTwitterSearch());

		database.update(PROJECT_TABLE_NAME, cv, PROJECT_ID+"="+Integer.parseInt(project.getProjectID()), null);

		for(int i=0;i<project.getCards().size();i++){

			int projectID=Integer.parseInt(project.getProjectID());
			String cardID=project.getCards().get(i).getCardID();

			if(isCardExist(projectID, cardID)){ //Card already in Local DB

				//Update the Existing Card
				updateCard(project.getCards().get(i));

			}else{								//Card not in Local DB

				//Insert the new Card
				insertCard(project.getCards().get(i));
			}

		}

		//close database object.
		database.close();
	}

	/**
	 * Function to Update card details 
	 * @param card - Card object
	 */
	public void updateCard(Card card){

		database=this.getWritableDatabase();
		ContentValues cv=new ContentValues();
		cv.put(CARD_ID, card.getCardID());
		cv.put(CARD_TITLE, card.getCardTitle());
		cv.put(CARD_CONTENT, card.getCardContent());

		//update the card in the local database.
		database.update(CARD_TABLE_NAME, cv,  PROJECT_ID+"="+Integer.parseInt(card.getCardProjectID())+" AND "+CARD_ID+"='"+card.getCardID()+"'", null);
		
		//close database object.
		database.close();
	}

	/**
	 * Function to retrieve all cards under a Project from the local DB
	 * @param projectID - Project Identifier of the Card
	 * @return card - collection
	 */

	public ArrayList<Card> getAllLocalCards(int projectID){
		
		database=this.getWritableDatabase();
		ArrayList<Card> cardList=new ArrayList<Card>();
		
		//Query the project table to get all cards with project id.
		Cursor crCards=database.query(CARD_TABLE_NAME, null, PROJECT_ID+"="+projectID, null, null, null, null);
		if(crCards.getCount()>0){
			while(crCards.moveToNext()){
				Card c=new Card();
				c.setCardProjectID(projectID+"");
				c.setCardID(crCards.getString(crCards.getColumnIndexOrThrow(CARD_ID)));
				c.setCardTitle(crCards.getString(crCards.getColumnIndexOrThrow(CARD_TITLE)));
				c.setCardContent(crCards.getString(crCards.getColumnIndexOrThrow(CARD_CONTENT)));
				cardList.add(c);
			}
		}
		//close database object.
		database.close();
		
		//return card collection.
		return cardList;
	}

	/**
	 * Function to get all Projects from local DB
	 * @return project collection
	 */
	public ArrayList<Project> getAllProjects(){
		
		database=this.getWritableDatabase();
		ArrayList<Project> projectList=new ArrayList<Project>();
		
		//Query the project table to get all project collection.
		Cursor crProjects=database.query(PROJECT_TABLE_NAME, null, null, null, null, null, null);

		if(crProjects.getCount()>0){
			while(crProjects.moveToNext()){
				Project p= new Project();
				p.setProjectID(crProjects.getInt(crProjects.getColumnIndexOrThrow(PROJECT_ID))+"");
				p.setProjectTitle(crProjects.getString(crProjects.getColumnIndexOrThrow(PROJECT_TITLE)));
				p.setTwitterSearch(crProjects.getString(crProjects.getColumnIndexOrThrow(PROJECT_TWITTER)));
				p.setDecription(crProjects.getString(crProjects.getColumnIndexOrThrow(PROJECT_DESC)));
				p.setCards(getAllLocalCards(crProjects.getInt(crProjects.getColumnIndexOrThrow(PROJECT_ID))));
				projectList.add(p);
			}
		}

		//close database object.
		database.close();

		//return project collection.
		return projectList;
	}
	
	/**
	 * Function to delete a card
	 * @param cardNo - Card identifier.
	 */

	public void deleteCard(String cardNo){
		
		database=this.getWritableDatabase();

		//delete the card from local database.
		database.delete(CARD_TABLE_NAME, CARD_ID+"='"+cardNo+"'", null);
		
		//close database object.
		database.close();
	}
	
	/**
	 * Function to delete all cards under the project.
	 * @param pID - Project identifier.
	 */

	public void deleteAllCardsForProject(int pID){
		
		database=this.getWritableDatabase();

		//delete the all cards of project with id from local database.
		database.delete(CARD_TABLE_NAME, PROJECT_ID+"="+pID, null);
		
		//close database object.
		database.close();
	}
	
	/**
	 * Function to delete project.
	 * @param projectID - Project identifier.
	 */

	public void deleteProject(int projectID){
		
		database=this.getWritableDatabase();

		//delete project with id from local database
		database.delete(PROJECT_TABLE_NAME, PROJECT_ID+"="+projectID, null);
		
		//close database object.
		database.close();
	}
	/**
	 * Function to get project identifier of the project. 
	 * @param cardNo - Card Identifier.
	 * @return Project Identifier.
	 */
	public Integer getProjectIDForCard(String cardNo){
		
		database=this.getWritableDatabase();
		Integer pID = null;
		
		//Query the card table to get project id of card.
		Cursor crCard=database.query(CARD_TABLE_NAME, new String[]{PROJECT_ID}, CARD_ID+"='"+cardNo+"'", null, null, null, null);
		
		if(crCard.getCount()>0){//check cursor row count greater than zero.
			crCard.moveToFirst();
			//retrieve the project id. 
			pID=crCard.getInt(crCard.getColumnIndexOrThrow(PROJECT_ID));
		}
		
		//close database object.
		database.close();
		//return project identifier.
		return pID;
	}

	/**
	 * Function to get twitter tag for the project.
	 * @param projectID - Project Identifier.
	 * @return twitter tag.
	 */
	public String getProjectTwitterSearch(int projectID){
		database=this.getWritableDatabase();
		String twitterSearch="";
		
		//Query the project table to get twitter search tag.
		Cursor crProjects=database.query(PROJECT_TABLE_NAME, new String[]{PROJECT_TWITTER}, PROJECT_ID+"="+projectID, null, null, null, null);
		
		if(crProjects.getCount()>0){//cursor row count greater than zero.
			crProjects.moveToFirst();
			//retrieve the twitter search tag.
			twitterSearch=crProjects.getString(crProjects.getColumnIndexOrThrow(PROJECT_TWITTER));
		}
		//close database object.
		database.close();
		
		//return the twitter search tag.
		return twitterSearch;
	}

}
