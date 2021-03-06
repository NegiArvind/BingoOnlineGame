package arvindandroid.com.arvind.bingoonlinegame.SqLiteDatabases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


//This class makes it easy for ContentProvider implementations to defer opening and upgrading the database until first use, to avoid blocking application startup with long-running database upgrades.
public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BingoOnline";//name of the database
    private static final int DATABASE_VERSION = 1;//database version
    private Context context;

    //syntax of sql to create a database
    private static final String CREATE = "create table " + DbContract.TABLE_NAME +
            "(id integer primary key autoincrement," + DbContract.CONTENT + " BLOB," + DbContract
            .SUBJECT_NAME+ " text," + DbContract.SUBJECT_TYPE + " text);";

    // if table is exist than drop this table(syntax)
    private static final String DROP_TABLE = "drop table if exists " + DbContract.TABLE_NAME;

//
    //constructor to create a DbHelper object
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;  //saving context
    }

    // calling this method results in database creation
    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    //called when a database is created for the first time
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE);

    }

    //called when a database need to be upgrade
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE);
        onCreate(sqLiteDatabase);
    }
//
//    // custom method to save list of content in database
//    public void saveContentList(List<Content> contentList, String subjectName, String subjectType) {
//
//        DbHelper dbHelper = new DbHelper(context); //creating DbHelper object
//
//        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();//creating database
//
//        // Fetching the already existing list
//        List<Content> existingList = dbHelper.readContentList(subjectName,subjectType);
//
//        //creating a ContentValues object, it will contain values in a set
//        ContentValues contentValues = new ContentValues();
//
//        for (int i = 0; i < contentList.size(); i++) {
//
//            // Don't add the object to database if it already exists..
//            if(existingList.contains(contentList.get(i)))
//            {
//                continue;
//            }
//
//            //serializing the object, since we can't store objects in sqlite
//            byte[] data = SerializationUtils.serialize(contentList.get(i));
//
//           //adding values in databse
//
//            contentValues.put(DbContract.CONTENT, data);
//            contentValues.put(DbContract.SUBJECT_NAME, subjectName);
//            contentValues.put(DbContract.SUBJECT_TYPE, subjectType);
//            sqLiteDatabase.insert(DbContract.TABLE_NAME, null, contentValues);
//
//        }
//        Log.i("savedtodb:",contentList.toString());
//    }

//    public List<Content> readContentList(String subjectName, String subjectType) {
//
//        List<Content> contentList = new ArrayList<>();
//
//        DbHelper dbHelper = new DbHelper(context);
//        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
//
//        // ALWAYS MAKE SURE THAT THE BITCHES LIKE (, ' and spaces) DON'T mentally harass you for hours. :(
//        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DbContract.TABLE_NAME + " WHERE "
//                + DbContract.SUBJECT_NAME + "= '" + subjectName + "' AND "
//                + DbContract.SUBJECT_TYPE + "= '" + subjectType+"'", null);
//
//        if (cursor.getCount() > 0) {
//
//            while (cursor.moveToNext()) {
//                int contentIndex = cursor.getColumnIndex(DbContract.CONTENT);
//                byte[] data = cursor.getBlob(contentIndex);
//                Content content = SerializationUtils.deserialize(data);
//                contentList.add(content);
//            }
//        //closing the database
//            cursor.close();
//            dbHelper.close();
//        }
//        return contentList;
//    }

    public void deleteContentList(String subjectName,String subjectType)
    {
        DbHelper dbHelper=new DbHelper(context);
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();

                int rowsDeleted= 0;
                rowsDeleted = sqLiteDatabase.delete(DbContract.TABLE_NAME,DbContract.SUBJECT_NAME
                        +"=? and "+DbContract.SUBJECT_TYPE+"=?",new String[]{subjectName,subjectType});
            Log.i("valuedeleted",Integer.toString(rowsDeleted));

        sqLiteDatabase.close();
        dbHelper.close();
        Log.i("Deleted","Old list cleared");
    }


}